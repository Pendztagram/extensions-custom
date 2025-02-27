package recloudstream

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup

class NgeFilmProvider : MainAPI() {
    override var mainUrl = "https://new5.ngefilm.site"
    override var name = "NgeFilm"
    override var lang = "id"
    override val hasMainPage = true

    override val mainPage = listOf(
        "/page/%d/?s&search=advanced&post_type=movie" to "Movies Terbaru",
        "/page/%d/?s=&search=advanced&post_type=tv" to "Series Terbaru",
        "/page/%d/?s=&search=advanced&post_type=tv&genre=drakor" to "Series Korea",
        "/page/%d/?s=&search=advanced&post_type=tv&country=indonesia" to "Series Indonesia"
    )

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/?s=${query.replace(" ", "+")}"
        val document = app.get(url).document
        val results = mutableListOf<SearchResponse>()

        document.select(".result-item").forEach { element ->
            val title = element.select(".title").text()
            val posterUrl = element.select("img").attr("src")
            val link = element.select("a").attr("href")
            results.add(MovieSearchResponse(title, link, this.name, posterUrl))
        }

        return results
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.select("h1.entry-title").text()
        val posterUrl = document.select(".post-thumbnail img").attr("src")
        val videoLinks = mutableListOf<ExtractorLink>()

        document.select("iframe[src]").forEach { iframe ->
            val iframeUrl = iframe.attr("src").httpsify()
            videoLinks.addAll(loadExtractor(iframeUrl, url))
        }

        return MovieLoadResponse(title, url, this.name, videoLinks, posterUrl)
    }
}
