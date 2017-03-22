package com.stocktool.model;

import static org.junit.Assert.*;

public class CandleStickTest {

    private CandleStick candleStick;

    @org.junit.Before
    public void setUp() throws Exception {
        candleStick = CandleStick.CandleStickBuilder.aCandleStick()
                .withOpen(99.0)
                .withHigh(100.0)
                .withLow(95.0)
                .withClose(99.9)
                .build();
    }

    @org.junit.Test
    public void isHammer() throws Exception {
        assertTrue("The candle does not fit into a hammer candlestick pattern", candleStick.isHammer());
    }

}