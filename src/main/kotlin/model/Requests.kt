package model


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


data class PagedRequest(val skip: Int, val take: Int)

data class SearchRequest(val searchType: SearchType, val searchField: Any)