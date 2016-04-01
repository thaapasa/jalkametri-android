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
package fi.tuska.jalkametri.media;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeMap;

import android.util.Pair;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.util.LogUtil;

public final class ToastLibrary {

    private static final TreeMap<Double, List<Pair<Integer, Integer>>> TOASTS = new TreeMap<Double, List<Pair<Integer, Integer>>>();

    private static final Random RANDOM = new Random();

    private static final String TAG = "ToastLibrary";

    private static final double EMPTY_LEVEL = -1d;
    private static final double SOBER_LEVEL = 0d;
    private static final double SMALL_LEVEL = 0.01d;
    private static final double MEDIUM_LEVEL = 0.5d;
    private static final double TUMU_LEVEL = 0.9d;

    public static final int NO_TOAST_SOUND = 0;

    static {
        addToast(EMPTY_LEVEL, R.string.toast_empty, NO_TOAST_SOUND);

        addToast(SOBER_LEVEL, R.string.toast_sober, NO_TOAST_SOUND);

        addToast(SMALL_LEVEL, R.string.toast_small_1, NO_TOAST_SOUND);
        addToast(SMALL_LEVEL, R.string.toast_small_2, NO_TOAST_SOUND);
        addToast(SMALL_LEVEL, R.string.toast_small_3, NO_TOAST_SOUND);
        addToast(SMALL_LEVEL, R.string.toast_small_4, NO_TOAST_SOUND);
        addToast(SMALL_LEVEL, R.string.toast_small_5, NO_TOAST_SOUND);
        addToast(SMALL_LEVEL, R.string.toast_small_6, NO_TOAST_SOUND);
        addToast(SMALL_LEVEL, R.string.toast_small_7, NO_TOAST_SOUND);

        addToast(MEDIUM_LEVEL, R.string.toast_medium_1, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_2, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_3, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_4, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_5, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_6, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_7, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_8, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_9, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_10, NO_TOAST_SOUND);
        addToast(MEDIUM_LEVEL, R.string.toast_medium_11, NO_TOAST_SOUND);

        addToast(TUMU_LEVEL, R.string.toast_tumu_1, NO_TOAST_SOUND);
        addToast(TUMU_LEVEL, R.string.toast_tumu_2, NO_TOAST_SOUND);
        addToast(TUMU_LEVEL, R.string.toast_tumu_3, NO_TOAST_SOUND);
        addToast(TUMU_LEVEL, R.string.toast_tumu_4, NO_TOAST_SOUND);
        addToast(TUMU_LEVEL, R.string.toast_tumu_5, NO_TOAST_SOUND);
        addToast(TUMU_LEVEL, R.string.toast_tumu_6, NO_TOAST_SOUND);
        addToast(TUMU_LEVEL, R.string.toast_tumu_7, NO_TOAST_SOUND);
        addToast(TUMU_LEVEL, R.string.toast_tumu_8, NO_TOAST_SOUND);
    }

    private static final void addToast(double alcoholLevel, int toastStringID, int toastSoundID) {
        List<Pair<Integer, Integer>> toastList = TOASTS.get(alcoholLevel);
        if (toastList == null) {
            toastList = new ArrayList<Pair<Integer, Integer>>();
            TOASTS.put(alcoholLevel, toastList);
        }
        toastList.add(new Pair<Integer, Integer>(toastStringID, toastSoundID));
    }

    public static Pair<Integer, Integer> getToast(double curAlcoholPercentage, boolean isDrinking) {
        Double level = SMALL_LEVEL;
        if (curAlcoholPercentage <= 0) {
            if (!isDrinking) {
                // Give the empty toast
                level = EMPTY_LEVEL;
            } else {
                level = SOBER_LEVEL;
            }
        } else {
            try {
                level = TOASTS.headMap(curAlcoholPercentage).lastKey();
            } catch (NoSuchElementException e) {
                LogUtil.w(TAG, "No proper toast found");
                level = SMALL_LEVEL;
            }
        }

        List<Pair<Integer, Integer>> toasts = TOASTS.get(level);

        int index = RANDOM.nextInt(toasts.size());
        return toasts.get(index);
    }

}
