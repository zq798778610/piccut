package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Controller {

    @FXML
    public Button btn_cut;
    @FXML
    public TextArea ta_message;
    @FXML
    public TextField tf_x;
    @FXML
    public TextField tf_y;
    @FXML
    public TextField tf_choice_dir;
    @FXML
    public ImageView iv_pic;
    @FXML
    public Pane pane_cut;
    @FXML
    public Label lb_image_size;
    private Preferences prefs;
    private double imageWidth;
    private double imageHeight;
    private int showSplit = 2;

    public void init(Stage stage) {
        prefs = Preferences.userNodeForPackage(Main.class);
        String choiseDir = prefs.get("choiseDir", "");
        tf_choice_dir.setText(choiseDir);
        tf_x.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                setShowWidth();
            }
        });
        tf_y.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                setShowWidth();
            }
        });
        initData();
    }

    private void initData() {
        String dir = tf_choice_dir.getText().toString().trim();
        if (dir == null || dir == "") {
            setMessage("ERROR:请设置图片文件夹！");
            return;
        }
        showPic(dir);
    }

    public double showWidth = 0;
    public double showHegint = 0;
    public double showEndHegint = 0;
    public double showEndWidth = 0;

    private void showPic(String dir) {
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            setMessage("ERROR:图片文件夹不存在！");
            return;
        }
        File newDir=new File(dir,fileDir.getName());
        if(!newDir.exists()){
            newDir.mkdirs();
        }
        File[] picFiles = fileDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowerCase = name.toLowerCase();
                if (lowerCase.endsWith("jpg") || lowerCase.endsWith("png")) {
                    File file=new File(dir,name);
                    try {
                        File file1=new File(newDir,name);
                        if(!file1.exists()){
                            FileUtils.copyFileToDirectory(file,newDir);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
        if (picFiles.length == 0) {
            setMessage("ERROR:图片文件夹下无图片");
            return;
        }
        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        double width = screenRectangle.getWidth();
        double height = screenRectangle.getHeight();

        Image image = new Image("file:" + picFiles[0].getPath());
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        lb_image_size.setText("图片尺寸:x:"+imageWidth+"   y:"+imageHeight);
        if(imageWidth>width){
            showSplit = 4;
        }

        iv_pic.setFitWidth(imageWidth / showSplit);
        iv_pic.setFitHeight(imageHeight / showSplit);
        iv_pic.setImage(image);
        setShowWidth();
    }

    private void setShowWidth() {
        if (imageWidth == 0 && imageHeight == 0) {
            setMessage("ERROR:切割范围显示错误!");
            return;
        }
        String x = tf_x.getText().toString().trim();
        String y = tf_y.getText().toString().trim();
        int xCut = 1000;
        int yCut = 1280;
        try {
            xCut = Integer.parseInt(x);
            yCut = Integer.parseInt(y);
        } catch (Exception e) {
            setMessage("ERROR:输入正确的切割点！");
            return;
        }
        if(xCut>imageWidth){
            xCut = (int) imageWidth;
        }
        if(yCut>imageHeight){
            yCut = (int) imageHeight;
        }
        showWidth = (imageWidth - xCut) / 2/showSplit;
        showHegint = (imageHeight - yCut) / 2/showSplit;
        pane_cut.setStyle("-fx-border-color:yellow");
        pane_cut.setPrefWidth(xCut / showSplit);
        pane_cut.setPrefHeight(yCut / showSplit);
        pane_cut.setLayoutX(showWidth);
        pane_cut.setLayoutY(showHegint);
    }

    public void choiceDir(MouseEvent mouseEvent) {
        Node source = (Node) mouseEvent.getSource();
        Window theStage = source.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择图片文件夹");
        File file = directoryChooser.showDialog(theStage);
        if (file != null) {
            prefs.put("choiseDir", file.getPath());
            tf_choice_dir.setText(file.getPath());
            initData();
        }
    }

    private void setMessage(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ta_message.setText(message + "\r\n" + ta_message.getText());
            }
        });
    }

    public void cut(MouseEvent mouseEvent) {

        String dir = tf_choice_dir.getText().toString().trim();

        String x = tf_x.getText().toString().trim();
        String y = tf_y.getText().toString().trim();
        int xCut = 1000;
        int yCut = 1280;
        try {
            xCut = Integer.parseInt(x);
            yCut = Integer.parseInt(y);
        } catch (Exception e) {
            setMessage("ERROR:输入正确的切割点！");
            return;
        }
        File fileDir = new File(dir);
        setMessage("进度：开始裁剪"+fileDir.getName());
        if (!fileDir.exists()) {
            setMessage("ERROR:图片文件夹不存在！");
            return;
        }
        if (!fileDir.exists()) {
            setMessage("ERROR:图片文件夹不存在！");
            return;
        }
        File[] picFiles = fileDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowerCase = name.toLowerCase();
                if (lowerCase.endsWith("jpg") || lowerCase.endsWith("png")) {
                    return true;
                }
                return false;
            }
        });

       for(int i=0;i<picFiles.length;i++){
           try {
               setMessage("进度：裁剪>>"+i);
               File picFile = picFiles[i];
               Thumbnails.of(picFile)
                       .sourceRegion(Positions.CENTER, xCut, yCut)
                       .scale(1.0)
                       .toFile(picFile);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
        setMessage("进度：裁剪完成！！！");
    }
}
