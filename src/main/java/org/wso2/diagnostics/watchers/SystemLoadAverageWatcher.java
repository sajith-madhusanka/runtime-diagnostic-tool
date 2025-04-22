package org.wso2.diagnostics.watchers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.diagnostics.actionexecutor.ActionExecutor;
import org.wso2.diagnostics.actionexecutor.ActionExecutorFactory;
import org.wso2.diagnostics.postexecutor.ZipFileExecutor;
import org.wso2.diagnostics.utils.CommonUtils;
import org.wso2.diagnostics.utils.FileUtils;
import org.wso2.diagnostics.utils.JMXDataRetriever;

/**
 * It will check the System Load Average and if it is consistently above the threshold,
 * it will execute the System Load Average watcher actions.
 */
public class SystemLoadAverageWatcher extends Thread {

    private static final Logger log = LogManager.getLogger(SystemLoadAverageWatcher.class);

    private final int retryCount;
    private final double threshold;
    private int count = 0;
    private long lastCountUpdatedTime;

    public SystemLoadAverageWatcher(double threshold, int retryCount) {
        this.threshold = threshold;
        this.retryCount = retryCount;
        this.lastCountUpdatedTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("System Load Average thread executing threshold: " + threshold +
                    ", retry count: " + retryCount + ", count: " + count + ", last count updated time: " +
                    lastCountUpdatedTime + ", current time: " + System.currentTimeMillis());
        }
        double systemLoadAverage =  JMXDataRetriever.getSystemLoadAverage();
        log.debug("System Load Average: " + systemLoadAverage + "%");
        if (systemLoadAverage > threshold) {
            log.info("System Load Average is above threshold. System Load Average: " + systemLoadAverage + "%, Retry count: " + count);
            count++;
            lastCountUpdatedTime = System.currentTimeMillis();
        }

        if (count > retryCount) {
            log.debug("System Load Average is consistently above threshold. Executing System Load Average watcher actions.");
            String tempFolderPath = FileUtils.createTimeStampFolder();
            String[] actionExecutors = CommonUtils.getActionExecutors("sys_la_watcher");
            if (actionExecutors != null) {
                for (String actionExecutor : actionExecutors) {
                    if (log.isDebugEnabled()) {
                        log.debug("Executing action executor: " + actionExecutor);
                    }
                    ActionExecutor executor = ActionExecutorFactory.getActionExecutor(actionExecutor);
                    if (executor != null) {
                        executor.execute(tempFolderPath);
                        if (log.isDebugEnabled()) {
                            log.debug("Action executor " + actionExecutor + " executed successfully.");
                        }
                    } else {
                        log.error("Action executor " + actionExecutor + " is not available.");
                    }
                }
            }
            ZipFileExecutor zipFileExecutor = new ZipFileExecutor();
            zipFileExecutor.execute(tempFolderPath);
            if (log.isDebugEnabled()) {
                log.debug("Zipping the folder " + tempFolderPath + " is successful.");
            }
            FileUtils.deleteFolder(tempFolderPath);
            if (log.isDebugEnabled()) {
                log.debug("Deleted the folder " + tempFolderPath + " successfully.");
            }
            count = 0;
            lastCountUpdatedTime = System.currentTimeMillis();
        }
        // reset the count after 1 hour
        if (System.currentTimeMillis() - lastCountUpdatedTime > 3600000) {
            if (log.isDebugEnabled()) {
                log.debug("Resetting the System Load Average Watcher count after 1 hour.");
            }
            count = 0;
        }
    }
}
