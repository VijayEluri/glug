package glug.parser;

import java.text.ParseException;

public class BracketCounter {

	int count=0;
	
	public int count(String inputText) throws ParseException {
		for (int index = 0; index<inputText.length(); ++index) {
			char c = inputText.charAt(index);
			switch (c) {
				case '[' :
					++count;
					break;
				case ']' :
					--count;
					if (count<0) {
						throw new ParseException(inputText,index);
					}
					break;
			}
		}
		return count;
	}

}
