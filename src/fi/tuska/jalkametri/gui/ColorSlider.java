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
package fi.tuska.jalkametri.gui;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import android.graphics.Color;
import android.util.Pair;
import fi.tuska.jalkametri.util.MathUtil;

/**
 * A color slider that allows you to get interpolated color values between
 * colors that you have specified at certain points in the slider.
 * 
 * For example, you could set up a slider with a red color at position 0, blue
 * color at position 0.5, and a yellow color at position 1. Then, getColor(0)
 * would return red, getColor(0.25) would return a red-blue interpolation,
 * getColor(0.4) would return a more bluey version of the red-blue mix, and so
 * on.
 * 
 * @author Tuukka Haapasalo
 */
public class ColorSlider {

    public static final int DEFAULT_COLOR = Color.TRANSPARENT;

    public TreeSet<Pair<Double, Integer>> colors = new TreeSet<Pair<Double, Integer>>(
        new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> p1, Pair<Double, Integer> p2) {
                return p1.first.compareTo(p2.first);
            }
        });

    public ColorSlider() {
    }

    public void addColor(double position, int color) {
        colors.add(new Pair<Double, Integer>(position, color));
    }

    public int getColor(double position) {
        Pair<Double, Integer> before = null;
        try {
            before = colors.headSet(new Pair<Double, Integer>(position, 0)).last();
        } catch (NoSuchElementException e) {
        }
        Pair<Double, Integer> after = null;
        try {
            after = colors.tailSet(new Pair<Double, Integer>(position, 0)).first();
        } catch (NoSuchElementException e) {
        }

        if (before == null && after == null) {
            return DEFAULT_COLOR;
        }
        if (before == null)
            return after.second;
        if (after == null)
            return before.second;
        if (position == before.first)
            return before.second;
        if (position == after.first)
            return after.second;

        // Position is between before and after
        double place = MathUtil.getPosition(position, before.first, after.first);

        int c1 = before.second;
        int c2 = after.second;
        int a1 = Color.alpha(c1);
        int r1 = Color.red(c1);
        int g1 = Color.green(c1);
        int b1 = Color.blue(c1);
        int a2 = Color.alpha(c2);
        int r2 = Color.red(c2);
        int g2 = Color.green(c2);
        int b2 = Color.blue(c2);

        return Color.argb(MathUtil.getValueAt(a1, a2, place), MathUtil.getValueAt(r1, r2, place),
            MathUtil.getValueAt(g1, g2, place), MathUtil.getValueAt(b1, b2, place));
    }

}
