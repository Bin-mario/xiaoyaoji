package cn.com.xiaoyaoji.core.global;

import cn.com.xiaoyaoji.data.bean.DocType;
import cn.com.xiaoyaoji.entity.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhoujingjie
 * @Date: 17/4/16
 */
public class GlobalProperties {
    public static Map<String, Object> properties = new HashMap<>();

    static {
        List<Document> createDocuments = new ArrayList<>();
        createDocuments.add(new Document(DocType.SYS_HTTP.getTypeName(), "Http"));
        createDocuments.add(new Document(DocType.SYS_WEBSOCKET.getTypeName(), "WebSocket"));
        createDocuments.add(new Document(DocType.SYS_DOC_MD.getTypeName(), "Markdown"));
        createDocuments.add(new Document(DocType.SYS_DOC_RICH_TEXT.getTypeName(), "富文本"));
        createDocuments.add(new Document(DocType.SYS_THIRDPARTY.getTypeName(), "第三方链接"));
        createDocuments.add(new Document(DocType.SYS_FOLDER.getTypeName(), "文件夹"));

        properties.put("document.create", createDocuments);


        List<Document> importDocuments = new ArrayList<>();
        importDocuments.add(new Document("import.pdf", "PDF"));
        importDocuments.add(new Document("import.postman", "Postman"));
        importDocuments.add(new Document("import.word", "Word"));
        importDocuments.add(new Document("import.excel", "Excel"));
        importDocuments.add(new Document("import.chm", "CHM"));
        importDocuments.add(new Document("import.markdown", "Markdown"));
        importDocuments.add(new Document("import.epub", "Epub电子书"));
        importDocuments.add(new Document("import.yxnote", "印象笔记"));
        importDocuments.add(new Document("import.ydnote", "有道笔记"));
        importDocuments.add(new Document("import.wznote", "为之笔记"));
        importDocuments.add(new Document("import.csdn_rss", "CSDN_RSS"));
        properties.put("document.import", importDocuments);
    }

    public static List<Document> getImportDocuments() {
        return (List<Document>) properties.get("document.import");
    }

    public static List<Document> getCreateDocuments() {
        return (List<Document>) properties.get("document.create");
    }


}
