package com.tstudioz.fax.fme.activities

import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.Application.FESBCompanion.Companion.instance
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.adapters.MeniesAdapter
import com.tstudioz.fax.fme.database.Meni
import com.tstudioz.fax.fme.databinding.ActivityMenzaBinding
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MenzaActivity : AppCompatActivity() {
    var menzaRealmConf = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("menza.realm")
        .schemaVersion(1)
        .deleteRealmIfMigrationNeeded()
        .build()
    private var mRealm: Realm? = null
    private var nRealm: Realm? = null
    private var snack: Snackbar? = null
    private var okHttpClient: OkHttpClient? = null
    private var binding: ActivityMenzaBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenzaBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setTextTypeface()
        checkConditions()
        //  loadAds();
    }

    fun checkConditions() {
        if (isNetworkAvailable) {
            startParsing()
        } else {
            showSnacOffline()
            binding!!.menzaProgress.visibility = View.VISIBLE
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    fun startParsing() {
        okHttpClient = instance!!.okHttpInstance
        val request: Request = Request.Builder()
            .url("http://sc.dbtouch.com/menu/api.php/?place=fesb_vrh")
            .get()
            .build()
        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                mRealm = Realm.getInstance(menzaRealmConf)
                mRealm?.executeTransaction(Realm.Transaction { mRealm?.deleteAll() })
                val json = response.body!!.string()
                try {
                    val jsonResponse = JSONObject(json)
                    val array = jsonResponse.getJSONArray("values")
                    for (j in 7..9) {
                        try {
                            val itemsArray = array.getJSONArray(j)
                            val meni = Meni()
                            meni.id = itemsArray.getString(0)
                            meni.type = itemsArray.getString(1)
                            meni.jelo1 = itemsArray.getString(2)
                            meni.jelo2 = itemsArray.getString(3)
                            meni.jelo3 = itemsArray.getString(4)
                            meni.jelo4 = itemsArray.getString(5)
                            meni.desert = itemsArray.getString(6)
                            meni.cijena = itemsArray.getString(7) + " eur"
                            mRealm?.executeTransaction(Realm.Transaction { mRealm?.copyToRealm(meni) })
                        } catch (ex: Exception) {
                            Log.d("Menza activity", ex.toString())
                        }
                    }
                    for (k in 13..15) {
                        try {
                            val itemsArray = array.getJSONArray(k)
                            val izborniMeni = Meni()
                            izborniMeni.id = itemsArray.getString(0)
                            izborniMeni.jelo1 = itemsArray.getString(1).substring(0,
                                itemsArray.getString(1).length - 4                            )
                            izborniMeni.cijena = itemsArray.getString(1).substring(
                                itemsArray.getString(1).length - 4,
                                itemsArray.getString(1).length
                            ) + " eur"
                            mRealm?.executeTransaction(Realm.Transaction {
                                mRealm?.copyToRealm(
                                    izborniMeni
                                )
                            })
                        } catch (exc: Exception) {
                            Log.d("Menza activity", exc.toString())
                        }
                    }
                    runOnUiThread {
                        binding!!.menzaProgress.visibility = View.INVISIBLE
                        showMenies()
                    }
                } catch (ex: Exception) {
                    Log.d("MenzaActivity", ex.message!!)
                } finally {
                    mRealm?.close()
                }
            }
        })
    }

    fun showMenies() {
        nRealm = Realm.getInstance(menzaRealmConf)
        val results = nRealm?.where(Meni::class.java)?.findAll()
        if (!results?.isEmpty()!!) {
            val adapter = MeniesAdapter(results)
            binding!!.menzaRecyclerview.layoutManager = LinearLayoutManager(this)
            binding!!.menzaRecyclerview.setHasFixedSize(true)
            binding!!.menzaRecyclerview.adapter = adapter
        } else {
            binding!!.menzaRecyclerview.visibility = View.INVISIBLE
            binding!!.cookieHeaderRoot.visibility = View.VISIBLE
        }
    }

    fun setTextTypeface() {
        val typeBold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold.ttf")
        val regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular.ttf")
        binding!!.menzaTitle.typeface = typeBold
        binding!!.cookieHeaderText.typeface = regular
    }

    override fun onBackPressed() {
        //  if (mInterstitialAd.isLoaded()) {
        //      mInterstitialAd.show();
        //  } else {
        finish()
        //  }
    }

    private val isNetworkAvailable: Boolean
        private get() {
            val manager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.activeNetworkInfo
            var isAvailable = false
            if (networkInfo != null && networkInfo.isConnected) {
                isAvailable = true
            }
            return isAvailable
        }

    fun showSnacOffline() {
        snack = Snackbar.make(
            findViewById(R.id.menza_root), "Niste povezani",
            Snackbar.LENGTH_INDEFINITE
        )
        val vjuz = snack!!.view
        vjuz.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.red_nice))
        snack!!.setAction("PONOVI") {
            snack!!.dismiss()
            checkConditions()
        }
        snack!!.setActionTextColor(resources.getColor(R.color.white))
        snack!!.show()
    }

    public override fun onStop() {
        super.onStop()
        if (okHttpClient != null) okHttpClient!!.dispatcher.cancelAll()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (nRealm != null) nRealm!!.close()
    }
}