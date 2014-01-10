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

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// TODO Rewrite tests using fixed dates so the build is predictible
public class CronScheduleFactoryTest
{

    @Test
    public void testFromExpression()
    {
        String expression = "0 * * * * * *";
        CronScheduleFactory factory = new CronScheduleFactoryImpl();
        CronSchedule cronSchedule = factory.newInstance( expression );
        assertEquals( expression, cronSchedule.toString() );
    }

    @Test
    public void testFromDate()
    {
        DateTime date = new DateTime();
        DateTime expectedDate = new DateTime( (long) ( 1000L * Math.ceil( date.getMillis() / 1000 ) ) );

        String expected = expectedDate.getSecondOfMinute() + " "
                          + expectedDate.getMinuteOfHour() + " "
                          + expectedDate.getHourOfDay() + " "
                          + expectedDate.getDayOfMonth() + " "
                          + expectedDate.getMonthOfYear() + " * "
                          + expectedDate.getYear();

        CronScheduleFactory factory = new CronScheduleFactoryImpl();

        CronSchedule cronSchedule = factory.newNowInstance();
        assertEquals( expected, cronSchedule.toString() );

        int delay = 1;
        cronSchedule = factory.newNowInstance( delay );
        DateTime plusSeconds = expectedDate.plusSeconds( delay );
        String expectedPlusSeconds = plusSeconds.getSecondOfMinute() + " "
                                     + plusSeconds.getMinuteOfHour() + " "
                                     + plusSeconds.getHourOfDay() + " "
                                     + plusSeconds.getDayOfMonth() + " "
                                     + plusSeconds.getMonthOfYear() + " * "
                                     + plusSeconds.getYear();
        assertEquals( expectedPlusSeconds, cronSchedule.toString() );

        cronSchedule = factory.newInstance( date.toDate() );
        assertEquals( expected, cronSchedule.toString() );

        cronSchedule = factory.newInstance( date.getMillis() );
        assertEquals( expected, cronSchedule.toString() );

    }

}
