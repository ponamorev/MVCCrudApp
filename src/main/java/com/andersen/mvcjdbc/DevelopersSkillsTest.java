package com.andersen.mvcjdbc;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

class DevelopersSkillsTest {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/developers_skills" +
            "?autoReconnect=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "2130";
    private static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) {
        int action;
        boolean repeat = true;

        System.out.println("Welcome to the app! You can work with database," +
                " which contains developers and their skills");
        System.out.println("You can add, update or delete developers or skills.");


        while (repeat) {
            try {
                Class.forName(JDBC_DRIVER);
                Connection connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                DevelopersDAO dao = new DevelopersDAO(statement);
                DevelopersView devView = new DevelopersView();
                SkillsDAO sao = new SkillsDAO();
                SkillsView skView = new SkillsView();

                while (true) {
                    System.out.println("\nAll available actions:\n\t1. Create developer\n\t2. " +
                            "Update developer\n\t3. Delete developer\n\t4. Read developers\n\t" +
                            "5. Print last developer which added/updated/deleted\n\t6. Create " +
                            "skill\n\t7. Update skill\n\t8. Delete skill\n\t9. Read" +
                            " skills\n\t0. Exit from app");
                    System.out.print("\nChoose action: ");
                    try {
                        action = reader.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("You didn't write a number! Please, try again..");
                        continue;
                    }
                    reader.nextLine();

                    switch (action) {
                        case 1:
                            dao.createDeveloper(connection, statement);
                            break;
                        case 2:
                            dao.updateDeveloper(connection, statement);
                            break;
                        case 3:
                            dao.deleteDeveloper(connection, statement);
                            break;
                        case 4:
                            devView.readDevelopers(connection);
                            break;
                        case 5:
                            dao.printLastDeveloper();
                            break;
                        case 6:
                            sao.createSkill(connection, statement);
                            break;
                        case 7:
                            sao.updateSkill(connection, statement);
                            break;
                        case 8:
                            sao.deleteSkill(statement);
                            break;
                        case 9:
                            skView.readSkills(connection);
                            break;
                        case 0:
                            System.out.println("Thank for using! Good bye!");
                            return;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("You didn't write a number! Return to start app..");
            } catch (ClassNotFoundException e) {
                System.out.println("There is a problem with JDBC driver..\n" +
                        "Please, check the driver on your PC.");
                repeat = false;
            } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e) {
                System.out.println("There is a problem with query. Try again. If a problem is " +
                        "repeat you must inform the developer about it.");
            } catch (SQLException e) {
                if (e.getMessage().equals("No suitable driver found for")) {
                    System.out.println("There is a problem with connection. " +
                            "Check the setting of the app..");
                    repeat = false;
                } else System.out.println("Unknown SQL exception. Please, try again. " +
                        "Write the developer if the situation will repeat.");
            }
        }
    }
}
