package com.gu.glug.gui;

import static java.awt.event.ActionEvent.SHIFT_MASK;
import static javax.swing.SwingUtilities.isLeftMouseButton;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.event.MouseInputAdapter;

import com.gu.glug.model.time.LogInstant;

public class TimelineCursorMouseInputListener extends MouseInputAdapter {

	private final TimelineComponent timelineComponent;
	private final TimelineCursor cursor;

	public TimelineCursorMouseInputListener(TimelineComponent timelineComponent) {
		this.timelineComponent = timelineComponent;
		this.cursor = timelineComponent.getTimelineCursor();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (willAcceptEvent(e)) {
			if (shiftPressedFor(e)) {
				moveCursorToLocationOf(e);
			} else {
				setCursorToLocationOf(e);
			}
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (willAcceptEvent(e)) {
			moveCursorToLocationOf(e);
		}
	}

	private boolean willAcceptEvent(MouseEvent e) {
		return isLeftMouseButton(e) && timelineComponent.containsData();
	}
	private void setCursorToLocationOf(MouseEvent e) {
		cursor.setDot(logInstantFor(e));
	}
	
	private void moveCursorToLocationOf(MouseEvent e) {
		cursor.moveDot(logInstantFor(e));
	}

	private LogInstant logInstantFor(MouseEvent e) {
		return timelineComponent.getLogInstantFor(e.getPoint());
	}
	

	private boolean shiftPressedFor(MouseEvent e) {
		return (e.getModifiers() & SHIFT_MASK) != 0;
	}
}
