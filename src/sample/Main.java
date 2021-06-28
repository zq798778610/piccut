package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage = primaryStage;
        primaryStage.setTitle("图片裁剪");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
        initRootLayout();
    }

    private double width = 562; //窗口宽度
    private double height = 342; //窗口高度
    private GridPane rootLayout;
    /**
     * 初始化parentView
     */
    private void initRootLayout() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("sample.fxml"));  //静态读取，无法获取controller
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout, width, height);
            stage.setScene(scene);
            stage.setMaximized(true);
            Controller controller = loader.getController();
            controller.init(stage);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
