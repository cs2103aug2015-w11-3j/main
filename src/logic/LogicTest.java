package logic;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import common.TasksBag;
import logic.exceptions.LogicException;

public class LogicTest {

    static Logic logic;

    @BeforeClass
    public static void initTest() {
        init();
    }

    
    @Test
    public void testAdd(){
        TasksBag temp;
        try {
            // Boundary for success 1 input  
            logic.executeCommand("add name");
            temp = logic.getTaskBag();
            temp.forEach( s -> s.getName());
        } catch (LogicException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }
    
    @Test 
    public void testDelete(){
        
    }
    
    
    private static void init() {
        logic = new Logic();
        logic.init();
        
        // logic.setParser(new ParserStub());
    }

}
