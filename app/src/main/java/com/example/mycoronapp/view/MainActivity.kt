package com.example.mycoronapp.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.mycoronapp.R
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.text.NumberFormat

class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showLoading(false)
        getDataGlobalWithIndo()
        getDataProvinsiSumateraUtara(applicationContext)

        negara_indonesia.setOnClickListener(this)
        prov_sumatera_utara.setOnClickListener(this)
        button_expand_provinsi.setOnClickListener(this)
        button_expand_negara.setOnClickListener(this)

    }


    //make a seperated value in number
    private fun numberFormat(value: Int) : String{
        return NumberFormat.getIntegerInstance().format(value)
    }

    //get data global and indo from api
    private fun getDataGlobalWithIndo() {

        val client = AsyncHttpClient()
        val url = "https://api.covid19api.com/summary"

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                val result = responseBody.let { String(it) }
                Log.d("result global : ", result)

                try {
                    val jsonObject = JSONObject(result)
                    val globalJson = jsonObject.getString("Global")
                    val totalConfirmed = JSONObject(globalJson).getString("TotalConfirmed").toInt()
                    val totalDeaths = JSONObject(globalJson).getString("TotalDeaths").toInt()
                    val totalRecovered = JSONObject(globalJson).getString("TotalRecovered").toInt()

                    //we take date data global from country date
                    val countries = jsonObject.getJSONArray("Countries")
                    val dataObject = countries.getJSONObject(0)
                    val lastUpdated = dataObject.getString("Date")

                    //we get length of the array countries from api
                    val count = countries.length()-1

                    //variable data negara indo
                    lateinit var confirmIndo: String
                    lateinit var recoverIndo: String
                    lateinit var deathIndo: String

                    for (i in 0..count){
                        val dataObjectCountries = countries.getJSONObject(i)
                        val countryName = dataObjectCountries.getString("Country")

                        if(countryName == "Indonesia"){
                            confirmIndo = dataObjectCountries.getString("TotalConfirmed")
                            recoverIndo = dataObjectCountries.getString("TotalRecovered")
                            deathIndo = dataObjectCountries.getString("TotalDeaths")
                        }

                    }

                    //format value data negara indo
                    val formatConfirmIndo = numberFormat(confirmIndo.toInt())
                    val formatRecoverIndo = numberFormat(recoverIndo.toInt())
                    val formatDeathIndo = numberFormat(deathIndo.toInt())


                    //format value data global
                    val conf = numberFormat(totalConfirmed)
                    val deaths = numberFormat(totalDeaths)
                    val recovered = numberFormat(totalRecovered)

                    //set text to data negara indo in UI
                    negara_terkonfirmasi.setText(formatConfirmIndo)
                    data_negara_indonesia_meninggal.setText(formatDeathIndo)
                    data_negara_indonesia_sembuh.setText(formatRecoverIndo)
                    last_updated_negara.setText(lastUpdated)

                    //set text to data global in UI
                    last_updated_global.setText(lastUpdated)
                    global_terkonfirmasi.setText(conf)
                    data_global_sembuh.setText(recovered)
                    data_global_meninggal.setText(deaths)

                } catch (e: Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }


        } )

    }


    fun showLoading(state: Boolean) {
        if (state) {
            progressBar?.visibility = View.VISIBLE

        } else {
            progressBar?.visibility = View.INVISIBLE
        }
    }


    fun getDataProvinsiSumateraUtara(context: Context){
        val client = AsyncHttpClient()
        val url = "https://data.covid19.go.id/public/api/prov.json"

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                val result = responseBody.let { String(it) }
                Log.d("result provinsi : ", result)

                try {
                    val jsonObject = JSONObject(result)
                    val lastUpdatedProvinsi = jsonObject.getString("last_date")
                    val provinsi = jsonObject.getJSONArray("list_data")

                    val count = provinsi.length()-1

                    lateinit var confirmSumut: String
                    lateinit var recoverSumut: String
                    lateinit var deathSumut: String

                    for (i in 0..count){
                        val dataObjectProvinsi = provinsi.getJSONObject(i)
                        val provinsiName = dataObjectProvinsi.getString("key")

                        if(provinsiName == "SUMATERA UTARA"){
                            confirmSumut = dataObjectProvinsi.getString("jumlah_kasus")
                            recoverSumut= dataObjectProvinsi.getString("jumlah_sembuh")
                            deathSumut = dataObjectProvinsi.getString("jumlah_meninggal")
                        }

                    }

                    //format value data negara indo
                    val formatConfirmSumut = numberFormat(confirmSumut.toInt())
                    val formatRecoverSumut = numberFormat(recoverSumut.toInt())
                    val formatDeathSumut = numberFormat(deathSumut.toInt())

                    last_updated_provinsi.setText(lastUpdatedProvinsi)
                    provinsi_terkorfimasi.setText(formatConfirmSumut)
                    data_prov_sumatera_utara_meninggal.setText(formatDeathSumut)
                    data_prov_sumatera_utara_sembuh.setText(formatRecoverSumut)


                } catch (e: Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun getDataProvinsiSearch(context: Context, prov: String){
        val client = AsyncHttpClient()
        val url = "https://data.covid19.go.id/public/api/prov.json"

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                val result = responseBody.let { String(it) }
                Log.d("result provinsi : ", result)

                try {
                    val jsonObject = JSONObject(result)
                    val lastUpdatedProvinsi = jsonObject.getString("last_date")
                    val provinsi = jsonObject.getJSONArray("list_data")

                    val count = provinsi.length()-1

                    lateinit var confirmSumut: String
                    lateinit var recoverSumut: String
                    lateinit var deathSumut: String

                    for (i in 0..count){
                        val dataObjectProvinsi = provinsi.getJSONObject(i)
                        val provinsiName = dataObjectProvinsi.getString("key")

                        if(provinsiName == prov){
                            confirmSumut = dataObjectProvinsi.getString("jumlah_kasus")
                            recoverSumut= dataObjectProvinsi.getString("jumlah_sembuh")
                            deathSumut = dataObjectProvinsi.getString("jumlah_meninggal")
                        }

                    }

                    //format value data negara indo
                    val formatConfirmSumut = numberFormat(confirmSumut.toInt())

                    Log.d("result search: ", formatConfirmSumut)


                } catch (e: Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onClick(v: View) {

        when(v.id){
            R.id.negara_indonesia -> {
                val intent = Intent(this, DetailNegaraActivity::class.java)
                startActivity(intent)
            }

            R.id.button_expand_provinsi -> {
                val intent = Intent(this, SearchProvinsiActivity::class.java)
                startActivity(intent)
            }
        }

    }


}
