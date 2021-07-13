package com.example.weteam.adapter

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.agenda.agenda.AgendaActivity
import com.example.messages.ChatList.MessagesActivity
import com.example.utils.setSafeOnClickListener
import com.example.weteam.R
import com.example.weteam.home.HomeActivity

class ListAdapter(private val myDataset: ArrayList<String>, private var context: Context) :
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item, parent, false) as View

        val layoutParams : ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = (parent.width /2)
        view.layoutParams = layoutParams
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that elementcru
        val iconName = myDataset[position]
        val iconId: Int = context.resources.getIdentifier(iconName,"drawable",context.packageName)
        val image = holder.view.findViewById<ImageView>(R.id.iconImage)
        val text = holder.view.findViewById<TextView>(R.id.iconText)
        image.setImageResource(iconId)
        //get the value of custom attrivute themeTextColor
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(com.example.utils.R.attr.themeTextColor, typedValue, true)
        @ColorInt val color = typedValue.data
        //set the value to image
        image.setColorFilter(color)
        text.setText(iconName)
            holder.view.setSafeOnClickListener { goToActivity(iconName)}
    }

    fun goToActivity(iconName : String){

        var intent: Intent

        when(iconName){
            "agenda" -> intent = Intent(context, AgendaActivity::class.java)
            "messages" ->  intent = Intent(context, MessagesActivity::class.java)
            else -> intent = Intent(context, HomeActivity::class.java)
        }
        context.startActivity(intent)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
