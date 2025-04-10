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

package org.wso2.diagnostics.actionexecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.diagnostics.utils.ConfigMapHolder;
import org.wso2.diagnostics.utils.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Class used to take threadDumps at given time intervals.
 * jstack command is used to take thread dumps.
 */
public class ThreadDumper implements ActionExecutor {

    private static final Logger log = LogManager.getLogger(ThreadDumper.class);

    /**
     * This string is used to represent process id.
     */
    private final String pID;
    /**
     * This long is used to refer delay between thread dumps.
     */
    private long delay;
    /**
     * This int is used to refer how many thread dumps needed.
     */
    private int threadDumpCount;

    public ThreadDumper() {
        this.pID = ServerProcess.getProcessId();
        // read thread dump count and delay from configmapholder
        Map configuration = ConfigMapHolder.getInstance().getConfigMap();
        ArrayList actionExecutorConfigs = (ArrayList) configuration.get(
                Constants.TOML_NAME_ACTION_EXECUTOR_CONFIGURATION);
        for (Object actionExecutorConfig : actionExecutorConfigs) {
            Map actionExecutorConfigMap = (Map) actionExecutorConfig;
            if (actionExecutorConfigMap.get("executor").equals("ThreadDumper")) {
                this.threadDumpCount = Integer.parseInt(actionExecutorConfigMap.get("count").toString());
                this.delay = Integer.parseInt(actionExecutorConfigMap.get("delay").toString());
            }
        }
    }

    /**
     * Method used to do thread dump with using Java Runtime Environment and jstack command.
     *
     * @param folderPath folder path of the dump folder
     */
    @Override
    public void execute(String folderPath) {

        if (new File(folderPath).exists()) { // check whether file exists before dumping.
            String commandFrame = System.getenv("JAVA_HOME") + "/bin/jstack " + pID;
            String commandFrame2 = "ps --pid " + pID + " -Lo pid,tid,%cpu,time,nlwp,c";

            for (int counter = 1; counter <= threadDumpCount; counter++) {
                try {
                    String currentTimeStamp = String.valueOf(System.currentTimeMillis());

                    String jstackFilePath = folderPath + "/thread-dump-" + counter + "-" + currentTimeStamp + ".txt";
                    String psFilePath = folderPath + "/thread-usage-" + counter + "-" + currentTimeStamp + ".txt";

                    Thread jstackThread = new Thread(() -> {
                        try {
                            Process process = Runtime.getRuntime().exec(commandFrame);
                            Scanner scanner = new Scanner(process.getInputStream());
                            scanner.useDelimiter("\\A");
                            if (scanner.hasNext()) {
                                try (FileWriter writer = new FileWriter(jstackFilePath)) {
                                    writer.write(scanner.next());
                                }
                                catch (IOException e) {
                                    log.error("Unable to do write in file while thread dumping");
                                }
                            }
                            scanner.close();
                        } catch (IOException e) {
                            log.error("Error during jstack command: " + e.getMessage());
                        }
                    });

                    Thread psThread = new Thread(() -> {
                        try {
                            Process process = Runtime.getRuntime().exec(commandFrame2);
                            Scanner scanner = new Scanner(process.getInputStream());
                            scanner.useDelimiter("\\A");
                            if (scanner.hasNext()) {
                                try (FileWriter writer = new FileWriter(psFilePath)) {
                                    writer.write(scanner.next());
                                }
                                catch (IOException e) {
                                    log.error("Unable to do write in file while thread dumping");
                                }

                            }
                            scanner.close();
                        } catch (IOException e) {
                            log.error("Error during ps command: " + e.getMessage());
                        }
                    });

                    jstackThread.start();
                    psThread.start();

                    jstackThread.join();
                    psThread.join();

                    synchronized (this) {
                        this.wait(delay);
                    }

                } catch (InterruptedException e) {
                    log.error("Thread interrupted while waiting: " + e.getMessage());
                }
            }

        }
        log.info("Thread dump execution is completed for " + pID + ", thread dump count: " +
                threadDumpCount + ", delay: " + delay + "ms");
    }
    public void execute() {
        String folderPath = (System.getProperty(Constants.APP_HOME) + "/temp/"); // get log file path
        File logFolder = new File(folderPath);
        if (!(logFolder.exists())) {
            logFolder.mkdir();
        }
        this.execute(folderPath);
    }

}
