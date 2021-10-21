package com.aldrek.graphqltutorial

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.apollographql.apollo.ApolloClient
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aldrek.graphqltutorial.adapter.LaunchListAdapter
import com.aldrek.graphqltutorial.databinding.ActivityMainBinding
import com.apollographql.apollo.coroutines.await
import com.example.rocketreserver.LaunchListQuery
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)


    companion object{
        const val URL = "https://apollo-fullstack-tutorial.herokuapp.com/graphql"
    }

    // TODO: 10/20/21  -> java3x and coroutine
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(this))
            .build()

        val apolloClient = ApolloClient.builder()
            .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com")
            .build()

        lifecycleScope.launchWhenResumed {

            try{
                val response = apolloClient.query(LaunchListQuery()).await()

                val launches = mutableListOf<LaunchListQuery.Launch>()
                val adapter = LaunchListAdapter(launches)
                binding.launches.layoutManager = LinearLayoutManager(this@MainActivity)
                binding.launches.adapter = adapter

                val newLaunches = response.data?.launches?.launches?.filterNotNull()

                if (newLaunches != null) {
                    launches.addAll(newLaunches)
                    adapter.notifyDataSetChanged()
                }


            }catch (e:Exception){
                Log.d("LaunchList", "Success ${e.stackTrace[0]}")
            }


        }

    }


}


private class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization",  "")
            .build()

        return chain.proceed(request)
    }
}
