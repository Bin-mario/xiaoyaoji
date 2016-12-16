package cn.com.xiaoyaoji.api.view;

import cn.com.xiaoyaoji.api.ex.NotLoginException;
import cn.com.xiaoyaoji.api.ex.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.mangoframework.core.dispatcher.Parameter;
import org.mangoframework.core.exception.InvalidArgumentException;
import org.mangoframework.core.view.ResultView;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhoujingjie
 * @date 2016-05-24
 */
public class MimeJsonView extends ResultView {
    @Override
    public void doRepresent(Parameter parameter) throws Exception {
        parameter.getResponse().setHeader("Access-Control-Allow-Origin", "*");
        parameter.getResponse().setHeader("Access-Control-Request-Method", "GET, POST, DELETE, PUT, OPTIONS");
        HttpServletResponse response = parameter.getResponse();
        response.setCharacterEncoding("UTF-8");
        if(super.getData() instanceof Result){
            super.setData(((Result)super.getData()).getData());
        }
        JSON.writeJSONStringTo(super.getData(),response.getWriter(), SerializerFeature.WriteDateUseDateFormat);
    }

    @Override
    public void handleException(Parameter parameter, Throwable e) throws Exception {
        if(e instanceof NotLoginException){
            super.setData(new Result<>(-2,"会话已过期"));
        }else {
            String err= e.getMessage();
            if(!(e instanceof InvalidArgumentException)) {
                e.printStackTrace();
                err="系统错误";
            }
            super.setData(new Result<>(false, err));
            parameter.getResponse().setHeader("Content-Type","text/plain");
            parameter.getResponse().setHeader("Content-Disposition",null);
            parameter.getResponse().setCharacterEncoding("UTF-8");
            parameter.getResponse().getWriter().write(err);
            //JSON.writeJSONStringTo(super.getData(),parameter.getResponse().getWriter(), SerializerFeature.WriteDateUseDateFormat);
        }
        //doRepresent(parameter);
    }


}
