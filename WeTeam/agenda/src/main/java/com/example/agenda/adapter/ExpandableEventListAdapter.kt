package com.example.agenda.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import com.example.agenda.agenda.Day
import com.example.agenda.agenda.Event
import com.example.agenda.R
import com.example.utils.changeGroupReaded
import com.example.utils.changeReaded
import kotlin.collections.ArrayList

class ExpandableEventListAdapter(val context: Context, final val listDataHeader: ArrayList<Day>) :
    BaseExpandableListAdapter(), Filterable {


    lateinit var listDataHeaderFull: ArrayList<Day>


    init {
        listDataHeaderFull = ArrayList(listDataHeader)
    }

    override fun getGroup(groupPosition: Int): Day {
        return listDataHeader.get(groupPosition)
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView2 = convertView
        val day = getGroup(groupPosition)
        day.setData()

        if (convertView2 == null) {
            val inflater: LayoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView2 = inflater.inflate(R.layout.event_item, null)
        }
        val mothYearView: TextView = convertView2!!.findViewById(R.id.info1)
        val dayView: TextView = convertView2!!.findViewById(R.id.info2)
        val dayIcon: TextView = convertView2!!.findViewById(R.id.dayIcon)
        mothYearView.setText(day.month + " " + day.year)
        dayView.setText(day.dayString)
        dayIcon.setText(day.dayInt)
        val isNew = day.checkNewEvents()

        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(com.example.utils.R.attr.themeGroupColor, typedValue, true)
        @ColorInt val groupColor = typedValue.data
        convertView2.setBackgroundColor(groupColor)

        changeGroupReaded(context,convertView2,isNew)

        return convertView2
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listDataHeader.get(groupPosition).events.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Event {
        return listDataHeader.get(groupPosition).events.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView2 = convertView
        var child = getChild(groupPosition, childPosition)
        val tipologia = child.tipoEvento
        val orario = child.ora
        val info = child.posizione
        if (convertView2 == null) {
            val inflater: LayoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView2 = inflater.inflate(R.layout.event_item, null)
        }
        val textView: TextView = convertView2!!.findViewById(R.id.info1)
        val textView2: TextView = convertView2!!.findViewById(R.id.info2)
        val icon: TextView = convertView2!!.findViewById(R.id.dayIcon)
        textView.setText(tipologia)
        textView2.setText(orario + ", " + info)
        when (tipologia) {
        "compleanno" -> icon.setBackgroundResource(R.drawable.birthday)
            "allenamento" -> icon.setBackgroundResource(R.drawable.workout)
            else ->icon.setBackgroundResource(R.drawable.info)

         }
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(com.example.utils.R.attr.themeTextColor, typedValue, true)
        @ColorInt val color = typedValue.data
        icon.background.setTint(color)


        changeReaded(context,convertView2,child.isNew)
        return convertView2
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return listDataHeader.size
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }


    val exampleFilter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: ArrayList<Day> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(listDataHeaderFull)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()

                listDataHeaderFull.forEach {
                    if (it.dayString.toLowerCase().contains(filterPattern)
                        || it.dayInt.toLowerCase().contains(filterPattern)
                        || it.year.toLowerCase().contains(filterPattern)
                        || it.month.toLowerCase().contains(filterPattern)
                    ) {
                        filteredList.add(it)
                    } else {
                        val eventArray = ArrayList<Event>()
                        it.events.forEach {
                            if (it.tipoEvento.toLowerCase().contains(filterPattern)
                                || it.ora.toLowerCase().contains(filterPattern)
                                || it.posizione.toLowerCase().contains(filterPattern)
                            ) {
                                eventArray.add(it)
                            }
                        }
                        if (eventArray.size > 0) {
                            filteredList.add(
                                Day(
                                    it.date,
                                    it.timestamp,
                                    eventArray
                                )
                            )
                        }
                    }
                }
            }
            val results: FilterResults = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            listDataHeader.clear()
            if(results!= null) {
                if(results.values != null) {
                    listDataHeader.addAll(results.values as (ArrayList<Day>))
                    notifyDataSetChanged()
                }
            }
        }
    }


}