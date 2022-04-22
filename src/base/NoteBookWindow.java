package base;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import base.Folder;
import base.Note;
import base.NoteBook;
import base.TextNote;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * 
 * NoteBook GUI with JAVAFX
 * 
 * COMP 3021
 * 
 * 
 * @author valerio
 *
 */
public class NoteBookWindow extends Application {

	/**
	 * TextArea containing the note
	 */
	final TextArea textAreaNote = new TextArea("");
	/**
	 * list view showing the titles of the current folder
	 */
	final ListView<String> titleslistView = new ListView<String>();
	/**
	 * 
	 * Combobox for selecting the folder
	 * 
	 */
	final ComboBox<String> foldersComboBox = new ComboBox<String>();
	/**
	 * This is our Notebook object
	 */
	NoteBook noteBook = null;
	/**
	 * current folder selected by the user
	 */
	String currentFolder = "";
	/**
	 * current search string
	 */
	String currentSearch = "";
	String currentNote = "";
	
	Stage stage;

	public static void main(String[] args) {
		launch(NoteBookWindow.class, args);
	}

	@Override
	public void start(Stage stage) {
		loadNoteBook();
		this.stage = stage;

		reloadScene();
		stage.setTitle("NoteBook COMP 3021");
		stage.show();
	}

	private void reloadScene() {
		currentFolder = "";
		currentSearch = "";
		
		BorderPane border = new BorderPane();

		border.setTop(addHBox());
		border.setLeft(addVBox());
		border.setCenter(addGridPane());

		Scene scene = new Scene(border);
		stage.setScene(scene);
	}
	
	/**
	 * This create the top section
	 * 
	 * @return
	 */
	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes

		Button buttonLoad = new Button("Load");
		buttonLoad.setPrefSize(100, 20);
		buttonLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please Choose a file which contains a notebook object!");
				
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
				fileChooser.getExtensionFilters().add(extFilter);
				
				File file = fileChooser.showOpenDialog(stage);
				
				if(file != null) {
					loadNoteBook(file);
					reloadScene();
				}
			}
		});
		
		Button buttonSave = new Button("Save");
		buttonSave.setPrefSize(100, 20);
		buttonSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Please Choose the file location to save!");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Serialized Object File (*.ser)", "*.ser"));
				File file = fileChooser.showSaveDialog(stage);
				
				if(file != null) {
					noteBook.save(file.getAbsolutePath());
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Successfullly saved");
					alert.setContentText("Your file has been saved to file " + file.getName());
					alert.showAndWait().ifPresent(rs -> {
						if(rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						} 
					});
				}
			}
		});
		
		Label labelSearch = new Label("Search : ");
		TextField textSearch = new TextField();
		textSearch.setPrefSize(200, 20);
		Button buttonSearch = new Button("Search");
		buttonSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				currentSearch = textSearch.getText();
				textAreaNote.setText("");
				showSearchListView(currentSearch);
			}
		});
		Button buttonClear = new Button("Clear Search");
		buttonClear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				currentSearch = "";
				textSearch.setText("");
				textAreaNote.setText("");
				updateListView();
			}
		});
		

		hbox.getChildren().addAll(buttonLoad, buttonSave, labelSearch, textSearch, buttonSearch, buttonClear);

		return hbox;
	}

	/**
	 * this create the section on the left
	 * 
	 * @return
	 */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8); // Gap between nodes
		
		foldersComboBox.getItems().clear();
		for(Folder folder: noteBook.getFolders()) {
			foldersComboBox.getItems().add(folder.getName());
		}
		foldersComboBox.setValue("");
		foldersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if(t1 != null) {
					currentFolder = t1.toString();
				}
				// this contains the name of the folder selected
				// TODO update listview
				updateListView();
			}
		});
		
		Button buttonAddFolder = new Button("Add a Folder");
		buttonAddFolder.setPrefSize(100, 20);
		buttonAddFolder.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TextInputDialog dialog = new TextInputDialog("New Folder");
				dialog.setTitle("Input");
				dialog.setHeaderText("Add a new folder for your notebook:");
				dialog.setContentText("Please enter the name you want to create:");
				
				Optional<String> result = dialog.showAndWait();
				if(result.isPresent()) {
					
					if(result.get().equals("")) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setContentText("Please input a valid folder name");
						alert.showAndWait().ifPresent(rs -> {
							if(rs == ButtonType.OK) {
								System.out.println("Pressed OK.");
							} 
						});
						return;
					}
					
					for(Folder f: noteBook.getFolders()) {
						if(result.get().equals(f.getName())) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("Warning");
							alert.setContentText("Please input a valid folder name");
							alert.showAndWait().ifPresent(rs -> {
								if(rs == ButtonType.OK) {
									System.out.println("Pressed OK.");
								} 
							});
							return;
						}
					}
					
					noteBook.addFolder(result.get());
					reloadScene();
				}
			}
		});

		titleslistView.setPrefHeight(100);

		titleslistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 == null)
					return;
				String title = t1.toString();
				// This is the selected title

				Folder selected = null;
				String content = "";
				for(Folder folder: noteBook.getFolders()) {
					if(folder.getName() == currentFolder) {
						selected = folder;
						break;
					}
				}
				if(selected != null) {
					for(Note note: selected.getNotes()) {
						if(note.getTitle() == title && note instanceof TextNote) {
							currentNote = note.getTitle();
							content = ((TextNote)note).getContent();
						}
					}
				}
				
				textAreaNote.setText(content);

			}
		});
		
		Button buttonAddNote = new Button("Add a Note");
		buttonAddNote.setPrefSize(100, 20);
		buttonAddNote.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(currentFolder == "") {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning");
					alert.setContentText("Please choose a folder first!");
					alert.showAndWait().ifPresent(rs -> {
						if(rs == ButtonType.OK) {
							System.out.println("Pressed OK.");
						} 
					});
					return;
				}
				
				TextInputDialog dialog = new TextInputDialog("New Note");
				dialog.setTitle("Input");
				dialog.setHeaderText("Add a new note to current folder");
				dialog.setContentText("Please enter the name of your note:");
				
				Optional<String> result = dialog.showAndWait();
				if(result.isPresent()) {
					if(result.get().equals("")) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setContentText("Please input a valid note name");
						alert.showAndWait().ifPresent(rs -> {
							if(rs == ButtonType.OK) {
								System.out.println("Pressed OK.");
							} 
						});
						return;
					}
					
					noteBook.createTextNote(currentFolder, result.get(), "");
					reloadScene();
				}
			}
		});
		
		vbox.getChildren().add(new Label("Choose folder: "));
		
		HBox HBoxFolder = new HBox();
		HBoxFolder.setSpacing(10);
		HBoxFolder.getChildren().add(foldersComboBox);
		HBoxFolder.getChildren().add(buttonAddFolder);
		
		vbox.getChildren().add(HBoxFolder);
		vbox.getChildren().add(new Label("Choose note title"));
		vbox.getChildren().add(titleslistView);
		vbox.getChildren().add(buttonAddNote);

		return vbox;
	}

	private void updateListView() {
		ArrayList<String> list = new ArrayList<String>();

		Folder selected = null;
		for(Folder folder: noteBook.getFolders()) {
			if(folder.getName() == currentFolder) {
				selected = folder;
				break;
			}
		}
		if(selected != null) {
			for(Note note: selected.getNotes()) {
				if(note instanceof TextNote) {
					list.add(note.getTitle());
				}
			}
		}

		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		titleslistView.setItems(combox2);
		currentNote = "";
		textAreaNote.setText("");
	}
	
	private void showSearchListView(String keyword) {
		ArrayList<String> list = new ArrayList<String>();

		Folder selected = null;
		for(Folder folder: noteBook.getFolders()) {
			if(folder.getName() == currentFolder) {
				selected = folder;
				break;
			}
		}
		if(selected != null) {
			for(Note note: selected.searchNotes(keyword)) {
				if(note instanceof TextNote) {
					list.add(note.getTitle());
				}
			}
		}
		
		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		titleslistView.setItems(combox2);
		currentNote = "";
		textAreaNote.setText("");
	}

	/*
	 * Creates a grid for the center region with four columns and three rows
	 */
	private GridPane addGridPane() {
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		try {
			HBox hBox = new HBox();
			hBox.setSpacing(10);
			
			Image imageSave = new Image(new FileInputStream("save.png"));
			ImageView imageViewSave = new ImageView(imageSave);
			imageViewSave.setFitHeight(20);
			imageViewSave.setFitWidth(20);
			
			Button buttonSaveNote = new Button("Save Note");
			buttonSaveNote.setPrefSize(100, 20);
			buttonSaveNote.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if(currentFolder.equals("") || currentNote.equals("")) {

						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setContentText("Please select a folder and a note");
						alert.showAndWait().ifPresent(rs -> {
							if(rs == ButtonType.OK) {
								System.out.println("Pressed OK.");
							} 
						});
						return;
					}
					
					for(Folder f: noteBook.getFolders()) {
						if(f.getName().equals(currentFolder)) {
							for(Note n: f.getNotes()) {
								if(n.getTitle().equals(currentNote)) {
									if(n instanceof TextNote) {
										((TextNote) n).setContent(textAreaNote.getText());
									}
								}
							}
						}
					}
				}
			});
			
			Image imageDelete = new Image(new FileInputStream("delete.png"));
			ImageView imageViewDelete = new ImageView(imageDelete);
			imageViewDelete.setFitHeight(20);
			imageViewDelete.setFitWidth(20);
			
			Button buttonDeleteNote = new Button("Delete Note");
			buttonDeleteNote.setPrefSize(100, 20);
			buttonDeleteNote.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if(currentFolder.equals("") || currentNote.equals("")) {

						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setContentText("Please select a folder and a note");
						alert.showAndWait().ifPresent(rs -> {
							if(rs == ButtonType.OK) {
								System.out.println("Pressed OK.");
							} 
						});
						return;
					}
					
					for(Folder f: noteBook.getFolders()) {
						if(f.getName().equals(currentFolder)) {
							if(f.removeNotes(currentNote)) {
								reloadScene();
							}
						}
					}
					
				}
			});
			
			hBox.getChildren().add(imageViewSave);
			hBox.getChildren().add(buttonSaveNote);
			hBox.getChildren().add(imageViewDelete);
			hBox.getChildren().add(buttonDeleteNote);

			grid.add(hBox, 0, 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		textAreaNote.setEditable(true);
		textAreaNote.setMaxSize(450, 400);
		textAreaNote.setWrapText(true);
		textAreaNote.setPrefWidth(450);
		textAreaNote.setPrefHeight(400);
		// 0 0 is the position in the grid
		grid.add(textAreaNote, 0, 1);

		return grid;
	}
	
	private void loadNoteBook(File file) {
		noteBook = new NoteBook(file.getAbsolutePath());
	}

	private void loadNoteBook() {
		NoteBook nb = new NoteBook();
		nb.createTextNote("COMP3021", "COMP3021 syllabus", "Be able to implement object-oriented concepts in Java.");
		nb.createTextNote("COMP3021", "course information",
				"Introduction to Java Programming. Fundamentals include language syntax, object-oriented programming, inheritance, interface, polymorphism, exception handling, multithreading and lambdas.");
		nb.createTextNote("COMP3021", "Lab requirement",
				"Each lab has 2 credits, 1 for attendence and the other is based the completeness of your lab.");

		nb.createTextNote("Books", "The Throwback Special: A Novel",
				"Here is the absorbing story of twenty-two men who gather every fall to painstakingly reenact what ESPN called â€œthe most shocking play in NFL historyâ€� and the Washington Redskins dubbed the â€œThrowback Specialâ€�: the November 1985 play in which the Redskinsâ€™ Joe Theismann had his leg horribly broken by Lawrence Taylor of the New York Giants live on Monday Night Football. With wit and great empathy, Chris Bachelder introduces us to Charles, a psychologist whose expertise is in high demand; George, a garrulous public librarian; Fat Michael, envied and despised by the others for being exquisitely fit; Jeff, a recently divorced man who has become a theorist of marriage; and many more. Over the course of a weekend, the men reveal their secret hopes, fears, and passions as they choose roles, spend a long night of the soul preparing for the play, and finally enact their bizarre ritual for what may be the last time. Along the way, mishaps, misunderstandings, and grievances pile up, and the comforting traditions holding the group together threaten to give way. The Throwback Special is a moving and comic tale filled with pitch-perfect observations about manhood, marriage, middle age, and the rituals we all enact as part of being alive.");
		nb.createTextNote("Books", "Another Brooklyn: A Novel",
				"The acclaimed New York Times bestselling and National Book Awardâ€“winning author of Brown Girl Dreaming delivers her first adult novel in twenty years. Running into a long-ago friend sets memory from the 1970s in motion for August, transporting her to a time and a place where friendship was everythingâ€”until it wasnâ€™t. For August and her girls, sharing confidences as they ambled through neighborhood streets, Brooklyn was a place where they believed that they were beautiful, talented, brilliantâ€”a part of a future that belonged to them. But beneath the hopeful veneer, there was another Brooklyn, a dangerous place where grown men reached for innocent girls in dark hallways, where ghosts haunted the night, where mothers disappeared. A world where madness was just a sunset away and fathers found hope in religion. Like Louise Meriwetherâ€™s Daddy Was a Number Runner and Dorothy Allisonâ€™s Bastard Out of Carolina, Jacqueline Woodsonâ€™s Another Brooklyn heartbreakingly illuminates the formative time when childhood gives way to adulthoodâ€”the promise and peril of growing upâ€”and exquisitely renders a powerful, indelible, and fleeting friendship that united four young lives.");

		nb.createTextNote("Holiday", "Vietnam",
				"What I should Bring? When I should go? Ask Romina if she wants to come");
		nb.createTextNote("Holiday", "Los Angeles", "Peter said he wants to go next Agugust");
		nb.createTextNote("Holiday", "Christmas", "Possible destinations : Home, New York or Rome");
		noteBook = nb;
	}

}
