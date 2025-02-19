# Mobile Project Documentation: Habit Tracker with Pomodoro

This project is a mobile application designed to help users track their daily habits and study progress using the Pomodoro technique. The app allows users to create, edit, and delete habits, track time spent on each habit, and visualize progress through interactive charts. The Pomodoro feature helps users manage their study sessions with timed focus and break intervals.

## Technologies Used
* Kotlin
* SQLite
* MPAndroidChart
* XML Layouts
* Dependency Management: Gradle

## Features
1. Habit Management:
* Create, edit, and delete habits.
* Set daily time goals for each habit.
2. Progress Tracking:
* Track time spent on each habit daily.
* View progress through bar charts (last 7 days) and pie charts (daily distribution).
3. Pomodoro Timer:
* Set custom focus and break durations.
* Associate Pomodoro sessions with specific habits.
* Notifications for focus and break transitions.
4. Data Visualization:
* Interactive bar and pie charts for progress tracking.
## Technical Implementation
### Database
The app uses an SQLite database managed by the DatabaseHelper class. The database consists of three tables:
1. habitos:
* `id`: Primary key.
* `nome`: Habit name.
* `tempoMeta`: Daily time goal (in minutes).
* `dataCriacao`: Creation date.
2. progresso:
* `id`: Primary key.
* `habitoId`: Foreign key referencing habitos.
* `data`: Date of progress entry.
* `tempoEstudado`: Time spent (in minutes).
3. pomodoro:
* `id`: Primary key.
* `duracaoFoco`: Focus duration (in minutes).
* `duracaoPausa`: Break duration (in minutes).
* `numeroCiclos`: Number of Pomodoro cycles.
### Models
1. Habito:
* Represents a habit with attributes: `id`, `nome`, `tempoMeta`, and `dataCriacao`.
1. ProgressoDiario:
* Represents daily progress with attributes: `id`, `habitoId`, `data`, and `tempoEstudo`.
### Repositories
1. HabitoRepository:
* Manages CRUD operations for the `habitos` table.
* Methods: `adicionarHabito`, `getHabitos`, `updateHabito`, `deleteHabito`, `getIdDoHabito`.
2. ProgressoRepository:
* Manages CRUD operations for the progresso table.
* Methods: adicionarProgressso, getProgressoDiario, atualizarProgresso.
### Adapters
1. HabitosAdapter:
* Binds habit data to the RecyclerView.
* Handles item clicks for editing and starting the timer.
* Displays habit name, time goal, and daily progress.
### Main Activities
1. HabitosActivity:
Main activity for managing habits and tracking progress.
Features:
* Add, edit, and delete habits.
* Start/stop timers for habits.
View progress charts (bar and pie charts).
2. PomodoroActivity:
* Implements the Pomodoro technique.
* Features:
* Set custom focus and break durations.
* Associate sessions with habits.
* Notifications for focus/break transitions.
## Class and Method Functionalities
### HabitosActivity
1. `iniciarTemporizador(habito: Habito)`:
* Starts a timer for the selected habit.
* Updates progress in real-time.
2. `pausarTemporizador()`:
* Pauses the active timer.
3. `pararTemporizador()`:
* Stops the timer and saves progress to the database.
4. `atualizarGrafico()`:
* Updates the bar chart with progress data for the last 7 days.
5. `setupPieChartMinimalista()`:
* Configures the pie chart to show daily progress distribution.
### PomodoroActivity
1. `startTimer()`:
* Starts the Pomodoro timer (focus or break).
* Registers progress for the selected habit.
2. `pauseTimer()`:
* Pauses the timer.
3. `resetTimer()`:
* Resets the timer to 00:00.
4. `carregarHabitos()`:
* Loads habits into the Spinner for selection.
5. `mostrarNotificacao(titulo: String, mensagem: String)`:
* Displays notifications for focus/break transitions.
