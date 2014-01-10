/*
 * Copyright (c) 2010-2014, Paul Merlin. All Rights Reserved.
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

import java.io.Serializable;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cron expression parsing is based on the GNU crontab manpage that can be found
 * here: http://unixhelp.ed.ac.uk/CGI/man-cgi?crontab+5
 *
 * The following extensions are used:
 * <ul>
 *      <li>a mandatory field is added at the begining: seconds.</li>
 *      <li>a special string is added: @minutely</li>
 *      <li>a special character is added: ? to choose between dayOfMonth and dayOfWeek</li>
 * </ul>
 *
 * The ? special char has the same behavior as in the Quartz Scheduler expression.
 * The wikipedia page http://en.wikipedia.org/wiki/CRON_expression explains Quartz
 * Scheduler expression, not simple cron expressions. You'll find there about the ?
 * special char and maybe that some other extensions you would like to use are missing
 * in this project.
 */
public final class CronSchedule
    implements Serializable
{
    public static boolean isExpressionValid( String cronExpression )
    {
        try
        {
            CronScheduleUtil.validateAndSplitExpression( cronExpression );
            return true;
        }
        catch( IllegalArgumentException ex )
        {
        }
        return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger( CronSchedule.class );
    private String expression;
    private transient CronAtom secondAtom;
    private transient CronAtom minuteAtom;
    private transient CronAtom hourAtom;
    private transient CronAtom dayOfMonthAtom;
    private transient CronAtom monthAtom;
    private transient CronAtom dayOfWeekAtom;
    private transient CronAtom yearAtom;
    private transient boolean loaded = false;

    public CronSchedule( String cronExpression )
    {
        load( cronExpression );
    }

    private CronSchedule loaded()
    {
        if( !loaded )
        {
            load( expression );
            loaded = true;
        }
        return this;
    }

    private void load( String cronExpression )
    {
        String[] splitted = CronScheduleUtil.validateAndSplitExpression( cronExpression );
        secondAtom = new SecondAtom( splitted[0] );
        minuteAtom = new MinuteAtom( splitted[1] );
        hourAtom = new HourAtom( splitted[2] );
        dayOfMonthAtom = new DayOfMonthAtom( splitted[3] );
        monthAtom = new MonthAtom( splitted[4] );
        dayOfWeekAtom = new DayOfWeekAtom( splitted[5] );
        yearAtom = new YearAtom( splitted[6] );
        expression = new StringBuilder().
            append( secondAtom ).append( " " ).
            append( minuteAtom ).append( " " ).
            append( hourAtom ).append( " " ).
            append( dayOfMonthAtom ).append( " " ).
            append( monthAtom ).append( " " ).
            append( dayOfWeekAtom ).append( " " ).
            append( yearAtom ).toString();
    }

    public Long firstRunAfter( Long start )
    {
        DateTime nextRun = firstRunAfter( new DateTime( start ) );
        if( nextRun == null )
        {
            return null;
        }
        return nextRun.getMillis();
    }

    private DateTime firstRunAfter( DateTime start )
    {
        int nil = -1;

        int baseYear = start.getYear();
        int baseMonth = start.getMonthOfYear();
        int baseDayOfMonth = start.getDayOfMonth();
        int baseHour = start.getHourOfDay();
        int baseMinute = start.getMinuteOfHour();
        int baseSecond = start.getSecondOfMinute();

        int year = baseYear;
        int month = baseMonth;
        int dayOfMonth = baseDayOfMonth;
        int hour = baseHour;
        int minute = baseMinute;
        int second = baseSecond;

        // Second
        second = loaded().secondAtom.nextValue( second );
        if( second == nil )
        {
            second = loaded().secondAtom.minAllowed();
            minute++;

            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextSecond was -1, set to {} and minute to {}",
                          start, second, minute );
        }
        else
        {
            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextSecond is {}", start, second );
        }

        // Minute
        minute = loaded().minuteAtom.nextValue( minute );
        if( minute == nil )
        {
            second = loaded().secondAtom.minAllowed();
            minute = loaded().minuteAtom.minAllowed();
            hour++;

            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextMinute was -1, set to {}, second to {} and hour to {}",
                          start, minute, second, hour );
        }
        else if( minute > baseMinute )
        {
            second = loaded().secondAtom.minAllowed();

            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextMinute was before baseMinute, set second to {}",
                          start, second );
        }
        else
        {
            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextMinute is {}", start, minute );
        }

        // Hour
        hour = loaded().hourAtom.nextValue( hour );
        if( hour == nil )
        {
            second = loaded().secondAtom.minAllowed();
            minute = loaded().minuteAtom.minAllowed();
            hour = loaded().hourAtom.minAllowed();
            dayOfMonth++;
        }
        else if( hour > baseHour )
        {
            second = loaded().secondAtom.minAllowed();
            minute = loaded().minuteAtom.minAllowed();
        }

        // DayOfMonth
        dayOfMonth = loaded().dayOfMonthAtom.nextValue( dayOfMonth );
        boolean retry = true;
        while( retry )
        {
            if( dayOfMonth == nil )
            {
                second = loaded().secondAtom.minAllowed();
                minute = loaded().minuteAtom.minAllowed();
                hour = loaded().hourAtom.minAllowed();
                dayOfMonth = loaded().dayOfMonthAtom.minAllowed();
                month++;
            }
            else if( dayOfMonth > baseDayOfMonth )
            {
                second = loaded().secondAtom.minAllowed();
                minute = loaded().minuteAtom.minAllowed();
                hour = loaded().hourAtom.minAllowed();
            }

            // Month
            month = loaded().monthAtom.nextValue( month );
            if( month == nil )
            {
                second = loaded().secondAtom.minAllowed();
                minute = loaded().minuteAtom.minAllowed();
                hour = loaded().hourAtom.minAllowed();
                dayOfMonth = loaded().dayOfMonthAtom.minAllowed();
                month = loaded().monthAtom.minAllowed();
                year++;
            }
            else if( month > baseMonth )
            {
                second = loaded().secondAtom.minAllowed();
                minute = loaded().minuteAtom.minAllowed();
                hour = loaded().hourAtom.minAllowed();
                dayOfMonth = loaded().dayOfMonthAtom.minAllowed();
            }

            boolean dateChanged = dayOfMonth != baseDayOfMonth
                                  || month != baseMonth
                                  || year != baseYear;

            if( dayOfMonth > 28
                && dateChanged
                && dayOfMonth > new DateTime( year, month, 15, 12, 0, 0, 0 ).dayOfMonth().getMaximumValue() )
            {
                dayOfMonth = nil;
            }
            else
            {
                retry = false;
            }

            if( retry && LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "CronSchedule.firstRunAfter({}) DayOfMonth retry", start );
            }
        }

        if( year > loaded().yearAtom.maxAllowed() )
        {
            // FIXME Better log message
            LOGGER.trace( "CronSchedule.firstRunAfter({}) Resolved is out of scope, returning null", start );
            return null;
        }

        DateTime nextTime = new DateTime( year, month, dayOfMonth, hour, minute, second, 0 );

        if( loaded().dayOfWeekAtom.nextValue( nextTime.getDayOfWeek() ) == nextTime.getDayOfWeek() )
        {
            LOGGER.trace( "CronSchedule.firstRunAfter({}) Got it! Returning {}", start, nextTime );
            return nextTime;
        }
        LOGGER.trace( "CronSchedule.firstRunAfter({}) Recursion: {} is not acceptable", start, nextTime );
        return firstRunAfter( new DateTime( year, month, dayOfMonth, 23, 59, 0, 0 ) );
    }

    @Override
    public String toString()
    {
        return loaded().expression;
    }
}
