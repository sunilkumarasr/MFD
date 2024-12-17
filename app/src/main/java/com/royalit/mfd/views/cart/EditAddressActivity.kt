package com.royalit.mfd.views.cart

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.royalit.mfd.R
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.ActivityAddAddressBinding
import com.royalit.mfd.databinding.ActivityEditAddressBinding
import com.royalit.mfd.models.OrderDetails
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.MyPref
import com.royalit.mfd.utils.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class EditAddressActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditAddressBinding

    lateinit var id:String
    lateinit var houseNumber:String
    lateinit var landmark:String
    lateinit var city:String
    lateinit var state:String
    lateinit var country:String
    lateinit var pincode:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)


        id= intent.getStringExtra("id").toString()
        houseNumber= intent.getStringExtra("houseNumber").toString()
        landmark= intent.getStringExtra("landmark").toString()
        city= intent.getStringExtra("city").toString()
        state= intent.getStringExtra("state").toString()
        country= intent.getStringExtra("country").toString()
        pincode= intent.getStringExtra("pincode").toString()


        binding.edittextHno.setText(houseNumber)
        binding.edittextNearLocation.setText(landmark)
        binding.edittextCity.setText(city)
        binding.edittextState.setText(state)
        binding.edittextCountry.setText(country)
        binding.edittextPincode.setText(pincode)


    }


    fun saveaddress(view: View){
        var house_number=binding.edittextHno.text.toString().trim()
        var landmark=binding.edittextNearLocation.text.toString().trim()
        var city=binding.edittextCity.text.toString().trim()
        var state=binding.edittextState.text.toString().trim()
        var country=binding.edittextCountry.text.toString().trim()
        var pincode=binding.edittextPincode.text.toString().trim()


        if(
            house_number.isEmpty()||landmark.isEmpty()||
            city.isEmpty()||state.isEmpty()||
            country.isEmpty()|| pincode.isEmpty()
        )
        {
            Utils.showMessage("Fill all details",applicationContext)
            return
        }


        val userdataStr= MyPref.getUser(applicationContext)

        val jsobObj= JSONObject(userdataStr);

        //  val email=jsobObj.getString("email");
        val userId=jsobObj.getString("user_id");
        val mobile_number=jsobObj.getString("mobile");
        val userName = jsobObj.getString("full_name");
        var hashMap = HashMap<String, String> (2)
        hashMap.putIfAbsent("address_id",id);
        hashMap.putIfAbsent("user_id",userId);
        hashMap.putIfAbsent("name",userName);
        hashMap.putIfAbsent("mobile_number",mobile_number);
        hashMap.putIfAbsent("house_number",house_number);
        hashMap.putIfAbsent("landmark",landmark);
        hashMap.putIfAbsent("city",city);
        hashMap.putIfAbsent("state",state);
        hashMap.putIfAbsent("country",country);
        hashMap.putIfAbsent("pincode",pincode);
        val customDialog= CustomDialog(this@EditAddressActivity);
        customDialog.showDialog(this@EditAddressActivity,true)
        var request= RetrofitClient.apiInterface.updateAddress(hashMap )
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                customDialog.closeDialog()
                var strRes= response.body();
                //strRes=strRes.replace("!!","")
                Log.d("strRes ",strRes.toString());
                Log.d("strRes ","Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                Utils.showMessage("success",applicationContext)
                finish()


//                if(jsonObject!!.get("status").asString=="200")
//                {
//                    val activeOrderType: Type = object : TypeToken<List<OrderDetails>>() {}.type
//                    var listOrderDetails:List<OrderDetails>?= Gson().fromJson(jsonObject.getAsJsonObject("data").getAsJsonArray("order_details"),activeOrderType)
//                    // activeOrderAdapter.setData(listActiveOrders!!)
//                    listOrderDetails.let {
//                        Log.d("Order Details Data","Order Details Data ${listOrderDetails?.size}")
//                    }
//                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("strRes ","Calling api 3");
                customDialog.closeDialog()
            }

        })

    }


}