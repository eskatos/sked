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
package org.codeartisans.sked.crontab.schedule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/* package */ final class CronScheduleUtil
{
    /* package */ static String[] validateAndSplitExpression( String cronExpression )
    {
        if( cronExpression == null || cronExpression.length() <= 0 )
        {
            throw new IllegalArgumentException( "Cron expression is null or empty" );
        }
        if( cronExpression.length() != cronExpression.trim().length() )
        {
            throw new IllegalArgumentException( "Cron expression has heading or trailing spaces" );
        }
        String[] splittedExpression = CronScheduleUtil.split( CronScheduleUtil.parseSpecialStrings( cronExpression ) );
        if( splittedExpression.length != 7 )
        {
            throw new IllegalArgumentException( "Cron expression did not resolve to a 7 atoms expression" );
        }
        for( int idx = 0; idx < splittedExpression.length; idx++ )
        {
            String atom = splittedExpression[idx];
            String regex;
            switch( idx )
            {
                case 0: // second
                case 1: // minute
                case 2: // hour
                    regex = "[0-9\\-\\*,\\/]";
                    break;
                case 3: // dayOfMonth
                    regex = "[0-9\\-\\*,\\/\\?LW]";
                    break;
                case 4: // month
                    atom = CronScheduleUtil.replaceMonthNames( atom );
                    splittedExpression[idx] = atom;
                    regex = "[0-9\\-\\*,\\/]";
                    break;
                case 5: // dayOfWeek
                    atom = CronScheduleUtil.replaceDayOfWeekNames( atom );
                    splittedExpression[idx] = atom;
                    regex = "[0-9\\-\\*,\\/\\?L#]";
                    break;
                case 6: // year
                    regex = "[0-9\\-\\*,\\/]";
                    break;
                default:
                    throw new IllegalStateException( "Guru meditation!" );
            }
            if( atom.replaceAll( regex, "" ).trim().length() > 0 )
            {
                throw new IllegalArgumentException( "String atom contains unauthorized characaters: " + atom );
            }
        }
        return splittedExpression;
    }

    private static final String YEARLY_SPECIAL = "@yearly";
    private static final String ANNUALY_SPECIAL = "@annualy";
    private static final String MONTHLY_SPECIAL = "@monthly";
    private static final String WEEKLY_SPECIAL = "@weekly";
    private static final String DAILY_SPECIAL = "@daily";
    private static final String MIDNIGHT_SPECIAL = "@midnight";
    private static final String HOURLY_SPECIAL = "@hourly";
    private static final String MINUTELY_SPECIAL = "@minutely";
    private static final Map<String, String> SPECIAL_STRINGS;

    static
    {
        Map<String, String> specialStrings = new HashMap<String, String>();
        specialStrings.put( YEARLY_SPECIAL, "0 0 0 1 1 *" );
        specialStrings.put( ANNUALY_SPECIAL, "0 0 0 1 1 *" );
        specialStrings.put( MONTHLY_SPECIAL, "0 0 0 1 * *" );
        specialStrings.put( WEEKLY_SPECIAL, "0 0 0 * * 0" );
        specialStrings.put( DAILY_SPECIAL, "0 0 0 * * *" );
        specialStrings.put( MIDNIGHT_SPECIAL, "0 0 0 * * *" );
        specialStrings.put( HOURLY_SPECIAL, "0 0 * * * *" );
        specialStrings.put( MINUTELY_SPECIAL, "0 * * * * *" );
        SPECIAL_STRINGS = Collections.unmodifiableMap( specialStrings );
    }

    private static final String SPLIT_REGEX = "\\s+";

    private static String[] split( String cronExpression )
    {
        String[] splittedExpression = cronExpression.split( SPLIT_REGEX );
        if( splittedExpression.length == 6 )
        {
            return ( cronExpression + " *" ).split( SPLIT_REGEX ); // Adding optional year
        }
        return splittedExpression;
    }

    private static String parseSpecialStrings( String cronExpression )
    {
        String specialString = SPECIAL_STRINGS.get( cronExpression );
        if( specialString == null )
        {
            return cronExpression;
        }
        return specialString;
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

    private CronScheduleUtil()
    {
    }
}
