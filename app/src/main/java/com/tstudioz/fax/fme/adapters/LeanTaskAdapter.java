package com.tstudioz.fax.fme.adapters;


import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.activities.NoteActivity;
import com.tstudioz.fax.fme.database.LeanTask;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class LeanTaskAdapter extends RecyclerView.Adapter<LeanTaskAdapter.LeanTaskViewHolder> implements RealmChangeListener {

    private RealmResults<LeanTask> mTasks;
    private static final int ADD_NEW = 2;
    private static final int NOTE = 1;
    Typeface light;

    public RealmConfiguration realmTaskConfiguration = new RealmConfiguration.Builder()
            .name("tasks.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .build();


    public LeanTaskAdapter(RealmResults<LeanTask> task) {
        this.mTasks = task;
        mTasks.addChangeListener(this);
    }

    @Override
    public LeanTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == ADD_NEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_lean_task_item,
                    parent, false);
            return new LeanTaskViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lean_task_item,
                    parent, false);
            return new LeanTaskViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(LeanTaskViewHolder holder, int position) {
        LeanTask leanTask = mTasks.get(position);
        holder.taskText.setText(leanTask.getTaskTekst());

        switch (holder.getItemViewType()) {
            case NOTE:
                if (mTasks.get(position).getChecked()) {
                    holder.taskText.setPaintFlags(holder.taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.point.setImageResource(R.drawable.circle_checked);
                } else {
                    holder.taskText.setPaintFlags(0);
                    holder.point.setImageResource(R.drawable.circle_white);
                }
                break;
            case ADD_NEW:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        LeanTask taskType = mTasks.get(position);
        if (taskType.getId().equals("ACTION_ADD")) {
            return 2;
        } else {
            return 1;
        }
    }

    public class LeanTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView taskText;
        ImageView point;
        Realm mRealm;

        public LeanTaskViewHolder(View itemView) {
            super(itemView);

            taskText = (TextView) itemView.findViewById(R.id.taskPointText);
            point = (ImageView) itemView.findViewById(R.id.taskPoint);

            light = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans" +
                    "-Light.ttf");
            taskText.setTypeface(light);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mRealm = Realm.getInstance(realmTaskConfiguration);

            point.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (getItemViewType()) {
                        case ADD_NEW:
                            Intent newIntent = new Intent(view.getContext(), NoteActivity.class);
                            newIntent.putExtra("mode", 2);
                            newIntent.putExtra("task_key",
                                    mTasks.get(getAdapterPosition()).getId());
                            view.getContext().startActivity(newIntent);
                            break;
                        case NOTE:
                            if (mTasks.get(getAdapterPosition()).getChecked()) {
                                taskText.setPaintFlags(0);
                                point.setImageResource(R.drawable.circle_white);
                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        mTasks.get(getAdapterPosition()).setChecked(false);
                                    }
                                });
                            } else {
                                point.setImageResource(R.drawable.circle_checked);
                                taskText.setPaintFlags(taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        mTasks.get(getAdapterPosition()).setChecked(true);
                                    }
                                });
                            }
                            break;
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (getItemViewType()) {
                case ADD_NEW:
                    Intent newIntent = new Intent(view.getContext(), NoteActivity.class);
                    newIntent.putExtra("mode", 2);
                    newIntent.putExtra("task_key", mTasks.get(getAdapterPosition()).getId());
                    view.getContext().startActivity(newIntent);
                    break;
                case NOTE:
                    taskText.setMaxLines(12);

                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {

            switch (getItemViewType()) {
                case ADD_NEW:
                    Intent newIntent = new Intent(view.getContext(), NoteActivity.class);
                    newIntent.putExtra("mode", 2);
                    newIntent.putExtra("task_key", mTasks.get(getAdapterPosition()).getId());
                    view.getContext().startActivity(newIntent);
                    break;
                case NOTE:
                    Intent intent = new Intent(view.getContext(), NoteActivity.class);
                    intent.putExtra("mode", 1);
                    intent.putExtra("task_key", mTasks.get(getAdapterPosition()).getId());
                    view.getContext().startActivity(intent);
                    break;
            }

            return true;
        }
    }

    @Override
    public void onChange(Object element) {
        notifyDataSetChanged();
    }
}
