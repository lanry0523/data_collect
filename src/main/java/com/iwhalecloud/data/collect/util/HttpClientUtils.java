package com.iwhalecloud.data.collect.util;

import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        Long now = new Date().getTime();
        String timestamp = Long.toString((long) Math.floor(now / 1000));
        String nonce = Long.toHexString(now) + "-" + Long.toHexString((long) Math.floor(Math.random() * 0xFFFFFF));
        String token= "";//EncryptUtil.getSHA256StrJava(timestamp + paasToken + nonce + timestamp);

        Map<String, String> header = new HashMap<>();
        header.put("x-tif-paasid", "");
        header.put("x-tif-timestamp", timestamp);
        header.put("x-tif-signature", "");
        header.put("Authorization", "Bearer " + token);
        header.put("Cache-Control","no-cache");
        return header;
    }
}
