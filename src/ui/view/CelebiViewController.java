package ui.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import common.Task;
import common.TasksBag;
import common.TasksBag.FilterBy;
import common.TasksBag.FilterDateState;
import ui.Main;
import ui.UIInterface;
import parser.Aliases;

public class CelebiViewController {
	//@@author A0133895U
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
    private TabPane statePane;
    @FXML
    private AnchorPane commandFieldPane;
    @FXML
    private AnchorPane feedbackPane;
    @FXML
    private Label filterLabel;
    
    private InlineCssTextArea commandArea;
    private InlineCssTextArea feedbackArea;
	private static final String[][] VALID_CMD_TOKENS = Aliases.getValidCmdTokens();
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	initializeCelebiTable();
    	initializeTableColumns();
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

	private void initializeCelebiTable() {
		celebiTable.setFixedCellSize(26.2);
		
		PseudoClass overdue = PseudoClass.getPseudoClass("overdue");
		celebiTable.setRowFactory(tableview -> {
			TableRow<Task> row = new TableRow<>();
			ChangeListener<Date> changeListener = (observable, oldEndDate, newEndDate) -> {
				row.pseudoClassStateChanged(overdue, newEndDate.before(new Date()));
			};
			row.itemProperty().addListener((observable, previousTask, currentTask) -> {
				if (previousTask != null) {
					previousTask.endProperty().removeListener(changeListener);
				}
				if (currentTask != null) {
					currentTask.endProperty().addListener(changeListener);
					if (currentTask.getEnd() != null) {
						row.pseudoClassStateChanged(overdue, currentTask.getEnd().before(new Date()));
					}
					else {
						row.pseudoClassStateChanged(overdue, false);
					}
				}
				else {
					row.pseudoClassStateChanged(overdue, false);
				}
			});
			return row;
		});
	}

	/**
	 * Initialize the table columns by setting the field that each column uses
	 */
	private void initializeTableColumns() {
		initializeIdColumn();
		initializeTaskNameColumn();
    	initializeStartTimeColumn();
    	initializeEndTimeColumn();
	}

	/**
	 * initialize task end time column to display the end field of cell data
	 */
	private void initializeEndTimeColumn() {
		endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().endProperty());
		// format the date displayed
		initializeDateColumn(endTimeColumn);
	}

	/**
	 * initialize task start time column to display the start field of cell data
	 */
	private void initializeStartTimeColumn() {
		startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
    	// format the date displayed
		initializeDateColumn(startTimeColumn);
	}

	/**
	 * initialize task name column to display the name field of cell data
	 */
	private void initializeTaskNameColumn() {
		taskNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
	}

	/**
	 * initialize id column to display 1,2,3,...till number of tasks
	 */
	private void initializeIdColumn() {
		idColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<Number>(
				celebiTable.getItems().indexOf(column.getValue())+1));
	}
    
    /**
     * Initialize the date column with formatted date
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
  
    private void initializeCommandPane() {
    	// add command area into the pane
    	commandArea = new InlineCssTextArea();
    	AnchorPane.setTopAnchor(commandArea, 5.0);
    	AnchorPane.setBottomAnchor(commandArea, 10.0);
    	AnchorPane.setLeftAnchor(commandArea, 50.0);
    	AnchorPane.setRightAnchor(commandArea, 50.0);
    	commandFieldPane.getChildren().add(commandArea);
    }
    
    /**
     * Initialize command field with enter action and keyword highlighting checker
     */
    private void initializeCommandField() {
    	commandArea.requestFocus();
    	commandArea.setId("command-area");
    	
    	setEnterPressedEvent();
    	setKeywordHighlightChecker();
    }

	private void setKeywordHighlightChecker() {
		commandArea.textProperty().addListener((observable, oldValue, newValue) -> {
    		String firstWord;
    		firstWord = extractFirstWord(newValue);  		
    		if (isCmdToken(firstWord)) {
    			// highlight the first word
    			commandArea.setStyle(0, firstWord.length(), "-fx-font-weight: bold; -fx-fill: #529228;");
    			// leave the rest of the command unhighlighted
    			if (newValue.length() > firstWord.length()) {
    				commandArea.setStyle(firstWord.length() + 1, newValue.length(),"-fx-font-weight: normal;");
    			}
    		}
    		// leave the command unhighlighted
    		else {
    			commandArea.setStyle(0, newValue.length(),"-fx-font-weight: normal;");
    		}
    	});
	}

	/**
	 * Extract the first word from a string
	 * @param string
	 * @return first word
	 */
	private String extractFirstWord(String string) {
		String firstWord;
		int i = string.indexOf(' ');
		if (i == -1) {
			firstWord = string;
		}
		else {
			firstWord = string.substring(0, i);
		}
		return firstWord;
	}

	/**
	 * Add in enter pressed event for command area so that it passes the command to UI everytime enter is hit
	 */
	private void setEnterPressedEvent() {
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
	}

    //@@author A0131891E
    // Helped you link the highlighting check to my parser's token string array.
    // Next time if I change the accepted token strings, it will automatically reflect in the UI.
	private boolean isCmdToken(String firstWord) {
		assert(firstWord != null);
		firstWord = firstWord.toLowerCase();
		for (String[] tokens : VALID_CMD_TOKENS) {
			for (String token : tokens) {
				if (firstWord.equals(token)) {
					return true;
				}
			}
		}
		return false;
	}
    
	//@@author A0133895U
    private void initializeFeedbackPane() {
    	// add feedback area into the pane
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
        updateTableItems(ui.getCelebiList());
    }
	
	public void setUI(UIInterface ui) {
		this.ui = ui;
	}
	
	public void updateTableItems(ObservableList<Task> celebiList) {
		celebiTable.setItems(celebiList);
	}
	
	public void clearCommand() {
		commandArea.clear();
	}
	
	/**
	 * Append feedback to the feedback area
	 * @param newFeedback
	 */
	public void appendFeedback(String newFeedback) {
		// if the text to be appended is the only line in feedback area, set its color green
		if(feedbackArea.getText().equals("")) {
			feedbackArea.appendText(newFeedback);
			feedbackArea.setStyle(0, "-fx-fill: #7eb758;");
		}
		// else make the first line in feedback area black, the rest green
		else {
			feedbackArea.appendText(newFeedback);
			feedbackArea.setStyle(0, "-fx-fill: black;");
			feedbackArea.setStyle(1, "-fx-fill: #7eb758;");
		}
	}
	
	public void clearFeedback() {
		feedbackArea.clear();;
	}
	
	/**
	 * Refresh the tab selection according to the current filter state
	 * @param bag
	 */
	public void refreshSelection(TasksBag bag) {
		SingleSelectionModel<Tab> selectionModel = statePane.getSelectionModel();
		FilterBy state = bag.getState();
		if (state == common.TasksBag.FilterBy.TODAY) {
			selectionModel.select(0);
		}
		else if (state == common.TasksBag.FilterBy.INCOMPLETE_TASKS) {
			selectionModel.select(1);
		}
		else if (state == common.TasksBag.FilterBy.COMPLETE_TASKS) {
			selectionModel.select(2);
		}
	}
	
	public void updateFilterDisplay(TasksBag bag) {
		String dateFilterString = getDateFilterString(bag);
		String searchKeywordString = getSearchKeywordString(bag);
		String displayString = "Now filtering: " + dateFilterString + ".   Now searching: " + searchKeywordString + ".";
		filterLabel.setText(displayString);
	}
	
	public String getDateFilterString(TasksBag bag) {
		String MESSAGE_NONE = "none";
		String MESSAGE_AFTER = "after %1$s";
		String MESSAGE_BEFORE = "before %1$s";
		String MESSAGE_BETWEEN = "from %1$s to %2$s";
		
		FilterDateState state = bag.getDateState();
		Date start = bag.getStartDate();
		Date end = bag.getEndDate();
		String dateFilterString = "none";
		switch(state) {
			case NONE:
				dateFilterString = MESSAGE_NONE;
				break;
			case AFTER:
				dateFilterString = String.format(MESSAGE_AFTER, start);
				break;
			case BEFORE:
				dateFilterString = String.format(MESSAGE_BEFORE, end);
				break;
			case BETWEEN:
				dateFilterString = String.format(MESSAGE_BETWEEN, start, end);
				break;
			default:
				break;
		}
		return dateFilterString;
	}
	
	public String getSearchKeywordString(TasksBag bag) {
		String keyword = bag.getSearchState();
		if (keyword == null || keyword.equals("")) {
			keyword = "none";
		}
		return keyword;
	}
}