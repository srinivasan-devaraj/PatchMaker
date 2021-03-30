import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class StatusUpdaterThread extends Thread{
	
	GridPane gridPane;
	
	public StatusUpdaterThread(GridPane gridPane) {
		this.gridPane = gridPane;
	}

	@SuppressWarnings("static-access")
	public void run() {
		ObservableList<Node> childrens = gridPane.getChildren();
		try {
			while(true) {
				for(Node node : childrens) {
					try {
						HBox hbox = (HBox) node;
						ObservableList<Node> hBoxChildrens = hbox.getChildren();
						Label nameLB = (Label) hBoxChildrens.get(1);
						String[] name_port = nameLB.getText().split("_");
						boolean status = FindAllFile.pingHost(name_port[0], Integer.parseInt(name_port[1]), 30);
						// Status Image
						HBox imageHBox = (HBox) hBoxChildrens.get(4);
						ImageView statusImgView = (ImageView) imageHBox.getChildren().get(0);
						// Start/Stop Action Image
						HBox actionHBox = (HBox) hBoxChildrens.get(9);
						ImageView actionImgView = (ImageView) actionHBox.getChildren().get(0);
						if(status) {
							statusImgView.setImage(new Image("up.gif"));
							actionImgView.setImage(new Image("shutdown.png"));
						}else {
							statusImgView.setImage(new Image("down.gif"));
							actionImgView.setImage(new Image("start.png"));
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("Details Updated...Going to Sleeping... | Name : "+Thread.currentThread().getName());
				Thread.currentThread().sleep(PatchMaker.UPDATE_DELAY);
			}
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}
}
