package sample.requests;

import api.MySession;
import api.User;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.hibernate.Session;

/**
 * Request is controlled by this class
 */
public class HandleRequestsController extends Application {
    private User user;
    HandleRequestGridPane gridPane;
    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Requests");

        Session session = MySession.getSession();

//        Faculty admin = session.get(Faculty.class, "raj ayyar@iiitd.ac.in");
//        Admin admin = session.get(Admin.class, "ravi@iiitd.ac.in");
        gridPane = new HandleRequestGridPane(user, false, false, true);
//        Scene scene = new Scene(gridPane.getGridPanel(), 1200, 480);
//        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Parent getRoot(){
        return gridPane.getGridPanel();
    }
}
