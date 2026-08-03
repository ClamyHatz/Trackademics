-- Trackademics shared schema.
-- Tables are created in dependency order: users first, then everything that
-- references it.

-- Slice 1: Accounts (Bay)

-- One row per user. Role is either STUDENT or TEACHER.
-- Username is unique so two people can't register the same name.
CREATE TABLE IF NOT EXISTS users (
                                     user_id   INTEGER PRIMARY KEY AUTOINCREMENT,
                                     username  TEXT    NOT NULL UNIQUE,
                                     password  TEXT    NOT NULL,
                                     role      TEXT    NOT NULL
);

-- Slice 2: Courses & Enrollment (Ayoung)

-- One class is one course being taught in one term by one teacher.
-- The unique constraint keeps the same code from being used twice in a term.
CREATE TABLE IF NOT EXISTS classes (
                                       class_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                                       class_code  TEXT    NOT NULL,
                                       title       TEXT    NOT NULL,
                                       term        TEXT    NOT NULL,
                                       teacher_id  INTEGER NOT NULL,
                                        FOREIGN KEY (teacher_id) REFERENCES users(user_id),
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
                                           FOREIGN KEY (student_id) REFERENCES users(user_id),
                                           UNIQUE (class_id, student_id)
    );


-- Slice 3: Assignments (Estefan)
-- One assignment belongs to one class.
-- The title, due date, and points are required.
-- The status starts as 'Not Started' when one is not provided.
CREATE TABLE IF NOT EXISTS assignments (
                                           assignment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           class_id INTEGER NOT NULL,
                                           title TEXT NOT NULL,
                                           description TEXT,
                                           due_date TEXT NOT NULL,
                                           points_possible REAL NOT NULL,
                                           status TEXT NOT NULL DEFAULT 'Not Started',
                                           FOREIGN KEY (class_id) REFERENCES classes(class_id)
    );
-- Slice 4: Grades & Statistics (Lily)
-- one grade belongs to one assignment
-- enrollment id gives us both student and class id
CREATE TABLE If NOT EXISTS grades (
    grade_id INTEGER PRIMARY KEY AUTOINCREMENT,
    enrollment_id INTEGER NOT NULL,
    assignment_id INTEGER NOT NULL,
    grade DOUBLE NOT NULL,
    weight DOUBLE NOT NULL,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id),
    FOREIGN KEY (assignment_id) REFERENCES assignments(assignment_id)
);
