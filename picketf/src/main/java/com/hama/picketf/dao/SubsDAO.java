package com.hama.picketf.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.hama.picketf.dto.SubsDTO;

@Mapper
public interface SubsDAO {

  // 하나만 추가도 필요하면 같이
  void insertSubs(SubsDTO subs);

  // 여러 건 한 번에 INSERT
  void insertSubsList(@Param("list") List<SubsDTO> list);

  // 사용자 구독 정보 조회
  List<SubsDTO> getSubsListByUser(@Param("userNum") Long userNum);

  // 정렬 추가
  List<SubsDTO> getSubsListByUserSorted(
      @Param("userNum") Long userNum,
      @Param("sort") String sort,
      @Param("dir") String dir);

}
