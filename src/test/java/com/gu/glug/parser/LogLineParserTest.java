package com.gu.glug.parser;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;

import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import com.gu.glug.SignificantInterval;
import com.gu.glug.SignificantIntervalOccupier;
import com.gu.glug.ThreadedSystem;
import com.gu.glug.parser.logmessages.CompletedPageRequest;
import com.gu.glug.parser.logmessages.LogMessageParserRegistry;

public class LogLineParserTest {

	private ThreadedSystem threadedSystem;
	private LogLineParser logLineParser;
	
	@Before
	public void setUp() {
		threadedSystem = new ThreadedSystem();
		logLineParser = new LogLineParser(new LogCoordinateParser(threadedSystem), LogMessageParserRegistry.EXAMPLE);
	}
	
	@Test
	public void shouldParseExampleFile() throws Exception {
		File file = new File("/home/roberto/development/glug/src/test/resources/r2frontend.respub01.first10000.log.gz");
		assert (file.exists());
		BufferedReader reader = new BufferedReader(new InputStreamReader( new GZIPInputStream(new FileInputStream(file))));
		String line;
		while ((line=reader.readLine())!=null) {
			logLineParser.parse(line);
		}
	}
	
	@Test
	public void shouldParsePageRequest() throws ParseException {
		String testInput = "2009-02-25 00:00:05,979 [resin-tcp-connection-respub.gul3.gnl:6802-197] INFO  com.gu.r2.common.webutil.RequestLoggingFilter - Request for /pages/Guardian/world/rss completed in 5 ms";
		SignificantInterval significantInterval = logLineParser.parse(testInput);
		assertThat(significantInterval.getThread().getName(), equalTo("resin-tcp-connection-respub.gul3.gnl:6802-197"));
		Interval interval = significantInterval.getInterval();
		assertThat(interval.toDurationMillis(),equalTo(5L));
		assertThat(interval.getStart().getInstant().toDateTime().getYear(),equalTo(2009));
		assertThat(significantInterval.getType(),equalTo((SignificantIntervalOccupier) new CompletedPageRequest("/pages/Guardian/world/rss")));
	}
	
	@Test
	public void shouldParsePageRequestWithoutThrowingADamnRuntimeException() throws ParseException {
		String testInput = "2009-02-25 00:00:00,539 [resin-tcp-connection-*:8080-631] INFO  com.gu.r2.common.webutil.RequestLoggingFilter - Request for /management/cache/clear completed in 470 ms";
		SignificantInterval significantInterval = logLineParser.parse(testInput);
		assertThat(significantInterval.getThread().getName(), equalTo("resin-tcp-connection-*:8080-631"));
		Interval interval = significantInterval.getInterval();
		assertThat(interval.toDurationMillis(),equalTo(470L));
		assertThat(interval.getStart().getInstant().toDateTime().getYear(),equalTo(2009));
	}
	
	@Test
	public void shouldNotParseLoggerNameIfMessageSplitIsNotFound() throws ParseException {
		LogCoordinateParser logCoordinateParser = mock(LogCoordinateParser.class);
		
		logLineParser = new LogLineParser(logCoordinateParser,null);
		logLineParser.parse("a log line that is full of junk");
		logLineParser.parse("a line that is too short - the dash normally comes much later");
		
		verify(logCoordinateParser,never()).getLoggerName(anyString(), anyInt());
	}

}
