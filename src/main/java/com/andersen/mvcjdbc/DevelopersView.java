package com.andersen.mvcjdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class DevelopersView {
    private ResultSet result;
    private String query;

    Developers findDeveloper(int ID, Statement statement)
            throws SQLException {
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
}
