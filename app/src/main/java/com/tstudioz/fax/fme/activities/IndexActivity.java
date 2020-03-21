package com.tstudioz.fax.fme.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.databinding.ActivityIndexBinding;

import java.util.ArrayList;
import java.util.List;


public class IndexActivity extends AppCompatActivity {

    private ActivityIndexBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIndexBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RadarEntry entry = new RadarEntry(5f, 0);
        RadarEntry entry4 = new RadarEntry(3f, 1);
        RadarEntry entry5 = new RadarEntry(5f, 2);
        RadarEntry entry3 = new RadarEntry(4f, 3);
        RadarEntry entry2 = new RadarEntry(5f, 4);

        IValueFormatter formatter = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                            ViewPortHandler viewPortHandler) {
                return ((int) value) + "";
            }
        };

        List<RadarEntry> entryList = new ArrayList<RadarEntry>();
        entryList.add(entry4);
        entryList.add(entry5);
        entryList.add(entry3);
        entryList.add(entry);
        entryList.add(entry2);

        List<String> labels = new ArrayList<String>();
        labels.add("Hello");
        labels.add("Hello");
        labels.add("Hello");
        labels.add("Hello");
        labels.add("Hello");

        RadarDataSet rDataSet = new RadarDataSet(entryList, "");
        rDataSet.setValueTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
        rDataSet.setColor(ContextCompat.getColor(getBaseContext(), R.color.blue_nice));
        rDataSet.setHighlightCircleFillColor(ContextCompat.getColor(getBaseContext(),
                R.color.white));
        rDataSet.setHighlightCircleStrokeColor(ContextCompat.getColor(getBaseContext(),
                R.color.white));
        rDataSet.setValueFormatter(formatter);
        rDataSet.setDrawFilled(true);

        RadarData data = new RadarData(rDataSet);
        data.setValueTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
        data.setLabels(labels);
        data.setValueTextSize(14f);

        binding.radarChart.setData(data);
        binding.radarChart.setDrawWeb(true);
        binding.radarChart.setWebColor(ContextCompat.getColor(getBaseContext(), R.color.white));
        binding.radarChart.setWebColorInner(ContextCompat.getColor(getBaseContext(),
                R.color.white));
        binding.radarChart.getYAxis().setAxisMinimum(0f);
        binding.radarChart.getYAxis().setAxisMaximum(5f);
        binding.radarChart.getYAxis().setDrawLabels(false);
        binding.radarChart.getYAxis().setDrawTopYLabelEntry(true);

        binding.radarChart.getLegend().setEnabled(false);
        binding.radarChart.getDescription().setEnabled(false);

        binding.radarChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        binding.radarChart.invalidate();
    }
}
