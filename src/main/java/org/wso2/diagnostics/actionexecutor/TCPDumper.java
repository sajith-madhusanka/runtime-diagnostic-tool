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
 * Class used to start capture TCP Dump.
 * tcpdump command is used to start capture tcpdump.
 */
public class TCPDumper implements ActionExecutor {

    private static final Logger log = LogManager.getLogger(TCPDumper.class);

    private String command;
    public TCPDumper() {
        // read command from configmapholder
        Map configuration = ConfigMapHolder.getInstance().getConfigMap();
        ArrayList actionExecutorConfigs = (ArrayList) configuration.get(
                Constants.TOML_NAME_ACTION_EXECUTOR_CONFIGURATION);
        for (Object actionExecutorConfig : actionExecutorConfigs) {
            Map actionExecutorConfigMap = (Map) actionExecutorConfig;
            if (actionExecutorConfigMap.get("executor").equals("TCPDumper")) {
                command = (String) actionExecutorConfigMap.get("command");
            }
        }
    }

    /**
     * Method used to start capture tcpdump using tcpdump command.
     *
     * @param filepath file path of the dump folder
     */
    @Override
    public void execute(String filepath) {

        if (new File(filepath).exists()) { // check whether file exists before dumping.
            String filename = "/tcpdump.pcap ";
            String frame =  command+ " -w "+filepath + filename;
            try {
                if (command != null) {
                    Runtime.getRuntime().exec(frame);
                } else {
                    log.error("Unable to detect the OS");
                }

            } catch (IOException e) {
                log.error("Unable to do tcpdump");
            }
        }
    }

    public void execute() {
        String folderPath = (System.getProperty(Constants.APP_HOME) +
                File.separator + "temp" + File.separator);
        File logFolder = new File(folderPath);
        if (!(logFolder.exists())) {
            logFolder.mkdir();
        }
        this.execute(folderPath);
    }
}
