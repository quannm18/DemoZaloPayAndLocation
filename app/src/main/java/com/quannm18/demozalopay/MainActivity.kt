package com.quannm18.demozalopay

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.quannm18.demozalopay.databinding.ActivityMainBinding
import com.quannm18.demozalopay.utils.Constants.Companion.REQUEST_CODE
import com.quannm18.demozalopay.zalopay.Api.CreateOrder
import org.json.JSONObject
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var edMoney: EditText
    private lateinit var btnCreateToken: Button
    private lateinit var btnOrder: Button
    private lateinit var tvToken: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)
        initData()
        initView()
        listenerLiveData()
        listener()
    }

    private val getAddress =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == REQUEST_CODE && result.data != null) {
                result.data?.let {
                    binding.btnGetAddress.text = it.getStringExtra("address")
                }
            }
            Log.e(TAG, "resultCode: ${result.resultCode}")
            Log.e(TAG, "result: ${result.data}")
        }

    private fun initData() {

    }

    private fun initView() {
        edMoney = binding.edtMoney
        btnCreateToken = binding.btnCreateOrder
        btnOrder = binding.btnOrder
        tvToken = binding.tvToken
    }

    private fun listenerLiveData() {

    }

    private fun listener() {
        binding.btnGetAddress.setOnClickListener {
            startGetAddress()
        }
        btnCreateToken.setOnClickListener {
            val orderApi = CreateOrder()
            try {
                val data: JSONObject = orderApi.createOrder(edMoney.text.toString())
                Log.e(TAG, "Amount: ${edMoney.text.toString()} - Data: $data")

                val code = data.getString("return_code")
                Log.e(TAG, "code $code")

                Toast.makeText(applicationContext, "code $code", Toast.LENGTH_SHORT).show()

                if (code.equals("1")) {
                    tvToken.text = data.getString("zp_trans_token")
                    Log.e(TAG, "zp_trans_token ${data.getString("zp_trans_token")}")

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        btnOrder.setOnClickListener {
            val token = tvToken.text.toString()
            ZaloPaySDK.getInstance()
                .payOrder(this, token, "quannm18://app", object : PayOrderListener {
                    override fun onPaymentSucceeded(
                        transactionId: String?,
                        transToken: String?,
                        appTransID: String?
                    ) {
                        runOnUiThread {
                            AlertDialog.Builder(this@MainActivity)
                                .setTitle("Payment Success")
                                .setMessage(
                                    String.format(
                                        "TransactionId: %s - TransToken: %s",
                                        transactionId,
                                        transToken
                                    )
                                )
                                .setPositiveButton(
                                    "OK"
                                ) { dialog, which -> }
                                .setNegativeButton("Cancel", null).show()
                            Log.e(
                                TAG,
                                "transactionId: $transactionId\n" +
                                        "transToken: $transToken\n" +
                                        "appTransID: $appTransID ",
                            )
                        }
                    }

                    override fun onPaymentCanceled(zpTransToken: String?, appTransID: String?) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("User Cancel Payment")
                            .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                            .setPositiveButton(
                                "OK"
                            ) { dialog, which -> }
                            .setNegativeButton("Cancel", null).show()
                        Log.e(
                            TAG,

                            "zpTransToken: $zpTransToken\n" +
                                    "appTransID: $appTransID ",
                        )
                    }

                    override fun onPaymentError(
                        zaloPayError: ZaloPayError?,
                        zpTransToken: String?,
                        appTransID: String?
                    ) {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Payment Fail")
                            .setMessage(
                                String.format(
                                    "ZaloPayErrorCode: %s \nTransToken: %s",
                                    zaloPayError.toString(),
                                    zpTransToken
                                )
                            )
                            .setPositiveButton(
                                "OK"
                            ) { dialog, which -> }
                            .setNegativeButton("Cancel", null).show()
                        Log.e(
                            TAG,
                            "ZaloPayError: $zaloPayError\n" +
                                    "zpTransToken: $zpTransToken\n" +
                                    "appTransID: $appTransID ",
                        )

                    }

                })
        }
    }

    fun mDialog(status: String?, transactionId: String?, transToken: String?) {

        AlertDialog.Builder(this@MainActivity)
            .setTitle("Payment $status")
            .setMessage(
                java.lang.String.format(
                    "TransactionId: %s - TransToken: %s",
                    transactionId,
                    transToken
                )
            )
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> })
            .setNegativeButton("Cancel", null).show()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }

    private fun startGetAddress() {
        val intent = Intent(this, AddressActivity::class.java)
        getAddress.launch(intent)
    }

    companion object {
        val TAG = javaClass.simpleName
    }

}