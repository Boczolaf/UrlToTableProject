package database;

import exceptions.ConnectionException;
import exceptions.DatabaseCheckTableException;
import exceptions.DatabaseCreateTableException;
import table.Table;

import java.sql.*;
import java.util.Objects;

public class MySqlConnector {

    private Connection connection;

    public Statement getStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void openConnection(String url, String user, String password) throws ConnectionException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    url, user, password);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("Error occurred while trying to open connection: " + e.getMessage());
            throw new ConnectionException(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found:" + e.getMessage());
            throw new ConnectionException(e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (!Objects.isNull(connection)) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while trying to close connection: " + e.getMessage());
        }
    }

    public boolean checkIfTableExists(String tableName) throws DatabaseCheckTableException {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            return tables.next();
        } catch (SQLException e) {
            System.out.println("Error occurred while checking table: " + e.getMessage());
            throw new DatabaseCheckTableException(e.getMessage());
        }
    }

    public void createTable(Table table) throws DatabaseCreateTableException {
        try {
            Statement statement = getStatement();
            String[] columnNames = table.getColumnNames();
            String[] dataTypes = table.getDataTypes();
            String tableName = table.getTableName();
            String tableCreationQuery = createTableQuery(tableName, columnNames, dataTypes);
            statement.executeUpdate(tableCreationQuery);
            String dataInsert;
            int i = 0;
            for (String[] row : table.getDataList()) {
                dataInsert = createDataInsertQuery(row, tableName, columnNames);
                statement.addBatch(dataInsert);
                if (i % 50 == 0) {
                    statement.executeBatch();
                    i = 0;
                }
                i++;
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Error occurred while executing sql statement: " + e.getMessage());
            System.out.println("Table " + table.getTableName() + " state is being rolled back");
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Table " + table.getTableName() + " state cannot be rolled back: " + ex.getMessage());
            }
            throw new DatabaseCreateTableException(e.getMessage());
        }

    }

    public void dropAndCreateTable(Table table) throws DatabaseCreateTableException {
        try {
            Statement statement = getStatement();
            statement.executeUpdate("DROP TABLE " + table.getTableName());
            String[] columnNames = table.getColumnNames();
            String[] dataTypes = table.getDataTypes();
            String tableName = table.getTableName();
            String tableCreationQuery = createTableQuery(tableName, columnNames, dataTypes);
            statement.executeUpdate(tableCreationQuery);
            String dataInsert;
            int i = 0;
            for (String[] row : table.getDataList()) {
                dataInsert = createDataInsertQuery(row, tableName, columnNames);
                statement.addBatch(dataInsert);
                if (i % 50 == 0) {
                    statement.executeBatch();
                    i = 0;
                }
                i++;
            }
            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Error occurred while executing sql statement: " + e.getMessage());
            System.out.println("Table " + table.getTableName() + " state is being rolled back");
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println("Table " + table.getTableName() + " state cannot be rolled back: " + ex.getMessage());
            }
            throw new DatabaseCreateTableException(e.getMessage());
        }

    }

    private String createDataInsertQuery(String[] row, String tableName, String[] columnNames) {
        StringBuilder dataInsert = new StringBuilder();
        StringBuilder values = new StringBuilder("VALUES (");
        dataInsert.append("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < columnNames.length; i++) {
            dataInsert.append(columnNames[i]);
            values.append("'");
            values.append(row[i]);
            values.append("'");
            if (i != columnNames.length - 1) {
                dataInsert.append(",");
                values.append(",");
            }
        }
        dataInsert.append(") ").append(values).append(")");
        return dataInsert.toString();
    }

    public String createTableQuery(String tableName, String[] columnNames, String[] dataTypes) {
        StringBuilder tableCreation = new StringBuilder("CREATE TABLE " + tableName + " (");

        for (int i = 0; i < columnNames.length; i++) {
            tableCreation.append(columnNames[i]).append(" ").append(dataTypes[i]);
            if (i != columnNames.length - 1) {
                tableCreation.append(", ");
            }
        }
        tableCreation.append(" )");
        return tableCreation.toString();
    }

}
