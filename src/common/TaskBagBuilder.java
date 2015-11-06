package common;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskBagBuilder {

    private ObservableList<Task> list;

    public TaskBagBuilder(TasksBag bag) {
        list = FXCollections.observableArrayList();

        bag.getList().forEach(t -> {
            list.add(t);
        });
    }

    public ObservableList<Task> build() {
        return list;
    }

    public TaskBagBuilder isNotComplete() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        list.forEach(task -> {
            if (task.isCompleted() == false) {
                taskList.add(task);
            }
        });
        list = taskList; 
        return this;
    }

    public TaskBagBuilder isComplete() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        list.forEach(task -> {
            if (task.isCompleted()) {
                taskList.add(task);
            }
        });
        list = taskList;
        return this;
    }

    public TaskBagBuilder noDate() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        list.forEach(task -> {
            if (task.hasDate() == false) {
                taskList.add(task);
            }
        });
        list = taskList;
        return this;
    }

    public TaskBagBuilder hasDate() {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        list.forEach(task -> {
            if (task.hasDate()) {
                taskList.add(task);
            }
        });
        list = taskList;
        return this;
    }

    public TaskBagBuilder hasSearchKeyword(String searchState) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        list.forEach(task -> {
            if (task.hasKeyword(searchState)) {
                taskList.add(task);
            }
        });
        list = taskList;
        return this;
    }
    
    public TaskBagBuilder isWithinDayLimit(int limit) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();

        list.forEach(task -> {
            if (task.isWithinDays(limit)) {
                taskList.add(task);
            }
        });
        list = taskList;
        return this;
    }
}
