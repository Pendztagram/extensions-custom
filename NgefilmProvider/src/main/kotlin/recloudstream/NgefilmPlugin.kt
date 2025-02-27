package recloudstream

import com.lagradost.cloudstream3.*

@CloudStreamPlugin
class NgeFilmPlugin : Plugin() {
    override fun load() {
        registerMainAPI(NgeFilmProvider())
    }
}
