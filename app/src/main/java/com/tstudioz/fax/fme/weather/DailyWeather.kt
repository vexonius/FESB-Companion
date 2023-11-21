package com.tstudioz.fax.fme.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tstudioz.fax.fme.R

class DailyWeather : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //set the layout you want to display in First Fragment
        val view = inflater.inflate(
            R.layout.daily,
            container, false
        )
        val bundle = this.requireArguments()
        val temperatura = bundle.getString("temperatura")
        val vlazno = bundle.getString("vlaznost")
        val vjetar = bundle.getString("vjetar")
        val oborina = bundle.getString("oborina")


        /*mtemperaturaDaily.setText(temperatura);
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
           });*/
        return view
    }
}