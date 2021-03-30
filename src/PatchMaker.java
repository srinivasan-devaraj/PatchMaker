import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
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
	public static JSONArray overallJSON = null;
	StatusUpdaterThread thread = null;
	public static final long UPDATE_DELAY = 1 * 60 * 1000; // 1 Min
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void stop() throws Exception {
		thread.interrupt();
		super.stop();
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Patch Maker Application");

		MenuItem dashboardItem = new MenuItem("Dashboard");
		MenuItem patchMenuItem = new MenuItem("Patch Maker");
		MenuItem dynamicBuildMenuItem = new MenuItem("Dynamic Build");
		MenuItem aboutMenuItem = new MenuItem("About Us");

		VBox menuVBox = JavaFXUtil.getMenuVBox(dashboardItem, patchMenuItem, dynamicBuildMenuItem); 

		// Default Scene - Dashboard Scene
		Scene defaultScene = getDashboardScene(primaryStage, menuVBox);
		primaryStage.setScene(defaultScene);

		primaryStage.show();

		dashboardItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(thread != null) {
					thread.interrupt();
				}
				System.out.println("Dashboard Menu Item Called");
				Scene dashboardScene = getDashboardScene(primaryStage, menuVBox);
				primaryStage.setScene(dashboardScene);
			}
		});
		
		patchMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(thread != null) {
					thread.interrupt();
				}
				System.out.println("Patch Maker Menu Item Called");
				Scene patchMakerScene = getPatchMakerScene(primaryStage, menuVBox);
				primaryStage.setScene(patchMakerScene);
			}
		});

		dynamicBuildMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(thread != null) {
					thread.interrupt();
				}
				System.out.println("Dynamic Build Menu Item Called");
				Scene patchMakerScene = getDynamicBuildScene(primaryStage, menuVBox);
				primaryStage.setScene(patchMakerScene);
			}
		});

		aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(thread != null) {
					thread.interrupt();
				}
				System.out.println("About Us Menu Item Called");
				//Scene patchMakerScene = getAboutUsScene(primaryStage, menuVBox);
				//primaryStage.setScene(patchMakerScene);
			}
		});

	}

	private Scene getDashboardScene(Stage primaryStage, VBox menuBarVBox) {
		GridPane gridPane = JavaFXUtil.createFormPaneForOuterDash();
		primaryStage.setMaximized(true);
		gridPane.setVgap(20);
		if(overallJSON == null) {
			overallJSON = FindAllFile.getAllData();
		}
		addUIControlsForDashboard(gridPane);
		VBox vbox = new VBox(menuBarVBox,gridPane);
		vbox.setSpacing(5);
        int width = (int) Screen.getPrimary().getBounds().getWidth();
        int height = (int) Screen.getPrimary().getBounds().getHeight();

		return new Scene(vbox, width, height);
	}
	
	private Scene getPatchMakerScene(Stage primaryStage, VBox menuBarVBox) {
		primaryStage.setMaximized(false);
		GridPane gridPane = JavaFXUtil.createFormPane();
		gridPane.setVgap(20);
		addUIControlsForPatchMaker(gridPane,primaryStage);	
		VBox vbox = new VBox(menuBarVBox,gridPane);
		return new Scene(vbox, 700, 250);
	}

	private Scene getDynamicBuildScene(Stage primaryStage, VBox menuBarVBox) {
		primaryStage.setMaximized(false);
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


	public void addUIControlsForDashboard(GridPane gridPane) {
	
		/*
		HBox checkHBox = new HBox(jspDirectory);
		checkHBox.setSpacing(20);
		checkHBox.setAlignment(Pos.BASELINE_LEFT);
		gridPane.add(checkHBox, 1, 0);
		
		HBox checkHBox1 = new HBox(silentRestart);
		checkHBox1.setSpacing(20);
		checkHBox1.setAlignment(Pos.BASELINE_LEFT);
		gridPane.add(checkHBox1, 1, 1);
		*/
		GridPane gP = JavaFXUtil.createGridPaneForDash(HPos.LEFT);

		try {
			int size = overallJSON.length();
			for(int i=0;i<size;i++) {
				JSONObject row = overallJSON.getJSONObject(i);
				HBox rowHBox = getSingleRowForDashboard(i+1,row);
				GridPane.setHgrow(rowHBox, Priority.ALWAYS);
				gP.add(rowHBox, 1, i);
			}			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		scrollPane.setContent(gP);
		//scrollPane
		Label snoLb = JavaFXUtil.getLabel("S.No", 35);
		Label nameLb = JavaFXUtil.getLabel("Name", 245);
		Label versionLb = JavaFXUtil.getLabel("Version", 60);
		Label editionLb = JavaFXUtil.getLabel("Edition", 60);
		Label buildStatus = JavaFXUtil.getLabel("Status", 60);
		Label dbTypeLb = JavaFXUtil.getLabel("DB Type", 70);
		Label dynamicLb = JavaFXUtil.getLabel("Dynamic", 65);
		Label consoleLb = JavaFXUtil.getLabel("Console", 60);
		Label actionLb = JavaFXUtil.getLabel("Action", 60);
		Label buildPathLb = JavaFXUtil.getLabel("Path", 60);
		HBox header = new HBox(snoLb,nameLb,versionLb,editionLb,buildStatus,dbTypeLb,dynamicLb,buildPathLb,consoleLb,actionLb);
		header.setSpacing(20);
		header.setPadding(new Insets(20));
		header.setAlignment(Pos.CENTER_LEFT);
		header.setStyle("-fx-font-weight: bold");

		header.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null , null)));
		gridPane.add(header,1,0);
		gridPane.add(scrollPane, 1, 1);
		
		thread = new StatusUpdaterThread(gP);
		thread.start();
	}

	private HBox getSingleRowForDashboard(int i,JSONObject row) {
		String version = "-",name ="-",dbType="-",edition="-",host = "",port = "",buildPath = "";
		boolean status = false,isDynamic = false;
		
		try {
			version = row.getString("Version");
		} catch (Exception e) {
			//e.printStackTrace();
		}
		try {
			host = row.getString("Host");
			port = row.getString("Port");
			name = host+"_"+port;
		} catch (Exception e) {
			e.printStackTrace();
		}
		final String hostFinal = host,portFinal = port;
		try {
			dbType = row.getString("DBType").toUpperCase();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			edition = row.getString("Edition");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			buildPath = row.getString("BuildPath");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		final String url = "http://"+host+":"+port+"/",dir = buildPath;
		Label snoLb = JavaFXUtil.getLabel(""+i, 20);
		Label nameLb = JavaFXUtil.getLabel(name, 260);
		Label versionLb = JavaFXUtil.getLabel(version, 60);
		Label editionLb = JavaFXUtil.getLabel(edition, 60);
		Label dbTypeLb = JavaFXUtil.getLabel(dbType, 70);
		
		try {
			status = row.getBoolean("IsRunning");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			isDynamic = row.getBoolean("IsDynamic");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HBox imageHBox = new HBox(JavaFXUtil.getBuildStatusImage(status));
		imageHBox.setAlignment(Pos.CENTER);
		imageHBox.setMaxWidth(60);
		imageHBox.setMinWidth(60);
		
		HBox buildPathHBox = new HBox(JavaFXUtil.getImageView(30,"directory.png"));
		buildPathHBox.setAlignment(Pos.BOTTOM_CENTER);
		buildPathHBox.setMaxWidth(60);
		buildPathHBox.setMinWidth(60);
		
		HBox webConsoleHBox = new HBox(JavaFXUtil.getImageView(25,"web.png"));
		webConsoleHBox.setAlignment(Pos.BOTTOM_CENTER);
		webConsoleHBox.setMaxWidth(60);
		webConsoleHBox.setMinWidth(60);
		
		String actionImg = "start.png";
		if(status) {
			actionImg = "shutdown.png";
		}
		HBox actionHBox = new HBox(JavaFXUtil.getImageView(30,actionImg));
		actionHBox.setAlignment(Pos.BOTTOM_CENTER);
		actionHBox.setMaxWidth(60);
		actionHBox.setMinWidth(60);
		
		
		actionHBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent e) {
	        	
	        	boolean status = FindAllFile.pingHost(hostFinal, Integer.parseInt(portFinal), 10);
	        	
	        	ImageView view = (ImageView) actionHBox.getChildren().get(0);
	        	
	        	ImageView statusview = (ImageView) imageHBox.getChildren().get(0);
	        	
	        	 
	        	if(status) {
		        	view.setImage(new Image("start.png"));
	        		shutdownApplication(dir);
	        		statusview.setImage(new Image("down.gif"));
	        	}else {
		        	view.setImage(new Image("shutdown.png"));
	        		restartApplication(dir);
	        		statusview.setImage(new Image("up.gif"));
	        	}
	        	
	        }
	    });
		
		webConsoleHBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent e) {
	        	getHostServices().showDocument(url);
	        }
	    });
		
		buildPathHBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent e) {
				ProcessBuilder processBuilder = new ProcessBuilder();
				try {
				    processBuilder.command("bash", "-c", "nautilus "+dir);
					processBuilder.start();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
	        }
	    });
		
		HBox dynamicBox = new HBox();
		if(isDynamic) {
			dynamicBox.getChildren().add(JavaFXUtil.getImageView(25,"dynamic.gif"));
		}
		dynamicBox.setAlignment(Pos.BOTTOM_CENTER);
		dynamicBox.setMaxWidth(60);
		dynamicBox.setMinWidth(60);
		
		HBox hBox = new HBox(snoLb,nameLb,versionLb,editionLb,imageHBox,dbTypeLb,dynamicBox,buildPathHBox,webConsoleHBox,actionHBox);
		hBox.setSpacing(20);
		hBox.setPadding(new Insets(20));
		hBox.setAlignment(Pos.CENTER_LEFT);
	    hBox.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null , null)));

		return hBox;
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
		jspDirectory.setTooltip(new Tooltip("Creates directory for jsp_class files while Applying/Generating Patch"));
		silentRestart = new CheckBox("Silent Restart");
		silentRestart.setTooltip(new Tooltip("Restarts without opening Terminal"));
		zipNeeded = new CheckBox("Zip Needed");
		zipNeeded.setTooltip(new Tooltip("Creates zip file while generating Patch"));


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
		List<String> innerClassFiles = new ArrayList<String>();
		String newClassFilesPath = "";

		ArrayList<String> toReturn = new ArrayList<String>();
		ZipFile zipFile = new ZipFile(path);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while(entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			String filePath = zipEntry.getName();
			File file = new File(filePath);
			String fileName = file.getName(); //AMSecurityUtil$1.class
			if(fileName.contains("$")) {
				int index = fileName.indexOf("$");
				String strBefore$ = fileName.substring(0, index);
				if(strBefore$.equals(srcFileName)) {
					toReturn.add(filePath);
					innerClassFiles.add(fileName);
				}
			}
			if(classFile.endsWith(filePath)) {
				newClassFilesPath = file.getParent();
				toReturn.add(filePath);
			}
		}
		zipFile.close();

		// Adds new Inner Class files - Starts
		if(!toReturn.isEmpty()) {
			File srcFileDir = srcFile.getParentFile();
			File[] listOfFiles = srcFileDir.listFiles();
			for(File file : listOfFiles){
				if(file.isFile()) {
					String fileName = file.getName();
					if(fileName.contains("$")) {
						int index = fileName.indexOf("$");
						String strBefore$ = fileName.substring(0, index);
						if(strBefore$.equals(srcFileName) && !innerClassFiles.contains(fileName)) {
							toReturn.add(newClassFilesPath+File.separator+fileName);
						}
					}
				}
			}
		}
		// Adds new Inner Class files - Ends 
		return toReturn;
	}
	
	private static String shutdownApplication(String path) {
		String out = "";
		try{
			Runtime run= Runtime.getRuntime();
			File s=new File(path);
			run.exec("sh shutdownApplicationsManager.sh -force", null, s);
		}catch (Exception e) {
			out=e.getMessage();
		}
		return out;
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

