package lifetracker.logic;

import lifetracker.calendar.CalendarList;
import lifetracker.command.AddCommand;
import lifetracker.logic.ExecuteResult.CommandType;
import lifetracker.parser.Parser;
import lifetracker.storage.Storage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@@author A0149467N

public class LogicImplTest {

    private static final String ERROR_INVALID_COMMAND = "Invalid Command: null";
    
    //test store constant
    private static final String DEFAULT_CONFIG_FILENAME = "config.properties";
    private static final String ALT_CONFIG_FILENAME = "config.properties.orig";
    private static final String DEFAULT_TEST_STORE = "lifetracker.dat";
    private static final String ALT_TEST_STORE = "lifetracker.dat.orig";
    private static final String TEST_SAVEAT_FILE = "location";

    private static Parser parser = mock(Parser.class);
    private static Storage storage = mock(Storage.class);
    private static LogicImpl logicTest;

    private static ExecuteResult expected1 = new CommandLineResult();
    private static ExecuteResult expected2 = new CommandLineResult();
    private static ExecuteResult expected3 = new CommandLineResult();

    private static ExecuteResult saveat = new CommandLineResult();
    private static ExecuteResult exit = new CommandLineResult();
    private static ExecuteResult help = new CommandLineResult();
    private static ExecuteResult error = new CommandLineResult();

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        File origFile = new File(DEFAULT_CONFIG_FILENAME);

        if (origFile.exists()) {
            origFile.renameTo(new File(ALT_CONFIG_FILENAME));
        }

        origFile = new File(DEFAULT_TEST_STORE);

        if (origFile.exists()) {
            origFile.renameTo(new File(ALT_TEST_STORE));
        }

        when(storage.load()).thenReturn("");

        logicTest = new LogicImpl(parser, storage);

        saveat.setComment("Calendar is saved at " + TEST_SAVEAT_FILE);
        saveat.setType(CommandType.SAVE);
        
        exit.setType(CommandType.EXIT);
        help.setType(CommandType.HELP);
        
        expected1.setComment("\"first meeting\" is added.");
        expected1.setType(CommandType.DISPLAY);

        expected2.setComment("\"second meeting\" is added.");
        expected2.setType(CommandType.DISPLAY);

        expected3.setComment("\"third meeting\" is added.");
        expected3.setType(CommandType.DISPLAY);

        error.setComment(ERROR_INVALID_COMMAND);
        error.setType(CommandType.ERROR);
    }

    @AfterClass
    public static void tearDown() {
        File testStore = new File(TEST_SAVEAT_FILE);

        if (testStore.exists()) {
            testStore.delete();
        }

        testStore = new File(DEFAULT_CONFIG_FILENAME);
        if(testStore.exists()){
            testStore.delete();
        }

        testStore = new File(DEFAULT_TEST_STORE);
        if(testStore.exists()){
            testStore.delete();
        }

        File origFile = new File(ALT_CONFIG_FILENAME);

        if (origFile.exists()) {
            origFile.renameTo(new File(DEFAULT_CONFIG_FILENAME));
        }

        origFile = new File(ALT_TEST_STORE);

        if (origFile.exists()) {
            origFile.renameTo(new File(DEFAULT_TEST_STORE));
        }

    }

    @Test
    public void testSaveat() {
        assertEquals(saveat.getComment(), logicTest.executeCommand("saveat " + TEST_SAVEAT_FILE).getComment());
        assertEquals(saveat.getEventList(), logicTest.executeCommand("saveat " + TEST_SAVEAT_FILE).getEventList());
        assertEquals(saveat.getTaskList(), logicTest.executeCommand("saveat " + TEST_SAVEAT_FILE).getTaskList());
        assertEquals(saveat.getType(), logicTest.executeCommand("saveat " + TEST_SAVEAT_FILE).getType());
    }
    
    @Test
    public void testExit() {
        assertEquals(exit.getComment(), logicTest.executeCommand("exit").getComment());
        assertEquals(exit.getEventList(), logicTest.executeCommand("exit").getEventList());
        assertEquals(exit.getTaskList(), logicTest.executeCommand("exit").getTaskList());
        assertEquals(exit.getType(), logicTest.executeCommand("exit").getType());
    }
    
    @Test
    public void testHelp() {
        assertEquals(help.getComment(), logicTest.executeCommand("help").getComment());
        assertEquals(help.getEventList(), logicTest.executeCommand("help").getEventList());
        assertEquals(help.getTaskList(), logicTest.executeCommand("help").getTaskList());
        assertEquals(help.getType(), logicTest.executeCommand("help").getType());
    }

    @Test
    public void testAddFloating() {
        AddCommand object = mock(AddCommand.class);
        CalendarList list = mock(CalendarList.class);

        when(object.getComment()).thenReturn("\"first meeting\" is added.");
        when(object.execute(any(CalendarList.class))).thenReturn(list);
        when(parser.parse("add first meeting")).thenReturn(object);

        ExecuteResult actual = logicTest.executeCommand("add first meeting");
        verify(parser).parse("add first meeting");

        assertEquals(expected1.getComment(), actual.getComment());
        assertEquals(expected1.getEventList(), actual.getEventList());
        assertEquals(expected1.getTaskList(), actual.getTaskList());
        assertEquals(expected1.getType(), actual.getType());
    }

    //This is the upper bound of the boundary case for the valid partition
    @Test
    public void testAddDeadline() {
        AddCommand object = mock(AddCommand.class);
        CalendarList list = mock(CalendarList.class);

        when(object.getComment()).thenReturn("\"second meeting\" is added.");
        when(object.execute(any(CalendarList.class))).thenReturn(list);
        when(parser.parse("add second meeting by 2016-12-31 23:59:59")).thenReturn(object);

        ExecuteResult actual = logicTest.executeCommand("add second meeting by 2016-12-31 23:59:59");
        verify(parser).parse("add second meeting by 2016-12-31 23:59:59");

        assertEquals(expected2.getComment(), actual.getComment());
        assertEquals(expected2.getEventList(), actual.getEventList());
        assertEquals(expected2.getTaskList(), actual.getTaskList());
        assertEquals(expected2.getType(), actual.getType());
    }

    //This is the lower bound of the boundary case for the valid partition
    @Test
    public void testAddEvent() {
        AddCommand object = mock(AddCommand.class);
        CalendarList list = mock(CalendarList.class);

        when(object.getComment()).thenReturn("\"third meeting\" is added.");
        when(object.execute(any(CalendarList.class))).thenReturn(list);
        when(parser.parse("add third meeting from 2017-01-01 00:00:00 to 2017-01-01 23:59:59")).thenReturn(object);

        ExecuteResult actual = logicTest
                .executeCommand("add third meeting from 2017-01-01 00:00:00 to 2017-01-01 23:59:59");
        verify(parser).parse("add third meeting from 2017-01-01 00:00:00 to 2017-01-01 23:59:59");

        assertEquals(expected3.getComment(), actual.getComment());
        assertEquals(expected3.getEventList(), actual.getEventList());
        assertEquals(expected3.getTaskList(), actual.getTaskList());
        assertEquals(expected3.getType(), actual.getType());
    }

    /*----------test adding error event----------*/
    @Test
    public void testAddError() {
        when(parser.parse("add error meeting by 2016-00-31 23:59:59")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-13-31 23:59:59")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-12-00 23:59:59")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-12-32 23:59:59")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-12-31 -1:59:59")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-12-31 24:59:59")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-12-31 23:-1:59")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-12-31 23:60:59")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-12-31 23:59:-1")).thenThrow(new IllegalArgumentException());
        when(parser.parse("add error meeting by 2016-12-31 23:59:60")).thenThrow(new IllegalArgumentException());

        //test error month
        //This is the boundary case for the invalid partition
        ExecuteResult actual1 = logicTest.executeCommand("add error meeting by 2016-00-31 23:59:59");
        verify(parser).parse("add error meeting by 2016-00-31 23:59:59");
        assertEquals(error.getComment(), actual1.getComment());
        assertEquals(error.getType(), actual1.getType());

        ExecuteResult actual2 = logicTest.executeCommand("add error meeting by 2016-13-31 23:59:59");
        verify(parser).parse("add error meeting by 2016-13-31 23:59:59");
        assertEquals(error.getComment(), actual2.getComment());
        assertEquals(error.getType(), actual2.getType());

        //test error day
        //This is the boundary case for the invalid partition
        ExecuteResult actual3 = logicTest.executeCommand("add error meeting by 2016-12-00 23:59:59");
        verify(parser).parse("add error meeting by 2016-12-00 23:59:59");
        assertEquals(error.getComment(), actual3.getComment());
        assertEquals(error.getType(), actual3.getType());

        ExecuteResult actual4 = logicTest.executeCommand("add error meeting by 2016-12-32 23:59:59");
        verify(parser).parse("add error meeting by 2016-12-32 23:59:59");
        assertEquals(error.getComment(), actual4.getComment());
        assertEquals(error.getType(), actual4.getType());

        //test error hour
        //This is the boundary case for the invalid partition
        ExecuteResult actual5 = logicTest.executeCommand("add error meeting by 2016-12-31 -1:59:59");
        verify(parser).parse("add error meeting by 2016-12-31 -1:59:59");
        assertEquals(error.getComment(), actual5.getComment());
        assertEquals(error.getType(), actual5.getType());

        ExecuteResult actual6 = logicTest.executeCommand("add error meeting by 2016-12-31 24:59:59");
        verify(parser).parse("add error meeting by 2016-12-31 24:59:59");
        assertEquals(error.getComment(), actual6.getComment());
        assertEquals(error.getType(), actual6.getType());

        //test error minute
        //This is the boundary case for the invalid partition
        ExecuteResult actual7 = logicTest.executeCommand("add error meeting by 2016-12-31 23:-1:59");
        verify(parser).parse("add error meeting by 2016-12-31 23:-1:59");
        assertEquals(error.getComment(), actual7.getComment());
        assertEquals(error.getType(), actual7.getType());

        ExecuteResult actual8 = logicTest.executeCommand("add error meeting by 2016-12-31 23:60:59");
        verify(parser).parse("add error meeting by 2016-12-31 23:60:59");
        assertEquals(error.getComment(), actual8.getComment());
        assertEquals(error.getType(), actual8.getType());

        //test error second
        //This is the boundary case for the invalid partition
        ExecuteResult actual9 = logicTest.executeCommand("add error meeting by 2016-12-31 23:59:-1");
        verify(parser).parse("add error meeting by 2016-12-31 23:59:-1");
        assertEquals(error.getComment(), actual9.getComment());
        assertEquals(error.getType(), actual9.getType());

        ExecuteResult actual10 = logicTest.executeCommand("add error meeting by 2016-12-31 23:59:60");
        verify(parser).parse("add error meeting by 2016-12-31 23:59:60");
        assertEquals(error.getComment(), actual10.getComment());
        assertEquals(error.getType(), actual10.getType());
    }
}
