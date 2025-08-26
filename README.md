# Taxi App - Android Jetpack Compose

This Android application is a Jetpack Compose implementation of the React taxi management system. It provides a comprehensive interface for managing taxi company operations including vehicles, drivers, trips, and requests.

## Features

### 🚗 Dashboard
- Company overview with key metrics
- Vehicle count, trip count, and pending requests
- Owner information display

### 🚙 Fleet Management
- Add new vehicles with brand, model, plate number, color, and seat count
- View all company vehicles in a clean list format
- Real-time updates to vehicle count

### 👥 Members Management
- Add drivers and dispatchers
- Email and role-based user management
- Employee list with contact information

### 🗺️ Trips Management
- Create new trips with departure/destination addresses
- Coordinate-based location system (lat/lng)
- Trip status management (draft, published, archived)
- Price and seat management
- Vehicle and driver assignment

### 📋 Requests Management
- View pending passenger requests
- Accept or decline requests
- Request details including passenger info and payment method

## Technical Stack

- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with StateFlow
- **Language**: Kotlin
- **Navigation**: Custom screen-based navigation
- **State Management**: ViewModel with StateFlow
- **Design**: Material Design 3 with custom taxi theme

## Design System

### Colors
- **Primary**: Taxi Yellow (#FFDD2C)
- **Background**: Cream (#FFFAF0)
- **Surface**: White (#FFFFFF)
- **Text**: Black (#000000)
- **Secondary**: Gray tones

### Components
- `TaxiCard`: Information display cards
- `TaxiTextField`: Custom input fields
- `TaxiDropdown`: Selection dropdowns
- `TaxiButton`: Action buttons
- `StatusBadge`: Trip status indicators
- `EmptyState`: Empty list placeholders

## Project Structure

```
app/src/main/java/com/example/taxi_app/
├── data/
│   └── Models.kt              # Data classes and navigation
├── ui/
│   ├── components/
│   │   ├── CommonComponents.kt    # Reusable UI components
│   │   └── TaxiLayout.kt          # Main app layout
│   ├── screens/
│   │   ├── DashboardScreen.kt     # Company overview
│   │   ├── FleetScreen.kt         # Vehicle management
│   │   ├── MembersScreen.kt       # Staff management
│   │   ├── TripsScreen.kt         # Trip management
│   │   └── RequestsScreen.kt      # Request handling
│   └── theme/
│       ├── Color.kt               # App color scheme
│       ├── Theme.kt               # Material theme
│       └── Type.kt                # Typography
├── viewmodel/
│   └── TaxiViewModel.kt           # State management
└── MainActivity.kt                # App entry point
```

## Localization

The app includes Armenian text labels matching the original React interface:
- Ընկերության վահանակ (Company Dashboard)
- Ֆլոտ (Fleet)
- Վարորդներ (Drivers)
- Երթուղիներ (Routes)
- Հայտեր (Requests)

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run the app on an emulator or device

## Future Enhancements

- Google Maps integration for trip visualization
- Real-time location tracking
- Push notifications for new requests
- Database integration (Room/SQLite)
- Network API integration
- User authentication
- Offline support

## Sample Data

The app comes with pre-populated sample data including:
- 3 sample vehicles (Toyota Camry, Honda Civic, Mercedes E-Class)
- 3 sample staff members (drivers and dispatcher)
- Company: "Taxi Yerevan"

This creates a fully functional demo environment for testing all features.
