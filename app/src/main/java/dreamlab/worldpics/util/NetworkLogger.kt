package dreamlab.worldpics.util

import android.util.Log
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response

internal class NetworkLogger {

    companion object {
        fun <T> success(call: Call<T>, response: Response<T>) {
            Log.d("NetworkResponse", "${call.request().url} ${response.code()}")
        }

        fun <T> success(call: Call<T>) {
            Log.d("NetworkResponseSuccess", "${call.request().url}")
        }

        fun error(response: Response<JsonElement>) {
            Log.e("NetworkError", "${response.raw()}")
        }

        fun  <T> failure(call: Call<T>, t: Throwable) {
            Log.e("NetworkFailure", "${call.request().url} ${t.message.toString()}")
        }

        fun <T> debug(call: Call<T>) {
            try {
                val request = call.request()
                Log.d("NetworkRequest", "${request.url}")
            } catch (ex: Exception) {
                Log.d("NetworkRequest", ex.toString())
            }

        }
    }

}