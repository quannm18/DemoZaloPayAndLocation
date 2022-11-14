package com.quannm18.demozalopay.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CityProvince(
    @SerializedName("code") @Expose val code: String,
    @SerializedName("name") @Expose val name: String,
    @SerializedName("name_with_type") @Expose val name_with_type: String,
    @SerializedName("slug") @Expose val slug: String,
    @SerializedName("type") @Expose val type: String
)