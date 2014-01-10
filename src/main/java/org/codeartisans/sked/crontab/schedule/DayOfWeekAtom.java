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

final class DayOfWeekAtom
        extends AbstractCronAtom
{

    public DayOfWeekAtom( String atom )
    {
        super( atom );
    }

    @Override
    public int maxAllowed()
    {
        return 7;
    }

    @Override
    protected void afterParseAtom()
    {
        // O and 7 are the same day of week, prefer 7 as it is the one used by Date
        if ( possibleValues.contains( 0 ) ) {
            possibleValues.remove( 0 );
            possibleValues.add( 7 );
        }
    }

    @Override
    protected boolean canBeOmmited()
    {
        return true;
    }

}