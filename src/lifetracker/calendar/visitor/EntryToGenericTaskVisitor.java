package lifetracker.calendar.visitor;

import lifetracker.calendar.CalendarEntry;
import lifetracker.calendar.DeadlineTask;
import lifetracker.calendar.Event;
import lifetracker.calendar.GenericEntry;
import lifetracker.calendar.RecurringEvent;
import lifetracker.calendar.RecurringTask;

//@@author A0091173J

public class EntryToGenericTaskVisitor implements EntryVisitor<OldNewEntryPair> {

    private String name;
    private boolean isConvertForced;

    public EntryToGenericTaskVisitor(String name, boolean isConvertForced) {
        this.name = name;
        this.isConvertForced = isConvertForced;
    }

    @Override
    public OldNewEntryPair visit(GenericEntry entry) {
        GenericEntry clone = new GenericEntry(entry);

        return edit(clone, entry);
    }

    @Override
    public OldNewEntryPair visit(DeadlineTask task) {
        DeadlineTask clone = new DeadlineTask(task);

        return edit(clone, task);
    }

    @Override
    public OldNewEntryPair visit(RecurringTask task) {
        RecurringTask clone = new RecurringTask(task);

        return edit(clone, task);
    }

    @Override
    public OldNewEntryPair visit(Event event) {
        Event clone = new Event(event);

        return edit(clone, event);
    }

    @Override
    public OldNewEntryPair visit(RecurringEvent event) {
        RecurringEvent clone = new RecurringEvent(event);

        return edit(clone, event);
    }

    private OldNewEntryPair edit(CalendarEntry clone, GenericEntry newEntry) {
        GenericEntry convertedEntry = newEntry;
        if (isConvertForced){
            convertedEntry = new GenericEntry(convertedEntry);
        }

        if (name != null && !name.isEmpty()) {
            convertedEntry.setName(name);
        }

        return new OldNewEntryPair(clone, convertedEntry);
    }
}
