package lifetracker.calendar;
import java.time.*;

public interface Event {

	//get() and set() functions for variables
	String getName();

	void setName(String name);

	LocalDateTime getStart();

	void setStart(LocalDateTime start);

	LocalDateTime getEnd();

	void setEnd(LocalDateTime end);

	LocalTime getStartTime();

	LocalTime getEndTime();

	boolean isToday();

	boolean isOngoing();

	boolean isOver();

}
