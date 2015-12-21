package com.thepaperpilot.solar.Levels;

public class Point {
    final int x;
    final int y;

    public Point(PointPrototype pointPrototype) {
        x = pointPrototype.x;
        y = pointPrototype.y;
    }

    public static class PointPrototype {
        public int x;
        public int y;

        public PointPrototype() {

        }
    }
}
