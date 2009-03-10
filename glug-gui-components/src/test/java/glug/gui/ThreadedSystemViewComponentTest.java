package glug.gui;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.Assert.assertThat;
import glug.model.ThreadedSystem;

import java.awt.Color;

import org.junit.Test;


public class ThreadedSystemViewComponentTest {
	@Test
	public void shouldReturnRightHexForColour() throws Exception {
		ThreadedSystemViewComponent component = new ThreadedSystemViewComponent(new UITimeScale(),new ThreadedSystem(),new TimelineCursor());
		assertThat(component.hexFor(Color.RED),equalToIgnoringCase("FF0000"));
	}
}
