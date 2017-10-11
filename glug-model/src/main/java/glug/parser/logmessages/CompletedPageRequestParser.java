package glug.parser.logmessages;

import glug.model.IntervalTypeDescriptor;
import org.joda.time.Duration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.awt.Color.RED;
import static java.lang.Integer.parseInt;

/*
2009-02-25 00:00:00,606 [resin-tcp-connection-respub.gul3.gnl:6802-39] INFO  com.gu.r2.common.webutil.RequestLoggingFilter - Request for /pages/Guardian/lifeandstyle completed in 712 ms
Request for /pages/Guardian/lifeandstyle completed in 712 ms
 */
public abstract class CompletedPageRequestParser {

    public static final IntervalTypeDescriptor PAGE_REQUEST = new IntervalTypeDescriptor(RED, "Page Request");

    private static final Pattern requestCompletedPattern = Pattern.compile("^Request for ([^ ]+?) completed in (\\d+?) ms$");

    public CompletedPageRequestParser(String loggerClassName) {
        //super(loggerClassName, requestCompletedPattern);
    }

    Duration durationFrom(Matcher matcher) {
        String durationInMillisText = matcher.group(2);
        return new Duration(parseInt(durationInMillisText));
    }

}
