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

public class Constants {

    public static final String CONFIG_FILE_PATH = "/conf/config.toml";
    public static final String APP_HOME = "app.home";
    public static final int WATCHER_INITIAL_DELAY = 60;

    // Toml file constants
    public static final String TOML_NAME_ACTION_EXECUTOR_CONFIGURATION = "action_executor_configuration";
    public static final String TOML_NAME_EXECUTOR = "executor";
    public static final String TOML_NAME_RELOAD_TIME = "reload_time";
    public static final String LOG_FILE_CONFIGURATION_FILE_PATH = "server_configuration.carbon_log_file_path";

    // cpu_watcher constants
    public static final String CPU_WATCHER_ENABLED = "cpu_watcher.enabled";
    public static final String CPU_WATCHER_THRESHOLD = "cpu_watcher.threshold";
    public static final String CPU_WATCHER_RETRY_COUNT = "cpu_watcher.attempts";
    public static final String CPU_WATCHER_INTERVAL = "cpu_watcher.interval";
    public static final String CPU_WATCHER_ACTION_EXECUTORS = "cpu_watcher.action_executors";

    public static final String SYS_LA_WATCHER_ENABLED = "sys_la_watcher.enabled";

    public static final String SYS_LA_WATCHER_THRESHOLD = "sys_la_watcher.threshold";
    public static final String SYS_LA_WATCHER_RETRY_COUNT = "sys_la_watcher.attempts";
    public static final String SYS_LA_WATCHER_INTERVAL = "sys_la_watcher.interval";

    public static final String SYS_LA_WATCHER_ACTION_EXECUTORS = "sys_la_watcher.action_executors";

    // MemoryWatcher constants
    public static final String MEMORY_WATCHER_ENABLED = "memory_watcher.enabled";
    public static final String MEMORY_WATCHER_THRESHOLD = "memory_watcher.threshold";
    public static final String MEMORY_WATCHER_RETRY_COUNT = "memory_watcher.attempts";
    public static final String MEMORY_WATCHER_INTERVAL = "memory_watcher.interval";
    public static final String MEMORY_WATCHER_ACTION_EXECUTORS = "memory_watcher.action_executors";

    // log_watcher constants
    public static final String LOG_WATCHER_ENABLED = "log_watcher.enabled";
    public static final String LOG_WATCHER_INTERVAL = "log_watcher.interval";

    // LogWatcher constants
    public static final String CUSTOM_WATCHERS = "custom_watchers";
    public static final String CUSTOM_WATCHER_CLASS = "class";

    // ZipExecutor constants
    public static final String ZIP_EXECUTOR_OUTPUT_DIRECTORY = "zip_file_configuration.output_directory";
    public static final String ZIP_EXECUTOR_MAX_COUNT = "zip_file_configuration.max_count";

    // ServerInfo constants
    public static final String DIAGNOSTIC_TOOL_ENABLED = "server_configuration.diagnostic_tool_enabled";
    public static final String DEPLOYMENT_TOML_PATH = "server_configuration.deployment_toml_path";
    public static final String LOGS_DIRECTORY = "server_configuration.logs_directory";
    public static final String UPDATES_CONFIG_PATH = "server_configuration.updates_config_path";
    public static final String DIAGNOSTIC_LOG_FILE_PATH = "server_configuration.diagnostic_log_file_path";
    public static final String PROCESS_ID_PATH = "server_configuration.process_id_path";
    public static final String NODE_ID = "server_configuration.node_id";
    public static final String SERVER_NAME = "server_configuration.server_name";
    public static final String SERVER_VERSION = "server_configuration.server_version";

    // traffic_analyzer constants
    public static final String LAST_SECOND_REQUESTS_ENABLED = "traffic_analyzer.last_second_requests_enabled";
    public static final String LAST_FIFTEEN_SECONDS_REQUESTS_ENABLED =
            "traffic_analyzer.last_fifteen_seconds_requests_enabled";
    public static final String LAST_MINUTE_REQUESTS_ENABLED = "traffic_analyzer.last_minutes_requests_enabled";

    public static final String LAST_SECOND_REQUESTS_WINDOW_SIZE = "traffic_analyzer.last_second_requests_window_size";
    public static final String LAST_FIFTEEN_SECONDS_REQUESTS_WINDOW_SIZE =
            "traffic_analyzer.last_fifteen_seconds_requests_window_size";
    public static final String LAST_MINUTE_REQUESTS_WINDOW_SIZE = "traffic_analyzer.last_minutes_requests_window_size";

    public static final String LAST_SECOND_REQUESTS_DELAY = "traffic_analyzer.last_second_requests_delay";
    public static final String LAST_FIFTEEN_SECONDS_REQUESTS_DELAY =
            "traffic_analyzer.last_fifteen_seconds_requests_delay";
    public static final String LAST_MINUTE_REQUESTS_DELAY = "traffic_analyzer.last_minutes_requests_delay";

    public static final String LAST_SECOND_REQUESTS_INTERVAL = "traffic_analyzer.last_second_requests_interval";
    public static final String LAST_FIFTEEN_SECONDS_REQUESTS_INTERVAL =
            "traffic_analyzer.last_fifteen_seconds_requests_interval";
    public static final String LAST_MINUTE_REQUESTS_INTERVAL = "traffic_analyzer.last_minutes_requests_interval";

    public static final String TRAFFIC_NOTIFY_INTERVAL = "traffic_analyzer.notify_interval";

    // ftp_uploader constants
    public static final String FTP_UPLOAD_ENABLED = "ftp_uploader.enabled";
    public static final String FTP_SERVER = "ftp_uploader.host";
    public static final String FTP_USER = "ftp_uploader.username";
    public static final String FTP_PASSWORD = "ftp_uploader.password";
    public static final String FTP_PORT = "ftp_uploader.port";
    public static final String FTP_DIR = "ftp_uploader.directory";

    // Sftp_uploader constants
    public static final String SFTP_UPLOAD_ENABLED = "sftp_uploader.enabled";
    public static final String SFTP_SERVER = "sftp_uploader.host";
    public static final String SFTP_USER = "sftp_uploader.username";
    public static final String SFTP_PASSWORD = "sftp_uploader.password";
    public static final String SFTP_PORT = "sftp_uploader.port";
    public static final String SFTP_DIR = "sftp_uploader.directory";
    public static final String SFTP_KNOWN_HOSTS = "sftp_uploader.known_hosts_path";
    public static final String SFTP_STRICT_HOST_KEY_CHECKING = "sftp_uploader.strict_host_key_checking";

}
