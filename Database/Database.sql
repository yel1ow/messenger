DROP DATABASE IF EXISTS skype2
GO
CREATE DATABASE skype2
GO
USE [skype2]
GO
DROP TABLE IF EXISTS contents
GO

CREATE TABLE contents
(
	Id int PRIMARY KEY NOT NULL IDENTITY
	,content varchar(255) NOT NULL
)
GO

DROP TABLE IF EXISTS mess
GO

CREATE TABLE mess
(
	Id int PRIMARY KEY NOT NULL IDENTITY
	,toId int
	,fromId int
	,contentId int
	,createdAt date
	,state varchar(20)
	,CHECK (state IN ('in process','sended','seen','not sended'))
	,type varchar(20)
	,CHECK (type IN ('text'))
)
GO

DROP TABLE IF EXISTS users
GO

CREATE TABLE users
(
	Id int PRIMARY KEY NOT NULL IDENTITY
	,name varchar(30) NOT NULL
	,surname varchar(30)
	,sex varchar 
	,CHECK (sex IN ('male','female'))	
	,bithday date 
	,login varchar(30) NOT NULL
	,password varchar(30) NOT NULL
	,lastOnline date
)
GO

DROP TABLE IF EXISTS friendList
GO

CREATE TABLE friendList
(
	friend1Id int NOT NULL
	,friend2Id int NOT NULL
)
GO

DROP FUNCTION
IF EXISTS gimmeMessages
GO

CREATE FUNCTION gimmeMessages(@id1 int, @id2 int) 
RETURNS @MESSAGES TABLE
		(
			user1Id int
			,user2Id int
			,contentsId int
			,messId int
		) 
AS
BEGIN
	INSERT @MESSAGES
		SELECT mess.fromId, mess.toId, contents.Id, mess.Id
		FROM users, contents, mess
		WHERE (
				(
					(mess.fromId = @id1)
					and
					(mess.toId = @id2)
				)
				or
				(
					(mess.fromId = @id1)
					and
					(mess.toId = @id2)
				)
			  )
			  and
			  (mess.contentId = contents.Id)
	RETURN
END
GO

DROP PROCEDURE
IF EXISTS registration
GO

CREATE PROCEDURE registration(@name varchar(30) 
							  ,@surname varchar(30)
							  ,@login varchar(30)
							  ,@password varchar(30))
AS
BEGIN
	IF (SELECT COUNT(*) FROM users WHERE login = @login) > 0
	THROW 50005, N'Login is already taken', 1;

	IF (@name IS NULL)or(@name = N'')
	THROW 50001, N'Enter name', 1;

	IF (@surname IS NULL)or(@surname = N'')
	THROW 50002, N'Enter surname', 1;

	IF  (@login IS NULL)or(@login = N'')
	THROW 50003, N'Enter login', 1;

	IF (@password IS NULL)or(@password = N'')
	THROW 50004, N'Enter password', 1;

	INSERT [dbo].[users] ([name], [surname], [login], [password]) VALUES (@name, @surname, @login, @password) 
	DECLARE @idd varchar(10) = CONVERT(varchar(10), @@IDENTITY);
	THROW 60001, @idd, 1;
END
GO

DROP PROCEDURE 
IF EXISTS authorisation
GO

CREATE PROCEDURE authorisation(@login varchar(30), @password varchar(30))
AS
BEGIN
	IF EXISTS (SELECT * FROM users WHERE users.login = @login)
	BEGIN
		IF NOT EXISTS (SELECT * FROM users WHERE (users.password = @password)AND(users.login = @login))
		THROW 50021, N'Not correct password', 1;
		ELSE BEGIN
				DECLARE @idd int = (SELECT users.Id  FROM users WHERE users.login = @login);
				DECLARE @iddstr varchar(10) = CONVERT(varchar(10), @idd);
				THROW 60000, @iddstr, 1;
			 END;
	END;
	ELSE THROW 50022, N'Not correct login', 1;
END
GO

DROP PROCEDURE
IF EXISTS addMessage
GO

CREATE PROCEDURE addMessage(@fromId int
							  ,@toId int
							  ,@createdAt date
							  ,@type varchar(20) 
							  ,@content varchar(255))
AS
BEGIN
	If (@content IS NULL) OR (@content = N'')
	THROW 50011, N'Enter your message', 1;
	INSERT [dbo].[contents] ([content]) VALUES(@content)
	INSERT [dbo].[mess] ([fromId], [toId], [contentId], [createdAt], [type], [state]) VALUES (@fromId, @toId, @@IDENTITY, @createdAt, @type, N'in process') 
END
GO

DROP PROCEDURE 
IF EXISTS getLoginById
GO

CREATE PROCEDURE getLoginById(@id int)
AS
BEGIN
	DECLARE @name varchar(30) = (SELECT users.name  FROM users WHERE users.Id = @id);
	DECLARE @login varchar(30) = (SELECT users.login  FROM users WHERE users.Id = @id);
	DECLARE @mess varchar(62) = @name + '(' + @login + ')';
	if (@name IS NULL)
	THROW 50025, N'incorrect Id', 1
	ELSE THROW 60025, @mess, 1
END
GO