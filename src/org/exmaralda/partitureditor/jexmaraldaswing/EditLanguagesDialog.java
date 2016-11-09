/*
 * EditLanguagesDialog.java
 *
 * Created on 7. August 2001, 09:31
 */

package org.exmaralda.partitureditor.jexmaraldaswing;

import org.exmaralda.common.helpers.Internationalizer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.xml.sax.*;
import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  

/**
 *
 * @author  Thomas
 * @version 
 */
public class EditLanguagesDialog extends JEscapeDialog {

    private Vector allLanguages;
    private Languages lUsed;
    private Languages l1;
    private Languages l2;
    private javax.swing.DefaultListModel luListModel;
    private javax.swing.DefaultListModel l1ListModel;
    private javax.swing.DefaultListModel l2ListModel;
    public boolean change;
    
    
    /** Creates new form EditLanguagesDialog */
    public EditLanguagesDialog(java.awt.Frame parent, boolean modal, Languages lu, Languages lang1, Languages lang2) {
        super (parent, modal);
        try {
            readAllLanguages();
        } catch (SAXException se){
            System.out.println("SAXError reading Languages.xml");
            System.out.println(se.getMessage());
            allLanguages = new Vector();
        }
        lUsed = lu.makeCopy();
        l1 = lang1.makeCopy();
        l2 = lang2.makeCopy();
        luListModel = new javax.swing.DefaultListModel();
        String[] allLu = lUsed.getAllLanguages();
        for (int pos=0; pos<allLu.length; pos++){
            luListModel.addElement(allLu[pos]);
        }
        l1ListModel = new javax.swing.DefaultListModel();
        String[] allL1 = l1.getAllLanguages();
        for (int pos=0; pos<allL1.length; pos++){
            l1ListModel.addElement(allL1[pos]);
        }
        l2ListModel = new javax.swing.DefaultListModel();
        String[] allL2 = l2.getAllLanguages();
        for (int pos=0; pos<allL2.length; pos++){
            l2ListModel.addElement(allL2[pos]);
        }
        initComponents ();
        this.getRootPane().setDefaultButton(okButton);
        pack ();
        Internationalizer.internationalizeDialogToolTips(this);

        this.allLanguagesList.setCellRenderer(new javax.swing.DefaultListCellRenderer(){

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String theValue = (String)value;
                if (theValue.startsWith("---")){
                    theValue = theValue.replaceAll("-", "").trim();
                }
                Component c =  super.getListCellRendererComponent(list, theValue, index, isSelected, cellHasFocus);
                if (((String)(value)).startsWith("---")){
                    c.setBackground(Color.BLUE);
                    c.setForeground(Color.WHITE);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
                return c;
            }

        });

    }

    public void configureForSingleSelection(){
        selectedLanguagesPanel.setVisible(false);
        allLanguagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setTitle("Select Language");
    }
    
    public String getSelectedLanguage(){
        String selectedLanguage = ((String)allLanguagesList.getSelectedValue());
        return selectedLanguage;
        
    }
    
    



    public Languages getLanguagesUsed(){
        return lUsed;
    }
    
    public Languages getL1(){
        return l1;
    }
    
    public Languages getL2(){
        return l2;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        allLanguagesScrollPane = new javax.swing.JScrollPane();
        allLanguagesList = new javax.swing.JList(allLanguages);
        selectedLanguagesPanel = new javax.swing.JPanel();
        luPanel = new javax.swing.JPanel();
        luButtonPanel = new javax.swing.JPanel();
        addLuButton = new javax.swing.JButton();
        addLuButton.setMnemonic('A');
        removeLuButton = new javax.swing.JButton();
        removeLuButton.setMnemonic('R');
        luList = new javax.swing.JList(luListModel);
        l1Panel = new javax.swing.JPanel();
        l1ButtonPanel = new javax.swing.JPanel();
        addL1Button = new javax.swing.JButton();
        addL1Button.setMnemonic('d');
        removeL1Button = new javax.swing.JButton();
        removeL1Button.setMnemonic('e');
        l1List = new javax.swing.JList(l1ListModel);
        l2Panel = new javax.swing.JPanel();
        l2ButtonPanel = new javax.swing.JPanel();
        addL2Button = new javax.swing.JButton();
        addL2Button.setMnemonic('-');
        removeL2Button = new javax.swing.JButton();
        removeL2Button.setMnemonic('m');
        l2List = new javax.swing.JList(l2ListModel);
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        okButton.setMnemonic('O');
        cancelButton = new javax.swing.JButton();
        cancelButton.setMnemonic('C');
        explanationPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setTitle("Edit languages");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        mainPanel.setPreferredSize(new java.awt.Dimension(600, 400));
        mainPanel.setLayout(new java.awt.GridLayout(1, 2));

        allLanguagesScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("All languages"));

        allLanguagesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                allLanguagesListValueChanged(evt);
            }
        });
        allLanguagesScrollPane.setViewportView(allLanguagesList);

        mainPanel.add(allLanguagesScrollPane);

        selectedLanguagesPanel.setLayout(new java.awt.GridLayout(3, 1));

        luPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Language(s) used"));
        luPanel.setLayout(new java.awt.GridLayout(1, 2));

        luButtonPanel.setLayout(new javax.swing.BoxLayout(luButtonPanel, javax.swing.BoxLayout.Y_AXIS));

        addLuButton.setFont(new java.awt.Font("Dialog", 0, 10));
        addLuButton.setText("Add");
        addLuButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addLuButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addLuButton.setPreferredSize(new java.awt.Dimension(80, 25));
        addLuButton.setEnabled(false);
        addLuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLuButtonActionPerformed(evt);
            }
        });
        luButtonPanel.add(addLuButton);

        removeLuButton.setFont(new java.awt.Font("Dialog", 0, 10));
        removeLuButton.setText("Remove");
        removeLuButton.setMaximumSize(new java.awt.Dimension(80, 25));
        removeLuButton.setMinimumSize(new java.awt.Dimension(80, 25));
        removeLuButton.setPreferredSize(new java.awt.Dimension(80, 25));
        removeLuButton.setEnabled(false);
        removeLuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLuButtonActionPerformed(evt);
            }
        });
        luButtonPanel.add(removeLuButton);

        luPanel.add(luButtonPanel);

        luList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                luListValueChanged(evt);
            }
        });
        luPanel.add(luList);

        selectedLanguagesPanel.add(luPanel);

        l1Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("First language(s)"));
        l1Panel.setLayout(new java.awt.GridLayout(1, 2));

        l1ButtonPanel.setLayout(new javax.swing.BoxLayout(l1ButtonPanel, javax.swing.BoxLayout.Y_AXIS));

        addL1Button.setFont(new java.awt.Font("Dialog", 0, 10));
        addL1Button.setText("Add");
        addL1Button.setMaximumSize(new java.awt.Dimension(80, 25));
        addL1Button.setMinimumSize(new java.awt.Dimension(80, 25));
        addL1Button.setPreferredSize(new java.awt.Dimension(80, 25));
        addL1Button.setEnabled(false);
        addL1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addL1ButtonActionPerformed(evt);
            }
        });
        l1ButtonPanel.add(addL1Button);

        removeL1Button.setFont(new java.awt.Font("Dialog", 0, 10));
        removeL1Button.setText("Remove");
        removeL1Button.setMaximumSize(new java.awt.Dimension(80, 25));
        removeL1Button.setMinimumSize(new java.awt.Dimension(80, 25));
        removeL1Button.setPreferredSize(new java.awt.Dimension(80, 25));
        removeL1Button.setEnabled(false);
        removeL1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeL1ButtonActionPerformed(evt);
            }
        });
        l1ButtonPanel.add(removeL1Button);

        l1Panel.add(l1ButtonPanel);

        l1List.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                l1ListValueChanged(evt);
            }
        });
        l1Panel.add(l1List);

        selectedLanguagesPanel.add(l1Panel);

        l2Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Second language(s)"));
        l2Panel.setLayout(new java.awt.GridLayout(1, 2));

        l2ButtonPanel.setLayout(new javax.swing.BoxLayout(l2ButtonPanel, javax.swing.BoxLayout.Y_AXIS));

        addL2Button.setFont(new java.awt.Font("Dialog", 0, 10));
        addL2Button.setText("Add");
        addL2Button.setMaximumSize(new java.awt.Dimension(80, 25));
        addL2Button.setMinimumSize(new java.awt.Dimension(80, 25));
        addL2Button.setPreferredSize(new java.awt.Dimension(80, 25));
        addL2Button.setEnabled(false);
        addL2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addL2ButtonActionPerformed(evt);
            }
        });
        l2ButtonPanel.add(addL2Button);

        removeL2Button.setFont(new java.awt.Font("Dialog", 0, 10));
        removeL2Button.setText("Remove");
        removeL2Button.setMaximumSize(new java.awt.Dimension(80, 25));
        removeL2Button.setMinimumSize(new java.awt.Dimension(80, 25));
        removeL2Button.setPreferredSize(new java.awt.Dimension(80, 25));
        removeL2Button.setEnabled(false);
        removeL2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeL2ButtonActionPerformed(evt);
            }
        });
        l2ButtonPanel.add(removeL2Button);

        l2Panel.add(l2ButtonPanel);

        l2List.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                l2ListValueChanged(evt);
            }
        });
        l2Panel.add(l2List);

        selectedLanguagesPanel.add(l2Panel);

        mainPanel.add(selectedLanguagesPanel);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(90, 27));
        okButton.setMinimumSize(new java.awt.Dimension(90, 27));
        okButton.setPreferredSize(new java.awt.Dimension(90, 27));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.setMaximumSize(new java.awt.Dimension(90, 27));
        cancelButton.setMinimumSize(new java.awt.Dimension(90, 27));
        cancelButton.setPreferredSize(new java.awt.Dimension(90, 27));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText("Language codes defined by ");
        explanationPanel.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel2.setForeground(java.awt.SystemColor.activeCaption);
        jLabel2.setText("http://www.ethnologue.com/");
        jLabel2.setToolTipText("Click to open website in browser");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        explanationPanel.add(jLabel2);

        getContentPane().add(explanationPanel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

  private void cancelButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
// Add your handling code here:
    change = false;    
    setVisible (false);
    dispose ();
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void okButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
// Add your handling code here:
    change = true;
    setVisible (false);
    dispose ();

  }//GEN-LAST:event_okButtonActionPerformed

  private void removeL2ButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeL2ButtonActionPerformed
// Add your handling code here:
    if (l2List.getSelectedIndex()!=-1){
        String remLang = (String)l2List.getSelectedValue();
        l2.removeLanguage(remLang);
        l2ListModel.removeElementAt(l2List.getSelectedIndex());
        l2List.setSelectedIndex(-1);
        removeLuButton.setEnabled(false);
    }    
  }//GEN-LAST:event_removeL2ButtonActionPerformed

  private void removeL1ButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeL1ButtonActionPerformed
// Add your handling code here:
    if (l1List.getSelectedIndex()!=-1){
        String remLang = (String)l1List.getSelectedValue();
        l1.removeLanguage(remLang);
        l1ListModel.removeElementAt(l1List.getSelectedIndex());
        l1List.setSelectedIndex(-1);
        removeL1Button.setEnabled(false);
    }    
  }//GEN-LAST:event_removeL1ButtonActionPerformed

  private void removeLuButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLuButtonActionPerformed
// Add your handling code here:
    if (luList.getSelectedIndex()!=-1){
        String remLang = (String)luList.getSelectedValue();
        lUsed.removeLanguage(remLang);
        luListModel.removeElementAt(luList.getSelectedIndex());
        luList.setSelectedIndex(-1);
        removeLuButton.setEnabled(false);
    }
  }//GEN-LAST:event_removeLuButtonActionPerformed

  private void addL2ButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addL2ButtonActionPerformed
// Add your handling code here:
    if (allLanguagesList.getSelectedIndex()!=-1){
        String newLanguage = ((String)allLanguagesList.getSelectedValue()).substring(0,3);
        if (l2.containsLanguage(newLanguage)) {return;}
        l2.addLanguage(newLanguage);
        l2ListModel.addElement(newLanguage);
    }    
  }//GEN-LAST:event_addL2ButtonActionPerformed

  private void addL1ButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addL1ButtonActionPerformed
// Add your handling code here:
    if (allLanguagesList.getSelectedIndex()!=-1){
        String newLanguage = ((String)allLanguagesList.getSelectedValue()).substring(0,3);
        if (l1.containsLanguage(newLanguage)) {return;}
        l1.addLanguage(newLanguage);
        l1ListModel.addElement(newLanguage);
    }    
  }//GEN-LAST:event_addL1ButtonActionPerformed

  private void addLuButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLuButtonActionPerformed
// Add your handling code here:
    if (allLanguagesList.getSelectedIndex()!=-1){
        String newLanguage = ((String)allLanguagesList.getSelectedValue()).substring(0,3);
        if (lUsed.containsLanguage(newLanguage)) {return;}
        lUsed.addLanguage(newLanguage);
        luListModel.addElement(newLanguage);
    }
  }//GEN-LAST:event_addLuButtonActionPerformed

  private void l2ListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_l2ListValueChanged
// Add your handling code here:
    if (l2List.getSelectedIndex()!=-1){
       removeL2Button.setEnabled(true);    
    }
    else {
       removeL2Button.setEnabled(false);    
    }            
  }//GEN-LAST:event_l2ListValueChanged

  private void l1ListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_l1ListValueChanged
// Add your handling code here:
    if (l1List.getSelectedIndex()!=-1){
       removeL1Button.setEnabled(true);    
    }
    else {
       removeL1Button.setEnabled(false);    
    }            
  }//GEN-LAST:event_l1ListValueChanged

  private void luListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_luListValueChanged
// Add your handling code here:
    if (luList.getSelectedIndex()!=-1){
       removeLuButton.setEnabled(true);    
    }
    else {
       removeLuButton.setEnabled(false);    
    }        
  }//GEN-LAST:event_luListValueChanged

  private void allLanguagesListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_allLanguagesListValueChanged
// Add your handling code here:
    boolean enable = ((allLanguagesList.getSelectedIndex()>=0) && (!((String)(allLanguagesList.getSelectedValue())).startsWith("---")));
    addLuButton.setEnabled(enable);
    addL1Button.setEnabled(enable);
    addL2Button.setEnabled(enable);
    allLanguagesList.ensureIndexIsVisible(allLanguagesList.getSelectedIndex());
  }//GEN-LAST:event_allLanguagesListValueChanged

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        change = false;
        setVisible (false);
        dispose ();
    }//GEN-LAST:event_closeDialog

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        try {
            org.exmaralda.partitureditor.partiture.BrowserLauncher.openURL("http://www.ethnologue.com/");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
        }
    }//GEN-LAST:event_jLabel2MouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
//        new EditLanguagesDialog (new javax.swing.JFrame (), true).show ();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addL1Button;
    private javax.swing.JButton addL2Button;
    private javax.swing.JButton addLuButton;
    private javax.swing.JList allLanguagesList;
    private javax.swing.JScrollPane allLanguagesScrollPane;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel explanationPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel l1ButtonPanel;
    private javax.swing.JList l1List;
    private javax.swing.JPanel l1Panel;
    private javax.swing.JPanel l2ButtonPanel;
    private javax.swing.JList l2List;
    private javax.swing.JPanel l2Panel;
    private javax.swing.JPanel luButtonPanel;
    private javax.swing.JList luList;
    private javax.swing.JPanel luPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeL1Button;
    private javax.swing.JButton removeL2Button;
    private javax.swing.JButton removeLuButton;
    private javax.swing.JPanel selectedLanguagesPanel;
    // End of variables declaration//GEN-END:variables

    public boolean editLanguages(){
        java.awt.Dimension dialogSize = this.getPreferredSize();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width/2 - dialogSize.width/2, screenSize.height/2 - dialogSize.height/2);
        this.setVisible(true);
        return change;
    }
    
    private void readAllLanguages() throws SAXException {
        try{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            XMLReader xmlReader = null;
            SAXParser saxParser = spf.newSAXParser();
            xmlReader = saxParser.getXMLReader();
            LanguageSaxHandler handler = new LanguageSaxHandler();
            xmlReader.setContentHandler(handler);
            java.io.InputStream is = getClass().getResourceAsStream("/org/exmaralda/partitureditor/jexmaralda/Languages.xml");
            xmlReader.parse(new InputSource(is));
            this.allLanguages = handler.allLanguages;
        }
        catch(SAXException se){
            throw (se);
        }
        catch (IOException e) {            
            System.out.println(e.getMessage());
            throw new SAXException ("I/O error", e);
        }
        catch (ParserConfigurationException pce) {
            throw new SAXException ("Parser Configuration error", pce);
        }        
    }
    
    class LanguageSaxHandler extends org.xml.sax.helpers.DefaultHandler {

        public Vector allLanguages = new Vector();
        
        public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {
            if (name.equals("language")){
                String code = atts.getValue("lang");
                String desc = atts.getValue("desc");
                String newItem = code + " - " + desc;
                allLanguages.addElement(newItem);
            }
        }
    }

}