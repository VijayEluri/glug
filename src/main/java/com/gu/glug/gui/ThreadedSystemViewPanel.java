/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gu.glug.gui;

import static java.lang.Math.ceil;
import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.joda.time.Instant;
import org.joda.time.Interval;

import com.gu.glug.SignificantInterval;
import com.gu.glug.ThreadModel;
import com.gu.glug.ThreadedSystem;
import com.gu.glug.parser.logmessages.CompletedPageRequest;
import com.gu.glug.time.LogInterval;

/**
 *
 * @author roberto
 */
public class ThreadedSystemViewPanel extends JComponent {

	private static final long serialVersionUID = 1L;
	private double magnifactionFactor = 0.1d;
	private ThreadedSystem threadedSystem;
	private LogInterval intervalCoveredByAllThreads;
	
	public ThreadedSystemViewPanel(ThreadedSystem threadedSystem) {
		this.threadedSystem = threadedSystem;
		cacheIntervalCoveredByAllThreads();
	}
	
    @Override
    public Dimension getPreferredSize() {
    	if (intervalCoveredByAllThreads==null) {
    		return super.getPreferredSize();
    	}
        int requiredWidth = (int) ceil(getDrawDistanceFor(intervalCoveredByAllThreads));
		return new Dimension(requiredWidth,threadedSystem.getNumThreads());
    }

	private void cacheIntervalCoveredByAllThreads() {
		intervalCoveredByAllThreads = threadedSystem.getIntervalCoveredByAllThreads();
	}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        Rectangle clipBounds = graphics2D.getClipBounds();
        cacheIntervalCoveredByAllThreads();
        if (intervalCoveredByAllThreads!=null) {
	        Interval visibleInterval=visibleIntervalFor(clipBounds);
	        int threadIndex=0;
	        for (ThreadModel threadModel : threadedSystem.getThreads()) {
	        	for (SignificantInterval significantInterval : threadModel.getSignificantIntervalsFor(visibleInterval)) {
	        		LogInterval aa = significantInterval.getLogInterval();
	        		if (significantInterval.getType() instanceof CompletedPageRequest) {
	        			g.setColor(Color.RED);		
	        		} else {
	        			g.setColor(Color.BLACK);
	        		}
	        		g.drawLine(graphicsXFor(aa.getStart().getInstant()), -threadIndex, graphicsXFor(aa.getEnd().getInstant()), -threadIndex);
	        	}
	        	--threadIndex;
	        }
        }
        
    }

    void setMagnification(double d) {
        this.magnifactionFactor = d;
        this.repaint();
    }


	private int graphicsXFor(Instant instant) {
		
		int graphicsX = (int) round((differenceInMillisFromStartOfIntervalCoveredByAllThreadsFor(instant)) * magnifactionFactor);
		return graphicsX;
	}

	private long differenceInMillisFromStartOfIntervalCoveredByAllThreadsFor(Instant instant) {
		return instant.getMillis() - intervalCoveredByAllThreads.getStart().getMillis();
	}

	private Interval visibleIntervalFor(Rectangle clipBounds) {
		return new Interval(instantFor(clipBounds.getMinX()),instantFor(clipBounds.getMaxX()));
	}

	private double getDrawDistanceFor(LogInterval interval) {
		return interval.toDurationMillis() * magnifactionFactor;
	}


	private Instant instantFor(double graphicsX) {
		return intervalCoveredByAllThreads.getStart().getInstant().plus(round(graphicsX/magnifactionFactor));
	}

	public void repaint(Interval interval) {
		cacheIntervalCoveredByAllThreads();
		repaint(graphicsXFor(interval.getStart().toInstant())-1, 0, graphicsXFor(interval.getEnd().toInstant())+1, threadedSystem.getNumThreads());
	}


}
