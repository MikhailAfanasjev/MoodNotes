package com.example.ainotes.utils

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class FetchNgrokTunnelsTask(
    private val baseUrlManager: BaseUrlManager
) : AsyncTask<Void, Void, String?>() {

    companion object {
        private const val TAG = ">>>BaseUrlManager"
        private const val NGROK_API_URL = "https://api.ngrok.com/tunnels"
        private const val API_KEY = "2vwuX6rCb0W5FrInoQ9yPPCr7wt_3qvbbxb9T4kLyjtwDRNoL"
        private const val API_TIMEOUT = 15000
    }

    override fun doInBackground(vararg params: Void?): String? {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(NGROK_API_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                connectTimeout = API_TIMEOUT
                readTimeout = API_TIMEOUT
                setRequestProperty("Authorization", "Bearer $API_KEY")
                setRequestProperty("Ngrok-Version", "2")
            }

            Log.d(TAG, "doInBackground: Sending request to Ngrok API")

            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    Log.d(TAG, "doInBackground: Response OK, reading input stream")
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        val response = reader.readText()
                        return parseNgrokResponse(response)
                    }
                }
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    Log.e(TAG, "doInBackground: Invalid Ngrok API Key or Missing Authorization")
                }
                HttpURLConnection.HTTP_FORBIDDEN -> {
                    Log.e(TAG, "doInBackground: Forbidden: The API key might be incorrect or has no access.")
                }
                else -> {
                    Log.e(TAG, "doInBackground: HTTP Error: ${connection.responseCode}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "doInBackground: Network error: ${e.message}")
        } finally {
            connection?.disconnect()
        }
        return null
    }

    private fun parseNgrokResponse(response: String): String? {
        Log.d(TAG, "parseNgrokResponse: Parsing Ngrok API response")
        return try {
            val json = JSONObject(response)
            val tunnels = json.getJSONArray("tunnels")

            for (i in 0 until tunnels.length()) {
                val tunnel = tunnels.getJSONObject(i)
                val proto = tunnel.getString("proto")
                val publicUrl = tunnel.getString("public_url")

                if (proto == "https") {
                    Log.d(TAG, "parseNgrokResponse: Found HTTPS tunnel: $publicUrl")
                    return publicUrl
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "parseNgrokResponse: JSON parsing error: ${e.message}")
            null
        }
    }

    override fun onPostExecute(result: String?) {
        result?.let { url ->
            Log.i(TAG, "onPostExecute: Updated base URL: $url")
            baseUrlManager.setBaseUrl(url)
        }
    }
}