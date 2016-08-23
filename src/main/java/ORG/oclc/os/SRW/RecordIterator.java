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
 * RecordIterator.java
 *
 * Created on November 1, 2005, 8:40 AM
 */

package ORG.oclc.os.SRW;

import java.util.Iterator;

/**
 *
 * @author levan
 */
public interface RecordIterator extends Iterator {
    public void close();
    public Record nextRecord() throws SRWDiagnostic;
}
