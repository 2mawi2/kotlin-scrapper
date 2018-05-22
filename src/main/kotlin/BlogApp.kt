package blog

import org.dizitart.kno2.getRepository
import org.dizitart.no2.objects.filters.ObjectFilters.ALL
import org.dizitart.no2.objects.filters.ObjectFilters.eq
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing

data class Video(val id: Int, val url: String)
data class VideoResult(val videos: List<Video>)

fun main(args: Array<String>) {
    embeddedServer(Netty, 5000) {
        routing {
            get("/") {
                getDb().use {
                    val videos = it.getRepository<Video>().find(ALL).toList()
                    call.respond(VideoResult(videos).toJson())
                }
            }
        }
    }.start(wait = true)
}

