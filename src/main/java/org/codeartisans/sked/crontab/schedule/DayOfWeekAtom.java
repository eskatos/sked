/*
 * Created on 7 déc. 2010
 *
 * Licenced under the Netheos Licence, Version 1.0 (the "Licence"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at :
 *
 * http://www.netheos.net/licences/LICENCE-1.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright (c) Netheos
 */
package org.codeartisans.sked.crontab.schedule;

public class DayOfWeekAtom
        extends AbstractCronAtom
{

    public DayOfWeekAtom( String atom )
    {
        super( atom );
    }

    @Override
    protected void afterParseAtom()
    {
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

    @Override
    public int maxAllowed()
    {
        return 7;
    }

}
