package np.com.lashman.challenge4.api


import np.com.lashman.challenge4.model.RickAndMortyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApiService {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): RickAndMortyResponse
}

