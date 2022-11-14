package com.quannm18.demozalopay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.quannm18.demozalopay.adapter.AddressAdapter
import com.quannm18.demozalopay.databinding.ActivityAddressBinding
import com.quannm18.demozalopay.utils.Constants.Companion.REQUEST_CODE
import com.quannm18.demozalopay.utils.ManagePermissions
import com.quannm18.demozalopay.viewmodel.GSonViewModel
import kotlinx.coroutines.launch
import java.util.*


class AddressActivity : AppCompatActivity() {
    private val gSonViewModel: GSonViewModel by viewModels()
    private lateinit var binding: ActivityAddressBinding
    private val addressAdapter: AddressAdapter by lazy {
        AddressAdapter()
    }
    val list = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val managePermissions: ManagePermissions by lazy {
        ManagePermissions(this, list.toList(), RESULT_OK)
    }
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    val hasGps: Boolean = false
    var hasNetwork: Boolean = false

    var mAddressChoose = ""
    private var locationRequest: LocationRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        listenerLiveData()
        listener()
    }

    private fun initData() {
        lifecycleScope.launchWhenCreated {
            addressAdapter.initData(gSonViewModel.getListCityAddress())
        }
        locationRequest = LocationRequest.create()
            .setPriority(PRIORITY_HIGH_ACCURACY)
            .setInterval(5000)
            .setFastestInterval(2000)
    }

    private fun initView() {
        binding.rcvAddress.visibility = View.VISIBLE
        binding.rcvAddress.apply {
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(this@AddressActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@AddressActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun listenerLiveData() {
        addressAdapter.event.observe(this) {
            when (it) {
                is com.quannm18.demozalopay.model.Address -> {
                    lifecycleScope.launch {
                        Log.e(TAG, "listenerLiveData: $it")
                        when (it.type) {
                            "0" -> {
                                binding.tvCity.text = "City " + it.name
                                addressAdapter.initData(gSonViewModel.getListDistrictAddress(it.code))
                            }
                            "1" -> {
                                binding.tvDistricts.text = "District " + it.name
                                addressAdapter.initData(gSonViewModel.getListCommuneAddress(it.code))
                            }
                            else -> {
                                binding.tvCommune.text = "Commune " + it.name
                                mAddressChoose = it.nameWithType
                                binding.rcvAddress.visibility = View.GONE
                                binding.btnSuccess.visibility = View.VISIBLE
                                binding.edDetail.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun listener() {
        binding.btnGetCurrentLocation.setOnClickListener {
//            getAddressInfo(20.987643359068667, 105.87702873961182)
            getCurrentLocation()
        }

        binding.btnSuccess.setOnClickListener {
            setResult(
                REQUEST_CODE,
                Intent().putExtra(
                    "address",
                    " ${binding.edDetail.text.toString()}, $mAddressChoose"
                )
            )
            finish()
        }
    }

    private fun getAddressInfo(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            Log.e(TAG, "lat $latitude - long $longitude")
            val geocoder = Geocoder(this@AddressActivity, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)

            val address = addresses[0].getAddressLine(0)
            Log.e(TAG, "${address} ")
            setResult(REQUEST_CODE, Intent().putExtra("address", address))
            finish()
//            val city: String = addresses[0].adminArea
//            val subAdmin: String = addresses[0].subAdminArea
//            val thoroughfare: String = addresses[0].thoroughfare

        }
    }

    companion object {
        val TAG = javaClass.simpleName
    }

    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE
            )
            false
        } else {
            true
        }
    }

    private fun getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(this)
                        .requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                LocationServices.getFusedLocationProviderClient(this@AddressActivity)
                                    .removeLocationUpdates(this)
                                if (locationResult != null && locationResult.locations.size > 0) {
                                    val index = locationResult.locations.size - 1
                                    val latitude = locationResult.locations[index].latitude
                                    val longitude = locationResult.locations[index].longitude
                                    getAddressInfo(latitude, longitude)
                                }
                            }
                        }, Looper.getMainLooper())
                } else {
                    turnOnGPS()
                }
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            }
        }
    }

    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(
            applicationContext
        )
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener(OnCompleteListener<LocationSettingsResponse?> { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                Toast.makeText(this, "GPS is already tured on", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(this, 2)
                    } catch (ex: SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        })
    }

    private fun isGPSEnabled(): Boolean {
        var locationManager: LocationManager? = null
        var isEnabled = false
        if (locationManager == null) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isEnabled
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    getCurrentLocation()
                } else {
                    turnOnGPS()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getCurrentLocation()
            }
        }
    }

}