package lifetracker.command;

import lifetracker.calendar.CalendarList;
import lifetracker.calendar.visitor.OldNewEntryPair;

//@@author A0091173J
public class MarkCommand extends CommandObject {

    private static final String MESSAGE_MARKED_DONE = "\"%1$d\" marked.";
    private static final String MESSAGE_MARKED_UNDONE = "\"%1$d\" unmarked.";

    private final int entryId;

    public MarkCommand(int entryId) {
        this.entryId = entryId;
    }

    @Override
    public CalendarList execute(CalendarList calendar) {
        OldNewEntryPair markedEntry = calendar.mark(entryId);
        setComment(String.format(MESSAGE_MARKED_DONE, entryId));

        return super.execute(calendar);
    }

    @Override
    public CalendarList undo(CalendarList calendar) {
        calendar.mark(entryId);
        setComment(String.format(MESSAGE_MARKED_UNDONE, entryId));

        return super.undo(calendar);
    }
}
