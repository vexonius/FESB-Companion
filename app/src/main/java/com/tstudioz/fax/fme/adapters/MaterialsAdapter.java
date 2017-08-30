package com.tstudioz.fax.fme.adapters;



import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.Materijal;


import io.realm.RealmChangeListener;
import io.realm.RealmList;



public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialViewHolder> implements RealmChangeListener {
    private RealmList<Materijal> materials;
    Typeface regulartf;


    public MaterialsAdapter(RealmList<Materijal> material) {
        this.materials = material;
        materials.addChangeListener(this);
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.materijal_item, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position) {

        Materijal materijal = materials.get(position);
        holder.name.setText(materijal.getImeMtarijala());
        holder.name.setTypeface(regulartf);

        }


    @Override
    public int getItemCount() {
        return materials.size();
    }

    public class MaterialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;


        public MaterialViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.mat_text);
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
            name.setTypeface(regulartf);

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


