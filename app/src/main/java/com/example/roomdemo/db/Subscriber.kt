package com.example.roomdemo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
@Entity
room에게 제공하기위한 DB 테이블 이름,
tableName을 주지 않으면 클래스 이름을 테이블 이름으로 사용
 */
@Entity(tableName = "subscriber_data_table")
data class Subscriber (

    @PrimaryKey(autoGenerate = true)
    //@ColumnInfo column 이름과 응답할 수 있게 설정
    @ColumnInfo(name = "subscriber_id")
    var id: Int,
    @ColumnInfo(name = "subscriber_name")
    var name: String,
    @ColumnInfo(name = "subscriber_email")
    var email: String
)