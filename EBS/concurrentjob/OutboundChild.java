package oracle.apps.wsh.outbound.cp.request;

import oracle.jdbc.OracleCallableStatement;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import oracle.xml.parser.v2.XMLDocument;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.Reader;
import java.io.StringReader;
import oracle.xml.parser.v2.DOMParser;
import java.sql.ResultSet;
import oracle.apps.fnd.util.NameValueType;
import oracle.apps.fnd.util.ParameterList;
import java.sql.Connection;
import java.util.Vector;
import oracle.jdbc.OraclePreparedStatement;
import java.util.Hashtable;
import oracle.apps.fnd.cp.request.CpContext;
import oracle.apps.fnd.cp.request.ReqCompletion;
import oracle.apps.fnd.cp.request.OutFile;
import oracle.apps.fnd.cp.request.LogFile;
import oracle.apps.fnd.cp.request.JavaConcurrentProgram;

public class OutboundChild implements JavaConcurrentProgram
{
    public static final String RCS_ID = "$Header: OutboundChild.java 120.11.12020000.4 2017/02/08 06:59:00 sunilku ship $";
    String idLow;
    String idHigh;
    String entityName;
    int batchSize;
    String applName;
    String idClient;
    String idshipFromOrganization;
    String lowName;
    String highName;
    String ticket;
    String endDate;
    String serverTimeZone;
    String returnStatus;
    LogFile lLF;
    OutFile lOF;
    ReqCompletion lRC;
    String proxyServer;
    String proxyPort;
    int maxLength;
    int max_size;
    
    public OutboundChild() {
        this.idLow = null;
        this.idHigh = null;
        this.entityName = null;
        this.applName = null;
        this.idClient = null;
        this.idshipFromOrganization = null;
        this.lowName = null;
        this.highName = null;
        this.ticket = null;
        this.endDate = null;
        this.serverTimeZone = null;
        this.returnStatus = null;
        this.lLF = null;
        this.lOF = null;
        this.lRC = null;
        this.proxyServer = null;
        this.proxyPort = null;
        this.maxLength = 32000;
        this.max_size = 4000;
        this.applName = "WSH";
    }
    
    public void runProgram(final CpContext cpContext) {
        final Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        OraclePreparedStatement oraclePreparedStatement = null;
        final Connection jdbcConnection = cpContext.getJDBCConnection();
        final ParameterList parameterList = cpContext.getParameterList();
        this.lRC = cpContext.getReqCompletion();
        this.lLF = cpContext.getLogFile();
        this.lOF = cpContext.getOutFile();
        while (parameterList.hasMoreElements()) {
            final NameValueType nextParameter = parameterList.nextParameter();
            if (nextParameter.getName().equals("ID_LOW")) {
                this.idLow = nextParameter.getValue();
            }
            if (nextParameter.getName().equals("ID_HIGH")) {
                this.idHigh = nextParameter.getValue();
            }
            if (nextParameter.getName().equals("NAME_LOW")) {
                this.lowName = nextParameter.getValue();
            }
            if (nextParameter.getName().equals("NAME_HIGH")) {
                this.highName = nextParameter.getValue();
            }
            if (nextParameter.getName().equals("CLIENT_ID")) {
                this.idClient = nextParameter.getValue();
            }
            if (nextParameter.getName().equals("ENTITY")) {
                this.entityName = nextParameter.getValue();
            }
            if (nextParameter.getName().equals("BATCH_SIZE")) {
                this.batchSize = Integer.parseInt(nextParameter.getValue());
            }
            if (nextParameter.getName().equals("ORG_ID")) {
                this.idshipFromOrganization = nextParameter.getValue();
            }
        }
        final LogFile llf = this.lLF;
        final String string = "Id Low : " + this.idLow;
        final LogFile llf2 = this.lLF;
        llf.writeln(string, 1);
        final LogFile llf3 = this.lLF;
        final String string2 = "Id High : " + this.idHigh;
        final LogFile llf4 = this.lLF;
        llf3.writeln(string2, 1);
        final LogFile llf5 = this.lLF;
        final String string3 = "Client Id : " + this.idClient;
        final LogFile llf6 = this.lLF;
        llf5.writeln(string3, 1);
        final LogFile llf7 = this.lLF;
        final String string4 = "Entity Name : " + this.entityName;
        final LogFile llf8 = this.lLF;
        llf7.writeln(string4, 1);
        final LogFile llf9 = this.lLF;
        final String string5 = "Batch size : " + this.batchSize;
        final LogFile llf10 = this.lLF;
        llf9.writeln(string5, 1);
        final LogFile llf11 = this.lLF;
        final String string6 = "Organization Id : " + this.idshipFromOrganization;
        final LogFile llf12 = this.lLF;
        llf11.writeln(string6, 1);
        final LogFile llf13 = this.lLF;
        final String string7 = "Name Low : " + this.lowName;
        final LogFile llf14 = this.lLF;
        llf13.writeln(string7, 1);
        final LogFile llf15 = this.lLF;
        final String string8 = "Name High : " + this.highName;
        final LogFile llf16 = this.lLF;
        llf15.writeln(string8, 1);
        final StringBuffer sb = new StringBuffer();
        if (this.entityName.equals("DELIVERY")) {
            sb.append("select delivery_id id , name from wsh_new_deliveries");
            sb.append(" where tms_interface_flag in ('CP','UP','DP') ");
            sb.append(" and delivery_id >= :" + ++n);
            sb.append(" and delivery_id <= :" + ++n);
            if (this.lowName != null && !this.lowName.equals("")) {
                sb.append(" and name >= :" + ++n);
            }
            if (this.highName != null && !this.highName.equals("")) {
                sb.append(" and name <= :" + ++n);
            }
            if (this.idClient != null && !this.idClient.equals("")) {
                sb.append(" and client_id = to_number(:" + ++n + ") ");
            }
            if (this.idshipFromOrganization != null && !this.idshipFromOrganization.equals("")) {
                sb.append(" and organization_id = to_number(:" + ++n + ") ");
            }
        }
        else if (this.entityName.equals("BATCH")) {
            sb.append("select tms_sub_batch_id id, name from wsh_tms_sub_batches wtsb");
            sb.append(" where wtsb.tms_interface_flag in ('CP','UP','DP','RP') ");
            sb.append(" and tms_sub_batch_id >= :" + ++n);
            sb.append(" and tms_sub_batch_id <= :" + ++n);
            if (this.lowName != null && !this.lowName.equals("")) {
                sb.append(" and name >= :" + ++n);
            }
            if (this.highName != null && !this.highName.equals("")) {
                sb.append(" and name <= :" + ++n);
            }
        }
        else {
            sb.append("select distinct wt.trip_id, wt.name from wsh_trip_stops wts, wsh_trips wt");
            sb.append(" where wts.tms_interface_flag = 'ASP'");
            sb.append(" and wt.trip_id = wts.trip_id");
            sb.append(" and wt.trip_id >= :" + ++n3);
            sb.append(" and wt.trip_id <= :" + ++n3);
            if (this.lowName != null && !this.lowName.equals("")) {
                sb.append(" and wt.name >= :" + ++n3);
            }
            if (this.highName != null && !this.highName.equals("")) {
                sb.append(" and wt.name <= :" + ++n3);
            }
        }
        final LogFile llf17 = this.lLF;
        final String string9 = "IdList Query " + sb.toString();
        final LogFile llf18 = this.lLF;
        llf17.writeln(string9, 1);
        try {
            oraclePreparedStatement = (OraclePreparedStatement)jdbcConnection.prepareStatement(sb.toString());
            oraclePreparedStatement.defineColumnType(1, 4);
            oraclePreparedStatement.defineColumnType(2, 12, this.max_size);
            oraclePreparedStatement.setInt(++n2, Integer.parseInt(this.idLow));
            oraclePreparedStatement.setInt(++n2, Integer.parseInt(this.idHigh));
            if (this.lowName != null && !this.lowName.equals("") && !this.entityName.equals("BATCH")) {
                oraclePreparedStatement.setString(++n2, this.lowName);
            }
            if (this.highName != null && !this.highName.equals("") && !this.entityName.equals("BATCH")) {
                oraclePreparedStatement.setString(++n2, this.highName);
            }
            if (this.idClient != null && !this.idClient.equals("") && this.entityName.equals("DELIVERY")) {
                oraclePreparedStatement.setInt(++n2, Integer.parseInt(this.idClient));
            }
            if (this.idshipFromOrganization != null && !this.idshipFromOrganization.equals("") && this.entityName.equals("DELIVERY")) {
                oraclePreparedStatement.setInt(++n2, Integer.parseInt(this.idshipFromOrganization));
            }
            final ResultSet executeQuery = oraclePreparedStatement.executeQuery();
            final Vector<Integer> vector = new Vector<Integer>();
            while (executeQuery.next()) {
                final Integer n4 = new Integer(executeQuery.getInt(1));
                vector.add(n4);
                hashtable.put(n4, executeQuery.getObject(2));
                final LogFile llf19 = this.lLF;
                final String string10 = "getObject(1) " + executeQuery.getObject(1);
                final LogFile llf20 = this.lLF;
                llf19.writeln(string10, 1);
                final LogFile llf21 = this.lLF;
                final String string11 = "getObject(2) " + executeQuery.getObject(2);
                final LogFile llf22 = this.lLF;
                llf21.writeln(string11, 1);
            }
            final Vector<Integer> vector2 = new Vector<Integer>();
            final LogFile llf23 = this.lLF;
            final String string12 = "Ids Picked up " + vector.size();
            final LogFile llf24 = this.lLF;
            llf23.writeln(string12, 1);
            final LogFile llf25 = this.lLF;
            final String string13 = " Size of Hash Table " + hashtable.size();
            final LogFile llf26 = this.lLF;
            llf25.writeln(string13, 1);
            boolean b;
            if (vector.size() == 0 && this.returnStatus == null) {
                final LogFile llf27 = this.lLF;
                final String s = "Setting up the return status as Success and idsPickedup as false  1";
                final LogFile llf28 = this.lLF;
                llf27.writeln(s, 1);
                this.returnStatus = "S";
                b = false;
            }
            else {
                final LogFile llf29 = this.lLF;
                final String s2 = "Setting up idsPickedup as true ";
                final LogFile llf30 = this.lLF;
                llf29.writeln(s2, 1);
                b = true;
            }
            boolean contains = false;
            if (hashtable.size() > 0) {
                contains = hashtable.contains(vector.elementAt(0));
            }
            if (contains) {
                final LogFile llf31 = this.lLF;
                final String s3 = " true ";
                final LogFile llf32 = this.lLF;
                llf31.writeln(s3, 1);
            }
            else {
                final LogFile llf33 = this.lLF;
                final String s4 = " false ";
                final LogFile llf34 = this.lLF;
                llf33.writeln(s4, 1);
            }
            for (int i = 0; i < vector.size(); ++i) {
                final LogFile llf35 = this.lLF;
                final String string14 = "Value of key at " + i + " is " + vector.elementAt(i) + "   ";
                final LogFile llf36 = this.lLF;
                llf35.write(string14, 1);
                vector2.addElement(vector.elementAt(i));
                final LogFile llf37 = this.lLF;
                final String string15 = "Hash Table Entry " + hashtable.get(vector.elementAt(i)).toString();
                final LogFile llf38 = this.lLF;
                llf37.writeln(string15, 1);
            }
            final Vector<Integer> vector3 = new Vector<Integer>();
            int j = 0;
            while (vector.size() > 0) {
                final Vector<Integer> vector4 = new Vector<Integer>();
                for (int index = 0; index < this.batchSize && index < vector.size(); ++index) {
                    vector4.addElement(vector.elementAt(index));
                }
                for (int n5 = 0; n5 < this.batchSize && vector.size() > 0; ++n5) {
                    vector.removeElementAt(0);
                }
                ++j;
                if (vector4.size() > 0) {
                    final LogFile llf39 = this.lLF;
                    final String string16 = "\nLaunching Bpel process for batch " + j;
                    final LogFile llf40 = this.lLF;
                    llf39.writeln(string16, 1);
                    final LogFile llf41 = this.lLF;
                    final String string17 = this.entityName + " processed in this batch ";
                    final LogFile llf42 = this.lLF;
                    llf41.writeln(string17, 1);
                    for (int k = 0; k < vector4.size(); ++k) {
                        final LogFile llf43 = this.lLF;
                        final String string18 = vector4.elementAt(k) + "   ";
                        final LogFile llf44 = this.lLF;
                        llf43.write(string18, 1);
                    }
                    final LogFile llf45 = this.lLF;
                    final String s5 = "\n";
                    final LogFile llf46 = this.lLF;
                    llf45.writeln(s5, 1);
                    this.getDbToken(jdbcConnection);
                    final String buildPayload = this.buildPayload(cpContext, this.entityName, vector4);
                    if (buildPayload == null) {
                        continue;
                    }
                    final SoapBpelInvoker soapBpelInvoker = new SoapBpelInvoker(cpContext, this.proxyServer, this.proxyPort);
                    final LogFile llf47 = this.lLF;
                    final String s6 = "Calling SoapBpelInvoker.callWebService";
                    final LogFile llf48 = this.lLF;
                    llf47.writeln(s6, 1);
                    final String callWebService = soapBpelInvoker.callWebService(buildPayload);
                    Vector<Object> vector5 = new Vector<Object>();
                    this.returnStatus = this.parseResponse(cpContext, callWebService, vector5);
                    final LogFile llf49 = this.lLF;
                    final String string19 = "ReturnStatus from BPEL process: " + this.returnStatus;
                    final LogFile llf50 = this.lLF;
                    llf49.writeln(string19, 1);
                    if (this.returnStatus != null && this.returnStatus.equals("RETRY")) {
                        final LogFile llf51 = this.lLF;
                        final String s7 = "Calling SoapBpelInvoker.callWebService again";
                        final LogFile llf52 = this.lLF;
                        llf51.writeln(s7, 1);
                        final String string20 = soapBpelInvoker.callWebService(buildPayload).toString();
                        vector5 = new Vector<Object>();
                        this.returnStatus = this.parseResponse(cpContext, string20, vector5);
                        final LogFile llf53 = this.lLF;
                        final String string21 = "ReturnStatus from BPEL process: " + this.returnStatus;
                        final LogFile llf54 = this.lLF;
                        llf53.writeln(string21, 1);
                    }
                    if (this.returnStatus == null || !this.returnStatus.equalsIgnoreCase("S")) {
                        for (int l = 0; l < vector4.size(); ++l) {
                            vector2.removeElement(vector4.elementAt(l));
                            vector3.addElement(vector4.elementAt(l));
                        }
                        for (int n6 = 0; n6 < vector.size(); ++n6) {
                            vector2.removeElement(vector.elementAt(n6));
                            vector3.addElement(vector.elementAt(n6));
                        }
                        this.lRC.setCompletion(2, "Unexcepted Error returned from Bpel Process");
                        break;
                    }
                    for (int n7 = 0; n7 < vector5.size(); ++n7) {
                        vector2.removeElement(new Integer(vector5.elementAt(n7).toString()));
                        vector3.addElement(new Integer(vector5.elementAt(n7).toString()));
                    }
                }
            }
            if (this.entityName.equals("DELIVERY")) {
                this.entityName = "Deliveries";
            }
            else if (this.entityName.equals("BATCH")) {
                this.entityName = "Batches";
            }
            else {
                this.entityName = "Trips";
            }
            this.lOF.writeln("Number of " + this.entityName + " processed successfully: " + vector2.size());
            for (int index2 = 0; index2 < vector2.size(); ++index2) {
                this.lOF.writeln(hashtable.get(vector2.elementAt(index2)).toString());
            }
            this.lOF.writeln("\nNumber of " + this.entityName + " in error: " + vector3.size());
            for (int index3 = 0; index3 < vector3.size(); ++index3) {
                this.lOF.writeln(hashtable.get(vector3.elementAt(index3)).toString());
            }
            oraclePreparedStatement.close();
            if (this.returnStatus.equalsIgnoreCase("S")) {
                if (vector3.size() > 0) {
                    final LogFile llf55 = this.lLF;
                    final String s8 = " unSuccIdList is >0 Setting up the return status as Warning ";
                    final LogFile llf56 = this.lLF;
                    llf55.writeln(s8, 1);
                    this.lRC.setCompletion(1, "Some records were processed with error");
                }
                else if (!b) {
                    final LogFile llf57 = this.lLF;
                    final String s9 = "Setting up the return status as Warning ";
                    final LogFile llf58 = this.lLF;
                    llf57.writeln(s9, 1);
                    this.lRC.setCompletion(1, "No records picked up by the query");
                }
                else {
                    final LogFile llf59 = this.lLF;
                    final String s10 = "Setting up the return status as Normal ";
                    final LogFile llf60 = this.lLF;
                    llf59.writeln(s10, 1);
                    this.lRC.setCompletion(0, "Request Completed Normal");
                }
            }
        }
        catch (Exception ex) {
            this.lRC.setCompletion(2, ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            cpContext.releaseJDBCConnection();
            try {
                if (oraclePreparedStatement != null) {
                    oraclePreparedStatement.close();
                }
            }
            catch (Exception ex2) {}
        }
    }
    
    String parseResponse(final CpContext cpContext, final String str, final Vector vector) throws Exception {
        try {
            final DOMParser domParser = new DOMParser();
            final LogFile llf = this.lLF;
            final String string = "Response XML" + str;
            final LogFile llf2 = this.lLF;
            llf.writeln(string, 1);
            final StringReader stringReader = new StringReader(str.trim());
            try {
                domParser.parse((Reader)stringReader);
            }
            catch (SAXException ex) {
                throw new Exception("saxException in parseResponse" + ex.getMessage());
            }
            catch (IOException ex2) {
                throw new Exception("IOException in parseResponse" + ex2.getMessage());
            }
            final XMLDocument document = domParser.getDocument();
            final String valuefromXML = this.getValuefromXML(document, "bpelInstanceId");
            final String valuefromXML2 = this.getValuefromXML(document, "returnStatus");
            final String valuefromXML3 = this.getValuefromXML(document, "authenticated");
            final String valuefromXML4 = this.getValuefromXML(document, "otmTransmissionId");
            final LogFile llf3 = this.lLF;
            final String string2 = "authenticated " + valuefromXML3;
            final LogFile llf4 = this.lLF;
            llf3.writeln(string2, 1);
            if (valuefromXML3 != null && valuefromXML3.equals("false")) {
                final String str2 = "RETRY";
                final LogFile llf5 = this.lLF;
                final String string3 = " Returning back from parseResponse with return Status " + str2;
                final LogFile llf6 = this.lLF;
                llf5.writeln(string3, 1);
                return str2;
            }
            if (valuefromXML != null && !valuefromXML.equals("")) {
                final LogFile llf7 = this.lLF;
                final String s = "For more detailed information on the processing of deliveries in this batch";
                final LogFile llf8 = this.lLF;
                llf7.writeln(s, 1);
                final LogFile llf9 = this.lLF;
                final String string4 = "Please look at Bpel Instance Id :" + valuefromXML;
                final LogFile llf10 = this.lLF;
                llf9.writeln(string4, 1);
            }
            final LogFile llf11 = this.lLF;
            final String string5 = "OTM Transmission Id " + valuefromXML4;
            final LogFile llf12 = this.lLF;
            llf11.writeln(string5, 1);
            final NodeList elementsByTagName = document.getElementsByTagName("errorIdList");
            for (int i = 0; i < elementsByTagName.getLength(); ++i) {
                final NodeList childNodes = elementsByTagName.item(i).getChildNodes();
                for (int j = 0; j < childNodes.getLength(); ++j) {
                    final Node item = childNodes.item(j);
                    if (item != null && item.getNodeType() == 1 && item.getFirstChild() != null) {
                        vector.addElement(item.getFirstChild().getNodeValue());
                    }
                }
            }
            return valuefromXML2;
        }
        catch (Exception ex3) {
            final LogFile llf13 = this.lLF;
            final String string6 = " Exception occured in parseResponse" + ex3.getMessage();
            final LogFile llf14 = this.lLF;
            llf13.writeln(string6, 5);
            ex3.printStackTrace();
            throw new Exception(ex3.getMessage());
        }
    }
    
    String getValuefromXML(final XMLDocument xmlDocument, final String s) {
        final NodeList elementsByTagName = xmlDocument.getElementsByTagName(s);
        Node firstChild = null;
        if (elementsByTagName.getLength() > 0) {
            firstChild = elementsByTagName.item(0).getFirstChild();
        }
        if (firstChild != null) {
            return firstChild.getNodeValue();
        }
        return null;
    }
    
    String buildPayload(final CpContext cpContext, final String nodeValue, final Vector vector) {
        final LogFile llf = this.lLF;
        final String s = "In the method buildPayload ";
        final LogFile llf2 = this.lLF;
        llf.writeln(s, 2);
        final int userId = cpContext.getReqDetails().getUserInfo().getUserId();
        final int respId = cpContext.getReqDetails().getUserInfo().getRespId();
        final int respAppId = cpContext.getReqDetails().getUserInfo().getRespAppId();
        final String string = cpContext.getEnvStore().getEnv("FND_TOP") + "/secure";
        this.proxyServer = cpContext.getProfileStore().getProfile("WSH_OTM_PROXY_SERVER");
        this.proxyPort = cpContext.getProfileStore().getProfile("WSH_OTM_PROXY_PORT");
        final XMLDocument xmlDocument = new XMLDocument();
        final Element elementNS = xmlDocument.createElementNS("http://xmlns.oracle.com/apps/wsh/outbound/txn/WshSendTxnToOtmService", "WshSendTxnToOtmServiceProcessRequest");
        xmlDocument.appendChild((Node)elementNS);
        final Element element = xmlDocument.createElement("input");
        elementNS.appendChild(element);
        final Element elementNS2 = xmlDocument.createElementNS("http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound", "entityType");
        element.appendChild(elementNS2);
        final Text textNode = xmlDocument.createTextNode("text#");
        textNode.setNodeValue(nodeValue);
        elementNS2.appendChild(textNode);
        final Element elementNS3 = xmlDocument.createElementNS("http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound", "entityIdList");
        element.appendChild(elementNS3);
        for (int i = 0; i < vector.size(); ++i) {
            this.createElementandText(xmlDocument, elementNS3, "entityId", vector.elementAt(i).toString());
        }
        final Element elementNS4 = xmlDocument.createElementNS("http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound", "securityContext");
        element.appendChild(elementNS4);
        this.createElementandText(xmlDocument, elementNS4, "ticket", this.ticket);
        this.createElementandText(xmlDocument, elementNS4, "proxyServer", this.proxyServer);
        this.createElementandText(xmlDocument, elementNS4, "proxyPort", this.proxyPort);
        this.createElementandText(xmlDocument, elementNS4, "dbcConn", string);
        this.createElementandText(xmlDocument, elementNS4, "opCode", "WSH_SEND_TO_OTM_OUTBOUND");
        this.createElementandText(xmlDocument, elementNS4, "argKey", "OUTBOUND_POST");
        final Element elementNS5 = xmlDocument.createElementNS("http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound", "appsContext");
        element.appendChild(elementNS5);
        this.createElementandText(xmlDocument, elementNS5, "userId", new Integer(userId).toString());
        this.createElementandText(xmlDocument, elementNS5, "respId", new Integer(respId).toString());
        this.createElementandText(xmlDocument, elementNS5, "respAppId", new Integer(respAppId).toString());
        String replaceall;
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            xmlDocument.print((OutputStream)byteArrayOutputStream);
            replaceall = this.replaceall(byteArrayOutputStream.toString(), " xmlns=\"\"", "");
            final LogFile llf3 = this.lLF;
            final String s2 = "Input XML to be passed to the BPEL process ";
            final LogFile llf4 = this.lLF;
            llf3.writeln(s2, 1);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return replaceall;
    }
    
    String replaceall(final String s, final String str, final String str2) {
        if (str.equals("")) {
            throw new IllegalArgumentException("Old pattern must have content.");
        }
        final StringBuffer sb = new StringBuffer();
        int beginIndex;
        int index;
        for (beginIndex = 0; (index = s.indexOf(str, beginIndex)) >= 0; beginIndex = index + str.length()) {
            sb.append(s.substring(beginIndex, index));
            sb.append(str2);
        }
        sb.append(s.substring(beginIndex));
        return sb.toString();
    }
    
    void createElementandText(final XMLDocument xmlDocument, final Element element, final String s, final String nodeValue) {
        final Element element2 = xmlDocument.createElement(s);
        element.appendChild(element2);
        final Text textNode = xmlDocument.createTextNode("text#");
        textNode.setNodeValue(nodeValue);
        element2.appendChild(textNode);
    }
    
    void getDbToken(final Connection connection) throws Exception {
        OracleCallableStatement oracleCallableStatement = null;
        final LogFile llf = this.lLF;
        final String s = " In the method getDBToken ";
        final LogFile llf2 = this.lLF;
        llf.writeln(s, 2);
        try {
            oracleCallableStatement = (OracleCallableStatement)connection.prepareCall("BEGIN  WSH_OTM_HTTP_UTL.GET_SECURE_TICKET_DETAILS('WSH_SEND_TO_OTM_OUTBOUND','OUTBOUND_POST',:1,:2,:3); END;");
            oracleCallableStatement.registerOutParameter(1, 12, this.maxLength);
            oracleCallableStatement.registerOutParameter(2, 12, this.maxLength);
            oracleCallableStatement.registerOutParameter(3, 12, this.maxLength);
            oracleCallableStatement.execute();
            this.ticket = oracleCallableStatement.getString(1);
            this.serverTimeZone = oracleCallableStatement.getString(2);
            this.returnStatus = oracleCallableStatement.getString(3);
        }
        catch (Exception ex) {
            final LogFile llf3 = this.lLF;
            final String string = " Exception occured in getDBToken " + ex.getMessage();
            final LogFile llf4 = this.lLF;
            llf3.writeln(string, 4);
            this.lRC.setCompletion(2, ex.getMessage());
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }
        finally {
            try {
                if (oracleCallableStatement != null) {
                    oracleCallableStatement.close();
                }
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }
}