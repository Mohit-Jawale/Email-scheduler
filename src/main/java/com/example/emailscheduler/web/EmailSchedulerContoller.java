package com.example.emailscheduler.web;

import com.example.emailscheduler.payload.EmailRequest;
import com.example.emailscheduler.payload.EmailResponse;
import com.example.emailscheduler.quartz.job.EmailJob;
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
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Slf4j
@RestController
public class EmailSchedulerContoller {

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

            for (Recipient r : response)
            {
                myList.add(r.getEmail_address());
            }
            myList.addAll(onlyEmails);
            String result = String.join(",", myList);
            logger.info("This is result-:"+result);
            emailRequest.setEmail(result);



            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getTimeZone());

            if(dateTime.isBefore(ZonedDateTime.now())) {
                EmailResponse emailResponse = new EmailResponse(false,
                        "dateTime must be after current time");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emailResponse);
            }

            JobDetail jobDetail = buildJobDetail(emailRequest);
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
    private JobDetail buildJobDetail(EmailRequest scheduleEmailRequest) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", scheduleEmailRequest.getEmail());
        jobDataMap.put("subject", scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());
        jobDataMap.put("attachment",scheduleEmailRequest.getAttachment());

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
