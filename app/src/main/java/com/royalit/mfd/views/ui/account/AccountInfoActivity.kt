package com.royalit.mfd.views.ui.account

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.royalit.mfd.R
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.ActivityAccountInfoBinding
import com.royalit.mfd.models.ProfileResponse
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.MyPref
import com.royalit.mfd.utils.Utils
import com.royalit.mfd.views.home.DashboardActivity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AccountInfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityAccountInfoBinding

//    val requestPermissions = registerForActivityResult(RequestMultiplePermissions()) { results ->
//        val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            results[READ_MEDIA_IMAGES] == true
//        } else {
//            results[READ_EXTERNAL_STORAGE] == true
//        }
//
//        if (permissionGranted) {
//            val intent = Intent(Intent.ACTION_PICK).apply {
//                type = "image/*" // Select images only
//            }
//            startActivityForResult(intent, IMAGE_PICK_CODE)
//        } else {
//            Utils.showMessage("Please grant permissions to select images.", applicationContext)
//        }
//    }

    //image selection
    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    var userId:String=""
    var userName:String=""
    var userProfileImage:String=""
    var base64Image=""
    var imageName=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAccountInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userdataStr= MyPref.getUser(applicationContext)
        val jsobObj= JSONObject(userdataStr);

        userId=jsobObj.getString("user_id");


        fetchProfile()


        binding.imgEdit.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }


        binding.btnRegister.setOnClickListener {
            updateProfile()
        }

        binding.lnrBack.setOnClickListener {
            finish()
        }
    }

    fun updateProfile(){

        val fullname=binding.edittextName.text.toString().trim()
        val mobile=binding.edittextPhone.text.toString().trim()
        val email=binding.edittextEmail.text.toString().trim()

        if(fullname.isEmpty())
        {
            Utils.showMessage("Enter full name", applicationContext)
            return
        }
        if(mobile.length<10)
        {
            Utils.showMessage("Enter valid mobile number", applicationContext)
            return
        }
        if(email.isEmpty())
        {
            Utils.showMessage("Enter Email", applicationContext)
            return
        }

        // Prepare form data
        val userId_ = RequestBody.create(MultipartBody.FORM, userId.toString())
        val name_ = RequestBody.create(MultipartBody.FORM, fullname)
        val mobile_ = RequestBody.create(MultipartBody.FORM, mobile)
        val email_ = RequestBody.create(MultipartBody.FORM, email)

        val body: MultipartBody.Part
        if (selectedImageUri != null) {
            val file = File(getRealPathFromURI(selectedImageUri!!))
            val requestFile = RequestBody.create(MultipartBody.FORM, file)
            body = MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
        } else {
            //send empty image
            body = createEmptyImagePart()
        }

        if(!Utils.checkConnectivity(applicationContext))
        {
            Utils.showMessage("Please check your connection ", applicationContext)
            return
        }
        //showMessage("Calling ",applicationContext)
        val customDialog= CustomDialog(applicationContext);
        customDialog.showDialog(this@AccountInfoActivity,true)
        RetrofitClient.apiInterface.updatenewProfile(userId_, name_, mobile_, email_, body).enqueue(object : Callback<Any> {

            override fun onResponse(call: Call<Any>, response: Response<Any>) {

                customDialog.closeDialog()
                var strRes= response.body();
                Log.d("","Call succeess ${response.code()}")

                Log.d("strRes ",strRes.toString());

                Log.d("strRes ","Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                if(jsonObject!!.get("status").asString=="200" || jsonObject!!.get("status").asString=="400")
                {
                    Utils.showMessage("Success", applicationContext)

                    startActivity(Intent(applicationContext, DashboardActivity::class.java))

                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                customDialog.closeDialog()
                Utils.showMessage("Try again", applicationContext)
            }

        }
        )
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //single image selection
        if (data != null) {
            selectedImageUri = data.data!!
        }
        val file = File(getRealPathFromURI(selectedImageUri!!))
        imageName = file.name
        //set image
        Glide.with(this)
            .load(selectedImageUri)
            .placeholder(R.drawable.img_account)
            .into(binding.imgProfile)
    }

    //update profile
    private fun createEmptyImagePart(): MultipartBody.Part {
        // Create an empty RequestBody
        val requestFile = RequestBody.create(MultipartBody.FORM, ByteArray(0))
        return MultipartBody.Part.createFormData("image", "", requestFile)
    }

    fun uploadIMage(){

        if(base64Image.isEmpty())
        {
            Utils.showMessage("Please try again",applicationContext)
            return
        }
        if(imageName.isEmpty())
        {
            imageName="$userName.jpeg"
        }

        var hashMap = HashMap<String,String> ()
        hashMap.put("user_id",userId);
        hashMap.put("file_name",imageName);
        hashMap.put("file_content",base64Image);

        if(!Utils.checkConnectivity(applicationContext))
        {
            Utils.showMessage("Please check your connection ", applicationContext)
            return
        }
        //showMessage("Calling ",applicationContext)
        val customDialog= CustomDialog(applicationContext);
        customDialog.showDialog(this@AccountInfoActivity,true)
        RetrofitClient.apiInterface.uploadImage(hashMap).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                customDialog.closeDialog()
                var strRes= response.body();
                Log.d("","Call succeess ${response.code()}")

                Log.d("strRes ",strRes.toString());

                Log.d("strRes ","Calling api 2");
                val jsonObject: JsonObject? = strRes?.let { Utils.getJsonObject(it) }

                try {
                    if (jsonObject != null) {
                        val message = jsonObject.get("message")?.asString ?: "No message found"

                        if (jsonObject.get("status")?.asString == "200") {
                            val data = jsonObject.getAsJsonObject("data")
                            userProfileImage = data?.get("file_name")?.asString ?: ""
                            updateProfile()
                        }
                    } else {
                        Log.e("API Response", "jsonObject is null")
                    }
                } catch (e: Exception) {
                    Log.e("API Error", "Error parsing JSON: ${e.localizedMessage}")
                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                customDialog.closeDialog()
                Utils.showMessage("Try again", applicationContext)
            }

        }
        )



    }

    fun fetchProfile() {
        val hashMap = hashMapOf("user_id" to (userId ?: ""))

        if (!Utils.checkConnectivity(applicationContext)) {
            Utils.showMessage("Please check your connection", applicationContext)
            return
        }

        val customDialog = CustomDialog(applicationContext)
        customDialog.showDialog(this@AccountInfoActivity, true)

        RetrofitClient.apiInterface.fetchProfile(hashMap).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                customDialog.closeDialog()

                val strRes = response.body()
                if (strRes == null) {
                    Utils.showMessage("Invalid response from server", applicationContext)
                    return
                }

                if (strRes.status == "400" && strRes.data?.userData != null) {
                    val userData = strRes.data!!.userData

                    // Save user data to preferences
                    val gson = Gson()
                    //MyPref.setUser(applicationContext, gson.toJson(userData))

                    // Update UI with user data
                    if (userData != null) {
                        binding.edittextName.setText(userData.get(0).fullName ?: "")
                        binding.edittextPhone.setText(userData.get(0).mobile ?: "")
                        binding.edittextEmail.setText(userData.get(0).email ?: "")
                        userData.get(0).profileImage?.let { profileImage ->
                            Glide.with(this@AccountInfoActivity)
                                .load("${RetrofitClient.Profile_Image_PAth}/$profileImage")
                                .placeholder(R.drawable.dummy_profile)
                                .into(binding.imgProfile)
                        }

                        val sharedPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("full_name", userData.get(0).fullName ?: "")
                        editor.putString("email", userData.get(0).email ?: "")
                        editor.putString("mobile", userData.get(0).mobile ?: "")
                        editor.apply()

                    }

                } else {
                    Utils.showMessage(strRes.message ?: "Unable to fetch profile data", applicationContext)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                customDialog.closeDialog()
                Utils.showMessage("Try again: ${t.message}", applicationContext)
            }
        })
    }



}