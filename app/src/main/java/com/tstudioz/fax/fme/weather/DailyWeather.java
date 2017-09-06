package com.tstudioz.fax.fme.weather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DailyWeather extends Fragment {

    @BindView(R.id.poweredBy) ImageView mPoweredBy;
    @BindView(R.id.temperaturaDaily) TextView mtemperaturaDaily;
    @BindView(R.id.vlaznost_vrijednost1) TextView mvlaznost_vrijednost1;
    @BindView(R.id.oborine_vrijednost1) TextView moborine_vrijednost1;
    @BindView(R.id.trenutni_vjetar1) TextView mtrenutni_vjetar1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //set the layout you want to display in First Fragment
        View view = inflater.inflate(R.layout.daily,
                container, false);

        ButterKnife.bind(this, view);


        Bundle bundle = this.getArguments();
            String temperatura = bundle.getString("temperatura");
            String vlazno = bundle.getString("vlaznost");
            String vjetar = bundle.getString("vjetar");
            String oborina = bundle.getString("oborina");


            mtemperaturaDaily.setText(temperatura);
            mtrenutni_vjetar1.setText(vjetar);
            mvlaznost_vrijednost1.setText(vlazno);
            moborine_vrijednost1.setText(oborina);


        mPoweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://darksky.net/poweredby/");
                Intent in = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(in);
            }
        });

        return view;

    }
}