package com.tstudioz.fax.fme.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.KolegijTjedan;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class CourseWeeksAdapter extends RecyclerView.Adapter<CourseWeeksAdapter.CoursesWeeksViewHolder> implements RealmChangeListener {

    private RealmResults<KolegijTjedan> tjedni;
    Typeface regulartf, lighttf, boldtf;
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

        holder.setIsRecyclable(false);

        KolegijTjedan tjedan = tjedni.get(position);

        holder.matAdapter = new MaterialsAdapter(tjedan.getMaterijali());
        materialRecycler.setAdapter(holder.matAdapter);

        if (position == 0 && tjedan.getTjedan().contains("This week")) {
            holder.mWeek.setText("Ovaj tjedan");
            holder.mWeek.setPadding(25, 20, 25, 20);
            holder.mWeek.setTypeface(boldtf);
            holder.mWeek.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.mWeek.setBackgroundColor(ContextCompat.getColor(holder.mWeek.getContext(), R.color.blue_nice));
            holder.mWeek.setTextColor(ContextCompat.getColor(holder.mWeek.getContext(), R.color.white));
        } else if (tjedan.getTjedan().isEmpty()) {
            holder.mWeek.setVisibility(View.GONE);
            holder.mWeek.setTypeface(lighttf);
        } else {
            holder.mWeek.setText(tjedan.getTjedan());
        }

        if (tjedan.getOpis().isEmpty()) {
            holder.mWeekDesc.setVisibility(View.GONE);
        } else {
            holder.mWeekDesc.setText(tjedan.getOpis());
            holder.mWeekDesc.setTypeface(regulartf);
        }


    }


    @Override
    public int getItemCount() {
        return tjedni.size();
    }

    public class CoursesWeeksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout relativeLayout;
        TextView mWeek, mWeekDesc;
        private MaterialsAdapter matAdapter;


        public CoursesWeeksViewHolder(final View itemView) {
            super(itemView);

            mWeek = (TextView) itemView.findViewById(R.id.tjedanText);
            lighttf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
            boldtf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Bold.ttf");
            mWeek.setTypeface(boldtf);

            mWeekDesc = (TextView) itemView.findViewById(R.id.tjedanOpis);
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            mWeekDesc.setTypeface(regulartf);

            Context context = itemView.getContext();
            materialRecycler = (RecyclerView) itemView.findViewById(R.id.mat_recyc);
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



