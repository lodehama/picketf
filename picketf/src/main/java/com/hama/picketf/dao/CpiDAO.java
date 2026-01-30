package com.hama.picketf.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hama.picketf.dto.CpiMinWageRow;

@Mapper
public interface CpiDAO {

  CpiMinWageRow findMinWageByYear(@Param("year") int year);

}
