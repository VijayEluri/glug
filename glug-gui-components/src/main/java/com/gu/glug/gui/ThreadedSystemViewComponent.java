package com.gu.glug.gui;

import static java.lang.Math.ceil;
import static java.lang.Math.round;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.Map.Entry;

import javax.swing.ToolTipManager;

import org.joda.time.Instant;
import org.joda.time.Interval;

import com.gu.glug.model.SignificantInterval;
import com.gu.glug.model.ThreadModel;
import com.gu.glug.model.ThreadedSystem;
import com.gu.glug.model.time.LogInstant;
import com.gu.glug.model.time.LogInterval;
import com.gu.glug.parser.logmessages.IntervalTypeDescriptor;

/**
 * 
 * @author roberto
 */
public class ThreadedSystemViewComponent extends TimelineComponent {

	private static final long serialVersionUID = 1L;
	private double millisecondsPerPixel = 0.25d;
	private ThreadedSystem threadedSystem;
	private LogInterval intervalCoveredByAllThreads;
	LogarithmicBoundedRange logarithmicBoundedRange;
	private final TimelineCursor timelineCursor;

	public ThreadedSystemViewComponent() {
		this(new ThreadedSystem(), new TimelineCursor());
	}
	
	public ThreadedSystemViewComponent(ThreadedSystem threadedSystem,
			TimelineCursor timelineCursor) {
		this.threadedSystem = threadedSystem;
		this.timelineCursor = timelineCursor;
		cacheIntervalCoveredByAllThreads();
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		makeResponsive(toolTipManager);
		toolTipManager.registerComponent(this);
		timelineCursor.install(this);
	}

	private void makeResponsive(ToolTipManager toolTipManager) {
		toolTipManager.setInitialDelay(20);
		toolTipManager.setReshowDelay(10);
		toolTipManager.setDismissDelay(10000);
	}

	@Override
	public Dimension getPreferredSize() {
		if (!containsData()) {
			return super.getPreferredSize();
		}
		int requiredWidth = (int) ceil(getDrawDistanceFor(intervalCoveredByAllThreads));
		return new Dimension(requiredWidth, threadedSystem.getNumThreads());
	}

	private boolean containsData() {
		return intervalCoveredByAllThreads != null;
	}

	@Override
	LogInterval getEntireInterval() {
		return getIntervalCoveredByAllThreads(true);
	}

	public LogInterval getIntervalCoveredByAllThreads(boolean update) {
		if (update) {
			cacheIntervalCoveredByAllThreads();
		}
		return intervalCoveredByAllThreads;
	}

	private void cacheIntervalCoveredByAllThreads() {
		intervalCoveredByAllThreads = threadedSystem
				.getIntervalCoveredByAllThreads();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics2D = (Graphics2D) g;
		Rectangle clipBounds = graphics2D.getClipBounds();
		cacheIntervalCoveredByAllThreads();
		if (containsData()) {
			Interval visibleInterval = visibleIntervalFor(clipBounds);
			paintThreadsFor(visibleInterval, g);
			timelineCursor.paintOn(this, graphics2D);
		}
	}

	private void paintThreadsFor(Interval visibleInterval, Graphics g) {
		int threadIndex = 0;
		for (ThreadModel threadModel : threadedSystem.getThreads()) {
			for (Entry<IntervalTypeDescriptor, Collection<SignificantInterval>> blah : threadModel
					.getSignificantIntervalsFor(visibleInterval).entrySet()) {
				IntervalTypeDescriptor intervalTypeDescriptor = blah.getKey();
				g.setColor(intervalTypeDescriptor.getColour());

				Collection<SignificantInterval> sigInts = blah.getValue();
				int size = sigInts.size();
				// System.out.println("size="+size);
				for (SignificantInterval significantInterval : sigInts) {
					LogInterval aa = significantInterval.getLogInterval();
					g.drawLine(graphicsXFor(aa.getStart().getRecordedInstant()),
							-threadIndex,
							graphicsXFor(aa.getEnd().getRecordedInstant()),
							-threadIndex);
				}
			}
			--threadIndex;
		}
	}

	void setMillisecondsPerPixel(double millisecondsPerPixel) {

		double oldMillisecondsPerPixel = this.millisecondsPerPixel;
		
		this.millisecondsPerPixel = millisecondsPerPixel;

		setSize(getPreferredSize());
		
		scrollViewToKeepCursorInSamePosition(oldMillisecondsPerPixel);

		// System.out.println("millisecondsPerPixel = "+millisecondsPerPixel);
		this.repaint();
	}

	private void scrollViewToKeepCursorInSamePosition(double oldMillisecondsPerPixel) {
		LogInstant cursorDot = getTimelineCursor().getDot();
		if (cursorDot != null) {
			int originalCursorHorizontalPositionInComponent = graphicsXFor(cursorDot.getRecordedInstant(), oldMillisecondsPerPixel);
			int updatedCursorHorizontalPositionInComponent = graphicsXFor(cursorDot.getRecordedInstant());
			int differenceInCursorHorizontalPositionInComponent = updatedCursorHorizontalPositionInComponent - originalCursorHorizontalPositionInComponent;
			Rectangle visibleRectangle = getVisibleRect();
			visibleRectangle.translate(differenceInCursorHorizontalPositionInComponent, 0);
			scrollRectToVisible(visibleRectangle);
		}
	}

	private int graphicsXFor(Instant instant) {
		return graphicsXFor(instant, millisecondsPerPixel);
	}

	private int graphicsXFor(Instant instant, double specifiedMillisPerPixel) {
		return (int) round((differenceInMillisFromStartOfIntervalCoveredByAllThreadsFor(instant))
				/ specifiedMillisPerPixel);
	}

	private long differenceInMillisFromStartOfIntervalCoveredByAllThreadsFor(
			Instant instant) {
		return instant.getMillis()
				- intervalCoveredByAllThreads.getStart().getMillis();
	}

	private Interval visibleIntervalFor(Rectangle clipBounds) {
		return new Interval(instantFor(clipBounds.getMinX()),
				instantFor(clipBounds.getMaxX()));
	}

	private double getDrawDistanceFor(LogInterval interval) {
		return interval.toDurationMillis() / millisecondsPerPixel;
	}

	private Instant instantFor(double graphicsX) {
		return intervalCoveredByAllThreads.getStart().getRecordedInstant().plus(
				round(graphicsX * millisecondsPerPixel));
	}

	public void repaint(Interval interval) {
		cacheIntervalCoveredByAllThreads();
		repaint(graphicsXFor(interval.getStart().toInstant()) - 1, 0,
				graphicsXFor(interval.getEnd().toInstant()) + 1, threadedSystem
						.getNumThreads());
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (!containsData()) {
			return null;
		}
		ThreadModel thread = threadFor(event.getPoint());
		if (thread == null) {
			return null;
		}
		Instant instant = instantFor(event.getX());
		SortedSet<SignificantInterval> significantIntervalsFor = thread
				.getSignificantIntervalsFor(new LogInstant(instant, 0));
		if (significantIntervalsFor.isEmpty()) {
			return null;
		}
		return "<html>" + significantIntervalsFor.toString() + "</html>";
	}

	private ThreadModel threadFor(Point point) {

		ArrayList<ThreadModel> threads = new ArrayList<ThreadModel>(
				threadedSystem.getThreads());
		int threadIndex = point.y;
		if (threadIndex >= 0 && threadIndex < threads.size()) {
			return threads.get(threadIndex);
		}
		return null;
	}

	@Override
	public LogInstant getLogInstantFor(Point point) {
		return new LogInstant(instantFor(point.x), 0);
	}

	@Override
	public TimelineCursor getTimelineCursor() {
		return timelineCursor;
	}

	@Override
	Rectangle getViewFor(LogInstant logInstant) {
		return new Rectangle(graphicsXFor(logInstant.getRecordedInstant()), 0, 0,
				getHeight());
	}

}
