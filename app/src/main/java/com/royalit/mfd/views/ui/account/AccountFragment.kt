package com.royalit.mfd.views.ui.account

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.royalit.mfd.R
import com.royalit.mfd.services.RetrofitClient
import com.royalit.mfd.utils.MyPref
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txt_profile_name=getView()?.findViewById<TextView>(R.id.txt_profile_name)
        val img_profile=getView()?.findViewById<ImageView>(R.id.img_profile)
        val userdataStr= MyPref.getUser(requireActivity())


        val sharedPref = requireActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("full_name", "Hello")
        val profileImage = sharedPref.getString("profileImage", "Hello")
        txt_profile_name?.text =userName
        if (img_profile != null) {
            Glide.with(requireActivity())
                .load("${RetrofitClient.Profile_Image_PAth}/$profileImage")
                .placeholder(R.drawable.dummy_profile)
                .into(img_profile)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}