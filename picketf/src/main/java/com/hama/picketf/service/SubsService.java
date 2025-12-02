package com.hama.picketf.service;

import java.time.LocalDate;
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
    LocalDate today = LocalDate.now();

    List<SubsDTO> list = forms.stream()
        .map(f -> {
          SubsDTO dto = new SubsDTO();
          dto.setSubsUsNum(userNum);
          dto.setSubsName(f.getName());
          dto.setSubsPrice(f.getPrice());
          dto.setSubsType(f.getType());
          dto.setSubsBillingDay(f.getBillingDay());
          dto.setSubsActive(true);
          dto.setSubsStartDate(today);
          dto.setSubsIcon(null); // 나중에 아이콘 쓰면 채우기
          return dto;
        })
        .toList();

    if (!list.isEmpty()) {
      subsDAO.insertSubsList(list);
    }
  }
}
