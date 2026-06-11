package com.hama.picketf.service;

import org.springframework.stereotype.Service;

import com.hama.picketf.dao.PageViewLogDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PageViewLogService {

  private final PageViewLogDAO pageViewLogDAO;

  public void recordPageView(String visitorKey, Integer usNum, String path) {
    pageViewLogDAO.insertPageView(visitorKey, usNum, path);
  }
}
