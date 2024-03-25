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
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.LeanTask
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import org.koin.android.ext.android.inject

class NoteActivity : AppCompatActivity() {

    private val dbManager: DatabaseManagerInterface by inject()

    private var mTaskId: String? = null
    private var et: EditText? = null

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

        val realm = Realm.open(dbManager.getDefaultConfiguration())

        if (mTaskId != null) {
            val leanTask: LeanTask? = realm.query<LeanTask>("id = $0", mTaskId).first()?.find()
            et?.setText(leanTask?.taskTekst)
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

        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val stringBiljeska = et?.text.toString().trim()

        if (stringBiljeska.isEmpty()) {
            return
        }

        realm.writeBlocking {
            if (mTaskId != null) {
                val leanTask = this.query<LeanTask>("id = $0", mTaskId).first().find()

                leanTask?.let {
                    it.taskTekst = stringBiljeska
                    it.checked = false
                    this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
                }
            } else {
                val leanTask = LeanTask().apply {
                    taskTekst = stringBiljeska
                    checked = false
                }
                this.copyToRealm(leanTask, updatePolicy = UpdatePolicy.ALL)
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