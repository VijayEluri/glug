package glug.gui;

import com.madgag.interval.Interval;
import glug.gui.zoom.ZoomFactorSlideUpdater;
import glug.model.ThreadedSystem;
import glug.model.time.LogInstant;

import static glug.model.time.LogInterval.toTimeInterval;

public class DataLoadedUIUpdater {
    private final ThreadedSystem threadedSystem;
    private final UITimeScale uiTimeScale;
    private final ZoomFactorSlideUpdater zoomFactorSlideUpdater;
    private final UIThreadScale threadScale;

    public DataLoadedUIUpdater(ThreadedSystem threadedSystem, UITimeScale uiTimeScale, UIThreadScale threadScale, ZoomFactorSlideUpdater zoomFactorSlideUpdater) {
        this.threadedSystem = threadedSystem;
        this.uiTimeScale = uiTimeScale;
        this.threadScale = threadScale;
        this.zoomFactorSlideUpdater = zoomFactorSlideUpdater;
    }

    public void updateUI(Interval<LogInstant> updatedLogInterval) {
        uiTimeScale.setFullInterval(toTimeInterval(threadedSystem.getIntervalCoveredByAllThreads()));
        threadScale.setNumThreads(threadedSystem.getNumThreads());
        zoomFactorSlideUpdater.updateSliderMax();
    }
}
