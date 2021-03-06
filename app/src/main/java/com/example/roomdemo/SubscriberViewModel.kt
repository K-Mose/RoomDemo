package com.example.roomdemo

import android.util.Patterns
import androidx.lifecycle.*
import androidx.room.Delete
import com.example.roomdemo.db.Subscriber
import com.example.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel() {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    // @Bindable
    val inputName = MutableLiveData<String?>()

    val inputEmail = MutableLiveData<String?>()

    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage


    init {
        // When user click the button change text dynamically.
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "ClearAll"
    }

    fun saveOrUpdate() {
        if (inputName.value.isNullOrEmpty()) {
            statusMessage.value =  Event("Please enter subscriber's name")
        } else if (inputEmail.value.isNullOrEmpty()) {
            statusMessage.value =  Event("Please enter subscriber's email")
        } else if(Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value =  Event("Please enter a correct email address")
        } else {
            if(isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!

                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                    inputName.value = null
                    inputEmail.value = null
            }
        }
    }

    fun clearAllOrDelete() {
        if(isUpdateOrDelete)
            delete(subscriberToUpdateOrDelete)
        else
            clearAll()
    }

    fun insert(subscriber: Subscriber) = viewModelScope.launch {
        val newRowId : Long = repository.insert(subscriber)
        if (newRowId > -1) {
            statusMessage.value = Event("Subscriber Inserted Successfully. $newRowId")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun update(subscriber: Subscriber) = viewModelScope.launch {
        val noOfRows = repository.update(subscriber)
        if (noOfRows > 0) {
            inputName.value = subscriber.name
            inputEmail.value = subscriber.email
            isUpdateOrDelete = false
            subscriberToUpdateOrDelete = subscriber
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "ClearAll"
            statusMessage.value = Event("$noOfRows Updated Successfully.")
            inputName.value = null
            inputEmail.value = null
        } else {
            statusMessage.value = Event("Error Occurred.")
        }
    }

    fun delete(subscriber: Subscriber) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(subscriber)
        if(noOfRowsDeleted > 0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            subscriberToUpdateOrDelete = subscriber
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "ClearAll"
            statusMessage.value = Event("$noOfRowsDeleted Subscriber Deleted Successfully.")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun clearAll() = viewModelScope.launch {
        val noOfRowDeleted = repository.deleteAll()
        if (noOfRowDeleted > 0) {
            statusMessage.value = Event("$noOfRowDeleted Subscriber Deleted Successfully.")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) { // Pass Selected Subscriber's Instance
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        // change buttons text
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

    //
    fun getSaveSubscribers() = liveData {
        repository.subscribers.collect {
            emit(it)
        }
    }
}