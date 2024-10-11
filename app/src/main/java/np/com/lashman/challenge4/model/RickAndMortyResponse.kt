package np.com.lashman.challenge4.model

data class RickAndMortyResponse(
    val results: List<Character>
)

data class Character(
    val id: Int,
    val name: String,
    val gender: String,
    val species: String,
    val image: String,
)