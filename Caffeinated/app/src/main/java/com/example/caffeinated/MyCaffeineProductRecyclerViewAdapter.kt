package com.example.caffeinated

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.caffeinated.placeholder.PlaceholderContent.PlaceholderItem
import com.example.caffeinated.databinding.FragmentCaffeineProductListBinding
import kotlinx.android.synthetic.main.fragment_caffeine_product.view.*

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyCaffeineProductRecyclerViewAdapter(
    private val values: MutableList<CaffeineProduct>
) : RecyclerView.Adapter<MyCaffeineProductRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_caffeine_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = (position+1).toString()
        holder.nameView.text = "Product:"+ item.productName.toString()
        holder.barcodeView.text = "Barcode:"+ item.barcode.toString()
        holder.quantityView.text = "Quantity:"+ item.numberConsumed.toString()
        holder.dateView.text = "Date:"+ item.dateConsumed.toString()
        holder.caffeineView.text = "Caffeine (mg):"+ item.caffeineAmount.toString()


    }

    fun clearData(){
        for (i in 0..values.size-1) {
            values.removeAt(0)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = values.size

    fun addCaffeineProduct(product: CaffeineProduct) {
        values.add(product)
        Log.d("DEBUG", "Added to values")
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.item_number)
        val nameView: TextView = view.findViewById(R.id.product_name)
        val barcodeView: TextView = view.findViewById(R.id.product_barcode)
        val quantityView: TextView = view.findViewById(R.id.product_quantity)
        val dateView: TextView = view.findViewById(R.id.product_date_consumed)
        val caffeineView: TextView = view.findViewById(R.id.product_caffeine_amount)

        override fun toString(): String {
            return "${super.toString()} - ${idView.text.toString()} - ${nameView.text.toString()}"
        }
    }

}