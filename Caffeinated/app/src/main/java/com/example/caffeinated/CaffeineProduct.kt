package com.example.caffeinated

class CaffeineProduct (val productName: String, val barcode: String ,val numberConsumed: Int, val dateConsumed: String, val caffeineAmount: String){

    override fun toString(): String {
        return "Product:$productName\nBarcode:$barcode\nQuantity:$numberConsumed\nDate:$dateConsumed\nCaffeine (Mg):${caffeineAmount}"
    }

}