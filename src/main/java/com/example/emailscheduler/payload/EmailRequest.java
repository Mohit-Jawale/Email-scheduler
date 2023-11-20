package com.example.emailscheduler.payload;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

@JsonInclude(JsonInclude.Include.NON_NULL)


public class EmailRequest {

    @NotEmpty
    String  email;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String body;

    @NotNull
    private LocalDateTime dateTime;
    @NotNull
    private ZoneId timeZone;

    private String attachment;

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }


}
