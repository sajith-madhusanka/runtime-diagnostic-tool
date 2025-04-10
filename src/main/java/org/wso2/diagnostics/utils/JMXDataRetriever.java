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

package org.wso2.diagnostics.utils;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXDataRetriever {
    private static final Logger log = LogManager.getLogger(JMXDataRetriever.class);
    private static volatile MBeanServerConnection mBeanServerConnection;

    public static String getAttributeValue(String type, String pid, String attribute) {
        return getJmxData(pid, attribute, "org.apache.synapse:Name=" + type + ",Type=PassThroughConnections");
    }

    public static int getIntAttributeValue(String type, String pid, String attribute) {
        return getInt(getJmxData(pid, attribute, "org.apache.synapse:Name=" + type + ",Type=PassThroughConnections"));
    }

    public static int getCpuUsage(String pid) {
        String output = getJmxData(pid, "ProcessCpuLoad", "java.lang:type=OperatingSystem");
        return getFloat(output) == -1 ? -1 : (int) (getFloat(output) * 100);
    }

    public static int getMemoryUsage(String pid) {
        String output = getJmxData(pid, "HeapMemoryUsage", "java.lang:type=Memory");
        long usedValue = extractLongValue(output, "used");
        long maxValue = extractLongValue(output, "max");
        // return percentage
        return getInt(String.valueOf((usedValue * 100) / maxValue));
    }

    public static double getSystemLoadAverage() {
        String output = getJmxData(null, "SystemLoadAverage", "java.lang:type=OperatingSystem");
        return getFloat(output); // This will return a load average value, or -1 if it fails
    }

    private static int getInt(String output) {
        if (StringUtils.isEmpty(output)) {
            return -1;
        }
        return Integer.parseInt(output);
    }

    private static Float getFloat(String output) {
        if (StringUtils.isEmpty(output)) {
            return -1f;
        }
        return Float.parseFloat(output);
    }

    public static long extractLongValue(String input, String fieldName) {
        String regex = fieldName + "=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }

        return -1; // Return -1 if field not found
    }

    public static String getJmxData(String pid, String attribute, String objectName) {

        try {
            MBeanServerConnection connection = getConnection(pid);
            if (connection != null) {
                ObjectName mbeanName = new ObjectName(objectName);
                return connection.getAttribute(mbeanName, attribute).toString();
            }
        } catch (InstanceNotFoundException e) {
            log.error("Error while getting JMX data, " + e.getMessage());
        } catch (Exception e) {
            log.error("Error while getting JMX data", e);
            mBeanServerConnection = null;
        }
        return "";
    }

    private static MBeanServerConnection getConnection(String pid) {
        // get connection in a synchronized way
        if (mBeanServerConnection == null) {
            synchronized (JMXDataRetriever.class) {
                if (mBeanServerConnection == null) {
                    log.debug("Creating JMX connection to the process with PID: " + pid);
                    try {
                        // Find the VirtualMachineDescriptor for the target process
                        VirtualMachineDescriptor vmDescriptor = findVirtualMachineDescriptor(pid);
                        if (vmDescriptor != null) {
                            // Attach to the target process
                            VirtualMachine vm = VirtualMachine.attach(vmDescriptor);
                            try {
                                // Get the connector address for JMX remote management
                                String connectorAddress = vm.getAgentProperties().getProperty(
                                        "com.sun.management.jmxremote.localConnectorAddress");
                                if (connectorAddress != null) {
                                    // Connect to the MBeanServer of the target process
                                    JMXServiceURL jmxServiceURL = new JMXServiceURL(connectorAddress);
                                    JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL);
                                    mBeanServerConnection = jmxConnector.getMBeanServerConnection();
                                }
                            } finally {
                                vm.detach();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error while getting JMX data", e);
                    }
                }
            }
        }
        return mBeanServerConnection;
    }

    private static VirtualMachineDescriptor findVirtualMachineDescriptor(String pid) {
        List<VirtualMachineDescriptor> vmDescriptors = VirtualMachine.list();
        for (VirtualMachineDescriptor vmDescriptor : vmDescriptors) {
            if (vmDescriptor.id().equals(pid)) {
                return vmDescriptor;
            }
        }
        return null;
    }
}
