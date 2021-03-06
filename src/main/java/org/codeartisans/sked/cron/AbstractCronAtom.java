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

import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings( "ProtectedField" )
/* package */ abstract class AbstractCronAtom
    implements CronAtom
{
    protected final String atom;
    protected final SortedSet<Integer> possibleValues = new TreeSet<Integer>();
    private boolean ommited = false;

    @SuppressWarnings( "OverridableMethodCallInConstructor" ) // The class hierarchy is stable as all chidren in this package are final
    /* package */ AbstractCronAtom( String atom )
    {
        this.atom = atom;
        parseAtom();
    }

    @SuppressWarnings( "AssignmentToForLoopParameter" )
    private void parseAtom()
    {
        if( "?".equals( atom ) )
        {
            if( !canBeOmmited() )
            {
                throw new IllegalArgumentException( "? is not allowed in this field" );
            }
            // Ommited
            ommited = true;
        }
        else if( "*".equals( atom ) )
        {
            // All values
            addRangeToPossibleValues( minAllowed(), maxAllowed() );
        }
        else if( !containsSpecialChars( atom ) )
        {
            // Simple numeric value
            possibleValues.add( Integer.valueOf( atom ) );
        }
        else
        {
            String[] subAtomSet = atom.split( "," );
            for( String subAtom : subAtomSet )
            {
                int step = 1;
                int stepMarkerIndex = subAtom.indexOf( "/" );
                if( stepMarkerIndex > 0 )
                {
                    // Stepped atom
                    step = Integer.valueOf( subAtom.substring( stepMarkerIndex + 1 ) );
                    subAtom = subAtom.substring( 0, stepMarkerIndex );
                }
                if( "*".equals( subAtom ) )
                {
                    // */step atom
                    addSteppedRangeToPossibleValues( minAllowed(), maxAllowed(), step );
                }
                else if( subAtom.contains( "-" ) )
                {
                    // <range> atom maybe stepped
                    int rangeMarkerIndex = subAtom.indexOf( "-" );
                    int start = Integer.valueOf( subAtom.substring( 0, rangeMarkerIndex ) );
                    int stop = Integer.valueOf( subAtom.substring( rangeMarkerIndex + 1 ) );
                    addSteppedRangeToPossibleValues( start, stop, step );
                }
                else
                {
                    if( step == 1 )
                    {
                        // SubAtom is a simple numeric value
                        possibleValues.add( Integer.valueOf( subAtom ) );
                    }
                    else
                    {
                        // SubAtom <number>/<step> means start a range at <number> and use <step>
                        addSteppedRangeToPossibleValues( Integer.valueOf( subAtom ), maxAllowed(), step );
                    }
                }
            }
        }
        afterParseAtom();
    }

    /**
     * Used to implement the ? special char handling for DayOfWeek and DayOfMonth.
     * @return If this atom can be ommited
     */
    protected boolean canBeOmmited()
    {
        return false;
    }

    protected void afterParseAtom()
    {
        // NOOP
    }

    @Override
    public int nextValue( int start )
    {
        SortedSet<Integer> tail = possibleValues.tailSet( start );
        if( tail.isEmpty() )
        {
            return -1;
        }
        return tail.first();
    }

    @Override
    public int minAllowed()
    {
        return 0;
    }

    @Override
    public abstract int maxAllowed();

    private static final char[] SPECIAL_CHARS = new char[]
    {
        '*', ',', '/', '-'
    };

    private boolean containsSpecialChars( String atom )
    {
        for( char specialChar : SPECIAL_CHARS )
        {
            if( atom.indexOf( specialChar ) != -1 )
            {
                return true;
            }
        }
        return false;
    }

    private void addRangeToPossibleValues( int start, int stop )
    {
        addSteppedRangeToPossibleValues( start, stop, 1 );
    }

    private void addSteppedRangeToPossibleValues( int start, int stop, int step )
    {
        for( int idx = start; idx <= stop; idx += step )
        {
            possibleValues.add( idx );
        }
    }

    /**
     * @return The atom string used to create this instance.
     */
    @Override
    public final String toString()
    {
        return atom;
    }
}
