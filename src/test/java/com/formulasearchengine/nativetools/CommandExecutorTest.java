package com.formulasearchengine.nativetools;

import org.apache.logging.log4j.Level;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Note that those tests base on the fact, that the command echo is one
 * of the rare commands that are available on all operating systems.
 *
 * @author Andre Greiner-Petter
 */
public class CommandExecutorTest {

    public static final String ROBUST_COMMAND = "echo";
    public static final String TEST_STRING = "TEST-IS";
    public static final String NEW_LINE = System.lineSeparator();

    @Test
    public void simpleEchoTest(){
        CommandExecutor executor = new CommandExecutor("JUnit-Test", ROBUST_COMMAND, TEST_STRING);
        NativeResponse response = executor.exec();
        assertEchoNativeResponse( response );
    }

    @Test
    public void simpleEchoNoWaitingTest(){
        CommandExecutor executor = new CommandExecutor("JUnit-Test", ROBUST_COMMAND, TEST_STRING);
        NativeResponse response = executor.execWithoutTimeout();
        assertEchoNativeResponse( response );
    }

    private void assertEchoNativeResponse( NativeResponse response ){
        assertEquals( 0, response.getStatusCode(), "Executed without exit code 0." );
        assertEquals( TEST_STRING + NEW_LINE, response.getResult(), "The result is not what we expected." );
        assertNull( response.getThrowedException(), "Expected no errors for " + ROBUST_COMMAND );
    }

    @Test
    public void robustCommandExistsTest(){
        assertTrue(CommandExecutor.commandCheck( ROBUST_COMMAND ),
                "Robust command " + ROBUST_COMMAND + " doesn't exists?");
    }

    @Test
    public void notRobustCommandExistsTest(){
        assertFalse(CommandExecutor.commandCheck( ROBUST_COMMAND+"XYZ" ),
                "Fake command " + ROBUST_COMMAND + "XYC exists? What the hack is that?");
    }

    @Test
    public void brokenCommandWithLogLevelTest(){
        CommandExecutor executor = new CommandExecutor("JUnit-Test", ROBUST_COMMAND+"XYZ", TEST_STRING);
        NativeResponse response = executor.exec( Level.TRACE );
        assertInvalidCommandResponse( response );
    }

    @Test
    public void brokenCommandWithoutTimeoutTest(){
        CommandExecutor executor = new CommandExecutor("JUnit-Test", ROBUST_COMMAND+"XYZ", TEST_STRING);
        NativeResponse response = executor.execWithoutTimeout();
        assertInvalidCommandResponse( response );
    }

    private void assertInvalidCommandResponse( NativeResponse response ){
        assertEquals( 1, response.getStatusCode(), "Unknown command "+ ROBUST_COMMAND +"XYZ executed with 0?" );
        assertNull( response.getResult(), "Result for unknown command should be null." );
        assertNotNull( response.getThrowedException(), "Expected an exception for " + ROBUST_COMMAND + "XYZ." );
        assertTrue( response.getMessage().contains("Error"), "Expected a message with 'Error' in it for unknown commands." );
    }
}
