package com.dspread.demoui.activity.nibssImpl;

import android.content.Context;
import android.util.Log;

import com.dspread.demoui.activity.nibssImpl.dao.AppDatabase;
import com.dspread.demoui.activity.nibssImpl.dao.ParamInfo;
import com.dspread.demoui.activity.nibssImpl.info.TermParamInfo;
import com.dspread.demoui.activity.nibssImpl.model.TranNetInfo;
import com.dspread.demoui.activity.nibssImpl.utils.OtaUtility;
import com.dspread.demoui.activity.nibssImpl.utils.SecurityUtil;
import com.dspread.demoui.activity.nibssImpl.van.PosPackager;

import org.apache.commons.codec.binary.Hex;
import org.jpos.core.VolatileSequencer;
import org.jpos.iso.ISODate;
import org.jpos.iso.ISOField;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.tlv.TLVList;
import org.jpos.util.LogSource;
import org.jpos.util.SimpleLogListener;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;


public class IsoMessageClient {

    AppDatabase appDatabase;

    Properties props = null;

    PostChannel channel = null;

    public static final String TAG = IsoMessageClient.class.getSimpleName();

//ISOMUX mux = null;

    org.jpos.util.Logger logger = new org.jpos.util.Logger();
    VolatileSequencer seq = new VolatileSequencer();

    public ISOMsg getCallHome(String keyType, String terminalId ) {
        Date d = new Date();
        // logger.info("Create Network Request ");
        ISOMsg m = new ISOMsg();
        try {

            //=====>***	m.setPackager(new PosPackager());
            m.setMTI("0800");

            String bitmap = "1";
            m.set(3, "9D" + "0000");
            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, OtaUtility.GetRefNumber("", 6));
            // m.set(new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
//            if (ptsp.equals("NETOP"))
//                m.set(32, "100001");
            m.set(41, terminalId);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public ISOMsg getNotification(String keyType, String terminalId) {
        return makeNotification(keyType, terminalId, "");
    }

    public ISOMsg makeNotification(String keyType, String terminalId, String ptsp) {
        Date d = new Date();
        // logger.info("Create Network Request ");
        ISOMsg m = new ISOMsg();
        try {

            //=====>***	m.setPackager(new PosPackager());
            m.setMTI("0800");

            String bitmap = "1";
            m.set(3, keyType + "0000");
            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, OtaUtility.GetRefNumber("", 6));
            // m.set(new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
//            if (ptsp.equals("NETOP"))
//                m.set(32, "100001");
            m.set(41, terminalId);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }




    public ISOMsg getNetworkMgtRequestRubies(String keyType, String terminalId) {
        return getNetworkMgtRequestRubies(keyType, terminalId, "");
    }

    public ISOMsg getNetworkMgtRequestRubies(String keyType, String terminalId, String ptsp) {
        Date d = new Date();
        // logger.info("Create Network Request ");
        ISOMsg m = new ISOMsg();
        try {

            //=====>***	m.setPackager(new PosPackager());
            m.setMTI("0800");

            String bitmap = "1";
            m.set(3, keyType + "0000");
            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, OtaUtility.GetRefNumber("", 6));
            // m.set(new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
//            if (ptsp.equals("NETOP"))
//                m.set(32, "100001");
            m.set(41, terminalId);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public ISOMsg getNetworkMgtRequestRubies2(String keyType, ISOMsg m2) {
        Date d = new Date();
        // logger.info("Create Network Request ");
        ISOMsg m = new ISOMsg();
        try {

            //=====>***	m.setPackager(new PosPackager());
            m.setMTI("0800");

            String bitmap = "1";
            m.set(3, keyType + "0000");
            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, m2.getString(7));  //"0327133744");  //MMDDhhmmss
            m.set(11, m2.getString(11)); //new ISOField(11,
            //ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(12, m2.getString(12)); // //new ISOField(12, ISODate.getTime(d)));
            m.set(13, m2.getString(13));  //new ISOField(13, ISODate.getDate(d)));
            m.set(41, m2.getString(41)); //  terminalId); //"2HIG0106"); //"20390004"); // "2HIG0106"); //"2HIG0004");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }











    public ISOMsg getNetworkMgtRequestRubiesParamDownload(String keyType, String terminalId) {
        Date d = new Date();
        // logger.info("Create Network Request ");
        ISOMsg m = new ISOMsg();
        try {

            m.setMTI("0800");

            String bitmap = "1";
            m.set(3, keyType + "0000");
            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(new ISOField(11,
                    ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(41, terminalId); //"2HIG0106"); //"20390004"); // "2HIG0106"); //"2HIG0004");
            //m.set(70, "101");
            //m.set(100, "00100100133");

            // m.unset(3);
            //int charge = 100;
            //m.set(28,"D00000" + charge); //Tranction Fee Charge
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }


    public void SendISOPurchase(ISOMsg m, String eid) {

        try {

            // "3.81.182.81"; //
            //7900; //
            //30000; //
            String serverIP = "";
            int port = 0;
            int timeout = 0;
            logger.addListener(new SimpleLogListener(System.out));
            //channel = new PostChannel(serverIP, port, new PostPackager()); // PosPackager());   //ISO87APackager());

            //String xml = "C:\\JAVA\\jpos-1.7.0\\jpos-1.7.0\\cfg\\packager\\iso87ascii.xml";

            //=====>***  channel = new PostChannel(serverIP, port,new PosPackager()); // new GenericPackager("C:\\JAVA\\jpos-1.7.0\\jpos-1.7.0\\cfg\\packager\\postpack.xml")); // PosPackager());   //ISO87APackager());
            // channel = new PostChannel(serverIP, port,new GenericPackager(xml));

            //mux = new ISOMUX(channel);

            ((LogSource) channel).setLogger(logger, "channel");

            //mux.setLogger(logger, "mux");

            System.out.println("ISo-Connect 1");

            if (!channel.isConnected()) {

                channel.connect();
                System.out.println("ISo-Connect 2");

            }

            VolatileSequencer seq = new VolatileSequencer();

            if (channel.isConnected()) {

                System.out.println("ISo-Connect 3");


                channel.send(m);
                System.out.println("ISo-Connect 33 " + timeout);

                channel.setTimeout(timeout * 1000);

                ISOMsg response = channel.receive();   //  req.getResponse(isoTimeOut);
                channel.disconnect();

                if (response != null) {


                    System.out.println("ISo-Connect Response " + response.getString(39));
                    String rspCode = response.getString(39);
                    String authCode = OtaUtility.GetRefNumber("", 6).trim();
                    if (response.hasField(38))
                        authCode = response.getString(38);

                    String refNo = "CAWI" + response.getString(37) + response.getString(11);
                    System.out.println("ISo-Connect 33 " + refNo);

                }


            }


        } catch (Exception ex) {

            ex.printStackTrace();

        }


    }

    public void SendISOmessage(String eid, String terminalId, String serverIP, int port, String key1, String key2) {

        try {


            int timeout = 0;

            logger.addListener(new SimpleLogListener(System.out));


            //====> To Be change todo
            //  channel = new PostChannel(serverIP, port, new PosPackager() );
            ISOPackager p = new GenericPackager();
            channel = new PostChannel(serverIP, port, p);

            ((LogSource) channel).setLogger(logger, "channel");

            System.out.println("ISo-Connect 1");

            if (!channel.isConnected()) {

                channel.connect();
                System.out.println("ISo-Connect 2");

            }

            VolatileSequencer seq = new VolatileSequencer();


            String tpke = "";

            if (channel.isConnected()) {

                System.out.println("ISo-Connect 3");

                ISOMsg m = getNetworkMgtRequestRubies("9A", terminalId);

                channel.send(m);
                System.out.println("ISo-Connect 33 " + timeout);

                channel.setTimeout(timeout * 1000);

                ISOMsg response = channel.receive();   //  req.getResponse(isoTimeOut);

                if (response != null) {
                    System.out.println("ISo-Connect Response " + response.getString(39));
                    String f53 = response.getString(53);
                    String tpk = "";
                    String zmk = ISOUtil.hexor(key1, key2);

                    //====> To Be change todo
                    //String tmk = PinBlockEncryptionUtil.DecryptSessionKey(zmk,f53);

//====> To Be change todo tmk
                    System.out.println("ISo-TMK " + "tmk");

                    m = getNetworkMgtRequestRubies("9B", terminalId);
                    String tsk = "";
                    channel.send(m);
                    ISOMsg response2 = channel.receive();
                    if (response2 != null) {
                        f53 = response2.getString(53);


                        //====> To Be change todo
                        //  tsk = PinBlockEncryptionUtil.DecryptSessionKey(tmk,f53);


                        System.out.println("ISo-TSK " + tsk);
                    }


                    m = getNetworkMgtRequestRubies("9G", terminalId);

                    channel.send(m);
                    ISOMsg response3 = channel.receive();
                    if (response3 != null) {
                        f53 = response3.getString(53);
                        tpke = f53;


                        //===> to be change todo
                        // tpk = PinBlockEncryptionUtil.DecryptSessionKey(tmk,f53);
                        System.out.println("ISo-TPK " + tpk);
                    }


                    m = getNetworkMgtRequestRubies("9C", terminalId);
                    m.set(64, new String(new byte[]{0x0}));

                    String f64 = generateHashForIsoMsg(m, tsk);
                    m.set(64, f64);

                    String f62 = "";

                    String cardAcceptorId = "";
                    String cardAcceptorLocation = "";
                    String merchantType = "";
                    String currencyCode = "";
                    channel.send(m);
                    ISOMsg response4 = channel.receive();
                    if (response4 != null) {
                        f62 = response4.getString(62);
                        //String tpk = PinBlockEncryptionUtil.DecryptSessionKey(tmk,f53);
                        // System.out.println("ISo-TPK " + tpk);

                        Map<String, String> decodedParameters = parseParameters(f62);
                        cardAcceptorId = decodedParameters.get("03");
                        cardAcceptorLocation = decodedParameters.get("52");
                        merchantType = decodedParameters.get("08");
                        currencyCode = decodedParameters.get("05");

                        System.out.println(" Card acceptor Id: " + cardAcceptorId);
                        System.out.println(" Card acceptor Location: " + cardAcceptorLocation);
                        System.out.println(" Merchant Type: " + merchantType);
                        System.out.println(" currencyCode: " + currencyCode);

                    }
                    String action = "U";
					    		/* BkTermParam termInfo = agNetJPA.GetTerminalParamInfo(eid, terminalId);
                                 if(termInfo == null)
                                 {
					    		    termInfo = new BkTermParam();
					    		    termInfo.setId("0");
					    		    termInfo.setStatus("Active");
					    		    termInfo.setBank("");
					    		    termInfo.setAndroid_Id("");
						    		termInfo.setUsername("");
					    		    action = "S";
                                 }
					    		 termInfo.setTerminal_Id(terminalId);
					    		 termInfo.setCard_Acceptor_Id(cardAcceptorId);
					    		 termInfo.setCardAcceptor_Location(cardAcceptorLocation);
					    		 termInfo.setEntity_Id(new BigInteger(eid));
					    		 
					    		 termInfo.setIso_Ccy_Code(currencyCode);
					    		 termInfo.setMerchant_Type(merchantType);
					    		 termInfo.setTmk(tmk);
					    		 termInfo.setTpk(tpke);
					    		 termInfo.setTsk(tsk);
					    		 termInfo.setZmk(zmk);
					    		 agNetJPA.SaveEntity(termInfo, action);*/

                    //====> To Be change todo tmk
                    //	String json = terminalId + "|" + tmk + "|" + tpke + "|" + tsk + "|" + zmk + "|" + cardAcceptorId;
                    String json = terminalId + "|" + "tmk" + "|" + tpke + "|" + tsk + "|" + zmk + "|" + cardAcceptorId;


                    json = SecurityUtil.encrypt(json, "");
                    String category = "TERMINAL_KEY";
                    ParamInfo pInfo = appDatabase.userProfileDAO().getParamInfo(category);
                    if (pInfo == null) {
                        pInfo = new ParamInfo();
                        pInfo.setCode(category);
                        pInfo.setName(json);
                        pInfo.setLastUpdate(new Date());
                        appDatabase.userProfileDAO().saveParamInfo(pInfo);
                    } else {
                        pInfo.setName(json);
                        pInfo.setLastUpdate(new Date());
                        appDatabase.userProfileDAO().updateParamInfo(pInfo);
                    }


                }

                channel.disconnect();

            }


        } catch (Exception ex) {

            ex.printStackTrace();

        }


    }

    public ISOMsg CreatePurchaseMessage(TranNetInfo tInfoc, TermParamInfo tparams, int retry) { //, String posAccountUpsl) {
        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0200");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv|accType;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));


            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];
            String accx = "00";
            if (accType.equals("1"))
                accx = "20";
            else if (accType.equals("2"))
                accx = "10";
            else if (accType.equals("3"))
                accx = "30";
            field23 = ISOUtil.padleft(field23, 3, '0');

            String tData2 = track2Data;//OtaUtility.tokenize(track2Data, "=")[1];


            String expDate = tData2.substring(0, 4);
            String serviceCode = tData2.substring(4, 7);

            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);
            String field11 = rrn.substring(6);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            m.set(3, "01" + accx + "00");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            //m.set(3, "00" + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, field11); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "6010");// 4");
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00");
            //m.set(26, "04"); //"06");
            m.set(26, "06"); // Set Offline PIN
            m.set(28, "C00000000");
            m.set(32, "111129"); //  "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            m.set(55, cds[4]);
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);


            //String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            String posAccountUpsl = tparams.getPoolAccount();
            if (posAccountUpsl == null || posAccountUpsl.equals(""))
                posAccountUpsl = "1234567890";

            //posAccountUpsl = "1774691015";

            String f60 = "010085C24300148041Meter Number=12.87001004.Acct=" + posAccountUpsl + ".Phone=" + mobileNo;
            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
            m.set(60, f60);
            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");
            m.set(123, "713101516344109");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);
            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            if (retry > 0) {
                m.set(52, pinBlock); // pinBytes);}
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public ISOMsg CreatePurchaseMessageRexConnect(TranNetInfo tInfoc, TermParamInfo tparams, int retry, boolean onlinePin,String m1) { //, String posAccountUpsl) {
        System.out.println("*****A*****tInfoc=>" + tInfoc.toString());
        System.out.println("*****B*****tparams=>" + tparams.toString());


        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {
            m.setPackager(new PosPackager());

          //  m.setMTI("0200");
            m.setMTI(m1);

            Log.d(TAG, "BILL-REF: " + tInfoc.getBillRefNo());

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];


            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());

            field23 = ISOUtil.padleft(field23, 3, '0');

            String accx = "00";
			/*if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";*/
            //field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = null, expDate = null, serviceCode = "";

            if (track2Data.contains("D")) {
                tData2 = OtaUtility.tokenize(track2Data, "D")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            } else if (track2Data.contains("=")) {
                tData2 = OtaUtility.tokenize(track2Data, "=")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            }



            System.out.println("**********serviceCode=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(0, 4));


            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
            String ccyCode = tparams.getCurrencyCode();

            String merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            System.out.println("********** .getCardData()=>" + tInfoc.getCardData());

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);


            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            //m.set(3, "01" + accx + "00");
         //   m.set(3, "000000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            m.set(3, tInfoc.getTranType()+"0000");
            //Long amts = new BigDecimal(amt * 100).longValue();


            Long amts = new BigDecimal(amt).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";DE64  ]
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss

            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));



            m.set(new ISOField(12, tInfoc.getTime()));
            m.set(new ISOField(13, tInfoc.getDate()));

//            m.set(new ISOField(12, ISODate.getTime(d)));
//            m.set(new ISOField(13, ISODate.getDate(d)));



            m.set(14, expDate);
            m.set(18, merchantNo); //5251"); // for NIBSS "5410"); //6010");// 4"); 6012
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00"); //"91"); //"00");
            m.set(26, "12"); //"06"); // Set Offline PIN
            m.set(28, "D00000000");  //D00000000
            m.set(32,"111129");
            //m.set(32,  "111129"); //  "111111");
            m.set(33,  "557694");
            track2Data2 = track2Data2.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);


            m.set(40, serviceCode);//"601"); // "221"); 201 serviceCode
            //m.set(40, "221") ;//"601"); // "221"); 201
            m.set(41, terminalId); // "2HIG0106");



            m.set(42, tparams.getCardAcceptorId());
            m.set(43, tparams.getCardAcceptionLocation());
            m.set(49, "566");

         if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            Log.e("PINDATA", tInfoc.getCardData());
            //if(retry > 0)
            if (onlinePin && 16 == tInfoc.getCardData().length())
                m.set(52, tInfoc.getCardData());

            m.set(55, field55);

            //m.set(59, "Reconciler&gt;GENERIC&amp;Option&gt;000");

            SimpleDateFormat dff4 = new SimpleDateFormat("yyyy");
            String dy = dff4.format(new Date());
//			m.set(59, terminalId +"-"+ rrn + "-" + dy + dx);



            if(tInfoc.getTranType() == "61"  ) {
                String f90 = ("0100") + stan + tInfoc.getTranDateX() + "00000" + m.getString(32) + "00000" + m.getString(33);
                m.set(90, f90);
            }
            if(  tInfoc.getTranType() == "20" ) {
                String f90 = "0200" + stan + tInfoc.getTranDateX() + "00000" + m.getString(32) + "00000" + m.getString(33);
                m.set(90, f90);
            }




            m.set(123, "51010151134C101");//510101513344101

//			m.set(128, new String(new byte[]{0x0}));
            m.set(128, "");

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
        }
        return m;
    }





    public ISOMsg CreatePurchaseMedusa(TranNetInfo tInfoc, TermParamInfo tparams, int retry, boolean onlinePin,String m1) { //, String posAccountUpsl) {
        System.out.println("*****A*****tInfoc=>" + tInfoc.toString());
        System.out.println("*****B*****tparams=>" + tparams.toString());

        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {
            m.setPackager(new PosPackager());

            //  m.setMTI("0200");
            m.setMTI(m1);

            Log.d(TAG, "BILL-REF: " + tInfoc.getBillRefNo());

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];


            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());

            field23 = ISOUtil.padleft(field23, 3, '0');

            String accx = "00";
			/*if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";*/
            //field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = null, expDate = null, serviceCode = "";

            if (track2Data.contains("D")) {
                tData2 = OtaUtility.tokenize(track2Data, "D")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            } else if (track2Data.contains("=")) {
                tData2 = OtaUtility.tokenize(track2Data, "=")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            }



            System.out.println("**********serviceCode=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(0, 4));


            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
            String ccyCode = tparams.getCurrencyCode();

            String merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            System.out.println("********** .getCardData()=>" + tInfoc.getCardData());

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);


            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            //m.set(3, "01" + accx + "00");
            //   m.set(3, "000000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            m.set(3, tInfoc.getTranType()+"0000");
            //Long amts = new BigDecimal(amt * 100).longValue();


            Long amts = new BigDecimal(amt).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";DE64  ]
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss

            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));



            m.set(new ISOField(12, tInfoc.getTime()));
            m.set(new ISOField(13, tInfoc.getDate()));

//            m.set(new ISOField(12, ISODate.getTime(d)));
//            m.set(new ISOField(13, ISODate.getDate(d)));



            m.set(14, expDate);
            m.set(18, "5411"); //5251"); // for NIBSS "5410"); //6010");// 4"); 6012
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00"); //"91"); //"00");
            m.set(26, "12"); //"06"); // Set Offline PIN
            m.set(28, "D00000000");  //D00000000
            m.set(32,"111129");
            //m.set(32,  "111129"); //  "111111");
            m.set(33,  "557694");
            track2Data2 = track2Data2.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);


            m.set(40, serviceCode);//"601"); // "221"); 201 serviceCode
            //m.set(40, "221") ;//"601"); // "221"); 201
            //      m.set(41, "2CU1F5JG"); // "2HIG0106");
            m.set(41, terminalId); // "2HIG0106");


            /*
            2215LA490425600
2215VR48
2215VQ35
2215VQ25
2215VV53
2215VQ22
2215VQ12
2215VR45
2215VR43
2215VR44
             */
        /*
  public static String masterKey = "27869791275814834349579874090163";
4:23 PM
2FI1K04U232
Tid
or public static final String TERMINAL_MASTER_KEY = "33333333333333333333333333333333";

packager.setField(42, "2302BA000009611");
11:18 AM
  setupTelpo("","511101512344101","", 0, Globals.PARAMS,"511101512344101","CW BY FCMB_AGENT @ 168681659528@2CUBT NG","","566");
mid 511101512344101
location "511101512344101","CW BY FCMB_AGENT @ 168681659528@2CUBT NG"
 */


            m.set(42, "511101512344101");
           // m.set(43, "CW BY FCMB_AGENT @ 168681659528@2CUBT NG");
            m.set(43, "3LINE CARD MANAGEMENT LLA           LANG"); // fit TID 2CU1F5JG





            m.set(49, "566");

            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            Log.e("PINDATA", tInfoc.getCardData());
            //if(retry > 0)
            if (onlinePin && 16 == tInfoc.getCardData().length())
                m.set(52, tInfoc.getCardData());

            m.set(55, field55);

            //m.set(59, "Reconciler&gt;GENERIC&amp;Option&gt;000");

            SimpleDateFormat dff4 = new SimpleDateFormat("yyyy");
            String dy = dff4.format(new Date());
//			m.set(59, terminalId +"-"+ rrn + "-" + dy + dx);
            m.set(60, "Payment from mpos");

            if(tInfoc.getTranType() == "61"  ) {
                String f90 = ("0100") + stan + tInfoc.getTranDateX() + "00000" + m.getString(32) + "00000" + m.getString(33);
                m.set(90, f90);
            }
            if(  tInfoc.getTranType() == "20" ) {
                String f90 = "0200" + stan + tInfoc.getTranDateX() + "00000" + m.getString(32) + "00000" + m.getString(33);
                m.set(90, f90);
            }




            m.set(123, "51010151134C101");//510101513344101

//			m.set(128, new String(new byte[]{0x0}));
            m.set(128, "");
//
            String f128 = generateHashForIsoMsg(m, "28300518865986737073478883921518");
                                                       //28300518865986737073478883921518""
            m.set(128, f128);

        } catch (Exception ex) {
        }
        return m;
    }


    public ISOMsg CreateCashAdvance(TranNetInfo tInfoc, TermParamInfo tparams, int retry, boolean onlinePin) { //, String posAccountUpsl) {
        System.out.println("*****A*****tInfoc=>" + tInfoc.toString());
        System.out.println("*****B*****tparams=>" + tparams.toString());


        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            m.setMTI("0200");

            Log.d(TAG, "BILL-REF: " + tInfoc.getBillRefNo());

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];


            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());

            field23 = ISOUtil.padleft(field23, 3, '0');

            String accx = "00";
			/*if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";*/
            //field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = null, expDate = null, serviceCode = "";

            if (track2Data.contains("D")) {
                tData2 = OtaUtility.tokenize(track2Data, "D")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            } else if (track2Data.contains("=")) {
                tData2 = OtaUtility.tokenize(track2Data, "=")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            }



            System.out.println("**********serviceCode=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(0, 4));


            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
            String ccyCode = tparams.getCurrencyCode();

            String merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            System.out.println("********** .getCardData()=>" + tInfoc.getCardData());

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);


            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            //m.set(3, "01" + accx + "00");
            //   m.set(3, "000000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            m.set(3, tInfoc.getTranType()+"0000");
            //Long amts = new BigDecimal(amt * 100).longValue();


            Long amts = new BigDecimal(amt).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";DE64  ]
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss

            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, merchantNo); //5251"); // for NIBSS "5410"); //6010");// 4"); 6012
            m.set(22, "051");

            m.set(23, field23); //"001");

            m.set(25, "00"); //"91"); //"00");




            m.set(26, "12"); //"06"); // Set Offline PIN
            m.set(28, "D00000000");  //D00000000
            m.set(32, getAcquiringInstitutionIdCode(track2Data)); //"415002"); //  "111111");
            track2Data2 = track2Data2.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221"); 201 serviceCode
            //m.set(40, "221") ;//"601"); // "221"); 201
            m.set(41, terminalId); // "2HIG0106");



            m.set(42, tparams.getCardAcceptorId());
            m.set(43, tparams.getCardAcceptionLocation());
            m.set(49, "566");

            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            Log.e("PINDATA", tInfoc.getCardData());
            //if(retry > 0)
            if (onlinePin && 16 == tInfoc.getCardData().length())
                m.set(52, tInfoc.getCardData()); // pinBytes);

            m.set(55, field55);

            //m.set(59, "Reconciler&gt;GENERIC&amp;Option&gt;000");

            SimpleDateFormat dff4 = new SimpleDateFormat("yyyy");
            String dy = dff4.format(new Date());
//			m.set(59, terminalId +"-"+ rrn + "-" + dy + dx);

            m.set(123, "51010151134C101");//510101513344101

//			m.set(128, new String(new byte[]{0x0}));
            m.set(128, "");

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }


    public ISOMsg CreateBalanceMessageRexConnect(TranNetInfo tInfoc, TermParamInfo tparams, int retry, boolean onlinePin) { //, String posAccountUpsl) {
        System.out.println("*****A*****tInfoc=>" + tInfoc.toString());
        System.out.println("*****B*****tparams=>" + tparams.toString());


        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0100");

            Log.d(TAG, "BILL-REF: " + tInfoc.getBillRefNo());

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            Log.d(TAG, "BILL-cds: " + cds.toString());

            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];


            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());

            field23 = ISOUtil.padleft(field23, 3, '0');

            String accx = "00";
			/*if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";*/
            //field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = null, expDate = null, serviceCode = "";

            if (track2Data.contains("D")) {
                tData2 = OtaUtility.tokenize(track2Data, "D")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            } else if (track2Data.contains("=")) {
                tData2 = OtaUtility.tokenize(track2Data, "=")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            }


            System.out.println("**********serviceCode=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(0, 4));


            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
            String ccyCode = tparams.getCurrencyCode();

            String merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            System.out.println("********** .getCardData()=>" + tInfoc.getCardData());

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            //m.set(3, "01" + accx + "00");
            m.set(3, "310000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            //m.set(3, "00" + accx + "00");
            //Long amts = new BigDecimal(amt * 100).longValue();
            Long amts = new BigDecimal(amt).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";DE64  ]
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss

            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, merchantNo); //5251"); // for NIBSS "5410"); //6010");// 4"); 6012
            m.set(22, "051");
            //5399237077728398D2307221019244246
            m.set(23, field23); //"001");
            if (onlinePin)
                m.set(25, "00");
            else
                m.set(25, "00"); //"91"); //"00");

//			m.set(26, "12"); //"06");


            m.set(26, "12"); //"06"); // Set Offline PIN
            m.set(28, "D00000000");  //D00000000
            m.set(32, getAcquiringInstitutionIdCode(track2Data)); //"415002"); //  "111111");
            track2Data2 = track2Data2.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221"); 201 serviceCode
            //m.set(40, "221") ;//"601"); // "221"); 201
            m.set(41, terminalId); // "2HIG0106");



            m.set(42, tparams.getCardAcceptorId());
            m.set(43, tparams.getCardAcceptionLocation());
            m.set(49, "566");

            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            Log.e("PINDATA", tInfoc.getCardData());
            //if(retry > 0)
            if (onlinePin && 16 == tInfoc.getCardData().length())
                m.set(52, tInfoc.getCardData()); // pinBytes);

            m.set(55, field55);

            //m.set(59, "Reconciler&gt;GENERIC&amp;Option&gt;000");

            SimpleDateFormat dff4 = new SimpleDateFormat("yyyy");
            String dy = dff4.format(new Date());
//			m.set(59, terminalId +"-"+ rrn + "-" + dy + dx);

            m.set(123, "51010151134C101");//510101513344101

//			m.set(128, new String(new byte[]{0x0}));
            m.set(128, "");

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }


    public ISOMsg CreatePurchaseReversalRexConnect_(TranNetInfo tInfoc, TermParamInfo tparams ) {
        System.out.println("*****A*****tInfoc=>" + tInfoc.toString());
        System.out.println("*****B*****tparams=>" + tparams.toString());


        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            //  m.setMTI("0200");
            m.setMTI("0420");

            Log.d(TAG, "BILL-REF: " + tInfoc.getBillRefNo());

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];


            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());

            field23 = ISOUtil.padleft(field23, 3, '0');

            String accx = "00";
			/*if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";*/
            //field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = null, expDate = null, serviceCode = "";

            if (track2Data.contains("D")) {
                tData2 = OtaUtility.tokenize(track2Data, "D")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            } else if (track2Data.contains("=")) {
                tData2 = OtaUtility.tokenize(track2Data, "=")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            }



            System.out.println("**********serviceCode=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(0, 4));


            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
            String ccyCode = tparams.getCurrencyCode();

            String merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            System.out.println("********** .getCardData()=>" + tInfoc.getCardData());

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);


            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            //m.set(3, "01" + accx + "00");
            //   m.set(3, "000000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            m.set(3, tInfoc.getTranType()+"0000");
            //Long amts = new BigDecimal(amt * 100).longValue();


            Long amts = new BigDecimal(amt).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);





//        Date d = new Date();
//
//        ISOMsg m = new ISOMsg();
//        try {
//
//            String mobileNo = tInfoc.getMobileNo();
//            m.setMTI("0420");
//
//            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
//            //tranInfo.setBillRefNo(cdx);
//            //XStream xs = new XStream();
//            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));
//
//
//            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
//
//
//            //String[] cds = tokenize(tInfo.getCardData(), "|");
//            String pan = cds[0]; // ""; // "";
//            //String pin = "";
//            String track2Data = cds[3]; // "";
//            String field55 = cds[4];
//            String field23 = cds[5];
//            String accType = cds[6];
//            String accx = "00";
//            if (accType.equals("1"))
//                accx = "20";
//            else if (accType.equals("2"))
//                accx = "10";
//            else if (accType.equals("3"))
//                accx = "30";
//            field23 = ISOUtil.padleft(field23, 3, '0');
//            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
//            String expDate = tData2.substring(0, 4);
//            String serviceCode = tData2.substring(4, 7);
//
//            String track2Data2 = track2Data; // + "10";//
//            track2Data2 = track2Data2.replace("&#0;", "").trim();
//            if (track2Data2.length() > 37)
//                track2Data2 = track2Data2.substring(0, 37);
//            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;
//
//            double amt = tInfoc.getAmount();
//
//            String tsk = tparams.getTsk();//termInfo.getTsk();
//            String terminalId = tparams.getTerminalId(); // tparams[0];
//            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
//            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
//            String ccyCode = tparams.getCurrencyCode();
//
//
//
//
//
//            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
//            String stan = tInfoc.getExternalRefNo().substring(12);
//
//            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);
//
//            m.set(2, pan);
//            //m.set(3, "001000");
//            m.set(3, "01" + accx + "00");
//            //m.set(3, "00" + accx + "00");
//            Long amts = new BigDecimal(amt * 100).longValue();
//            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
//            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
              merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            m.set(18, merchantNo); //5251"); // for NIBSS "5410"); //6010");// 4"); 6012
            m.set(22, "051");
            //5399237077728398D2307221019244246
            m.set(23, field23); //"001");

                m.set(25, "00"); //"91"); //"00");

//			m.set(26, "12"); //"06");


            m.set(26, "12"); //"06"); // Set Offline PIN
            m.set(28, "D00000000");  //D00000000
            m.set(32, getAcquiringInstitutionIdCode(track2Data)); //"415002"); //  "111111");

            // m.set(32,  "111129"); //  "111111");
            m.set(33, "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);

            m.set(52, pinBlock); // pinBytes);
            m.set(55, field55);
          //  String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
          //  m.set(60, f60);
            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");

            String f90 = "0200" + stan + dx + "00000" + m.getString(32) + "00000" + m.getString(33);
            m.set(90, f90);
            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "C00000000";
            m.set(95, f95);

            m.set(123, "51010151134C101");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);
            System.out.println("********** .getCardData()=>" + "tInfoc.getCardData()");
            System.out.println("********** .getCardData()=>" + m.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }


    public ISOMsg _CreatePurchaseReversalRexConnect(TranNetInfo tInfoc, TermParamInfo tparams ) { //, String posAccountUpsl) {
        System.out.println("*****A*****tInfoc=>" + tInfoc.toString());
        System.out.println("*****B*****tparams=>" + tparams.toString());


        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            //  m.setMTI("0200");
            m.setMTI("0420");

            Log.d(TAG, "BILL-REF: " + tInfoc.getBillRefNo());

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];


            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());

            field23 = ISOUtil.padleft(field23, 3, '0');

            String accx = "00";
			/*if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";*/
            //field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = null, expDate = null, serviceCode = "";

            if (track2Data.contains("D")) {
                tData2 = OtaUtility.tokenize(track2Data, "D")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            } else if (track2Data.contains("=")) {
                tData2 = OtaUtility.tokenize(track2Data, "=")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            }



            System.out.println("**********serviceCode=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(0, 4));


            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
            String ccyCode = tparams.getCurrencyCode();

            String merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            System.out.println("********** .getCardData()=>" + tInfoc.getCardData());

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);


            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            //m.set(3, "01" + accx + "00");
            //   m.set(3, "000000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            m.set(3,  "200000");
            //Long amts = new BigDecimal(amt * 100).longValue();


            Long amts = new BigDecimal(amt).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";DE64  ]
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss

            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, merchantNo); //5251"); // for NIBSS "5410"); //6010");// 4"); 6012
            m.set(22, "051");

          //  m.set(23, field23); //"001");

            m.set(25, "00"); //"91"); //"00");




          //  m.set(26, "12"); //"06"); // Set Offline PIN
         //   m.set(28, "D00000000");  //D00000000
            //m.set(32, getAcquiringInstitutionIdCode(track2Data)); //"415002"); //  "111111");
            track2Data2 = track2Data2.replace('=', 'D');




            m.set(32,"100001");
            //m.set(32,  "111129"); //  "111111");
          //  m.set(33,  "111111");


         //   m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
          //  m.set(40, serviceCode);//"601"); // "221"); 201 serviceCode
            //m.set(40, "221") ;//"601"); // "221"); 201
            m.set(41, terminalId); // "2HIG0106");



            m.set(42, tparams.getCardAcceptorId());
            m.set(43, tparams.getCardAcceptionLocation());
            m.set(49, "566");

            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            Log.e("PINDATA", tInfoc.getCardData());
            //if(retry > 0)
//            if (  16 == tInfoc.getCardData().length())
//                m.set(52, tInfoc.getCardData()); // pinBytes);

     //       m.set(55, field55);
//111111
//           m.set(59, "010101");
            //m.set(59, "Reconciler&gt;GENERIC&amp;Option&gt;000");

            SimpleDateFormat dff4 = new SimpleDateFormat("yyyy");
            String dy = dff4.format(new Date());
//			m.set(59, terminalId +"-"+ rrn + "-" + dy + dx);


// I        <field id="32" value="111129"/>
// I        <field id="33" value="424367"/>



            m.set(56, "4000");
           // m.set(56, "4021");


//            String f90 ="0200" +  stan +  tInfoc.getTranDateX() + "00000" + m.getString(32) + "00000" + m.getString(33);
//            m.set(90, f90);
//            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "C00000000";
//            m.set(95, f95);

         //   m.set(90, "0200"+stan+tInfoc.getTranDateX()+"0000011112900000424367");//510101513344101
          //  m.set(95, "000000000208000000000000D00000000D00000000");//510101513344101






            //            m.set(32,"200013");
//            //m.set(32,  "111129"); //  "111111");
//            m.set(33,  "507870");
//            String f90 ="0200" +  stan +  tInfoc.getTranDateX()      + "00000" + "200013" + "00000"+ "507870" ;
//            m.set(90, f90);
//            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "C00000000";
//            //C00000000C00000000
//            m.set(95, f95);

            String f90 ="0200" +  stan + tInfoc.getTranDateX()  + "00000" + m.getString(32) + "00000111111"  ;
            //String f90 ="0200" +  stan + tInfoc.getTranDateX()  + "00000" + m.getString(32) + "00000" + m.getString(33);
            m.set(90, f90);

            //000000000208000000000000D00000000D00000000
//            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "D00000000";
//            m.set(95, f95);

            String newAMount = "000000000000";
            String f95 = m.getString(4) +newAMount+ "D00000000" + "D00000000";
            m.set(95, f95);


            m.set(123, "51010151134C101");//510101513344101
            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
        }
        return m;
    }






    /*
    reversal
 <send>
   <isomsg direction="outgoing">
     <!-- com.nexgo.apiv3.iso8583.NIBSSPackager -->
     <field id="0" value="0420"/>
     <field id="2" value="5399412006239169"/>
     <field id="3" value="000000"/>
     <field id="4" value="000000000100"/>
     <field id="7" value="0630123652"/>
     <field id="11" value="002334"/>
     <field id="12" value="123543"/>
     <field id="13" value="0630"/>
     <field id="14" value="2309"/>
     <field id="18" value="5251"/>
     <field id="22" value="051"/>
     <field id="23" value="000"/>
     <field id="25" value="00"/>
     <field id="28" value="D00000000"/>
     <field id="32" value="111129"/>
     <field id="35" value="5399412006239169D2309201187212020000"/>
     <field id="37" value="218112002334"/>
     <field id="38" value="492A69"/>
     <field id="40" value="201"/>
     <field id="41" value="2070AL32"/>
     <field id="42" value="FBP204011021396"/>
     <field id="43" value="GLOBAL ACCELEREX TEST  LA           LANG"/>
     <field id="49" value="566"/>
     <field id="55" value="5F3401009F2608D8B35F28683A7E659F2701809F10120110A50003020000000000000000000000FF9F3704487EB5EB9F36020429950504000080009A032206309C01009F02060000000001005F2A020566820239009F1A0205669F03060000000000009F3303E0F8C89F34034103029F3501219F1E0830573230323535329F090200028407A00000000410109F4104000023349F0607A0000000041010"/>
     <field id="56" value="4021"/>
     <field id="59" value="2070AL32-218112002334-20220630123652"/>
     <field id="90" value="020000233406301235430000011112900000557694"/>
     <field id="95" value="000000000100000000000000D00000000D00000000"/>
     <field id="123" value="511101513344101"/>
     <field id="128" value="1A9350EC5140499C3BD9577E270977B6F9BE823E37A19A2762EF03C550599336" type="binary"/>
   </isomsg>
 </send>
/log>
     */







    public ISOMsg XCreatePurchaseReversalRexConnect(TranNetInfo tInfoc, TermParamInfo tparams ) {
        System.out.println("*****A*****tInfoc=>" + tInfoc.toString());
        System.out.println("*****B*****tparams=>" + tparams.toString());


        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            //  m.setMTI("0200");
            m.setMTI("0420");

            Log.d(TAG, "BILL-REF: " + tInfoc.getBillRefNo());

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];


            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());

            field23 = ISOUtil.padleft(field23, 3, '0');

            String accx = "00";
			/*if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";*/
            //field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = null, expDate = null, serviceCode = "";

            if (track2Data.contains("D")) {
                tData2 = OtaUtility.tokenize(track2Data, "D")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            } else if (track2Data.contains("=")) {
                tData2 = OtaUtility.tokenize(track2Data, "=")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            }



            System.out.println("**********serviceCode=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(0, 4));


            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
            String ccyCode = tparams.getCurrencyCode();

            String merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            System.out.println("********** .getCardData()=>" + tInfoc.getCardData());

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);


            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            //m.set(3, "01" + accx + "00");
            //   m.set(3, "000000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            m.set(3,  "000000");
            //Long amts = new BigDecimal(amt * 100).longValue();


            Long amts = new BigDecimal(amt).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";DE64  ]
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, tInfoc.getTranDateX());  //"0327133744");  //MMDDhhmmss

            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));





            m.set(new ISOField(12, tInfoc.getTime()));
            m.set(new ISOField(13, tInfoc.getDate()));



//            m.set(new ISOField(12, ISODate.getTime(d)));
//       //     m.set(new ISOField(13, ISODate.getDate(d)));
//            m.set(new ISOField(13, ISODate.getDate(d)));





            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, merchantNo); //5251"); // for NIBSS "5410"); //6010");// 4"); 6012
            m.set(22, "051");

            //  m.set(23, field23); //"001");

            m.set(25, "00"); //"91"); //"00");




          m.set(26, "12"); //"06"); // Set Offline PIN
               m.set(28, "D00000000");  //D00000000
            //m.set(32, getAcquiringInstitutionIdCode(track2Data)); //"415002"); //  "111111");
            track2Data2 = track2Data2.replace('=', 'D');



            /*
            <field id="32" value="200017"/>
 I        <field id="33" value="507870"/>
             */

            m.set(32,"111129");
            //m.set(32,  "111129"); //  "111111");
     // m.set(33,  "507870");


         m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(38, tInfoc.getF38());


            //  m.set(40, serviceCode);//"601"); // "221"); 201 serviceCode
             m.set(40, "226") ;//"601"); // "221"); 201
            m.set(41, terminalId); // "2HIG0106");



            m.set(42, tparams.getCardAcceptorId());
            m.set(43,  tparams.getCardAcceptionLocation());
            m.set(49, "566");

            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            Log.e("PINDATA", tInfoc.getCardData());
            //if(retry > 0)
//            if (  16 == tInfoc.getCardData().length())
//                m.set(52, tInfoc.getCardData()); // pinBytes);

            //       m.set(55, field55);
//111111
//           m.set(59, "010101");
            //m.set(59, "Reconciler&gt;GENERIC&amp;Option&gt;000");

            SimpleDateFormat dff4 = new SimpleDateFormat("yyyy");
            String dy = dff4.format(new Date());
//			m.set(59, terminalId +"-"+ rrn + "-" + dy + dx);


// I        <field id="32" value="111129"/>
// I        <field id="33" value="424367"/>



            m.set(56, "4021");
            // m.set(56, "4021");


          //  String f90 ="0200" +  stan +  tInfoc.getTranDateX() + "00000" + m.getString(32) + "00000" + m.getString(33);
        //    String f90 ="0200" +  stan +  tInfoc.getTranDateX() +"0000011112900000557694";
            String f90 ="0200" +  stan +  tInfoc.getTranDateX() +"0000000000000000000000";
//            m.set(90, f90);
//            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "C00000000";
//            m.set(95, f95);

            //   m.set(90, "0200"+stan+tInfoc.getTranDateX()+"0000011112900000424367");//510101513344101
            //  m.set(95, "000000000208000000000000D00000000D00000000");//510101513344101






            //            m.set(32,"200013");
//            //m.set(32,  "111129"); //  "111111");
//            m.set(33,  "507870");
//            String f90 ="0200" +  stan +  tInfoc.getTranDateX()      + "00000" + "200013" + "00000"+ "507870" ;
//            m.set(90, f90);
//            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "C00000000";
//            //C00000000C00000000
//            m.set(95, f95);

           // String f90 ="0200" +  stan + tInfoc.getTranDateX()  + "00000" + m.getString(32) + "00000111111"  ;
            //String f90 ="0200" +  stan + tInfoc.getTranDateX()  + "00000" + m.getString(32) + "00000" + m.getString(33);
            m.set(90, f90);

            //000000000208000000000000D00000000D00000000
//            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "D00000000";
//            m.set(95, f95);

            String newAMount = "000000000000";
            String f95 = m.getString(4) +newAMount+ "D00000000" + "D00000000";
            m.set(95, f95);


            m.set(123, "51010151134C101");//510101513344101
            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
        }
        return m;
    }
//, int retry, boolean onlinePin,String m1
    public ISOMsg CreatePurchaseReversal3Line(TranNetInfo tInfoc, TermParamInfo tparams) { //, String posAccountUpsl) {
        System.out.println("*****A*****tInfoc=>" + tInfoc.toString());
        System.out.println("*****B*****tparams=>" + tparams.toString());


        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {
            m.setPackager(new PosPackager());

            //  m.setMTI("0200");
          //  m.setMTI(m1);
            m.setMTI("0420");
            Log.d(TAG, "BILL-REF: " + tInfoc.getBillRefNo());

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];


            System.out.println("cds0==>" + cds[0]);
            System.out.println("cds1==>" + cds[1]);
            System.out.println("cds=2=>" + cds[2]);
            System.out.println("cds3==>" + cds[3]);
            System.out.println("cds4==>" + cds[4]);
            System.out.println("cds=5=>" + cds[5]);
            System.out.println("cds=6=>" + cds[6]);

            System.out.println("tparams =>" + tparams.toString());

            field23 = ISOUtil.padleft(field23, 3, '0');

            String accx = "00";
			/*if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";*/
            //field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = null, expDate = null, serviceCode = "";

            if (track2Data.contains("D")) {
                tData2 = OtaUtility.tokenize(track2Data, "D")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            } else if (track2Data.contains("=")) {
                tData2 = OtaUtility.tokenize(track2Data, "=")[1];
                expDate = tData2.substring(0, 4);
                serviceCode = tData2.substring(4, 7);
            }



            System.out.println("**********serviceCode=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(4, 7));
            System.out.println("**********expDate=>" + tData2.substring(0, 4));


            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];
            String ccyCode = tparams.getCurrencyCode();

            String merchantNo = tparams.getMcc();
            System.out.println("**********merchantNo=>" + merchantNo);
            System.out.println("********** .getCardData()=>" + tInfoc.getCardData());

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);


            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL
            //m.set(3, "01" + accx + "00");
            //   m.set(3, "000000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            m.set(3, tInfoc.getTranType()+"0000");
            //Long amts = new BigDecimal(amt * 100).longValue();


            Long amts = new BigDecimal(amt).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";DE64  ]
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss

            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));



            m.set(new ISOField(12, tInfoc.getTime()));
            m.set(new ISOField(13, tInfoc.getDate()));

//            m.set(new ISOField(12, ISODate.getTime(d)));
//            m.set(new ISOField(13, ISODate.getDate(d)));



            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, merchantNo); //5251"); // for NIBSS "5410"); //6010");// 4"); 6012
            m.set(22, "051");

            m.set(23, field23); //"001");

            m.set(25, "00"); //"91"); //"00");




            m.set(26, "12"); //"06"); // Set Offline PIN
            m.set(28, "D00000000");  //D00000000
            //  m.set(32, getAcquiringInstitutionIdCode(track2Data)); //"415002"); //  "111111");

//            m.set(32,"100001");
//            //m.set(32,  "111129"); //  "111111");
//            m.set(33,  "111111");

//            "32" value="200017"/>\
//        id="33" value="557694"/>\

            m.set(32,"111129");
            //m.set(32,  "111129"); //  "111111");
            m.set(33,  "557694");


            track2Data2 = track2Data2.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);


            m.set(40, serviceCode);//"601"); // "221"); 201 serviceCode
            //m.set(40, "221") ;//"601"); // "221"); 201
            m.set(41, terminalId); // "2HIG0106");



            m.set(42, tparams.getCardAcceptorId());
            m.set(43, tparams.getCardAcceptionLocation());
            m.set(49, "566");

            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            Log.e("PINDATA", tInfoc.getCardData());
            //if(retry > 0)
         //   if (onlinePin && 16 == tInfoc.getCardData().length())
              //  m.set(52, tInfoc.getCardData()); // pinBytes);

            m.set(55, field55);

            //m.set(59, "Reconciler&gt;GENERIC&amp;Option&gt;000");

            SimpleDateFormat dff4 = new SimpleDateFormat("yyyy");
            String dy = dff4.format(new Date());
//			m.set(59, terminalId +"-"+ rrn + "-" + dy + dx);


                 String f90 = "0200" + stan + tInfoc.getTranDateX() + "00000" + m.getString(32) + "00000" + m.getString(33);
                m.set(90, f90);




            m.set(123, "51010151134C101");//510101513344101

//			m.set(128, new String(new byte[]{0x0}));
            m.set(128, "");

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
        }
        return m;
    }

    public static String getAcquiringInstitutionIdCode(String track2Data) {
        return track2Data.substring(0, 6);
    }

//	public ISOMsg CreateCashAdvanceRefundDepositCashbackMessage(TranNetInfo tInfoc, String[] tparams, String tranCode) {
//		Date d = new Date();
//
//		ISOMsg m = new ISOMsg();
//		try {
//
//			String mobileNo = tInfoc.getMobileNo();
//			m.setMTI("0200");
//
//            if(tranCode.equals("60")) //Pre-Auth
//            {
//                m.setMTI("0100");
//            }
//            else if(tranCode.equals("61")) //Pre-Authorization Sale Completion
//			{
//				m.setMTI("0220");
//			}
//
//
//            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
//
//
//			//String[] cds = tokenize(tInfo.getCardData(), "|");
//			String pan =  cds[0]; // ""; // "";
//			//String pin = "";
//			String track2Data =  cds[3]; // "";
//			String field55 = cds[4];
//			String field23 = cds[5];
//			String accType = cds[6];
//			String accx = "00";
//			if(accType.equals("1"))
//				accx ="20";
//			else if(accType.equals("2"))
//				accx ="10";
//			else if(accType.equals("3"))
//				accx ="30";
//			field23 = ISOUtil.padleft(field23, 3, '0');
//			String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
//			String expDate = tData2.substring(0,4);
//			String serviceCode =tData2.substring(4,7);
//
//			String track2Data2 = track2Data; // + "10";//
//			track2Data2 = track2Data2.replace("&#0;", "").trim();
//			if(track2Data2.length() > 37)
//				track2Data2 = track2Data2.substring(0,37);
//			//Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;
//
//			double amt = tInfoc.getAmount();
//
//
//
//			String tsk = tparams[3];  //termInfo.getTsk();
//			//String serverIP = tparams[6];
//			//int port = Integer.parseInt(tparams[7]);
//			String terminalId = tparams[0];
//			String cardAcceptorId = tparams[5];
//			String cardAcceptorLocation = tparams[8];
//
//			String rrn = tInfoc.getExternalRefNo().substring(0,12);
//			String stan = tInfoc.getExternalRefNo().substring(12);
//
//			String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);
//
//			m.set(2, pan);
//			m.set(3, tranCode + "0000");
//			//m.set(3, "01" + accx + "00");  //Purchase
//
//			//m.set(3, "01" + accx + "00");  //Cash Advance
//			//m.set(3, "09" + accx + "00");  //Purchase with Cash back
//			//m.set(3, "20" + accx + "00");  //Refund
//
//			//m.set(3, "00" + accx + "00");
//			Long amts = new BigDecimal(amt * 100).longValue();
//			String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
//			m.set(4, amt3);
//
//			Date date = new Date();
//			String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
//			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//			String dx = sdf.format(date);
//			System.out.println("Today is " + dx);
//			m.set(7, dx);  //"0327133744");  //MMDDhhmmss
//			m.set(11, stan); //new ISOField(11,
//			// ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
//			m.set(new ISOField(12, ISODate.getTime(d)));
//			m.set(new ISOField(13, ISODate.getDate(d)));
//			m.set(14,expDate);
//			//m.set(new ISOField(15, ISODate.getDate(d)));
//			m.set(18, "6014");
//			m.set(22, "051");
//			m.set(23, field23); //"001");
//			m.set(25, "00");
//			m.set(26, "04"); //"06");
//			m.set(28, "C00000000");
//			//m.set(32,  "111129"); //  "111111");
//			m.set(32,  "111129"); //  "111111");
//			m.set(33,  "111111");
//			//track2Data = track2Data.replace('=', 'D');
//			m.set(35,  track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
//			m.set(37, rrn);
//			m.set(40, serviceCode) ;//"601"); // "221");
//			m.set(41,  terminalId) ; // "2HIG0106");
//			m.set(42,  cardAcceptorId) ;// "2HIGP010000P010");
//			m.set(43,  cardAcceptorLocation) ; // "2HIGP010 PHLEX LAGOS                LANG");
//			m.set(49, "566");
//			//byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
//			if(pinBlock.length() > 16)
//				pinBlock = pinBlock.substring(0,16);
//
//			m.set(52,pinBlock); // pinBytes);
//			m.set(55,field55);
//			String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
//			//	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
//			m.set(60, f60);
//			//	m.set(62,"00698WD0101333" + mobileNo);
//			//m.set(98, "High   Street   MFB      ");
//			//m.set(100, "506180");
//
//			// m.set(103,"0001189745");
//			m.set(123, "510101513344101");
//
//			m.set(128, new String(new byte[]{0x0}));
//
//			String f128 = generateHashForIsoMsg(m, tsk);
//			m.set(128, f128);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return m;
//	}
//
//    public ISOMsg CreatePurchaseMessageUPSL(TranNetInfo tInfoc, String[] tparams,String zmk, Context ctx) {
//        Date d = new Date();
//
//        ISOMsg m = new ISOMsg();
//        try {
//			InputStream is = ctx.getAssets().open("postpack.xml");
//
//			GenericPackager packager = new GenericPackager(is);
//         	m.setPackager(packager);
//
//
//            String mobileNo = tInfoc.getMobileNo();
//            m.setMTI("0200");
//
//            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
//            //tranInfo.setBillRefNo(cdx);
//            //XStream xs = new XStream();
//            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));
//
//
//
//            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");
//
//
//            //String[] cds = tokenize(tInfo.getCardData(), "|");
//            String pan =  cds[0]; // ""; // "";
//            //String pin = "";
//            String track2Data =  cds[3]; // "";
//            String field55 = cds[4];
//            String field23 = cds[5];
//            String accType = cds[6];
//            String accx = "00";
//            if(accType.equals("1"))
//                accx ="20";
//            else if(accType.equals("2"))
//                accx ="10";
//            else if(accType.equals("3"))
//                accx ="30";
//            field23 = ISOUtil.padleft(field23, 3, '0');
//            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
//            String expDate = tData2.substring(0,4);
//            String serviceCode =tData2.substring(4,7);
//
//            String track2Data2 = track2Data; // + "10";//
//            track2Data2 = track2Data2.replace("&#0;", "").trim();
//            if(track2Data2.length() > 37)
//                track2Data2 = track2Data2.substring(0,37);
//            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;
//
//            double amt = tInfoc.getAmount();
//
//            String tsk = tparams[3];  //termInfo.getTsk();
//            //String serverIP = tparams[6];
//            //int port = Integer.parseInt(tparams[7]);
//            String terminalId = tparams[0];
//            String cardAcceptorId = tparams[5];
//            String cardAcceptorLocation = tparams[8];
//
//            String rrn = tInfoc.getExternalRefNo().substring(0,12);
//            String stan = tInfoc.getExternalRefNo().substring(12);
//
//            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);
//
//            m.set(2, pan);
//            m.set(3, "001000");
//           // m.set(3, "00" + accx + "00");  //Purchase
//
//            //m.set(3, "01" + accx + "00");  //Cash Advance
//            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
//            //m.set(3, "20" + accx + "00");  //Refund
//
//            //m.set(3, "00" + accx + "00");
//            Long amts = new BigDecimal(amt * 100).longValue();
//            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
//            m.set(4, amt3);
//
//            Date date = new Date();
//            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
//            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//            String dx = sdf.format(date);
//            System.out.println("Today is " + dx);
//            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
//            m.set(11, stan); //new ISOField(11,
//            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
//            m.set(new ISOField(12, ISODate.getTime(d)));
//            m.set(new ISOField(13, ISODate.getDate(d)));
//            m.set(14,expDate);
//            m.set(new ISOField(15, ISODate.getDate(d)));
//            m.set(18, "6014");
//            m.set(22, "051");
//            m.set(23, field23); //"001");
//            m.set(25, "00");
//            m.set(26, "04"); //"06");
//            m.set(28, "C00000000");
//            m.set(32,  "111129"); //  "111111");
//            //track2Data = track2Data.replace('=', 'D');
//            m.set(35,  track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
//            m.set(37, rrn);
//            m.set(40, serviceCode) ;//"601"); // "221");
//            m.set(41,  terminalId) ; // "2HIG0106");
//            m.set(42,  cardAcceptorId) ;// "2HIGP010000P010");
//            m.set(43,  cardAcceptorLocation) ; // "2HIGP010 PHLEX LAGOS                LANG");
//            m.set(49, "566");
//            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
//            if(pinBlock.length() > 16)
//                pinBlock = pinBlock.substring(0,16);
//
//           // m.set(52,pinBlock); // pinBytes);
//            Log.d(TAG,"field 555:" + field55);
//
//            //m.set(55,PinBlockEncryptionUtil.EncryptMessage(field55,zmk));
//            String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
//            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
//			m.set(56,"1510");
//           // m.set(60, f60);
//            //	m.set(62,"00698WD0101333" + mobileNo);
//            //m.set(98, "High   Street   MFB      ");
//            //m.set(100, "506180");
//
//            // m.set(103,"0001189745");
//            m.set(103,"87001001");
//            m.set(123, "510101513344101");
//            String hexData = field55;  //ISOUtil.hexString(field55.getBytes());
//
//            //m.set("127.25", field55); // PinBlockEncryptionUtil.EncryptMessage(hexData,zmk));
//
//			ISOMsg inner = new ISOMsg(127);
//				inner.set(2,"000000000400");
//				inner.set(3,"                        001156001156            ");
//				inner.set(25,field55);
//			    m.set(inner);
//
//			//new GenericPackager("C:\\JAVA\\jpos-1.7.0\\jpos-1.7.0\\cfg\\packager\\postpack.xml"); // PosPackager());   //ISO87APackager())
//
//           // m.set(128, new String(new byte[]{0x0}));
//
//            String f128 = generateHashForIsoMsg2(m, tsk);
//            //m.set(128, f128);
//
//           // String packData = new String(m.pack());
//
//           // Log.d(TAG,"ISO-PACK-PURCHASE:  " + packData);
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return m;
//    }


    public ISOMsg CreatePurchaseMessageUPSLNew(TranNetInfo tInfoc, String[] tparams, String zmk, Context ctx) {
        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {
            InputStream is = ctx.getAssets().open("postpack.xml");

            GenericPackager packager = new GenericPackager(is);
            m.setPackager(packager);


            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0200");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));


            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];
            String accx = "00";
            if (accType.equals("1"))
                accx = "20";
            else if (accType.equals("2"))
                accx = "10";
            else if (accType.equals("3"))
                accx = "30";
            field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
            String expDate = tData2.substring(0, 4);
            String serviceCode = tData2.substring(4, 7);

            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();

            String tsk = tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams[0];
            String cardAcceptorId = tparams[5];
            String cardAcceptorLocation = tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            m.set(3, "001000");
            // m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            //m.set(3, "00" + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "6014");
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00");
            m.set(26, "04"); //"06");
            m.set(28, "C00000000");
            m.set(32, "111129"); //  "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);

            // m.set(52,pinBlock); // pinBytes);
            Log.d(TAG, "field 555:" + field55);

            //m.set(55,PinBlockEncryptionUtil.EncryptMessage(field55,zmk));
            String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
            m.set(56, "1510");
            // m.set(60, f60);
            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");
            m.set(103, "87001001");
            m.set(123, "510101513344101");
            String hexData = field55;  //ISOUtil.hexString(field55.getBytes());

            //m.set("127.25", field55); // PinBlockEncryptionUtil.EncryptMessage(hexData,zmk));

            ISOMsg inner = new ISOMsg(127);
            inner.set(2, "0106200448002004480106200448");
            inner.set(3, "                        001156001156            ");

            m.set("127.2", "0106200448002004480106200448");
            m.set("127.3", "                        200448200448");
            m.set("127.13", "     000000   566");
            m.set("127.20", "19800106");

            //"<?xml version=\"1.0\" encoding=\"utf-8\"?><IccData><IccRequest>" +
            //	"<AmountAuthorized>000000000210</AmountAuthorized><AmountOther>000000000000</AmountOther><ApplicationInterchangeProfile>3900</ApplicationInterchangeProfile><ApplicationTransactionCounter>0076</ApplicationTransactionCounter><Cryptogram>C7ADFF959253C60D</Cryptogram><CryptogramInformationData>80</CryptogramInformationData><CvmResults>410302</CvmResults><IssuerApplicationData>0110A50003020000000000000000000000FF</IssuerApplicationData><TerminalCapabilities>E0F8C8</TerminalCapabilities><TerminalCountryCode>566</TerminalCountryCode><TerminalType>22</TerminalType><TerminalVerificationResult>0480000000</TerminalVerificationResult><TransactionCurrencyCode>566</TransactionCurrencyCode><TransactionDate>210518</TransactionDate><TransactionType>00</TransactionType><UnpredictableNumber>00ABDB62</UnpredictableNumber></IccRequest></IccData>6008->
            //if (m.hasField(55)) {
            TLVList tlv = new TLVList();
            tlv.unpack(field55.getBytes()); //m.getBytes(55));
            m.set("127.25", buildRequestICCData(tlv, m));
            //inner.unset(55);
            //}
            //inner.set(25,field55);
            //m.set(inner);

            //new GenericPackager("C:\\JAVA\\jpos-1.7.0\\jpos-1.7.0\\cfg\\packager\\postpack.xml"); // PosPackager());   //ISO87APackager())

            // m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg2(m, tsk);
            //m.set(128, f128);

            // String packData = new String(m.pack());

            // Log.d(TAG,"ISO-PACK-PURCHASE:  " + packData);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }


    private String buildRequestICCData(TLVList tlv, ISOMsg isoMsg) {

        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<IccData><IccRequest>");

        String type = isoMsg.getString(3).substring(0, 2);
        //for BI override
        if ("31".equals(type)) {
            sb.append(String.format("<AmountAuthorized>%s</AmountAuthorized>", "000000000000"));
        } else {
            if (tlv.hasTag(0x9F02))
                sb.append(String.format("<AmountAuthorized>%s</AmountAuthorized>", tlv.getString(0x9F02)));
        }


        if (tlv.hasTag(0x9F03))
            sb.append(String.format("<AmountOther>%s</AmountOther>", tlv.getString(0x9F03)));

        if (tlv.hasTag(0x4F))
            sb.append(String.format("<ApplicationIdentifier>%s</ApplicationIdentifier>", tlv.getString(0x4F)));

        if (tlv.hasTag(0x82))
            sb.append(String.format("<ApplicationInterchangeProfile>%s</ApplicationInterchangeProfile>", tlv.getString(0x82)));

        if (tlv.hasTag(0x9F36))
            sb.append(String.format("<ApplicationTransactionCounter>%s</ApplicationTransactionCounter>", tlv.getString(0x9F36)));

        if (tlv.hasTag(0x9F07))
            sb.append(String.format("<ApplicationUsageControl>%s</ApplicationUsageControl>", tlv.getString(0x9F07)));

        if (tlv.hasTag(0x9F26))
            sb.append(String.format("<Cryptogram>%s</Cryptogram>", tlv.getString(0x9F26)));

        if (tlv.hasTag(0x9F27))
            sb.append(String.format("<CryptogramInformationData>%s</CryptogramInformationData>", tlv.getString(0x9F27)));

        if (tlv.hasTag(0x8E))
            sb.append(String.format("<CvmList>%s</CvmList>", tlv.getString(0x8E)));

        if (tlv.hasTag(0x9F34))
            sb.append(String.format("<CvmResults>%s</CvmResults>", tlv.getString(0x9F34)));

        if (tlv.hasTag(0x9F1E))
            sb.append(String.format("<InterfaceDeviceSerialNumber>%s</InterfaceDeviceSerialNumber>", tlv.getString(0x9F1E)));

        if (tlv.hasTag(0x9F10))
            sb.append(String.format("<IssuerApplicationData>%s</IssuerApplicationData>", tlv.getString(0x9F10)));

        if (tlv.hasTag(0x9F08))
            sb.append(String.format("<TerminalApplicationVersionNumber>%s</TerminalApplicationVersionNumber>", tlv.getString(0x9F08)));

        if (tlv.hasTag(0x9F33))
            sb.append(String.format("<TerminalCapabilities>%s</TerminalCapabilities>", tlv.getString(0x9F33)));

        if (tlv.hasTag(0x9F1A))
            sb.append(String.format("<TerminalCountryCode>%s</TerminalCountryCode>", tlv.getString(0x9F1A)));

        if (tlv.hasTag(0x9F35))
            sb.append(String.format("<TerminalType>%s</TerminalType>", tlv.getString(0x9F35)));

        if (tlv.hasTag(0x95))
            sb.append(String.format("<TerminalVerificationResult>%s</TerminalVerificationResult>", tlv.getString(0x95)));

        if (tlv.hasTag(0x9F53))
            sb.append(String.format("<TransactionCategoryCode>%s</TransactionCategoryCode>", tlv.getString(0x9F53)));

        if (tlv.hasTag(0x5F2A))
            sb.append(String.format("<TransactionCurrencyCode>%s</TransactionCurrencyCode>", tlv.getString(0x5F2A)));

        if (tlv.hasTag(0x9A))
            sb.append(String.format("<TransactionDate>%s</TransactionDate>", tlv.getString(0x9A)));

        appendICCTag(sb, tlv, 0x9F41, "TransactionSequenceCounter");
        //for BI override
        if ("31".equals(type)) {
            appendICCTag(sb, "00", "TransactionType");
        } else {
            appendICCTag(sb, tlv, 0x9C, "TransactionType");
        }

        if (tlv.hasTag(0x9F37))
            sb.append(String.format("<UnpredictableNumber>%s</UnpredictableNumber>", tlv.getString(0x9F37)));

        sb.append("</IccRequest></IccData>");

        return sb.toString();
    }

    private static void appendICCTag(StringBuilder sb, TLVList tlv, int tag, String elementName) {

        if (tlv.hasTag(tag))
            sb.append(String.format("<%s>%s</%s>",
                    elementName, tlv.getString(tag), elementName));
    }

    private static void appendICCTag(StringBuilder sb, String value, String elementName) {

        if (value != null)
            sb.append(String.format("<%s>%s</%s>",
                    elementName, value, elementName));
    }

    public ISOMsg CreatePayattitudeMessage(TranNetInfo tInfoc, TermParamInfo tparams) { // String[] tparams, String posAccountUpsl) {
        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0200");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));


            //String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            //String pan =  cds[0]; // ""; // "";
            //String pin = "";
			/*String track2Data =  cds[3]; // "";
			String field55 = cds[4];
			String field23 = cds[5];
			String accType = cds[6];
			String accx = "00";
			if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";
			field23 = ISOUtil.padleft(field23, 3, '0');
			String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
			String expDate = tData2.substring(0,4);
			String serviceCode =tData2.substring(4,7);

			String track2Data2 = track2Data; // + "10";//
			track2Data2 = track2Data2.replace("&#0;", "").trim();
			if(track2Data2.length() > 37)
				track2Data2 = track2Data2.substring(0,37);*/
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, "9501000000000001"); //pan);
            m.set(3, "010000"); // + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, "2512"); //expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "6014");
            m.set(22, "00");
            //m.set(22, "051");
            m.set(23, "002"); //field23); //"001");
            m.set(25, "00");
            m.set(26, "04"); //"06");
            m.set(28, "C00000000");
            m.set(32, "111129"); //  "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, "9501000000000001D3012"); // track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, "601"); //serviceCode) ;//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            //if(pinBlock.length() > 16)
            //pinBlock = pinBlock.substring(0,16);

            //m.set(52,pinBlock); // pinBytes);
            //m.set(55,field55);

            String posAccountUpsl = tparams.getPoolAccount();

            if (posAccountUpsl == null || posAccountUpsl.equals(""))
                posAccountUpsl = "0001451023";

            //if(posAccountUpsl == null || posAccountUpsl.equals(""))
            //posAccountUpsl ="1234567890";

            //posAccountUpsl = "1774691015";

            //String f60 = "010085C24300148041Meter Number=12.87001004.Acct=" + posAccountUpsl + ".Phone=" + mobileNo;


            // m.set(60, "010083K07226448041Static Number=12.87001001.Acct=" + posAccountUpsl + ".Phone=" + mobileNo);
            m.set(60, "010083K07226448041Static Number=12.87001004.Acct=" + posAccountUpsl + ".Phone=" + mobileNo);


            //m.set(60, "010083K07226448041Static Number=12.57001214.Acct=0001451023.Phone=" + mobileNo);
            m.set(62, "00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");
            m.set(123, "510101513344101");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }
    public ISOMsg CreatePayattitudeMessageBLC(TranNetInfo tInfoc, TermParamInfo tparams) { // String[] tparams, String posAccountUpsl) {
        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0100");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));


            //String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            //String pan =  cds[0]; // ""; // "";
            //String pin = "";
			/*String track2Data =  cds[3]; // "";
			String field55 = cds[4];
			String field23 = cds[5];
			String accType = cds[6];
			String accx = "00";
			if(accType.equals("1"))
				accx ="20";
			else if(accType.equals("2"))
				accx ="10";
			else if(accType.equals("3"))
				accx ="30";
			field23 = ISOUtil.padleft(field23, 3, '0');
			String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
			String expDate = tData2.substring(0,4);
			String serviceCode =tData2.substring(4,7);

			String track2Data2 = track2Data; // + "10";//
			track2Data2 = track2Data2.replace("&#0;", "").trim();
			if(track2Data2.length() > 37)
				track2Data2 = track2Data2.substring(0,37);*/
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, "9501000000000001"); //pan);
            m.set(3, "310000"); // + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
         //   m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, "2512"); //expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "6014");
            m.set(22, "00");
            //m.set(22, "051");
            m.set(23, "002"); //field23); //"001");
            m.set(25, "00");
            m.set(26, "04"); //"06");
            m.set(28, "C00000000");
            m.set(32, "111129"); //  "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, "9501000000000001D3012"); // track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, "601"); //serviceCode) ;//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            //if(pinBlock.length() > 16)
            //pinBlock = pinBlock.substring(0,16);

            //m.set(52,pinBlock); // pinBytes);
            //m.set(55,field55);

            String posAccountUpsl = tparams.getPoolAccount();

            if (posAccountUpsl == null || posAccountUpsl.equals(""))
                posAccountUpsl = "0001451023";

            //if(posAccountUpsl == null || posAccountUpsl.equals(""))
            //posAccountUpsl ="1234567890";

            //posAccountUpsl = "1774691015";

            //String f60 = "010085C24300148041Meter Number=12.87001004.Acct=" + posAccountUpsl + ".Phone=" + mobileNo;


            // m.set(60, "010083K07226448041Static Number=12.87001001.Acct=" + posAccountUpsl + ".Phone=" + mobileNo);
            m.set(60, "010083K07226448041Static Number=12.87001004.Acct=" + posAccountUpsl + ".Phone=" + mobileNo);


            //m.set(60, "010083K07226448041Static Number=12.57001214.Acct=0001451023.Phone=" + mobileNo);
            m.set(62, "00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");
            m.set(123, "510101513344101");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public ISOMsg CreatePurchaseReversalMessage(TranNetInfo tInfoc, String[] tparams, String dx) {
        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0420");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));


            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];
            String accx = "00";
            if (accType.equals("1"))
                accx = "20";
            else if (accType.equals("2"))
                accx = "10";
            else if (accType.equals("3"))
                accx = "30";
            field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
            String expDate = tData2.substring(0, 4);
            String serviceCode = tData2.substring(4, 7);

            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            String tsk = tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams[0];
            String cardAcceptorId = tparams[5];
            String cardAcceptorLocation = tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            //m.set(3, "001000");
            m.set(3, "01" + accx + "00");
            //m.set(3, "00" + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            //Date date = new Date();
            //String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            //SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            //String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "6014");
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00");
            m.set(26, "04"); //"06");
            m.set(28, "C00000000");
            //m.set(32,  "111129"); //  "111111");
            m.set(32, "111129"); //  "111111");
            m.set(33, "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);

            m.set(52, pinBlock); // pinBytes);
            m.set(55, field55);
            String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
            m.set(60, f60);
            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");

            String f90 = "0200" + stan + dx + "00000" + m.getString(32) + "00000" + m.getString(33);
            m.set(90, f90);
            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "C00000000";
            m.set(95, f95);

            m.set(123, "510101513344101");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public ISOMsg CreatePreAuthSaleCompletionMessage(TranNetInfo tInfoc, String[] tparams, String dx) {
        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0220");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));


            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");

            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];
            String accx = "00";
            if (accType.equals("1"))
                accx = "20";
            else if (accType.equals("2"))
                accx = "10";
            else if (accType.equals("3"))
                accx = "30";
            field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
            String expDate = tData2.substring(0, 4);
            String serviceCode = tData2.substring(4, 7);

            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            String tsk = tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams[0];
            String cardAcceptorId = tparams[5];
            String cardAcceptorLocation = tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            m.set(3, "610000");
            //m.set(3, "01" + accx + "00");
            //m.set(3, "00" + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            //Date date = new Date();
            //String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            //SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            //String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "6014");
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00");
            m.set(26, "04"); //"06");
            m.set(28, "C00000000");
            //m.set(32,  "111129"); //  "111111");
            m.set(32, "111129"); //  "111111");
            m.set(33, "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);

            m.set(52, pinBlock); // pinBytes);
            m.set(55, field55);
            String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
            m.set(60, f60);
            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");

            String f90 = "0100" + stan + dx + "00000" + m.getString(32) + "00000" + m.getString(33);
            m.set(90, f90);
            String f95 = m.getString(4) + m.getString(4) + "D00000000" + "C00000000";
            m.set(95, f95);

            m.set(123, "510101513344101");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public ISOMsg CreateBalanceInquiryMessage(TranNetInfo tInfoc, String[] tparams) {
        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0100");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));


            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];
            String accx = "00";
            if (accType.equals("1"))
                accx = "20";
            else if (accType.equals("2"))
                accx = "10";
            else if (accType.equals("3"))
                accx = "30";
            field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
            String expDate = tData2.substring(0, 4);
            String serviceCode = tData2.substring(4, 7);

            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            String tsk = tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams[0];
            String cardAcceptorId = tparams[5];
            String cardAcceptorLocation = tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            //m.set(3, "001000");
            m.set(3, "31" + "0000");
            //m.set(3, "00" + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "6014");
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00");
            m.set(26, "04"); //"06");
            m.set(28, "C00000000");
            m.set(32, "111129"); //  "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);

            m.set(52, pinBlock); // pinBytes);
            m.set(55, field55);
            String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
            m.set(60, f60);
            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");
            m.set(123, "510101513344101");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public ISOMsg CreatePurchaseMessageNIBSS(TranNetInfo tInfoc, TermParamInfo tparams) {
        return new ISOMsg();
    }

    public ISOMsg CreatePurchaseMessageNIBSS(TranNetInfo tInfoc, String terminalId, String tsk, String cardAcceptorId, String cardAcceptorLocation, String isodt) {
        // Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0200");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];
            String accx = "00";
            if (accType.equals("1"))
                accx = "20";
            else if (accType.equals("2"))
                accx = "10";
            else if (accType.equals("3"))
                accx = "30";
            field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
            String expDate = tData2.substring(0, 4);
            String serviceCode = tData2.substring(4, 7);

            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            //String tsk = tparams.getTsk(); // tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            //String terminalId = tparams.getTerminalId(); // tparams[0];
            //String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            //String cardAcceptorLocation = tparams.getCardAcceptionLocation(); // tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            //m.set(3, "001000");
            m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            //m.set(3, "00" + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            String dx = isodt.substring(4);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            //m.set(new ISOField(12, ISODate.getTime(d)));
            //m.set(new ISOField(13, ISODate.getDate(d)));

            m.set(new ISOField(12, isodt.substring(8)));
            m.set(new ISOField(13, isodt.substring(4, 8)));

            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "5011"); //"6014");
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00");
            m.set(26, "04"); //"06");
            m.set(28, "C00000000");
            // m.set(32,  "111129"); //  "111111");
            m.set(32, "100001");
            m.set(33, "111111");

            track2Data2 = track2Data2.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);

            m.set(52, pinBlock); // pinBytes);
            m.set(55, field55);
            String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
            // m.set(60, f60);
            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");
            m.set(123, "510101513344101");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

    public ISOMsg CreatePurchaseMessageUPSL(TranNetInfo tInfoc, String terminalId, String tsk, String cardAcceptorId, String cardAcceptorLocation, String isodt, String posAccountUpsl) {

        return CreatePurchaseMessageUPSL(tInfoc, terminalId, tsk, cardAcceptorId, cardAcceptorLocation, isodt, posAccountUpsl, true);

    }

    public ISOMsg CreatePurchaseMessageUPSL(TranNetInfo tInfoc, String terminalId, String tsk, String cardAcceptorId, String cardAcceptorLocation, String isodt, String posAccountUpsl, boolean isOfflinePIN) {
        // Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0200");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));

            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];
            String accx = "00";
            if (accType.equals("1"))
                accx = "20";
            else if (accType.equals("2"))
                accx = "10";
            else if (accType.equals("3"))
                accx = "30";
            field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
            String expDate = tData2.substring(0, 4);
            String serviceCode = tData2.substring(4, 7);

            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            //String tsk = tparams.getTsk(); // tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            //String terminalId = tparams.getTerminalId(); // tparams[0];
            //String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            //String cardAcceptorLocation = tparams.getCardAcceptionLocation(); // tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            //m.set(3, "001000");
            //m.set(3, "00" + accx + "00");  //Purchase
            m.set(3, "010000"); //UPSL

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            //m.set(3, "00" + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            String dx = isodt.substring(4);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            //m.set(new ISOField(12, ISODate.getTime(d)));
            //m.set(new ISOField(13, ISODate.getDate(d)));

            m.set(new ISOField(12, isodt.substring(8)));
            m.set(new ISOField(13, isodt.substring(4, 8)));

            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "6010");// 4");
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00");
            m.set(26, "06"); //"06");
            m.set(28, "C00000000");
            m.set(32, "111129"); //
            // m.set(32,  "111129"); //  "111111");

            //m.set(33,  "111111");

            //track2Data = track2Data.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            Log.d(TAG, "Card acceptor id" + cardAcceptorId);
            m.set(43, "GLOBAL ACCELEREX LIM    LA          LANG"); // "2HIGP010 PHLEX LAGOS     cardAcceptorLocation            LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            if (!isOfflinePIN) {
                if (pinBlock.length() > 16)
                    pinBlock = pinBlock.substring(0, 16);

                m.set(52, pinBlock); // pinBytes);
            }

            m.set(55, field55);
            String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            if (posAccountUpsl == null || posAccountUpsl.equals(""))
                posAccountUpsl = "1453105168"; //0060846532";

            //posAccountUpsl = "1774691015";

            f60 = "010085C24300148041Meter Number=12.87001004.Acct=" + posAccountUpsl + ".Phone=" + mobileNo;
            //	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
            m.set(60, f60);

            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");
            m.set(123, "510101513344101");

            m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }


    public ISOMsg CreatePurchaseMessageREXNEW(TranNetInfo tInfoc, TermParamInfo tparams, int retry) { //, String posAccountUpsl) {
        Date d = new Date();

        ISOMsg m = new ISOMsg();
        try {

            String mobileNo = tInfoc.getMobileNo();
            m.setMTI("0200");

            //String cdx = cardNo + "|" + cardName + "|" + expDate + "|" + track2 + "|" + track1|tlv;
            //tranInfo.setBillRefNo(cdx);
            //XStream xs = new XStream();
            //LoggingUtil.DebugInfo("XML:: " + xs.toXML(tInfoc));


            String[] cds = OtaUtility.tokenize(tInfoc.getBillRefNo(), "|");


            //String[] cds = tokenize(tInfo.getCardData(), "|");
            String pan = cds[0]; // ""; // "";
            //String pin = "";
            String track2Data = cds[3]; // "";
            String field55 = cds[4];
            String field23 = cds[5];
            String accType = cds[6];
            String accx = "00";
            if (accType.equals("1"))
                accx = "20";
            else if (accType.equals("2"))
                accx = "10";
            else if (accType.equals("3"))
                accx = "30";
            field23 = ISOUtil.padleft(field23, 3, '0');
            String tData2 = OtaUtility.tokenize(track2Data, "=")[1];
            String expDate = tData2.substring(0, 4);
            String serviceCode = tData2.substring(4, 7);

            String track2Data2 = track2Data; // + "10";//
            track2Data2 = track2Data2.replace("&#0;", "").trim();
            if (track2Data2.length() > 37)
                track2Data2 = track2Data2.substring(0, 37);
            //Settings.tokenize(track2Data, "=")[0] + "=" + expDate + serviceCode ;

            double amt = tInfoc.getAmount();


            String tsk = tparams.getTsk();// tparams[3];  //termInfo.getTsk();
            //String serverIP = tparams[6];
            //int port = Integer.parseInt(tparams[7]);
            String terminalId = tparams.getTerminalId(); // tparams[0];
            String cardAcceptorId = tparams.getCardAcceptorId(); // tparams[5];
            String cardAcceptorLocation = tparams.getCardAcceptionLocation();// tparams[8];

            String rrn = tInfoc.getExternalRefNo().substring(0, 12);
            String stan = tInfoc.getExternalRefNo().substring(12);

            String pinBlock = tInfoc.getCardData(); // PinBlockEncryptionUtil.GenerateISO0Format0PinBlock(pan, pin);

            m.set(2, pan);
            //m.set(3, "001000");
            //m.set(3, "010000"); //added for UPSL

            m.set(3, "000000");
            //010000
            //m.set(3, "00" + accx + "00");  //Purchase

            //m.set(3, "01" + accx + "00");  //Cash Advance
            //m.set(3, "09" + accx + "00");  //Purchase with Cash back
            //m.set(3, "20" + accx + "00");  //Refund

            //m.set(3, "00" + accx + "00");
            Long amts = new BigDecimal(amt * 100).longValue();
            String amt3 = ISOUtil.padleft(Long.toString(amts), 12, '0');
            m.set(4, amt3);

            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, stan); //new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            m.set(14, expDate);
            //m.set(new ISOField(15, ISODate.getDate(d)));
            m.set(18, "5251");// 4");
            m.set(22, "051");
            m.set(23, field23); //"001");
            m.set(25, "00");
            //m.set(26, "04"); //"06");
            //m.set(26, "06"); // Set Offline PIN
//			m.set(28, "C00000000");
            m.set(28, "D00000000");
            m.set(32, "111130"); //  "111111");
            //track2Data = track2Data.replace('=', 'D');
            m.set(35, track2Data2); // track2Data); //pan +  "=" + expDate + "226" +  "19123451");
            m.set(37, rrn);
            m.set(40, serviceCode);//"601"); // "221");
            m.set(41, terminalId); // "2HIG0106");
            m.set(42, cardAcceptorId);// "2HIGP010000P010");
            m.set(43, cardAcceptorLocation); // "2HIGP010 PHLEX LAGOS                LANG");
            m.set(49, "566");
            //byte[] pinBytes  = ISOUtil.hex2byte(pinBlock);
            if (pinBlock.length() > 16)
                pinBlock = pinBlock.substring(0, 16);
            if (retry > 0)
                m.set(52, pinBlock); // pinBytes);

            m.set(55, field55);
            //String f60 = "010085C24300148041Meter Number=12.87001001.Acct=1234567890.Phone=" + mobileNo;
            String posAccountUpsl = tparams.getPoolAccount();
            if (posAccountUpsl == null || posAccountUpsl.equals(""))
                posAccountUpsl = "1234567890";

            //posAccountUpsl = "1774691015";\
            m.set(59, terminalId + "-" + rrn + "-" + dx);

//			String f60 = "010085C24300148041Meter Number=12.87001004.Acct=" + posAccountUpsl + ".Phone=" + mobileNo;
//			//	String f60 = "010085C24300148041Meter Number=12.87001003.Acct=1234567890.Phone=" + mobileNo;
//			m.set(60, f60);
            //	m.set(62,"00698WD0101333" + mobileNo);
            //m.set(98, "High   Street   MFB      ");
            //m.set(100, "506180");

            // m.set(103,"0001189745");
            m.set(123, "511101513344101");

//			m.set(128, new String(new byte[]{0x0}));

            String f128 = generateHashForIsoMsg(m, tsk);
            m.set(128, f128);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }


    public String[] tokenize(String input, String delim) {
        Vector v = new Vector();
        StringTokenizer t;
        //System.out.println("...TOKENIZE::" + input + "    " + delim);
        if (delim.equals("default")) {
            t = new StringTokenizer(input);
        } else {
            t = new StringTokenizer(input, delim);
        }
        for (; t.hasMoreTokens(); v.addElement(t.nextToken())) ;
        String[] cmd = new String[v.size()];
        for (int i = 0; i < cmd.length; i++) {
            cmd[i] = (String) v.elementAt(i);
            //System.out.println("...TOKENIZE CMD::" + cmd[i]);
        }

        return cmd;
    }


    public static Map<String, String> parseParameters(String parameters) {

        int length = parameters.length();
        Map<String, String> decodedValues = new HashMap<String, String>();
        try {
            String key;
            int valueLen;
            while (length > 0) {
                key = parameters.substring(0, 2);
                valueLen = Integer.parseInt(parameters.substring(2, 5)) + 5;
                decodedValues.put(key, parameters.substring(5, valueLen));
                parameters = parameters.substring(valueLen);
                //"02 014 20220719225450 030152044LA3109236580400245050035660600356607001108004599952040ZINTERNET NIGERIA LIMITLA           LANG"/>
                length = parameters.length();
            }
        } catch (Exception e) {
            //fail silently
        }
        return decodedValues;

    }
    public static Map<String, String> parseParameters_(String parameters) {

        int length = parameters.length();
        Map<String, String> decodedValues = new HashMap<String, String>();
        try {
            String key;
            int valueLen;

            String location = parameters.substring(parameters.indexOf("52040",65)+5, (parameters.length()));   System.out.println("location of 50=>"+ location );//5 i.e. the index of
            decodedValues.put("52", location.substring(0,40));



            String Acceptor = parameters.substring(parameters.indexOf("03015",15)+5, 39);
            decodedValues.put("03",Acceptor);
            System.out.println("Card Acceptor Identification  of =>"+ Acceptor );//5 i.e. the index of another is



            String mcc= parameters.substring(parameters.indexOf("08004",50)+5, (parameters.length() ));
            decodedValues.put("08",mcc.substring(0,4));
            System.out.println("mcc of =>"+ mcc);//5 i.e. the index of another is

            String cco= parameters.substring(parameters.indexOf("05003",42)+5, 54);
            decodedValues.put("05",cco.substring(0,3));
            System.out.println("cco =>"+ cco);//5 i.e. the index of another is
            return decodedValues;
        } catch (Exception e) {
            System.out.println("e of =>"+ e.toString());//5 i.e. the index of another is

        }
        return decodedValues;

    }

    public static Map<String, String> parseBalanceParameters(String parameters) {
        System.out.println("parameters ===>"+parameters);
        int length = parameters.length();
        Map<String, String> decodedValues = new HashMap<String, String>();
      //  try {
            String key;
            int valueLen;

            double v = Double.parseDouble( parameters.substring(9,20));
            System.out.println("v ===>"+v);
            double blc = v/100;
            System.out.println("blc ===>"+blc);
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            System.out.println(formatter.format(blc));

            String valueBlc =   (formatter.format(blc));
            System.out.println("valueBlc ===>"+valueBlc);
               valueBlc = valueBlc.substring(1);

         //   valueBlc = String.valueOf(Double.parseDouble( );
            System.out.println("valueBlc ===>"+valueBlc);


    decodedValues.put("09",   valueBlc);
            System.out.println("bls===>"+formatter.format(blc));
            return decodedValues;

     //   return decodedValues;

    }


    /*
    Field 54

 Account type (positions 1 - 2)
 Amount type (positions 3 - 4)
 Currency code (positions 5 - 7)
 Amount sign (position 8) - "C" or "D"
 Amount (position 9 - 20)  - this is the balance
     */












    public static String generateHashForIsoMsg(ISOMsg isoMsg, String key) throws Exception {


        PosPackager packager = new PosPackager();
        isoMsg.setPackager(packager);
        String generatedHashValue = "";
        try {
            byte[] data = isoMsg.pack();
            int length = data.length;
            System.out.println("HASH_LEN:: " + length);
            byte[] dataToHash = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(data, 0, dataToHash, 0, dataToHash.length);
            }

            generatedHashValue = hash(dataToHash, Hex.decodeHex(key.toCharArray())).toUpperCase();

            Log.e("RESULT : ", new String(data));
            Log.e("RES:: ", generatedHashValue);
            Log.e("KEY:: ", key);

            return ISOUtil.padleft(generatedHashValue, 64, '0').toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return generatedHashValue;
    }

    public static String generateHashForIsoMsg2(ISOMsg isoMsg, String key) throws Exception {
        //PosPackager packager = new PosPackager();
        //isoMsg.setPackager(packager);
        String generatedHashValue = "";
        try {
            byte[] data = isoMsg.pack();
            int length = data.length;
            System.out.println("HASH_LEN:: " + length);
            byte[] dataToHash = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(data, 0, dataToHash, 0, dataToHash.length);
            }

            generatedHashValue = hash(dataToHash, Hex.decodeHex(key.toCharArray()));

            return ISOUtil.padleft(generatedHashValue, 64, '0').toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedHashValue;
    }

    private static String hash(byte[] data, byte[] key) {
        MessageDigest md = getDigest();
        md.update(key);
        md.update(data);

        return new String(Hex.encodeHex(md.digest()));
    }

    private static MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String Hash256Message(String data) {
        String msg = "";
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] messageDigest = digest.digest(hashBytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                sb.append(h);
            }
            msg = sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            //LoggingUtil.ExceptionInfo(ex);
        }

        return msg;

    }
    //============================================================= For Horizon


    public ISOMsg SendTMKRequest_A(String keyType, String terminalId) {
        return getSendTMKResponse(keyType, terminalId, "");
    }

    public ISOMsg getSendTMKResponse(String keyType, String terminalId, String ptsp) {
        Date d = new Date();
        // logger.info("Create Network Request ");
        ISOMsg m = new ISOMsg();
        try {
/*
     fields = new TreeMap<>();
        maxField = -1;
        dirty = true;
        maxFieldDirty=true;
        direction = 0;
        header = null;
        trailer = null;
 */
            //=====>***	m.setPackager(new PosPackager());
            m.setMTI("0800");

            String bitmap = "1";
            m.set(3, keyType + "0000");
            Date date = new Date();
            String DATE_FORMAT = "MMddHHmmss"; //""MMdd-yyyy hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dx = sdf.format(date);
            System.out.println("Today is " + dx);
            m.set(7, dx);  //"0327133744");  //MMDDhhmmss
            m.set(11, OtaUtility.GetRefNumber("", 6));
            // m.set(new ISOField(11,
            // ISOUtil.zeropad(new Integer(seq.get("traceno")).toString(), 6)));
            m.set(new ISOField(12, ISODate.getTime(d)));
            m.set(new ISOField(13, ISODate.getDate(d)));
            if (ptsp.equals("NETOP"))
                m.set(32, "100001");
            m.set(41, terminalId);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return m;
    }

}
