DROP DATABASE IF EXISTS task;
CREATE DATABASE task WITH ENCODING='UTF8' TEMPLATE='template0' OWNER='task';

DROP TABLE IF EXISTS googleuser CASCADE;
CREATE TABLE IF NOT EXISTS googleuser (
  userid VARCHAR(255) NOT NULL,
  sort VARCHAR(255) NOT NULL,
  PRIMARY KEY (userid)
);

DROP TABLE IF EXISTS task CASCADE;
CREATE TABLE IF NOT EXISTS task (
  taskid serial NOT NULL,
  userid VARCHAR(255) NOT NULL,
  taskname varchar(255) NOT NULL,
  estimatedtime double precision NOT NULL,
  scheduleddate varchar(255) NOT NULL,
  starttime varchar(255) NOT NULL,
  accumlatedtime varchar(255),
  done boolean default false,
  completiondate varchar(255),
  priority varchar(255),
  PRIMARY KEY (taskid)
);