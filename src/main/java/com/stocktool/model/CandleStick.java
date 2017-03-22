package com.stocktool.model;

import java.time.LocalDateTime;

public class CandleStick {

    public Double open;
    public Double close;
    public Double low;
    public Double high;
    public LocalDateTime date;
    public Long interval;


    public static final class CandleStickBuilder {
        private Double open;
        private Double close;
        private Double low;
        private Double high;
        private LocalDateTime date;
        private Long interval;

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

        public CandleStick build() {
            CandleStick candleStick = new CandleStick();
            candleStick.low = this.low;
            candleStick.date = this.date;
            candleStick.open = this.open;
            candleStick.high = this.high;
            candleStick.close = this.close;
            candleStick.interval = this.interval;
            return candleStick;
        }
    }

    public Boolean isHammer() {
        Double height = this.high-this.low;
        Double upperShadow = this.high-this.close;
        Double body = this.close-this.open;

        if (height > open * 0.01 &&
                upperShadow < height/20 &&
                body < height*0.3) return true;
        else return false;

    }

    @Override
    public String toString() {
        return this.date+","+this.open+","+this.high+","+this.low+","+this.close+","+isHammer();
    }
}
