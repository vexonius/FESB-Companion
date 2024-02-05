package com.tstudioz.fax.fme.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.view.adapters.PredavanjaRaspAdapterTable.PredavanjaRaspViewHolderTable
import io.realm.kotlin.query.RealmResults

class PredavanjaRaspAdapterTable(private var mPredavanja: RealmResults<Predavanja>) : RecyclerView.Adapter<PredavanjaRaspViewHolderTable>() {

    private var dialog: BottomSheetDialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredavanjaRaspViewHolderTable {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.table_row_item, parent, false)
        return PredavanjaRaspViewHolderTable(view)
    }

    override fun onBindViewHolder(holder: PredavanjaRaspViewHolderTable, position: Int) {
        val compactImePredavanja = StringBuilder()

        val predavanja = mPredavanja[position]

        if (predavanja?.predmetPredavanja != null && predavanja.predmetPredavanja?.isEmpty()==false && predavanja.predmetPredavanja?.contains(" ")==true) {
            for (str in predavanja.predmetPredavanja!!.split(" ").toTypedArray())
                compactImePredavanja.append(str[0])

            holder.tablename.text = compactImePredavanja.toString()
        } else {
            holder.tablename.text = predavanja?.predmetPredavanja ?: ""
        }
        holder.tablename.text = compactImePredavanja.toString()
        holder.tabletype.text = predavanja?.rasponVremena ?: ""
        holder.tablemjesto.text = predavanja?.dvorana ?: ""

        when (predavanja?.predavanjeIme) {
            "Predavanje" -> holder.tableboja.setBackgroundResource(R.color.blue_nice)
            "Auditorne vježbe" -> holder.tableboja.setBackgroundResource(R.color.green_nice)
            "Kolokvij" -> holder.tableboja.setBackgroundResource(R.color.purple_nice)
            "Laboratorijske vježbe" -> holder.tableboja.setBackgroundResource(R.color.red_nice)
            "Konstrukcijske vježbe" -> holder.tableboja.setBackgroundResource(R.color.grey_nice)
            "Seminar" -> holder.tableboja.setBackgroundResource(R.color.blue_nice)
            "Ispit" -> holder.tableboja.setBackgroundResource(R.color.purple_dark)
        }
    }

    override fun getItemCount(): Int {
        return mPredavanja.size
    }

    inner class PredavanjaRaspViewHolderTable(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tablename: TextView
        var tabletype: TextView
        var tablemjesto: TextView
        var tableboja: ConstraintLayout

        override fun onClick(view: View) {
            onShowBottomsheet(view, adapterPosition)
        }

        init {
            tablename = itemView.findViewById<View>(R.id.table_name) as TextView
            tabletype = itemView.findViewById<View>(R.id.table_type) as TextView
            tableboja = itemView.findViewById<View>(R.id.colorMe) as ConstraintLayout
            tablemjesto = itemView.findViewById<View>(R.id.table_mjesto) as TextView
            itemView.setOnClickListener(this)
        }
    }


    fun onShowBottomsheet(view: View, position: Int) {
        val context = view.context
        val predavanja = mPredavanja[position]
        val infoKolegij: TextView
        val infoPredavanje: TextView
        val infoProf: TextView
        val infoVrijeme: TextView
        val infoGrupa: TextView
        val infoLokacija: TextView
        val views = LayoutInflater.from(context).inflate(R.layout.bottom_sheep, null)

        infoKolegij = views.findViewById<View>(R.id.predavanjeImeDialog) as TextView
        infoPredavanje = views.findViewById<View>(R.id.opisPredavanja) as TextView
        infoProf = views.findViewById<View>(R.id.text_ispod2) as TextView
        infoVrijeme = views.findViewById<View>(R.id.text_ispod3) as TextView
        infoGrupa = views.findViewById<View>(R.id.text_ispod4) as TextView
        infoLokacija = views.findViewById<View>(R.id.text_ispod5) as TextView

        var imePredavanja = predavanja?.predavanjeIme
        if (!imePredavanja.isNullOrEmpty()) {
            imePredavanja = imePredavanja.substring(0, imePredavanja.length)
        }
        var imeGrupe = predavanja?.grupa
        imeGrupe = if (!imeGrupe.isNullOrEmpty()) {
            imeGrupe.substring(0, imeGrupe.length - 1)
        }else{
            "Jedna grupe"
        }
        infoKolegij.text = predavanja?.predmetPredavanja
        infoPredavanje.text = imePredavanja
        infoProf.text = predavanja?.profesor
        infoVrijeme.text = predavanja?.rasponVremena
        infoGrupa.text = imeGrupe
        infoLokacija.text = predavanja?.dvorana

        when (predavanja?.predavanjeIme) {
            "Predavanje" -> infoKolegij.setBackgroundResource(R.color.blue_nice)
            "Auditorne vježbe" -> infoKolegij.setBackgroundResource(R.color.green_nice)
            "Kolokvij" -> infoKolegij.setBackgroundResource(R.color.purple_nice)
            "Laboratorijske vježbe" -> infoKolegij.setBackgroundResource(R.color.red_nice)
            "Konstrukcijske vježbe" -> infoKolegij.setBackgroundResource(R.color.grey_nice)
            "Seminar" -> infoKolegij.setBackgroundResource(R.color.blue_nice)
            "Ispit" -> infoKolegij.setBackgroundResource(R.color.purple_dark)
        }

        dialog = BottomSheetDialog(context).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            setContentView(views)
        }
        dialog?.show()
    }

}