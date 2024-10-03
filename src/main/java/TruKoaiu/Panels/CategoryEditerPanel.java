package TruKoaiu.Panels;

import TruKoaiu.SharedController;
import TruKoaiu.SharedNoteController;
import TruKoaiu.classes.Note;
import TruKoaiu.classes.NoteCategory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
//import NoteApp.SharedController;
//import NoteApp.SharedNoteController;
//import NoteApp.classes.Note;
//import NoteApp.classes.NoteCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CategoryEditerPanel {


    private VBox mainPanel;
    private HBox sectionControlBar;

    private SharedNoteController mainInstance;


    private List<Note> notes;
    private List<NoteCategory> noteCategories;

    //Needed here for deleting Listener when changing the MainPanel. Yes, I could just setUpListView(),
    //but I use this project to learn as well, so I wanted to use it (i know performance improvement will be in nano secs xD).
    private ChangeListener<String> changeListener;
    private ChangeListener<String> categoryListener;

    private ChoiceBox chooseGroup;
    private ListView itemListView;
    private TextField titleInput;
    private ChoiceBox chooseOrder;
    private Button delete;
    private TextField categoryCreatorInput;
    private Button categoryCreatorButton;

    public void showCategoryEditPanel(SharedNoteController mainInstance, String previousSection) {
        this.mainInstance = mainInstance;
        notes = mainInstance.getNotes();
        noteCategories = mainInstance.getNoteCategories();
        itemListView = mainInstance.getItemListView();
        chooseGroup = mainInstance.getNoteCategoryChoiceBox();

        mainPanel = mainInstance.getMainPanel();

        HBox headerContainer = setUpHeader();

        GridPane userInputElements = createGridSetUp();
        setUpGridContentElements(userInputElements);

//        noteContent = new TextArea();
//        noteContent.setPromptText("Note content");
//        noteContent.setWrapText(true);
//        VBox.setVgrow(noteContent, Priority.ALWAYS);

        mainInstance.removeBaseItemListViewListener();
        mainInstance.removeBaseChoiceBoxListener();
        setUpItemListViewListener(notes);


        mainPanel.getChildren().addAll(headerContainer, userInputElements);
//        mainPanel.getChildren().addAll(headerContainer, userInputElements, noteContent);

        ArrayList<String> test = new ArrayList<>();
        for (NoteCategory item : noteCategories) {
            test.add(item.getCategory());
        }

        reloadChoiceGroup();
//        reloadItemListView(test);
        setUpChoiseBoxListener();
        chooseGroup.setValue(previousSection);
    }

    public void showSectionControlBar(SharedNoteController mainInstance) {
        sectionControlBar = mainInstance.getSectionControlBar();
        sectionControlBar.setSpacing(16);

        //Probably not needed to be here, but I will leave it here for now.
        Button add = new Button("Save Changes");
        add.setOnAction(e -> editButtonFunctionality());
        delete = new Button("Delete");
        delete.setOnAction(e -> deleteButtonFunctionality());
        Pane spacer = new Pane();
        spacer.setMinSize(10, 1);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button goBack = new Button("Go back");
        goBack.setOnAction(e -> clearPanels());

        sectionControlBar.getChildren().addAll(add, delete, spacer, goBack);
//        sectionControlBar.getChildren().addAll(add, spacer, goBack);
    }

    private void editButtonFunctionality() {
        if (mainInstance.getItemListView().getSelectionModel().getSelectedItem() == null) {
            System.out.println("Current selected Note equals null");
            return;
        }

        if (titleInput.getText().equals("")) {
            System.out.println("New title is empty");
            return;
        }

        changeOrderAction();
        editInDB();

        noteCategories = SharedController.reloadNoteCategories();
        mainInstance.setNoteCategories(noteCategories);
        titleInput.setText("");
        chooseOrder.setValue("");
        mainInstance.getItemListView().getSelectionModel().clearSelection();

        reloadItemListView(SharedController.getNoteCategoryNames(noteCategories));
    }

    private void changeOrderUp(NoteCategory targetCategory, Integer finalOrder) {
        Integer startingOrder = targetCategory.getOrder();

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");


            for (int i = startingOrder; i < finalOrder; i++) {
                String editNoteCategory =
                        "UPDATE noteCategories " +
                                "SET [order]=?" +
                                " WHERE category= ?";
                PreparedStatement preparedEditStatement = conn.prepareStatement(editNoteCategory);
                preparedEditStatement.setInt(1, (noteCategories.get(i - 1).getOrder() - 1));
                preparedEditStatement.setString(2, noteCategories.get(i - 1).getCategory());
                preparedEditStatement.executeUpdate();
                preparedEditStatement.close();
            }

            String editNoteCategory =
                    "UPDATE noteCategories " +
                            "SET [order]=?" +
                            " WHERE category= ?";
            PreparedStatement preparedEditStatement = conn.prepareStatement(editNoteCategory);
            preparedEditStatement.setInt(1, finalOrder);
            preparedEditStatement.setString(2, targetCategory.getCategory());
            preparedEditStatement.executeUpdate();
            preparedEditStatement.close();

            conn.close();
        } catch (SQLException event) {
            System.out.println("The connection failed");
            event.printStackTrace();
        }
    }

    private void changeOrderDown(NoteCategory targetCategory, Integer finalOrder) {
        Integer startingOrder = targetCategory.getOrder();

        System.out.println("Final order: " + finalOrder);

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");


            for (int i = finalOrder; i < startingOrder; i++) {
                String editNoteCategory =
                        "UPDATE noteCategories " +
                                "SET [order]=?" +
                                " WHERE category= ?";
                PreparedStatement preparedEditStatement = conn.prepareStatement(editNoteCategory);
                preparedEditStatement.setInt(1, (noteCategories.get(i - 1).getOrder() + 1));
                preparedEditStatement.setString(2, noteCategories.get(i - 1).getCategory());
                preparedEditStatement.executeUpdate();
                preparedEditStatement.close();
//            System.out.println(noteCategories.get(i-1).getCategory() + "From: " + noteCategories.get(i-1).getOrder() +
//                    " To: " + (noteCategories.get(i-1).getOrder()+1));
            }

//            System.out.println(targetCategory.getCategory() + "From: " + targetCategory.getOrder() +
//                    " To: " + (finalOrder));
            String editNoteCategory =
                    "UPDATE noteCategories " +
                            "SET [order]=?" +
                            " WHERE category= ?";
            PreparedStatement preparedEditStatement = conn.prepareStatement(editNoteCategory);
            preparedEditStatement.setInt(1, finalOrder);
            preparedEditStatement.setString(2, targetCategory.getCategory());
            preparedEditStatement.executeUpdate();
            preparedEditStatement.close();

            conn.close();
        } catch (SQLException event) {
            System.out.println("The connection failed");
            event.printStackTrace();
        }
    }

    private void changeOrderAction() {
        String selectedItemName = mainInstance.getItemListView().getSelectionModel().getSelectedItem().toString();
        NoteCategory selectedItem = SharedController.findNoteCategoryByTitle(noteCategories, selectedItemName);
        if (selectedItem.getOrder() == Integer.valueOf(chooseOrder.getValue().toString())) {
            return;
        } else if (selectedItem.getOrder() < Integer.valueOf(chooseOrder.getValue().toString())) {
            changeOrderUp(selectedItem, Integer.valueOf(chooseOrder.getValue().toString()));
        } else {
            changeOrderDown(selectedItem, Integer.valueOf(chooseOrder.getValue().toString()));
        }
    }

    private void editInDB() {
        String oldNoteTitle = mainInstance.getItemListView().getSelectionModel().getSelectedItem().toString();
//        Note oldNote = SharedController.findNoteByTitle(notes, oldNoteTitle);

        String newTitle = titleInput.getText();
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");

            String editNote = "UPDATE noteCategories " +
                    "SET category = ?" +
                    "WHERE category = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(editNote);
            preparedStatement.setString(1, newTitle);
            preparedStatement.setString(2, oldNoteTitle);

            preparedStatement.executeUpdate();

            preparedStatement.close();
            conn.close();
        } catch (SQLException event) {
            System.out.println("The connection failed");
            event.printStackTrace();
        }
    }

    private void deleteButtonFunctionality() {
        String targetTitle = mainInstance.getItemListView().getSelectionModel().getSelectedItem().toString();
        NoteCategory targetNoteCategory = SharedController.findNoteCategoryByTitle(noteCategories, targetTitle);

        if (targetNoteCategory.getGoal() != null) {
            System.out.println("The goal is set, can't delete this category");
            return;
        }

        if (delete.getText().equals("Delete")) {
            delete.setText("Confirm?");
            return;
        }

        Integer selectedCategoryIndex = mainInstance.getItemListView().getSelectionModel().getSelectedIndex();
        String nextElementTitle = mainInstance.getItemListView().getItems().get(selectedCategoryIndex + 1).toString();

        deleteInDB(targetTitle);

        delete.setText("Delete");
        mainInstance.setNotes(SharedController.reloadNotes());
        notes = mainInstance.getNotes();

        noteCategories = SharedController.reloadNoteCategories();
        mainInstance.setNoteCategories(noteCategories);

        reloadItemListView(SharedController.getNoteCategoryNames(noteCategories));

        ListView itemListView = mainInstance.getItemListView();
        if (!itemListView.getItems().isEmpty() && itemListView.getItems().size() >= selectedCategoryIndex) {
            mainInstance.getItemListView().getSelectionModel().select(nextElementTitle);
        }
    }

    private void deleteInDB(String targetTitle) {
        NoteCategory categoryToDelete = SharedController.findNoteCategoryByTitle(noteCategories, targetTitle);

        if (categoryToDelete.getGoal() != null) {
            System.out.println("The goal is set, don't delete it");
            return;
        }

        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");

            String deleteNoteCategory =
                    "DELETE FROM noteCategories " +
                            "WHERE category= ?";
            PreparedStatement preparedDeleteStatement = conn.prepareStatement(deleteNoteCategory);
            preparedDeleteStatement.setString(1, categoryToDelete.getCategory());
            preparedDeleteStatement.executeUpdate();
            preparedDeleteStatement.close();

            Integer deletedCategoryOrder = categoryToDelete.getOrder();

            for (int i = deletedCategoryOrder - 1; i < noteCategories.size(); i++) {
                String editNoteCategory =
                        "UPDATE noteCategories " +
                                "SET [order]= ?" +
                                " WHERE category= ?";
                PreparedStatement preparedEditStatement = conn.prepareStatement(editNoteCategory);
                preparedEditStatement.setInt(1, (i));
                preparedEditStatement.setString(2, noteCategories.get(i).getCategory());
                preparedEditStatement.executeUpdate();
                preparedEditStatement.close();
            }

            for (Note note : notes) {
                if (note.getCategory().equals(categoryToDelete.getCategory())) {
                    String editNoteCategory =
                            "UPDATE notes " +
                                    "SET category= ?" +
                                    " WHERE category= ?";
                    PreparedStatement preparedEditStatement = conn.prepareStatement(editNoteCategory);
                    preparedEditStatement.setString(1, noteCategories.get(noteCategories.size() - 1).getCategory());
                    preparedEditStatement.setString(2, categoryToDelete.getCategory());
                    preparedEditStatement.executeUpdate();
                    preparedEditStatement.close();
                }
            }

            conn.close();
        } catch (SQLException event) {
            System.out.println("The connection failed");
            event.printStackTrace();
        }
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
        titleInput.setPromptText("Input changed category title...");
        GridPane.setHgrow(titleInput, Priority.ALWAYS);
        VBox.setVgrow(titleInput, Priority.ALWAYS);

        grid.add(title, 0, 0, 1, 1);
        grid.add(titleInput, 1, 0, 1, 1);
    }

    private void setUpCategorySection(GridPane grid) {
        Label categorySectionLabel = new Label(""); //let's hope it temporary empty (i will probably forget)
        categorySectionLabel.getStyleClass().add("bold");
        categorySectionLabel.getStyleClass().add("boost");

        Label categoryLabel = new Label("Swap order:");
        categoryLabel.getStyleClass().add("boost");

        chooseOrder = new ChoiceBox();
        String otherCategoryName = NoteCategory.getOtherGoalNoteCategory(noteCategories).getCategory();

//        reloadCategoryPicker(otherCategoryName);                                         =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

        chooseOrder.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(chooseOrder, Priority.ALWAYS);

        grid.add(categorySectionLabel, 0, 1, 3, 1);
        grid.add(categoryLabel, 0, 2, 1, 1);
        grid.add(chooseOrder, 1, 2, 1, 1);
    }

    private void fillOrderChoices() {
        chooseOrder.getItems().clear();
        if (chooseGroup.getValue().equals("Note categories")) {
            for (int i = 1; i < noteCategories.size(); i++) {
                chooseOrder.getItems().add(i);
            }
        } else {
            System.out.println("Don't support planer yet");
        }
    }

    private void setUpCategoryCreatorSection(GridPane grid) {
//        Label categoryCreatorLabel = new Label("Or create:");
//        categoryCreatorLabel.getStyleClass().add("boost");
//        categoryCreatorLabel.setTextFill(Color.color(1, 1, 1)); // Styling here is temporary
//
//        categoryCreatorInput = new TextField();
//        categoryCreatorInput.setPromptText("Input your custom category name...");
//        HBox.setHgrow(categoryCreatorInput, Priority.ALWAYS);
//
//        categoryCreatorButton = new Button("Confirm");
//        categoryCreatorButton.setOnAction(e -> handleCategoryCreation());
//
//        grid.add(categoryCreatorLabel, 0, 3, 1, 1);
//        grid.add(categoryCreatorInput, 1, 3, 1, 1);
//        grid.add(categoryCreatorButton, 2, 3, 1, 1);
    }

    private void clearUserInput() {
        titleInput.setText("");
        chooseOrder.setValue("");
    }

    private void handleCategoryCreation() {
        boolean succeeded = mainInstance.createCategory(categoryCreatorInput);
        if (!succeeded) {
            return;
        }
        ;
        mainInstance.reloadItemList();
        noteCategories = mainInstance.getNoteCategories();

        String newlyCreatedCategoryName = categoryCreatorInput.getText();
        reloadCategoryPicker(newlyCreatedCategoryName);

        categoryCreatorInput.setText("");
    }

    private void reloadCategoryPicker(String setValue) {
        noteCategories = mainInstance.getNoteCategories();

        chooseOrder.getItems().clear();
        String[] noteCategoryNames = SharedController.getNoteCategoryNames(noteCategories);
        Set<String> noteCategoryNamesSet = new LinkedHashSet<>();
        String baseCategoryName = NoteCategory.getAllGoalNoteCategory(noteCategories).getCategory();
        noteCategoryNamesSet.addAll(List.of(noteCategoryNames));
        noteCategoryNamesSet.remove(baseCategoryName);
        chooseOrder.getItems().addAll(noteCategoryNamesSet);
        chooseOrder.setValue(setValue);
    }

    private HBox setUpHeader() {
        HBox headerContainer = new HBox();

        Label navigationText = new Label("Edit category");
        navigationText.getStyleClass().add("big");
        navigationText.getStyleClass().add("bold");
        navigationText.setTextFill(Color.color(1, 1, 1));
        Button close = new Button("X");
        close.setOnAction(e -> clearPanels());

        Pane spacer = new Pane();
        spacer.setMinSize(10, 1);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerContainer.getChildren().addAll(navigationText, spacer, close);

        return headerContainer;
    }

    private void reloadChoiceGroup() {
        chooseGroup.getItems().clear();

        chooseGroup.getItems().add("Note categories");
        chooseGroup.getItems().add("Plan categories");
    }

    private void reloadItemListView(String[] categories) {
        itemListView.getItems().clear();

        for (String category : categories) {
            itemListView.getItems().add(category);
        }
    }

    private void setUpChoiseBoxListener() {
        categoryListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String previous, String current) {
                System.out.println("Currently shown elements, are: " + current);

                if (current == null) {
                    return;
                }

                if (chooseGroup.getValue().equals("Note categories")) {
                    String[] noteCategoryTitles = SharedController.getNoteCategoryNames(noteCategories);
                    reloadItemListView(noteCategoryTitles);
                } else {
                    System.out.println("hello there... OBI WAN KENOBIII. But fr, i don't have it ready in db :/ cant add now");
                    String[] empty = {};
                    reloadItemListView(empty);
                }
                fillOrderChoices();
            }
        };
        mainInstance.getNoteCategoryChoiceBox().getSelectionModel().selectedItemProperty().addListener(categoryListener);
    }

    private void setUpItemListViewListener(List<Note> notes) {
        changeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String lastTitle, String currentTitle) {
                if (currentTitle == null || currentTitle.equals("")) {
                    return;
                }
                if (chooseGroup.getValue().equals("Note categories")) {
                    NoteCategory targetNote = SharedController.findNoteCategoryByTitle(noteCategories, currentTitle);
                    titleInput.setText(targetNote.getCategory());
                    chooseOrder.setValue(targetNote.getOrder());
                } else {
                    System.out.println("hello there... OBI WAN KENOBIII");
                }
                delete.setText("Delete");
            }
        };
        mainInstance.getItemListView().getSelectionModel().selectedItemProperty().addListener(changeListener);
    }

    private void clearPanels() {
        removeItemListViewListener();
        removeChoiseBoxListener();

        mainInstance.setCurrentScene("Note");

        sectionControlBar.getChildren().clear();
        mainPanel.getChildren().clear();
        mainInstance.getItemListView().getSelectionModel().clearSelection();
        mainInstance.setUpBaseItemListViewListener();
        mainInstance.setUpFunctionalSectionControlBar();
        mainInstance.reloadItemList();
        mainInstance.setUpBaseChoiceBoxListener();
    }

    public void removeItemListViewListener() {
        if (changeListener == null) {
            return;
        }

        mainInstance.getItemListView().getSelectionModel().selectedItemProperty().removeListener(changeListener);
    }

    public void removeChoiseBoxListener() {
        if (categoryListener == null) {
            return;
        }

        mainInstance.getNoteCategoryChoiceBox().getSelectionModel().selectedItemProperty().removeListener(categoryListener);
    }
}
