/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org).
 *
 *   WSO2 LLC. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.diagnostics.watchers.logwatcher;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogWatcher extends Thread {

    private static final Logger log = LogManager.getLogger(LogWatcher.class);

    /**
     * The file which will be tailed.
     */
    private final File file;
    /**
     * The amount of time to wait for the file to be updated.
     */
    private final long delay;
    /**
     * The interpreter to notify of events when tailing.
     */
    private final Interpreter interpreter;

    /**
     * Creates a Tailer for the given file.
     *
     * @param filepath   the file to follow.
     * @param interpreter   the TailerListener to use.
     * @param delay      the delay between checks of the file for new content in seconds.
     */
    public LogWatcher(String filepath, Interpreter interpreter, double delay) {

        this.file = new File(filepath);
        this.delay = Math.round(delay * 1000);
        this.interpreter = interpreter;
    }

    public void run() {
        try {

            RandomAccessFile reader = null;
            long position = 0;
            StringBuilder logBuilder = new StringBuilder(); // To accumulate log lines
            while (reader == null) {
                try {
                    reader = new RandomAccessFile(file, "r");
                    log.info("Initiating LogWatcher for file: " + file.getPath());
                    position = file.length();
                    reader.seek(position);
                } catch (FileNotFoundException e) {
                    // ignoring this exception as the file may not be created yet
                    log.debug("Log file " + file.getPath() + " not found.");
                    Thread.sleep(delay);
                }
            }
            while (true) {
                long fileLength = file.length();
                if (fileLength < position) {
                    // File was rotated
                    log.info("Log file has been rotated. Reopening the file " + file.getPath());
                    // Reopen the reader after rotation
                    try {
                        // Ensure that the old file is closed
                        closeQuietly(reader);
                        reader = new RandomAccessFile(file, "r");
                        readLines(reader, logBuilder);
                        position = 0;
                    } catch (FileNotFoundException e) {
                        log.error("Log file " + file.getPath() + " not found." , e);
                    }
                    continue;
                }
                // Check if the file has been updated
                if (fileLength > position) {
                    readLines(reader, logBuilder);
                    // Update the file length
                    position = fileLength;
                    // Move the file pointer to the end
                    reader.seek(fileLength);
                }
                // Sleep for a short duration before checking for updates again
                Thread.sleep(delay);
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error while tailing the log file: " + file.getPath(), e);
        }
    }

    private void closeQuietly(Closeable closeable) {

        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            log.error("unable to close the file: " + file.getPath(), ioe);
        }
    }

    private void readLines(RandomAccessFile reader, StringBuilder logBuilder) throws IOException {

        // Read the new lines
        String line;
        String errorLine = "";
        while ((line = reader.readLine()) != null) {
            // Check if the line indicates the start of a stack trace
            if (line.contains("ERROR") || line.contains("WARN")) {
                if (logBuilder.length() == 0) {
                    errorLine = line;
                    logBuilder.append(line).append("\n");
                } else {
                    interpreter.interpret(errorLine, logBuilder.toString());
                    logBuilder.setLength(0);
                    errorLine = line;
                    logBuilder.append(line).append("\n");
                }
            } else {
                logBuilder.append(line).append("\n");
            }
            // If the logBuilder exceeds a threshold, interpret the log lines and clear the logBuilder
            if (logBuilder.length() > 1000000) {
                interpreter.interpret(errorLine, logBuilder.toString());
                logBuilder.setLength(0);
            }
        }
    }
}
