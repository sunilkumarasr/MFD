package com.royalit.mfd.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.royalit.mfd.R
import com.royalit.mfd.customviews.CustomDialog
import com.royalit.mfd.databinding.ActivityLoginBinding
import com.royalit.mfd.models.LoginResponse
import com.royalit.mfd.services.RetrofitClient.apiInterface
import com.royalit.mfd.utils.Utils
import com.royalit.mfd.utils.Utils.Companion.checkConnectivity
import com.royalit.mfd.utils.Utils.Companion.showMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding


    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //open close eye
        binding.edittextPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.edittextPassword.compoundDrawables[2] // Index 2 for drawableEnd
                if (drawableEnd != null && event.rawX >= (binding.edittextPassword.right - drawableEnd.bounds.width())) {
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
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

    }

    fun login(view: View) {
        val email_ = binding.edittextEmail.text.toString().trim()
        val password = binding.edittextPassword.text.toString().trim()
        if (!isValidEmail(email_)) {
            Utils.showMessage("Enter valid Email address", applicationContext)
            return
        }
        if (email_.isEmpty()) {
            Utils.showMessage("Please enter email", applicationContext)
            return
        }
        if (password.isEmpty()) {
            Utils.showMessage("Please enter password", applicationContext)
            return
        }

        var hashMap = HashMap<String, String>(2)
        hashMap.putIfAbsent("email", email_);
        hashMap.putIfAbsent("password", password);

        if (!checkConnectivity(applicationContext)) {
            showMessage("Please check your connection ", applicationContext)
            return
        }
        //showMessage("Calling ",applicationContext)
        val customDialog = CustomDialog(applicationContext);
        customDialog.showDialog(this@LoginActivity, true)
        apiInterface.login(hashMap).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                customDialog.closeDialog()
                var strRes = response.body();
                //strRes=strRes.replace("!!","")
                Log.d("strRes ", strRes.toString());
                if (strRes!!.status == "400") {
                    showMessage(strRes!!.message!!, applicationContext)
                }

                if (strRes!!.status == "200") {
                    val intent = Intent(applicationContext, OTPActivity::class.java)
                    val email_ = strRes!!.data!!.userData!!.email
                    val user_id = strRes!!.data!!.userData!!.userId
                    Log.d("email_", email_ + "")
                    intent.putExtra("email_", email_)
                    intent.putExtra("password", password)
                    intent.putExtra("user_id", user_id)
                    startActivity(intent)
                    finish()
                    return
                }
                /* val gson=Gson()
                val loginResponse= gson.fromJson<LoginResponse>(strRes, LoginResponse::class.java)
               */  Log.d("", "Call succeess ${strRes!!.status}")

            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                customDialog.closeDialog()
                showMessage("Login Failed", applicationContext)
            }

        }
        )
        // startActivity(Intent(applicationContext,OTPActivity::class.java))
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun register(view: View) {
        startActivity(Intent(applicationContext, SigupActivity::class.java))
        finish()
    }

    fun forgotpass(view: View) {
        startActivity(Intent(applicationContext, ForgotActivity::class.java))
        finish()
    }


}