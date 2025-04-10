package br.com.tecsus.sigaubs.utils;

import org.springframework.web.context.annotation.ApplicationScope;

import java.time.LocalDateTime;

@ApplicationScope
public class ContemplationScheduleStatus {

    public static Status status;
    public static LocalDateTime startTime;
    public static LocalDateTime endTime;

    public ContemplationScheduleStatus() {
    }

    public enum Status{
        RUNNING,
        DONE,
        FAILED
    }
}
