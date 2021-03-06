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

import java.io.IOException;
import org.codeartisans.junit.Assert.PostSerializationAssertions;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.codeartisans.junit.Assert.assertSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@SuppressWarnings( "ResultOfObjectAllocationIgnored" )
public class CronScheduleTest
{
    @Test
    public void testSerialization()
        throws IOException, ClassNotFoundException
    {
        final CronSchedule tested = new CronSchedule( "@minutely" );
        assertSerializable( tested, new PostSerializationAssertions<CronSchedule>()
        {
            @Override
            public void postSerializationAssertions( CronSchedule copy )
            {
                assertEquals( tested.toString(), copy.toString() );
            }
        } );
    }

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
        try
        {
            new CronSchedule( cronExpression );
            fail( message );
        }
        catch( IllegalArgumentException ex )
        {
            // expected
        }
    }

    @Test
    public void testSpecialStrings()
    {
        DateTime start = new DateTime();

        CronSchedule minutely = new CronSchedule( "@minutely" );
        assertEquals( "0 * * * * * *", minutely.toString() );
        assertEquals( Long.valueOf( start.withMillisOfSecond( 0 ).withSecondOfMinute( 0 ).plusMinutes( 1 ).getMillis() ),
                      minutely.firstRunAfter( start.getMillis() ) );

        CronSchedule hourly = new CronSchedule( "@hourly" );
        assertEquals( "0 0 * * * * *", hourly.toString() );
        assertEquals( Long.valueOf( start.withMillisOfSecond( 0 ).withSecondOfMinute( 0 ).withMinuteOfHour( 0 ).plusHours( 1 ).getMillis() ),
                      hourly.firstRunAfter( start.getMillis() ) );

        CronSchedule midnight = new CronSchedule( "@midnight" );
        assertEquals( "0 0 0 * * * *", midnight.toString() );
        assertEquals( Long.valueOf( start.withMillisOfDay( 0 ).plusDays( 1 ).getMillis() ),
                      midnight.firstRunAfter( start.getMillis() ) );

        CronSchedule daily = new CronSchedule( "@daily" );
        assertEquals( "0 0 0 * * * *", daily.toString() );
        assertEquals( Long.valueOf( start.withMillisOfDay( 0 ).plusDays( 1 ).getMillis() ),
                      daily.firstRunAfter( start.getMillis() ) );

        CronSchedule weekly = new CronSchedule( "@weekly" );
        assertEquals( "0 0 0 * * 0 *", weekly.toString() );
        assertEquals( Long.valueOf( start.withMillisOfDay( 0 ).plusDays( start.dayOfWeek().getMaximumValue() - ( start.getDayOfWeek() == 7 ? 0 : start.getDayOfWeek() ) ).getMillis() ),
                      weekly.firstRunAfter( start.getMillis() ) );

        CronSchedule monthly = new CronSchedule( "@monthly" );
        assertEquals( "0 0 0 1 * * *", monthly.toString() );
        assertEquals( Long.valueOf( start.withMillisOfDay( 0 ).plusMonths( 1 ).withDayOfMonth( 1 ).getMillis() ),
                      monthly.firstRunAfter( start.getMillis() ) );

        CronSchedule annualy = new CronSchedule( "@annualy" );
        assertEquals( "0 0 0 1 1 * *", annualy.toString() );
        assertEquals( Long.valueOf( start.withMillisOfDay( 0 ).withDayOfYear( 1 ).plusYears( 1 ).getMillis() ),
                      annualy.firstRunAfter( start.getMillis() ) );

        CronSchedule yearly = new CronSchedule( "@yearly" );
        assertEquals( "0 0 0 1 1 * *", yearly.toString() );
        assertEquals( Long.valueOf( start.withMillisOfDay( 0 ).withDayOfYear( 1 ).plusYears( 1 ).getMillis() ),
                      yearly.firstRunAfter( start.getMillis() ) );
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
    public void testSecondCornerCase()
    {
        CronSchedule minutely = new CronSchedule( "@minutely" );
        Long cornerCase = 1292957700053L;
        Long nextRun = minutely.firstRunAfter( cornerCase );
        assertTrue( nextRun > cornerCase );
        DateTime weirdoDateTime = new DateTime( cornerCase );
        assertEquals( Long.valueOf( weirdoDateTime.withMillisOfSecond( 0 ).withSecondOfMinute( 0 ).plusMinutes( 1 ).getMillis() ),
                      nextRun );
    }

}
