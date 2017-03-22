package com.stocktool.net;

import com.stocktool.model.CandleStick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GoogleRequest {

    LocalDateTime timeStamp;
    final Long INTERVAL = 1800L;


    public void getData(String ticker) throws Exception {

        URL url = new URL("https://www.google.com/finance/getprices?i="+INTERVAL+"&p=1d&f=d,o,h,l,c,v&df=cpct&q="+ticker);
        URLConnection goog = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(goog.getInputStream()));
        Function<String, Long> mapToTimeStamp = (line) -> {
            String[] p = line.split(",");
            return Long.valueOf(p[0].substring(1));
        };

        Function<String, CandleStick> mapToCandleStick = (line) -> {
            String[] p = line.split(",");
            return CandleStick.CandleStickBuilder.aCandleStick()
                    .withClose(Double.valueOf(p[1]))
                    .withHigh(Double.valueOf(p[2]))
                    .withLow(Double.valueOf(p[3]))
                    .withOpen(Double.valueOf(p[4]))
                    .withDate(getCandleStickDateTime(p[0]))
                    .build();
        };

        CandleStick firstCandle = in.lines()
                .skip(7)
                .findFirst()
                .map(mapToCandleStick)
                .get();

        timeStamp = firstCandle.date;

        List<CandleStick> candleStickList = in.lines()
                .map(mapToCandleStick)
                .collect(Collectors.toList());
        candleStickList.add(0,firstCandle);

        System.out.println(ticker);
        candleStickList.stream()
                .forEach(System.out::println);

    }

    private LocalDateTime getCandleStickDateTime(String line) {

        if (line.startsWith("a"))
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(line.substring(1))), ZoneId.systemDefault());
        else
            return timeStamp.plusSeconds(INTERVAL*Integer.valueOf(line));
    }

    public static void main(String[]args) {
        GoogleRequest g = new GoogleRequest();
        try {
            g.getData("NFLX");
            g.getData("TSLA");
            g.getData("SAN");
            g.getData("TEF");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
