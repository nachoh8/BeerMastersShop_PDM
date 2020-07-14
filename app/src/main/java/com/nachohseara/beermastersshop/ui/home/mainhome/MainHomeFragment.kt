package com.nachohseara.beermastersshop.ui.home.mainhome

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.model.db.DBProducts
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.ui.home.HomeActivity

class MainHomeFragment : BaseFragment() {
    private lateinit var mView: View
    private val controller = MainHomeController(this)
    private lateinit var rView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_home, container, false)
        rView = mView.findViewById(R.id.recyclerView_home)

        swipeRefresh = mView.findViewById(R.id.refreshProducts)
        swipeRefresh.setOnRefreshListener {
            rView.visibility = View.INVISIBLE
            controller.getProducts()
        }

        var json = ""
        if (arguments != null) {
            json = requireArguments().getString("filter", "")
        }

        when (getLabel()) {
            getString(R.string.n_all) -> controller.onCreate(DBProducts.GETPRODS_ALL, json)
            else -> controller.onCreate(json = json)
        }

        return mView
    }

    override fun onResume() {
        (requireActivity() as HomeActivity).setMenu(false)
        super.onResume()
    }

    fun setFloattingButton(admin: Boolean) {
        val btFloatting: FloatingActionButton = mView.findViewById(R.id.btHomeFloatting)
        if (admin) {
            btFloatting.setOnClickListener {
                controller.onAddProduct()
            }
        } else {
            btFloatting.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (controller.isAdmin()) {
            inflater.inflate(R.menu.home_admin, menu)
        } else {
            inflater.inflate(R.menu.home, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter_products -> {
                controller.onFilter()
                true
            }
            R.id.action_cart -> {
                controller.goToCart()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun showProducts(products: List<Product>) {
        if (products.isNotEmpty()) {
            if (rView.visibility == View.INVISIBLE) rView.visibility = View.VISIBLE
            rView.setHasFixedSize(true)

            val lManager = LinearLayoutManager(requireContext())
            rView.layoutManager = lManager

            val adapter = ProductAdapter(products, requireContext(), controller)
            rView.adapter = adapter
            swipeRefresh.isRefreshing = false
        }
    }

    fun getLabel() : String {
        val dest = findNavController().currentDestination
        return dest?.label?.toString() ?: ""
    }

    override fun getMyView(): View = mView
}