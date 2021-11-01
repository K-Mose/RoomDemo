package com.example.roomdemo

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
    val clearAllOrDeleteButtonTexxt = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage


    init {
        // When user click the button change text dynamically.
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonTexxt.value = "ClearAll"
    }

    fun saveOrUpdate() {
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

    fun clearAllOrDelete() {
        if(isUpdateOrDelete)
            delete(subscriberToUpdateOrDelete)
        else
            clearAll()
    }

    fun insert(subscriber: Subscriber) = viewModelScope.launch {
            repository.insert(subscriber)
            statusMessage.value = Event("Subscriber Inserted Successfully.")
    }

    fun update(subscriber: Subscriber) = viewModelScope.launch {
        repository.update(subscriber)
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = false
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonTexxt.value = "ClearAll"
        statusMessage.value = Event("Subscriber Updated Successfully.")
    }

    fun delete(subscriber: Subscriber) = viewModelScope.launch {
        repository.delete(subscriber)
        inputName.value = null
        inputEmail.value = null
        isUpdateOrDelete = false
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonTexxt.value = "ClearAll"
        statusMessage.value = Event("Subscriber Deleted Successfully.")
    }

    fun clearAll() = viewModelScope.launch {
        repository.deleteAll()
        statusMessage.value = Event("All Subscriber Deleted Successfully.")
    }

    fun initUpdateAndDelete(subscriber: Subscriber) { // Pass Selected Subscriber's Instance
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        // change buttons text
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonTexxt.value = "Delete"
    }

    //
    fun getSaveSubscribers() = liveData {
        repository.subscribers.collect {
            emit(it)
        }
    }
}