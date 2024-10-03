package TruKoaiu.Panels;

import TruKoaiu.SharedController;
import TruKoaiu.SharedNoteController;
import TruKoaiu.classes.Note;
import TruKoaiu.classes.NoteCategory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
//import NoteApp.SharedController;
//import NoteApp.SharedNoteController;
//import NoteApp.classes.NoteCategory;
//import NoteApp.classes.Note;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class MainNotesPanel {

    private VBox mainPanel;
    private HBox sectionControlBar;

    private SharedNoteController mainInstance;

    private List<Note> notes;
    private List<NoteCategory> noteCategories;

    //Needed here for deleting Listener when changing the MainPanel. Yes, I could just setUpListView(),
    //but I use this project to learn as well, so I wanted to use it (i know performance improvement will be in nano secs xD).
    private ChangeListener<String> changeListener;

    private Label title;
    private Label category;
    private Label date;
    private TextArea noteContent;

    private Button delete;

    public void setUpMainPanelView(SharedNoteController mainInstance) {
        this.mainInstance = mainInstance;
        notes = mainInstance.getNotes();
        noteCategories = mainInstance.getNoteCategories();
        mainPanel = mainInstance.getMainPanel();


        Pane spacer1 = new Pane();
        spacer1.setMinSize(10, 1);
        HBox.setHgrow(spacer1, javafx.scene.layout.Priority.ALWAYS);

        //Probably not needed to be here, but I will leave it here for now.
        HBox titleCloseHolder = new HBox();
        title = new Label("Title");
        title.getStyleClass().add("big");
        title.getStyleClass().add("bold");
        title.setTextFill(Color.color(1, 1, 1)); //TO CSS LATER ON, just like other styling stuff.
        Button close = new Button("X");
        close.setOnAction(e -> clearPanels());

        titleCloseHolder.getChildren().addAll(title, spacer1, close);

        HBox categoryDateHolder = new HBox();
        Pane spacer2 = new Pane();
        spacer2.setMinSize(10, 1);
        HBox.setHgrow(spacer2, javafx.scene.layout.Priority.ALWAYS);
        category = new Label("Category");
        category.getStyleClass().add("boost");
        category.setTextFill(Color.color(1, 1, 1));
        date = new Label("DD-MM-YYYY HH-MM-SS");
        date.setTextFill(Color.color(0.8, 0.8, 0.8));
        categoryDateHolder.getChildren().addAll(category, spacer2, date);

        noteContent = new TextArea("Note content");
        noteContent.setWrapText(true);
        noteContent.setEditable(false);
        VBox.setVgrow(noteContent, Priority.ALWAYS);

        mainPanel.getChildren().addAll(titleCloseHolder, categoryDateHolder, noteContent);

        ListView<String> ItemListView = mainInstance.getItemListView();
        mainInstance.removeBaseItemListViewListener();
        setUpItemListViewListener(mainInstance, notes);
        Note selectedNote = SharedController.findNoteByTitle(notes, ItemListView.getSelectionModel().getSelectedItem().toString());

        title.setText(selectedNote.getTitle());
        category.setText(selectedNote.getCategory());
        date.setText(selectedNote.getDate());
        noteContent.setText(selectedNote.getContent());
    }

    public void showSectionControlBar(SharedNoteController mainInstance) {
        sectionControlBar = mainInstance.getSectionControlBar();
        sectionControlBar.setSpacing(16);

        //Probably not needed to be here, but I will leave it here for now.
        Button add = new Button("Add");
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                removeItemListViewListener(mainInstance);

                sectionControlBar.getChildren().clear();
                mainPanel.getChildren().clear();
                mainInstance.getItemListView().getSelectionModel().clearSelection();
                AddNotesPanel addNotesPanelInstance = new AddNotesPanel();
                addNotesPanelInstance.showAddPanel(mainInstance);
                addNotesPanelInstance.showSectionControlBar(mainInstance);
            }
        });
        Button edit = new Button("Edit");
        edit.setOnAction(e -> editButtonFunctinality());
        delete = new Button("Delete");
        delete.setOnAction(e -> deleteButtonFunctionality());

        sectionControlBar.getChildren().addAll(add, edit, delete);
    }

    private void editButtonFunctinality() {
        Object selectedNoteObject = mainInstance.getItemListView().getSelectionModel().getSelectedItem();
        removeItemListViewListener(mainInstance);

        sectionControlBar.getChildren().clear();
        mainPanel.getChildren().clear();
        mainInstance.getItemListView().getSelectionModel().clearSelection();
        EditNotesPanel editNotesPanelInstance = new EditNotesPanel();
        editNotesPanelInstance.showEditPanel(mainInstance, selectedNoteObject);
        editNotesPanelInstance.showSectionControlBar(mainInstance);
    }

    private void deleteButtonFunctionality() {
        if (delete.getText().equals("Delete")) {
            delete.setText("Confirm?");
            return;
        }

        String targetTitle = mainInstance.getItemListView().getSelectionModel().getSelectedItem().toString();
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:notes.db");
//            conn = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db");

            String deleteFromnotes = "DELETE FROM notes WHERE title = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(deleteFromnotes);
            preparedStatement.setString(1, targetTitle);
            preparedStatement.executeUpdate();
            preparedStatement.close();

            conn.close();
        } catch (SQLException event) {
            throw new RuntimeException(event);
        }

        delete.setText("Delete");
        mainInstance.setNotes(SharedController.reloadNotes());
        notes = mainInstance.getNotes();
        mainInstance.reloadItemList();
        clearPanels();

        ListView itemListView = mainInstance.getItemListView();
        if (!itemListView.getItems().isEmpty()) {
            mainInstance.getItemListView().getSelectionModel().select(0);
        }
    }

    private void setUpItemListViewListener(SharedNoteController mainInstance, List<Note> notes) {
        changeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldNoteTitle, String newNoteTitle) {
                Note selectedNote = SharedController.findNoteByTitle(notes, newNoteTitle);
                if (selectedNote == null) {
                    return;
                }

                title.setText(newNoteTitle);
                category.setText(selectedNote.getCategory());
                date.setText(selectedNote.getDate());
                noteContent.setText(selectedNote.getContent());

                if (delete.getText().equals("Confirm?")) {
                    delete.setText("Delete");
                }
            }
        };
        mainInstance.getItemListView().getSelectionModel().selectedItemProperty().addListener(changeListener);
    }

    private void clearPanels() {
        removeItemListViewListener(mainInstance);

        sectionControlBar.getChildren().clear();
        mainPanel.getChildren().clear();
        mainInstance.getItemListView().getSelectionModel().clearSelection();
        mainInstance.setUpBaseItemListViewListener();
        mainInstance.setUpFunctionalSectionControlBar();
    }

    public void removeItemListViewListener(SharedNoteController mainInstance) {
        if (changeListener == null) {return;}

        mainInstance.getItemListView().getSelectionModel().selectedItemProperty().removeListener(changeListener);
    }

}
