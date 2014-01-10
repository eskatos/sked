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
package org.codeartisans.sked.cron;

import java.util.Calendar;
import java.util.Date;

public class CronScheduleFactoryImpl
    implements CronScheduleFactory
{
    @Override
    public CronSchedule newInstance( String cronExpression )
    {
        return new CronSchedule( cronExpression );
    }

    @Override
    public CronSchedule newNowInstance()
    {
        return newInstance( System.currentTimeMillis() );
    }

    @Override
    public CronSchedule newNowInstance( int initialSecondsDelay )
    {
        return newInstance( System.currentTimeMillis() + initialSecondsDelay * 1000 );
    }

    @Override
    public CronSchedule newInstance( Date date )
    {
        return newInstance( date.getTime() );
    }

    @Override
    public CronSchedule newInstance( long timestamp )
    {
        // Rounding up timestamp on seconds
        long ceiledTimestamp = (long) ( 1000L * Math.ceil( timestamp / 1000 ) );

        // Building Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis( ceiledTimestamp );

        // Writing cron expression
        StringBuilder cronEx = new StringBuilder();
        cronEx.append( cal.get( Calendar.SECOND ) ).append( " " );
        cronEx.append( cal.get( Calendar.MINUTE ) ).append( " " );
        cronEx.append( cal.get( Calendar.HOUR_OF_DAY ) ).append( " " );
        cronEx.append( cal.get( Calendar.DAY_OF_MONTH ) ).append( " " );
        cronEx.append( cal.get( Calendar.MONTH ) + 1 ).append( " * " );
        cronEx.append( cal.get( Calendar.YEAR ) );

        return new CronSchedule( cronEx.toString() );
    }
}
