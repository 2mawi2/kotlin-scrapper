package persistence

import model.PagedRequest
import model.SearchRequest
import model.SearchType
import model.Video
import org.dizitart.kno2.getRepository
import org.dizitart.no2.Document
import org.dizitart.no2.objects.Cursor
import org.dizitart.no2.objects.filters.ObjectFilters.*
import app.getPropertyValue

interface IVideoRepo {
    fun all(): List<Video>
    fun all(pq: PagedRequest): List<Video>
    fun updateFavourite(id: String, isFavourite: Boolean)
    fun search(sq: SearchRequest): List<Video>
}

class VideoRepo(private val db: IDbProvider) : IVideoRepo {
    override fun search(sq: SearchRequest): List<Video> {
        db.get().use { db ->
            val videos = db.getRepository<Video>().find(ALL)
            return when (sq.searchType) {
                SearchType.All -> searchByAll(videos, sq.searchField.toString())
                else -> searchSpecific(videos, sq)
            }
        }
    }

    private fun searchByAll(videos: Cursor<Video>, sf: String): List<Video> = videos
            .map { Pair(it, countContains(it, sf)) }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
            .distinct()

    private fun countContains(i: Video, sf: String): Int =
            listOf(i.description.contains(sf),
                    i.keywords.contains(sf),
                    i.actor.contains(sf),
                    i.date.contains(sf),
                    i.title.contains(sf))
                    .filter { it }
                    .count()

    private fun searchSpecific(videos: Cursor<Video>, sq: SearchRequest): List<Video> = videos
            .filter { propertyContains(it, sq, sq.searchField.toString()) }
            .distinct()


    private fun propertyContains(i: Video, sq: SearchRequest, sf: String): Boolean {
        val prop = i.getPropertyValue(sq.searchType.name.toLowerCase())
        return when (prop) {
            is String -> prop.contains(sf)
            is Int -> prop == sf.toDouble().toInt()
            else -> prop == sq.searchField
        }
    }

    override fun updateFavourite(id: String, isFavourite: Boolean) {
        db.get().use {
            it.getRepository<Video>().update(eq("id", id.toInt()), Document.createDocument("favourite", isFavourite))
        }
    }

    override fun all(pq: PagedRequest): List<Video> {
        db.get().use {
            return it.getRepository<Video>()
                    .find(ALL)
                    .drop(pq.skip)
                    .take(pq.take)
        }
    }

    override fun all(): List<Video> {
        db.get().use {
            return it.getRepository<Video>()
                    .find(ALL).toList()
        }
    }
}