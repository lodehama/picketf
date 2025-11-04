CREATE TABLE etf (
  etf_num       BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 자동 증가 고유번호
  etf_category  VARCHAR(50) NOT NULL,            	-- 탭 이름 (QQQ, S&P500, M7, SCHD 등)
  etf_code      VARCHAR(12) NOT NULL UNIQUE,        -- 종목코드 (KRX ISU_CD)
  etf_name      VARCHAR(200) NOT NULL,              -- 종목명
  etf_issuer    VARCHAR(100) NOT NULL,              -- 운용사
  etf_ter 		DECIMAL(5,3),						-- 수수료
  etf_leverage  DECIMAL(3,1) NOT NULL DEFAULT 1.0,  -- 1.0, 2.0, 3.0 ...
  etf_fx_hedged TINYINT NOT NULL DEFAULT 0          -- 0: 비헷지, 1: 환헷지
);

INSERT INTO etf (etf_category, etf_code, etf_name, etf_issuer, etf_leverage, etf_fx_hedged)
VALUES
-- 일반 1배수 (비헷지)
('SNP', '360750', 'TIGER 미국S&P500', '미래에셋자산운용', 1.0, 0),
('SNP', '379800', 'KODEX 미국S&P500', '삼성자산운용', 1.0, 0),
('SNP', '360200', 'ACE 미국S&P500', '한국투자신탁운용', 1.0, 0),
('SNP', '379780', 'RISE 미국S&P500', '케이비자산운용', 1.0, 0),
('SNP', '433330', 'SOL 미국S&P500', '신한자산운용', 1.0, 0),
('SNP', '0026S0', '1Q 미국S&P500', '하나자산운용', 1.0, 0),
('SNP', '449770', 'KIWOOM 미국S&P500', '키움투자자산운용', 1.0, 0),
('SNP', '444490', 'WON 미국S&P500', '우리자산운용', 1.0, 0),
('SNP', '429760 ', 'PLUS 미국S&P500', '한화자산운용', 1.0, 0),

-- 일반 1배수 (환헷지)
('SNP', '449180', 'KODEX 미국S&P500(H)', '삼성자산운용', 1.0, 1),
('SNP', '448290', 'TIGER 미국S&P500(H)', '미래에셋자산운용', 1.0, 1),
('SNP', '453330', 'RISE 미국S&P500(H)', '케이비자산운용', 1.0, 1),
('SNP', '269540', 'PLUS 미국S&P500(H)', '한화자산운용', 1.0, 1),
('SNP', '449780', 'KIWOOM 미국S&P500(H)', '키움투자자산운용', 1.0, 1),

-- 레버리지 2배 (비헷지)

-- 레버리지 2배 (환헷지)
('SNP', '225040', 'TIGER 미국S&P500레버리지(합성 H)', '미래에셋자산운용', 2.0, 1);

INSERT INTO etf (etf_category, etf_code, etf_name, etf_issuer, etf_leverage, etf_fx_hedged)
VALUES
-- 일반 1배수 (비헷지)
('QQQ', '133690', 'TIGER 미국나스닥100', '미래에셋자산운용', 1.0, 0),
('QQQ', '379810', 'KODEX 미국나스닥100', '삼성자산운용', 1.0, 0),
('QQQ', '367380', 'ACE 미국나스닥100', '한국투자신탁운용', 1.0, 0),
('QQQ', '368590', 'RISE 미국나스닥100', '케이비자산운용', 1.0, 0),
('QQQ', '476030', 'SOL 미국나스닥100', '신한자산운용', 1.0, 0),
('QQQ', '0069M0', '1Q 미국나스닥100', '하나자산운용', 1.0, 0),
('QQQ', '287180', 'PLUS 미국나스닥테크', '한화자산운용', 1.0, 0),

-- 일반 1배수 (환헷지)
('QQQ', '449190', 'KODEX 미국나스닥100(H)', '삼성자산운용', 1.0, 1),
('QQQ', '448300', 'TIGER 미국나스닥100(H)', '미래에셋자산운용', 1.0, 1),
('QQQ', '453080', 'KIWOOM 미국나스닥100(H)', '키움투자자산운용', 1.0, 1),

-- 레버리지 2배 (비헷지)
('QQQ', '418660', 'TIGER 미국나스닥100레버리지(합성)', '미래에셋자산운용', 2.0, 0),

-- 레버리지 2배 (환헷지)
('QQQ', '409820', 'KODEX 미국나스닥100레버리지(합성 H)', '삼성자산운용', 2.0, 1);