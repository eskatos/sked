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

/**
 * @author Paul Merlin
 */
interface CronAtom
{

    /**
     * Find the next occurence of this atom.
     *
     * @param start     From where this atom start searching, this is inclusive.
     * @return          The nextValue occurence of this atom, -1 if none.
     */
    int nextValue( int start );

    /**
     * @return The minimum allowed value for this atom
     */
    int minAllowed();

    /**
     * @return The maximum allowed value for this atom
     */
    int maxAllowed();

}