package glug.parser.logmessages;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogMessageParserRegistry {
	
	public static final List<LogMessageParser> ALL_PARSERS 
		= asList(
				new JVMUptimeParser(),
				new CompletedR2PageRequestParser(),
				new CompletedPageRequestDiagnosticParser(),
				new CompletedDatabaseQueryParser(),
				new CompletedHTTPRequestParser());
	
	public static final LogMessageParserRegistry EXAMPLE = new LogMessageParserRegistry(ALL_PARSERS);
	
	private final Map<String,List<LogMessageParser>> parsersByLoggerName = new HashMap<String, List<LogMessageParser>>();
	
	public LogMessageParserRegistry(List<LogMessageParser> parsers) {
		for (LogMessageParser parser : parsers) {
			storeParserByLoggerClassName(parser);
		}
	}

	private void storeParserByLoggerClassName(LogMessageParser parser) {
		List <LogMessageParser> parsersWithLoggerName = parsersByLoggerName.get(parser.getLoggerClassName());
		if (parsersWithLoggerName==null) {
			parsersWithLoggerName = new ArrayList<LogMessageParser>();
			parsersByLoggerName.put(parser.getLoggerClassName(), parsersWithLoggerName);
		}
		parsersWithLoggerName.add(parser);
	}

	public List<LogMessageParser> getMessageParsersFor(String loggerName) {
		return parsersByLoggerName.get(loggerName);
	}
	
}
