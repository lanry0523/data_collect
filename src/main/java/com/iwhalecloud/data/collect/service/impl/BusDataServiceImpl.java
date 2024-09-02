package com.iwhalecloud.data.collect.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.iwhalecloud.data.collect.dao.RouteStationInfoDao;
import com.iwhalecloud.data.collect.dao.SegMentInfoDao;
import com.iwhalecloud.data.collect.dao.SegStationInfoDao;
import com.iwhalecloud.data.collect.domain.RouteStationInfo;
import com.iwhalecloud.data.collect.domain.SegMentInfo;
import com.iwhalecloud.data.collect.domain.SegStationInfo;
import com.iwhalecloud.data.collect.domain.StationRouteCorrelation;
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
import org.springframework.beans.factory.annotation.Autowired;
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
            String allUrl = "http://120.238.166.245:62100/BusService/Query_AllSubRouteData/";
            String routUrl = "http://120.238.166.245:62100/BusService/Query_RouteStatData/";
            Map<String, Object> params = HttpClientUtils.getParams(null);
            String result = HttpClientUtils.sendPost(allUrl, params, 0, 0, 0);
            Map<String, Object> buMap = getCustomer(result);//查询返回所有线路信息
            if(MapUtil.isNotEmpty(buMap)){
                List<RouteStationInfo> rst = new ArrayList<>(16);
                List<SegMentInfo> smf = new ArrayList<>(16);
                List<SegStationInfo> ssf = new ArrayList<>();
                List<Map<String, Object>> itemMapList = (List<Map<String, Object>>) MapUtils.getObject(buMap, "RouteList", new ArrayList<>(16));
                for(Map<String, Object> itemMap : itemMapList){
                    if(MapUtil.isNotEmpty(itemMap)){
                        //根据线路ID查询所有线路下站点信息
                        Integer RouteID = (Integer) itemMap.get("RouteID");
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
                                            ssfInfo.setSngserialId(ss.getSngserialId());
                                            ssf.add(ssfInfo);

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
         }catch (SqlSessionException sql){
             sql.printStackTrace();
         }
         catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int instBus() {
        List<SegStationInfo> rstList = new ArrayList<>();
        for(int i =0;i < 3000;i++){
            SegStationInfo ssfInfo = new SegStationInfo();
            ssfInfo.setSegmentId(i+1);
            ssfInfo.setStationId("222");
            ssfInfo.setStationName("测试");
            ssfInfo.setLatitude(new BigDecimal(2.345));
            ssfInfo.setLongitude(new BigDecimal(2.345));
            ssfInfo.setSpeed("33");
            ssfInfo.setDualSerial(32);
            ssfInfo.setStationMemo("333");
            ssfInfo.setSngserialId(112);
            rstList.add(ssfInfo);
        }
        int i = segStationInfoDao.batchInsert(rstList);
        return i;
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
