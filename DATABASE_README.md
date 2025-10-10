# SQLite Integration with Room Database

## 📋 Overview
This project uses Room Persistence Library for SQLite database integration in Android.

## 🗄️ Database Structure

### Entities
- **User**: Stores player information (username, email, high score, level, creation date)

### DAOs (Data Access Objects)
- **UserDao**: Provides database operations for User entity

### Repository
- **UserRepository**: Business logic layer with security validations

## 🔒 Security Features
- Parameterized queries prevent SQL injection
- Input validation and sanitization
- Type safety with Kotlin
- No raw SQL string concatenation

## 📊 Database Location
- **Path**: `/data/data/com.example.appplaypulse_grupo4/databases/app_database`
- **Access**: Use Android Studio Database Inspector for development

## 🚀 Usage Examples

### Create a User
```kotlin
val viewModel: MainViewModel = viewModel()
viewModel.createUser("username", "email@example.com")
```

### Update User Score
```kotlin
viewModel.updateUserScore(newScore)
```

### Get Leaderboard
```kotlin
val topUsers by viewModel.topUsers.collectAsState()
```

## 🧪 Testing
Run database tests with:
```bash
./gradlew connectedAndroidTest
```

## 🔍 Debugging
1. Use Android Studio Database Inspector
2. ADB shell: `adb shell`, then `sqlite3 /data/data/com.example.appplaypulse_grupo4/databases/app_database`

## 🛡️ Security Guidelines
- Always use parameterized queries (`:parameter`)
- Validate input at repository level
- Use type-safe parameters
- Never concatenate user input into SQL strings

## 📝 Common SQL Queries
```sql
-- View all users
SELECT * FROM users;

-- Top scorers
SELECT * FROM users ORDER BY highScore DESC LIMIT 10;

-- User statistics
SELECT COUNT(*) as total_users, AVG(highScore) as avg_score FROM users;
```
