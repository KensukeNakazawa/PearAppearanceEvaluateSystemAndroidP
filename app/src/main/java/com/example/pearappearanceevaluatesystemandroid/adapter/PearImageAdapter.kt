package com.example.pearappearanceevaluatesystemandroid.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.pearappearanceevaluatesystemandroid.R

import android.view.LayoutInflater
import com.example.pearappearanceevaluatesystemandroid.model.EvaluatedPearModel

class PearImageAdapter(
    context: Context,
    itemLayoutId: Int,
    _evaluatedPearModel: EvaluatedPearModel
) : BaseAdapter() {
    private val layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val evaluatedPearModel: EvaluatedPearModel = _evaluatedPearModel
    private var layoutId = itemLayoutId

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var viewHolder: ViewHolder

        var view: View? = convertView

        // Viewを再利用できるか判定する
        if (view == null) {
            // レイアウトを生成する
            view = layoutInflater.inflate(layoutId, null)

            // Viewオブジェクトを生成する
            viewHolder = createViewHolder(view)

            // Viewにオブジェクトを保持する
            view.tag = viewHolder
        }

        viewHolder = view!!.tag as ViewHolder

        viewHolder.pearImageView.setImageBitmap(
            evaluatedPearModel.pearImages[position]
        )
        if (evaluatedPearModel.pearImages.size == evaluatedPearModel.evaluatedPearImages.size) {
            viewHolder.evaluatedPearImageView.setImageBitmap(
                evaluatedPearModel.evaluatedPearImages[position]
            )
        }

        return view

    }

    override fun getItem(position: Int) = evaluatedPearModel.pearImages[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = evaluatedPearModel.pearImages.size


    private fun createViewHolder(view: View): ViewHolder {
        val pearImageView: ImageView = view.findViewById(R.id.pear_image_item)
        val evaluatedPearImageView: ImageView = view.findViewById(R.id.evaluated_pear_image_item)
        return ViewHolder(pearImageView, evaluatedPearImageView)
    }

    internal class ViewHolder(
        var pearImageView: ImageView,
        var evaluatedPearImageView: ImageView
    )

}