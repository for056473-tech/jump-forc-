package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.GameDao
import com.example.data.local.GameDatabase
import com.example.engine.EngineRegistry
import com.example.engine.LaunchResult
import com.example.model.GameItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DreamBoxRobolectricTest {

    private lateinit var database: GameDatabase
    private lateinit var gameDao: GameDao
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        gameDao = database.gameDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `verify app name matches DreamBox Emulator`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("DreamBox Emulator", appName)
    }

    @Test
    fun `test engine registry platform detection for rom extensions`() {
        val ps1 = EngineRegistry.inferPlatform("iso")
        assertEquals("PS1", ps1.code)

        val gba = EngineRegistry.inferPlatform("gba")
        assertEquals("GBA", gba.code)

        val nds = EngineRegistry.inferPlatform("nds")
        assertEquals("NDS", nds.code)

        val nes = EngineRegistry.inferPlatform("nes")
        assertEquals("NES", nes.code)

        val snes = EngineRegistry.inferPlatform("sfc")
        assertEquals("SNES", snes.code)

        val generic = EngineRegistry.inferPlatform("unknown_ext")
        assertEquals("GENERIC", generic.code)
    }

    @Test
    fun `test launch check reports no engine installed with clear message`() {
        val game = GameItem(
            id = 1,
            uriString = "content://com.android.providers.media.documents/document/123",
            fileName = "Pokemon_Emerald.gba",
            fileExtension = "gba",
            fileSizeBytes = 16777216L,
            platformName = "جيم بوي أدفانس",
            platformCode = "GBA",
            isAvailable = true
        )

        val launchResult = EngineRegistry.checkEngineStatus(game)
        assertTrue(launchResult is LaunchResult.EngineNotInstalled)

        val notice = launchResult as LaunchResult.EngineNotInstalled
        assertEquals("لا يوجد محرك متوافق مثبت", notice.message)
        assertTrue(notice.detailsAr.contains("واجهة تشغيل وإدارة مكتبة"))
    }

    @Test
    fun `test launch check reports file unavailable when permission revoked`() {
        val game = GameItem(
            id = 2,
            uriString = "content://missing/file",
            fileName = "Crash_Bandicoot.chd",
            fileExtension = "chd",
            fileSizeBytes = 500000000L,
            platformName = "سوني بلايستيشن 1",
            platformCode = "PS1",
            isAvailable = false
        )

        val launchResult = EngineRegistry.checkEngineStatus(game)
        assertTrue(launchResult is LaunchResult.FileUnavailable)
    }

    @Test
    fun `test inserting and reloading game in database`() = runBlocking {
        val initialList = gameDao.getAllGames().first()
        assertTrue(initialList.isEmpty())

        val testGame = GameItem(
            uriString = "content://com.android.externalstorage.documents/document/primary%3AROMs%2Fgame.iso",
            fileName = "Gran_Turismo_2.iso",
            fileExtension = "iso",
            fileSizeBytes = 650000000L,
            platformName = "سوني بلايستيشن 1",
            platformCode = "PS1",
            badgeColorHex = "#38BDF8",
            isAvailable = true,
            statusMessage = "جاهز للربط"
        )

        val insertedId = gameDao.insertGame(testGame)
        assertTrue(insertedId > 0)

        val loadedGames = gameDao.getAllGames().first()
        assertEquals(1, loadedGames.size)
        assertEquals("Gran_Turismo_2.iso", loadedGames[0].fileName)
        assertEquals("Gran_Turismo_2", loadedGames[0].cleanName)
        assertEquals(true, loadedGames[0].isAvailable)

        // Verify retrieval by URI
        val byUri = gameDao.getGameByUri(testGame.uriString)
        assertNotNull(byUri)
        assertEquals(insertedId, byUri?.id)

        // Test status update (e.g. when checking availability on app reopen)
        val updatedGame = loadedGames[0].copy(
            isAvailable = false,
            statusMessage = "الملف غير متوفر أو أُلغيت الصلاحية"
        )
        gameDao.updateGame(updatedGame)

        val reloaded = gameDao.getAllGames().first()[0]
        assertFalse(reloaded.isAvailable)
        assertEquals("الملف غير متوفر أو أُلغيت الصلاحية", reloaded.statusMessage)

        // Test deleting game
        gameDao.deleteGame(reloaded)
        val afterDelete = gameDao.getAllGames().first()
        assertTrue(afterDelete.isEmpty())
    }

    @Test
    fun `test viewmodel instantiation via default AndroidViewModelFactory`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(app)
        val viewModel = factory.create(com.example.ui.viewmodel.DreamBoxViewModel::class.java)
        assertNotNull(viewModel)
    }

    @Test
    fun `test MainActivity launch without crashing`() {
        androidx.test.core.app.ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertNotNull(activity)
            }
        }
    }
}
