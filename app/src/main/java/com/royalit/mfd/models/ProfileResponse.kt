package com.royalit.mfd.models

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ProfileData? = null
)

data class ProfileData(
    @SerializedName("profile_data") var userData: List<ProfileUser>? = null
)

data class ProfileUser(
    @SerializedName("id") var id: String? = null,
    @SerializedName("full_name") var fullName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("mobile") var mobile: String? = null,
    @SerializedName("profile_image") var profileImage: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("firebase_token") var firebaseToken: String? = null,
    @SerializedName("created_at") var createdAt: String? = null
)

