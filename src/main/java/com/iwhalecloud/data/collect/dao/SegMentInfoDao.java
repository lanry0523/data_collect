package com.iwhalecloud.data.collect.dao;

import com.iwhalecloud.data.collect.domain.SegMentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SegMentInfoDao {

    int insertRecord(@Param("item") SegMentInfo segMentInfo);
}
