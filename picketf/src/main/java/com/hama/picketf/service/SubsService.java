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

  // 추천 구독 한 번에 추가
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

  // 유저 구독 목록 조회
  public List<SubsDTO> getSubsListByUser(Long userNum) {
    List<SubsDTO> raw = subsDAO.getSubsListByUser(userNum);
    return (raw != null) ? raw : new ArrayList<>();
  }

  // DTO 리스트 그대로 저장 (컨트롤러에서 사용)
  public void insertSubsList(List<SubsDTO> list) {
    if (list == null || list.isEmpty()) {
      return;
    }
    subsDAO.insertSubsList(list);
  }

  // 정렬 추가
  public List<SubsDTO> getSubsListByUserSorted(Long userNum, String sort, String dir) {

    if (!"price".equals(sort)) {
      sort = "date";
    }
    if (!"asc".equals(dir) && !"desc".equals(dir)) {
      dir = "desc";
    }

    return subsDAO.getSubsListByUserSorted(userNum, sort, dir);
  }

  // 구독 활성/비활성 상태 업데이트
  public void updateSubsActive(Long userNum, Long subsNum, int active) {
    subsDAO.updateSubsActive(userNum, subsNum, active);
  }
}
