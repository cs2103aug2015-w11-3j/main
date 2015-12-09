//@@author A0131891E
package parser;

import static org.junit.Assert.*;
import org.junit.Test;

import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;

import parser.commands.CommandParams;
import parser.commands.CommandParamsImpl;

import org.junit.Before;

import java.util.Random;
import java.nio.charset.*;

// imports omitted. Uses Junit framework
// this piece of code tests that the parser correctly handles invalid command tokens (first word in input string)
// test input is a bounded number of bounded random length strings of bounded random characters that are
// NOT contained in VALID_CMD_TOKENS (case insensitive)
// devs can set the strength of this test by changing the MAX_RANDOM_RETESTS and MAX_RANDOM_GENERATED_BYTES values
// failures will record the offending input to stdout for inspection
public class ParserCmdTokenTest {

	private DefaultParserFacade pFacade;
	private Random RNG;
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	
	private static final int FIRST_KEY_CAPTURABLE_CHAR = 0x20;
	private static final int LAST_KEY_CAPTURABLE_CHAR = 0x7E;
	// range of standard keyboard input character codes
	private static final int KEY_CAPTURABLE_RANGE = LAST_KEY_CAPTURABLE_CHAR - FIRST_KEY_CAPTURABLE_CHAR + 1; 
	
	private static final int NUM_RANDOM_RETESTS = 32; // strength of random testing in number of cycles
	private static final int MAX_RANDOM_GENERATED_BYTES = 100; // largest random string size generatable
	
	private static final String[] VALID_CMD_TOKENS = { // valid command tokens
			"a", "add", "new", "create",
			"d", "del", "delete", "rm", "remove",
			"u", "upd", "update", "set", "edit",
			"q", "quit", "exit",
			"mark", "complete",
			"unmark", "reopen",
			"un", "undo",
			"re", "redo",
			"show"
	};
	
	@Before
	public void setUp () {
		RNG = new Random();
		pFacade = DefaultParserFacade.getInstance();
	}
	
	/** Tests invalid command tokens in input string
	 * invalid inputs are generated randomly and bounded by above constants
	 * all failure-causing inputs will be recorded to stdout
	 */
	@Test
	public void testInvalidCmdToken () {
		StringBuilder testInput;
		CommandParams cmd;
		// test random inputs with guaranteed invalid command tokens 
		for (int i = 0; i < NUM_RANDOM_RETESTS; i++) {
			testInput = new StringBuilder(randInvalidCmdToken()); // start with a random invalid command token
			testInput.append(' ');
			testInput.append(randInputString(MAX_RANDOM_GENERATED_BYTES, CHARSET)); // simulates real command; <cmd token> <space> <command arguments string>
			System.out.println(testInput);
			// testInput now holds a random command token + a space + random data
			cmd = pFacade.parseCommandData(testInput.toString());
			
			if (cmd.getCmdType() != CommandParams.CmdType.INVALID) {
				DefaultParserFacade.printCmd(cmd); // shows the input string that triggered the failure
				fail("invalid command token string does not correctly return INVALID\n");
			}
		}		
	}
	// returns a random string without spaces and not equal to any of the valid_cmd_tokens,
	private String randInvalidCmdToken () {
		while (true) {
			
			byte[] strByteArray = new byte[RNG.nextInt(MAX_RANDOM_GENERATED_BYTES)];

			// seed with random keyboard enterable characters. (random character in range defined by FIRST/LAST_KEY_CAPTURABLE_CHAR)
			for (int i = 0; i < strByteArray.length; i++) {
				// +1 -1 to exclude spaces
				strByteArray[i] = (byte) (FIRST_KEY_CAPTURABLE_CHAR+1 + RNG.nextInt(KEY_CAPTURABLE_RANGE-1));
			}
			
			String randCmdToken = new String(strByteArray);
			if (!isValidCmdToken(randCmdToken)) {
				return randCmdToken;
			}
		}
	}
	
	// Checking if testee string is a valid command token
	private boolean isValidCmdToken (String testee) {
	  testee = testee.toLowerCase(); // command tokens are case insensitive
		for (String validCmdToken : VALID_CMD_TOKENS) {
			if (testee.equals(validCmdToken)) {
				return true;
			}
		}
		return false;
	}
	
	// Generates random length (bounded by sizeLimit) string of random bytes encoded with chset
	private String randInputString (int sizeLimit, Charset chset) {
		byte[] bytes = new byte[RNG.nextInt(sizeLimit)];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = randKeyCapturableByte();
		}
		return new String(bytes, chset);
	}
	
	// Returns a random byte representing a character accessible with a standard english keyboard
	private byte randKeyCapturableByte () {
		return (byte) (FIRST_KEY_CAPTURABLE_CHAR + RNG.nextInt(KEY_CAPTURABLE_RANGE));
	}
	
	// ... other trivial tests omitted

}
	
	/**
	 *  ADD TESTS:
	 *  NAME: STRING [ANY STRING WITHOUT SEMICOLONS]
	 */
	
	@Test
	public void testAddWithValidName () {
		// 
		CommandParams cmd = P.parseCommandData("add ");
	}

	//private String getRandomVa
}
