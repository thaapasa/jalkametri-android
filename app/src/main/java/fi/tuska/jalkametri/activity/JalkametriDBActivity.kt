package fi.tuska.jalkametri.activity

import android.os.Bundle
import fi.tuska.jalkametri.DBActivity
import fi.tuska.jalkametri.db.DBAdapter

/**
 * Abstract base class for jAlkaMetri activities that use the database;
 * contains common functionality.
 */
abstract class JalkametriDBActivity protected constructor(titleResourceId: Int, helpTextResId: Int) : JalkametriActivity(titleResourceId, helpTextResId), GUIActivity, DBActivity {

    lateinit var db: DBAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.db = DBAdapter(this)
    }

    override fun onPause() {
        db.close()
        super.onPause()
    }

    override fun getDB(): DBAdapter {
        return db
    }

}
