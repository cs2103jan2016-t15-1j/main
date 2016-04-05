package lifetracker.calendar.visitor;

import lifetracker.calendar.CalendarEntry;
import lifetracker.calendar.CalendarProperty;
import lifetracker.calendar.DeadlineTask;
import lifetracker.calendar.Event;
import lifetracker.calendar.GenericEntry;
import lifetracker.calendar.RecurringEvent;
import lifetracker.calendar.RecurringTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class EntryToRecurringTaskVisitor implements EntryVisitor<OldNewEntryPair> {

    private static final String ERROR_EMPTY_DEADLINE = "Task deadline cannot be empty!";
    private static final String ERROR_EMPTY_RECURRING = "Task recurring period cannot be empty!";

    private static final int LIMIT_INF = -2;
    private static final int LIMIT_DATE = -1;

    private final String name;
    private final LocalDateTime deadline;
    private final Period recurringPeriod;
    private final int occurLimit;
    private final LocalDate limitDate;
    private final boolean isConvertForced;
    private final boolean isLimitKept;

    public EntryToRecurringTaskVisitor(String name, LocalDateTime deadLine, Period recurringPeriod,
            boolean isLimitKept, boolean isConvertForced) {
        this.recurringPeriod = recurringPeriod;
        this.deadline = deadLine;
        this.name = name;
        occurLimit = LIMIT_INF;
        limitDate = null;
        this.isLimitKept = isLimitKept;
        this.isConvertForced = isConvertForced;
    }

    public EntryToRecurringTaskVisitor(String name, LocalDateTime deadLine, Period recurringPeriod, int occurLimit,
            boolean isConvertForced) {
        assert occurLimit > 0;

        this.name = name;
        this.deadline = deadLine;
        this.recurringPeriod = recurringPeriod;
        this.occurLimit = occurLimit;
        this.limitDate = null;
        this.isLimitKept = false;
        this.isConvertForced = isConvertForced;
    }

    public EntryToRecurringTaskVisitor(String name, LocalDateTime deadLine, Period recurringPeriod,
            LocalDate limitDate, boolean isConvertForced) {

        this.name = name;
        this.deadline = deadLine;
        this.recurringPeriod = recurringPeriod;
        this.occurLimit = LIMIT_DATE;
        this.limitDate = limitDate;
        this.isLimitKept = false;
        this.isConvertForced = isConvertForced;
    }

    @Override
    public OldNewEntryPair visit(GenericEntry entry) {
        if (deadline == null) {
            throw new IllegalArgumentException(ERROR_EMPTY_DEADLINE);
        }

        if (recurringPeriod == null) {
            throw new IllegalArgumentException(ERROR_EMPTY_RECURRING);
        }

        RecurringTask newTask = new RecurringTask(entry.getName(), deadline, recurringPeriod);
        return edit(entry, newTask);

    }

    @Override
    public OldNewEntryPair visit(DeadlineTask task) {
        if (recurringPeriod == null) {
            throw new IllegalArgumentException(ERROR_EMPTY_RECURRING);
        }

        RecurringTask newTask = new RecurringTask(task.getName(), task.getDateTime(CalendarProperty.END),
                recurringPeriod);
        return edit(task, newTask);
    }

    @Override
    public OldNewEntryPair visit(RecurringTask task) {
        RecurringTask clone = new RecurringTask(task);
        return edit(clone, task);
    }

    @Override
    public OldNewEntryPair visit(Event event) {
        if (recurringPeriod == null) {
            throw new IllegalArgumentException(ERROR_EMPTY_RECURRING);
        }

        if (isConvertForced) {
            OldNewEntryPair pair = visit(new DeadlineTask(event));
            pair.oldEntry = event;
            return pair;
        } else {
            RecurringEvent newEvent = new RecurringEvent(
                    event.getName(),
                    event.getDateTime(CalendarProperty.START),
                    event.getDateTime(CalendarProperty.END),
                    recurringPeriod);

            return edit(event, newEvent);
        }
    }

    @Override
    public OldNewEntryPair visit(RecurringEvent event) {
        RecurringEvent clone = new RecurringEvent(event);
        return edit(clone, event);
    }

    private OldNewEntryPair edit(CalendarEntry clone, RecurringTask task) {
        if (isConvertForced) {
            task = new RecurringTask(task);
        }

        if (name != null && !name.isEmpty()) {
            task.setName(name);
        }

        if (deadline != null) {
            task.setDateTime(CalendarProperty.END, deadline);
        }

        if (recurringPeriod != null) {
            task.setPeriod(recurringPeriod);
        }

        if (occurLimit == LIMIT_INF && !isLimitKept) {
            task.removeLimit();
        } else if (occurLimit == LIMIT_DATE && limitDate!=null) {
            task.setDateTime(CalendarProperty.DATE_LIMIT, limitDate.atStartOfDay());
        } else if (occurLimit != LIMIT_INF && occurLimit != LIMIT_DATE) {
            task.setOccurrenceLimit(occurLimit);
        }

        return new OldNewEntryPair(clone, task);
    }
}
