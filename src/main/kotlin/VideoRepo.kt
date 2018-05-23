package scrapper

import org.dizitart.kno2.getRepository
import org.dizitart.no2.Document
import org.dizitart.no2.objects.filters.ObjectFilters

interface IVideoRepo {
    fun all(): List<Video>
    fun all(pq: PagedRequest): List<Video>
    fun updateFavourite(id: String, isFavourite: Boolean)
    fun search(sq: SearchRequest): List<Video>
}

class VideoRepo(private val db: IDbProvider) : IVideoRepo {
    override fun search(sq: SearchRequest): List<Video> {
        db.get().use {
            return it.getRepository<Video>()
                    .find(ObjectFilters.ALL)
                    .filter { i ->
                        when (sq.searchType) {
                            SearchType.All -> anyPropertyContains(i, sq.searchField.toString())
                            else -> propertyContains(i, sq, sq.searchField.toString())
                        }
                    }.distinct()
        }
    }

    private fun propertyContains(i: Video, sq: SearchRequest, sf: String): Boolean {
        val prop = i.getPropertyValue(sq.searchType.name.toLowerCase())
        return when (prop) {
            is String -> prop.contains(sf)
            is Int -> prop == sf.toDouble().toInt()
            else -> prop == sq.searchField
        }
    }

    private fun anyPropertyContains(i: Video, sf: String): Boolean {
        return i.description.contains(sf) or
                i.keywords.contains(sf) or
                i.actor.contains(sf) or
                i.title.contains(sf)
    }

    override fun updateFavourite(id: String, isFavourite: Boolean) {
        db.get().use {
            it.getRepository<Video>().update(ObjectFilters.eq("id", id.toInt()), Document.createDocument("favourite", isFavourite))
        }
    }

    override fun all(pq: PagedRequest): List<Video> {
        db.get().use {
            return it.getRepository<Video>()
                    .find(ObjectFilters.ALL)
                    .drop(pq.skip)
                    .take(pq.take)
        }
    }

    override fun all(): List<Video> {
        db.get().use {
            return it.getRepository<Video>()
                    .find(ObjectFilters.ALL).toList()
        }
    }
}