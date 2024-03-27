package com.tstudioz.fax.fme.view.adapters

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.view.activities.NoteActivity
import com.tstudioz.fax.fme.view.adapters.NoteAdapter.NoteViewHolder
import io.realm.kotlin.Realm
import io.realm.kotlin.query.RealmResults
import org.koin.core.KoinComponent
import org.koin.core.inject

class NoteAdapter(private val mNotes: RealmResults<Note>) :
    RecyclerView.Adapter<NoteViewHolder>(), KoinComponent {
    var light: Typeface? = null

    private val dbManager: DatabaseManagerInterface by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view: View
        return if (viewType == ADD_NEW) {
            view = LayoutInflater.from(parent.context).inflate(
                R.layout.add_note_item,
                parent, false
            )
            NoteViewHolder(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(
                R.layout.note_item,
                parent, false
            )
            NoteViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = mNotes[position]
        holder.noteText.text = note.noteTekst
        when (holder.itemViewType) {
            NOTE -> if (mNotes[position].checked) {
                holder.noteText.paintFlags =
                    holder.noteText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.point.setImageResource(R.drawable.circle_checked)
            } else {
                holder.noteText.paintFlags = 0
                holder.point.setImageResource(R.drawable.circle_white)
            }

            ADD_NEW -> {}
        }
    }

    override fun getItemCount(): Int {
        return mNotes.size
    }

    override fun getItemViewType(position: Int): Int {
        val note = mNotes[position]
        return if ((note.id == "ACTION_ADD")) {
            ADD_NEW
        } else {
            NOTE
        }
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, OnLongClickListener {
        var noteText: TextView
        var point: ImageView
        private var mRealm: Realm

        init {
            noteText = itemView.findViewById<View>(R.id.notePointText) as TextView
            point = itemView.findViewById<View>(R.id.notePoint) as ImageView
            light = Typeface.createFromAsset(
                itemView.context.assets, "fonts/OpenSans" +
                        "-Light.ttf"
            )
            noteText.typeface = light
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            mRealm = Realm.open(dbManager.getDefaultConfiguration())
            point.setOnClickListener { view ->
                when (itemViewType) {
                    ADD_NEW -> {
                        val newIntent = Intent(view.context, NoteActivity::class.java)
                        newIntent.putExtra("mode", 2)
                        newIntent.putExtra("note_key", mNotes[bindingAdapterPosition].id)
                        view.context.startActivity(newIntent)
                    }

                    NOTE -> {
                        if (mNotes[bindingAdapterPosition].checked) {
                            noteText.paintFlags = 0
                            point.setImageResource(R.drawable.circle_white)
                            mRealm.writeBlocking {
                                findLatest(mNotes[bindingAdapterPosition]).let {
                                    if (it != null) {
                                        it.checked = false
                                    }
                                }
                            }
                        } else {
                            point.setImageResource(R.drawable.circle_checked)
                            noteText.paintFlags = noteText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                            mRealm.writeBlocking {
                                findLatest(mNotes[bindingAdapterPosition]).let {
                                    if (it != null) {
                                        it.checked = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun onClick(view: View) {
            when (itemViewType) {
                ADD_NEW -> {
                    val newIntent = Intent(view.context, NoteActivity::class.java)
                    newIntent.putExtra("mode", 2)
                    newIntent.putExtra("note_key", mNotes[bindingAdapterPosition].id)
                    view.context.startActivity(newIntent)
                }

                NOTE -> noteText.maxLines = 12
            }
        }

        override fun onLongClick(view: View): Boolean {
            when (itemViewType) {
                ADD_NEW -> {
                    val newIntent = Intent(view.context, NoteActivity::class.java)
                    newIntent.putExtra("mode", 2)
                    newIntent.putExtra("note_key", mNotes[bindingAdapterPosition].id)
                    view.context.startActivity(newIntent)
                }

                NOTE -> {
                    val intent = Intent(view.context, NoteActivity::class.java)
                    intent.putExtra("mode", 1)
                    intent.putExtra("note_key", mNotes[bindingAdapterPosition].id)
                    view.context.startActivity(intent)
                }
            }
            return true
        }
    }

    companion object {
        private const val ADD_NEW = 2
        private const val NOTE = 1
    }

}