package com.tstudioz.fax.fme.feature.home.di

import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.feature.home.dao.NoteDao
import com.tstudioz.fax.fme.feature.home.repository.NoteRepository
import com.tstudioz.fax.fme.feature.home.repository.NoteRepositoryInterface
import com.tstudioz.fax.fme.feature.home.repository.WeatherRepository
import com.tstudioz.fax.fme.feature.home.repository.WeatherRepositoryInterface
import com.tstudioz.fax.fme.feature.home.services.WeatherService
import com.tstudioz.fax.fme.feature.home.services.WeatherServiceInterface
import com.tstudioz.fax.fme.feature.home.view.HomeViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@OptIn(InternalCoroutinesApi::class)
val homeModule = module {
    single<NoteDao>{getNoteDao(get())}
    single<NoteRepositoryInterface> { NoteRepository(get()) }
    single<WeatherServiceInterface> { WeatherService(get()) }
    single<WeatherRepositoryInterface> { WeatherRepository(get()) }
    viewModel { HomeViewModel(androidApplication(), get(), get(), get(), get()) }
}


fun getNoteDao(db:AppDatabase): NoteDao {
    return db.NoteRoom()
}