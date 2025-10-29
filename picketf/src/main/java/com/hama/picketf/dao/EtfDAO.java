package com.hama.picketf.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EtfDAO {

  private static final String NAMESPACE = "EtfMapper.";

  @Autowired
  private SqlSession sqlSession;

  public Double selectEtfTerByCode(String code) {
    return sqlSession.selectOne(NAMESPACE + "selectEtfTerByCode", code);
  }
}
