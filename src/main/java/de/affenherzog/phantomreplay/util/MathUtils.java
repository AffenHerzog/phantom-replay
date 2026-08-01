package de.affenherzog.phantomreplay.util;

public class MathUtils {

    public static double roundPosition(double coordinate) {
        return (double) Math.round(coordinate * 10000) / 10000;
    }

    public static float roundRotation(float rotation) {
        return (float) Math.round(rotation * 100) / 100;
    }
}
