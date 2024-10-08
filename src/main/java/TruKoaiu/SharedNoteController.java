package TruKoaiu;

import TruKoaiu.Panels.AddNotesPanel;
import TruKoaiu.Panels.CategoryEditerPanel;
import TruKoaiu.Panels.MainNotesPanel;
import TruKoaiu.classes.Note;
import TruKoaiu.classes.NoteCategory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
//import NoteApp.Panels.AddNotesPanel;
//import NoteApp.Panels.CategoryEditerPanel;
//import NoteApp.Panels.MainNotesPanel;
//import NoteApp.classes.Note;
//import NoteApp.classes.NoteCategory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SharedNoteController {
    private String lastScene;
    private String currentScene;
    private String targetListElement;

    private String currentSection;

    private List<Note> notes;
    private List<NoteCategory> noteCategories;

    private GridPane mainContainer;
    private HBox navBar;
    private VBox itemList;
    private VBox mainPanel;
    private HBox sectionControlBar;

    private ListView itemListView;
    private ChoiceBox noteCategoryChoiceBox;

    private ChangeListener<String> baseListener;
    private ChangeListener<String> baseChoiceBoxListener;


    public void setUpMainContainer() {
        currentSection = "Note";
        setUpNavBar();
        setUpItemList();
        setUpSectionControlBar();
        setUpMainPanel();
        setUpFunctionalSectionControlBar();

        mainContainer = setUpGridPane();
    }

    private GridPane setUpGridPane() {
        mainContainer = new GridPane();
        mainContainer.setStyle("-fx-background-color: #2e2d2b;");
        mainContainer.setPadding(new Insets(16, 16, 16, 16));

        mainContainer.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        RowConstraints row1 = new RowConstraints();
        row1.setPrefHeight(60);
        row1.setMinHeight(60);
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS);
//        row2.setPrefHeight(400);
        RowConstraints row3 = new RowConstraints();
        row3.setPrefHeight(60);
        row3.setMinHeight(60);

        ColumnConstraints column1 = new ColumnConstraints();
//        column1.setMinWidth(250);
//        column1.setPercentWidth(25);
//        column1.setFillWidth(true);
//        column1.setHgrow(Priority.ALWAYS);
        column1.setMinWidth(250);
        column1.setPrefWidth(250);
        column1.setMaxWidth(250);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setMinWidth(250);
        column2.setFillWidth(true);

        mainContainer.getRowConstraints().addAll(row1, row2, row3);
        mainContainer.getColumnConstraints().addAll(column1, column2);

        mainContainer.setHgap(10);
        mainContainer.setVgap(10);

        mainContainer.add(navBar, 0, 0, 2, 1);
        mainContainer.add(itemList, 0, 1, 1, 2);
        mainContainer.add(mainPanel, 1, 1, 1, 1);
        mainContainer.add(sectionControlBar, 1, 2, 1, 1);
        return mainContainer;
    }

    private void setUpItemList() {
        itemList = new VBox();
//        itemList.setPrefWidth(400.);
        itemList.setStyle("-fx-background-color: #3b3a38;");
        itemList.setPadding(new Insets(16, 16, 16, 16));
        itemList.setSpacing(16);

        noteCategories = SharedController.reloadNoteCategories();
        noteCategoryChoiceBox = new ChoiceBox<>();
        noteCategoryChoiceBox.setPrefWidth(Double.MAX_VALUE);
        noteCategoryChoiceBox.getItems().clear();
        String[] noteCategoryNames = SharedController.getNoteCategoryNames(noteCategories);
        noteCategoryChoiceBox.getItems().addAll(noteCategoryNames);

        String baseCategoryName = NoteCategory.getAllGoalNoteCategory(noteCategories).getCategory();
        noteCategoryChoiceBox.setValue(baseCategoryName);

        setUpBaseChoiceBoxListener();

        notes = SharedController.reloadNotes();
        itemListView = new ListView<>();
        itemListView.getItems().clear();
        itemListView.setPrefHeight(5000);
//        itemListView.setStyle("-fx-background-color: red;");

        itemListView.setMaxWidth(Double.MAX_VALUE);
        itemListView.setMaxHeight(Double.MAX_VALUE);

        GridPane.setHgrow(itemListView, Priority.ALWAYS);

        String[] noteTitles;
        noteTitles = SharedController.getNotesTitles(notes);

        itemListView.getItems().addAll(noteTitles);

        itemList.getChildren().add(noteCategoryChoiceBox);
        itemList.getChildren().add(itemListView);
    }

    public void setUpBaseChoiceBoxListener () {
        baseChoiceBoxListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String previous, String current) {
                filterItemListView(current);
            }
        };
        noteCategoryChoiceBox.getSelectionModel().selectedItemProperty().addListener(baseChoiceBoxListener);
    }

    public void removeBaseChoiceBoxListener() {
        if (baseChoiceBoxListener != null) {
            noteCategoryChoiceBox.getSelectionModel().selectedItemProperty().removeListener(baseChoiceBoxListener);
            baseChoiceBoxListener = null;
        }
    }

    private void setUpNavBar() {
        navBar = new HBox();
        navBar.setStyle("-fx-background-color: #3b3a38;");

        navBar.setPrefWidth(Double.MAX_VALUE);
        navBar.setSpacing(20);
        navBar.getStyleClass().add("align-center");

        Button notesButton = new Button("Notes");
//        Button plansButton = new Button("Plans");
        Button categoriesEditButton = new Button("Edit Categories");
        categoriesEditButton.setOnAction(e -> {
            removeBaseItemListViewListener();


            sectionControlBar.getChildren().clear();
            mainPanel.getChildren().clear();
            itemListView.getSelectionModel().clearSelection();

            CategoryEditerPanel categoryEditerPanelInstance = new CategoryEditerPanel();
            categoryEditerPanelInstance.showCategoryEditPanel(this, "Note categories");
            categoryEditerPanelInstance.showSectionControlBar(this);

//            removeItemListViewListener(mainInstance);
//
//            sectionControlBar.getChildren().clear();
//            mainPanel.getChildren().clear();
//            mainInstance.getItemListView().getSelectionModel().clearSelection();
//            AddNotesPanel addNotesPanelInstance = new AddNotesPanel();
//            addNotesPanelInstance.showAddPanel(mainInstance);
//            addNotesPanelInstance.showSectionControlBar(mainInstance);
        });

        //Sends to old app, currently
        notesButton.setOnAction(e -> {
            System.out.println("Currently nothing");
        });
//        plansButton.setOnAction(e -> {
//            System.out.println("Currently nothing");
//        });

        navBar.getChildren().addAll(notesButton, categoriesEditButton);
//        navBar.getChildren().addAll(notesButton, plansButton, categoriesEditButton);
    }

    private void setUpSectionControlBar() {
        sectionControlBar = new HBox();

        sectionControlBar.setStyle("-fx-background-color: #3b3a38;");
        sectionControlBar.getStyleClass().add("align-center");

//        sectionControlBar.setPrefWidth(Double.MAX_VALUE);
        sectionControlBar.setSpacing(20);
    }

    public void setUpFunctionalSectionControlBar() {
        sectionControlBar.setSpacing(16);

        Button add = new Button("Add");
        add.setOnAction(e -> addButtonFunctionality());

        sectionControlBar.getChildren().add(add);
    }

    private void addButtonFunctionality() {
        sectionControlBar.getChildren().clear();
        mainPanel.getChildren().clear();

        AddNotesPanel addNotesPanelInstance = new AddNotesPanel();
        addNotesPanelInstance.showAddPanel(SharedNoteController.this);
        addNotesPanelInstance.showSectionControlBar(SharedNoteController.this);
    }

    private void setUpMainPanel() {
        mainPanel = new VBox();
        mainPanel.setStyle("-fx-background-color: #3b3a38;");
        mainPanel.setSpacing(16);
        mainPanel.setPadding(new Insets(16, 16, 16, 16));

        setUpBaseItemListViewListener();
    }

    public void reloadItemList() {
        String selectedCategory;
        if (noteCategoryChoiceBox.getValue() != null) {
            selectedCategory = noteCategoryChoiceBox.getValue().toString();
        } else {
            selectedCategory = "";
        }
        noteCategoryChoiceBox.getItems().clear();

        String[] noteCategoryNames = SharedController.getNoteCategoryNames(noteCategories);
        noteCategoryChoiceBox.getItems().addAll(noteCategoryNames);
        if (noteCategories.contains(selectedCategory)) {
            //Because maybe selectedCategory was meant to be destroyed.
            noteCategoryChoiceBox.setValue(selectedCategory);
        } else {
            String baseCategoryName = NoteCategory.getAllGoalNoteCategory(noteCategories).getCategory();
            noteCategoryChoiceBox.setValue(baseCategoryName);
        }

        itemListView.getItems().clear();
        String[] noteTitles = SharedController.getNotesTitles(notes);
        itemListView.getItems().addAll(noteTitles);
    }

    public void setUpBaseItemListViewListener() {
        SharedNoteController thisInstance = this;
        baseListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldNoteTitle, String newNoteTitle) {
                sectionControlBar.getChildren().clear();
                System.out.println("Listener execute");
                MainNotesPanel mainNotesPanelInstance = new MainNotesPanel();
                mainNotesPanelInstance.setUpMainPanelView(thisInstance);
                mainNotesPanelInstance.showSectionControlBar(thisInstance);
            }
        };
        itemListView.getSelectionModel().selectedItemProperty().addListener(baseListener);
    }

    public void removeBaseItemListViewListener() {
        if (baseListener != null) {
            itemListView.getSelectionModel().selectedItemProperty().removeListener(baseListener);
            baseListener = null;
        }
    }

    private void filterItemListView(String categoryName) {
        NoteCategory selectedCategory = getGoalOfNoteCategoryFromName(categoryName, noteCategories);

        if (selectedCategory == null) {
            return;
        }

        getItemListView().getItems().clear();

        if (selectedCategory.getGoal() != null
                && selectedCategory.getGoal().equals("all")) {
            String[] noteTitles;
            noteTitles = SharedController.getNotesTitles(notes);

            getItemListView().getItems().addAll(noteTitles);
            return;
        }


        for (Note note : notes) {
            if (note.getCategory() == null) {
                continue;
            }
            if (note.getCategory().equals(categoryName)) {
                getItemListView().getItems().add(note.getTitle());
            }
        }
    }

    public boolean createCategory(TextField inputField){
        String newCategoryTitle = inputField.getText();
        if (newCategoryTitle.equals("")) {
            System.out.println("The category title field can't be empty");
            return false;
        }

        for (NoteCategory noteCategory : noteCategories) {
            if (noteCategory.getCategory().equals(newCategoryTitle)) {
                System.out.println("The category already exists");
                return false;
            }
        }

        NoteCategory newNoteCategory = new NoteCategory(newCategoryTitle);
        addToDbWithSetOrder(newNoteCategory);
        noteCategories = SharedController.reloadNoteCategories();
        return true;
    }

    private void addToDbWithSetOrder(NoteCategory newCategory) {
        NoteCategory lastElement = noteCategories.get(noteCategories.size() - 1);
        int newOrderNumber = lastElement.getOrder();

        //Last element is always "Other" which is base value for not selected category when creating new item.
        lastElement.setOrder(lastElement.getOrder() + 1);
        newCategory.setOrder(newOrderNumber);

        //Change in DB !!!
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");

            String editNoteCategory =
                    "UPDATE noteCategories " +
                            "SET [order]=?" +
                            " WHERE category= ?";
            PreparedStatement preparedEditStatement = conn.prepareStatement(editNoteCategory);
            preparedEditStatement.setInt(1, lastElement.getOrder());
            preparedEditStatement.setString(2, lastElement.getCategory());
            preparedEditStatement.executeUpdate();

            String addNewCategory =
                    "INSERT INTO noteCategories VALUES " +
                            "(?, ?, null)";
            PreparedStatement preparedAddStatement = conn.prepareStatement(addNewCategory);
            preparedAddStatement.setString(1, newCategory.getCategory());
            preparedAddStatement.setInt(2, newCategory.getOrder());
            preparedAddStatement.execute();

            conn.close();
        } catch (SQLException event) {
            System.out.println("The connection failed");
            event.printStackTrace();
        }
    }

    public String getLastScene() {
        return lastScene;
    }

    public String getCurrentScene() {
        return currentScene;
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public String getTargetListElement() {
        return targetListElement;
    }

    public List<NoteCategory> getNoteCategories() {
        return noteCategories;
    }

    public GridPane getMainContainer() {
        return mainContainer;
    }

    public VBox getMainPanel() {
        return mainPanel;
    }

    public HBox getSectionControlBar() {
        return sectionControlBar;
    }

    public ListView getItemListView() {
        return itemListView;
    }

    public ChoiceBox getNoteCategoryChoiceBox() {
        return noteCategoryChoiceBox;
    }

    public ChangeListener<String> getBaseListener() {
        return baseListener;
    }

    public void setLastScene(String lastScene) {
        this.lastScene = lastScene;
    }

    public void setCurrentScene(String currentScene) {
        this.currentScene = currentScene;
    }

    public void setCurrentSection(String currentSection) {
        this.currentSection = currentSection;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void setNoteCategories(List<NoteCategory> noteCategories) {
        this.noteCategories = noteCategories;
    }

    public void setTargetListElement(String targetListElement) {
        this.targetListElement = targetListElement;
    }

    public void selectTargetListElement() {
        System.out.println("Test of selecting targetListElement");
    }

    public void setMainContainer(GridPane mainContainer) {
        this.mainContainer = mainContainer;
    }

    public void setMainPanel(VBox mainPanel) {
        this.mainPanel = mainPanel;
    }

    public void setSectionControlBar(HBox sectionControlBar) {
        this.sectionControlBar = sectionControlBar;
    }

    public void setBaseListener(ChangeListener<String> baseListener) {
        this.baseListener = baseListener;
    }

    protected NoteCategory getGoalOfNoteCategoryFromName(String noteCategoryName, List<NoteCategory> noteCategories) {
        for (NoteCategory noteCategory : noteCategories) {
            if (noteCategory.getCategory().equals(noteCategoryName)) {
                return noteCategory;
            }
        }
        return null;
    }
}
