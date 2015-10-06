package logic;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

public class LogicTest {

	private static BufferedReader brIn, brOut;
	
	Logic logic;
	@Test
	public void test() {
			//System.out.println("1");
			init();
			//System.out.println("2");
			runTest();
			//System.out.println("yess");
	}
	private void runTest()
	{
		int count = 0;
		String tempIn, tempOut;
		try {
			// welcome msg is not part of test case but will be generated 
			assertEquals("Welcome to SimpleRouteStore!", brOut.readLine());
			
			while((tempIn = brIn.readLine()) != null && 
					(tempOut = brOut.readLine()) != null)
			{
				//System.out.println("yess" + tempIn + "   " + tempOut);
				//String inputAns = logic.executeCommand(tempIn);
				//System.out.println("yess2" + tempIn + "   " + tempOut);
				//assertEquals("Enter command:" + inputAns, tempOut);
			}
		} catch (IOException e) {
			e.printStackTrace();
			//fail("io");
		}
		System.out.println("complete");
	}
	private void init()
	{
		/*
		try
		{
			brIn = new BufferedReader(new FileReader(TEST_INPUT_FILENAME));
			
			brOut = new BufferedReader(new FileReader(TEST_OUTPUT_FILENAME));
		//assert
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		*/
		
		logic = new Logic();
		logic.init();
		logic.setParser(new ParserStub());
	}
	
}
