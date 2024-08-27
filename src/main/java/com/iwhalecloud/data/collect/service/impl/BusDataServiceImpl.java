package com.iwhalecloud.data.collect.service.impl;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwhalecloud.data.collect.dao.BusDataDao;
import com.iwhalecloud.data.collect.dao.RouteStationInfoDao;
import com.iwhalecloud.data.collect.dao.SegMentInfoDao;
import com.iwhalecloud.data.collect.dao.SegStationInfoDao;
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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BusDataServiceImpl implements BusDataService {
    @Resource
    private RouteStationInfoRep routeStationInfoRep;
    @Resource
    private SegMentInfoDao segMentInfoDao;
    @Resource
    private SegStationInfoDao segStationInfoDao;
    @Autowired
    private RouteStationInfoDao routeStationInfoDao;


    @Override
    public void syncBusDataInfo() throws Exception{
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
                    routeStationInfoDao.batchInsert(rst);
                    segMentInfoDao.batchInsert(smf);
                    segStationInfoDao.batchInsert(ssf);
                }
            }
        }
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
