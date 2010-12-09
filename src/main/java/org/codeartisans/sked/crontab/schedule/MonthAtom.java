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

public class MonthAtom
        extends AbstractCronAtom
{

    public MonthAtom( String atom )
    {
        super( atom );
    }

    @Override
    public int minAllowed()
    {
        return 1;
    }

    @Override
    public int maxAllowed()
    {
        return 12;
    }

}
