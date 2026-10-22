package com.example.engine

import android.content.Context
import com.example.model.GameItem

sealed interface LaunchResult {
    data class Success(val message: String) : LaunchResult
    data class EngineNotInstalled(
        val message: String,
        val engineName: String,
        val platform: String,
        val suggestedCore: String,
        val detailsAr: String
    ) : LaunchResult
    data class UnsupportedFormat(val extension: String) : LaunchResult
    data class FileUnavailable(val reason: String) : LaunchResult
}

data class SystemPlatformInfo(
    val code: String,
    val nameAr: String,
    val nameEn: String,
    val extensions: List<String>,
    val badgeColorHex: String,
    val targetCore: String,
    val descriptionAr: String
)

interface EmulatorEngine {
    val id: String
    val nameEn: String
    val nameAr: String
    val supportedExtensions: Set<String>
    val isInstalled: Boolean
    suspend fun launchGame(context: Context, game: GameItem): LaunchResult
}

object EngineRegistry {
    val supportedPlatforms: List<SystemPlatformInfo> = listOf(
        SystemPlatformInfo(
            code = "PS1",
            nameAr = "سوني بلايستيشن 1",
            nameEn = "Sony PlayStation (PS1)",
            extensions = listOf("iso", "bin", "cue", "chd", "pbp", "img"),
            badgeColorHex = "#38BDF8", // Cyan
            targetCore = "PCSX-ReARMed / SwanStation",
            descriptionAr = "أقراص ألعاب PS1 بصيغ ISO و CHD و BIN/CUE."
        ),
        SystemPlatformInfo(
            code = "PSP",
            nameAr = "بلايستيشن محمول (PSP)",
            nameEn = "PlayStation Portable",
            extensions = listOf("cso", "pbp", "iso"),
            badgeColorHex = "#818CF8", // Indigo
            targetCore = "PPSSPP Core",
            descriptionAr = "ألعاب PSP المحمولة بصيغ ISO و CSO المضغوطة."
        ),
        SystemPlatformInfo(
            code = "GBA",
            nameAr = "جيم بوي أدفانس",
            nameEn = "Game Boy Advance",
            extensions = listOf("gba"),
            badgeColorHex = "#C084FC", // Purple
            targetCore = "mGBA / VBA-Next",
            descriptionAr = "خراطيش نينتندو جيم بوي أدفانس (16-بت)."
        ),
        SystemPlatformInfo(
            code = "GBC",
            nameAr = "جيم بوي / كولور",
            nameEn = "Game Boy / Color",
            extensions = listOf("gb", "gbc"),
            badgeColorHex = "#34D399", // Emerald
            targetCore = "Gambatte Core",
            descriptionAr = "ألعاب الجيم بوي الكلاسيكي والألوان (8-بت)."
        ),
        SystemPlatformInfo(
            code = "NDS",
            nameAr = "نينتندو دي إس (NDS)",
            nameEn = "Nintendo DS",
            extensions = listOf("nds"),
            badgeColorHex = "#F472B6", // Pink
            targetCore = "melonDS / DeSmuME",
            descriptionAr = "ألعاب الشاشة المزدوجة لجهاز Nintendo DS."
        ),
        SystemPlatformInfo(
            code = "SNES",
            nameAr = "سوبر نينتندو (SNES)",
            nameEn = "Super Nintendo Entertainment System",
            extensions = listOf("sfc", "smc"),
            badgeColorHex = "#FBBF24", // Amber
            targetCore = "Snes9x Current",
            descriptionAr = "ألعاب سوبر نينتندو الكلاسيكية (16-بت)."
        ),
        SystemPlatformInfo(
            code = "NES",
            nameAr = "نينتندو إنترتينمنت سيستم (NES)",
            nameEn = "Nintendo Entertainment System",
            extensions = listOf("nes", "fds"),
            badgeColorHex = "#FB7185", // Rose
            targetCore = "FCEUmm / Mesen",
            descriptionAr = "ألعاب فاملي/كمبيوتر العائلة نينتندو الأصلي (8-بت)."
        ),
        SystemPlatformInfo(
            code = "N64",
            nameAr = "نينتندو 64 (N64)",
            nameEn = "Nintendo 64",
            extensions = listOf("n64", "v64", "z64"),
            badgeColorHex = "#A78BFA", // Violet
            targetCore = "Mupen64Plus-Next",
            descriptionAr = "ألعاب الجيل الخامس ثلاثية الأبعاد N64."
        ),
        SystemPlatformInfo(
            code = "GENESIS",
            nameAr = "سيجا ميجادرايف / جينيسيس",
            nameEn = "Sega Genesis / Mega Drive",
            extensions = listOf("gen", "md", "smd"),
            badgeColorHex = "#2DD4BF", // Teal
            targetCore = "Genesis Plus GX",
            descriptionAr = "ألعاب سيجا ميجادرايف الكلاسيكية."
        ),
        SystemPlatformInfo(
            code = "ARCHIVE",
            nameAr = "حزمة أرشيف ألعاب",
            nameEn = "Compressed ROM Archive",
            extensions = listOf("zip", "7z"),
            badgeColorHex = "#A3E635", // Lime
            targetCore = "Archive Extractor / Multi-Core",
            descriptionAr = "ملفات ROM مضغوطة قابلة للفك أو التحميل المباشر."
        )
    )

    fun inferPlatform(extension: String): SystemPlatformInfo {
        val cleanExt = extension.lowercase().removePrefix(".")
        for (platform in supportedPlatforms) {
            if (platform.extensions.any { it.equals(cleanExt, ignoreCase = true) }) {
                return platform
            }
        }
        return SystemPlatformInfo(
            code = "GENERIC",
            nameAr = "ملف لعبة عام",
            nameEn = "Generic ROM / Binary",
            extensions = listOf(cleanExt),
            badgeColorHex = "#94A3B8",
            targetCore = "DreamBox Universal Core",
            descriptionAr = "ملف لعبة لم يتم تحديد نظامه تلقائيًا."
        )
    }

    /**
     * Attempts to simulate launch verification.
     * As per specification, no real emulator core is installed yet.
     * This safely returns EngineNotInstalled with complete architectural context.
     */
    fun checkEngineStatus(game: GameItem): LaunchResult {
        if (!game.isAvailable) {
            return LaunchResult.FileUnavailable(
                reason = "الملف غير متوفر في مساره المسجل أو تم إلغاء صلاحية القراءة من قبل نظام التشغيل."
            )
        }
        val platform = inferPlatform(game.fileExtension)
        return LaunchResult.EngineNotInstalled(
            message = "لا يوجد محرك متوافق مثبت",
            engineName = platform.nameAr,
            platform = platform.nameEn,
            suggestedCore = platform.targetCore,
            detailsAr = "هذه النسخة من DreamBox هي واجهة تشغيل وإدارة مكتبة (Frontend). لم يتم تضمين نواة تشغيل برمجية (Core) متوافقة مع ألعاب ${platform.nameAr} بعد. صُممت بنية التطبيق لتوصيل نوى التشغيل المستقبلية تلقائيًا عبر واجهة EmulatorEngine دون الحاجة لإعادة إضافة ملفاتك."
        )
    }
}
