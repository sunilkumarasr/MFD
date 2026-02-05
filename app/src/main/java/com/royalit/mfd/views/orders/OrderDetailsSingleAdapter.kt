package com.royalit.mfd.views.orders

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.mfd.R
import com.royalit.mfd.models.Products


class OrderDetailsSingleAdapter(val mContext: Context) :
    RecyclerView.Adapter<OrderDetailsSingleAdapter.PorductViewHolder>() {
    lateinit var list: ArrayList<Products>

    init {

        list = ArrayList<Products>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PorductViewHolder {
        val rowView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_child_order, parent, false)
        return PorductViewHolder(rowView)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PorductViewHolder, position: Int) {

        holder.txtName?.text = list.get(position).productName
        holder.txtQtity?.text = "Quantity: " + list.get(position).quantity
        holder.txtPrice?.text = "Price:  ₹" + list.get(position).price

    }

    class PorductViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val txtName = row.findViewById(R.id.txtName) as TextView?
        val txtQtity = row.findViewById(R.id.txtQtity) as TextView?
        val txtPrice = row.findViewById(R.id.txtPrice) as TextView?
    }

    fun setData(lists: List<Products>) {
        list.clear()
        list.addAll(lists)
        notifyDataSetChanged()
    }

}



