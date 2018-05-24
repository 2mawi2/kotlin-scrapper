import app.second
import junit.framework.Assert.*
import model.*
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.Test

class BlogAppKtTest : IntegrationTestBase() {
    override val data = listOf(
            Video(id = 1, url = "url"),
            Video(id = 2, url = "url2"),
            Video(id = 3, url = "url2", actor = "some actor"),
            Video(id = 4, url = "url2", description = "some description", keywords = "some"),
            Video(id = 5, url = "url2", keywords = "some keywords"),
            Video(id = 6, url = "url2", title = "some title", description = "some", keywords = "some"),
            Video(id = 7, url = "url2", favourite = true)
    )

    @Test
    fun `should respond with videos`() {
        val videoResult = HttpClient.get<VideoResult>("/videos")

        assertEquals(data, videoResult!!.videos)
    }

    @Test
    fun `should respond paged with videos`() {
        val videoResult = HttpClient.post<VideoResult>("/videos/paged", PagedRequest(0, 1))

        assertEquals(listOf(data.first()), videoResult!!.videos)
    }

    @Test
    fun `should favourite video`() {
        HttpClient.get<VideoResult>("/videos/favourite/1")
        val videoResult = HttpClient.get<VideoResult>("/videos")

        videoResult!!.videos.first().favourite `should be` true
    }

    @Test
    fun `should unfavourite video`() {
        HttpClient.get<VideoResult>("/videos/unfavourite/3")
        val videoResult = HttpClient.get<VideoResult>("/videos")

        videoResult!!.videos[2].favourite `should be` false
    }

    @Test
    fun `should search by id`() {
        val videoResult = HttpClient.post<VideoResult>("/videos/search", SearchRequest(SearchType.Id, 1))

        videoResult!!.videos `should equal` listOf(data.first())
    }

    @Test
    fun `should search by favourite`() {
        val videoResult = HttpClient.post<VideoResult>(
                "/videos/search", SearchRequest(SearchType.Favourite, true))

        videoResult!!.videos.count() `should be greater than` 0
        videoResult.videos.forEach { it.favourite `should be` true }
    }


    private fun assertHasProperty(videos: List<Video>, expected: String, selector: (Video) -> Any) {
        assertTrue(videos.any())
        videos.forEach { assertEquals(expected, selector(it)) }
    }

    @Test
    fun `should search by actor`() {
        val videoResult = HttpClient.post<VideoResult>(
                "/videos/search", SearchRequest(SearchType.Actor, "some actor"))

        assertHasProperty(videoResult!!.videos, "some actor") { it.actor }

    }

    @Test
    fun `should search by description`() {
        val videoResult = HttpClient.post<VideoResult>(
                "/videos/search", SearchRequest(SearchType.Description, "some description"))

        assertHasProperty(videoResult!!.videos, "some description") { it.description }
    }

    @Test
    fun `should search by keywords`() {
        val videoResult = HttpClient.post<VideoResult>(
                "/videos/search", SearchRequest(SearchType.Keywords, "some keywords"))
        assertHasProperty(videoResult!!.videos, "some keywords") { it.keywords }
    }

    @Test
    fun `should search by title`() {
        val videoResult = HttpClient.post<VideoResult>(
                "/videos/search", SearchRequest(SearchType.Title, "some title"))

        assertHasProperty(videoResult!!.videos, "some title") { it.title }
    }

    @Test
    fun `should check for contains while searching`() {
        val videoResult = HttpClient.post<VideoResult>(
                "/videos/search", SearchRequest(SearchType.Title, "title"))

        assertHasProperty(videoResult!!.videos, "some title") { it.title }
    }


    @Test
    fun `should search by all`() {
        val videoResult = HttpClient.post<VideoResult>(
                "/videos/search", SearchRequest(SearchType.All, "some"))

        assertTrue(videoResult!!.videos.any())
        videoResult.videos.map { it.id }.forEach { assertTrue(listOf(3, 4, 5, 6).contains(it)) }
    }

    @Test
    fun `should search with ranked order`() {
        val videoResult = HttpClient.post<VideoResult>(
                "/videos/search", SearchRequest(SearchType.All, "some"))

        videoResult!!.videos.first().id `should be` 6
        videoResult.videos.second().id `should be` 4
    }
}

