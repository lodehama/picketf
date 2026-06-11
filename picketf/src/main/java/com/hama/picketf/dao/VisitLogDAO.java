package com.hama.picketf.dao;

import org.apache.ibatis.annotations.Param;

public interface VisitLogDAO {

  void upsertVisit(
      @Param("visitorKey") String visitorKey,
      @Param("usNum") Integer usNum,
      @Param("path") String path);

  void deleteAnonymousVisits(
      @Param("anonymousVisitorKey") String anonymousVisitorKey,
      @Param("usNum") Integer usNum);
}
