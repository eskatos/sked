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

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings( "ProtectedField" )
abstract class AbstractCronAtom
        implements CronAtom
{

    protected final String atom;
    protected final SortedSet<Integer> possibleValues = new TreeSet<Integer>( new Comparator<Integer>()
    {

        public int compare( Integer o1, Integer o2 )
        {
            return o1.compareTo( o2 );
        }

    } );
    private boolean ommited = false;

    public AbstractCronAtom( String atom )
    {
        this.atom = atom;
        parseAtom();
    }

    @SuppressWarnings( "AssignmentToForLoopParameter" )
    private void parseAtom()
    {
        if ( "?".equals( atom ) ) {
            if ( !canBeOmmited() ) {
                throw new IllegalArgumentException( "? is not allowed in this field" );
            }

            // Ommited
            ommited = true;

        } else if ( "*".equals( atom ) ) {

            // All values
            addRangeToPossibleValues( minAllowed(), maxAllowed() );

        } else if ( !containsSpecialChars( atom ) ) {

            // Simple numeric value
            possibleValues.add( Integer.valueOf( atom ) );

        } else {
            String[] subAtomSet = atom.split( "," );
            for ( String subAtom : subAtomSet ) {
                int step = 1;
                int stepMarkerIndex = subAtom.indexOf( "/" );
                if ( stepMarkerIndex > 0 ) { // Stepped atom
                    step = Integer.valueOf( subAtom.substring( stepMarkerIndex + 1 ) );
                    subAtom = subAtom.substring( 0, stepMarkerIndex );
                }
                if ( "*".equals( subAtom ) ) {

                    // */step atom
                    addSteppedRangeToPossibleValues( minAllowed(), maxAllowed(), step );

                } else if ( subAtom.contains( "-" ) ) {

                    // <range> atom maybe stepped
                    int rangeMarkerIndex = subAtom.indexOf( "-" );
                    int start = Integer.valueOf( subAtom.substring( 0, rangeMarkerIndex ) );
                    int stop = Integer.valueOf( subAtom.substring( rangeMarkerIndex + 1 ) );
                    addSteppedRangeToPossibleValues( start, stop, step );

                } else {
                    if ( step == 1 ) {

                        // SubAtom is a simple numeric value
                        possibleValues.add( Integer.valueOf( subAtom ) );

                    } else {

                        // SubAtom <number>/<step> means start a range at <number> and use <step>
                        addSteppedRangeToPossibleValues( Integer.valueOf( subAtom ), maxAllowed(), step );

                    }
                }
            }
        }
        afterParseAtom();
    }

    protected void afterParseAtom()
    {
        // NOOP
    }

    public int next( int start )
    {
        System.out.println( "Atom.next(" + start + "), possibles are: " + Arrays.toString( possibleValues.toArray() ) );
        SortedSet<Integer> tail = possibleValues.tailSet( start );
        if ( tail.isEmpty() ) {
            return -1;
        }
        return tail.first();
    }

    protected boolean canBeOmmited()
    {
        return false;
    }

    public int minAllowed()
    {
        return 0;
    }

    public abstract int maxAllowed();

    @Override
    public String toString()
    {
        return atom;
    }

    private void addRangeToPossibleValues( int start, int stop )
    {
        addSteppedRangeToPossibleValues( start, stop, 1 );
    }

    private void addSteppedRangeToPossibleValues( int start, int stop, int step )
    {
        for ( int idx = start; idx <= stop; idx += step ) {
            possibleValues.add( idx );
        }
    }

    private boolean containsSpecialChars( String atom )
    {
        for ( String eachSpecialChars : new String[]{ "*", ",", "/", "-" } ) {
            if ( atom.contains( eachSpecialChars ) ) {
                return true;
            }
        }
        return false;
    }

}
