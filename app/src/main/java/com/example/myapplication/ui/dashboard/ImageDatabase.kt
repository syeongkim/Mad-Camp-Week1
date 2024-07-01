import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity
data class Image (
    var name: String,
    var date: String,
    var comment: String,
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

@Dao
interface ImageDao {
    @Insert
    fun insert(image: Image)

    @Update
    fun update(image: Image)

    @Delete
    fun delete(image: Image)

    @Query("SELECT * FROM Image WHERE name = :name")
    fun getByName(name: String): Image?
}

@Database(entities = [Image::class], version = 1)
abstract class ImageDatabase: RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        private var instance: ImageDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ImageDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImageDatabase::class.java,
                    "image_database"
                ).build()
            }
            return instance!!
        }
    }
}