package TruKoaiu.Panels;

import TruKoaiu.SharedController;
import TruKoaiu.SharedNoteController;
import TruKoaiu.classes.Note;
import TruKoaiu.classes.NoteCategory;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
//import NoteApp.classes.Note;
//import NoteApp.classes.NoteCategory;
//import NoteApp.SharedNoteController;
//import NoteApp.SharedController;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AddNotesPanel {

    private VBox mainPanel;
    private HBox sectionControlBar;

    private SharedNoteController mainInstance;

    private List<Note> notes;
    private List<NoteCategory> noteCategories;

    //Needed here for deleting Listener when changing the MainPanel. Yes, I could just setUpListView(),
    //but I use this project to learn as well, so I wanted to use it (i know performance improvement will be in nano secs xD).
    private ChangeListener<String> changeListener;

//    private Label title;
    private TextField titleInput;
    private ChoiceBox categoryPicker;
    private TextField categoryCreatorInput;
    private Button categoryCreatorButton;
    private TextArea noteContent;

    public void showAddPanel(SharedNoteController mainInstance) {
        notes = SharedController.reloadNotes();
        noteCategories = SharedController.reloadNoteCategories();
        this.mainInstance = mainInstance;

        mainPanel = mainInstance.getMainPanel();

        HBox headerContainer = setUpHeader();

        GridPane userInputElements = createGridSetUp();
        setUpGridContentElements(userInputElements);

        noteContent = new TextArea();
        noteContent.setPromptText("Note content");
        noteContent.setWrapText(true);
        VBox.setVgrow(noteContent, Priority.ALWAYS);

        mainInstance.removeBaseItemListViewListener();

        mainPanel.getChildren().addAll(headerContainer, userInputElements, noteContent);
    }

    public void showSectionControlBar(SharedNoteController mainInstance) {
        sectionControlBar = mainInstance.getSectionControlBar();
        sectionControlBar.setSpacing(16);

        //Probably not needed to be here, but I will leave it here for now.
        Button add = new Button("Add");
        add.setOnAction(e -> tryAddingNote());
        Pane spacer = new Pane();
        spacer.setMinSize(10, 1);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Button goBack = new Button("Go back");
        goBack.setOnAction(e -> clearPanels());

        sectionControlBar.getChildren().addAll(add, spacer, goBack);
    }

    private void tryAddingNote() {
        String targetTitle = titleInput.getText();
        if (targetTitle.isEmpty()) {
            System.out.println("The title is empty");
            return;
        }

        for (Note note : notes) {
            if (note.getTitle().equals(targetTitle)) {
                System.out.println("This title is already in use");
                return;
            }
        }

        insertNoteIntoDB();
        notes = SharedController.reloadNotes();
        mainInstance.setNotes(notes);
        clearUserInput();
        mainInstance.reloadItemList();
    }

    private void insertNoteIntoDB() {
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");

            String currentDateSQLCommand = "strftime('%Y-%m-%d %H:%M:%S', datetime('now', 'localtime'))";
            String insertIntoNotes =
                    "INSERT INTO notes VALUES (?, ?, " + currentDateSQLCommand + ", ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertIntoNotes);
            preparedStatement.setString(1, titleInput.getText());
            preparedStatement.setString(2, noteContent.getText());
            preparedStatement.setString(3, categoryPicker.getValue().toString());
            preparedStatement.execute();

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearUserInput() {
        titleInput.setText("");
        String baseCategoryName = NoteCategory.getOtherGoalNoteCategory(noteCategories).getCategory();
        categoryPicker.setValue(baseCategoryName);
        noteContent.setText("");
    }

    private GridPane createGridSetUp() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(8);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();
        RowConstraints row4 = new RowConstraints();

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setMinWidth(75);
        column1.setPrefWidth(75);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setFillWidth(true);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setMinWidth(75);

        grid.getRowConstraints().addAll(row1, row2, row3, row4);
        grid.getColumnConstraints().addAll(column1, column2, column3);

        return grid;
    }

    private void setUpGridContentElements(GridPane grid) {
        setUpTitleSection(grid);

        setUpCategorySection(grid);

        setUpCategoryCreatorSection(grid);
    }

    private void setUpTitleSection(GridPane grid) {
        VBox titleHolder = new VBox();
        GridPane.setVgrow(titleHolder, Priority.ALWAYS);
        titleHolder.setFillWidth(true);

        Label title = new Label("Title:");
        title.getStyleClass().add("boost");
        title.setTextFill(Color.color(1, 1, 1)); // Styling here is temporary

        titleInput = new TextField();
        titleInput.setPromptText("Input new note title...");
        GridPane.setHgrow(titleInput, Priority.ALWAYS);
        VBox.setVgrow(titleInput, Priority.ALWAYS);

        grid.add(title, 0, 0, 1, 1);
        grid.add(titleInput, 1, 0, 1, 1);
    }

    private void setUpCategorySection(GridPane grid) {
        Label categorySectionLabel = new Label("Category");
        categorySectionLabel.getStyleClass().add("boost");
        categorySectionLabel.getStyleClass().add("bold");
        categorySectionLabel.setTextFill(Color.color(1, 1, 1)); // Styling here is temporary

        Label categoryLabel = new Label("Select:");
        categoryLabel.getStyleClass().add("boost");
        categoryLabel.setTextFill(Color.color(1, 1, 1)); // Styling here is temporary

        categoryPicker = new ChoiceBox();
        String otherCategoryName = NoteCategory.getOtherGoalNoteCategory(noteCategories).getCategory();

        reloadCategoryPicker(otherCategoryName);

        categoryPicker.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(categoryPicker, Priority.ALWAYS);

        grid.add(categorySectionLabel, 0, 1, 3, 1);
        grid.add(categoryLabel, 0, 2, 1, 1);
        grid.add(categoryPicker, 1, 2, 1, 1);
    }

    private void setUpCategoryCreatorSection(GridPane grid) {
        Label categoryCreatorLabel = new Label("Or create:");
        categoryCreatorLabel.getStyleClass().add("boost");
        categoryCreatorLabel.setTextFill(Color.color(1, 1, 1)); // Styling here is temporary

        categoryCreatorInput = new TextField();
        categoryCreatorInput.setPromptText("Input your custom category name...");
        HBox.setHgrow(categoryCreatorInput, Priority.ALWAYS);

        categoryCreatorButton = new Button("Confirm");
        categoryCreatorButton.setOnAction(e -> handleCategoryCreation());

        grid.add(categoryCreatorLabel, 0, 3, 1, 1);
        grid.add(categoryCreatorInput, 1, 3, 1, 1);
        grid.add(categoryCreatorButton, 2, 3, 1, 1);
    }

    private void handleCategoryCreation() {
        boolean succeeded = mainInstance.createCategory(categoryCreatorInput);
        if (!succeeded) {return;};
        mainInstance.reloadItemList();
        noteCategories = mainInstance.getNoteCategories();

        String newlyCreatedCategoryName = categoryCreatorInput.getText();
        reloadCategoryPicker(newlyCreatedCategoryName);

        categoryCreatorInput.setText("");
    }

    private void reloadCategoryPicker(String setValue) {
        noteCategories = mainInstance.getNoteCategories();

        categoryPicker.getItems().clear();
        String[] noteCategoryNames = SharedController.getNoteCategoryNames(noteCategories);
        Set<String> noteCategoryNamesSet = new LinkedHashSet<>();
        String baseCategoryName = NoteCategory.getAllGoalNoteCategory(noteCategories).getCategory();
        noteCategoryNamesSet.addAll(List.of(noteCategoryNames));
        noteCategoryNamesSet.remove(baseCategoryName);
        categoryPicker.getItems().addAll(noteCategoryNamesSet);
        categoryPicker.setValue(setValue);
    }

    private HBox setUpHeader() {
        HBox headerContainer = new HBox();

        Label navigationText = new Label("Add note");
        navigationText.getStyleClass().add("big");
        navigationText.getStyleClass().add("bold");
        navigationText.setTextFill(Color.color(1, 1, 1));
        Button close = new Button("X");
        close.setOnAction(e -> clearPanels());

        Pane spacer = new Pane();
        spacer.setMinSize(10, 1);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        headerContainer.getChildren().addAll(navigationText, spacer, close);

        return headerContainer;
    }

    private void clearPanels() {
        sectionControlBar.getChildren().clear();
        mainPanel.getChildren().clear();
        mainInstance.getItemListView().getSelectionModel().clearSelection();
        mainInstance.setUpBaseItemListViewListener();
        mainInstance.setUpFunctionalSectionControlBar();
    }
}
