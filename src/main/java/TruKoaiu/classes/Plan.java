package TruKoaiu.classes;

public class Plan {

    private String toDo;
    private String targetDate;
    private String targetHour;
    private boolean isDone;
    private String date;
    private int rowId;

    public Plan(){}

    public Plan(String toDo, String targetDate, String targetHour, boolean isDone, String date, int rowId) {
        this.toDo = toDo;
        this.isDone = isDone;
        this.date = date;
        this.targetDate = targetDate;
        this.targetHour = targetHour;
        this.rowId = rowId;
    }

    public String getToDo(){
        return toDo;
    }

    public Boolean getIsDone(){
        return isDone;
    }

    public String getDate(){
        return date;
    }

    public String getTargetDate(){
        return targetDate;
    }

    public String getTargetHour(){
        return targetHour;
    }

    public int getRowId() {
        return rowId;
    }

    public void setToDo(String newToDo) {
        toDo = newToDo;
    }

    public void setIsDone(Boolean newIsDone) {
        isDone = newIsDone;
    }

    public void setDate(String newDate) {
        date = newDate;
    }

    public void setTargetDate(String newTargetDate) {
        targetDate = newTargetDate;
    }

    public void setTargetHour(String newTargetHour) {
        targetHour = newTargetHour;
    }

    public void setRowId(int newRowId) {
        rowId = newRowId;
    }
}
