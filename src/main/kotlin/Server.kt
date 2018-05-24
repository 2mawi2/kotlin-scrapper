package scrapper

import app.fromJson
import app.toJson
import model.PagedRequest
import model.SearchRequest
import model.VideoResult
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.receiveText
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing
import org.koin.Koin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import persistence.DbProvider
import persistence.IDbProvider
import persistence.IVideoRepo
import persistence.VideoRepo


val module: Module = applicationContext {
    factory { DbProvider(getProperty("development")) as IDbProvider }
    factory { VideoRepo(get()) as IVideoRepo }
}

fun main(args: Array<String>) {
    Koin.logger = PrintLogger()
    val isDevelopment = args.any { it == "development" }
    val container = startKoin(listOf(module), extraProperties = mapOf("development" to isDevelopment))
    val videoRepo = container.koinContext.get<IVideoRepo>()

    embeddedServer(Netty, 5000) {
        routing {
            get("/videos") {
                val videos = videoRepo.all().take(200)
                call.respond(VideoResult(videos).toJson())
            }
            post("/videos/paged") {
                val pq = call.receiveText().fromJson<PagedRequest>()
                val videos = videoRepo.all(pq)
                call.respond(VideoResult(videos).toJson())
            }
            get("/videos/favourite/{id}") {
                val id = call.parameters["id"] ?: throw Exception()
                videoRepo.updateFavourite(id, true)
                call.respond(HttpStatusCode.OK)
            }
            get("/videos/unfavourite/{id}") {
                val id = call.parameters["id"] ?: throw Exception()
                videoRepo.updateFavourite(id, false)
                call.respond(HttpStatusCode.OK)
            }
            post("/videos/search") {
                val sq = call.receiveText().fromJson<SearchRequest>()
                val videos = videoRepo.search(sq).take(1000)
                call.respond(VideoResult(videos = videos).toJson())
            }
        }
    }.start(wait = true)
}




