package com.andersen.mvcjdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

class DevelopersView {
    private Scanner reader = new Scanner(System.in);
    private StringBuilder builder = new StringBuilder();
    private String query;

    Developers findDeveloper(int ID, Statement statement)
            throws SQLException {
        ResultSet result;
        DevelopersDAO devDAO = new DevelopersDAO(statement);
        Developers dev = null;

        query = "SELECT * FROM developers WHERE id = " + ID;
        result = statement.executeQuery(query);
        while (result.next())
            dev = new Developers(result.getInt("id"),
                    result.getString("name"), result.getInt("salary"), null);

        if (dev != null) {
            for (int index = 1; index <= devDAO.developersList.size(); index++)
                if (dev.getID() == devDAO.developersList.get(index).getID()) {
                    dev.setSkills(devDAO.developersList.get(index).getSkills());
                    return dev;
                }
        }

        return null;
    }

    void readDevelopers(Connection connection) throws SQLException {
        String condition, choice;
        boolean flag = false;

        System.out.println("Output all from table?.. (Y/N)");
        choice = reader.nextLine();
        if (choice.equals("y") || choice.equals("Y")) {
            query = "SELECT * FROM developers";
            Common.printTable(query, new String[] {"id", "name", "salary"}, connection);
        }
        else {

            query = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = " +
                    "'developers_skills' AND table_name = 'developers'";
            Common.printTable(query, new String[] {"columns"}, connection);

            System.out.println("Enter one and more values or range of values.\n" +
                    "If you enter string value you will write this value in follow " +
                    "format: string_column = 'some_value'.\nFor example, 'column = value' " +
                    "or 'column1 = value1, column2 > value2' or 'column BETWEEN " +
                    "min_value AND max_value'..");
            condition = reader.nextLine();
            builder.append(condition);

            while (!flag) {

                System.out.println("Would you like to add another value to search? (Y/any key)");
                choice = reader.nextLine();

                if (choice.equals("y") || choice.equals("Y")) {

                    condition = reader.nextLine();

                    System.out.println("It's required condition? (Y/any key)");
                    choice = reader.nextLine();

                    if (choice.equals("y") || choice.equals("Y"))
                        builder.append(" AND ").append(condition);
                    else builder.append(" OR ").append(condition);
                } else flag = true;
            }

            if (builder.toString().equals("") || builder.toString().equals("\n"))
                query = "SELECT * FROM developers";
            else
                query = "SELECT * FROM developers WHERE " + builder.toString();

            Common.printTable(query, new String[] {"id", "name", "salary"}, connection);
        }
    }
}
