package TruKoaiu;

//import NoteApp.classes.Note;
//import NoteApp.classes.NoteCategory;
//import NoteApp.classes.Plan;

import TruKoaiu.classes.Note;
import TruKoaiu.classes.NoteCategory;
import TruKoaiu.classes.Plan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class SharedController {

    public static Note findNoteByTitle(List<Note> notes, String title) {
        for (Note note : notes) {
            if (note.getTitle().equals(title)){
                return note;
            }
        }
        return null;
    }


    public static NoteCategory findNoteCategoryByTitle(List<NoteCategory> noteCategories, String title) {
        for (NoteCategory noteCategory : noteCategories) {
            if (noteCategory.getCategory().equals(title)){
                return noteCategory;
            }
        }
        return null;
    }

    public static String[] getNotesTitles(List<Note> notes) {
        String[] noteTitles = new String[notes.size()];

        for (int i = 0; i < notes.size(); i ++) {
            noteTitles[i] = notes.get(i).getTitle();
        }

        return noteTitles;
    }

    public static List<Note> reloadNotes() {
        List<Note> notes = new ArrayList<>();

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");
            Statement statement = conn.createStatement();

            String showTable = "SELECT * FROM notes ORDER BY DATETIME(time) DESC";
            ResultSet getEverything = statement.executeQuery(showTable);

            while (getEverything.next()) {
                String title = getEverything.getString("title");
                String note = getEverything.getString("note");
                String date = getEverything.getString("time");
                String category = getEverything.getString("category");

                notes.add(new Note(title, note, date, category));
            }

            statement.close();
            conn.close();

            return notes;
        } catch (SQLException event) {
            System.out.println("The connection failed");
            event.printStackTrace();
        }
        return null;
    }

    private static List<NoteCategory> sortNoteCategoryByOrder(List<NoteCategory> categories) {
        for (int i = 1; i < categories.size(); i++) {
            int j = i;
            NoteCategory targetNoteCategory = categories.get(j);
            while (j > 0 && categories.get(j-1).getOrder() > targetNoteCategory.getOrder()) {
                categories.set(j, categories.get(j-1));
                j--;
            }
            categories.set(j, targetNoteCategory);
        }

        return categories;
    }

    public static List<NoteCategory> reloadNoteCategories() {
        List<NoteCategory> categories = new ArrayList<>();

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");
            Statement statement = conn.createStatement();

            String showTable = "SELECT * FROM noteCategories";
            ResultSet getEverything = statement.executeQuery(showTable);

            while (getEverything.next()) {
                String category = getEverything.getString("category");
                int order = getEverything.getInt("order");
                String goal = getEverything.getString("goal");

                categories.add(new NoteCategory(category, order, goal));
            }

            sortNoteCategoryByOrder(categories);

            statement.close();
            conn.close();

            return categories;
        } catch (SQLException event) {
            System.out.println("The connection failed");
            event.printStackTrace();
        }
        return null;
    }

    public static String[] getNoteCategoryNames(List<NoteCategory> noteCategories) {
        String[] noteCategoryNames = new String[noteCategories.size()];

        for (int i = 0; i < noteCategories.size(); i++) {
            noteCategoryNames[i] = noteCategories.get(i).getCategory();
        }

        return noteCategoryNames;
    }

    public static List<Plan> reloadPlans() {
        List<Plan> plans = new ArrayList<>();

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");
            Statement statement = conn.createStatement();

            String showTable = "SELECT rowid, * FROM plans ORDER BY DATETIME(targetDate) DESC";
            ResultSet getEverything = statement.executeQuery(showTable);

            while (getEverything.next()) {
                String toDo = getEverything.getString("toDo");
                Boolean isDone = getEverything.getBoolean("isDone");
                String date = getEverything.getString("time");
                String targetDate = getEverything.getString("targetDate");
                String targetHour = getEverything.getString("targetHour");
                int rowId = getEverything.getInt("rowid");

                plans.add(new Plan(toDo, targetDate, targetHour, isDone, date, rowId));
            }

            statement.close();
            conn.close();

            return plans;
        } catch (SQLException event) {
            System.out.println("The connection failed");
            event.printStackTrace();
        }
        return null;
    }

    public static Plan findPlanByRowId(List<Plan> plans, int rowId) {
        for (Plan plan : plans) {
            if (plan.getRowId() == rowId) {
                return plan;
            }
        }
        return null;
    }
}
