package cn.com.xiaoyaoji.core.plugin;

import cn.com.xiaoyaoji.core.common.FileType;
import cn.com.xiaoyaoji.data.bean.DocType;

/**
 * @author zhoujingjie
 *         created on 2017/5/19
 */
public interface ExportPlugin {

    boolean support(DocType docType, FileType targetFileType);

    Object doExport(DocType docType,Object data);
}
