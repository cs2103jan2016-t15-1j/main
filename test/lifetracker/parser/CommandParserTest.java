package lifetracker.parser;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class CommandParserTest {

    private Set<String> commands = new HashSet<>();

    {
        commands.add("add");
        commands.add("testcommand");
    }

    private Map<String, Predicate<String>> keyWordVerification = new HashMap<>();

    {
        keyWordVerification.put("three", s -> s.length() == 3);
        keyWordVerification.put("empty", String::isEmpty);
        keyWordVerification.put("empty2", String::isEmpty);
        keyWordVerification.put("anything", s -> true);
    }

    private String defaultCommand = "add";

    private String fullCommandSeperator = "| ";

    private CommandParser cmdParser = new CommandParser(commands, defaultCommand);

    @Test
    public void testParseFullCommand() throws Exception {

        //Parition: Default (no) command with spaces
        String command = "abc testcommand";

        List<String> expected = new ArrayList<>();

        expected.add(defaultCommand);
        expected.add(command);

        Assert.assertEquals(expected, cmdParser.parseFullCommand(command, fullCommandSeperator));

        //Partition: Explicit command specified
        //Boundary: more than one space between words
        command = "testcommand  add  testcommand ";

        expected.clear();
        expected.add("testcommand");
        expected.add(" add  testcommand ");

        Assert.assertEquals(expected, cmdParser.parseFullCommand(command, fullCommandSeperator));

        //Partition: Command with mulitple sections
        command = "testcommand add testcommand |  add";
        expected.set(1, "add testcommand ");
        expected.add(" add");

        Assert.assertEquals(expected, cmdParser.parseFullCommand(command, fullCommandSeperator));

        expected.clear();

        //Boundary: Spaces after command itself
        //Boundary: Blank last section
        command = "testcommand   | add| two| ";
        expected.add("testcommand");
        expected.add("add");
        expected.add("two");

        Assert.assertEquals(expected, cmdParser.parseFullCommand(command, fullCommandSeperator));

        //Boundary: Spaces right after separator
        command = "testcommand|  abc |def";
        expected.clear();
        expected.add("testcommand");
        expected.add(" abc |def");

        Assert.assertEquals(expected, cmdParser.parseFullCommand(command, fullCommandSeperator));

    }

    @Test
    public void testParseCommandBody() throws Exception {

        //Partition: All keyword arguments valid
        String commandBody = "name empty three abc";

        Map<String, String> expected = new LinkedHashMap<>();

        expected.put("three", "abc");
        expected.put("empty", "");
        expected.put("name", "name");

        Assert.assertEquals(expected, cmdParser.parseCommandBody(commandBody, keyWordVerification));

        //Boundary: Blank name
        commandBody = "empty empty2";

        expected.clear();
        expected.put("empty2", "");
        expected.put("empty", "");
        expected.put("name", "");

        Assert.assertEquals(expected, cmdParser.parseCommandBody(commandBody, keyWordVerification));

        //Partition: repeated keywords
        commandBody = "name anything abs anything abc def three abc";

        expected.clear();
        expected.put("three", "abc");
        expected.put("anything", "abc def");
        expected.put("name", "name anything abs");

        Assert.assertEquals(expected, cmdParser.parseCommandBody(commandBody, keyWordVerification));

        //Partition: Invalid keyword arguements
        commandBody = "anything something something empty aaa";

        expected.clear();
        expected.put("name", "anything something something empty aaa");

        Assert.assertEquals(expected, cmdParser.parseCommandBody(commandBody, keyWordVerification));
    }
}