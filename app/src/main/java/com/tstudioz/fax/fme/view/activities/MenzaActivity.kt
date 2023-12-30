package com.tstudioz.fax.fme.view.activities

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.Application.FESBCompanion.Companion.instance
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.view.adapters.MeniesAdapter
import com.tstudioz.fax.fme.database.Meni
import com.tstudioz.fax.fme.databinding.ActivityMenzaBinding
import com.tstudioz.fax.fme.random.NetworkUtils
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

@OptIn(InternalCoroutinesApi::class)
class MenzaActivity : AppCompatActivity() {
    private var mRealm: Realm? = null
    private var nRealm: Realm? = null
    private var snack: Snackbar? = null
    private var okHttpClient: OkHttpClient? = null
    private var binding: ActivityMenzaBinding? = null
    private var menzaRealmConf: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("menza.realm")
        .schemaVersion(1)
        .deleteRealmIfMigrationNeeded()
        .build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenzaBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setTextTypeface()
        checkConditions()
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Back is pressed... Finishing the activity
                finish()
            }
        })
    }

    private fun checkConditions() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            startParsing()
        } else {
            showSnacOffline()
            binding?.menzaProgress?.visibility = View.VISIBLE
        }
    }

    private fun startParsing() {
        okHttpClient = instance?.okHttpInstance
        val request: Request = Request.Builder()
            .url("http://sc.dbtouch.com/menu/api.php/?place=fesb_vrh")
            .get()
            .build()
        val call = okHttpClient?.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                parsePage(json)
            }
        })
    }
    fun parsePage(json:String?){
        mRealm = Realm.getInstance(menzaRealmConf)
        mRealm?.executeTransaction { mRealm?.deleteAll() }
        try {
            val jsonResponse = json?.let { JSONObject(it) }
            val array = jsonResponse?.getJSONArray("values")
            for (j in 7..9) {
                try {
                    val itemsArray = array?.getJSONArray(j)
                    val meni = Meni()
                    meni.id = itemsArray?.getString(0)
                    meni.type = itemsArray?.getString(1)
                    meni.jelo1 = itemsArray?.getString(2)
                    meni.jelo2 = itemsArray?.getString(3)
                    meni.jelo3 = itemsArray?.getString(4)
                    meni.jelo4 = itemsArray?.getString(5)
                    meni.desert = itemsArray?.getString(6)
                    meni.cijena = itemsArray?.getString(7) + " eur"
                    mRealm?.executeTransaction { mRealm?.copyToRealm(meni) }
                } catch (ex: Exception) {
                    Log.d("Menza activity", ex.toString())
                }
            }
            for (k in 13..15) {
                try {
                    val itemsArray = array?.getJSONArray(k)
                    val izborniMeni = Meni()
                    izborniMeni.id = itemsArray?.getString(0)
                    izborniMeni.jelo1 = itemsArray?.getString(1)?.substring(0,
                        itemsArray.getString(1).length - 4)
                    izborniMeni.cijena = itemsArray?.getString(1)?.substring(
                        itemsArray.getString(1).length - 4,
                        itemsArray.getString(1).length) + " eur"
                    mRealm?.executeTransaction { mRealm?.copyToRealm(izborniMeni)}
                } catch (exc: Exception) {
                    Log.d("Menza activity", exc.toString())
                }
            }
            runOnUiThread {
                binding?.menzaProgress?.visibility = View.INVISIBLE
                showMenies()
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.d("MenzaActivity", it) }
        } finally {
            mRealm?.close()
        }
    }

    private fun showMenies() {
        nRealm = Realm.getInstance(menzaRealmConf)
        val results = nRealm?.where(Meni::class.java)?.findAll()
        if ((results?.isEmpty()) != null) {
            val adapter = MeniesAdapter(results)
            binding?.menzaRecyclerview?.layoutManager = LinearLayoutManager(this)
            binding?.menzaRecyclerview?.setHasFixedSize(true)
            binding?.menzaRecyclerview?.adapter = adapter
        } else {
            binding?.menzaRecyclerview?.visibility = View.INVISIBLE
            binding?.cookieHeaderRoot?.visibility = View.VISIBLE
        }
    }

    private fun setTextTypeface() {
        val typeBold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold.ttf")
        val regular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular.ttf")
        binding?.menzaTitle?.typeface = typeBold
        binding?.cookieHeaderText?.typeface = regular
    }

    private fun showSnacOffline() {
        snack = Snackbar.make(
            findViewById(R.id.menza_root), "Niste povezani",
            Snackbar.LENGTH_INDEFINITE
        )
        val vjuz = snack?.view
        vjuz?.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.red_nice))
        snack?.setAction("PONOVI") {
            snack?.dismiss()
            checkConditions()
        }
        snack?.setActionTextColor(ContextCompat.getColor(this , R.color.white))
        snack?.show()
    }

    public override fun onStop() {
        super.onStop()
        if (okHttpClient != null) okHttpClient?.dispatcher?.cancelAll()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (nRealm != null) nRealm?.close()
    }
}