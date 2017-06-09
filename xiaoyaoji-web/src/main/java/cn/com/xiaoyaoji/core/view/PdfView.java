package cn.com.xiaoyaoji.core.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author zhoujingjie
 * @date 2016-05-24
 */
public class PdfView extends AbstractView {
    private byte[] data;
    private String fileName;

    public PdfView(byte[] data, String fileName) {
        this.data = data;
        this.fileName = fileName;
    }

    /**
     * Subclasses must implement this method to actually render the view.
     * <p>The first step will be preparing the request: In the JSP case,
     * this would mean setting model objects as request attributes.
     * The second step will be the actual rendering of the view,
     * for example including the JSP via a RequestDispatcher.
     *
     * @param model    combined output Map (never {@code null}),
     *                 with dynamic values taking precedence over static attributes
     * @param request  current HTTP request
     * @param response current HTTP response
     * @throws Exception if rendering failed
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setContentLength(data.length);
        fileName = URLEncoder.encode(fileName, Charset.forName(CharEncoding.UTF_8).displayName());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\";");
        OutputStream os = response.getOutputStream();
        os.write(data);
        os.flush();
    }
}
