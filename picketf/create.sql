
CREATE TABLetfE etf (
  etf_code      VARCHAR(12) PRIMARY KEY,
  etf_name      VARCHAR(200) NOT NULL,
  etf_leverage  DECIMAL(3,1) NOT NULL DEFAULT 1.0, -- 1.0, 2.0, 3.0 ...
  etf_fx_hedged   TINYINT NOT NULL DEFAULT 0       -- 0: 기본, 1: 환헷지
);
