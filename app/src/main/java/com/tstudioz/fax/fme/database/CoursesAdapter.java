package com.tstudioz.fax.fme.database;



import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.activities.MainActivity;
import com.tstudioz.fax.fme.fragments.Kolegiji;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;



public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder> implements RealmChangeListener {
    private RealmResults<Kolegij> kolegijs;
    Typeface regulartf;


    public CoursesAdapter(RealmResults<Kolegij> kolegij) {
        this.kolegijs = kolegij;
        kolegijs.addChangeListener(this);
    }

    @Override
    public CoursesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_course, parent, false);
        return new CoursesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CoursesViewHolder holder, int position) {

        Kolegij kolegij = kolegijs.get(position);
        holder.name.setText(kolegij.getName());
        holder.name.setTypeface(regulartf);

        }


    @Override
    public int getItemCount() {
        return kolegijs.size();
    }

    public class CoursesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;


        public CoursesViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.courseName);
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            name.setTypeface(regulartf);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
         //   Kolegij course = kolegijs.get(getAdapterPosition());
         //   Toast.makeText(view.getContext(), course.getLink(), Toast.LENGTH_SHORT).show();
//
         //   new Kolegiji().fetchCourseContent(course.getLink(), view.getContext());

        }


    }


        @Override
        public void onChange(Object element) {
            notifyDataSetChanged();
        }





}


