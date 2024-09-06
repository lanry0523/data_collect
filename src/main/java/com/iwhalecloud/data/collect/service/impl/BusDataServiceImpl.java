package com.iwhalecloud.data.collect.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.data.collect.dao.RouteStationInfoDao;
import com.iwhalecloud.data.collect.dao.SegMentInfoDao;
import com.iwhalecloud.data.collect.dao.SegStationInfoDao;
import com.iwhalecloud.data.collect.domain.ConvertAmapCoordinate;
import com.iwhalecloud.data.collect.domain.RouteStationInfo;
import com.iwhalecloud.data.collect.domain.SegMentInfo;
import com.iwhalecloud.data.collect.domain.SegStationInfo;
import com.iwhalecloud.data.collect.domain.response.RouteStationInfoRep;
import com.iwhalecloud.data.collect.domain.response.SegMentInfoRep;
import com.iwhalecloud.data.collect.domain.response.SegStationInfoRep;
import com.iwhalecloud.data.collect.service.BusDataService;
import com.iwhalecloud.data.collect.util.EncryptHelper;
import com.iwhalecloud.data.collect.util.HttpClientUtils;
import com.iwhalecloud.data.collect.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.session.SqlSessionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class BusDataServiceImpl implements BusDataService {

    @Value("${amap.key}")
    private String amapKey;
    @Value("${amap.url}")
    private String amapUrl;
    @Value("${bus.allUrl}")
    private String allUrl;
    @Value("${bus.routUrl}")
    private String routUrl;
    @Resource
    private SegMentInfoDao segMentInfoDao;
    @Resource
    private SegStationInfoDao segStationInfoDao;
    @Resource
    private RouteStationInfoDao routeStationInfoDao;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void syncBusDataInfo(){
        log.info("<-------------------获取公交定时任务启动----------------->");

         try{
            Map<String, Object> params = HttpClientUtils.getParams(null);
            String result = HttpClientUtils.sendPost(allUrl, params, 0, 0, 0);
            Map<String, Object> buMap = getCustomer(result);//查询返回所有线路信息
            if(MapUtil.isNotEmpty(buMap)){
                List<RouteStationInfo> rst = new ArrayList<>(16);
                List<SegMentInfo> smf = new ArrayList<>(16);
                List<SegStationInfo> ssf = new ArrayList<>();

                List<Map<String, Object>> itemMapList = (List<Map<String, Object>>) MapUtils.getObject(buMap, "RouteList", new ArrayList<>(16));
                int ii = 0;
                int sumCount = 0;
                for(Map<String, Object> itemMap : itemMapList){
                    if(MapUtil.isNotEmpty(itemMap)){
                        //根据线路ID查询所有线路下站点信息
                        Integer RouteID = (Integer) itemMap.get("RouteID");
                        Set<Integer> idList = new HashSet<>();
                        idList.add(22601501);
                        idList.add(22615101);
                        idList.add(20601301);
                        if(idList.contains(RouteID)){
                            continue;
                        }
                        Map<String, Object> paramsRout = HttpClientUtils.getParams(EncryptHelper.EncryptCodeString(RouteID.toString()));
                        String resultRout = HttpClientUtils.sendGet(routUrl, paramsRout, 0, 0, 0);
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

//                                    Set<ConvertAmapCoordinate> cac = new HashSet<>();
                                    for (SegStationInfoRep ss : sf.getStationList()) {
                                        if(sf.getStationList().size() > 0){
                                            //站点信息
                                            SegStationInfo ssfInfo = new SegStationInfo();
                                            Map<String, Object> paramsAmap = new HashMap<>();
                                            ConvertAmapCoordinate ca = new ConvertAmapCoordinate();
                                            ca.setStationId(ss.getStationID());
                                            //查询库里是否已存在转换过的站点坐标，存在跳过
                                            log.info("查询是否存在---------,{}",ss.getStationID());
                                            List<ConvertAmapCoordinate> convertAmapCoordinates = segStationInfoDao.queryById(ss.getStationID());
                                            if(convertAmapCoordinates.size() > 0){
                                                //如果已存在直接使用
                                                log.info("已存在直接使用,{}",convertAmapCoordinates);

                                                ssfInfo.setLatitude(convertAmapCoordinates.get(0).getLatitude());
                                                ssfInfo.setLongitude(convertAmapCoordinates.get(0).getLongitude());
                                                log.info("坐标,{},{}",ssfInfo.getLongitude(),ssfInfo.getLatitude());
                                            }else {
                                                log.info("新坐标转换查询---------,{}",ss.getStationID());
                                                StringBuilder stringBuilder = new StringBuilder();
                                                stringBuilder.append(ss.getStationPostion().getLongitude()).append(",").append(ss.getStationPostion().getLatitude());
                                                paramsAmap.put("locations", stringBuilder.toString());
                                                paramsAmap.put("coordsys", "gps");
                                                paramsAmap.put("output", "json");
                                                paramsAmap.put("key", amapKey);
                                                String amap = HttpClientUtils.sendGet(amapUrl, paramsAmap, 0, 0, 0);
                                                Map<String, Object> aMapMap = getCustomer(amap);
                                                String str = aMapMap.get("locations").toString();
                                                String[] parts = str.split(",");
                                                log.info("拆分坐标结果：,{},{}",new BigDecimal(parts[0]),new BigDecimal(parts[1]));
                                                ca.setLongitude(new BigDecimal(parts[0]));
                                                ca.setLatitude(new BigDecimal(parts[1]));
                                                log.info("转换坐标结果：,{},{}",ca.getLongitude(),ca.getLatitude());
                                                //cac.add(ca);
                                                int i = segStationInfoDao.insertAmap(ca);

                                                log.info("坐标转换数;,{}", i);
                                                ssfInfo.setLatitude(ca.getLatitude());
                                                ssfInfo.setLongitude(ca.getLongitude());
                                            }

                                            ssfInfo.setSegmentId(segmentID);
                                            ssfInfo.setStationId(ss.getStationID());
                                            ssfInfo.setStationName(ss.getStationName());
                                            ssfInfo.setSpeed(ss.getSpeed());
                                            ssfInfo.setDualSerial(ss.getDualSerial());
                                            ssfInfo.setStationMemo(ss.getStationName());
                                            ssfInfo.setSngserialId(ss.getSngserialId());
                                            ssf.add(ssfInfo);
                                            ii +=1;
                                            log.info("站点数量;,{}",ii);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                routeStationInfoDao.batchDelete();
                segMentInfoDao.batchDelete();
                segStationInfoDao.batchDelete();
                log.info("站点集合,{},{},{}",ssf.size(),smf.size(),rst.size());
                int i3 = routeStationInfoDao.batchInsert(rst);
                log.info("线路入库,{}",i3);
                int i2 = segMentInfoDao.batchInsert(smf);
                log.info("单程入库,{}",i2);
                if(ssf.isEmpty() || ssf == null){
                    log.info("站点未获取到何任数据");
                    return;
                }
                int nThreads = 10;
                int size = ssf.size();
                ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
                for(int i = 0; i < nThreads; i++){
                    final List<SegStationInfo> rstList = ssf.subList(size / nThreads * i,size / nThreads * (i+1));
                    log.info("批量入库开始,{} 当前入库数量,{}",i,rstList.size());
                    int i1 = segStationInfoDao.batchInsert(rstList);
                }
                executorService.shutdown();
                int sum = segStationInfoDao.selectCheckStation();
                log.info("set StationInfo cont,{}",sum);
                log.info("get bus data The time is now : " + new java.util.Date());
            }
         }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int instBus() throws Exception {
        final String key = amapKey;//"9adc24e0e7a6147a492f851d87460537";
        String url = amapUrl;//"https://restapi.amap.com/v3/assistant/coordinate/convert";
        Map<String, Object> params = new HashMap<>();

        int i = 0;
        Set<ConvertAmapCoordinate> cac = new HashSet<>();

        List<SegStationInfo> segStationInfos = segStationInfoDao.selectCheckList();
        for(SegStationInfo segStationInfo : segStationInfos){

            ConvertAmapCoordinate ca = new ConvertAmapCoordinate();
            ca.setStationId(segStationInfo.getStationId());
            //查询库里是否已存在转换过的站点坐标，存在跳过
            List<ConvertAmapCoordinate> convertAmapCoordinates = segStationInfoDao.queryById(segStationInfo.getStationId());
            if(convertAmapCoordinates.size() > 0){
                log.info("已转换，,{}",convertAmapCoordinates);
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(segStationInfo.getLongitude()).append(",").append(segStationInfo.getLatitude());
            String  result = sb.toString();
            log.info("坐标转换前：,{}",result);
            params.put("locations", result);
            params.put("coordsys", "gps");
            params.put("output", "json");
            params.put("key",key);
            String amap = HttpClientUtils.sendGet(url, params, 0, 0, 0);
            Map<String, Object> buMap = getCustomer(amap);
            log.info("坐标转换后：,{}",buMap.get("locations").toString());
            String str = buMap.get("locations").toString();
            String[] parts = str.split(",");

            ca.setLongitude(new BigDecimal(parts[0]));
            ca.setLatitude(new BigDecimal(parts[1]));
            cac.add(ca);
            int i1 = segStationInfoDao.insertAmap(ca);
            log.info("add ,{}",i1);
        }
        log.info("已存在转换数;,{}",i);
//        if(cac.size() > 0){
//            int cont = segStationInfoDao.batchInsertAmap(new ArrayList<>(cac));
//            log.info("总数;,{}",cont);
//        }
        return 0;
    }

    @Override
    public int batchDeleteAmapList() {
        return segStationInfoDao.batchDeleteAmapList();
    }

    @Override
    public int selectCount() {
        return segStationInfoDao.selectStationCount();
    }


    /**
     * 解析数据
     * @param jsonBody
     * @return
     */
    public Map<String, Object> getCustomer(String jsonBody){
        Map<String, Object>  custom = null;
        try {
            custom = JsonUtils.json2map(jsonBody);
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
}
