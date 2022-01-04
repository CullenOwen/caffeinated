package com.example.caffeinated

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.findFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("DEBUG", "In OnCreateView")

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val dbManager = DatabaseManager(requireActivity())

        val listFragment = childFragmentManager.findFragmentById(R.id.fragment_product_list) as? CaffeineProductList
        var allRows = dbManager.readAllRows()

        for (row in allRows) {
            var currentRow = row.split(',')

            var name = currentRow[0]
            var barcode = currentRow[1]
            var numConsumed = currentRow[2].toInt()
            var dateConsumed = currentRow[3]
            var caffeineAmount = currentRow[4]
            listFragment?.updateProductList(CaffeineProduct(name, barcode, numConsumed, dateConsumed, caffeineAmount))
        }

        val button: Button = view.findViewById(R.id.button_clear_data)
        button.setOnClickListener {

            var builder = AlertDialog.Builder(requireActivity())
            builder.setCancelable(true)
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure?")
            builder.setPositiveButton("Confirm", DialogInterface.OnClickListener { dialog, which ->
                dbManager.clear()
                listFragment?.clearProductList()
            })

            builder.setNegativeButton("Decline", DialogInterface.OnClickListener { dialog, which ->
                //Do Nothing
            })

            val dialog = builder.create()
            dialog.show()
        }

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}