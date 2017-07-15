package cn.com.xiaoyaoji.docformatter.paragraph2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.itextpdf.text.*;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class NormalContent extends Paragraph {

    private static final long serialVersionUID = 7905478670996186260L;

    private static final Logger log = LoggerFactory.getLogger(NormalContent.class);

    private static final String SYMBOL_LIST = "●";
    private static final float DEFAULT_TABLE_INDENT = -48F;
    public static final float DEFAULT_SPACE_BEFORE = 10F;

    private boolean useParentStyle = true;

    private Font font = new Font(BaseInfo.cjkFont, 10.5f, Font.NORMAL, BaseColor.BLACK);

    private void init(Font font){

        setSpacingBefore(DEFAULT_SPACE_BEFORE);
        setFont(font == null ? this.font : font);
    }

    public NormalContent() {

        init(null);
    }

    public NormalContent(float fontSize, boolean bold) {

        font = new Font(BaseInfo.font, fontSize, bold ? Font.BOLD : Font.NORMAL, BaseColor.BLACK);
        init(font);
    }

    public NormalContent(float fontSize, boolean bold, boolean useParentStyle) {

        this(fontSize, bold);
        setUseParentStyle(useParentStyle);
    }

    public void setUseParentStyle(boolean useParentStyle) {
        this.useParentStyle = useParentStyle;
    }

    private void userParentStyle(Paragraph child) {

        if (!useParentStyle) {
            return;
        }
        child.setAlignment(getAlignment());
    }

    @SuppressWarnings("unchecked")
    public void addContent(Object data) {

        if (data == null) {
            return;
        }
        if (data instanceof Map) {
            for (Entry<Object, Object> entry : ((Map<Object, Object>) data).entrySet()) {
                boolean isElementKey = entry.getKey() instanceof Element;
                boolean isElementValue = entry.getValue() instanceof Element;
                boolean isHtml = entry.getValue() instanceof HtmlBean;
                if (!isElementKey && !isElementValue) {
                    if (isHtml) {
                        addHtml("<div>" + entry.getKey() + ":</div><div>" + ((HtmlBean) entry.getValue()).getHtml()+ "</div>");
                    } else {
                        Paragraph tmp = new Paragraph(entry.getKey() + ":" + entry.getValue(), font);
                        userParentStyle(tmp);
                        add(tmp);
                    }
                } else if (isElementKey) {
                    add((Element) entry.getKey());
                    add(new Chunk(":" + entry.getValue().toString(), font));
                } else {
                    add(new Chunk(entry.getKey().toString() + ":", font));
                    add((Element) entry.getKey());
                }
            }
        } else if (data instanceof List) {
            com.itextpdf.text.List dataList = new com.itextpdf.text.List();
            dataList.setListSymbol(new Chunk(SYMBOL_LIST, font));
            for (Object item : (List<?>) data) {
                if (item instanceof Chunk) {
                    dataList.add(new ListItem((Chunk) item));
                } else {
                    dataList.add(new ListItem(item.toString(), font));
                }
            }
            add(dataList);
        } else {
            if (data instanceof Element) {
                add((Element) data);
            } else {
                Paragraph tmp = new Paragraph(data.toString(), font);
                userParentStyle(tmp);
                add(tmp);
            }
        }
    }

    public void addHtml(String content) {

        if (content == null) {
            return;
        }
        try {
            content = content.replace("<br></br>", "<br>");
            content = content.replace("</br>", "<br>");
            content = content.replace("<br>", "<br></br>");
            content = content.replace("<br/>", "<br></br>");
            ElementList list = parseToElementList(content);
            for (Element element : list) {
                add(element);
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static ElementList parseToElementList(String html) throws IOException {

        CSSResolver cssResolver = new StyleAttrCSSResolver();
        HtmlPipelineContext hpc = new HtmlPipelineContext(new CssAppliersImpl(new AsianFontProvider()));
        hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory(Tags.getHtmlTagProcessorFactory());
        ElementList elements = new ElementList();
        ElementHandlerPipeline end = new ElementHandlerPipeline(elements, (Pipeline) null);
        HtmlPipeline htmlPipeline = new HtmlPipeline(hpc, end);
        CssResolverPipeline cssPipeline = new CssResolverPipeline(cssResolver, htmlPipeline);
        XMLWorker worker = new XMLWorker(cssPipeline, true);
        XMLParser p = new XMLParser(worker);
        p.parse(new ByteArrayInputStream(html.getBytes()));
        return elements;
    }

    public void addTable(List<List<Object>> data) {

        if (data == null) {
            return;
        }

        PdfPTable table = null;
        for (List<Object> row : data) {
            if (table == null) {
                table = new PdfPTable(row.size());
            }
            for (Object cell : row) {
                if (cell == null) {
                    cell = "";
                }
                if (cell instanceof Phrase) {
                    table.addCell((Phrase) cell);
                } else {
                    table.addCell(new Phrase(cell.toString(), font));
                }
            }
        }
        setIndentationLeft(DEFAULT_TABLE_INDENT);
        add(table);
    }
}
