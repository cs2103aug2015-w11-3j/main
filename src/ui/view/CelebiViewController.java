package ui.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import common.Task;
import common.TasksBag;
import common.TasksBag.FilterBy;
import ui.Main;
import ui.UIInterface;
import parser.Parser;

public class CelebiViewController {
	//@@author TODO
	Main mainApp;
	UIInterface ui;
	
    @FXML
    private TableView<Task> celebiTable;
    @FXML
    private TableColumn<Task, Number> idColumn;
    @FXML
    private TableColumn<Task, String> taskNameColumn;
    @FXML
    private TableColumn<Task, Date> startTimeColumn;
    @FXML
    private TableColumn<Task, Date> endTimeColumn;
    @FXML
    private TableColumn<Task, String> tagColumn;
    @FXML
    private TabPane statePane;
    /*
    @FXML
    private TableColumn<Task, Task.Type> firstPrepColumn;
    @FXML
    private TableColumn<Task, Task.Type> secondPrepColumn;
    */
    @FXML
    private AnchorPane commandFieldPane;
    @FXML
    private AnchorPane feedbackPane;
    
    private InlineCssTextArea commandArea;
    private InlineCssTextArea feedbackArea;
	
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	celebiTable.setFixedCellSize(26.2);
    	// Initialize the celebi table with the six columns.
    	idColumn.setSortable(false);
    	idColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<Number>(celebiTable.getItems().indexOf(column.getValue())+1));
        
    	taskNameColumn.setCellValueFactory(
                cellData -> cellData.getValue().nameProperty());
    	startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
    	initializeDateColumn(startTimeColumn);
    	endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().endProperty());
    	initializeDateColumn(endTimeColumn);
    	
    	/*
    	firstPrepColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    	initializeFirstPrepColumn(firstPrepColumn);
    	secondPrepColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    	initializeSecondPrepColumn(secondPrepColumn);
    	*/
    	
    	//celebiTable.setMouseTransparent(true);
    	
    	initializeCommandPane();
    	initializeCommandField();
    	initializeFeedbackPane();
    	initializeFeedbackArea();
    	
    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
                commandArea.requestFocus();
            }
        });
    }
    
    /**
     * Initialize the date column with text
     * @param column
     */
    private void initializeDateColumn(TableColumn<Task, Date> column) {
    	column.setCellFactory(col -> {
    		return new TableCell<Task, Date>(){
    	        @Override
    	        protected void updateItem(Date item, boolean empty) {
    	            super.updateItem(item, empty);

    	            if (item == null || empty) {
    	                setText(null);
    	                setStyle("");
    	            } else {
    	                // Format date.
    	            	String displayDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(item);
    	            	setText(displayDate);
    	            }
    	        }
    	    };
    	});
    }
    
    private void initializeFirstPrepColumn(TableColumn<Task, Task.Type> column) {
    	column.setCellFactory(col -> {
    		return new TableCell<Task, Task.Type>(){
    			@Override
    			protected void updateItem(Task.Type item, boolean empty) {
    				super.updateItem(item, empty);
    				
    				if (item == Task.Type.FLOATING) {
    					setText(null);
    				}
    				else if (item == Task.Type.NOEND) {
    					setText("from");
    				}
    				else if (item == Task.Type.DEADLINE) {
    					setText(null);
    				}
    				else if (item == Task.Type.EVENT) {
    					setText("from");
    				}
    			}
    		};
    	});
    }
    
    private void initializeSecondPrepColumn(TableColumn<Task, Task.Type> column) {
    	column.setCellFactory(col -> {
    		return new TableCell<Task, Task.Type>(){
    			@Override
    			protected void updateItem(Task.Type item, boolean empty) {
    				super.updateItem(item, empty);
    				
    				if (item == Task.Type.FLOATING) {
    					setText(null);
    				}
    				else if (item == Task.Type.NOEND) {
    					setText(null);
    				}
    				else if (item == Task.Type.DEADLINE) {
    					setText("due");
    				}
    				else if (item == Task.Type.EVENT) {
    					setText("to");
    				}
    			}
    		};
    	});
    }
    
    private void initializeCommandPane() {
    	commandArea = new InlineCssTextArea();
    	AnchorPane.setTopAnchor(commandArea, 5.0);
    	AnchorPane.setBottomAnchor(commandArea, 10.0);
    	AnchorPane.setLeftAnchor(commandArea, 50.0);
    	AnchorPane.setRightAnchor(commandArea, 50.0);
    	commandFieldPane.getChildren().add(commandArea);
    }
    
    /**
     * Initialize command field with enter action
     */
    private void initializeCommandField() {
    	commandArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
    	    @Override
    	    public void handle(KeyEvent keyEvent) {
    	        KeyCode code = keyEvent.getCode();
    	    	if (code == KeyCode.ENTER)  {
    	            // when enter is hit, pass the user input to UI
    	        	String text = commandArea.getText();
    	            ui.passCommand(text);
    	            commandArea.clear();
    	            keyEvent.consume();
    	        }
    	    }
    	});
    	
    	commandArea.textProperty().addListener((observable, oldValue, newValue) -> {
    		String firstWord;
    		int i = newValue.indexOf(' ');
    		if (i == -1) {
    			firstWord = newValue;
    		}
    		else {
    			firstWord = newValue.substring(0, i);
    		}
    		
    		// (yijin) see the isCmdToken method i made below.
    		/*
    		 boolean found = false;
    		 for (int j=0; j<commandKeywords.length; j++) {
    			if(firstWord.equals(commandKeywords[j])) {
    				found = true;
    			}
    		}*/
    		
    		if (isCmdToken(firstWord)) {
    			commandArea.setStyle(0, firstWord.length(), "-fx-font-weight: bold; -fx-fill: #529228;");
    			if (newValue.length() > firstWord.length()) {
    				commandArea.setStyle(firstWord.length() + 1, newValue.length(),"-fx-font-weight: normal;");
    			}
    		}
    		else {
    			commandArea.setStyle(0, newValue.length(),"-fx-font-weight: normal;");
    		}
    	});
    	
    	commandArea.requestFocus();
    	commandArea.setId("command-area");
    }

    //@@author A0131891E
    // Helped you link the highlighting check to my parser's token string array.
    // Next time if I change the accepted token strings, it will automatically reflect in the UI.
	private boolean isCmdToken(String firstWord) {
		assert(firstWord != null);
		firstWord = firstWord.toLowerCase();
		for (String[] tokens : Parser.TOKENS) {
			for (String token : tokens) {
				if (firstWord.equals(token)) {
					return true;
				}
			}
		}
		return false;
	}
    //@@author TODO
    
    private void initializeFeedbackPane() {
    	feedbackArea = new InlineCssTextArea();
    	feedbackArea.setWrapText(true);
    	AnchorPane.setTopAnchor(feedbackArea, 5.0);
    	AnchorPane.setBottomAnchor(feedbackArea, 0.0);
    	AnchorPane.setLeftAnchor(feedbackArea, 50.0);
    	AnchorPane.setRightAnchor(feedbackArea, 50.0);
    	feedbackPane.getChildren().add(feedbackArea);
    }
    
    /**
     * Initialize feedback area.
     */
    private void initializeFeedbackArea() {
    	// set the feedback area to be uneditable and set it to be always at the bottom
    	feedbackArea.setEditable(false);
    	feedbackArea.setId("feedback-area");
    	//feedbackA.textProperty().addListener((observable, oldValue, newValue) -> feedbackA.setScrollTop(Double.MIN_VALUE));
    }
	
	public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        System.out.println(ui == null);
        setTableItems(ui.getCelebiList());
    }
	
	public void setUI(UIInterface ui) {
		this.ui = ui;
	}
	
	public void setTableItems(ObservableList<Task> celebiList) {
		celebiTable.setItems(celebiList);
	}
	
	public void clearCommand() {
		commandArea.clear();
	}
	
	public void appendFeedback(String newFeedback) {
		if(feedbackArea.getText().equals("")) {
			feedbackArea.appendText(newFeedback);
			feedbackArea.setStyle(0, "-fx-fill: #7eb758;");
		}
		else {
			feedbackArea.appendText(newFeedback);
			feedbackArea.setStyle(0, "-fx-fill: black;");
			feedbackArea.setStyle(1, "-fx-fill: #7eb758;");
		}
	}
	
	public void clearFeedback() {
		feedbackArea.clear();;
	}
	
	public void refreshSelection(TasksBag bag) {
		SingleSelectionModel<Tab> selectionModel = statePane.getSelectionModel();
		FilterBy state = bag.getState();
		if (state == common.TasksBag.FilterBy.COMPLETE_TASKS) {
			selectionModel.select(2);
		}
		else if (state == common.TasksBag.FilterBy.INCOMPLETE_TASKS) {
			selectionModel.select(1);
		}
		else if (state == common.TasksBag.FilterBy.TODAY) {
			selectionModel.select(0);
		}
	}
}
