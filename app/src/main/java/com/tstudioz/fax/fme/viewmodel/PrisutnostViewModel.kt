package com.tstudioz.fax.fme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tstudioz.fax.fme.database.Dolazak
import com.tstudioz.fax.fme.models.data.Repository
import com.tstudioz.fax.fme.models.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.java.KoinJavaComponent.inject



@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class PrisutnostViewModel : ViewModel() {

    private val repository: Repository by inject(Repository::class.java)
    private var _gotPri = MutableLiveData<Boolean>(true)

    val gotPri: LiveData<Boolean>
        get() = _gotPri

    suspend fun fetchPrisutnost() {
        when (val dolazci = repository.fetchPrisutnost()){
            is Result.PrisutnostResult.Success -> {
                insertOrUpdatePrisutnost(dolazci.pris)
                _gotPri.postValue(true)
            }
            is Result.PrisutnostResult.Failure -> {
                _gotPri.postValue(false)
            }
        }

    }
    private fun insertOrUpdatePrisutnost(freshPris: MutableList<Dolazak>){
        repository.insertOrUpdatePrisutnost(freshPris)
    }

}


