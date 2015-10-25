package ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import common.Task;
import common.TasksBag;
import common.TasksBag.FilterBy;
import ui.Main;
import ui.UIInterface;

public class CelebiViewController {
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
    private TextArea feedbackArea;
    @FXML
    private TextField commandField;
	
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
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
    	
    	celebiTable.setMouseTransparent(true);
    	
    	initializeCommandField();
    	initializeFeedbackArea();
    	
    	Platform.runLater(new Runnable() {
            @Override
            public void run() {
                commandField.requestFocus();
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
    
    /**
     * Initialize command field with enter action
     */
    private void initializeCommandField() {
    	commandField.setOnKeyPressed(new EventHandler<KeyEvent>() {
    	    @Override
    	    public void handle(KeyEvent keyEvent) {
    	        if (keyEvent.getCode() == KeyCode.ENTER)  {
    	            // when enter is hit, pass the user input to UI
    	        	String text = commandField.getText();
    	            ui.passCommand(text);
    	            commandField.setText("");
    	        }
    	    }
    	});
    	commandField.requestFocus();
    }
    
    /**
     * Initialize feedback area.
     */
    private void initializeFeedbackArea() {
    	// set the feedback area to be uneditable and set it to be always at the bottom
    	feedbackArea.setEditable(false);
    	feedbackArea.textProperty().addListener((observable, oldValue, newValue) -> feedbackArea.setScrollTop(Double.MIN_VALUE));
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
	
	public void appendFeedback(String newFeedback) {
		feedbackArea.appendText(newFeedback);
	}
	
	public void clearFeedback() {
		feedbackArea.setText("");
	}
	
	public void refreshSelection(TasksBag bag) {
		System.out.printf("I'm here");
		SingleSelectionModel<Tab> selectionModel = statePane.getSelectionModel();
		FilterBy state = bag.getState();
		if (state == common.TasksBag.FilterBy.COMPLETE_TASKS) {
			System.out.printf("complete");
			selectionModel.select(1);
		}
		else if (state == common.TasksBag.FilterBy.INCOMPLETE_TASKS) {
			System.out.printf("incomplete");
			selectionModel.select(0);
		}
	}
}
