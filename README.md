<!--
CST 338 Project 2 — README template.
Copy this file into the ROOT of your team's repository as README.md and keep it current.
This README is your project dashboard: it is the first thing the instructor reads when
grading, and a working, up-to-date README is part of your integration score.

GitHub Issues are your LIVE tracker — every slice task, enhancement, and scope decision is
an Issue: assigned to its owner, labeled (slice-1, testing, enhancement, will-not-do,
extra-credit), and closed by a PR via "Closes #N". The tables below link into those Issues
and PRs. Replace every <placeholder> and delete this comment before you submit.
-->

# <App Name>

<One-line description.> CST 338 Project 2 — Team ALTool Devs.

## Grade / Assignment Tracker

## Team & Slice Ownership
| Slice | Owner | GitHub username | Issues | Branch(es) | PR(s) | Enhancement chosen | Status      |
|-------|-------|-----------------|--------|------------|-------|--------------------|-------------|
| 1 — Accounts | Bay Shahryar | baycs | #1 #20 #21 #22 | bay/user-dao-impl, bay/auth-service, bay/login-scene, bay/register-scene, bay/home-logout, bay/testfx-login | #23 #25 #32 #35 #47 #48 #49 #52 #56 | Notifications/alerts | complete |
| 2 — Courses & Enrollment | Ayoung Choi | achoi0123 | #11 #12 #13 #14 | ayoung/courses-enrollment, ayoung/enrollment-dao | #16 #24 #30 #40 | Data binding (ObservableList / Property) | in-progress |
| 3 — Assignments | Estefan Vicencio | stef-VnV | #17 #18 #19 | estefan/assignments estefan/assignment-data estefan/assignment-dao estefan/assignment-scene estefan/assignment-form estefan/assignment-validation | #5 #26 #27 #31 | TableView populated with live data | complete     |
| 4 — Grades & Statistics| Lily Keus | ClamyHatz | #8 #9 #10 #33 #37 #39 | lily/grade-viewer, lily/grade-var-patch, lily/grade-viewer-DAO-and-Test, lily/home-page, lily/fake-data-insertion, lily/fxml-visuals, lily/AI-Tests-And-Code-Review | #28 #29 #34 #38 #43 #51 #54 | TableView / ListView populated with live data | complete |

_Status values: planned · in-progress · complete_

## WILL NOT DO (declared scope cuts)
_Slices and beyond-scope items we are consciously NOT building. Move an item to a tracked
Issue if the team later decides to attempt it for extra credit._

- Slice 5 — <name>: not building (team size).


## Code Review Log
| PR | Author | Human reviewer(s) | AI review (link) | Outcome |
|----|--------|-------------------|------------------|---------|
| #24 | achoi0123 | ClamyHatz | | merged |
| #26 | stef-VnV | baycs | | merged |
| #27 | stef-VnV | ClamyHatz | | merged |
| #30 | achoi0123 | stef-VnV | | merged |
| #31 | stef-VnV | baycs | | merged |
| #40 | achoi0123 | ClamyHatz | [adjudication](https://github.com/ClamyHatz/Trackademics/pull/40) | merged |
| #28 | ClamyHatz | achoi0123 |  | merged |
| #29 | ClamyHatz | ClamyHatz |  |merged  |
| #34 | ClamyHatz | baycs |  | merged |
| #38 | ClamyHatz | stef-VnV |  | merged |
| #42 | stef-VnV | ClamyHatz | | merged |
| #43 | ClamyHatz | stef-VnV |  | merged |
| #44 | stef-VnV | achoi0123 | [adjudication](https://github.com/ClamyHatz/Trackademics/pull/44#issue-5096052086) | merged |
| #51 | ClamyHatz | stef-VnV |  | merged |
| #54 | ClamyHatz | stef-VnV | [adjudication](https://github.com/ClamyHatz/Trackademics/pull/54) | merged |
| #55 | ClamyHatz | stef-VnV |  | merged |
|  |  |  |  |  |

## AI Usage Log
- **AI-drafted tests:** see [TESTING.md](TESTING.md) — per owner.
  - Bay Shahryar — AI-drafted UserDao and AuthService tests, curated in [TESTING.md](TESTING.md).
- **AI code reviews:** <PR link + adjudication note> — per owner.
  - Ayoung Choi — [PR #40](https://github.com/ClamyHatz/Trackademics/pull/40), adjudicated in the PR description: accepted the test cleanup fix (5d54fde), rejected the concurrency, JDBC date, and extra coverage suggestions.
  - Bay Shahryar — [PR #35](https://github.com/ClamyHatz/Trackademics/pull/35), adjudicated in the PR and TESTING.md: accepted the insert() no-generated-key fix and the update() null guard, rejected password hashing.

## Extra Credit Log
| Item | Who | Evidence (Issue/PR) |
|------|-----|---------------------|
| Built Slice 5 | |  |

## Build & Run
```
./gradlew run        # launch the app
./gradlew test       # run the test suite
```
## Log-in Credentials
| username | password | account type |
|------|-----|---------------------|
| Prf. Stewart | password | TEACHER |
| Dr. Doctor | doctor | TEACHER |
| Bob | password1 | STUDENT |
| Sally | CoolThing72 | STUDENT |
| Charlie | passwordz | STUDENT|
| Joe | SomethingIG | STUDENT |

Requirements: JDK <version>, JavaFX <version>.
