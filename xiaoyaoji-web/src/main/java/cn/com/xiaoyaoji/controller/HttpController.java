package cn.com.xiaoyaoji.controller;

import cn.com.xiaoyaoji.core.annotations.Ignore;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author zhoujingjie
 *         created on 2017/8/16
 */
@RestController
@RequestMapping("/http")
public class HttpController {

    @Ignore
    @RequestMapping(value = "proxy")
    public void proxy(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String url = request.getHeader("url");
        OutputStream os =null;
        if(StringUtils.isBlank(url)){
        }else {
            HttpURLConnection connection = null;
            try {
                String queryString = request.getQueryString();
                String suffix=null;
                if (StringUtils.isNoneBlank(queryString)) {
                    queryString = queryString.replaceAll("&_=\\d*", "");
                    if(url.contains("?")){
                        suffix="&";
                    }else{
                        suffix="?";
                    }
                    suffix += URLEncoder.encode(queryString,"UTF-8");
                }
                URL u = new URL(url+suffix);
                if(u.getPath().equals("")){
                    u = new URL(url+"/"+suffix);
                }
                if("https".equals(u.getProtocol())){
                    HttpsURLConnection httpsURLConnection =(HttpsURLConnection) u.openConnection();
                    SSLContext ctx = SSLContext.getInstance("SSL");
                    ctx.init(new KeyManager[0],new TrustManager[]{new DefaultTrustManager()},new SecureRandom());
                    SSLContext.setDefault(ctx);
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return false;
                        }
                    });
                    connection = httpsURLConnection;
                }else {
                    connection = (HttpURLConnection) u.openConnection();
                }
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod(request.getMethod());
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(30000);
                connection.setUseCaches(false);

                //设置header
                for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); ) {
                    String header = e.nextElement();
                    switch (header.toLowerCase()){
                        case "url":
                        case "host":
                        case "connection":
                        case "referer":
                            continue;
                        default:
                            connection.setRequestProperty(header,request.getHeader(header));
                    }
                }
                connection.connect();
                InputStream in=null;
                String method =request.getMethod();

                if(method.equals("POST")
                        || method.equals("PUT")){
                    os = connection.getOutputStream();
                    in = request.getInputStream();
                    byte[] b = new byte[1024];
                    int len = 0;
                    while ((len = in.read(b)) != -1) {
                        os.write(b, 0, len);
                    }
                    os.close();
                    in.close();
                }
                in = connection.getInputStream();
                Map<String, List<String>> headerMap = connection.getHeaderFields();
                if (headerMap != null) {
                    for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                        if(entry.getKey() == null)
                            continue;
                        StringBuilder headerValue = new StringBuilder();
                        for (Iterator<String> it = entry.getValue().iterator(); it.hasNext(); ) {
                            String next = it.next();
                            headerValue.append(next);
                            while (it.hasNext()) {
                                next = it.next();
                                headerValue.append(", ").append(next);
                            }
                        }
                        if(entry.getKey().equals("Transfer-Encoding") && headerValue.toString().equals("chunked")){
                            continue;
                        }
                        response.setHeader(entry.getKey(),headerValue.toString());
                    }
                }
                os = response.getOutputStream();
                if(in != null) {
                    IOUtils.copy(in, os);
                }
                response.setContentType(connection.getContentType());
                response.setStatus(connection.getResponseCode());
                response.setCharacterEncoding(connection.getContentEncoding());
            } catch (Exception e) {
                response.setStatus(503);
                response.getWriter().write(e.getMessage());
            }finally {
                IOUtils.closeQuietly(os);
                if(connection != null){
                    connection.disconnect();
                }
            }
        }
        /*return new _HashMap<String,Object>()
                .add("_type_","proxy")
                .add("statusCode",statusCode)
                .add("content",content)
                .add("headers",returnHeaders)
                ;*/
    }


    private static class DefaultTrustManager implements X509TrustManager {

        /* (non-Javadoc)
         * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
         */
        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {

        }

        /* (non-Javadoc)
         * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
         */
        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] arg0, String arg1)
                throws CertificateException {

        }

        /* (non-Javadoc)
         * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }


    }
}
