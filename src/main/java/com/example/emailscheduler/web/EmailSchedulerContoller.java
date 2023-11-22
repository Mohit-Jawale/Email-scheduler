package com.example.emailscheduler.web;

import com.example.emailscheduler.payload.EmailRequest;
import com.example.emailscheduler.payload.EmailResponse;
import com.example.emailscheduler.payload.EmailTemplate;

import com.example.emailscheduler.payload.User;
import com.example.emailscheduler.quartz.job.EmailJob;
import com.example.emailscheduler.repository.UserRepository;
import com.example.emailscheduler.service.EmailTemplateService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
public class EmailSchedulerContoller {
    private final EmailTemplateService templateService;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);

    public static List<String> extractEmails(List<String> inputStrings) {
        List<String> extractedEmails = new ArrayList<>();
        String emailRegex = "[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}";
        Pattern pattern = Pattern.compile(emailRegex);

        for (String str : inputStrings) {
            Matcher matcher = pattern.matcher(str);
            while (matcher.find()) {
                extractedEmails.add(matcher.group());
            }
        }

        return extractedEmails;
    }

    @GetMapping("/Recipient")
    public  List<Recipient> getAllEmails(List<String> names){

        StringJoiner sj = new StringJoiner("','", "'", "'");
        for (String name : names) {
            sj.add(name);
        }
        String sql = "SELECT * FROM recipient WHERE name IN (" + sj.toString() + ")";
        logger.info(sql);
        List<Recipient> recipients =  jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(Recipient.class));

        return  recipients;

    }
    @PostMapping("/schedule/email")
    public ResponseEntity<EmailResponse> scheduleEmail(@Valid  @RequestBody @ModelAttribute EmailRequest emailRequest) {
        try {
            List<String> emails = Arrays.asList(emailRequest.getEmail().split(","));

            List<String> onlyEmails = extractEmails(emails);
            logger.info(onlyEmails.toString());
            List<Recipient> response = getAllEmails(emails);
            List<String> myList = new ArrayList<>();
            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());
            for (Recipient r : response)
            {
                myList.add(r.getEmail_address());
            }
            myList.addAll(onlyEmails);
            String result = String.join(",", myList);
            logger.info("This is result-:"+result);
            emailRequest.setEmail(result);
            User user = userRepository.findByEmail(emailRequest.getEmail());
            if(dateTime.isBefore(ZonedDateTime.now())) {
                EmailResponse emailResponse = new EmailResponse(false,
                        "dateTime must be after current time");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emailResponse);
            }


            JobDetail jobDetail = buildJobDetail(emailRequest,emailRequest.getTemplate());
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            EmailResponse emailResponse = new EmailResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
            return ResponseEntity.ok(emailResponse);
        } catch (SchedulerException ex) {
            log.error("Error scheduling email", ex);

            EmailResponse emailResponse = new EmailResponse(false,
                    "Error scheduling email. Please try later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
        }
    }
    @GetMapping("/get")
    public ResponseEntity<String> getAPiTest(){
        return ResponseEntity.ok("Get ApiTest-Pass");
    }

    @GetMapping("/jobs")
    public String getScheduleEmails(Model model) throws SchedulerException {

        List<JobDataMap> jobs = new ArrayList<>();

        for (String groupName : scheduler.getJobGroupNames()) {

            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                jobs.add(scheduler.getJobDetail(jobKey).getJobDataMap());
            }
        }
        model.addAttribute("jobs", jobs);
        return "jobs";
      // return ResponseEntity.ok(jobs);
    }

    private JobDetail buildJobDetail(EmailRequest scheduleEmailRequest, String emailTemplate) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", scheduleEmailRequest.getEmail());
        jobDataMap.put("subject", scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());
        jobDataMap.put("attachment",scheduleEmailRequest.getAttachment());
        jobDataMap.put("template",emailTemplate);
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }


}
