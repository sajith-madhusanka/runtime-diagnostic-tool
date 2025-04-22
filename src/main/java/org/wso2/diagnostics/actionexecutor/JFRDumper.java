package org.wso2.diagnostics.actionexecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.diagnostics.utils.ConfigMapHolder;
import org.wso2.diagnostics.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class JFRDumper implements ActionExecutor {

    private static final Logger log = LogManager.getLogger(JFRDumper.class);

    private String duration;
    private final String serverProcess;

    public JFRDumper() {
        // read command from configmapholder
        Map configuration = ConfigMapHolder.getInstance().getConfigMap();
        ArrayList actionExecutorConfigs = (ArrayList) configuration.get(
                Constants.TOML_NAME_ACTION_EXECUTOR_CONFIGURATION);
        for (Object actionExecutorConfig : actionExecutorConfigs) {
            Map actionExecutorConfigMap = (Map) actionExecutorConfig;
            if (actionExecutorConfigMap.get("executor").equals("JFRDumper")) {
                duration = (String) actionExecutorConfigMap.get("duration");
            }
        }
        this.serverProcess = ServerProcess.getProcessId();
    }

    /**
     * Method used to start capture tcpdump using tcpdump command.
     *
     * @param filepath file path of the dump folder
     */
    @Override
    public void execute(String filepath) {

        if (new File(filepath).exists()) { // check whether file exists before dumping.

            String javaHome = System.getenv("JAVA_HOME");
            if (javaHome == null) {
                log.error("JAVA_HOME is not set.");
                return;
            }

            String filename = filepath + "/wso2_profiling.jfr ";
            String prefix = javaHome + "/bin/jcmd " + serverProcess + " JFR.start name=wso2_profiler duration=" + duration+"m";
            String captureCommand = prefix + " filename=" + filename;

            try {

                Process captureProcess = Runtime.getRuntime().exec(captureCommand);
                log.info("JFR capture command executed. Waiting for "+duration+ " minutes until the command execution completed");
                Thread.sleep(Long.parseLong(duration)* 60000);


            } catch (IOException | InterruptedException e) {
                log.error("Error while executing jfr commands", e);
                Thread.currentThread().interrupt(); // Restore interrupted status
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
