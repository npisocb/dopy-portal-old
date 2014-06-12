package emailer;

import java.io.*;

import javax.mail.*;
import javax.swing.*;
import java.util.*;

//import javax.activation.*;
import javax.mail.internet.*;

/*
 * Do not have anything except for the snaps in outsti folder especially csv file
 * For now i have assumed that csv file has only 2 inputs per image. The image number, without DSC_ and also, email ID.
 * I have assumed all images are jpegs 
 */

public class Emailer {
	
	public static void main(String[] args) throws IOException, FileNotFoundException, AddressException, MessagingException {
		GUI gui=new GUI();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(180,200);
		gui.setVisible(true);
		String CSVpath=gui.getCSVPath();
		String delimiter=gui.getDelimiter();
		BufferedReader br=new BufferedReader(new FileReader(CSVpath));
		String[] list=br.readLine().split(delimiter);    //have no return characters in the csv
		br.close();
		String folder_path=gui.getFolderPath();
		File folder=new File(folder_path);
		renamer(folder);
		
		//Assuming the list has 2 fields per image, email adress image number (part after DSC)
		
		for(int i=0;i<list.length-2;i+=2)
			validateAndSend(list[i],list[i+1],folder);
		JOptionPane.showMessageDialog(null,"Done");
	}
	public static void validateAndSend(String ToAddress, String FileName, File folder) throws AddressException, MessagingException {
		String FilePath=folder.getPath()+"\\"+FileName+".jpg";
		File image=new File(FilePath);
		if(image.exists()) {
			email(ToAddress,FilePath);
		}
		else
			System.out.printf("File Named %s doesn't exist in %s",FileName,folder.getName());
	}
	public static void renamer(File folder) throws IOException {
		for(File image: folder.listFiles()) {
			String newName=image.getName().substring(4);  //First 4 letters are DSC_
			File temp=new File(folder.getPath()+"\\"+newName);
			if(temp.exists())
				throw new java.io.IOException(String.format("A file named %s already exists in %d folder!.\n",newName,folder.getName()));
			image.renameTo(temp);
		}
	}
	public static String input(String msg) throws IOException {
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		System.out.println(msg);
		return in.readLine();
	}
	public static void email(String toAddress, String attachment) throws AddressException, MessagingException {
		final String host="smtp.gmail.com";
		final String port="587";
		final String username="dopy@gmail.com";	//change accordingly  
		final String password="xxxxxx";	//change accordingly  
		final String subject="Your DoPy Snaps For Fest in 201x!";
		final String message="Attached Files are your DoPy snaps for the recent Fest.";
		  
		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.user", username);
		properties.put("mail.password", password);
	      
		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
	          	}
	      	};
	    Session session = Session.getInstance(properties, auth);
	    // creates a new e-mail message
	    Message msg = new MimeMessage(session);
	 
	    msg.setFrom(new InternetAddress(username));
	    InternetAddress[] toAddresses = {new InternetAddress(toAddress)};
	    msg.setRecipients(Message.RecipientType.TO, toAddresses);
	    msg.setSubject(subject);
	    msg.setSentDate(new Date());
	    
	    // creates message part
	    MimeBodyPart messageBodyPart = new MimeBodyPart();
	    messageBodyPart.setContent(message,"text/html");
	    
	    // creates multi-part
	    Multipart multipart = new MimeMultipart();
	    multipart.addBodyPart(messageBodyPart);
	    
	    // adds attachment
	    MimeBodyPart attachPart = new MimeBodyPart();
	    try {
	    	attachPart.attachFile(attachment);
	    }
	    catch (IOException ex) {
	     	ex.printStackTrace();
	   	}
	 	multipart.addBodyPart(attachPart);
	 
	    // sets the multi-part as e-mail's content
	    msg.setContent(multipart);
	 
	    // sends the e-mail
	    Transport.send(msg);
		}  
}
