package com.iwhalecloud.data.collect.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwhalecloud.data.collect.domain.RouteStationInfo;
import com.iwhalecloud.data.collect.domain.SegMentInfo;
import com.iwhalecloud.data.collect.domain.SegStationInfo;
import com.iwhalecloud.data.collect.domain.response.RouteStationInfoRep;
import com.iwhalecloud.data.collect.domain.response.SegMentInfoRep;
import com.iwhalecloud.data.collect.domain.response.SegStationInfoRep;
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
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.*;
import java.math.BigDecimal;
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
     * @param url    请求地址
     * @param params 请求实体
     * @return String
     * @throws
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求 ， 请求数据默认使用json格式 ， 默认使用UTF - 8编码)
     * @author wangxy
     * @date 2018年5月10日 下午6:11:05
     */
    public static String sendPost(String url, Map<String, Object> params, Integer httpConnectTimeout, Integer httpSocketTimeout, Integer hcRequestTimeout) throws Exception {
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
        // 设置默认请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        // 将map转成json
        JSONObject data = JSONObject.parseObject(JSON.toJSONString(params));
        return sendPost(url, headers, data, httpConnectTimeout, httpSocketTimeout, hcRequestTimeout);
    }
    /**
     * @param url                请求地址
     * @param headers            请求头
     * @param data               请求实体
     * @param httpConnectTimeout 字符集
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求)
     * @author wangxy
     * @date 2018年5月10日 下午4:36:17
     */
    public static String sendPost(String url, Map<String, String> headers, JSONObject data, Integer httpConnectTimeout, Integer httpSocketTimeout, Integer hcRequestTimeout) throws Exception {
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
        //log.info("进入post请求方法...");
        //log.info("请求入参：URL= " + url);
        //log.info("请求入参：headers=" + JSON.toJSONString(headers));
        //log.info("请求入参：data=" + JSON.toJSONString(data));
        // 请求返回结果
        String resultJson = null;
        // 创建Client
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建HttpPost对象
        HttpPost httpPost = new HttpPost();

        try {
            // 设置请求地址
            httpPost.setURI(new URI(url));
            // 设置请求头
            if (headers != null) {
                Header[] allHeader = new BasicHeader[headers.size()];
                int i = 0;
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    allHeader[i] = new BasicHeader(entry.getKey(), entry.getValue());
                    i++;
                }
                httpPost.setHeaders(allHeader);
            }
            // 设置实体
            //httpPost.setEntity(new StringEntity(JSON.toJSONString(data)));
            httpPost.setEntity(new StringEntity(JSON.toJSONString(data), CONTENT_TYPE_FORM, DEFAULT_CHARSET));
            // 超时时间设置
            RequestConfig config = RequestConfig.custom().setSocketTimeout(httpSocketTimeout).setConnectTimeout(httpConnectTimeout).setConnectionRequestTimeout(hcRequestTimeout).build();
            //.setConnectionRequestTimeout(HTTP_CONNECTION_REQUEST_TIMEOUT).build();
            httpPost.setConfig(config);
            // 发送请求,返回响应对象
            CloseableHttpResponse response = client.execute(httpPost);
            // 获取响应状态
            int status = response.getStatusLine().getStatusCode();
            if (status == org.apache.http.HttpStatus.SC_OK) {
                // 获取响应结果
                resultJson = EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
            } else {
                log.error("响应失败，状态码：" + status);
                log.error("响应失败：" + EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET));
            }

        } catch (Exception e) {
            //log.error("发送post请求失败", e);
            e.printStackTrace();

        } finally {
            httpPost.releaseConnection();
        }
        return resultJson;
    }

    public static Map<String, Object> getParams(String RouteID){
        Map<String, Object> params = new HashMap<>();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        String timeStamp = df.format(new Date());
        String randomValue = "" + (int) (100 + Math.random() * (999 - 100 + 1));
        String signKe = EncryptHelper.SignUp("943a871e607611efa459ad3f1a49204d",timeStamp+ randomValue);
        params.put("TimeStamp", timeStamp);
        params.put("Random", randomValue);
        params.put("SignKey", signKe);
        params.put("PackageName","com.hengqin");
        params.put("RouteID",RouteID);
        return params;
    }
    /**
     * 解析数据
     * @param jsonBody
     * @return
     */
    public static Map<String, Object> getCustomer(String jsonBody){
        Map<String, Object>  custom = null;
        try {
            custom = JSON.parseObject(jsonBody);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析数据失败",e);
        }
        return custom;
    }
    /**
     * 解析数据
     * @param jsonBody
     * @return
     */
    public static List<RouteStationInfoRep> getCustomerArray(String jsonBody) throws Exception{
        List<RouteStationInfoRep> listData = new ArrayList<>();
        try {
            listData = JSON.parseArray(jsonBody, RouteStationInfoRep.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析数据失败",e);
        }
        return listData;
    }
    public static void test() throws Exception {
        String allUrl = "http://120.238.166.245:62100/BusService/Query_AllSubRouteData/";
        String routUrl = "http://120.238.166.245:62100/BusService/Query_RouteStatData/";
        Map<String, Object> params = HttpClientUtils.getParams(null);
        String result = HttpClientUtils.sendPost(allUrl, params, 0, 0, 0);
        Map<String, Object> buMap = getCustomer(result);//查询返回所有线路信息
        log.info("buMap：{}",buMap);
        if(MapUtil.isNotEmpty(buMap)){
            List<RouteStationInfo> rst = new ArrayList<>(16);
            List<SegMentInfo> smf = new ArrayList<>(16);
            List<SegStationInfo> ssf = new ArrayList<>(16);

            List<Map<String, Object>> itemMapList = (List<Map<String, Object>>) MapUtils.getObject(buMap, "RouteList", new ArrayList<>(16));
            log.info("itemMapList：{}",itemMapList);
            for(Map<String, Object> itemMap : itemMapList){
                if(MapUtil.isNotEmpty(itemMap)){

                    //根据线路ID查询所有线路下站点信息
                    Integer RouteID = (Integer) itemMap.get("RouteID");
                    if(RouteID == 357){
                        continue;
                    }
                    Map<String, Object> paramsRout = HttpClientUtils.getParams(EncryptHelper.EncryptCodeString(RouteID.toString()));

                    String resultRout = HttpClientUtils.sendGet(routUrl, paramsRout, 0, 0, 0);
                    log.info(resultRout);
                    List<RouteStationInfoRep> customerArray = getCustomerArray(resultRout);

                    for(RouteStationInfoRep rs : customerArray){
                        //线路信息
                        RouteStationInfo stationInfo = new RouteStationInfo();
                        stationInfo.setIsBrt(rs.getIsBRT());
                        stationInfo.setIsMainSub(rs.getIsmainsub());
                        stationInfo.setIsHaveSubRouteCombine(rs.getRsHaveSubRouteCombine());
                        stationInfo.setRouteMemo(rs.getRouteMemo());
                        stationInfo.setRouteName(rs.getRouteName());
                        stationInfo.setRouteNameExt(rs.getRouteNameExt());
                        stationInfo.setRouteId(rs.getRouteID());
                        stationInfo.setRouteType(rs.getRouteType());
                        rst.add(stationInfo);

                        for (SegMentInfoRep sf :rs.getSegmentList()) {
                            if(rs.getSegmentList().size() > 0){
                                //单程信息
                                Integer segmentID = sf.getSegmentID();
                                SegMentInfo smfInfo = new SegMentInfo();
                                smfInfo.setSegmentId(segmentID);
                                smfInfo.setSegmentName(sf.getSegmentName());
                                smfInfo.setAmapId(sf.getSegmentName());
                                smfInfo.setRouteId(RouteID);
                                smfInfo.setFirtLastShiftInfo(sf.getFirtLastShiftInfo());
                                smfInfo.setMemos(sf.getMemos());
                                smfInfo.setDrawType(sf.getDrawType());
                                smfInfo.setFirstTime(sf.getFirstTime());
                                smfInfo.setFirtLastShiftInfo2(sf.getFirtLastShiftInfo2());
                                smfInfo.setBaiduMapId(sf.getBaidumapid());
                                smfInfo.setNormalTimeSpan(Integer.valueOf(sf.getNormalTimeSpan()));
                                smfInfo.setRunDirection(sf.getRunDirection());
                                smfInfo.setRoutePrice(Integer.valueOf(sf.getRoutePrice() != null ? sf.getRoutePrice() : 0 ));
                                smfInfo.setPeakTimeSpan(Integer.valueOf(sf.getPeakTimeSpan()));
                                smfInfo.setLastTime(sf.getLastTime());
                                smf.add(smfInfo);

                                for (SegStationInfoRep ss : sf.getStationList()) {
                                    if(sf.getStationList().size() > 0){
                                        //站点信息
                                        SegStationInfo ssfInfo = new SegStationInfo();
                                        ssfInfo.setSegmentId(segmentID);
                                        ssfInfo.setStationId(ss.getStationID());
                                        ssfInfo.setStationName(ss.getStationName());
                                        ssfInfo.setLatitude(ss.getStationPostion().getLatitude());
                                        ssfInfo.setLongitude(ss.getStationPostion().getLongitude());
                                        ssfInfo.setSpeed(ss.getSpeed());
                                        ssfInfo.setDualSerial(ss.getDualSerial());
                                        ssfInfo.setStationMemo(ss.getStationName());
                                        ssfInfo.setStationNo(ss.getStationNO());
                                        ssfInfo.setStationSort(ss.getStationSort());
                                        ssf.add(ssfInfo);
                                    }
                                }
                            }
                        }
                    }
                    log.info("rs:{}", rst.size());
                    log.info("smf:{}", smf.size());
                    log.info("ssf:{}", ssf.size());
                }
            }
        }

    }
    public static void main(String[] args) throws Exception {
        /**String url = "http://120.238.166.245:62100/BusService/Query_AllSubRouteData/";
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
        //String s = sendGet(url, params,HTTP_CONNECT_TIMEOUT,HTTP_SOCKET_TIMEOUT,HTTP_CONNECTION_REQUEST_TIMEOUT);
        String s = sendPost(url, params,HTTP_CONNECT_TIMEOUT,HTTP_SOCKET_TIMEOUT,HTTP_CONNECTION_REQUEST_TIMEOUT);
        log.debug(s);

         **/

        test();


    }
}
