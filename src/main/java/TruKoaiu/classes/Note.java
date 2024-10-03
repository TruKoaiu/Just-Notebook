package TruKoaiu.classes;

public class Note {

    private String title;
    private String content;
    private String date;
    private String category;

    public Note(){}

    public Note(String title) {
        this.title = title;
        content = "";
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Note(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Note(String title, String content, String date, String category) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.category = category;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public String getDate(){
        return date;
    }

    public String getCategory(){
        return category;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setContent(String newContent) {
        content = newContent;
    }

    public void setDate(String newDate) {
        date = newDate;
    }

    public void setCategoty(String newCategory) {
        category = newCategory;
    }
}
