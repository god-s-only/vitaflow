# VitaFlow 🌊

VitaFlow is an Android health & fitness app built with Kotlin and Jetpack Compose. It brings nutrition tracking, workout guidance, step tracking, and smart reminders together in one place.

## Features

- **Nutrition Tracking** — Log meals by meal type, search foods via the Spoonacular API, scan barcodes, or snap a photo of your food and let on-device ML Kit image labeling estimate calories and macros. Daily calorie/carb/protein/fat totals are calculated and stored automatically.
- **Water Intake** — Track daily hydration against a configurable target.
- **Workouts** — Browse exercises by body part (via the ExerciseDB API), with detailed instructions, target muscles, and equipment.
- **Step Tracking** — Sync daily steps, distance, calories burned, and active minutes from Health Connect, with a weekly bar chart and progress ring.
- **Smart Reminders** — Periodic WorkManager notifications nudge you to log meals based on your remaining calories for the day.
- **Authentication** — Firebase Auth sign-in/sign-up with session persistence via DataStore.

## Architecture

The project follows Clean Architecture with three layers. Dependencies point inward only — the domain layer knows nothing about data or presentation:

```
presentation (Compose UI, ViewModels, navigation)
     ↓ calls
domain (models, repository interfaces, use cases)
     ↑ implemented by
data (Room, Retrofit, DataStore, Health Connect, WorkManager workers, mappers)
```

- **Domain** (`domain/`) — Plain Kotlin data models (`Food`, `FoodEntry`, `DailyNutrition`, `DailySteps`, `StepsData`, `FoodAnalysisResult`, ...), repository interfaces, and one class per use case. No Android framework or persistence imports.
- **Data** (`data/`) — Room entities live in `data/local/entity/` and are mapped to/from domain models in `data/mappers/` (never exposed past the repository boundary). Retrofit services, Health Connect integration, DataStore preferences, and the `NotificationWorker`/`StepsSyncWorker` also live here.
- **Presentation** (`presentation/`) — Compose screens per feature, `State`/`Event` classes, and ViewModels that talk to domain use cases only.

Dependency injection is Hilt (`di/` modules). Coroutines + Flow are used throughout.

## Tech Stack

- **Language/UI:** Kotlin, Jetpack Compose (Material 3)
- **DI:** Hilt
- **Persistence:** Room (foods, food entries, daily nutrition, daily steps) + DataStore Preferences (targets, auth session)
- **Networking:** Retrofit + OkHttp + Gson (Spoonacular, ExerciseDB)
- **Health:** Health Connect (steps, distance, calories, exercise sessions)
- **Background:** WorkManager (periodic nutrition reminders, hourly steps sync)
- **ML:** ML Kit image labeling for food photo estimation, ML Kit barcode scanning
- **Auth:** Firebase Authentication
- **Images:** Coil 3 (incl. GIF support)

## Getting Started

### Prerequisites

- Android Studio (comes with a compatible JBR — the project uses Gradle 8.14)
- Android device or emulator, minSdk 26 (Android 8.0)

### Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/god-s-only/vitaflow.git
   ```
2. Add your API keys to `local.properties` in the project root:
   ```properties
   SPOONACULAR_API_KEY=your_spoonacular_key
   RAPIDAPI_KEY=your_rapidapi_key
   RAPIDAPI_HOST=exercisedb.p.rapidapi.com
   ```
3. Add your `google-services.json` for Firebase Auth (a placeholder is included).
4. Build and run from Android Studio, or:
   ```bash
   ./gradlew :app:assembleDebug
   ```

> **Note:** If your system JDK is newer than what Gradle 8.14 supports, point the build at Android Studio's bundled JBR: set `JAVA_HOME` to `<Android Studio>/jbr`.

### Health Connect

Step tracking uses Health Connect. The app detects whether it's installed and walks you through granting the `READ_STEPS`, `READ_DISTANCE`, `READ_ACTIVE_CALORIES_BURNED`, and `READ_EXERCISE` permissions on first use.

## Module Layout

```
app/src/main/java/com/vitaflow/app/
├── common/            # Shared constants (Routes, Resource wrapper, UIEvent)
├── data/
│   ├── local/         # Room DB, DAOs, entities, DataStore preferences
│   ├── mappers/       # Entity/DTO <-> domain model mapping
│   ├── remote/        # Retrofit APIs, DTOs, Health Connect service
│   ├── repository/    # Repository implementations
│   └── worker/        # NotificationWorker, StepsSyncWorker
├── di/                # Hilt modules
├── domain/
│   ├── models/        # Pure Kotlin domain models
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Use cases (auth, nutrition, recipes, steps, workout)
└── presentation/      # Compose UI, ViewModels, theme, navigation entry
```

## License

All rights reserved.
