package org.saki.demo2;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;

import sun.nio.cs.StandardCharsets;

import com.sap.engine.services.webservices.espbase.client.dynamic.content.ObjectFactory;




import ws.adapterframework.server.mdt.aii.sap.com.AdapterFilter;
import ws.adapterframework.server.mdt.aii.sap.com.AdapterFrameworkData;
import ws.adapterframework.server.mdt.aii.sap.com.ArrayOfAdapterFrameworkData;
import ws.adapterframework.server.mdt.aii.sap.com.MessageSearchReturnValue;

import adaptermessagemonitoringvi.GetMessageList;

import adaptermessagemonitoringwsd.basic.AdapterMessageMonitoringVi;
import adaptermessagemonitoringwsd.basic.AdapterMessageMonitoringViService;
import adaptermessagemonitoringwsd.basic.GetMessageBytesJavaLangStringBooleanComSapAiiMdtServerAdapterframeworkWsInvalidKeyExceptionDoc;
import adaptermessagemonitoringwsd.basic.GetMessageBytesJavaLangStringBooleanComSapAiiMdtServerAdapterframeworkWsOperationFailedExceptionDoc;
import adaptermessagemonitoringwsd.basic.GetMessageBytesJavaLangStringIntBooleanComSapAiiMdtServerAdapterframeworkWsInvalidKeyExceptionDoc;
import adaptermessagemonitoringwsd.basic.GetMessageBytesJavaLangStringIntBooleanComSapAiiMdtServerAdapterframeworkWsOperationFailedExceptionDoc;
import adaptermessagemonitoringwsd.basic.GetMessageListComSapAiiMdtServerAdapterframeworkWsOperationFailedExceptionDoc;

public class ExtractFailedMessages {

	

	

	
static String monitoring_full_URL = "";	
static String monitoring_URL =	"/AdapterMessageMonitoring/basic?wsdl=binding&style=document&mode=ws_policy";
static String serverPort = "http://server:port";
static String user = "";
static String password = "";
static String fileforMessage = "C://temp//failed_messages//";
static String QoS = "EO";
static boolean onlyFaultMessages = true;
static Boolean allVersions = false;

public static void gc() {
    Object obj = new Object();
    WeakReference ref = new WeakReference<Object>(obj);
    obj = null;
    while(ref.get() != null) {
      System.gc();
    }
  }


	public static void main(String[] args) throws IOException, GetMessageListComSapAiiMdtServerAdapterframeworkWsOperationFailedExceptionDoc, GetMessageBytesJavaLangStringBooleanComSapAiiMdtServerAdapterframeworkWsOperationFailedExceptionDoc, GetMessageBytesJavaLangStringBooleanComSapAiiMdtServerAdapterframeworkWsInvalidKeyExceptionDoc, GetMessageBytesJavaLangStringIntBooleanComSapAiiMdtServerAdapterframeworkWsInvalidKeyExceptionDoc, GetMessageBytesJavaLangStringIntBooleanComSapAiiMdtServerAdapterframeworkWsOperationFailedExceptionDoc {
	
		
		serverPort = args[0];
        user = args[1];
        password = args[2];
        fileforMessage = args[3];
        QoS = args[4];
        onlyFaultMessages = true;
        allVersions = Boolean.valueOf(args[5]);
        
        
		
		
		AdapterMessageMonitoringVi port = null;
		AdapterMessageMonitoringViService sendMessages = null;
		sendMessages = new AdapterMessageMonitoringViService();
		
		port = (AdapterMessageMonitoringVi) sendMessages.getAdapterMessageMonitoringVi_Port();
		
		BindingProvider bp = (BindingProvider)port;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, user);
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		monitoring_full_URL = serverPort.concat(monitoring_URL);
		

            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, monitoring_full_URL);

            GetMessageList getMessages = new GetMessageList();
            
            
            
                       
            
            AdapterFilter filterValue = new AdapterFilter();
            esiext.ws.adapterframework.server.mdt.aii.sap.com.ObjectFactory fact = new esiext.ws.adapterframework.server.mdt.aii.sap.com.ObjectFactory();  
            
            
            JAXBElement<String> qoS = fact.createMessagingSystemDataQualityOfService(QoS);

            
/*            Calendar createDate = Calendar.getInstance();
            Date cDate = createDate.getTime();
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(cDate);
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            
            
            XMLGregorianCalendar value = 2009-05-07T17:05:45.678Z;
			fact.createMessagingSystemDataStartTime(value);
*/            
//			filterValue.setQualityOfService(qoS);
			filterValue.setQualityOfService(qoS);
            filterValue.setOnlyFaultyMessages(true);
            getMessages.setFilter(filterValue);
            MessageSearchReturnValue out = port.getMessageList(filterValue, 10000);
            
            
			JAXBElement<ArrayOfAdapterFrameworkData> tempJAXB = out.getList();
			
			List <AdapterFrameworkData> adapterList = tempJAXB.getValue().getAdapterFrameworkData();
			
			for (AdapterFrameworkData adapterData : adapterList){
				
				
				
				String messageID = adapterData.getMessageKey().getValue();
				

				
				System.out.println(messageID);
				
				

			
			
			if ( allVersions.equals(true)) {	
				
				for ( int i = -1; i<=2; i++){
					
					String thisMessageFile  = fileforMessage + adapterData.getSenderName().getValue() + "_" + 
					adapterData.getInterface().getValue().getName().getValue().replace(".", "__").replace("/", "__") + "_" 
					+ adapterData.getReceiverName().getValue() + "_" 
					+ adapterData.getMessageID().getValue() +"_"+ i + "_"+ ".txt";

					
				byte[] message = port.getMessageBytesJavaLangStringIntBoolean(messageID,i,false);
				String messageAsString = new String(message);
				PrintWriter outFile = new PrintWriter(thisMessageFile);
				outFile.println(messageAsString);
				outFile.close();
				
				gc();
				}
				
			}else{
				String thisMessageFile2  = fileforMessage + adapterData.getSenderName().getValue() + "_" + 
				adapterData.getInterface().getValue().getName().getValue().replace(".", "__").replace("/", "__") + "_" 
				+ adapterData.getReceiverName().getValue() + "_" 
				+ adapterData.getMessageID().getValue() +"_"+ "_latest_" + "_"+ ".txt";

				
			byte[] message = port.getMessageBytesJavaLangStringBoolean(messageID,false);
			String messageAsString = new String(message);
			PrintWriter outFile = new PrintWriter(thisMessageFile2);
			outFile.println(messageAsString);
			outFile.close();
			
			gc();
				
				
			}
				
				
			}
            
			


	}

}
