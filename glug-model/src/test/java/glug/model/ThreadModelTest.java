package glug.model;

import glug.model.time.LogInstant;
import glug.model.time.LogInterval;
import org.junit.Test;
import org.threeten.extra.Interval;

import java.time.Duration;
import java.util.Collections;

import static com.google.common.collect.ImmutableMap.of;
import static java.time.Instant.ofEpochMilli;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ThreadModelTest {
    @Test
    public void shouldReturnEmptySetIfNoBuggerMatches() throws Exception {
        ThreadModel thread = new ThreadModel("blahthread", null);
        new SignificantInterval(of("type", "My Type"),
                new LogInterval(Duration.ofSeconds(1), new LogInstant(1000, 1)));

        LogInstant instantWhereNoDamnThingWasHappening = new LogInstant(5000, 5);

        assertThat(thread.getSignificantIntervalsFor(instantWhereNoDamnThingWasHappening), equalTo(Collections.<Object, SignificantInterval>emptyMap()));
    }

    @Test
    public void shouldBoggleToAswad() {
        ThreadModel thread = new ThreadModel("blahthread", null);
        thread.add(new SignificantInterval(of("type", "My Type"), new LogInterval(Interval.of(ofEpochMilli(3000), ofEpochMilli(7000)))));
        assertThat(thread.countOccurencesDuring(new LogInterval(Interval.of(ofEpochMilli(2000), ofEpochMilli(8000))), "My Type").get("My Type"), equalTo(1));

    }

}
