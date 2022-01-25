package oracle.apps.wsh.outbound.cp.request;

import oracle.apps.fnd.cp.request.RequestSubmissionException;
import oracle.apps.fnd.cp.request.ConcurrentRequest;
import java.util.Arrays;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import java.sql.SQLException;
import oracle.jbo.domain.Number;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import java.sql.ResultSet;
import oracle.apps.fnd.util.NameValueType;
import oracle.apps.fnd.util.ParameterList;
import oracle.apps.fnd.cp.request.CpContext;
import java.util.Vector;
import java.sql.Connection;
import oracle.apps.fnd.cp.request.ReqCompletion;
import oracle.apps.fnd.cp.request.OutFile;
import oracle.apps.fnd.cp.request.LogFile;
import oracle.jdbc.OraclePreparedStatement;
import oracle.apps.fnd.cp.request.JavaConcurrentProgram;

public class OutboundInterface implements JavaConcurrentProgram
{
    public static final String RCS_ID = "$Header: OutboundInterface.java 120.9.12020000.8 2017/07/18 14:06:39 sunilku ship $";
    String applName;
    private OraclePreparedStatement m_pSelStmt;
    private LogFile lLF;
    private OutFile lOF;
    private ReqCompletion lRC;
    private Connection mJConn;
    private int maxLength;
    public static Vector notsentIdList;
    
    public OutboundInterface() {
        this.m_pSelStmt = null;
        this.lLF = null;
        this.lOF = null;
        this.lRC = null;
        this.mJConn = null;
        this.maxLength = 32000;
        this.applName = "WSH";
    }
    
    public void runProgram(final CpContext cpContext) {
        String value = null;
        String value2 = null;
        String value3 = null;
        String value4 = null;
        String value5 = null;
        String value6 = null;
        String value7 = null;
        final String s = null;
        final String s2 = null;
        String value8 = null;
        String value9 = null;
        String value10 = null;
        String value11 = null;
        String value12 = null;
        String value13 = null;
        String value14 = null;
        String value15 = null;
        int n = 1;
        final int n2 = 4000;
        final Vector<Integer> vector = new Vector<Integer>();
        final Vector<Integer> vector2 = new Vector<Integer>();
        final Vector<String> vector3 = new Vector<String>();
        final Vector<Integer> vector4 = new Vector<Integer>();
        final Vector<Object> vector5 = new Vector<Object>();
        final Vector<String> vector6 = new Vector<String>();
        final Vector<String> vector7 = new Vector<String>();
        final Vector<Object> vector8 = new Vector<Object>();
        final Vector<Object> vector9 = new Vector<Object>();
        final Vector<Integer> vector10 = new Vector<Integer>();
        int int1 = 1;
        int int2 = 1;
        boolean b = true;
        Vector<Object> callDeliverySplitter = new Vector<Object>();
        this.mJConn = cpContext.getJDBCConnection();
        final ParameterList parameterList = cpContext.getParameterList();
        this.lRC = cpContext.getReqCompletion();
        this.lLF = cpContext.getLogFile();
        this.lOF = cpContext.getOutFile();
        try {
            final String reqData = cpContext.getReqDetails().getGeneralInfo().getReqData();
            if (reqData == null) {
                if (!this.isOTMInstalled()) {
                    final LogFile llf = this.lLF;
                    final String s3 = " OTM is not installed, hence cannot run this concurrent program";
                    final LogFile llf2 = this.lLF;
                    llf.writeln(s3, 4);
                    this.lOF.writeln("OTM is not installed");
                    this.lRC.setCompletion(2, "OTM is not installed ");
                    b = false;
                }
                if (!this.checkProfiles(cpContext)) {
                    this.lRC.setCompletion(2, "Profile option values are not set up properly");
                    b = false;
                }
                if (b) {
                    while (parameterList.hasMoreElements()) {
                        final NameValueType nextParameter = parameterList.nextParameter();
                        if (nextParameter.getName().equals("INTERFACE_ACTION")) {
                            value = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("INTERFACE_BASIS")) {
                            value2 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("AUTO_SPLIT_DELIVERIES")) {
                            value3 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("SHIP_FROM_ORGANIZATION")) {
                            value15 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("TRIP_NAME_LOW")) {
                            value4 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("TRIP_NAME_HIGH")) {
                            value5 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("DELIVERY_NAME_LOW")) {
                            value6 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("DELIVERY_NAME_HIGH")) {
                            value7 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("BATCH_NAME_LOW")) {
                            value8 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("BATCH_NAME_HIGH")) {
                            value9 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("WSH_BATCH_CREATE_DATE_LOW")) {
                            value13 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("WSH_BATCH_CREATE_DATE_HIGH")) {
                            value14 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("PICK_UP_DATE_LOW")) {
                            value11 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("PICK_UP_DATE_HIGH")) {
                            value12 = nextParameter.getValue();
                        }
                        if (nextParameter.getName().equals("BATCH_SIZE") && nextParameter.getValue() != null && !nextParameter.getValue().equals("")) {
                            int1 = Integer.parseInt(nextParameter.getValue());
                        }
                        if (nextParameter.getName().equals("NO_OF_CHILD_REQUESTS") && nextParameter.getValue() != null && !nextParameter.getValue().equals("")) {
                            int2 = Integer.parseInt(nextParameter.getValue());
                        }
                        if (nextParameter.getName().equals("CLIENT_ID") && nextParameter.getValue() != null && !nextParameter.getValue().equals("")) {
                            value10 = nextParameter.getValue();
                        }
                    }
                    final LogFile llf3 = this.lLF;
                    final String string = "Interface Action : " + value;
                    final LogFile llf4 = this.lLF;
                    llf3.writeln(string, 1);
                    final LogFile llf5 = this.lLF;
                    final String string2 = "Interface Basis : " + value2;
                    final LogFile llf6 = this.lLF;
                    llf5.writeln(string2, 1);
                    final LogFile llf7 = this.lLF;
                    final String string3 = "autoSplitDlvFlag : " + value3;
                    final LogFile llf8 = this.lLF;
                    llf7.writeln(string3, 1);
                    final LogFile llf9 = this.lLF;
                    final String string4 = "shipFromOrganizationId : " + value15;
                    final LogFile llf10 = this.lLF;
                    llf9.writeln(string4, 1);
                    final LogFile llf11 = this.lLF;
                    final String string5 = "clientId : " + value10;
                    final LogFile llf12 = this.lLF;
                    llf11.writeln(string5, 1);
                    final LogFile llf13 = this.lLF;
                    final String string6 = "tripNameHigh  : " + value5;
                    final LogFile llf14 = this.lLF;
                    llf13.writeln(string6, 1);
                    final LogFile llf15 = this.lLF;
                    final String string7 = "tripNameLow  : " + value4;
                    final LogFile llf16 = this.lLF;
                    llf15.writeln(string7, 1);
                    final LogFile llf17 = this.lLF;
                    final String string8 = "dlvNameLow  : " + value6;
                    final LogFile llf18 = this.lLF;
                    llf17.writeln(string8, 1);
                    final LogFile llf19 = this.lLF;
                    final String string9 = "dlvNameHigh  : " + value7;
                    final LogFile llf20 = this.lLF;
                    llf19.writeln(string9, 1);
                    final LogFile llf21 = this.lLF;
                    final String string10 = "batchNameLow  : " + value8;
                    final LogFile llf22 = this.lLF;
                    llf21.writeln(string10, 1);
                    final LogFile llf23 = this.lLF;
                    final String string11 = "batchNameHigh  : " + value9;
                    final LogFile llf24 = this.lLF;
                    llf23.writeln(string11, 1);
                    final LogFile llf25 = this.lLF;
                    final String string12 = "BatchCreateDateLow  : " + value13;
                    final LogFile llf26 = this.lLF;
                    llf25.writeln(string12, 1);
                    final LogFile llf27 = this.lLF;
                    final String string13 = "BatchCreateDateHigh  : " + value14;
                    final LogFile llf28 = this.lLF;
                    llf27.writeln(string13, 1);
                    final LogFile llf29 = this.lLF;
                    final String string14 = "pickUpDateLow  : " + value11;
                    final LogFile llf30 = this.lLF;
                    llf29.writeln(string14, 1);
                    final LogFile llf31 = this.lLF;
                    final String string15 = "pickUpDateHigh  : " + value12;
                    final LogFile llf32 = this.lLF;
                    llf31.writeln(string15, 1);
                    final LogFile llf33 = this.lLF;
                    final String string16 = "Number of Transactions per batch : " + int1;
                    final LogFile llf34 = this.lLF;
                    llf33.writeln(string16, 1);
                    final LogFile llf35 = this.lLF;
                    final String string17 = "noOfChilds  : " + int2;
                    final LogFile llf36 = this.lLF;
                    llf35.writeln(string17, 1);
                    final StringBuffer sb = new StringBuffer();
                    final Vector<String> vector11 = new Vector<String>();
                    if (value.equals("ACTUAL_SHIPMENT")) {
                        final LogFile llf37 = this.lLF;
                        final String s4 = " Inside sending actual shipment ";
                        final LogFile llf38 = this.lLF;
                        llf37.writeln(s4, 1);
                        if (value10 != null && !value10.equals("")) {
                            sb.append(" and wnd.client_id = :" + n++);
                            vector11.add(value10);
                        }
                        if (value4 != null && !value4.equals("")) {
                            sb.append(" and wt.name >= :" + n++);
                            vector11.add(value4);
                        }
                        if (value5 != null && !value5.equals("")) {
                            sb.append(" and wt.name <= :" + n++);
                            vector11.add(value5);
                        }
                        if (value11 != null && !value11.equals("")) {
                            final LogFile llf39 = this.lLF;
                            final String s5 = "Parameter Pick Up Date Low is not applicable when Interface Action is 'Actual Shipment'";
                            final LogFile llf40 = this.lLF;
                            llf39.writeln(s5, 1);
                        }
                        if (value12 != null && !value12.equals("")) {
                            final LogFile llf41 = this.lLF;
                            final String s6 = "Parameter Pick Up Date High is not applicable when Interface Action is 'Actual Shipment'";
                            final LogFile llf42 = this.lLF;
                            llf41.writeln(s6, 1);
                        }
                        if (value13 != null && !value13.equals("")) {
                            final LogFile llf43 = this.lLF;
                            final String s7 = "Parameter Batch create Date Low is not applicable when Interface Action is 'Actual Shipment'";
                            final LogFile llf44 = this.lLF;
                            llf43.writeln(s7, 1);
                        }
                        if (value14 != null && !value14.equals("")) {
                            final LogFile llf45 = this.lLF;
                            final String s8 = "Parameter Batch create Date high is not applicable when Interface Action is 'Actual Shipment'";
                            final LogFile llf46 = this.lLF;
                            llf45.writeln(s8, 1);
                        }
                        this.getTripStops(sb, vector11, value15, vector8);
                        final LogFile llf47 = this.lLF;
                        final String string18 = "No of Trips Picked up for processing : " + vector8.size();
                        final LogFile llf48 = this.lLF;
                        llf47.writeln(string18, 1);
                        for (int i = 0; i < vector8.size(); ++i) {
                            final LogFile llf49 = this.lLF;
                            final String string19 = vector8.elementAt(i) + "  ";
                            final LogFile llf50 = this.lLF;
                            llf49.write(string19, 1);
                        }
                        if (vector8.size() > 0) {
                            this.updateInterfaceFlag(cpContext, vector8, vector9, "TRIP", "IN_PROCESS");
                            final LogFile llf51 = this.lLF;
                            final String string20 = "\nTrips rejected in updateInterfaceFlag method : " + vector9.size();
                            final LogFile llf52 = this.lLF;
                            llf51.writeln(string20, 1);
                            for (int j = 0; j < vector9.size(); ++j) {
                                final LogFile llf53 = this.lLF;
                                final String string21 = vector9.elementAt(j) + "  ";
                                final LogFile llf54 = this.lLF;
                                llf53.write(string21, 1);
                            }
                            final LogFile llf55 = this.lLF;
                            final String string22 = "\nSo the final number of Trips picked up for processing : " + vector8.size();
                            final LogFile llf56 = this.lLF;
                            llf55.writeln(string22, 1);
                            for (int k = 0; k < vector8.size(); ++k) {
                                final LogFile llf57 = this.lLF;
                                final String string23 = vector8.elementAt(k) + "  ";
                                final LogFile llf58 = this.lLF;
                                llf57.write(string23, 1);
                            }
                        }
                        if (vector8.size() > 0) {
                            this.BatchRecords(vector8, "TRIP", int1, int2, "", "", value4, value5);
                            this.lRC.setPaused("1");
                        }
                        else {
                            this.lRC.setCompletion(1, "No records were picked up for processing");
                            this.lOF.writeln("None of the deliveries/trips were picked up for processing");
                        }
                    }
                    else {
                        final LogFile llf59 = this.lLF;
                        final String s9 = " Inside sending not actual shipment ";
                        final LogFile llf60 = this.lLF;
                        llf59.writeln(s9, 1);
                        final StringBuffer sb2 = new StringBuffer();
                        if ("D".equals(value2)) {
                            final LogFile llf61 = this.lLF;
                            final String s10 = " Sending delivery ";
                            final LogFile llf62 = this.lLF;
                            llf61.writeln(s10, 1);
                            sb2.append("select distinct wnd.delivery_id, wnd.name, ");
                            sb2.append(" wnd.organization_id, wnd.gross_weight, ");
                            sb2.append(" wnd.weight_uom_code, wnd.planned_flag, wnd.initial_pickup_location_id  from ");
                            sb2.append(" wsh_new_deliveries wnd ");
                            sb2.append(" where");
                            sb2.append(" ((wnd.tms_interface_flag in ( ");
                            final char char1 = value.charAt(0);
                            int n3 = 0;
                            switch (char1) {
                                case 67: {
                                    sb2.append("'CR','CP')");
                                    sb2.append("  and nvl(wnd.ignore_for_planning,'N') = 'N'))");
                                    sb2.append(" and wnd.status_code = 'OP' ");
                                    break;
                                }
                                case 85: {
                                    sb2.append("'UR','UP')");
                                    sb2.append("   and nvl(wnd.ignore_for_planning,'N') = 'N'))");
                                    sb2.append(" and wnd.status_code = 'OP' ");
                                    break;
                                }
                                case 68: {
                                    sb2.append("'DR','DP')))");
                                    break;
                                }
                                case 82: {
                                    sb2.append("'CR','UR','CP','UP')");
                                    sb2.append("  and nvl(wnd.ignore_for_planning,'N') = 'N'))");
                                    sb2.append(" and wnd.status_code = 'OP' ");
                                    break;
                                }
                                case 65: {
                                    sb2.append("'CR','UR','CP','UP') and nvl(wnd.ignore_for_planning,'N') = 'N' and wnd.status_code = 'OP'  ) ");
                                    sb2.append(" OR (wnd.tms_interface_flag in ('DP', 'DR' ))) ");
                                    break;
                                }
                            }
                            if (value10 != null && !value10.equals("")) {
                                sb.append(" and wnd.client_id = :" + ++n3);
                                vector11.add(value10);
                            }
                            if (value6 != null && !value6.equals("")) {
                                sb.append(" and wnd.name >= :" + ++n3);
                                vector11.add(value6);
                            }
                            if (value7 != null && !value7.equals("")) {
                                sb.append(" and wnd.name <= :" + ++n3);
                                vector11.add(value7);
                            }
                            if (value11 != null && !value11.equals("")) {
                                sb.append("and  initial_pickup_date >= to_date(:" + ++n3 + ",'DD-MON-YYYY HH24:MI:SS') ");
                                vector11.add(value11);
                            }
                            if (value12 != null && !value12.equals("")) {
                                sb.append(" and initial_pickup_date <= to_date(:" + ++n3 + ",'DD-MON-YYYY HH24:MI:SS') ");
                                vector11.add(value12.substring(0, 11) + "23:59:59");
                            }
                            if (value15 != null && !value15.equals("")) {
                                sb.append(" and wnd.organization_id = :" + ++n3);
                            }
                            final String string24 = sb2.append((Object)sb + " order by wnd.delivery_id").toString();
                            final LogFile llf63 = this.lLF;
                            final String string25 = "Release Query : " + string24;
                            final LogFile llf64 = this.lLF;
                            llf63.writeln(string25, 1);
                            (this.m_pSelStmt = (OraclePreparedStatement)this.mJConn.prepareStatement(string24)).defineColumnType(1, 4);
                            this.m_pSelStmt.defineColumnType(2, 12, n2);
                            this.m_pSelStmt.defineColumnType(3, 4);
                            this.m_pSelStmt.defineColumnType(4, 8);
                            this.m_pSelStmt.defineColumnType(5, 12, n2);
                            this.m_pSelStmt.defineColumnType(6, 12, n2);
                            this.m_pSelStmt.defineColumnType(7, 4);
                            int l;
                            for (l = 0; l < vector11.size(); ++l) {
                                this.m_pSelStmt.setString(l + 1, (String)vector11.elementAt(l));
                            }
                            if (value15 != null && !value15.equals("")) {
                                this.m_pSelStmt.setInt(l + 1, Integer.parseInt(value15));
                            }
                            final ResultSet executeQuery = this.m_pSelStmt.executeQuery();
                            while (executeQuery.next()) {
                                vector.addElement(new Integer(executeQuery.getInt(1)));
                                vector3.addElement(executeQuery.getString(2));
                                vector4.addElement(new Integer(executeQuery.getInt(3)));
                                vector5.addElement(executeQuery.getObject(4));
                                vector6.addElement(executeQuery.getString(5));
                                vector7.addElement(executeQuery.getString(6));
                                vector10.addElement(new Integer(executeQuery.getInt(7)));
                            }
                            this.m_pSelStmt.close();
                            final LogFile llf65 = this.lLF;
                            final String string26 = "Number of deliveries picked up for processing : " + vector.size();
                            final LogFile llf66 = this.lLF;
                            llf65.writeln(string26, 1);
                            for (int index = 0; index < vector.size(); ++index) {
                                final LogFile llf67 = this.lLF;
                                final String string27 = vector.elementAt(index) + " ";
                                final LogFile llf68 = this.lLF;
                                llf67.write(string27, 1);
                            }
                            if (vector.size() > 0) {
                                callDeliverySplitter = (Vector<Object>)this.callDeliverySplitter(vector, vector3, vector4, vector5, vector6, vector7, vector10, vector9, value3);
                                final LogFile llf69 = this.lLF;
                                final String string28 = "\nDeliveries  rejected in callDeliverySplitter method : " + vector9.size();
                                final LogFile llf70 = this.lLF;
                                llf69.writeln(string28, 1);
                                for (int index2 = 0; index2 < vector9.size(); ++index2) {
                                    final LogFile llf71 = this.lLF;
                                    final String string29 = vector9.elementAt(index2) + "  ";
                                    final LogFile llf72 = this.lLF;
                                    llf71.write(string29, 1);
                                }
                                if (callDeliverySplitter.size() > 0) {
                                    this.updateInterfaceFlag(cpContext, callDeliverySplitter, vector9, "DELIVERY", "IN_PROCESS");
                                    final LogFile llf73 = this.lLF;
                                    final String string30 = "\nDeliveries  rejected in updateInterfaceFlag method : " + vector9.size();
                                    final LogFile llf74 = this.lLF;
                                    llf73.writeln(string30, 1);
                                    for (int index3 = 0; index3 < vector9.size(); ++index3) {
                                        final LogFile llf75 = this.lLF;
                                        final String string31 = vector9.elementAt(index3) + "  ";
                                        final LogFile llf76 = this.lLF;
                                        llf75.write(string31, 1);
                                    }
                                }
                                final LogFile llf77 = this.lLF;
                                final String string32 = "\nSo the Final Number of deliveries picked up for processing : " + callDeliverySplitter.size();
                                final LogFile llf78 = this.lLF;
                                llf77.writeln(string32, 1);
                                for (int index4 = 0; index4 < callDeliverySplitter.size(); ++index4) {
                                    final LogFile llf79 = this.lLF;
                                    final String string33 = callDeliverySplitter.elementAt(index4) + " ";
                                    final LogFile llf80 = this.lLF;
                                    llf79.write(string33, 1);
                                }
                            }
                            if (callDeliverySplitter.size() > 0) {
                                this.BatchRecords(callDeliverySplitter, "DELIVERY", int1, int2, value10, value15, value6, value7);
                                if (vector9.size() > 0) {
                                    this.lRC.setPaused("ContainsRejectedDeliveries");
                                }
                                else {
                                    this.lRC.setPaused("1");
                                }
                            }
                            else {
                                this.lRC.setCompletion(1, "No records were picked up for processing");
                                this.lOF.writeln("None of the deliveries/trips were picked up for processing");
                            }
                        }
                        else if ("B".equals(value2)) {
                            final LogFile llf81 = this.lLF;
                            final String s11 = " Sending Batch ";
                            final LogFile llf82 = this.lLF;
                            llf81.writeln(s11, 1);
                            sb2.append("select distinct wtsb.tms_sub_batch_id, wtsb.name from ");
                            sb2.append(" wsh_tms_sub_batches wtsb, wsh_tms_batches wtb ");
                            sb2.append(" where wtb.tms_batch_id = wtsb.tms_batch_id ");
                            sb2.append(" and wtsb.tms_interface_flag in ( ");
                            final char char2 = value.charAt(0);
                            int n4 = 0;
                            switch (char2) {
                                case 67: {
                                    sb2.append("'CR','CP')");
                                    break;
                                }
                                case 85: {
                                    sb2.append("'UR','UP','RC','RP')");
                                    break;
                                }
                                case 68: {
                                    sb2.append("'DR','DP')");
                                    break;
                                }
                                case 82: {
                                    sb2.append("'CR','UR','CP','UP','RC','RP')");
                                    break;
                                }
                                case 65: {
                                    sb2.append("'CR','UR','CP','UP','DP','DR','RC','RP')");
                                    break;
                                }
                            }
                            if (value8 != null && !value8.equals("")) {
                                sb.append(" and wtb.name >= :" + ++n4);
                                vector11.add(value8);
                            }
                            if (value9 != null && !value9.equals("")) {
                                sb.append(" and wtb.name <= :" + ++n4);
                                vector11.add(value9);
                            }
                            if (value13 != null && !value13.equals("")) {
                                sb.append(" and  wtb.creation_date >= to_date(:" + ++n4 + ",'DD-MON-YYYY HH24:MI:SS') ");
                                vector11.add(value13);
                            }
                            if (value14 != null && !value14.equals("")) {
                                sb.append(" and wtb.creation_date <= to_date(:" + ++n4 + ",'DD-MON-YYYY HH24:MI:SS') ");
                                vector11.add(value14.substring(0, 11) + "23:59:59");
                            }
                            final String string34 = sb2.append((Object)sb + " order by wtsb.tms_sub_batch_id").toString();
                            final LogFile llf83 = this.lLF;
                            final String string35 = "Release Query : " + string34;
                            final LogFile llf84 = this.lLF;
                            llf83.writeln(string35, 1);
                            (this.m_pSelStmt = (OraclePreparedStatement)this.mJConn.prepareStatement(string34)).defineColumnType(1, 4);
                            this.m_pSelStmt.defineColumnType(2, 12, n2);
                            for (int index5 = 0; index5 < vector11.size(); ++index5) {
                                this.m_pSelStmt.setString(index5 + 1, (String)vector11.elementAt(index5));
                            }
                            final ResultSet executeQuery2 = this.m_pSelStmt.executeQuery();
                            while (executeQuery2.next()) {
                                vector2.addElement(new Integer(executeQuery2.getInt(1)));
                            }
                            this.m_pSelStmt.close();
                            final LogFile llf85 = this.lLF;
                            final String string36 = "Number of batches picked up for processing : " + vector2.size();
                            final LogFile llf86 = this.lLF;
                            llf85.writeln(string36, 1);
                            for (int index6 = 0; index6 < vector2.size(); ++index6) {
                                final LogFile llf87 = this.lLF;
                                final String string37 = vector2.elementAt(index6) + " ";
                                final LogFile llf88 = this.lLF;
                                llf87.write(string37, 1);
                            }
                            if (vector2.size() > 0) {
                                this.updateInterfaceFlag(cpContext, vector2, vector9, "BATCH", "IN_PROCESS");
                                final LogFile llf89 = this.lLF;
                                final String string38 = "\nBatches  rejected in updateInterfaceFlag method : " + vector9.size();
                                final LogFile llf90 = this.lLF;
                                llf89.writeln(string38, 1);
                                for (int index7 = 0; index7 < vector9.size(); ++index7) {
                                    final LogFile llf91 = this.lLF;
                                    final String string39 = vector9.elementAt(index7) + "  ";
                                    final LogFile llf92 = this.lLF;
                                    llf91.write(string39, 1);
                                }
                                final LogFile llf93 = this.lLF;
                                final String string40 = "\nSo the Final Number of batches picked up for processing : " + vector2.size();
                                final LogFile llf94 = this.lLF;
                                llf93.writeln(string40, 1);
                                for (int index8 = 0; index8 < vector2.size(); ++index8) {
                                    final LogFile llf95 = this.lLF;
                                    final String string41 = vector2.elementAt(index8) + " ";
                                    final LogFile llf96 = this.lLF;
                                    llf95.write(string41, 1);
                                }
                            }
                            if (vector2.size() > 0) {
                                this.BatchRecords(vector2, "BATCH", int1, int2, value10, value15, s, s2);
                                this.lRC.setPaused("1");
                            }
                            else {
                                this.lRC.setCompletion(1, "No records were picked up for processing");
                                this.lOF.writeln("None of the deliveries/trips were picked up for processing");
                            }
                        }
                    }
                }
            }
            else {
                int n5 = -1;
                final StringBuffer sb3 = new StringBuffer();
                sb3.append(" select request_id , status_code, ");
                sb3.append(" completion_text from fnd_concurrent_requests");
                sb3.append(" where parent_request_id = ");
                sb3.append(cpContext.getReqDetails().getRequestId());
                (this.m_pSelStmt = (OraclePreparedStatement)this.mJConn.prepareStatement(sb3.toString())).defineColumnType(1, 4);
                this.m_pSelStmt.defineColumnType(2, 12, n2);
                this.m_pSelStmt.defineColumnType(3, 12, n2);
                final ResultSet executeQuery3 = this.m_pSelStmt.executeQuery();
                while (executeQuery3.next()) {
                    final int int3 = executeQuery3.getInt(1);
                    final String string42 = executeQuery3.getString(2);
                    final String string43 = executeQuery3.getString(3);
                    this.lOF.writeln("Child request with " + int3 + " completed with status " + this.getStatus(int3));
                    final LogFile llf97 = this.lLF;
                    final String string44 = int3 + " : " + string43;
                    final LogFile llf98 = this.lLF;
                    llf97.writeln(string44, 1);
                    final LogFile llf99 = this.lLF;
                    final String string45 = "statusCode  : " + string42;
                    final LogFile llf100 = this.lLF;
                    llf99.writeln(string45, 1);
                    final LogFile llf101 = this.lLF;
                    final String string46 = "parentStatus  : " + n5;
                    final LogFile llf102 = this.lLF;
                    llf101.writeln(string46, 1);
                    if (string42.equals("E") && (n5 != 1 || n5 != 0)) {
                        n5 = 2;
                    }
                    else if (string42.equals("E") && (n5 == 1 || n5 == 0)) {
                        n5 = 1;
                    }
                    else if (string42.equals("G")) {
                        n5 = 1;
                    }
                    else if (string42.equals("C") && (n5 == 1 || n5 == 2)) {
                        n5 = 1;
                    }
                    else {
                        if (!string42.equals("C")) {
                            continue;
                        }
                        n5 = 0;
                    }
                }
                this.m_pSelStmt.close();
                final LogFile llf103 = this.lLF;
                final String string47 = "parentStatus based on Child requests : " + n5;
                final LogFile llf104 = this.lLF;
                llf103.writeln(string47, 1);
                if (reqData.equals("ContainsRejectedDeliveries") && n5 == 0) {
                    n5 = 1;
                }
                this.lRC.setCompletion(n5, "Request Completed Normal");
            }
            this.mJConn.commit();
        }
        catch (Exception ex) {
            this.lRC.setCompletion(2, ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            cpContext.releaseJDBCConnection();
            try {
                if (this.m_pSelStmt != null) {
                    this.m_pSelStmt.close();
                }
            }
            catch (Exception ex2) {}
        }
    }
    
    void updateInterfaceFlag(final CpContext cpContext, final Vector vector, final Vector vector2, final String str, final String s) {
        OracleCallableStatement oracleCallableStatement = null;
        final Object[] array = new Object[vector.size()];
        final LogFile llf = this.lLF;
        final String string = "In the method updateInterfaceFlag for entity " + str;
        final LogFile llf2 = this.lLF;
        llf.writeln(string, 4);
        for (int i = 0; i < vector.size(); ++i) {
            array[i] = vector.elementAt(i);
        }
        try {
            final ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("WSH_OTM_ID_TAB", this.mJConn);
            final ARRAY array2 = new ARRAY(descriptor, this.mJConn, (Object)array);
            final ARRAY array3 = new ARRAY(descriptor, this.mJConn, (Object)null);
            final StringBuffer sb = new StringBuffer();
            sb.append(" BEGIN WSH_OTM_OUTBOUND.UPDATE_ENTITY_INTF_STATUS( ");
            sb.append("p_entity_type => :1,");
            sb.append("p_new_intf_status => :2,");
            sb.append("p_entity_id_tab => :3,");
            sb.append("p_error_id_tab => :4,");
            sb.append("x_return_status =>:5);");
            sb.append("END;");
            oracleCallableStatement = (OracleCallableStatement)this.mJConn.prepareCall(sb.toString());
            oracleCallableStatement.setString(1, str);
            oracleCallableStatement.setString(2, s);
            oracleCallableStatement.setARRAY(3, array2);
            oracleCallableStatement.setARRAY(4, array3);
            oracleCallableStatement.registerOutParameter(3, 2003, "WSH_OTM_ID_TAB");
            oracleCallableStatement.registerOutParameter(4, 2003, "WSH_OTM_ID_TAB");
            oracleCallableStatement.registerOutParameter(5, 12, this.maxLength);
            oracleCallableStatement.executeUpdate();
            this.mJConn.commit();
            oracleCallableStatement.getARRAY(3);
            final ARRAY array4 = oracleCallableStatement.getARRAY(4);
            final String string2 = oracleCallableStatement.getString(5);
            final LogFile llf3 = this.lLF;
            final String string3 = "Return Status from the api WSH_OTM_OUTBOUND.UPDATE_ENTITY_INTF_STATUS : " + string2;
            final LogFile llf4 = this.lLF;
            llf3.writeln(string3, 1);
            if (string2.equals("S")) {
                final Object[] array5 = (Object[])array4.getArray();
                for (int j = 0; j < array5.length; ++j) {
                    final Object obj = array5[j];
                    final LogFile llf5 = this.lLF;
                    final String string4 = "idList size before removing element" + vector.size();
                    final LogFile llf6 = this.lLF;
                    llf5.writeln(string4, 1);
                    vector.removeElement(new Number(obj));
                    final LogFile llf7 = this.lLF;
                    final String string5 = "idList  size after removing element" + vector.size();
                    final LogFile llf8 = this.lLF;
                    llf7.writeln(string5, 1);
                    vector2.addElement(obj);
                    try {
                        final LogFile llf9 = this.lLF;
                        final String string6 = "Error Entity  Ids" + (String)obj;
                        final LogFile llf10 = this.lLF;
                        llf9.writeln(string6, 1);
                    }
                    catch (Exception ex2) {
                        if (obj != null) {
                            final LogFile llf11 = this.lLF;
                            final String string7 = "Error Entity  Id s" + obj.toString();
                            final LogFile llf12 = this.lLF;
                            llf11.writeln(string7, 1);
                        }
                        else {
                            final LogFile llf13 = this.lLF;
                            final String s2 = "Error Entity  Id is null ";
                            final LogFile llf14 = this.lLF;
                            llf13.writeln(s2, 1);
                        }
                    }
                }
            }
            else {
                vector.removeAllElements();
                final LogFile llf15 = this.lLF;
                final String string8 = "Removed all elements from idList , size :" + vector.size();
                final LogFile llf16 = this.lLF;
                llf15.writeln(string8, 4);
            }
            oracleCallableStatement.close();
        }
        catch (SQLException ex) {
            vector.removeAllElements();
            final LogFile llf17 = this.lLF;
            final String string9 = "SQL Exception occured in method updateInterfaceFlag method " + ex.toString();
            final LogFile llf18 = this.lLF;
            llf17.writeln(string9, 4);
            this.lRC.setCompletion(2, ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                if (oracleCallableStatement != null) {
                    oracleCallableStatement.close();
                }
            }
            catch (Exception ex3) {}
        }
    }
    
    String getStatus(final int i) {
        String string = null;
        OracleCallableStatement oracleCallableStatement = null;
        final LogFile llf = this.lLF;
        final String string2 = "In the method getStatus  for request Id " + i;
        final LogFile llf2 = this.lLF;
        llf.writeln(string2, 4);
        try {
            final StringBuffer sb = new StringBuffer();
            sb.append("declare b boolean; retval varchar2(1); ");
            sb.append(" BEGIN b := FND_CONCURRENT.get_request_status( ");
            sb.append("request_id => :1,");
            sb.append("phase => :2,");
            sb.append("status => :3,");
            sb.append("dev_phase => :4,");
            sb.append("dev_status => :5,");
            sb.append("message =>:6);");
            sb.append(" if ( b ) then  retval := 'T'; else retval := 'F'; ");
            sb.append(" end if; :7 := retval; END; ");
            oracleCallableStatement = (OracleCallableStatement)this.mJConn.prepareCall(sb.toString());
            oracleCallableStatement.setInt(1, i);
            oracleCallableStatement.registerOutParameter(2, 12, this.maxLength);
            oracleCallableStatement.registerOutParameter(3, 12, this.maxLength);
            oracleCallableStatement.registerOutParameter(4, 12, this.maxLength);
            oracleCallableStatement.registerOutParameter(5, 12, this.maxLength);
            oracleCallableStatement.registerOutParameter(6, 12, this.maxLength);
            oracleCallableStatement.registerOutParameter(7, 12, this.maxLength);
            oracleCallableStatement.executeQuery();
            string = oracleCallableStatement.getString(3);
            final LogFile llf3 = this.lLF;
            final String string3 = " Status from the api FND_CONCURRENT.get_request_status : " + string;
            final LogFile llf4 = this.lLF;
            llf3.writeln(string3, 1);
            oracleCallableStatement.close();
        }
        catch (SQLException ex) {
            final LogFile llf5 = this.lLF;
            final String string4 = "SQL Exception occured in method getStatus method " + ex.toString();
            final LogFile llf6 = this.lLF;
            llf5.writeln(string4, 4);
            this.lRC.setCompletion(2, ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                if (oracleCallableStatement != null) {
                    oracleCallableStatement.close();
                }
            }
            catch (Exception ex2) {}
        }
        return string;
    }
    
    Vector callDeliverySplitter(final Vector vector, final Vector vector2, final Vector vector3, final Vector vector4, final Vector vector5, final Vector vector6, final Vector vector7, final Vector vector8, final String s) {
        OracleCallableStatement oracleCallableStatement = null;
        final Object[] array = new Object[vector.size()];
        Object[] array2 = null;
        final LogFile llf = this.lLF;
        final String s2 = "\nIn the method callDeliverySplitter ";
        final LogFile llf2 = this.lLF;
        llf.writeln(s2, 2);
        final Vector<Number> vector9 = new Vector<Number>();
        for (int i = 0; i < vector.size(); ++i) {
            final Object o = null;
            array2 = new Object[] { vector.elementAt(i), vector2.elementAt(i), o, o, vector4.elementAt(i), vector3.elementAt(i), vector5.elementAt(i), o, vector7.elementAt(i), vector6.elementAt(i) };
            array[i] = array2;
        }
        try {
            final STRUCT struct = new STRUCT(StructDescriptor.createDescriptor("WSH_ENTITY_INFO_REC", this.mJConn), this.mJConn, array2);
            final ARRAY array3 = new ARRAY(ArrayDescriptor.createDescriptor("WSH_ENTITY_INFO_TAB", this.mJConn), this.mJConn, (Object)array);
            final StringBuffer sb = new StringBuffer();
            sb.append(" BEGIN WSH_DELIVERY_SPLITTER_PKG.DELIVERY_SPLITTER( ");
            sb.append("p_delivery_tab => :1,");
            sb.append("p_autosplit_flag => :2,");
            sb.append("x_accepted_del_id => :3,");
            sb.append("x_rejected_del_id => :4,");
            sb.append("x_return_status =>:5);");
            sb.append("END;");
            final LogFile llf3 = this.lLF;
            final String string = "calling " + sb.toString();
            final LogFile llf4 = this.lLF;
            llf3.writeln(string, 1);
            oracleCallableStatement = (OracleCallableStatement)this.mJConn.prepareCall(sb.toString());
            oracleCallableStatement.setARRAY(1, array3);
            oracleCallableStatement.setString(2, s);
            oracleCallableStatement.registerOutParameter(3, 2003, "WSH_OTM_ID_TAB");
            oracleCallableStatement.registerOutParameter(4, 2003, "WSH_OTM_ID_TAB");
            oracleCallableStatement.registerOutParameter(5, 12, this.maxLength);
            oracleCallableStatement.execute();
            final ARRAY array4 = oracleCallableStatement.getARRAY(3);
            final ARRAY array5 = oracleCallableStatement.getARRAY(4);
            final String string2 = oracleCallableStatement.getString(5);
            oracleCallableStatement.close();
            final LogFile llf5 = this.lLF;
            final String string3 = "Return status from WSH_DELIVERY_SPLITTER_PKG.DELIVERY_SPLITTER " + string2;
            final LogFile llf6 = this.lLF;
            llf5.writeln(string3, 1);
            final LogFile llf7 = this.lLF;
            final String string4 = "acceptedDlvIdTab Length : " + array4.length();
            final LogFile llf8 = this.lLF;
            llf7.writeln(string4, 1);
            final Object[] a = (Object[])array4.getArray();
            Arrays.sort(a);
            final LogFile llf9 = this.lLF;
            final String s3 = "Delivery Splitter Accepted Ids";
            final LogFile llf10 = this.lLF;
            llf9.writeln(s3, 4);
            for (int j = 0; j < a.length; ++j) {
                final LogFile llf11 = this.lLF;
                final String string5 = "Accepted Delivery Id : " + a[j];
                final LogFile llf12 = this.lLF;
                llf11.writeln(string5, 1);
            }
            final LogFile llf13 = this.lLF;
            final String string6 = "rejectedDlvIdTab Length : " + array5.length();
            final LogFile llf14 = this.lLF;
            llf13.writeln(string6, 1);
            final Object[] a2 = (Object[])array5.getArray();
            Arrays.sort(a2);
            final LogFile llf15 = this.lLF;
            final String s4 = "Delivery Splitter Rejected Ids";
            final LogFile llf16 = this.lLF;
            llf15.writeln(s4, 4);
            for (int k = 0; k < a2.length; ++k) {
                final LogFile llf17 = this.lLF;
                final String string7 = "Rejected Delivery Id : " + a2[k];
                final LogFile llf18 = this.lLF;
                llf17.writeln(string7, 1);
            }
            if (string2.equals("S") || (string2.equals("W") && array4.length() > 0)) {
                final Object[] array6 = (Object[])array5.getArray();
                for (int l = 0; l < array6.length; ++l) {
                    final Number number = new Number(array6[l]);
                    vector.removeElement(number);
                    vector8.addElement(number);
                }
                final Object[] a3 = (Object[])array4.getArray();
                Arrays.sort(a3);
                final LogFile llf19 = this.lLF;
                final String s5 = "Delivery Splitter Success Ids";
                final LogFile llf20 = this.lLF;
                llf19.writeln(s5, 4);
                for (int n = 0; n < a3.length; ++n) {
                    final LogFile llf21 = this.lLF;
                    final String string8 = "Success Delivery Ids" + a3[n];
                    final LogFile llf22 = this.lLF;
                    llf21.writeln(string8, 1);
                    vector9.addElement(new Number(a3[n]));
                }
            }
            else {
                vector.removeAllElements();
                final LogFile llf23 = this.lLF;
                final String string9 = "Removed all elemenst from dlvIdList , size" + vector.size();
                final LogFile llf24 = this.lLF;
                llf23.writeln(string9, 4);
            }
        }
        catch (SQLException ex) {
            vector.removeAllElements();
            final LogFile llf25 = this.lLF;
            final String string10 = "SQL Exception occured in method callDeliverySplitter method " + ex.toString();
            final LogFile llf26 = this.lLF;
            llf25.writeln(string10, 4);
            this.lRC.setCompletion(2, ex.getMessage());
        }
        finally {
            try {
                if (oracleCallableStatement != null) {
                    oracleCallableStatement.close();
                }
            }
            catch (Exception ex2) {}
        }
        return vector9;
    }
    
    void getTripStops(final StringBuffer sb, final Vector vector, final String s, final Vector vector2) {
        final LogFile llf = this.lLF;
        final String s2 = "In the method getTripStops ";
        final LogFile llf2 = this.lLF;
        llf.writeln(s2, 2);
        final StringBuffer sb2 = new StringBuffer();
        sb2.append("select distinct wt.trip_id ");
        sb2.append(" from wsh_trips wt, wsh_trip_stops wts ");
        sb2.append(" where wts.TMS_INTERFACE_FLAG in ('ASR', 'ASP') ");
        sb2.append(" and wts.trip_id = wt.trip_id");
        sb2.append(sb);
        sb2.append(" order by trip_id");
        final LogFile llf3 = this.lLF;
        final String string = "Actual Shipment Query " + sb2.toString();
        final LogFile llf4 = this.lLF;
        llf3.writeln(string, 1);
        try {
            (this.m_pSelStmt = (OraclePreparedStatement)this.mJConn.prepareStatement(sb2.toString())).defineColumnType(1, 4);
            for (int i = 0; i < vector.size(); ++i) {
                this.m_pSelStmt.setString(i + 1, (String)vector.elementAt(i));
            }
            final ResultSet executeQuery = this.m_pSelStmt.executeQuery();
            while (executeQuery.next()) {
                final LogFile llf5 = this.lLF;
                final String string2 = "trip Id " + executeQuery.getInt(1);
                final LogFile llf6 = this.lLF;
                llf5.writeln(string2, 1);
                final Integer n = new Integer(executeQuery.getInt(1));
                if (s != null && !s.equals("")) {
                    if (!this.checkTripOrganization(n, s)) {
                        continue;
                    }
                    if (this.checkDlvStatus(n)) {
                        vector2.addElement(n);
                    }
                    else {
                        final LogFile llf7 = this.lLF;
                        final String s3 = "Skipping the Trip as it has a delivery in 'DP' or 'DR' status";
                        final LogFile llf8 = this.lLF;
                        llf7.writeln(s3, 1);
                    }
                }
                else if (this.checkDlvStatus(n)) {
                    vector2.addElement(n);
                }
                else {
                    final LogFile llf9 = this.lLF;
                    final String s4 = "Skipping the Trip as it has a delivery in 'DP' or 'DR' status";
                    final LogFile llf10 = this.lLF;
                    llf9.writeln(s4, 1);
                }
            }
            this.m_pSelStmt.close();
        }
        catch (SQLException ex) {
            final LogFile llf11 = this.lLF;
            final String string3 = "Exception occured in getTripStops method" + ex.toString();
            final LogFile llf12 = this.lLF;
            llf11.writeln(string3, 1);
            this.lRC.setCompletion(2, ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                if (this.m_pSelStmt != null) {
                    this.m_pSelStmt.close();
                }
            }
            catch (Exception ex2) {}
        }
    }
    
    boolean checkDlvStatus(final Integer n) throws SQLException {
        final LogFile llf = this.lLF;
        final String s = "In the method checkDlvStatus ";
        final LogFile llf2 = this.lLF;
        llf.writeln(s, 2);
        final StringBuffer sb = new StringBuffer();
        sb.append("select  wts.trip_id ");
        sb.append(" from  wsh_trip_stops wts , wsh_delivery_legs wdl, wsh_new_deliveries wnd");
        sb.append(" where (wts.stop_id = wdl.pick_up_stop_id or wts.stop_id = wdl.drop_off_stop_id) ");
        sb.append(" and wdl.delivery_id = wnd.delivery_id ");
        sb.append(" and wnd.tms_interface_flag in ('DP', 'DR') ");
        sb.append(" and wts.trip_id = :1");
        try {
            (this.m_pSelStmt = (OraclePreparedStatement)this.mJConn.prepareStatement(sb.toString())).defineColumnType(1, 4);
            this.m_pSelStmt.setInt(1, (int)n);
            final ResultSet executeQuery = this.m_pSelStmt.executeQuery();
            if (executeQuery.next()) {
                final LogFile llf3 = this.lLF;
                final String string = "trip " + executeQuery.getInt(1) + " has a  delivery with 'DP' or 'DR' status";
                final LogFile llf4 = this.lLF;
                llf3.writeln(string, 1);
                return false;
            }
            this.m_pSelStmt.close();
            return true;
        }
        catch (SQLException ex) {
            final LogFile llf5 = this.lLF;
            final String string2 = "Exception occured in checkDlvStatus method" + ex.toString();
            final LogFile llf6 = this.lLF;
            llf5.writeln(string2, 1);
            throw new SQLException(ex.getMessage());
        }
        finally {
            try {
                if (this.m_pSelStmt != null) {
                    this.m_pSelStmt.close();
                }
            }
            catch (Exception ex2) {}
        }
    }
    
    boolean checkTripOrganization(final Integer obj, final String s) {
        OracleCallableStatement oracleCallableStatement = null;
        final LogFile llf = this.lLF;
        final String string = "In the method checkTripOrganization  for Trip Id " + obj;
        final LogFile llf2 = this.lLF;
        llf.writeln(string, 4);
        try {
            final StringBuffer sb = new StringBuffer();
            sb.append(" BEGIN :1 := WSH_UTIL_CORE.GET_TRIP_ORGANIZATION_ID( ");
            sb.append("p_trip_id => :2); END;");
            oracleCallableStatement = (OracleCallableStatement)this.mJConn.prepareCall(sb.toString());
            oracleCallableStatement.setInt(2, (int)obj);
            oracleCallableStatement.registerOutParameter(1, 4);
            oracleCallableStatement.executeQuery();
            final int int1 = oracleCallableStatement.getInt(1);
            final LogFile llf3 = this.lLF;
            final String string2 = "OrganizationId  from the api WSH_UTIL_CORE.GET_TRIP_ORGANIZATION : " + int1;
            final LogFile llf4 = this.lLF;
            llf3.writeln(string2, 1);
            oracleCallableStatement.close();
            if (int1 == Integer.parseInt(s)) {
                return true;
            }
        }
        catch (SQLException ex) {
            final LogFile llf5 = this.lLF;
            final String string3 = "SQL Exception occured in method checkTripOrganization method " + ex.toString();
            final LogFile llf6 = this.lLF;
            llf5.writeln(string3, 4);
            this.lRC.setCompletion(2, ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                if (oracleCallableStatement != null) {
                    oracleCallableStatement.close();
                }
            }
            catch (Exception ex2) {}
        }
        return false;
    }
    
    boolean isOTMInstalled() {
        OracleCallableStatement oracleCallableStatement = null;
        final LogFile llf = this.lLF;
        final String s = "In the method isOTMInstalled ";
        final LogFile llf2 = this.lLF;
        llf.writeln(s, 2);
        try {
            final StringBuffer sb = new StringBuffer();
            sb.append(" BEGIN :1 := WSH_UTIL_CORE.GC3_IS_INSTALLED; ");
            sb.append(" END;");
            oracleCallableStatement = (OracleCallableStatement)this.mJConn.prepareCall(sb.toString());
            oracleCallableStatement.registerOutParameter(1, 12, this.maxLength);
            oracleCallableStatement.executeQuery();
            final String string = oracleCallableStatement.getString(1);
            oracleCallableStatement.close();
            if (string.equals("Y")) {
                return true;
            }
            return false;
        }
        catch (SQLException ex) {
            final LogFile llf3 = this.lLF;
            final String string2 = "SQL Exception occured in method isOTMInstalled method " + ex.toString();
            final LogFile llf4 = this.lLF;
            llf3.writeln(string2, 4);
            this.lRC.setCompletion(2, ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                if (oracleCallableStatement != null) {
                    oracleCallableStatement.close();
                }
            }
            catch (Exception ex2) {}
        }
        return false;
    }
    
    void BatchRecords(final Vector vector, final String e, final int n, final int i, final String e2, final String s, final String s2, final String s3) throws RequestSubmissionException {
        final LogFile llf = this.lLF;
        final String s4 = "In the method BatchRecords ";
        final LogFile llf2 = this.lLF;
        llf.writeln(s4, 2);
        final LogFile llf3 = this.lLF;
        final String string = "batch Size " + n;
        final LogFile llf4 = this.lLF;
        llf3.writeln(string, 1);
        final LogFile llf5 = this.lLF;
        final String string2 = "No of Childs " + i;
        final LogFile llf6 = this.lLF;
        llf5.writeln(string2, 1);
        final LogFile llf7 = this.lLF;
        final String string3 = "Total Records " + vector.size();
        final LogFile llf8 = this.lLF;
        llf7.writeln(string3, 1);
        final LogFile llf9 = this.lLF;
        final String string4 = "Organization_id " + s;
        final LogFile llf10 = this.lLF;
        llf9.writeln(string4, 1);
        final LogFile llf11 = this.lLF;
        final String string5 = "nameLow " + s2;
        final LogFile llf12 = this.lLF;
        llf11.writeln(string5, 1);
        final LogFile llf13 = this.lLF;
        final String string6 = "nameHigh " + s3;
        final LogFile llf14 = this.lLF;
        llf13.writeln(string6, 1);
        final int n2 = vector.size() / i;
        int j;
        if (n2 <= n) {
            j = n;
        }
        else {
            j = n2;
        }
        final LogFile llf15 = this.lLF;
        final String string7 = "Increment size " + j;
        final LogFile llf16 = this.lLF;
        llf15.writeln(string7, 1);
        for (int k = 0; k < vector.size(); k += j) {
            final Vector<String> vector2 = new Vector<String>();
            final String string8 = vector.elementAt(k).toString();
            String s5;
            if (k + j - 1 < vector.size()) {
                s5 = vector.elementAt(k + j - 1).toString();
            }
            else {
                s5 = vector.lastElement().toString();
            }
            vector2.add(string8);
            vector2.add(s5);
            vector2.add(s2);
            vector2.add(s3);
            vector2.add(e2);
            vector2.add(e);
            vector2.add(Integer.toString(n));
            vector2.add(s);
            this.lLF.writeln("Launching a  Child request with parameters " + string8 + " and " + s5, 1);
            this.lLF.writeln("Request Id " + new ConcurrentRequest(this.mJConn).submitRequest("WSH", "WSHOOCHL", "Outbound Child Concurrent program", (String)null, true, (Vector)vector2), 1);
        }
    }
    
    boolean checkProfiles(final CpContext cpContext) {
        boolean b = true;
        final String profile = cpContext.getProfileStore().getProfile("WSH_OTM_DOMAIN_NAME");
        if (profile == null || profile.equals("")) {
            final LogFile llf = this.lLF;
            final String s = "Please provide a value for the profile option OTM: Domain Name ( WSH_OTM_DOMAIN_NAME)";
            final LogFile llf2 = this.lLF;
            llf.writeln(s, 4);
            this.lOF.writeln("Please provide a value for the profile option OTM: Domain Name  ");
            b = false;
        }
        final String profile2 = cpContext.getProfileStore().getProfile("WSH_OTM_OB_SERVICE_ENDPOINT");
        if (profile2 == null || profile2.equals("")) {
            final LogFile llf3 = this.lLF;
            final String s2 = " Please provide a value for the profile option WSH: BPEL Webservice URI for OTM ( WSH_OTM_OB_SERVICE_ENDPOINT)";
            final LogFile llf4 = this.lLF;
            llf3.writeln(s2, 4);
            this.lOF.writeln(" Please provide a value for the profile option WSH: BPEL Webservice URI for OTM");
            b = false;
        }
        final String profile3 = cpContext.getProfileStore().getProfile("WSH_OTM_USER_ID");
        if (profile3 == null || profile3.equals("")) {
            final LogFile llf5 = this.lLF;
            final String s3 = " Please provide a value for the profile option OTM: Domain User( WSH_OTM_USER_ID)";
            final LogFile llf6 = this.lLF;
            llf5.writeln(s3, 4);
            this.lOF.writeln(" Please provide a value for the profile option OTM: Domain User");
            b = false;
        }
        final String profile4 = cpContext.getProfileStore().getProfile("WSH_OTM_PASSWORD");
        if (profile4 == null || profile4.equals("")) {
            final LogFile llf7 = this.lLF;
            final String s4 = " Please provide a value for the profile option OTM: Domain Password( WSH_OTM_PASSWORD)";
            final LogFile llf8 = this.lLF;
            llf7.writeln(s4, 4);
            this.lOF.writeln(" Please provide a value for the profile option OTM: Domain Password");
            b = false;
        }
        return b;
    }
    
    static {
        OutboundInterface.notsentIdList = new Vector();
    }
}