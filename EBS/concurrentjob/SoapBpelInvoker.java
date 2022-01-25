// 
// Decompiled by Procyon v0.5.36
// 

package oracle.apps.wsh.outbound.cp.request;

import oracle.apps.fnd.wf.bes.BusinessEventException;
import oracle.apps.fnd.wf.bes.BusinessEvent;
import oracle.apps.fnd.cp.request.CpContext;
import oracle.apps.fnd.cp.request.LogFile;

public class SoapBpelInvoker
{
    public static final String RCS_ID = "$Header: SoapBpelInvoker.java 120.8 2011/08/23 09:29:11 aashah ship $";
    private String _endpoint;
    private String l_bpelDomain;
    LogFile lLF;
    private String proxyHost;
    private String proxyPort;
    private boolean isDebugOn;
    CpContext lCpContext;
    
    public SoapBpelInvoker(final CpContext lCpContext, final String proxyHost, final String proxyPort) {
        this.lLF = null;
        this.proxyHost = null;
        this.proxyPort = null;
        this.isDebugOn = false;
        this.lCpContext = lCpContext;
        this.lLF = lCpContext.getLogFile();
        final String profile = lCpContext.getProfileStore().getProfile("WSH_DEBUG_MODE");
        if (profile != null && profile.equals("T")) {
            this.isDebugOn = true;
        }
        this.l_bpelDomain = lCpContext.getProfileStore().getProfile("WSH_OTM_BPEL_DOMAIN_NAME");
        if (this.l_bpelDomain == null || this.l_bpelDomain.equals("")) {
            this.l_bpelDomain = "default";
        }
        final LogFile llf = this.lLF;
        final String string = "BPEL Domain " + this.l_bpelDomain;
        final LogFile llf2 = this.lLF;
        llf.writeln(string, 1);
        if (proxyHost != null && proxyHost.length() > 0) {
            this.proxyHost = proxyHost;
        }
        if (proxyPort != null && proxyPort.length() > 0) {
            this.proxyPort = proxyPort;
        }
        if (this.l_bpelDomain.equals("NOT-USED")) {
            this._endpoint = lCpContext.getProfileStore().getProfile("WSH_OTM_OB_SERVICE_ENDPOINT") + "/WshSendTxnToOtmService/client";
        }
        else {
            this._endpoint = lCpContext.getProfileStore().getProfile("WSH_OTM_OB_SERVICE_ENDPOINT") + "/orabpel/" + this.l_bpelDomain + "/WshSendTxnToOtmService/1.0";
        }
    }
    
    private String sendDocumentViaSIF(final String s, final String str) {
        final String string = str + "?wsdl";
        final String str2 = "WshSendTxnToOtmService";
        final String str3 = "WshSendTxnToOtmService";
        final String str4 = "process";
        String s2 = null;
        String str5;
        if (str.contains("/soa-infra/")) {
            str5 = "WshSendTxnToOtmService";
        }
        else {
            str5 = "WshSendTxnToOtmServicePort";
        }
        if (this.isDebugOn) {
            final LogFile llf = this.lLF;
            final String s3 = "Entering SoapBpelInvoker.sendDocumentViaSIF \n";
            final LogFile llf2 = this.lLF;
            llf.writeln(s3, 1);
            final LogFile llf3 = this.lLF;
            final String string2 = "xml is " + s + "\n";
            final LogFile llf4 = this.lLF;
            llf3.writeln(string2, 1);
            final LogFile llf5 = this.lLF;
            final String string3 = "wsdlURL is " + string + "\n";
            final LogFile llf6 = this.lLF;
            llf5.writeln(string3, 1);
        }
        try {
            final String string4 = "WSH_OUTBOUND_OTM" + System.currentTimeMillis();
            final BusinessEvent businessEvent = new BusinessEvent("oracle.apps.sif.webservice.inline.invoke", string4);
            if (this.isDebugOn) {
                final LogFile llf7 = this.lLF;
                final String s4 = "Entering BusinessEvent setting\n";
                final LogFile llf8 = this.lLF;
                llf7.writeln(s4, 1);
            }
            businessEvent.setStringProperty("SERVICE_WSDL_URL", string);
            businessEvent.setStringProperty("SERVICE_NAME", str2);
            businessEvent.setStringProperty("SERVICE_PORT", str5);
            businessEvent.setStringProperty("SERVICE_PORTTYPE", str3);
            businessEvent.setStringProperty("SERVICE_OPERATION", str4);
            if (this.isDebugOn) {
                final LogFile llf9 = this.lLF;
                final String string5 = "wsdlURL: " + string + "\n";
                final LogFile llf10 = this.lLF;
                llf9.writeln(string5, 1);
                final LogFile llf11 = this.lLF;
                final String string6 = "serviceName: " + str2 + "\n";
                final LogFile llf12 = this.lLF;
                llf11.writeln(string6, 1);
                final LogFile llf13 = this.lLF;
                final String string7 = "servicePort: " + str5 + "\n";
                final LogFile llf14 = this.lLF;
                llf13.writeln(string7, 1);
                final LogFile llf15 = this.lLF;
                final String string8 = "servicePortType: " + str3 + "\n";
                final LogFile llf16 = this.lLF;
                llf15.writeln(string8, 1);
                final LogFile llf17 = this.lLF;
                final String string9 = "serviceOperation: " + str4 + "\n";
                final LogFile llf18 = this.lLF;
                llf17.writeln(string9, 1);
            }
            if (this.proxyHost != null && this.proxyHost.length() > 0) {
                System.setProperty("http.proxyHost ", this.proxyHost);
                if (this.isDebugOn) {
                    final LogFile llf19 = this.lLF;
                    final String string10 = "ProxyHost is  " + this.proxyHost + "\n";
                    final LogFile llf20 = this.lLF;
                    llf19.writeln(string10, 1);
                }
            }
            if (this.proxyPort != null && this.proxyPort.length() > 0) {
                System.setProperty("http.proxyPort", this.proxyPort);
                if (this.isDebugOn) {
                    final LogFile llf21 = this.lLF;
                    final String string11 = "ProxyPort is  " + this.proxyPort + "\n";
                    final LogFile llf22 = this.lLF;
                    llf21.writeln(string11, 1);
                }
            }
            businessEvent.setData(s);
            if (this.isDebugOn) {
                final LogFile llf23 = this.lLF;
                final String string12 = "Raising SIF Event oracle.apps.sif.webservice.inline.invoke with event key " + string4 + "\n";
                final LogFile llf24 = this.lLF;
                llf23.writeln(string12, 1);
            }
            try {
                businessEvent.raise(this.lCpContext.getJDBCConnection());
                s2 = (String)businessEvent.getResponseData();
            }
            catch (BusinessEventException ex) {
                final LogFile llf25 = this.lLF;
                final String s5 = "Got Exception Invoking SIF event oracle.apps.sif.webservice.inline.invoke \n";
                final LogFile llf26 = this.lLF;
                llf25.writeln(s5, 1);
                final LogFile llf27 = this.lLF;
                final String string13 = "Exception is e.getLinkedException().getMessage() \n\n" + ex.getLinkedException().getMessage() + "\n";
                final LogFile llf28 = this.lLF;
                llf27.writeln(string13, 1);
            }
            if (this.isDebugOn) {
                final LogFile llf29 = this.lLF;
                final String s6 = "Got response from Sendtxn flow \n";
                final LogFile llf30 = this.lLF;
                llf29.writeln(s6, 1);
            }
        }
        catch (Exception ex2) {
            final LogFile llf31 = this.lLF;
            final String string14 = "Exception is " + ex2.toString() + "\n";
            final LogFile llf32 = this.lLF;
            llf31.writeln(string14, 1);
        }
        return s2;
    }
    
    public String getEndpoint() {
        return this._endpoint;
    }
    
    public void setEndpoint(final String endpoint) {
        this._endpoint = endpoint;
    }
    
    public String callWebService(final String s) throws Exception {
        String sendDocumentViaSIF = null;
        try {
            final LogFile llf = this.lLF;
            final String string = "Endpoint " + this._endpoint;
            final LogFile llf2 = this.lLF;
            llf.writeln(string, 1);
            final String trim = s.trim();
            final LogFile llf3 = this.lLF;
            final String s2 = " Calling sendDocumentViaSIF ";
            final LogFile llf4 = this.lLF;
            llf3.writeln(s2, 1);
            sendDocumentViaSIF = this.sendDocumentViaSIF(trim, this._endpoint);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return sendDocumentViaSIF;
    }
}