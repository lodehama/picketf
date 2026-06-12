package com.hama.picketf.dao;

import org.apache.ibatis.annotations.Param;

public interface PageViewLogDAO {

  void insertPageView(
      @Param("visitorKey") String visitorKey,
      @Param("usNum") Integer usNum,
      @Param("path") String path,
      @Param("deviceType") String deviceType);
}
