package com.othman.jaserestate.activities

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.othman.jaserestate.R
import com.othman.jaserestate.adapters.ImagesAdapter
import com.othman.jaserestate.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_estate_detail.*
import kotlinx.android.synthetic.main.activity_estate_detail.view.*


class EstateDetailActivity : AppCompatActivity() {
    private var model:HappyPlaceModel? = null
    private lateinit var imagesAdapter :ImagesAdapter
    private var estateSharingText = ""
    private lateinit var defaultImage: String


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
        tbPlaceDetail.tv_share.setOnClickListener {
            shareImage()
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            model = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }

        showEstateDate()


        defaultImage = ContentResolver.SCHEME_ANDROID_RESOURCE +"://" +
                resources.getResourcePackageName(R.drawable.add_screen_image_placeholder) + '/' +
                resources.getResourceTypeName(R.drawable.add_screen_image_placeholder) + '/' +
                resources.getResourceEntryName(R.drawable.add_screen_image_placeholder)

        ll_owner_tel.setOnClickListener {
            call(model?.ownerTel!!)
        }
        ll_logger_tel.setOnClickListener {
            call(model?.loggerTel!!)
        }

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
            when{
                view == tv_type -> {
                    estateSharingText += " منزل لل$value:  \n"
                }
                (view == tv_price) ||
                        (view == tv_owner) ||
                        (view == tv_logger_tel) ||
                        (view == tv_owner_tel) ||
                        (view == tv_date) ||
                        (view == tv_priority) ||
                        (view == tv_negatives) ||
                        (view == tv_logger) -> {
                    null
                }
                else -> {
                    estateSharingText += "- ${view.text}: $value.\n"
                }
            }
            if (view == tv_owner_tel)  {
                ll_owner_tel.visibility = View.VISIBLE
            }
            if (view == tv_logger_tel)  {
                ll_logger_tel.visibility = View.VISIBLE
            }
            view.append(": $value")
        }
    }

    private fun setupImagesRecyclerView() {
        rvDetailImages.layoutManager = LinearLayoutManager(this)
        rvDetailImages.setHasFixedSize(true)
        imagesAdapter = ImagesAdapter(this, model?.images!!)
        rvDetailImages.adapter = imagesAdapter
    }

    private fun shareImage(){
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label",estateSharingText)
        clipboard.setPrimaryClip(clip)
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND_MULTIPLE
        shareIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_TEXT,estateSharingText)
        if (model?.images?.get(0) != Uri.parse(defaultImage)) {
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, model?.images)
        }else{
            shareIntent.type = "text/*"
        }
        startActivity(Intent.createChooser(shareIntent, "مشاركة العقار"))
    }

    private fun call(phoneNumber: String){
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

}