/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ExtraDataType;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ralph
 */
class OpenSearchRecordIterator implements RecordIterator {
    private final ListIterator<Node> items;

    public OpenSearchRecordIterator(long index, int numRecs, String schemaId,
            ExtraDataType edt, Element channel) {
        items = asList(channel.getElementsByTagNameNS("*", "item")).listIterator();
    }

    @Override
    public void close() {
    }

    @Override
    public Record nextRecord() throws SRWDiagnostic {
        Record record = new Record("", "rss");
        Element item=(Element) items.next();
        record.setIdentifier(item.getElementsByTagName("guid").item(0).getTextContent());
        return record;
    }

    @Override
    public boolean hasNext() {
        return items.hasNext();
    }

    @Override
    public Object next() {
        try {
            return nextRecord();
        } catch (SRWDiagnostic ex) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static List<Node> asList(NodeList n) {
        return n.getLength() == 0
                ? Collections.<Node>emptyList() : new NodeListWrapper(n);
    }

    static final class NodeListWrapper extends AbstractList<Node>
            implements RandomAccess {

        private final NodeList list;

        NodeListWrapper(NodeList l) {
            list = l;
        }

        @Override
        public Node get(int index) {
            return list.item(index);
        }

        @Override
        public int size() {
            return list.getLength();
        }
    }
}
