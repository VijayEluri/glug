package glug.model;

import glug.model.time.LogInstant;
import glug.model.time.LogInterval;
import glug.parser.ThreadId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class ThreadModel {
	
	private Map<IntervalTypeDescriptor, SignificantInstants> map =
		new HashMap<IntervalTypeDescriptor, SignificantInstants>();
	
	private final ThreadedSystem threadedSystem;

	private final ThreadId threadId;
	
	public ThreadModel(String name, ThreadedSystem threadedSystem) {
		this.threadId = new ThreadId(name);
		this.threadedSystem = threadedSystem;
	}
	

	public ThreadId getThreadId() {
		return threadId;
	}
	
	public void add(SignificantInterval significantInterval) {
		IntervalTypeDescriptor intervalTypeDescriptor = significantInterval.getType().getIntervalTypeDescriptor();
        if (!map.containsKey(intervalTypeDescriptor)) {
			map.put(intervalTypeDescriptor, new SignificantInstants());
        }
		map.get(intervalTypeDescriptor).add(significantInterval);
	}

	public LogInterval getInterval() {
		LogInterval interval = null;
		for (SignificantInstants significantInstants : map.values()) {
			interval = significantInstants.getLogInterval().union(interval);
		}
		return interval;
	}

	public SortedMap<IntervalTypeDescriptor,Collection<SignificantInterval>> getSignificantIntervalsFor(LogInterval logInterval) {
		SortedMap<IntervalTypeDescriptor,Collection<SignificantInterval>> significantIntervals = new TreeMap<IntervalTypeDescriptor,Collection<SignificantInterval>>();
		for (Map.Entry<IntervalTypeDescriptor, SignificantInstants> entry : map.entrySet()) {
			Collection<SignificantInterval> significantIntervalsDuringInterval = entry.getValue().getSignificantIntervalsDuring(logInterval);
			significantIntervals.put(entry.getKey(), significantIntervalsDuringInterval);
		}
		return significantIntervals;
	}

	public String getName() {
		return threadId.getName();
	}

	public SortedMap<IntervalTypeDescriptor, SignificantInterval> getSignificantIntervalsFor(LogInstant instant) {
		SortedMap<IntervalTypeDescriptor, SignificantInterval> significantIntervals = new TreeMap<IntervalTypeDescriptor,SignificantInterval>();
		for (SignificantInstants significantInstants : map.values()) {
			SignificantInterval significantIntervalAtInstant = significantInstants.getSignificantIntervalAt(instant);
			if (significantIntervalAtInstant!=null) {
				significantIntervals.put(significantIntervalAtInstant.getType().getIntervalTypeDescriptor(),significantIntervalAtInstant);
			}
		}
		return significantIntervals;
		
	}

	public ThreadedSystem getThreadedSystem() {
		return threadedSystem;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+"["+getInterval()+"]";
	}

}
