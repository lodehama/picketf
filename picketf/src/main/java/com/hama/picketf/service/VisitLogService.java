package com.hama.picketf.service;

import org.springframework.stereotype.Service;

import com.hama.picketf.dao.VisitLogDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitLogService {

  private final VisitLogDAO visitLogDAO;

  public void recordVisit(String visitorKey, Integer usNum, String path) {
    visitLogDAO.upsertVisit(visitorKey, usNum, path);
  }

  public void deleteAnonymousVisits(String anonymousVisitorKey, Integer usNum) {
    visitLogDAO.deleteAnonymousVisits(anonymousVisitorKey, usNum);
  }
}
