USE northstar;

CREATE TABLE tblAppConfig (
  AppConfigID       INTEGER       AUTO_INCREMENT PRIMARY KEY,
  AppConfigName     VARCHAR(256)  NOT NULL,
  AppConfigType     SMALLINT      NOT NULL,
  AppConfigOrder    SMALLINT      NOT NULL DEFAULT 0,
  AppConfigValInt   INTEGER       NULL,
  AppConfigValChar  VARCHAR(256)  NULL,
  AppConfigValDate  DATETIME      NULL,
  ConfidentialVal   INTEGER       NOT NULL DEFAULT 0,
  CreateDate        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tblAppPassphrase (
  PassphraseID      INTEGER       AUTO_INCREMENT PRIMARY KEY,
  Passphrase        VARCHAR(512)  NOT NULL,
  CreateDate        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

/* App Config Types:
   int = 0
   char = 1  
   date = 2
   boolean = 3 */

CREATE TABLE tblAppTelemetry (
  TelemetryID      INTEGER       AUTO_INCREMENT PRIMARY KEY,
  ExternalIP       VARCHAR(256)  NOT NULL,
  EmailsProcessed  INTEGER       NOT NULL DEFAULT 0,
  EmailsResponded  INTEGER       NOT NULL DEFAULT 0
);

CREATE TABLE tblAuthorization (
  AuthID           INTEGER       AUTO_INCREMENT PRIMARY KEY,
  AuthEmail        VARCHAR(256)  NOT NULL,
  AuthDesc         VARCHAR(512)  NOT NULL,
  CreateDate       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tblEventType (
  EventTypeID      SMALLINT      PRIMARY KEY,
  EventTypeName    VARCHAR(50)   NOT NULL
);

INSERT INTO tblEventType VALUES (0, 'INFO');
INSERT INTO tblEventType VALUES (1, 'WARN');
INSERT INTO tblEventType VALUES (2, 'ERROR');
INSERT INTO tblEventType VALUES (3, 'FATAL');

CREATE TABLE tblEvent (
  EventID          INTEGER       AUTO_INCREMENT PRIMARY KEY,
  EventTypeID      SMALLINT      NOT NULL,
  EventCode        VARCHAR(16)   NOT NULL,
  EventMessage     VARCHAR(2048) NOT NULL,
  CreateDate       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  Acknowledged     INTEGER       NOT NULL DEFAULT 0,
  CONSTRAINT fk_tblEvent_tblEventType FOREIGN KEY (EventTypeID) REFERENCES tblEventType(EventTypeID)
);
CREATE INDEX idxEvent_EventTypeID_Acknowledged ON tblEvent (EventTypeID, Acknowledged);