package lifetracker.logic;

import java.time.LocalDateTime;

import lifetracker.calendar.CalendarList;
import lifetracker.calendar.CalendarListImpl;
import lifetracker.command.CommandObject;

//@@author A0149467N-unused

public class LogicCommandStub implements CommandObject {
    
    private static final String MESSAGE_ADDED = "\"%1$s\" is added.";
    
    private final String name;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    
    private String comment = MESSAGE_ERROR;
    
    public LogicCommandStub(String name) {
        this.name = name;
        this.startDateTime = null;
        this.endDateTime = null;
    }
    
    public LogicCommandStub(String name, LocalDateTime dueDateTime) {
        this.name = name;
        this.startDateTime = null;
        this.endDateTime = dueDateTime;
    }
    
    public LogicCommandStub(String name, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.name = name;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
    
    @Override
    public CalendarList execute(CalendarList calendar){
        assert calendar != null;

        if (endDateTime == null) {
            calendar.add(name);
        } else if (startDateTime == null) {
            calendar.add(name, endDateTime);
        } else {
            calendar.add(name, startDateTime, endDateTime);
        }

        comment = String.format(MESSAGE_ADDED, name);

        return new CalendarListImpl();
    }
    
    @Override
    public CalendarList undo(CalendarList calendar){
        return null;
    }
    
    @Override
    public String getComment(){
        return this.comment;
    }
}
