package com.royalit.mfd.views.orders

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.royalit.mfd.R
import com.royalit.mfd.models.OrderDetailModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ORdersAdapter(val res: Resources): RecyclerView.Adapter<ORdersAdapter.OrderViewHolder>() {
lateinit var listOrder:ArrayList<OrderDetailModel>
        init {
            listOrder=ArrayList()
        }
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtOrdStatus=view.findViewById<TextView>(R.id.txtOrdStatus)
        val txt_orderid=view.findViewById<TextView>(R.id.txt_orderid)
        val txt_name=view.findViewById<TextView>(R.id.txt_name)
        val txt_payment_sub=view.findViewById<TextView>(R.id.txt_payment_sub)
        val txt_payment_tax=view.findViewById<TextView>(R.id.txt_payment_tax)
        val txt_payable_amount=view.findViewById<TextView>(R.id.txt_payable_amount)
        val txt_payment_status=view.findViewById<TextView>(R.id.txt_payment_status)
        val txt_address=view.findViewById<TextView>(R.id.txt_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {

        val view=LayoutInflater.from(parent.context).inflate(R.layout.layout_order_items,parent,false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
       return listOrder.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {


        if (listOrder.get(position).orderStatus.equals("2")) {
            holder.txtOrdStatus.background = res.getDrawable(R.drawable.rectangle_order_picked)
            holder.txtOrdStatus.text = "InActive"
        } else if (listOrder.get(position).orderStatus.equals("3")) {
            holder.txtOrdStatus.background = res.getDrawable(R.drawable.rectangle_order_picked)
            holder.txtOrdStatus.text = "Pending"
        } else if (listOrder.get(position).orderStatus.equals("4")) {
            holder.txtOrdStatus.background =
                res.getDrawable(R.drawable.rectangle_order_confirmed)
            holder.txtOrdStatus.text = "Confirmed"
        } else if (listOrder.get(position).orderStatus.equals("5")) {
            holder.txtOrdStatus.background =
                res.getDrawable(R.drawable.rectangle_order_completed)
            holder.txtOrdStatus.text = "Picked Up"
        } else if (listOrder.get(position).orderStatus.equals("6")) {
            holder.txtOrdStatus.background =
                res.getDrawable(R.drawable.rectangle_order_completed)
            holder.txtOrdStatus.text = "In Progress"
        } else if (listOrder.get(position).orderStatus.equals("7")) {
            holder.txtOrdStatus.background =
                res.getDrawable(R.drawable.rectangle_order_completed)
            holder.txtOrdStatus.text = "Delivered"
        } else {
            holder.txtOrdStatus.background =
                res.getDrawable(R.drawable.rectangle_order_completed)
            holder.txtOrdStatus.text = "Active"
        }

        //date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy - hh:mm a", Locale.getDefault())
        try {
            val date = inputFormat.parse(listOrder[position].expectedPickupDate)
            holder.txt_name.text = outputFormat.format(date!!)
        } catch (e: Exception) {
            holder.txt_name.text = "Invalid date"
        }

        holder.txt_orderid.text= "#"+listOrder.get(position).uniqueId
        holder.txt_payment_sub.text= "₹"+listOrder.get(position).subTotal
        holder.txt_payment_tax.text= "₹"+listOrder.get(position).tax
        holder.txt_payable_amount.text= "₹"+listOrder.get(position).total
        holder.txt_payment_status.text=listOrder.get(position).paymentStatus
        var addressDetails=listOrder.get(position).addressDetails
        if (addressDetails != null) {
            holder.txt_address.text="${addressDetails.houseNumber}, " +
                    "${addressDetails.landmark}, " +
                    "${addressDetails.city}, " +
                    "${addressDetails.state}, " +
                    "${addressDetails.country}, "
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, OrderDetailsActivity::class.java).apply {
                putExtra("orderid", listOrder[position].orderId) // Pass orderId
                putExtra("orderPosition", listOrder[position]) // Pass Parcelable object
            }
            context.startActivity(intent)
        }

    }
    fun setData(listOrders:ArrayList<OrderDetailModel>)
    {
        listOrder.clear()
        listOrder.addAll(listOrders)
        notifyDataSetChanged()
    }
}