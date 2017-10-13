package glug.gui.zoom;

import glug.gui.UITimeScale;
import glug.gui.model.LogarithmicBoundedRange;
import org.threeten.extra.Interval;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class ZoomFactorSlideUpdater {
    private final LogarithmicBoundedRange logarithmicBoundedRange;
    private final JViewport viewport;
    private final UITimeScale timeScale;

    public ZoomFactorSlideUpdater(UITimeScale timeScale, LogarithmicBoundedRange logarithmicBoundedRange, JViewport viewport) {
        this.viewport = viewport;
        this.timeScale = timeScale;
        this.logarithmicBoundedRange = logarithmicBoundedRange;

        updateSliderBounds();

        timeScale.addChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateSliderValue();
            }
        });

        viewport.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSliderMax();
            }
        });
    }

    private void updateSliderValue() {
        // We should be safe from an infinite recursive loop because LogarithmicBoundedRange round-trips
        logarithmicBoundedRange.setCurrentMillisecondsPerPixel(timeScale.getMillisecondsPerPixel());
    }

    private void updateSliderBounds() {
        logarithmicBoundedRange.setMinMillisecondsPerPixel(0.1);
        updateSliderMax();
    }

    public void updateSliderMax() {
        Interval fullInterval = timeScale.getFullInterval();
        if (fullInterval != null) {
            logarithmicBoundedRange.setMaxMillisecondsPerPixel(millisPerPixelRequredToShowEntireIntervalInViewPort(fullInterval));
        }
    }

    private double millisPerPixelRequredToShowEntireIntervalInViewPort(Interval interval) {
        return ((double) interval.toDuration().toMillis()) / viewport.getExtentSize().width;
    }

}
