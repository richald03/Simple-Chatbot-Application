# Simple Chatbot Application

An Android chatbot application built with Java and the Gemini API, designed as a polished portfolio project with production-minded UX patterns, defensive error handling, and secure local configuration practices.

## Highlights

- Professional chat interface with distinct user and bot bubbles
- Message timestamps and smooth scrolling behavior
- Dark/light mode toggle
- Runtime chat color theme customization (Blue, Green, Rose)
- Profile badges for user and bot
- Improved AI response cleanliness (plain-text normalization)
- Friendly fallback messaging for common API errors, including quota/rate-limit scenarios
- Chat state preservation across UI mode/theme changes

## Tech Stack

- Android (Java, XML)
- RecyclerView for chat rendering
- Material components
- Google Gemini SDK (`com.google.ai.client.generativeai:generativeai:0.9.0`)
- Guava futures for async callback handling

## Project Structure

```text
app/
  src/main/
    java/com/example/simplechatbot/
      MainActivity.java
      ChatMessage.java
      ChatAdapter.java
      ChatTheme.java
    res/
      layout/
      drawable/
      values/
      values-night/
```

## Getting Started

### 1. Requirements

- Android Studio (latest stable recommended)
- JDK 8+ (project configured for Java 8 compatibility)
- Android SDK (minSdk 24, targetSdk 34)
- Gemini API key from Google AI Studio

### 2. Configure API Key

Use `local.properties` (already ignored by git):

```properties
sdk.dir=C\:\\Users\\<YourUser>\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=YOUR_REAL_API_KEY_HERE
```

The app reads this key via `BuildConfig.GEMINI_API_KEY`.

## Run the App

1. Open the project in Android Studio.
2. Sync Gradle.
3. Run on an emulator or physical Android device.
4. Send a test prompt in chat.

## Security Notes

- API keys are not hardcoded in source files.
- Sensitive local files and signing artifacts are excluded via `.gitignore`.
- For true production hardening, route AI calls through your own backend instead of exposing direct client-side API usage.

## Error Handling Coverage

The app includes explicit fallback handling for:

- Model not found (`404`)
- Authentication/authorization (`401`, `403`)
- Quota/rate limit (`429`, quota/resource exhausted signals)
- Network failures
- Empty AI responses

## Portfolio Positioning

This project demonstrates:

- Mobile UI craftsmanship and theming systems
- Asynchronous API integration in Android
- State resilience and UX continuity
- Practical security hygiene for app repositories

## License

This repository is intended for educational and portfolio use.
