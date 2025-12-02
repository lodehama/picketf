package com.hama.picketf.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hama.picketf.dao.SubsDAO;
import com.hama.picketf.dto.SubsDTO;
import com.hama.picketf.dto.SubsRecommendForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubsService {

  private final SubsDAO subsDAO;

  public void addRecommendedSubs(Long userNum, List<SubsRecommendForm> forms) {
    List<SubsDTO> list = new ArrayList<>();

    for (SubsRecommendForm form : forms) {
      SubsDTO dto = new SubsDTO();
      dto.setSubsUsNum(userNum);
      dto.setSubsName(form.getName());
      dto.setSubsType(form.getType());
      dto.setSubsPrice(form.getPrice());
      dto.setSubsBillingDay(form.getBillingDay());
      dto.setSubsActive(1);
      dto.setSubsStartDate(LocalDate.now());
      dto.setSubsIcon(form.getIcon());

      list.add(dto);
    }

    subsDAO.insertSubsList(list);
  }

  public List<SubsDTO> getSubsListByUser(Long userNum) {
    System.out.println("DEBUG SubsService.getSubsListByUser userNum = " + userNum);
    List<SubsDTO> raw = subsDAO.getSubsListByUser(userNum);
    System.out.println("DEBUG SubsService.getSubsListByUser raw size = " + (raw == null ? "null" : raw.size()));

    if (raw == null) return new ArrayList<>();

    for (int i = 0; i < raw.size(); i++) {
      System.out.println("  raw[" + i + "] = " + raw.get(i));
    }

    // 일단 그대로 리턴 (필터 X)
    return raw;
  }

  public void insertSubsList(List<SubsDTO> list) {
    subsDAO.insertSubsList(list);
  }
}
