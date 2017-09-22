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
