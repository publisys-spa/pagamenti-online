package it.publisys.pagamentionline.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by micaputo on 22/08/2016.
 */
@Component
public class JobsComponent {

    //@Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        System.out.println("Fixed delay task - " + System.currentTimeMillis()/1000);
    }
}
