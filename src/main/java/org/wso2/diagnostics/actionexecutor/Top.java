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

public class Top implements ActionExecutor {

    private static final Logger log = LogManager.getLogger(TCPDumper.class);

    private String command;
    public Top() {
        // read command from configmapholder
        Map configuration = ConfigMapHolder.getInstance().getConfigMap();
        ArrayList actionExecutorConfigs = (ArrayList) configuration.get(
                Constants.TOML_NAME_ACTION_EXECUTOR_CONFIGURATION);
        for (Object actionExecutorConfig : actionExecutorConfigs) {
            Map actionExecutorConfigMap = (Map) actionExecutorConfig;
            if (actionExecutorConfigMap.get("executor").equals("Top")) {
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
            String filename = "/top.txt ";
            String frame = filepath+filename;
            try {
                if (command != null) {
                    Process process = Runtime.getRuntime().exec(command);
                    Scanner scanner = new Scanner(process.getInputStream());
                    scanner.useDelimiter("\\A");
                    try {
                        FileWriter writer = new FileWriter(frame);
                        writer.write(scanner.next());
                        writer.close();
                    } catch (IOException e) {
                        log.error("Unable to do write in file in top");
                    }
                    scanner.close();
                    log.info("Top executed successfully");
                } else {
                    log.error("Unable to detect the OS");
                }

            } catch (IOException e) {
                log.error("Unable to do Top");
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
