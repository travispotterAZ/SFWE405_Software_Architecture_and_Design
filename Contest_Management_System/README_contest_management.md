# Programming Contest Management System

A full-stack Spring Boot REST API for managing programming contest registrations, teams, and participants — extended with asynchronous person intake via RabbitMQ messaging. Built incrementally across four assignments, each layer adding on top of the last: domain modeling, JPA persistence, REST endpoints, and finally message-driven architecture.

---

## Overview

This application manages the lifecycle of programming competitions. Contests can have preliminary rounds that feed into a main event. Teams of up to three students are registered to contests, validated against eligibility rules, and can be promoted from a preliminary round to the main contest. Coaches are assigned to teams, and managers oversee contests. On top of the REST API, a RabbitMQ integration allows people to be submitted asynchronously via a terminal client, with validation errors routed to a separate error queue.

---

## Tech Stack

- **Java 21**
- **Spring Boot 4.0** — Web MVC, Data JPA, Validation, AMQP
- **H2** — in-memory relational database
- **RabbitMQ** — asynchronous message broker
- **Lombok** — boilerplate reduction
- **Jackson** — JSON serialization with `JavaTimeModule` for `LocalDate` support
- **Maven** — build and dependency management

---

## Project Structure

```
src/main/java/assignment4/
├── Assignment4Application.java          # Spring Boot entry point
├── model/
│   ├── Person.java                      # Entity: Student, Coach, or Manager
│   ├── Team.java                        # Entity: team with members, state, rank
│   └── Contest.java                     # Entity: contest with capacity and registration window
├── data/
│   ├── PersonRepository.java            # JPA repository for Person
│   ├── TeamRepository.java              # JPA repository for Team
│   └── ContestRepository.java           # JPA repository for Contest
├── service/
│   ├── PersonService.java               # Person creation and coach assignment
│   ├── TeamService.java                 # Team CRUD, editable/read-only locking
│   ├── ContestService.java              # Contest CRUD, team registration, eligibility, promotion
│   ├── ReportService.java               # Reports (students grouped by age)
│   └── PopulateDataService.java         # Seeds database with sample data on startup
├── web/api/
│   ├── PersonController.java            # REST: /person
│   ├── TeamController.java              # REST: /teams
│   ├── ContestController.java           # REST: /contests
│   └── ReportController.java            # REST: /reports
├── messaging/
│   ├── RabbitPersonMessagingService.java  # Sends Person objects to RabbitMQ queue
│   └── PersonListener.java              # Consumes messages, validates, and persists
├── config/
│   └── RabbitConfig.java                # RabbitMQ queue, converter, and template config
└── clients/
    ├── RabbitClient.java                # CLI client: submits people via RabbitMQ
    └── RestClient.java                  # CLI client: submits people via HTTP POST
```

---

## Domain Model

### `Person`
Represents anyone involved in a contest. Has a `Role` of `Student`, `Coach`, or `Manager`. Stores name, email, university, and birthdate. Age is computed at runtime from birthdate for eligibility checks.

- A **Manager** manages a list of contests.
- A **Coach** is assigned to a list of teams.
- A **Student** is a member of a team.

### `Team`
A group of up to three `Person` members competing in a `Contest`. Tracks a `State` (`Accepted`, `Pending`, or `Cancelled`), a rank (1–5 for promotion eligibility), and an `editable` flag that locks the record from changes. A team can be a clone of another team when promoted to a higher-level contest.

### `Contest`
A competition with a capacity, registration window, and optional `preliminary_round` reference linking it to a sub-contest. Also has an `editable` flag.

---

## REST API Endpoints

### `/person`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/person/all` | List all people |
| POST | `/person/addPerson` | Add a person (validated — name required) |
| POST | `/person/addMember` | Add a student |
| POST | `/person/addCoach` | Add a coach |
| POST | `/person/addManager` | Add a manager |

### `/teams`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/teams/all` | List all teams |
| GET | `/teams/{id}` | Get team by ID |
| POST | `/teams/addTeam` | Create a team with members |
| POST | `/teams/assignTeamToCoach?team_id=&coach_id=` | Assign a coach to a team |
| POST | `/teams/editTeam` | Edit a team (only if editable) |
| POST | `/teams/setEditable` | Unlock a team for editing |
| POST | `/teams/setReadOnly` | Lock a team from editing |

### `/contests`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/contests/all` | List all contests |
| GET | `/contests/{id}` | Get contest by ID |
| GET | `/contests/{contestName}/teams` | List all teams in a contest |
| GET | `/contests/{contestName}/occupancy` | Show occupancy vs. capacity |
| POST | `/contests/addContest` | Create a contest |
| POST | `/contests/teamReg?contest_id=` | Register a team to a contest (runs eligibility check) |
| POST | `/contests/editContest` | Edit a contest (only if editable) |
| POST | `/contests/setEditable` | Unlock a contest for editing |
| POST | `/contests/setReadOnly` | Lock a contest from editing |
| POST | `/contests/promoteTeam?superContest_id=&team_id=` | Promote a team to a higher-level contest |

### `/reports`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/reports/students/age` | Report: students grouped by age |

---

## Team Registration Eligibility Rules

When a team is registered to a contest via `/contests/teamReg`, the following checks are enforced:

1. The team must have exactly **one coach** assigned.
2. The team must have exactly **three members**.
3. All members and the coach must be **distinct people** (no overlap).
4. The contest must not be **at capacity**.
5. All student members must be **under 24 years old**.
6. No member may already be on **another accepted team in the same contest**.

If any check fails, the team's state is set to `Cancelled`. If all pass, it is set to `Accepted`.

---

## Team Promotion

`/contests/promoteTeam` advances a team from a preliminary round to the main contest. In addition to the standard eligibility checks, the team's rank must be between **1 and 5**. A clone of the original team is created and registered to the target contest, preserving a reference to the source team via `cloneSource`.

---

## RabbitMQ Messaging

Two queues are declared in `RabbitConfig`:

| Queue | Purpose |
|-------|---------|
| `assignment4.person` | Incoming `Person` objects to be validated and saved |
| `assignment4.person.errors` | Validation failure messages |

**Flow:** `RabbitClient` → `assignment4.person` queue → `PersonListener` → validates with Jakarta Bean Validation → saves to DB or routes error to `assignment4.person.errors`.

Messages are serialized as JSON using `Jackson2JsonMessageConverter` with `JavaTimeModule` registered to handle `LocalDate` birthdate fields.

---

## CLI Clients

Two standalone terminal clients are included for adding people to the system:

### `RabbitClient`
Connects to Spring context (no web server), collects person details interactively, and sends them as a JSON message to RabbitMQ. The `PersonListener` then consumes, validates, and persists the entry.

```
Enter name: Alice
Enter email: alice@uofa.edu
Enter birthdate: 2004-06-10
Enter university: Arizona
Enter person Role: Student
Person sent to RabbitMQ!
```

### `RestClient`
A pure Java HTTP client (no Spring) that collects the same inputs and submits them directly to the `POST /person/addPerson` REST endpoint.

Both clients accept `exit` as the name input to quit, and validate date and role inputs locally before sending.

---

## Sample Data

On startup, `PopulateDataService` seeds the database with:

- **2 contests**: `UofA_Programming_Contest` (main, capacity 10) and `UofA-SWFE405_Final` (preliminary, capacity 5), linked as a preliminary round
- **2 managers**, **4 coaches**, **12+ students**
- **3 pre-built teams** (Bulldogs, Tigers, Bears) registered to the preliminary contest

---

## Running the Application

### Prerequisites
- Java 21
- Maven
- RabbitMQ running locally on the default port (`localhost:5672`) with default credentials (`guest` / `guest`)

### Start the server

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. The H2 database is populated automatically on startup.

### Run a CLI client

In a separate terminal, run either client as a standard Java main class through your IDE or via Maven, pointing to `RabbitClient` or `RestClient` as the main class.

---

## Configuration

`application.yml` configures RabbitMQ connection defaults. The host and port default to `localhost:5672` (standard RabbitMQ defaults) and can be uncommented and changed if needed:

```yaml
spring:
  rabbitmq:
    # host: localhost
    # port: 5672
    username: guest
    password: guest
```

---

## Postman Collection

`PotterTravis_Assignment4_AllPeople.postman_collection.json` is included and can be imported directly into Postman to test the `/person` endpoints with pre-built requests.
