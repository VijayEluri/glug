package com.gu.glug.gui;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.joda.time.MutableDateTime.Property;

public class TickInterval {

	private final DateTimeFieldType dateTimeFieldType;
	private final int value;
	private final Duration duration;

	public TickInterval(DateTimeFieldType dateTimeFieldType, int value) {
		this.dateTimeFieldType = dateTimeFieldType;
		this.value = value;
		this.duration = new Period().withField(dateTimeFieldType.getDurationType(), value).toStandardDuration();
	}
	
	public Duration getDuration() {
		return duration;
	}

	public DateTime floor(DateTime dateTime) {
		MutableDateTime mutableDateTime = dateTime.toMutableDateTime();
		Property fieldProperty = mutableDateTime.property(dateTimeFieldType);
		fieldProperty.roundFloor();
		int fieldMinimumValue=fieldProperty.getMinimumValue();
		fieldProperty.set((((fieldProperty.get()-fieldMinimumValue)/value)*value)+fieldMinimumValue);
		return mutableDateTime.toDateTime();
	}
	
	public static TickInterval tick(int value, DateTimeFieldType dateTimeFieldType) {
		return new TickInterval(dateTimeFieldType, value);
	}

	public Iterator<DateTime> ticksFor(final Interval interval) {
		return new Iterator<DateTime>() {

			DateTime dateTime = floor(interval.getStart());
			
			@Override
			public boolean hasNext() {
				return dateTime!=null;
			}

			@Override
			public DateTime next() {
				DateTime dateTimeToReturn = dateTime;
				dateTime=dateTime.property(dateTimeFieldType).addToCopy(value);
				if (dateTimeToReturn.isAfter(interval.getEnd())) {
					dateTime=null;
				}
				return dateTimeToReturn;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
