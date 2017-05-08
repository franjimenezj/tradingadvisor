package com.stocktool.model;

import java.time.LocalDateTime;

public class CandleStick {

    public Double open;
    public Double close;
    public Double low;
    public Double high;
    public LocalDateTime date;
    public Long interval;
    public String ticker;
    public String market;


    public static final class CandleStickBuilder {
        private Double open;
        private Double close;
        private Double low;
        private Double high;
        private LocalDateTime date;
        private Long interval;
        private String ticker;
        private String market;

        private CandleStickBuilder() {
        }

        public static CandleStickBuilder aCandleStick() {
            return new CandleStickBuilder();
        }

        public CandleStickBuilder withOpen(Double open) {
            this.open = open;
            return this;
        }

        public CandleStickBuilder withClose(Double close) {
            this.close = close;
            return this;
        }

        public CandleStickBuilder withLow(Double low) {
            this.low = low;
            return this;
        }

        public CandleStickBuilder withHigh(Double high) {
            this.high = high;
            return this;
        }

        public CandleStickBuilder withDate(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public CandleStickBuilder withInterval(Long interval) {
            this.interval = interval;
            return this;
        }

        public CandleStickBuilder withTicker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        public CandleStickBuilder withMarket(String market) {
            this.market = market;
            return this;
        }

        public CandleStick build() {
            CandleStick candleStick = new CandleStick();
            candleStick.low = this.low;
            candleStick.date = this.date;
            candleStick.open = this.open;
            candleStick.high = this.high;
            candleStick.close = this.close;
            candleStick.interval = this.interval;
            candleStick.ticker = this.ticker;
            candleStick.market = this.market;
            return candleStick;
        }
    }

    public Boolean isHammer() {
        Double height = this.high-this.low;
        Double upperShadow = this.high-this.close;
        Double body = this.close-this.open;

        if (height > open * 0.005 &&
                upperShadow < height/15 &&
                body < height*0.20) return true;
        else return false;

    }

    @Override
    public String toString() {
        return "ticker = [" + this.ticker + "], market = [" + this.market + "] "+
                this.date+", o="+this.open+", h="+this.high+", l="+this.low+", c="+this.close+", "+isHammer();
    }
}
