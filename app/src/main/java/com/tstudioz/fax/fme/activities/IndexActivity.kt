package com.tstudioz.fax.fme.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.databinding.ActivityIndexBinding

class IndexActivity : AppCompatActivity() {
    private var binding: ActivityIndexBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndexBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val entry = RadarEntry(5f, 0)
        val entry4 = RadarEntry(3f, 1)
        val entry5 = RadarEntry(5f, 2)
        val entry3 = RadarEntry(4f, 3)
        val entry2 = RadarEntry(5f, 4)

        val formatter = IValueFormatter { value, entry, dataSetIndex, viewPortHandler ->
            value.toInt()
                .toString() + ""
        }

        val entryList: MutableList<RadarEntry> = ArrayList()
        entryList.add(entry4)
        entryList.add(entry5)
        entryList.add(entry3)
        entryList.add(entry)
        entryList.add(entry2)

        val labels: MutableList<String> = ArrayList()
        labels.add("Hello")
        labels.add("Hello")
        labels.add("Hello")
        labels.add("Hello")
        labels.add("Hello")

        val rDataSet = RadarDataSet(entryList, "")
        rDataSet.valueTextColor = ContextCompat.getColor(baseContext, R.color.white)
        rDataSet.color = ContextCompat.getColor(baseContext, R.color.blue_nice)
        rDataSet.highlightCircleFillColor = ContextCompat.getColor(
            baseContext,
            R.color.white
        )

        rDataSet.highlightCircleStrokeColor = ContextCompat.getColor(
            baseContext,
            R.color.white
        )
        rDataSet.valueFormatter = formatter
        rDataSet.setDrawFilled(true)
        val data = RadarData(rDataSet)
        data.setValueTextColor(ContextCompat.getColor(baseContext, R.color.white))
        data.labels = labels
        data.setValueTextSize(14f)
        binding!!.radarChart.data = data
        binding!!.radarChart.setDrawWeb(true)
        binding!!.radarChart.webColor = ContextCompat.getColor(baseContext, R.color.white)
        binding!!.radarChart.webColorInner = ContextCompat.getColor(
            baseContext,
            R.color.white
        )
        binding!!.radarChart.yAxis.axisMinimum = 0f
        binding!!.radarChart.yAxis.axisMaximum = 5f
        binding!!.radarChart.yAxis.setDrawLabels(false)
        binding!!.radarChart.yAxis.setDrawTopYLabelEntry(true)
        binding!!.radarChart.legend.isEnabled = false
        binding!!.radarChart.description.isEnabled = false
        binding!!.radarChart.animateXY(
            1400, 1400,
            Easing.EasingOption.EaseInOutQuad,
            Easing.EasingOption.EaseInOutQuad
        )
        binding!!.radarChart.invalidate()
    }
}