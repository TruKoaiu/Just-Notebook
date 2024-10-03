package TruKoaiu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class StarterFile extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DataBaseValidator.check();

        SharedNoteController controller = new SharedNoteController();
        controller.setUpMainContainer();

        Scene scene = new Scene(controller.getMainContainer(), 800, 600);  // Set up the scene with the controller's mainContainer
        String css = this.getClass().getResource("MainStyles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Just Notebook");
        stage.getIcons().add(new Image("BookAndQuill.png"));
        stage.setScene(scene);  // Set the scene
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}