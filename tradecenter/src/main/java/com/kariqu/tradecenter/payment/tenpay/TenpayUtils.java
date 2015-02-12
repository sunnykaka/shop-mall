package com.kariqu.tradecenter.payment.tenpay;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;


/**
 * 财付通支付工具类
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-21 下午3:40
 */
public class TenpayUtils {


    public static final String PARTNER = "1215343201";

    public static final String PAY_GATEWAY = "https://gw.tenpay.com/gateway/pay.htm?";

    public static final String NOTIFY_GATEWAY = "https://gw.tenpay.com/gateway/simpleverifynotifyid.xml?";

    public static final String SIGN_TYPE = "MD5";

    public static final String INPUT_CHARSET = "UTF-8";

    public static final String SERVICE_VERSION = "1.0";

    public static final String KEY = "bfe7249f88e4cc8135e7a2ae5b80a908";

    public static final Log logger = LogFactory.getLog(TenpayUtils.class);

    public static boolean verify(String reqType, Map<String, String> params) {
        try {
            boolean success = createSign(params).equalsIgnoreCase(params.get("sign"));
            if ("return".equals(reqType)) {
                return success;
            } else if ("notify".equals(reqType)) {
                if (!success) return false;
                Map<String, String> notifyParams = new TreeMap<String, String>();
                notifyParams.put("partner", PARTNER);
                notifyParams.put("notify_id", params.get("notify_id"));
                notifyParams.put("sign", createSign(notifyParams));
                StringBuilder sb = new StringBuilder();
                Set es = notifyParams.entrySet();
                for (Object e : es) {
                    Map.Entry entry = (Map.Entry) e;
                    String k = (String) entry.getKey();
                    String v = (String) entry.getValue();
                    sb.append(k + "=" + URLEncoder.encode(v, INPUT_CHARSET) + "&");
                }

                //去掉最后一个&
                String reqPars = sb.substring(0, sb.lastIndexOf("&"));

                TenpayHttpClient httpClient = new TenpayHttpClient();
                httpClient.setReqContent(NOTIFY_GATEWAY + reqPars);
                httpClient.setTimeOut(5);
                if (httpClient.call()) {
                    String resContent = httpClient.getResContent();
                    Map<String, String> xmlMap = doParse(resContent);
                    String sign = createSign(xmlMap);
                    if (sign != null && sign.equalsIgnoreCase(xmlMap.get("sign")) && "0".equals(xmlMap.get("retcode"))) {
                        return true;
                    }
                }
                logger.error("httpClient.errorinfo=: " + httpClient.getErrInfo());
                return false;
            }
            return false;
        } catch (Exception e) {
            logger.error("财付通回调验证异常", e);
            return false;
        }

    }

    private static Map<String, String> doParse(String content) throws JDOMException, IOException {
        SortedMap<String, String> result = new TreeMap<String, String>();
        //解析xml,得到map
        Map xmlMap = XMLUtil.doXMLParse(content);

        //设置参数
        for (Object o : xmlMap.keySet()) {
            String k = (String) o;
            String v = (String) xmlMap.get(k);
            result.put(k, v);
        }
        return result;

    }

    private static String createSign(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        Set es = params.entrySet();
        for (Object e : es) {
            Map.Entry entry = (Map.Entry) e;
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }

        sb.append("key=" + KEY);
        logger.info("财富通回传参数签名为：" + MD5Encode(sb.toString(), INPUT_CHARSET));
        return MD5Encode(sb.toString(), INPUT_CHARSET);
    }

    public static String buildMysign(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        Set es = params.entrySet();
        for (Object e : es) {
            Map.Entry entry = (Map.Entry) e;
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + TenpayUtils.KEY);
        return MD5Encode(sb.toString(), INPUT_CHARSET).toLowerCase();

    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) resultSb.append(byteToHexString(aB));
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static String MD5Encode(String origin, String charsetname) {
        String resultString = origin;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception ignored) {
        }
        return resultString;
    }

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};


    /**
     * 财付通http或者https网络通信客户端<br/>
     * ========================================================================<br/>
     * api说明：<br/>
     * setReqContent($reqContent),设置请求内容，无论post和get，都用get方式提供<br/>
     * getResContent(), 获取应答内容<br/>
     * setMethod(method),设置请求方法,post或者get<br/>
     * getErrInfo(),获取错误信息<br/>
     * setCertInfo(certFile, certPasswd),设置证书，双向https时需要使用<br/>
     * setCaInfo(caFile), 设置CA，格式未pem，不设置则不检查<br/>
     * setTimeOut(timeOut)， 设置超时时间，单位秒<br/>
     * getResponseCode(), 取返回的http状态码<br/>
     * call(),真正调用接口<br/>
     * getCharset()/setCharset(),字符集编码<br/>
     * <p/>
     * ========================================================================<br/>
     */
    private static class TenpayHttpClient {

        private static final String USER_AGENT_VALUE =
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows XP)";

        private static final String JKS_CA_FILENAME =
                "tenpay_cacert.jks";

        private static final String JKS_CA_ALIAS = "tenpay";

        private static final String JKS_CA_PASSWORD = "";

        /**
         * ca证书文件
         */
        private File caFile;

        /**
         * 证书文件
         */
        private File certFile;

        /**
         * 证书密码
         */
        private String certPasswd;

        /**
         * 请求内容，无论post和get，都用get方式提供
         */
        private String reqContent;

        /**
         * 应答内容
         */
        private String resContent;

        /**
         * 请求方法
         */
        private String method;

        /**
         * 错误信息
         */
        private String errInfo;

        /**
         * 超时时间,以秒为单位
         */
        private int timeOut;

        /**
         * http应答编码
         */
        private int responseCode;

        /**
         * 字符编码
         */
        private String charset;

        private InputStream inputStream;

        public TenpayHttpClient() {
            this.caFile = null;
            this.certFile = null;
            this.certPasswd = "";

            this.reqContent = "";
            this.resContent = "";
            this.method = "POST";
            this.errInfo = "";
            this.timeOut = 30;//30秒

            this.responseCode = 0;
            this.charset = "GBK";

            this.inputStream = null;
        }

        /**
         * 设置证书信息
         *
         * @param certFile   证书文件
         * @param certPasswd 证书密码
         */
        public void setCertInfo(File certFile, String certPasswd) {
            this.certFile = certFile;
            this.certPasswd = certPasswd;
        }

        /**
         * 设置ca
         *
         * @param caFile
         */
        public void setCaInfo(File caFile) {
            this.caFile = caFile;
        }

        /**
         * 设置请求内容
         *
         * @param reqContent 表求内容
         */
        public void setReqContent(String reqContent) {
            this.reqContent = reqContent;
        }

        /**
         * 获取结果内容
         *
         * @return String
         * @throws IOException
         */
        public String getResContent() {
            try {
                this.doResponse();
            } catch (IOException e) {
                this.errInfo = e.getMessage();
                //return "";
            }
            return this.resContent;
        }

        /**
         * 设置请求方法post或者get
         *
         * @param method 请求方法post/get
         */
        public void setMethod(String method) {
            this.method = method;
        }

        /**
         * 获取错误信息
         *
         * @return String
         */
        public String getErrInfo() {
            return this.errInfo;
        }

        /**
         * 设置超时时间,以秒为单位
         *
         * @param timeOut 超时时间,以秒为单位
         */
        public void setTimeOut(int timeOut) {
            this.timeOut = timeOut;
        }

        /**
         * 获取http状态码
         *
         * @return int
         */
        public int getResponseCode() {
            return this.responseCode;
        }

        /**
         * 执行http调用。true:成功 false:失败
         *
         * @return boolean
         */
        public boolean call() {

            boolean isRet = false;

            //http
            if (null == this.caFile && null == this.certFile) {
                try {
                    this.callHttp();
                    isRet = true;
                } catch (IOException e) {
                    this.errInfo = e.getMessage();
                }
                return isRet;
            }

            //https
            try {
                this.callHttps();
                isRet = true;
            } catch (UnrecoverableKeyException e) {
                this.errInfo = e.getMessage();
            } catch (KeyManagementException e) {
                this.errInfo = e.getMessage();
            } catch (CertificateException e) {
                this.errInfo = e.getMessage();
            } catch (KeyStoreException e) {
                this.errInfo = e.getMessage();
            } catch (NoSuchAlgorithmException e) {
                this.errInfo = e.getMessage();
            } catch (IOException e) {
                this.errInfo = e.getMessage();
            }

            return isRet;

        }

        protected void callHttp() throws IOException {

            if ("POST".equals(this.method.toUpperCase())) {
                String url = HttpClientUtil.getURL(this.reqContent);
                String queryString = HttpClientUtil.getQueryString(this.reqContent);
                byte[] postData = queryString.getBytes(this.charset);
                this.httpPostMethod(url, postData);

                return;
            }

            this.httpGetMethod(this.reqContent);

        }

        protected void callHttps() throws IOException, CertificateException,
                KeyStoreException, NoSuchAlgorithmException,
                UnrecoverableKeyException, KeyManagementException {

            // ca目录
            String caPath = this.caFile.getParent();

            File jksCAFile = new File(caPath + "/"
                    + TenpayHttpClient.JKS_CA_FILENAME);
            if (!jksCAFile.isFile()) {
                X509Certificate cert = (X509Certificate) HttpClientUtil
                        .getCertificate(this.caFile);

                FileOutputStream out = new FileOutputStream(jksCAFile);

                // store jks file
                HttpClientUtil.storeCACert(cert, TenpayHttpClient.JKS_CA_ALIAS,
                        TenpayHttpClient.JKS_CA_PASSWORD, out);

                out.close();

            }

            FileInputStream trustStream = new FileInputStream(jksCAFile);
            FileInputStream keyStream = new FileInputStream(this.certFile);

            SSLContext sslContext = HttpClientUtil.getSSLContext(trustStream,
                    TenpayHttpClient.JKS_CA_PASSWORD, keyStream, this.certPasswd);

            //关闭流
            keyStream.close();
            trustStream.close();

            if ("POST".equals(this.method.toUpperCase())) {
                String url = HttpClientUtil.getURL(this.reqContent);
                String queryString = HttpClientUtil.getQueryString(this.reqContent);
                byte[] postData = queryString.getBytes(this.charset);

                this.httpsPostMethod(url, postData, sslContext);

                return;
            }

            this.httpsGetMethod(this.reqContent, sslContext);

        }

        /**
         * 以http post方式通信
         *
         * @param url
         * @param postData
         * @throws IOException
         */
        protected void httpPostMethod(String url, byte[] postData)
                throws IOException {

            HttpURLConnection conn = HttpClientUtil.getHttpURLConnection(url);

            this.doPost(conn, postData);
        }

        /**
         * 以http get方式通信
         *
         * @param url
         * @throws IOException
         */
        protected void httpGetMethod(String url) throws IOException {

            HttpURLConnection httpConnection =
                    HttpClientUtil.getHttpURLConnection(url);

            this.setHttpRequest(httpConnection);

            httpConnection.setRequestMethod("GET");

            this.responseCode = httpConnection.getResponseCode();

            this.inputStream = httpConnection.getInputStream();

        }

        /**
         * 以https get方式通信
         *
         * @param url
         * @param sslContext
         * @throws IOException
         */
        protected void httpsGetMethod(String url, SSLContext sslContext)
                throws IOException {

            SSLSocketFactory sf = sslContext.getSocketFactory();

            HttpsURLConnection conn = HttpClientUtil.getHttpsURLConnection(url);

            conn.setSSLSocketFactory(sf);

            this.doGet(conn);

        }

        protected void httpsPostMethod(String url, byte[] postData,
                                       SSLContext sslContext) throws IOException {

            SSLSocketFactory sf = sslContext.getSocketFactory();

            HttpsURLConnection conn = HttpClientUtil.getHttpsURLConnection(url);

            conn.setSSLSocketFactory(sf);

            this.doPost(conn, postData);

        }

        /**
         * 设置http请求默认属性
         *
         * @param httpConnection
         */
        protected void setHttpRequest(HttpURLConnection httpConnection) {

            //设置连接超时时间
            httpConnection.setConnectTimeout(this.timeOut * 1000);

            //User-Agent
            httpConnection.setRequestProperty("User-Agent",
                    TenpayHttpClient.USER_AGENT_VALUE);

            //不使用缓存
            httpConnection.setUseCaches(false);

            //允许输入输出
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);

        }

        /**
         * 处理应答
         *
         * @throws IOException
         */
        protected void doResponse() throws IOException {

            if (null == this.inputStream) {
                return;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(this.inputStream, this.charset));

            //获取应答内容
            this.resContent = HttpClientUtil.bufferedReader2String(reader);

            //关闭流
            reader.close();

            //关闭输入流
            this.inputStream.close();

        }

        /**
         * post方式处理
         *
         * @param conn
         * @param postData
         * @throws IOException
         */
        protected void doPost(HttpURLConnection conn, byte[] postData)
                throws IOException {

            // 以post方式通信
            conn.setRequestMethod("POST");

            // 设置请求默认属性
            this.setHttpRequest(conn);

            // Content-Type
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            BufferedOutputStream out = new BufferedOutputStream(conn
                    .getOutputStream());

            final int len = 1024; // 1KB
            HttpClientUtil.doOutput(out, postData, len);

            // 关闭流
            out.close();

            // 获取响应返回状态码
            this.responseCode = conn.getResponseCode();

            // 获取应答输入流
            this.inputStream = conn.getInputStream();

        }

        /**
         * get方式处理
         *
         * @param conn
         * @throws IOException
         */
        protected void doGet(HttpURLConnection conn) throws IOException {

            //以GET方式通信
            conn.setRequestMethod("GET");

            //设置请求默认属性
            this.setHttpRequest(conn);

            //获取响应返回状态码
            this.responseCode = conn.getResponseCode();

            //获取应答输入流
            this.inputStream = conn.getInputStream();
        }


    }

    /**
     * Http客户端工具类<br/>
     * 这是内部调用类，请不要在外部调用。
     *
     * @author miklchen
     */
    private static class HttpClientUtil {

        public static final String SunX509 = "SunX509";
        public static final String JKS = "JKS";
        public static final String PKCS12 = "PKCS12";
        public static final String TLS = "TLS";

        /**
         * get HttpURLConnection
         *
         * @param strUrl url地址
         * @return HttpURLConnection
         * @throws IOException
         */
        public static HttpURLConnection getHttpURLConnection(String strUrl)
                throws IOException {
            URL url = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            return httpURLConnection;
        }

        /**
         * get HttpsURLConnection
         *
         * @param strUrl url地址
         * @return HttpsURLConnection
         * @throws IOException
         */
        public static HttpsURLConnection getHttpsURLConnection(String strUrl)
                throws IOException {
            URL url = new URL(strUrl);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url
                    .openConnection();
            return httpsURLConnection;
        }

        /**
         * 获取不带查询串的url
         *
         * @param strUrl
         * @return String
         */
        public static String getURL(String strUrl) {

            if (null != strUrl) {
                int indexOf = strUrl.indexOf("?");
                if (-1 != indexOf) {
                    return strUrl.substring(0, indexOf);
                }

                return strUrl;
            }

            return strUrl;

        }

        /**
         * 获取查询串
         *
         * @param strUrl
         * @return String
         */
        public static String getQueryString(String strUrl) {

            if (null != strUrl) {
                int indexOf = strUrl.indexOf("?");
                if (-1 != indexOf) {
                    return strUrl.substring(indexOf + 1, strUrl.length());
                }

                return "";
            }

            return strUrl;
        }

        /**
         * 查询字符串转换成Map<br/>
         * name1=key1&name2=key2&...
         *
         * @param queryString
         * @return
         */
        public static Map queryString2Map(String queryString) {
            if (null == queryString || "".equals(queryString)) {
                return null;
            }

            Map m = new HashMap();
            String[] strArray = queryString.split("&");
            for (int index = 0; index < strArray.length; index++) {
                String pair = strArray[index];
                HttpClientUtil.putMapByPair(pair, m);
            }

            return m;

        }

        /**
         * 把键值添加至Map<br/>
         * pair:name=value
         *
         * @param pair name=value
         * @param m
         */
        public static void putMapByPair(String pair, Map m) {

            if (null == pair || "".equals(pair)) {
                return;
            }

            int indexOf = pair.indexOf("=");
            if (-1 != indexOf) {
                String k = pair.substring(0, indexOf);
                String v = pair.substring(indexOf + 1, pair.length());
                if (null != k && !"".equals(k)) {
                    m.put(k, v);
                }
            } else {
                m.put(pair, "");
            }
        }

        /**
         * BufferedReader转换成String<br/>
         * 注意:流关闭需要自行处理
         *
         * @param reader
         * @return String
         * @throws IOException
         */
        public static String bufferedReader2String(BufferedReader reader) throws IOException {
            StringBuffer buf = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
                buf.append("\r\n");
            }

            return buf.toString();
        }

        /**
         * 处理输出<br/>
         * 注意:流关闭需要自行处理
         *
         * @param out
         * @param data
         * @param len
         * @throws IOException
         */
        public static void doOutput(OutputStream out, byte[] data, int len)
                throws IOException {
            int dataLen = data.length;
            int off = 0;
            while (off < dataLen) {
                if (len >= dataLen) {
                    out.write(data, off, dataLen);
                } else {
                    out.write(data, off, len);
                }

                //刷新缓冲区
                out.flush();

                off += len;

                dataLen -= len;
            }

        }

        /**
         * 获取SSLContext
         *
         * @param trustPasswd
         * @param keyPasswd
         * @return
         * @throws NoSuchAlgorithmException
         * @throws KeyStoreException
         * @throws IOException
         * @throws CertificateException
         * @throws UnrecoverableKeyException
         * @throws KeyManagementException
         */
        public static SSLContext getSSLContext(
                FileInputStream trustFileInputStream, String trustPasswd,
                FileInputStream keyFileInputStream, String keyPasswd)
                throws NoSuchAlgorithmException, KeyStoreException,
                CertificateException, IOException, UnrecoverableKeyException,
                KeyManagementException {

            // ca
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(HttpClientUtil.SunX509);
            KeyStore trustKeyStore = KeyStore.getInstance(HttpClientUtil.JKS);
            trustKeyStore.load(trustFileInputStream, HttpClientUtil
                    .str2CharArray(trustPasswd));
            tmf.init(trustKeyStore);

            final char[] kp = HttpClientUtil.str2CharArray(keyPasswd);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(HttpClientUtil.SunX509);
            KeyStore ks = KeyStore.getInstance(HttpClientUtil.PKCS12);
            ks.load(keyFileInputStream, kp);
            kmf.init(ks, kp);

            SecureRandom rand = new SecureRandom();
            SSLContext ctx = SSLContext.getInstance(HttpClientUtil.TLS);
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), rand);

            return ctx;
        }

        /**
         * 获取CA证书信息
         *
         * @param cafile CA证书文件
         * @return Certificate
         * @throws CertificateException
         * @throws IOException
         */
        public static Certificate getCertificate(File cafile)
                throws CertificateException, IOException {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            FileInputStream in = new FileInputStream(cafile);
            Certificate cert = cf.generateCertificate(in);
            in.close();
            return cert;
        }

        /**
         * 字符串转换成char数组
         *
         * @param str
         * @return char[]
         */
        public static char[] str2CharArray(String str) {
            if (null == str) return null;

            return str.toCharArray();
        }

        /**
         * 存储ca证书成JKS格式
         *
         * @param cert
         * @param alias
         * @param password
         * @param out
         * @throws KeyStoreException
         * @throws NoSuchAlgorithmException
         * @throws CertificateException
         * @throws IOException
         */
        public static void storeCACert(Certificate cert, String alias,
                                       String password, OutputStream out) throws KeyStoreException,
                NoSuchAlgorithmException, CertificateException, IOException {
            KeyStore ks = KeyStore.getInstance("JKS");

            ks.load(null, null);

            ks.setCertificateEntry(alias, cert);

            // store keystore
            ks.store(out, HttpClientUtil.str2CharArray(password));

        }

        public static InputStream String2Inputstream(String str) {
            return new ByteArrayInputStream(str.getBytes());
        }

    }

    /**
     * xml工具类
     *
     * @author miklchen
     */
    public static class XMLUtil {

        /**
         * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
         *
         * @param strxml
         * @return
         * @throws JDOMException
         * @throws IOException
         */
        public static Map doXMLParse(String strxml) throws JDOMException, IOException {
            if (null == strxml || "".equals(strxml)) {
                return null;
            }

            Map m = new HashMap();
            InputStream in = HttpClientUtil.String2Inputstream(strxml);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            List list = root.getChildren();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String k = e.getName();
                String v = "";
                List children = e.getChildren();
                if (children.isEmpty()) {
                    v = e.getTextNormalize();
                } else {
                    v = XMLUtil.getChildrenText(children);
                }

                m.put(k, v);
            }

            //关闭流
            in.close();

            return m;
        }

        /**
         * 获取子结点的xml
         *
         * @param children
         * @return String
         */
        public static String getChildrenText(List children) {
            StringBuffer sb = new StringBuffer();
            if (!children.isEmpty()) {
                Iterator it = children.iterator();
                while (it.hasNext()) {
                    Element e = (Element) it.next();
                    String name = e.getName();
                    String value = e.getTextNormalize();
                    List list = e.getChildren();
                    sb.append("<" + name + ">");
                    if (!list.isEmpty()) {
                        sb.append(XMLUtil.getChildrenText(list));
                    }
                    sb.append(value);
                    sb.append("</" + name + ">");
                }
            }

            return sb.toString();
        }

        /**
         * 获取xml编码字符集
         *
         * @param strxml
         * @return
         * @throws IOException
         * @throws JDOMException
         */
        public static String getXMLEncoding(String strxml) throws JDOMException, IOException {
            InputStream in = HttpClientUtil.String2Inputstream(strxml);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            in.close();
            return (String) doc.getProperty("encoding");
        }


    }


}
