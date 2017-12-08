package com.andersen.mvcjdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

class Common {

    // Check number or string
    static boolean isNumber(String value) {
        if (!value.equals("")) {
            char[] charsValue = value.toCharArray();
            for (char ch : charsValue)
                if (!Character.isDigit(ch))
                    return false;
        }
        return true;
    }


    // Print table
    static void printTable(String query, String[] columns, Connection connection)
            throws SQLException {
        ResultSet result;
        String fieldResult[][], addString;

        if (!query.equals("")) {
            int count, max_length[], row;

            // Set number of columns
            max_length = new int[columns.length];

            // Record of the name of the columns from the table
            for (int i = 0; i < columns.length; i++) {
                max_length[i] = columns[i].length() + 1;
            }
            System.out.println();


            // Get number last row
            result = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY).executeQuery(query);
            result.last();
            row = result.getRow();
            fieldResult = new String[row][columns.length];


            // Find maximum width to each column
            // And fill the arrays
            for (int a = 0; a < 2; a++) {
                result.beforeFirst();
                row = 0;
                while (result.next()) {
                    count = 0;
                    while (count++ < columns.length) {
                        fieldResult[row][count - 1] = result.getString(count);
                        if (fieldResult[row][count - 1] != null) {
                            if (max_length[count - 1] <= fieldResult[row][count - 1].length())
                                max_length[count - 1] = fieldResult[row][count - 1].length();
                            else {
                                addString = "";
                                for (int i = 0; i < max_length[count - 1] - fieldResult[row][count - 1].length(); i++)
                                    addString += " ";
                                fieldResult[row][count - 1] += addString;
                            }
                        } else {
                            char[] nullChar = new char[max_length[count - 1]];
                            int i = 0;
                            while (i < nullChar.length)
                                nullChar[i++] = ' ';
                            fieldResult[row][count - 1] = new String(nullChar);
                        }
                    }
                    row++;
                }
            }


            // Align column names
            count = 0;
            while (count < columns.length)
                if (columns[count].length() < max_length[count]) {
                    addString = "";
                    for (int i = 0; i < max_length[count] - columns[count].length(); i++)
                        addString += " ";
                    columns[count++] += addString;
                }


            // Output to the console
            count = 0;
            System.out.print("| ");
            while (count < columns.length) {
                System.out.print(columns[count]);
                if (count + 1 < columns.length)
                    System.out.print(" | ");
                count++;
            }

            count = 0;
            int len = 2;
            while (count < columns.length) {
                len += max_length[count] + 2;
                if (count++ + 1 < columns.length)
                    len++;
            }
            count = 0;
            addString = "";
            while (count++ < len)
                addString += "-";
            System.out.println("\n" + addString);

            for (row = 0; row < fieldResult.length; row++) {
                System.out.print("| ");
                for (count = 0; count < fieldResult[row].length; count++) {
                    System.out.print(fieldResult[row][count]);
                    if (count + 1 <= fieldResult[row].length)
                        System.out.print(" | ");
                }
                System.out.println();
            }

            System.out.println(addString);
        }
        else System.out.println("The query is empty.");
    }
}
