/*--------------------------------------------------------
 * Product Name : modus TR-069
 * Version : 1.1
 * Module Name : TR69ClientAPI
 *
 * Copyright © 2011 France Telecom
 *
 * This software is distributed under the Apache License, Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 or see the "license.txt" file for
 * more details
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */ 
package com.francetelecom.admindm.api;
import java.io.IOException;
import com.francetelecom.admindm.api.Getter;
import com.francetelecom.admindm.model.Parameter;
import com.francetelecom.admindm.soap.Fault;
/**
 * The Class GetterSystem.
 */
public final class GetterSystem implements Getter {
    /**
     * Instantiates a new getter system.
     * @param pKey the key
     * @param pCmd the cmd
     * @param pType the type
     */
    public GetterSystem(final String pKey, final String pCmd, final int pType) {
        this.cmd = pCmd;
        this.type = pType;
        this.key = pKey;
    }
    /** The cmd. */
    private String cmd;
    /**
     * Sets the cmd.
     * @param pCmd the new cmd
     */
    public void setCmd(final String pCmd) {
        this.cmd = pCmd;
    }
    /** The type. */
    private int type;
    /** The key. */
    private final String key;
    /** The obj. */
    private Object obj = null;
    /** The result. */
    private final StringBuffer result = new StringBuffer();
    /**
     * Gets the.
     * @param sessionId the session id
     * @return the object
     * @see com.francetelecom.admindm.api.Getter#get(java.lang.String)
     */
    public Object get(final String sessionId) {
        String[] args = { "/bin/sh", "-c", cmd };
        Process p;
        try {
            p = Runtime.getRuntime().exec(args);
            ThreadProcessReader t;
            ThreadProcessReader t2;
            t = new ThreadProcessReader(p.getInputStream());
            t2 = new ThreadProcessReader(p.getErrorStream());
            t.start();
            t2.start();
            t.join();
            t2.join();
            if (!"".equals(t.result.toString())) {
                result.append(t.result);
            } else if (!"".equals(t2.result.toString())) {
                result.append(t2.result);
            }
            obj = Parameter.getValue(key, result.toString(), type);
        } catch (IOException e1) {
            p = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Fault e) {
            obj = null;
        }
        return obj;
    }
}
