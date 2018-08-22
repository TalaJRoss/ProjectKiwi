# ProjectKiwi
A SQL assignment generator, runner and marker.

## Functionality
Note: the project is currently in development and not all features have been implemented and/or tested yet.

### Lecturer-end Application
- Allows the lecturer to upload csv files containing students' information, the assignment's query data and
the questions. This information is then loaded into a MYSQL database on the server.
- Autogenerates additional questions and data.
- Provides view of a list of student grades.

### Student-end Application
- Processes student 'logins', checking that the student number is in the database on the server.
- Runs assignments, allowing for student to check out put of their answer statements as well as submitting their answers.
- Provides highest grade, number of submissions remaining and deadline information.

### Server
- Interacts with the MySQL database.
- Saves csv files from lecturer.
- Autogenerates data and questions for lecturer.
- Autogenerates assignements fairly based on student number.
- Marks student's answers by comparing the expected output and actual output of the student's SQL statement.
- Calculates assignment grades and updates student details.

## Authors
- Tala Ross
- Nikai Jagganath
- Steve Shun Wang
