# RoomDemo
Room Database Library를 이용해 Local 저장소에 데이터()를 입력, 수정, 삭제 할 수 있는 앱 입니다.

## Room
Room은 이제 데이터 영속성을 위한 SQLiteDatabase보다 더 나은 접근으로 여겨집니다. Room은 더 쉽고, boilerplate code를 줄이고 그리고 SQL 쿼리문을 컴파일 시에 확인합니다. 

### Why use Room?
* 컴파일 시간 때 검사
* 보일러플레이트 코드 감소
* 다른 아키텍쳐 컴포넌트와의 쉬운 통합
@Query와 @Entity는 컴파일 시간에 확인 되어지고, 런타임에 생긴 앱 오류들을 보존합니다. 그리고 문법을 확인할 뿐만 아니라 빠진 테이블도 확인합니다.

### Major problems with SQLite usage
* SQL 쿼리에 대한 컴파일 시간에 확인이 없습니다. 만약 컬럼이름을 잘못 적고 실행하게된다면 컴파일 때는 찾지 못하지만 런타임 시 오류가 발생하게 됩니다. 
* 스키마 변경 시 그에따라 영향을 받는 SQL 쿼리들로 일일이 바꿔야 합니다. 이러한 일련의 작업은 시간이 많이 들고 오류가 발생하기 쉽습니다. 
* SQL 쿼리에서 POJO로 변환 하는데에 상당히 많은 보일러플레이트 코드를 사용하게 됩니다. 

### Room vs SQLite
Room은 ORM(Object Relational Mapping library) 입니다. 즉, Room은 database 객체를 java 객체로 mappging하게 됩니다. Room은 풍부한 database 접근을 허용하는 abstraction layer를 제공합니다. 
<br>


### Component
<a href=""><img src="https://developer.android.com/images/training/data-storage/room_architecture.png"></a>
Room의 세가지 주요 구성요소
* **Database** : Database holder를 포함하며 앱에 지속 가능한 데이터에 기본적인 엑세스포인트가 됩니다.
<br>Database 는 아래와 같은 조건을 반드시 만족해야 합니다.
  * Database 클래스를 만들기 위해서 추상클래스인 RoomDatabase를 상속해야 합니다. 
  * Entity의 리스트가 포함된 @Database 어노테이션을 지정해야 합니다. 
  * DB와 연관된 각각의 DAO클래스들을 Database 클래스 안에서 abstract 메소드로 정의해야합니다. 해당 메소드들은 인자가 없고, DAO 클래스의 인스턴스를 반환해야 합니다. 

* **Entity** : 
데이터베이스 안에 있는 테이블을 나타냅니다. Room은 @Entitiy 어노테이션을 갖는 클래스를가지고 테이블을 생성합니다. 클래스가 갖는 필드가 테이블에 들어가는 컬럽에 대응합니다.
그래서, Entity 클래스는 어떠한 로직도 갖지않는 작은 형태의 클래스로 존재합니다. <br>
**Entitiy annotations** - 모델링을 하기전에 알면 좋은 유용한 어노네티션들<br>
  @Entity - 모델 클래스로 지정하고, DB에 테이블로 매핑시킵니다.<br>
  * foreingKeys - 외래키 이름
  * indices - 테이블의 인덱스 리스트를
  * primaryKeys - 기본키 이름
  * tableName - 테이블 이름

  @PrirmayKey - 지정된 필드를 Entity에서 PrimaryKey(기본키)로 지정합니다. autuGenerate가 true로 되어있다면 SQLite가 unique id로 컬럼을 생성합니다. <br>
  @ColumnInfo - 커스텀 정보입력을 허용<br>
  @Ignore - Room이 필드를 유지하지 않습니다.(Room will not persist that field.) <br>
  @Embeded - SQL 쿼리문으로 직접적으로 언급될 수 있는 중첩된 필드.
    
* **DAO** : DAO에는 데이터에 접근할 수 있는 메소드들을 정의합니다. 초기 SQLite에서는 Cursor 객체를 사용했지만, Room에서는 Cursor 없이 간단하게 어노테이션 안에 쿼리문을 작성하면 됩니다.
```
  @Query("SELECT * FROM subscriber_data_table")
  fun getAllSubscribers():Flow<List<Subscriber>>
```

## Structure 
```
RoomDemo
└─ app
   └─ src
      └─ main
         ├─ AndroidManifest.xml
         └─ java
            └─ com
               └─ example
                  └─ roomdemo
                     ├─ db
                     │  ├─ Subscriber.kt
                     │  ├─ SubscriberDAO.kt
                     │  ├─ SubscriberDatabase.kt
                     │  ├─ SubscriberRepository.kt
                     │  └─ SubscribersViewModelFactory.kt
                     ├─ Event.kt
                     ├─ MainActivity.kt
                     ├─ MyRecyclerViewAdapter.kt
                     └─ SubscriberViewModel.kt
```
### Entity 
```
@Entity(tableName = "subscriber_data_table")
data class Subscriber (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subscriber_id")
    var id: Int,
    @ColumnInfo(name = "subscriber_name")
    var name: String,
    @ColumnInfo(name = "subscriber_email")
    var email: String
)
```
Entity 는 kotlin <a href="https://kotlinlang.org/docs/data-classes.html#properties-declared-in-the-class-body">data class</a>로 작성합니다. 

### DAO - Data Access Object
```
@Dao
interface SubscriberDAO {
    @Insert
    suspend fun insertSubscriber(subscriber: Subscriber): Long 

    @Update
    suspend fun updateSubscriber(subscriber: Subscriber) : Int

    @Delete
    suspend fun deleteSubscriber(subscriber: Subscriber) : Int 

    @Query("DELETE FROM subscriber_data_table")
    suspend fun deleteAll() : Int
    
    @Query("SELECT * FROM subscriber_data_table")
    fun getAllSubscribers():Flow<List<Subscriber>> 
}
```
SQLite에서는 CRUD를 위해서 ContentValues나 Cursor를 사용하여 장황하게 데이터 테이블에 접근하였지만, 여기선 간단히 @Insert, @Update, @Delete, @Query 어노테이션만으로 모든 작업이 가능합니다. Room은 내부적으로 Insert, Update, Delete에 대한 코드가 존재하지만 나머지 쿼리에 대해서는 @Query 어노테이션을 사용해서 추가해줘야 합니다. 

#### Repository - for MVVM 
해당 프로젝트가 MVVM Clean Architecture를 위한 단계 중 하나이므로 데이터에 접근 할 수 있는 Repository 클래스를 만듭니다.
```
class SubscriberRepository(private val dao: SubscriberDAO) {
    val subscribers = dao.getAllSubscribers()

    suspend fun insert(subscriber: Subscriber) : Long {
        return dao.insertSubscriber(subscriber)
    }

    suspend fun update(subscriber: Subscriber) : Int {
        return dao.updateSubscriber(subscriber)
    }

    suspend fun delete(subscriber: Subscriber) : Int {
        return dao.deleteSubscriber(subscriber)
    }

    suspend fun deleteAll() : Int {
        return dao.deleteAll()
    }
}
```
Repository는 ViewModel에게 데이터를 CRUD 하기 쉽게 clean API를 제공합니다. <br>
Repository에 접근은 ViewModel 클래스를 만들어서 접근합니다. 자세한 내용은 ViewModel은 <a href="https://github.com/K-Mose/TwoWayDataBinding#viewmodel">여기</a> MVVM Clean Architecture는 <a href="https://github.com/K-Mose/TMDBClient---MVVM-with-Clean-Architecture#android-mvvm-pattern">여기</a>를 확인하세요.


### Database - Make Room for Data
```
@Database(entities = [Subscriber::class], version = 1)
abstract class SubscriberDatabase : RoomDatabase() {
    abstract val subscriberDAO: SubscriberDAO
    companion object {
        @Volatile
        private var INSTANCE: SubscriberDatabase? = null
        fun getInstance(context: Context): SubscriberDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SubscriberDatabase::class.java,
                        "subscriber_data_base"
                    ).build()
                }
                return instance
            }
        }
    }
}
```
DB객체는 하나로 관리하기 위해 싱글톤으로 생성하고, 비동기식으로 접근되어지는 공유 자원의 동기화를 위해 synchronized를 사용합니다. 
INSTANCE에 @Volatile 어노테이션을 적용하여 해당 필드가 다른 스레드에 보이도록 하고, 인스턴스의 생성은 Room의 databaseBuilder 함수를 이용하여 생성합니다. 


## Preview

<div>
<img src="https://user-images.githubusercontent.com/55622345/161080992-9513ff86-5ee3-4646-9ed6-1c2ef5ab9948.gif" width="300px"/>
<img src="https://user-images.githubusercontent.com/55622345/161078795-c3d0346b-636e-49bd-8b41-0bd0e3fa3f42.gif" width="300px"/>
<img src="https://user-images.githubusercontent.com/55622345/161078811-f0c6f88a-0e1b-4a8b-aa83-422c1bd0bf3d.gif" width="300px"/>
</div>


## Dependencies
ROOM -
* androidx.room:room-runtime:2.3.0
* androidx.room:room-ktx:2.3.0
* androidx.room:room-compiler:2.3.0

LiveData & ViewModel - 
* androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0
* androidx.lifecycle:lifecycle-livedata-ktx:2.3.0
* androidx.lifecycle:lifecycle-compiler:2.3.0


## Ref.
https://developer.android.com/training/data-storage/room
https://medium.com/mindorks/using-room-database-android-jetpack-675a89a0e942
https://johncodeos.com/how-to-use-room-in-android-using-kotlin/
