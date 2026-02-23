# Java-Thread-Runner

A small Java race demo rebuilt as a web app.

This project keeps the original Java/Swing source and adds a Spring Boot + HTML/CSS/JS version with a Windows 98-inspired frontend.

## Project Structure

- `spring-race-demo/`
  - Spring Boot backend (race engine + REST API)
  - Static frontend (`index.html`, `styles.css`, `app.js`)
- `Java threads race/`
  - Original Java source and assets from the desktop version

## How The Application Works

### Backend (Spring Boot)

The backend is responsible for race state and race updates.

- Creates a race with a configurable number of racers
- Starts a timed update loop
- Moves each racer forward by a random amount each tick
- Detects the winner when a racer reaches the finish line
- Exposes race state through REST endpoints

Main endpoints:

- `POST /api/races?racers=5` - create a race
- `GET /api/races/{id}` - get current race state
- `POST /api/races/{id}/start` - start race
- `POST /api/races/{id}/restart` - reset race

### Frontend (HTML/CSS/JS)

The frontend is served by Spring Boot and renders the UI in the browser.

- Windows 98-style interface
- Uses the original `races.gif` runner image
- Polls the backend for race updates and moves racers across the lanes
- Includes hidden "easter egg" screens (About page and image popup)

## How To Run Locally

### Requirements

- Java 11+
- Maven

### Run with Maven

From the repo root:

```powershell
cd .\spring-race-demo
mvn spring-boot:run
```

Open:

- `http://localhost:8080`

### Run from a packaged JAR

```powershell
cd .\spring-race-demo
mvn package
java -jar .\target\spring-race-demo-0.0.1-SNAPSHOT.jar
```

Then open:

- `http://localhost:8080`

## Using The App

- `New Race` creates a new race and generates a race ID
- `Start` begins the race
- `Restart` resets the current race
- `About` (menu item) opens the hidden About/source viewer page
- `Original` (inside the source viewer toolbar) opens a popup showing the original desktop screenshot
- Top-right `X` button opens a close confirmation dialog (Win98-style easter egg)

## Notes

- This is a demo app and stores race state in memory (no database)
- Refreshing the page resets frontend UI state (the backend app keeps running while the server is up)
- GitHub Pages alone cannot run the Spring Boot backend (a separate backend host is required)
