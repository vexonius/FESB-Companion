package com.tstudioz.fax.fme.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.activities.CourseActivity;
import com.tstudioz.fax.fme.activities.MainActivity;
import com.tstudioz.fax.fme.database.Kolegij;
import com.tstudioz.fax.fme.util.CircularAnim;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_course,
                parent, false);
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
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts" +
                    "/OpenSans-Regular.ttf");
            name.setTypeface(regulartf);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Context context = view.getContext();
            Kolegij kolegiji = kolegijs.get(getAdapterPosition());

            final Intent intent = new Intent(context, CourseActivity.class);
            intent.putExtra("kolegij", kolegiji.getName());
            intent.putExtra("link_na_kolegij", kolegiji.getLink());


            CircularAnim.fullActivity((MainActivity) (view.getContext()), view)
                    .colorOrImageRes(R.color.colorPrimaryDark)
                    .go(new CircularAnim.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd() {
                            context.startActivity(intent);
                        }
                    });

        }
    }

    @Override
    public void onChange(Object element) {
        notifyDataSetChanged();
    }


}


