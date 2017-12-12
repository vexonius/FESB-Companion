package com.tstudioz.fax.fme.adapters;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.LeanTask;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class LeanTaskAdapter extends RecyclerView.Adapter<LeanTaskAdapter.LeanTaskViewHolder> implements RealmChangeListener {

    private RealmResults<LeanTask> mTasks;
   // public static int EMPTY_VIEW = 1;
    Typeface light;

    public LeanTaskAdapter(RealmResults<LeanTask> task){
        this.mTasks = task;
        mTasks.addChangeListener(this);
    }

    @Override
    public LeanTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
      //  if (viewType == EMPTY_VIEW){
      //       view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lean_task_item, parent, false);
      //  } else {}
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lean_task_item, parent, false);
        return new LeanTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeanTaskViewHolder holder, int position){
        LeanTask leanTask = mTasks.get(position);
        holder.taskText.setText(leanTask.getTaskTekst());
    }



    @Override
    public int getItemCount(){
        return mTasks.size();
    }

    public class LeanTaskViewHolder extends RecyclerView.ViewHolder{

        TextView taskText;

        public LeanTaskViewHolder(View itemView) {
            super(itemView);

            taskText = (TextView)itemView.findViewById(R.id.taskPointText);
            light = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
            taskText.setTypeface(light);
        }
    }

    @Override
    public void onChange(Object element) {
        notifyDataSetChanged();
    }
}
