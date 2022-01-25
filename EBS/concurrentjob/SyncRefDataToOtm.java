// 
// Decompiled by Procyon v0.5.36
// 

package oracle.apps.wsh.outbound.cp.request;

import oracle.apps.fnd.common.VersionInfo;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.sql.CallableStatement;
import oracle.jdbc.OracleCallableStatement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.DOMParser;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import oracle.apps.fnd.wf.bes.BusinessEventException;
import oracle.apps.fnd.wf.bes.BusinessEvent;
import oracle.apps.fnd.util.NameValueType;
import oracle.apps.fnd.util.ParameterList;
import oracle.apps.fnd.common.Context;
import oracle.apps.fnd.common.AppsContext;
import oracle.apps.fnd.cp.request.CpContext;
import oracle.apps.fnd.common.ErrorStack;
import oracle.apps.fnd.common.AppsProfileStore;
import oracle.apps.fnd.cp.request.LogFile;
import oracle.apps.fnd.cp.request.OutFile;
import oracle.apps.fnd.cp.request.ReqCompletion;
import oracle.apps.fnd.cp.request.JavaConcurrentProgram;

public class SyncRefDataToOtm implements JavaConcurrentProgram
{
    public static final String RCS_ID = "$Header: SyncRefDataToOtm.java 120.9 2011/08/23 10:57:46 aashah ship $";
    public static final boolean RCS_ID_RECORDED;
    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private static final String STD_DATE_FORMAT = "DD-MON-YYYY HH24:MI:SS";
    private static final String OP_CODE = "refOpCode";
    private static final String TKT_KEY = "refFndTktKey";
    private static final String ARG_KEY = "refArgKey";
    private String carrierNameLow;
    private String carrierNameHigh;
    private String lastUpdateDateLow;
    private String lastUpdateDateHigh;
    private ReqCompletion reqComp;
    private String refTktKey;
    private boolean compSuccess;
    private boolean isDebugOn;
    private int numWarn;
    private OutFile outFile;
    private LogFile logFile;
    private AppsProfileStore profStore;
    private ErrorStack eStack;
    private String l_bpelDomain;
    
    public SyncRefDataToOtm() {
        this.carrierNameLow = null;
        this.carrierNameHigh = null;
        this.lastUpdateDateLow = null;
        this.lastUpdateDateHigh = null;
        this.reqComp = null;
        this.refTktKey = null;
        this.compSuccess = true;
        this.isDebugOn = false;
        this.numWarn = 0;
        this.outFile = null;
        this.logFile = null;
        this.profStore = null;
        this.eStack = null;
    }
    
    public void runProgram(final CpContext cpContext) {
        this.profStore = new AppsProfileStore((AppsContext)cpContext);
        this.checkDebugger(cpContext);
        this.logFile = cpContext.getLogFile();
        if (this.isDebugOn) {
            this.logMessage("Entering SyncRefDataToOtm.runProgram");
        }
        this.initVariables(cpContext);
        this.initSecurityInfo(cpContext);
        this.validateProfiles(cpContext);
        if (this.compSuccess) {
            this.queryAndSendEntities(cpContext, "CARRIER");
        }
        if (this.compSuccess) {
            this.reqComp.setCompletion(0, "SUCCESS");
        }
        cpContext.releaseJDBCConnection();
        if (this.isDebugOn) {
            this.logMessage("Completion Status " + this.compSuccess);
            this.logMessage("Exiting SyncRefDataToOtm.runProgram");
        }
    }
    
    private void initVariables(final CpContext cpContext) {
        if (this.isDebugOn) {
            this.logMessage("Entering SyncRefDataToOtm.initVariables");
        }
        this.outFile = cpContext.getOutFile();
        this.reqComp = cpContext.getReqCompletion();
        this.eStack = new ErrorStack((Context)cpContext);
        final ParameterList parameterList = cpContext.getParameterList();
        while (parameterList.hasMoreElements()) {
            final NameValueType nextParameter = parameterList.nextParameter();
            final String name = nextParameter.getName();
            final String value = nextParameter.getValue();
            if (this.isDebugOn) {
                this.logMessage("paramName is " + name);
                this.logMessage("paramValue is " + value);
            }
            if (name.equalsIgnoreCase("CARRIER_NAME_LOW")) {
                this.carrierNameLow = value;
            }
            else if (name.equals("CARRIER_NAME_HIGH")) {
                this.carrierNameHigh = value;
            }
            else if (name.equalsIgnoreCase("LAST_UPDATE_DATE_LOW")) {
                this.lastUpdateDateLow = value;
            }
            else {
                if (!name.equals("LAST_UPDATE_DATE_HIGH") || value == null || value.equals("")) {
                    continue;
                }
                this.lastUpdateDateHigh = value.substring(0, 11) + "23:59:59";
            }
        }
        if (this.isDebugOn) {
            this.logMessage("Exiting SyncRefDataToOtm.initVariables");
        }
    }
    
    private void sendDocumentViaSIF(final String s, final String str, final CpContext cpContext) {
        final String string = str + "?wsdl";
        final String str2 = "WshSendRefDataStlToOtm";
        final String str3 = "WshSendRefDataStlToOtm";
        final String str4 = "process";
        final String s2 = "WSH_OTM_PROXY_SERVER";
        final String s3 = "WSH_OTM_PROXY_PORT";
        String str5;
        if (str.contains("/soa-infra/")) {
            str5 = "WshSendRefDataStlToOtm";
        }
        else {
            str5 = "WshSendRefDataStlToOtmPort";
        }
        if (this.isDebugOn) {
            this.logMessage("Entered SyncRefDataToOtm.sendDocumentViaSIF  \n");
            this.logMessage("xml is " + s + "\n");
            this.logMessage("wsdlURL is " + string + "\n");
        }
        try {
            final String string2 = "WSH_CARRIER_OTM" + System.currentTimeMillis();
            final BusinessEvent businessEvent = new BusinessEvent("oracle.apps.sif.webservice.inline.invoke", string2);
            if (this.isDebugOn) {
                this.logMessage("Entering BusinessEvent setting \n");
            }
            businessEvent.setStringProperty("SERVICE_WSDL_URL", string);
            businessEvent.setStringProperty("SERVICE_NAME", str2);
            businessEvent.setStringProperty("SERVICE_PORT", str5);
            businessEvent.setStringProperty("SERVICE_PORTTYPE", str3);
            businessEvent.setStringProperty("SERVICE_OPERATION", str4);
            final String profileValue = this.getProfileValue(s2);
            final String profileValue2 = this.getProfileValue(s3);
            if (this.isDebugOn) {
                this.logMessage("wsdlURL: " + string + "\n");
                this.logMessage("serviceName: " + str2 + "\n");
                this.logMessage("servicePort: " + str5 + "\n");
                this.logMessage("servicePortType: " + str3 + "\n");
                this.logMessage("serviceOperation: " + str4 + "\n");
            }
            if (profileValue != null && profileValue.length() > 0) {
                System.setProperty("http.proxyHost", profileValue);
            }
            if (profileValue2 != null && profileValue2.length() > 0) {
                System.setProperty("http.proxyPort", profileValue2);
            }
            businessEvent.setData(s);
            if (this.isDebugOn) {
                this.logMessage("Raising SIF Event oracle.apps.sif.webservice.inline.invoke with event key " + string2 + "\n");
            }
            try {
                businessEvent.raise(cpContext.getJDBCConnection());
            }
            catch (BusinessEventException ex) {
                ++this.numWarn;
                this.logMessage("Got Exception Invoking SIF event oracle.apps.sif.webservice.inline.invoke \n");
                this.logMessage("Exception is e.getLinkedException().getMessage() \n\n" + ex.getLinkedException().getMessage() + "\n");
            }
            final String s4 = (String)businessEvent.getResponseData();
            final InputSource inputSource = new InputSource(new StringReader(s4));
            final DOMParser domParser = new DOMParser();
            domParser.parse(inputSource);
            final XMLDocument document = domParser.getDocument();
            final String valuefromXML = this.getValuefromXML(document, "bpelInstanceId");
            final String valuefromXML2 = this.getValuefromXML(document, "returnStatus");
            final String valuefromXML3 = this.getValuefromXML(document, "otmTransmissionId");
            if (!"S".equalsIgnoreCase(valuefromXML2) && valuefromXML == null && valuefromXML3 == null) {
                ++this.numWarn;
                this.logMessage("Unexpected Error Occured. Please check Response message for more details. Either Bpel Instnace Id is null or Transmission Id is null or Ebiz Adapter response status is not successful\n");
                this.logMessage(s4 + "\n");
                this.logMessage("Please look at BPEL Instance Id " + valuefromXML + " for more Details \n");
                throw new Exception();
            }
            this.logMessage("Successful Update of Values \n");
            this.logMessage("Successful Completion Of BPEL instance Id " + valuefromXML + "\n");
            this.logMessage(" OTM TransmissionId is  " + valuefromXML3 + "\n");
        }
        catch (Exception ex2) {
            ++this.numWarn;
            this.logMessage("Got Exception Invoking SIF event oracle.apps.sif.webservice.inline.invoke \n");
            this.logMessage("Exception is " + ex2.toString() + "\n");
        }
    }
    
    private void queryAndSendEntities(final CpContext cpContext, final String str) {
        if (this.isDebugOn) {
            this.logMessage("Entering SyncRefDataToOtm.queryAndSendEntities");
            this.logMessage("entity Type is " + str);
            this.logMessage("carrierNameLow " + this.carrierNameLow);
            this.logMessage("carrierNameHigh " + this.carrierNameHigh);
            this.logMessage("lastUpdateDateLow " + this.lastUpdateDateLow);
            this.logMessage("lastUpdateDateHigh " + this.lastUpdateDateHigh);
        }
        final Connection jdbcConnection = cpContext.getJDBCConnection();
        final StringBuffer sb = new StringBuffer(" SELECT HPS.PARTY_SITE_ID,");
        sb.append("        HPS.PARTY_ID,");
        sb.append("        HPS.LOCATION_ID");
        sb.append(" FROM   WSH_CARRIERS_V WC,");
        sb.append("        HZ_PARTY_SITES HPS");
        sb.append(" WHERE  HPS.PARTY_ID = WC.CARRIER_ID");
        sb.append(" AND    WC.ACTIVE = 'A'");
        sb.append(" AND    WC.CARRIER_NAME BETWEEN :1 AND :2");
        sb.append(" AND    (   (   (:3 is null or WC.LAST_UPDATE_DATE >= to_date(:4,'YYYY/MM/DD HH24:MI:SS'))");
        sb.append("                AND    (:5 is null or WC.LAST_UPDATE_DATE <= to_date(:6,'YYYY/MM/DD HH24:MI:SS') ))");
        sb.append(" OR (   (:7 is null or HPS.LAST_UPDATE_DATE >= to_date(:8,'YYYY/MM/DD HH24:MI:SS'))");
        sb.append(" AND    (:9 is null or HPS.LAST_UPDATE_DATE <= to_date(:10,'YYYY/MM/DD HH24:MI:SS'))))");
        sb.append(" ORDER BY WC.CARRIER_ID");
        PreparedStatement prepareStatement = null;
        ResultSet executeQuery = null;
        int n = 0;
        int n2 = 0;
        final StringBuffer sb2 = new StringBuffer();
        final StringBuffer sb3 = new StringBuffer();
        final StringBuffer sb4 = new StringBuffer();
        String lastUpdateDateLow = null;
        if (this.lastUpdateDateLow != null && !this.lastUpdateDateLow.equals("")) {
            lastUpdateDateLow = this.lastUpdateDateLow;
        }
        String lastUpdateDateHigh = null;
        if (this.lastUpdateDateHigh != null && !this.lastUpdateDateHigh.equals("")) {
            lastUpdateDateHigh = this.lastUpdateDateHigh;
        }
        this.l_bpelDomain = cpContext.getProfileStore().getProfile("WSH_OTM_BPEL_DOMAIN_NAME");
        if (this.l_bpelDomain == null || this.l_bpelDomain.equals("")) {
            this.l_bpelDomain = "default";
        }
        if (this.isDebugOn) {
            this.logMessage("BPEL Domain is " + this.l_bpelDomain);
        }
        String str2;
        if (this.l_bpelDomain.equals("NOT-USED")) {
            str2 = cpContext.getProfileStore().getProfile("WSH_OTM_OB_SERVICE_ENDPOINT") + "/WshSendRefDataStlToOtm/client";
        }
        else {
            str2 = this.profStore.getProfile("WSH_OTM_OB_SERVICE_ENDPOINT") + "/orabpel/" + this.l_bpelDomain + "/WshSendRefDataStlToOtm/1.0";
        }
        int i = 0;
        try {
            if (this.isDebugOn) {
                this.logMessage("query String is  " + sb.toString());
            }
            prepareStatement = jdbcConnection.prepareStatement(sb.toString());
            prepareStatement.setString(1, this.carrierNameLow);
            prepareStatement.setString(2, this.carrierNameHigh);
            if (this.isDebugOn) {
                if (lastUpdateDateLow != null) {
                    this.logMessage("l_dateLow is " + lastUpdateDateLow);
                }
                else {
                    this.logMessage("l_dateLow is " + (Object)null);
                }
                if (lastUpdateDateHigh != null) {
                    this.logMessage("l_dateHigh is " + lastUpdateDateHigh);
                }
                else {
                    this.logMessage("l_dateHigh is " + (Object)null);
                }
            }
            if (lastUpdateDateLow != null) {
                prepareStatement.setString(3, lastUpdateDateLow);
                prepareStatement.setString(4, lastUpdateDateLow);
            }
            else {
                prepareStatement.setString(3, null);
                prepareStatement.setString(4, null);
            }
            if (lastUpdateDateHigh != null) {
                prepareStatement.setString(5, lastUpdateDateHigh);
                prepareStatement.setString(6, lastUpdateDateHigh);
            }
            else {
                prepareStatement.setString(5, null);
                prepareStatement.setString(6, null);
            }
            if (lastUpdateDateLow != null) {
                prepareStatement.setString(7, lastUpdateDateLow);
                prepareStatement.setString(8, lastUpdateDateLow);
            }
            else {
                prepareStatement.setString(7, null);
                prepareStatement.setString(8, null);
            }
            if (lastUpdateDateHigh != null) {
                prepareStatement.setString(9, lastUpdateDateHigh);
                prepareStatement.setString(10, lastUpdateDateHigh);
            }
            else {
                prepareStatement.setString(9, null);
                prepareStatement.setString(10, null);
            }
            executeQuery = prepareStatement.executeQuery();
            long lng = 0L;
            int j = 0;
            if (this.isDebugOn) {
                this.logMessage(" endPoint is " + str2);
            }
            while (executeQuery.next()) {
                final long long1 = executeQuery.getLong(1);
                final long long2 = executeQuery.getLong(2);
                final long long3 = executeQuery.getLong(3);
                if (this.isDebugOn) {
                    this.logMessage(" curCarrSiteId is " + long1);
                    this.logMessage(" curCarrierId is " + long2);
                    this.logMessage(" curLocationId is " + long3);
                    this.logMessage(" prevCarrierId is " + lng);
                    this.logMessage(" elementsCount is " + j);
                    this.logMessage(" totCarrCount is " + n);
                    this.logMessage(" totCarrSiteCount is " + n2);
                }
                if ((j >= 98 && long2 != lng) || j == 99) {
                    sb2.append(sb3.toString());
                    sb2.append("</entityIdList>");
                    sb2.append(sb4.toString());
                    sb2.append("</parentIdList>");
                    sb2.append("<securityContext xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound\">");
                    sb2.append("<ticket>" + this.refTktKey + "</ticket>");
                    sb2.append("<proxyServer />");
                    sb2.append("<proxyPort />");
                    sb2.append("<dbcConn />");
                    sb2.append("<opCode>" + "refOpCode" + "</opCode>");
                    sb2.append("<argKey>" + "refArgKey" + "</argKey>");
                    sb2.append("</securityContext>");
                    sb2.append("<appsContext xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound\">");
                    sb2.append("<userId />");
                    sb2.append("<respId />");
                    sb2.append("<respAppId />");
                    sb2.append("</appsContext>");
                    sb2.append("</input>");
                    sb2.append("</WshSendRefDataStlToOtmProcessRequest>\n");
                    this.sendDocumentViaSIF(sb2.toString(), str2, cpContext);
                    sb2.delete(0, sb2.length());
                    sb3.delete(0, sb3.length());
                    sb4.delete(0, sb4.length());
                    j = 0;
                }
                if (j == 0) {
                    sb2.append("<WshSendRefDataStlToOtmProcessRequest xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/ref/WshSendRefDataStlToOtm\">\n");
                    sb2.append("<input xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/ref/WshSendRefDataStlToOtm\" client=\"http://xmlns.oracle.com/apps/wsh/outbound/ref/WshSendRefDataStlToOtm\" >");
                    sb2.append("<entityType xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound\">CARRIER</entityType>");
                    sb3.append("<entityIdList xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound\">");
                    sb4.append("<parentIdList xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound\">");
                    j += 2;
                }
                else if (long2 != lng) {
                    j += 2;
                }
                else {
                    ++j;
                }
                sb3.append("<entityId>" + long1 + "</entityId>");
                sb4.append("<entityId>" + long2 + "</entityId>");
                if (long2 != lng && j >= 2) {
                    ++n;
                }
                ++n2;
                lng = long2;
                ++i;
            }
        }
        catch (SQLException ex) {
            this.logMessage("Exception occured" + ex.toString());
            this.reqComp.setCompletion(2, "ERROR " + ex.toString());
            this.compSuccess = false;
            try {
                executeQuery.close();
                prepareStatement.close();
            }
            catch (SQLException ex2) {
                this.logMessage("Exception occured" + ex2.toString());
                this.reqComp.setCompletion(2, "ERROR " + ex2.toString());
                this.compSuccess = false;
            }
        }
        finally {
            try {
                executeQuery.close();
                prepareStatement.close();
            }
            catch (SQLException ex3) {
                this.logMessage("Exception occured" + ex3.toString());
                this.reqComp.setCompletion(2, "ERROR " + ex3.toString());
                this.compSuccess = false;
            }
        }
        if ((sb2.toString() != null || !sb2.toString().equals("")) && i > 0) {
            sb2.append(sb3.toString());
            sb2.append("</entityIdList>");
            sb2.append(sb4.toString());
            sb2.append("</parentIdList>");
            sb2.append("<securityContext xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound\">");
            sb2.append("<ticket>" + this.refTktKey + "</ticket>");
            sb2.append("<proxyServer />");
            sb2.append("<proxyPort />");
            sb2.append("<dbcConn />");
            sb2.append("<opCode>" + "refOpCode" + "</opCode>");
            sb2.append("<argKey>" + "refArgKey" + "</argKey>");
            sb2.append("</securityContext>");
            sb2.append("<appsContext xmlns=\"http://xmlns.oracle.com/apps/wsh/outbound/util/WshOtmGlobalOutbound\">");
            sb2.append("<userId />");
            sb2.append("<respId />");
            sb2.append("<respAppId />");
            sb2.append("</appsContext>");
            sb2.append("</input>");
            sb2.append("</WshSendRefDataStlToOtmProcessRequest>");
            if (this.isDebugOn) {
                this.logMessage("Calling SendDocument outside the loop");
            }
            this.sendDocumentViaSIF(sb2.toString(), str2, cpContext);
            sb2.delete(0, sb2.length());
        }
        else if (this.isDebugOn) {
            this.logMessage("No Carriers were picked up based on the inputs");
        }
        if (this.isDebugOn) {
            this.logMessage("Total number of loops = " + i);
            this.logMessage("Total number of Warnings = " + this.numWarn);
        }
        if (this.numWarn != 0 && this.numWarn >= i) {
            this.logMessage(" COMP success = false");
            this.compSuccess = false;
        }
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_MSG_HDR", "CURR_DATE", new Date().toString()));
        this.outFile.writeln("");
        this.outFile.writeln("");
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_PARAM_HDR", "", ""));
        this.outFile.writeln("");
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_CARR_LOW", "CARR_NAME", this.carrierNameLow));
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_CARR_HIGH", "CARR_NAME", this.carrierNameHigh));
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_UPD_DATE_LOW", "UPD_DATE", this.lastUpdateDateLow));
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_UPD_DATE_HIGH", "UPD_DATE", this.lastUpdateDateHigh));
        this.outFile.writeln("");
        this.outFile.writeln("");
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_RESULTS_HDR", "", ""));
        this.outFile.writeln("");
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_CARR_NUM", "CARR_NUM", "" + n));
        this.outFile.writeln(this.getMessage(this.eStack, "WSH_OTM_REF_CARR_ST_NUM", "CARR_ST_NUM", "" + n2));
        if (this.isDebugOn) {
            this.logMessage("Exiting SyncRefDataToOtm.queryAndSendEntities");
        }
    }
    
    private String getProfileValue(final String str) {
        if (this.isDebugOn) {
            this.logMessage("Entering SyncRefDataToOtm.getProfileValue");
            this.logMessage("profileName = " + str);
        }
        final String profile = this.profStore.getProfile(str);
        if (this.isDebugOn) {
            this.logMessage("Exiting SyncRefDataToOtm.getProfileValue");
            this.logMessage("profileValue = " + profile);
        }
        return profile;
    }
    
    private void initSecurityInfo(final CpContext cpContext) {
        if (this.isDebugOn) {
            this.logMessage("Entering SyncRefDataToOtm.initSecurityInfo");
        }
        final Connection jdbcConnection = cpContext.getJDBCConnection();
        if (this.isDebugOn) {
            this.logMessage(" Connection object " + jdbcConnection);
        }
        CallableStatement prepareCall = null;
        try {
            prepareCall = jdbcConnection.prepareCall("{call WSH_OTM_HTTP_UTL.GET_SECURE_TICKET_DETAILS(:1,:2,:3,:4,:5)}");
            prepareCall.setString(1, "refOpCode");
            prepareCall.setString(2, "refArgKey");
            ((OracleCallableStatement)prepareCall).registerOutParameter(3, 12, 0, 500);
            ((OracleCallableStatement)prepareCall).registerOutParameter(4, 12, 0, 500);
            ((OracleCallableStatement)prepareCall).registerOutParameter(5, 12, 0, 1);
            prepareCall.execute();
            this.refTktKey = prepareCall.getString(3);
            if (!prepareCall.getString(5).equals("S")) {
                this.compSuccess = false;
                this.reqComp.setCompletion(2, " Error occurred in the API call WSH_OTM_HTTP_UTL.GET_SECURE_TICKET_DETAILS");
            }
        }
        catch (SQLException ex) {
            this.compSuccess = false;
            this.logMessage("Exception occured " + ex.toString());
            this.reqComp.setCompletion(2, "ERROR " + ex.toString());
            try {
                if (prepareCall != null) {
                    prepareCall.close();
                }
            }
            catch (SQLException ex2) {
                this.compSuccess = false;
                this.logMessage("Exception occured " + ex2.toString());
                this.reqComp.setCompletion(2, "ERROR " + ex2.toString());
            }
        }
        finally {
            try {
                if (prepareCall != null) {
                    prepareCall.close();
                }
            }
            catch (SQLException ex3) {
                this.compSuccess = false;
                this.logMessage("Exception occured " + ex3.toString());
                this.reqComp.setCompletion(2, "ERROR " + ex3.toString());
            }
        }
        if (this.isDebugOn) {
            this.logMessage("Exiting SyncRefDataToOtm.initSecurityInfo");
        }
    }
    
    private void logMessage(final String s) {
        if (s != null && this.logFile != null) {
            this.logFile.writeln(s, 1);
        }
    }
    
    private void checkDebugger(final CpContext cpContext) {
        final String profile = this.profStore.getProfile("WSH_DEBUG_MODE");
        if (profile != null && profile.equals("T")) {
            this.isDebugOn = true;
        }
    }
    
    private String getMessage(final ErrorStack errorStack, final String s, final String s2, final String s3) {
        errorStack.addMessage("WSH", s);
        if (s2 != null && !s2.equals("")) {
            errorStack.addToken(s2, s3);
        }
        return errorStack.nextMessage();
    }
    
    private String getMessage(final Connection connection, final String s, final String s2, final String s3) {
        if (this.isDebugOn) {
            this.logMessage("Entering SyncRefDataToOtm.getMessage");
        }
        CallableStatement callableStatement = null;
        String string = null;
        try {
            callableStatement = connection.prepareCall("{call FND_MESSAGE.SET_NAME(:1,:2)}");
            callableStatement.setString(1, "WSH");
            callableStatement.setString(2, s);
            callableStatement.execute();
            callableStatement.close();
            if (s3 != null && !s3.equals("") && s2 != null && !s2.equals("")) {
                callableStatement = connection.prepareCall("{call FND_MESSAGE.SET_TOKEN(:1,:2)}");
                callableStatement.setString(1, s2);
                callableStatement.setString(2, s3);
                callableStatement.execute();
                callableStatement.close();
            }
            callableStatement = connection.prepareCall("{call :1 = FND_MESSAGE.GET}");
            ((OracleCallableStatement)callableStatement).registerOutParameter(1, 12, 0, 500);
            callableStatement.execute();
            string = callableStatement.getString(1);
            try {
                if (callableStatement != null) {
                    callableStatement.close();
                }
            }
            catch (SQLException ex) {
                return "";
            }
        }
        catch (SQLException ex2) {
            return "";
        }
        finally {
            try {
                if (callableStatement != null) {
                    callableStatement.close();
                }
            }
            catch (SQLException ex3) {
                return "";
            }
        }
        if (this.isDebugOn) {
            this.logMessage("Exiting SyncRefDataToOtm.getMessage");
        }
        return string;
    }
    
    private void validateProfiles(final CpContext cpContext) {
        if (this.isDebugOn) {
            this.logMessage("Entering SyncRefDataToOtm.validateProfiles");
        }
        this.isOtmInstalled(cpContext);
        final String profile = this.profStore.getProfile("WSH_OTM_DOMAIN_NAME");
        final String profile2 = this.profStore.getProfile("WSH_OTM_CORP_COUNTRY_CODE");
        if (this.isDebugOn) {
            this.logMessage("domain Name is " + profile);
            this.logMessage("country code is " + profile2);
        }
        if (profile == null || profile.equalsIgnoreCase("")) {
            this.compSuccess = false;
            this.logMessage("");
            this.logMessage(this.getMessage(this.eStack, "WSH_OTM_DOMAIN_NOT_SET_ERR", "", ""));
            this.logMessage("");
        }
        if (profile2 == null || profile2.equalsIgnoreCase("")) {
            this.compSuccess = false;
            this.logMessage("");
            this.logMessage(this.getMessage(this.eStack, "WSH_OTM_CNTR_CODE_NOT_SET_ERR", "", ""));
            this.logMessage("");
        }
        if (this.isDebugOn) {
            this.logMessage("Exiting SyncRefDataToOtm.validateProfiles");
        }
    }
    
    private void isOtmInstalled(final CpContext cpContext) {
        if (this.isDebugOn) {
            this.logMessage("Entering SyncRefDataToOtm.isOtmInstalled");
        }
        final Connection jdbcConnection = cpContext.getJDBCConnection();
        if (this.isDebugOn) {
            this.logMessage(" Connection object " + jdbcConnection);
        }
        CallableStatement prepareCall = null;
        try {
            prepareCall = jdbcConnection.prepareCall(" BEGIN :1 := WSH_UTIL_CORE.GC3_IS_INSTALLED; END;");
            ((OracleCallableStatement)prepareCall).registerOutParameter(1, 12, 0, 1);
            prepareCall.execute();
            if (prepareCall.getString(1).equals("N")) {
                this.compSuccess = false;
                this.logMessage("");
                this.logMessage(this.getMessage(this.eStack, "FTE_NOT_INSTALLED", "", ""));
                this.logMessage("");
            }
        }
        catch (SQLException ex) {
            this.compSuccess = false;
            this.logMessage("Exception occured " + ex.toString());
            this.reqComp.setCompletion(2, "ERROR " + ex.toString());
            try {
                if (prepareCall != null) {
                    prepareCall.close();
                }
            }
            catch (SQLException ex2) {
                this.compSuccess = false;
                this.logMessage("Exception occured " + ex2.toString());
                this.reqComp.setCompletion(2, "ERROR " + ex2.toString());
            }
        }
        finally {
            try {
                if (prepareCall != null) {
                    prepareCall.close();
                }
            }
            catch (SQLException ex3) {
                this.compSuccess = false;
                this.logMessage("Exception occured " + ex3.toString());
                this.reqComp.setCompletion(2, "ERROR " + ex3.toString());
            }
        }
        if (this.isDebugOn) {
            this.logMessage("Exiting SyncRefDataToOtm.isOtmInstalled");
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
    
    static {
        RCS_ID_RECORDED = VersionInfo.recordClassVersion("$Header: SyncRefDataToOtm.java 120.9 2011/08/23 10:57:46 aashah ship $", "oracle.apps.wsh.outbound.cp.request");
    }
}