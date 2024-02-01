package com.tstudioz.fax.fme.view.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.LeanTask
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.UUID


//Converted NoteActivity to kotlin, fixed return by adding a condition



class NoteActivity : AppCompatActivity() {
    private var tRealm: Realm? = null
    private var mTaskId: String? = null
    private var et: EditText? = null
    private var realmTaskConfiguration: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("tasks.realm")
        .deleteRealmIfMigrationNeeded()
        .schemaVersion(1)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.note_layout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        et = findViewById<View>(R.id.textEditor) as EditText

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
        if (intent.extras?.getInt("mode") == 1) {
            mTaskId = intent.extras?.getString("task_key")
        }

        tRealm = Realm.getInstance(realmTaskConfiguration)
        tRealm.use { tRealm ->
            if (mTaskId != null) {
                val leanTask = tRealm?.where(LeanTask::class.java)?.equalTo("id", mTaskId)?.findFirst()
                et?.setText(leanTask?.taskTekst)
            }
        }
        saveNoteListener()
    }

    private fun saveNoteListener() {
        val button : Button =  findViewById(R.id.saveButton)
        button.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        val sRealm = Realm.getInstance(realmTaskConfiguration)
        val stringBiljeska = et?.text.toString()
        sRealm.use { sRlm ->
            if (mTaskId != null && stringBiljeska.trim { it <= ' ' } != "") {
                sRlm.executeTransaction { realm ->
                    val leanTask = realm.where(LeanTask::class.java).equalTo("id", mTaskId).findFirst()
                    leanTask?.taskTekst = stringBiljeska
                    leanTask?.checked = false
                }
            } else if (mTaskId != null && stringBiljeska.trim { it <= ' ' } == "") {
                sRlm.executeTransaction { realm ->
                    val leanTask = realm.where(LeanTask::class.java).equalTo("id", mTaskId).findFirst()
                    leanTask?.deleteFromRealm()
                }
            } else if (stringBiljeska.trim { it <= ' ' } != "") {
                sRlm.executeTransaction { realm ->
                    val leanTask = realm.createObject(LeanTask::class.java, UUID.randomUUID().toString())
                    leanTask?.taskTekst = stringBiljeska
                    leanTask?.checked = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                et?.setText("")
                finish()
                true
            }
            R.id.saveButton -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}