package com.stas.gchords;

public class Util {
    public static int mod(int x, int y) {
        int m = x % y;
        return m < 0 ? m + y : m;
    }

    public static int randi(int min, int max) {
        return (int)(min + Math.random() * (max - min));
    }

    public static int randSign() {
        return Math.random() < 0.5 ? -1 : 1;
    }
}
