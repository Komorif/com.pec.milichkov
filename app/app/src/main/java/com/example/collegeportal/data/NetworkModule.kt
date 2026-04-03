package com.example.collegeportal.data

import android.content.Context

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private var baseUrl = "http://10.0.2.2:8888/api/"
    private var authToken: String? = null
    
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
        
        authToken?.let {
            builder.header("Authorization", "Bearer $it")
        }
        
        builder.header("Accept", "application/json")
        chain.proceed(builder.build())
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private var retrofit: Retrofit = buildRetrofit(baseUrl)
    var apiService: ApiService = retrofit.create(ApiService::class.java)
        private set

    private fun buildRetrofit(url: String): Retrofit {
        val validUrl = url.toHttpUrlOrNull() ?: "http://10.0.2.2:8888/api/".toHttpUrlOrNull()!!
        return Retrofit.Builder()
            .baseUrl(validUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun setAuthToken(context: Context, token: String?) {
        authToken = token
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("auth_token", token).apply()
    }

    fun initialize(context: Context) {
        try {
            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val savedIp = prefs.getString("server_ip", "10.0.2.2") ?: "10.0.2.2"
            val savedPort = prefs.getString("server_port", "8888") ?: "8888"
            authToken = prefs.getString("auth_token", null)
            updateBaseUrl(savedIp, savedPort)
        } catch (e: Exception) {
            updateBaseUrl("10.0.2.2", "8888")
        }
    }

    fun updateBaseUrl(ip: String, port: String) {
        var cleanIp = ip.trim().removePrefix("http://").removePrefix("https://").removeSuffix("/")
        var finalPort = port.trim()

        // Logic to extract port from IP if it's there (e.g. "127.0.0.1:8000")
        if (cleanIp.contains(":")) {
            val parts = cleanIp.split(":")
            cleanIp = parts[0]
            finalPort = parts[1]
        }

        if (cleanIp == "127.0.0.1" || cleanIp == "localhost") {
            cleanIp = "10.0.2.2"
        }

        if (cleanIp.isEmpty()) cleanIp = "10.0.2.2"
        if (finalPort.isEmpty()) finalPort = "8888"
        
        baseUrl = "http://$cleanIp:$finalPort/api/"
        retrofit = buildRetrofit(baseUrl)
        apiService = retrofit.create(ApiService::class.java)
    }

    fun getBaseUrl(): String {
        return baseUrl
    }
    
    fun fixUrl(url: String?): String? {
        if (url == null) return null
        val currentHost = baseUrl.removePrefix("http://").removePrefix("https://").substringBefore("/")
        return url.replace("localhost", currentHost).replace("127.0.0.1", currentHost)
    }

    fun getCurrentIp(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("server_ip", "10.0.2.2") ?: "10.0.2.2"
    }

    fun getCurrentPort(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("server_port", "8888") ?: "8888"
    }

    fun saveSettings(context: Context, ip: String, port: String) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        
        var finalIp = ip.trim()
        var finalPort = port.trim()
        
        // If IP contains a port, split it before saving to keep prefs clean
        if (finalIp.contains(":")) {
            val parts = finalIp.split(":")
            finalIp = parts[0]
            finalPort = parts[1]
        }

        prefs.edit()
            .putString("server_ip", finalIp)
            .putString("server_port", finalPort)
            .apply()
        
        updateBaseUrl(finalIp, finalPort)
    }
}
