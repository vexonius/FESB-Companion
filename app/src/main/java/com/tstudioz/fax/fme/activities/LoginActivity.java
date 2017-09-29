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
import android.util.Log;
import android.view.View;
import android.view.Window;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.util.CircularAnim;
import com.tstudioz.fax.fme.database.Korisnik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_button) Button but;
    @BindView(R.id.relative_login) RelativeLayout relativeLayout;
    @BindView(R.id.login_text) EditText editText;
    @BindView(R.id.login_pass) EditText pass;
    @BindView(R.id.login_pomoc) TextView loginHelp;
    @BindView(R.id.progress_login) ProgressBar bar;

    Snackbar snack;
    Realm mLogRealm;

    final RealmConfiguration loginRealmCf = new RealmConfiguration.Builder()
            .name("encrypted.realm")
            .schemaVersion(5)
            .deleteRealmIfMigrationNeeded()
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);
        Boolean prvi_put = sharedPreferences.getBoolean("first_open", true);

        if(prvi_put==true) {
            startActivity(new Intent(LoginActivity.this, Welcome.class));
        }

        Boolean prijavljen = sharedPreferences.getBoolean("loged_in", false);

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
                        bar.bringToFront();
                        bar.setVisibility(View.VISIBLE);
                        but.setText(" ");
                        validateUser(username, password, view);
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

    public void showErrorSnack(){
        snack = Snackbar.make(relativeLayout, "Uneseni podatci su pogrešni!", Snackbar.LENGTH_SHORT);
        View snackBarView3 = snack.getView();
        snackBarView3.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_nice));
        snack.show();
    }

     public void validateUser(final String user, final String pass, final View mView){

         final CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));

         final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
         logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .cookieJar(cookieJar)
                .addInterceptor(logging)
                .build();

         final RequestBody formData = new FormBody.Builder()
                 .add("Username", user)
                 .add("Password", pass)
                 .add("IsRememberMeChecked", "true")
                 .build();

         final Request rq = new Request.Builder()
                 .url("https://korisnik.fesb.unist.hr/prijava")
                 .post(formData)
               //  .get()
                 .build();

         Call call0 = okHttpClient.newCall(rq);
         call0.enqueue(new Callback() {
             @Override
             public void onFailure(Call call, IOException e) {
                 Log.d("pogreska", "failure");
             }

             @Override
             public void onResponse(Call call, Response response) throws IOException {

                 Log.e("koji kurac", response.request().url().toString());

         Log.e("konacni url", response.toString());

                if(response.request().url().toString().equals("https://korisnik.fesb.unist.hr/")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            register(user, pass, mView);
                        }
                    });

                }
                else {
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         showErrorSnack();
                         bar.setVisibility(View.INVISIBLE);
                         but.setText("PRIJAVA");
                     }
                 });
                }


            }
        });

    }

    public void register(final String username, final String password, View nView){

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

        if (nView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(nView.getWindowToken(), 0);
        }

        CircularAnim.fullActivity(LoginActivity.this, nView)
                .colorOrImageRes(R.color.colorAccent)
                .go(new CircularAnim.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mLogRealm!=null) {
            mLogRealm.close();
        }
    }


}
