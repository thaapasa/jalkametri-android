package fi.tuska.jalkametri.activity

import android.os.Bundle
import fi.tuska.jalkametri.DBActivity
import fi.tuska.jalkametri.db.DBAdapter

/**
 * Abstract base class for jAlkaMetri activities that use the database;
 * contains common functionality.
 */
abstract class JalkametriDBActivity protected constructor(titleResourceId: Int, helpTextResId: Int) : JalkametriActivity(titleResourceId, helpTextResId), GUIActivity, DBActivity {

    private var currentAdapter: DBAdapter? = null
    protected val adapter: DBAdapter
        get() = currentAdapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.currentAdapter = DBAdapter(this)
    }

    override fun onPause() {
        adapter.close()
        super.onPause()
    }

    override fun getDBAdapter(): DBAdapter {
        return adapter
    }

}
