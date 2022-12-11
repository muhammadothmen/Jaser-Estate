package com.othman.jaserestate

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.slider.RangeSlider
import com.othman.jaserestate.activities.AddEstateActivity
import com.othman.jaserestate.activities.MainActivity
import kotlinx.android.synthetic.main.activity_add_estate.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_filter.*


class FilterFragment : Fragment(R.layout.fragment_filter) {

    private var typeQuery = ""
    private var directionQuery = ""
    private var roomQuery = ""
    private var areaQuery = ""
    private var heightQuery = ""
    private var situationQuery = ""
    private var furnitureSituationQuery = ""
    private var furnitureQuery = ""
    private var legalQuery = ""
    private var priorityQuery = ""

    private var situationValues = arrayListOf( AddEstateActivity.FULL_MAINTENANCE,
        AddEstateActivity.UNDER_MEDIUM, AddEstateActivity.MEDIUM ,AddEstateActivity.GOOD,
        AddEstateActivity.VERY_GOOD ,AddEstateActivity.DELUXE,AddEstateActivity.SUPER_DELUXE)
    private var roomNoValues = arrayListOf(AddEstateActivity.ONE_ROOM,
        AddEstateActivity.ONE_R00M_WITH_SALON, AddEstateActivity.TWO_ROOM_WITH_DISTRIBUTOR,
        AddEstateActivity.TWO_R00M_WITH_SALON, AddEstateActivity.TWO_ROOM_WITH_DISTRIBUTOR,
        AddEstateActivity.THREE_R00M_WITH_SALON, AddEstateActivity.FOUR_ROOM_WITH_DISTRIBUTOR,
        AddEstateActivity.FOUR_R00M_WITH_SALON, AddEstateActivity.FIFE_ROOM_WITH_DISTRIBUTOR,
        AddEstateActivity.FIFE_R00M_WITH_SALON, AddEstateActivity.SIX_ROOM_WITH_DISTRIBUTOR,
        AddEstateActivity.SIX_R00M_WITH_SALON, AddEstateActivity.SEVEN_ROOM_WITH_DISTRIBUTOR,
        AddEstateActivity.SEVEN_R00M_WITH_SALON, AddEstateActivity.EIGHT_ROOM_WITH_DISTRIBUTOR,
        AddEstateActivity.EIGHT_ROOM_WITH_SALON, AddEstateActivity.NINE_ROOM_WITH_DISTRIBUTOR,
        AddEstateActivity.NINE_ROOM_WITH_SALON, AddEstateActivity.OTHER_ROOM_N0)
    private var filterSituationValues = arrayListOf(2,3)
    private var filterFurnitureSituationValues = arrayListOf(2,3)
    private var filterRoomNoValues = arrayListOf(2,3)
    private var filterAreaValues = arrayListOf(100,200)
    private var filterHeightValues = arrayListOf(2,3)
    private var filterDirectionValues = arrayListOf(2,3)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context as AppCompatActivity).apply {
            setSupportActionBar(tb_fragment_filter)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "الفلتر"
        }

        tb_fragment_filter.setNavigationOnClickListener {
            activity?.fl_show_data?.visibility = View.VISIBLE
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        btn_apply.setOnClickListener {
            val allQuery = areaQuery + typeQuery + situationQuery + furnitureQuery +
                    furnitureSituationQuery + legalQuery + priorityQuery + roomQuery +
                    heightQuery + directionQuery
            (activity as MainActivity).setWhereClauseQuery()
            (activity as MainActivity).whereClauseQuery += allQuery
            (activity as MainActivity).getHappyPlacesListFromLocalDB()
            activity?.fl_show_data?.visibility = View.VISIBLE
            activity?.onBackPressedDispatcher?.onBackPressed()
        }


        sw_filter_area.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                areaQuery = " and area * 10 between (${filterAreaValues[0]} * 10) and (${filterAreaValues[1]} * 10)"
                ll2_filter_area.visibility = View.VISIBLE
            }else{
                ll2_filter_area.visibility = View.GONE
                areaQuery = ""
            }
        }
        rs_filter_area.addOnChangeListener(object : RangeSlider.OnChangeListener {
            override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
                filterAreaValues[0] = slider.values[0].toInt()
                filterAreaValues[1] = slider.values[1].toInt()
                tv_filter_area_low_value.text = slider.values[0].toInt().toString()
                tv_filter_area_high_value.text = slider.values[1].toInt().toString()
                areaQuery = " and area * 10 between (${filterAreaValues[0]} * 10) and (${filterAreaValues[1]} * 10)"
            }
        })

        sw_filter_type.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                ll2_filter_type.visibility = View.VISIBLE
            }else{
                ll2_filter_type.visibility = View.GONE
                typeQuery = ""
            }
        }
        val typeCheckBoxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                when (buttonView) {
                    cb_filter_sale -> {
                        typeQuery = " and type = '${AddEstateActivity.SALE}'"
                        cb_filter_rent.isChecked = false
                        cb_filter_bet.isChecked = false
                        cb_filter_buy.isChecked = false
                    }
                    cb_filter_rent -> {
                        typeQuery = " and type = '${AddEstateActivity.RENT}'"
                        cb_filter_sale.isChecked = false
                        cb_filter_bet.isChecked = false
                        cb_filter_buy.isChecked = false
                    }
                    cb_filter_bet -> {
                        typeQuery = " and type = '${AddEstateActivity.BET}'"
                        cb_filter_rent.isChecked = false
                        cb_filter_sale.isChecked = false
                        cb_filter_buy.isChecked = false
                    }
                    cb_filter_buy -> {
                        typeQuery = " and type = '${AddEstateActivity.BUY}'"
                        cb_filter_rent.isChecked = false
                        cb_filter_bet.isChecked = false
                        cb_filter_sale.isChecked = false
                    }
                }
            }
        }
        cb_filter_sale.setOnCheckedChangeListener(typeCheckBoxListener)
        cb_filter_rent.setOnCheckedChangeListener(typeCheckBoxListener)
        cb_filter_bet.setOnCheckedChangeListener(typeCheckBoxListener)
        cb_filter_buy.setOnCheckedChangeListener(typeCheckBoxListener)


        sw_filter_situation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                filterSituationValues = arrayListOf(2,3)
                setSituationQuery()
                ll2_filter_situation.visibility = View.VISIBLE
            }else{
                ll2_filter_situation.visibility = View.GONE
                situationQuery = ""
            }
        }
        rs_filter_situation.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            filterSituationValues[0] = slider.values[0].toInt()
            filterSituationValues[1] = slider.values[1].toInt()
            tv_filter_situation_low_value.text = situationValues[filterSituationValues[0]]
            tv_filter_situation_high_value.text = situationValues[filterSituationValues[1]]
            setSituationQuery()
        })

        sw_filter_furniture.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                ll2_filter_furniture.visibility = View.VISIBLE
            }else{
                ll2_filter_furniture.visibility = View.GONE
                furnitureQuery = ""
            }
        }
        val furnitureCheckBoxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                when (buttonView) {
                    cb_filter_full_furniture -> {
                        furnitureQuery = " and furniture = '${AddEstateActivity.FULL_FURNITURE}'"
                        cb_filter_partial_furniture.isChecked = false
                        cb_filter_no_furniture.isChecked = false
                    }
                    cb_filter_partial_furniture -> {
                        furnitureQuery = " and furniture = '${AddEstateActivity.PARTIAL_FURNITURE}'"
                        cb_filter_full_furniture.isChecked = false
                        cb_filter_no_furniture.isChecked = false
                    }
                    cb_filter_no_furniture -> {
                        furnitureQuery = " and furniture = '${AddEstateActivity.NO_FURNITURE}'"
                        cb_filter_partial_furniture.isChecked = false
                        cb_filter_full_furniture.isChecked = false
                    }
                }
            }
        }
        cb_filter_full_furniture.setOnCheckedChangeListener(furnitureCheckBoxListener)
        cb_filter_partial_furniture.setOnCheckedChangeListener(furnitureCheckBoxListener)
        cb_filter_no_furniture.setOnCheckedChangeListener(furnitureCheckBoxListener)


        sw_filter_furniture_situation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                filterFurnitureSituationValues = arrayListOf(2,3)
                setFurnitureSituationQuery()
                ll2_filter_furniture_situation.visibility = View.VISIBLE
            }else{
                ll2_filter_furniture_situation.visibility = View.GONE
                furnitureSituationQuery = ""
            }
        }
        rs_filter_furniture_situation.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            filterFurnitureSituationValues[0] = slider.values[0].toInt()
            filterFurnitureSituationValues[1] = slider.values[1].toInt()
            tv_filter_furniture_situation_low_value.text = situationValues[filterFurnitureSituationValues[0]]
            tv_filter_furniture_situation_high_value.text = situationValues[filterFurnitureSituationValues[1]]
            setFurnitureSituationQuery()
        })


        sw_filter_legal.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                ll2_filter_legal.visibility = View.VISIBLE
            }else{
                ll2_filter_legal.visibility = View.GONE
                legalQuery = ""
            }
        }
        val legalCheckBoxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                when (buttonView) {
                    cb_filter_legal_green_stamp -> {
                        legalQuery = " and legal = '${AddEstateActivity.GREEN_STAMP}'"
                        cb_filter_legal_contact.isChecked = false
                        cb_filter_legal_court.isChecked = false
                    }
                    cb_filter_legal_court -> {
                        legalQuery = " and legal = '${AddEstateActivity.COURT}'"
                        cb_filter_legal_green_stamp.isChecked = false
                        cb_filter_legal_contact.isChecked = false
                    }
                    cb_filter_legal_contact -> {
                        legalQuery = " and legal = '${AddEstateActivity.CONTRACT}'"
                        cb_filter_legal_green_stamp.isChecked = false
                        cb_filter_legal_court.isChecked = false
                    }
                }
            }
        }
        cb_filter_legal_green_stamp.setOnCheckedChangeListener(legalCheckBoxListener)
        cb_filter_legal_court.setOnCheckedChangeListener(legalCheckBoxListener)
        cb_filter_legal_contact.setOnCheckedChangeListener(legalCheckBoxListener)

        sw_filter_priority.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                ll2_filter_priority.visibility = View.VISIBLE

            }else{
                ll2_filter_priority.visibility = View.GONE
                priorityQuery = ""
            }
        }
        val priorityCheckBoxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                when (buttonView) {
                    cb_filter_priority_important -> {
                        priorityQuery = priorityQuery.removeSuffix(" and priority like '%وغير هام'")
                        priorityQuery += " and priority like '%وهام'"
                    }
                    cb_filter_priority_urgent -> {
                        priorityQuery = priorityQuery.removeSuffix(" and priority like 'غير مستعجل%'")
                        priorityQuery += " and priority like 'مستعجل%'"
                    }
                }
            }else{
                when (buttonView) {
                    cb_filter_priority_important -> {
                        priorityQuery = priorityQuery.removeSuffix(" and priority like '%وهام'")
                        priorityQuery += " and priority like '%وغير هام'"

                    }
                    cb_filter_priority_urgent -> {
                        priorityQuery = priorityQuery.removeSuffix(" and priority like 'مستعجل%'")
                        priorityQuery += " and priority like 'غير مستعجل%'"

                    }
                }

            }
        }
        cb_filter_priority_important.setOnCheckedChangeListener(priorityCheckBoxListener)
        cb_filter_priority_urgent.setOnCheckedChangeListener(priorityCheckBoxListener)


        sw_filter_roomNo.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                filterRoomNoValues = arrayListOf(2,3)
                setRoomNoQuery()
                ll2_filter_roomNo.visibility = View.VISIBLE
            }else{
                ll2_filter_roomNo.visibility = View.GONE
                roomQuery = ""
            }
        }
        rs_filter_roomNO.addOnChangeListener(RangeSlider.OnChangeListener { slider, value, fromUser ->
            filterRoomNoValues[0] = slider.values[0].toInt()
            filterRoomNoValues[1] = slider.values[1].toInt()
            tv_filter_roomNo_low_value.text = filterRoomNoValues[0].toString()
            tv_filter_roomNo_high_value.text = filterRoomNoValues[1].toString()
            setRoomNoQuery()
        })



        sw_filter_height.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                filterHeightValues = arrayListOf(2,3)
                setHeightQuery()
                ll2_filter_height.visibility = View.VISIBLE
            }else{
                ll2_filter_height.visibility = View.GONE
                heightQuery = ""
            }
        }
        rs_filter_height.addOnChangeListener(object : RangeSlider.OnChangeListener {
            override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
                filterHeightValues[0] = slider.values[0].toInt()
                filterHeightValues[1] = slider.values[1].toInt()
                tv_filter_height_low_value.text = slider.values[0].toInt().toString()
                tv_filter_height_high_value.text = slider.values[1].toInt().toString()
                setHeightQuery()
            }
        })


        sw_filter_direction.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                filterDirectionValues = arrayListOf(2,3)
                setDirectionQuery()
                ll2_filter_direction.visibility = View.VISIBLE
            }else{
                ll2_filter_direction.visibility = View.GONE
                directionQuery = ""
            }
        }
        rs_filter_direction.addOnChangeListener(object : RangeSlider.OnChangeListener {
            override fun onValueChange(slider: RangeSlider, value: Float, fromUser: Boolean) {
                filterDirectionValues[0] = slider.values[0].toInt()
                filterDirectionValues[1] = slider.values[1].toInt()
                tv_filter_direction_low_value.text = slider.values[0].toInt().toString()
                tv_filter_direction_high_value.text = slider.values[1].toInt().toString()
                setDirectionQuery()
            }
        })


    }

    private fun setSituationQuery() {
        situationQuery = ""
        val situationList = arrayListOf(
            "situation = '${AddEstateActivity.FULL_MAINTENANCE}'",
            "situation = '${AddEstateActivity.UNDER_MEDIUM}'",
            "situation = '${AddEstateActivity.MEDIUM}'",
            "situation = '${AddEstateActivity.GOOD}'",
            "situation = '${AddEstateActivity.VERY_GOOD}'",
            "situation = '${AddEstateActivity.DELUXE}'",
            "situation = '${AddEstateActivity.SUPER_DELUXE}'"
        )
        situationQuery = " and ("
        for (situation in filterSituationValues[0] until filterSituationValues[1] + 1) {
            situationQuery += situationList[situation]
            if (situation != filterSituationValues[1]) {
                situationQuery += " or "
            }else{
                situationQuery += ")"
            }
        }
    }

    private fun setFurnitureSituationQuery() {
        furnitureSituationQuery = ""
        val situationList = arrayListOf(
            "furnitureSituation = '${AddEstateActivity.FULL_MAINTENANCE}'",
            "furnitureSituation = '${AddEstateActivity.UNDER_MEDIUM}'",
            "furnitureSituation = '${AddEstateActivity.MEDIUM}'",
            "furnitureSituation = '${AddEstateActivity.GOOD}'",
            "furnitureSituation = '${AddEstateActivity.VERY_GOOD}'",
            "furnitureSituation = '${AddEstateActivity.DELUXE}'",
            "furnitureSituation = '${AddEstateActivity.SUPER_DELUXE}'"
        )
        furnitureSituationQuery = " and ("
        for (situation in filterFurnitureSituationValues[0] until filterFurnitureSituationValues[1] + 1) {
            furnitureSituationQuery += situationList[situation]
            if (situation != filterFurnitureSituationValues[1]) {
                furnitureSituationQuery += " or "
            }else{
                furnitureSituationQuery += ")"
            }
        }
    }

    private fun setRoomNoQuery() {
        roomQuery = ""
        val roomList = arrayListOf(
            "roomNo = '${AddEstateActivity.ONE_ROOM}'",
            "roomNo = '${AddEstateActivity.ONE_R00M_WITH_SALON}' or roomNo = '${AddEstateActivity.TWO_ROOM_WITH_DISTRIBUTOR}'",
            "roomNo = '${AddEstateActivity.TWO_R00M_WITH_SALON}' or roomNo = '${AddEstateActivity.THREE_ROOM_WITH_DISTRIBUTOR}'",
            "roomNo = '${AddEstateActivity.THREE_R00M_WITH_SALON}' or roomNo = '${AddEstateActivity.FOUR_ROOM_WITH_DISTRIBUTOR}'",
            "roomNo = '${AddEstateActivity.FOUR_R00M_WITH_SALON}' or roomNo = '${AddEstateActivity.FIFE_ROOM_WITH_DISTRIBUTOR}'",
            "roomNo = '${AddEstateActivity.FIFE_R00M_WITH_SALON}' or roomNo = '${AddEstateActivity.SIX_ROOM_WITH_DISTRIBUTOR}'",
            "roomNo = '${AddEstateActivity.SIX_R00M_WITH_SALON}' or roomNo = '${AddEstateActivity.SEVEN_ROOM_WITH_DISTRIBUTOR}'",
            "roomNo = '${AddEstateActivity.SEVEN_R00M_WITH_SALON}' or roomNo = '${AddEstateActivity.EIGHT_ROOM_WITH_DISTRIBUTOR}'",
            "roomNo = '${AddEstateActivity.EIGHT_ROOM_WITH_SALON}' or roomNo = '${AddEstateActivity.NINE_ROOM_WITH_DISTRIBUTOR}'",
            "roomNo = '${AddEstateActivity.NINE_ROOM_WITH_SALON}' or roomNo = '${AddEstateActivity.OTHER_ROOM_N0}'"
            )
        roomQuery = " and ("
        for (situation in filterRoomNoValues[0] -1 until filterRoomNoValues[1]) {
            roomQuery += roomList[situation]
            if (situation != filterRoomNoValues[1]-1) {
                roomQuery += " or "
            }else{
                roomQuery += ")"
            }
        }
    }



    private fun setHeightQuery() {
        heightQuery = ""
        val heightList = arrayListOf(
            "height = '${AddEstateActivity.SECOND_UNDER_GROUND}'",
            "height = '${AddEstateActivity.FIRST_UNDER_GROUND}' or height = '${AddEstateActivity.GARDEN_UNDER_GROUND}'",
            "height = '${AddEstateActivity.SAME_GROUND}'  or height = '${AddEstateActivity.GARDEN_SAME_GROUND}'",
            "height = '${AddEstateActivity.FIRST}'  or height = '${AddEstateActivity.HANGING}' or height = '${AddEstateActivity.GARDEN_UPPER_GROUND}'",
            "height = '${AddEstateActivity.SECOND}'",
            "height = '${AddEstateActivity.THIRD}'",
            "height = '${AddEstateActivity.FOURTH}'",
            "height = '${AddEstateActivity.FIFTH}'",
            "height = '${AddEstateActivity.SIXTH}'",
            "height = '${AddEstateActivity.SURFACE}'"
        )
        heightQuery = " and ("
        for (situation in filterHeightValues[0] + 2 until filterHeightValues[1] + 3) {
            heightQuery += heightList[situation]
            if (situation != filterHeightValues[1]+2) {
                heightQuery += " or "
            }else{
                heightQuery += ")"
            }
        }
    }

    private fun setDirectionQuery() {
        directionQuery = ""
        val directionList = arrayListOf(
        "directions = '${AddEstateActivity.NORTH}' or directions = '${AddEstateActivity.SOUTH}' or directions = '${AddEstateActivity.EAST}' or directions = '${AddEstateActivity.WEST}'",
            "directions LIKE 'زاوية%' or directions LIKE 'حشوة%'",
            "directions LIKE 'نصف%'",
            "directions = '${AddEstateActivity.FULL}'"
        )
        directionQuery = " and ("
        for (situation in filterDirectionValues[0] -1 until filterDirectionValues[1] ) {
            directionQuery += directionList[situation]
            if (situation != filterDirectionValues[1]-1) {
                directionQuery += " or "
            }else{
                directionQuery += ")"
            }
        }
    }




    override fun onStop() {
        super.onStop()
        activity?.fl_show_data?.visibility = View.VISIBLE
    }

    }

