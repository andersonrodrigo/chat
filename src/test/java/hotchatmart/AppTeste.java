package hotchatmart;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTeste extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTeste( String testName ){
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite(){
		return new TestSuite( AppTeste.class );
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp(){
		assertTrue( true );
	}
}