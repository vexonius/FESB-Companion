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
import com.tstudioz.fax.fme.database.models.LeanTask
import com.tstudioz.fax.fme.view.activities.NoteActivity
import com.tstudioz.fax.fme.view.adapters.NoteAdapter.NoteViewHolder
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import okhttp3.internal.notifyAll
import org.koin.core.KoinComponent
import org.koin.core.inject

class NoteAdapter(private val mTasks: RealmResults<LeanTask>) :
    RecyclerView.Adapter<NoteViewHolder>(), KoinComponent {
    var light: Typeface? = null

    private val dbManager: DatabaseManagerInterface by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view: View
        return if (viewType == ADD_NEW) {
            view = LayoutInflater.from(parent.context).inflate(
                R.layout.add_lean_task_item,
                parent, false
            )
            NoteViewHolder(view)
        } else {
            view = LayoutInflater.from(parent.context).inflate(
                R.layout.lean_task_item,
                parent, false
            )
            NoteViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val leanTask = mTasks[position]
        holder.taskText.text = leanTask?.taskTekst
        when (holder.itemViewType) {
            NOTE -> if (mTasks[position]?.checked == true) {
                holder.taskText.paintFlags =
                    holder.taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.point.setImageResource(R.drawable.circle_checked)
            } else {
                holder.taskText.paintFlags = 0
                holder.point.setImageResource(R.drawable.circle_white)
            }

            ADD_NEW -> {}
        }
    }

    override fun getItemCount(): Int {
        return mTasks.size
    }

    override fun getItemViewType(position: Int): Int {
        val taskType = mTasks[position]
        return if ((taskType?.id == "ACTION_ADD")) {
            2
        } else {
            1
        }
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, OnLongClickListener {
        var taskText: TextView
        var point: ImageView
        var mRealm: Realm

        init {
            taskText = itemView.findViewById<View>(R.id.taskPointText) as TextView
            point = itemView.findViewById<View>(R.id.taskPoint) as ImageView
            light = Typeface.createFromAsset(
                itemView.context.assets, "fonts/OpenSans" +
                        "-Light.ttf"
            )
            taskText.typeface = light
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            mRealm = Realm.open(dbManager.getDefaultConfiguration())
            point.setOnClickListener { view ->
                val leanTask = mRealm.query<LeanTask>("id = $0",  mTasks[adapterPosition].id).first().find()
                when (itemViewType) {
                    ADD_NEW -> {
                        val newIntent = Intent(view.context, NoteActivity::class.java)
                        newIntent.putExtra("mode", 2)
                        newIntent.putExtra(
                            "task_key",
                            mTasks[adapterPosition]?.id
                        )
                        view.context.startActivity(newIntent)
                    }

                    NOTE -> if (leanTask?.checked == true) {
                        taskText.paintFlags = 0
                        point.setImageResource(R.drawable.circle_white)
                        mRealm.writeBlocking {
                            findLatest(mTasks[adapterPosition]).let {
                                if (it != null) {
                                    it.checked = false
                                }
                            }
                        }
                    } else {
                        point.setImageResource(R.drawable.circle_checked)
                        taskText.paintFlags = taskText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        mRealm.writeBlocking {
                            findLatest(mTasks[adapterPosition]).let {
                                if (it != null) {
                                    it.checked = true
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
                    newIntent.putExtra("task_key", mTasks[adapterPosition]?.id)
                    view.context.startActivity(newIntent)
                }

                NOTE -> taskText.maxLines = 12
            }
        }

        override fun onLongClick(view: View): Boolean {
            when (itemViewType) {
                ADD_NEW -> {
                    val newIntent = Intent(view.context, NoteActivity::class.java)
                    newIntent.putExtra("mode", 2)
                    newIntent.putExtra("task_key", mTasks[adapterPosition]?.id)
                    view.context.startActivity(newIntent)
                }

                NOTE -> {
                    val intent = Intent(view.context, NoteActivity::class.java)
                    intent.putExtra("mode", 1)
                    intent.putExtra("task_key", mTasks[adapterPosition]?.id)
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