BEGIN TRY
	EXEC registration N'Kostya', N'Kurd', N'N0fail', N'322'
	
	EXEC registration N'Vova', N'Lavr', N'nelox', N'228'

	EXEC addMessage 1, 2, N'2018-08-31', N'text', N''

	EXEC authorisation N'nelox', N'228'
END TRY
BEGIN CATCH
	THROW;
END CATCH;

SELECT *
FROM contents,mess
WHERE (contents.Id = mess.contentId)
GO

SELECT *
FROM users
GO