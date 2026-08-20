package com.example.hookgps.utils;

import java.util.Random;

public class HKLocationGenerator {
    // 香港的经纬度边界
    private static final double MIN_LAT = 22.15;
    private static final double MAX_LAT = 22.56;
    private static final double MIN_LON = 113.83;
    private static final double MAX_LON = 114.44;

    private static final Random random = new Random();

    public static double[] getRandomHKLocation() {
        double lat = MIN_LAT + (MAX_LAT - MIN_LAT) * random.nextDouble();
        double lon = MIN_LON + (MAX_LON - MIN_LON) * random.nextDouble();
        return new double[]{lat, lon};
    }

    public static void main(String[] args) {
        double[] location = getRandomHKLocation();
        System.out.printf("随机香港坐标 -> 纬度: %.6f, 经度: %.6f%n", location[0], location[1]);
    }
}