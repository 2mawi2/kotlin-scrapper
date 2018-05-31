package app

import com.google.gson.Gson
import kotlin.reflect.full.declaredMemberProperties


inline fun <reified T : Any> T.toJson(): String = Gson().toJson(this, T::class.java)
inline fun <reified T : Any> String.fromJson(): T = Gson().fromJson(this, T::class.java)
fun <E> List<E>.second(): E = this[1]
fun <E> List<E>.third(): E = this[2]


fun <T : Any> T.getPropertyValue(propertyName: String): Any? {
    return this.javaClass.kotlin
            .declaredMemberProperties
            .firstOrNull { it.name == propertyName }?.get(this)
}

