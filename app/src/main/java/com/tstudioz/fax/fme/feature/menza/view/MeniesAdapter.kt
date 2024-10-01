package com.tstudioz.fax.fme.feature.menza.view

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Meni
import com.tstudioz.fax.fme.feature.menza.view.MeniesAdapter.MeniViewHolder

class MeniesAdapter(private var mMenies: List<Meni>?) : RecyclerView.Adapter<MeniViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeniViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meni_item, parent, false)
        return MeniViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeniViewHolder, position: Int) {
        val meni = mMenies?.get(position)
        holder.title.typeface = holder.regulartf
        holder.jelo1.typeface = holder.lighttf
        holder.jelo2.typeface = holder.lighttf
        holder.jelo3.typeface = holder.lighttf
        holder.jelo4.typeface = holder.lighttf
        holder.jelo5.typeface = holder.lighttf
        holder.cijena.typeface = holder.regulartf
        if (meni?.id == "R-MENI") {
            holder.title.text = meni.type
            holder.jelo1.text = meni.jelo1
            holder.jelo2.text = meni.jelo2
            holder.jelo3.text = meni.jelo3
            holder.jelo4.text = meni.jelo4
            holder.jelo5.text = meni.desert
            holder.cijena.text = meni.cijena + " eur"
        } else if (meni?.id == "R-JELO PO IZBORU") {
            holder.title.text = "JELO PO IZBORU"
            holder.jelo1.text = meni.jelo1
            holder.cijena.text = meni.cijena + " eur"
            holder.jelo2.visibility = View.GONE
            holder.jelo3.visibility = View.GONE
            holder.jelo4.visibility = View.GONE
            holder.jelo5.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mMenies?.size ?: 0
    }

    inner class MeniViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        var title: TextView
        var jelo1: TextView
        var jelo2: TextView
        var jelo3: TextView
        var jelo4: TextView
        var jelo5: TextView
        var cijena: TextView
        var regulartf: Typeface
        var lighttf: Typeface
        var boldtf: Typeface

        init {
            title = mView.findViewById<View>(R.id.meni_title) as TextView
            jelo1 = mView.findViewById<View>(R.id.meni_jelo1) as TextView
            jelo2 = mView.findViewById<View>(R.id.meni_jelo2) as TextView
            jelo3 = mView.findViewById<View>(R.id.meni_jelo3) as TextView
            jelo4 = mView.findViewById<View>(R.id.meni_jelo4) as TextView
            jelo5 = mView.findViewById<View>(R.id.meni_jelo5) as TextView
            cijena = mView.findViewById<View>(R.id.meni_cijena) as TextView
            regulartf = Typeface.createFromAsset(
                itemView.context.assets, "fonts" +
                        "/OpenSans-Regular.ttf"
            )
            lighttf = Typeface.createFromAsset(
                itemView.context.assets, "fonts/OpenSans" +
                        "-Light.ttf"
            )
            boldtf = Typeface.createFromAsset(
                itemView.context.assets, "fonts/OpenSans" +
                        "-Bold.ttf"
            )
        }
    }

}