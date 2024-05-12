package com.tstudioz.fax.fme.view.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.view.adapters.HomePredavanjaAdapter.HomePredavanjaViewHolder
import io.realm.kotlin.query.RealmResults

class HomePredavanjaAdapter(private val mPredavanjaDanas: List<Event>) :
    RecyclerView.Adapter<HomePredavanjaViewHolder>() {
    var boldtf: Typeface? = null
    var regulartf: Typeface? = null
    var lighttf: Typeface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePredavanjaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_item, parent,
            false
        )
        return HomePredavanjaViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomePredavanjaViewHolder, position: Int) {
        val predavanja = mPredavanjaDanas[position]
        holder.name.text = predavanja.name
        holder.name.typeface = regulartf
        holder.type.text = predavanja.start.toLocalTime().toString() + " - " + predavanja.end.toLocalTime().toString()
        holder.vrstaPredavanja.text = predavanja.eventType
        holder.mjesto.text = predavanja.classroom

        holder.boja.setBackgroundResource(predavanja.colorId)
    }

    override fun getItemCount(): Int {
        return mPredavanjaDanas.size
    }

    inner class HomePredavanjaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var type: TextView
        var vrstaPredavanja: TextView
        var mjesto: TextView
        var boja: RelativeLayout

        init {
            name = itemView.findViewById<View>(R.id.name) as TextView
            lighttf = Typeface.createFromAsset(
                itemView.context.assets, "fonts/OpenSans" +
                        "-Light.ttf"
            )
            boldtf = Typeface.createFromAsset(
                itemView.context.assets, "fonts/OpenSans" +
                        "-Bold.ttf"
            )
            name.typeface = regulartf
            type = itemView.findViewById<View>(R.id.type) as TextView
            regulartf = Typeface.createFromAsset(
                itemView.context.assets, "fonts" +
                        "/OpenSans-Regular.ttf"
            )
            type.typeface = regulartf
            vrstaPredavanja = itemView.findViewById<View>(R.id.vrstaPredavanja) as TextView
            vrstaPredavanja.typeface = regulartf
            mjesto = itemView.findViewById<View>(R.id.mjesto) as TextView
            mjesto.typeface = regulartf
            boja = itemView.findViewById<View>(R.id.textBox) as RelativeLayout
        }
    }

}