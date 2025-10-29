CREATE TABLE etf (
  etf_num       BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 자동 증가 고유번호
  etf_code      VARCHAR(12) NOT NULL UNIQUE,        -- 종목코드 (KRX ISU_CD)
  etf_name      VARCHAR(200) NOT NULL,              -- 종목명
  etf_issuer    VARCHAR(100) NOT NULL,              -- 운용사
  etf_leverage  DECIMAL(3,1) NOT NULL DEFAULT 1.0,  -- 1.0, 2.0, 3.0 ...
  etf_fx_hedged TINYINT NOT NULL DEFAULT 0          -- 0: 비헷지, 1: 환헷지
);


INSERT INTO etf (etf_code, etf_name, etf_leverage, etf_fx_hedged)
VALUES
-- ① 일반 1배수 (비헷지)
('360750', 'TIGER 미국S&P500', 1.0, 0),
('379800', 'KODEX 미국S&P500', 1.0, 0),
('360750', 'TIGER 미국S&P500', 1.0, 0),
('360750', 'TIGER 미국S&P500', 1.0, 0),
('360750', 'TIGER 미국S&P500', 1.0, 0),

-- ② 일반 1배수 (환헷지형)
('360750', 'TIGER 미국S&P500선물(H)', 1.0, 1),

-- ③ 레버리지 2배 (비헷지)
('122630', 'KODEX 레버리지', 2.0, 0),

-- ④ 인버스 1배 (비헷지)
('114800', 'KODEX 인버스', 1.0, 0),

-- ⑤ 인버스 2배 (비헷지)
('252670', 'KODEX 200선물인버스2X', 2.0, 0),

-- ⑥ 환헷지 + 레버리지 조합 예시 (가정)
('467810', 'HANARO 미국나스닥100레버리지(H)', 2.0, 1);
