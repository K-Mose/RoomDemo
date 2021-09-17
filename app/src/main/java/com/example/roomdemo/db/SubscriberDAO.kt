package com.example.roomdemo.db

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriberDAO {
/*
suspend 사용 이유
Room은 main thread에서 db 접근을 허용하지 않는다.
그래서 UI thread에서 긴 시간 묶여있게 된다.
그렇기 떄문에 함수들을 백그라운드에서 실행시킬 수 있도록 한다.
*suspend 함수는 단순히 정지했다가 다시 실행 될 수 있는 함수를 말한다.
*/

    @Insert // Room libray은 함수 이름이 아닌 annotation을 읽고 Insert 기능이라 알아차린다.
    suspend fun insertSubscriber(subscriber: Subscriber): Long

    @Update
    suspend fun updateSubscriber(subscriber: Subscriber)

    @Delete
    suspend fun deleteSubscriber(subscriber: Subscriber)

    @Query("DELETE FROM subscriber_data_table") // complie time에서 verify되어서 Room에게 db가 작동된다는 것을 확신시킨다. runtime시 query 에러가 일어나지 않는다.
    suspend fun deleteAll()

    /*
    LiveData to Flow - https://kotlinlang.org/docs/flow.html#flows
    @Query("SELECT * FROM subscriber_data_table")
    fun getAllSubscribers():LiveData<List<Subscriber>>
    */
    @Query("SELECT * FROM subscriber_data_table")
    fun getAllSubscribers():Flow<List<Subscriber>> // 백그라운드에서 실행시킬 수


    /*
    @Insert // suspend 없이 백그라운드에 실행시키기
    fun insertSubscriber2(subscriber: Subscriber): Long

    @Insert
    fun insertSubscribers(subscriber: Subscriber, subscriber2: Subscriber, subscriber3: Subscriber): List<Long>

    @Insert
    fun insertSubscribers(subscriber: List<Subscriber>): List<Long>*/
}