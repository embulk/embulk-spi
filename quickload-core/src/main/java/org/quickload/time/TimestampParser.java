package org.quickload.time;

import org.joda.time.DateTimeZone;
import org.jruby.embed.ScriptingContainer;
import org.quickload.config.ConfigException;
import static org.quickload.time.TimestampFormatConfig.parseDateTimeZone;

public class TimestampParser
{
    private final JRubyTimeParserHelper helper;
    private final DateTimeZone defaultTimeZone;

    public TimestampParser(ScriptingContainer jruby, String format, TimestampParserTask task)
    {
        JRubyTimeParserHelperFactory helperFactory = (JRubyTimeParserHelperFactory) jruby.runScriptlet("QuickLoad::Java::TimeParserHelper::Factory.new");
        // TODO get default current time from ExecTask.getExecTimestamp
        this.helper = (JRubyTimeParserHelper) helperFactory.newInstance(format, 1970, 1, 1, 0, 0, 0, 0);  // TODO default time zone
        this.defaultTimeZone = task.getDefaultTimeZone();
    }

    public Timestamp parse(String text) throws TimestampParseException
    {
        long localMillis = helper.strptime(text);
        String zone = helper.getZone();

        DateTimeZone timeZone = defaultTimeZone;
        if (zone != null) {
            // TODO cache parsed zone?
            timeZone = parseDateTimeZone(zone);
            if (timeZone == null) {
                throw new TimestampParseException();
            }
        }

        long milli = timeZone.convertLocalToUTC(localMillis, false);

        return Timestamp.ofEpochMilli(milli);
    }
}
