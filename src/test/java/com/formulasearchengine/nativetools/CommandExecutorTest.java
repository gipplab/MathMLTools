package com.formulasearchengine.nativetools;

import org.apache.logging.log4j.Level;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Note that those tests base on the fact, that the command echo is one
 * of the rare commands that are available on all operating systems.
 *
 * @author Andre Greiner-Petter
 */
public class CommandExecutorTest {
    
    private static final String ROBUST_ECHO_COMMAND = "echo";
    private static final String TEST_STRING = "TEST-IS";
    private static final String NEW_LINE = System.lineSeparator();

    @Test
    public void simpleEchoTest(){
        CommandExecutor executor = new CommandExecutor("JUnit-Test", ROBUST_ECHO_COMMAND, TEST_STRING);
        NativeResponse response = executor.exec();
        assertEchoNativeResponse( response );
    }

    @Test
    public void simpleEchoWithTimeoutTest(){
        CommandExecutor executor = new CommandExecutor("JUnit-Test", ROBUST_ECHO_COMMAND, TEST_STRING);
        NativeResponse response = executor.exec( 1000, Level.DEBUG );
        assertEchoNativeResponse( response );
    }

    @Test
    public void simpleEchoNoWaitingTest(){
        LinkedList<String> args = new LinkedList<>();
        args.add("JUnit-Test");     // service name
        args.add(ROBUST_ECHO_COMMAND);   // command
        args.add(TEST_STRING);      // command argument
        CommandExecutor executor = new CommandExecutor(args);
        NativeResponse response = executor.execWithoutTimeout( Level.INFO );
        assertEchoNativeResponse( response );
    }

    private void assertEchoNativeResponse( NativeResponse response ){
        assertEquals( 0, response.getStatusCode(), "Executed without exit code 0." );
        assertEquals( TEST_STRING + NEW_LINE, response.getResult(), "The result is not what we expected." );
        assertNull( response.getThrowedException(), "Expected no errors for " + ROBUST_ECHO_COMMAND);
    }

    @Test
    public void robustCommandExistsTest(){
        assertTrue(CommandExecutor.commandCheck(ROBUST_ECHO_COMMAND),
                "Robust command " + ROBUST_ECHO_COMMAND + " doesn't exists?");
    }

    @Test
    public void notRobustCommandExistsTest(){
        assertFalse(CommandExecutor.commandCheck( ROBUST_ECHO_COMMAND +"XYZ" ),
                "Fake command " + ROBUST_ECHO_COMMAND + "XYC exists? What the hack is that?");
    }

    @Test
    public void brokenCommandWithLogLevelTest(){
        CommandExecutor executor = new CommandExecutor("JUnit-Test", ROBUST_ECHO_COMMAND +"XYZ", TEST_STRING);
        NativeResponse response = executor.exec( Level.TRACE );
        assertInvalidCommandResponse( response );
    }

    @Test
    public void brokenCommandWithoutTimeoutTest(){
        CommandExecutor executor = new CommandExecutor("JUnit-Test", ROBUST_ECHO_COMMAND +"XYZ", TEST_STRING);
        NativeResponse response = executor.execWithoutTimeout();
        assertInvalidCommandResponse( response );
    }

    private void assertInvalidCommandResponse( NativeResponse response ){
        assertEquals( 1, response.getStatusCode(), "Unknown command "+ ROBUST_ECHO_COMMAND +"XYZ executed with 0?" );
        assertNull( response.getResult(), "Result for unknown command should be null." );
        assertNotNull( response.getThrowedException(), "Expected an exception for " + ROBUST_ECHO_COMMAND + "XYZ." );
        assertTrue( response.getMessage().contains("Error"), "Expected a message with 'Error' in it for unknown commands." );
    }
}
