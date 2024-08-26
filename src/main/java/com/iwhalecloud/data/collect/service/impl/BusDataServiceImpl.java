package com.iwhalecloud.data.collect.service.impl;

import cn.hutool.core.map.MapUtil;
import com.iwhalecloud.data.collect.dao.BusDataDao;
import com.iwhalecloud.data.collect.domain.RouteStationInfo;
import com.iwhalecloud.data.collect.domain.SegMentInfo;
import com.iwhalecloud.data.collect.domain.SegStationInfo;
import com.iwhalecloud.data.collect.service.BusDataService;
import com.iwhalecloud.data.collect.util.EncryptHelper;
import com.iwhalecloud.data.collect.util.HttpClientUtils;
import com.iwhalecloud.data.collect.util.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
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
    private BusDataDao busDataDao;


    @Override
    public void syncBusDataInfo() throws Exception{
        String allUrl = "http://120.238.166.245:62100/BusService/Query_AllSubRouteData/";
        String routUrl = "http://120.238.166.245:62100/BusService/Query_RouteStatData/";
        Map<String, Object> params = HttpClientUtils.getParams();
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
                    Map<String, Object> paramsRout = HttpClientUtils.getParams();
                    paramsRout.put("RouteID", EncryptHelper.EncryptCodeString(itemMap.get("RouteID").toString()));
                    String resultRout = HttpClientUtils.sendPost(routUrl, paramsRout, 0, 0, 0);
                    Map<String, Object> routMap = getCustomer(resultRout);
                    //线路信息
                    RouteStationInfo stationInfo = new RouteStationInfo();
                    stationInfo.setRouteId(Integer.valueOf(routMap.get("RouteID").toString()));
                    stationInfo.setRouteName(routMap.get("RouteName").toString());
                    stationInfo.setRouteType(routMap.get("RouteType").toString());
                    stationInfo.setRouteMemo(routMap.get("RouteMemo").toString());
                    stationInfo.setRouteNameExt(routMap.get("RouteNameExt").toString());
                    stationInfo.setIsMainSub(routMap.get("IsMainSub").toString());
                    stationInfo.setIsHaveSubRouteCombine(routMap.get("IsHaveSubRouteCombine").toString());
                    stationInfo.setIsBrt(routMap.get("IsBrt").toString());
                    rst.add(stationInfo);

                    List<Map<String, Object>> smfList = (List<Map<String, Object>>) MapUtils.getObject(itemMap, "SegmentList", new ArrayList<>(16));
                    log.info("smfList：{}",smfList);
                    for(Map<String, Object> smfMap : smfList){
                        if(MapUtil.isNotEmpty(smfMap)){
                            //单程信息
                            SegMentInfo smfInfo = new SegMentInfo();
                            smfInfo.setSegmentId(Integer.valueOf(smfMap.get("SegmentID").toString()));
                            smfInfo.setSegmentName(smfMap.get("SegmentName").toString());
                            smfInfo.setAmapId(smfMap.get("AmapID").toString());
                            smfInfo.setRouteId(Integer.valueOf(smfMap.get("RouteID").toString()));
                            smfInfo.setFirtLastShiftInfo(smfMap.get("FirtLastShiftInfo").toString());
                            smfInfo.setMemos(smfMap.get("Memos").toString());
                            smfInfo.setDrawType(smfMap.get("DrawType").toString());
                            smfInfo.setFirstTime((Date) smfMap.get("firstTime"));
                            smfInfo.setFirtLastShiftInfo2(smfMap.get("FirtLastShiftInfo2").toString());
                            smfInfo.setBaiduMapId(smfMap.get("BaiduMapID").toString());
                            smfInfo.setNormalTimeSpan(Integer.valueOf(smfMap.get("normalTimeSpan").toString()));
                            smfInfo.setRunDirection(smfMap.get("RunDirection").toString());
                            smfInfo.setRoutePrice(Integer.valueOf(smfMap.get("routePrice").toString()));
                            smfInfo.setPeakTimeSpan(Integer.valueOf(smfMap.get("peakTimeSpan").toString()));
                            smfInfo.setLastTime((Date) smfMap.get("lastTime"));
                            smf.add(smfInfo);
                        }
                    }
                    //站点信息
                    List<Map<String, Object>> ssfList = (List<Map<String, Object>>) MapUtils.getObject(itemMap, "StationList", new ArrayList<>(16));
                    log.info("ssfList：{}",ssfList);
                    for(Map<String, Object> ssfMap : ssfList){
                        if(MapUtil.isNotEmpty(ssfMap)){
                            SegStationInfo ssfInfo = new SegStationInfo();
                            ssfInfo.setSegmentId(Integer.valueOf(ssfMap.get("SegmentID").toString()));
                            ssfInfo.setStationId(ssfMap.get("StationID").toString());
                            ssfInfo.setStationName(ssfMap.get("StationName").toString());
                            ssfInfo.setLatitude(BigDecimal.valueOf(Long.valueOf(ssfMap.get("latitude").toString())));
                            ssfInfo.setLongitude(BigDecimal.valueOf(Long.valueOf(ssfMap.get("longitude").toString())));
                            ssfInfo.setSpeed(ssfMap.get("Speed").toString());
                            ssfInfo.setDualSerial(Integer.valueOf(ssfMap.get("DualSerial").toString()));
                            ssfInfo.setStationMemo(ssfMap.get("StationMemo").toString());
                            ssfInfo.setStationNo(Integer.valueOf(ssfMap.get("StationNo").toString()));
                            ssfInfo.setStationSort(Integer.valueOf(ssfMap.get("StationSort").toString()));
                            ssf.add(ssfInfo);
                        }
                    }
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
}
