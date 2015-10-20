package logic;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.Test;

public class LogicTest {

    private static BufferedReader brIn, brOut;

    Logic logic;

    @Test
    public void test() {
        // System.out.println("1");
        init();
        // System.out.println("2");
        runTest();
        // System.out.println("yess");
    }

    private void runTest() {
        System.out.println("complete");
    }

    private void init() {
        logic = new Logic();
        logic.init();
        // logic.setParser(new ParserStub());
    }

}
