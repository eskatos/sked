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

import java.util.regex.Pattern;

import org.joda.time.DateTime;

/**
 * Cron expression parsing is based on the GNU crontab manpage that can be found
 * here: http://unixhelp.ed.ac.uk/CGI/man-cgi?crontab+5
 *
 * The following extensions are used:
 * <ul>
 *      <li>a mandatory field is added at the begining: seconds.</li>
 *      <li>a special string is added: @minutely</li>
 * </ul>
 *
 * So, no, it does not follow the Quartz Scheduler expression format.
 * And yes, the wikipedia page http://en.wikipedia.org/wiki/CRON_expression has
 * nothing to do with unix cron expression but only Quartz Scheduler expression
 * format.
 * 
 * @author Paul Merlin
 */
public class CronSchedule
{

    private final CronAtom secondAtom;
    private final CronAtom minuteAtom;
    private final CronAtom hourAtom;
    private final CronAtom dayOfMonthAtom;
    private final CronAtom monthAtom;
    private final CronAtom dayOfWeekAtom;
    private final CronAtom yearAtom;

    public CronSchedule( String cronExpression )
    {
        String[] splitted = validateAndSplitExpression( cronExpression );
        secondAtom = new SecondAtom( splitted[0] );
        minuteAtom = new MinuteAtom( splitted[1] );
        hourAtom = new HourAtom( splitted[2] );
        dayOfMonthAtom = new DayOfMonthAtom( splitted[3] );
        monthAtom = new MonthAtom( splitted[4] );
        dayOfWeekAtom = new DayOfWeekAtom( splitted[5] );
        yearAtom = new YearAtom( splitted[6] );
    }

    public Long firstRunAfter( Long start )
    {
        DateTime nextRun = firstRunAfter( new DateTime( start ) );
        if ( nextRun == null ) {
            return null;
        }
        return nextRun.getMillis();
    }

    public DateTime firstRunAfter( DateTime start )
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
        second = secondAtom.next( second );
        if ( second == nil ) {
            second = secondAtom.minAllowed();
            minute++;
        }

        // Minute
        minute = minuteAtom.next( minute );
        if ( minute == nil ) {
            second = secondAtom.minAllowed();
            minute = minuteAtom.minAllowed();
            hour++;
        } else if ( minute > baseMinute ) {
            second = secondAtom.minAllowed();
        }

        // Hour
        hour = hourAtom.next( hour );
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

        dayOfMonth = dayOfMonthAtom.next( dayOfMonth );
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
            month = monthAtom.next( month );
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

            if ( dayOfMonth > 28 && dateChanged && dayOfMonth > daysOfMonth( year, month ) ) {
                dayOfMonth = nil;
            } else {
                retry = false;
            }
        }

        if ( year > yearAtom.maxAllowed() ) {
            return null;
        }

        DateTime nextTime = new DateTime( year, month, dayOfMonth, hour, minute, second, 0 );

        if ( dayOfWeekAtom.next( nextTime.getDayOfWeek() ) == nextTime.getDayOfWeek() ) {
            return nextTime;
        }

        return firstRunAfter( new DateTime( year, month, dayOfMonth, 23, 59, 0, 0 ) );
    }

    private static int daysOfMonth( int year, int month )
    {
        DateTime dateTime = new DateTime( year, month, 15, 12, 0, 0, 0 );
        return dateTime.dayOfMonth().getMaximumValue();
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

    public static boolean isExpressionValid( String cronExpression )
    {
        try {
            validateAndSplitExpression( cronExpression );
            return true;
        } catch ( IllegalArgumentException ex ) {
        }
        return false;
    }

    private static String[] validateAndSplitExpression( String cronExpression )
    {
        if ( cronExpression == null || cronExpression.length() <= 0 ) {
            throw new IllegalArgumentException( "Cron expression is null or empty" );
        }
        if ( cronExpression.length() != cronExpression.trim().length() ) {
            throw new IllegalArgumentException( "Cron expression has heading or trailing spaces" );
        }
        String[] splittedExpression = CronScheduleUtil.split( CronScheduleUtil.parseSpecialStrings( cronExpression ) );
        if ( splittedExpression.length != 7 ) {
            throw new IllegalArgumentException( "Cron expression did not resolve to a 7 atoms expression" );
        }
        for ( int idx = 0; idx < splittedExpression.length; idx++ ) {
            String atom = splittedExpression[idx];
            String regex;
            switch ( idx ) {
                case 0: // second
                case 1: // minute
                case 2: // hour
                    regex = "[0-9\\-\\*,\\/]";
                    break;
                case 3: // dayOfMonth
                    regex = "[0-9\\-\\*,\\/\\?LW]";
                    break;
                case 4: // month
                    atom = replaceMonthNames( atom );
                    splittedExpression[idx] = atom;
                    regex = "[0-9\\-\\*,\\/]";
                    break;
                case 5: // dayOfWeek
                    atom = replaceDayOfWeekNames( atom );
                    splittedExpression[idx] = atom;
                    regex = "[0-9\\-\\*,\\/\\?L#]";
                    break;
                case 6: // year
                    regex = "[0-9\\-\\*,\\/]";
                    break;
                default:
                    throw new IllegalStateException( "Guru meditation!" );
            }
            if ( atom.replaceAll( regex, "" ).trim().length() > 0 ) {
                throw new IllegalArgumentException( "String atom contains unauthorized characaters: " + atom );
            }
        }
        // TODO
        return splittedExpression;
    }

    private static final Pattern JAN = Pattern.compile( "jan", Pattern.CASE_INSENSITIVE );
    private static final Pattern FEB = Pattern.compile( "feb", Pattern.CASE_INSENSITIVE );
    private static final Pattern MAR = Pattern.compile( "mar", Pattern.CASE_INSENSITIVE );
    private static final Pattern APR = Pattern.compile( "apr", Pattern.CASE_INSENSITIVE );
    private static final Pattern MAY = Pattern.compile( "may", Pattern.CASE_INSENSITIVE );
    private static final Pattern JUN = Pattern.compile( "jun", Pattern.CASE_INSENSITIVE );
    private static final Pattern JUL = Pattern.compile( "jul", Pattern.CASE_INSENSITIVE );
    private static final Pattern AUG = Pattern.compile( "aug", Pattern.CASE_INSENSITIVE );
    private static final Pattern SEP = Pattern.compile( "sep", Pattern.CASE_INSENSITIVE );
    private static final Pattern OCT = Pattern.compile( "oct", Pattern.CASE_INSENSITIVE );
    private static final Pattern NOV = Pattern.compile( "nov", Pattern.CASE_INSENSITIVE );
    private static final Pattern DEC = Pattern.compile( "dec", Pattern.CASE_INSENSITIVE );

    @SuppressWarnings( "AssignmentToMethodParameter" )
    private static String replaceMonthNames( String atom )
    {
        atom = JAN.matcher( atom ).replaceAll( "1" );
        atom = FEB.matcher( atom ).replaceAll( "2" );
        atom = MAR.matcher( atom ).replaceAll( "3" );
        atom = APR.matcher( atom ).replaceAll( "4" );
        atom = MAY.matcher( atom ).replaceAll( "5" );
        atom = JUN.matcher( atom ).replaceAll( "6" );
        atom = JUL.matcher( atom ).replaceAll( "7" );
        atom = AUG.matcher( atom ).replaceAll( "8" );
        atom = SEP.matcher( atom ).replaceAll( "9" );
        atom = OCT.matcher( atom ).replaceAll( "10" );
        atom = NOV.matcher( atom ).replaceAll( "11" );
        atom = DEC.matcher( atom ).replaceAll( "12" );
        return atom;
    }

    private static final Pattern MON = Pattern.compile( "mon", Pattern.CASE_INSENSITIVE );
    private static final Pattern TUE = Pattern.compile( "tue", Pattern.CASE_INSENSITIVE );
    private static final Pattern WED = Pattern.compile( "wed", Pattern.CASE_INSENSITIVE );
    private static final Pattern THU = Pattern.compile( "thu", Pattern.CASE_INSENSITIVE );
    private static final Pattern FRI = Pattern.compile( "fri", Pattern.CASE_INSENSITIVE );
    private static final Pattern SAT = Pattern.compile( "sat", Pattern.CASE_INSENSITIVE );
    private static final Pattern SUN = Pattern.compile( "sun", Pattern.CASE_INSENSITIVE );

    @SuppressWarnings( "AssignmentToMethodParameter" )
    private static String replaceDayOfWeekNames( String atom )
    {
        atom = MON.matcher( atom ).replaceAll( "1" );
        atom = TUE.matcher( atom ).replaceAll( "2" );
        atom = WED.matcher( atom ).replaceAll( "3" );
        atom = THU.matcher( atom ).replaceAll( "4" );
        atom = FRI.matcher( atom ).replaceAll( "5" );
        atom = SAT.matcher( atom ).replaceAll( "6" );
        atom = SUN.matcher( atom ).replaceAll( "7" );
        return atom;
    }

}
