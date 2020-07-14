package com.nachohseara.beermastersshop.ui.home.editproduc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.LoaderFragment
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.ui.home.HomeActivity
import com.nachohseara.beermastersshop.utils.Permission
import java.io.FileNotFoundException
import java.io.InputStream


class EditProductFragment : LoaderFragment() {
    companion object {
        const val GALLERY_REQUEST = 0
    }
    private lateinit var mView: View
    private val controller = EditProductController(this)

    private lateinit var eName: EditText
    private lateinit var txtNumChar: TextView
    private var colorDefault: ColorStateList? = null
    private lateinit var ePrice: EditText
    private lateinit var spnActive: Spinner

    private lateinit var img: ImageView
    private var imgUri: Uri? = null
    private var bitmap: Bitmap? = null
    private var imgPick = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView =  inflater.inflate(R.layout.fragment_edit_product, container, false)

        var json =  ""
        if (arguments != null) {
            json = requireArguments().getString("prod", "")
            if (bitmap == null) {
                val ba = requireArguments().getByteArray("img")
                if (ba != null && ba.isNotEmpty()) bitmap = BitmapFactory.decodeByteArray(ba, 0, ba.size)
            }
        }

        blockMenu(true)
        loadViews()
        controller.onCreate(json)

        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_product, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save_product) {
            controller.onSave()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun blockMenu(block: Boolean) {
        if (context is HomeActivity) {
            (context as HomeActivity).setMenu(block)
        }
    }

    fun loadViews() {
        spnActive = mView.findViewById(R.id.spinnerProdState)
        ePrice = mView.findViewById(R.id.eProdPrice)
        eName = mView.findViewById(R.id.eProdName)
        txtNumChar = mView.findViewById(R.id.txtNumCharProdName)
        txtNumChar.text = "0/${Product.MAX_LENGTH_PROD_NAME}"
        val prodNameWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val txt = "${s.length}/${Product.MAX_LENGTH_PROD_NAME}"
                txtNumChar.text = txt
                if (s.length > Product.MAX_LENGTH_PROD_NAME) {
                    if (colorDefault == null) {
                        colorDefault = txtNumChar.textColors
                    }
                    txtNumChar.setTextColor(resources.getColor(R.color.review_opinion_error))
                    eName.setTextColor(resources.getColor(R.color.review_opinion_error))
                } else {
                    if (colorDefault != null) {
                        txtNumChar.setTextColor(colorDefault)
                        eName.setTextColor(colorDefault)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        }
        eName.addTextChangedListener(prodNameWatcher)


        img = mView.findViewById(R.id.imgProdPage)

        val imgBt: FrameLayout = mView.findViewById(R.id.frameImgProdPage)
        imgBt.setOnClickListener {
            controller.onSelectImg()
        }

        val btBrand: Button = mView.findViewById(R.id.btBrand)
        btBrand.setOnClickListener {
            controller.onSelectBrand()
        }
    }

    fun infoProduct(prod: Product) {
        if (bitmap != null) {
            if (prod.isEmpty()) {
                updateImg(bitmap!!)
            } else {
                setImg(bitmap!!)
            }
        }

        val btBrand: Button = mView.findViewById(R.id.btBrand)
        btBrand.visibility = if (prod.isEmpty()) View.VISIBLE else View.GONE

        val name = prod.getName()
        if (name.isNotEmpty()) {
            eName.hint = name
            eName.setText(prod.getName(), TextView.BufferType.EDITABLE)
        }
        val price = prod.getPrice()
        if (price != 0) {
            ePrice.hint = prod.getFormattedPrice()
            ePrice.setText(prod.getFormattedPrice(false), TextView.BufferType.EDITABLE)
        }

        loadBrand(prod.getBrand())
        loadCountry(prod.getCountry())

        if (prod.getActive()) {
            spnActive.setSelection(0)
        } else {
            spnActive.setSelection(1)
        }
        loadInfoComplete()
    }

    fun getActive() : String {
        return spnActive.selectedItem as String
    }
    fun getName() : String = eName.text.toString()
    fun errorName() {
        eName.error = "Product Name is required"
    }
    fun getPrice() : String = ePrice.text.toString()
    fun errorPrice() {
        ePrice.error = "Price bad formatted, [0-9]+.[0-9][0-9]"
    }

    private fun loadBrand(brand: String) {
        val txtBrand: TextView = mView.findViewById(R.id.txtProdBrand)
         txtBrand.text = if (brand.isEmpty()) "?" else brand
    }
    private fun loadCountry(country: String) {
        val txtCountry: TextView = mView.findViewById(R.id.txtProdCountry)
       txtCountry.text = if (country.isEmpty()) "?" else country
    }

    fun hideKeyboard() {
        eName.onEditorAction(EditorInfo.IME_ACTION_DONE)
        ePrice.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    override fun actionPostComplete() {
        val progBar: ProgressBar = mView.findViewById(R.id.progBar)
        val contentView: ScrollView = mView.findViewById(R.id.contentView)
        progBar.visibility = View.GONE
        contentView.visibility = View.VISIBLE
    }

    override fun getMyView(): View = mView

    /// Image
    fun getBitmap() : Bitmap = img.drawable.toBitmap()

    fun imgPick() : Boolean = imgPick

    private fun setImg(bm: Bitmap) {
        img.setImageBitmap(bm)
    }

    private fun updateImg(bm: Bitmap) {
        imgPick = true
        setImg(bm)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                try {
                    imgUri = data!!.data
                    val imageStream: InputStream = requireActivity().contentResolver.openInputStream(imgUri!!)!!
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    //val bitmapScaled = Bitmap.createScaledBitmap(selectedImage, 250, 250, false)
                    bitmap = selectedImage
                    updateImg(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    msgToast("Something went wrong")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Permission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    controller.onSelectImg()
                } else {
                    msgSnackBar("Permission denied")
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
