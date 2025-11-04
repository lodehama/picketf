package com.hama.picketf.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hama.picketf.dto.EtfMetaDTO;

@Repository
public class EtfDAO {

  private static final String NAMESPACE = "EtfMapper.";

  @Autowired
  private SqlSession sqlSession;

  public Double selectEtfRealCostByCode(String code) {
    return sqlSession.selectOne(NAMESPACE + "selectEtfRealCostByCode", code);
  }

  // 테이블 필터 기능
  public List<EtfMetaDTO> selectEtfMetaByCodes(List<String> codes) {
    return sqlSession.selectList(NAMESPACE + "selectEtfMetaByCodes", codes);
  }
}
