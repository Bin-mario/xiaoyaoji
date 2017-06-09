package cn.com.xiaoyaoji.plugins.exportpdf;

import cn.com.xiaoyaoji.core.common.FileType;
import cn.com.xiaoyaoji.core.plugin.ExportPlugin;
import cn.com.xiaoyaoji.data.bean.DocType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/5/19
 */
public class PdfExportPlugin implements ExportPlugin {

    private Map<DocType,ExportHandler> handlerMap;
    public PdfExportPlugin(){
        handlerMap = new HashMap<>();
        handlerMap.put(DocType.SYS_HTTP,new Http2PdfExportHandler());
        handlerMap.put(DocType.SYS_WEBSOCKET,new Websocket2PdfExportHandler());
    }

    @Override
    public boolean support(DocType docType, FileType targetFileType) {
        if(FileType.PDF != targetFileType){
            return false;
        }
        if(!handlerMap.containsKey(docType)){
            return false;
        }
        return true;
    }

    @Override
    public Object doExport(DocType docType,Object data) {
        return handlerMap.get(docType).handle(data);
    }
}
