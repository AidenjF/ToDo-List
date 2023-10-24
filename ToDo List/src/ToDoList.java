
/**
 * Holds the methods that will be needed to run the ToDoList.java program allowing you to
 * keep an object in memory, load it up, and create a todo list for you to use
 * @author Aiden Foster
*/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ToDoList extends Application {

	private VBox pane;
	private GridPane buttonPane;
	private ListView<String> listView;
	private ObservableList<String> observableList;
	private ArrayList<String> list;
	private Button topButton = new Button("Top");
	private Button bottomButton = new Button("Bottom");
	private Button raiseButton = new Button("Raise");
	private Button lowerButton = new Button("Lower");
	private Button removeButton = new Button("Remove");
	private Button saveButton = new Button("Save current List");
	private Label enterLabel = new Label("Enter a new ToDo:");
	private TextField text;

	/**
	 * The main function that launches the GUI program
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * This is where we set up the stage and set it to show so the user can play the
	 * game
	 * 
	 * @throws Exception
	 */
	@Override
	public void start(Stage stage) throws Exception {
		list = new ArrayList<>();
		alert();
		layoutGUI();

		registerHandlers();

		Scene scene = new Scene(pane, 600, 400);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(event -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText("Click cancel to not save any changes");
			alert.setContentText("Click OK to save the current ToDo list");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				String fileName = "objects.ser";
				try {
					FileOutputStream bytesToDisk = new FileOutputStream(fileName);
					ObjectOutputStream outFile = new ObjectOutputStream(bytesToDisk);
					outFile.writeObject(list);
					outFile.close();
				} catch (IOException ioe) {
					System.out.println("Fail");
				}
			}
		});
	}

	/**
	 * This method creates the layout for the ToDo List. It creates the buttons
	 * textField, observable list, and sets up the layout for everything.
	 */
	private void layoutGUI() {
		pane = new VBox();
		buttonPane = new GridPane();
		observableList = FXCollections.observableArrayList(list);
		listView = new ListView<>();
		listView.setItems(observableList);
		text = new TextField();

		listView.getSelectionModel().select(0);

		setUpButtons();

		pane.setSpacing(10);
		pane.setPadding(new Insets(10, 10, 10, 10));

		pane.getChildren().addAll(enterLabel, text, saveButton, listView, buttonPane);
	}

	/*
	 * This is the method that handles the alert that is thrown at the beginning of
	 * the project being ran, it asks if you would like to read in a serialized
	 * object or not
	 */
	private void alert() throws IOException, ClassNotFoundException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText("Click cancel to start with an empty list");
		alert.setContentText("Click OK to read from a .ser file");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			System.out.println("read from serialized data file");
			try {
				String fileName = "objects.ser";
				FileInputStream rawBytes = new FileInputStream(fileName);
				ObjectInputStream inFile = new ObjectInputStream(rawBytes);
				list = (ArrayList<String>) inFile.readObject();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			System.out.println("Create an empty list");
		}
	}

	/**
	 * This method sets up the buttons in a grid pane so we can make sure they are
	 * spaced evenly and all look the same.
	 */
	private void setUpButtons() {
		buttonPane.add(topButton, 0, 0);
		buttonPane.add(bottomButton, 1, 0);
		buttonPane.add(raiseButton, 2, 0);
		buttonPane.add(lowerButton, 3, 0);
		buttonPane.add(removeButton, 4, 0);
		buttonPane.setAlignment(Pos.CENTER);
		buttonPane.setHgap(12);

		topButton.setPrefWidth(100);
		bottomButton.setPrefWidth(100);
		raiseButton.setPrefWidth(100);
		lowerButton.setPrefWidth(100);
		removeButton.setPrefWidth(100);
	}

	/**
	 * This method handles creating the action events so that we know when the
	 * buttons are clicked and what we need to do when they are clicked
	 */
	private void registerHandlers() {
		topButton.setOnAction(event -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index >= 0) {
				String item = observableList.get(index).toString();
				observableList.remove(index);
				observableList.add(0, item);

				list.remove(index);
				list.add(0, item);
				listView.getSelectionModel().select(0);
			}
		});

		bottomButton.setOnAction(event -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index >= 0) {
				String item = observableList.get(index).toString();
				observableList.remove(index);
				observableList.add(observableList.size(), item);
				list.remove(index);
				list.add(list.size(), item);
				listView.getSelectionModel().select(observableList.size() - 1);
			}
		});

		raiseButton.setOnAction(event -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index - 1 >= 0) {
				Collections.swap(observableList, index, index - 1);
				Collections.swap(list, index, index - 1);
				listView.getSelectionModel().select(index - 1);
			}
		});

		lowerButton.setOnAction(event -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index + 1 < observableList.size()) {
				Collections.swap(observableList, index, index + 1);
				Collections.swap(list, index, index + 1);
				listView.getSelectionModel().select(index + 1);
			}
		});

		removeButton.setOnAction(event -> {
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index >= 0) {
				observableList.remove(index);
				list.remove(index);
			}
		});

		saveButton.setOnAction(event -> {
			String fileName = "objects.ser";
			try {
				FileOutputStream bytesToDisk = new FileOutputStream(fileName);
				ObjectOutputStream outFile = new ObjectOutputStream(bytesToDisk);
				outFile.writeObject(list);
				outFile.close();
			} catch (IOException ioe) {
				System.out.println("Fail");
			}
		});

		text.setOnAction(event -> {
			String newItem = text.getText();
			if (!newItem.equals("")) {
				observableList.add(0, newItem);
				list.add(0, newItem);
				text.clear();
				listView.getSelectionModel().select(0);

			}
		});
	}
}
