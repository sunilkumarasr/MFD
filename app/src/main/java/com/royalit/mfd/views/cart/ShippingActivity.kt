package com.royalit.mfd.views.cart

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.royalit.mfd.R
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.ActivityShippingBinding
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.MyPref
import com.royalit.mfd.utils.Utils
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar

class ShippingActivity : AppCompatActivity() {

    lateinit var binding: ActivityShippingBinding

    var startDate="";
    var startTime="";
    var startDateTime:Long=0
    var dateformat= SimpleDateFormat("E, dd MMM")
    var dateformat2= SimpleDateFormat("yyyy-MM-dd")
    var timeformat= SimpleDateFormat("hh:mm")
    var timeformat2= SimpleDateFormat("hh:mm a")
    var startCalendar: Calendar = Calendar.getInstance()

    var paymentType=-1
    var hno=""
    var landmark=""
    var city=""
    var state=""
    var country=""
    var pincode=""
    var addressid=""
    var subtotal=0.0
    var total=0.0
    var tax=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityShippingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAddAddress.setOnClickListener {
            var intent=Intent(applicationContext,AddressListActivity::class.java)
                intent.putExtra("showButton",true)
            startActivityForResult(intent,200)
        }
        binding.radioGrp.setOnCheckedChangeListener(object:OnCheckedChangeListener{
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                if(p1==R.id.radio_one)
                {
                    paymentType=2;
                }else if(p1==R.id.radio_cash)
                {
                    paymentType=1;
                }
            }

        })
        CardData.cartListMap.value?.forEach {
            subtotal=subtotal+(it.price.toDouble()*it.quantity)
            tax=0.0
        }
        total=subtotal+tax
        binding.txtCostNo.text="₹ ${total}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==200&&resultCode==200)
        {
            hno=data?.getStringExtra("hno").toString()
            landmark=data?.getStringExtra("landmark").toString()
            city=data?.getStringExtra("city").toString()
            state=data?.getStringExtra("state").toString()
            country=data?.getStringExtra("country").toString()
            pincode=data?.getStringExtra("pincode").toString()
            addressid=data?.getStringExtra("id").toString()
            binding.txtSelectedAddress.text="$hno, $landmark, $city, $state, $country"
        }
    }

    fun paynow(view: View) {
        if(startDate.isEmpty()||startTime.isEmpty())
        {
            Utils.showMessage("Select Pickup date",applicationContext)
            return
        }
        if(addressid.isEmpty())
        {
            Utils.showMessage("Please add address",applicationContext)
            return
        }
        val userdataStr = MyPref.getUser(applicationContext)
        val jsonObj = JSONObject(userdataStr)
        val userId = jsonObj.getString("user_id")

        val request = HashMap<String, Any>()
        request["user_id"] = userId
        request["address_id"] = addressid
        request["card_id"] = "0"
        request["payment_method"] = paymentType.toString()
        request["sub_total"] = subtotal.toString()
        request["tax"] = tax.toString()
        request["total"] = total.toString()
        request.put("expected_pickup_date",startDate+" "+startTime)

        //request["expected_delivery_date"] = "2023-06-26 10:00:00"
        val productIds = mutableListOf<String>()
        val prices = mutableListOf<String>()
        val quantities = mutableListOf<String>()

        CardData.cartListMap.value?.forEach {
            productIds.add(it.id.toString())
            prices.add(it.price.toString())
            quantities.add(it.quantity.toString())
        }

        request["product_id"] = productIds
        request["price"] = prices
        request["quantity"] = quantities

        Log.e("Request Payload", Gson().toJson(request))

        if(paymentType==1)
        {
            Log.e("request_s ","${request.toString()}")
            placeOrder(request)
        }
        else if(paymentType==2)
            Utils.showMessage("Payment Gateway Integration",applicationContext)
        else
            Utils.showMessage("Choose Payment Type",applicationContext)
    }

    private fun placeOrder(hashMap:Any) {
        var request= RetrofitClient.apiInterface.placeOrder(hashMap,"E28DA37796A786FAB9DC6427B1D19" )
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                var strRes= response.body();
                if(strRes==null)
                {
                    return
                }
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }
                if(jsonObject!!.get("status").asString=="200")
                {
                    Utils.showMessage(jsonObject!!.get("message").asString,applicationContext)
                    var listAddress= jsonObject.getAsJsonObject("data").getAsJsonObject("order_data").get("reference_id").asString
                    listAddress.let {
                        Log.d("Order Details Data","Order Details Data ${listAddress}")
                      var intent=  Intent(applicationContext,PlaceOrderActivity::class.java)
                        intent.putExtra("orderid",listAddress)
                        startActivity(intent)

                    }
                }
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("strRes ","Calling api 3");
            }
        })
    }

    fun pickuptime(view: View) {
        pickDateTime()
    }
    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        val datePickerDialog=  DatePickerDialog(this@ShippingActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(this@ShippingActivity, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month+1, day, hour, minute)

                    var monthsdate="${month+1}"
                    if(monthsdate.length==1)
                        monthsdate="0$monthsdate"

                    var daydate="$day"
                    if(daydate.length==1)
                        daydate="0$daydate"

                    startDate="$year-$monthsdate-$daydate"
                    startTime="$hour:$minute"
                    binding.inputPickip.setText(dateformat.format(dateformat2.parse(startDate))+"\n"+timeformat2.format(timeformat.parse(startTime)))

                    startCalendar.set(year,month+1,day,hour,minute)
                    Log.d("date start","$startDate $startTime")
                    Log.d("date start",dateformat.format( dateformat2.parse(startDate))+"\n"+timeformat2.format(timeformat.parse(startTime)))

            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay)
        datePickerDialog.datePicker.minDate=Calendar.getInstance().timeInMillis
        datePickerDialog.show()
    }


    fun back(view: View) {
        finish()
    }

}