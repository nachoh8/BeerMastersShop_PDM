package com.nachohseara.beermastersshop.ui.home.editproduc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseActivity
import com.nachohseara.beermastersshop.model.db.DBCloud
import com.nachohseara.beermastersshop.model.db.DBProducts
import com.nachohseara.beermastersshop.model.db.IFBConnector
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.model.entity.ProductData
import com.nachohseara.beermastersshop.utils.Permission

class EditProductController(private val myView: EditProductFragment) : IFBConnector {
    private var prod = Product.emptyProduct()
    private lateinit var myAct: BaseActivity
    private val db = DBProducts(this)
    private val cloud = DBCloud(this)

    private var newProd = true
    private var hasChanges = false
    private var endChanges = -1
    private var endUpImg = -1
    private var btPressed = false

    fun onAttach() {
        myAct = myView.getBaseActivity()
    }

    fun onCreate(json: String) {
        if (json.isNotEmpty()) {
            prod = Product.toProduct(json)
            newProd = prod.isEmpty()
        }
        myView.infoProduct(prod)
    }

    fun onNewProduct() : Boolean = newProd

    fun onSave() {
        myView.hideKeyboard()
        if (checkFields() && myAct.onlineOrMsg() && !btPressed) {
            btPressed = true
            myAct.startLoading()
            uploadProduct()
        }
    }

    private fun checkFields() : Boolean {
        if (onNewProduct() && !myView.imgPick()) {
            myView.msgSnackBar("You have to select an image")
            return false
        }
        if (myView.getName().isEmpty()) {
            myView.errorName()
            return false
        }
        if (myView.getName().length > Product.MAX_LENGTH_PROD_NAME) {
            return false
        }

        if (!Product.checkFormattedPrice(myView.getPrice())) {
            myView.errorPrice()
            return false
        }
        if (prod.getBrand().isEmpty()) {
            myView.msgSnackBar("You have to select a Brand")
            return false
        }

        return true
    }

    fun onSelectImg() {
        if (Permission.checkPermissionReadExternalStorage(myAct)) {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            myView.startActivityForResult(photoPickerIntent, EditProductFragment.GALLERY_REQUEST)
        }
    }

    private fun uploadProduct() {
        val newProd = getNewProd()

        if (onNewProduct()) {
            newProd.setActive(false)
            prod = newProd
            db.addProduct(prod)
        } else {
            hasChanges = !prod.sameAs(newProd)
            if (hasChanges) prod = newProd

            if (myView.imgPick()) {
                cloud.uploadImg(myView.getBitmap(), prod.getDocId())
            } else if (hasChanges){
                db.editProduct(prod.getDocId(), prod)
            } else {
                endLoading()
                myView.msgSnackBar("There are no changes")
            }
        }
    }

    private fun updateProductEnd() {
        val endInfo = (hasChanges && endChanges != -1) ||  !hasChanges
        val endImg = (myView.imgPick() && endUpImg != -1) || !myView.imgPick()
        val bothError = endChanges == DBProducts.UP_DATA_FAILURE && endUpImg == DBCloud.UPLOAD_IMG_FAILURE
        val end = endInfo && endImg

        if (end && !bothError) {
            endLoading()
            goTo()
        }
    }

    private fun getNewProd() : Product {
        val strPrice = myView.getPrice()
        val price = if (strPrice.isNotEmpty()) Product.strToPrice(strPrice) else 0
        val data = ProductData(prod.getDocId(), myView.getName(), prod.getImgUrl(), price, prod.getNumReviews(),
            prod.getRating(), prod.getBrandId(), prod.getBrand(), prod.getCountry(), Product.toActive(myView.getActive()))

        return Product(data)
    }

    private fun endLoading() {
        btPressed = false
        myAct.endLoading()
    }

    fun onSelectBrand() {
        val args = Bundle()
        args.putString("prod", Product.toJSON(getNewProd()))

        myView.findNavController().navigate(R.id.action_editProductFragment_to_brandsFragment, args)
    }

    private fun goTo() {
        val args = Bundle()
        args.putString("prodId", prod.getDocId())
        myView.findNavController().navigate(R.id.action_editprod_to_prodpage, args)
    }


    // Database
    override fun onSuccessFB(cod: Int, data: Any?) {
        when (cod) {
            DBCloud.UPLOAD_IMG_OK -> {
                val dataOk = data != null && data is Uri

                if (onNewProduct()) {
                    if (dataOk) {
                        val active = Product.toActive(myView.getActive())
                        if (active) {
                            prod.setActive(active)
                        }
                        prod.setImgUrl(data.toString())
                        db.editProduct(prod.getDocId(), prod)
                    } else {
                        myView.msgSnackBar("Error uploading the image, try it later")
                        endLoading()
                        goTo()
                    }
                } else {
                    if (dataOk) {
                        prod.setImgUrl(data.toString())
                        db.editProduct(prod.getDocId(), prod)
                    } else {
                        endLoading()
                        myView.msgSnackBar(myView.getString(R.string.error_data))
                    }
                    endUpImg = DBCloud.UPLOAD_IMG_OK
                    //updateProductEnd()
                }
            }
            DBProducts.UP_DATA_OK -> {
                if (onNewProduct()) {
                    if (prod.isEmpty()) {
                        prod.setDocId(data as String)
                        cloud.uploadImg(myView.getBitmap(), prod.getDocId())
                    } else {
                        endLoading()
                        goTo()
                    }
                } else {
                    endChanges = DBProducts.UP_DATA_OK
                    endLoading()
                    goTo()
                }
            }
        }
    }

    override fun onFailureFB(cod: Int, error: String) {
        when (cod) {
            DBProducts.UP_DATA_FAILURE -> {
                endLoading()
                myView.msgSnackBar(myView.getString(R.string.error_data))
                /*if (onNewProduct()) {
                    endLoading()
                    myView.msgSnackBar(myView.getString(R.string.error_data))
                } else {
                    myView.msgSnackBar("Error updating info, try it later")
                    endChanges = DBProducts.UP_DATA_FAILURE
                    updateProductEnd()
                }*/
            }
            DBCloud.UPLOAD_IMG_FAILURE, DBCloud.GET_URL_FAILURE -> {
                if (onNewProduct()) {
                    myView.msgSnackBar("Error uploading the image, try it later")
                    endLoading()
                    goTo()
                } else {
                    endLoading()
                    myView.msgSnackBar(myView.getString(R.string.error_data))
                }
            }
        }
    }
}