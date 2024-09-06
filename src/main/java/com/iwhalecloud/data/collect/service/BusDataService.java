package com.iwhalecloud.data.collect.service;

public interface BusDataService {

    void syncBusDataInfo();
    int instBus() throws Exception;
    int batchDeleteAmapList();
    int selectCount();
}
