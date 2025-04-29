package com.xii.pillar.test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xii.pillar.utils.IdGenerator;
import com.xii.pillar.utils.JsonUtil;
import okhttp3.*;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionTest {

    public final static String deviceId = "1234567890";
    public final static String requestId = IdGenerator.requestId(deviceId);
    public final static String url = "ws://127.0.0.1:8000/endpoint/connect?" + "deviceId=" + deviceId
            + "&userId=123456&token=x&requestId=" + requestId;

    private static String PING_STR = "{\"type\":\"CHECK\",\"action\":\"PING\"}";

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
//                .pingInterval(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newWebSocket(request, listener());
    }

    public static WebSocketListener listener() {
        return new WebSocketListener() { AtomicInteger viewRetryNum = new AtomicInteger(0);
            AtomicInteger scrapRetryNum = new AtomicInteger(1);
            AtomicBoolean isRunning = new AtomicBoolean(true);

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                System.out.println("onOpen >>>>>");

                new Thread(() -> {
                    try {
                        while (true) {
                            ObjectNode test = JsonUtil.read(PING_STR, ObjectNode.class);
                            test.put("sn", "ENDPOINT" + System.currentTimeMillis());
                            test.put("deviceId", deviceId);
                            test.put("timestamp", System.currentTimeMillis());
                            webSocket.send(test.toString());

                            // Ping Cloud Per 2min
                            Thread.sleep(2 * 60 * 1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                System.out.println("MESSAGE: " + text);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }
            @Override
            public void onClosed( WebSocket webSocket, int code,  String reason) {
                super.onClosed(webSocket, code, reason);
                System.out.println(new Date() + "onClosed" + code + reason);
                webSocket.close(1000, "closed by client");
            }

            @Override
            public void onClosing( WebSocket webSocket, int code,  String reason) {
                super.onClosing(webSocket, code, reason);
                System.out.println(new Date() + "onClosing" + code + reason);
            }

            @Override
            public void onFailure( WebSocket webSocket,  Throwable t,  Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println(new Date() + "onFailure" + t + response);
            }
        };
    }





}
