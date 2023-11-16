package com.example.apprutas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map:GoogleMap
    private lateinit var btnCalcular: Button
    var poly: Polyline? = null
    private var start: String =""
    private var end: String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        btnCalcular = findViewById(R.id.btnCalcularRuta)
        btnCalcular.setOnClickListener {
            start = ""
            end = ""
            if(poly!=null){
                poly?.remove()
                poly = null
            }
            if(::map.isInitialized){
                map.setOnMapClickListener {
                    if (start.isEmpty()) {
                        start = "${it.longitude},${it.latitude}"
                    }else{
                        if(end.isEmpty()){
                            end = "${it.longitude},${it.latitude}"
                            crearRutas()
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        this.map = p0
    }
    private fun crearRutas(){
        CoroutineScope(Dispatchers.IO).launch {
            val call= getRetrofit().create(ApiService::class.java).getRoute("5b3ce3597851110001cf624822a20c1876e349b387ea9268a0d77eb8",start,end)
            if(call.isSuccessful){
                dibujarRuta(call.body())

            }else{
                Log.i("msj","KO")
            }
        }
    }

    private fun dibujarRuta(routeResponseRuta: ResponseRuta?) {
        val polylineOptions = PolylineOptions()
        routeResponseRuta?.features?.first()?.geometry?.coordinates?.forEach {
            polylineOptions.add(LatLng(it[1],it[0]))
        }
        runOnUiThread {
            poly = map.addPolyline(polylineOptions)
        }

    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

    }
}