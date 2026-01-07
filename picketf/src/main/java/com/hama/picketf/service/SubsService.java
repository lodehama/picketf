package com.hama.picketf.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  // 구독 정보 수정
  public void updateSubs(Long userNum, SubsDTO dto) {
    // 안전하게 현재 로그인 유저 번호 세팅
    dto.setSubsUsNum(userNum);
    subsDAO.updateSubs(dto);
  }

  // 구독 정보 삭제
  public void deleteSubs(Long subsNum, Long userNum) {
    subsDAO.deleteSubsByUser(subsNum, userNum);
  }

  // 구독 차트 추가
  public Map<String, Object> getSubsTypeDonutData(Long subsUsNum) {
    List<Map<String, Object>> rows = subsDAO.selectSubsTypeSumPrice(subsUsNum);

    List<String> labels = new ArrayList<>();
    List<Integer> values = new ArrayList<>();
    int total = 0;

    for (Map<String, Object> r : rows) {

      // type 방어 (null이면 "기타")
      Object typeObj = r.get("type");
      String type = (typeObj == null) ? "기타" : String.valueOf(typeObj);

      // sumPrice 방어 (키 불일치/NULL 대비)
      Object sumObj = r.get("sumPrice");
      int sumPrice = (sumObj instanceof Number) ? ((Number) sumObj).intValue() : 0;

      labels.add(type);
      values.add(sumPrice);
      total += sumPrice;
    }

    Map<String, Object> res = new HashMap<>();
    res.put("labels", labels);
    res.put("values", values);
    res.put("total", total);
    return res;
  }

}
