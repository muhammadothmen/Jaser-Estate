package com.othman.jaserestate.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.othman.jaserestate.R
import com.othman.jaserestate.adapters.DialogAdapter
import com.othman.jaserestate.adapters.ImagesAdapter
import com.othman.jaserestate.database.DatabaseHandler
import com.othman.jaserestate.models.HappyPlaceModel
import com.othman.jaserestate.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_add_estate.*
import kotlinx.android.synthetic.main.area_dialog_layout.*
import kotlinx.android.synthetic.main.edit_text_others_dialog_layout.*
import kotlinx.android.synthetic.main.item_images.*
import kotlinx.android.synthetic.main.price_dialog_layout.*
import kotlinx.android.synthetic.main.selcet_items_dialog.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class AddEstateActivity : AppCompatActivity(), View.OnClickListener {

    private var floorHousesNo = 1
    private var cal = Calendar.getInstance()
    private var imagesList = ArrayList<Uri>()
    private var temporaryImageList = ArrayList<Uri>()
    private var toDeleteImageList = ArrayList<Uri>()
    private var mLatitude : Double = 0.0
    private var mLongitude: Double = 0.0
    private var mHappyPlaceDetails :HappyPlaceModel? = null
    private var otherStandard = ""
    private var loggerName = ""
    private var furniture = ""
    private var partialFurnitureItems = ""
    private var area = 50
    private var price = 0.0f
    private var priceType = ""
    private var priceUnit = ""
    private var type = SALE
    private var loggerType = ""
    private var offerOrDemand = 1
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var selectDialog: Dialog
    private lateinit var othersDialog: Dialog
    private lateinit var selectAreaDialog: Dialog
    private lateinit var selectRecycleView: RecyclerView
    private lateinit var heightList: ArrayList<String>
    private lateinit var heightAdapter: DialogAdapter
    private lateinit var roomNoList: ArrayList<String>
    private lateinit var roomNoAdapter: DialogAdapter
    private lateinit var situationList: ArrayList<String>
    private lateinit var situationAdapter: DialogAdapter
    private lateinit var furnitureList: ArrayList<String>
    private lateinit var furnitureAdapter: DialogAdapter
    private lateinit var furnitureSituationList: ArrayList<String>
    private lateinit var furnitureSituationAdapter: DialogAdapter
    private lateinit var frontOrBackList: ArrayList<String>
    private lateinit var frontOrBackAdapter: DialogAdapter
    private lateinit var directionsList: ArrayList<String>
    private lateinit var directionsAdapter: DialogAdapter
    private lateinit var legalList: ArrayList<String>
    private lateinit var legalAdapter: DialogAdapter
    private lateinit var loggerTypeList: ArrayList<String>
    private lateinit var loggerTypeAdapter: DialogAdapter
    private lateinit var priorityList: ArrayList<String>
    private lateinit var priorityAdapter: DialogAdapter
    private lateinit var ownerStandardsList: ArrayList<String>
    private lateinit var ownerStandardsAdapter: DialogAdapter
    private lateinit var rentDurationList: ArrayList<String>
    private lateinit var rentDurationAdapter: DialogAdapter
    private lateinit var salePriceTypeList: ArrayList<String>
    private lateinit var salePriceTypeAdapter: DialogAdapter
    private lateinit var rentPriceTypeList: ArrayList<String>
    private lateinit var rentPriceTypeAdapter: DialogAdapter
    private lateinit var betPriceTypeList: ArrayList<String>
    private lateinit var betPriceTypeAdapter: DialogAdapter
    private lateinit var priceDialog: Dialog
    private lateinit var defaultImage: String
    private lateinit var cameraPhotoPath: String
    private lateinit var cameraPhotoUri: Uri


    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                try {
                    if (result.data?.data != null) {
                        saveGalleryImage(result.data!!.data!!)
                    }else if (result.data?.clipData != null){
                        val count = result.data?.clipData?.itemCount
                        for (i in 0 until count!!) {
                            saveGalleryImage(result.data?.clipData?.getItemAt(i)?.uri!!)
                            if (imagesList.size >= 10){
                                break
                            }
                                Log.e("saved image", "path: ${imagesList[i]}")
                        }
                    }
                    imagesAdapter.notifyDataSetChanged()
                }catch (e: Exception){
                    e.printStackTrace()
                    Toast.makeText(this@AddEstateActivity,
                        "Oops, Failed",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }


    private val openCameraLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK ) {
                resizeAndSaveImage(cameraPhotoPath,cameraPhotoUri)
                imagesAdapter.notifyDataSetChanged()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_estate)

        //set tool bar
        setSupportActionBar(tbAddPlace)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "إضافة عقار"
        }
        tbAddPlace.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //get offerOrDemand form main Activity
        if (intent.hasExtra(MainActivity.OFFER_OR_DEMAND)) {
            offerOrDemand = intent.getIntExtra(MainActivity.OFFER_OR_DEMAND,1)
            if (offerOrDemand == MainActivity.DEMAND){
                et_negatives.visibility = View.GONE
                rvAddImages.visibility = View.GONE
                tv_add_image.visibility = View.GONE
            }else{
                et_negatives.visibility = View.VISIBLE
                rvAddImages.visibility = View.VISIBLE
                tv_add_image.visibility = View.VISIBLE
            }
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlaceDetails =  intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }

        selectDialogInit()

        defaultImage = ContentResolver.SCHEME_ANDROID_RESOURCE +"://" +
                resources.getResourcePackageName(R.drawable.add_screen_image_placeholder) + '/' +
                resources.getResourceTypeName(R.drawable.add_screen_image_placeholder) + '/' +
                resources.getResourceEntryName(R.drawable.add_screen_image_placeholder)
        if (mHappyPlaceDetails == null){
        imagesList.add(Uri.parse(defaultImage))
        }

        //radio button set
        rgTypes.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == R.id.rbSale){
                if (offerOrDemand == MainActivity.OFFER) {
                    type = SALE
                }else{
                    type = BUY
                }
                til_owner_standards.visibility = View.GONE
                til_rent_duration.visibility = View.GONE
                til_legal.visibility = View.VISIBLE
                if (furnitureList.contains(FULL_FURNITURE_ABLE_RENT_WITHOUT)) {
                    furnitureList.remove(FULL_FURNITURE_ABLE_RENT_WITHOUT)
                }
                et_price.text!!.clear()
                et_furniture.text!!.clear()
            }
            if (checkedId == R.id.rbBet){
                type = BET
                til_owner_standards.visibility = View.GONE
                til_rent_duration.visibility = View.GONE
                til_legal.visibility = View.GONE
                if (!furnitureList.contains(FULL_FURNITURE_ABLE_RENT_WITHOUT)) {
                    furnitureList.add(FULL_FURNITURE_ABLE_RENT_WITHOUT)
                }
                et_price.text!!.clear()
            }
            if (checkedId == R.id.rbRent) {
                type = RENT
                til_owner_standards.visibility = View.VISIBLE
                til_rent_duration.visibility = View.VISIBLE
                til_legal.visibility = View.GONE
                if (!furnitureList.contains(FULL_FURNITURE_ABLE_RENT_WITHOUT)) {
                    furnitureList.add(FULL_FURNITURE_ABLE_RENT_WITHOUT)
                }
                et_price.text!!.clear()
            }

        }

        //init date dialog
        dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        updateDateInView()

        //set data on views when edit estate
        if (mHappyPlaceDetails != null ) {
            supportActionBar?.title = "تعديل العقار"
            et_date.setText(mHappyPlaceDetails!!.loggingDate)
            type = mHappyPlaceDetails!!.type!!
            offerOrDemand = mHappyPlaceDetails!!.offerOrDemand
            when(type){
                RENT ->{
                    rbRent.isChecked = true
                    rbSale.isChecked = false
                    rbBet.isChecked = false

                }
                BET ->{
                    rbRent.isChecked = false
                    rbSale.isChecked = false
                    rbBet.isChecked = true

                }
                SALE ->{
                    rbRent.isChecked = false
                    rbSale.isChecked = true
                    rbBet.isChecked = false

                }
            }
            et_location.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude
            area = mHappyPlaceDetails!!.area
            et_area.setText("${area.toString()} متر مربع")
            et_roomNo.setText(mHappyPlaceDetails!!.roomNo)
            et_directions.setText(mHappyPlaceDetails!!.directions)
            et_height.setText(mHappyPlaceDetails!!.height)
            et_front_or_back.setText(mHappyPlaceDetails!!.frontOrBack)
            floorHousesNo = mHappyPlaceDetails!!.floorHousesNo
            et_floor_houses_no.setText(floorHousesNo.toString())
            et_situation.setText(mHappyPlaceDetails!!.situation)
            partialFurnitureItems = mHappyPlaceDetails!!.partialFurniture!!
            furniture = mHappyPlaceDetails!!.furniture!!
            et_furniture.setText("${mHappyPlaceDetails!!.furniture}: $partialFurnitureItems")
            et_furniture_situation.setText(mHappyPlaceDetails!!.furnitureSituation)
            price = mHappyPlaceDetails!!.price
            priceType = mHappyPlaceDetails!!.priceType!!
            et_price.setText("${price.toString()} $priceType")
            et_legal.setText(mHappyPlaceDetails!!.legal)
            et_owner.setText(mHappyPlaceDetails!!.owner)
            et_owner_standards.setText(mHappyPlaceDetails!!.ownerStandards)
            loggerName = mHappyPlaceDetails!!.loggerName!!
            loggerType = mHappyPlaceDetails!!.loggerType!!
            et_logger_type.setText("${mHappyPlaceDetails!!.loggerType}")
            if (loggerName.isNotEmpty()){
                et_logger_type.append(": $loggerName")
            }
            et_owner_tel.setText(mHappyPlaceDetails!!.ownerTel)
            et_logger_tel.setText(mHappyPlaceDetails!!.loggerTel)
            et_priority.setText(mHappyPlaceDetails!!.priority)
            et_rent_duration.setText(mHappyPlaceDetails!!.rentDuration)
            et_positives.setText(mHappyPlaceDetails!!.positives)
            et_negatives.setText(mHappyPlaceDetails!!.negatives)
            imagesList = mHappyPlaceDetails!!.images!!
            Log.e("hplist","${imagesList}")

            btn_save.text = "تعديل"
        }

        if(offerOrDemand == MainActivity.DEMAND){
            type = BUY
            rbSale.text = resources.getString(R.string.rb_text_buy)
        }else{
            rbSale.text = resources.getString(R.string.rb_text_sale)
            type = SALE
        }

        setupImagesRecyclerView()

        activateListenerForViews()


    }

    private fun activateListenerForViews() {
        et_date.setOnClickListener(this)
        tv_add_image.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        et_location.setOnClickListener(this)
        et_height.setOnClickListener(this)
        et_roomNo.setOnClickListener(this)
        et_directions.setOnClickListener(this)
        et_front_or_back.setOnClickListener(this)
        et_furniture.setOnClickListener(this)
        et_furniture_situation.setOnClickListener(this)
        et_situation.setOnClickListener(this)
        et_legal.setOnClickListener(this)
        et_area.setOnClickListener(this)
        et_logger_type.setOnClickListener(this)
        et_priority.setOnClickListener(this)
        et_price.setOnClickListener(this)
        et_owner_standards.setOnClickListener(this)
        et_rent_duration.setOnClickListener(this)
        et_floor_houses_no.setOnClickListener(this)
    }

    private fun selectDialogInit() {
        selectDialog = Dialog(this, R.style.Theme_Dialog)
        selectDialog.setContentView(R.layout.selcet_items_dialog)
        othersDialog = Dialog(this, R.style.Theme_Dialog)
        othersDialog.setContentView(R.layout.edit_text_others_dialog_layout)
        selectAreaDialog = Dialog(this, R.style.Theme_Dialog_price)
        selectAreaDialog.setContentView(R.layout.area_dialog_layout)
        priceDialog = Dialog(this, R.style.Theme_Dialog_price)
        priceDialog.setContentView(R.layout.price_dialog_layout)
        selectRecycleView = selectDialog.rvDialog

        heightList = arrayListOf(SECOND_UNDER_GROUND, FIRST_UNDER_GROUND, SAME_GROUND,
            HANGING, FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SURFACE, GARDEN_UNDER_GROUND,
            GARDEN_SAME_GROUND, GARDEN_UPPER_GROUND)
        heightAdapter = DialogAdapter(this,heightList)

        roomNoList = arrayListOf(ONE_ROOM, ONE_R00M_WITH_SALON,
            TWO_ROOM_WITH_DISTRIBUTOR, TWO_R00M_WITH_SALON,
            THREE_ROOM_WITH_DISTRIBUTOR, THREE_R00M_WITH_SALON,
            FOUR_ROOM_WITH_DISTRIBUTOR, FOUR_R00M_WITH_SALON,
            FIFE_ROOM_WITH_DISTRIBUTOR , FIFE_R00M_WITH_SALON,
            SIX_ROOM_WITH_DISTRIBUTOR, SIX_R00M_WITH_SALON,
            SEVEN_ROOM_WITH_DISTRIBUTOR, SEVEN_R00M_WITH_SALON,
            EIGHT_ROOM_WITH_DISTRIBUTOR, EIGHT_ROOM_WITH_SALON,
            NINE_ROOM_WITH_DISTRIBUTOR, NINE_ROOM_WITH_SALON,
            OTHER_ROOM_N0)
        roomNoAdapter = DialogAdapter(this,roomNoList)

        situationList = arrayListOf(SUPER_DELUXE, DELUXE, VERY_GOOD, GOOD, MEDIUM,
            UNDER_MEDIUM, FULL_MAINTENANCE)
        situationAdapter = DialogAdapter(this,situationList)

        furnitureList = arrayListOf(FULL_FURNITURE, PARTIAL_FURNITURE, NO_FURNITURE)
        furnitureAdapter = DialogAdapter(this,furnitureList)

        furnitureSituationList = arrayListOf(SUPER_DELUXE, DELUXE, VERY_GOOD, GOOD, MEDIUM, UNDER_MEDIUM)
        furnitureSituationAdapter = DialogAdapter(this,furnitureSituationList)

        frontOrBackList = arrayListOf(FRONT, BACK)
        frontOrBackAdapter = DialogAdapter(this,frontOrBackList)

        directionsList = arrayListOf(NORTH, SOUTH, WEST, EAST,
            CORNER_NORTH_EAST, CORNER_NORTH_WEST, CORNER_SOUTH_EAST, CORNER_SOUTH_WEST,
            PADDING_NORTH_SOUTH, PADDING_EAST_WEST,
            HALF_NORTH_EAST_SOUTH, HALF_NORTH_WEST_SOUTH, HALF_EAST_NORTH_WEST, HALF_EAST_SOUTH_WEST,
            FULL)
        directionsAdapter = DialogAdapter(this, directionsList)

        legalList = arrayListOf(CONTRACT, COURT, GREEN_STAMP)
        legalAdapter = DialogAdapter(this, legalList)

        loggerTypeList = arrayListOf(OWNER, OFFICE, MOBILE_OFFICE)
        loggerTypeAdapter = DialogAdapter(this, loggerTypeList)

        priorityList = arrayListOf(IMPORTANT_URGENT, NOT_IMPORTANT_URGENT, IMPORTANT_NOT_URGENT,
            NOT_IMPORTANT_NOT_URGENT)
        priorityAdapter = DialogAdapter(this, priorityList)

        ownerStandardsList = arrayListOf(NULL, GROOMS_ONLY, FEMALE_STUDENTS, CHILDREN_LESS_FAMILY,
            SMALL_FAMILY, OTHER_STANDARDS)
        ownerStandardsAdapter = DialogAdapter(this, ownerStandardsList)

        rentDurationList = arrayListOf(LESS_YEAR, ONE_YEAR, TWO_FIVE_YEAR, FOR_RENT)
        rentDurationAdapter = DialogAdapter(this, rentDurationList)

        salePriceTypeList = arrayListOf(FINAL_PRICE, LITTLE_ARGUE_PRICE, ARGUE_PRICE)
        salePriceTypeAdapter = DialogAdapter(this, salePriceTypeList)

        rentPriceTypeList = arrayListOf(ANNUAL_RENT, HALF_ANNUAL_RENT, MONTHLY_RENT)
        rentPriceTypeAdapter = DialogAdapter(this, rentPriceTypeList)

        betPriceTypeList = arrayListOf(ONE_YEAR_BET, TWO_YEAR_BET, THREE_YEAR_BET, FOUR_YEAR_BET, FIVE_YEAR_BET)
        betPriceTypeAdapter = DialogAdapter(this, betPriceTypeList)
    }

    private fun setupImagesRecyclerView() {

        rvAddImages.layoutManager = LinearLayoutManager(this)
        rvAddImages.setHasFixedSize(true)

        imagesAdapter = ImagesAdapter(this, imagesList)
        rvAddImages.adapter = imagesAdapter

        val deleteSwipeToDelete = object: SwipeToDeleteCallback(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    if (imagesList[0] != Uri.parse(defaultImage)){
                        if (mHappyPlaceDetails == null) {
                            deleteFile(imagesList[viewHolder.adapterPosition])
                        }else{
                            toDeleteImageList.add(imagesList[viewHolder.adapterPosition])
                        }
                        temporaryImageList.remove(imagesList[viewHolder.adapterPosition])
                        imagesList.removeAt(viewHolder.adapterPosition)
                        if (imagesList.size == 0){
                            imagesList.add(Uri.parse(defaultImage))
                        }
                    }
                    imagesAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeToDelete)
        deleteItemTouchHelper.attachToRecyclerView(rvAddImages)


    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddEstateActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image -> {
                if (imagesList.size < 10){
                val pictureDialog = AlertDialog.Builder(this)
                //pictureDialog.setTitle("اختر الطريقة")
                val pictureDialogItems = arrayOf("اختر من المعرض" ,"التقط عن طريق الكاميرا")
                pictureDialog.setItems(pictureDialogItems){
                    dialog, which ->
                    when(which){
                        0 ->{chooseImageFromGallery()}
                        1 ->{takePhotoFromCamera()}
                    }
                }
                val alertDialog : AlertDialog = pictureDialog.create()
                alertDialog.setCancelable(true)
                alertDialog.show()
            }else{
                Toast.makeText(this@AddEstateActivity,
                    "لا يمكن إضافة أكثر من عشرة صور",
                    Toast.LENGTH_SHORT).show()
            }
            }
            R.id.btn_save -> {
                when {
                    et_location.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "رجاء أدخل العنوان", Toast.LENGTH_SHORT).show()
                    }
                    et_roomNo.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "رجاء أدخل عدد الغرف", Toast.LENGTH_SHORT).show()
                    }
                    et_height.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "رجاء أدخل الطابق", Toast.LENGTH_SHORT).show()
                    }
                    et_furniture.text.isNullOrEmpty()  -> {
                        Toast.makeText(this, "رجاء أدخل الفرش", Toast.LENGTH_SHORT).show()
                    }
                    et_owner_tel.text.isNullOrEmpty() && et_logger_tel.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "رجاء أدخل رقم الهاتف", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val happyPlaceModel = HappyPlaceModel(
                            if (mHappyPlaceDetails == null) 0 else mHappyPlaceDetails!!.id,
                            et_date.text.toString(),
                            offerOrDemand,
                            type,
                            et_location.text.toString(),
                            mLatitude,
                            mLongitude,
                            area,
                            et_roomNo.text.toString(),
                            et_directions.text.toString(),
                            et_height.text.toString(),
                            et_front_or_back.text.toString(),
                            floorHousesNo,
                            et_situation.text.toString(),
                            furniture,
                            partialFurnitureItems,
                            et_furniture_situation.text.toString(),
                            price,
                            priceType,
                            et_legal.text.toString(),
                            et_owner.text.toString(),
                            et_owner_standards.text.toString(),
                            loggerName,
                            loggerType,
                            et_owner_tel.text.toString(),
                            et_logger_tel.text.toString(),
                            et_priority.text.toString(),
                            et_rent_duration.text.toString(),
                            et_positives.text.toString(),
                            et_negatives.text.toString(),
                            MainActivity.NOT_DONE,
                            "",
                            "",
                            "",
                            "",
                            imagesList
                            )
                        val dbHandler = DatabaseHandler(this)
                        if (mHappyPlaceDetails == null){
                        val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                        if (addHappyPlace > 0){
                            setResult(RESULT_OK)
                            finish()
                        }
                        }else{
                            for (image in toDeleteImageList){
                                deleteFile(image)
                            }
                            val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)
                            if (updateHappyPlace > 0){
                                setResult(RESULT_OK)
                                finish()
                            }
                        }

                    }
                }
            }
            R.id.et_height -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_height)
                selectRecycleView.adapter = heightAdapter
                heightAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_height.setText(heightList[position])
                        selectDialog.dismiss()
                    }
                })
                selectDialog.show()
            }
            R.id.et_roomNo -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_roomNo)
                selectRecycleView.adapter = roomNoAdapter
                roomNoAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        if (roomNoList[position] == OTHER_ROOM_N0){
                            othersDialog.tv_other_dialog_title.text =
                                resources.getString(R.string.other_dialog_title_room_no)
                            othersDialog.tv_other_dialog_cancel.setOnClickListener {
                                othersDialog.dismiss()
                                et_roomNo.text?.clear()
                            }
                            othersDialog.tv_other_dialog_ok.setOnClickListener {
                                if (othersDialog.et_other_dialog.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@AddEstateActivity,
                                        "رجاءً أدخل عدد الغرف",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    et_roomNo.text = othersDialog.et_other_dialog.text
                                    othersDialog.dismiss()
                                }
                            }
                            othersDialog.et_other_dialog.text?.clear()
                            othersDialog.show()
                        }else {
                            et_roomNo.setText(roomNoList[position])
                        }
                        selectDialog.dismiss()
                    }
                })
                selectDialog.show()
            }
            R.id.et_situation -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_situation)
                selectRecycleView.adapter = situationAdapter
                situationAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_situation.setText(situationList[position])
                        selectDialog.dismiss()
                    }
                })
                selectDialog.show()
            }
            R.id.et_front_or_back -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_front_or_back)
                selectRecycleView.adapter = frontOrBackAdapter
                frontOrBackAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_front_or_back.setText(frontOrBackList[position])
                        selectDialog.dismiss()
                    }
                })
                selectDialog.show()
            }
            R.id.et_furniture -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_furniture)
                selectRecycleView.adapter = furnitureAdapter
                furnitureAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        furniture = furnitureList[position]
                        et_furniture.setText(furnitureList[position])
                        if (furniture == NO_FURNITURE){
                            til_furniture_situation.visibility = View.GONE
                        }else{
                            til_furniture_situation.visibility = View.VISIBLE
                        }
                        selectDialog.dismiss()
                        if (furnitureList[position] == PARTIAL_FURNITURE) {
                            othersDialog.tv_other_dialog_title.text =
                                resources.getString(R.string.other_dialog_title_furniture)
                            othersDialog.tv_other_dialog_cancel.setOnClickListener {
                                othersDialog.dismiss()
                                et_furniture.text?.clear()
                            }
                            othersDialog.tv_other_dialog_ok.setOnClickListener {
                                if (othersDialog.et_other_dialog.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@AddEstateActivity,
                                        "رجاءً أدخل الفرش",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    partialFurnitureItems = othersDialog.et_other_dialog.text.toString()
                                    et_furniture.append(": $partialFurnitureItems")
                                    othersDialog.dismiss()
                                }
                            }
                            othersDialog.et_other_dialog.setText(partialFurnitureItems)
                            othersDialog.show()
                        }
                    }
                })
                selectDialog.show()
            }
            R.id.et_furniture_situation -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_furniture_situation)
                selectRecycleView.adapter = furnitureSituationAdapter
                furnitureSituationAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_furniture_situation.setText(furnitureSituationList[position])
                        selectDialog.dismiss()

                    }
                })
                selectDialog.show()
            }
            R.id.et_legal -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_legal)
                selectRecycleView.adapter = legalAdapter
                legalAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_legal.setText(legalList[position])
                        selectDialog.dismiss()
                    }
                })
                selectDialog.show()
            }
            R.id.et_directions -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_directions)
                selectRecycleView.adapter = directionsAdapter
                directionsAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_directions.setText(directionsList[position])
                        selectDialog.dismiss()
                    }
                })
                selectDialog.show()
            }
            R.id.et_logger_type -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_logger)
                selectRecycleView.adapter = loggerTypeAdapter
                loggerTypeAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        loggerType = loggerTypeList[position]
                        et_logger_type.setText(loggerTypeList[position])
                        selectDialog.dismiss()
                        if (loggerTypeList[position] != OWNER) {
                            othersDialog.tv_other_dialog_title.text =
                                resources.getString(R.string.other_dialog_title_name)
                            othersDialog.tv_other_dialog_cancel.setOnClickListener {
                                othersDialog.dismiss()
                                et_logger_type.text?.clear()
                            }
                            othersDialog.tv_other_dialog_ok.setOnClickListener {
                                if (othersDialog.et_other_dialog.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@AddEstateActivity,
                                        "رجاءً أدخل الإسم",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    loggerName = othersDialog.et_other_dialog.text.toString()

                                    et_logger_type.append(": $loggerName")
                                    othersDialog.dismiss()
                                }
                            }
                            othersDialog.et_other_dialog.setText(loggerName)
                            othersDialog.show()
                        }else{
                            loggerName = ""
                        }

                        }

                })
                selectDialog.show()
            }
            R.id.et_priority -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_priority)
                selectRecycleView.adapter = priorityAdapter
                priorityAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_priority.setText(priorityList[position])
                        selectDialog.dismiss()
                    }
                })
                selectDialog.show()
            }
            R.id.et_owner_standards -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_owner_standards)
                selectRecycleView.adapter = ownerStandardsAdapter
                ownerStandardsAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_owner_standards.setText(ownerStandardsList[position])
                        selectDialog.dismiss()
                        if (ownerStandardsList[position] == OTHER_STANDARDS) {
                            othersDialog.tv_other_dialog_title.text =
                                resources.getString(R.string.other_dialog_title_standard)
                            othersDialog.tv_other_dialog_cancel.setOnClickListener {
                                othersDialog.dismiss()
                                et_owner_standards.text?.clear()
                            }
                            othersDialog.tv_other_dialog_ok.setOnClickListener {
                                if (othersDialog.et_other_dialog.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@AddEstateActivity,
                                        "رجاءً أدخل المعيار",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    otherStandard = othersDialog.et_other_dialog.text.toString()
                                    et_owner_standards.append(": $otherStandard")
                                    othersDialog.dismiss()
                                }
                            }
                            othersDialog.et_other_dialog.setText(otherStandard)
                            othersDialog.show()
                        }
                    }
                })
                selectDialog.show()
            }
            R.id.et_rent_duration -> {
                selectDialog.tv_dialog_title.text = resources.getString(R.string.edit_text_hint_rent_duration)
                selectRecycleView.adapter = rentDurationAdapter
                rentDurationAdapter.setOnClickListener(object: DialogAdapter.OnClickListener{
                    override fun onClick(position: Int) {
                        et_rent_duration.setText(rentDurationList[position])
                        selectDialog.dismiss()
                    }
                })
                selectDialog.show()
            }
            R.id.et_price -> {
                var priceTypesList = ArrayList<String>()
                val priceUnitsList = arrayListOf(THOUSANDS_POUNDS, MILLION_POUNDS, BILLION_POUNDS, DOLLARS, THOUSANDS_DOLLARS)
                when(type){
                    SALE -> {
                        priceTypesList = arrayListOf(FINAL_PRICE, LITTLE_ARGUE_PRICE, ARGUE_PRICE)
                    }
                    BET -> {
                        priceTypesList = arrayListOf(ONE_YEAR_BET, TWO_YEAR_BET, THREE_YEAR_BET, FOUR_YEAR_BET, FIVE_YEAR_BET)
                    }
                    RENT-> {
                        priceTypesList = arrayListOf(ANNUAL_RENT, HALF_ANNUAL_RENT, MONTHLY_RENT)
                    }
                }
                var factor = 5
                var numberFormat = "%.3f"
                val unitsSpinnerAdapter = ArrayAdapter(this@AddEstateActivity,
                    android.R.layout.simple_spinner_item, priceUnitsList)
                val typesSpinnerAdapter = ArrayAdapter(this@AddEstateActivity,
                    android.R.layout.simple_spinner_item, priceTypesList)
                priceDialog.sp_unit_price_dialog.adapter = unitsSpinnerAdapter
                priceDialog.sp_type_price_dialog.adapter = typesSpinnerAdapter
                priceDialog.sb_price_dialog.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                    price = progress*5f/factor
                                    priceDialog.tv_dialog_price_value.text = String.format(numberFormat, price)
                                }
                                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                                }
                                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                }
                            })
                sliderButtonListenersSet()
                priceDialog.sp_unit_price_dialog.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        priceUnit = priceUnitsList[position]
                        when (priceUnitsList[position]){
                            THOUSANDS_POUNDS -> {
                                priceDialog.sb_price_dialog.max = 199
                                factor=1
                                numberFormat = "%.0f"
                            }
                            BILLION_POUNDS -> {
                                priceDialog.sb_price_dialog.max = 2000
                                factor=100
                                numberFormat = "%.3f"

                            }
                            MILLION_POUNDS -> {
                                priceDialog.sb_price_dialog.max = 19999
                                factor=100
                                numberFormat = "%.3f"

                            }
                            THOUSANDS_DOLLARS -> {
                                priceDialog.sb_price_dialog.max = 19999
                                factor=100
                                numberFormat = "%.3f"

                            }
                            DOLLARS -> {
                                priceDialog.sb_price_dialog.max = 199
                                factor=1
                                numberFormat = "%.0f"
                            }
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                    }
                priceDialog.sp_type_price_dialog.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        priceType = priceTypesList[position]
                    }
                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
                priceDialog.tv_price_dialog_cancel.setOnClickListener {
                    et_price.setText("")
                    priceDialog.dismiss()
                }
                priceDialog.tv_price_dialog_ok.setOnClickListener {
                    priceType = "$priceUnit - $priceType"
                    et_price.setText("$price $priceType")
                    priceDialog.dismiss()
                }
                priceDialog.show()
            }
            R.id.et_area -> {
                selectAreaDialog.tv_area_dialog_title.text = resources.getString(R.string.dialog_enter_area)
                selectAreaDialog.sb_area_dialog.max = 100
                selectAreaDialog.sb_area_dialog.progress = area/10
                selectAreaDialog.tv_dialog_area_value.text = area.toString()
                selectAreaDialog.show()
                selectAreaDialog.sb_area_dialog.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        selectAreaDialog.tv_dialog_area_value.text = (progress*10).toString()
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        area = seekBar?.progress!!*10
                    }
                })
                selectAreaDialog.tv_area_dialog_cancel.setOnClickListener {
                    selectAreaDialog.dismiss()
                }
                selectAreaDialog.tv_area_dialog_ok.setOnClickListener {
                    val unit = resources.getString(R.string.tv_text_area_unit)
                    et_area.setText("$area $unit")
                    selectAreaDialog.dismiss()
                }
            }
            R.id.et_floor_houses_no -> {
                selectAreaDialog.tv_area_dialog_title.text = resources.getString(R.string.dialog_enter_floor_houses_number)
                selectAreaDialog.sb_area_dialog.max = 4
                selectAreaDialog.sb_area_dialog.progress = floorHousesNo
                selectAreaDialog.tv_dialog_area_value.text = floorHousesNo.toString()
                selectAreaDialog.show()
                selectAreaDialog.sb_area_dialog.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        selectAreaDialog.tv_dialog_area_value.text = (progress).toString()
                    }
                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        floorHousesNo = seekBar!!.progress
                    }
                })
                selectAreaDialog.tv_area_dialog_cancel.setOnClickListener {
                    selectAreaDialog.dismiss()
                }
                selectAreaDialog.tv_area_dialog_ok.setOnClickListener {
                    et_floor_houses_no.setText(floorHousesNo.toString())
                    selectAreaDialog.dismiss()
                }
            }

        }
    }

    private fun sliderButtonListenersSet() {
        var isPlusPressed = false
        var isMinusPressed = false
        priceDialog.tv_price_dialog_plus.setOnClickListener {
            priceDialog.sb_price_dialog.progress += 1
        }
        priceDialog.tv_price_dialog_minus.setOnClickListener {
            priceDialog.sb_price_dialog.progress -= 1
        }


        priceDialog.tv_price_dialog_plus.setOnLongClickListener {
            val handler = Handler(Looper.myLooper()!!)
            val runnable: Runnable = object : Runnable {
                override fun run() {
                    handler.removeCallbacks(this)
                    if (isPlusPressed) {
                        priceDialog.sb_price_dialog.progress += 1
                        handler.postDelayed(this, 50)
                    }
                }
            }
            handler.postDelayed(runnable, 0)
            true
        }

        priceDialog.tv_price_dialog_minus.setOnLongClickListener {
            val handler = Handler(Looper.myLooper()!!)
            val runnable: Runnable = object : Runnable {
                override fun run() {
                    handler.removeCallbacks(this)
                    if (isMinusPressed) {
                        priceDialog.sb_price_dialog.progress -= 1
                        handler.postDelayed(this, 50)
                    }
                }
            }
            handler.postDelayed(runnable, 0)
            true
        }

        priceDialog.tv_price_dialog_plus.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    isPlusPressed = true
                }
                MotionEvent.ACTION_UP -> {
                    isPlusPressed = false
                }
            }

            false
        }

        priceDialog.tv_price_dialog_minus.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    isMinusPressed = true
                }
                MotionEvent.ACTION_UP -> {
                    isMinusPressed = false
                }
            }
            false
        }
    }

    private fun takePhotoFromCamera() {
        Dexter.withContext(this).withPermission(
            android.Manifest.permission.CAMERA).withListener(
            object : PermissionListener{
                override fun onPermissionGranted(report: PermissionGrantedResponse?) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    val file = createImageFile()
                    cameraPhotoPath = file.absolutePath
                    cameraPhotoUri = createAccessibleUriForFile(file)
                    cameraPhotoUri.let {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,it)
                        openCameraLauncher.launch(intent)
                    }

                }

                override fun onPermissionDenied(report: PermissionDeniedResponse?) {
                    Toast.makeText(this@AddEstateActivity,
                        "Oops, you have just denied the permissions",
                        Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissons: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }
            ).onSameThread().check()
    }

    private fun chooseImageFromGallery() {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val pickIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    pickIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    pickIntent.type = "image/*"
                    openGalleryLauncher.launch(pickIntent)
                }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissons: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermissions()
            }
    }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss()
            }.show()
    }

    private fun updateDateInView() {
        val myFormat = "yyyy.MM.dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        et_date.setText(sdf.format(cal.time).toString())
    }

    private fun copyGalleryImageToAppData(context: Context, pathFrom: Uri, pathTo: Uri?) {
        context.contentResolver.openInputStream(pathFrom).use { inputStream: InputStream? ->
            if (pathTo == null || inputStream == null) return
            context.contentResolver.openOutputStream(pathTo).use { out ->
                if (out == null) return
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
        }
    }

    fun deleteFile(uri: Uri){
        val deleted = contentResolver.delete(uri, null, null)
        Log.e("Jasser",deleted.toString())
    }

    private fun resizeAndSaveImage(photoPath: String, uriToDelete: Uri): Uri? {
        // Get the dimensions of the View
        // val targetW: Int = iv_image.width
        // val targetH: Int = iv_image.height
        var scaledBitmap:Bitmap? = null
        val bmOptions = BitmapFactory.Options()
        bmOptions .apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            scaledBitmap = BitmapFactory.decodeFile(photoPath, bmOptions)
            val photoW: Int = outWidth
            val photoH: Int = outHeight
            val scale = Math.max(outHeight/1000,outWidth/1000)
            // Determine how much to scale down the image
            // val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))
            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scale
        }
        val exif =  ExifInterface(photoPath)
        val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        Log.d("Jasser", "Exif: $orientation");
        val matrix = Matrix()
        when (orientation) {
            6 -> {
                matrix.postRotate(90f)
                Log.d("Jasser", "Exif: $orientation")
            }
            3->  {
                matrix.postRotate(180f)
                Log.d("Jasser", "Exif: $orientation")
            }
            8-> {
                matrix.postRotate(270f)
                Log.d("Jasser", "Exif: $orientation")
            }
        }

        return BitmapFactory.decodeFile(photoPath, bmOptions)?.let { bitmap ->
            try {
                val file = createImageFile()
                val stream: OutputStream = FileOutputStream(file)
                scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.width, bitmap.height, matrix, true)
                scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
                createAccessibleUriForFile(file).apply {
                    addImageToArray(this)
                    deleteFile(uriToDelete)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Uri.parse("")
            }
        }
    }

    private fun saveGalleryImage(sourceUri: Uri){
        val file = createImageFile()
        val photoPath = file.absolutePath
        val photoUri = createAccessibleUriForFile(file)
        copyGalleryImageToAppData(this@AddEstateActivity,sourceUri,photoUri)
        resizeAndSaveImage(photoPath,photoUri)
    }

    private fun addImageToArray(uri: Uri){
        if (imagesList[0].toString() == defaultImage) {
            imagesList.remove(Uri.parse(defaultImage))
        }
        imagesList.add(uri)
        temporaryImageList.add(uri)
    }

    private fun createAccessibleUriForFile(file: File): Uri {
        return file.let {
            FileProvider.getUriForFile(
                this@AddEstateActivity,
                "com.othman.jaserestate.fileProvider",
                it
            )
        }


    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss_SSS",Locale.ENGLISH).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        return File(storageDir + File.separator +
                "Jasser_Estate_" + timeStamp + ".jpg")
    }

    override fun onBackPressed() {
        super.onBackPressed()
            for (image in temporaryImageList) {
                deleteFile(image)
            }

    }



    //object for constants
    companion object{
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
        private const val IS_DONE = 1
        private const val IS_NOT_DONE = 0
        //types
        internal const val SALE = "بيع"
        internal const val RENT = "آجار"
        internal const val BET = "رهنية"
        internal const val BUY = "شراء"
        //height list
        internal const val SECOND_UNDER_GROUND = "ثاني تحت الأرض"
        internal const val FIRST_UNDER_GROUND = "أول تحت الأرض"
        internal const val SAME_GROUND = "أرضي"
        internal const val HANGING = "معلق"
        internal const val FIRST = "أول"
        internal const val SECOND = "ثاني"
        internal const val THIRD = "ثالث"
        internal const val FOURTH = "رابع"
        internal const val FIFTH = "خامس"
        internal const val SIXTH = "سادس"
        internal const val SURFACE = "سطوح"
        internal const val GARDEN_UNDER_GROUND = "حديقة نزول"
        internal const val GARDEN_UPPER_GROUND = "حديقة فوق الأرض"
        internal const val GARDEN_SAME_GROUND = "حديقة أرضي"
        //roomNo list
        internal const val ONE_ROOM = "غرفة واحدة"
        internal const val ONE_R00M_WITH_SALON = "غرفة وصالون"
        internal const val TWO_ROOM_WITH_DISTRIBUTOR  = "غرفتين وموزع"
        internal const val TWO_R00M_WITH_SALON = "غرفتين وصالون"
        internal const val THREE_ROOM_WITH_DISTRIBUTOR  = "ثلاث غرف وموزع"
        internal const val THREE_R00M_WITH_SALON = "ثلاث غرف وصالون"
        internal const val FOUR_ROOM_WITH_DISTRIBUTOR  = "أربع غرف وموزع"
        internal const val FOUR_R00M_WITH_SALON = "أربع غرف وصالون"
        internal const val FIFE_ROOM_WITH_DISTRIBUTOR  = "خمس غرف وموزع"
        internal const val FIFE_R00M_WITH_SALON = "خمس غرف وصالون"
        internal const val SIX_ROOM_WITH_DISTRIBUTOR  = "ست غرف وموزع"
        internal const val SIX_R00M_WITH_SALON = "ست غرف وصالون"
        internal const val SEVEN_ROOM_WITH_DISTRIBUTOR  = "سبع غرف وموزع"
        internal const val SEVEN_R00M_WITH_SALON = "سبع غرف وصالون"
        internal const val EIGHT_ROOM_WITH_DISTRIBUTOR  = "ثمان غرف وموزع"
        internal const val EIGHT_ROOM_WITH_SALON  = "ثمان غرف وصالون"
        internal const val NINE_ROOM_WITH_DISTRIBUTOR  = "تسع غرف وموزع"
        internal const val NINE_ROOM_WITH_SALON  = "تسع غرف وصالون"
        internal const val OTHER_ROOM_N0 = "أخرى"
        //situation and furniture list
        internal const val SUPER_DELUXE  = "سوبر ديلوكس"
        internal const val DELUXE  = "ديلوكس"
        internal const val VERY_GOOD  = "جيد جداً"
        internal const val GOOD  = "جيد"
        internal const val MEDIUM  = "وسط"
        internal const val UNDER_MEDIUM  = "دون الوسط"
        internal const val FULL_MAINTENANCE  = "صيانة كاملة"
        internal const val NO_FURNITURE  = "غير مفروش"
        internal const val FULL_FURNITURE  = "فرش كامل"
        internal const val PARTIAL_FURNITURE  = "فرش جزئي"
        internal const val FULL_FURNITURE_ABLE_RENT_WITHOUT  = "فرش كامل مع إمكانية الآجار بدونه"
        internal const val PARTIAL_FURNITURE_ABLE_RENT_WITHOUT  = "فرش جزئي مع إمكانية الآجار بدونه"


        //front and back list
        private const val FRONT  = "أمامي"
        private const val BACK  = "خلفي"
        //directions list
        internal const val FULL  = "بلاطة كاملة: الاتجاهات الأربعة"
        internal const val HALF_EAST_NORTH_WEST  = "نصف بلاطة: شرقي - شمالي - غربي"
        internal const val HALF_EAST_SOUTH_WEST  = "نصف بلاطة: شرقي - قبلي - غربي"
        internal const val HALF_NORTH_WEST_SOUTH  = "نصف بلاطة: شمالي - غربي - قبلي"
        internal const val HALF_NORTH_EAST_SOUTH  = "نصف بلاطة: شمالي - شرقي - قبلي"
        internal const val CORNER_NORTH_EAST  = "زاوية: شمالي - شرقي"
        internal const val CORNER_NORTH_WEST  = "زاوية: شمالي - غربي"
        internal const val CORNER_SOUTH_EAST  = "زاوية: قبلي - شرقي"
        internal const val CORNER_SOUTH_WEST  = "زاوية: قبلي - غربي"
        internal const val PADDING_EAST_WEST  = "حشوة: غربي - شرقي"
        internal const val PADDING_NORTH_SOUTH  = "حشوة: شمالي - قبلي"
        internal const val NORTH  = "شمالي"
        internal const val SOUTH  = "قبلي"
        internal const val WEST  = "غربي"
        internal const val EAST  = "شرقي"
        //legal list
        internal const val CONTRACT  = "عقد"
        internal const val  COURT = "حكم محكمة"
        internal const val GREEN_STAMP  = "طابو أخضر"
        //is direct list
        private const val OWNER  = "المالك"
        private const val OFFICE  = "مكتب"
        private const val MOBILE_OFFICE  = "مكتب جوال"

        //priority
        internal const val IMPORTANT_URGENT  = "مستعجل وهام"
        internal const val IMPORTANT_NOT_URGENT  = "غير مستعجل وهام"
        internal const val NOT_IMPORTANT_URGENT  = "مستعجل وغير هام"
        internal const val NOT_IMPORTANT_NOT_URGENT  = "غير مستعجل وغير هام"
        //owner standards list
        private const val NULL  = "لا يوجد"
        private const val GROOMS_ONLY  = "عرسان حصراً"
        private const val FEMALE_STUDENTS  = "طالبات حصراً"
        private const val SMALL_FAMILY  = "عائلة صغيرة"
        private const val CHILDREN_LESS_FAMILY  = "عائلة بدون أطفال"
        private const val OTHER_STANDARDS  = "معايير أخرى"
        //rent duration list
        private const val LESS_YEAR  = "أقل من سنة"
        private const val ONE_YEAR  = "سنة"
        private const val TWO_FIVE_YEAR  = "سنتين إلى خمس"
        private const val FOR_RENT  = "معد للآجار"
        //price units list
        private const val THOUSANDS_POUNDS  = "ألف ليرة"
        private const val MILLION_POUNDS  = "مليون ليرة"
        private const val BILLION_POUNDS = "مليار ليرة"
        private const val DOLLARS  = "دولار"
        private const val THOUSANDS_DOLLARS  = "ألف دولار"
        //price type list
        private const val FINAL_PRICE  = "سعر نهائي"
        private const val LITTLE_ARGUE_PRICE  = "بازار خفيف"
        private const val ARGUE_PRICE  = "وبازار"

        private const val ANNUAL_RENT  = "آجار سنوي"
        private const val HALF_ANNUAL_RENT  = "آجار شهري"
        private const val MONTHLY_RENT  = "آجار نصف سنوي"

        private const val ONE_YEAR_BET  = "رهنية سنة واحدة"
        private const val TWO_YEAR_BET  = "رهنية سنتين"
        private const val THREE_YEAR_BET  = "رهنية ثلاث سنوات"
        private const val FOUR_YEAR_BET  = "رهنية أربع سنوات"
        private const val FIVE_YEAR_BET  = "رهنية خمس سنوات"
















    }

}