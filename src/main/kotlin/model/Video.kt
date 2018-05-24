package model

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