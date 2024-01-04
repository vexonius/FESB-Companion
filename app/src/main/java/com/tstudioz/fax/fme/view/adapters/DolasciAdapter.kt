package com.tstudioz.fax.fme.view.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.view.adapters.DolasciAdapter.DolazakViewHolder
import com.tstudioz.fax.fme.database.Dolazak
import io.realm.RealmChangeListener
import io.realm.RealmResults

class DolasciAdapter(private val context: Context, private val mDolazak: RealmResults<Dolazak>) : RecyclerView.Adapter<DolazakViewHolder?>(), RealmChangeListener<Any?> {
    var regulartf: Typeface? = null

    init {
        mDolazak.addChangeListener(this as RealmChangeListener<RealmResults<Dolazak>>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DolazakViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_item_attendance, parent, false
        )
        return DolazakViewHolder(view)
    }

    override fun onBindViewHolder(holder: DolazakViewHolder, position: Int) {
        val predavanja = mDolazak[position]
        val entries: MutableList<PieEntry> = ArrayList()
        if (predavanja !=null){
            holder.name.text = predavanja.predmet
            holder.name.typeface = regulartf
            holder.type.text = predavanja.vrsta
            val dolazaknum = "Obavezno " + predavanja.required
            holder.dolazakNum.text = dolazaknum
            entries.add(PieEntry(predavanja.attended.toFloat()))
            entries.add(PieEntry(predavanja.absent.toFloat()))
            val neood =
                PieEntry((predavanja.total - predavanja.absent - predavanja.attended).toFloat())
            if (neood.value.toDouble() != 0.0) {
                entries.add(neood)
            }
        }
        val formatter = IValueFormatter { value, _, _, _ ->
            value.toInt()
                .toString() + ""
        }
        holder.chart.setHoleColor(android.R.color.transparent)
        holder.chart.setDrawCenterText(false)
        holder.chart.setDrawEntryLabels(false)
        holder.chart.legend.isEnabled = false
        holder.chart.description.isEnabled = false
        val set = PieDataSet(entries, null)
        set.valueTextSize = 14f
        set.valueTextColor = holder.colorWhite
        set.valueTypeface = regulartf
        set.setColors(*holder.colors)
        set.valueFormatter = formatter
        val data = PieData(set)
        holder.chart.data = data

        // holder.chart.invalidate(); // refresh
        holder.chart.animateY(600)
    }

    override fun getItemCount(): Int {
        return mDolazak.size
    }

    inner class DolazakViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var type: TextView
        var dolazakNum: TextView
        var root: RelativeLayout
        var chart: PieChart
        var colors: IntArray
        var colorWhite: Int

        init {
            root = itemView.findViewById<View>(R.id.attend_row_root) as RelativeLayout
            name = itemView.findViewById<View>(R.id.predmetName) as TextView
            regulartf = Typeface.createFromAsset(
                itemView.context.assets, "fonts" +
                        "/OpenSans-Regular.ttf"
            )
            name.typeface = regulartf
            type = itemView.findViewById<View>(R.id.predmetType) as TextView
            type.typeface = regulartf
            dolazakNum = itemView.findViewById<View>(R.id.dolazakNum) as TextView
            dolazakNum.typeface = regulartf
            colors = itemView.resources.getIntArray(R.array.rainbow)
            colorWhite = ContextCompat.getColor(context, R.color.white)
            chart = itemView.findViewById<View>(R.id.chart) as PieChart
        }
    }

    override fun onChange(element: Any?) {
        notifyDataSetChanged()
    }
}