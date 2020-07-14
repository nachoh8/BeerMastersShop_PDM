package com.nachohseara.beermastersshop.ui.home.editproduc

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.LoaderFragment
import com.nachohseara.beermastersshop.model.entity.Brand

class BrandsFragment : LoaderFragment() {
    private lateinit var mView: View
    private val controller = BrandsController(this)

    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton

    private var ba: ByteArray? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_brands, container, false)

        var json =  ""
        if (arguments != null) {
            json = requireArguments().getString("prod", "")
            ba = requireArguments().getByteArray("img")
        }

        loadViews()
        controller.onCreate(json)

        return mView
    }

    fun loadViews() {
        recyclerView = mView.findViewById(R.id.recyclerView)

        floatingActionButton = mView.findViewById(R.id.btAddBrand)
        floatingActionButton.setOnClickListener {
            controller.onAddBrand()
        }
    }

    fun showList(list: List<Brand>) {
        recyclerView.setHasFixedSize(true)

        val lManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = lManager

        val adapter = BrandsAdapter(list, requireContext(), controller)
        recyclerView.adapter = adapter
    }

    fun showAlertDialogAddBrand() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.add_brand_tittle)
        val viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.input_brand, view as ViewGroup, false)
        val inputName: EditText = viewInflated.findViewById(R.id.inputBrandName)
        val spinner: Spinner = viewInflated.findViewById(R.id.spinner)
        builder.setView(viewInflated)
        builder.setPositiveButton("Add") { dialog, which ->
            val brandName = inputName.text.toString()
            if (brandName.isEmpty()) {
                inputName.error = getString(R.string.brand_is_required)
            } else {
                val country = spinner.selectedItem as String
                controller.addBrand(brandName, country)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun actionPostComplete() {
        val progBar: ProgressBar = mView.findViewById(R.id.progBar)
        progBar.visibility = View.GONE
        val txtError: TextView = mView.findViewById(R.id.txtError)
        floatingActionButton.show()
        if (controller.isEmpty()) {
            txtError.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtError.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun getMyView(): View = mView

    fun getByteArray() : ByteArray? = ba
}
