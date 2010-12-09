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

import org.joda.time.DateTime;

import static org.junit.Assert.*;
import org.junit.Test;

import org.codeartisans.sked.crontab.schedule.CronSchedule;

/**
 * @author Paul Merlin
 */
@SuppressWarnings( "ResultOfObjectAllocationIgnored" )
public class CronTest
{

    @Test
    public void testWrongExpressions()
    {
        testWrongExpressionsEffective( null, "Cron expression cannot be null" );
        testWrongExpressionsEffective( "", "Cron expression cannot be empty" );
        testWrongExpressionsEffective( "1", "Cron expression must be composed of 6 or 7 atoms" );
        testWrongExpressionsEffective( "1 2", "Cron expression must be composed of 6 or 7 atoms" );
        testWrongExpressionsEffective( "1 2 3", "Cron expression must be composed of 6 or 7 atoms" );
        testWrongExpressionsEffective( "1 2 3 4", "Cron expression must be composed of 6 or 7 atoms" );
        testWrongExpressionsEffective( "1 2 3 4 5", "Cron expression must be composed of 6 or 7 atoms" );
        testWrongExpressionsEffective( "1 2 3 4 5 6 7 8", "Cron expression must be composed of 6 or 7 atoms" );
        testWrongExpressionsEffective( "A * * * * *", "Cron expression contains unauthorized characters" );
        testWrongExpressionsEffective( "1 ? * * * *", "Cron expression allow ? only on dof and dow" );
    }

    private void testWrongExpressionsEffective( String cronExpression, String message )
    {
        try {
            new CronSchedule( cronExpression );
            fail( message );
        } catch ( IllegalArgumentException ex ) {
            // expected
        }
    }

    @Test
    public void testSpecialStrings()
    {
        DateTime start = new DateTime();

        CronSchedule minutely = new CronSchedule( "@minutely" );
        assertEquals( "0 * * * * * *", minutely.toString() );
        assertEquals( start.withMillisOfSecond( 0 ).withSecondOfMinute( 0 ).plusMinutes( 1 ),
                      minutely.firstRunAfter( start ) );

        CronSchedule hourly = new CronSchedule( "@hourly" );
        assertEquals( "0 0 * * * * *", hourly.toString() );
        assertEquals( start.withMillisOfSecond( 0 ).withSecondOfMinute( 0 ).withMinuteOfHour( 0 ).plusHours( 1 ),
                      hourly.firstRunAfter( start ) );

        CronSchedule midnight = new CronSchedule( "@midnight" );
        assertEquals( "0 0 0 * * * *", midnight.toString() );
        assertEquals( start.withMillisOfDay( 0 ).plusDays( 1 ),
                      midnight.firstRunAfter( start ) );

        CronSchedule daily = new CronSchedule( "@daily" );
        assertEquals( "0 0 0 * * * *", daily.toString() );
        assertEquals( start.withMillisOfDay( 0 ).plusDays( 1 ),
                      daily.firstRunAfter( start ) );

        CronSchedule weekly = new CronSchedule( "@weekly" );
        assertEquals( "0 0 0 * * 0 *", weekly.toString() );
        assertEquals( start.withMillisOfDay( 0 ).plusDays( start.dayOfWeek().getMaximumValue() - start.getDayOfWeek() ),
                      weekly.firstRunAfter( start ) );

        CronSchedule monthly = new CronSchedule( "@monthly" );
        assertEquals( "0 0 0 1 * * *", monthly.toString() );
        assertEquals( start.withMillisOfDay( 0 ).plusMonths( 1 ).withDayOfMonth( 1 ),
                      monthly.firstRunAfter( start ) );

        CronSchedule annualy = new CronSchedule( "@annualy" );
        assertEquals( "0 0 0 1 1 * *", annualy.toString() );
        assertEquals( start.withMillisOfDay( 0 ).withDayOfYear( 1 ).plusYears( 1 ),
                      annualy.firstRunAfter( start ) );

        CronSchedule yearly = new CronSchedule( "@yearly" );
        assertEquals( "0 0 0 1 1 * *", yearly.toString() );
        assertEquals( start.withMillisOfDay( 0 ).withDayOfYear( 1 ).plusYears( 1 ),
                      yearly.firstRunAfter( start ) );
    }

    @Test
    public void testGoodExpressions()
    {
        new CronSchedule( "2-4,10-30/2 */2 * * * * *" );
        new CronSchedule( "2-4,10-30/2 */2 * ? * 3 *" );
        new CronSchedule( "0 0 23 ? * MON-fRi" );
        new CronSchedule( "0 2/3 1,9,22 11-26 1-6 ? 2003" );
    }

    @Test
    public void test()
    {
        DateTime start = new DateTime();
        DateTime expected = start.withMillisOfSecond( 0 ).withSecondOfMinute( 0 ).plusMinutes( 1 );
        CronSchedule cronSchedule = new CronSchedule( "@minutely" );
        Long nextRun = cronSchedule.firstRunAfter( start ).getMillis();

        System.out.println( "====================================" );
        System.out.println( start );
        System.out.println( expected );
        System.out.println( nextRun );
        System.out.println( new DateTime( nextRun ) );
        System.out.println( "====================================" );

    }

}
