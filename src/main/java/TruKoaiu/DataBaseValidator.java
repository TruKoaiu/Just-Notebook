package TruKoaiu;

import java.io.File;
import java.sql.*;

public class DataBaseValidator {

    private static final String DB_URL = "jdbc:sqlite:notes.db";

    public static void check() {
        if (!checkDatabaseExists()) {
            System.out.println("Database file not found. Creating new database with tables.");
            createTables();
        } else {
            System.out.println("Database found. Checking for tables.");
            if (!checkTablesExist()) {
                System.out.println("Tables not found. Creating tables.");
                createTables();
            } else {
                System.out.println("All tables are present.");
            }
        }
    }

    // Check if the database file exists
    private static boolean checkDatabaseExists() {
        File dbFile = new File("notes.db");
        return dbFile.exists();
    }

    // Check if all required tables exist in the database
    private static boolean checkTablesExist() {
        boolean tablesExist = false;
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                // Check existence of each table
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rsNoteCategories = metaData.getTables(null, null, "noteCategories", null);
                ResultSet rsNotes = metaData.getTables(null, null, "notes", null);
                ResultSet rsPlans = metaData.getTables(null, null, "plans", null);

                tablesExist = rsNoteCategories.next() && rsNotes.next() && rsPlans.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tablesExist;
    }

    // Create the required tables in the database
    private static void createTables() {
        String createNoteCategoriesTable = "CREATE TABLE IF NOT EXISTS noteCategories (category TEXT, \"order\" INTEGER, goal TEXT)";
        String createNotesTable = "CREATE TABLE IF NOT EXISTS notes (title TEXT, note TEXT, time DATETIME, category TEXT)";
        String createPlansTable = "CREATE TABLE IF NOT EXISTS plans (targetDate DATE, targetHour TIME, toDo TEXT, isDone BOOLEAN, time DATETIME)";

        String createBaseNoteCategory1 = "INSERT INTO noteCategories VALUES " +
                "(?, ?, ?)";

        String createBaseNoteCategory2 = "INSERT INTO noteCategories VALUES " +
                "(?, ?, null)";

        String createBaseNoteCategory3 = "INSERT INTO noteCategories VALUES " +
                "(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // Execute the SQL commands to create tables
            stmt.execute(createNoteCategoriesTable);
            stmt.execute(createNotesTable);
            stmt.execute(createPlansTable);

            PreparedStatement preparedAddStatement1 = conn.prepareStatement(createBaseNoteCategory1);
            preparedAddStatement1.setString(1, "All");
            preparedAddStatement1.setInt(2, 1);
            preparedAddStatement1.setString(3, "all");
            preparedAddStatement1.execute();

            PreparedStatement preparedAddStatement2 = conn.prepareStatement(createBaseNoteCategory2);
            preparedAddStatement2.setString(1, "Dates");
            preparedAddStatement2.setInt(2, 1);
            preparedAddStatement2.execute();

            PreparedStatement preparedAddStatement3 = conn.prepareStatement(createBaseNoteCategory3);
            preparedAddStatement3.setString(1, "Other");
            preparedAddStatement3.setInt(2, 1);
            preparedAddStatement3.setString(3, "other");
            preparedAddStatement3.execute();

            conn.close();
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
