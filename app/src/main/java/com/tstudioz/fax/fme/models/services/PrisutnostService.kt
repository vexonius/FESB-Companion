package com.tstudioz.fax.fme.models.services

import android.util.Log
import android.view.View
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.tstudioz.fax.fme.database.Dolazak
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.interfaces.PrisutnostInterface
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.Call
import okhttp3.Callback
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.FormBody.Builder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.koin.java.KoinJavaComponent
import java.io.IOException
import java.util.StringTokenizer
import java.util.UUID

class PrisutnostService : PrisutnostInterface{

    private var cRealm: Realm? = null
    private val client: OkHttpClient by KoinJavaComponent.inject(OkHttpClient::class.java)
    val realmConfig: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("prisutnost.realm")
        .schemaVersion(10)
        .deleteRealmIfMigrationNeeded()
        .build()

    override suspend fun fetchPrisutnost(): Result.PrisutnostResult {
        cRealm = Realm.getDefaultInstance()

        val korisnik = cRealm?.where(Korisnik::class.java)?.findFirst()
        val formData: FormBody? = korisnik?.let { Builder()
                .add("Username", it.getUsername())
                .add("Password", korisnik.getLozinka())
                .add("IsRememberMeChecked", "true")
                .build() }
        val rq: Request? = formData?.let { Request.Builder()
                .url("https://korisnik.fesb.unist.hr/prijava?returnUrl=https://raspored.fesb" + ".unist.hr")
                .post(it)
                .build() }
        val call0 = rq?.let { client.newCall(it) }

        call0?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { Log.d("pogreska", "failure") }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val request: Request = Request.Builder()
                    .url("https://raspored.fesb.unist.hr/part/prisutnost/opcenito/tablica")
                    .get()
                    .build()
                val call1 = client.newCall(request)

                call1.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) { Log.d("pogreska", "failure") }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.code != 500) {
                            val doc = Jsoup.parse(response.body?.string())
                            try {
                                val zimski = doc.select("div.semster.winter").first()
                                val litnji = doc.select("div.semster.summer").first()
                                val zimskaPredavanja = zimski.select("div.body.clearfix").first()
                                val litnjaPredavanja = litnji.select("div.body.clearfix").first()
                                if (zimski.getElementsByClass("emptyList").first() == null) {
                                    val zimskiKolegiji = zimskaPredavanja.select("a")
                                    for (element in zimskiKolegiji) {
                                        val request: Request = Request.Builder()
                                            .url("https://raspored.fesb.unist.hr" + element.attr("href")
                                                .toString())
                                            .get()
                                            .build()
                                        val callonme = client.newCall(request)
                                        callonme.enqueue(object : Callback {
                                            override fun onFailure(call: Call, e: IOException) { Log.d("pogreska", "failure") }

                                            @Throws(IOException::class)
                                            override fun onResponse(call: Call, response: Response) {
                                                val mRealm1 = Realm.getInstance(realmConfig)
                                                try {
                                                    parseAndToDatabase(element,response, 1, mRealm1)
                                                } catch (exception: Exception) {
                                                    exception.message?.let { Log.d("Exception prisutnost", it) }
                                                    exception.printStackTrace()
                                                } finally {
                                                    mRealm1.close()
                                                }
                                            }
                                        })
                                    }
                                }
                                if (litnji.getElementsByClass("emptyList").first() == null) {
                                    val litnjiKolegiji = litnjaPredavanja.select("a")
                                    for (element in litnjiKolegiji) {
                                        val request: Request = Request.Builder()
                                            .url("https://raspored.fesb.unist.hr" + element.attr("href")
                                                    .toString())
                                            .get()
                                            .build()
                                        val callonme1 = client.newCall(request)
                                        callonme1.enqueue(object : Callback {
                                            override fun onFailure(call: Call, e: IOException) { Log.d("pogreska", "failure") }

                                            @Throws(IOException::class)
                                            override fun onResponse(call: Call, response: Response) {
                                                val mRealm2 = Realm.getInstance(realmConfig)
                                                try {
                                                    parseAndToDatabase(element,response, 2, mRealm2)
                                                } catch (e: Exception) {
                                                    e.message?.let { Log.d("Exception prisutnost", it) }
                                                    e.printStackTrace()
                                                } finally {
                                                    mRealm2.close()
                                                }
                                            }
                                        })
                                    }
                                }
                            } catch (ex: Exception) {
                                Log.d("Exception pris", ex.message!!)
                                ex.printStackTrace()
                            }
                        }
                    }
                })
            }
        })
        return Result.PrisutnostResult.Success(true) //popravit ovo tako da vrati failure kada je failure
    // i premjestit i minimizirat pisanje u bazu u dao
}

private fun deletePreviousResults() {
    var nRealm: Realm? = null
    nRealm = Realm.getInstance(realmConfig)
    val svaPrisutnost = nRealm?.where(Dolazak::class.java)?.findAll()
    nRealm?.executeTransaction { svaPrisutnost?.deleteAllFromRealm() }
}
fun parseAndToDatabase(element: Element, response: Response, semester: Int, mRealm: Realm) {
        val document = Jsoup.parse(response.body?.string())
        val content = document.getElementsByClass("courseCategories").first()
        val kategorije = content.select("div.courseCategory")
        mRealm.executeTransaction { realm ->
            for (kat in kategorije) {
                val mDolazak = realm.createObject( Dolazak::class.java, UUID.randomUUID().toString() )
                mDolazak.setSemestar(semester)
                mDolazak.setPredmet(element.select("div.cellContent").first().text())
                mDolazak.setVrsta(kat.getElementsByClass("name" ).first().text())
                mDolazak.setAttended(kat.select("div.attended > span.num").first().text().toInt() )
                mDolazak.setAbsent(kat.select("div.absent > span.num").first().text().toInt())
                mDolazak.setRequired(kat.select("div.required-attendance " +"> span" ).first().text())
                val string = kat.select("div" +".required-attendance > " + "span").first().text()
                val st = StringTokenizer(string, " ")
                val ric1 = st.nextToken()
                val ric2 = st.nextToken()
                val max = st.nextToken()
                mDolazak.setTotal(max.toInt())
            }
        }

}
}