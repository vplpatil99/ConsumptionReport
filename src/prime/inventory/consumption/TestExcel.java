package prime.inventory.consumption;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TestExcel {
	 XSSFWorkbook workbook;
	 Connection connection=null;
	 Connection conn=null;
	 String filename="";
	private Scanner sc;
 ConnectionDB conobj;
	SimpleDateFormat Currentdate= new SimpleDateFormat("yyyyMMdd");
	String ApplicationPath="D:\\ConsumptionReport\\";

 		public TestExcel()
		    {

  				SimpleDateFormat CurrentdateFormat = new SimpleDateFormat("MM");
				
		        // Blank workbook
	        	workbook = new XSSFWorkbook();
		        conobj=new ConnectionDB();
		        connection=conobj.getConnectionDB();
		        conn=conobj.getConnectionTallyserverDB();
		        

		        
		        
		        
		        filename=getCityName()+"consumption_"+getMonthandYearLable()+".xlsx";
		        try {
			        Statement m_Statement = connection.createStatement();
			        String query = "select top 1 * from reportsendingmaster where reportname='Consumption' and month(sending_date)='"+CurrentdateFormat.format(new Date())+"'";

			        ResultSet m_ResultSet = m_Statement.executeQuery(query);
			        if ((m_ResultSet.next())) {
			        	System.exit(0);
			        }
			        m_ResultSet.close();
		        	
							String SPsql = "EXEC ConsumptionDATA";   // for stored proc taking 2 parameters
							
							PreparedStatement ps = connection.prepareStatement(SPsql);
							ps.setEscapeProcessing(true);
							ps.setQueryTimeout(0);
							ps.execute();
			
							
							
				        	ssv();
				        	ssb();
				        	fs();
				        	fs1();
				        	fb();
				        	fb1();
				        	SendAttachmentInEmail();				     
 				        	productionReport();
				        	System.out.println("Done");
			        
			        }catch(Exception e) {
			        
			        	System.out.println(""+e);
			        }
		    }
		
		void ssb() {
			try {
				 // Create a blank sheet
		        XSSFSheet sheet = workbook.createSheet("SSB");		       		      		        

		        
		        
		        Statement m_Statement = connection.createStatement();
		        String query = "select * from ssb_consumed order by lens_type";

		        ResultSet m_ResultSet = m_Statement.executeQuery(query);
		        Map<String, Object[]> data = new TreeMap<String, Object[]>();
		        
		        data.put("1", new Object[]{ "Lens_type", "Qty" });
		        int count=1;
		        while (m_ResultSet.next()) {
		        count++;
		          //System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "+ m_ResultSet.getString(3));
		          data.put(count+"", new Object[]{m_ResultSet.getString(1), m_ResultSet.getString(2)});

		        }
		        
		        // This data needs to be written (Object[])
		       
		        /*data.put("1", new Object[]{ "ID", "NAME", "LASTNAME" });
		        data.put("2", new Object[]{ 1, "Pankaj", "Kumar" });
		        data.put("3", new Object[]{ 2, "Prakashni", "Yadav" });
		        data.put("4", new Object[]{ 3, "Ayan", "Mondal" });
		        data.put("5", new Object[]{ 4, "Virat", "kohli" });*/
		 
		        // Iterate over data and write to sheet
		        Set<String> keyset = data.keySet();
		        int rownum = 0;
		        for (String key : keyset) {
		            // this creates a new row in the sheet
		            Row row = sheet.createRow(rownum++);
		            Object[] objArr = data.get(key);
		            int cellnum = 0;
		            for (Object obj : objArr) {
		                // this line creates a cell in the next column of that row
		                Cell cell = row.createCell(cellnum++);
		                if (obj instanceof String)
		                    cell.setCellValue((String)obj);
		                else if (obj instanceof Integer)
		                    cell.setCellValue((Integer)obj);
		            }
		        }

		            // this Writes the workbook gfgcontribute
		            FileOutputStream out = new FileOutputStream(new File(ApplicationPath+filename));
		            workbook.write(out);
		            out.close();
		            //System.out.println("data written successfully on disk.");
		        }
		        catch (Exception e) {
		            e.printStackTrace();
		        }
		}
		
		void ssv() {
			try {
			XSSFSheet sheet = workbook.createSheet("SSV");
			Statement m_Statement = connection.createStatement();
	        String query = "select * from ssv_consumed order by lens_type";

	        ResultSet m_ResultSet = m_Statement.executeQuery(query);
	        Map<String, Object[]> data = new TreeMap<String, Object[]>();
	        
	        data.put("1", new Object[]{ "Lens_type", "Qty" });
	        int count=1;
	        
	        // This data needs to be written (Object[])	  
	        while (m_ResultSet.next()) {
	        count++;
	          //System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "+ m_ResultSet.getString(3));
	          data.put(count+"", new Object[]{m_ResultSet.getString(1), m_ResultSet.getString(2)});

	        }
	        
   
	
	        // Iterate over data and write to sheet
	        Set<String> keyset = data.keySet();
	        int rownum = 0;
	        for (String key : keyset) {
	            // this creates a new row in the sheet
	            Row row = sheet.createRow(rownum++);
	            Object[] objArr = data.get(key);
	            int cellnum = 0;
	            for (Object obj : objArr) {
	                // this line creates a cell in the next column of that row
	                Cell cell = row.createCell(cellnum++);
	                if (obj instanceof String)
	                    cell.setCellValue((String)obj);
	                else if (obj instanceof Integer)
	                    cell.setCellValue((Integer)obj);
	            }
	        }

	            // this Writes the workbook gfgcontribute
	            FileOutputStream out = new FileOutputStream(new File(ApplicationPath+filename));
	            workbook.write(out);
	            out.close();
	            //System.out.println("data written successfully on disk.");
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		void fs() {
			try {
			XSSFSheet sheet = workbook.createSheet("FS");
			Statement m_Statement = connection.createStatement();
	        String query = "select * from FSV_consumed order by lens_type";

	        ResultSet m_ResultSet = m_Statement.executeQuery(query);
	        Map<String, Object[]> data = new TreeMap<String, Object[]>();
	        
	        data.put("1", new Object[]{ "Lens_type", "Qty" });
	        int count=1;
	        
	        // This data needs to be written (Object[])	  
	        while (m_ResultSet.next()) {
	        count++;
	          //System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "+ m_ResultSet.getString(3));
	          data.put(count+"", new Object[]{m_ResultSet.getString(1), m_ResultSet.getString(2)});

	        }
	        
   
	
	        // Iterate over data and write to sheet
	        Set<String> keyset = data.keySet();
	        int rownum = 0;
	        for (String key : keyset) {
	            // this creates a new row in the sheet
	            Row row = sheet.createRow(rownum++);
	            Object[] objArr = data.get(key);
	            int cellnum = 0;
	            for (Object obj : objArr) {
	                // this line creates a cell in the next column of that row
	                Cell cell = row.createCell(cellnum++);
	                if (obj instanceof String)
	                    cell.setCellValue((String)obj);
	                else if (obj instanceof Integer)
	                    cell.setCellValue((Integer)obj);
	            }
	        }

	            // this Writes the workbook gfgcontribute
	            FileOutputStream out = new FileOutputStream(new File(ApplicationPath+filename));
	            workbook.write(out);
	            out.close();
	            //System.out.println("data written successfully on disk.");
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		void fs1() {
			try {
			XSSFSheet sheet = workbook.createSheet("FS1");
			Statement m_Statement = connection.createStatement();
	        String query = "select * from FSVB_consumed order by lens_type";

	        ResultSet m_ResultSet = m_Statement.executeQuery(query);
	        Map<String, Object[]> data = new TreeMap<String, Object[]>();
	        
	        data.put("1", new Object[]{ "Lens_type", "Qty" });
	        int count=1;
	        
	        // This data needs to be written (Object[])	  
	        while (m_ResultSet.next()) {
	        count++;
	          //System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "+ m_ResultSet.getString(3));
	          data.put(count+"", new Object[]{m_ResultSet.getString(1), m_ResultSet.getString(2)});

	        }
	        
   
	
	        // Iterate over data and write to sheet
	        Set<String> keyset = data.keySet();
	        int rownum = 0;
	        for (String key : keyset) {
	            // this creates a new row in the sheet
	            Row row = sheet.createRow(rownum++);
	            Object[] objArr = data.get(key);
	            int cellnum = 0;
	            for (Object obj : objArr) {
	                // this line creates a cell in the next column of that row
	                Cell cell = row.createCell(cellnum++);
	                if (obj instanceof String)
	                    cell.setCellValue((String)obj);
	                else if (obj instanceof Integer)
	                    cell.setCellValue((Integer)obj);
	            }
	        }

	            // this Writes the workbook gfgcontribute
	            FileOutputStream out = new FileOutputStream(new File(ApplicationPath+filename));
	            workbook.write(out);
	            out.close();
	            //System.out.println("data written successfully on disk.");
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		void fb() {
			try {
			XSSFSheet sheet = workbook.createSheet("FB");
			Statement m_Statement = connection.createStatement();
	        String query = "select * from  FSB_consumed order by lens_type";

	        ResultSet m_ResultSet = m_Statement.executeQuery(query);
	        Map<String, Object[]> data = new TreeMap<String, Object[]>();
	        
	        data.put("1", new Object[]{ "Lens_type", "Qty" });
	        int count=1;
	        
	        // This data needs to be written (Object[])	  
	        while (m_ResultSet.next()) {
	        count++;
	          //System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "+ m_ResultSet.getString(3));
	          data.put(count+"", new Object[]{m_ResultSet.getString(1), m_ResultSet.getString(2)});

	        }
	        
   
	
	        // Iterate over data and write to sheet
	        Set<String> keyset = data.keySet();
	        int rownum = 0;
	        for (String key : keyset) {
	            // this creates a new row in the sheet
	            Row row = sheet.createRow(rownum++);
	            Object[] objArr = data.get(key);
	            int cellnum = 0;
	            for (Object obj : objArr) {
	                // this line creates a cell in the next column of that row
	                Cell cell = row.createCell(cellnum++);
	                if (obj instanceof String)
	                    cell.setCellValue((String)obj);
	                else if (obj instanceof Integer)
	                    cell.setCellValue((Integer)obj);
	            }
	        }

	            // this Writes the workbook gfgcontribute
	            FileOutputStream out = new FileOutputStream(new File(ApplicationPath+filename));
	            workbook.write(out);
	            out.close();
	            //System.out.println("data written successfully on disk.");
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		void fb1() {
			try {
			XSSFSheet sheet = workbook.createSheet("FB1");
			Statement m_Statement = connection.createStatement();
	        String query = "select * from  FSBB_consumed order by lens_type";

	        ResultSet m_ResultSet = m_Statement.executeQuery(query);
	        Map<String, Object[]> data = new TreeMap<String, Object[]>();
	        
	        data.put("1", new Object[]{ "Lens_type", "Qty" });
	        int count=1;
	        
	        // This data needs to be written (Object[])	  
	        while (m_ResultSet.next()) {
	        count++;
	          //System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "+ m_ResultSet.getString(3));
	          data.put(count+"", new Object[]{m_ResultSet.getString(1), m_ResultSet.getString(2)});

	        }
	        
   
	
	        // Iterate over data and write to sheet
	        Set<String> keyset = data.keySet();
	        int rownum = 0;
	        for (String key : keyset) {
	            // this creates a new row in the sheet
	            Row row = sheet.createRow(rownum++);
	            Object[] objArr = data.get(key);
	            int cellnum = 0;
	            for (Object obj : objArr) {
	                // this line creates a cell in the next column of that row
	                Cell cell = row.createCell(cellnum++);
	                if (obj instanceof String)
	                    cell.setCellValue((String)obj);
	                else if (obj instanceof Integer)
	                    cell.setCellValue((Integer)obj);
	            }
	        }

	            // this Writes the workbook gfgcontribute
	            FileOutputStream out = new FileOutputStream(new File(ApplicationPath+filename));
	            workbook.write(out);
	            out.close();
	            //System.out.println("data written successfully on disk.");
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		void SendAttachmentInEmail() {
		      // Recipient's email ID needs to be mentioned.
		      //String to = "vipul.patil@primelenses.com";

		      // Sender's email ID needs to be mentioned
		      String from = "prime@primelenses.com";

		      final String username = "prime@primelenses.com";//change accordingly
		      final String password = "optimalrx";//change accordingly
		      Properties props = new Properties();
		      props.put("mail.smtp.host", "smtp.gmail.com");    
		      props.put("mail.smtp.socketFactory.port", "465");    
		      props.put("mail.smtp.socketFactory.class",    
		                "javax.net.ssl.SSLSocketFactory");    
		      props.put("mail.smtp.auth", "true");    
		      props.put("mail.smtp.port", "465");    

		      // Get the Session object.
		      Session session = Session.getInstance(props,
		         new javax.mail.Authenticator() {
		            protected PasswordAuthentication getPasswordAuthentication() {
		               return new PasswordAuthentication(username, password);
		            }
		         });

		      try {
		         // Create a default MimeMessage object.
		         Message message = new MimeMessage(session);

		         // Set From: header field of the header.
		         message.setFrom(new InternetAddress(from));
		         Iterator itr=getEmailAddress(conn).iterator();
		         
		         while(itr.hasNext()){  
		         // Set To: header field of the header.
		         //message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(itr.next().toString()));
		        	 message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(itr.next().toString()));
		         }
		         message.addRecipients(Message.RecipientType.CC,InternetAddress.parse("nitin.hiwase@primelenses.com"));
		         message.addRecipients(Message.RecipientType.CC,InternetAddress.parse("vipul.patil@primelenses.com"));

		         // Set Subject: header field
		         message.setSubject(getCityName()+" Consumption "+getMonthandYearLable());

		         // Create the message part
		         BodyPart messageBodyPart = new MimeBodyPart();

		         // Now set the actual message
		         messageBodyPart.setText("Dear Sir,\nPlease find the attachmentThanks and Regards\r\n" + 
		         		"IT Team\r\n" + 
		         		"ALL INDIA\r\n" + 
		         		"\r\n" + 
		         		"[This email is system generated email,please do not reply to this email.]");

		         // Create a multipar message
		         Multipart multipart = new MimeMultipart();

		         // Set text message part
		         multipart.addBodyPart(messageBodyPart);

		         // Part two is attachment
		         messageBodyPart = new MimeBodyPart();
		         String filepath = filename;
		         DataSource source = new FileDataSource(ApplicationPath+filepath);
		         messageBodyPart.setDataHandler(new DataHandler(source));
		         messageBodyPart.setFileName(filepath);
		         multipart.addBodyPart(messageBodyPart);

		         // Send the complete message parts
		         message.setContent(multipart);

		         // Send message
		         Transport.send(message);

		         System.out.println("Sent message successfully....");
		         
		         Statement st = connection.createStatement(); 
		         st.executeUpdate("INSERT INTO reportsendingmaster VALUES ('Consumption', '"+Currentdate.format(new Date())+"')"); 
		  
		      } catch (Exception e) {
		         throw new RuntimeException(e);
		      }
		   }
		
		public String getMonthandYearLable()
		{
			 Date referenceDate = new Date();
			 Calendar c = Calendar.getInstance(); 
			 c.setTime(referenceDate); 
			 c.add(Calendar.MONTH, -1);
			 
			 
			 SimpleDateFormat f1 = new SimpleDateFormat("MMM");
			 SimpleDateFormat f2 = new SimpleDateFormat("yyyy");
			// Date date= format.parse("12:06:30");
			 return f1.format(c.getTime())+"_"+f2.format(c.getTime());
		}
		
		public ArrayList<String> getEmailAddress(Connection conn)
		{
			ArrayList<String> list=new ArrayList<String>();
			
			//list.add("neson.konar@primelenses.com");
			
			
			try {
			Statement m_Statement = conn.createStatement();
	        String query = "select email from tbl_emailreceivermaster where reportname='Consumption'";

	        ResultSet m_ResultSet = m_Statement.executeQuery(query);
	        
	        while (m_ResultSet.next()) {
	        
	        	list.add(m_ResultSet.getString(1));

	        }
			}catch(Exception e)
			{
				
			}
			
			return list;
		}
		
		public String getCityName()
		{
			String City_Name="";
			String s="";
			try {
			
		 	File file =new File(ApplicationPath+"ServerConfig.txt");
			sc = new Scanner(file);
			 
			    while (sc.hasNextLine()) {
			    if((s=sc.nextLine()).contains("City_Name"))	
			    {			    
			      City_Name=s.substring(s.indexOf('=')+1);
			    }
			  }
			}catch(Exception e)
			{
				
			}

 			 return City_Name;
		}
		
		public void productionReport()
		{
			String lab = null,order_received = null,reissue = null,rework = null,Forder_received = null,Freissue = null,Frework = null;
			try {
			//procedure call for local data 
			String SPsql = "EXEC USP_production_report @CityName='"+getCityName()+"'";   // for stored proc taking 2 parameters			
			PreparedStatement ps = connection.prepareStatement(SPsql);
			ps.setEscapeProcessing(true);
			ps.setQueryTimeout(0);
			//ps.setString(1,getCityName().toString());
			ps.execute();
			
			//retrieve the local data table of tbl_productionreport
			
			Statement m_Statement = connection.createStatement();
	        String query = "select lab,order_received,reissue,rework,Forder_received,Freissue,Frework from tbl_production";

	        ResultSet m_ResultSet = m_Statement.executeQuery(query);
	        
	        while (m_ResultSet.next()) {
	        
	        	lab=m_ResultSet.getString(1);
	        	order_received=m_ResultSet.getString(2);
	        	reissue=m_ResultSet.getString(3);
	        	rework=m_ResultSet.getString(4);
	        	Forder_received=m_ResultSet.getString(5);
	        	Freissue=m_ResultSet.getString(6);
	        	Frework=m_ResultSet.getString(7);

	        }
	     // create a Statement from the connection
	        Statement statement = conn.createStatement();

	        // insert the data
	        statement.executeUpdate("INSERT INTO tbl_production(lab,order_received,reissue,rework,Forder_received,Freissue,Frework,updatedate) VALUES ('"+lab+"','"+order_received+"','"+reissue+"','"+rework+"','"+Forder_received+"','"+Freissue+"','"+Frework+"',getdate())");
			
			
			}catch(Exception e) {
				System.out.println(e);
			}
		}
}
