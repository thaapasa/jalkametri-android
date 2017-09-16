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

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class SoundPlayer {

    private static final OnCompletionListener RELEASE_ON_COMPLETE = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.release();
        }
    };

    /**
     * Plays sound files given as raw resources.
     * 
     * @param context the context file
     * @param resID the resource identifier (R.raw.xxxx)
     * @return true on success; false if the media player could not be created
     */
    public static final boolean playSound(Context context, int resID) {
        MediaPlayer mp = MediaPlayer.create(context, resID);
        if (mp == null) {
            return false;
        }
        mp.setOnCompletionListener(RELEASE_ON_COMPLETE);
        mp.start();
        return true;
    }
}
