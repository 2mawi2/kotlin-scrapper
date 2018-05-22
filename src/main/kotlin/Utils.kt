package blog

import com.google.gson.Gson
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite


inline fun <reified T : Any> T.toJson(): String = Gson().toJson(this, T::class.java)

inline fun <reified T : Any> String.fromJson(): T = Gson().fromJson(this, T::class.java)


fun getDb(): Nitrite = nitrite {
    path = VIDEOS_DB       // or, path = fileName
    autoCommitBufferSize = 2048
    compress = true
    autoCompact = false
}