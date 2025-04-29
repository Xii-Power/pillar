package com.xii.pillar.utils;

import java.util.UUID;

public class IdGenerator {

    public final static String SPLIT = "_";
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String authId() {
        return "AU" + UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String accessToken() {
        return "AC" + IdGenerator.uuid();
    }

    public static String refreshToken() {
        return "RF" + IdGenerator.uuid();
    }

    public static String requestId(String deviceId) {
        return deviceId + SPLIT + uuid();
    }

    public static void main(String[] args) {
        System.out.println(uuid());
    }
}
