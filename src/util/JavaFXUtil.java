package util;

import java.io.File;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class JavaFXUtil {
	
	private static String lastSourcePath = System.getProperty("user.home");
	private static String lastBuildPath = System.getProperty("user.home");
	

	public static GridPane createFormPane() {
		GridPane gridPane = new GridPane();
		// Position the pane at the center of the screen, both vertically and horizontally
		gridPane.setAlignment(Pos.CENTER);

		// Set a padding of 20px on each side
		gridPane.setPadding(new Insets(20, 20, 20, 0));

		// Set the horizontal gap between columns
		gridPane.setHgap(10);

		// Set the vertical gap between rows
		gridPane.setVgap(10);

		// Add Column Constraints

		// columnOneConstraints will be applied to all the nodes placed in column one.
		ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
		columnOneConstraints.setHalignment(HPos.RIGHT);

		// columnTwoConstraints will be applied to all the nodes placed in column two.
		ColumnConstraints columnTwoConstrains = new ColumnConstraints(200,200, Double.MAX_VALUE);
		columnTwoConstrains.setHgrow(Priority.ALWAYS);

		gridPane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

		return gridPane;
	}

	public static TextField generateTextField(int prefHeight,String promptText) {
		TextField textField = new TextField();
		textField.setPrefHeight(prefHeight);
		textField.setPromptText(promptText); //to set the hint text
		return textField;
	}
	
	public static ComboBox<String> generateComboBox(int prefHeight,String promptText, boolean editable) {
		ComboBox<String> textField = new ComboBox<String>();
		textField.setPrefHeight(prefHeight);
		textField.setPromptText(promptText); //to set the hint text
		textField.setPrefWidth(5000);
		textField.setEditable(editable);
		return textField;
	}

	public static Button generateButton(String text,int prefHeight, int prefWidth, boolean isDefault) {
		Button button = new Button(text);
		button.setPrefHeight(prefHeight);
		button.setPrefWidth(prefWidth);
		button.setDefaultButton(isDefault);
		return button;
	}
	
	public static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.getDialogPane().setMinHeight(Region.USE_COMPUTED_SIZE);
		//alert.setWidth(500);
		alert.initOwner(owner);
		alert.show();
	}
	
	public static void showAlertNew(Alert.AlertType alertType, Window owner, String title, String header, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);

		Label label = new Label("List of copied files :");

		TextArea textArea = new TextArea(message);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.show();
	}
	
	public static void setFileChooserActionWithComboBox(Stage stage,Button button,ComboBox<String> textField, boolean isDirchooser, boolean isZip) {
		FileChooser fileChooser = new FileChooser();
		String[] nameWithExtension = null;
		if(!isZip) {
			nameWithExtension = new String[] {"Java class files", "*.class"};
		}else {
			nameWithExtension = new String[] {"Zip files", "*.zip"};
		}
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(nameWithExtension[0], nameWithExtension[1])
				);
		fileChooser.setInitialDirectory(new File(lastSourcePath));
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(lastBuildPath));
		
		// Persisting the build path over each and every tabs
		if(isDirchooser) {
			File buildPathFile = new File(lastBuildPath);
			String lastDir = buildPathFile.getName();
			if(lastDir.toLowerCase().contains("appmanager")) {
				textField.setValue(lastBuildPath);
			}
		}		
				
		button.setOnAction(e -> {
			File selectedFile = null;
			List<File> selectedFilesList = null;
			if(isDirchooser) {
				selectedFile = directoryChooser.showDialog(stage);
			}else {
				selectedFilesList = fileChooser.showOpenMultipleDialog(stage);
			}
			if(isDirchooser) {
				if(selectedFile != null) {
					textField.setValue(selectedFile.getPath());
					lastBuildPath = selectedFile.getPath();
				}
			}else {
				String fileNames = textField.getValue();
				if(selectedFilesList!=null && !selectedFilesList.isEmpty()) {
					for(File file : selectedFilesList) {
						if("".equals(fileNames)) {
							fileNames = file.getPath();
						}else {
							fileNames += ","+file.getPath();
						}
						lastSourcePath = file.getParentFile().getPath();
					}
					textField.setValue(fileNames);
				}
			}
			fileChooser.setInitialDirectory(new File(lastSourcePath));
			directoryChooser.setInitialDirectory(new File(lastBuildPath));
		});
	}
	
	public static void setFileChooserAction(Stage stage,Button button,TextField textField, boolean isDirchooser, boolean isZip) {
		FileChooser fileChooser = new FileChooser();
		String[] nameWithExtension = null;
		if(!isZip) {
			nameWithExtension = new String[] {"Java class files", "*.class"};
		}else {
			nameWithExtension = new String[] {"Zip files", "*.zip"};
		}
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(nameWithExtension[0], nameWithExtension[1])
				);
		fileChooser.setInitialDirectory(new File(lastSourcePath));
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(lastBuildPath));
		
		// Persisting the build path over each and every tabs
		if(isDirchooser) {
			File buildPathFile = new File(lastBuildPath);
			String lastDir = buildPathFile.getName();
			if(lastDir.toLowerCase().contains("appmanager")) {
				textField.setText(lastBuildPath);
			}
		}
		
		button.setOnAction(e -> {
			File selectedFile = null;
			List<File> selectedFilesList = null;
			if(isDirchooser) {
				selectedFile = directoryChooser.showDialog(stage);
			}else {
				selectedFilesList = fileChooser.showOpenMultipleDialog(stage);
			}
			if(isDirchooser) {
				if(selectedFile != null) {
					textField.setText(selectedFile.getPath());
					lastBuildPath = selectedFile.getPath();
				}
			}else {
				String fileNames = textField.getText();
				if(selectedFilesList!=null && !selectedFilesList.isEmpty()) {
					for(File file : selectedFilesList) {
						if("".equals(fileNames)) {
							fileNames = file.getPath();
						}else {
							fileNames += ","+file.getPath();
						}
						lastSourcePath = file.getParentFile().getPath();
					}
					textField.setText(fileNames);
				}
			}
			fileChooser.setInitialDirectory(new File(lastSourcePath));
			directoryChooser.setInitialDirectory(new File(lastBuildPath));
		});
	}
	public static VBox getMenuVBox(MenuItem... items) {
		 Menu menu = new Menu();	 
		 Image img = new Image("menu.png");
		 ImageView view = new ImageView(img);
		 view.setFitHeight(20);
		 view.setPreserveRatio(true);
		 menu.setGraphic(view);
		 menu.getItems().addAll(items);
		 MenuBar mb = new MenuBar();
	     mb.getMenus().addAll(menu); 
	     return new VBox(mb); 
	}
	
	 public static void populateCompanyName(ComboBox<String> combo) {
        combo.getItems().add("Intel");
        combo.getItems().add("Apple");
        combo.getItems().add("Microsoft");
	 }
}
