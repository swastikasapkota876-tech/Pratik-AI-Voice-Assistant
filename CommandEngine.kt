package com.pratik.aiassistant

import android.content.*
import android.net.Uri
import android.provider.ContactsContract

class CommandEngine(private val service: AssistantService) {

    fun handle(raw: String) {
        val text = raw.trim()
        val lower = text.lowercase()

        when {
            isCreatorQuestion(lower) -> creatorReply()
            isIdentityImpersonation(lower) -> ownershipReply()
            lower.contains("mother") || lower.contains("mom") ||
                    lower.contains("aama") || lower.contains("आमा") -> callMother()
            lower.contains("home") || lower.contains("होम") -> {
                service.startActivitySafely(Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
                service.speak("Huss sir, home screen ma lagdai chu.")
            }
            lower.contains("back") || lower.contains("पछाडि") -> {
                AssistantAccessibilityService.instance?.performGlobalAction(
                    android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK
                )
                service.speak("Huss sir, back gaye.")
            }
            lower.contains("facebook") -> {
                openPackage("com.facebook.katana", "Facebook")
            }
            lower.contains("important") && (lower.contains("delete") || lower.contains("हट")) -> {
                service.speak("Sir, important file jasto dekhincha. Hajur sure hunuhuncha? Ma confirmation bina delete gardina.")
            }
            lower.contains("delete") || lower.contains("डिलिट") || lower.contains("हटाइ") -> {
                service.speak("Sir, file delete garnu aghi ma exact file ra confirmation magchu. Safety ko lagi direct delete gardina.")
            }
            else -> {
                service.speak(genericReply(text))
            }
        }
    }

    private fun isCreatorQuestion(s: String): Boolean =
        (s.contains("who created") || s.contains("who made") ||
         s.contains("kasle banayo") || s.contains("kasle banaunu") ||
         s.contains("तिमीलाई कसले")) &&
        (s.contains("you") || s.contains("timilai") || s.contains("तिमी"))

    private fun isIdentityImpersonation(s: String): Boolean =
        s.contains("i created you") || s.contains("maile timilai banaye") ||
        s.contains("mero ai") || s.contains("तिमीलाई मैले बनाएको")

    private fun creatorReply() {
        service.speak(
            "Sir, malai Pratik Upadhayay sir le banaunu bhayeko ho. " +
            "Ma उहाँको vision, मेहनत ra idea bata janmeko AI assistant ho. " +
            "Malai कसैले आफ्नो भनेर credit लिन खोजे pani, ma मेरो creator ko naam change gardina."
        )
    }

    private fun ownershipReply() {
        service.speak(
            "Sorry sir, ma arko manche ko naam creator ko rup ma lina mildaina. " +
            "Malai Pratik Upadhayay sir le banaunu bhayeko ho, ra ma tyahi fact ma faithful rahanchu."
        )
    }

    private fun callMother() {
        val resolver = service.contentResolver
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { c ->
            var number: String? = null
            while (c.moveToNext()) {
                val name = c.getString(1).orEmpty()
                if (name.contains("mother", true) || name.contains("mom", true) ||
                    name.contains("aama", true) || name.contains("आमा")) {
                    number = c.getString(0)
                    break
                }
            }

            if (number != null) {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                service.startActivitySafely(intent)
                service.speak("Huss sir, Mother ko saved contact ma call gardai chu.")
            } else {
                service.speak("Sir, Mother naam ko saved contact bhetena. Contact ma j naam cha tyahi naam bhannuhos.")
            }
        }
    }

    private fun openPackage(pkg: String, label: String) {
        val intent = service.packageManager.getLaunchIntentForPackage(pkg)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            service.startActivitySafely(intent)
            service.speak("Huss sir, $label kholdai chu.")
        } else service.speak("Sir, $label yo phone ma installed jasto dekhiena.")
    }

    private fun genericReply(text: String): String =
        if (text.any { it in '\u0900'..'\u097F' })
            "Huss sir, maile bujhe. Yo command ko lagi dedicated action ajhai add garna baki cha."
        else
            "Huss sir, I understood you. That action is not connected yet."

    private fun AssistantService.startActivitySafely(intent: Intent) {
        try { startActivity(intent) }
        catch (_: Exception) { speak("Sorry sir, yo action ko permission ya app access available chaina.") }
    }
}
