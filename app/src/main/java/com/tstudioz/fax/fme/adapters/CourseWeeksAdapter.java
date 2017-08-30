package com.tstudioz.fax.fme.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.KolegijTjedan;
import com.tstudioz.fax.fme.database.Materijal;

import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;


public class CourseWeeksAdapter extends RecyclerView.Adapter<CourseWeeksAdapter.CoursesWeeksViewHolder> implements RealmChangeListener {

    private RealmResults<KolegijTjedan> tjedni;
    Typeface regulartf, lighttf;
    private static RecyclerView materialRecycler;

    public CourseWeeksAdapter(RealmResults<KolegijTjedan> tjedan) {
        this.tjedni = tjedan;
        tjedni.addChangeListener(this);
    }

    @Override
    public CoursesWeeksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_course_week, parent, false);
        return new CoursesWeeksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CoursesWeeksViewHolder holder, int position) {

        KolegijTjedan tjedan = tjedni.get(position);
        holder.matAdapter = new MaterialsAdapter(tjedan.getMaterijali());
        materialRecycler.setAdapter(holder.matAdapter);

        if(tjedan.getTjedan().isEmpty()) {
            holder.mWeek.setVisibility(View.GONE);
        }else{
            holder.mWeek.setText(tjedan.getTjedan());
        }
        holder.mWeekDesc.setText(tjedan.getOpis());
        holder.mWeek.setTypeface(lighttf);
        holder.mWeekDesc.setTypeface(regulartf);

    }


    @Override
    public int getItemCount() {
        return tjedni.size();
    }

    public class CoursesWeeksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mWeek, mWeekDesc;
        private MaterialsAdapter matAdapter;


        public CoursesWeeksViewHolder(final View itemView) {
            super(itemView);
            mWeek = (TextView) itemView.findViewById(R.id.tjedanText);
            lighttf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
            mWeek.setTypeface(lighttf);

            mWeekDesc = (TextView) itemView.findViewById(R.id.tjedanOpis);
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            mWeekDesc.setTypeface(regulartf);

            Context context = itemView.getContext();
            materialRecycler = (RecyclerView)itemView.findViewById(R.id.mat_recyc);
            materialRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            materialRecycler.hasFixedSize();

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


        }
    }

    @Override
    public void onChange(Object element) {
        notifyDataSetChanged();
    }

}



