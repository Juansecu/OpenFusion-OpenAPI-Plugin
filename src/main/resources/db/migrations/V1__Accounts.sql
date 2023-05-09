ALTER TABLE Accounts
ADD COLUMN Email TEXT COLLATE NOCASE DEFAULT '' NOT NULL;



ALTER TABLE Accounts
ADD COLUMN Verified INTEGER DEFAULT 0 NOT NULL;



CREATE TRIGGER IF NOT EXISTS BeforeInsertAccountsEmailUnique
BEFORE INSERT ON Accounts
FOR EACH ROW
WHEN (NEW.Email != '')
BEGIN
    SELECT RAISE(ABORT, 'Email must be unique') WHERE EXISTS (
        SELECT 1 FROM Accounts WHERE Email = NEW.Email
    );
END;



CREATE TRIGGER IF NOT EXISTS BeforeUpdateAccountsEmailUnique
BEFORE UPDATE ON Accounts
FOR EACH ROW
WHEN (NEW.Email != '')
BEGIN
    SELECT RAISE(ABORT, 'Email must be unique') WHERE EXISTS (
        SELECT 1 FROM Accounts WHERE Email = NEW.Email AND AccountID != NEW.AccountID
    );
END;



CREATE TRIGGER IF NOT EXISTS OnUpdateAccountsEmail
AFTER UPDATE ON Accounts
FOR EACH ROW
WHEN (NEW.Email != OLD.Email AND NEW.Verified = 1)
BEGIN
    UPDATE Accounts SET Verified = 0 WHERE AccountID = NEW.AccountID;
END;
