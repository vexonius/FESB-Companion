package com.tstudioz.fax.fme.fragments;



import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.NativeExpressAdView;
import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.adapters.EmployeeRVAdapterTable;
import com.tstudioz.fax.fme.database.Korisnik;
import com.tstudioz.fax.fme.database.Predavanja;
import com.tstudioz.fax.fme.migrations.CredMigration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class Left extends Fragment implements DatePickerDialog.OnDateSetListener{

    @BindView(R.id.recyclerPon) RecyclerView mRecyclerPon;
    @BindView(R.id.recyclerUto) RecyclerView recyclerUto;
    @BindView(R.id.recyclerSri) RecyclerView recyclerSri;
    @BindView(R.id.recyclerCet) RecyclerView recyclerCet;
    @BindView(R.id.recyclerPet) RecyclerView mRecyclerPet;
    @BindView(R.id.recyclerSub) RecyclerView mRecyclerSub;
    @BindView(R.id.linear_parent) LinearLayout mLinearParent;
    @BindView(R.id.linearSub) LinearLayout mLinearSub;
    @BindView(R.id.odaberiDan) Button mOdaberiDan;
    @BindView(R.id.raspored_progress) ProgressBar mRasporedProgress;


     RealmConfiguration CredRealmCf = new RealmConfiguration.Builder()
            .name("encrypted.realm")
            .schemaVersion(6)
             .migration(new CredMigration())
            .build();

     RealmConfiguration tempRealm = new RealmConfiguration.Builder()
            .name("temporary.realm")
            .schemaVersion(12)
            .deleteRealmIfMigrationNeeded()
            .build();

    Realm rlm, prealm, ptrealm, urealm, utrealm, srealm, strealm, crealm, ctrealm, petrealm, pettrealm, subrealm,subtrealm;

    Snackbar snack;
    EmployeeRVAdapterTable adapterPonTemp, adapterUtoTemp, adapterSriTemp, adapterCetTemp, adapterPetTemp, adapterSubTemp;
    OkHttpClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        //set the layout you want to display in First Fragment
        View view = inflater.inflate(R.layout.left_tab,
                container, false);


        ButterKnife.bind(this, view);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPon();
                showUto();
                showSri();
                showCet();
                showPet();
                showSub();
            }
        });



        NativeExpressAdView adView = (NativeExpressAdView)view.findViewById(R.id.adViewleft);

       // if(!isNetworkAvailable()){
            adView.setVisibility(View.GONE);
     /*   }else{

            adView.setVisibility(View.VISIBLE);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice("0F0806B7833336104F00247BA81C120D")
                    .build();
            adView.loadAd(request);
        }

*/
        Calendar min = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        max.add(Calendar.YEAR, 10);
        min.add(Calendar.YEAR, -1);
        final BottomSheetPickerDialog.Builder builder = new DatePickerDialog.Builder(
                Left.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        DatePickerDialog.Builder dateDialogBuilder = (DatePickerDialog.Builder) builder;
        dateDialogBuilder.setMaxDate(max)
                .setMinDate(min)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setThemeDark(true)
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark))
                .setHeaderColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

     checkNetwork();

     mOdaberiDan.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

             builder.build().show(getFragmentManager(), TAG);
         }
     });


        return view;

    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresMe).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        Calendar kal = Calendar.getInstance();
        kal.set(Calendar.YEAR, year);
        kal.set(Calendar.MONTH, monthOfYear);
        kal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        DateFormat sday = new SimpleDateFormat("dd");
        DateFormat smonth = new SimpleDateFormat("MM");
        DateFormat syear = new SimpleDateFormat("yyyy");

        kal.get(Calendar.DAY_OF_WEEK);
        kal.add(Calendar.DAY_OF_MONTH, -(kal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY));

        String mMonth = smonth.format(kal.getTime());
        String mDay = sday.format(kal.getTime());
        String mYear = syear.format(kal.getTime());

        kal.add(Calendar.DAY_OF_MONTH, 5);

        String sMonth = smonth.format(kal.getTime());
        String sDay = sday.format(kal.getTime());
        String sYear = syear.format(kal.getTime());

        mojRaspored(mMonth, mDay, mYear, sMonth, sDay, sYear);
        mOdaberiDan.setText("Raspored za " + mDay + "." + mMonth + " - " + sDay + "." + sMonth);

    }

    public void mojRaspored(String mMonth, String mDay, String mYear, String sMonth, String sDay, String sYear) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLinearParent.setVisibility(View.INVISIBLE);
                    mRasporedProgress.setVisibility(View.VISIBLE);
                    showPonTemp();
                    showUtoTemp();
                    showSriTemp();
                    showCetTemp();
                    showPetTemp();
                    showSubTemp();

                }
            });

            rlm = Realm.getInstance(CredRealmCf);
            Korisnik kor = rlm.where(Korisnik.class).findFirst();

            client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url("https://raspored.fesb.unist.hr/part/raspored/kalendar?DataType=User&DataId=" + kor.getUsername().toString() + "&MinDate=" + mMonth + "%2F" +  mDay + "%2F" + mYear + "%2022%3A44%3A48&MaxDate=" + sMonth + "%2F" + sDay + "%2F" + sYear + "%2022%3A44%3A48")
                    .get()
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Exception caught", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String kod = String.valueOf(response.code());

                        if (response.code() == 500) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showSnackError();
                                }
                            });

                        } else {
                            Document doc = Jsoup.parse(response.body().string());

                            Realm trealm = Realm.getInstance(tempRealm);
                            trealm.beginTransaction();
                            RealmResults<Predavanja> svaPredavanja = trealm.where(Predavanja.class).findAll();
                            svaPredavanja.deleteAllFromRealm();
                            trealm.commitTransaction();

                            if (response.isSuccessful()) {
                                Elements elements = doc.select("div.event");
                                try {

                                    trealm.beginTransaction();

                                    for (final Element e : elements) {

                                        Predavanja predavanja = trealm.createObject(Predavanja.class, UUID.randomUUID().toString());

                                        if (e.hasAttr("data-id")) {
                                            String attr = e.attr("data-id");
                                            predavanja.setObjectId(Integer.parseInt(attr));
                                        }

                                        predavanja.setPredavanjeIme(e.select("span.groupCategory").text());
                                        predavanja.setPredmetPredavanja((e.select("span.name.normal").text()));
                                        predavanja.setRasponVremena(e.select("div.timespan").text());
                                        predavanja.setGrupa(e.select("span.group.normal").text());
                                        predavanja.setGrupaShort(e.select("span.group.short").text());
                                        predavanja.setDvorana(e.select("span.resource").text());
                                        predavanja.setDetaljnoVrijeme(e.select("div.detailItem.datetime").text());
                                        predavanja.setProfesor(e.select("div.detailItem.user").text());

                                    }
                                    trealm.commitTransaction();

                                } finally {
                                    trealm.close();
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateTemporaryWeek();

                                        if(adapterSubTemp.getItemCount() > 0) {
                                            mLinearParent.setWeightSum(6);
                                            mLinearSub.setVisibility(View.VISIBLE);
                                            mLinearParent.invalidate();
                                        } else {
                                            mLinearSub.setVisibility(View.INVISIBLE);
                                            mLinearParent.setWeightSum(5);
                                            mLinearParent.invalidate();
                                        }

                                        mRasporedProgress.setVisibility(View.INVISIBLE);
                                        mLinearParent.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                        } catch(IOException e){
                            Log.e(TAG, "Exception caught: ", e);
                        }
                    }

            });
        }

    public void showPon(){
        prealm = Realm.getDefaultInstance();
        RealmResults<Predavanja> rezulatiPon = prealm.where(Predavanja.class).contains("detaljnoVrijeme", "Ponedjeljak", Case.INSENSITIVE).findAll();

        EmployeeRVAdapterTable adapter = new EmployeeRVAdapterTable(rezulatiPon);
        mRecyclerPon.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPon.setHasFixedSize(true);
        mRecyclerPon.setAdapter(adapter);

    }

    public void showPonTemp(){
        ptrealm = Realm.getInstance(tempRealm);
        RealmResults<Predavanja> rezulatiPon1 = ptrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Ponedjeljak", Case.INSENSITIVE).findAll();


        adapterPonTemp = new EmployeeRVAdapterTable(rezulatiPon1);
        mRecyclerPon.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPon.setHasFixedSize(true);
        mRecyclerPon.setAdapter(adapterPonTemp);

    }

    public void showUto(){
        urealm = Realm.getDefaultInstance();
        RealmResults<Predavanja> rezulatiUto = urealm.where(Predavanja.class).contains("detaljnoVrijeme", "Utorak", Case.INSENSITIVE).findAll();

        EmployeeRVAdapterTable adapter2 = new EmployeeRVAdapterTable(rezulatiUto);
        recyclerUto.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerUto.setHasFixedSize(true);
        recyclerUto.setAdapter(adapter2);

    }

    public void showUtoTemp() {
        utrealm = Realm.getInstance(tempRealm);
        RealmResults<Predavanja> rezulatiUto1 = utrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Utorak", Case.INSENSITIVE).findAll();

        adapterUtoTemp = new EmployeeRVAdapterTable(rezulatiUto1);
        recyclerUto.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerUto.setHasFixedSize(true);
        recyclerUto.setAdapter(adapterUtoTemp);

    }

    public void showSri(){
        srealm = Realm.getDefaultInstance();
        RealmResults<Predavanja> rezulatiSri = srealm.where(Predavanja.class).contains("detaljnoVrijeme", "Srijeda", Case.INSENSITIVE).findAll();

        EmployeeRVAdapterTable adapter3 = new EmployeeRVAdapterTable(rezulatiSri);
        recyclerSri.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerSri.setHasFixedSize(true);
        recyclerSri.setAdapter(adapter3);

    }

    public void showSriTemp(){
        strealm = Realm.getInstance(tempRealm);
        RealmResults<Predavanja> rezulatiSri1 = strealm.where(Predavanja.class).contains("detaljnoVrijeme", "Srijeda", Case.INSENSITIVE).findAll();

        adapterSriTemp = new EmployeeRVAdapterTable(rezulatiSri1);
        recyclerSri.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerSri.setHasFixedSize(true);
        recyclerSri.setAdapter(adapterSriTemp);

    }

    public void showCet(){
        crealm = Realm.getDefaultInstance();
        RealmResults<Predavanja> rezulatiCet = crealm.where(Predavanja.class).contains("detaljnoVrijeme", "četvrtak", Case.INSENSITIVE).findAll();

        EmployeeRVAdapterTable adapter4 = new EmployeeRVAdapterTable(rezulatiCet);
        recyclerCet.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCet.setHasFixedSize(true);
        recyclerCet.setAdapter(adapter4);

    }

    public void showCetTemp( ){
        ctrealm = Realm.getInstance(tempRealm);
        RealmResults<Predavanja> rezulatiCet1 = ctrealm.where(Predavanja.class).contains("detaljnoVrijeme", "četvrtak", Case.INSENSITIVE).findAll();

        adapterCetTemp = new EmployeeRVAdapterTable(rezulatiCet1);
        recyclerCet.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCet.setHasFixedSize(true);
        recyclerCet.setAdapter(adapterCetTemp);

    }

    public void showPet(){
        petrealm = Realm.getDefaultInstance();
        RealmResults<Predavanja> rezulatiPet = petrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Petak", Case.INSENSITIVE).findAll();

        EmployeeRVAdapterTable adapter5 = new EmployeeRVAdapterTable(rezulatiPet);
        mRecyclerPet.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPet.setHasFixedSize(true);
        mRecyclerPet.setAdapter(adapter5);

    }

    public void showPetTemp(){
        pettrealm = Realm.getInstance(tempRealm);
        RealmResults<Predavanja> rezulatiPet1 = pettrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Petak", Case.INSENSITIVE).findAll();

        adapterPetTemp = new EmployeeRVAdapterTable(rezulatiPet1);
        mRecyclerPet.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPet.setHasFixedSize(true);
        mRecyclerPet.setAdapter(adapterPetTemp);

    }

    public void showSub() {
        subrealm = Realm.getDefaultInstance();
        RealmResults<Predavanja> rezulatiSub = subrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Subota", Case.INSENSITIVE).findAll();

        if (rezulatiSub.isEmpty()) {
            mLinearSub.setVisibility(View.GONE);
            mLinearParent.setWeightSum(5);

        } else {
            mLinearSub.setVisibility(View.VISIBLE);
            mLinearParent.setWeightSum(6);
            EmployeeRVAdapterTable adapter6 = new EmployeeRVAdapterTable(rezulatiSub);
            mRecyclerSub.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerSub.setHasFixedSize(true);
            mRecyclerSub.setAdapter(adapter6);
        }
    }

    public void showSubTemp(){

        subtrealm = Realm.getInstance(tempRealm);

        RealmResults<Predavanja> rezulatiSub1 = subtrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Subota", Case.INSENSITIVE).findAll();

         adapterSubTemp = new EmployeeRVAdapterTable(rezulatiSub1);
         mRecyclerSub.setLayoutManager(new LinearLayoutManager(getActivity()));
         mRecyclerSub.setAdapter(adapterSubTemp);

    }

    public void updateTemporaryWeek(){
        adapterPonTemp.notifyDataSetChanged();
        adapterUtoTemp.notifyDataSetChanged();
        adapterSriTemp.notifyDataSetChanged();
        adapterCetTemp.notifyDataSetChanged();
        adapterPetTemp.notifyDataSetChanged();
        adapterSubTemp.notifyDataSetChanged();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    public void checkNetwork(){
        if(isNetworkAvailable()){
            mOdaberiDan.setVisibility(View.VISIBLE);
        }else{
            mOdaberiDan.setVisibility(View.INVISIBLE);
            showSnacOffline();
        }
    }

    public void showSnacOffline(){
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Niste povezani.\nPrikazuje se raspored ovog tjedna.", Snackbar.LENGTH_INDEFINITE);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.setAction("OSVJEŽI", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snack.dismiss();
                checkNetwork();
            }
        });
        snack.setActionTextColor(getResources().getColor(R.color.white));
        snack.show();
    }

    public void showSnackError(){
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Došlo je do pogreške pri dohvaćanju rasporeda", Snackbar.LENGTH_SHORT);
        View vjuzs = snack.getView();
        vjuzs.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.show();
    }

    public void onStop(){
        super.onStop();
        if(snack!=null){
            snack.dismiss();
        }

        if (client!=null){
            client.dispatcher().cancelAll();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(rlm!=null) {
            rlm.close();
        }
        if(prealm!=null) {
            prealm.close();
        }
        if (ptrealm!=null) {
            ptrealm.close();
        }
        if(urealm!=null) {
            urealm.close();
        }
        if(utrealm!=null) {
            utrealm.close();
        }
        if (srealm!=null){
            srealm.close();
        }
        if (strealm!=null){
            strealm.close();
        }
        if(crealm!=null) {
            crealm.close();
        }
        if(ctrealm!=null){
            ctrealm.close();
        }
        if(petrealm!=null){
            petrealm.close();
        }
        if(pettrealm!=null){
            pettrealm.close();
        }
        if(subrealm!=null) {
            subrealm.close();
        }
        if(subtrealm!=null){
        subtrealm.close();
        }
    }
}
