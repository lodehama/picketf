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

      // ★ 아이콘 매핑
      dto.setSubsIcon(form.getIcon()); // 예: "Netflix.svg" / "YouTube.svg"

      list.add(dto);
    }

    subsDAO.insertSubsList(list);
  }

  public void insertSubsList(List<SubsDTO> list) {
    subsDAO.insertSubsList(list);
  }
}
