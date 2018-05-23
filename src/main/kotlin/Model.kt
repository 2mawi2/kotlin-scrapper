package scrapper

data class Video(
        val id: Int,
        val url: String,
        val preview: String = "",
        val title: String = "",
        val actor: String = "",
        val date: String = "",
        val favourite: Boolean = false,
        val description: String = "",
        val keywords: String = ""
)

data class VideoResult(val videos: List<Video>)

data class PagedRequest(val skip: Int, val take: Int)

enum class SearchType {
    Id,
    Title,
    Actor,
    Date,
    Favourite,
    Keywords,
    Description,
    All,
}

data class SearchRequest(val searchType: SearchType, val searchField: Any)