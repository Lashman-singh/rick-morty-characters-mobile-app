package np.com.lashman.challenge4.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import np.com.lashman.challenge4.api.RetrofitClient
import np.com.lashman.challenge4.database.CharacterDao
import np.com.lashman.challenge4.model.Character
import np.com.lashman.challenge4.database.CharacterEntity
import retrofit2.HttpException
import android.util.Log

class RickAndMortyViewModel(private val characterDao: CharacterDao) : ViewModel() {
    var characters = mutableStateOf<List<Character>>(emptyList())
    var isLoading = mutableStateOf(false)
    var localCharacters = mutableStateOf<List<CharacterEntity>>(emptyList())

    init {
        fetchCharacters()
        fetchLocalCharacters()
    }

    private fun fetchCharacters() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val apiService = RetrofitClient.getInstance()
                val response = apiService.getCharacters(1)
                characters.value = response.results
                Log.d("ViewModel", "Fetched characters from API: ${response.results.size}")
            } catch (e: HttpException) {
                Log.e("ViewModel", "Error fetching characters: ${e.message()}")
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun fetchLocalCharacters() {
        viewModelScope.launch {
            try {
                localCharacters.value = characterDao.getAllCharacters()
                Log.d("ViewModel", "Fetched local characters: ${localCharacters.value.size}")
            } catch (e: Exception) {
                Log.e("ViewModel", "Error fetching local characters: ${e.message}")
            }
        }
    }

    fun insertCharacter(character: Character) {
        viewModelScope.launch {
            val characterEntity = CharacterEntity(
                id = character.id,
                name = character.name,
                species = character.species,
                gender = character.gender,
                image = character.image
            )

            val existingCharacter = characterDao.getAllCharacters().find { it.id == characterEntity.id }
            if (existingCharacter == null) {
                try {
                    characterDao.insertCharacter(characterEntity)
                    fetchLocalCharacters()
                    Log.d("Database", "Character inserted: ${character.name}")
                } catch (e: Exception) {
                    Log.e("Database", "Error inserting character: ${e.message}")
                }
            } else {
                Log.d("Database", "Character already exists: ${character.name}")
            }
        }
    }

    fun deleteCharacter(character: CharacterEntity) {
        viewModelScope.launch {
            try {
                characterDao.deleteCharacter(character)
                fetchLocalCharacters()
                Log.d("Database", "Character deleted: ${character.name}")
            } catch (e: Exception) {
                Log.e("Database", "Error deleting character: ${e.message}")
            }
        }
    }
}

