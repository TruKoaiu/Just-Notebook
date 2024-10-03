package TruKoaiu.classes;

import java.util.List;

public class PlansCategory {
    private String category;
    private int order;
    private String goal;

    public PlansCategory(String category, int order, String goal) {
        this.category = category;
        this.order = order;
        this.goal = goal;
    }

    public static NoteCategory getAllGoalNoteCategory(List<NoteCategory> noteCategories) {
        for (NoteCategory noteCategory : noteCategories) {
            if (noteCategory.getGoal() != null && noteCategory.getGoal().equals("all")) {
                return noteCategory;
            }
        }
        return null;
    }

    public static NoteCategory getOtherGoalNoteCategory(List<NoteCategory> noteCategories) {
        for (NoteCategory noteCategory : noteCategories) {
            if (noteCategory.getGoal() != null && noteCategory.getGoal().equals("other")) {
                return noteCategory;
            }
        }
        return null;
    }

    public String getCategory() {
        return category;
    }

    public int getOrder() {
        return order;
    }

    public String getGoal() {
        return goal;
    }

    public void setCategory(String newOrder) {
        category = newOrder;
    }

    public void setOrder(int newOrder) {
        order = newOrder;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
