# PRATIK AI Voice Assistant

A native Android starter project for a multilingual, emotional voice assistant.

## Included

- Voice input using Android SpeechRecognizer
- Voice output using Android TextToSpeech
- Nepali/English language selection
- "Mother/Aama" contact lookup and call
- Home and Back commands
- Facebook launcher
- Accessibility-service foundation for future UI control
- Creator/ownership personality:
  - "Pratik Upadhayay sir le malai banaunu bhayeko ho."
  - Refuses to falsely credit another creator
- Safety guard for deletion commands
- Foreground microphone service

## Important safety design

The assistant does NOT silently delete files. If a command appears destructive or involves an important-looking file, it asks for confirmation. For a real production assistant, sensitive actions should require an explicit user confirmation UI.

## Build

Open this folder in Android Studio, let Gradle sync, then build/install the `app`.

Grant:
1. Microphone
2. Contacts
3. Phone
4. Accessibility / Phone Control (only if you want UI automation)

## Expanding to a real AGI-style assistant

Connect an LLM backend (Gemini/OpenAI-compatible API/local model) to `CommandEngine`, but keep a permission layer between the model and Android actions.

Recommended architecture:

Voice -> Speech-to-Text -> LLM/Planner -> Permission/Safety Gate -> Android Tool -> Result -> TTS

Never give an LLM unrestricted destructive access to the device.
