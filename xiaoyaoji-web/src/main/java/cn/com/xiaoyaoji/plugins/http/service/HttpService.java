package cn.com.xiaoyaoji.plugins.http.service;

import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.service.ServiceFactory;

/**
 * @author: zhoujingjie
 * @Date: 17/4/13
 */
public class HttpService {
    private static HttpService instance;
    private HttpService(){
    }
    static {
        instance = new HttpService();
    }

    public static HttpService instance(){
         return instance;
    }

    public Doc getDoc(String docId){
        return ServiceFactory.instance().getById(docId,Doc.class);
    }
}
