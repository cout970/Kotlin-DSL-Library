package kdl.internal.client

object TranslationManager {

    private val defaultTranslations = mutableMapOf<String, String>()

    fun getDefaultTranslation(key: String): String? {
        return defaultTranslations[key]
    }

    fun registerDefaultTranslation(key: String, default: String) {
        defaultTranslations[key] = default
    }
}