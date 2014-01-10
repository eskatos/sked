/*
 * Copyright (c) 2010, Paul Merlin. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.codeartisans.sked.crontab;

import java.io.Serializable;
import java.util.SortedMap;

import org.codeartisans.sked.crontab.schedule.CronSchedule;

/**
 * Collection of CronSchedule and Runnables.
 *
 * Things to know:
 * <ul>
 *  <li></li>
 *  <li></li>
 * </ul>
 *
 */
public interface Crontab
        extends SortedMap<CronSchedule, Runnable>, Serializable
{
}