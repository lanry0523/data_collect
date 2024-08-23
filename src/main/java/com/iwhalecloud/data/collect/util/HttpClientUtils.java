package com.iwhalecloud.data.collect.util;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpClientUtils {


    /**
     * Default charset for each request.
     */
    public static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * Default timeout for each request.
     */
    public static final int DEFAULT_TIMEOUT = 60000;

    public static final String CONTENT_TYPE = "application/json";

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String CONTENT_TYPE_TEXT = "text/plain";
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    // 连接主机超时（20s）   20,设置连接超时时间，单位毫秒。setConnectTimeout
    public static final int HTTP_CONNECT_TIMEOUT = 60 * 1000;

    // 从主机读取数据超时（1min）  60，请求获取数据的超时时间（即响应时间），单位毫秒，如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用setSocketTimeout
    public static final int HTTP_SOCKET_TIMEOUT = 60 * 1000;

    //setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
    public static final int HTTP_CONNECTION_REQUEST_TIMEOUT = 60 * 1000;

    /**
     * 人脸识别专用post方法
     *
     * @param url    请求地址
     * @param params 参数
     * @param header 请求头参数
     * @return
     */
    public static String postJson(String url, Map<String, Object> params, Map<String, String> header, Integer timout) throws IOException {
        PostMethod method = new PostMethod(url);
        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> entry : header.entrySet()
            ) {
                method.setRequestHeader(entry.getKey(), entry.getValue());
            }

        }
        method.setRequestHeader("Content-Type", CONTENT_TYPE);
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, DEFAULT_CHARSET);
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, timout == null ? 60000 : timout);
        constructMethodParams(params, method, CONTENT_TYPE, DEFAULT_CHARSET);
        return executeMethod(method);
    }

    /**
     * 私有方法构造方法参数
     *
     * @param params 参数
     * @param method 方法
     * @throws UnsupportedEncodingException
     */
    private static void constructMethodParams(Map<String, Object> params, EntityEnclosingMethod method, String contentType, String charset) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        if (MapUtils.isNotEmpty(params)) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        }
        method.setRequestEntity(new StringRequestEntity(jsonObject.toString(), contentType, charset));
    }
    /**
     * 执行post或者get方法
     *
     * @param method post或者get方法
     * @return
     * @throws IOException
     */
    private static String executeMethod(HttpMethodBase method) throws IOException {
        HttpClient httpClient = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        httpClient.getParams().setIntParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        try {

            InputStream stream = method.getResponseBodyAsStream();

            String charset = method.getRequestCharSet();
            String response;
            try {
                // try to turn stream to bytes.
                byte[] responseBytes = IOUtils.toByteArray(stream);
                // decode with the reponse charset.
                if (StringUtils.isNotBlank(charset)) {
                    try {
                        response = new String(responseBytes, charset);
                    } catch (UnsupportedEncodingException e) {
                        response = new String(responseBytes);
                    }
                } else {
                    // decode with default charset.
                    response = new String(responseBytes);
                }

                return response;
            } finally {
                // close stream.
                IOUtils.closeQuietly(stream);
            }
        } finally {
            // release connection.
            method.releaseConnection();
        }
    }

    private String sendPost(String url, Map<String, Object> params) throws IOException {

        Map<String, String> headerMap = getHeader();
        String result = HttpClientUtils.postJson(url, params, headerMap, null);
        return result;
    }
    private Map<String, String> getHeader() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String timeStamp = df.format(new Date());
        String randomValue = "" + (int) (100 + Math.random() * (999 - 100 + 1));
        String signKe = EncryptHelper.SignUp("943a871e607611efa459ad3f1a49204d",timeStamp+ randomValue);

        Map<String, String> header = new HashMap<>();
        header.put("TimeStamp", timeStamp);
        header.put("Random", randomValue);
        header.put("SignKey", signKe);
        header.put("PackageName","com.hengqin");
        return header;
    }

    /**
     * @param url                请求地址
     * @param params             请求参数
     * @param httpConnectTimeout 编码
     * @return String
     * @throws
     * @Title: sendGet
     * @Description: TODO(发送get请求)
     * @author wangxy
     * @date 2018年5月14日 下午2:39:01
     */
    @SuppressWarnings("deprecation")
    public static String sendGet(String url, Map<String, Object> params, Integer httpConnectTimeout, Integer httpSocketTimeout, Integer hcRequestTimeout) throws Exception {
        //如果没值给对象赋值
        if (httpConnectTimeout == null || httpConnectTimeout.intValue() == 0) {
            httpConnectTimeout = HTTP_CONNECT_TIMEOUT;
        }
        if (httpSocketTimeout == null || httpSocketTimeout.intValue() == 0) {
            httpSocketTimeout = HTTP_SOCKET_TIMEOUT;
        }
        if (hcRequestTimeout == null || hcRequestTimeout.intValue() == 0) {
            hcRequestTimeout = HTTP_CONNECTION_REQUEST_TIMEOUT;
        }
        //log.info("进入get请求方法...");
        //log.info("请求入参：URL= " + url);
        //log.info("请求入参：params=" + JSON.toJSONString(params));
        // 请求结果
        String resultJson = null;
        // 创建client
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建HttpGet
        HttpGet httpGet = new HttpGet();
        try {
            //url = URLEncoder.encode(url);
            //URL urlObject = new URL(url);
            //URI uri = new URI(urlObject.getProtocol(), urlObject.getHost(), urlObject.get,urlObject.getPath(), urlObject.getQuery(), null);
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            // 封装参数
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key).toString());
                }
            }
            URI uri = builder.build();
            RequestConfig config = RequestConfig.custom().setSocketTimeout(httpSocketTimeout).setConnectTimeout(httpConnectTimeout).setConnectionRequestTimeout(hcRequestTimeout).build();
            //.setConnectionRequestTimeout(HTTP_CONNECTION_REQUEST_TIMEOUT).build();
            httpGet.setConfig(config);
            //log.info("请求地址："+ uri);
            // 设置请求地址
            httpGet.setURI(uri);
            // 发送请求，返回响应对象
            CloseableHttpResponse response = client.execute(httpGet);
            // 获取响应状态
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                // 获取响应数据
                resultJson = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
            } else {
                log.error("响应失败，状态码：" + status);
                log.error("响应失败：" + EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET));


            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送get请求失败",e);
        } finally {
            httpGet.releaseConnection();
        }
        return resultJson;
    }
    /**
     * 发送一个get请求
     *
     * @param url 地址
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        GetMethod method = new GetMethod(url);
        HttpMethodParams hmp = method.getParams();

        hmp.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, DEFAULT_CHARSET);
        hmp.setParameter(HttpMethodParams.SO_TIMEOUT, DEFAULT_TIMEOUT);

        return executeMethod(method);
    }


    public static String sendGet(String url, Map<String, String> parameters) {
        String result = "";
        BufferedReader in = null;// 读取响应输入流
        StringBuffer sb = new StringBuffer();// 存储参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            String full_url = url + "?" + params;
            System.out.println(full_url);
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(full_url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 建立实际的连接
            httpConn.connect();
            // 响应头部获取
            Map<String, List<String>> headers = httpConn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : headers.keySet()) {
                System.out.println(key + "\t：\t" + headers.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应,并设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://cloud.tiamaes.com:17570/BusService/Query_AllSubRouteData/";
        Map<String, Object> params = new HashMap<>();


        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String timeStamp = df.format(new Date());
        String randomValue = "" + (int) (100 + Math.random() * (999 - 100 + 1));
        String signKe = EncryptHelper.SignUp("943a871e607611efa459ad3f1a49204d",timeStamp+ randomValue);
        params.put("TimeStamp", timeStamp);
        params.put("Random", randomValue);
        params.put("SignKey", signKe);
        params.put("PackageName","com.hengqin");
        log.debug(signKe);
        log.debug(timeStamp);
        log.debug(randomValue);
        //String s1 = get(url + "?TimeStamp="+timeStamp+"&Random="+randomValue+"&signKey="+signKe+"PackageName=com.hengqin");
        String s = sendGet(url, params,HTTP_CONNECT_TIMEOUT,HTTP_SOCKET_TIMEOUT,HTTP_CONNECTION_REQUEST_TIMEOUT);
        log.debug(s);
    }
}
