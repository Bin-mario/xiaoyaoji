package cn.com.xiaoyaoji.plugins.exportpdf;

import cn.com.xiaoyaoji.core.common.FileType;
import cn.com.xiaoyaoji.core.common.DocType;
import cn.com.xiaoyaoji.plugins.ExportHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/5/19
 */
public class PdfExportPlugin {

    private Map<DocType,ExportHandler> handlerMap;
    public PdfExportPlugin(){
        handlerMap = new HashMap<>();
        handlerMap.put(DocType.SYS_HTTP,new Http2PdfExportHandler());
        handlerMap.put(DocType.SYS_WEBSOCKET,new Websocket2PdfExportHandler());
    }

    public boolean support(DocType docType, FileType targetFileType) {
        if(FileType.PDF != targetFileType){
            return false;
        }
        if(!handlerMap.containsKey(docType)){
            return false;
        }
        return true;
    }

    public Object doExport(DocType docType,Object data) {
        return handlerMap.get(docType).handle(data);
    }
}
