package cn.com.xiaoyaoji.docformatter.paragraph2.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.Chunk;
import org.junit.Test;

import cn.com.xiaoyaoji.docformatter.paragraph2.NormalContent;
import cn.com.xiaoyaoji.docformatter.paragraph2.NormalSubTitle;
import cn.com.xiaoyaoji.docformatter.paragraph2.NormalTitle;
import cn.com.xiaoyaoji.docformatter.paragraph2.RootTitle;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfExportTest {

    public static final String DEST = "target/chapter_title.pdf";

    @Test
    public void test() throws IOException, DocumentException {

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(DEST));
        document.open();
        putData(document);
        document.close();
    }

    private void putData(Document document) throws DocumentException {

        getRootTile(document);
        getInfo(document);
        getChapter1(document);
        getChapter2(document);
    }

    private void getRootTile(Document document) throws DocumentException {

        RootTitle title = new RootTitle(document, "小幺鸡使用说明");
        document.add(title);
    }

    private void getInfo(Document document) throws DocumentException {

        NormalContent content = new NormalContent(12, false);
        content.setAlignment(Element.ALIGN_RIGHT);
        Map<Object, Object> info = new HashMap<Object, Object>();
        info.put("作者", "Hello World");
        info.put("更新时间", "2016-09-21 10:17:05");
        content.addContent(info);
        content.add(Chunk.NEXTPAGE);
        document.add(content);
    }

    private void getChapter1(Document document) throws DocumentException {

        NormalTitle title = new NormalTitle("文档说明", 1);
        NormalContent content = new NormalContent();
        List<String> data = new ArrayList<String>();
        data.add("支持http、websocket测试");
        data.add("支持json，xml，txt，jsonp等测试");
        data.add("支持form-data，x-www-form-urlencoded ，raw，binary 上传格式");
        data.add("支持rest地址，http://www.test.com/test/{id}.json 这样的地址会自动替换id");
        data.add("由于浏览器跨域访问限制，为了更好的体验服务，请下载安装扩展 https://chrome.google.com/webstore/detail/%E5%B0%8F%E5%B9%BA%E9%B8%A1/omohfhadnbkakganodaofplinheljnbd");
        data.add("如果在使用过程中发现界面排版错误，请切换至chrome最新版本浏览器。其他浏览器，其他浏览器正在适配中。");
        data.add("如果配置了地址前缀，则该模块下所有url访问时会自动带上前缀。");
        data.add("如果有任何建议或意见都可以在这儿留言 http://www.xiaoyaoji.com.cn/help.html");
        data.add("有任何bug 都可以在这儿提出来 http://git.oschina.net/zhoujingjie/apiManager/issues");
        data.add("支持markdown编辑器");
        data.add("支持mock");
        data.add("支持变量");
        content.addContent(data);
//        String html = "<code><br>    使用方式：$变量名$<br>    如：$host$ = <a href=\"http://www.xiaoyaoji.com.cn\">http://www.xiaoyaoji.com.cn</a><br>    则：$host$/test/url = <a href=\"http://www.xiaoyaoji.com.cn/test/url\">http://www.xiaoyaoji.com.cn/test/url</a><br></code>";
        String html = "<p><h1><ul><li>范德萨发、hello发送是的</li><li>2、markdown</li></ul><h5><ol><li>是否是一个正确的文档</li></ol></h5></h1></p><p><br></p>";
        content.addHtml(html);
        title.add(content);
        document.add(title);
    }

    private void getChapter2(Document document) throws DocumentException {

        NormalTitle title = new NormalTitle("JSON测试", 2);
        NormalSubTitle title1 = new NormalSubTitle(title, "对象数组", 1);
        NormalSubTitle title2 = new NormalSubTitle(title1, "基本信息", 1, 10);
        title2.getFont().setSize(12F);
        NormalContent content = new NormalContent();
        Map<String, String> data = new LinkedHashMap<String, String>();
        data.put("请求类型", "HTTP");
        data.put("接口地址", "http://www.xiaoyaoji.com.cn/test/json/3.json");
        data.put("请求方式", "GET");
        data.put("数据类型", "X-WWW-FORM-URLENCODED");
        data.put("响应类型", "JSON");
        data.put("接口状态", "启用");
        content.addContent(data);
        title2.add(content);
        NormalSubTitle title3 = new NormalSubTitle(title1, "响应数据", 2, 10);
        title3.getFont().setSize(12F);
        NormalContent content3 = new NormalContent();
        List<List<Object>> table = new ArrayList<List<Object>>();
        Object[] header = new Object[]{"参数名称", "是否必须", "数据类型", "描述"};
        Object[] param1 = new Object[]{"menu", "true", "object", ""};
        Object[] param2 = new Object[]{"menu.header", "true", "string", ""};
        Object[] param3 = new Object[]{"items.header", "true", "array[object]", ""};
        Object[] param4 = new Object[]{"items.id", "true", "string", ""};
        Object[] param5 = new Object[]{"items.label", "true", "string", ""};
        table.add(Arrays.asList(header));
        table.add(Arrays.asList(param1));
        table.add(Arrays.asList(param2));
        table.add(Arrays.asList(param3));
        table.add(Arrays.asList(param4));
        table.add(Arrays.asList(param5));
        content3.addTable(table);
        title3.add(content3);
        document.add(title);
    }
}
