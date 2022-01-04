package com.example.caffeinated

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * A fragment representing a list of Items.
 */
class CaffeineProductList : Fragment() {
    private lateinit var adapter: MyCaffeineProductRecyclerViewAdapter

    private var columnCount = 1

    fun updateProductList(product: CaffeineProduct) {
        adapter.addCaffeineProduct(product)
        Log.d("DEBUG", "Item count: ${adapter.itemCount}")
    }

    fun clearProductList() {
        adapter.clearData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_caffeine_product_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyCaffeineProductRecyclerViewAdapter(mutableListOf())
            }
            adapter = view.adapter as MyCaffeineProductRecyclerViewAdapter
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            CaffeineProductList().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}