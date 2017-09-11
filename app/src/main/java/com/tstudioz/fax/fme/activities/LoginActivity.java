package com.tstudioz.fax.fme.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.util.CircularAnim;
import com.tstudioz.fax.fme.database.Korisnik;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_button) Button but;
    @BindView(R.id.relative_login) RelativeLayout relativeLayout;
    @BindView(R.id.login_text) EditText editText;
    @BindView(R.id.login_pass) EditText pass;
    @BindView(R.id.login_pomoc) TextView loginHelp;

    Snackbar snack;
    Realm mLogRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        /**Realm inicijalizacija*/
        Realm.init(this);

       final RealmConfiguration loginRealmCf = new RealmConfiguration.Builder()
                .name("encrypted.realm")
                .schemaVersion(5)
                .deleteRealmIfMigrationNeeded()
                .build();

        SharedPreferences sharedPref = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);
        Boolean prijavljen = sharedPref.getBoolean("loged_in", false);

        if(prijavljen==true){
            Intent nwIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(nwIntent);
            finish();
        }

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkAvailable()==true) {
                    final String username = editText.getText().toString();
                    final String password = pass.getText().toString();

                    if(username.isEmpty() || password.isEmpty()){
                       showNoDataSnack();

                    }else{

                        SharedPreferences sharedPref = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor =  sharedPref.edit();
                        editor.putBoolean("loged_in", true);
                        editor.commit();

                        mLogRealm = Realm.getInstance(loginRealmCf);

                        mLogRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Korisnik user = mLogRealm.createObject(Korisnik.class);
                                user.setUsername(username);
                                user.setLozinka(password);
                            }
                        });

                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        CircularAnim.fullActivity(LoginActivity.this, view)
                                .colorOrImageRes(R.color.colorAccent)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }
                                });
                    }

                }else {
                    showNoConnSnack();

                }
            }
        });

        loginHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpMe();
            }
        });
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void helpMe() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.help_layout);
        dialog.setTitle("Pomoć");
        dialog.show();
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void showNoDataSnack(){
        snack = Snackbar.make(relativeLayout, "Niste unijeli korisničke podatke", Snackbar.LENGTH_SHORT);
        View snackBarView = snack.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_nice));
        snack.show();
    }

    public void showNoConnSnack(){
        snack = Snackbar.make(relativeLayout, "Niste povezani!", Snackbar.LENGTH_SHORT);
        View snackBarView2 = snack.getView();
        snackBarView2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_nice));
        snack.show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mLogRealm!=null) {
            mLogRealm.close();
        }
    }


}
