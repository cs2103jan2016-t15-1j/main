package lifetracker.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import lifetracker.calendar.CalendarEntry.EntryType;

public class CalendarListImpl implements CalendarList {

    private static final String ERROR_EMPTY_NAME = "Task/Event's name cannot be empty!";

    // variables
    protected TreeMap<Integer, CalendarEntry> taskList = new TreeMap<>();
    protected TreeMap<Integer, CalendarEntry> eventList = new TreeMap<>();
    protected TreeMap<Integer, CalendarEntry> archivedTaskList = new TreeMap<>();
    protected TreeMap<Integer, CalendarEntry> archivedEventList = new TreeMap<>();

    // get() and set() functions for variables

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#getTaskList()
     */
    @Override
    public List<CalendarEntry> getTaskList() {
        List<CalendarEntry> list = new ArrayList<CalendarEntry>(taskList.values());
        return list;
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#getEventList()
     */
    @Override
    public List<CalendarEntry> getEventList() {
        List<CalendarEntry> list = new ArrayList<CalendarEntry>(eventList.values());
        return list;
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#getArchivedTaskList()
     */
    @Override
    public List<CalendarEntry> getArchivedTaskList() {
        List<CalendarEntry> list = new ArrayList<CalendarEntry>(archivedTaskList.values());
        return list;
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#getArchivedEventList()
     */
    @Override
    public List<CalendarEntry> getArchivedEventList() {
        List<CalendarEntry> list = new ArrayList<CalendarEntry>(archivedEventList.values());
        return list;
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#add(java.lang.String)
     */
    @Override
    public int add(String name) {
        assert name != null;

        if (name.isEmpty()) {
            throw new IllegalArgumentException(ERROR_EMPTY_NAME);
        }

        int idToSet = this.getNextId();
        CalendarEntryImpl ft = new CalendarEntryImpl(name, null, null, idToSet);
        taskList.put(idToSet, ft);
        return idToSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#add(java.lang.String,
     * java.time.LocalDateTime)
     */
    @Override
    public int add(String name, LocalDateTime deadline) {
        assert name != null;
        assert deadline != null;

        if (name.isEmpty()) {
            throw new IllegalArgumentException(ERROR_EMPTY_NAME);
        }

        int idToSet = this.getNextId();
        CalendarEntryImpl dt = new CalendarEntryImpl(name, null, deadline, idToSet);
        taskList.put(idToSet, dt);
        return idToSet;
    }

    @Override
    public int add(String name, LocalDateTime deadline, TemporalAmount period) {
        assert period != null;

        int idToSet = this.add(name, deadline);
        this.taskList.get(idToSet).setPeriod(period);
        return idToSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#add(java.lang.String,
     * java.time.LocalDateTime, java.time.LocalDateTime)
     */
    @Override
    public int add(String name, LocalDateTime start, LocalDateTime end) {
        assert name != null;
        assert start != null;
        assert end != null;

        if (name.isEmpty()) {
            throw new IllegalArgumentException(ERROR_EMPTY_NAME);
        }

        int idToSet = this.getNextId();
        CalendarEntryImpl event = new CalendarEntryImpl(name, start, end, idToSet);
        eventList.put(idToSet, event);
        return idToSet;
    }

    @Override
    public int add(String name, LocalDateTime start, LocalDateTime end, TemporalAmount period) {
        assert period != null;

        int idToSet = this.add(name, start, end);
        this.eventList.get(idToSet).setPeriod(period);
        return idToSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#delete(int)
     */
    @Override
    public CalendarEntry delete(int id) {
        if (taskList.containsKey(id)) {
            CalendarEntry copy = taskList.get(id);
            taskList.remove(id);
            return copy;
        } else if (eventList.containsKey(id)) {
            CalendarEntry copy = eventList.get(id);
            eventList.remove(id);
            return copy;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#update(int, java.lang.String)
     */
    @Override
    public CalendarEntry update(int id, String newName, LocalDateTime newStart, LocalDateTime newEnd,
            TemporalAmount newPeriod) {
        CalendarEntry toUpdate;
        CalendarEntry copy;
        if (taskList.containsKey(id)) {
            toUpdate = taskList.get(id);
            copy = toUpdate.copy();
        } else if (eventList.containsKey(id)) {
            toUpdate = eventList.get(id);
            copy = toUpdate.copy();
        } else {
            return null;
        }
        checkUpdateArguments(toUpdate, newStart, newEnd);
        updateEntryName(toUpdate, newName);
        updateEntryStart(toUpdate, newStart);
        updateEntryEnd(toUpdate, newEnd);
        updateEntryPeriod(toUpdate, newPeriod);
        return copy;
    }

    @Override
    public CalendarEntry mark(int id) {
        CalendarEntry entry;
        if (this.taskList.containsKey(id)) {
            entry = this.taskList.get(id);
            assert entry.isActive();
            entry.mark();
            if (entry.getType().equals(EntryType.FLOATING)) {
                this.archiveTask(id);
            } else {
                assert entry.getType().equals(EntryType.DEADLINE);
                if (!entry.isRecurring()) {
                    this.archiveTask(id);
                }
            }
            return entry;
        } else if (this.eventList.containsKey(id)) {
            entry = this.eventList.get(id);
            assert entry.isActive();
            assert entry.getType().equals(EntryType.EVENT);
            entry.mark();
            this.archiveEvent(id);
            return entry;
        } else if (this.archivedTaskList.containsKey(id)) {
            entry = this.archivedTaskList.get(id);
            assert !entry.isActive();
            entry.mark();
            this.unarchiveTask(id);
            return entry;
        } else if (this.archivedEventList.containsKey(id)) {
            entry = this.archivedEventList.get(id);
            assert !entry.isActive();
            assert entry.getType().equals(EntryType.EVENT);
            entry.mark();
            this.unarchiveEvent(id);
            return entry;
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see lifetracker.calendar.CalenderList#find(String)
     */
    @Override
    public CalendarList findByName(String toSearch) {
        CalendarListTemp result = new CalendarListTemp();
        TreeMap<Integer, CalendarEntry> copyTaskList = new TreeMap<>();
        copyTaskList.putAll(this.taskList);
        filterByName(copyTaskList, toSearch);
        TreeMap<Integer, CalendarEntry> copyEventList = new TreeMap<>();
        copyEventList.putAll(this.eventList);
        filterByName(copyEventList, toSearch);
        result.setTaskList(copyTaskList);
        result.setEventList(copyEventList);
        return result;
    }

    @Override
    public CalendarList findArchivedByName(String toSearch) {
        CalendarListTemp result = new CalendarListTemp();
        result.setTaskList(this.archivedTaskList);
        result.setEventList(this.archivedEventList);
        return result.findByName(toSearch);
    }

    @Override
    public CalendarList findAllByName(String toSearch) {
        CalendarListTemp result = (CalendarListTemp) this.findByName(toSearch);
        CalendarListTemp resultArchived = (CalendarListTemp) this.findArchivedByName(toSearch);
        result.taskList.putAll(resultArchived.taskList);
        result.eventList.putAll(resultArchived.eventList);
        return result;
    }

    TreeMap<Integer, CalendarEntry> filterList(TreeMap<Integer, CalendarEntry> treeMap, String toSearch,
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        TreeMap<Integer, CalendarEntry> copyMap = new TreeMap<>();
        copyMap.putAll(treeMap);
        filterByName(copyMap, toSearch);
        filterByStartDate(copyMap, startDate);
        filterByStartTime(copyMap, startTime);
        filterByEndDate(copyMap, endDate);
        filterByEndTime(copyMap, endTime);
        return copyMap;
    }

    void updateEntryName(CalendarEntry toUpdate, String newName) {
        if (newName != null && !newName.isEmpty()) {
            toUpdate.setName(newName);
        }
    }

    void updateEntryStart(CalendarEntry toUpdate, LocalDateTime newStart) {
        if (newStart != null) {
            toUpdate.setStart(newStart);
        }
    }

    void updateEntryEnd(CalendarEntry toUpdate, LocalDateTime newEnd) {
        if (toUpdate.getType().equals(EntryType.EVENT)) {
            if (newEnd != null) {
                toUpdate.setEnd(newEnd);
            }
        } else if (toUpdate.getType().equals(EntryType.DEADLINE)) {
            if (newEnd != null) {
                toUpdate.setEnd(newEnd);
            }
        } else {
            assert toUpdate.getType().equals(EntryType.FLOATING);
            if (newEnd != null) {
                toUpdate.setEnd(newEnd);
                ((CalendarEntryImpl) toUpdate).setType(EntryType.DEADLINE);
            }
        }
        // allowed to change floating task to deadline but not the other way
        // around
    }

    void updateEntryPeriod(CalendarEntry toUpdate, TemporalAmount newPeriod) {
        if (newPeriod != null) {
            toUpdate.setPeriod(newPeriod);
        }
    }

    void checkUpdateArguments(CalendarEntry toUpdate, LocalDateTime newStart, LocalDateTime newEnd) {
        if (toUpdate.getType().equals(EntryType.EVENT)) {
            if (newStart != null && newEnd != null) {
                CalendarEntry.checkStartBeforeEnd(newStart, newEnd);
            } else if (newStart != null && newEnd == null) {
                CalendarEntry.checkStartBeforeEnd(newStart, toUpdate.getEnd());
            } else if (newStart == null && newEnd != null) {
                CalendarEntry.checkStartBeforeEnd(toUpdate.getStart(), newEnd);
            }
        } else if (toUpdate.getType().equals(EntryType.DEADLINE)) {
            if (newStart != null) {
                throw new IllegalArgumentException(CalendarEntry.MESSAGE_ERROR_ILLEGAL_TYPE_CHANGE_TASK_TO_EVENT);
            }
        } else {
            assert toUpdate.getType().equals(EntryType.FLOATING);
            if (newStart != null) {
                throw new IllegalArgumentException(CalendarEntry.MESSAGE_ERROR_ILLEGAL_TYPE_CHANGE_TASK_TO_EVENT);
            }
        }
    }

    void filterByName(TreeMap<Integer, CalendarEntry> treeMap, String toSearch) {
        if (toSearch == null || toSearch.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Integer, CalendarEntry>> iterator = treeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, CalendarEntry> entry = iterator.next();
            String entryName = entry.getValue().getName();
            if (containsAnyWord(entryName, toSearch)) {
                iterator.remove();
            }
        }
    }

    void filterByStartDate(TreeMap<Integer, CalendarEntry> treeMap, LocalDate startDate) {
        if (startDate == null) {
            return;
        }
        Iterator<Map.Entry<Integer, CalendarEntry>> iterator = treeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, CalendarEntry> entry = iterator.next();
            if (entry.getValue().getStart() == null) {
                iterator.remove();
            } else {
                LocalDate entryStartDate = entry.getValue().getStart().toLocalDate();
                if (!entryStartDate.equals(startDate)) {
                    iterator.remove();
                }
            }
        }
    }

    void filterByStartTime(TreeMap<Integer, CalendarEntry> treeMap, LocalTime startTime) {
        if (startTime == null) {
            return;
        }
        Iterator<Map.Entry<Integer, CalendarEntry>> iterator = treeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, CalendarEntry> entry = iterator.next();
            if (entry.getValue().getStart() == null) {
                iterator.remove();
            } else {
                LocalTime entryStartTime = entry.getValue().getStart().toLocalTime();
                if (!entryStartTime.equals(startTime)) {
                    iterator.remove();
                }
            }
        }
    }

    void filterByEndDate(TreeMap<Integer, CalendarEntry> treeMap, LocalDate endDate) {
        if (endDate == null) {
            return;
        }
        Iterator<Map.Entry<Integer, CalendarEntry>> iterator = treeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, CalendarEntry> entry = iterator.next();
            if (entry.getValue().getEnd() == null) {
                iterator.remove();
            } else {
                LocalDate entryEndDate = entry.getValue().getEnd().toLocalDate();
                if (!entryEndDate.equals(endDate)) {
                    iterator.remove();
                }
            }
        }
    }

    void filterByEndTime(TreeMap<Integer, CalendarEntry> treeMap, LocalTime endTime) {
        if (endTime == null) {
            return;
        }
        Iterator<Map.Entry<Integer, CalendarEntry>> iterator = treeMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, CalendarEntry> entry = iterator.next();
            if (entry.getValue().getEnd() == null) {
                iterator.remove();
            } else {
                LocalTime entryEndTime = entry.getValue().getEnd().toLocalTime();
                if (!entryEndTime.equals(endTime)) {
                    iterator.remove();
                }
            }
        }
    }

    void archiveTask(int id) {
        if (!this.taskList.containsKey(id)) {
            throw new IllegalArgumentException(MESSAGE_ERROR_TASK_NOT_FOUND);
        }
        CalendarEntry task = this.taskList.get(id);
        this.taskList.remove(id);
        this.archivedTaskList.put(id, task);
    }

    void unarchiveTask(int id) {
        if (!this.archivedTaskList.containsKey(id)) {
            throw new IllegalArgumentException(MESSAGE_ERROR_TASK_NOT_FOUND);
        }
        CalendarEntry task = this.archivedTaskList.get(id);
        this.archivedTaskList.remove(id);
        this.taskList.put(id, task);
    }

    void archiveEvent(int id) {
        if (!this.eventList.containsKey(id)) {
            throw new IllegalArgumentException(MESSAGE_ERROR_EVENT_NOT_FOUND);
        }
        CalendarEntry event = this.eventList.get(id);
        this.eventList.remove(id);
        this.archivedEventList.put(id, event);
    }

    void unarchiveEvent(int id) {
        if (!this.archivedEventList.containsKey(id)) {
            throw new IllegalArgumentException(MESSAGE_ERROR_EVENT_NOT_FOUND);
        }
        CalendarEntry event = this.archivedEventList.get(id);
        this.archivedEventList.remove(id);
        this.eventList.put(id, event);
    }

    private int getNextId() {
        int taskMax = this.taskList.isEmpty() ? 0 : this.taskList.lastKey();
        int eventMax = this.eventList.isEmpty() ? 0 : this.eventList.lastKey();
        int archivedTaskMax = this.archivedTaskList.isEmpty() ? 0 : this.archivedTaskList.lastKey();
        int archivedEventMax = this.archivedEventList.isEmpty() ? 0 : this.archivedEventList.lastKey();
        int idToSet = Math.max(taskMax, eventMax);
        idToSet = Math.max(idToSet, archivedTaskMax);
        idToSet = Math.max(idToSet, archivedEventMax);
        idToSet += 1;
        return idToSet;
    }

    boolean containsAnyWord(String entryName, String toSearch) {
        String[] arrayOfWords = toSearch.split(" ");
        for (String word : arrayOfWords) {
            if (StringUtils.containsIgnoreCase(entryName, word)) {
                return true;
            }
        }
        return false;
    }

}
