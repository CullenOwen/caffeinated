package com.example.caffeinated

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.text.SimpleDateFormat

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CaffeineHistoryFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_caffeine_history, container, false)

        var dbMan = DatabaseManager(requireActivity())

        var chart: LineChart = view.findViewById(R.id.chart)
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.valueFormatter = object: ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                var date = Date(value.toLong())
                var sdf = SimpleDateFormat("M/d/yy")
                return sdf.format(date)
            }
        }
        chart.xAxis.textColor = Color.LTGRAY
        chart.axisRight.textColor = Color.LTGRAY
        chart.axisLeft.textColor = Color.LTGRAY
        chart.legend.textColor = Color.LTGRAY
        chart.description.textColor = Color.LTGRAY

        var entries = mutableListOf<Entry>()
        var dataset = LineDataSet(entries, "Caffeine (mg)")
        dataset.values = entries
        var linedata = LineData(dataset)
        chart.data = linedata

        var result = listOf<CaffeineProduct>()

        var date_one = view.findViewById<EditText>(R.id.history_date_one)
        date_one.setOnClickListener {
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                var formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                var text = date.format(formatter)
                date_one.setText(text)
            }, year, month, day)

            dialog.show()
        }


        var date_two = view.findViewById<EditText>(R.id.history_date_two)
        date_two.setOnClickListener {
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                var formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                var text = date.format(formatter)
                date_two.setText(text)
            }, year, month, day)

            dialog.show()
        }

        var submitButton = view.findViewById<Button>(R.id.history_finish_input)
        submitButton.setOnClickListener {

            entries.clear()

            var lowerDate = if (date_one.text.toString() <= date_two.text.toString()) { date_one.text.toString() } else { date_two.text.toString() }
            var upperDate = if (date_one.text.toString() >= date_two.text.toString()) { date_one.text.toString() } else { date_two.text.toString() }

            result = dbMan.dateRangeQuery(lowerDate, upperDate)
            Log.d("HELP", result.toString())

            var dataPoints = Array<MyDataPoint?>(result.size) {null}

            for (i in result.indices) {

                var curr = result[i]
                var parsed = SimpleDateFormat("yyyy/MM/dd").parse(curr.dateConsumed)
                dataPoints[i] = MyDataPoint(parsed, curr.caffeineAmount.toDouble())

                if (entries.size > 0 && entries.last().x == parsed.time.toFloat()) {
                    var newY: Float = entries.last().y + curr.caffeineAmount.toFloat() * curr.numberConsumed
                    linedata.removeEntry(entries.last(), 0)
                    linedata.addEntry(Entry(parsed.time.toFloat(), newY), 0)
                } else {
                    linedata.addEntry(Entry(parsed.time.toFloat(), curr.caffeineAmount.toFloat() * curr.numberConsumed), 0)
                }
            }
            // Update the chart
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()
        }

        var clearButton = view.findViewById<Button>(R.id.history_clear_input)
        clearButton.setOnClickListener {
            date_one.setText("")
            date_two.setText("")

            entries.clear()
            // Update the chart
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CaffeineHistoryFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CaffeineHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}