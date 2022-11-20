package com.othman.jaserestate.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
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
import kotlinx.android.synthetic.main.price_dialog_layout.*
import kotlinx.android.synthetic.main.selcet_items_dialog.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddEstateActivity : AppCompatActivity(), View.OnClickListener {

    private var floorHousesNo = 1
    private var cal = Calendar.getInstance()
    private var saveImageToInternalStorage = ArrayList<Uri>()
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
    private lateinit var OthersDialog: Dialog
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

    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                try {
                    if (result.data?.data != null) {
                        val selectImageBitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            result.data!!.data
                        )
                        if (saveImageToInternalStorage.size <= 10)
                        {
                            if (saveImageToInternalStorage[0].toString() == defaultImage) {
                                saveImageToInternalStorage.remove(Uri.parse(defaultImage))
                            }
                                saveImageToInternalStorage.add(
                                    saveImageToInternalStorage(
                                        selectImageBitmap
                                    )
                                )


                        }else{
                            Toast.makeText(this,"You Can not add more than 10 images",Toast.LENGTH_SHORT).show()
                        }
                        imagesAdapter.notifyDataSetChanged()
                    //iv_place_image.setImageBitmap(selectImageBitmap)
                        //iv_place_image.setImageURI(result.data?.data)
                    }else if (result.data?.clipData != null){
                        val count = result.data?.clipData?.itemCount
                        for (i in 0 until count!!) {
                            val selectImageBitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                result.data?.clipData?.getItemAt(i)?.uri
                            )

                            //lifecycleScope.launch {}
                            if (saveImageToInternalStorage[0].toString() == defaultImage) {
                                saveImageToInternalStorage.remove(Uri.parse(defaultImage))
                            }
                                saveImageToInternalStorage.add(
                                    saveImageToInternalStorage(
                                        selectImageBitmap
                                    )
                                )
                            if (saveImageToInternalStorage.size >= 10){
                                break
                            }
                                Log.e("saved image", "path: ${saveImageToInternalStorage[i]}")

                        }
                        imagesAdapter.notifyDataSetChanged()

                    }
                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this@AddEstateActivity,
                        "Oops, Failed",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val openCameraLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                try {
                    val thumbnail: Bitmap = result.data!!.extras!!.get("data") as Bitmap

                    if (saveImageToInternalStorage[0].toString() == defaultImage) {
                        saveImageToInternalStorage.remove(Uri.parse(defaultImage))
                    }
                        saveImageToInternalStorage.add(
                            saveImageToInternalStorage(
                                thumbnail
                            )
                        )
                    imagesAdapter.notifyDataSetChanged()
                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this@AddEstateActivity,
                        "Oops, Failed",
                        Toast.LENGTH_SHORT).show()
                }
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
            onBackPressed()
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
            mHappyPlaceDetails = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }

        selectDialogInit()

        defaultImage = ContentResolver.SCHEME_ANDROID_RESOURCE +"://" +
                resources.getResourcePackageName(R.drawable.add_screen_image_placeholder) + '/' +
                resources.getResourceTypeName(R.drawable.add_screen_image_placeholder) + '/' +
                resources.getResourceEntryName(R.drawable.add_screen_image_placeholder)
        if (mHappyPlaceDetails == null){
        saveImageToInternalStorage.add(Uri.parse(defaultImage))
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
            if (!loggerName.isNullOrEmpty()){
                et_logger_type.append(": $loggerName")
            }
            et_owner_tel.setText(mHappyPlaceDetails!!.ownerTel)
            et_logger_tel.setText(mHappyPlaceDetails!!.loggerTel)
            et_priority.setText(mHappyPlaceDetails!!.priority)
            et_rent_duration.setText(mHappyPlaceDetails!!.rentDuration)
            et_positives.setText(mHappyPlaceDetails!!.positives)
            et_negatives.setText(mHappyPlaceDetails!!.negatives)
            saveImageToInternalStorage = mHappyPlaceDetails!!.images!!
            Log.e("hplist","${saveImageToInternalStorage}")

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
        OthersDialog = Dialog(this, R.style.Theme_Dialog)
        OthersDialog.setContentView(R.layout.edit_text_others_dialog_layout)
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

        imagesAdapter = ImagesAdapter(this, saveImageToInternalStorage)
        rvAddImages.adapter = imagesAdapter

        val deleteSwipeToDelete = object: SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (saveImageToInternalStorage.size > 1) {
                    saveImageToInternalStorage.removeAt(viewHolder.adapterPosition)
                    imagesAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                }else{
                    saveImageToInternalStorage[0] = Uri.parse(defaultImage)
                    imagesAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
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
                if (saveImageToInternalStorage.size < 10){
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery" ,"Capture photo from camera")
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
                        val defaultImage = ContentResolver.SCHEME_ANDROID_RESOURCE +"://" +
                                resources.getResourcePackageName(R.drawable.add_screen_image_placeholder) + '/' +
                                resources.getResourceTypeName(R.drawable.add_screen_image_placeholder) + '/' +
                                resources.getResourceEntryName(R.drawable.add_screen_image_placeholder)
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
                            saveImageToInternalStorage
                            )
                        val dbHandler = DatabaseHandler(this)
                        if (mHappyPlaceDetails == null){
                        val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                        if (addHappyPlace > 0){
                            setResult(RESULT_OK)
                            finish()
                        }
                        }else{
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
                            OthersDialog.tv_other_dialog_title.text =
                                resources.getString(R.string.other_dialog_title_room_no)
                            OthersDialog.tv_other_dialog_cancel.setOnClickListener {
                                OthersDialog.dismiss()
                                et_roomNo.text?.clear()
                            }
                            OthersDialog.tv_other_dialog_ok.setOnClickListener {
                                if (OthersDialog.et_other_dialog.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@AddEstateActivity,
                                        "رجاءً أدخل عدد الغرف",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    et_roomNo.text = OthersDialog.et_other_dialog.text
                                    OthersDialog.dismiss()
                                }
                            }
                            OthersDialog.et_other_dialog.text?.clear()
                            OthersDialog.show()
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
                            OthersDialog.tv_other_dialog_title.text =
                                resources.getString(R.string.other_dialog_title_furniture)
                            OthersDialog.tv_other_dialog_cancel.setOnClickListener {
                                OthersDialog.dismiss()
                                et_furniture.text?.clear()
                            }
                            OthersDialog.tv_other_dialog_ok.setOnClickListener {
                                if (OthersDialog.et_other_dialog.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@AddEstateActivity,
                                        "رجاءً أدخل الفرش",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    partialFurnitureItems = OthersDialog.et_other_dialog.text.toString()
                                    et_furniture.append(": $partialFurnitureItems")
                                    OthersDialog.dismiss()
                                }
                            }
                            OthersDialog.et_other_dialog.setText(partialFurnitureItems)
                            OthersDialog.show()
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
                            OthersDialog.tv_other_dialog_title.text =
                                resources.getString(R.string.other_dialog_title_name)
                            OthersDialog.tv_other_dialog_cancel.setOnClickListener {
                                OthersDialog.dismiss()
                                et_logger_type.text?.clear()
                            }
                            OthersDialog.tv_other_dialog_ok.setOnClickListener {
                                if (OthersDialog.et_other_dialog.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@AddEstateActivity,
                                        "رجاءً أدخل الإسم",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    loggerName = OthersDialog.et_other_dialog.text.toString()

                                    et_logger_type.append(": $loggerName")
                                    OthersDialog.dismiss()
                                }
                            }
                            OthersDialog.et_other_dialog.setText(loggerName)
                            OthersDialog.show()
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
                            OthersDialog.tv_other_dialog_title.text =
                                resources.getString(R.string.other_dialog_title_standard)
                            OthersDialog.tv_other_dialog_cancel.setOnClickListener {
                                OthersDialog.dismiss()
                                et_owner_standards.text?.clear()
                            }
                            OthersDialog.tv_other_dialog_ok.setOnClickListener {
                                if (OthersDialog.et_other_dialog.text.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@AddEstateActivity,
                                        "رجاءً أدخل المعيار",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    otherStandard = OthersDialog.et_other_dialog.text.toString()
                                    et_owner_standards.append(": $otherStandard")
                                    OthersDialog.dismiss()
                                }
                            }
                            OthersDialog.et_other_dialog.setText(otherStandard)
                            OthersDialog.show()
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
                    openCameraLauncher.launch(intent)
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

    private  fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        var result: Uri
        //withContext(Dispatchers.IO) {}
            val wrapper = ContextWrapper(applicationContext)
            val path = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
            val file = File(path, "${UUID.randomUUID()}.jpg")
            try {
                val stream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                stream.flush()
                stream.close()
                result = Uri.parse(file.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
                result = Uri.parse("")
            }

        return result
    }

    //object for constants
    companion object{
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
        private const val IS_DONE = 1
        private const val IS_NOT_DONE = 0
        //types
        private const val SALE = "بيع"
        private const val RENT = "آجار"
        private const val BET = "رهنية"
        private const val BUY = "شراء"
        //height list
        private const val SECOND_UNDER_GROUND = "ثاني تحت الأرض"
        private const val FIRST_UNDER_GROUND = "أول تحت الأرض"
        private const val SAME_GROUND = "أرضي"
        private const val HANGING = "معلق"
        private const val FIRST = "أول"
        private const val SECOND = "ثاني"
        private const val THIRD = "ثالث"
        private const val FOURTH = "رابع"
        private const val FIFTH = "خامس"
        private const val SIXTH = "سادس"
        private const val SURFACE = "سطوح"
        private const val GARDEN_UNDER_GROUND = "حديقة نزول"
        private const val GARDEN_UPPER_GROUND = "حديقة فوق الأرض"
        private const val GARDEN_SAME_GROUND = "حديقة أرضي"
        //roomNo list
        private const val ONE_ROOM = "غرفة واحدة"
        private const val ONE_R00M_WITH_SALON = "غرفة وصالون"
        private const val TWO_ROOM_WITH_DISTRIBUTOR  = "غرفتين وموزع"
        private const val TWO_R00M_WITH_SALON = "غرفتين وصالون"
        private const val THREE_ROOM_WITH_DISTRIBUTOR  = "ثلاث غرف وموزع"
        private const val THREE_R00M_WITH_SALON = "ثلاث غرف وصالون"
        private const val FOUR_ROOM_WITH_DISTRIBUTOR  = "أربع غرف وموزع"
        private const val FOUR_R00M_WITH_SALON = "أربع غرف وصالون"
        private const val FIFE_ROOM_WITH_DISTRIBUTOR  = "خمس غرف وموزع"
        private const val FIFE_R00M_WITH_SALON = "خمس غرف وصالون"
        private const val SIX_ROOM_WITH_DISTRIBUTOR  = "ست غرف وموزع"
        private const val SIX_R00M_WITH_SALON = "ست غرف وصالون"
        private const val SEVEN_ROOM_WITH_DISTRIBUTOR  = "سبع غرف وموزع"
        private const val SEVEN_R00M_WITH_SALON = "سبع غرف وصالون"
        private const val EIGHT_ROOM_WITH_DISTRIBUTOR  = "ثمان غرف وموزع"
        private const val EIGHT_ROOM_WITH_SALON  = "ثمان غرف وصالون"
        private const val NINE_ROOM_WITH_DISTRIBUTOR  = "تسع غرف وموزع"
        private const val NINE_ROOM_WITH_SALON  = "تسع غرف وصالون"
        private const val OTHER_ROOM_N0 = "أخرى"
        //situation and furniture list
        private const val SUPER_DELUXE  = "سوبر ديلوكس"
        private const val DELUXE  = "ديلوكس"
        private const val VERY_GOOD  = "جيد جداً"
        private const val GOOD  = "جيد"
        private const val MEDIUM  = "وسط"
        private const val UNDER_MEDIUM  = "دون الوسط"
        private const val FULL_MAINTENANCE  = "صيانة كاملة"
        private const val NO_FURNITURE  = "غير مفروش"
        private const val FULL_FURNITURE  = "فرش كامل"
        internal const val PARTIAL_FURNITURE  = "فرش جزئي"
        private const val FULL_FURNITURE_ABLE_RENT_WITHOUT  = "فرش كامل مع إمكانية الآجار بدونه"
        private const val PARTIAL_FURNITURE_ABLE_RENT_WITHOUT  = "فرش جزئي مع إمكانية الآجار بدونه"


        //front and back list
        private const val FRONT  = "أمامي"
        private const val BACK  = "خلفي"
        //directions list
        private const val FULL  = "بلاطة كاملة: الاتجاهات الأربعة"
        private const val HALF_EAST_NORTH_WEST  = "نصف بلاطة: شرقي-شمالي-غربي"
        private const val HALF_EAST_SOUTH_WEST  = "نصف بلاطة: شرقي-قبلي-غربي"
        private const val HALF_NORTH_WEST_SOUTH  = "نصف بلاطة: شمالي-غربي-قبلي"
        private const val HALF_NORTH_EAST_SOUTH  = "نصف بلاطة: شمالي-شرقي-قبلي"
        private const val CORNER_NORTH_EAST  = "زاوية: شمالي-شرقي"
        private const val CORNER_NORTH_WEST  = "زاوية: شمالي-غربي"
        private const val CORNER_SOUTH_EAST  = "زاوية: قبلي-شرقي"
        private const val CORNER_SOUTH_WEST  = "زاوية: قبلي-غربي"
        private const val PADDING_EAST_WEST  = "حشوة: غربي-شرقي"
        private const val PADDING_NORTH_SOUTH  = "حشوة: شمالي-قبلي"
        private const val NORTH  = "شمالي"
        private const val SOUTH  = "قبلي"
        private const val WEST  = "غربي"
        private const val EAST  = "شرقي"
        //legal list
        private const val CONTRACT  = "عقد"
        private const val  COURT = "حكم محكمة"
        private const val GREEN_STAMP  = "طابو أخضر"
        //is direct list
        private const val OWNER  = "المالك"
        private const val OFFICE  = "مكتب"
        private const val MOBILE_OFFICE  = "مكتب جوال"

        //priority
        private const val IMPORTANT_URGENT  = "مستعجل وهام"
        private const val IMPORTANT_NOT_URGENT  = "غير مستعجل وهام"
        private const val NOT_IMPORTANT_URGENT  = "مستعجل وغير هام"
        private const val NOT_IMPORTANT_NOT_URGENT  = "غير مستعجل وغير هام"
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