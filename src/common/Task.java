package common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/*
 * Our task object.
 * Stores task data
 */
public class Task {

    // Reference for possible updatable Celebi data fields
    public static enum DataType {
        NAME, DATE_START, DATE_END, IMPORTANCE //, IS_COMPLETED
    }

    public static enum Type {
        FLOATING, NOEND, DEADLINE, EVENT
    }

    private int cId;

    private StringProperty cName;

    private final ObjectProperty<Date> cStart;
    private final ObjectProperty<Date> cEnd;

    private ObjectProperty<Type> cType;

    private boolean cIsImportant;
    private boolean cIsCompleted;

    /*
     * Abstract data structure used is Set. Primary operations: find, remove,
     * iterateAll. Constraints: no duplicates.
     * 
     * Use LinkedHashSet implementation. HashMap provide O(1) access and
     * removal. LinkedHashSet provides O(n) iterateAll compared to HashMap's
     * O(m). n = number of values, m = table size, n < m.
     */

    // constructor, use property instead of value
    public Task(String name, Date start, Date end) {
        cName = new SimpleStringProperty(name);
        cStart = new SimpleObjectProperty<Date>(start);
        cEnd = new SimpleObjectProperty<Date>(end);
        cType = new SimpleObjectProperty<Type>(Type.FLOATING);
        updateType();

        // cStart = new SimpleObjectProperty<LocalDate>(localStart);
        // cEnd = new SimpleObjectProperty<LocalDate>(localEnd);
    }

    // setters
    public void setComplete(boolean isComplete) {
        cIsCompleted = isComplete;
    }

    public void setId(int id) {
        cId = id;
    }

    public void setName(String name) {
        cName.set(name);
    }

    public void setStart(Date start) {
        cStart.set(start);
        updateType();
        // cStart.set(convertToLocalDate((Date)start.clone()));
    }

    public void setEnd(Date end) {
        cEnd.set(end);
        updateType();
        // cEnd.set(convertToLocalDate((Date)end.clone()));
    }

    // getters
    public boolean isComplete() {
        return cIsCompleted;
    }

    public int getId() {
        return cId;
    }

    public String getName() {
        return cName.get();
    }

    public Date getStart() {
        if (cStart.get() == null) {
            return null;
        } else {
            return (Date) cStart.get().clone();
        }
    }

    public Date getEnd() {
        if (cEnd.get() == null) {
            return null;
        } else {
            return (Date) cEnd.get().clone();
        }
    }

    public Type getType() {
        return cType.get();
    }

    // get properties
    public StringProperty nameProperty() {
        return cName;
    }

    public ObjectProperty<Date> startProperty() {
        return cStart;
    }

    public ObjectProperty<Date> endProperty() {
        return cEnd;
    }

    public ObjectProperty<Type> typeProperty() {
        updateType();
        return cType;
    }

    public boolean isImportant() {
        return cIsImportant;
    }

    public void setImportant(Boolean impt) {
        this.cIsImportant = impt;
    }

    public boolean hasKeyword(String keyword) {
        if (keyword == null) {
            return true;
        } else {
            ArrayList<String> tokens = new ArrayList<>();
            Collections.addAll(tokens, keyword.split(" "));
            return nameHasTokens(tokens);
        }
    }

    private boolean nameHasTokens(List<String> tokens) {
        String nameLowerCase = cName.get().toLowerCase();
        
        for(String toCompare : tokens){
            toCompare = toCompare.toLowerCase();
            if(nameLowerCase.contains(toCompare)){
                return true;
            }
        }        
        return false;
    }

    private void updateType() {
        if (cStart.get() == null && cEnd.get() == null) {
            cType.set(Type.FLOATING);
        } else if (cStart.get() != null && cEnd.get() == null) {
            cType.set(Type.NOEND);
        } else if (cStart.get() == null && cEnd.get() != null) {
            cType.set(Type.DEADLINE);
        } else {
            cType.set(Type.EVENT);
        }
    }

    public boolean isWithinDate(Date cFilterDateStart, Date cFilterDateEnd) {
        System.out.println(cFilterDateStart + " " + cFilterDateEnd);
        if (cStart.get() == null && cEnd.get() == null) {
            return false;
        } else if (cStart.get() == null) {
            // No start date, thus check end date is within period
            return isWithinBothDates(cFilterDateStart, cFilterDateEnd, cEnd.get());
        } else if (cEnd.get() == null) {
            // No end date, thus check start date is within period
            return isWithinBothDates(cFilterDateStart, cFilterDateEnd, cStart.get());
        } else {
            // Have both dates
            boolean startIsWithin = isWithinBothDates(cFilterDateStart, cFilterDateEnd, cStart.get());
            boolean endIsWithin = isWithinBothDates(cFilterDateStart, cFilterDateEnd, cEnd.get());

            return startIsWithin && endIsWithin;
        }
    }
    
    public Task clone() {
    	Task newTask = new Task(cName.get(), cStart.get(), cEnd.get());
    	newTask.setImportant(cIsImportant);
    	newTask.setComplete(cIsCompleted);
    	return newTask;
    }

    private boolean isWithinBothDates(Date start, Date end, Date toCompare) {
        return toCompare.before(end) && toCompare.after(start);
    }
    // private methods
    /*
     * private LocalDate convertToLocalDate(Date date) { LocalDate localDate; if
     * (date != null) { localDate =
     * date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); } else {
     * localDate = null; } return localDate; }
     * 
     * private Date convertToDate(LocalDate localDate) { Date date; if
     * (localDate != null) { date =
     * Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()); }
     * else { date = null; } return date; }
     */
}
