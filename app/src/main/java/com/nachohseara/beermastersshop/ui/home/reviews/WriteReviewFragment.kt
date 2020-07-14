package com.nachohseara.beermastersshop.ui.home.reviews

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import com.nachohseara.beermastersshop.R
import com.nachohseara.beermastersshop.base.BaseFragment
import com.nachohseara.beermastersshop.model.entity.Product
import com.nachohseara.beermastersshop.model.entity.Review


class WriteReviewFragment : BaseFragment() {
    private lateinit var mView: View
    private val controller = WriteReviewController(this)

    private lateinit var ratingBar: RatingBar
    private lateinit var txtOpinion: EditText
    private lateinit var txtNumChar: TextView
    private var colorDefault: ColorStateList? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controller.onAttach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_write_review, container, false)

        val json = if (arguments != null) {
            requireArguments().getString("prod", "")
        } else {
            ""
        }
        val prod = Product.toProduct(json)

        loadViews()
        controller.onCreate(prod)

        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.write_review, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_post_review-> {
                if (checkFields()) controller.onPost()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    fun loadViews() {
        ratingBar = mView.findViewById(R.id.ratingBar)
        txtOpinion = mView.findViewById(R.id.txtOpinion)
        txtNumChar = mView.findViewById(R.id.txtNumChar)
        txtNumChar.text = "0/${Review.MAX_TEXT_LENGTH}"
        val opinionWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val txt = "${s.length}/${Review.MAX_TEXT_LENGTH}"
                txtNumChar.text = txt
                if (s.length > Review.MAX_TEXT_LENGTH) {
                    if (colorDefault == null) {
                        colorDefault = txtNumChar.textColors
                    }
                    txtNumChar.setTextColor(resources.getColor(R.color.review_opinion_error))
                    txtOpinion.setTextColor(resources.getColor(R.color.review_opinion_error))
                } else {
                    if (colorDefault != null) {
                        txtNumChar.setTextColor(colorDefault)
                        txtOpinion.setTextColor(colorDefault)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        }
        txtOpinion.addTextChangedListener(opinionWatcher)
    }

    private fun checkFields() : Boolean {
        val txtNotStars: TextView = mView.findViewById(R.id.txtNotStars)
        if (getRating() < 1f) {
            txtNotStars.visibility = View.VISIBLE
            return false
        } else {
            txtNotStars.visibility = View.GONE
        }
        if (getOpinion().length > Review.MAX_TEXT_LENGTH) {
            return false
        }
        return true
    }

    fun getRating() : Float = ratingBar.rating
    fun getOpinion() : String = txtOpinion.text.toString()

    fun hideKeyboard() {
        txtOpinion.onEditorAction(EditorInfo.IME_ACTION_DONE)
    }

    override fun getMyView(): View = mView
}
