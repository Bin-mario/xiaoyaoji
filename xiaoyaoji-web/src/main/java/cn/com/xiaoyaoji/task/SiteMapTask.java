package cn.com.xiaoyaoji.task;

import cn.com.xiaoyaoji.service.ServiceFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * 生成sitemap
 * @author zhoujingjie
 *         created on 2017/8/29
 */
public class SiteMapTask extends TimerTask {
    private static Logger logger = LoggerFactory.getLogger(SiteMapTask.class);
    private ServletContext servletContext;

    private static SiteMapTask instance;


    private SiteMapTask(ServletContext servletContext){
        this.servletContext = servletContext;
    }

    //手动运行
    public static void manualRunTask(){
        instance.run();
    }

    public static void start(ServletContext servletContext){
        if(instance == null) {
            instance = new SiteMapTask(servletContext);
        }
        //每天凌晨1点执行
        Date oclock1 = getClock(1);
        ScheduleManager.schdule(instance,oclock1,24*60*60*1000);

    }

    private static Date getClock(int hour){
        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.HOUR_OF_DAY) > hour){
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    private String getURL(String id){
        return "http://www.xiaoyaoji.cn/project/"+id;
    }
    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        //
        //查询所有有效的projectId
        List<String> ids = ServiceFactory.instance().getAllProjectValidIds();
        if(ids.size() == 0 )
            return;
        logger.info("the daily sitemap generation task begin");
        File siteMapFile = new File(servletContext.getRealPath("/sitemap.txt"));
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(siteMapFile), StandardCharsets.UTF_8);
            writer.write("http://www.xiaoyaoji.cn/");
            for(String id:ids){
                writer.write(getURL(id));
                writer.write("\n");
            }
            logger.info("the daily sitemap generation task has completed");
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }finally {
            IOUtils.closeQuietly(writer);
        }
    }
}
