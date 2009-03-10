package glug.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.nullValue;
import static org.joda.time.Duration.standardSeconds;
import static org.mockito.MockitoAnnotations.initMocks;
import glug.model.time.LogInstant;
import glug.model.time.LogInterval;

import java.util.Collection;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;



public class SignificantInstantsTest {

	ThreadModel thread;
	SignificantInstants significantInstants;
	
	@Mock SignificantIntervalOccupier sio;
	
	@Before
	public void setUp() {
		initMocks(this);
		thread = new ThreadModel("blahThread", null);
		significantInstants = new SignificantInstants();
	}
	
	@Test
	public void shouldNotReturnASignificantIntervalMoreThanOnceInTheResultSet() {
		LogInterval logIntervalForSignificantInterval = new LogInterval(standardSeconds(1),new LogInstant(3000,0));
		SignificantInterval significantInterval = new SignificantInterval(thread, sio, logIntervalForSignificantInterval);
		
		significantInstants.add(significantInterval);
		
		LogInterval searchIntervalContainingEvent = new LogInterval(standardSeconds(5),new LogInstant(5000,5));
		
		Collection<SignificantInterval> significantIntervalsDuringSearchInterval = significantInstants.getSignificantIntervalsDuring(searchIntervalContainingEvent);
		assertThat(significantIntervalsDuringSearchInterval.size(), equalTo(1));
		assertThat(significantIntervalsDuringSearchInterval, hasItem(significantInterval));
	}
	
	@Test
	public void shouldAcceptSignificantIntervalsWhichHaveSameRecordedTimeSoLongAsTheyDoNotOverlap() {
		Instant endOfACrowdedMillisecond = new Instant(1234L);
		int logLine=345;
		
		SignificantInterval si1 = new SignificantInterval(null, sio, new LogInterval(Duration.ZERO,new LogInstant(endOfACrowdedMillisecond,logLine++)));
		SignificantInterval si2 = new SignificantInterval(null, sio, new LogInterval(Duration.ZERO,new LogInstant(endOfACrowdedMillisecond,logLine++)));
		SignificantInterval si3 = new SignificantInterval(null, sio, new LogInterval(Duration.ZERO,new LogInstant(endOfACrowdedMillisecond,logLine++)));
		
		significantInstants.add(si1);
		significantInstants.add(si2);
		significantInstants.add(si3);
		
		Collection<SignificantInterval> storedSignificantIntervals = significantInstants.getSignificantIntervalsDuring(new LogInterval(standardSeconds(1),new LogInstant(endOfACrowdedMillisecond,logLine++)));
		
		assertThat(storedSignificantIntervals, hasItems(si1,si2,si3));
	}
	
	@Test
	public void shouldReturnASignificantIntervalWhichStartsAndEndsOutsideOfTheRequestedBounds() {
		LogInterval logIntervalForSignificantInterval = new LogInterval(standardSeconds(5),new LogInstant(5000,0));
		SignificantInterval significantInterval = new SignificantInterval(thread, sio, logIntervalForSignificantInterval);
		
		significantInstants.add(significantInterval);
		
		LogInterval searchIntervalEntirelyWithinDurationOfEvent = new LogInterval(standardSeconds(1),new LogInstant(3000,3));
		
		assertThat(significantInstants.getSignificantIntervalsDuring(searchIntervalEntirelyWithinDurationOfEvent),hasItem(significantInterval));
	}
	
	@Test
	public void shouldOverrideOtherIntervals() throws Exception {
		SignificantInterval si1 = new SignificantInterval(thread, sio, new LogInterval(standardSeconds(3),new LogInstant(3000,3)));
		
		significantInstants.overrideWith(si1);
		SignificantInterval si2 = new SignificantInterval(thread, sio, new LogInterval(standardSeconds(5),new LogInstant(5000,5)));
		significantInstants.overrideWith(si2);
		
		assertThat(significantInstants.getSignificantIntervalAt(new LogInstant(1000,1)), equalTo(si2));
	}
	
	@Test
	public void shouldGetLatestSignificantIntervalStartingAtOrBefore() {
		SignificantInterval significantInterval = new SignificantInterval(null, null, new LogInterval(standardSeconds(3),new LogInstant(5000)));
		significantInstants.add(significantInterval);
		
		assertThat(significantInstants.getLatestSignificantIntervalStartingAtOrBefore(new Instant(1000)), nullValue());
		assertThat(significantInstants.getLatestSignificantIntervalStartingAtOrBefore(new Instant(2000)), equalTo(significantInterval));
		assertThat(significantInstants.getLatestSignificantIntervalStartingAtOrBefore(new Instant(3000)), equalTo(significantInterval));
		assertThat(significantInstants.getLatestSignificantIntervalStartingAtOrBefore(new Instant(6000)), equalTo(significantInterval));
	}
}
