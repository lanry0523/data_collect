package com.iwhalecloud.data.collect.service.impl;


import com.iwhalecloud.data.collect.dao.RequestLogDao;
import com.iwhalecloud.data.collect.domain.RequestLog;
import com.iwhalecloud.data.collect.service.RequestLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class RequestLogServiceImpl implements RequestLogService {
    @Resource
    private RequestLogDao requestLogDao;

    @Override
    public void addRequestLog(RequestLog requestLog) {
        requestLog.setLogTime(new Date());
        requestLogDao.insertRecord(requestLog);
    }
}
