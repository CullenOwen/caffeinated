package com.example.caffeinated

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManualAdd.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManualAdd : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //Returns true if valid input - if valid, the data is inserted & the fragment is reloaded/cleared of past data
    //Returns false otherwise - if invalid, have a shake animation play on the submit button
    fun readInput(v: View): Boolean {

        var productNameEntry: EditText = v.findViewById(R.id.manual_product_name_text)
        var productNameText = productNameEntry.text.toString()
        if (productNameText.length == 0) {
            var productNameToast = Toast.makeText(requireActivity(), "Product Name Error", Toast.LENGTH_SHORT)
            productNameToast.show()

            return false
        }

        var numberConsumedSpinner = v.findViewById<NumberPicker>(R.id.manual_picker_quantity)
        var numberConsumed = numberConsumedSpinner.value

        var dateConsumedEntry: EditText = v.findViewById(R.id.manual_date_entry_text)
        var dateConsumed = dateConsumedEntry.text.toString()
        Log.d("DEBUG", "dateConsumed:$dateConsumed")
        if (dateConsumed.length == 0) {
            var dateConsumedToast = Toast.makeText(requireActivity(), "Date Consumed Error", Toast.LENGTH_SHORT)
            dateConsumedToast.show()

            return false
        }

        var caffeineAmountEditText = v.findViewById<EditText>(R.id.manual_caffeine_amount)
        var caffeineAmount = caffeineAmountEditText.text.toString()
        if (caffeineAmount.length == 0) {
            var caffeineAmountToast = Toast.makeText(requireActivity(), "Caffeine Amount Error", Toast.LENGTH_SHORT)
            caffeineAmountToast.show()

            return false
        }

        var barcodeEntryText: EditText = v.findViewById(R.id.manual_barcode_entry)
        var barcode = barcodeEntryText.text.toString()
        if (barcode.length == 0) {
            barcode = "Not Supplied..."
        }

        val dbMan = DatabaseManager(requireActivity())
        dbMan.insert(CaffeineProduct(productNameText, barcode, numberConsumed, dateConsumed, caffeineAmount))

        return true
    }

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
        val view = inflater.inflate(R.layout.fragment_manual_add, container, false)

        var productName: EditText = view.findViewById(R.id.manual_product_name_text)
        productName.setOnEditorActionListener { view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("DEBUG", "Finished entering product name...")

                true
            }
            false
        }

        var numberPicker: NumberPicker = view.findViewById(R.id.manual_picker_quantity)
        numberPicker.minValue = 1
        numberPicker.maxValue = 50

        var datePickerText: EditText = view.findViewById(R.id.manual_date_entry_text)
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

        var caffeineAmountText: EditText = view.findViewById(R.id.manual_caffeine_amount)

        var barcodeText: EditText = view.findViewById(R.id.manual_barcode_entry)

        var submitButton = view.findViewById<Button>(R.id.manual_finish_input)
        submitButton.setOnClickListener {
            Log.d("DEBUG", "Manual Submit button pressed")
            var returnValue = readInput(view)
            if (!returnValue) {
                submitButton.startAnimation(AnimationUtils.loadAnimation(requireActivity(), R.anim.shake))
            } else {
                //Clear Data and/or Reload Fragment
                productName.setText("")
                numberPicker.value = 1
                datePickerText.setText("")
                caffeineAmountText.setText("")
                barcodeText.setText("")



                var successToast = Toast.makeText(requireActivity(), "Submitted!", Toast.LENGTH_SHORT)
                successToast.show()

            }
        }

        var clearButton = view.findViewById<Button>(R.id.manual_clear_input)
        clearButton.setOnClickListener {
            productName.setText("")
            numberPicker.value = 1
            datePickerText.setText("")
            caffeineAmountText.setText("")
            barcodeText.setText("")

            var clearedToast = Toast.makeText(requireActivity(), "Cleared...", Toast.LENGTH_SHORT)
            clearedToast.show()
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
         * @return A new instance of fragment ManualAdd.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManualAdd().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}