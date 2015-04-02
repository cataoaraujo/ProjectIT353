
CREATE TABLE UserAccount
(
id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
firstName varchar(45) NOT NULL,
lastName varchar(45) NOT NULL,
userID varchar(45) NOT NULL UNIQUE,
password varchar(45) NOT NULL,
email varchar(45) NOT NULL,
securityQuestion varchar(45) NOT NULL,
securityAnswer varchar(45) NOT NULL,
accountReason varchar(128) NOT NULL,
type varchar(20) NOT NULL,
accountApproval boolean
);

CREATE TABLE Project
(
id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
name varchar(128) NOT NULL,
student_id int NOT NULL,
courseNumber varchar(15),
liveLink varchar(128),
abstract varchar(128),
screencastLink varchar(128),
semester varchar(20),
dateCreated TIMESTAMP,
highlighted boolean
);

CREATE TABLE Committee
(
commitee_id int GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
project_id int NOT NULL,
committeeEmail varchar(45) NOT NULL,
type varchar(45) NOT NULL,
primary key(project_id, committeeEmail)
);

CREATE TABLE ProjectSubmission
(
submission_id int GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
project_id int NOT NULL,
document_id int NOT NULL,
type varchar(45) NOT NULL,
approved boolean,
dateSubmitted TIMESTAMP,
primary key(submission_id)
);

CREATE TABLE Approval
(
submission_id int NOT NULL,
committee_id int NOT NULL,
approved boolean,
primary key(submission_id, committee_id)
);

CREATE TABLE Documents
(
id int PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
documentName varchar(45) NOT NULL,
documentLink varchar(45) NOT NULL
);

CREATE TABLE ProjectStatistics
(
project_id int NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
views int NOT NULL,
downloads int NOT NULL
);

CREATE TABLE Keyword
(
id int GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
keyword varchar(45) NOT NULL PRIMARY KEY
);

CREATE TABLE ProjectKeywords
(
keyword_id int NOT NULL,
project_id int NOT NULL,
primary key(keyword_id, project_id)

);

CREATE TABLE Subscribers
(
keyword_id int NOT NULL,
email varchar(45) NOT NULL,
primary key(keyword_id, email)
);
