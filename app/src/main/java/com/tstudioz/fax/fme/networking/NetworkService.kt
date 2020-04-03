package com.tstudioz.fax.fme.networking

import retrofit2.Retrofit


class NetworkService {

    companion object {
        private const val BASE_URL: String = "https://fesb.hr"
    }

    fun provideClient(): Retrofit = Retrofit.Builder().baseUrl(Companion.BASE_URL).build()
}