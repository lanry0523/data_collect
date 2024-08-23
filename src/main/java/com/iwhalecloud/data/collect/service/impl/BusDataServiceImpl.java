package com.iwhalecloud.data.collect.service.impl;

import cn.hutool.core.map.MapUtil;
import com.iwhalecloud.data.collect.dao.BusDataDao;
import com.iwhalecloud.data.collect.service.BusDataService;
import com.iwhalecloud.data.collect.util.HttpClientUtils;
import com.iwhalecloud.data.collect.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class BusDataServiceImpl implements BusDataService {
    @Resource
    private BusDataDao busDataDao;


    @Override
    public void syncBusDataInfo() throws Exception{
        String allUrl = "http://cloud.tiamaes.com:17570/BusService/Query_AllSubRouteData/";
        Map<String, Object> params = HttpClientUtils.getParams();
        String result = HttpClientUtils.sendPost(allUrl, params, 0, 0, 0);
        Map<String, Object> buMap = getCustomer(result);
        log.info("结果：{}",buMap);
        if(MapUtil.isNotEmpty(buMap)){

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
            Map<String, Object> json2map = JsonUtils.json2map(jsonBody);
            custom = JsonUtils.json2map(JsonUtils.obj2json(json2map.get("CUSTOM")));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析数据失败",e);
        }
        return custom;
    }
}
