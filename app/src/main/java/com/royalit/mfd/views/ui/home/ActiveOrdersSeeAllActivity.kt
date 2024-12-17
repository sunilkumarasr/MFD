package com.royalit.mfd.views.ui.home

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.royalit.mfd.R
import com.royalit.mfd.adapters.ActiveOrderAdapter
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.ActivityAboutBinding
import com.royalit.mfd.databinding.ActivityActiveOrdersSeeAllBinding
import com.royalit.mfd.models.ActiveOrder
import com.royalit.mfd.models.OrderDetailModel
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.MyPref
import com.royalit.mfd.utils.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class ActiveOrdersSeeAllActivity : AppCompatActivity() {

    lateinit var binding: ActivityActiveOrdersSeeAllBinding

    lateinit var activeOrderAdapter: ActiveOrderAdapter

    var userId:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityActiveOrdersSeeAllBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val userdataStr= MyPref.getUser(this@ActiveOrdersSeeAllActivity.applicationContext)
        val jsobObj= JSONObject(userdataStr);
        userId=jsobObj.getString("user_id");


        activeOrderAdapter= ActiveOrderAdapter(resources)
        binding.recyclerActiveOrder.layoutManager= LinearLayoutManager(this@ActiveOrdersSeeAllActivity.applicationContext)
        binding.recyclerActiveOrder.adapter=activeOrderAdapter

        getActiveORDERS()


        binding.lnrBack.setOnClickListener {
            finish()
        }

    }


    fun getActiveORDERS() {
        /*val userdataStr= MyPref.getUser(requireActivity().applicationContext)

        val jsobObj= JSONObject(userdataStr);

        //  val email=jsobObj.getString("email");
        val userId=jsobObj.getString("user_id");*/
        var hashMap = HashMap<String, String>(2)
        hashMap.putIfAbsent("user_id", userId);
        val customDialog = CustomDialog(this@ActiveOrdersSeeAllActivity.applicationContext);
        customDialog.showDialog(this@ActiveOrdersSeeAllActivity, true)
        var request = RetrofitClient.apiInterface.orders(hashMap)
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                customDialog.closeDialog()
                var strRes = response.body();
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }
                if (jsonObject!!.get("status").asString == "200") {
                    val activeOrderType: Type = object : TypeToken<List<OrderDetailModel>>() {}.type
                    var listActiveOrders: List<OrderDetailModel>? = Gson().fromJson(
                        jsonObject.getAsJsonObject("data")
                            .getAsJsonArray("users_order_data"), activeOrderType
                    )
                    activeOrderAdapter.setData(listActiveOrders!!)
                    listActiveOrders.let {
                        Log.d("users_order_data", "users_order_data ${listActiveOrders?.size}")
                    }
                }
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("strRes ", "Calling api 3");
                customDialog.closeDialog()
            }
        })
    }


}