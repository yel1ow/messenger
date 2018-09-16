package yel1ow.messenger.db;

import java.sql.*;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost;databaseName=skype2;integratedSecurity=true;";
    private static Connection connection;

    public static boolean connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String executeFunctionReturnsInt(String sqlQuery) {
        try {
            CallableStatement callableStatement = connection.prepareCall(sqlQuery);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.execute();
            int id = callableStatement.getInt(1);
            return "0" + Integer.toString(id);
        } catch (SQLException e) {
            e.getMessage();
        }
        return "";
    }

    public static String checkLoginAndPassword(String login, String password) {
        String sqlQuery = "EXEC authorisation N'" + login + "', N'" + password + "'";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);
            statement.close();
        } catch (SQLException e) {
            return e.getMessage();
        }
        return "success";
    }

    public static String registerNewUser(String name, String surname, String login, String password) {
        String sqlQuery = "EXEC registration N'" + name + "', N'" + surname + "', N'" + login + "', N'" + password + "'";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);
            statement.close();
        } catch (SQLException e) {
            return e.getMessage();
        }
        return "success";
    }

    public static String getNameById(int id) {
        String sqlQuery = "EXEC getLoginById " + Integer.toString(id);
        try {
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);
            statement.close();
        } catch (SQLException e) {
            return e.getMessage();
        }
        return "success";
    }

    public static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        if (!DBConnection.connect()) {
//            throw new RuntimeException("Connection to DB exception");
//        }
//
//        System.out.println(registerNewUser("vasya","petrov","login","password"));
//
//        System.out.println(checkLoginAndPassword("login", "password"));
//
//        try {
//            connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
////        String sql = "{? = call autorisation(login, password)}";
////        try {
////            String login = "login";
////            String password = "password";
////            CallableStatement callableStatement = connection.prepareCall(sql);
////            callableStatement.registerOutParameter(1, Types.INTEGER);
////            callableStatement.execute();
////            Integer id = callableStatement.getInt(1);
////            System.out.println(id);
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
//
////        try (Statement statement = connection.createStatement();
////             ResultSet resultSet = statement.executeQuery(sql)) {
////            System.out.println("Top 20 categories:");
////            while (resultSet.next()) {
////                System.out.println(resultSet.getString(1) + " "
////                        + resultSet.getString(2) + " "
////                        + resultSet.getString(3) + " "
////                        + resultSet.getString(4));
////            }
////            //connection.close();
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
//    }
}