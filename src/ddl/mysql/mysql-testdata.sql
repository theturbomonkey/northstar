use northstar;

INSERT INTO tblAppConfig (AppConfigName, AppConfigType, AppConfigOrder, AppConfigValInt, ConfidentialVal)  VALUES ('Enabled',                  3, 0, 0, 0);
INSERT INTO tblAppConfig (AppConfigName, AppConfigType, AppConfigOrder, AppConfigValChar, ConfidentialVal) VALUES ('Email Address',            1, 1, '', 0);
INSERT INTO tblAppConfig (AppConfigName, AppConfigType, AppConfigOrder, AppConfigValChar, ConfidentialVal) VALUES ('Password',                 1, 2, '', 1);
INSERT INTO tblAppConfig (AppConfigName, AppConfigType, AppConfigOrder, AppConfigValChar, ConfidentialVal) VALUES ('Outbound SMTP',            1, 3, '', 0);
INSERT INTO tblAppConfig (AppConfigName, AppConfigType, AppConfigOrder, AppConfigValChar, ConfidentialVal) VALUES ('Inbound IMAP',             1, 4, '', 0);
INSERT INTO tblAppConfig (AppConfigName, AppConfigType, AppConfigOrder, AppConfigValChar, ConfidentialVal)  VALUES ('Email Subject Passphrase', 1, 5, '', 0);
INSERT INTO tblAppConfig (AppConfigName, AppConfigType, AppConfigOrder, AppConfigValInt, ConfidentialVal)  VALUES ('Poll Interval (sec)',      0, 6, 60, 0);

INSERT INTO tblAppTelemetry (ExternalIP, EmailsProcessed, EmailsResponded) VALUES ('00.00.00.00', 12, 1);

INSERT INTO tblAuthorization (AuthEmail, AuthDesc) VALUES ('theturbomonkey@gmail.com', 'The Turbo Monkey');
INSERT INTO tblAuthorization (AuthEmail, AuthDesc) VALUES ('*@*', 'Anonymous');

INSERT INTO tblAppPassphrase (Passphrase) VALUES ('Believe');