/**
 * Copyright 2006-2011 Tuukka Haapasalo
 * 
 * This file is part of jAlkaMetri.
 * 
 * jAlkaMetri is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with jAlkaMetri (LICENSE.txt). If not, see <http://www.gnu.org/licenses/>.
 */
package fi.tuska.jalkametri.util;

import android.util.Pair;

public final class MathUtil {

    private MathUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the value of the integer in between a and b, where pos is the
     * position from 0 to 1.
     * 
     * @param a value at 0
     * @param b value at 1
     * @param pos position, from 0 to 1
     * @return the value at the given position
     */
    public static int getValueAt(int a, int b, double pos) {
        return (int) ((b - a) * pos + a);
    }

    public static double getValueAt(double a, double b, double pos) {
        return (b - a) * pos + a;
    }

    /**
     * Returns the position of the value variable in between min and max,
     * scaled to the range of 0 to 1.
     * 
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double getPosition(double value, double min, double max) {
        assert min < max;

        double rangeSize = max - min;
        assert rangeSize > 0;

        double place = (value - min) / rangeSize;

        return place;
    }

    public static Pair<Double, Double> rectangularToPolar(double x, double y) {
        double radius = Math.sqrt(x * x + y * y);
        double angleInRadians = Math.acos(x / radius);
        return new Pair<Double, Double>(radius, angleInRadians);
    }

    public static Pair<Double, Double> polarToRectangular(double radius, double angle) {
        double x = radius * Math.cos(angle);
        double y = radius * Math.sin(angle);
        return new Pair<Double, Double>(x, y);
    }

}
