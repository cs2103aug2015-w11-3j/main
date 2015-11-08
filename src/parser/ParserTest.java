//@@author A0131891E
package parser;

import static org.junit.Assert.*;
import org.junit.Test;

import parser.commands.CommandData;
import parser.commands.CommandDataImpl;

import org.junit.Before;

import java.util.Random;
import java.nio.charset.*;

public class ParserTest {

	private ParserControllerImpl P;
	private Random RNG;
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	
	private static final int FIRST_KEY_CAPTURABLE_CHAR = 0x20;
	private static final int LAST_KEY_CAPTURABLE_CHAR = 0x7E;
	private static final int KEY_CAPTURABLE_RANGE = LAST_KEY_CAPTURABLE_CHAR - FIRST_KEY_CAPTURABLE_CHAR + 1;
	
	private static final int NUM_RANDOM_RETESTS = 32;
	private static final int MAX_RANDOM_GENERATED_BYTES = 100; // largest random string size generatable
	
	private static final String[] VALID_CMD_TOKENS = {
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
		P = ParserControllerImpl.getParser();
	}
	
	
	/** Tests invalid command tokens in input string
	 * no boundary cases 
	 * 
	 */
	@Test
	public void testInvalidCmdToken () {
		StringBuilder testInput;
		CommandDataImpl cmd;
		// test random inputs with guaranteed invalid command tokens 
		for (int i = 0; i < NUM_RANDOM_RETESTS; i++) {
			testInput = new StringBuilder(randInvalidCmdToken());
			testInput.append(' ');
			testInput.append(randInputString());
			System.out.println(testInput);
			// testInput now holds a random command token + a space + random data
			cmd = P.parseCommandData(testInput.toString());
			
			if (cmd.getCmdType() != CommandData.Type.INVALID) {
				ParserControllerImpl.printCmd(cmd);
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
	private boolean isValidCmdToken (String testee) {
		for (String validCmdToken : VALID_CMD_TOKENS) {
			if (testee.equals(validCmdToken)) {
				return true;
			}
		}
		return false;
	}
	private String randInputString () {
		byte[] bytes = new byte[RNG.nextInt(MAX_RANDOM_GENERATED_BYTES)];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = randKeyCapturableByte();
		}
		return new String(bytes);
	}
	private byte randKeyCapturableByte () {
		return (byte) (FIRST_KEY_CAPTURABLE_CHAR + RNG.nextInt(KEY_CAPTURABLE_RANGE));
	}
	
	/**
	 *  ADD TESTS:
	 *  NAME: STRING [ANY STRING WITHOUT SEMICOLONS]
	 */
	
	@Test
	public void testAddWithValidName () {
		// 
		CommandDataImpl cmd = P.parseCommandData("add ");
	}

	//private String getRandomVa
}
