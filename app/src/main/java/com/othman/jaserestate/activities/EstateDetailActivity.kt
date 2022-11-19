package com.othman.jaserestate.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.othman.jaserestate.R
import com.othman.jaserestate.adapters.ImagesAdapter
import com.othman.jaserestate.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_estate_detail.*

class EstateDetailActivity : AppCompatActivity() {
    private var model:HappyPlaceModel? = null
    private lateinit var imagesAdapter :ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estate_detail)

        setSupportActionBar(tbPlaceDetail)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        tbPlaceDetail.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            model = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }

        showEstateDate()

    }

    private fun showEstateDate() {
        if (model != null) {
            supportActionBar?.title = model?.location
            setTextViewString(model?.type, tv_type)
            setTextViewString(model?.location, tv_location)
            setTextViewString(model?.roomNo, tv_roomNo)
            setTextViewString("${model?.area.toString()} متر مربع", tv_area)
            setTextViewString(model?.height, tv_height)
            setTextViewString(model?.directions, tv_directions)
            setTextViewString(model?.frontOrBack, tv_front_or_back)
            setTextViewString(model?.floorHousesNo.toString(), tv_floor_houses_no)
            setTextViewString(model?.furniture, tv_furniture)
            if (model?.furniture == AddEstateActivity.PARTIAL_FURNITURE) {
                tv_furniture.append(" - ${model?.partialFurniture}")
            }
            setTextViewString(model?.furnitureSituation, tv_furniture_situation)
            setTextViewString(model?.situation, tv_situation)
            setTextViewString(model?.legal, tv_legal)
            setTextViewString(model?.positives, tv_positives)
            setTextViewString(model?.negatives, tv_negatives)
            setTextViewString("${model?.price.toString()} ${model?.priceType}", tv_price)
            setTextViewString(model?.owner, tv_owner)
            setTextViewString(model?.ownerTel, tv_owner_tel)
            setTextViewString(model?.loggerType, tv_logger)
            if (!model?.loggerName.isNullOrEmpty()) {
                tv_logger.append(" - ${model?.loggerName}")
            }
            setTextViewString(model?.loggerTel, tv_logger_tel)
            setTextViewString(model?.priority, tv_priority)
            setTextViewString(model?.ownerStandards, tv_owner_standard)
            setTextViewString(model?.rentDuration, tv_rent_duration)
            setTextViewString(model?.loggingDate, tv_date)

            if (model?.offerOrDemand == MainActivity.DEMAND) {
                rvDetailImages.visibility = View.GONE
            } else {
                rvDetailImages.visibility = View.VISIBLE
                setupImagesRecyclerView()
            }
        }
    }

    private fun setTextViewString(value: String?, view:TextView) {
        if (value != "") {
            view.visibility = View.VISIBLE
            view.append(": $value")
        }
    }

    private fun setupImagesRecyclerView() {

        rvDetailImages.layoutManager = LinearLayoutManager(this)
        rvDetailImages.setHasFixedSize(true)

        imagesAdapter = ImagesAdapter(this, model?.images!!)
        rvDetailImages.adapter = imagesAdapter
    }
}