package persistence

import app.second
import model.SearchRequest
import model.SearchType
import model.Video
import org.amshove.kluent.*
import org.dizitart.kno2.getRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import org.junit.Before
import org.junit.Test

class VideoRepoTest {
    val data = listOf(
            Video(id = 1, url = "url"),
            Video(id = 2, url = "url2"),
            Video(id = 3, url = "url2", actor = "some actor"),
            Video(id = 4, url = "url2", description = "some description", keywords = "some"),
            Video(id = 5, url = "url2", keywords = "some keywords"),
            Video(id = 6, url = "url2", title = "some title", description = "some", keywords = "some"),
            Video(id = 7, url = "url2", favourite = true),
            Video(id = 8, url = "url2", title = "another")
    )

    @Before
    internal fun setUp() {
        DbProvider(true).get().use { db ->
            db.getRepository<Video>().remove(ObjectFilters.ALL)
            data.forEach { db.getRepository<Video>().insert(it) }
        }
    }

    fun videoRepo(): IVideoRepo {
        return VideoRepo(DbProvider(true))
    }

    @Test
    fun `should search by all`() {
        val result = videoRepo().search(SearchRequest(SearchType.All, "some"))
        result.any().shouldBeTrue()
        result.forEach { listOf(3, 4, 5, 6).shouldContain(it.id) }
    }


    @Test
    fun `should search with contains operator`() {
        val result = videoRepo().search(SearchRequest(SearchType.Title, "title"))
        assertHasProperty(result, "title", { it.title })
    }

    private fun assertHasProperty(videos: List<Video>, expected: String, selector: (Video) -> String) {
        videos.shouldNotBeEmpty()
        videos.forEach { selector(it).shouldContain(expected) }
    }

    @Test
    fun `should search by actor`() {
        val result = videoRepo().search(SearchRequest(SearchType.Actor, "some actor"))
        assertHasProperty(result, "some actor") { it.actor }
    }

    @Test
    fun `should search by description`() {
        val result = videoRepo().search(SearchRequest(SearchType.Description, "some description"))
        assertHasProperty(result, "some description") { it.description }
    }

    @Test
    fun `should search by keywords`() {
        val result = videoRepo().search(SearchRequest(SearchType.Keywords, "some keywords"))
        assertHasProperty(result, "some keywords") { it.keywords }
    }

    @Test
    fun `should search by title`() {
        val result = videoRepo().search(SearchRequest(SearchType.Title, "some title"))
        assertHasProperty(result, "some title") { it.title }
    }

    @Test
    fun `should search by id`() {
        val result = videoRepo().search(SearchRequest(SearchType.Id, 1))
        data.first().shouldEqual(result.first())
    }

    @Test
    fun `should search by favourite`() {
        val result = videoRepo().search(SearchRequest(SearchType.Favourite, true))
        result.forEach { it.favourite.shouldBeTrue() }
        result.shouldNotBeEmpty()
    }


    @Test
    fun `should search with ranked order`() {
        val result = videoRepo().search(SearchRequest(SearchType.All, "some"))
        result.first().id.shouldEqual(6)
        result.second().id.shouldEqual(4)
    }

    @Test
    fun `should match all given keywords`() {
        val result = videoRepo().search(SearchRequest(SearchType.All, "another some"))
        result.any { it.id == 8 }.shouldBeTrue()
        result.any { it.id == 4 }.shouldBeTrue()
    }
}