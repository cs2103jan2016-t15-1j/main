package lifetracker.UI;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.util.Callback;
import lifetracker.LifeTracker;
import lifetracker.logic.ExecuteResult;
import lifetracker.logic.Logic;
import lifetracker.logic.LogicEvent;
import lifetracker.logic.LogicTask;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

//@@author A0114240B
public class UIController implements Initializable {
    private static final String PATH_README_CSS = "/lifetracker/UI/README.css";
    private static final String PATH_README_HTML = "/lifetracker/UI/README.html";
    private static final String TEXT_EMPTY = "";
    private static final String FIELD_DAY = "day(s)";
    private static final String FIELD_MONTH = "month(s)";
    private static final String FIELD_YEAR = "year(s)";
    private static final String FIELD_MINUTE = "minute(s)";
    private static final String FIELD_HOUR = "hour(s)";
    private static final PseudoClass PSEUDO_CLASS_OVERDUE = PseudoClass.getPseudoClass("overdue");
    private static final PseudoClass PSEUDO_CLASS_DONE = PseudoClass.getPseudoClass("done");
    
    private static Logic l;
    private static List<String> inputHistory;
    private static int inputHistoryIndex;
    private static ObservableList<LogicTask> taskList = FXCollections.observableArrayList();
    private static ObservableList<LogicEvent> eventList = FXCollections.observableArrayList();
    private static WebEngine webEngine;

    @FXML Label labelTitle;
    @FXML TextField textInput;
    @FXML Label labelFeedback;
    @FXML TableView<LogicTask> tableTask;
    @FXML TableColumn<LogicTask, String> columnTaskID;
    @FXML TableColumn<LogicTask, String> columnTaskName;
    @FXML TableColumn<LogicTask, String> columnTaskTime;
    @FXML TableColumn<LogicTask, String> columnTaskRecurring;
    @FXML TableView<LogicEvent> tableEvent;
    @FXML TableColumn<LogicEvent, String> columnEventID;
    @FXML TableColumn<LogicEvent, String> columnEventName;
    @FXML TableColumn<LogicEvent, String> columnEventStartTime;
    @FXML TableColumn<LogicEvent, String> columnEventEndTime;
    @FXML TableColumn<LogicEvent, String> columnEventRecurring;
    @FXML WebView webView;

    @FXML
    public void getInput() {
        String userInput = textInput.getText();
        addInputToHistory(userInput);
        process(userInput);
        textInput.setText(TEXT_EMPTY);

    }

    private void addInputToHistory(String userInput) {
        inputHistory.add(userInput);
        inputHistoryIndex = inputHistory.size();
    }

    private void process(String userInput) {
        ExecuteResult result = l.executeCommand(userInput);
        ExecuteResult.CommandType commnadType = result.getType();
        String comment = result.getComment();
        
        switch (commnadType) {
        case HELP :
            showWebView();
            break;
        case EXIT :
            Platform.exit();
            break;
        case DISPLAY :
            hideWebView();
            labelFeedback.setText(comment);
            populateList(result);
            break;
        default :
            hideWebView();
            labelFeedback.setText(comment);
            break;
        }

    }

    private void hideWebView() {
        webView.setVisible(false);
        tableEvent.setVisible(true);
        tableTask.setVisible(true);
    }

    private void showWebView() {
        webView.setVisible(true);
        tableEvent.setVisible(false);
        tableTask.setVisible(false);
    }

    public static Logic getLogic() {
        return l;
    }

    public static void setLogic(Logic l) {
        assert l != null;
        UIController.l = l;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTabBehaviour();
        initWebView();
        initInputHistory();
        initTableTask();
        
        columnEventID
                .setCellValueFactory(param -> new ReadOnlyStringWrapper(Integer.toString(param.getValue().getId())));
        columnEventName.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
        columnEventStartTime.setCellValueFactory(param -> new ReadOnlyStringWrapper(
                param.getValue().getStart().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))));
        columnEventEndTime.setCellValueFactory(param -> new ReadOnlyStringWrapper(
                param.getValue().getEnd().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))));
        columnEventRecurring.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<LogicEvent, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<LogicEvent, String> param) {
                        String periodString = convertTemporalToString(param.getValue().getPeriod());
                        return new ReadOnlyStringWrapper(periodString);
                    }
                });


        tableEvent.setRowFactory(new Callback<TableView<LogicEvent>, TableRow<LogicEvent>>() {
            @Override
            public TableRow<LogicEvent> call(TableView<LogicEvent> tableEventView) {
                return new TableRow<LogicEvent>() {
                    @Override
                    protected void updateItem(LogicEvent event, boolean b) {
                        super.updateItem(event, b);
                        boolean overdue = event != null && event.getOverdue();
                        boolean done = event != null && !event.isDone();

                        if (!done) {
                            pseudoClassStateChanged(PSEUDO_CLASS_OVERDUE, overdue);
                        }

                        super.updateItem(event, b);
                        pseudoClassStateChanged(PSEUDO_CLASS_DONE, done);
                    }

                };
            }
        });


        tableEvent.setItems(eventList);

        textInput.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.UP) {

                    if (inputHistoryIndex > 0) {
                        inputHistoryIndex--;
                        textInput.setText(inputHistory.get(inputHistoryIndex));
                        textInput.positionCaret(textInput.getText().length());
                    }
                }

                if (event.getCode() == KeyCode.DOWN) {
                    if (inputHistoryIndex < inputHistory.size() - 1) {
                        inputHistoryIndex++;
                        textInput.setText(inputHistory.get(inputHistoryIndex));
                        textInput.positionCaret(textInput.getText().length());
                    } else {
                        textInput.setText(TEXT_EMPTY);
                    }
                }
            }
        });

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                textInput.requestFocus();
            }
        });
    }

    private void initTableTask() {
        initColumnTaskId();
        initColumnTaskName();
        initColumnTaskTime();
        
        columnTaskRecurring.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<LogicTask, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<LogicTask, String> param) {
                        String periodString = convertTemporalToString(param.getValue().getPeriod());

                        LocalDate limitDate = param.getValue().getLimitDate();
                        int limitOccur = param.getValue().getLimitOccur();

                        if (limitOccur > 0) {
                            periodString += " for " + limitOccur + " time(s)";
                        } else if (limitDate != null) {
                            periodString += " until "
                                    + limitDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
                        }
                        return new ReadOnlyStringWrapper(periodString);
                    }
                });
        
        tableTask.setRowFactory(new Callback<TableView<LogicTask>, TableRow<LogicTask>>() {
            @Override
            public TableRow<LogicTask> call(TableView<LogicTask> tableEventView) {
                return new TableRow<LogicTask>() {
                    @Override
                    protected void updateItem(LogicTask task, boolean b) {
                        super.updateItem(task, b);
                        boolean overdue = task != null && task.getOverdue();
                        boolean done = task != null && !task.isDone();
                        if (!done) {
                            pseudoClassStateChanged(PSEUDO_CLASS_OVERDUE, overdue);
                        }

                        super.updateItem(task, b);
                        pseudoClassStateChanged(PSEUDO_CLASS_DONE, done);
                    }

                };
            }
        });

        tableTask.setItems(taskList);
    }

    private void initColumnTaskTime() {
        columnTaskTime.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<LogicTask, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<LogicTask, String> param) {
                        LocalDateTime deadline = param.getValue().getDeadline();
                        String deadlineString = convertDeadlineToString(deadline);
                        return new ReadOnlyStringWrapper(deadlineString);
                    }


                });
    }
    
    private String convertDeadlineToString(LocalDateTime deadline) {
        String deadlineString;
        if (deadline != null) {
            deadlineString = deadline.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
        } else {
            deadlineString = TEXT_EMPTY;
        }
        return deadlineString;
    }

    private void initColumnTaskName() {
        columnTaskName.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getName()));
    }

    private void initColumnTaskId() {
        columnTaskID.setCellValueFactory(param -> new ReadOnlyStringWrapper(Integer.toString(param.getValue().getId())));
    }

    private void initInputHistory() {
        inputHistory = new ArrayList<String>();
        inputHistoryIndex = -1;
    }

    private void initWebView() {
        String htmlURL = LifeTracker.class.getResource(PATH_README_HTML).toExternalForm();
        String cssURL = LifeTracker.class.getResource(PATH_README_CSS).toString();
        webView.setVisible(false);
        webEngine = webView.getEngine();
        webEngine.setUserStyleSheetLocation(cssURL);
        webEngine.load(htmlURL);
    }

    private void initTabBehaviour() {
        textInput.setFocusTraversable(true);
        tableEvent.setFocusTraversable(true);
        tableEvent.setFocusTraversable(true);
        webView.setFocusTraversable(true);
        labelFeedback.setFocusTraversable(false);
        labelTitle.setFocusTraversable(false);
    }

    private String convertTemporalToString(TemporalAmount period) {
        String periodString;
        if (period == null) {
            periodString = TEXT_EMPTY;
        } else if (period instanceof Period) {
            periodString = convertPeriodToString(((Period) period).normalized());
        } else {
            periodString = convertDurationToString((Duration) period);
        }
        return periodString;
    }

    private String convertPeriodToString(Period period) {
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        return formatDuration(years, FIELD_YEAR) + formatDuration(months, FIELD_MONTH)
                + formatDuration(days, FIELD_DAY);
    }

    private String convertDurationToString(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return formatDuration(hours, FIELD_HOUR) + formatDuration(minutes, FIELD_MINUTE);
    }

    private String formatDuration(long duration, String label) {
        return duration == 0 ? TEXT_EMPTY : duration + " " + label + " ";
    }

    public static void populateList(ExecuteResult result) {
        taskList.clear();
        for (LogicTask task : result.getTaskList()) {
            taskList.add(task);
        }
        eventList.clear();
        for (LogicEvent event : result.getEventList()) {
            eventList.add(event);
        }
    }

}
