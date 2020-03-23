package com.tstudioz.fax.fme.di

import com.tstudioz.fax.fme.data.Repository
import org.koin.dsl.module

val module = module {
    single { Repository() }
}