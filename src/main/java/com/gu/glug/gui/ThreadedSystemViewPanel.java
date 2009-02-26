/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gu.glug.gui;

import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.joda.time.Instant;
import org.joda.time.Interval;

import com.gu.glug.SignificantInterval;
import com.gu.glug.ThreadModel;
import com.gu.glug.ThreadedSystem;

/**
 *
 * @author roberto
 */
public class ThreadedSystemViewPanel extends JComponent {

	private static final long serialVersionUID = 1L;
	private double magnifactionFactor = 0.1d;
	private ThreadedSystem threadedSystem;
	private Interval intervalCoveredByAllThreads;
	
    @Override
    public Dimension getPreferredSize() {
        int requiredWidth = (int) Math.ceil(getDrawDistanceFor(intervalCoveredByAllThreads));
		return new Dimension(requiredWidth,threadedSystem.getNumThreads());
    }

	public void setThreadedSystem(ThreadedSystem threadedSystem) {
		this.threadedSystem = threadedSystem;
		intervalCoveredByAllThreads = threadedSystem.getIntervalCoveredByAllThreads();
	}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        Rectangle clipBounds = graphics2D.getClipBounds();
        Interval visibleInterval=visibleIntervalFor(clipBounds);
        g.setColor(Color.RED);
        int threadIndex=0;
        for (ThreadModel threadModel : threadedSystem.getThreads()) {
        	for (SignificantInterval significantInterval : threadModel.getSignificantIntervalsFor(visibleInterval)) {
        		Interval aa = significantInterval.getInterval();
				g.drawLine(graphicsXFor(aa.getStart().toInstant()), -threadIndex, graphicsXFor(aa.getEnd().toInstant()), -threadIndex);
        	}
        	--threadIndex;
        }
        
    }


	private int graphicsXFor(Instant instant) {
		int graphicsX = (int) round(instant.minus(intervalCoveredByAllThreads.getStartMillis()).getMillis() * magnifactionFactor);
		return graphicsX;
	}

	private Interval visibleIntervalFor(Rectangle clipBounds) {
		return new Interval(instantFor(clipBounds.getMinX()),instantFor(clipBounds.getMaxX()));
	}

	private double getDrawDistanceFor(Interval interval) {
		return interval.toDurationMillis() * magnifactionFactor;
	}


	private Instant instantFor(double graphicsX) {
		return intervalCoveredByAllThreads.getStart().toInstant().plus(round(graphicsX/magnifactionFactor));
	}


}
