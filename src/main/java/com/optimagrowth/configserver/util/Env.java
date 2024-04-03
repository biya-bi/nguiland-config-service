package com.optimagrowth.configserver.util;

public final class Env {

    private Env() {
    }

    public static String get(String name) {
        return System.getenv(name);
    }
}
