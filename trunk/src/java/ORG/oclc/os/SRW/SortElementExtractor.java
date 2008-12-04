/*
   Copyright 2006 OCLC Online Computer Library Center, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
/*
 * SortElementExtractor.java
 *
 * Created on October 17, 2005, 3:47 PM
 */

package ORG.oclc.os.SRW;

/**
 *
 * @author levan
 */
public interface SortElementExtractor {
    public void init(String xpath, String prefix, String schema) throws SortElementExtractorException;
    public String extract(Object record) throws SortElementExtractorException;
}
