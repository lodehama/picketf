package com.hama.picketf.dto;

public class StockQuote {
    private String code;
    private String name;
    private String price;
    private String marketCap;
    private String per;
    private String pbr;

    public StockQuote() {}
    public StockQuote(String code, String name, String price, String marketCap, String per, String pbr) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.marketCap = marketCap;
        this.per = per;
        this.pbr = pbr;
    }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getMarketCap() { return marketCap; }
    public String getPer() { return per; }
    public String getPbr() { return pbr; }
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setPrice(String price) { this.price = price; }
    public void setMarketCap(String marketCap) { this.marketCap = marketCap; }
    public void setPer(String per) { this.per = per; }
    public void setPbr(String pbr) { this.pbr = pbr; }
}
