package com.andersen.mvcjdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

class DevelopersDAO {
    private String query, select;
    private Developers developer;
    private Scanner reader = new Scanner(System.in);
    List<Developers> developersList;
    private Set<Skills> skillsSet;
    private ResultSet result;


    // Select ID of developer and check it in database
    private int checkDeveloper(Statement statement)
            throws SQLException {
        int ID = 0, amount = 0;

        while (true) {
            System.out.print("Select ID of the developer: ");

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
        }
    }



    // Method to check on exit from method
    private boolean repeatCycle() {
        select = reader.nextLine();
        return !(!select.equals("y") & !select.equals("Y"));
    }


    // Constructor of this class, initiate List<Developers>,
    // this collection is filled data from database
    DevelopersDAO(Statement statement) throws SQLException {
        int amountDevs = 0;

        query = "SELECT COUNT(id) FROM developers";
        result = statement.executeQuery(query);
        while (result.next())
            amountDevs = result.getInt(1);

        developersList = new LinkedList<Developers>();

        for (int ID = 0; ID < amountDevs; ID++) {
            skillsSet = new HashSet<Skills>();

            query = "SELECT * FROM developers WHERE id = " + ID;
            result = statement.executeQuery(query);
            while (result.next())
                developer = new Developers(result.getInt("id"),
                        result.getString("name"), result.getInt("salary"), null);

            query = "SELECT * FROM skills WHERE id IN (SELECT id_skill FROM developers_skills " +
                    "WHERE id_developer = " + developer.getID() + ")";
            result = statement.executeQuery(query);
            while (result.next()) {
                Skills skill = new Skills(result.getInt("id"),
                        result.getString("specialty"));
                skillsSet.add(skill);
            }

            developer.setSkills(skillsSet);
            developersList.add(developer);
        }
    }


    // Creating of the developer and adding skills,
    // save changing to database and collection
    void createDeveloper(Connection connection, Statement statement)
            throws SQLException {
        String ID, name = "-", skill;
        int id = -1, ID_skill = 0, salary, amount = 0, countRow;
        boolean checkName = true, addSkill = true, repeat = true;

        while (repeat) {
            // Enter and check ID
            while (id == -1) {
                System.out.print("Enter ID of new developer (press 'enter' for skip): ");
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
                query = "SELECT COUNT(id) FROM developers WHERE id = " + id;
                result = statement.executeQuery(query);
                while (result.next())
                    amount = result.getInt(1);

                if (amount > 0) {
                    System.out.println("This ID already exists!");
                    query = "SELECT * FROM developers";
                    result = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY).executeQuery(query);
                    result.last();
                    countRow = result.getRow();
                    id = 0;
                    for (int i = 0; i < countRow; i++) {
                        result = statement.executeQuery("SELECT COUNT(id) FROM developers WHERE id = " + i);
                        while (result.next())
                            amount = result.getInt(1);
                        if (amount == 0) {
                            id = i;
                            break;
                        }
                    }
                    if (id == 0)
                        id = countRow + 1;
                    System.out.println("New ID is - " + id);
                }
            }


            // Enter and check name
            while (checkName) {
                System.out.print("Enter name of new developer: ");
                name = reader.nextLine();

                query = "SELECT COUNT(name) FROM developers WHERE name = '" + name + "'";
                result = statement.executeQuery(query);
                while (result.next())
                    amount = result.getInt(1);

                if (amount > 0) System.out.println("This name is already exists! Try again..");
                else checkName = false;
            }


            // Set salary
            System.out.println("Do you want to add a salary? (Y/N)");
            select = reader.nextLine();
            if (select.equals("y") || select.equals("Y")) {
                System.out.print("Enter the salary: ");
                salary = reader.nextInt();
                reader.nextLine();
            } else salary = 0;

            // Create object 'developer'
            developer = new Developers(id, name, salary, null);


            // Set skills
            skillsSet = developer.getSkills();
            while (addSkill) {
                query = "SELECT * FROM skills";
                Common.printTable(query, new String[]{"id", "specialty"}, connection);
                System.out.print("Enter the skill for new developer: ");
                skill = reader.nextLine();
                Skills specialty = null;

                if (Common.isNumber(skill))
                    query = "SELECT * FROM skills WHERE id = " + skill;
                else query = "SELECT * FROM skills WHERE specialty = '" + skill + "'";

                result = statement.executeQuery(query);
                while (result.next()) {
                    ID_skill = result.getInt("id");
                    specialty = new Skills(ID_skill, result.getString("specialty"));
                }

                if (specialty != null & ID_skill != 0) {
                    if (skillsSet != null)
                        skillsSet.add(specialty);
                    else {
                        skillsSet = new HashSet<Skills>();
                        skillsSet.add(specialty);
                    }

                    query = "INSERT INTO developers_skills VALUES (" + id + ", " + ID_skill + ")";

                    System.out.println("Do you want to add another skill? (Y/N)");
                    select = reader.nextLine();
                    if (!select.equals("y") & !select.equals("Y"))
                        addSkill = false;
                }
            }

            developer.setSkills(skillsSet);

            // Executing of query
            if (id == 0)
                query = "INSERT INTO developers VALUES (NULL, '" + name + "', " + salary + ")";
            else query = "INSERT INTO developers VALUES (" + id + ", '" + name + "', " + salary + ")";
            System.out.println("Add " + statement.executeUpdate(query) + " row(-s) in the table..");

            developersList.add(developer);

            System.out.println("\nWould you like to add another developer? (Y/N)");
            repeat = repeatCycle();
        }
    }




    // Updating developers, save changing to database and collection
    void updateDeveloper(Connection connection, Statement statement)
            throws SQLException {
        SkillsDAO skillDAO = new SkillsDAO();
        Set<Skills> skillDev;
        DevelopersView view = new DevelopersView();
        StringBuilder builder = new StringBuilder();
        String columns[] = new String[]{"name", "salary"}, ID_skill;
        int checker, ID, idSkill;
        boolean change = true, repeat = true;


        while (repeat) {

            ID = checkDeveloper(statement);

            // Next action will do if id of the developer is not equal nought
            // If id = 0 it will mean that no developers has not this id
            if (ID != 0) {
                query = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = '" +
                        "developers_skills' AND table_name = 'developers'";
                Common.printTable(query, new String[]{"column_name"}, connection);

                developer = view.findDeveloper(ID, statement);

                // Change developer
                while (change) {
                    builder.delete(0, builder.length());
                    checker = 0;
                    System.out.println("Select a column to change (besides 'ID'). For adding skill" +
                            "write 'add skill', for deleting write 'delete skill'");
                    select = reader.nextLine();
                    if (select.equals("id")) {
                        System.out.println("ID can not be changed!");
                        continue;
                    }

                    // Adding skill to developer
                    if (select.equals("add skill")) {
                        // Check skills which the developer has
                        if (developer.getSkills() != null) {
                            System.out.println("The developer has next skills:");
                            for (Skills skill : developer.getSkills())
                                System.out.println(skill.getID() + ". " + skill.getSpecialty());
                        } else
                            System.out.println("The developer has not any skills till..");

                        // Output all skills from database and enter skill to add
                        query = "SELECT * FROM skills";
                        Common.printTable(query, new String[]{"id", "specialty"}, connection);
                        System.out.println("Select skill to add: ");
                        ID_skill = reader.nextLine();
                        idSkill = skillDAO.checkSkill(ID_skill, statement);

                        // Check entered value
                        // If it is not equal nought, some skill will have this id or name
                        if (idSkill != 0) {
                            if (developer.getSkills() != null) {
                                for (Skills element : developer.getSkills())
                                    if (element.getID() == idSkill)
                                        checker++;
                                if (checker > 0) {
                                    System.out.println("You try to add skill which the developer is already had.");
                                    break;
                                }

                                // Add changing to database..
                                query = "INSERT INTO developers_skills VALUE (" + ID + ", " + idSkill + ")";
                                System.out.println("Add " + statement.executeUpdate(query) + " skill to chosen " +
                                        "developer..");

                                // Add changing to collection..
                                query = "SELECT * FROM skills WHERE id = " + idSkill;
                                result = statement.executeQuery(query);

                                if (developer.getSkills() != null)
                                    skillDev = developer.getSkills();
                                else
                                    skillDev = new HashSet<Skills>();

                                while (result.next())
                                    skillDev.add(new Skills(result.getInt("id"),
                                            result.getString("specialty")));
                                developer.setSkills(skillDev);
                            }
                        }


                    } else if (select.equals("delete skill")) {
                        Skills delSkill = null;
                        checker = 0;

                        // Check skills which the developer has..
                        if (developer.getSkills() != null) {
                            System.out.println("The developer has next skills:");
                            for (Skills skill : developer.getSkills())
                                System.out.println(skill.getID() + ". " + skill.getSpecialty());
                        } else {
                            System.out.println("The developer has not any skills till..");
                            break;
                        }

                        System.out.print("Enter the skill to delete: ");
                        select = reader.nextLine();
                        // Divide: id or name
                        // ID
                        if (Common.isNumber(select)) {
                            for (Skills skill : developer.getSkills())
                                if (skill.getID() == Integer.parseInt(select))
                                    checker++;

                            // Check database to exist this skill
                            if (checker > 0) {
                                query = "SELECT * FROM skills WHERE id = " + select;
                                result = statement.executeQuery(query);
                                while (result.next())
                                    delSkill = new Skills(result.getInt("id"),
                                            result.getString("specialty"));

                            } else {
                                System.out.println("The developer has not this skill..");
                                break;
                            }
                        }
                        // Name
                        else {
                            checker = 0;
                            for (Skills skill : developer.getSkills())
                                if (skill.getSpecialty().equals(select))
                                    checker++;

                            // Check database to exist this skill
                            if (checker > 0) {
                                query = "SELECT * FROM skills WHERE specialty = '" + select + "'";
                                result = statement.executeQuery(query);
                                while (result.next())
                                    delSkill = new Skills(result.getInt("id"),
                                            result.getString("specialty"));

                            } else {
                                System.out.println("The developer has not this skill..");
                                break;
                            }
                        }

                        query = "DELETE FROM developers_skills WHERE id_skill = " + delSkill.getID() +
                                " AND id_developer = " + ID;

                    } else {
                        checker = 0;
                        for (String column : columns)
                            if (column.equals(select))
                                checker = 1;

                        if (checker == 1) {
                            builder.append(select);
                            System.out.print("Enter a new value: ");
                            if (select.equals("name")) {
                                select = reader.nextLine();
                                builder.append(" = '").append(select).append("'");
                            } else {
                                select = reader.nextLine();
                                builder.append(" = ").append(select);
                            }

                            query = "UPDATE developers SET " + builder.toString() + " WHERE id = " + ID;
                            System.out.println("Update " + statement.executeUpdate(query) + " row(-s) in the table..");

                            query = "SELECT * FROM developers WHERE id = " + ID;
                            result = statement.executeQuery(query);
                            while (result.next())
                                developer = new Developers(ID, result.getString("name"),
                                        result.getInt("salary"), null);

                        } else {
                            System.out.println("This columns is not exist!");
                            continue;
                        }

                        System.out.println("Would you like to update another column? (Y/N)");
                        select = reader.nextLine();
                        if (!select.equals("y") & !select.equals("Y"))
                            change = false;

                    }
                }
            }
            else return;

            System.out.println("\nWould you like to update another developer? (Y/N)");
            repeat = repeatCycle();
        }
    }



    // Deleting developers, save changing to database and collection
    void deleteDeveloper(Connection connection, Statement statement)
            throws SQLException {

        ResultSet result;
        int ID;
        boolean repeat = true;

        while (repeat) {

            ID = checkDeveloper(statement);

            if (ID != 0) {
                query = "SELECT * FROM developers WHERE id = " + ID;
                result = statement.executeQuery(query);
                while (result.next())
                    developer = new Developers(result.getInt("id"),
                            result.getString("name"), result.getInt("salary"), null);

                query = "DELETE FROM developers_skills WHERE id_developer = " + ID;
                System.out.println("Delete " + statement.executeUpdate(query) + " row(-s) in 'developers_skills'..");

                query = "DELETE FROM developers WHERE id = " + ID;
                System.out.println("Delete " + statement.executeUpdate(query) + " row(-s) in 'developers'..");
            }
            else return;

            System.out.println("\nWould you like to delete another developer? (Y/N)");
            repeat = repeatCycle();
        }
    }



    void develete() {}
    // Output last developer!
}
