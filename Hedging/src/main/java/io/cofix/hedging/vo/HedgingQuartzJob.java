package io.cofix.hedging.vo;

import io.cofix.hedging.service.HedgingJobService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Component
public class HedgingQuartzJob extends QuartzJobBean {
    @Autowired
    private HedgingJobService   hedgingJobService;

    @Autowired
    private Lock    lock;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (!lock.tryLock()) return;

        try {
            hedgingJobService.hedging();
        } finally {
            lock.unlock();
        }
    }
}
