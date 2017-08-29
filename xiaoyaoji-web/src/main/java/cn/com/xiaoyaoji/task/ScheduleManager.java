package cn.com.xiaoyaoji.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhoujingjie
 *         created on 2017/8/29
 */
public class ScheduleManager {
    private static Timer timer;
    private static Logger logger = LoggerFactory.getLogger(ScheduleManager.class);

    static {
        timer = new Timer("task_thread", true);
    }

    public static void schdule(TimerTask timerTask, Date first, long period) {
        timer.schedule(timerTask, first, period);
    }

    public static void shutdown(){
        timer.cancel();
    }


}
