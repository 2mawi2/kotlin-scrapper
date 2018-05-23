package scrapper

import com.google.gson.Gson
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import kotlin.reflect.full.declaredMemberProperties


inline fun <reified T : Any> T.toJson(): String = Gson().toJson(this, T::class.java)

inline fun <reified T : Any> String.fromJson(): T = Gson().fromJson(this, T::class.java)


fun <T : Any> T.getPropertyValue(propertyName: String): Any? =
        this.javaClass.kotlin
                .declaredMemberProperties
                .firstOrNull { it.name == propertyName }?.get(this)