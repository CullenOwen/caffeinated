package com.example.caffeinated

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SearchConfirmationActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_confirmation)

        var productName = intent.getStringExtra("ProductName")
        var barcode = intent.getStringExtra("Barcode")
        var numberConsumed = intent.getIntExtra("NumConsumed", 1)
        var dateConsumed = intent.getStringExtra("DateConsumed")
        var caffeineAmount = intent.getStringExtra("CaffeineAmount")

        var productNameEntry = findViewById<EditText>(R.id.confirmation_product_name_text)
        productNameEntry.setText(productName)

        var barcodeEntry: EditText = findViewById(R.id.confirmation_barcode_entry)
        barcodeEntry.setText(barcode)

        var numberConsumedEntry: NumberPicker = findViewById(R.id.confirmation_picker_quantity)
        numberConsumedEntry.minValue = 1
        numberConsumedEntry.maxValue = 50
        numberConsumedEntry.value = numberConsumed

        var dateConsumedEntry: EditText = findViewById(R.id.confirmation_date_entry_text)
        dateConsumedEntry.setText(dateConsumed)
        dateConsumedEntry.setOnClickListener {
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                var formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                var text = date.format(formatter)
                dateConsumedEntry.setText(text)
            }, year, month, day)

            dialog.show()
        }

        var caffeineAmountEntry: EditText = findViewById(R.id.confirmation_caffeine_amount)
        caffeineAmountEntry.setText(caffeineAmount)

        var confirmButton: Button = findViewById(R.id.confirmation_button_confirm)
        var declineButton: Button = findViewById(R.id.confirmation_button_decline)

        val dbMan = DatabaseManager(this)

        confirmButton.setOnClickListener {
            dbMan.insert(CaffeineProduct(productNameEntry.text.toString(),
                                         barcodeEntry.text.toString(),
                                         numberConsumedEntry.value,
                                         dateConsumedEntry.text.toString(),
                                         caffeineAmountEntry.text.toString()))
            val resultIntent = Intent()
            setResult(77, resultIntent)
            finish()
        }
        declineButton.setOnClickListener {
            val resultIntent = Intent()
            setResult(77, resultIntent)
            finish()
        }
    }
}