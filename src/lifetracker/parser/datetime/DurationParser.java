package lifetracker.parser.datetime;

import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//@@author A0091173J
public class DurationParser {
    private static DurationParser ourInstance = new DurationParser();
    private static final String FORMAT_ERROR = "Invalid duration format!";
    private final String DURATION_PATTERN = "(\\d+\\s+)?\\w+";
    private final String TERM_SEPARATOR_PATTERN = "\\s+";
    
    public static DurationParser getInstance() {
        return ourInstance;
    }

    private Map<String, Function<Integer, Period>> parserMap = new HashMap<>();

    {
        parserMap.put("year", Period::ofYears);
        parserMap.put("month", Period::ofMonths);
        parserMap.put("week", Period::ofWeeks);
        parserMap.put("day", Period::ofDays);
    }

    private DurationParser() {
    }

    public boolean isDurationString(String durationString) {
        try {
            parse(durationString);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }

    public Period parse(String durationString) {

        if (!isValidFormat(durationString)) {
            throw new IllegalArgumentException(FORMAT_ERROR);
        }

        int num = getNum(durationString);
        String expression = getDurationExpression(durationString);

        if (parserMap.containsKey(expression) && num > 0) {
            return parserMap.get(expression).apply(num);
        } else {
            throw new IllegalArgumentException(FORMAT_ERROR);
        }
    }

    private boolean isValidFormat(String durationString) {
        return durationString != null && durationString.matches(DURATION_PATTERN);
    }

    private int getNum(String durationString) {
        String[] splitString = durationString.split(TERM_SEPARATOR_PATTERN);

        if (splitString.length == 1) {
            return 1;
        } else {
            return Integer.parseInt(splitString[0]);
        }
    }

    private String getDurationExpression(String durationString) {
        String[] splitString = durationString.split(TERM_SEPARATOR_PATTERN);

        if (splitString.length == 1) {
            return singularize(splitString[0]);
        } else {
            return singularize(splitString[1]);
        }
    }

    private String singularize(String word) {
        word = word.trim();

        if (!word.isEmpty() && word.charAt(word.length() - 1) == 's') {
            return word.substring(0, word.length() - 1);
        } else {
            return word;
        }
    }
}