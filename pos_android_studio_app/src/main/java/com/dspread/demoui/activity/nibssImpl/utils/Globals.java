package com.dspread.demoui.activity.nibssImpl.utils;

public class Globals {

    public static final String PTSP = "GA";
    public static final String SUCCESS = "SUCCESS";
    public static final String TID = "TID";
    public static final String latitude = "latitude";
    public static final String xapikey = "x-api-key";

    public static String TerminalMaster = "9A";
    public static String TerminalSession = "9B";
    public static String TerminalPINKey = "9G";
    public static String TerminalParameterDownload = "9C";




    //public static String TermiPIN = "9G";
    public static final String PURCHASE = "PURCHASE";
    public static final String BALANCE = "BALANCE";
    public static final String EOD = "EOD";



    public static final String KEYEXCHANGE = "KEY_EXCHANGE";
    public static final String REPRINT = "REPRINT";


    public static final String TMX = "TMX";
    public static final String KIMONO = "KIMONO";
    public static final String NIBSS = "NIBSS";
    public static final String NIBSSTEST = "NIBSSTEST";

    public static final String REVERSAL = "REVERSAL";
    public static final String REFUND = "REFUND";
    public static final String CASHADVANCE = "Cash Advance" ;
    public static final String GANIBSSPOSVAS = "GANIBSSPOSVAS";
    public static final String IN = "IN";
    public static final String SN = "SN";
    public static final String NON = null;
    public static final String X_TERMINAL_ID = "X_TERMINAL_ID";
    public static final String DATA = "data";
    public static final String ENKEY = "ArAmYdE@GA2023HorizoneAPP";
    public static final String REXCONNECT = "RexConnect" ;
    public static final String POSVAS = "POSVAS";
    public static final String PROVIDER = "PROVIDER";
    public static final String EXTERNAL = "EXTERNAL";
    public static final String GEOLOCATION ="GEOLOCATION";
    public static final CharSequence RESPONSE_CODE = "00";
    public static final String POS = "POS" ;
    public static final String BEARER_ = "Bearer" ;
    public static final String CALLHOME = "CALLHOME";
    public static final String POSSocket = "POSSocket";


    public static final String PRINTPARAMS = "PRINTPARAMS";
    public static final String TERMINALPARAMITER = "TERMINALPARAMITER";
    public static final String GATRANSACTION = "GATRANSACTION";
    public static final String TranNetInfo = "TranNetInfo";
    public static final String TermParamInfo = "TermParamInfo";
    public static final String ME = "me";
    public static final String KEY = "28300518865986737073478883921518";


    public static  String param = "param";
    public static String TermiParams = "9C";
    public static String TermiMASTKEY = "9A";
    public static String TermiSESSI = "9B";
    public static String TermiPIN = "9G";
    //public static String TermiPIN = "9G";

    public static final String REPRINTPARAMS = "REPRINTPARAMS";
    public static final String PURCHASEWITHCB = "PURCHASEWITHCB";
    public static String Successfully = "Successfully";
    public static String Try_Again = "Try Again";
    public static String RequestData = "requestData";
    public static String transType= "transType";
    public static String amount = "amount";
    public static String amount1 = "amount1";
    public static String amount2 = "amount2";
    public static String print = "print";
    public static String token = "token";
    public static String colour = "colour";
    public static String rrn = "rrn";
    public static String stan = "stan";
    public static String stage = "stage";
    public static String ip = "ip";
    public static String port = "port";
    public static String tid = "TID";
    public static String Invalid_Parameters = "INVALID FORMAT";
    public static char[] Provider;

    public static final String PREAUTH = "PRE-AUTH";
    public static final String COMPLETION = "COMPLETION";
    public static String longitude = "0000";

    private static Globals instance;
    public static final String BANK = "Bank";
    public static final String PARAMETER = "PARAMETER";


    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE_TEXT = "Content-Type";
    public static final String X_SECRET_TEXT = "x-client-secret";
    public static final String X_SOURCE_CODE_TEXT = "x-source-code";
    public static final String X_ENTITY_CODE_TEXT = "x-entity-code";
    public static final String X_USERNAME_TEXT = "x-username";
    public static final String X_CLIENT_ID_TEXT = "x-client-id";
    public static final String X_DEVICE_ID = "x-device-id";
    public static final String X_LANGUAGE = "x-language";
    public static final String CHANNEL = "channel";
    public static final String CONTENT_TYPE = "application/json";
    public static final String CHANNEL_TYPE = "channelType";

    public static final String MOBILE = "MOBILE";

    private Globals(){}

    public static synchronized Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }
}
