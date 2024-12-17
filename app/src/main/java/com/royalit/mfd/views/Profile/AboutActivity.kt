package com.royalit.mfd.views.Profile

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.royalit.mfd.R
import com.royalit.mfd.databinding.ActivityAboutBinding
import com.royalit.mfd.databinding.ActivityPrivacyPolicyBinding
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lnrBack.setOnClickListener {
            finish()
        }

        about_Api()

    }

    private fun about_Api() {
        lateinit var request: Call<Any>;
        request= RetrofitClient.apiInterface.newAbout()
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                var strRes= response.body();
                //strRes=strRes.replace("!!","")
                Log.d("strRes ",strRes.toString());
                Log.d("strRes ","Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }
                if(jsonObject!!.get("status").asString=="200")
                {
                    var data=""
                    data=jsonObject!!.getAsJsonObject("data").getAsJsonArray("about_data").get(0).getAsJsonObject().get("about_content").asString
                    var img =jsonObject!!.getAsJsonObject("data").getAsJsonArray("about_data").get(0).getAsJsonObject().get("app_image").asString

                    binding.txtNote.text = Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY)

                    Glide.with(this@AboutActivity)
                        .load("${RetrofitClient.About_IMAGE_PATH}/${img}")
                        .into(binding.imgBanner);
                }
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("strRes ","Calling api 3");
            }
        })
    }

}