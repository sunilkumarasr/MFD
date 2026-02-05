package com.royalit.mfd.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.royalit.mfd.R
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.ActivitySigupBinding
import com.royalit.mfd.models.LoginResponse
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.Utils
import com.royalit.mfd.utils.Utils.Companion.showMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySigupBinding

    private var isPasswordVisible1 = false
    private var isPasswordVisible2 = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySigupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //open close eye
        binding.edittextPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.edittextPassword.compoundDrawables[2] // Index 2 for drawableEnd
                if (drawableEnd != null && event.rawX >= (binding.edittextPassword.right - drawableEnd.bounds.width())) {
                    isPasswordVisible1 = !isPasswordVisible1
                    if (isPasswordVisible1) {
                        binding.edittextPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.edittextPassword.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(this, R.drawable.eye_open), null
                        )
                    } else {
                        binding.edittextPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.edittextPassword.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(this, R.drawable.eye_close), null
                        )
                    }
                    binding.edittextPassword.setSelection(binding.edittextPassword.text?.length ?: 0) // Maintain cursor position
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        //open close eye
        binding.edittextConfPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.edittextConfPassword.compoundDrawables[2] // Index 2 for drawableEnd
                if (drawableEnd != null && event.rawX >= (binding.edittextConfPassword.right - drawableEnd.bounds.width())) {
                    isPasswordVisible2 = !isPasswordVisible2
                    if (isPasswordVisible2) {
                        binding.edittextConfPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.edittextConfPassword.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(this, R.drawable.eye_open), null
                        )
                    } else {
                        binding.edittextConfPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.edittextConfPassword.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, ContextCompat.getDrawable(this, R.drawable.eye_close), null
                        )
                    }
                    binding.edittextConfPassword.setSelection(binding.edittextConfPassword.text?.length ?: 0) // Maintain cursor position
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }


    }
    fun register(view: View) {

        val fullname=binding.edittextName.text.toString().trim()
        val mobile=binding.edittextPhone.text.toString().trim()
        val email=binding.edittextEmail.text.toString().trim()
        val password=binding.edittextPassword.text.toString().trim()
        val confirmPass=binding.edittextConfPassword.text.toString().trim()


        if(fullname.isEmpty())
        {
            showMessage("Enter full name",applicationContext)
            return
        }
        if(mobile.isEmpty())
        {
            showMessage("Enter mobile",applicationContext)
            return
        }
        if(email.isEmpty())
        {
            showMessage("Enter email",applicationContext)
            return
        }
        if(mobile.length<10)
        {
            showMessage("Enter valid mobile number",applicationContext)
            return
        }
        if (!validateMobileNumber(mobile)) {
            showMessage("Enter valid mobile", applicationContext)
            return
        }
        if (!isValidEmail(email)) {
            showMessage("Enter valid Email address", applicationContext)
            return
        }
        if(password.length<6)
        {
            showMessage("Password should be minimum  6 characters",applicationContext)
            return
        }
        if(!password.equals(confirmPass))
        {
            showMessage("Confirm Password should should match with Password",applicationContext)
            return
        }

        var hashMap = HashMap<String,String> ()
        hashMap.put("full_name",fullname);
        hashMap.put("mobile",mobile);
        hashMap.put("email",email);
        hashMap.put("password",password);

        if(!Utils.checkConnectivity(applicationContext))
        {
            Utils.showMessage("Please check your connection ", applicationContext)
            return
        }
        //showMessage("Calling ",applicationContext)
        val customDialog= CustomDialog(applicationContext);
        customDialog.showDialog(this@SigupActivity,true)
        RetrofitClient.apiInterface.register(hashMap).enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                customDialog.closeDialog()
                var strRes= response.body();
                Log.d("","Call succeess ${response.code()}")

                if(response.code()!=200)
                {
                    Utils.showMessage("Please try again", applicationContext)
                    return
                }
                Log.d("","Call succeess ${strRes}")
                Utils.showMessage(strRes!!.message!!, applicationContext)
                 if(strRes!!.status=="200")
                {
                   // MyPref.setUser(applicationContext,gson.toJson(strRes!!.data!!.userData))
                    val intent= Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    return
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                customDialog.closeDialog()
                Utils.showMessage("Already registered with us", applicationContext)
            }
        }
        )

    }

    private fun validateMobileNumber(mobile: String): Boolean {
        val mobilePattern = "^[6-9][0-9]{9}\$"
        return Patterns.PHONE.matcher(mobile).matches() && mobile.matches(Regex(mobilePattern))
    }


    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun login(view: View) {
        startActivity(Intent(applicationContext,LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext,LoginActivity::class.java))
        finish()
    }
}

