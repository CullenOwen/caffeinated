package com.example.caffeinated

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.findFragment
import kotlinx.android.synthetic.main.fragment_loading.*
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import org.json.JSONObject
import org.json.JSONArray
import java.net.URL

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AutomatedAdd : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var loadingDialog = LoadingFragment()

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

        var view = inflater.inflate(R.layout.fragment_automated_add, container, false)

        var numberPicker: NumberPicker = view.findViewById(R.id.automated_picker_quantity)
        numberPicker.minValue = 1
        numberPicker.maxValue = 50

        var datePickerText: EditText = view.findViewById(R.id.automated_date_entry_text)

        datePickerText.setOnClickListener {
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                var formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                var text = date.format(formatter)
                datePickerText.setText(text)
            }, year, month, day)

            dialog.show()
        }

        val scan: Button = view.findViewById(R.id.button_scan)
        scan.setOnClickListener {
            val intent = Intent(activity, BarcodeScanner::class.java)
            startActivityForResult(intent, 1)
        }

        val barcodeEntry: EditText = view.findViewById(R.id.text_barcode)

        val searchButton: Button = view.findViewById(R.id.automated_search_button)
        searchButton.setOnClickListener {

            if (barcodeEntry.text.toString().length == 0) {

                searchButton.startAnimation(AnimationUtils.loadAnimation(requireActivity(), R.anim.shake))

                var barcodeToast = Toast.makeText(requireActivity(), "Barcode Error!", Toast.LENGTH_SHORT)
                barcodeToast.show()

            } else if (datePickerText.text.toString().length == 0) {

                searchButton.startAnimation(AnimationUtils.loadAnimation(requireActivity(), R.anim.shake))

                var dateToast = Toast.makeText(requireActivity(), "Date Error!", Toast.LENGTH_SHORT)
                dateToast.show()
            } else {
                loadingDialog.show(childFragmentManager.beginTransaction(), "LoadingFragment")
                Log.d("DEBUG", "BarcodeEntry was filled... performing search...")

                var intent = Intent(requireActivity(), SearchConfirmationActivity::class.java)

                var productToEnter = PerformSearch(barcodeEntry.text.toString(), numberPicker.value, datePickerText.text.toString())

                if (productToEnter != null) {
                    Log.d("Returned", productToEnter?.toString())

                    with (intent) {
                        putExtra("ProductName", productToEnter.productName)
                        putExtra("Barcode", productToEnter.barcode)
                        putExtra("NumConsumed", productToEnter.numberConsumed)
                        putExtra("DateConsumed", productToEnter.dateConsumed)
                        putExtra("CaffeineAmount", productToEnter.caffeineAmount)

                        startActivityForResult(this, 77)
                    }

                    barcodeEntry.setText("")
                    datePickerText.setText("")
                    numberPicker.value = 1

                } else {
                    Log.d("Returned", "NULL")
                    loadingDialog.dismiss()

                    var toast = Toast.makeText(requireActivity(), "Error Finding Data", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val barcode = data?.getStringExtra("BARCODE")

            val barcodeEntry: EditText? = view?.findViewById(R.id.text_barcode)
            barcodeEntry?.setText(barcode)
        }
        if (requestCode == 77)
        {
            Log.d("DEBUG2", "onActivityResultEntered")
            loadingDialog.dismiss()
        }
    }

    private fun PerformSearch(barcodeToSearch: String, numConsumed: Int, dateConsumed: String): CaffeineProduct? {
        val url = URL("https://usa.openfoodfacts.org/api/v0/product/${barcodeToSearch}.json")
        var returnProduct: CaffeineProduct? = null

        runBlocking {

            val jobs = GlobalScope.launch {
                val text_read = url.readText()
                Log.d("API", "URL: ${url.toString()}")

                val jsonObject = JSONObject(text_read)

                val statusValue = jsonObject.getInt("status")
                val statusVerbose = jsonObject.getString("status_verbose")

                if (statusValue == 1 && statusVerbose == "product found") {
                    Log.d("API", "Found Product \"${barcodeToSearch}\"")

                    var productObject = jsonObject.getJSONObject("product")
                    var productName = productObject.getString("product_name")
                    var caffeine_serving: Double = 0.0

                    try {
                        var nutriments = productObject.getJSONObject("nutriments")

                        caffeine_serving = nutriments.getDouble("caffeine_serving") * 1000

                        Log.d("API", "Serving: ${caffeine_serving.toInt().toString()}mg")
                        returnProduct = CaffeineProduct(productName, barcodeToSearch, numConsumed, dateConsumed, caffeine_serving.toInt().toString())

                    } catch (ex: Exception) {
                        Log.d("API", "Nutriments Exception: ${ex.localizedMessage}")
                    }
                } else {
                    Log.d("API", "Did not find product \"${barcodeToSearch}\"")
                }
            }

            jobs.join()

        }

        return returnProduct
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AutomatedAdd().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}