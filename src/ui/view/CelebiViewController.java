// @@author A0133895U
/**
 * This is the controller for the main view.
 */
package ui.view;

import java.util.Date;

import org.fxmisc.richtext.InlineCssTextArea;

import common.Task;
import common.TasksBag;
import common.TasksBag.FilterDateState;
import common.TasksBag.ViewType;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import common.Configuration;
import common.Task;
import common.TasksBag;
import common.TasksBag.ViewType;
import common.Utilities;
import common.TasksBag.FilterDateState;
import ui.Main;
import ui.UIInterface;
import parser.Aliases;
import parser.AliasesImpl;
import parser.HelpStrings;

public class CelebiViewController {

    Main mainApp;
    UIInterface ui;
    Stage stage;
    private static final DateFormatter df = new DateFormatter();
    private static final Aliases ALIASES = AliasesImpl.getInstance();

    // UI elements created from FXML
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
    @FXML
    private ImageView logo;

    // UI elements that are not created directly from FXML
    private InlineCssTextArea commandArea;
    private InlineCssTextArea feedbackArea;
    private LabelFader popupFader;
    private Label tablePlaceHolder;

    // Colors used
    private static final String DAY_CELEBI_COLOR = "#7eb758";
    private static final String NIGHT_CELEBI_COLOR = "#16a085";
    private static final String DAY_USER_COLOR = "#000000";
    private static final String NIGHT_USER_COLOR = "#ecf0f1";
    private static final String DAY_WARNING_COLOR = "#c74122";
    private static final String NIGHT_WARNING_COLOR = "#ca2f5b";
    private static final String DAY_KEYWORD_COLOR = "#529228";
    private static final String NIGHT_KEYWORD_COLOR = "#1abc9c";

    // Colors used
    private static final Color DAY_NORMAL_TASK_COLOR = Color.rgb(86, 87, 85);
    private static final Color DAY_COMPLETED_TASK_COLOR = Color.rgb(82, 146, 40);
    private static final Color DAY_OVERDUE_TASK_COLOR = Color.rgb(158, 158, 156);
    private static final Color NIGHT_NORMAL_TASK_COLOR = Color.rgb(236, 240, 241);
    private static final Color NIGHT_COMPLETED_TASK_COLOR = Color.rgb(22, 160, 133);
    private static final Color NIGHT_OVERDUE_TASK_COLOR = Color.rgb(158, 158, 156);

    // Load the necessary files
    private static final Font DEFAULT_FONT = Font.loadFont(Main.class.getResourceAsStream("resource/Oxygen regular.ttf"), 13);
    private static final Image DAY_LOGO = new Image(Main.class.getResourceAsStream("resource/Celebi logo_day.png"));
    private static final Image NIGHT_LOGO = new Image(Main.class.getResourceAsStream("resource/Celebi logo_night.png"));
    
    private static final String DATE_FILTER_NONE = "none";
    private static final String SEARCH_FILTER_NONE = "none";

    // Enum for skin
    public static enum Skin {
        DAY, NIGHT
    }

    private Skin skinMode = Skin.DAY;
    private String currentCelebiColor = DAY_CELEBI_COLOR;
    private String currentUserColor = DAY_USER_COLOR;
    private String currentWarningColor = DAY_WARNING_COLOR;
    private String currentKeywordColor = DAY_KEYWORD_COLOR;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void initialize() {
        initializeCelebiTable();
        initializeTableColumns();
        initializeCommandPane();
        initializeCommandField();
        initializeFeedbackPane();
        initializeFeedbackArea();
        initializePopupLabel();

        Platform.runLater(() -> {
            commandArea.requestFocus();
        });
    }

    /**
     * Initialize the pop up label with fading effect
     */
    private void initializePopupLabel() {
        popupFader = new LabelFader(popupLabel);
    }

    private void initializeCelebiTable() {
    	setTablePlaceHolder();
    	disableTableColumnReordering();
    	initializeRowPseudoclassListeners();
    }

	private void setTablePlaceHolder() {
		tablePlaceHolder = new Label("Come and add something here!\nEnter a question mark if you just met me ;D");
		// change the font color according to the current skin
		switch (skinMode) {
        	case DAY:
        		tablePlaceHolder.setTextFill(DAY_COMPLETED_TASK_COLOR);
        		break;
        	case NIGHT:
        		tablePlaceHolder.setTextFill(NIGHT_COMPLETED_TASK_COLOR);
        		break;
		}
		celebiTable.setPlaceholder(tablePlaceHolder);
	}

    private void disableTableColumnReordering() {
        TableColumn[] columns = { spaceColumn, idColumn, taskNameColumn, startTimeColumn, endTimeColumn };
        
        // force the tableview to display columns in a fixed order. Once changed, resume
        // at once
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

    /**
     * Initialize two pseudo class listeners to table rows to represent overdue tasks and
     * complete tasks
     */
    private void initializeRowPseudoclassListeners() {
        PseudoClass overdue = PseudoClass.getPseudoClass("overdue");
        PseudoClass complete = PseudoClass.getPseudoClass("complete");
        addRowPseudoClassListeners(overdue, complete);
    }

    private void addRowPseudoClassListeners(PseudoClass overdue, PseudoClass complete) {
        celebiTable.setRowFactory(tableview -> {
            TableRow<Task> row = new TableRow<>();

            // Listen to the end time of task and check if it's overdue
            ChangeListener<Date> endChangeListener = (observable, oldEndDate, newEndDate) -> {
                row.pseudoClassStateChanged(overdue, newEndDate.before(new Date()));
            };
            
            // Listen to the complete status of the task
            ChangeListener<Boolean> completeChangeListener = (observable, oldIsComplete, newIsComplete) -> {
                row.pseudoClassStateChanged(complete, newIsComplete);
            };

            // Add listener to each row. Every row can be under at most one pseudo class.
            row.itemProperty().addListener((observable, previousTask, currentTask) -> {
                // Remove the previous listener if there is any.
            	if (previousTask != null) {
                    previousTask.endProperty().removeListener(endChangeListener);
                    previousTask.isCompletedProperty().removeListener(completeChangeListener);
                }

            	// Reset the new listener if the row is not empty.
                if (currentTask != null) {
                    currentTask.endProperty().addListener(endChangeListener);
                    currentTask.isCompletedProperty().addListener(completeChangeListener);

                    // Complete has higher priority than Overdue. If the task is
                    // completed, set its row to be under class Complete but not Overdue.
                    if (currentTask.isCompleted()) {
                        row.pseudoClassStateChanged(complete, true);
                        row.pseudoClassStateChanged(overdue, false);
                    }
                    // Else check if the row is Overdue.
                    else if (currentTask.getEnd() != null) {
                        row.pseudoClassStateChanged(complete, false);
                        row.pseudoClassStateChanged(overdue, currentTask.getEnd().before(new Date()));
                    } 
                    else {
                        row.pseudoClassStateChanged(complete, false);
                        row.pseudoClassStateChanged(overdue, false);
                    }
                }
                // Else if the row is empty, it belongs to neither of the classes
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
     * initialize id column to display 1,2,3,...till number of tasks
     */
    private void initializeIdColumn() {
        idColumn.setCellValueFactory(
                column -> new ReadOnlyObjectWrapper<Number>(celebiTable.getItems().indexOf(column.getValue()) + 1));
    }

    /**
     * initialize task name column to display the name field of cell data
     */
    private void initializeTaskNameColumn() {
        taskNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        setTaskNameStyle();
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
     * initialize task end time column to display the end field of cell data
     */
    private void initializeEndTimeColumn() {
        endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().endProperty());
        // format the date displayed
        initializeDateColumn(endTimeColumn);
    }

    /**
     * Set the style and appearance of task name displayed
     */
    private void setTaskNameStyle() {
        taskNameColumn.setCellFactory(col -> {
            return new TableCell<Task, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    Text nameText = new Text();
                    // Get the task of this row
                    Task task = (Task) getTableRow().getItem();
                    if (task != null) {
                    	// Set the content of row to be nameText
                    	setGraphic(nameText);
                    	setColorForTaskName(nameText, task);
                        nameText.setFont(DEFAULT_FONT);
                        setPrefHeight(26.2);
                        // wrap the task name
                        nameText.wrappingWidthProperty().bind(taskNameColumn.widthProperty().subtract(15));
                        nameText.textProperty().bind(itemProperty());
                    }
                }
            };
        });
    }

    /**
     * Set the color for task name according to the current skin
     * @param nameText
     * @param task
     */
    private void setColorForTaskName(Text nameText, Task task) {
        switch (skinMode) {
            case DAY:
                if (task.isCompleted()) {
                    nameText.setFill(DAY_COMPLETED_TASK_COLOR);
                } else if (task.isOverDue()) {
                    nameText.setFill(DAY_OVERDUE_TASK_COLOR);
                } else {
                    nameText.setFill(DAY_NORMAL_TASK_COLOR);
                }
                break;
            case NIGHT:
                if (task.isCompleted()) {
                    nameText.setFill(NIGHT_COMPLETED_TASK_COLOR);
                } else if (task.isOverDue()) {
                    nameText.setFill(NIGHT_OVERDUE_TASK_COLOR);
                } else {
                    nameText.setFill(NIGHT_NORMAL_TASK_COLOR);
                }
                break;
        }
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
                
                /**
                 * Set the date display format
                 * @param item
                 * @param empty
                 */
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
        
        // set the position of command area inside the pane
        AnchorPane.setTopAnchor(commandArea, 5.0);
        AnchorPane.setBottomAnchor(commandArea, 13.0);
        AnchorPane.setLeftAnchor(commandArea, 50.0);
        AnchorPane.setRightAnchor(commandArea, 50.0);
        commandFieldPane.getChildren().add(commandArea);
    }

    /**
     * Initialize command field with enter action and keyword highlighting and
     * scroll bar disabler checker
     */
    private void initializeCommandField() {
        commandArea.requestFocus();
        commandArea.setId("command-area");

        setPressedEvent();
        setKeywordHighlightChecker();
        setCommandScrollBarDisabler();
    }

    /**
     * Disable the scroll bar when it appears
     */
    private void setCommandScrollBarDisabler() {
        commandArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (commandArea.lookup(".scroll-bar") != null) {
                ScrollBar scrollBarv = (ScrollBar) commandArea.lookup(".scroll-bar");
                scrollBarv.setDisable(false);
                scrollBarv.setId("command-scroll-bar");
            }
        });
    }

    /**
     * Check the keyword to be highlighted in command area and in tooltip
     */
    private void setKeywordHighlightChecker() {
        commandArea.textProperty().addListener((observable, oldValue, newValue) -> {
            String firstWord;
            firstWord = extractFirstWord(newValue);
            checkToolTip(firstWord);
            
            // catches commandArea resetting when enter is pressed.
            if ("".equals(firstWord)) {
            	return; 
            }
            
            if (ALIASES.isCmdAlias(firstWord)) {
                // highlight the first word
                commandArea.setStyle(0, firstWord.length(),
                        "-fx-font-weight: bold; -fx-fill: " + currentKeywordColor + ";");
                // leave the rest of the command unhighlighted
                if (newValue.length() > firstWord.length()) {
                    commandArea.setStyle(firstWord.length() + 1, newValue.length(),
                            "-fx-font-weight: normal; -fx-fill: " + currentUserColor + ";");
                }
            }
            // leave the command unhighlighted if no keyword presents
            else {
                commandArea.setStyle(0, newValue.length(),
                        "-fx-font-weight: normal; -fx-fill: " + currentUserColor + ";");
            }
        });
    }

    //@@author A0125546E
    /**
     * To check if the first word is a keyword that activates tool tip
     * @param firstWord
     */
    private void checkToolTip(String firstWord) {
    	if (firstWord == null) {
    		return; // catches command input field clearing change
    	}
        // show the tool-tip
        String toolTip = HelpStrings.getHelpToolTip(firstWord);
        if (toolTip == null) {
            //popupLabel.setText("");
            popupFader.fadeOut();
        } else {
            
            //toolTip = Utilities.textSpacer(toolTip, 1);
            popupLabel.setText(toolTip);
            popupFader.fadeIn();
        }
    }

    //@@author A0133895U
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
     * Detects enter pressed event and tab pressed event for command area
     */
    private void setPressedEvent() {
        commandArea.setOnKeyPressed((keyEvent) -> {
            KeyCode code = keyEvent.getCode();

            // The command is passed to UI whenever enter is hit
            if (code == KeyCode.ENTER) {
                String text = commandArea.getText();
                ui.passCommand(text);
                commandArea.clear();
                // consume the new line left in the command area
                keyEvent.consume();
            }

            // Tab event is sent to UI whenever tab is hit
            if (code == KeyCode.TAB) {
                ui.passKeyEvent(code);
                // consume the tab space left in the command area
                keyEvent.consume();
            }
        });
    }

    private void initializeFeedbackPane() {
        // add feedback area into the pane
        feedbackArea = new InlineCssTextArea();
        feedbackArea.setWrapText(true);
        
        // set the position of feedback area inside the pane
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
        // set the feedback area to be uneditable
        feedbackArea.setEditable(false);
        feedbackArea.setId("feedback-area");
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
    public void showFeedback(String newFeedback) {
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

    public void showWarning(String newWarning) {
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
    private void refreshSelection(TasksBag bag) {
        SingleSelectionModel<Tab> selectionModel = statePane.getSelectionModel();
        ViewType state = bag.getView();
        switch (state) {
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

    /**
     * Update the text displayed on filter label.
     * @param bag
     */
    private void updateFilterDisplay(TasksBag bag) {
        String MESSAGE_FILTER = "Now filtering: %1$s. ";
        String MESSAGE_SEARCH = "Now searching: %1$s.";
    	
    	String displayString = "";
        String dateFilterString = getDateFilterString(bag.getDateState(), bag.getStartDate(), bag.getEndDate());
        String searchKeywordString = getSearchKeywordString(bag);

        // if the string is empty, don't show the filter message
        if (dateFilterString == DATE_FILTER_NONE) {

        } 
        else {
            displayString = String.format(MESSAGE_FILTER, dateFilterString);
        }

        // if the string is empty, don't show the search message
        if (searchKeywordString == SEARCH_FILTER_NONE) {

        } 
        else {
            displayString = String.format(MESSAGE_SEARCH, searchKeywordString);
        }

        filterLabel.setText(displayString);
    }

    /**
     * Format the filter display
     * @param state
     * @param start
     * @param end
     * @return
     */
    public String getDateFilterString(FilterDateState state, Date start, Date end) {
        String MESSAGE_NONE = DATE_FILTER_NONE;
        String MESSAGE_AFTER = "after %1$s";
        String MESSAGE_BEFORE = "before %1$s";
        String MESSAGE_BETWEEN = "from %1$s to %2$s";

        String formattedStart = df.formatDate(start);
        String formattedEnd = df.formatDate(end);
        String dateFilterString = DATE_FILTER_NONE;
        
        // format according to the current filter state
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
            keyword = SEARCH_FILTER_NONE;
        }
        return keyword;
    }

    public void switchNightSkin() {
    	skinMode = Skin.NIGHT;
    	changeToNightCSS();
        changeToNightColor();
        logo.setImage(NIGHT_LOGO);
    }
    
    public void switchDaySkin() {
        changeToDayCSS();
        skinMode = Skin.DAY;
        changeToDayColor();
        logo.setImage(DAY_LOGO);
    }

	private void changeToNightCSS() {
		String css = Main.class.getResource("resource/skinNight.css").toExternalForm();
        rootPane.getStylesheets().clear();
        rootPane.getStylesheets().add(css);
	}

	/**
	 * Change the colors to be night mode
	 */
	private void changeToNightColor() {
		currentCelebiColor = NIGHT_CELEBI_COLOR;
        currentUserColor = NIGHT_USER_COLOR;
        currentWarningColor = NIGHT_WARNING_COLOR;
        currentKeywordColor = NIGHT_KEYWORD_COLOR;
        
        // change the color of task name column and table place holder
        setTaskNameStyle();
        setTablePlaceHolder();
	}

	/**
	 * Change the colors to be day mode
	 */
	private void changeToDayColor() {
		currentCelebiColor = DAY_CELEBI_COLOR;
        currentUserColor = DAY_USER_COLOR;
        currentWarningColor = DAY_WARNING_COLOR;
        currentKeywordColor = DAY_KEYWORD_COLOR;
        
        // change the color of task name column and table place holder
        setTaskNameStyle();
        setTablePlaceHolder();
	}

	private void changeToDayCSS() {
		String css = Main.class.getResource("resource/skinDay.css").toExternalForm();
        rootPane.getStylesheets().clear();
        rootPane.getStylesheets().add(css);
	}
    
	/**
	 * Helper data class to keep track of mouse dragging delta
	 * @author yucca
	 *
	 */
    class Delta {double x, y;}
    
    /**
     * Make the window draggable when dragging the customized window bar
     * @param stage
     * @param windowBar
     */
    public void makeDraggable(Stage stage, ToolBar windowBar) {
    	Delta dragDelta = new Delta();
    	
    	// Set the mouse events
    	windowBar.setOnMousePressed(new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent mouseEvent) {
    			windowBar.setCursor(Cursor.MOVE);
    			dragDelta.x = stage.getX() - mouseEvent.getScreenX();
    			dragDelta.y = stage.getY() - mouseEvent.getScreenY();
    		}
    	});
    	
    	windowBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
    		@Override
    		public void handle(MouseEvent mouseEvent) {
    			stage.setX(mouseEvent.getScreenX() + dragDelta.x);
    			stage.setY(mouseEvent.getScreenY() + dragDelta.y);
    		}
    	});
    }
    
    /**
     * Set the layout of window bar
     */
    public void setWindowBar() {
		ToolBar bar = new ToolBar(new WindowButtons(stage));
		
		int height = 25;
		bar.setPrefHeight(height);
		bar.setMinHeight(height);
		bar.setMaxHeight(height);
		bar.setId("window-bar");
		makeDraggable(stage, bar);
		
		// set the position of command area inside the pane
		AnchorPane.setTopAnchor(bar, 0.0);
        AnchorPane.setLeftAnchor(bar, 0.0);
        AnchorPane.setRightAnchor(bar, 0.0);
        rootPane.getChildren().add(bar);
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
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    /**
     * Called by UI class to update the UI view
     * @param cb
     */
    public void updateUI(TasksBag cb) {
    	refreshSelection(cb);
        updateFilterDisplay(cb);
        updateTableItems(cb.getList());
    }

    /**
     * Called by GUI test to get the current skin
     * @return
     */
    public Skin getSkin() {
    	return skinMode;
    }
}