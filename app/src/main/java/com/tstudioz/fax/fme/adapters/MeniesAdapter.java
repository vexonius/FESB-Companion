package com.tstudioz.fax.fme.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.Meni;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class MeniesAdapter extends RecyclerView.Adapter<MeniesAdapter.MeniViewHolder>  implements RealmChangeListener{

    public RealmResults<Meni> mMenies;

    public MeniesAdapter(RealmResults<Meni> meni){
        this.mMenies = meni;
        mMenies.addChangeListener(this);
    }

    @Override
    public MeniViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meni_item, parent, false);
        return new MeniViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeniViewHolder holder , int position){
        Meni meni = mMenies.get(position);

        holder.title.setTypeface(holder.regulartf);
        holder.jelo1.setTypeface(holder.lighttf);
        holder.jelo2.setTypeface(holder.lighttf);
        holder.jelo3.setTypeface(holder.lighttf);
        holder.jelo4.setTypeface(holder.lighttf);
        holder.jelo5.setTypeface(holder.lighttf);
        holder.cijena.setTypeface(holder.regulartf);

        if(meni.getId().equals("R-MENI")){
            holder.title.setText(meni.getType());
            holder.jelo1.setText(meni.getJelo1());
            holder.jelo2.setText(meni.getJelo2());
            holder.jelo3.setText(meni.getJelo3());
            holder.jelo4.setText(meni.getJelo4());
            holder.jelo5.setText(meni.getDesert());
            holder.cijena.setText(meni.getCijena());

        } else if(meni.getId().equals("R-JELO PO IZBORU")){
            holder.title.setText("JELO PO IZBORU");
            holder.jelo1.setText(meni.getJelo1());
            holder.cijena.setText(meni.getCijena());

            holder.jelo2.setVisibility(View.GONE);
            holder.jelo3.setVisibility(View.GONE);
            holder.jelo4.setVisibility(View.GONE);
            holder.jelo5.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount(){
        return mMenies.size();
    }

    public class MeniViewHolder extends RecyclerView.ViewHolder {
        TextView title, jelo1, jelo2, jelo3, jelo4, jelo5, cijena;
        Typeface regulartf, lighttf, boldtf;

        public  MeniViewHolder(final View mView){
            super(mView);

            title = (TextView)mView.findViewById(R.id.meni_title);
            jelo1 = (TextView)mView.findViewById(R.id.meni_jelo1);
            jelo2 = (TextView)mView.findViewById(R.id.meni_jelo2);
            jelo3 = (TextView)mView.findViewById(R.id.meni_jelo3);
            jelo4 = (TextView)mView.findViewById(R.id.meni_jelo4);
            jelo5 = (TextView)mView.findViewById(R.id.meni_jelo5);
            cijena = (TextView)mView.findViewById(R.id.meni_cijena);

            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            lighttf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
            boldtf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Bold.ttf");
        }

    }

    @Override
    public void onChange(Object element){
        notifyDataSetChanged();
    }
}
