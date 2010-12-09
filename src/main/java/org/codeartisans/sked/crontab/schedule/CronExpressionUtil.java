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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Paul Merlin
 */
public final class CronExpressionUtil
{

    static final String YEARLY_SPECIAL = "@yearly";
    static final String ANNUALY_SPECIAL = "@annualy";
    static final String MONTHLY_SPECIAL = "@monthly";
    static final String WEEKLY_SPECIAL = "@weekly";
    static final String DAILY_SPECIAL = "@daily";
    static final String MIDNIGHT_SPECIAL = "@midnight";
    static final String HOURLY_SPECIAL = "@hourly";
    static final String MINUTELY_SPECIAL = "@minutely";
    public static final Map<String, String> SPECIAL_STRINGS;

    static {
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

    public static String[] split( String cronExpression )
    {
        String[] splittedExpression = cronExpression.split( SPLIT_REGEX );
        if ( splittedExpression.length == 6 ) {
            return ( cronExpression + " *" ).split( SPLIT_REGEX ); // Adding optional year
        }
        return splittedExpression;
    }

    public static String parseSpecialStrings( String cronExpression )
    {
        String specialString = SPECIAL_STRINGS.get( cronExpression );
        if ( specialString == null ) {
            return cronExpression;
        }
        return specialString;
    }

    private CronExpressionUtil()
    {
    }

}
