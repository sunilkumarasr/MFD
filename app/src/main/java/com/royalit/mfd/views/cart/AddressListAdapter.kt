package com.royalit.mfd.views.cart

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.royalit.mfd.R
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.models.AddressModel
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.MyPref
import com.royalit.mfd.utils.Utils
import com.royalit.mfd.views.orders.OrderDetailsActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class AddressListAdapter(val listActivity: AddressListActivity, val showButton: Boolean): RecyclerView.Adapter<AddressListAdapter.AddressListViewHolder>() {

    lateinit var listAddress:ArrayList<AddressModel>

    init {

        listAddress=ArrayList()
    }
    class AddressListViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txt_address=view.findViewById<CheckedTextView>(R.id.txt_address)
        val radio=view.findViewById<RadioButton>(R.id.radio)
        val imgEdit=view.findViewById<ImageView>(R.id.imgEdit)
        val imgDelete=view.findViewById<ImageView>(R.id.imgDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressListViewHolder {
        return AddressListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_address_list_item,parent,false))
    }

    override fun getItemCount(): Int {
        return  listAddress.size
    }

    override fun onBindViewHolder(holder: AddressListViewHolder, position: Int) {
        val addressModel=listAddress.get(position)
        holder.txt_address.text=addressModel.houseNumber+","+
                addressModel.landmark+", "+
                addressModel.city+", "+
                addressModel.state+", "+
                addressModel.country
        holder.radio.isChecked=addressModel.isChecked
        if(showButton)
            holder.radio.visibility=View.VISIBLE
        else
            holder.radio.visibility=View.INVISIBLE

       holder.itemView.setOnClickListener {
           listAddress.forEach {
               it.isChecked=false;
               if (addressModel.id==it.id)
                   it.isChecked=true

           }
           notifyDataSetChanged()

           listActivity.selectedAddress(addressModel)
       }

        holder.imgEdit.setOnClickListener {
            var intent = Intent(holder.itemView.context, EditAddressActivity::class.java)
            intent.putExtra("id", addressModel.id)
            intent.putExtra("houseNumber", addressModel.houseNumber)
            intent.putExtra("landmark", addressModel.landmark)
            intent.putExtra("city", addressModel.city)
            intent.putExtra("state", addressModel.state)
            intent.putExtra("country", addressModel.country)
            intent.putExtra("pincode", addressModel.pincode)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgDelete.setOnClickListener {
            var hashMap = HashMap<String, String> (2)
            hashMap.putIfAbsent("address_id",addressModel.id.toString());
            RetrofitClient.apiInterface.deleteAddress(hashMap).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    var strRes= response.body();
                    //strRes=strRes.replace("!!","")
                    Log.d("strRes ",strRes.toString());

                    Log.d("strRes ","Calling api 2");
                    val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                    Utils.showMessage("Success", holder.itemView.context)
                    listAddress.remove(addressModel)
                    notifyDataSetChanged()
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Utils.showMessage("Try again", holder.itemView.context)
                }
            })
        }


    }

    fun setData(listAddresss: List<AddressModel>?)
    {
        listAddress.clear()
        listAddresss?.let { listAddress.addAll(it) }
        notifyDataSetChanged()
    }
}