package com.example.pearappearanceevaluatesystemandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pearappearanceevaluatesystemandroid.R
import com.example.pearappearanceevaluatesystemandroid.model.PearListItem
import com.example.pearappearanceevaluatesystemandroid.originenum.EvaluateCode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ItemAdapter(
    private val context: Context,
    private val pearList: ArrayList<PearListItem>
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var mainListener: View.OnClickListener? = null

    private var pearId: Int? = null

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val pearListItemButton: Button = view.findViewById(R.id.pear_list_item_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = pearList[position]

        val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val date: Date = df.parse(item.startAt)!!

        val displayDate = SimpleDateFormat("yyyy/M/d H:mm", Locale.US).format(date)
        val display = "No${item.pearId}\n${displayDate}"

        holder.pearListItemButton.text = display
        holder.pearListItemButton.setBackgroundColor(
            ContextCompat.getColor(
                context,
                getEvaluateCodeColor(item.evaluateCode)
            )
        )
        holder.pearListItemButton.setOnClickListener {
            pearId = item.pearId
            mainListener?.onClick(it)
        }
    }

    override fun getItemCount() = pearList.size

    fun setOnItemClickListener(listener: View.OnClickListener) {
        mainListener = listener
    }

    fun getPearItemId(): Int? {
        return pearId
    }

    /**
     * EnumのEvaluateCodeから当該の等級の色に変換する
     */
    private fun getEvaluateCodeColor(evaluateCode: EvaluateCode): Int {
        return when (evaluateCode) {
            EvaluateCode.Good -> {
                R.color.evaluate_list_no
            }
            EvaluateCode.Blue -> {
                R.color.evaluate_list_blue
            }
            EvaluateCode.Red -> {
                R.color.evaluate_list_red
            }
            EvaluateCode.No -> {
                R.color.evaluate_list_no
            }
            else -> {
                R.color.evaluate_list_not_yet
            }
        }
    }
}