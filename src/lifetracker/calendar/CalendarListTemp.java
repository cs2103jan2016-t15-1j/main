package lifetracker.calendar;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.TreeMap;

public class CalendarListTemp extends CalendarListImpl {

    void setTaskList(TreeMap<Integer, CalendarEntry> map) {
        this.taskList = map;
    }

    void setEventList(TreeMap<Integer, CalendarEntry> map) {
        this.eventList = map;
    }

    void setArchivedTaskList(TreeMap<Integer, CalendarEntry> map) {
        this.archivedTaskList = map;
    }

    void setArchivedEventList(TreeMap<Integer, CalendarEntry> map) {
        this.archivedEventList = map;
    }

    @Override
    public int add(String name) {
        return 0;
    }

    @Override
    public int add(String name, LocalDateTime deadline) {
        return 0;
    }

    @Override
    public int add(String name, LocalDateTime deadline, TemporalAmount period) {
        return 0;
    }

    @Override
    public int add(String name, LocalDateTime start, LocalDateTime end) {
        return 0;
    }

    @Override
    public int add(String name, LocalDateTime start, LocalDateTime end, TemporalAmount period) {
        return 0;
    }

    @Override
    public CalendarEntry delete(int id) {
        return null;
    }

    @Override
    public CalendarEntry update(int id, String newName, LocalDateTime newStart, LocalDateTime newEnd,
            TemporalAmount newPeriod) {
        return null;
    }

}