package com.tstudioz.fax.fme.database;

/**
 * Created by amarthus on 25-Mar-17.
 */



import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tstudioz.fax.fme.R;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class EmployeeRVAdapterTable extends RecyclerView.Adapter<EmployeeRVAdapterTable.EmployeeViewHolderTable> implements RealmChangeListener {
    private RealmResults<Predavanja> mEmployees;

    BottomSheetDialog dialog;

    public EmployeeRVAdapterTable(RealmResults<Predavanja> employee) {
        this.mEmployees = employee;
        mEmployees.addChangeListener(this);
    }

    @Override
    public EmployeeViewHolderTable onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_item, parent, false);
        return new EmployeeViewHolderTable(view);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolderTable holder, int position) {

        Predavanja predavanja = mEmployees.get(position);
        holder.tablename.setText(predavanja.getPredmetPredavanja());
        holder.tabletype.setText(predavanja.getRasponVremena());
        holder.tablemjesto.setText(predavanja.getDvorana());


        switch (predavanja.getPredavanjeIme()){
                case("Predavanja,"):
                    holder.tableboja.setBackgroundResource(R.color.blue_nice);
                    break;
                case("Auditorne vježbe,"):
                    holder.tableboja.setBackgroundResource(R.color.green_nice);
                    break;
                case("Kolokviji,"):
                    holder.tableboja.setBackgroundResource(R.color.purple_nice);
                    break;
                case("Laboratorijske vježbe,"):
                    holder.tableboja.setBackgroundResource(R.color.red_nice);
                    break;
                case("Konstrukcijske vježbe,"):
                    holder.tableboja.setBackgroundResource(R.color.grey_nice);
                    break;
            case("Seminar,"):
                    holder.tableboja.setBackgroundResource(R.color.blue_nice);
                    break;
                case("Ispiti,"):
                    holder.tableboja.setBackgroundResource(R.color.purple_dark);
                    break;
        }


    }

    @Override
    public int getItemCount() {
        return mEmployees.size();
    }

    public class EmployeeViewHolderTable extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tablename, tabletype, tablemjesto;
        RelativeLayout tableboja;


        public EmployeeViewHolderTable(View itemView) {
            super(itemView);
            tablename = (TextView) itemView.findViewById(R.id.table_name);
            tabletype = (TextView) itemView.findViewById(R.id.table_type);
            tableboja = (RelativeLayout)itemView.findViewById(R.id.colorMe);
            tablemjesto = (TextView)itemView.findViewById(R.id.table_mjesto);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onShowBottomsheet(view, getAdapterPosition());
        }
    }


    @Override
    public void onChange(Object element) {
        notifyDataSetChanged();
    }

    public void onShowBottomsheet(View view, int position){
        Context context = view.getContext();


        Predavanja predavanja = mEmployees.get(position);

        TextView infoKolegij, infoPredavanje,infoProf, infoVrijeme, infoGrupa, infoLokacija;
        View views = LayoutInflater.from(context).inflate(R.layout.bottom_sheep, null);
        infoKolegij = (TextView) views.findViewById(R.id.predavanjeImeDialog);
        infoPredavanje = (TextView) views.findViewById(R.id.opisPredavanja);
        infoProf = (TextView) views.findViewById(R.id.text_ispod2);
        infoVrijeme = (TextView) views.findViewById(R.id.text_ispod3);
        infoGrupa = (TextView) views.findViewById(R.id.text_ispod4);
        infoLokacija = (TextView) views.findViewById(R.id.text_ispod5);


        String imePredavanja = predavanja.getPredavanjeIme();
        if(imePredavanja.length()>0){
            imePredavanja = imePredavanja.substring(0, imePredavanja.length()-1);
        }

        String imeGrupe = predavanja.getGrupa();

        if(imeGrupe.length()>0) {
            imeGrupe = imeGrupe.substring(0, imeGrupe.length() - 1);
        }

        infoKolegij.setText(predavanja.getPredmetPredavanja());
        infoPredavanje.setText(imePredavanja);
        infoProf.setText(predavanja.getProfesor());
        infoVrijeme.setText(predavanja.getRasponVremena());
        infoGrupa.setText(imeGrupe);
        infoLokacija.setText(predavanja.getDvorana());


        switch (predavanja.getPredavanjeIme()){
            case("Predavanja,"):
                infoKolegij.setBackgroundResource(R.color.blue_nice);
                break;
            case("Auditorne vježbe,"):
                infoKolegij.setBackgroundResource(R.color.green_nice);
                break;
            case("Kolokviji,"):
                infoKolegij.setBackgroundResource(R.color.purple_nice);
                break;
            case("Laboratorijske vježbe,"):
                infoKolegij.setBackgroundResource(R.color.red_nice);
                break;
            case("Konstrukcijske vježbe,"):
                infoKolegij.setBackgroundResource(R.color.grey_nice);
                break;
            case("Seminar,"):
                infoKolegij.setBackgroundResource(R.color.blue_nice);
                break;
            case("Ispiti,"):
                infoKolegij.setBackgroundResource(R.color.purple_dark);
                break;
        }

        dialog = new BottomSheetDialog(context);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(views);
        dialog.show();
    }
}
