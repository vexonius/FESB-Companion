package com.tstudioz.fax.fme.feature.attendance.services

import android.util.Log
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.FormBody.Builder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.StringTokenizer
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AttendanceService(private val client: OkHttpClient) : AttendanceServiceInterface {

    override suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult {
        val prisutnost: MutableMap<String, MutableList<Dolazak>> = mutableMapOf()

        val formData: FormBody = Builder()
            .add("Username", user.username)
            .add("Password", user.password)
            .add("IsRememberMeChecked", "true")
            .build()
        val rq: Request = Request.Builder()
            .url("https://korisnik.fesb.unist.hr/prijava?returnUrl=https://raspored.fesb" + ".unist.hr")
            .post(formData)
            .build()
        val response = makeNetworkCall(rq)

        if (response.isSuccessful) {
            val request: Request = Request.Builder()
                .url("https://raspored.fesb.unist.hr/part/prisutnost/opcenito/tablica")
                .get()
                .build()
            val response1 = makeNetworkCall(request)
            if (response1.isSuccessful) {
                if (response.code != 500) {
                    val doc = response1.body?.string()?.let { Jsoup.parse(it) }
                    try {
                        val zimski = doc?.select("div.semster.winter")?.first()
                        val litnji = doc?.select("div.semster.summer")?.first()
                        val zimskaPredavanja = zimski?.select("div.body.clearfix")?.first()
                        val litnjaPredavanja = litnji?.select("div.body.clearfix")?.first()
                        if (zimski != null && zimski.getElementsByClass("emptyList")
                                .first() == null
                        ) {
                            zimskaPredavanja?.let { prisutnost.putAll(getDetailedPrisutnost(it, 1)) }
                        }
                        if (litnji != null && litnji.getElementsByClass("emptyList")
                                .first() == null
                        ) {
                            litnjaPredavanja?.let { prisutnost.putAll(getDetailedPrisutnost(it, 2)) }
                        }
                    } catch (ex: Exception) {
                        ex.message?.let { Log.d("Exception pris", it) }
                        ex.printStackTrace()
                        return NetworkServiceResult.PrisutnostResult.Failure(Throwable("Failed to parse attendance"))
                    }
                }
            }
        } else {
            return NetworkServiceResult.PrisutnostResult.Failure(Throwable("Failed to fetch attendance"))
        }

        return NetworkServiceResult.PrisutnostResult.Success(prisutnost) //popravit ovo tako da vrati failure kada je failure
    }

    private suspend fun makeNetworkCall(request: Request): Response =
        suspendCoroutine { continuation ->
            val callback = object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response)
                }
            }

            client.newCall(request).enqueue(callback)
        }

    private fun parseAndToDatabase(
        element: Element,
        response: Response,
        semester: Int
    ): MutableList<Dolazak> {
        val document = response.body?.string()?.let { Jsoup.parse(it) }
        val content = document?.getElementsByClass("courseCategories")?.first()
        val kategorije = content?.select("div.courseCategory")
        val svaFreshPrisutnost = mutableListOf<Dolazak>()
        if (kategorije != null) {
            for (kat in kategorije) {
                val mDolazak = Dolazak()
                mDolazak.semestar = semester
                mDolazak.predmet = element.select("div.cellContent").first()?.text()
                mDolazak.vrsta = kat.getElementsByClass("name").first()?.text()
                val attended = kat.select("div.attended > span.num").first()?.text()
                if (attended != null) {
                    mDolazak.attended = attended.toInt()
                }
                val absent = kat.select("div.absent > span.num").first()?.text()
                if (absent != null) {
                    mDolazak.absent = absent.toInt()
                }
                mDolazak.required =
                    kat.select("div.required-attendance " + "> span").first()?.text()
                val string = kat.select("div" + ".required-attendance > " + "span").first()?.text()
                val st = StringTokenizer(string, " ")
                st.nextToken()
                st.nextToken()
                val max = st.nextToken()
                mDolazak.total = max.toInt()
                val str =
                    "${mDolazak.attended}${mDolazak.absent}${mDolazak.predmet}${mDolazak.vrsta}${mDolazak.required}${mDolazak.total}${mDolazak.semestar}"
                val id = UUID.nameUUIDFromBytes(str.toByteArray())
                mDolazak.id = id.toString()
                svaFreshPrisutnost.add(mDolazak)
            }
        }

        return svaFreshPrisutnost
    }

    private suspend fun getDetailedPrisutnost(
        listaPredavanja: Element,
        sem: Int
    ): MutableMap<String, MutableList<Dolazak>> {
        val pris: MutableMap<String, MutableList<Dolazak>> = mutableMapOf()
        val kolegiji = listaPredavanja.select("a")
        for (element in kolegiji) {
            val request: Request = Request.Builder()
                .url(
                    "https://raspored.fesb.unist.hr" + element.attr("href")
                        .toString()
                )
                .get()
                .build()
            val response = makeNetworkCall(request)
            if (response.isSuccessful) {
                try {
                    parseAndToDatabase(element, response, sem).let {
                        pris[it.first().predmet ?:""] = it
                    }
                } catch (exception: Exception) {
                    exception.message?.let { Log.d("Exception prisutnost", it) }
                    exception.printStackTrace()
                }
            }
        }

        return pris
    }
}