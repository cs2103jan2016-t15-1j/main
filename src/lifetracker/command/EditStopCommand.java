package lifetracker.command;

import lifetracker.calendar.CalendarEntry;
import lifetracker.calendar.CalendarList;
import lifetracker.calendar.CalendarProperty;

//@@author A0091173J
public class EditStopCommand extends EditGenericTaskCommand {

    public EditStopCommand(int id, String name) {
        super(id, name, true);
    }

    @Override
    public CalendarList execute(CalendarList calendar) {

        CalendarEntry entryToEdit = calendar.get(id);

        if (entryToEdit.getDateTime(CalendarProperty.START) == null) {
            calendar.updateToDeadline(id, name, null, true);
        } else {
            calendar.updateToEvent(id, name, null, null, true);
        }

        setExecuted(true);
        setComment(String.format(MESSAGE_EDITED, id));

        return calendar;
    }
}
