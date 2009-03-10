package glug.parser.logmessages;

import static java.awt.Color.YELLOW;
import glug.model.SignificantIntervalOccupier;


public class JVMUptime implements SignificantIntervalOccupier {

	private static final IntervalTypeDescriptor intervalTypeDescriptor = new IntervalTypeDescriptor(-1,YELLOW,"JVM Uptime");
	
	
	@Override
	public IntervalTypeDescriptor getIntervalTypeDescriptor() {
		return intervalTypeDescriptor;
	}

}
