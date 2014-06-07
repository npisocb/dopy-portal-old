package emailer;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

public class GUI extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8336419645422699976L;
	private JTextField item;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JButton browse1;
	private JButton browse2;
	String CSVPath;
	String FolderPath;
	String Delimiter;
	public GUI () {
		super("Emailer!");
		setLayout(new FlowLayout());
		
		label1=new JLabel("Enter Delimiter:   ");
		add(label1);
		item=new JTextField(10);
		item.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent ae) {
		    	  if(ae.getSource()==item)
		    		  Delimiter=ae.getActionCommand();
		      }
		});
		add(item);
		label2=new JLabel("Enter CSV FIle Path:   ");
		add(label2);
		browse1=new JButton("Browse...");
		browse1.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent ae) {
		    	  if(ae.getSource()==browse1){
		    		  JFileChooser fileChooser = new JFileChooser();
		    		  int returnValue = fileChooser.showOpenDialog(null);
		    		  if (returnValue == JFileChooser.APPROVE_OPTION) {
		    			  File selectedFile = fileChooser.getSelectedFile();
		    			  CSVPath=selectedFile.getPath();
		    		  }
		    	  }
		      }
		});
		add(browse1);
		label3=new JLabel("Enter Outsti Folder Path:   ");
		add(label3);
		browse2=new JButton("Browse...");
		browse2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(ae.getSource()==browse2){
					JFileChooser fileChooser = new JFileChooser();
					int returnValue = fileChooser.showOpenDialog(null);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						File selectedFile = fileChooser.getSelectedFile();
						FolderPath=selectedFile.getPath();
					}
				}
			}
		});
		add(browse2);
	}
	public String getDelimiter() {
		return Delimiter;
	}
	public String getCSVPath() {
		return CSVPath;
	}
	public String getFolderPath() {
		return FolderPath;
	}
}