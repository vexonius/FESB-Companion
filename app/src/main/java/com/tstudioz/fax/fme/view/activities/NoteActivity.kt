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
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Note
import io.realm.kotlin.Realm
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
            mTaskId = intent.extras?.getString("note_key")
        }

        val realm = Realm.open(dbManager.getDefaultConfiguration())

        if (mTaskId != null) {
            val note: Note? = realm.query<Note>("id = $0", mTaskId).first()?.find()
            et?.setText(note?.noteTekst)
        }

        saveNoteListener()
    }

    private fun saveNoteListener() {
        val button: Button = findViewById(R.id.saveButton)
        button.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()

        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val stringBiljeska = et?.text.toString().trim()

        realm.writeBlocking {
            if (mTaskId != null && stringBiljeska.isNotEmpty()) {
                val note = this.query<Note>("id = $0", mTaskId).first().find()

                note?.let {
                    it.noteTekst = stringBiljeska
                    it.checked = false
                    this.copyToRealm(it, updatePolicy = UpdatePolicy.ALL)
                }
            } else if (mTaskId != null && stringBiljeska.isEmpty()) {
                findLatest(realm.query<Note>("id = $0", mTaskId).find().first()).let {
                    if (it != null) {
                        delete(it)
                    }
                }
            } else if (stringBiljeska.isNotEmpty()) {
                val note = Note().apply {
                    noteTekst = stringBiljeska
                    checked = false
                }
                this.copyToRealm(note, updatePolicy = UpdatePolicy.ALL)
            } else {
                // Do nothing
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