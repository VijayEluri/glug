package glug.gui.zoom;

import glug.gui.UITimeScale;
import glug.gui.timelinecursor.TimelineCursor;
import glug.model.time.LogInstant;
import org.joda.time.Instant;
import org.joda.time.Interval;

import javax.swing.*;

public class ZoomFocusFinder {

    private final JViewport viewport;
    private final UITimeScale uiTimeScale;
    private final TimelineCursor timelineCursor;

    public ZoomFocusFinder(TimelineCursor timelineCursor, JViewport viewport, UITimeScale uiTimeScale) {
        this.timelineCursor = timelineCursor;
        this.viewport = viewport;
        this.uiTimeScale = uiTimeScale;
    }

    public Instant instantToZoomAround() {
        Instant instantToZoomAround = null;
        Interval visibleInterval = uiTimeScale.viewToModel(viewport.getViewRect());
        LogInstant dot = timelineCursor.getDot();
        if (dot != null && visibleInterval.contains(dot.getRecordedInstant())) {
            instantToZoomAround = dot.getRecordedInstant();
        }
        if (instantToZoomAround == null) {
            instantToZoomAround = new Instant(visibleInterval.getStartMillis()
                    + visibleInterval.toDurationMillis() / 2);
        }
        return instantToZoomAround;
    }
}
