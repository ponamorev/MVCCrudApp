package com.andersen.mvcjdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

class SkillsDAO {
    private ResultSet result;
    private Scanner reader = new Scanner(System.in);
    private String query;

    // Select ID of skill and check it in database
    private int checkSkill(Statement statement)
            throws SQLException {
        String select;
        int ID, amount = 0;

        while (true) {
            System.out.print("Select ID of the skill: ");

            try {
                ID = reader.nextInt();
            } catch (InputMismatchException e) {
                reader.nextLine();
                System.out.println("You didn't write a number!");
                System.out.println("Would you like to return in main menu? (Y/N)");
                select = reader.nextLine();
                if (select.equals("y") || select.equals("Y"))
                    return 0;
                continue;
            }
            reader.nextLine();

            query = "SELECT COUNT(id) FROM skills WHERE id = " + ID;
            result = statement.executeQuery(query);
            while (result.next())
                amount = result.getInt(1);

            if (amount == 1)
                return ID;
            else {
                System.out.println("You wrote nonexistent developer!");
                System.out.println("Would you like to try again? (Y/N)");
                select = reader.nextLine();
                if (select.equals("y") || select.equals("Y"))
                    continue;
            }

            return 0;
        }
    }

    // Check skill
    int checkSkill(String ID_skill, Statement statement)
            throws SQLException {
        int ID, count = 0;

        if (Common.isNumber(ID_skill)) {
            try {
                ID = Integer.parseInt(ID_skill);
            } catch (NumberFormatException e) {
                System.out.println("You didn't write a number!");
                return 0;
            }
            query = "SELECT COUNT(id) FROM skills WHERE id = " + ID;
            result = statement.executeQuery(query);
            while (result.next())
                count = result.getInt(1);

            if (count > 0)
                return ID;
        }
        else {
            query = "SELECT id FROM skills WHERE specialty = '" + ID_skill + "'";
            result = statement.executeQuery(query);
            while (result.next())
                count = result.getInt("id");

            if (count != 0) {
                ID = count;
                return ID;
            } else System.out.println("You wrote non-existent skill..");
        }
        return 0;
    }


    // Creating skill
    void createSkill(Connection connection, Statement statement)
            throws SQLException, NumberFormatException {
        String ID, specialty = "-";
        int id, amount = 0, row;
        boolean repeat = true, checkName;

        while (repeat) {
            id = -1;
            checkName = true;
            // Enter and check ID
            while (id == -1) {
                System.out.print("Enter ID of new skill (press 'enter' for skip): ");
                ID = reader.nextLine();
                if (ID.equals(""))
                    id = 0;
                else {
                    if (Common.isNumber(ID))
                        id = Integer.parseInt(ID);
                    else System.out.println("You didn't write a number! Try again..");
                }
            }

            if (id != 0) {
                query = "SELECT COUNT(id) FROM skills WHERE id = " + id;
                result = statement.executeQuery(query);
                while (result.next())
                    amount = result.getInt(1);

                if (amount > 0) {
                    System.out.println("This ID already exists!");
                    query = "SELECT * FROM skills";
                    result = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY).executeQuery(query);
                    result.last();
                    row = result.getRow();
                    id = 0; amount = 0;
                    for (int i = 1; i <= row; i++) {
                        result = statement.executeQuery("SELECT COUNT(id) FROM skills WHERE id = " + i);
                        while (result.next())
                            amount = result.getInt(1);
                        if (amount == 0) {
                            id = i;
                            break;
                        }
                    }

                    if (id == 0)
                        id = row + 1;
                    System.out.println("New ID is - " + id);
                }
            }

            amount = 0;
            // Enter and check name
            while (checkName) {
                System.out.print("Enter name of new skill: ");
                specialty = reader.nextLine();

                query = "SELECT COUNT(specialty) FROM skills WHERE specialty = '" + specialty + "'";
                result = statement.executeQuery(query);
                while (result.next())
                    amount = result.getInt(1);

                if (amount > 0) System.out.println("This skill already exists! Try again..");
                else checkName = false;
            }

            // Executing query
            if (id == 0)
                query = "INSERT INTO skills VALUES (NULL, '" + specialty + "')";
            else query = "INSERT INTO skills VALUES (" + id + ", '" + specialty + "')";

            System.out.println("Add " + statement.executeUpdate(query) + " row(-s) in 'skills'..");

            System.out.println("Would you like to add another skill? (Y/N)");
            repeat = Common.repeatCycle();
        }
    }


    // Updating skill
    void updateSkill(Connection connection, Statement statement) throws SQLException {
        String name, condition;
        int ID;
        boolean repeat = true;

        while (repeat) {

            query = "SELECT * FROM skills";
            Common.printTable(query, new String[]{"id", "specialty"}, connection);
            ID = checkSkill(statement);

            System.out.print("Enter a new name of the skill: ");
            name = reader.nextLine();

            condition = "specialty = '" + name + "' WHERE id = " + ID;

            query = "UPDATE skills SET " + condition;
            System.out.println("Change " + statement.executeUpdate(query) + " row in 'skills'..");

            System.out.println("Would you like to change another skill? (Y/N)");
            repeat = Common.repeatCycle();
        }
    }


    // Deleting skill
    void deleteSkill(Statement statement) throws SQLException {
        int ID;
        boolean repeat = true;

        while (repeat) {

            ID = checkSkill(statement);

            query = "DELETE FROM developers_skills WHERE id_skill = " + ID;
            System.out.println("Delete " + statement.executeUpdate(query) + " row(-s) in 'developers_skills'..");

            query = "DELETE FROM skills WHERE id = " + ID;
            System.out.println("Delete " + statement.executeUpdate(query) + " row in 'skills'..");

            System.out.println("Would you like to delete another skill? (Y/N)");
            repeat = Common.repeatCycle();
        }
    }
}
