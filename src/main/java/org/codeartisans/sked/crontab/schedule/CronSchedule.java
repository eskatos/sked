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
package org.codeartisans.sked.crontab.schedule;

import java.io.Serializable;
import org.codeartisans.sked.Sked;

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
public class CronSchedule
        implements Serializable
{

    private static final long serialVersionUID = 1L;

    public static boolean isExpressionValid( String cronExpression )
    {
        try {
            CronScheduleUtil.validateAndSplitExpression( cronExpression );
            return true;
        } catch ( IllegalArgumentException ex ) {
        }
        return false;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger( Sked.LOGGER_NAME );

    private final CronAtom secondAtom;

    private final CronAtom minuteAtom;

    private final CronAtom hourAtom;

    private final CronAtom dayOfMonthAtom;

    private final CronAtom monthAtom;

    private final CronAtom dayOfWeekAtom;

    private final CronAtom yearAtom;

    public CronSchedule( String cronExpression )
    {
        String[] splitted = CronScheduleUtil.validateAndSplitExpression( cronExpression );
        secondAtom = new SecondAtom( splitted[0] );
        minuteAtom = new MinuteAtom( splitted[1] );
        hourAtom = new HourAtom( splitted[2] );
        dayOfMonthAtom = new DayOfMonthAtom( splitted[3] );
        monthAtom = new MonthAtom( splitted[4] );
        dayOfWeekAtom = new DayOfWeekAtom( splitted[5] );
        yearAtom = new YearAtom( splitted[6] );
    }

    public final Long firstRunAfter( Long start )
    {
        DateTime nextRun = firstRunAfter( new DateTime( start ) );
        if ( nextRun == null ) {
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
        second = secondAtom.nextValue( second );
        if ( second == nil ) {

            second = secondAtom.minAllowed();
            minute++;

            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextSecond was -1, set to {} and minute to {}",
                          new Object[]{ start, second, minute } );
        } else {
            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextSecond is {}", start, second );
        }

        // Minute
        minute = minuteAtom.nextValue( minute );
        if ( minute == nil ) {

            second = secondAtom.minAllowed();
            minute = minuteAtom.minAllowed();
            hour++;

            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextMinute was -1, set to {}, second to {} and hour to {}",
                          new Object[]{ start, minute, second, hour } );
        } else if ( minute > baseMinute ) {

            second = secondAtom.minAllowed();

            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextMinute was before baseMinute, set second to {}", start, second );
        } else {
            LOGGER.trace( "CronSchedule.firstRunAfter({}) nextMinute is {}", start, minute );
        }

        // Hour
        hour = hourAtom.nextValue( hour );
        if ( hour == nil ) {
            second = secondAtom.minAllowed();
            minute = minuteAtom.minAllowed();
            hour = hourAtom.minAllowed();
            dayOfMonth++;
        } else if ( hour > baseHour ) {
            second = secondAtom.minAllowed();
            minute = minuteAtom.minAllowed();
        }

        // DayOfMonth
        dayOfMonth = dayOfMonthAtom.nextValue( dayOfMonth );
        boolean retry = true;
        while ( retry ) {
            if ( dayOfMonth == nil ) {
                second = secondAtom.minAllowed();
                minute = minuteAtom.minAllowed();
                hour = hourAtom.minAllowed();
                dayOfMonth = dayOfMonthAtom.minAllowed();
                month++;
            } else if ( dayOfMonth > baseDayOfMonth ) {
                second = secondAtom.minAllowed();
                minute = minuteAtom.minAllowed();
                hour = hourAtom.minAllowed();
            }

            // Month
            month = monthAtom.nextValue( month );
            if ( month == nil ) {
                second = secondAtom.minAllowed();
                minute = minuteAtom.minAllowed();
                hour = hourAtom.minAllowed();
                dayOfMonth = dayOfMonthAtom.minAllowed();
                month = monthAtom.minAllowed();
                year++;
            } else if ( month > baseMonth ) {
                second = secondAtom.minAllowed();
                minute = minuteAtom.minAllowed();
                hour = hourAtom.minAllowed();
                dayOfMonth = dayOfMonthAtom.minAllowed();
            }

            boolean dateChanged = dayOfMonth != baseDayOfMonth || month != baseMonth || year != baseYear;

            if ( dayOfMonth > 28 && dateChanged && dayOfMonth > new DateTime( year, month, 15, 12, 0, 0, 0 ).dayOfMonth().getMaximumValue() ) {
                dayOfMonth = nil;
            } else {
                retry = false;
            }

            if ( retry && LOGGER.isTraceEnabled() ) {
                LOGGER.trace( "CronSchedule.firstRunAfter({}) DayOfMonth retry", start );
            }

        }

        if ( year > yearAtom.maxAllowed() ) {
            LOGGER.trace( "CronSchedule.firstRunAfter({}) Resolved is out of scope, returning null", start ); // FIXME Better log message
            return null;
        }

        DateTime nextTime = new DateTime( year, month, dayOfMonth, hour, minute, second, 0 );

        if ( dayOfWeekAtom.nextValue( nextTime.getDayOfWeek() ) == nextTime.getDayOfWeek() ) {
            LOGGER.trace( "CronSchedule.firstRunAfter({}) Got it! Returning {}", start, nextTime );
            return nextTime;
        }
        LOGGER.trace( "CronSchedule.firstRunAfter({}) Recursion: {} is not acceptable", start, nextTime );
        return firstRunAfter( new DateTime( year, month, dayOfMonth, 23, 59, 0, 0 ) );
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append( secondAtom ).append( " " ).
                append( minuteAtom ).append( " " ).
                append( hourAtom ).append( " " ).
                append( dayOfMonthAtom ).append( " " ).
                append( monthAtom ).append( " " ).
                append( dayOfWeekAtom ).append( " " ).
                append( yearAtom ).toString();
    }

}
