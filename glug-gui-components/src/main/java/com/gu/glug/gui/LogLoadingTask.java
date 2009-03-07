package com.gu.glug.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.swing.SwingWorker;

import com.gu.glug.model.ThreadedSystem;
import com.gu.glug.model.time.LogInterval;
import com.gu.glug.parser.LogCoordinateParser;
import com.gu.glug.parser.LogLineParser;
import com.gu.glug.parser.LogLoader;
import com.gu.glug.parser.LogParsingReader;
import com.gu.glug.parser.LogLoader.LoadReport;
import com.gu.glug.parser.logmessages.LogMessageParserRegistry;

public class LogLoadingTask extends SwingWorker<ThreadedSystem, LoadReport> {

	private final File logFile;
	private final ThreadedSystem threadedSystem;
	private final ThreadedSystemViewComponent threadedSystemViewPanel;
	private final ZoomFactorSlideUpdater zoomFactorSlideUpdater;
	
	public LogLoadingTask(File logFile,ThreadedSystem threadedSystem, ThreadedSystemViewComponent threadedSystemViewPanel, ZoomFactorSlideUpdater zoomFactorSlideUpdater) {
		this.logFile = logFile;
		this.threadedSystem = threadedSystem;
		this.threadedSystemViewPanel = threadedSystemViewPanel;
		this.zoomFactorSlideUpdater = zoomFactorSlideUpdater;
	}

	@Override
	public ThreadedSystem doInBackground() {
		System.out.print("Processing "+logFile);
		LineNumberReader reader;
		try {
			reader = new LineNumberReader(new InputStreamReader( new GZIPInputStream(new FileInputStream(logFile))));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		LogLoader logLoader = new LogLoader(new LogParsingReader(reader,new LogLineParser(new LogCoordinateParser(threadedSystem),LogMessageParserRegistry.EXAMPLE )));
		System.out.print("woo");
		LoadReport loadReport;
		try {
			while (!isCancelled() && !(loadReport=logLoader.loadLines(10000)).endOfStreamReached()) {
				publish(loadReport);
				System.out.print(".");
				// setProgress(100 * numbers.size() / numbersToFind);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		System.out.println("Finished loading");
		return threadedSystem;
	}
	
	@Override
	protected void process(List<LoadReport> loadReports) {
		LogInterval interval = null;
		for (LoadReport loadReport : loadReports) {
			interval = loadReport.getUpdatedInterval().union(interval);
		}
		zoomFactorSlideUpdater.updateSliderMax();
		threadedSystemViewPanel.repaint(interval.toJodaInterval());
	}

}
