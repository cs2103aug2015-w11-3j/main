package ui.view;

import java.util.Date;
import java.util.Map;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import common.Configuration;
import common.Task;
import common.TasksBag;
import common.TasksBag.ViewType;
import common.TasksBag.FilterDateState;
import ui.Main;
import ui.UIInterface;
import parser.Aliases;
import parser.Command;
import parser.HelpStrings;

public class CelebiViewController {
    // @@author A0133895U
    Main mainApp;
    UIInterface ui;
    final DateFormatter df = new DateFormatter();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Task> celebiTable;
    @FXML
    private TableColumn<Task, String> spaceColumn;
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
    @FXML
    private Label popupLabel;

    private InlineCssTextArea commandArea;
    private InlineCssTextArea feedbackArea;
    
    private Map<String, Command.Type> VALID_CMD_TOKENS;

    private static final String DAY_CELEBI_COLOR = "#7eb758";
    private static final String NIGHT_CELEBI_COLOR = "#16a085";
    private static final String DAY_USER_COLOR = "#000000";
    private static final String NIGHT_USER_COLOR = "#ecf0f1";
    private static final String DAY_WARNING_COLOR = "#c74122";
    private static final String NIGHT_WARNING_COLOR = "#ca2f5b";
    private static final String DAY_KEYWORD_COLOR = "#529228";
    private static final String NIGHT_KEYWORD_COLOR = "#1abc9c";
    
    private static final Color DAY_NORMAL_TASK_COLOR = Color.rgb(86, 87, 85);
    private static final Color DAY_COMPLETED_TASK_COLOR = Color.rgb(82, 146, 40);
    private static final Color DAY_OVERDUE_TASK_COLOR = Color.rgb(158, 158, 156);
    private static final Color NIGHT_NORMAL_TASK_COLOR = Color.rgb(236, 240, 241);
    private static final Color NIGHT_COMPLETED_TASK_COLOR = Color.rgb(22, 160, 133);
    private static final Color NIGHT_OVERDUE_TASK_COLOR = Color.rgb(158, 158, 156);
    
    private static final Font DEFAULT_FONT = Font.font("Oxygen", 13);

    public static enum Skin {
        DAY, NIGHT
    }

    private Skin skinMode = Skin.DAY;
    private String currentCelebiColor = DAY_CELEBI_COLOR;
    private String currentUserColor = DAY_USER_COLOR;
    private String currentWarningColor = DAY_WARNING_COLOR;
    private String currentKeywordColor = DAY_KEYWORD_COLOR;

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

        VALID_CMD_TOKENS = Aliases.getInstance().getAliasMap();
        
        Platform.runLater(() -> {
            commandArea.requestFocus();
        });
    }

    // private void temp(ObservableV)
    private void initializeCelebiTable() {
        //celebiTable.setCellSize(26.2);
        initializeRowPseudoclassListeners();
        disableTableColumnReordering();
    }

	private void disableTableColumnReordering() {
		TableColumn[] columns = { spaceColumn, idColumn, taskNameColumn, startTimeColumn, endTimeColumn };
        celebiTable.getColumns().addListener(new ListChangeListener<TableColumn>() {
            public boolean reordered = false;

            @Override
            public void onChanged(Change change) {
                change.next();
                if (change.wasReplaced() && !reordered) {
                    reordered = true;
                    celebiTable.getColumns().setAll(columns);
                    reordered = false;
                }
            }
        });
	}

	private void initializeRowPseudoclassListeners() {
		PseudoClass overdue = PseudoClass.getPseudoClass("overdue");
        PseudoClass complete = PseudoClass.getPseudoClass("complete");
        addRowPseudoClassListeners(overdue, complete);
	}

	private void addRowPseudoClassListeners(PseudoClass overdue, PseudoClass complete) {
		celebiTable.setRowFactory(tableview -> {
            TableRow<Task> row = new TableRow<>();

            ChangeListener<Date> endChangeListener = (observable, oldEndDate, newEndDate) -> {
                row.pseudoClassStateChanged(overdue, newEndDate.before(new Date()));
            };
            ChangeListener<Boolean> completeChangeListener = (observable, oldIsComplete, newIsComplete) -> {
                row.pseudoClassStateChanged(complete, newIsComplete);
            };

            row.itemProperty().addListener((observable, previousTask, currentTask) -> {
                if (previousTask != null) {
                    previousTask.endProperty().removeListener(endChangeListener);
                    previousTask.isCompletedProperty().removeListener(completeChangeListener);
                }

                if (currentTask != null) {
                    currentTask.endProperty().addListener(endChangeListener);
                    currentTask.isCompletedProperty().addListener(completeChangeListener);

                    if (currentTask.isCompleted()) {
                    	row.pseudoClassStateChanged(complete, true);
                    	row.pseudoClassStateChanged(overdue, false);
                    }
                    else if (currentTask.getEnd() != null) {
                    	row.pseudoClassStateChanged(complete, false);
                    	row.pseudoClassStateChanged(overdue, currentTask.getEnd().before(new Date()));
                    } 
                    else {
                    	row.pseudoClassStateChanged(complete, false);
                    	row.pseudoClassStateChanged(overdue, false);
                    }

                } 
                else {
                	row.pseudoClassStateChanged(complete, false);
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
    	setTaskNameAppearence();
    }

	private void setTaskNameAppearence() {
		taskNameColumn.setCellFactory(col -> {
    		return new TableCell<Task, String>() {
    			@Override
    			protected void updateItem(String item, boolean empty) {
    				super.updateItem(item, empty);
    				Text nameText = new Text();
    				Task task = (Task)getTableRow().getItem();
    				if (task != null) {
    					setColorForTaskName(nameText, task);
    					nameText.setFont(DEFAULT_FONT);
    					setGraphic(nameText);
        				setPrefHeight(26.2);
        				// wrap the task name
        				nameText.wrappingWidthProperty().bind(taskNameColumn.widthProperty().subtract(15));
        				nameText.textProperty().bind(itemProperty());
    				}
    			}
    		};
        	//cellData.getValue().nameProperty();	
        });
	}
	
	private void setColorForTaskName(Text nameText, Task task) {
		switch(skinMode) {
			case DAY:
				if (task.isCompleted()) {
					nameText.setFill(DAY_COMPLETED_TASK_COLOR);
				}
				else if (task.isOverDue()) {
					nameText.setFill(DAY_OVERDUE_TASK_COLOR);
				}
				else {
					nameText.setFill(DAY_NORMAL_TASK_COLOR);
				}
				break;
			case NIGHT:
				if (task.isCompleted()) {
					nameText.setFill(NIGHT_COMPLETED_TASK_COLOR);
				}
				else if (task.isOverDue()) {
					nameText.setFill(NIGHT_OVERDUE_TASK_COLOR);
				}
				else {
					nameText.setFill(NIGHT_NORMAL_TASK_COLOR);
				}
		}
	}

    /**
     * initialize id column to display 1,2,3,...till number of tasks
     */
    private void initializeIdColumn() {
    	idColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<Number>(celebiTable.getItems().indexOf(column.getValue()) + 1));
    }

    /**
     * Initialize the date column with formatted date
     * 
     * @param column
     */
    private void initializeDateColumn(TableColumn<Task, Date> column) {
        column.setCellFactory(col -> {
            return new TableCell<Task, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    setDateDisplay(item, empty);
                }

				private void setDateDisplay(Date item, boolean empty) {
					if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        // Format date.
                        setText(df.formatDate(item));
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
     * Initialize command field with enter action and keyword highlighting and scroll bar disabler
     * checker
     */
    private void initializeCommandField() {
        commandArea.requestFocus();
        commandArea.setId("command-area");

        setPressedEvent();
        setKeywordHighlightChecker();
        setCommandScrollBarDisabler();
    }

    private void setTabPressedEvent() {
        commandArea.setOnKeyPressed((keyEvent) -> {
            KeyCode code = keyEvent.getCode();

            // take only tab event. Else would interact with other key presses
            
        });
    }

    /**
     * Disable the scroll bar when it appears
     */
    private void setCommandScrollBarDisabler() {
    	commandArea.textProperty().addListener((observable, oldValue, newValue) -> {
    		if (commandArea.lookup(".scroll-bar") != null) {
    			ScrollBar scrollBarv = (ScrollBar)commandArea.lookup(".scroll-bar");
    			scrollBarv.setDisable(false);
    			scrollBarv.setId("command-scroll-bar");
    		}
    	});
    }
    
    private void setKeywordHighlightChecker() {
        commandArea.textProperty().addListener((observable, oldValue, newValue) -> {
            String firstWord;
            firstWord = extractFirstWord(newValue);
            checkToolTip(firstWord);
            if (isCmdToken(firstWord)) {
                // highlight the first word
                commandArea.setStyle(0, firstWord.length(),
                        "-fx-font-weight: bold; -fx-fill: " + currentKeywordColor + ";");
                // leave the rest of the command unhighlighted
                if (newValue.length() > firstWord.length()) {
                    commandArea.setStyle(firstWord.length() + 1, newValue.length(),
                            "-fx-font-weight: normal; -fx-fill: " + currentUserColor + ";");
                }
            }
            // leave the command unhighlighted
            else {
                commandArea.setStyle(0, newValue.length(),
                        "-fx-font-weight: normal; -fx-fill: " + currentUserColor + ";");
            }
        });
    }

    private void checkToolTip(String firstWord) {
        // show the tool-tip
        String toolTip = HelpStrings.getHelpToolTip(firstWord);
        if(toolTip == null){
            popupLabel.setVisible(false);
            popupLabel.setText("");
        }
        else{
            popupLabel.setVisible(true);
            popupLabel.setText(toolTip);
        }
    }

    /**
     * Extract the first word from a string
     * 
     * @param string
     * @return first word
     */
    private String extractFirstWord(String string) {
        String firstWord;
        int i = string.indexOf(' ');
        if (i == -1) {
            firstWord = string;
        } else {
            firstWord = string.substring(0, i);
        }
        return firstWord;
    }

    /**
     * Add in enter pressed event for command area so that it passes the command
     * to UI everytime enter is hit
     */
    private void setPressedEvent() {
        commandArea.setOnKeyPressed((keyEvent) -> {
            KeyCode code = keyEvent.getCode();
            
            if (code == KeyCode.ENTER) {
                // when enter is hit, pass the user input to UI
                String text = commandArea.getText();
                ui.passCommand(text);
                commandArea.clear();
                keyEvent.consume();
            }
            
            if (code == KeyCode.TAB) {
                ui.passKeyEvent(code);
                keyEvent.consume();
            }
        });

        /*
         * new EventHandler<KeyEvent>() {
         * 
         * @Override public void handle(KeyEvent keyEvent) { KeyCode code =
         * keyEvent.getCode(); if (code == KeyCode.ENTER) { // when enter is
         * hit, pass the user input to UI String text = commandArea.getText();
         * ui.passCommand(text); commandArea.clear(); keyEvent.consume(); } }
         * });
         */
    }

    // @@author A0131891E
    // Helped you link the highlighting check to my parser's token string array.
    // Next time if I change the accepted token strings, it will automatically
    // reflect in the UI.
    private boolean isCmdToken(String firstWord) {
        assert (firstWord != null);
        firstWord = firstWord.toLowerCase();
        return Configuration.getInstance().isUserAlias(firstWord) || VALID_CMD_TOKENS.containsKey(firstWord);
    }

    // @@author A0133895U
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
        // set the feedback area to be uneditable and set it to be always at the
        // bottom
        feedbackArea.setEditable(false);
        feedbackArea.setId("feedback-area");
        // feedbackA.textProperty().addListener((observable, oldValue, newValue)
        // -> feedbackA.setScrollTop(Double.MIN_VALUE));
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
        celebiTable.getColumns().get(0).setVisible(false);
        celebiTable.getColumns().get(0).setVisible(true);
    }

    public void clearCommand() {
        commandArea.clear();
    }

    /**
     * Append feedback to the feedback area
     * 
     * @param newFeedback
     */
    public void appendFeedback(String newFeedback) {
        // if the text to be appended is the only line in feedback area, set its
        // color green
        if (feedbackArea.getText().equals("")) {
            feedbackArea.appendText(newFeedback);
            feedbackArea.setStyle(0, "-fx-fill: " + currentCelebiColor + ";");
        }
        // else make the first line in feedback area black, the rest green
        else {
            feedbackArea.appendText(newFeedback);
            feedbackArea.setStyle(0, "-fx-fill: " + currentUserColor + ";");
            feedbackArea.setStyle(1, "-fx-fill: " + currentCelebiColor + ";");
        }
    }
    
    public void appendWarning(String newWarning) {
    	feedbackArea.appendText("\n");
    	feedbackArea.appendText(newWarning);
    	feedbackArea.setStyle(2, "-fx-fill: " + currentWarningColor + ";");
    }

    public void clearFeedback() {
        feedbackArea.clear();
    }

    /**
     * Refresh the tab selection according to the current filter state
     * 
     * @param bag
     */
    public void refreshSelection(TasksBag bag) {
        SingleSelectionModel<Tab> selectionModel = statePane.getSelectionModel();
        ViewType state = bag.getView();
        switch(state) {
        case DEFAULT:
        	selectionModel.select(0);
        	break;
        case INCOMPLETE:
        	selectionModel.select(1);
        	break;
        case COMPLETED:
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
        String formattedStart = df.formatDate(start);
        String formattedEnd = df.formatDate(end);

        String dateFilterString = "none";
        switch (state) {
            case NONE:
                dateFilterString = MESSAGE_NONE;
                break;
            case AFTER:
                dateFilterString = String.format(MESSAGE_AFTER, formattedStart);
                break;
            case BEFORE:
                dateFilterString = String.format(MESSAGE_BEFORE, formattedEnd);
                break;
            case BETWEEN:
                dateFilterString = String.format(MESSAGE_BETWEEN, formattedStart, formattedEnd);
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

    public void switchNightSkin() {
        String css = Main.class.getResource("resource/skinNight.css").toExternalForm();
        rootPane.getStylesheets().clear();
        rootPane.getStylesheets().add(css);
        skinMode = Skin.NIGHT;
        currentCelebiColor = NIGHT_CELEBI_COLOR;
        currentUserColor = NIGHT_USER_COLOR;
        currentWarningColor = NIGHT_WARNING_COLOR;
        currentKeywordColor = NIGHT_KEYWORD_COLOR;
        setTaskNameAppearence();
    }

    public void switchDaySkin() {
        String css = Main.class.getResource("resource/skinDay.css").toExternalForm();
        rootPane.getStylesheets().clear();
        rootPane.getStylesheets().add(css);
        skinMode = Skin.DAY;
        currentCelebiColor = DAY_CELEBI_COLOR;
        currentUserColor = DAY_USER_COLOR;
        currentWarningColor = DAY_WARNING_COLOR;
        currentKeywordColor = DAY_KEYWORD_COLOR;
        setTaskNameAppearence();
    }
}