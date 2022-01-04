package com.example.caffeinated

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseManager (val context: Context): SQLiteOpenHelper(context, "CaffeineHistory", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {            // 0            1           2               3               4
        db?.execSQL("CREATE TABLE IF NOT EXISTS HISTORY(product_name, barcode, number_consumed, date_consumed, caffeine_val)")
    }

    fun insert(product: CaffeineProduct) {
        writableDatabase.execSQL("INSERT INTO HISTORY VALUES (\"${product.productName}\",\"${product.barcode}\",${product.numberConsumed},\"${product.dateConsumed}\",${product.caffeineAmount})")
    }

    fun clear() {
        writableDatabase.execSQL("DELETE FROM HISTORY WHERE TRUE")
    }

    fun readAllRows(): List<String> {
        var result = mutableListOf<String>()
        var cursor = writableDatabase.rawQuery("SELECT * FROM HISTORY ORDER BY date_consumed DESC", null)
        while (cursor.moveToNext()) {
            result.add("${cursor.getString(0)},${cursor.getString(1)},${cursor.getString(2)},${cursor.getString(3)},${cursor.getString(4)}")
        }
        return result
    }

    fun dateRangeQuery(lowerBound: String, upperBound: String): List<CaffeineProduct> {
        var result = mutableListOf<CaffeineProduct>()
        var cursor = writableDatabase.rawQuery("SELECT * FROM HISTORY WHERE date_consumed >= '${lowerBound}' AND date_consumed <= '${upperBound}' ORDER BY date_consumed ASC", null)

        while (cursor.moveToNext()) {
            result.add(CaffeineProduct("${cursor.getString(0)}", "${cursor.getString(1)}", cursor.getString(2).toInt(), "${cursor.getString(3)}", "${cursor.getString(4)}"))
            Log.d("Iteration", result.toString())
        }

        return result
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}