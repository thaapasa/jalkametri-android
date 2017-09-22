package fi.tuska.jalkametri.gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import fi.tuska.jalkametri.R;

/**
 * Executes a task, showing a progress dialog during the execution.
 *
 * @author Tuukka Haapasalo
 */
public class TaskExecutor {

    public interface Task<V> {
        V runTask();
    }

    public interface Result<V> {
        void processResult(V result);
    }

    /**
     * Must only be called from the UI thread.
     *
     * @param task the task to run. Will be run in a background thread.
     * @param uponCompletion the task to run upon completion. Will be run on
     * the UI thread. May be null.
     */
    public static void execute(Context context, int msgResId, final Runnable task,
        final Runnable uponCompletion) {
        execute(context, msgResId, new Task<Void>() {
            @Override
            public Void runTask() {
                if (task != null)
                    task.run();
                return null;
            }
        }, new Result<Void>() {

            @Override
            public void processResult(Void result) {
                if (uponCompletion != null)
                    uponCompletion.run();
            }
        });
    }

    /**
     * Must only be called from the UI thread.
     *
     * @param task the task to run. Will be run in a background thread.
     * @param uponCompletion the task to run upon completion. Will be run on
     * the UI thread. May be null.
     */
    public static <V> void execute(Context context, int msgResId, final Task<V> task,
        final Result<V> uponCompletion) {

        final Resources res = context.getResources();
        final ProgressDialog dialog = ProgressDialog.show(context,
            res.getString(R.string.title_wait), res.getString(msgResId), true, false);

        AsyncTask<Void, Void, V> asTask = new AsyncTask<Void, Void, V>() {
            @Override
            protected V doInBackground(Void... params) {
                if (task != null)
                    return task.runTask();
                return null;
            }

            @Override
            protected void onPostExecute(V result) {
                dialog.dismiss();
                if (uponCompletion != null)
                    uponCompletion.processResult(result);

            }
        };

        asTask.execute();
    }
}
