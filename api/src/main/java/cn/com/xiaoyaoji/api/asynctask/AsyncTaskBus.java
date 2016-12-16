package cn.com.xiaoyaoji.api.asynctask;

import cn.com.xiaoyaoji.api.asynctask.log.Log;
import cn.com.xiaoyaoji.api.data.bean.ProjectLog;
import cn.com.xiaoyaoji.api.data.bean.User;
import cn.com.xiaoyaoji.api.ex.ProjectMessage;
import cn.com.xiaoyaoji.api.service.ServiceFactory;
import cn.com.xiaoyaoji.api.utils.MemoryUtils;
import cn.com.xiaoyaoji.api.utils.StringUtils;
import cn.com.xiaoyaoji.api.websocket.WsUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: zhoujingjie
 * @Date: 16/9/14
 */
public class AsyncTaskBus {
    private static Logger logger = Logger.getLogger(AsyncTaskBus.class);
    private BlockingQueue queue ;
    private static AsyncTaskBus instance;
    private ExecutorService threadPool = Executors.newFixedThreadPool(20);
    static {
        instance = new AsyncTaskBus();
    }
    private AsyncTaskBus(){
        queue = new ArrayBlockingQueue(50);
        start();
    }

    private void start(){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Object data = queue.take();
                        if(data instanceof ProjectMessage){
                            WsUtils.pushMessage((ProjectMessage) data);
                        }
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(),e);
                    }
                }
            }
        });

    }
    public static AsyncTaskBus instance(){
        return instance;
    }

    private void log2db(String userId,String logDetails,String action,String projectId){
        ProjectLog log = new ProjectLog();
        log.setId(StringUtils.id());
        log.setUserId(userId);
        log.setCreateTime(new Date());
        log.setLog(logDetails);
        log.setAction(action);
        log.setProjectId(projectId);
        ServiceFactory.instance().create(log);
    }
    public void push(String projectId,String action,String id,String token,String logDetails,String... ext){
        if(action == null)
            return;
        User user = MemoryUtils.getUser(token);
        if(user== null){
            return;
        }
        log2db(user.getId(),logDetails,action,projectId);
        final ProjectMessage message = new ProjectMessage(action);
        message.setProjectId(projectId);
        message.setToken(token);
        if(action.contains("interface")){
            message.setInterfaceId(id);
        }else if (action.contains("module")){
            message.setModuleId(id);
        }else if (action.contains("folder")){
            message.setFolderId(id);
        }else if (action.contains("project")){
        }else {
            logger.info("action not found");
            return;
        }
        message.setExt(ext);
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    queue.put(message);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(),e);
                }
            }
        });
    }

}
