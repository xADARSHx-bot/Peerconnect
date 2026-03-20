# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

PeerConnect+ is a Java desktop application built with Swing that connects students for academic peer support and book exchange. The application uses SQLite for data persistence and follows a modular MVC architecture.

## Development Setup

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- SQLite JDBC driver: `sqlite-jdbc-3.44.1.0.jar` (included in project root)
- SLF4J logging libraries: `slf4j-api-1.7.36.jar` and `slf4j-nop-1.7.36.jar` (included in project root)
- FlatLaF library: `flatlaf-3.2.jar` (referenced in classpath)
- Eclipse IDE (project configured for Eclipse)

### Building and Running

```powershell
# Compile the project (from project root)
javac -cp "sqlite-jdbc-3.44.1.0.jar;slf4j-api-1.7.36.jar;slf4j-nop-1.7.36.jar;C:/Users/User/Downloads/flatlaf-3.2.jar;src" -d bin src/com/peerconnect/**/*.java

# Run the application
java -cp "bin;sqlite-jdbc-3.44.1.0.jar;slf4j-api-1.7.36.jar;slf4j-nop-1.7.36.jar;C:/Users/User/Downloads/flatlaf-3.2.jar" com.peerconnect.ui.MainApplication

# Alternative: Run directly in Eclipse
# Right-click MainApplication.java → Run As → Java Application
# (Make sure to add SLF4J JARs to Eclipse classpath if needed)
```

### Database
- SQLite database file: `peerconnect.db` (auto-created in project root)
- Connection string: `jdbc:sqlite:peerconnect.db`

## Architecture

### Core Components

**Model Layer (`com.peerconnect.model`)**
- `UserProfile` - Core data model containing user credentials, academic strengths, subjects needing support, and book lists
- `PeerMatch` - Data model for peer matching results and compatibility scores
- `ChecklistItem` - Task model for collaborative checklists with creator tracking
- `Book` - Enhanced book model with title, author, ISBN, category, condition, and owner details

**Data Layer (`com.peerconnect.database`)**
- `DatabaseManager` - Handles SQLite operations with prepared statements and connection management
- Auto-creates users table on initialization
- Uses comma-separated strings to store book lists in database

**Business Layer (`com.peerconnect.manager`)**
- `ProfileManager` - Business logic for user authentication and account creation
- `PeerMatchingManager` - Algorithm for finding compatible study partners
- Acts as bridge between UI and database layer

**UI Layer (`com.peerconnect.ui`)**
- `MainApplication` - Main entry point using CardLayout for panel switching
- `LoginPanel` - User authentication interface
- `CreateAccountPanel` - New user registration interface  
- `ProfileDashboard` - User profile management interface
- `PeerMatchingPanel` - Interface for finding and viewing peer matches
- `ChecklistPanel` - Collaborative task management for matched peers
- `BookExchangePanel` - Full-featured book listing and exchange interface
- `Theme` - Centralized UI styling with FlatLaF integration
- `RoundedButton` - Custom styled button component
- Uses Swing with modern theming and responsive layouts

### Data Flow
1. UI components interact with `ProfileManager`
2. `ProfileManager` delegates database operations to `DatabaseManager`
3. `DatabaseManager` handles SQLite persistence
4. `UserProfile` objects are passed between layers

## Development Status & Roadmap

### ✅ Completed Features
- User Profile data model and storage
- SQLite database integration with prepared statements
- Core UI panels (Login, Create Account, Profile Dashboard)
- User authentication and account creation
- Basic profile management interface
- Peer matching system with `PeerMatchingManager` and `PeerMatchingPanel`
- Collaborative checklist system with shared workspace functionality
- **Book Exchange System** - Full-featured book listing, browsing, and contact management
- Modern UI theming with FlatLaF integration

### 🚧 Potential Enhancements
- Book exchange persistence (currently in-memory)
- Advanced book search and filtering
- Book exchange matching algorithms with peer compatibility
- Real-time collaboration features
- User notification system

## Key Development Guidelines

### Database Operations
- All database access goes through `DatabaseManager`
- Use prepared statements to prevent SQL injection
- Handle `SQLException` appropriately with user-friendly messages
- Book lists stored as comma-separated strings (consider JSON for complex data)

### UI Development
- Follow existing Swing patterns with `CardLayout` for main navigation
- Use `BoxLayout` and `FlowLayout` for consistent spacing
- All UI actions should go through `MainApplication` for panel switching
- Show appropriate `JOptionPane` messages for user feedback

### Code Organization
- Maintain package structure: `model`, `database`, `manager`, `ui`
- Keep business logic in manager classes, not UI components
- Use dependency injection pattern (manager classes passed to UI components)

### Testing Approach
```powershell
# Manual testing workflow
java -cp "bin;sqlite-jdbc-3.44.1.0.jar;slf4j-api-1.7.36.jar;slf4j-nop-1.7.36.jar;C:/Users/User/Downloads/flatlaf-3.2.jar" com.peerconnect.ui.MainApplication

# Test scenarios:
# 1. Create new account → Login → Update profile
# 2. Login with invalid credentials (should fail gracefully) 
# 3. Create duplicate username (should show error)
# 4. Find peer matches → View compatibility scores
# 5. Create collaborative checklist → Add/complete tasks
```

## Future Development Notes

### Current Implementation Notes
- Peer matching is implemented in `com.peerconnect.matching.PeerMatchingManager`
- `DatabaseManager.getAllUsersExcept()` provides user queries for matching
- Matching algorithm compares `academicStrengths` with `subjectsNeedingSupport`
- Collaborative checklists use shared workspace IDs for multi-user task management

### For Book-Exchange Feature  
- Consider implementing in `com.peerconnect.exchange` package
- Will need enhanced book data model (ISBN, condition, etc.)
- May require additional database tables for book metadata

### Performance Considerations
- Current design loads full user objects - consider lazy loading for large datasets
- Book lists as comma-separated strings may need refactoring for complex queries
- Consider connection pooling if multiple concurrent users expected

## Troubleshooting

### Common Issues

**NoClassDefFoundError: org/slf4j/LoggerFactory**
- Ensure SLF4J JAR files are in classpath: `slf4j-api-1.7.36.jar` and `slf4j-nop-1.7.36.jar`
- Download from: https://repo1.maven.org/maven2/org/slf4j/

**Buttons not appearing or not styled correctly**
- Verify FlatLaF library is in classpath: `flatlaf-3.2.jar`
- Check that `Theme.apply()` is called before creating UI components
- Ensure `CustomButtons` and `RoundedPanels` classes are compiled in `bin/com/peerconnect/ui/components/`
- Button texts are intentionally kept short for better UI fit (e.g., "Find Peers" instead of "Find Study Partners")

**Database connection issues**
- Check that `peerconnect.db` file is created in project root
- Verify SQLite JDBC driver is accessible: `sqlite-jdbc-3.44.1.0.jar`
- Database file is auto-created on first run

**UI components not responding**
- All button actions are handled by `MainApplication` for panel navigation
- Check console output for any runtime exceptions
- Verify all required dependencies are in classpath
