package com.wei;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wjj on 4/8/17.
 */
public class ScheduleTest {

    private static class  ThreadFactory implements java.util.concurrent.ThreadFactory{

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r,"wei pool cleaner");
                        t.setDaemon(true);
                        return t;
        }
    }
    public static void main(String[] args) throws InterruptedException {

        ScheduledExecutorService  service = Executors.newSingleThreadScheduledExecutor( new ThreadFactory()
        );
        long timeSleep = 5000;
        service.scheduleWithFixedDelay(new Runnable() {
                                           @Override
                                           public void run() {
                                               System.out.println("bbbb");
                                           }
                                       },
                timeSleep, timeSleep, TimeUnit.MILLISECONDS);

        Thread.sleep(600000);
    }
}
