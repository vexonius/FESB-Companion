package com.tstudioz.fax.fme.models.services

import android.util.Log
import com.tstudioz.fax.fme.database.Dolazak
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.interfaces.PrisutnostInterface
import io.realm.Realm
import okhttp3.Call
import okhttp3.Callback
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


    override suspend fun fetchPrisutnost(): Result.PrisutnostResult {
        var zimskaPris :MutableList<Dolazak> = mutableListOf()
        var ljetnaPris :MutableList<Dolazak> = mutableListOf()
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
                                    zimskaPris = getDetailedPrisutnost(zimskaPredavanja, 1)
                                }
                                if (litnji.getElementsByClass("emptyList").first() == null) {
                                    ljetnaPris = getDetailedPrisutnost(litnjaPredavanja, 2)
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
        return Result.PrisutnostResult.Success(zimskaPris + ljetnaPris) //popravit ovo tako da vrati failure kada je failure
}

fun parseAndToDatabase(element: Element, response: Response, semester: Int): MutableList<Dolazak> {
    val document = Jsoup.parse(response.body?.string())
    val content = document.getElementsByClass("courseCategories").first()
    val kategorije = content.select("div.courseCategory")
    val svaFreshPrisutnost = mutableListOf<Dolazak>()
    for (kat in kategorije) {
        val mDolazak = Dolazak()
        mDolazak.setId(UUID.randomUUID().toString() )
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
        svaFreshPrisutnost.add(mDolazak)
    }
    return svaFreshPrisutnost
}
    fun getDetailedPrisutnost(listaPredavanja: Element, sem: Int): MutableList<Dolazak> {
        var pris :MutableList<Dolazak> = mutableListOf()
        val kolegiji = listaPredavanja.select("a")
        for (element in kolegiji) {
            val request: Request = Request.Builder()
                .url("https://raspored.fesb.unist.hr" + element.attr("href")
                        .toString())
                .get()
                .build()
            val callonme = client.newCall(request)
            callonme.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("pogreska", "failure")
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    try {
                        pris = parseAndToDatabase(element, response, sem)
                    } catch (exception: Exception) {
                        exception.message?.let { Log.d("Exception prisutnost", it) }
                        exception.printStackTrace()
                    }
                }
            })
        }
        return  pris
    }
}