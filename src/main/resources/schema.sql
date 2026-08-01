-- Trackademics shared schema.
-- Tables are created in dependency order: users first, then everything that
-- references it.

-- Slice 1: Accounts (Bay)
-- users table goes here.

-- Slice 2: Courses & Enrollment (Ayoung)

-- One class is one course being taught in one term by one teacher.
-- The unique constraint keeps the same code from being used twice in a term.
CREATE TABLE IF NOT EXISTS classes (
                                       class_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                                       class_code  TEXT    NOT NULL,
                                       title       TEXT    NOT NULL,
                                       term        TEXT    NOT NULL,
                                       teacher_id  INTEGER NOT NULL,
                                       UNIQUE (class_code, term)
    );

-- Links students to the classes they're in.
-- Dropping a class sets status to 'dropped' instead of deleting the row,
-- so we keep a record of it.
-- The unique constraint keeps a student from enrolling in the same class twice.
CREATE TABLE IF NOT EXISTS enrollments (
                                           enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           class_id      INTEGER NOT NULL,
                                           student_id    INTEGER NOT NULL,
                                           enrolled_on   TEXT    NOT NULL,
                                           status        TEXT    NOT NULL DEFAULT 'active',
                                           FOREIGN KEY (class_id) REFERENCES classes(class_id),
    UNIQUE (class_id, student_id)
    );

-- TODO: add FK from classes.teacher_id and enrollments.student_id to
-- users(user_id) once the users table lands.

-- Slice 3: Assignments (Estefan)
-- assignments table goes here.

-- Slice 4: Grades & Statistics (Lily)
-- grades table goes here.