package com.stocktool.model;

import static org.junit.Assert.*;

public class CandleStickTest {

    private CandleStick candleStick;

    @org.junit.Before
    public void setUp() throws Exception {
        candleStick = CandleStick.CandleStickBuilder.aCandleStick()
                .withOpen(145.36)
                .withHigh(145.57)
                .withLow(144.65)
                .withClose(145.52)
                .build();
    }

    @org.junit.Test
    public void isHammer() throws Exception {
        assertTrue("The candle does not fit into a hammer candlestick pattern", candleStick.isHammer());
    }

}