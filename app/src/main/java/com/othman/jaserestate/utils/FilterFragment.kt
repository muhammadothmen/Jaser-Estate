package com.othman.jaserestate.utils

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.slider.RangeSlider
import com.othman.jaserestate.R
import com.othman.jaserestate.activities.AddEstateActivity
import com.othman.jaserestate.activities.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
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

    private var filterSituationValues = arrayListOf(2,3)
    private var filterFurnitureSituationValues = arrayListOf(2,3)
    private var filterRoomNoValues = arrayListOf(2,3)
    private var filterAreaValues = arrayListOf(100,200)
    private var filterHeightValues = arrayListOf(2,3)
    private var filterDirectionValues = arrayListOf(2,3)

    private var filterTypeList = arrayListOf(filterSituationValues, filterFurnitureSituationValues,
    filterHeightValues, filterDirectionValues, filterRoomNoValues, filterAreaValues)
    private var situationValues = arrayListOf( AddEstateActivity.FULL_MAINTENANCE,
        AddEstateActivity.UNDER_MEDIUM, AddEstateActivity.MEDIUM ,AddEstateActivity.GOOD,
        AddEstateActivity.VERY_GOOD ,AddEstateActivity.DELUXE,AddEstateActivity.SUPER_DELUXE)

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
            (activity as MainActivity).resetWhereClauseQuery()
            (activity as MainActivity).whereClauseQuery += allQuery
            (activity as MainActivity).getHappyPlacesListFromLocalDB()
            activity?.fl_show_data?.visibility = View.VISIBLE
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        val checkBoxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
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

        switchInit(sw_filter_type, ll2_filter_type,null, TYPE)
        cb_filter_sale.setOnCheckedChangeListener(checkBoxListener)
        cb_filter_rent.setOnCheckedChangeListener(checkBoxListener)
        cb_filter_bet.setOnCheckedChangeListener(checkBoxListener)
        cb_filter_buy.setOnCheckedChangeListener(checkBoxListener)

        switchInit(sw_filter_furniture, ll2_filter_furniture, null, FURNITURE)
        cb_filter_full_furniture.setOnCheckedChangeListener(checkBoxListener)
        cb_filter_partial_furniture.setOnCheckedChangeListener(checkBoxListener)
        cb_filter_no_furniture.setOnCheckedChangeListener(checkBoxListener)

        switchInit(sw_filter_legal, ll2_filter_legal, null, LEGAL)
        cb_filter_legal_green_stamp.setOnCheckedChangeListener(checkBoxListener)
        cb_filter_legal_court.setOnCheckedChangeListener(checkBoxListener)
        cb_filter_legal_contact.setOnCheckedChangeListener(checkBoxListener)

        switchInit(sw_filter_priority, ll2_filter_priority, null, PRIORITY)
        cb_filter_priority_important.setOnCheckedChangeListener(checkBoxListener)
        cb_filter_priority_urgent.setOnCheckedChangeListener(checkBoxListener)

        val rangeSliderListener = RangeSlider.OnChangeListener { slider, value, fromUser ->
            when(slider){
                rs_filter_situation -> {
                    sliderInit(rs_filter_situation, tv_filter_situation_low_value, tv_filter_situation_high_value, ::setSituationQuery, SITUATION)
                }
                rs_filter_furniture_situation -> {
                    sliderInit(rs_filter_furniture_situation, tv_filter_furniture_situation_low_value, tv_filter_furniture_situation_high_value, ::setFurnitureSituationQuery, FURNITURE_SITUATION)
                }
                rs_filter_roomNO -> {
                    sliderInit(rs_filter_roomNO, tv_filter_roomNo_low_value, tv_filter_roomNo_high_value, ::setRoomNoQuery, ROOM)
                }
                rs_filter_height -> {
                    sliderInit(rs_filter_height, tv_filter_height_low_value, tv_filter_height_high_value, ::setHeightQuery , HEIGHT)
                }
                rs_filter_direction -> {
                    sliderInit(rs_filter_direction, tv_filter_direction_low_value, tv_filter_direction_high_value, ::setDirectionQuery , DIRECTION)
                }
                rs_filter_area -> {
                    sliderInit(rs_filter_area, tv_filter_area_low_value, tv_filter_area_high_value, ::setAreaQuery, AREA)
                }
            }
        }

        switchInit(sw_filter_area, ll2_filter_area, ::setAreaQuery, AREA)
        rs_filter_area.addOnChangeListener(rangeSliderListener)

        switchInit(sw_filter_situation, ll2_filter_situation, ::setSituationQuery, SITUATION)
        rs_filter_situation.addOnChangeListener(rangeSliderListener)

        switchInit(sw_filter_furniture_situation, ll2_filter_furniture_situation,::setFurnitureSituationQuery, FURNITURE_SITUATION)
        rs_filter_furniture_situation.addOnChangeListener(rangeSliderListener)

        switchInit(sw_filter_roomNo, ll2_filter_roomNo, ::setRoomNoQuery, ROOM)
        rs_filter_roomNO.addOnChangeListener(rangeSliderListener)

        switchInit(sw_filter_height, ll2_filter_height, ::setHeightQuery, HEIGHT)
        rs_filter_height.addOnChangeListener(rangeSliderListener)

        switchInit(sw_filter_direction, ll2_filter_direction, ::setDirectionQuery, DIRECTION)
        rs_filter_direction.addOnChangeListener(rangeSliderListener)

    }

    private fun setAreaQuery() {
        areaQuery =
            " and area * 10 between (${filterTypeList[AREA][0]} * 10) and (${filterTypeList[AREA][1]} * 10)"
        Log.e("Jasser",areaQuery)

    }

    private fun setSituationQuery() {
        situationQuery = setQuery(SITUATION, situationList,0,1)
    }

    private fun setFurnitureSituationQuery() {
        furnitureSituationQuery = setQuery(FURNITURE_SITUATION, furnitureSituationList,0,1)
    }

    private fun setRoomNoQuery() {
        roomQuery = setQuery(ROOM, roomList,-1,0)
    }

    private fun setHeightQuery() {
        heightQuery = setQuery(HEIGHT, heightList, 2,3)
    }

    private fun setDirectionQuery() {
        directionQuery = setQuery(DIRECTION, directionList, -1,0)
    }

    private fun setQuery(type: Int, queryList: ArrayList<String>, lowOffset: Int, highOffset: Int): String{
        resetFilterTypeList()
        val mFilterTypeList = filterTypeList[type]
        var query = " and ("
        for (item in mFilterTypeList[0] + lowOffset until mFilterTypeList[1] + highOffset) {
            query += queryList[item]
            if (item != mFilterTypeList[1] + highOffset - 1) {
                query += " or "
            }else{
                query += ")"
            }
        }
        Log.e("Jasser",query)
        return  query
    }

    private fun resetFilterTypeList() {
        filterTypeList = arrayListOf(
            filterSituationValues, filterFurnitureSituationValues,
            filterHeightValues, filterDirectionValues, filterRoomNoValues, filterAreaValues
        )
    }

    private fun switchInit(switch: SwitchCompat, filterLayout: LinearLayout, setQueryFunction: (() -> Unit)?, type: Int){

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                resetFilterValues(type)
                if (setQueryFunction != null) {
                    setQueryFunction()
                }
                filterLayout.visibility = View.VISIBLE
            }else{
                filterLayout.visibility = View.GONE
                resetFilterValues(type)
            }
        }
    }

    private fun sliderInit(slider: RangeSlider, tv_low: TextView, tv_high: TextView, setQueryFunction: (() -> Unit)?, type: Int){
        filterTypeList[type][0] = slider.values[0].toInt()
        filterTypeList[type][1] = slider.values[1].toInt()
        if (type == SITUATION || type == FURNITURE_SITUATION){
            tv_low.text = situationValues[slider.values[0].toInt()]
            tv_high.text = situationValues[slider.values[1].toInt()]
        }else{
            tv_low.text = slider.values[0].toInt().toString()
            tv_high.text = slider.values[1].toInt().toString()
        }
        if (setQueryFunction != null) {
            setQueryFunction()
        }
    }

    private fun resetFilterValues(type: Int){
        when(type){
            FURNITURE_SITUATION -> {
                filterFurnitureSituationValues = arrayListOf(2,3)
                furnitureSituationQuery = ""
            }
            FURNITURE ->{
                furnitureQuery = ""
            }
            SITUATION -> {
                filterSituationValues = arrayListOf(2,3)
                situationQuery = ""
            }
            AREA -> {
                filterAreaValues = arrayListOf(100,200)
                areaQuery = ""
            }
            ROOM -> {
                filterRoomNoValues = arrayListOf(2,3)
                roomQuery = ""
            }
            HEIGHT -> {
                filterHeightValues = arrayListOf(2,3)
                heightQuery = ""
            }
            DIRECTION -> {
                filterDirectionValues = arrayListOf(2,3)
                directionQuery = ""
            }
            TYPE ->{
                typeQuery = ""
            }

        }
    }

    companion object{
        private const val FURNITURE = 6
        private const val FURNITURE_SITUATION = 1
        private const val SITUATION = 0
        private const val ROOM = 4
        private const val AREA = 5
        private const val HEIGHT = 2
        private const val DIRECTION = 3
        private const val TYPE = 7
        private const val LEGAL = 8
        private const val PRIORITY = 9
        private val furnitureSituationList = arrayListOf(
            "furnitureSituation = '${AddEstateActivity.FULL_MAINTENANCE}'",
            "furnitureSituation = '${AddEstateActivity.UNDER_MEDIUM}'",
            "furnitureSituation = '${AddEstateActivity.MEDIUM}'",
            "furnitureSituation = '${AddEstateActivity.GOOD}'",
            "furnitureSituation = '${AddEstateActivity.VERY_GOOD}'",
            "furnitureSituation = '${AddEstateActivity.DELUXE}'",
            "furnitureSituation = '${AddEstateActivity.SUPER_DELUXE}'"
        )
        val situationList = arrayListOf(
            "situation = '${AddEstateActivity.FULL_MAINTENANCE}'",
            "situation = '${AddEstateActivity.UNDER_MEDIUM}'",
            "situation = '${AddEstateActivity.MEDIUM}'",
            "situation = '${AddEstateActivity.GOOD}'",
            "situation = '${AddEstateActivity.VERY_GOOD}'",
            "situation = '${AddEstateActivity.DELUXE}'",
            "situation = '${AddEstateActivity.SUPER_DELUXE}'"
        )
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
        val directionList = arrayListOf(
            "directions = '${AddEstateActivity.NORTH}' or directions = '${AddEstateActivity.SOUTH}' or directions = '${AddEstateActivity.EAST}' or directions = '${AddEstateActivity.WEST}'",
            "directions LIKE 'زاوية%' or directions LIKE 'حشوة%'",
            "directions LIKE 'نصف%'",
            "directions = '${AddEstateActivity.FULL}'"
        )

    }

    override fun onStop() {
        super.onStop()
        //activity?.onBackPressedDispatcher?.onBackPressed()
        activity?.fl_show_data?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        activity?.fl_show_data?.visibility = View.GONE
    }

    }

