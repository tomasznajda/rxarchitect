package com.tomasznajda.rxarchitect.sample.view.notes.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import com.tomasznajda.rxarchitect.sample.entity.Category
import com.tomasznajda.simplerecyclerview.SrvViewHolder
import kotlinx.android.synthetic.main.viewholder_category.view.*

class CategoryViewHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView), SrvViewHolder<Category> {

    override fun bind(item: Category) = with(itemView) {
        txtName.setText(item.nameResId)
    }
}