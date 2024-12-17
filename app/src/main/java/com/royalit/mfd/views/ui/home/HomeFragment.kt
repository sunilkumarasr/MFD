package com.royalit.mfd.views.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.royalit.mfd.R
import com.royalit.mfd.adapters.ActiveOrderAdapter
import com.royalit.mfd.adapters.ServiceCategoryAdapter
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.FragmentHomeBinding
import com.royalit.mfd.models.ActiveOrder
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.MyPref
import com.royalit.mfd.utils.Utils
import com.royalit.mfd.views.home.AppViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.royalit.mfd.models.OrderDetailModel
import com.royalit.mfd.models.ProfileResponse
import com.royalit.mfd.views.Profile.TermsAndConditionsActivity
import com.royalit.mfd.views.ui.service.ServiceSeeAllActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var serviceCategoryAdapter: ServiceCategoryAdapter
    lateinit var activeOrderAdapter: ActiveOrderAdapter
    private val sharedViewModel: AppViewModel by activityViewModels()
    var userId: String = ""
    var userName: String? = null
    var userProfileImage: String? = null
    var bannerImage: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        serviceCategoryAdapter = ServiceCategoryAdapter()

        activeOrderAdapter = ActiveOrderAdapter(resources)
        binding.recyclerServices.layoutManager = LinearLayoutManager(
            requireActivity().applicationContext,
            RecyclerView.HORIZONTAL,
            false
        )
        binding.recyclerActiveOrder.layoutManager =
            LinearLayoutManager(requireActivity().applicationContext)
        binding.recyclerServices.adapter = serviceCategoryAdapter
        binding.recyclerActiveOrder.adapter = activeOrderAdapter

        binding.txtServiceAll.setOnClickListener {
            startActivity(Intent(requireActivity(), ServiceSeeAllActivity::class.java))
        }

        binding.txtRequestSeeAll.setOnClickListener {
            startActivity(Intent(requireActivity(), ActiveOrdersSeeAllActivity::class.java))
        }

        binding.imgWhatsapp.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://api.whatsapp.com/send?phone=+9059158484&text=Hi%20there")
            )
            startActivity(browserIntent)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.categories.observe(requireActivity(), Observer {
            Log.d("Categories List", "Categories List ${it.size}")
            serviceCategoryAdapter.setData(it)
        })

        val userdataStr = MyPref.getUser(requireActivity().applicationContext)

        val jsobObj = JSONObject(userdataStr);

        //  val email=jsobObj.getString("email");
        userId = jsobObj.getString("user_id");

        getActiveORDERS()

        fetchProfile()

        if (bannerImage == null || bannerImage.isEmpty())
            getBaners()

    }

    private fun getBaners() {
        var hashMap = HashMap<String, String>(2)
        hashMap.putIfAbsent("user_id", userId);
        // val customDialog= CustomDialog(requireActivity().applicationContext);
        // customDialog.showDialog(requireActivity(),true)
        var request = RetrofitClient.apiInterface.about()
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                //  customDialog.closeDialog()

                var strRes = response.body();
                //strRes=strRes.replace("!!","")
                Log.d("strRes ", strRes.toString());

                Log.d("strRes ", "Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                if (jsonObject!!.get("status").asString == "200") {
                    bannerImage =
                        jsonObject!!.getAsJsonObject("data").getAsJsonArray("about_data").get(0)
                            .getAsJsonObject().get("home_banner").asString
                    updateBanners()
                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("strRes ", "Calling api 3");

            }

        })
    }

    fun getActiveORDERS() {
        /*val userdataStr= MyPref.getUser(requireActivity().applicationContext)

        val jsobObj= JSONObject(userdataStr);

        //  val email=jsobObj.getString("email");
        val userId=jsobObj.getString("user_id");*/
        var hashMap = HashMap<String, String>(2)
        hashMap.putIfAbsent("user_id", userId);
        val customDialog = CustomDialog(requireActivity().applicationContext);
        customDialog.showDialog(requireActivity(), true)
        var request = RetrofitClient.apiInterface.orders(hashMap)
        request.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                customDialog.closeDialog()

                var strRes = response.body();
                //strRes=strRes.replace("!!","")
                Log.d("strRes ", strRes.toString());

                Log.d("strRes ", "Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                if (jsonObject!!.get("status").asString == "200") {
                    val activeOrderType: Type = object : TypeToken<List<OrderDetailModel>>() {}.type
                    var listActiveOrders: List<OrderDetailModel>? = Gson().fromJson(
                        jsonObject.getAsJsonObject("data")
                            .getAsJsonArray("users_order_data"), activeOrderType
                    )
                    activeOrderAdapter.setData(listActiveOrders!!)
                    listActiveOrders.let {
                        if(listActiveOrders.size>0)
                        {
                            binding.lnrEmpty.visibility=View.GONE
                        }else{
                            binding.lnrEmpty.visibility=View.VISIBLE
                        }
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


    fun fetchProfile() {
        val hashMap = hashMapOf("user_id" to (userId ?: ""))

        if (!Utils.checkConnectivity(requireActivity())) {
            Utils.showMessage("Please check your connection", requireActivity())
            return
        }

        RetrofitClient.apiInterface.fetchProfile(hashMap).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                val strRes = response.body()
                if (strRes == null) {
                    Utils.showMessage("Invalid response from server", requireActivity())
                    return
                }

                if (strRes.status == "400" && strRes.data?.userData != null) {
                    val userData = strRes.data!!.userData

                    // Save user data to preferences
                    val gson = Gson()
                    //MyPref.setUser(applicationContext, gson.toJson(userData))

                    // Update UI with user data
                    if (userData != null) {

                        binding.txtName.text = "Hi, "+userData.get(0).fullName ?: ""

                        val sharedPref = requireActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("full_name", userData.get(0).fullName ?: "")
                        editor.putString("email", userData.get(0).email ?: "")
                        editor.putString("mobile", userData.get(0).mobile ?: "")
                        editor.putString("profileImage", userData.get(0).profileImage ?: "")
                        editor.apply()

                    }

                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                Utils.showMessage("Try again: ${t.message}", requireActivity())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getBaners()
    }

    fun updateBanners() {
        if (bannerImage == null || bannerImage.isEmpty()) {

            return
        }
        Glide.with(this)
            .load("${RetrofitClient.About_IMAGE_PATH}/${bannerImage}")
            .into(binding.imgBanner);
    }

}