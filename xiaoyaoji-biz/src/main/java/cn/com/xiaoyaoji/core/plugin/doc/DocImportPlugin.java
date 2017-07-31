package cn.com.xiaoyaoji.core.plugin.doc;

import cn.com.xiaoyaoji.core.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhoujingjie
 *         created on 2017/7/1
 */
public abstract class DocImportPlugin extends Plugin<DocImportPlugin> {

    public abstract void doImport(String fileName,InputStream file,String userId,String projectId,String parentId) throws  IOException;

}
