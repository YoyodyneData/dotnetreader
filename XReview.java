/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handyXMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;

import javax.swing.tree.TreeNode;

import org.w3c.dom.Notation;
import org.w3c.dom.Attr;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

/**
 *
 * @author kkell
 */
public class XReview {
    private String sErrorMsg;
    private String strSqlCommand;
    private Document xOutput;
    private String srcFilename;
    private boolean bGetFields=true;
    private boolean bGetQry=true;
    
    public XReview() {
        // constructor 
    }
    
    public String getMsg() {
        return sErrorMsg;
    }
    
    public boolean loadFields() {
        return bGetFields;
    }
    
    public void loadFields(boolean bVal) {
        bGetFields = bVal;
    }
    
    public boolean loadQry() {
        return bGetQry;
    }
    
    public void loadQry(boolean bVal){
        bGetQry = bVal;
    }
    
            
    
    public String sourceSQL() {
        return strSqlCommand;
    }
    
    public void setOutput(Document xVal) {
        xOutput = xVal;
    }
    
    public Document getOutput() {
        return xOutput;
    }
    
    public void setSourceFilename(String sVal) {
        srcFilename = sVal;
    }
    
    
    public Node addFields(String SqlStatement) {
        // add the fields from the SQL Statement
        Node nFields = xOutput.createElement("fld");
        
        int iPtr = 0;
        
        do {
            iPtr = SqlStatement.indexOf("[");
            if (iPtr>0) {
                int iEndPtr = SqlStatement.indexOf("]");
                        Node nField = xOutput.createElement("fldname");
                nField.setTextContent(SqlStatement.substring(iPtr+1, iEndPtr));
               
                nFields.appendChild(nField);
                SqlStatement = SqlStatement.substring(iEndPtr+1);
            }
        } while (iPtr>0);
        
        
        return nFields;
    }
    
    public boolean removeElement(Document xDoc, String sRemoveNode) { 
        // remove the node that matches sRemvoeNode
        boolean bReturn = false;
        
        Node nDocNode = xDoc.getDocumentElement();
        
        Node nToRemove = findFirstNode(nDocNode, sRemoveNode);
        
        if (nToRemove == null) {
            // do nothing 
        } else {
            nDocNode.removeChild(nToRemove);
            bReturn = true;
        }
        
        return bReturn;
    }
   
    
    public boolean updateElement(Document xDoc, String sNodeName, Node nNewValue) {
        // update the specified Node in the XDoc with the new Node that is supplied
        boolean bReturn = false;
        
        Node nDocNode = xDoc.getDocumentElement();
        
        Node nToChange = findFirstNode(nDocNode, sNodeName);
        
        if (nToChange == null) {
            // nothing to do here
        } else {
        
            nDocNode.replaceChild(nNewValue, nToChange);
            bReturn = true;
        }
        
        return bReturn;
    }
    
   
    public Node findFirstNode(Node nRoot, String sNodeToFind) {
        // same as FindNode but this function will exit and return as soon as it finds the first node with a name matching the text 
        Node nResults = null;
        
        
        if (nRoot.hasChildNodes()) {

            NodeList nlChildren = nRoot.getChildNodes();

            // first we must get the total count of matching nodes so we can initialize the array to return them
            for (int iPtr=0; iPtr<nlChildren.getLength()-1; iPtr++) {
                Node nChild = nlChildren.item(iPtr);
               
                if (nChild.getNodeName().indexOf(sNodeToFind)>0) {
                    nResults = nChild;
                    break;
                }
                
                Node nReturned = findFirstNode(nChild, sNodeToFind);
                if (nReturned==null) {
                } else { 
                    nResults = nChild;
                    break;
                }
            }
            
            
        }
        
        return nResults;
        
    }
    
    
    
    public Node[] findNode(Node nRoot, String sNodeToFind) {
        // look through the rootNode for child nodes matching the sNodeToFind 
        // returnt he results in a Node array
        Node[] nResults = null;
        
        
        if (nRoot.hasChildNodes()) {

            NodeList nlChildren = nRoot.getChildNodes();
            int iMatches = 0;

            // first we must get the total count of matching nodes so we can initialize the array to return them
            for (int iPtr=0; iPtr<nlChildren.getLength()-1; iPtr++) {
                Node nChild = nlChildren.item(iPtr);
               
                if (nChild.getNodeName().indexOf(sNodeToFind)>0) {
                    iMatches++;
                }
                
                Node[] nReturned = findNode(nChild, sNodeToFind);
                if (nReturned.length>0) {
                    iMatches += nReturned.length;
                }
            }
            
            if (iMatches>0) {
                nResults = new Node[iMatches];
                int iNodePtr = 0;
                
                // now get the matching nodes and add them to the nResults Node array
                for (int iPtr=0; iPtr<nlChildren.getLength()-1; iPtr++) {
                    Node nChild = nlChildren.item(iPtr);

                    if (nChild.getNodeName().indexOf(sNodeToFind)>0) {
                        nResults[iNodePtr] = nChild;
                        iNodePtr++;
                    }

                    Node[] nReturned = findNode(nChild, sNodeToFind);
                    if (nReturned.length>0) {
                        for (int iLoop=0; iLoop<nReturned.length-1; iLoop++) {
                            nResults[iNodePtr] = nReturned[iLoop];
                            iNodePtr++;
                        }
                    }
                }
            }
            
        }
        
        return nResults;
        
    }
    
    
    public Node addSQL(String SqlStatement){
        // add the specified SqlStatement as a new node on the document
        
        Node nSQL = xOutput.createElement("sourceSQL");
        
        
        nSQL.setTextContent(SqlStatement);
       
        return nSQL;        
    }
    
     
    public int findSourceNodes(Document xDoc) {
        // get the main XML documentElement and search for source query objects 
        // returns a count of the number of source nodes found 
                
        // create return XML object
        
        Node nDocNode = xDoc.getDocumentElement();
       
        
        
        return searchOLEDB(nDocNode);
        
    }
    
    public int searchOLEDB(Node nDocNode) {
        //get the child nodes of each node in the nodelist 
        //until we get to the component with description OLE DB Source
        // then call the find property method to find the property with name=SqlCommand
        // counts the number of SQL commands found and returns that value 
        
        int iQryCtr=0;
        boolean bDisabled=false; 
        
        // only process enabled executables  
        Node nExecDesc = getNamedAttr(nDocNode, "DTS:Disabled");
        if (nExecDesc!=null) {
            bDisabled = nExecDesc.getNodeValue().equals("True");
          
        }
        
        if (!bDisabled) {
        Node nAttr = getNamedAttr(nDocNode, "description");
        
        if (nAttr!=null)  {
            if (nAttr.getNodeValue().equals("OLE DB Source"))        
            {            
                // loop through child nodes to find SqlCommand
                if(searchSQLCmd(nDocNode)) iQryCtr++; 
                //bReturn = findProperty(nDocNode);
            }
        } 
        
        
        // check child nodes for more queries 
            NodeList nlChildren = nDocNode.getChildNodes();
        
            for (int iPtr=0; iPtr<nlChildren.getLength()-1; iPtr++) {
                iQryCtr = iQryCtr + searchOLEDB(nlChildren.item(iPtr));
                                
            }    
        }
        
        return iQryCtr; 
    }
    
   
    public boolean searchSQLCmd(Node nDocNode) {
        //get the child nodes named properties/property
        //until we get to the property with the name='SqlCommand'
        
        boolean bReturn = false;
        
        Node nAttr = getNamedAttr(nDocNode, "name");
        if (nAttr!=null) {
            System.out.println(nAttr.getNodeValue());
            System.out.println(nAttr.getNodeName());
        if (nAttr.getNodeValue().equals("SqlCommand"))
        {            
            // retrieve value from this attribute 
            
            System.out.println(nDocNode.getTextContent());
            strSqlCommand = nDocNode.getTextContent();
            
            Node nRoot = xOutput.getDocumentElement();
            
            Node nChild;
            
            if (bGetQry) {
                nChild = xOutput.createElement("ISPackage");
        
                Attr attFN = xOutput.createAttribute("filename");
                attFN.setValue(srcFilename);
                NamedNodeMap sqlAttr = nChild.getAttributes();
                sqlAttr.setNamedItem(attFN);
            
                nChild.appendChild(addSQL(strSqlCommand));
                nChild.appendChild(addFields(strSqlCommand));
                
             

            } else {
                                
                 nChild = addFields(strSqlCommand);
            }
            
            
            nRoot.appendChild(nChild);
            
            
            bReturn = true;
        }
        } 
        
        if (!bReturn) {
        
        
            NodeList nlChildren = nDocNode.getChildNodes();
        
            for (int iPtr=0; iPtr<nlChildren.getLength()-1; iPtr++) {
                if (searchSQLCmd(nlChildren.item(iPtr))) {
                    bReturn = true;
                    break;
                }                
            }
                                    
        }
        
               
        return bReturn;
    }
    
    
    
    
        public TreeNode loadXMLView(Document xmlDoc) {
            // load the contents of the XML document into a TreeNode
            // return the TreeNode so it can be displayed on a JTree 
            TreeNode nRoot = null;
            
            try {
                Node nDocNode = xmlDoc.getDocumentElement();
                
                nRoot = (TreeNode)loadNode(nDocNode);
                    
                
            } catch (Exception e) {
                sErrorMsg = e.getMessage();
                
            }
            
            return nRoot;
        }
        
        public DefaultMutableTreeNode loadNode(Node sourceNode) {
            // get the name and id attribute of the sourceNode. 
            // create Node and assign name and ID to it 
            // add child nodes 
            // return Node 
            
            String sNodeValue = sourceNode.getNodeName();
            
            //String sIdValue = getIDAttr(sourceNode);
            String sIdValue = getAttr(sourceNode);
            
            // test if first child node is the text value 
           NodeList nlSiblings = sourceNode.getChildNodes();
           if (nlSiblings.getLength()>0) {
           
           if (nlSiblings.item(0).getNodeName().equals("#text")) 
               sNodeValue += " " + nlSiblings.item(0).getNodeValue();
           }
           
            if (sIdValue!=null) sNodeValue += ":" + sIdValue;
            
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(sNodeValue);
            
            // add child nodes 
            for (int iLoop=0; iLoop<nlSiblings.getLength(); iLoop++) {
                
                   if (!nlSiblings.item(iLoop).getNodeName().equals ("#text")) {   
                       newNode.add(loadNode(nlSiblings.item(iLoop)));                
                   }
                   
            }
           
           
            
            return newNode;
        }
        
        public String getIDAttr(Node sourceNode) {
            // find an attribute node for the idNode labelled id 
            // return the value of the node 
            // check for attribute named id
            String sIdValue = null;
            
            if (sourceNode.hasAttributes()) {
                NamedNodeMap attr = sourceNode.getAttributes();
                
                for (int i=0; i<=attr.getLength()-1; i++) {
                    Attr node = (Attr)attr.item(i);
                    
                    if (node.getNodeName().equals("id")) {
                                             
                        sIdValue = node.getNodeValue();
                        break;
                    }
                }
            }
            
            return sIdValue;
            
        }
        
        public String getAttr(Node sourceNode) {
            // get all attributes of the current node and return them in a string 
            String sAttr = null;
            
            if (sourceNode.hasAttributes()) {
                NamedNodeMap attr = sourceNode.getAttributes();
                
                for (int i=0; i<=attr.getLength()-1; i++) {
                    Attr atNode = (Attr)attr.item(i);
                    
                    if (sAttr==null)
                        sAttr = " @" + atNode.getNodeName() + "=" + atNode.getNodeValue();
                    else 
                        sAttr += " @" + atNode.getNodeName() + "=" + atNode.getNodeValue();
                    
                }
            }
            
            return sAttr;
        }

        public Node getNamedAttr(Node sourceNode, String sAttrName) {
            // find an attribute node where the attributes value 
            // matches the sAttrName strings
            
            Node namedAttr = null;
            
            if (sourceNode.hasAttributes()) {
                NamedNodeMap attr = sourceNode.getAttributes();
                for (int i=0; i<attr.getLength(); i++) {
                                        
                    Attr node = (Attr)attr.item(i);                    
                    System.out.println(node.getNodeName());
                    
                    if (node.getNodeName().equals(sAttrName)) {
                        
                        namedAttr = node;
                        break;
                    }
                }
            }
            
            return namedAttr;
            
        }
    
}
