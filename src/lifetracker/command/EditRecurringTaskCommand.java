package lifetracker.command;

import lifetracker.calendar.CalendarList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

//@@author A0091173J

/**
 * A {@code CommandObject} edits recurring period and limit of an task entry in the calendar.
 * <p>
 * If the entry was not a recurring task beforehand, it will be converted to a recurring task.
 */
public class EditRecurringTaskCommand extends EditRecurringEntryCommand {

    protected final LocalDateTime endDateTime;

    public EditRecurringTaskCommand(int id, String name, LocalDateTime endDateTime, Period recurringPeriod,
            boolean isForcedConvert) {
        super(id, name, recurringPeriod, isForcedConvert);
        this.endDateTime = endDateTime;
    }

    public EditRecurringTaskCommand(int id, String name, LocalDateTime endDateTime, Period recurringPeriod,
            int occurLimit) {
        super(id, name, recurringPeriod, occurLimit);
        this.endDateTime = endDateTime;
    }

    public EditRecurringTaskCommand(int id, String name, LocalDateTime endDateTime, Period recurringPeriod,
            LocalDate dateLimit) {
        super(id, name, recurringPeriod, dateLimit);
        this.endDateTime = endDateTime;
    }

    @Override
    public CalendarList execute(CalendarList calendar) {
        if (occurLimit == OCCUR_INF) {
            oldEntry = calendar.updateToRecurringTask(id, name, endDateTime, recurringPeriod, !isForcedConvert, true);
        } else if (occurLimit == OCCUR_DATE) {
            oldEntry = calendar
                    .updateToRecurringTask(id, name, endDateTime, recurringPeriod, dateLimit, true);
        } else {
            oldEntry = calendar.updateToRecurringTask(id, name, endDateTime, recurringPeriod, occurLimit, true);
        }

        addHighlightEntry(id);

        setExecuted(true);
        setComment(String.format(MESSAGE_EDITED, id));
        return calendar;
    }
}
