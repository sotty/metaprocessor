package metaprocessor;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 * 
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 */
	public void testApp() throws SecurityException, ClassNotFoundException {
		assertEquals(1, Class.forName("metaprocessor.Person_")
				.getDeclaredMethods().length);
		System.out
				.println("The generated class has only one method, with name: "
						+ Class.forName("metaprocessor.Person_")
								.getDeclaredMethods()[0].getName());
	}
}
