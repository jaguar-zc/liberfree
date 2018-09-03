package cn.liberfree.http;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Http(s)请求工具
 */
public class Request {

    public final static Logger logger = LoggerFactory.getLogger(Request.class);

    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    public static final String CONTENT_TYPE_TEXT = "Content-Type";

    public static final String CHARSET = "UTF-8";

    public static final String HTTPS = "https";

    public static final Integer BUFFER = 1024;

    public enum RequestMethod {GET, POST, DELETE, PUT, PATCH}

    private RuntimeException exception ;

    private String url;

    private String encoding = CHARSET;

    private Integer connectTimeout = 300000;

    private Integer readTimeout = 300000;

    private byte[] body = new byte[0];

    private String contentType = CONTENT_TYPE;

    private RequestMethod method = RequestMethod.GET;

    private Map<String, String> headers = new HashMap<String, String>();

    private Map<String, String> params = new HashMap<String, String>();

    public Request() {
        setContentType(CONTENT_TYPE);
    }

    public void setException(RuntimeException exception) {
        this.exception = exception;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setContentType(String contentType) {
        headers.put(CONTENT_TYPE_TEXT,contentType);
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public void addHeaders(String name, String value) {
        this.headers.put(name, value);
    }

    public void addParams(String name, String value) {
        this.params.put(name, value);
    }

    public HttpURLConnection createConnection(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection httpURLConnection = null;
        httpURLConnection = (HttpURLConnection) url1.openConnection();
        httpURLConnection.setRequestMethod(method.name());
        httpURLConnection.setConnectTimeout(connectTimeout);// 连接超时时间
        httpURLConnection.setReadTimeout(readTimeout);// 读取结果超时时间
        httpURLConnection.setDoInput(true); // 可读
        if (isDoOutput()) {
            httpURLConnection.setDoOutput(true); // 可写
        }
        httpURLConnection.setUseCaches(false);// 取消缓存
        for (Map.Entry<String, String> header : headers.entrySet()) {
            httpURLConnection.setRequestProperty(header.getKey(), header.getValue());
        }
        //支持Https单向
        if (url1.getProtocol().equals(HTTPS)) {
            ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(new HttpSSLSocketFactory());
            ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(new TrustAnyHostnameVerifier());
        }
        return httpURLConnection;
    }


    public boolean isDoOutput() {
        return (method == RequestMethod.POST || method == RequestMethod.PUT || method == RequestMethod.PATCH);
    }


    private HttpURLConnection build() throws IOException {
        String url = this.url;
        if(logger != null){
            logger.info("url - " + url);
        }else{
            System.out.println("url - " + url);
        }
        if (!isDoOutput()) {
            if (params.size() > 0) {
                String requestParamString = getRequestParamString(params);
                url = url+"?" + requestParamString;
                if(logger != null){
                    logger.info("params - " + requestParamString);
                }else{
                    System.out.println("params - " + requestParamString);
                }
            }
        }else{
           if (params.size() > 0 && body == null) {
                body = getRequestParamString(params).getBytes(encoding);
            }
            if(body != null){
                if(logger != null){
                    logger.info("body - " + new String(body,encoding));
                }else{
                    System.out.println("body - " + new String(body,encoding));
                }
            }
        }
        HttpURLConnection connection = createConnection(url);
        return connection;
    }


    public Response req() {

        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            HttpURLConnection con = build();
            con.connect();
            if (isDoOutput()) {
                outputStream = con.getOutputStream();
                outputStream.write(body);
            }
            inputStream = con.getInputStream();
            byte[] resp = getStreamBytes(inputStream);
            int responseCode = con.getResponseCode();
            Map<String, List<String>> headerFields = con.getHeaderFields();
            HashMap<String, String> headers = new HashMap<String, String>();
            for (Map.Entry<String, List<String>> header : headerFields.entrySet()) {
                if(!"".equals(header.getKey()) && header.getKey() != null){
                    headers.put(header.getKey(),getHeaderValue(header.getValue()));
                }
            }
            if(logger != null) {
                logger.info("responseCode - " + responseCode);
                logger.info("responseBody - " + new String(resp, encoding));
            }else{
                System.out.println("responseCode - " + responseCode);
                System.out.println("responseBody - " + new String(resp, encoding));
            }
            return new Response(resp,headers , responseCode);
        } catch (Exception e) {
            if(exception != null){
                throw exception;
            }else{
                throw new RuntimeException(e);
            }
        } finally {
            close(outputStream, inputStream);
        }
    }

    public String getHeaderValue(List<String> values){
        StringBuffer v = new StringBuffer("");
        for (int i = 0; i < values.size(); i++) {
            v.append(values.get(i));
            if((i+1) != values.size()){
                v.append(",");
            }
        }
        return v.toString();
    }


    private byte[] getStreamBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER];
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] b = baos.toByteArray();
        baos.close();
        return b;
    }


    private void close(OutputStream outputStream, InputStream in) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 转换参数为  k-v&k=v
     * @param requestParam
     * @return
     */
    private String getRequestParamString(Map<String, String> requestParam) {
        StringBuffer sf = new StringBuffer("");
        String reqstr = "";
        if (null != requestParam && 0 != requestParam.size()) {
            for (Map.Entry<String, String> en : requestParam.entrySet()) {
                try {
                    sf.append(en.getKey()
                            + "="
                            + (null == en.getValue() || "".equals(en.getValue()) ? "" : URLEncoder.encode(en.getValue(), encoding)) + "&");
                } catch (UnsupportedEncodingException e) {
                    if(logger != null){
                        logger.info(e.getMessage());
                    }else{
                        System.out.println(e.getMessage());
                    }
                    return "";
                }
            }
            reqstr = sf.substring(0, sf.length() - 1);
        }
        return reqstr;
    }


    /**
     * 响应对象
     */
    public class Response {
        public byte[] content;
        public Map<String, String> headers;
        public int status;

        public Response(byte[] content, Map<String, String> headers, int status) {
            this.content = content;
            this.headers = headers;
            this.status = status;
        }

        public byte[] getContent() {
            return content;
        }
        public String getContentString() {
            try {
                return new String(content,encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public int getStatus() {
            return status;
        }
    }

    public class HttpSSLSocketFactory extends SSLSocketFactory {

        private SSLContext getSSLContext() {
            return createEasySSLContext();
        }

        @Override
        public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
                                   int arg3) throws IOException {
            return getSSLContext().getSocketFactory().createSocket(arg0, arg1,
                    arg2, arg3);
        }

        @Override
        public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
                throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(arg0, arg1,
                    arg2, arg3);
        }

        @Override
        public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
            return getSSLContext().getSocketFactory().createSocket(arg0, arg1);
        }

        @Override
        public Socket createSocket(String arg0, int arg1) throws IOException,
                UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(arg0, arg1);
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return null;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return null;
        }

        @Override
        public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3)
                throws IOException {
            return getSSLContext().getSocketFactory().createSocket(arg0, arg1,
                    arg2, arg3);
        }

        private SSLContext createEasySSLContext() {
            try {
                SSLContext context = SSLContext.getInstance("SSL");
                context.init(null, new TrustManager[]{DefaultTrustManager.manger}, null);
                return context;
            } catch (Exception e) {
                if(logger != null) {
                    logger.info(e.getMessage());
                }else{
                    System.out.println(e.getMessage());
                }
                return null;
            }
        }
    }

    private static class DefaultTrustManager implements X509TrustManager {
        static DefaultTrustManager manger = new DefaultTrustManager();

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    public static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            //直接返回true 不验证服务端证书
            return true;
        }
    }
}
