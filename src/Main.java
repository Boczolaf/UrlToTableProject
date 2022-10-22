import database.MySqlConnector;
import exceptions.ConnectionException;
import exceptions.DatabaseCheckTableException;
import exceptions.DatabaseCreateTableException;
import exceptions.TableCreationException;
import table.Table;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int simpleControl = -1;
        Scanner myObj = new Scanner(System.in);
        MySqlConnector connector = new MySqlConnector();
        boolean connected = false;
        while (simpleControl != 0) {
            System.out.println("""
                    
                    Options (please input number to proceed):
                    3 - Use default database connection
                    2 - Configure database connection
                    1 - Upload a table from url
                    0 - Quit""");
            try {
                if (myObj.hasNextInt()) {
                    simpleControl = myObj.nextInt();
                    if (simpleControl == 2 || simpleControl == 3) {
                        if (simpleControl == 2) {
                            promptConnection(myObj, connector);
                        } else {
                            String url = "jdbc:mysql://localhost:3306/sys";
                            String username = "root";
                            String password = "root";
                            connector.openConnection(url, username, password);
                        }
                        connected = true;
                        System.out.println("Connected");

                    } else if (simpleControl == 1) {
                        if (connected) {
                            Table table;
                            table = promptUrl(myObj);
                            System.out.println("Successfully mapped table named " + table.getTableName());
                            if (connector.checkIfTableExists(table.getTableName())) {
                                System.out.println("Table already exists in the database, overwriting...");
                                connector.dropAndCreateTable(table);
                            } else {
                                System.out.println("Table doesn't exist in the database, creating...");
                                connector.createTable(table);
                            }
                            System.out.println("Table successfully imported into database");
                        } else {
                            System.out.println("You must connect to the database first");
                        }
                    }
                } else {
                    myObj.next();
                }
            } catch (ConnectionException e) {
                System.out.println("Connection aborted");
                connected = false;
                simpleControl = -1;
            } catch (TableCreationException e) {
                System.out.println("Table mapped incorrectly: " + e.getMessage());
                simpleControl = -1;
            } catch (DatabaseCreateTableException e) {
                System.out.println("Table couldn't be imported: " + e.getMessage());
                connected = false;
                simpleControl = -1;
            } catch (DatabaseCheckTableException e){
                System.out.println("Table couldn't be checked: " + e.getMessage());
                connected = false;
                simpleControl = -1;
            }catch (Exception e){
                System.out.println("Other exception occurred: " + e.getMessage());
                break;
            }
        }
        connector.closeConnection();
    }

    private static Table promptUrl(Scanner myObj) throws TableCreationException {
        System.out.println("Please provide an url to a table:");
        String link = myObj.next();
        return new Table(link);
    }

    private static void promptConnection(Scanner myObj, MySqlConnector connector) throws ConnectionException {
        System.out.println("Please provide an url:");
        String url = myObj.next();
        System.out.println("Please provide username:");
        String user = myObj.next();
        System.out.println("Please provide password:");
        String password = myObj.next();
        connector.openConnection(url, user, password);

    }
}
