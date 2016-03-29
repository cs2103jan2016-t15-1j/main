package lifetracker.UI;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import lifetracker.logic.ExecuteResult;
import lifetracker.logic.Logic;
import lifetracker.logic.Task;

public class UIController implements Initializable {

    private static Logic l;

    @FXML TextField textInput;
    @FXML Label labelFeedback;
    @FXML TableView<Task> tableTask;
    @FXML TableColumn<Task, String> columnTaskID;
    @FXML TableColumn<Task, String> columnTaskName;
    @FXML TableColumn<Task, String> columnTaskTime;
    @FXML TableView<Task> tableEvent;
    @FXML TableColumn<Task, String> columnEventID;
    @FXML TableColumn<Task, String> columnEventName;
    @FXML TableColumn<Task, String> columnEventStartTime;
    @FXML TableColumn<Task, String> columnEventEndTime;

    private static ObservableList<Task> taskList = FXCollections.observableArrayList();
    private static ObservableList<Task> eventList = FXCollections.observableArrayList();
    
    @FXML
    public void getInput() {
        String userInput;

        if (!textInput.getText().toLowerCase().equals("exit")) {
            userInput = textInput.getText();
            process(userInput);
            textInput.setText("");
        } else {
            System.exit(0);
        }
    }

    private void process(String userInput) {
        ExecuteResult result;

        result = l.executeCommand(userInput);
        List<List<String>> taskList = result.getTaskList();
        
        for (List<String> task: taskList) {
            for(String line: task){
                System.out.println(line);
            }
        }
        
        if (result.getType() == ExecuteResult.CommandType.DISPLAY) {
            populateList();
        }
        labelFeedback.setText(result.getComment());
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
        columnTaskID.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Task, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<Task, String> param) {
                        return new ReadOnlyStringWrapper(param.getValue().getTask().get(0));
                    }
                });

        columnTaskName.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Task, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<Task, String> param) {
                        return new ReadOnlyStringWrapper(param.getValue().getTask().get(1));
                    }
                });

        columnTaskTime.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Task, String>, ObservableValue<String>>() {

                    @Override
                    public ObservableValue<String> call(CellDataFeatures<Task, String> param) {
                        return new ReadOnlyStringWrapper(param.getValue().getTask().get(2));
                    }
                });
        
        columnEventID.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Task,String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(CellDataFeatures<Task, String> param) {
                return null;
            }
        });
        tableTask.setItems(taskList);
    }

    public static void populateList() {
        ExecuteResult result;
        result = l.executeCommand("list");
        taskList.clear();
        for (List<String> task : result.getTaskList()) {
                taskList.add(new Task(task));
        }
    }
    

    /*
     * private ObservableList<Task> parseUserList() { ExecuteResult result;
     * 
     * result = l.executeCommand("list"); return
     * FXCollections.observableArrayList(new Task(this.task)); }
     */
}
