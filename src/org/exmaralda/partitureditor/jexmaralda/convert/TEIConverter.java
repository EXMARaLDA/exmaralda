/*
 * TEIConverter.java
 *
 * Created on 12. August 2004, 17:24
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
import java.util.HashSet;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;

import org.jdom.JDOMException;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.parsers.ParserConfigurationException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.common.corpusbuild.TEIMerger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

/**
 *
 * @author  thomas
 */
public class TEIConverter {
    
    static String STYLESHEET_PATH = "/org/exmaralda/partitureditor/jexmaralda/xsl/EXMARaLDA2TEI.xsl";

    public static final int GENERIC_METHOD = 0;
    public static final int AZM_METHOD = 1;
    public static final int MODENA_METHOD = 2;
    public static final int HIAT_METHOD = 3;
    public static final int CGAT_METHOD = 4;
    public static final int HIAT_NEW_METHOD = 5;
    public static final int ISO_GENERIC_METHOD = 6;
    public static final int HIAT_ISO_METHOD = 7;
    public static final int CGAT_MINIMAL_ISO_METHOD = 8;
    public static final int ISO_EVENT_TOKEN_METHOD = 9;

    
    String language = "en";
    
    /** Creates a new instance of TEIConverter */
    public TEIConverter() {
    }
    
    /** Creates a new instance of TEIConverter */
    public TEIConverter(String ss) {
        STYLESHEET_PATH = ss;
    }
    
    public void setLanguage(String l){
        language = l;
        System.out.println("Language of converter set to " + language);
    }

    public BasicTranscription readTEIFromFile(String path) throws SAXException, JDOMException, IOException { 
        Document doc = IOUtilities.readDocumentFromLocalFile(path);
        if ("TEI.2".equals(doc.getRootElement().getName())){
            // i.e. this must be a TEI document like the one defined in the AzM paper
            TEISaxReader reader = new TEISaxReader();
            return reader.readFromFile(path);
        }
        StylesheetFactory ssf = new StylesheetFactory(true);
        try {
            String result = ssf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/tei2exmaralda.xsl", IOUtilities.documentToString(doc));
            BasicTranscription bt = new BasicTranscription();
            bt.BasicTranscriptionFromString(result);
            return bt;
        } catch (JexmaraldaException ex) {
            throw new IOException(ex);
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        } catch (TransformerConfigurationException ex) {
            throw new IOException(ex);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }
    
    @Deprecated
    public String BasicTranscriptionToTEI(BasicTranscription bt) throws SAXException,
                                                                        IOException,
                                                                        ParserConfigurationException,
                                                                        TransformerConfigurationException,
                                                                        TransformerException,
                                                                        JDOMException {
        return BasicTranscriptionToTEI(bt, false);
    }

    @Deprecated
    public String BasicTranscriptionToTEI(BasicTranscription bt, boolean callBracketReplacer) throws SAXException,
                                                                                                     IOException,
                                                                                                     ParserConfigurationException,
                                                                                                     TransformerConfigurationException,
                                                                                                     TransformerException,
                                                                                                     JDOMException {
        SegmentedTranscription st = bt.toSegmentedTranscription();
        ListTranscription lt = st.toListTranscription(new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION));
        // CHANGED FOR VERSION 1.3.3, 20-10-2006
        lt.getBody().sort();
        // CHANGED 30-08-2010: use XSLT2
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString(STYLESHEET_PATH, lt.toXML());
        if (callBracketReplacer){
            return replaceBrackets(result);
        }
        return result;
    }
    
    @Deprecated
    public void writeTEIToFile(BasicTranscription bt, String path, int method) throws SAXException, 
                                                                                      IOException, 
                                                                                      ParserConfigurationException, 
                                                                                      TransformerConfigurationException, 
                                                                                      TransformerException, 
                                                                                      JDOMException, 
                                                                                      FSMException, 
                                                                                      XSLTransformException, 
                                                                                      Exception{
        switch (method){
            case GENERIC_METHOD :
                writeGenericTEIToFile(bt,path);
            case AZM_METHOD :
                writeTEIToFile(bt, path);
                break;
            case MODENA_METHOD :
                writeModenaTEIToFile(bt, path);
                break;
            case HIAT_METHOD :
                writeHIATTEIToFile(bt, path);
                break;
        }
    }


    
    public void writeGenericISOTEIToFile(BasicTranscription bt, String path) throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/exmaralda2isotei.xsl", bt.toXML());
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        setDocLanguage(teiDoc, language);        
        IOUtilities.writeDocumentToLocalFile(path, teiDoc);
    }
    
    public void writeEventTokenISOTEIToFile(BasicTranscription bt, String path)  throws JDOMException, IOException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/exmaralda2isotei_eventToken.xsl", bt.toXML());
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        setDocLanguage(teiDoc, language);        
        IOUtilities.writeDocumentToLocalFile(path, teiDoc);
    }
    

    public void writeHIATISOTEIToFile(BasicTranscription bt, String filename) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        writeHIATISOTEIToFile(bt, filename, false);
    }
    
    public void writeHIATISOTEIToFile(BasicTranscription bt, 
                                      String filename,
                                      boolean includeFullText) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();        
        System.out.println("started writing document...");
        HIATSegmentation segmentation = new HIATSegmentation();
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        System.out.println("Segmented transcription created");
        String nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        TEIMerger teiMerger = new TEIMerger(true);
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, 
                nameOfDeepSegmentation, 
                "SpeakerContribution_Event", 
                true,
                includeFullText);
        System.out.println("Merged");
        generateWordIDs(teiDoc);
        setDocLanguage(teiDoc, language);
        FileIO.writeDocumentToLocalFile(filename, teiDoc);
        System.out.println("document written.");        
    }
    
    public void writeCGATMINIMALISOTEIToFile(BasicTranscription bt, String filename, String customFSM) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();        
        System.out.println("started writing document...");
        cGATMinimalSegmentation segmentation;
        if (customFSM==null || customFSM.length()==0){
            segmentation = new cGATMinimalSegmentation();
        } else {
            segmentation = new cGATMinimalSegmentation(customFSM);
        }
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        System.out.println("Segmented transcription created");
        String nameOfDeepSegmentation = "SpeakerContribution_Word";
        TEIMerger teiMerger = new TEIMerger(true);
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event", true);
        System.out.println("Merged");
        generateWordIDs(teiDoc);
        setDocLanguage(teiDoc, language);        
        FileIO.writeDocumentToLocalFile(filename, teiDoc);
        System.out.println("document written.");        
    }

    public void writeFOLKERISOTEIToFile(BasicTranscription bt, String path) throws SAXException, 
                                                                                ParserConfigurationException, 
                                                                                IOException, 
                                                                                TransformerConfigurationException, 
                                                                                TransformerException, 
                                                                                JDOMException{
        EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
        elt.updateContributions();
        Element e = elt.toJDOMElement(new File(path));
        Document doc = new Document(e);
        GATParser parser = new GATParser(org.exmaralda.folker.utilities.Constants.getAlphabetLanguage());
        for (int level=1; level<=2; level++){
            parser.parseDocument(doc, level);
        }
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/folker2isotei.xsl", IOUtilities.documentToString(doc));
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        generateWordIDs(teiDoc);
        setDocLanguage(teiDoc, language);                
        IOUtilities.writeDocumentToLocalFile(path, teiDoc);                
    }
    
    public void writeFOLKERISOTEIToFile(Document flnDoc, String absolutePath) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException, JDOMException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/folker2isotei.xsl", IOUtilities.documentToString(flnDoc));
        Document teiDoc = IOUtilities.readDocumentFromString(result);
        generateWordIDs(teiDoc);
        setDocLanguage(teiDoc, language);                
        IOUtilities.writeDocumentToLocalFile(absolutePath, teiDoc);                
    }
    
    
    
    //////////////////////////////////////////////////////////////////
    //////////////// OLDER METHODS, NOT NEEDED SINCE ISO /////////////
    //////////////////////////////////////////////////////////////////
    
    /** transforms the basic transcription and writes a TEI file to path
     * according to the method described in Schmidt 2005 */
    @Deprecated
    public void writeTEIToFile(BasicTranscription bt, String path) throws SAXException,
                                                                          IOException, 
                                                                          ParserConfigurationException, 
                                                                          TransformerConfigurationException, 
                                                                          TransformerException,
                                                                          JDOMException {
        String toBeWritten = BasicTranscriptionToTEI(bt);
        System.out.println("started writing document...");
        java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File(path));
        fos.write(toBeWritten.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");        
    }

    /** transforms the basic transcription and writes a TEI file to path
     * according to the method described by Natacha Niemants (Modena) */
    @Deprecated
    public void writeModenaTEIToFile(BasicTranscription bt, String path) throws SAXException,
                                                                                IOException,
                                                                                ParserConfigurationException,
                                                                                TransformerConfigurationException,
                                                                                TransformerException,
                                                                                JDOMException {
        String toBeWritten = BasicTranscriptionToTEI(bt, true);
        System.out.println("started writing document...");
        java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File(path));
        fos.write(toBeWritten.getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
    }

    @Deprecated
    public void writeHIATTEIToFile(BasicTranscription bt, String path) throws SAXException, 
                                                                              FSMException, 
                                                                              XSLTransformException, 
                                                                              JDOMException, 
                                                                              IOException, 
                                                                              Exception {
        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();

        System.out.println("started writing document...");
        HIATSegmentation segmentation = new HIATSegmentation();
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        String nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        TEIMerger teiMerger = new TEIMerger();
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event");
        FileIO.writeDocumentToLocalFile(path, teiDoc);
        System.out.println("document written.");                
    }

    @Deprecated
    public void writeNewHIATTEIToFile(BasicTranscription bt, String filename) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {
        writeNewHIATTEIToFile(bt, filename, true);
    }

    @Deprecated
    public void writeNewHIATTEIToFile(BasicTranscription bt, String filename, boolean generateWordIDs) throws SAXException,
                                                                              FSMException,
                                                                              XSLTransformException,
                                                                              JDOMException,
                                                                              IOException,
                                                                              Exception {

        // added 13-12-2013
        BasicTranscription copyBT = bt.makeCopy();
        copyBT.normalize();
        
        System.out.println("started writing document...");
        HIATSegmentation segmentation = new HIATSegmentation();
        SegmentedTranscription st = segmentation.BasicToSegmented(copyBT);
        String nameOfDeepSegmentation = "SpeakerContribution_Utterance_Word";
        TEIMerger teiMerger = new TEIMerger();
        Document stdoc = FileIO.readDocumentFromString(st.toXML());
        Document teiDoc = teiMerger.SegmentedTranscriptionToTEITranscription(stdoc, nameOfDeepSegmentation, "SpeakerContribution_Event", true);

        /*StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/transformhiattei.xsl", IOUtilities.documentToString(teiDoc));
        Document transformedTEIDoc = IOUtilities.readDocumentFromString(result);
        FileIO.writeDocumentToLocalFile(filename, transformedTEIDoc);*/
        if (generateWordIDs){
            generateWordIDs(teiDoc);
        }
        FileIO.writeDocumentToLocalFile(filename, teiDoc);
        System.out.println("document written.");

    }

    
    @Deprecated
    public void writeFOLKERTEIToFile(BasicTranscription bt, String path) throws SAXException, 
                                                                                ParserConfigurationException, 
                                                                                IOException, 
                                                                                TransformerConfigurationException, 
                                                                                TransformerException, 
                                                                                JDOMException{
        EventListTranscription elt = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
        elt.updateContributions();
        Element e = elt.toJDOMElement(new File(path));
        Document doc = new Document(e);
        GATParser parser = new GATParser(org.exmaralda.folker.utilities.Constants.getAlphabetLanguage());
        for (int level=1; level<=2; level++){
            parser.parseDocument(doc, level);
        }
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/folker2tei.xsl", IOUtilities.documentToString(doc));
        Document d = IOUtilities.readDocumentFromString(result);
        generateWordIDs(d);
        IOUtilities.writeDocumentToLocalFile(path, d);                
    }

    @Deprecated
    public void writeGenericTEIToFile(BasicTranscription bt, String path) throws SAXException,
                                                                                ParserConfigurationException,
                                                                                JDOMException,
                                                                                IOException,
                                                                                TransformerConfigurationException,
                                                                                TransformerException {
        StylesheetFactory sf = new StylesheetFactory(true);
        String result = sf.applyInternalStylesheetToString("/org/exmaralda/tei/xml/exmaralda2tei.xsl", bt.toXML());
        Document d = IOUtilities.readDocumentFromString(result);
        IOUtilities.writeDocumentToLocalFile(path, d);
    }



    //****************************************************
    //********* private processing methods ***************
    //****************************************************

    /*private void generateWordIDs(Document document) throws JDOMException{
        XPath wordXPath = XPath.newInstance("//tei:w"); 
        wordXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        
        List words = wordXPath.selectNodes(document);
        int count=0;
        for (Object o : words){
            count++;
            Element word = (Element)o;
            String wordID = "w" + Integer.toString(count);
            word.setAttribute("id", wordID, Namespace.XML_NAMESPACE);
        }
        
        XPath pcXPath = XPath.newInstance("//tei:pc"); 
        pcXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        
        List pcs = pcXPath.selectNodes(document);
        count=0;
        for (Object o : pcs){
            count++;
            Element pc = (Element)o;
            String pcID = "pc" + Integer.toString(count);
            pc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }
        
    } */

    HashSet<String> allExistingIDs = new HashSet<String>();
    
    // new 30-03-2016
    private void generateWordIDs(Document document) throws JDOMException{
        // added 30-03-2016
        XPath idXPath = XPath.newInstance("//tei:*[@xml:id]"); 
        idXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        idXPath.addNamespace(Namespace.XML_NAMESPACE);
        List idElements = idXPath.selectNodes(document);
        for (Object o : idElements){
            Element e = (Element)o;
            allExistingIDs.add(e.getAttributeValue("id", Namespace.XML_NAMESPACE));
        }
        
        
        // changed 30-03-2016
        XPath wordXPath = XPath.newInstance("//tei:w[not(@xml:id)]"); 
        wordXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        wordXPath.addNamespace(Namespace.XML_NAMESPACE);
        
        List words = wordXPath.selectNodes(document);
        int count=1;
        for (Object o : words){
            Element word = (Element)o;
            while(allExistingIDs.contains("w" + Integer.toString(count))){
                count++;
            }

            String wordID = "w" + Integer.toString(count);
            allExistingIDs.add(wordID);
            //System.out.println("*** " + wordID);
            word.setAttribute("id", wordID, Namespace.XML_NAMESPACE);
        }
        
        
        // new 02-12-2014
        XPath pcXPath = XPath.newInstance("//tei:pc[not(@xml:id)]"); 
        pcXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        pcXPath.addNamespace(Namespace.XML_NAMESPACE);
        
        List pcs = pcXPath.selectNodes(document);
        count=1;
        for (Object o : pcs){
            Element pc = (Element)o;
            while(allExistingIDs.contains("pc" + Integer.toString(count))){
                count++;
            }
            
            String pcID = "pc" + Integer.toString(count);
            allExistingIDs.add(pcID);
            //System.out.println("*** " + wordID);
            pc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }

        // new 19-11-2019
        XPath vocXPath = XPath.newInstance("//tei:vocal[not(@xml:id)]"); 
        vocXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        vocXPath.addNamespace(Namespace.XML_NAMESPACE);        
        List vocs = vocXPath.selectNodes(document);
        count=1;
        for (Object o : vocs){
            Element voc = (Element)o;
            while(allExistingIDs.contains("v" + Integer.toString(count))){
                count++;
            }
            
            String pcID = "v" + Integer.toString(count);
            allExistingIDs.add(pcID);
            //System.out.println("*** " + wordID);
            voc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }

        // new 19-11-2019
        XPath incXPath = XPath.newInstance("//tei:incident[not(@xml:id)]"); 
        incXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        incXPath.addNamespace(Namespace.XML_NAMESPACE);        
        List incs = incXPath.selectNodes(document);
        count=1;
        for (Object o : incs){
            Element inc = (Element)o;
            while(allExistingIDs.contains("i" + Integer.toString(count))){
                count++;
            }
            
            String pcID = "i" + Integer.toString(count);
            allExistingIDs.add(pcID);
            //System.out.println("*** " + wordID);
            inc.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }

        // new 19-11-2019
        XPath pauseXPath = XPath.newInstance("//tei:pause[not(@xml:id)]"); 
        pauseXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        pauseXPath.addNamespace(Namespace.XML_NAMESPACE);        
        List pauses = pauseXPath.selectNodes(document);
        count=1;
        for (Object o : pauses){
            Element pause = (Element)o;
            while(allExistingIDs.contains("p" + Integer.toString(count))){
                count++;
            }
            
            String pcID = "p" + Integer.toString(count);
            allExistingIDs.add(pcID);
            //System.out.println("*** " + wordID);
            pause.setAttribute("id", pcID, Namespace.XML_NAMESPACE);
        }
        
    }
    
    
    
    private Element makeNonphoElement(String matchedText) {
        Element returnValue = new Element("x");
        String content = matchedText.substring(1,matchedText.length()-1);
        if (matchedText.startsWith("[")){
            returnValue.setName("vocal");
            returnValue.setAttribute("desc", content);
        } else if (matchedText.startsWith("{")){
            returnValue.setName("event");
            returnValue.setAttribute("desc", content);
        } else if (matchedText.startsWith("(")){
            returnValue.setName("kinesic");
            returnValue.setAttribute("desc", content);
        } else if (matchedText.startsWith("<")){
            returnValue.setName("pause");
            if (content.startsWith("dur=")){
               returnValue.setAttribute("dur" , content.substring(content.indexOf("=")+1));
            } else {
                returnValue.setAttribute("dur", content);
            }
        }
        return returnValue;
    }

    private String replaceBrackets(String input) throws JDOMException, IOException {
        Document doc = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString(input);

        String regex_no_bracket = "[^\\(\\)\\[\\]\\{\\}\\<\\>]+";
        String regex_round = "\\(" + regex_no_bracket + "\\)";
        String regex_square = "\\[" + regex_no_bracket + "\\]";
        String regex_curly = "\\{" + regex_no_bracket + "\\}";
        String regex_angle = "\\<" + regex_no_bracket + "\\>";
        String regex = "(" + regex_round + "|" + regex_square + "|" + regex_curly + "|" + regex_angle +  ")";
        Pattern pattern = Pattern.compile(regex);

        Vector<Element> allSegments = new Vector<Element>();
        Iterator i = doc.getDescendants(new ElementFilter("seg"));
        while (i.hasNext()){
            Element seg = (Element)(i.next());
            allSegments.add(seg);
        }
        for (Element seg : allSegments){
            if (!(seg.getAttributeValue("type").equals("segmental"))) continue;
            Vector<Content> newContent = new Vector<Content>();
            for (Object o : seg.getContent()){
                Content c = (Content)o;
                if (!(c instanceof Text)) {
                    newContent.add(c);
                    continue;
                }
                Text t = (Text)c;
                String originalText = t.getText();
                //System.out.println("==>" + originalText);
                Matcher m = pattern.matcher(originalText);
                int lastStart = 0;
                
                while (m.find()){
                    int s = m.start();
                    int e = m.end();
                    String matchedText = originalText.substring(s, e);
                    //System.out.println("**** " + s + " / " + e + " / " + matchedText);
                    if (lastStart<s){
                        Text text = new Text(originalText.substring(lastStart, s));
                        newContent.add(text);
                    }
                    Element nonpho = makeNonphoElement(matchedText);
                    newContent.add(nonpho);
                    //lastStart = e+1;
                    lastStart = e;
                }
                if (lastStart<originalText.length()-1){
                    Text text = new Text(originalText.substring(lastStart));
                    newContent.add(text);
                }
            }
            seg.removeContent();
            seg.setContent(newContent);

        }

        return org.exmaralda.common.jdomutilities.IOUtilities.documentToString(doc,false);
    }

    private void setDocLanguage(Document teiDoc, String language) throws JDOMException {
        // /TEI/text[1]/@*[namespace-uri()='http://www.w3.org/XML/1998/namespace' and local-name()='lang']
        XPath xpathToLangAttribute = XPath.newInstance("//tei:text/@xml:lang");
        xpathToLangAttribute.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        xpathToLangAttribute.addNamespace(Namespace.XML_NAMESPACE);
        Attribute langAtt = (Attribute) xpathToLangAttribute.selectSingleNode(teiDoc);
        if (langAtt!=null){
            langAtt.setValue(language);
        } else {
            XPath xpathToTextElement = XPath.newInstance("//tei:text");
            xpathToTextElement.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
            xpathToTextElement.addNamespace(Namespace.XML_NAMESPACE);
            Element textEl = (Element) xpathToTextElement.selectSingleNode(teiDoc);
            textEl.setAttribute("lang", language, Namespace.XML_NAMESPACE);
        }
        System.out.println("Language of document set to " + language);
        
    }





    
}
