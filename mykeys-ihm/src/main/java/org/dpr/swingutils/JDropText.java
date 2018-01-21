package org.dpr.swingutils;

import java.awt.FlowLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JDropText extends JPanel  {

    private JDropTextField textfield;
    private JButton chooseButton;

	public JDropText(JComponent jc1, JComponent jc2) {
		setLayout(new FlowLayout(FlowLayout.LEADING));
		add(jc1);
		add(jc2);
	}

	public JDropText(JComponent jc1, JComponent jc2, int position) {
		setLayout(new FlowLayout(position));
		add(jc1);
		add(jc2);
	}
	
	   public JDropText() {
	       setLayout(new FlowLayout(FlowLayout.LEADING));
	        textfield  = new JDropTextField("", 20);
	        textfield.setEditable(false);
	        chooseButton = new JButton("...");
	        add(textfield);
	        add(chooseButton);
	        initComponent();
	    }
	   
	   private void initComponent(){
	       
	       chooseButton.addActionListener(new GenericAction());
	       chooseButton.setActionCommand("CHOOSE_IN");	 
	     
	       //textfield.addFocusListener(l);
	        //DocumentListener myListener = new TextListener();
	        
	        DropTarget dropTarget = new DropTarget(textfield, textfield);	       
	   }


    private class TextListener implements DocumentListener
	    {

	        public void changedUpdate(DocumentEvent e)
	        {
	            updateOutput();

	        }

	        public void insertUpdate(DocumentEvent e)
	        {

	            updateOutput();

	        }

	        public void removeUpdate(DocumentEvent e)
	        {
	            updateOutput();

	        }

        }

    class GenericAction extends AbstractAction
	    {

	        public void actionPerformed(ActionEvent event)
	        {
	            String command = event.getActionCommand();
	          

	            if (command.equals("CHOOSE_IN"))
	            {
	                JFileChooser jfc = new JFileChooser();

	                if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
	                {
	                    textfield.setText(jfc.getSelectedFile().getAbsolutePath());

	                }

	            }
	        }
	    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     */
    private void updateOutput()
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * .
     * 
     *<BR><pre>
     *<b>Algorithme : </b>
     *DEBUT
     *    
     *FIN</pre>
     *
     * @return
     */
    public String getText()
    {
        // TODO Auto-generated method stub
        return textfield.getText();
    }
    
    public void addListener(DocumentListener myListener){
        textfield.getDocument().addDocumentListener(myListener);
    }

}
