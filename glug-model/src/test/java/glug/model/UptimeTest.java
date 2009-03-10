package glug.model;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.joda.time.Duration.standardSeconds;
import static org.junit.Assert.assertThat;
import glug.model.time.LogInstant;
import glug.model.time.LogInterval;

import org.joda.time.Instant;
import org.junit.Test;


public class UptimeTest {
	@Test
	public void shouldReturnCorrectUptimeForInstant() throws Exception {
		Uptime uptime = new Uptime();
		uptime.addUptime(new SignificantInterval(null,null, new LogInterval(standardSeconds(6), new LogInstant(10000))));
		
		assertThat(uptime.at(new Instant(3000)), nullValue());
		assertThat(uptime.at(new Instant(4000)), equalTo(standardSeconds(0)));
		assertThat(uptime.at(new Instant(5000)), equalTo(standardSeconds(1)));
		assertThat(uptime.at(new Instant(10000)), equalTo(standardSeconds(6)));
		assertThat(uptime.at(new Instant(11000)), equalTo(standardSeconds(7)));
	}
}
