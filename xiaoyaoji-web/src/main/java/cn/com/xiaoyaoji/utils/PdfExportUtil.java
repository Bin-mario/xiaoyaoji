package cn.com.xiaoyaoji.utils;

import cn.com.xiaoyaoji.data.bean.Doc;
import cn.com.xiaoyaoji.data.bean.DocType;
import cn.com.xiaoyaoji.data.bean.Project;
import cn.com.xiaoyaoji.data.bean.ProjectGlobal;
import cn.com.xiaoyaoji.docformatter.paragraph2.*;
import cn.com.xiaoyaoji.service.ProjectService;
import cn.com.xiaoyaoji.service.ServiceFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.*;

/**
 * Created by luofei on 2017/6/2.
 */
public class PdfExportUtil {

    private static final Logger log = LoggerFactory.getLogger(PdfExportUtil.class);

    private static final int TYPE_ROOT_DOC = 1;

    private static final String PLACEHOLDER_CHILDPARAM = "    ";

    public static boolean export(Project project, OutputStream stream) {

        AssertUtils.notNull("pdf export error:project or output stream cannot be null.", project, stream);
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, stream);
            document.open();
            printRootTitle(project, document);
            printProjectInfo(project, document);
            printChapters(project, document);
            document.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private static void printRootTitle(Project project, Document document) throws DocumentException {

        RootTitle title = new RootTitle(document, project.getName());
        document.add(title);
    }

    private static void printProjectInfo(Project project, Document document) throws DocumentException {

        NormalContent desc = new NormalContent(12, false);
        desc.addContent(project.getDescription());
        document.add(desc);
        NormalContent content = new NormalContent(12, false);
        content.setAlignment(Element.ALIGN_RIGHT);
        Map<Object, Object> info = new HashMap<Object, Object>();
        info.put("作者", ServiceFactory.instance().getUserName(project.getUserId()));
        info.put("更新时间", project.getLastUpdateTime());
        content.addContent(info);
//        content.add(Chunk.NEXTPAGE);
        document.add(content);
    }

    private static void printChapters(Project project, Document document) throws DocumentException {


        List<Doc> docs = ProjectService.instance().getProjectDocs(project.getId(), true);
        ProjectGlobal global = ProjectService.instance().getProjectGlobal(project.getId());
        int order = printGlobleParam(0, global, document);
        order = printEnvironment(order, global, document);
        for (int i = 0; i < docs.size(); i++) {
            printDoc(i + order, null, docs.get(i), document);
        }
    }

    private static int printGlobleParam(int order, ProjectGlobal global, Document document) throws DocumentException {

        String params = global.getHttp();
        JSONObject json = JSONObject.parseObject(params);
        if (json == null || json.isEmpty()) {
            return order;
        }
        Doc doc = new Doc();
        doc.setName("全局变量");
        Element title = printFolder(order, null, doc);
        int index = 0;

        JSONArray requestHeaders = json.getJSONArray("requestHeaders");
        if (requestHeaders != null && !requestHeaders.isEmpty()) {
            List<List<Object>> table = genReqArgs(null, requestHeaders);
            if (table != null) {
                NormalSubTitle title3 = null;
                if (title instanceof NormalTitle) {
                    title3 = new NormalSubTitle((NormalTitle) title, "全局请求头", ++index);
                } else if (title instanceof NormalSubTitle) {
                    title3 = new NormalSubTitle((NormalSubTitle) title, "全局请求头", ++index);
                }
                title3.getFont().setSize(12F);
                NormalContent content3 = new NormalContent();
                content3.addTable(table);
                title3.add(content3);
            }
        }

        JSONArray requestArgs = json.getJSONArray("requestArgs");
        if (requestArgs != null && !requestArgs.isEmpty()) {
            List<List<Object>> table = genReqHeaders(null, requestArgs);
            if (table != null) {
                NormalSubTitle title3 = null;
                if (title instanceof NormalTitle) {
                    title3 = new NormalSubTitle((NormalTitle) title, "全局请求数据", ++index);
                } else if (title instanceof NormalSubTitle) {
                    title3 = new NormalSubTitle((NormalSubTitle) title, "全局请求数据", ++index);
                }
                title3.getFont().setSize(12F);
                NormalContent content3 = new NormalContent();
                content3.addTable(table);
                title3.add(content3);
            }
        }

        JSONArray responseHeaders = json.getJSONArray("responseHeaders");
        if (requestArgs != null && !requestArgs.isEmpty()) {
            List<List<Object>> table = genRspHeaders(null, responseHeaders);
            if (table != null) {
                NormalSubTitle title3 = null;
                if (title instanceof NormalTitle) {
                    title3 = new NormalSubTitle((NormalTitle) title, "全局响应头", ++index);
                } else if (title instanceof NormalSubTitle) {
                    title3 = new NormalSubTitle((NormalSubTitle) title, "全局响应头", ++index);
                }
                title3.getFont().setSize(12F);
                NormalContent content3 = new NormalContent();
                content3.addTable(table);
                title3.add(content3);
            }
        }

        JSONArray responseArgs = json.getJSONArray("responseArgs");
        if (requestArgs != null && !requestArgs.isEmpty()) {
            List<List<Object>> table = genRspArgs(null, responseArgs);
            if (table != null) {
                NormalSubTitle title3 = null;
                if (title instanceof NormalTitle) {
                    title3 = new NormalSubTitle((NormalTitle) title, "全局响应数据", ++index);
                } else if (title instanceof NormalSubTitle) {
                    title3 = new NormalSubTitle((NormalSubTitle) title, "全局响应数据", ++index);
                }
                title3.getFont().setSize(12F);
                NormalContent content3 = new NormalContent();
                content3.addTable(table);
                title3.add(content3);
            }
        }
        document.add(title);
        return ++order;
    }

    private static int printEnvironment(int order, ProjectGlobal global, Document document) throws DocumentException {

        String env = global.getEnvironment();
        JSONArray json = JSONObject.parseArray(env);
        if (json == null || json.isEmpty()) {
            return order;
        }
        Doc doc = new Doc();
        doc.setName("环境变量");
        Element title = printFolder(order, null, doc);
        int index = 0;

        for (int i = 0; i < json.size(); i++) {
            JSONObject tmp = JSONObject.parseObject(json.getString(i));
            NormalSubTitle title3 = new NormalSubTitle((NormalTitle) title, tmp.getString("name"), i);
            title3.getFont().setSize(12F);
            JSONArray vars = tmp.getJSONArray("vars");
            if (vars != null && !vars.isEmpty()) {
                NormalContent content3 = new NormalContent();
                Map<String, String> varsMap = new HashMap<>();
                for (int j = 0; j < vars.size(); j++) {
                    JSONObject kv = vars.getJSONObject(j);
                    varsMap.put(kv.getString("name"), kv.getString("value"));
                }
                content3.addContent(varsMap);
                title3.add(content3);
            }
        }
        document.add(title);
        return ++order;
    }

    private static void printDoc(int order, Object parent, Doc doc, Document document) throws DocumentException {

        boolean isFolder = false;
        Element title = null;
        switch (DocType.parse(doc.getType())) {
            case SYS_FOLDER:
                title = printFolder(order, parent, doc);
                isFolder = true;
                break;
            case SYS_HTTP:
                title = printHttp(order, parent, doc);
                break;
            case SYS_DOC_RICH_TEXT:
                title = printRichText(order, parent, doc);
                break;
            case SYS_DOC_MD:
                title = printMd(order, parent, doc);
                break;
            case SYS_WEBSOCKET:
                title = printWebsocket(order, parent, doc);
                break;
            case SYS_THIRDPARTY:
                title = printThirdpart(order, parent, doc);
                break;
        }
        if (isFolder) {
            List<Doc> children = doc.getChildren();
            for (int i = 0; i < children.size(); i++) {
                printDoc(i, title, children.get(i), document);
            }
        }
        if (parent == null) {
            document.add(title);
        }
    }

    private static Element printFolder(int order, Object parent, Doc doc) throws DocumentException {

        if (parent == null) {
            NormalTitle title = new NormalTitle(doc.getName(), order + 1);
            return title;
        } else {
            if (parent instanceof NormalTitle) {
                NormalSubTitle title = new NormalSubTitle((NormalTitle) parent, doc.getName(), order + 1);
                return title;
            } else if (parent instanceof NormalSubTitle) {
                NormalSubTitle title = new NormalSubTitle((NormalSubTitle) parent, doc.getName(), order + 1);
                return title;
            }
        }
        throw new IllegalArgumentException("Unknown parent type:" + parent.getClass());
    }

    private static void addContent(Object parent, NormalContent content) {

        if (parent instanceof NormalTitle) {
            ((NormalTitle) parent).add(content);
        } else if (parent instanceof NormalSubTitle) {
            ((NormalSubTitle) parent).add(content);
        }
    }

    private static Element printHttp(int order, Object parent, Doc doc) throws DocumentException {

        Element title = printFolder(order, parent, doc);
        JSONObject json = JSONObject.parseObject(doc.getContent());
        if (json == null) {
            return title;
        }
        NormalSubTitle title2 = null;
        int index = 0;
        if (title instanceof NormalTitle) {
            title2 = new NormalSubTitle((NormalTitle) title, "基本信息", ++index);
        } else if (title instanceof NormalSubTitle) {
            title2 = new NormalSubTitle((NormalSubTitle) title, "基本信息", ++index);
        }
        title2.getFont().setSize(12F);
        NormalContent content = new NormalContent();
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("接口名称", doc.getName());
        data.put("接口地址", json.getString("url"));
        data.put("请求方法", json.getString("requestMethod"));
        data.put("请求数据类型", json.getString("dataType"));
        data.put("响应类型", json.getString("contentType"));
        data.put("状态", json.getString("status"));
        data.put("接口描述", new HtmlBean(json.getString("description")));
        content.addContent(data);
        title2.add(content);

        JSONArray requestArgs = json.getJSONArray("requestArgs");
        if (requestArgs != null && !requestArgs.isEmpty()) {
            List<List<Object>> table = genReqArgs(null, requestArgs);
            if (table != null) {
                NormalSubTitle title3 = null;
                if (title instanceof NormalTitle) {
                    title3 = new NormalSubTitle((NormalTitle) title, "请求参数", ++index);
                } else if (title instanceof NormalSubTitle) {
                    title3 = new NormalSubTitle((NormalSubTitle) title, "请求参数", ++index);
                }
                title3.getFont().setSize(12F);
                NormalContent content3 = new NormalContent();
                content3.addTable(table);
                title3.add(content3);
            }
        }

        JSONArray requestHeaders = json.getJSONArray("requestHeaders");
        if (requestHeaders != null && !requestHeaders.isEmpty()) {
            List<List<Object>> table = genReqHeaders(null, requestHeaders);
            if (table != null) {
                NormalSubTitle title3 = null;
                if (title instanceof NormalTitle) {
                    title3 = new NormalSubTitle((NormalTitle) title, "请求头", ++index);
                } else if (title instanceof NormalSubTitle) {
                    title3 = new NormalSubTitle((NormalSubTitle) title, "请求头", ++index);
                }
                title3.getFont().setSize(12F);
                NormalContent content3 = new NormalContent();
                content3.addTable(table);
                title3.add(content3);
            }
        }

        JSONArray responseArgs = json.getJSONArray("responseArgs");
        if (responseArgs != null && !responseArgs.isEmpty()) {
            List<List<Object>> table = genRspArgs(null, responseArgs);
            if (table != null) {
                NormalSubTitle title3 = null;
                if (title instanceof NormalTitle) {
                    title3 = new NormalSubTitle((NormalTitle) title, "响应参数", ++index);
                } else if (title instanceof NormalSubTitle) {
                    title3 = new NormalSubTitle((NormalSubTitle) title, "响应参数", ++index);
                }
                title3.getFont().setSize(12F);
                NormalContent content3 = new NormalContent();
                content3.addTable(table);
                title3.add(content3);
            }
        }
        JSONArray responseHeaders = json.getJSONArray("responseHeaders");
        if (responseHeaders != null && !responseHeaders.isEmpty()) {
            List<List<Object>> table = genRspHeaders(null, responseHeaders);
            if (table != null) {
                NormalSubTitle title3 = null;
                if (title instanceof NormalTitle) {
                    title3 = new NormalSubTitle((NormalTitle) title, "响应头", ++index);
                } else if (title instanceof NormalSubTitle) {
                    title3 = new NormalSubTitle((NormalSubTitle) title, "响应头", ++index);
                }
                title3.getFont().setSize(12F);
                NormalContent content3 = new NormalContent();
                content3.addTable(table);
                title3.add(content3);
            }
        }
        return title;
    }

    private static List<List<Object>> genReqArgs(String parentKey, JSONArray jsonArray) {

        if (jsonArray == null || jsonArray.isEmpty()) {
            return null;
        }
        List<List<Object>> result = new ArrayList<>();
        if (parentKey == null) {
            Object[] header = new Object[]{"参数名称", "是否必须", "数据类型", "默认值", "描述"};
            result.add(Arrays.asList(header));
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject entry = jsonArray.getJSONObject(i);
            parentKey = parentKey == null ? "" : parentKey + PLACEHOLDER_CHILDPARAM;
            String name = parentKey + entry.getString("name");
            String require = entry.getString("require");
            String type = entry.getString("type");
            String defaultValue = entry.getString("defaultValue");
            String description = entry.getString("description");
            Object[] param = new Object[]{name, require, type, defaultValue, description};
            result.add(Arrays.asList(param));
            JSONArray children = entry.getJSONArray("children");
            if (children != null && !children.isEmpty()) {
                result.addAll(genReqArgs(name, children));
            }
        }

        return result;
    }

    private static List<List<Object>> genReqHeaders(String parentKey, JSONArray jsonArray) {

        if (jsonArray == null || jsonArray.isEmpty()) {
            return null;
        }
        List<List<Object>> result = new ArrayList<>();
        if (parentKey == null) {
            Object[] header = new Object[]{"参数名称", "是否必须", "默认值", "描述"};
            result.add(Arrays.asList(header));
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject entry = jsonArray.getJSONObject(i);
            String name = (parentKey == null ? "" : ("    ")) + entry.getString("name");
            String require = entry.getString("require");
            String defaultValue = entry.getString("defaultValue");
            String description = entry.getString("description");
            Object[] param = new Object[]{name, require, defaultValue, description};
            result.add(Arrays.asList(param));
            JSONArray children = entry.getJSONArray("children");
            if (children != null && !children.isEmpty()) {
                result.addAll(genReqHeaders(name, children));
            }
        }

        return result;
    }

    private static List<List<Object>> genRspArgs(String parentKey, JSONArray jsonArray) {

        if (jsonArray == null || jsonArray.isEmpty()) {
            return null;
        }
        List<List<Object>> result = new ArrayList<>();
        if (parentKey == null) {
            Object[] header = new Object[]{"参数名称", "是否必须", "数据类型", "描述"};
            result.add(Arrays.asList(header));
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject entry = jsonArray.getJSONObject(i);
            String name = (parentKey == null ? "" : ("    ")) + entry.getString("name");
            String require = entry.getString("require");
            String type = entry.getString("type");
            String description = entry.getString("description");
            Object[] param = new Object[]{name, require, type, description};
            result.add(Arrays.asList(param));
            JSONArray children = entry.getJSONArray("children");
            if (children != null && !children.isEmpty()) {
                result.addAll(genRspArgs(name, children));
            }
        }

        return result;
    }

    private static List<List<Object>> genRspHeaders(String parentKey, JSONArray jsonArray) {

        if (jsonArray == null || jsonArray.isEmpty()) {
            return null;
        }
        List<List<Object>> result = new ArrayList<>();
        if (parentKey == null) {
            Object[] header = new Object[]{"参数名称", "是否必须", "描述"};
            result.add(Arrays.asList(header));
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject entry = jsonArray.getJSONObject(i);
            String name = (parentKey == null ? "" : ("    ")) + entry.getString("name");
            String require = entry.getString("require");
            String description = entry.getString("description");
            Object[] param = new Object[]{name, require, description};
            result.add(Arrays.asList(param));
            JSONArray children = entry.getJSONArray("children");
            if (children != null && !children.isEmpty()) {
                result.addAll(genRspHeaders(name, children));
            }
        }

        return result;
    }

    private static Element printRichText(int order, Object parent, Doc doc) throws DocumentException {

        Element title = printFolder(order, parent, doc);
        NormalContent content = new NormalContent();
        content.addHtml(doc.getContent());
        addContent(title, content);
        return title;
    }

    private static Element printMd(int order, Object parent, Doc doc) throws DocumentException {

        Element title = printFolder(order, parent, doc);
        PegDownProcessor processor = new PegDownProcessor(10 * 1000);
        String html = processor.markdownToHtml(doc.getContent());
        NormalContent content = new NormalContent();
        content.addHtml(html);
        addContent(title, content);
        return title;
    }

    private static Element printWebsocket(int order, Object parent, Doc doc) throws DocumentException {

        Element title = printFolder(order, parent, doc);
        JSONObject json = JSONObject.parseObject(doc.getContent());
        if (json == null) {
            return title;
        }
        NormalSubTitle title2 = null;
        if (title instanceof NormalTitle) {
            title2 = new NormalSubTitle((NormalTitle) title, "基本信息", 1);
        } else if (title instanceof NormalSubTitle) {
            title2 = new NormalSubTitle((NormalSubTitle) title, "基本信息", 1);
        }
        title2.getFont().setSize(12F);
        NormalContent content = new NormalContent();
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("接口名称", doc.getName());
        data.put("接口地址", json.getString("url"));
        data.put("状态", json.getString("status"));
        data.put("接口描述", new HtmlBean(json.getString("description")));
        content.addContent(data);
        title2.add(content);

        JSONArray requestArgs = json.getJSONArray("requestArgs");
        if (requestArgs != null && !requestArgs.isEmpty()) {
            NormalSubTitle title3 = null;
            if (title instanceof NormalTitle) {
                title3 = new NormalSubTitle((NormalTitle) title, "请求参数", 2);
            } else if (title instanceof NormalSubTitle) {
                title3 = new NormalSubTitle((NormalSubTitle) title, "请求参数", 2);
            }
            title3.getFont().setSize(12F);
            NormalContent content3 = new NormalContent();
            List<List<Object>> table = genReqArgs(null, requestArgs);
            content3.addTable(table);
            title3.add(content3);
        }

        JSONArray responseArgs = json.getJSONArray("responseArgs");
        if (requestArgs != null && !requestArgs.isEmpty()) {
            NormalSubTitle title3 = null;
            if (title instanceof NormalTitle) {
                title3 = new NormalSubTitle((NormalTitle) title, "响应参数", 3);
            } else if (title instanceof NormalSubTitle) {
                title3 = new NormalSubTitle((NormalSubTitle) title, "响应参数", 3);
            }
            title3.getFont().setSize(12F);
            NormalContent content3 = new NormalContent();
            List<List<Object>> table = genReqArgs(null, responseArgs);
            content3.addTable(table);
            title3.add(content3);
        }
        return title;
    }

    private static Element printThirdpart(int order, Object parent, Doc doc) throws DocumentException {

        Element title = printFolder(order, parent, doc);
        NormalContent content = new NormalContent();
        content.addContent(doc.getContent());
        addContent(title, content);
        return title;
    }
}
