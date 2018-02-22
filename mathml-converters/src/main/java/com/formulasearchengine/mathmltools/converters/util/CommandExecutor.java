package com.formulasearchengine.mathmltools.converters.util;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;


/**
 * Executor that executes a UNIX-style command and returns output in exec() method.
 * Partial code adopted from http://www.rgagnon.com/javadetails/java-0014.html.
 * (Heavily modified copy from SciPlore/CitePlag)
 *
 * @author Vincent Stange, Norman Meuschke
 */
public class CommandExecutor {

    private static Logger logger = LogManager.getLogger(CommandExecutor.class);

    /**
     * The input command.
     */
    private ProcessBuilder pb;

    public Process getProcess() {
        return process;
    }

    private Process process;

    /**
     * Instantiates a new command executor.
     *
     * @param command Unix-style command input
     */
    public CommandExecutor(String... command) {
        pb = new ProcessBuilder(command);
    }

    /**
     * Executes the command in the runtime environment.
     *
     * @param timeoutMs Read timeout interval in ms
     * @return Output as a result of the command execution.
     * @throws Exception process execution failed
     */
    public String exec(long timeoutMs) throws Exception {
        StringBuilder output = new StringBuilder();

        try {
            process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        long startTime = System.currentTimeMillis();
        InputStream stdout = process.getInputStream();
        OutputStream stdin = process.getOutputStream();
        InputStream stderr = process.getErrorStream();
        try (BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout, "UTF-8"));
             BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr, "UTF-8"))) {

            int timeout = 60;
            while (timeout > 0) {
                try {
                    int character;
                    while ((character = stdoutReader.read()) >= 0) {
                        output.append((char) character);
                    }
                } catch (IOException e) {
                    logger.error("execution error", e);
                    throw e;
                }
                try {
                    // iterate until process is finished or timeout reached
                    process.exitValue();
                    break;
                } catch (IllegalThreadStateException e) {
                    try {
                        Thread.sleep(timeoutMs);
                    } catch (InterruptedException e2) {
                        logger.warn("command executer was interrupted", e2);
                    } finally {
                        timeout--;
                    }
                }
            }

            try {
                output.append(IOUtils.toString(stdoutReader));
            } catch (IOException e) {
                logger.error("Command Executor Output Stream", e);
                throw e;
            }

            try {
                if (process.exitValue() != 0) {
                    long processTime = System.currentTimeMillis() - startTime;
                    String error = IOUtils.toString(stderrReader);
                    logger.error("CommandExecuter " + pb.command()
                            + " (Timeout: (Attempts: " + timeout + ") " + processTime + " ms) "
                            + "Output: " + output + ", Error: " + error);
                    throw new Exception("Process exited with status " + process.exitValue() + ".");
                }
            } catch (IOException e) {
                logger.error("Command Executor Error Stream", e);
                throw e;
            } catch (IllegalThreadStateException e) {
                logger.error("Command Executor Timeout", e);
            }

            return output.toString();
        } // try - close readers
        finally {
            // its *very* important to close all streams from the process
            // this is still an unfixed bug, see http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6462165
            stdout.close();
            stdin.close();
            stderr.close();
            // process will never be null at this point
            process.destroy();
        }
    }
}
