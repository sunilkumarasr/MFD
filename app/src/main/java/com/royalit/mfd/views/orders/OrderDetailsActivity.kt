package com.royalit.mfd.views.orders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.ActivityOrderDetailsBinding
import com.royalit.mfd.models.OrderDetails
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.MyPref
import com.royalit.mfd.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.royalit.mfd.R
import com.royalit.mfd.models.GetAddress
import com.royalit.mfd.models.OrderDetailModel
import com.royalit.mfd.models.Products
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderDetailsBinding
    lateinit var orderDetailsAdapter: OrderDetailsSingleAdapter
    lateinit var orderid:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderid= intent.getStringExtra("orderid").toString()

        val orderDetails = intent.getParcelableExtra<OrderDetailModel>("orderPosition")

        getAOrderItems()
        getAOrderDetails()


        binding.lnrBack.setOnClickListener {
            finish()
        }

    }

    fun getAOrderItems() {
        var hashMap = HashMap<String, String> (2)
        hashMap.putIfAbsent("order_id",orderid);
        val customDialog= CustomDialog(this@OrderDetailsActivity);
        customDialog.showDialog(this@OrderDetailsActivity,true)
        var request= RetrofitClient.apiInterface.getOrderItems(hashMap )
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                customDialog.closeDialog()
                var strRes= response.body();
                //strRes=strRes.replace("!!","")
                Log.d("strRes ",strRes.toString());

                Log.d("strRes ","Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                if(jsonObject!!.get("status").asString=="200")
                {
                    val activeOrderType: Type = object : TypeToken<List<Products>>() {}.type

                    var listOrderDetails:List<Products>?= Gson().fromJson(jsonObject.getAsJsonObject("data").getAsJsonArray("order_details"),activeOrderType)

                    // activeOrderAdapter.setData(listActiveOrders!!)
                    if (::orderDetailsAdapter.isInitialized) {
                        if (listOrderDetails != null) {
                            orderDetailsAdapter.setData(listOrderDetails)
                        }
                    } else {
                        orderDetailsAdapter = OrderDetailsSingleAdapter(applicationContext)
                        binding.recyclerOrderDetails.layoutManager = LinearLayoutManager(applicationContext)
                        binding.recyclerOrderDetails.adapter = orderDetailsAdapter
                        if (listOrderDetails != null) {
                            orderDetailsAdapter.setData(listOrderDetails)
                        }
                    }

                    if (listOrderDetails != null) {
                        getAddress(listOrderDetails.get(0).address_id)
                    }

                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("strRes ","Calling api 3");
                customDialog.closeDialog()
            }

        })
    }

    private fun getAddress(addressId: String?) {
        var hashMap = HashMap<String, String> (2)
        hashMap.putIfAbsent("address_id",addressId.toString());
        var request= RetrofitClient.apiInterface.getAddress(hashMap )
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                var strRes= response.body();
                Log.d("strRes ",strRes.toString());

                Log.d("strRes ","Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                if(jsonObject!!.get("status").asString=="200")
                {

                    val activeOrderType: Type = object : TypeToken<List<GetAddress>>() {}.type

                    var address_ :List<GetAddress>?= Gson().fromJson(jsonObject.getAsJsonObject("data").getAsJsonArray("address_detail"),activeOrderType)

                    if (address_ != null) {
                        binding.txtAddress.text = address_.get(0).name+", "+address_.get(0).house_number+", "+address_.get(0).landmark+", "+address_.get(0).city+", "+address_.get(0).state+", "+address_.get(0).country
                    }

                }
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("strRes ","Calling api 3");
            }
        })
    }

    fun getAOrderDetails() {
        val userdataStr= MyPref.getUser(applicationContext)
        val jsobObj= JSONObject(userdataStr);
        //  val email=jsobObj.getString("email");
        val userId=jsobObj.getString("user_id");
        var hashMap = HashMap<String, String> (2)
        hashMap.putIfAbsent("order_id",orderid);
        hashMap.putIfAbsent("user_id",userId);
        hashMap.putIfAbsent("cart_id","1");
        val customDialog= CustomDialog(this@OrderDetailsActivity);
        customDialog.showDialog(this@OrderDetailsActivity,true)
        var request= RetrofitClient.apiInterface.orderDetails(hashMap )
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                customDialog.closeDialog()
                var strRes= response.body();
                //strRes=strRes.replace("!!","")
                Log.d("strRes ",strRes.toString());

                Log.d("strRes ","Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }


                if(jsonObject!!.get("status").asString=="200")
                {

                    val activeOrderType: Type = object : TypeToken<List<OrderDetails>>() {}.type

                   // var listDetails:List<OrderDetails>?= Gson().fromJson(jsonObject.getAsJsonObject("data").getAsJsonArray("order_details"),activeOrderType)


                    val listDetails: List<OrderDetails>? = Gson().fromJson(
                        jsonObject.getAsJsonObject("data").getAsJsonArray("order_details"),
                        activeOrderType
                    )

                    // activeOrderAdapter.setData(listActiveOrders!!)
                    listDetails?.let {
                        Log.d("Order Details Data","Order Details Data ${listDetails?.size}")
                        updateUI(listDetails)
                    }

                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("strRes ","Calling api 3");
                customDialog.closeDialog()
            }
        })
    }


    fun updateUI(listDetails:List<OrderDetails>) {
        if(listDetails!=null&&listDetails.size>0)
        {
            val orderDetails=listDetails.get(0)
            binding.txtOrderId.text="Order #"+orderDetails.uniqueId

            //date
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy - hh:mm a", Locale.getDefault())
            try {
                val date = inputFormat.parse(orderDetails.orderedDate)
                binding.txtOrderDate.text = outputFormat.format(date!!)
            } catch (e: Exception) {
                binding.txtOrderDate.text = "Invalid date"
            }

            binding.txtSubTotal.text= "₹"+orderDetails.subTotal
            binding.txtTax.text="₹"+orderDetails.tax
            binding.txtCostNo.text="₹"+orderDetails.total
            binding.txtOrderStatus.text=orderDetails.status
        }
    }


}