import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.DynamicBuildUtil;
import util.JavaFXUtil;
import util.ZipUtil;

public class PatchMaker extends Application {

	// /home/local/ZOHOCORP/srini-10093/Work/Workspace/me-am-switchable/bin/com/me/apm/xenapp/util/XenAppUtil.class
	// /home/local/ZOHOCORP/srini-10093/Work/Build/MMH_Dynamic/AppManager14
	private static StringBuilder copiedFiles = new StringBuilder();
	static CheckBox jspDirectory;
	static CheckBox silentRestart;
	static CheckBox zipNeeded;
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Patch Maker Application");
		
		 MenuItem patchMenuItem = new MenuItem("Patch Maker");
		 MenuItem dynamicBuildMenuItem = new MenuItem("Dynamic Build");
		 MenuItem aboutMenuItem = new MenuItem("About Us");
		 
	     VBox menuVBox = JavaFXUtil.getMenuVBox(patchMenuItem, dynamicBuildMenuItem, aboutMenuItem); 
		
	     // Default Scene - Patch Maker
	     Scene patchMakerScene = getPatchMakerScene(primaryStage, menuVBox);
	     primaryStage.setScene(patchMakerScene);
		
	     primaryStage.show();
		
	     patchMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Patch Maker Menu Item Called");
				Scene patchMakerScene = getPatchMakerScene(primaryStage, menuVBox);
				primaryStage.setScene(patchMakerScene);
			}
	     });
	     
	     dynamicBuildMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Dynamic Build Menu Item Called");
				Scene patchMakerScene = getDynamicBuildScene(primaryStage, menuVBox);
				primaryStage.setScene(patchMakerScene);
			}
		 });
	     
	     aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("About Us Menu Item Called");
				//Scene patchMakerScene = getAboutUsScene(primaryStage, menuVBox);
				//primaryStage.setScene(patchMakerScene);
			}
		 });
       
	}
	
	private Scene getPatchMakerScene(Stage primaryStage, VBox menuBarVBox) {
		 GridPane gridPane = JavaFXUtil.createFormPane();
		 gridPane.setVgap(20);
		 addUIControlsForPatchMaker(gridPane,primaryStage);	
		 VBox vbox = new VBox(menuBarVBox,gridPane);
		 return new Scene(vbox, 700, 250);
	}
	
	private Scene getDynamicBuildScene(Stage primaryStage, VBox menuBarVBox) {
		 GridPane gridPane = JavaFXUtil.createFormPane();
		 gridPane.setVgap(20);
		 addUIControlsForDynamicBuild(gridPane,primaryStage);	
		 VBox vbox = new VBox(menuBarVBox,gridPane);
		 return new Scene(vbox, 700, 200);
	}
	
	private void addUIControlsForDynamicBuild(GridPane gridPane,Stage primaryStage) {
		// Add Header
		final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load

		Image img = new Image("file.png");
	    ImageView view = new ImageView(img);
	    view.setFitHeight(20);
	    view.setPreserveRatio(true);
		// Add Name Label
		Label sourcePathLabel = new Label("JSP Zip File");
		gridPane.add(sourcePathLabel, 0,0);

		// Add Name Text Field
		TextField sourcePath = JavaFXUtil.generateTextField(20, "Choose the zip file");
		gridPane.add(sourcePath, 1,0);

		sourcePath.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
			if(newValue && firstTime.get()){
				gridPane.requestFocus(); // Delegate the focus to container
				firstTime.setValue(false); // Variable value changed for future references
			}
		});

		 //Image img = new Image("file.png");
		    ImageView view1 = new ImageView(img);
		    view1.setFitHeight(20);
		    view1.setPreserveRatio(true);
		//Source Choose Button
		Button srcChoose = JavaFXUtil.generateButton("", 20, 10, true);
		srcChoose.setGraphic(view1);
		gridPane.add(srcChoose, 2, 0);
		GridPane.setHalignment(srcChoose, HPos.CENTER);

		// Add Email Label
		Label buildPathLabel = new Label("Build Path");
		gridPane.add(buildPathLabel, 0, 1);

		// Add Email Text Field
		TextField buildPath = JavaFXUtil.generateTextField(20, "Choose AppManager directory");
		gridPane.add(buildPath, 1, 1);

		//Destination Choose Button
		Button desChoose = JavaFXUtil.generateButton("", 20, 10, true);
		desChoose.setGraphic(view);
		gridPane.add(desChoose, 2, 1);
		GridPane.setHalignment(desChoose, HPos.CENTER);

		// Generate Patch Button
		Button generateButton = JavaFXUtil.generateButton("Convert into Dynamic", 20, 250, true);
		
		HBox hbox = new HBox(generateButton);

		gridPane.add(hbox, 1, 2);
		hbox.setSpacing(30);
		hbox.setAlignment(Pos.BASELINE_CENTER);

		JavaFXUtil.setFileChooserAction(primaryStage, srcChoose, sourcePath, false, true);
		JavaFXUtil.setFileChooserAction(primaryStage, desChoose, buildPath, true, false);
		
		generateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(sourcePath.getText().isEmpty()) {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter zip file path");
					return;
				}
				if(buildPath.getText().isEmpty()) {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter build path");
					return;
				}
				String output= convertIntoDynamic(sourcePath.getText(), buildPath.getText());
				if(output!=null && output.contains("Successful")) {
					JavaFXUtil.showAlert(Alert.AlertType.INFORMATION, gridPane.getScene().getWindow(), "Message", "Successfully convered into Dynamic build");
				}else {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Error!", output);
				}
			}
		});
	}
	
	

	private void addUIControlsForPatchMaker(GridPane gridPane,Stage primaryStage) {
		// Add Header
		final BooleanProperty firstTime = new SimpleBooleanProperty(true); // Variable to store the focus on stage load

		Image img = new Image("file.png");
	    ImageView view = new ImageView(img);
	    view.setFitHeight(20);
	    view.setPreserveRatio(true);
	    //Check boxes
	    
	    jspDirectory = new CheckBox("Jsp Directory");
	    silentRestart = new CheckBox("Silent Restart");
	    zipNeeded = new CheckBox("Zip Needed");

	    
        HBox checkHBox = new HBox(jspDirectory,silentRestart,zipNeeded);
        checkHBox.setSpacing(20);
        checkHBox.setAlignment(Pos.BASELINE_LEFT);
	    gridPane.add(checkHBox, 1, 0);
	    
		// Add Name Label
		Label sourcePathLabel = new Label("Source Path");
		gridPane.add(sourcePathLabel, 0,1);

		// Add Name Text Field
		ComboBox<String> sourcePath = JavaFXUtil.generateComboBox(20, "Choose the class file", true);
		gridPane.add(sourcePath, 1,1);

		jspDirectory.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
			if(newValue && firstTime.get()){
				gridPane.requestFocus(); // Delegate the focus to container
				firstTime.setValue(false); // Variable value changed for future references
			}
		});

		 //Image img = new Image("file.png");
		    ImageView view1 = new ImageView(img);
		    view1.setFitHeight(20);
		    view1.setPreserveRatio(true);
		//Source Choose Button
		Button srcChoose = JavaFXUtil.generateButton("", 20, 10, true);
		srcChoose.setGraphic(view1);
		gridPane.add(srcChoose, 2, 1);
		GridPane.setHalignment(srcChoose, HPos.CENTER);

		// Add Email Label
		Label buildPathLabel = new Label("Build Path");
		gridPane.add(buildPathLabel, 0, 2);

		// Add Email Text Field
		ComboBox<String> buildPath = JavaFXUtil.generateComboBox(20, "Choose AppManager directory", true);
		gridPane.add(buildPath, 1, 2);

		//Destination Choose Button
		Button desChoose = JavaFXUtil.generateButton("", 20, 10, true);
		desChoose.setGraphic(view);
		gridPane.add(desChoose, 2, 2);
		GridPane.setHalignment(desChoose, HPos.CENTER);

		// Generate Patch Button
		Button generateButton = JavaFXUtil.generateButton("Generate Patch", 20, 150, true);

		// Apply Patch Button
		Button applyPatchButton = JavaFXUtil.generateButton("Apply Patch", 20, 150, true);
		
		// Apply Restart Button
		Button restartApplicationButton = JavaFXUtil.generateButton("Restart Application", 20, 150, true);
		HBox hbox = new HBox(generateButton, applyPatchButton,restartApplicationButton);

		gridPane.add(hbox, 1, 3);
		hbox.setSpacing(30);
		hbox.setAlignment(Pos.BASELINE_CENTER);

		JavaFXUtil.setFileChooserActionWithComboBox(primaryStage, srcChoose, sourcePath, false, false);
		JavaFXUtil.setFileChooserActionWithComboBox(primaryStage, desChoose, buildPath, true, false);
		
		generateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(sourcePath.getValue() == null || sourcePath.getValue().isEmpty()) {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter source file path");
					return;
				}
				if(buildPath.getValue() == null || buildPath.getValue().isEmpty()) {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter build path");
					return;
				}
				String output= generatePatch(sourcePath.getValue(), buildPath.getValue(), true);
				if(output!=null && output.contains("Successful")) {
					if(zipNeeded.isSelected()) {
						String zipPath = new File(buildPath.getValue()).getParentFile().getPath();
						ZipUtil.zipDirectory(zipPath+File.separator+"working", zipPath+File.separator+"working.zip");
					}
					JavaFXUtil.showAlertNew(Alert.AlertType.INFORMATION, gridPane.getScene().getWindow(), "Message", "Patch generated successfully!!!", copiedFiles.toString());
					copiedFiles = new StringBuilder();
				}else {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Error!", output);
				}

			}
		});

		applyPatchButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(sourcePath.getValue() == null || sourcePath.getValue().isEmpty()) {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter source file path");
					return;
				}
				if(buildPath.getValue() == null || buildPath.getValue().isEmpty()) {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter build path");
					return;
				}
				String output = generatePatch(sourcePath.getValue(), buildPath.getValue(), false);
				if(output!=null && output.contains("Successful")) {
					JavaFXUtil.showAlertNew(Alert.AlertType.INFORMATION, gridPane.getScene().getWindow(), "Message", "Patch applied successfully!!!", copiedFiles.toString());
					copiedFiles = new StringBuilder();
				}else {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Error!", output);
				}

			}
		});
		
		restartApplicationButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(buildPath.getValue() == null || buildPath.getValue().isEmpty()) {
					JavaFXUtil.showAlert(Alert.AlertType.ERROR, gridPane.getScene().getWindow(), "Form Error!", "Please enter build path");
					return;
				}
				String output = restartApplication(buildPath.getValue());
				if(output!=null) {
					JavaFXUtil.showAlert(Alert.AlertType.INFORMATION, gridPane.getScene().getWindow(), "Message", output);
				}
			}
		});
	}
	
	public static String convertIntoDynamic(String zipSourcePath, String buildPath) {
		String out="Successful";
		File buildPathFile = new File(buildPath);
		File file = new File(zipSourcePath);

		try {
			if(!buildPathFile.getName().toLowerCase().contains("appmanager")) {
				return "Please choose build path upto Appmanager";
			}
		}catch(Exception e) {
			return "Choose correct path";
		}
		try {
			if(!file.exists()){
				return "Zip file not found";
			}	
		}catch(Exception e) {
			return "Zip file not found";
		}
		try {
			File buildJSPPath = new File(buildPath+File.separator+"working"+File.separator+"jsp");
			if(buildJSPPath.exists()) {
				return "jsp folder already exists";
			}
		}catch(Exception e) {
			return e.getMessage();
		}
		DynamicBuildUtil.doEachTask(buildPath, zipSourcePath);
		return out;
	}
	
	public static String generatePatch(String sourcePathArr,String buildPath,boolean isGenerate) {
		String out="Successful";
		//working/classes/AdventNetAppManager.jar
		//working/WEB-INF/lib/AdventNetAppManagerWebClient.jar
		try {
			File buildPathFile = new File(buildPath);
			if(!buildPathFile.getName().toLowerCase().contains("appmanager")) {
				return "Please choose build path upto Appmanager";
			}
		}catch(Exception e) {
			return "Choose correct path";
		}
		//Special handling done for Ubuntu 20.04,it will remove the clipboard content if there is any
		if(sourcePathArr.contains("x-special/nautilus-clipboardcopy")) {
			sourcePathArr = sourcePathArr.replace("x-special/nautilus-clipboardcopy", "");
		}
		if(sourcePathArr.contains("file://")) {
			sourcePathArr = sourcePathArr.replace("file://", ",");
		}
		//Special handling ends
		String[] sourcePathSplitted = sourcePathArr.split(","); //AMSecurityUtil.class
		for(String sourcePath : sourcePathSplitted) {
			if(sourcePath.trim().isEmpty()) {
				continue;
			}
			try {
				ArrayList<String> workingClassPath = extractPath(buildPath+File.separator+"working"+File.separator+"classes"+File.separator+"AdventNetAppManager.jar",sourcePath);
				ArrayList<String> webinfPath = extractPath(buildPath+File.separator+"working"+File.separator+"WEB-INF"+File.separator+"lib"+File.separator+"AdventNetAppManagerWebClient.jar",sourcePath);
				
				if(!workingClassPath.isEmpty()) {
					for(String path : workingClassPath) {
						String[] arr1 = sourcePath.split(File.separator);
						String[] arr2 = path.split(File.separator);
						arr1[arr1.length-1] = arr2[arr2.length-1];
						String newSource = String.join(File.separator, arr1);
						if(isGenerate) {
							File parentFile = new File(buildPath);
							copyFilesToDestinations(newSource,parentFile.getParentFile().getPath(),"working"+File.separator+"classes",path);	
						}else {
							copyFilesToDestinations(newSource,buildPath,"working"+File.separator+"classes",path);	
						}
					}
				}
				if(!webinfPath.isEmpty()) {
					for(String path : webinfPath) {
						String[] arr1 = sourcePath.split(File.separator);
						String[] arr2 = path.split(File.separator);
						arr1[arr1.length-1] = arr2[arr2.length-1];
						String newSource = String.join(File.separator, arr1);
						if(isGenerate) {
							File parentFile = new File(buildPath);
							copyFilesToDestinations(newSource,parentFile.getParentFile().getPath(),"working"+File.separator+"WEB-INF"+File.separator+"classes",path);	
						}else {
							copyFilesToDestinations(newSource,buildPath,"working"+File.separator+"WEB-INF"+File.separator+"classes",path);	
						}
					}
				}
				///home/local/ZOHOCORP/srini-10093/Work/Workspace/me-am-switchable/bin/test/com/adventnet/adaptors/clients/AbstractJMXConnector.class
				// Generate JSP files directory
				if(jspDirectory.isSelected()) {
					String srcPath = buildPath;
					if(isGenerate) {
						File parentFile = new File(buildPath);
						srcPath = parentFile.getParentFile().getPath();
					}
					File jspDir = new File(srcPath+File.separator+"working"+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"org"+File.separator+"apache"+File.separator+"jsp"+File.separator+"jsp");
					System.out.println(jspDir.toString());
					if(!jspDir.exists()) {
						jspDir.mkdirs();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				out="Something went wrong!";
			}
		}
		return out;
	}
	public static String copyFilesToDestinations(String source,String destination,String orgin,String classPath) {
		File sourceFile = new File(source);
		File buildFile = new File(destination+File.separator+orgin+File.separator+classPath);
		File dir = new File(buildFile.getParent());
		dir.mkdirs();
		if(buildFile.exists()){
			buildFile.delete();
		}
		try {
			Files.copy(sourceFile.toPath(),buildFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File Copied to : "+buildFile.getPath());
		String pathStr = buildFile.getPath();
		copiedFiles.append(pathStr.substring(pathStr.indexOf("working"))+"\n");
		return buildFile.getPath();
	}

	public static ArrayList<String> extractPath(String path,String classFile) throws Exception {
		/* WARNING This copies same source into different file hence skipped this feature */
		File srcFile = new File(classFile);
		String srcFileName = srcFile.getName().replace(".class", ""); // AMSecurtiyUtil
		
		ArrayList<String> toReturn = new ArrayList<String>();
		ZipFile zipFile = new ZipFile(path);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while(entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			String filePath = zipEntry.getName();
			/* WARNING This copies same source into different file hence skipped this feature */
			File file = new File(filePath);
			String fileName = file.getName(); //AMSecurityUtil$1.class
			if(fileName.contains("$")) {
				int index = fileName.indexOf("$");
				String strBefore$ = fileName.substring(0, index);
				if(strBefore$.equals(srcFileName)) {
					toReturn.add(filePath);
				}
			}
			
			if(classFile.endsWith(filePath)) {
				toReturn.add(filePath);
			}
		}
		zipFile.close();
		return toReturn;
	}

	 private static String restartApplication(String path) {
	    	String out="starting..."; 
	    	try {
				File buildPathFile = new File(path);
				if(!buildPathFile.getName().toLowerCase().contains("appmanager")) {
					return "Please choose build path upto Appmanager";
				}
			}catch(Exception e) {
				return "Choose correct path";
			}
	    	try{
				Runtime run= Runtime.getRuntime();
				File s=new File(path);
				Process execute=run.exec("sh shutdownApplicationsManager.sh -force", null, s);
				execute.waitFor();
				if(silentRestart.isSelected()) {
					run.exec("sh startApplicationsManager.sh", null, s);
				}else {
					run.exec("gnome-terminal -- sh startApplicationsManager.sh", null, s);
				}
	    	}catch (Exception e) {
				out=e.getMessage();
			}
	      return out;
		}

}