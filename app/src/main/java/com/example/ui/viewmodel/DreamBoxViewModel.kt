package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.GameRepository
import com.example.engine.EngineRegistry
import com.example.engine.LaunchResult
import com.example.model.GameItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DreamBoxTab {
    LIBRARY,
    DETAILS,
    SYSTEMS,
    SETTINGS
}

data class DreamBoxUiState(
    val games: List<GameItem> = emptyList(),
    val filteredGames: List<GameItem> = emptyList(),
    val selectedGame: GameItem? = null,
    val activeTab: DreamBoxTab = DreamBoxTab.LIBRARY,
    val searchQuery: String = "",
    val selectedPlatformCode: String? = null,
    val isLoading: Boolean = false,
    val userMessage: String? = null,
    val launchNotice: LaunchResult.EngineNotInstalled? = null,
    val isClearConfirmationOpen: Boolean = false,
    val isDeleteConfirmationGame: GameItem? = null
)

class DreamBoxViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: GameRepository = GameRepository.create(application)
) : AndroidViewModel(application) {

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DreamBoxViewModel(application) as T
                }
            }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedPlatformCode = MutableStateFlow<String?>(null)
    val selectedPlatformCode = _selectedPlatformCode.asStateFlow()

    private val _selectedGame = MutableStateFlow<GameItem?>(null)
    val selectedGame = _selectedGame.asStateFlow()

    private val _activeTab = MutableStateFlow(DreamBoxTab.LIBRARY)
    val activeTab = _activeTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage = _userMessage.asStateFlow()

    private val _launchNotice = MutableStateFlow<LaunchResult.EngineNotInstalled?>(null)
    val launchNotice = _launchNotice.asStateFlow()

    private val _isClearConfirmationOpen = MutableStateFlow(false)
    val isClearConfirmationOpen = _isClearConfirmationOpen.asStateFlow()

    private val _isDeleteConfirmationGame = MutableStateFlow<GameItem?>(null)
    val isDeleteConfirmationGame = _isDeleteConfirmationGame.asStateFlow()

    // Combined UI state
    val uiState: StateFlow<DreamBoxUiState> = combine(
        repository.allGames,
        _searchQuery,
        _selectedPlatformCode,
        _selectedGame,
        _activeTab,
        _isLoading,
        _userMessage,
        _launchNotice,
        _isClearConfirmationOpen,
        _isDeleteConfirmationGame
    ) { params ->
        val rawGames = params[0] as List<GameItem>
        val query = params[1] as String
        val platformCode = params[2] as String?
        val selected = params[3] as GameItem?
        val tab = params[4] as DreamBoxTab
        val loading = params[5] as Boolean
        val msg = params[6] as String?
        val notice = params[7] as LaunchResult.EngineNotInstalled?
        val clearDialog = params[8] as Boolean
        val deleteDialogGame = params[9] as GameItem?

        val filtered = rawGames.filter { game ->
            val matchesQuery = query.isBlank() ||
                    game.fileName.contains(query, ignoreCase = true) ||
                    game.platformName.contains(query, ignoreCase = true) ||
                    game.fileExtension.contains(query, ignoreCase = true)
            val matchesPlatform = platformCode == null || game.platformCode == platformCode
            matchesQuery && matchesPlatform
        }

        // Keep selected game in sync if its status changes in the DB
        val updatedSelected = if (selected != null) {
            rawGames.find { it.id == selected.id } ?: selected
        } else null

        DreamBoxUiState(
            games = rawGames,
            filteredGames = filtered,
            selectedGame = updatedSelected,
            activeTab = tab,
            searchQuery = query,
            selectedPlatformCode = platformCode,
            isLoading = loading,
            userMessage = msg,
            launchNotice = notice,
            isClearConfirmationOpen = clearDialog,
            isDeleteConfirmationGame = deleteDialogGame
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DreamBoxUiState(isLoading = false)
    )

    init {
        // Automatically check availability of previously saved games on app startup
        verifyAllFiles(application)
    }

    fun onGamePicked(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addGameFromUri(context, uri)
            _isLoading.value = false
            result.onSuccess { game ->
                _userMessage.value = "تمت إضافة «${game.cleanName}» إلى المكتبة بنجاح"
            }.onFailure { err ->
                _userMessage.value = "تعذر استيراد الملف: ${err.localizedMessage ?: "خطأ غير معروف"}"
            }
        }
    }

    fun verifyAllFiles(context: Context) {
        viewModelScope.launch {
            val currentList = uiState.value.games
            if (currentList.isNotEmpty()) {
                repository.verifyAllGames(context, currentList)
            }
        }
    }

    fun verifySingleGame(context: Context, game: GameItem) {
        viewModelScope.launch {
            _isLoading.value = true
            val updated = repository.verifyGameAvailability(context, game)
            _selectedGame.value = updated
            _isLoading.value = false
            _userMessage.value = if (updated.isAvailable) {
                "تم التحقق: الملف متوفر وقابل للقراءة"
            } else {
                "تنبيه: تعذر الوصول إلى الملف أو أُلغيت الصلاحية"
            }
        }
    }

    fun selectGame(game: GameItem) {
        _selectedGame.value = game
        _activeTab.value = DreamBoxTab.DETAILS
    }

    fun navigateBackToLibrary() {
        _activeTab.value = DreamBoxTab.LIBRARY
    }

    fun setActiveTab(tab: DreamBoxTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPlatformFilter(code: String?) {
        _selectedPlatformCode.value = code
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun tryLaunchGame(game: GameItem) {
        when (val result = EngineRegistry.checkEngineStatus(game)) {
            is LaunchResult.EngineNotInstalled -> {
                _launchNotice.value = result
            }
            is LaunchResult.FileUnavailable -> {
                _userMessage.value = result.reason
            }
            else -> {
                // Not supported
            }
        }
    }

    fun dismissLaunchNotice() {
        _launchNotice.value = null
    }

    fun promptDeleteGame(game: GameItem) {
        _isDeleteConfirmationGame.value = game
    }

    fun dismissDeleteGame() {
        _isDeleteConfirmationGame.value = null
    }

    fun confirmDeleteGame(context: Context) {
        val game = _isDeleteConfirmationGame.value ?: return
        viewModelScope.launch {
            repository.deleteGame(context, game)
            _isDeleteConfirmationGame.value = null
            if (_selectedGame.value?.id == game.id) {
                _selectedGame.value = null
                _activeTab.value = DreamBoxTab.LIBRARY
            }
            _userMessage.value = "تمت إزالة اللعبة من المكتبة"
        }
    }

    fun promptClearLibrary() {
        _isClearConfirmationOpen.value = true
    }

    fun dismissClearLibrary() {
        _isClearConfirmationOpen.value = false
    }

    fun confirmClearLibrary(context: Context) {
        viewModelScope.launch {
            val games = uiState.value.games
            repository.clearAll(context, games)
            _isClearConfirmationOpen.value = false
            _selectedGame.value = null
            _activeTab.value = DreamBoxTab.LIBRARY
            _userMessage.value = "تم مسح جميع بيانات المكتبة"
        }
    }
}
