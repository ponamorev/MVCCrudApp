package com.andersen.mvcjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

class SkillsDAO {
    private ResultSet result;
    private Scanner reader = new Scanner(System.in);
    private String select, query;

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
        return 0;
       /* int ID = 0, amount = 0;

        while (true) {
            System.out.print("Select ID of the developer to update: ");

            try {
                ID = reader.nextInt();
                reader.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("You didn't write a number!");
                System.out.println("Would you like to return in main menu? (Y/N)");
                select = reader.nextLine();
                if (select.equals("y") || select.equals("Y"))
                    return 0;
                continue;
            }

            query = "SELECT COUNT(id) FROM developer WHERE id = " + ID;
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
        }*/
    }
}
