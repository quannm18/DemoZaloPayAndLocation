package com.quannm18.demozalopay.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class District(
    @SerializedName("code") @Expose val code: String,
    @SerializedName("name") @Expose val name: String,
    @SerializedName("name_with_type") @Expose val name_with_type: String,
    @SerializedName("parent_code") @Expose val parent_code: String,
    @SerializedName("path") @Expose val path: String,
    @SerializedName("path_with_type") @Expose val path_with_type: String,
    @SerializedName("slug") @Expose val slug: String,
    @SerializedName("type") @Expose val type: String
)