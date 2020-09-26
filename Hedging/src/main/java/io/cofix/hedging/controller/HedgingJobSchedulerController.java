package io.cofix.hedging.controller;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("/schedule")
@Slf4j
public class HedgingJobSchedulerController {
    @Autowired
    private Scheduler   scheduler;

}
