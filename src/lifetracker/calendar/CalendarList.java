package lifetracker.calendar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import lifetracker.calendar.visitor.OldNewEntryPair;

public interface CalendarList {

    String ERROR_INVALID_ID = "Entry %1$s is not found!";
    int BASE_ID = 0;

    /**
     * Sorts all active task objects in ascending order of deadline, and all
     * archived task objects in descending order of deadline, in two separate
     * lists. Floating tasks are at the tail end of both lists. The lists are
     * concatenated and returned as a single list.
     * 
     * @return A sorted {@code List} of task objects.
     */
    List<CalendarEntry> getTaskList();

    /**
     * Sorts all active event objects in ascending order, and all archived
     * events in descending order, of end date and time followed by start date
     * and time, in two separate lists. The lists are concatenated and returned
     * as a single list.
     * 
     * @return A sorted {@code List} of event objects.
     */
    List<CalendarEntry> getEventList();

    /**
     * Sorts and returns only the archived tasks in the {@code CalendarList}, in
     * descending order.
     * 
     * @return A sorted {@code List} of archived task objects.
     */
    List<CalendarEntry> getArchivedTaskList();

    /**
     * Sorts and returns only the archived events in the {@code CalendarList},
     * in descending order of end date and time, followed by start date and
     * time.
     * 
     * @return A sorted {@code List} of archived event objects.
     */
    List<CalendarEntry> getArchivedEventList();

    /**
     * Adds a floating task.
     *
     * @param name
     *            Name of floating task.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name); // floating task

    /**
     * Adds a deadline task.
     *
     * @param name
     *            Name of deadline task.
     * 
     * @param deadline
     *            Deadline of said task.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name, LocalDateTime deadline);

    /**
     * Adds a recurring deadline task that occurs periodically with an unlimited
     * number of occurrences.
     *
     * @param name
     *            Name of deadline task.
     * 
     * @param deadline
     *            Deadline of the task.
     * 
     * @param period
     *            Period between each deadline.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name, LocalDateTime deadline, Period period);

    /**
     * Adds a recurring deadline task that occurs periodically with a limited
     * number of occurrences.
     *
     * @param name
     *            Name of deadline task.
     * 
     * @param deadline
     *            Deadline of the task.
     * 
     * @param period
     *            Period between each deadline.
     * 
     * @param limit
     *            Maximum number of occurrences of this task.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name, LocalDateTime deadline, Period period, int limit);

    /**
     * Adds a recurring deadline task that occurs periodically up to a specified
     * date, inclusive.
     *
     * @param name
     *            Name of deadline task.
     * 
     * @param deadline
     *            Deadline of the task.
     * 
     * @param period
     *            Period between each deadline.
     * 
     * @param limitDate
     *            Date after which no more occurrences of this task will be
     *            recorded.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name, LocalDateTime deadline, Period period, LocalDate limitDate);

    /**
     * Adds a one-time event.
     *
     * @param name
     *            Name of event.
     * 
     * @param start
     *            Start date and time of the event.
     * 
     * @param end
     *            End date and time of the event.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name, LocalDateTime start, LocalDateTime end);

    /**
     * Adds a recurring event that occurs periodically with an unlimited number
     * of occurrences.
     *
     * @param name
     *            Name of event.
     * 
     * @param start
     *            Start date and time of the event.
     * 
     * @param end
     *            End date and time of the event.
     * 
     * @param period
     *            Period between each occurrence of this event.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name, LocalDateTime start, LocalDateTime end, Period period);

    /**
     * Adds a recurring event that occurs periodically with a limited number of
     * occurrences.
     *
     * @param name
     *            Name of event.
     * 
     * @param start
     *            Start date and time of the event.
     * 
     * @param end
     *            End date and time of the event.
     * 
     * @param period
     *            Period between each occurrence of this event.
     * 
     * @param limit
     *            Maximum number of occurrences of this event.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name, LocalDateTime start, LocalDateTime end, Period period, int limit);

    /**
     * Adds a recurring event that occurs periodically up to a specified
     * starting date, inclusive.
     *
     * @param name
     *            Name of event.
     * 
     * @param start
     *            Start date and time of the event.
     * 
     * @param end
     *            End date and time of the event.
     * 
     * @param period
     *            Period between each occurrence of this event.
     * 
     * @param limit
     *            Maximum number of occurrences of this event.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(String name, LocalDateTime start, LocalDateTime end, Period period, LocalDate limitDate);

    /**
     * Adds an entry of any of the following types: {@code GenericEntry},
     * {@code DeadlineTask}, {@code RecurringTask}, {@code Event},
     * {@code RecurringEvent}.
     * <p>
     * If the entry does not have a valid ID number assigned to it already, it
     * will be reassigned a new one.
     * <p>
     * The entry will be added to the main or archived lists depending on
     * whether it is a task or event, and whether it is active or otherwise.
     *
     * @param entry
     *            An object of one of the five supported classes.
     * 
     * @return The entry ID of the newly added task.
     */
    int add(CalendarEntry entry);

    /**
     * Deletes an entry.
     *
     * @param id
     *            ID number of the entry to be deleted.
     * 
     * @return The entry object deleted.
     */
    CalendarEntry delete(int id);

    /**
     * Changes an entry into a {@code GenericEntry}.
     * <p>
     * Empty fields will be skipped during update, unless a conversion for the
     * entry requires it, in which case an {@code IllegalArgumentException} will
     * be thrown.
     * <p>
     * If {@code isConvertForced} is set, then this method will forcefully
     * convert the entry into a recurring task, even if information will be
     * lost. For example, if the entry was a {@code DeadlineTask}, the deadline
     * will be lost.
     *
     * @param id
     *            The ID of the entry to update.
     * 
     * @param newName
     *            The new name.
     * 
     * @param isConvertForced
     *            If information can be discarded during the conversion.
     * 
     * @return The old entry object before the update.
     * 
     * @throws IllegalArgumentException
     *             If fields that are required are empty.
     */
    CalendarEntry updateToGeneric(int id, String newName, boolean isConvertForced);

    CalendarEntry updateToDeadline(int id, String newName, LocalDateTime newDeadline, boolean isConvertForced);

    CalendarEntry updateToEvent(int id, String newName, LocalDateTime newStartTime, LocalDateTime newEndTime,
            boolean isConvertForced);

    CalendarEntry updateToRecurringTask(int id, String newName, LocalDateTime newDeadLine, Period newPeriod,
            boolean isLimitKept, boolean isConvertForced);

    /**
     * Changes an entry into a {@code RecurringTask}.
     * <p>
     * Empty fields will be skipped during update, unless a conversion for the
     * entry requires it, in which case an {@code IllegalArgumentException} will
     * be thrown.
     * <p>
     * If {@code isConvertForced} is set, then this method will forcefully
     * convert the entry into a recurring task, even if information will be
     * lost. For example, if the entry was a {@code RecurringEvent}, the start
     * date and time will be lost.
     * <p>
     * {@code newLimit} specifies the new number of occurrences to happen after
     * and including the current occurrence. For example, if {@code newLimit} is
     * set to 4, then the recurring task will happen 4 more times (including the
     * current iteration), no matter how many times it has occurred in the past.
     *
     * @param id
     *            The ID of the entry to update.
     * 
     * @param newName
     *            The new name.
     * 
     * @param newDeadLine
     *            The new deadline.
     * 
     * @param newPeriod
     *            The new period.
     * 
     * @param newLimit
     *            The new occurrence limit.
     * 
     * @param isConvertForced
     *            If information can be discarded during the conversion.
     * 
     * @return The old entry object before the update.
     * 
     * @throws IllegalArgumentException
     *             If fields that are required are empty.
     */
    CalendarEntry updateToRecurringTask(int id, String newName, LocalDateTime newDeadLine, Period newPeriod,
            int newLimit, boolean isConvertForced);

    CalendarEntry updateToRecurringTask(int id, String newName, LocalDateTime newDeadLine, Period newPeriod,
            LocalDate newLimitDate, boolean isConvertForced);

    /**
     * Updates an entry into a recurring event without a limiting number of
     * occurrences.
     * <p>
     * Empty fields will be skipped during update, unless a conversion for the
     * entry requires it, in which case an {@code IllegalArgumentException} will
     * be thrown.
     * <p>
     * If {@code isLimitKept} is set, then this method will discard the previous
     * recurring limit set in an entry with such a limit.
     *
     * @param id
     *            The ID of the entry to update.
     * 
     * @param newName
     *            The new name.
     * 
     * @param newStart
     *            The new start date and time.
     * 
     * @param newEnd
     *            The new end date and time.
     * 
     * @param newPeriod
     *            The new recurring time period.
     * 
     * @param isLimitKept
     *            If the original limit should be discarded.
     * 
     * @return The old entry before the update.
     * 
     * @throws IllegalArgumentException
     *             If fields that are required are empty.
     */
    CalendarEntry updateToRecurringEvent(int id, String newName, LocalDateTime newStart, LocalDateTime newEnd,
            Period newPeriod, boolean isLimitKept);

    CalendarEntry updateToRecurringEvent(int id, String newName, LocalDateTime newStart, LocalDateTime newEnd,
            Period newPeriod, int newLimit);

    CalendarEntry updateToRecurringEvent(int id, String newName, LocalDateTime newStart, LocalDateTime newEnd,
            Period newPeriod, LocalDate newLimit);

    /**
     * Overwrites an entry in the {@code CalendarList} with the new entry
     * provided, if they have the same ID.
     * <p>
     * If there are no old entries with the same ID, the new entry is simply
     * added to the CalendarList.
     *
     * @param newEntry
     *            The new entry.
     * 
     * @return The old entry before the update if present, {@code null}
     *         otherwise.
     */
    CalendarEntry update(CalendarEntry newEntry);

    OldNewEntryPair mark(int id);

    /**
     * Finds and returns the {@code CalendarEntry} object with the specified ID
     * number.
     *
     * @param id
     *            ID number of desired entry.
     * 
     * @return The entry object with the specified ID number.
     * 
     * @throws IllegalArgumentException
     *             If ID number is not found.
     */
    CalendarEntry get(int id);

    /**
     * Creates and returns a copy of the {@code CalendarListResult} object with
     * task and event lists filtered to contain only entries with descriptions
     * containing the desired text. Allows room for minor typographical errors.
     * 
     * @param toSearch
     *            The desired text to search for.
     * 
     * @return A {@code CalendarList} with customized task and event lists.
     */
    CalendarList findByName(String toSearch);

    /**
     * Creates and returns a {@code CalendarListResult} object with task and
     * event lists set as the archived lists of the main {@code CalendarList},
     * filtered to contain only entries with descriptions containing the desired
     * text. Allows room for minor typographical errors.
     * 
     * @param toSearch
     *            The desired text to search for.
     * @return A {@code CalendarList} with customized task and event lists.
     */
    CalendarList findArchivedByName(String toSearch);

    CalendarList findAllByName(String toSearch);

    CalendarList findToday();

}
