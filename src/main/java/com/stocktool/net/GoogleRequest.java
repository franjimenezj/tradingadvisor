package com.stocktool.net;

import com.stocktool.model.CandleStick;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import sun.invoke.empty.Empty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GoogleRequest {

    final static Long INTERVAL_HALF_HOUR = 1800L;
    final static Long INTERVAL_HOUR = 3600L;


    public void getData(String ticker, String market, Long interval) throws Exception {

        URL url = new URL("https://www.google.com/finance/getprices?i="+interval+"&p=4d&f=d,o,h,l,c,v&df=cpct&q="+ticker+"&x="+market);
        System.out.println("ticker = [" + ticker + "], market = [" + market + "]");
        URLConnection goog = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(goog.getInputStream()));

        List<CandleStick> candleStickList = getCandleStickList(in, interval, ticker, market);
        printHammer(candleStickList);

    }

    public CompletableFuture<Void> getDataHttpClient(String ticker, String market, Long interval, AsyncHttpClient asyncHttpClient) throws Exception {

        CompletableFuture<Response> f = asyncHttpClient
                .prepareGet("https://www.google.com/finance/getprices?i="+interval+"&p=4d&f=d,o,h,l,c,v&df=cpct&q="+ticker+"&x="+market)
                .execute()
                .toCompletableFuture();

        return f.thenAccept( response  -> {

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getResponseBodyAsStream()));

            List<CandleStick> candleStickList = getCandleStickList(in, interval, ticker, market);
            printHammer(candleStickList);
        });

    }

    private void printHammer(List<CandleStick> candleStickList) {
        candleStickList.stream()
                .filter(CandleStick::isHammer)
                .forEach(System.out::println);
    }

    public List<CandleStick> getCandleStickList(BufferedReader in, Long interval, String ticker, String market) {

        Optional<CandleStick> firstCandle = in
                .lines()
                .skip(7)
                .findFirst()
                .map(i -> getCandleStick(i, interval, null, ticker, market));

        List<CandleStick> candleStickList = new ArrayList <>();

        firstCandle.ifPresent( first -> {
            LocalDateTime timeStamp = first.date;

            candleStickList.addAll(in
                    .lines()
                    .map(i -> getCandleStick(i, interval, timeStamp, ticker, market))
                    .collect(Collectors.toCollection(ArrayList::new)));

            candleStickList.add(0, first);

        });

        return candleStickList;
    }


    private LocalDateTime getCandleStickDateTime(String line, Long interval, LocalDateTime timeStamp) {

        if (line.startsWith("a"))
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.valueOf(line.substring(1))), ZoneId.systemDefault());
        else
            return timeStamp.plusSeconds(interval*Integer.valueOf(line));
    }

    private CandleStick getCandleStick(String line, Long interval, LocalDateTime timeStamp, String ticker, String market) {

        BiFunction<String, Long, CandleStick> mapToCandleStick;
        mapToCandleStick = (l, i) -> {
            String[] p = l.split(",");
            return CandleStick.CandleStickBuilder.aCandleStick()
                    .withClose(Double.valueOf(p[1]))
                    .withHigh(Double.valueOf(p[2]))
                    .withLow(Double.valueOf(p[3]))
                    .withOpen(Double.valueOf(p[4]))
                    .withDate(getCandleStickDateTime(p[0], i, timeStamp))
                    .withTicker(ticker)
                    .withMarket(market)
                    .build();
        };

        return mapToCandleStick.apply(line, interval);

    }

    public static void main(String[]args) {
        GoogleRequest g = new GoogleRequest();
        Instant start = Instant.now();
        g.way1();
        Instant end = Instant.now();
        System.out.println("1 "+Duration.between(start, end));
        /*start = Instant.now();
        g.way2();
        end = Instant.now();
        System.out.println("2 "+Duration.between(start, end));*/
    }

    public void way1() {

        try {

            ArrayList<String> ibexStockList = new ArrayList<>(
                    Arrays.asList("BME:ABE", "BME:ACS", "BME:ACX", "BME:AENA", "BME:AMS", "BME:ANA", "BME:BBVA", "BME:BKT", "BME:CABK", "BME:CLNX", "BME:DIA", "BME:ELE", "BME:ENG", "BME:FCC", "BME:FER", "BME:GAM", "BME:GAS", "BME:GRF", "BME:IAG", "BME:IBE", "BME:ITX", "BME:MAP", "BME:MRL", "BME:MTS", "BME:POP", "BME:REE", "BME:SAB", "BME:SAN", "BME:TEF", "BME:TRE", "BME:VIS"));

            ArrayList<String> nasdaqStockList = new ArrayList<>(
                    Arrays.asList( "NASD:AAPL", "NASD:AMZN", "NASD:NFLX", "NASD:NVDA", "NASD:TSLA"));

            ArrayList<String> otherStockList = new ArrayList<>(
                    Arrays.asList( "CIEL3:BVMF", "CURRENCY:EURUSD"));

            ArrayList<String> allStockList = new ArrayList<>();

            allStockList.addAll(ibexStockList);
            allStockList.addAll(nasdaqStockList);
            allStockList.addAll(otherStockList);

            AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

            List<CompletableFuture<Void>> futureList = allStockList
                    .stream()
                    .map(pair -> {
                                try {
                                    return getDataHttpClient(pair.split(":")[1],
                                            pair.split(":")[0], INTERVAL_HOUR, asyncHttpClient);
                                } catch (Exception e) {
                                    return CompletableFuture.<Void>completedFuture(null);
                                }
                            }

                    ).collect(Collectors.<CompletableFuture<Void>>toList());

            final CompletableFuture <Void> voidCompletableFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]))
                    .thenAccept(v -> {
                        futureList.forEach(CompletableFuture::join);
                    });

            Thread.sleep(1000);
            asyncHttpClient.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void way2() {

        try {

            ArrayList<String> ibexStockList = new ArrayList<>(
                    Arrays.asList("BME:ABE", "BME:ACS", "BME:ACX", "BME:AENA", "BME:AMS", "BME:ANA", "BME:BBVA", "BME:BKT", "BME:CABK", "BME:CLNX", "BME:DIA", "BME:ELE", "BME:ENG", "BME:FCC", "BME:FER", "BME:GAM", "BME:GAS", "BME:GRF", "BME:IAG", "BME:IBE", "BME:ITX", "BME:MAP", "BME:MRL", "BME:MTS", "BME:POP", "BME:REE", "BME:SAB", "BME:SAN", "BME:TEF", "BME:TRE", "BME:VIS"));

            ArrayList<String> nasdaqStockList = new ArrayList<>(
                    Arrays.asList( "NASD:AAPL", "NASD:AMZN", "NASD:NFLX", "NASD:NVDA", "NASD:TSLA"));

            ArrayList<String> otherStockList = new ArrayList<>(
                    Arrays.asList( "CIEL3:BVMF", "CURRENCY:EURUSD"));

            ArrayList<String> allStockList = new ArrayList<>();

            allStockList.addAll(ibexStockList);
            allStockList.addAll(nasdaqStockList);
            allStockList.addAll(otherStockList);

            allStockList
                    .forEach(pair -> {
                        try {
                            getData(pair.split(":")[1], pair.split(":")[0], INTERVAL_HOUR);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
