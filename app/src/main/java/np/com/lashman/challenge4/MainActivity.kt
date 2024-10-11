package np.com.lashman.challenge4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import coil.compose.AsyncImage
import np.com.lashman.challenge4.database.AppDatabase
import np.com.lashman.challenge4.viewmodel.RickAndMortyViewModel
import np.com.lashman.challenge4.model.Character
import np.com.lashman.challenge4.viewmodel.RickAndMortyViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "character-database"
        ).build()

        val characterDao = db.characterDao()

        val viewModelFactory = RickAndMortyViewModelFactory(characterDao)
        val viewModel: RickAndMortyViewModel = ViewModelProvider(this, viewModelFactory).get(RickAndMortyViewModel::class.java)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CharacterList(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CharacterList(viewModel: RickAndMortyViewModel) {
    val characters = viewModel.characters.value
    val isLoading = viewModel.isLoading.value
    val localCharacters = viewModel.localCharacters.value

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                // Display API Characters
                items(characters) { character ->
                    CharacterItem(
                        character = character,
                        onInsert = { viewModel.insertCharacter(character) },
                        onDelete = { /*API character deletion */ }
                    )
                }

                // Display Local Characters
                items(localCharacters) { localCharacter ->
                    CharacterItem(
                        character = Character(
                            id = localCharacter.id,
                            name = localCharacter.name,
                            gender = localCharacter.gender,
                            species = localCharacter.species,
                            image = localCharacter.image
                        ),
                        onInsert = { /* Logic to prevent duplicate inserts */ },
                        onDelete = { viewModel.deleteCharacter(localCharacter) }  // Delete from local database
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterItem(character: Character, onInsert: () -> Unit, onDelete: () -> Unit) {
    Row(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()) {

        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = character.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Species: ${character.species}")
            Text(text = "Gender: ${character.gender}")
        }
        // Insert Button
        IconButton(onClick = onInsert) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Insert Character")
        }
        // Delete Button
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Character")
        }
    }
}

