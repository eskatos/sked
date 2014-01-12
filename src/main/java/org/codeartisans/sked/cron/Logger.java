/*
 * Copyright (c) 2014, Paul Merlin. All Rights Reserved.
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

import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings(
     {
        "CallToThreadDumpStack", "BroadCatchBlock", "TooBroadCatch", "UseSpecificCatch"
    } )
/* package */ final class Logger
{
    private static final Object LOGGER;
    private static final Method TRACE_METHOD;

    static
    {
        Object logger;
        Method traceMethod;
        try
        {
            logger = Class.forName( "org.slf4j.LoggerFactory" ).
                getMethod( "getLogger", new Class[]
                    {
                        String.class
                } ).
                invoke( null, "org.codeartisans.sked.cron" );
            traceMethod = Class.forName( "org.slf4j.Logger" ).
                getMethod( "trace", new Class[]
                    {
                        String.class,
                        Object[].class
                } );
        }
        catch( Exception ex )
        {
            logger = null;
            traceMethod = null;
        }
        LOGGER = logger;
        TRACE_METHOD = traceMethod;
    }

    /* package */ static void trace( String message, Object... objects )
    {
        if( LOGGER != null )
        {
            try
            {
                TRACE_METHOD.invoke( LOGGER, new Object[]
                {
                    message, objects
                } );
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
            }
        }
    }
}
