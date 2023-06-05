/*
 * Copyright 2014 The Embulk project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.embulk.spi;

import java.util.List;
import org.embulk.config.ConfigDiff;
import org.embulk.config.ConfigSource;
import org.embulk.config.TaskReport;
import org.embulk.config.TaskSource;

/**
 * The main class that a File Output Plugin implements.
 *
 * <p>A File Output Plugin writes file-like byte sequences from a Formatter Plugin, or an Encoder Plugin, into the configured
 * destination.
 *
 * @since 0.4.0
 */
public interface FileOutputPlugin {
    /**
     * A controller of the following tasks provided from the Embulk core.
     *
     * @since 0.4.0
     */
    interface Control {
        /**
         * Runs the following tasks of the File Output Plugin.
         *
         * <p>It would be executed in {@link #resume(org.embulk.config.TaskSource, int, FileOutputPlugin.Control)}.
         *
         * @param taskSource  {@link org.embulk.config.TaskSource} processed for tasks from {@link org.embulk.config.ConfigSource}
         * @return reports of tasks
         *
         * @since 0.7.0
         */
        List<TaskReport> run(TaskSource taskSource);
    }

    /**
     * Processes the entire file output transaction.
     *
     * @param config  a configuration for the File Output Plugin given from a user
     * @param taskCount  the number of tasks
     * @param control  a controller of the following tasks provided from the Embulk core
     * @return {@link org.embulk.config.ConfigDiff} to represent the difference the next incremental run
     *
     * @since 0.4.0
     */
    ConfigDiff transaction(ConfigSource config, int taskCount, FileOutputPlugin.Control control);

    /**
     * Resumes a file output transaction.
     *
     * @param taskSource  a configuration processed for the task from {@link org.embulk.config.ConfigSource}
     * @param taskCount  the number of tasks
     * @param control  a controller of the following tasks provided from the Embulk core
     * @return {@link org.embulk.config.ConfigDiff} to represent the difference the next incremental run
     *
     * @since 0.4.0
     */
    ConfigDiff resume(TaskSource taskSource, int taskCount, FileOutputPlugin.Control control);

    /**
     * Cleans up resources used in the transaction.
     *
     * @param taskSource  a configuration processed for the task from {@link org.embulk.config.ConfigSource}
     * @param taskCount  the number of tasks
     * @param successTaskReports  reports of successful tasks
     *
     * @since 0.7.0
     */
    void cleanup(TaskSource taskSource, int taskCount, List<TaskReport> successTaskReports);

    /**
     * Opens a {@link org.embulk.spi.TransactionalFileOutput} instance that receives {@link org.embulk.spi.Buffer}s from a
     * Formatter Plugin, or an Encoder Plugin, and writes them into the configured destination.
     *
     * <p>It processes each file output task.
     *
     * @param taskSource  a configuration processed for the task from {@link org.embulk.config.ConfigSource}
     * @param taskIndex  the index number of the task
     * @return an implementation of {@link org.embulk.spi.TransactionalFileOutput} that receives {@link org.embulk.spi.Buffer}s
     *     from a Formatter Plugin, or an Encoder Plugin, and writes them into the configured destination
     *
     * @since 0.4.0
     */
    TransactionalFileOutput open(TaskSource taskSource, int taskIndex);
}
