package com.tstudioz.fax.fme.adapters;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.Predavanja;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class EmployeeRVAdapter extends RecyclerView.Adapter<EmployeeRVAdapter.EmployeeViewHolder> implements RealmChangeListener {
    private RealmResults<Predavanja> mEmployees;
    Typeface boldtf;
    Typeface regulartf;
    Typeface lighttf;


    public EmployeeRVAdapter(RealmResults<Predavanja> employee) {
        this.mEmployees = employee;
        mEmployees.addChangeListener(this);
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {

        Predavanja predavanja = mEmployees.get(position);
        holder.name.setText(predavanja.getPredmetPredavanja());
        holder.name.setTypeface(regulartf);

        holder.type.setText(predavanja.getRasponVremena());

        String imePredavanja = predavanja.getPredavanjeIme();
        if(imePredavanja.length()>0){
        imePredavanja = imePredavanja.substring(0, imePredavanja.length()-1);  }
        holder.vrstaPredavanja.setText(imePredavanja);

        holder.mjesto.setText(predavanja.getDvorana());

        switch (predavanja.getPredavanjeIme()){
                case("Predavanja,"):
                    holder.boja.setBackgroundResource(R.color.blue_nice);
                    break;
                case("Auditorne vježbe,"):
                    holder.boja.setBackgroundResource(R.color.green_nice);
                    break;
                case("Kolokviji,"):
                    holder.boja.setBackgroundResource(R.color.purple_nice);
                    break;
                case("Laboratorijske vježbe,"):
                    holder.boja.setBackgroundResource(R.color.red_nice);
                    break;
                case("Konstrukcijske vježbe,"):
                    holder.boja.setBackgroundResource(R.color.grey_nice);
                    break;
            case("Seminar,"):
                    holder.boja.setBackgroundResource(R.color.blue_nice);
                    break;
                case("Ispiti,"):
                    holder.boja.setBackgroundResource(R.color.purple_dark);
                    break;
        }


    }

    @Override
    public int getItemCount() {
        return mEmployees.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, vrstaPredavanja, mjesto;
        RelativeLayout boja;


        public EmployeeViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            lighttf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
            boldtf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Bold.ttf");
            name.setTypeface(regulartf);

            type = (TextView) itemView.findViewById(R.id.type);
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            type.setTypeface(regulartf);

            vrstaPredavanja = (TextView) itemView.findViewById(R.id.vrstaPredavanja);
            vrstaPredavanja.setTypeface(regulartf);

            mjesto = (TextView)itemView.findViewById(R.id.mjesto);
            mjesto.setTypeface(regulartf);

            boja = (RelativeLayout)itemView.findViewById(R.id.textBox);
        }
    }

    @Override
    public void onChange(Object element) {
        notifyDataSetChanged();
    }
}