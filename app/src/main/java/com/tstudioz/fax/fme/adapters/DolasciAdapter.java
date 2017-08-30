package com.tstudioz.fax.fme.adapters;



import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.Dolazak;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class DolasciAdapter extends RecyclerView.Adapter<DolasciAdapter.DolazakViewHolder> implements RealmChangeListener{
    private RealmResults<Dolazak> mDolazak;
    Typeface regulartf;

    public DolasciAdapter(RealmResults<Dolazak> dolazak) {
        this.mDolazak = dolazak;
        mDolazak.addChangeListener(this);
    }

    @Override
    public DolazakViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_attendance, parent, false);
        return new DolazakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DolazakViewHolder holder, int position) {

        Dolazak predavanja = mDolazak.get(position);
        holder.name.setText(predavanja.getPredmet());
        holder.name.setTypeface(regulartf);

        holder.type.setText(predavanja.getVrsta());

        holder.dolazakNum.setText("Obavezno " + predavanja.getRequired());

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(predavanja.getAttended()));
        entries.add(new PieEntry(predavanja.getAbsent()));
        PieEntry neood = new PieEntry(predavanja.getTotal() - predavanja.getAbsent() - predavanja.getAttended());
        if(neood.getValue()!=0.0){
            entries.add(neood);
        }

        IValueFormatter formatter = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return ((int) value) + "";
            }
        };

        holder.chart.setHoleColor(android.R.color.transparent);
        holder.chart.setDrawCenterText(false);
        holder.chart.setDrawEntryLabels(false);

        holder.chart.getLegend().setEnabled(false);
        holder.chart.getDescription().setEnabled(false);

        PieDataSet set = new PieDataSet(entries, null);
        set.setValueTextSize(12f);
        set.setValueTextColor(holder.colorWhite);
        set.setValueTypeface(regulartf);
        set.setColors(holder.colors);
        set.setValueFormatter(formatter);
        PieData data = new PieData(set);
        holder.chart.setData(data);

        holder.chart.invalidate(); // refresh
        holder.chart.animateY(600);
    }

    @Override
    public int getItemCount() {
        return mDolazak.size();
    }

    public class DolazakViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, dolazakNum;
        RelativeLayout boja;
        PieChart chart;
        int colors[];
        int colorWhite;


        public DolazakViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.predmetName);
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Regular.ttf");
            name.setTypeface(regulartf);

            type = (TextView) itemView.findViewById(R.id.predmetType);
            type.setTypeface(regulartf);

            dolazakNum = (TextView) itemView.findViewById(R.id.dolazakNum);
            dolazakNum.setTypeface(regulartf);

             colors = itemView.getResources().getIntArray(R.array.rainbow);
            colorWhite = itemView.getResources().getColor(R.color.white);

            chart = (PieChart) itemView.findViewById(R.id.chart);
        }
    }

    @Override
    public void onChange(Object element) {
        notifyDataSetChanged();
    }

}
