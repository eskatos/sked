/*
 * Copyright (c) 2011-2014, Paul Merlin. All Rights Reserved.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.codeartisans.sked.crontab.schedule;

import java.util.Date;

/**
 * Produces CronSchedule instances.
 *
 * WARN: When using timestamps or dates they are rounded up by seconds.
 */
public interface CronScheduleFactory
{
    CronSchedule newInstance( String cronExpression );

    CronSchedule newNowInstance();

    CronSchedule newNowInstance( int initialSecondsDelay );

    CronSchedule newInstance( Date date );

    CronSchedule newInstance( long timestamp );
}
