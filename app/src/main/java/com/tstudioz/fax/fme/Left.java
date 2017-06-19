package com.tstudioz.fax.fme;



import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.tstudioz.fax.fme.database.EmployeeRVAdapterTable;
import com.tstudioz.fax.fme.database.Korisnik;
import com.tstudioz.fax.fme.database.Predavanja;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
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

    @InjectView(R.id.recyclerPon) RecyclerView mRecyclerPon;
    @InjectView(R.id.recyclerUto) RecyclerView recyclerUto;
    @InjectView(R.id.recyclerSri) RecyclerView recyclerSri;
    @InjectView(R.id.recyclerCet) RecyclerView recyclerCet;
    @InjectView(R.id.recyclerPet) RecyclerView mRecyclerPet;
    @InjectView(R.id.recyclerSub) RecyclerView mRecyclerSub;
    @InjectView(R.id.linear_parent) LinearLayout mLinearParent;
    @InjectView(R.id.linearSub) LinearLayout mLinearSub;
    @InjectView(R.id.odaberiDan) Button mOdaberiDan;
    @InjectView(R.id.raspored_progress) ProgressBar mRasporedProgress;


    final Realm mrealm = Realm.getDefaultInstance();

    final RealmConfiguration CredRealmCf = new RealmConfiguration.Builder()
            .name("encrypted.realm")
            .schemaVersion(5)
            .deleteRealmIfMigrationNeeded()
            .build();

    final RealmConfiguration tempRealm = new RealmConfiguration.Builder()
            .name("temporary.realm")
            .schemaVersion(12)
            .deleteRealmIfMigrationNeeded()
            .build();

    Snackbar snack;
    EmployeeRVAdapterTable adapterPonTemp, adapterUtoTemp, adapterSriTemp, adapterCetTemp, adapterPetTemp, adapterSubTemp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        //set the layout you want to display in First Fragment
        View view = inflater.inflate(R.layout.left_tab,
                container, false);


        ButterKnife.inject(this, view);

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
                .setThemeDark(true);

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

            Realm rlm = Realm.getInstance(CredRealmCf);
            Korisnik kor = rlm.where(Korisnik.class).findFirst();

            OkHttpClient client = new OkHttpClient();

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


                                Log.d("moj odgovor ", "ODGOVOR JE USPJESAN");

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
                                        }else {
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

        Realm.init(getActivity().getBaseContext());
        Realm mrealm = Realm.getDefaultInstance();

        mrealm.beginTransaction();
        RealmResults<Predavanja> rezulatiPon = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Ponedjeljak", Case.INSENSITIVE).findAll();
        mrealm.commitTransaction();

        EmployeeRVAdapterTable adapter = new EmployeeRVAdapterTable(rezulatiPon);
        mRecyclerPon.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPon.setHasFixedSize(true);
        mRecyclerPon.setAdapter(adapter);

    }

    public void showPonTemp(){

        Realm trealm = Realm.getInstance(tempRealm);

        trealm.beginTransaction();
        RealmResults<Predavanja> rezulatiPon1 = trealm.where(Predavanja.class).contains("detaljnoVrijeme", "Ponedjeljak", Case.INSENSITIVE).findAll();
        trealm.commitTransaction();

        adapterPonTemp = new EmployeeRVAdapterTable(rezulatiPon1);
        mRecyclerPon.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPon.setHasFixedSize(true);
        mRecyclerPon.setAdapter(adapterPonTemp);

    }

    public void showUto(){

        Realm.init(getActivity().getBaseContext());
        Realm mrealm = Realm.getDefaultInstance();

        mrealm.beginTransaction();
        RealmResults<Predavanja> rezulatiUto = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Utorak", Case.INSENSITIVE).findAll();
        mrealm.commitTransaction();

        EmployeeRVAdapterTable adapter2 = new EmployeeRVAdapterTable(rezulatiUto);
        recyclerUto.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerUto.setHasFixedSize(true);
        recyclerUto.setAdapter(adapter2);

    }

    public void showUtoTemp() {


        Realm trealm = Realm.getInstance(tempRealm);

        trealm.beginTransaction();
        RealmResults<Predavanja> rezulatiUto1 = trealm.where(Predavanja.class).contains("detaljnoVrijeme", "Utorak", Case.INSENSITIVE).findAll();
        trealm.commitTransaction();

        adapterUtoTemp = new EmployeeRVAdapterTable(rezulatiUto1);
        recyclerUto.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerUto.setHasFixedSize(true);
        recyclerUto.setAdapter(adapterUtoTemp);

    }

    public void showSri(){

        Realm.init(getActivity().getBaseContext());
        Realm mrealm = Realm.getDefaultInstance();

        mrealm.beginTransaction();
        RealmResults<Predavanja> rezulatiSri = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Srijeda", Case.INSENSITIVE).findAll();
        mrealm.commitTransaction();

        EmployeeRVAdapterTable adapter3 = new EmployeeRVAdapterTable(rezulatiSri);
        recyclerSri.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerSri.setHasFixedSize(true);
        recyclerSri.setAdapter(adapter3);

    }

    public void showSriTemp(){


        Realm trealm = Realm.getInstance(tempRealm);

        trealm.beginTransaction();
        RealmResults<Predavanja> rezulatiSri1 = trealm.where(Predavanja.class).contains("detaljnoVrijeme", "Srijeda", Case.INSENSITIVE).findAll();
        trealm.commitTransaction();

        adapterSriTemp = new EmployeeRVAdapterTable(rezulatiSri1);
        recyclerSri.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerSri.setHasFixedSize(true);
        recyclerSri.setAdapter(adapterSriTemp);

    }

    public void showCet(){

        Realm.init(getActivity().getBaseContext());
        Realm mrealm = Realm.getDefaultInstance();

        mrealm.beginTransaction();
        RealmResults<Predavanja> rezulatiCet = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", "četvrtak", Case.INSENSITIVE).findAll();
        mrealm.commitTransaction();

        EmployeeRVAdapterTable adapter4 = new EmployeeRVAdapterTable(rezulatiCet);
        recyclerCet.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCet.setHasFixedSize(true);
        recyclerCet.setAdapter(adapter4);

    }

    public void showCetTemp( ){

        Realm trealm = Realm.getInstance(tempRealm);

        trealm.beginTransaction();
        RealmResults<Predavanja> rezulatiCet1 = trealm.where(Predavanja.class).contains("detaljnoVrijeme", "četvrtak", Case.INSENSITIVE).findAll();
        trealm.commitTransaction();

        adapterCetTemp = new EmployeeRVAdapterTable(rezulatiCet1);
        recyclerCet.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCet.setHasFixedSize(true);
        recyclerCet.setAdapter(adapterCetTemp);

    }

    public void showPet(){

        Realm.init(getActivity().getBaseContext());
        Realm mrealm = Realm.getDefaultInstance();

        mrealm.beginTransaction();
        RealmResults<Predavanja> rezulatiPet = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Petak", Case.INSENSITIVE).findAll();
        mrealm.commitTransaction();

        EmployeeRVAdapterTable adapter5 = new EmployeeRVAdapterTable(rezulatiPet);
        mRecyclerPet.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPet.setHasFixedSize(true);
        mRecyclerPet.setAdapter(adapter5);

    }

    public void showPetTemp(){

        Realm trealm = Realm.getInstance(tempRealm);

        trealm.beginTransaction();
        RealmResults<Predavanja> rezulatiPet1 = trealm.where(Predavanja.class).contains("detaljnoVrijeme", "Petak", Case.INSENSITIVE).findAll();
        trealm.commitTransaction();

        adapterPetTemp = new EmployeeRVAdapterTable(rezulatiPet1);
        mRecyclerPet.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerPet.setHasFixedSize(true);
        mRecyclerPet.setAdapter(adapterPetTemp);

    }

    public void showSub() {

        Realm.init(getActivity().getBaseContext());
        Realm mrealm = Realm.getDefaultInstance();

        mrealm.beginTransaction();
        RealmResults<Predavanja> rezulatiSub = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", "Subota", Case.INSENSITIVE).findAll();
        mrealm.commitTransaction();

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

        Realm trealm = Realm.getInstance(tempRealm);

        trealm.beginTransaction();
        RealmResults<Predavanja> rezulatiSub1 = trealm.where(Predavanja.class).contains("detaljnoVrijeme", "Subota", Case.INSENSITIVE).findAll();
        trealm.commitTransaction();

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
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Prikazuje se raspored ovog tjedna. Za odabir drugog tjedna potrebna je internetska veza.", Snackbar.LENGTH_INDEFINITE);
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
    }
}
