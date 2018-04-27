package com.formulasearchengine.mathmltools.nativetools;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A native command executor. Note that it assumes the services of the native code will
 * be printed to the standard output streams while error and also logging information
 * will be printed in the error output stream.
 * <p>
 * This command executor is very simple and doesn't allow further communications with the
 * native code (such as listening for inputs) yet.
 *
 * @author Andre Greiner-Petter
 */
public class CommandExecutor {
    /**
     * Use log4j2
     */
    private static final Logger LOG = LogManager.getLogger(CommandExecutor.class.getName());

    /**
     * A default timeout for response from the executed native command (2 seconds)
     */
    public static final long DEFAULT_TIMEOUT = 2000L;

    /**
     * The process builder
     */
    private ProcessBuilder pb;

    private Process process;

    /**
     * String service name
     */
    private final String serviceName;

    /**
     * Constructs a command executor object. The service name
     * is the first argument. When you execute this object,
     * it will run the given arguments on the native console.
     * <p>
     * For example, to find out weather a native program exists, you can
     * call
     * new CommandExecutor("which", native command name").exec();
     *
     * @param args the commands for the native console
     */
    public CommandExecutor(List<String> args) {
        this(args.remove(0), args);
    }

    /**
     * Constructs a command executor object with a service name and
     * the command with arguments.
     * <p>
     * For example, to find out weather a native program exists, you can
     * call
     * new CommandExecutor("CommandExists", "which", native command name").exec();
     *
     * @param serviceName gives this command executor a name (or ID)
     * @param args        the commands for the native console
     */
    public CommandExecutor(String serviceName, String... args) {
        this.pb = new ProcessBuilder(args);
        this.serviceName = serviceName;
    }

    /**
     * Constructs a command executor object with a service name and
     * the command with arguments.
     * <p>
     * For example, to find out weather a native program exists, you can
     * call
     * new CommandExecutor("CommandExists", "which", native command name").exec();
     *
     * @param serviceName gives this command executor a name (or ID)
     * @param args        the commands for the native console
     */
    public CommandExecutor(String serviceName, List<String> args) {
        this.pb = new ProcessBuilder(args);
        this.serviceName = serviceName;
    }

    /**
     * Specify a working directory for the process. Obviously, needs to be done
     * before any executions.
     *
     * @param directoryPath must be a directory path
     */
    public void setWorkingDirectoryForProcess(Path directoryPath) {
        pb.directory(directoryPath.toFile());
    }

    /**
     * Runs the constructed native argument with the default timeout {@link #DEFAULT_TIMEOUT}.
     * This command deactivates logging for the error stream.
     *
     * @return Response object.
     */
    public NativeResponse exec() {
        return exec(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * Runs the constructed native argument with the default timeout {@link #DEFAULT_TIMEOUT}.
     *
     * @return Response object.
     */
    public NativeResponse exec(Level logLevel) {
        return exec(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS, logLevel);
    }

    /**
     * Execute with a given timeout there will be no logging information tagged.
     *
     * @param timeoutMs a
     * @return a
     */
    public NativeResponse exec(long timeoutMs) {
        return exec(timeoutMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute with a given timeout and sets the log level for the
     * error output stream.
     *
     * @param timeoutMs a
     * @param logLevel a
     * @return a
     */
    public NativeResponse exec(long timeoutMs, Level logLevel) {
        return exec(timeoutMs, TimeUnit.MILLISECONDS, logLevel);
    }

    /**
     * Specify the timeout for different time units.
     *
     * @param timeout a
     * @param unit a
     * @return a
     */
    public NativeResponse exec(long timeout, TimeUnit unit) {
        return exec(timeout, unit, null);
    }

    /**
     * Just wait as long as possible for the response.
     *
     * @return a
     */
    public NativeResponse execWithoutTimeout() {
        return internalexec(0, null, null);
    }

    /**
     * Just wait as long as possible for the response and puts the error stream
     * to the given log level (log4j2).
     *
     * @param logLevel a
     * @return a
     */
    public NativeResponse execWithoutTimeout(Level logLevel) {
        return internalexec(0, null, logLevel);
    }

    /**
     * Combination of everything before.
     *
     * @param timeout a
     * @param unit a
     * @param logLevel a
     * @return a
     */
    public NativeResponse exec(long timeout, TimeUnit unit, Level logLevel) {
        return internalexec(timeout, unit, logLevel);
    }

    private NativeResponse internalexec(long timeout, TimeUnit unit, Level logLevel) {
        int exitCode = 1;
        try {
            String result = setupInernal(logLevel);
            if (unit == null) {
                process.waitFor();
            } else {
                process.waitFor(timeout, unit);
            }
            exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new IOException("Execution Fail!");
            }
            safetyExit(process);
            process.destroy();
            return new NativeResponse(result);
        } catch (InterruptedException ie) {
            LOG.warn(serviceName + " - Process exceeded timeout -> return null.", ie);
            return null;
        } catch (IOException ioe) {
            LOG.error("Cannot execute " + serviceName, ioe);
            return new NativeResponse(exitCode, exitCode + "-Error in " + serviceName + ": " + ioe.getMessage(), ioe);
        }
    }

    private String setupInernal(Level logLevel) throws IOException {
        process = pb.start();
        logErrorStream(process.getErrorStream(), logLevel);
        return buildFromStream(process.getInputStream());
    }

    private void logErrorStream(InputStream err, Level logLevel) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(err))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    if (logLevel != null) {
                        LOG.log(logLevel, serviceName + " - " + line);
                    }
                }
            }
            LOG.trace("Finished sub-process logging.");
        } catch (IOException ioe) {
            LOG.error("Error while reading from error stream.", ioe);
        }
    }

    private String buildFromStream(InputStream in) {
        StringBuilder builder = new StringBuilder();
        try (InputStreamReader br = new InputStreamReader(in)) {
            builder.append(IOUtils.toString(br));
        } catch (IOException ioe) {
            LOG.error("Cannot build result from stream.", ioe);
        }
        return builder.toString();
    }

    /**
     * Has to be done in the end manually. Close all streams manually.
     *
     * @param process process with open streams
     * @throws IOException if something went wrong due closing streams
     */
    private void safetyExit(Process process) throws IOException {
        process.getErrorStream().close();
        process.getInputStream().close();
        process.getOutputStream().close();
    }

    /**
     * Checks if the given native command exists. No error will be thrown.
     * The program waits for 100 milliseconds. That's enough to find out if
     * the program exists or not.
     *
     * @param nativeCommand The command you want to check
     * @return true if the command can be executed or false if not.
     */
    public static boolean commandCheck(String nativeCommand) {
        CommandExecutor executor = new CommandExecutor(
                "DefinitionCheck", "which", nativeCommand
        );
        NativeResponse res = executor.exec(100);
        return res.getStatusCode() == 0;
    }
}
