package com.royalit.mfd.adapters

import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.royalit.mfd.R
import com.royalit.mfd.models.ActiveOrder
import com.royalit.mfd.models.OrderDetailModel
import com.royalit.mfd.utils.Utils
import com.royalit.mfd.views.orders.OrderDetailsActivity
import java.text.SimpleDateFormat
import java.util.Locale

class ActiveOrderAdapter(val res: Resources) :
    RecyclerView.Adapter<ActiveOrderAdapter.ActiveORderHolder>() {
    lateinit var listCatagories: ArrayList<OrderDetailModel>

    init {
        listCatagories = ArrayList()
    }

    class ActiveORderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtName = itemView.findViewById<TextView>(R.id.txtName)
        val txt_order = itemView.findViewById<TextView>(R.id.txt_order)
        val txt_order_status = itemView.findViewById<TextView>(R.id.txt_order_status)
        val txtSub = itemView.findViewById<TextView>(R.id.txtSub)
        val txtTotal = itemView.findViewById<TextView>(R.id.txtTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveORderHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_active_order, parent, false)
        return ActiveORderHolder(view)
    }

    override fun getItemCount(): Int {
        return listCatagories.size
    }

    override fun onBindViewHolder(holder: ActiveORderHolder, position: Int) {
        holder.txt_order.text = "Order #${listCatagories.get(position).uniqueId}"

        if (listCatagories.get(position).orderStatus.equals("3")) {
            holder.txt_order_status.background = res.getDrawable(R.drawable.rectangle_order_picked)
            holder.txt_order_status.text = "Pending"
        } else if (listCatagories.get(position).orderStatus.equals("4")) {
            holder.txt_order_status.background =
                res.getDrawable(R.drawable.rectangle_order_confirmed)
            holder.txt_order_status.text = "Confirmed"
        } else if (listCatagories.get(position).orderStatus.equals("5")) {
            holder.txt_order_status.background =
                res.getDrawable(R.drawable.rectangle_order_completed)
            holder.txt_order_status.text = "Shipping"
        } else if (listCatagories.get(position).orderStatus.equals("6")) {
            holder.txt_order_status.background =
                res.getDrawable(R.drawable.rectangle_order_cancel)
            holder.txt_order_status.text = "Cancelled"
        } else if (listCatagories.get(position).orderStatus.equals("7")) {
            holder.txt_order_status.background =
                res.getDrawable(R.drawable.rectangle_order_completed)
            holder.txt_order_status.text = "Delivered"
        } else {
            holder.txt_order_status.visibility = View.GONE
            holder.txt_order_status.background =
                res.getDrawable(R.drawable.rectangle_order_completed)
            holder.txt_order_status.text = "Active"
        }


        //date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy - hh:mm a", Locale.getDefault())
        try {
            val date = inputFormat.parse(listCatagories[position].expectedPickupDate)
            holder.txtName.text = outputFormat.format(date!!)
        } catch (e: Exception) {
            holder.txtName.text = "Invalid date"
        }

        holder.txtSub.text = "₹${listCatagories.get(position).subTotal}"
        holder.txtTotal.text = "₹${listCatagories.get(position).total}"

        holder.itemView.setOnClickListener {
            var intent = Intent(holder.itemView.context, OrderDetailsActivity::class.java)
            intent.putExtra("orderid", listCatagories.get(position).orderId)
            holder.itemView.context.startActivity(intent)
        }

    }


    fun setData(listCatagori: List<OrderDetailModel>) {
        // listCatagories.clear()
        listCatagories.clear()
        listCatagories.addAll(listCatagori)

        notifyDataSetChanged()

    }
}