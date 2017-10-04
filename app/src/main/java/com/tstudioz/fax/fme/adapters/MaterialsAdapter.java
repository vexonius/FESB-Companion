package com.tstudioz.fax.fme.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.Materijal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;


public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialViewHolder> implements RealmChangeListener {
    private RealmList<Materijal> materials;
    Typeface regulartf;
    private String doc_name;
    private String doc_ext;
    private String url;


    public MaterialsAdapter(RealmList<Materijal> material) {
        this.materials = material;
        materials.addChangeListener(this);
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.materijal_item, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position) {

        Materijal materijal = materials.get(position);
        holder.name.setText(materijal.getImeMtarijala());
        holder.name.setTypeface(regulartf);

        holder.icon.setImageResource(materijal.getIcon());

        if(materijal.getDownloadable()==1){
            holder.download.setImageResource(R.drawable.download);
        }else{
            holder.download.setImageResource(R.drawable.open_in_browser);
        }

    }


    @Override
    public int getItemCount() {
        return materials.size();
    }

    public class MaterialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView icon, download;
        CircularProgressBar progressBar;


        public MaterialViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.mat_text);
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
            name.setTypeface(regulartf);

            icon = (ImageView)itemView.findViewById(R.id.mat_src);
            download = (ImageView)itemView.findViewById(R.id.mat_dl);
            progressBar = (CircularProgressBar )itemView.findViewById(R.id.mat_progress);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {


            final Context context = view.getContext();

            doc_name = materials.get(getAdapterPosition()).getImeMtarijala();
            doc_ext = materials.get(getAdapterPosition()).getVrsta();
            String chromeurl = materials.get(getAdapterPosition()).getUrl();

            if (materials.get(getAdapterPosition()).getDownloadable() == 0) {

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.setToolbarColor(context.getResources().getColor(R.color.colorPrimaryDark)).build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse("https://korisnik.fesb.unist.hr/prijava?returnUrl=" + chromeurl));

            } else {


                CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(view.getContext()));

                download.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .cookieJar(cookieJar)
                        .build();

                final Request rq = new Request.Builder()
                        .url(materials.get(getAdapterPosition()).getUrl())
                        .get()
                        .build();


                Call call = okHttpClient.newCall(rq);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("material_adapter", "failure");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        Document doc = Jsoup.parse(response.body().string());
                        //   Log.d("material_ad_body", doc.body().text());

                        switch (doc_ext) {

                            case "pdf":

                                Element element = doc.select("div.region-content").first();
                                final Element links = element.select("a[href]").first();
                                if(links !=null){
                                    url = links.attr("href");
                                }else {
                                    url = rq.url().toString();
                                }


                                break;

                            case "docx":
                            case "txt":
                            case "pptx":
                            case "xlsx":
                            case "zip":
                                url = rq.url().toString();
                                break;

                            case "jpg":
                                Element elementx = doc.select("div.region-content").first();

                                final Element src = elementx.select("img[src]").first();
                           //      Log.d("link za doc", src.attr("src"));
                                url = src.attr("src");

                                break;

                        }

                        Request.Builder builder = new Request.Builder()
                                .url(url)
                                .get();

                        Call callDown = okHttpClient.newCall(builder.build());
                        callDown.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                      //          Log.e("TAG", "=============onFailure===============");
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                       //         Log.e("TAG", "=============onResponse===============");
                       //         Log.e("TAG", "request headers:" + response.request().headers());
                       //         Log.e("TAG", "response headers:" + response.headers());

                                ResponseBody body = response.body();
                                //wrap the original response body with progress
                                ResponseBody responseBody = ProgressHelper.withProgress(body, new ProgressUIListener() {

                                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                                    @Override
                                    public void onUIProgressStart(long totalBytes) {
                                        super.onUIProgressStart(totalBytes);
                       //                 Log.e("TAG", "onUIProgressStart:" + totalBytes);
                                    }

                                    @Override
                                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                           //             Log.e("TAG", "=============start===============");
                           //             Log.e("TAG", "numBytes:" + numBytes);
                           //             Log.e("TAG", "totalBytes:" + totalBytes);
                           //             Log.e("TAG", "percent:" + percent);
                           //             Log.e("TAG", "speed:" + speed);
                           //             Log.e("TAG", "============= end ===============");

                                        progressBar.setProgressWithAnimation((int) (100 * percent), 200);
                                    }

                                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                                    @Override
                                    public void onUIProgressFinish() {
                                        super.onUIProgressFinish();
                            //            Log.e("TAG", "onUIProgressFinish:");
                                        progressBar.setVisibility(View.INVISIBLE);
                                        download.setVisibility(View.VISIBLE);
                                        download.setImageResource(R.drawable.checked);
                                    }

                                });
                                //read the body to file
                                BufferedSource source = responseBody.source();
                                File outFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + doc_name + "." + doc_ext);
                                outFile.delete();
                                outFile.getParentFile().mkdirs();
                                outFile.createNewFile();
                                BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                                source.readAll(sink);
                                sink.flush();
                                source.close();

                                showDocSnack(view, doc_name, doc_ext, outFile);

                            }
                        });


                    }

                });

            }

        }
    }

        @Override
        public void onChange(Object element) {
            notifyDataSetChanged();
        }

        private void showDocSnack(View mView, String name, final String extension, final File file){
            Snackbar snackbar = Snackbar.make(mView, "Dokument " + name + "." + extension + " je preuzet.",Snackbar.LENGTH_LONG);
            snackbar.setAction("OTVORI", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                    view.getContext().startActivity(intent);
                }
            });
            snackbar.show();
        }

}


