/*--------------------------------------------------------
 * Product Name : modus TR-069
 * Version : 1.1
 * Module Name : DownloadBundle
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
 * Author : Orange Labs R&D O.Beyler
 */
package com.francetelecom.admindm.download;
import org.kxml2.kdom.Element;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.francetelecom.admindm.api.RPCDecoder;
import com.francetelecom.admindm.api.RPCMethod;
import com.francetelecom.admindm.api.XMLUtil;
import com.francetelecom.admindm.download.api.Download;
import com.francetelecom.admindm.download.api.IEngine;
import com.francetelecom.admindm.soap.Fault;
import com.francetelecom.admindm.soap.FaultUtil;
/**
 * The Class DownloadDecoder.
 */
@Component(properties="name=Download")
public class DownloadDecoder implements RPCDecoder {
    /** The engine. */
    private IEngine engine;

    @Reference
    public void setEngine(IEngine engine) {
		this.engine = engine;
	}
    
    /**
     * Decode.
     * @param element the element
     * @return the RPC method
     * @throws Fault the decoder exception
     */
    public final RPCMethod decode(final Element element) throws Fault {
        Download result = new Download(engine);
        result.setCommandKey(XMLUtil.extractValue(element, "CommandKey"));
        result.setFileType(XMLUtil.extractValue(element, "FileType"));
        result.setUrl(XMLUtil.extractValue(element, "URL"));
        if ("".equals(result.getUrl())) {
            StringBuffer error = new StringBuffer(FaultUtil.STR_FAULT_9007);
            error.append(" : url field is empty.");
            throw new Fault(FaultUtil.FAULT_9007, error.toString());
        }
        result.setUsername(XMLUtil.extractValue(element, "Username"));
        result.setPassword(XMLUtil.extractValue(element, "Password"));
        String value = XMLUtil.extractValue(element, "FileSize");
        if ("".equals(value)) {
            result.setFileSize(0);
        } else {
            try {
                result.setFileSize(Integer.parseInt(value));
                if (result.getFileSize()<0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                StringBuffer error = new StringBuffer(FaultUtil.STR_FAULT_9007);
                error.append(" :'");
                error.append(value);
                error.append("' is not an Unsigned Integer");
                throw new Fault(FaultUtil.FAULT_9007, error.toString(), e);
            }
        }
        result.setTargetFileName(XMLUtil.extractValue(element,
                "TargetFileName"));
        value = XMLUtil.extractValue(element, "DelaySeconds");
        if ("".equals(value)) {
            result.setDelaySeconds(0);
        } else {
            try {
                result.setDelaySeconds(Integer.parseInt(value));
                if (result.getDelaySeconds()<0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                StringBuffer error = new StringBuffer(FaultUtil.STR_FAULT_9007);
                error.append(" :'");
                error.append(value);
                error.append("' is not an unsigned integer.");
                error.append("Unable to set delay.");
                throw new Fault(FaultUtil.FAULT_9007, error.toString(), e);
            }
        }
        result.setSuccessURL(XMLUtil.extractValue(element, "SuccessURL"));
        result.setFailureURL(XMLUtil.extractValue(element, "FailureURL"));
        result.setTargetFileName(extractFilename(result));
        return result;
    }
    /**
     * Extract filename from Download information.
     * @param download the download
     * @return the string
     */
    protected static String extractFilename(final Download download) {
        String result = download.getTargetFileName();
        if ("".equals(download.getTargetFileName())) {
            String urlName = download.getUrl();
            int index = urlName.lastIndexOf('/') + 1;
            if (index != -1) {
                result = urlName.substring(index, urlName.length());
            } else {
                result = urlName;
            }
        }
        return result;
    }
}
