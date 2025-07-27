package com.dspread.demoui.ui.dialog;

import static com.dspread.demoui.activity.nibssImpl.utils.FormUtility.getNibssMessage;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.BALANCE;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.KEYEXCHANGE;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.PARAMETER;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.PTSP;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.PURCHASE;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.PURCHASEWITHCB;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.SUCCESS;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.Successfully;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.TerminalMaster;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.TerminalPINKey;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.TerminalParameterDownload;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.TerminalSession;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.Try_Again;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.amount;
import static com.dspread.demoui.activity.nibssImpl.utils.Globals.transType;
import static com.xuexiang.xutil.app.ActivityUtils.startActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dspread.demoui.BaseApplication;
import com.dspread.demoui.R;
import com.dspread.demoui.activity.MainActivity;
import com.dspread.demoui.activity.PaymentActivity;
import com.dspread.demoui.activity.nibssImpl.IsoMessageClient;
import com.dspread.demoui.activity.nibssImpl.dao.AppDatabase;
import com.dspread.demoui.activity.nibssImpl.dao.ParamInfo;
import com.dspread.demoui.activity.nibssImpl.info.TermParamInfo;
import com.dspread.demoui.activity.nibssImpl.model.GaTranResponseInfo;
import com.dspread.demoui.activity.nibssImpl.utils.SecurityUtil;
import com.dspread.demoui.activity.nibssImpl.van.PinBlockEncryptionUtil;
import com.dspread.demoui.activity.nibssImpl.van.PosPackager;
import com.dspread.demoui.activity.nibssImpl.van.SunSSLSocketFactory;
import com.dspread.demoui.beans.Constants;
import com.dspread.demoui.utils.MoneyUtil;
import com.dspread.demoui.utils.SharedPreferencesUtil;
import com.dspread.demoui.utils.Utils;
import com.dspread.demoui.widget.MyAdapter;
import com.dspread.xpos.QPOSService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jpos.core.VolatileSequencer;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.PostChannel;
import org.jpos.util.LogSource;
import org.jpos.util.SimpleLogListener;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Mydialog {
    private QPOSService pos;
     boolean loadKeyOne, loadKeytwo = false;
    BaseApplication application;
    GaTranResponseInfo gaTranResponseInfo, gaTranResponseInfoForCallHome;
    Window window;
    Activity mContext;


    int countInject = 0;
    String terminalParamCode = "TERMINAL";
    org.jpos.util.Logger logger = new org.jpos.util.Logger();
    PostChannel channel = new PostChannel();
    String zmk, zmk4, terminalId, serverIP, otherTermDetails, KCV, CTMK;
    boolean sslorNot = true ;

    private AppDatabase appDatabase;
    private Gson gson;
    TermParamInfo termParamInfo = new TermParamInfo();
    String pinblock_decrypt, pinblock_encrypt;
    private long pressedTime;
    String pinBlock, status, statusMessage;
    String cardBlc;
    public static final String TAG =  "ydialog=>";
    private static SharedPreferencesUtil preferencesUtil;
    public interface OnMyClickListener {
        void onCancel();

        void onConfirm();
    }

    /**
     * loading
     */


    public static Dialog Ldialog;


    private SunSSLSocketFactory getSocketSSlInstance() {
        SunSSLSocketFactory sslSockFactory = new SunSSLSocketFactory();
        sslSockFactory.setServerAuthNeeded(false);
        // sslSockFactory.setServerAuthNeeded(true);
        return sslSockFactory;
    }


    public static void loading(Activity mContext, String msg) {
        Ldialog = new Dialog(mContext);
        Ldialog.setContentView(R.layout.processing_dialog);
        Button confirmButton = Ldialog.findViewById(R.id.confirmButton);
        TextView messageTextView = Ldialog.findViewById(R.id.msgTextView);
        messageTextView.setText(msg);
        messageTextView.setTextSize(20);
        Window dialogWindow = Ldialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        Ldialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        lp.dimAmount = 0.4f;
        dialogWindow.setAttributes(lp);
        Ldialog.setCanceledOnTouchOutside(false);
//        Ldialog.setCancelable(false);
        if (!mContext.isFinishing()) {
            Ldialog.show();
        }
    }

    /*
     *Error message prompt box
     */
    public static AlertDialog ErrorDialog;
    public static AlertDialog KeyExchangeDialog;

    public static void ErrorDialog(Activity mContext, String msg, OnMyClickListener listener) {
        View view = View.inflate(mContext, R.layout.alert_dialog, null);
        Button mbtnConfirm = view.findViewById(R.id.btnConfirm);
        TextView mtvInfo = view.findViewById(R.id.tvInfo);
        mtvInfo.setText(msg);
        Button mbtnCancel = view.findViewById(R.id.btnCancel);
        mbtnCancel.setVisibility(View.GONE);
        mbtnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirm();
            } else {
                ErrorDialog.dismiss();
                if (!msg.equals(mContext.getString(R.string.bad_swipe))) {
                    mContext.finish();
                }
            }
        });

        ErrorDialog = new AlertDialog.Builder(mContext).create();
        ErrorDialog.setCanceledOnTouchOutside(false);
        ErrorDialog.setCancelable(false);
        if (!mContext.isFinishing()) {
            ErrorDialog.show();
        }
        //显示对话框
        Window window = ErrorDialog.getWindow();
        window.setWindowAnimations(R.style.popupAnimation);
        window.setBackgroundDrawable(null);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        Display d = wm.getDefaultDisplay();
        WindowManager.LayoutParams p = ErrorDialog.getWindow().getAttributes();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        ErrorDialog.getWindow().setAttributes(p);
        window.setContentView(view);

    }


    public static AlertDialog manualExitDialog;

    public static void manualExitDialog(Activity mContext, String msg, OnMyClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.alert_dialog, null);
        View viewv = view.findViewById(R.id.view_v);
        viewv.setVisibility(View.VISIBLE);
        TextView mtvInfo = view.findViewById(R.id.tvInfo);
        mtvInfo.setText(msg);
        Button mbtnConfirm = view.findViewById(R.id.btnConfirm);
        Button mbtnCancel = view.findViewById(R.id.btnCancel);
        mbtnCancel.setVisibility(View.VISIBLE);
        mbtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirm();
            }
        });
        mbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
            }
        });

        manualExitDialog = builder.create();
        if (!mContext.isFinishing()) {
            manualExitDialog.show();
        }
        Window window = manualExitDialog.getWindow();
        window.setWindowAnimations(R.style.popupAnimation);
        window.setBackgroundDrawable(null);
        window.setGravity(Gravity.BOTTOM);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        WindowManager.LayoutParams p = manualExitDialog.getWindow().getAttributes();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        manualExitDialog.getWindow().setAttributes(p);
        manualExitDialog.setCanceledOnTouchOutside(true);
        window.setContentView(view);
    }


    public static AlertDialog payTypeDialog;
    private static RecyclerView rvlist;
    private static String transactionTypeString = "GOODS";
    private static MyAdapter myAdapter;
    public static final int BLUETOOTH =1;
    public static final int UART =2;
    public static final int USB_OTG_CDC_ACM =3;

    public static void payTypeDialog(Activity mContext, String amount, long inputMoney, String[] data) {

        payTypeDialog = new AlertDialog.Builder(mContext).create();
        if (!mContext.isFinishing()) {
            payTypeDialog.show();
        }
        Window window = payTypeDialog.getWindow();
        window.setWindowAnimations(R.style.popupAnimation);
        window.setBackgroundDrawable(null);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        WindowManager.LayoutParams p = payTypeDialog.getWindow().getAttributes();
        p.height = (int) (d.getHeight() * 0.6);
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        payTypeDialog.getWindow().setAttributes(p);
        View view = View.inflate(mContext, R.layout.paytype_dialog_view, null);
        rvlist = view.findViewById(R.id.rv_list);
        rvlist.setLayoutManager(new LinearLayoutManager(mContext.getApplicationContext()));
        myAdapter = new MyAdapter(getArrayList(data));
        rvlist.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener((view1, position, content) -> {
            if (Utils.islistFastClick()) {
                preferencesUtil = SharedPreferencesUtil.getInstance(mContext);
                String conType = (String) preferencesUtil.get(Constants.connType, "");
                if ("CASHBACK".equals(content)) {
                    String inputMoneyString = String.valueOf(inputMoney);
                    cashBackPaymentDialog(mContext,inputMoneyString);
                } else {
                    if (!"".equals(conType)) {
                        transactionTypeString = content;
                        Intent intent = new Intent(mContext, PaymentActivity.class);
                        String inputMoneyString = String.valueOf(inputMoney);
                        intent.putExtra("inputMoney", inputMoneyString);
                        intent.putExtra("paytype", transactionTypeString);
                        mContext.startActivity(intent);
                    }else {
                        ((MainActivity)mContext).switchFragment(1);
                    }
                }
                if(payTypeDialog != null) {
                    payTypeDialog.dismiss();
                }
                payTypeDialog = null;
                myAdapter = null;

            }
        });
        window.setContentView(view);
        payTypeDialog.setCanceledOnTouchOutside(true);
        payTypeDialog.setCancelable(true);
    }

    private static List<String> getArrayList(String[] data) {
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            dataList.add(data[i]);
        }
        return dataList;
    }

    //Transaction confirmation information
    public static AlertDialog onlingDialog;

    public static Dialog cashBackPaymentDialog;

    public static void cashBackPaymentDialog(Activity mContext, String inputMoney) {
        View view = View.inflate(mContext, R.layout.cashback_dialog, null);
        TextView textView = view.findViewById(R.id.messageTextView);
        textView.setTextSize(20);

        EditText etInputMoney = view.findViewById(R.id.et_inputMoney);
        etInputMoney.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        etInputMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (s.length() == 1 && s.toString().equals(".")) {
                    etInputMoney.setText("");
                }
                if (str.contains(".")) {
                    String[] strArr = str.split("\\.");
                    if (strArr.length > 1 && strArr[1].length() > 2) {
                        s.delete(str.length() - 1, str.length());
                    }
                }
                if (str.length() > 10 && !str.contains(".")) {
                    s.delete(str.length() - 1, str.length());
                }

            }
        });

        view.findViewById(R.id.confirmButton).setOnClickListener(
                v -> {
                    String cashbackAmounts = etInputMoney.getText().toString().trim();
                    if (!"".equals(cashbackAmounts) && !"0".equals(cashbackAmounts)) {
                        Double inputCashbackAmount = Double.valueOf(cashbackAmounts);
                        Long inputCashbackAmounts=MoneyUtil.yuan2fen(inputCashbackAmount);
                        String inputcashbackMoney = String.valueOf(inputCashbackAmounts);
                        preferencesUtil = SharedPreferencesUtil.getInstance(mContext);
                        String conType = (String) preferencesUtil.get( "conType", "");

                        if (conType != null) {
                            Intent intent = new Intent(mContext, PaymentActivity.class);
                            String inputMoneyString = String.valueOf(inputMoney);
                            intent.putExtra("inputMoney", inputMoneyString);
                            intent.putExtra("paytype", "CASHBACK");
                            intent.putExtra("cashbackAmounts", inputcashbackMoney);
                            intent.putExtra("connect_type", 2);
                            mContext.startActivity(intent);
                            cashBackPaymentDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(mContext, mContext.getString(R.string.set_amount), Toast.LENGTH_SHORT).show();
                    }


                });


        cashBackPaymentDialog = new Dialog(mContext);
        cashBackPaymentDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        cashBackPaymentDialog.setCanceledOnTouchOutside(false);
        if (!mContext.isFinishing()) {
            cashBackPaymentDialog.show();
        }

        etInputMoney.setFocusable(true);
        etInputMoney.setFocusableInTouchMode(true);
        etInputMoney.requestFocus();
        etInputMoney.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etInputMoney, 0);
        }, 100);

        Window window = cashBackPaymentDialog.getWindow();
        window.setWindowAnimations(R.style.popupAnimation);
        window.setBackgroundDrawable(null);
        window.setGravity(Gravity.BOTTOM);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        WindowManager.LayoutParams p = cashBackPaymentDialog.getWindow().getAttributes();

        p.width = (int) (d.getWidth() * 1);

        cashBackPaymentDialog.getWindow().setAttributes(p);
        cashBackPaymentDialog.setContentView(view);
    }








    public  void doKeyExchange(Activity mContext,String terminalId, String serverIP, int port, String zmk) {
            application = (BaseApplication) mContext.getApplication();
        View view = View.inflate(mContext, R.layout.dialog_keyexchange, null);
        TextView mtvInfo = view.findViewById(R.id.tv_log);
        ProgressBar progressBar = view.findViewById(R.id.progress_loading);
        mtvInfo.setText("msg");
        progressBar.setVisibility(View.VISIBLE);

        KeyExchangeDialog = new AlertDialog.Builder(mContext).create();
        KeyExchangeDialog.setCanceledOnTouchOutside(false);
        KeyExchangeDialog.setCancelable(false);
        if (!mContext.isFinishing()) {
            KeyExchangeDialog.show();
        }
        //显示对话框
          window = KeyExchangeDialog.getWindow();
        window.setWindowAnimations(R.style.popupAnimation);
        window.setBackgroundDrawable(null);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        Display d = wm.getDefaultDisplay();
        WindowManager.LayoutParams p = KeyExchangeDialog.getWindow().getAttributes();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        KeyExchangeDialog.getWindow().setAttributes(p);
        window.setContentView(view);


        new Thread(new Runnable() {
            @Override
            public void run() {
                SendISOmessage9A( "2057B983","196.6.103.10",55533,zmk);

            }
        }).start();



    }



    public void SendISOmessage9A(String terminalId, String serverIP, int port, String zmk) {
        termParamInfo.setTerminalId(terminalId);
        termParamInfo.setServerIP(serverIP);
        termParamInfo.setPort(port);
        try {

            IsoMessageClient isoMessageClient = new IsoMessageClient();
            int timeout = 60;
            logger.addListener(new SimpleLogListener(System.out));

            channel = new PostChannel(serverIP, port, new PosPackager());
            Log.d(TAG, "ISO-Server:sslorNot " + serverIP + " " + port);

            Log.d(TAG, "ISO-Server:sslorNot " + serverIP + " " + port + "==" + sslorNot);
          sslorNot = true;
            if (sslorNot) channel.setSocketFactory(getSocketSSlInstance());

            ((LogSource) channel).setLogger(logger, "channel");

            Log.d("==>", "ISo-Connect 9A");
            Log.d(TAG, "ISO-Server: " + serverIP + " " + port);
            Log.d(TAG, "ZMK4--" + zmk4);
            Log.d(TAG, "ZMK--" + zmk);


            if (!channel.isConnected()) {
                channel.connect();
                Log.d("==>", "ISo-Connect 9A");
            }

            VolatileSequencer seq = new VolatileSequencer();

            ISOMsg m = isoMessageClient.getNetworkMgtRequestRubies(TerminalMaster, terminalId);

            if (channel.isConnected()) {

                Log.d("==>", "ISo-Connect -9A");

                channel.send(m);
                Log.d("==>", "ISo-Connect 33 " + timeout);

                channel.setTimeout(timeout * 60000);

                ISOMsg response = channel.receive();   //  req.getResponse(isoTimeOut);

                if (response != null) {
                    Log.d("==>", "ISo-Connect Response 1 at +=>" + response.getString(39));
                    String f53 = response.getString(53);

                    String rspCode = response.getString(39);
                    Log.d("==>", " Card rspCode : " + rspCode);
                    if (!rspCode.equals("00")) {
                        String message = getNibssMessage(rspCode);
                        status = "306";
                        statusMessage = message;
                        close(message, "Get Params 9A Failed: -1", status);
                        return;
                    }
                    termParamInfo.setZmk(zmk);
//                    Log.d("==>", "ZMK=> " + );
                    Log.d("==>", "getCtmk=> " + termParamInfo.getZmk());
                    //  String tmk = PinBlockEncryptionUtil.DecryptSessionKey(termParamInfo.getCtmk(), "8d99a2cfb693d9a68a6f3c0072ef67616fc2c88748aad0423954d7ca1f52574e", Globals.TermiMASTKEY);


                    String tmk = PinBlockEncryptionUtil.DecryptSessionKey(termParamInfo.getZmk(), f53, TerminalMaster);
                    Log.d("==>", "ISo-TMK => " + tmk);

                    String etmk = f53.substring(0, 32);
                    String kcv = f53.substring(32, 38);

                    termParamInfo.setTmkKCV(kcv);
                    termParamInfo.setTmk(tmk);
                    termParamInfo.setTmk2(etmk);

                    addKCV(f53, "ISo-Connect -9A");
                    channel.disconnect();
                    Thread.sleep(1000);
                    this.SendISOmessage9B(termParamInfo.getTerminalId(), termParamInfo.getServerIP(), termParamInfo.getPort());
                }
            }


        } catch (Exception ex) {
          System.out.println( "Timeout=> " + "Timeout=>"+ex);

            status = "306";
            statusMessage = "TMK Failed: -1";
//            close("TMK Failed: -1", "Timeout", status);

        }

    }


    public void SendISOmessage9B(String terminalId, String serverIP, int port ) {

        try {

           // showResultC(etCashAmount, getString(R.string.please_wait_tsk));
            IsoMessageClient isoMessageClient = new IsoMessageClient();
            int timeout = 60;

            logger.addListener(new SimpleLogListener(System.out));

            channel = new PostChannel(serverIP, port, new PosPackager());
            Log.d(TAG, "ISO-Server: sslorNot  " + sslorNot + " " + port);

            if (sslorNot) channel.setSocketFactory(getSocketSSlInstance());

            ((LogSource) channel).setLogger(logger, "channel");

            Log.d("==>", "ISo-Connect 1");

            if (!channel.isConnected()) {

                channel.connect();
                Log.d("==>", "ISo-Connect 2");

            }

            VolatileSequencer seq = new VolatileSequencer();

            ISOMsg m = isoMessageClient.getNetworkMgtRequestRubies(TerminalSession, terminalId);

            if (channel.isConnected()) {

                Log.d("==>", "<==Terminal Session Key => ");
                Log.d("==>", "ISo-Connect ");

                channel.send(m);
                Log.d("==>", "ISo-Connect 33 " + timeout);

                channel.setTimeout(timeout * 60000);

                ISOMsg response = channel.receive();   //  req.getResponse(isoTimeOut);

                if (response != null) {

                    String rspCode = response.getString(39);
                    Log.d("==>", " Card rspCode : " + rspCode);
                    if (!rspCode.equals("00")) {
                        String message = getNibssMessage(rspCode);
                        status = "306";
                        statusMessage = message;
                        close(message, "Get Params 9B Failed: -1", status);
                        return;
                        //  close(message, "null");
                    }


                    String f53 = response.getString(53);
                    String eTSK = f53.substring(0, 32);
                    String kcv = f53.substring(32, 38);
                    String tsk = PinBlockEncryptionUtil.DecryptSessionKey(termParamInfo.getTmk(), f53, TerminalSession);
                   // addKCV(f53, "ISo-Connect -9B");

                    Log.d("==>", "eTSK: " + eTSK);
                    Log.d("==>", "KCVV: " + kcv);
                    Log.d("==>", "TSK: " + tsk);

                    termParamInfo.setTskKCV(kcv);
                    termParamInfo.setTsk(tsk);
                    termParamInfo.setTsk2(eTSK);


                    channel.disconnect();
                    Thread.sleep(1000);
                    this.SendISOmessage9G(terminalId, serverIP, port );
                }

            }


        } catch (Exception ex) {
            ex.printStackTrace();
            status = "306";
            statusMessage = "TMK Failed: -9b";
            close(mContext.getString(R.string.tpk_failed), ex.toString(), status);

        }

    }

    public void SendISOmessage9G(String terminalId, String serverIP, int port ) {

        try {

          //  showResultC(etCashAmount, getString(R.string.please_wait_tpk));
            Log.d("==>", "<==Terminal PIN Key => ");

            IsoMessageClient isoMessageClient = new IsoMessageClient();
            int timeout = 60;

            logger.addListener(new SimpleLogListener(System.out));

            channel = new PostChannel(serverIP, port, new PosPackager());
            if (sslorNot) channel.setSocketFactory(getSocketSSlInstance());

            ((LogSource) channel).setLogger(logger, "channel");

            if (!channel.isConnected()) {
                channel.connect();
                Log.d("==>", "ISo-Connect 2");
            }

            VolatileSequencer seq = new VolatileSequencer();

            ISOMsg m = isoMessageClient.getNetworkMgtRequestRubies(TerminalPINKey, terminalId);

            if (channel.isConnected()) {
                channel.send(m);
                Log.d("==>", "ISo-Connect 33 " + timeout);

                channel.setTimeout(timeout * 60000);

                ISOMsg response = channel.receive();   //  req.getResponse(isoTimeOut);

                if (response != null) {

                    String rspCode = response.getString(39);
                    Log.d("==>", " Card rspCode : " + rspCode);
                    if (!rspCode.equals("00")) {
                        statusMessage = getNibssMessage(rspCode);
                        status = "306";
                        close(statusMessage, "Get Params 9G Failed: -1", status);
                        return;
                        //  close(message, "null");
                    }

                    String f53 = response.getString(53);
                    String eTPK = f53.substring(0, 32);
                    String kcv = f53.substring(32, 38);

                    String tpk = PinBlockEncryptionUtil.DecryptSessionKey(termParamInfo.getTmk(), f53, TerminalPINKey);
                    //  String sdf = PinBlockEncryptionUtil.DecryptSessionKey(termParamInfo.getTmk(), "8d99a2cfb693d9a68a6f3c0072ef67616fc2c88748aad0423954d7ca1f52574e", PARAMETER);
                    // addKCV(f53, "ISo-Connect -sdf==>=>" + sdf);

                    addKCV(f53, "ISo-Connect -9G");
                    termParamInfo.setTpkKCV(kcv);
                    termParamInfo.setTpk(tpk);
                    termParamInfo.setTpk2(eTPK);
                    channel.disconnect();
                    Thread.sleep(1000);
                    this.SendISOmessage9C(terminalId, serverIP, port );
                }
            }


        } catch (Exception ex) {
            //    ex.printStackTrace();
            Log.d("==>", "<==TIME OUT => ");
            status = "306";
            statusMessage = "TSK Failed: -9G";
            close("TSK Failed: -9G", "Timeout", status);

        }

    }

    public void SendISOmessage9C(String terminalId, String serverIP, int port ) {


        try {
            Log.d("==>", "<==Terminal Parameter Download => ");

            IsoMessageClient isoMessageClient = new IsoMessageClient();
            int timeout = 60;

            logger.addListener(new SimpleLogListener(System.out));

            channel = new PostChannel(serverIP, port, new PosPackager());
            if (sslorNot) channel.setSocketFactory(getSocketSSlInstance());

            ((LogSource) channel).setLogger(logger, "channel");

            Log.d("==>", "ISo-Connect 1");
            Log.d(TAG, "ISO-Server: " + serverIP + " " + port);

            if (!channel.isConnected()) {
                channel.connect();
                Log.d("==>", "ISo-Connect 2");
            }

            VolatileSequencer seq = new VolatileSequencer();

            if (channel.isConnected()) {
                channel.setTimeout(timeout * 60000);
                ISOMsg m = isoMessageClient.getNetworkMgtRequestRubies(TerminalParameterDownload, terminalId);

                m.set(62, "01009233542415");
                m.set(64, new String(new byte[]{0x0}));

                String f64 = isoMessageClient.generateHashForIsoMsg(m, termParamInfo.getTsk());
                m.set(64, f64);


                String f62 = "";
                String cardAcceptorId = "";
                String cardAcceptorLocation = "";
                String merchantType = "";
                String currencyCode = "";

                channel.send(m);
                ISOMsg response4 = channel.receive();
                String rspCode = response4.getString(39);
                if (response4 != null) {
                    f62 = response4.getString(62);
                    rspCode = response4.getString(39);
                    Log.d("==>", " Card f62 : " + f62);
                    status = rspCode;
                    if (!rspCode.equals("00")) {

                        String message = getNibssMessage(rspCode);
                        status = "306";
                        statusMessage = message;
                        close(message, "Get Params Failed: -1", status);
                        return;
                        //  close(message, "null");
                    }
                //    updateForParams();
                    Map<String, String> decodedParameters = IsoMessageClient.parseParameters_(f62);
                    Log.d("==>", " decodedParameters f62 : " + decodedParameters.toString());


                    cardAcceptorId = decodedParameters.get("03");
                    cardAcceptorLocation = decodedParameters.get("52");
                    merchantType = decodedParameters.get("08");
                    currencyCode = decodedParameters.get("05");

                    termParamInfo.setMerchantNo(cardAcceptorId);
                    termParamInfo.setMerchantName(cardAcceptorLocation);

                }
                channel.disconnect();

                String json = terminalId + "|" + termParamInfo.getTmk() + "|" + termParamInfo.getTpk() + "|" + termParamInfo.getTsk() + "|" + termParamInfo.getZmk() + "|" + cardAcceptorId +
                        "|" + termParamInfo.getServerIP() + "|" + termParamInfo.getPort() + "|" + cardAcceptorLocation + "|" + merchantType + "|" + currencyCode +
                        "|" + otherTermDetails;//, + "|"key1, String key2


                termParamInfo.setCurrencyCode(currencyCode);
                termParamInfo.setMcc(merchantType);
                termParamInfo.setTerminalId(terminalId);

                termParamInfo.setCardAcceptorId(cardAcceptorId);
                termParamInfo.setCardAcceptionLocation(cardAcceptorLocation);

                termParamInfo.setPtsp("NIBSS");
                Log.d(TAG, "PTSP: " + termParamInfo.getPtsp());

                if (true) {
                    termParamInfo.setCardAcceptionLocation(cardAcceptorLocation);
                    termParamInfo.setCardAcceptorId(cardAcceptorId);

                } else if (PTSP.equals(termParamInfo.getPtsp2())) {
                    termParamInfo.setCardAcceptionLocation2(cardAcceptorLocation);
                    termParamInfo.setCardAcceptorId2(cardAcceptorId);
                }

                Type type = new TypeToken<TermParamInfo>() {
                }.getType();
//                json = gson.toJson(termParamInfo, type);
//
//                Log.d(TAG, "TERMINAL-MESSAGE::" + json);
//                json = SecurityUtil.encrypt(json, "");
//
//                ParamInfo pInfo = appDatabase.userProfileDAO().getParamInfo(terminalParamCode);
//                if (pInfo == null) {
//                    pInfo = new ParamInfo();
//                    pInfo.setCode(terminalParamCode);
//                    pInfo.setName(json);
//                    pInfo.setLastUpdate(new Date());
//                    appDatabase.userProfileDAO().saveParamInfo(pInfo);
//                    Log.d(TAG, "TERMINAL-MESSAGE-UPDATE::");
//                } else {
//                    pInfo.setName(json);
//                    pInfo.setLastUpdate(new Date());
//                    appDatabase.userProfileDAO().updateParamInfo(pInfo);
//                    Log.d(TAG, "TERMINAL-MESSAGE-SAVE::");
//                }
                Date date = new Date();
                //  String DATE_FORMAT = "MM-dd HH-mm:ss"; //""MMdd-yyyy hh:mm:ss";
                String DATE_FORMAT = "dd/MM/YYYY HH:mm:ss"; //""MMdd-yyyy hh:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                String dx = sdf.format(date);
//                gaTranResponseInfo.setDatetime(dx);
//
//              Log.d(TAG, "dx--" + gaTranResponseInfo.getDatetime());


                pos = application.getQposService();
                if(pos == null){
                    //goToSetting();
                    Log.d(TAG, "pos: " + pos.toString());

                    return;
                }else {
                    pos.setMasterKey("1A4D672DCA6CB3351FD1B02B237AF9AE", "08D7B4FB629D0885", 0);
                    pos.updateWorkKey(0,"1A4D672DCA6CB3351FD1B02B237AF9AE", "08D7B4FB629D0885",
                            "1A4D672DCA6CB3351FD1B02B237AF9AE", "08D7B4FB629D0885",
                            "1A4D672DCA6CB3351FD1B02B237AF9AE", "08D7B4FB629D0885");
                }

                Toast.makeText(mContext, "DONE", Toast.LENGTH_SHORT).show();
                cashBackPaymentDialog.dismiss();









//                mContext.runOnUiThread(new Runnable() {
//                    public void run() {
//                        try {
//                            while (!loadKeytwo && !loadKeyOne) {
//                                Log.d(TAG, "-loadKeytwo::" + loadKeytwo);
//                                Log.d(TAG, "-loadKeytwo::" + loadKeyOne);
//                                countInject++;
//                                if (countInject >= 5) {
//
//
//                                    pos = application.getQposService();
//                                    if(pos == null){
//                                        //goToSetting();
//                                        return;
//                                    }else {
//                                        pos.settMasterKey("1A4D672DCA6CB3351FD1B02B237AF9AE", "08D7B4FB629D0885", 0);
//                                        pos.updateWorkKey(0,"1A4D672DCA6CB3351FD1B02B237AF9AE", "08D7B4FB629D0885",
//                                                "1A4D672DCA6CB3351FD1B02B237AF9AE", "08D7B4FB629D0885",
//                                                "1A4D672DCA6CB3351FD1B02B237AF9AE", "08D7B4FB629D0885");
//                                    }
//                                    //          LoadKeys();
//                                    //initiateCardPaymentHorizon();
//                                }
//                               // loadClearMasterKeyA(termParamInfo.getTmk2());
//
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

                switch (transType) {
                    case PURCHASE:
                     //   doHouseKeepingStuff();
                        break;

                    case KEYEXCHANGE:
                        Log.d(TAG, "KEY EXCHANGE key--" + transType);
                        gaTranResponseInfo.setStatuscode("00");
                        gaTranResponseInfo.setMessage(Successfully);
                        mContext.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
//                                  //  lyprogressBar.setVisibility(View.GONE);
//                                    mainlayout.setVisibility(View.VISIBLE);
                                   // print();
                                    if (status == "00") {

                                    }
                                    //statusMessage = SUCCESS;
                                    close(SUCCESS, null, status);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;

                    default:
                        status = "306";
                        statusMessage = "Cant Read Params";
                        close(Try_Again, "Cant Read Params", status);
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            status = "306";
            statusMessage = "Cant Read Params";
            close( mContext.getString(R.string.params_faild), ex.toString(), status);
        }
    }
    void close(String msg, String e, String resultCode) {
        String data = null;

        try {
            data = getResult(msg, e);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("TAG", "***********END********: " + ex.toString());
        }


        Log.d("TAG", "***********END********: ");
        Log.d("TAG", "**transType*********END********: " + transType);
        Log.d("TAG", "*msg**********END********: " + msg);
        Log.d("TAG", "***resultCode********status********: " + resultCode);
        Log.d("==>", "msg => " + msg);
        Log.d("==>", String.format("data => %s", data));


        Intent intent = new Intent();
        intent.putExtra("status", resultCode);
        intent.putExtra("statusMessage", msg);

        if (!transType.equals(KEYEXCHANGE)) {
            intent.putExtra("data", data);
        }
        if (transType.equals(KEYEXCHANGE)) {

        }
        if (transType.equals(PARAMETER) || transType.equals(PURCHASE)) {
            Log.d("TAG", "***********transType********: " + transType);
            intent.putExtra("data", data);
        }



//        Log.d("TAG", "***********status finish ********: ");
//        Log.d("TAG", "**transType*********resultCode********: " + resultCode);
//        Log.d("TAG", "**transType*********msg********: " + msg);
//        Log.d("TAG", "**transType*********data********: " + data);
//        Log.d("TAG", "***********status finish ********: ");
//        intent.putExtra("status", resultCode);
//        intent.putExtra("data", data);
//        intent.putExtra("statusMessage", msg);
       // mContext.setResult(Activity.RESULT_OK, intent);


//        mContext.finish();
        return;
    }
    private String getResult(String msg, String e) {

        gaTranResponseInfo = new GaTranResponseInfo();
        String data = null;
        Log.d("TAG", "***********status getResult ********: ");
        Log.d("TAG", "***********status getResult transType ********: " + transType);
        switch (transType) {


            case PURCHASE:
            case PURCHASEWITHCB:
                data = "{" +
                        "\"aid\": \"" + gaTranResponseInfo.getAid() + "\",\n" +
                        "\"amount\": \"" + amount + "\",\n" +
                        "\"appLabel\": \"" + gaTranResponseInfo.getCardScheme() + "\",\n" +
                        "\"authcode\": \"" + gaTranResponseInfo.getAuthcode() + "\",\n" +
//                        "\"bankLogo\": \"" + tInfo.getAuthCode() + "\",\n" +
//                        "\"payAuthCode\": \"" + tInfo.getAuthCode() + "\",\n" +
//                        "\"bankName\": \"" + tInfo.getAuthCode() + "\",\n" +
                        //                      "\"baseAppVersion\": \"" + packageInfo.versionName + "\",\n" +
                        "\"cardExpireDate\": \"" + gaTranResponseInfo.getCardExpireDate() + "\",\n" +
                        "\"cardHolderName\": \"" + gaTranResponseInfo.getCardHolderName() + "\",\n" +
                        "\"currency\": \"" + "NGN" + "\",\n" +
                        "\"datetime\": \"" + gaTranResponseInfo.getDatetime() + "\",\n" +
                        "\"maskedPan\": \"" + gaTranResponseInfo.getMaskedPan() + "\",\n" +
                        "\"message\": \"" + msg + "\",\n" +
                        "\"error\": \"" + e + "\",\n" +
                        "\"nuban\": \"" + "amt" + "\",\n" +
                        "\"pinType\": \"" + "amt" + "\",\n" +
                        "\"rrn\": \"" + gaTranResponseInfo.getRrn() + "\",\n" +
                        "\"stan\": \"" + gaTranResponseInfo.getStan() + "\",\n" +
                        "\"ptsp\": \"" + termParamInfo.getPtsp() + "\",\n" +
                        "\"transactionType\": \"" + gaTranResponseInfo.getTransactionType() + "\",\n" +
                        "\"tranCode\": \"" + gaTranResponseInfo.getTranCode() + "\",\n" +
                        "\"providerReference\": \"" + gaTranResponseInfo.getStan() + "\",\n" +
                        "\"transactionReference\": \"" + gaTranResponseInfo.getProviderReference() + "\",\n" +
                        "\"narration\": \"" + gaTranResponseInfo.getNarration() + "\",\n" +
                        "\"customerName\": \"" + "null" + "\",\n" +
                        "\"cardScheme\": \"" + gaTranResponseInfo.getCardScheme() + "\",\n" +

                        "\"retrievalNumber\": \"" + gaTranResponseInfo.getRrn() + "\",\n" +
                        "\"beneficiaryAccount\": \"" + "null" + "\",\n" +
                        "\"beneficiaryName\": \"" + "null" + "\",\n" +
                        "\"beneficiaryBankCode\": \"" + "null" + "\",\n" +
                        "\"walletAccount\": \"" + "null" + "\",\n" +

                        "\"statuscode\": \"" + gaTranResponseInfo.getStatuscode() + "\",\n" +

                        "\"merchantAddress\": \"" + termParamInfo.getCardAcceptionLocation() + "\",\n" +
                        "\"MerchantCategoryCode\": \"" + termParamInfo.getMcc() + "\",\n" +
                        "\"merchantId\": \"" + termParamInfo.getMerchantNo() + "\",\n" +
                        "\"merchantName\": \"" + termParamInfo.getMerchantName() + "\",\n" +
                        "\"TerminalID\": \"" + terminalId + "\"\n" +
                        "}";
                Log.d("TAG", "PARAMETER1=>: " + "PURCHASE In 555");
                return data;


            case KEYEXCHANGE:
            case PARAMETER:
                Log.d("TAG", "PARAMETER1=>: " + "PARAMETER");

                try {
                    data = "{" +
                            "\"Biller_ID\": \"" + termParamInfo.getBiller_ID() + "\",\n" +
                            "\"merchantId\": \"" + termParamInfo.getMerchantNo() + "\",\n" +
                            "\"TerminalID\": \"" + termParamInfo.getTerminalId() + "\",\n" +
                            "\"ptsp\": \"" + termParamInfo.getPtsp() + "\",\n" +
                            "\"FooterMessage\": \"" + termParamInfo.getFooterMessage() + "\",\n" +
                            "\"merchantName\": \"" + termParamInfo.getMerchantName() + "\",\n" +
                            "\"merchantAddress\": \"" + termParamInfo.getCardAcceptionLocation() + "\",\n" +
                            "\"bankName\": \"" + termParamInfo.getBankName() + "\",\n" +
                            "\"bankLogo\": \"" + termParamInfo.getBankLogo() + "\",\n" +
                            "\"MerchantCategoryCode\": \"" + termParamInfo.getMcc() + "\",\n" +
//                            "\"baseAppVersion\": \"" + packageInfo.versionName + "\",\n" +
                            "\"serialNumber\": \"" + termParamInfo.getSerialNumber() + "\",\n" +
                            "\"currency\": \"" + "NGN" + "\",\n" +
                            "\"message\": \"" + msg + "\",\n" +
                            "\"error\": \"" + e + "\",\n" +
                            "\"statuscode\": \"" + gaTranResponseInfo.getStatuscode() + "\"\n" +
                            "}";
                    Log.d("TAG", "PARAMETER1=>: " + "NO ERROr");
                } catch (Exception ex) {
                    Log.d("TAG", "Exception=>: " + ex.toString());
                }
                Log.d("TAG", "PARAMETER1=>: " + data);
                Log.d("TAG", "PARAMETER1=>: " + "NO ERROr2");
                return data;
            case BALANCE:
                Log.d("TAG", "PARAMETER1=>: " + "PARAMETER");
                data = "{" +
                        "\"balance\": \"" + cardBlc + "\",\n" +
                        "\"currency\": \"" + "NGN" + "\",\n" +
                        "\"statusMessage\": \"" + msg + "\",\n" +
                        "\"error\": \"" + e + "\",\n" +
                        "\"statuscode\": \"" + gaTranResponseInfo.getStatuscode() + "\"\n" +
                        "}";
                Log.d("TAG", "PARAMETER1=>: " + data);

                return data;

            default:
                data = "{" +
                        "\"aid\": \"" + gaTranResponseInfo.getAid() + "\",\n" +
                        "\"amount\": \"" + amount + "\",\n" +
                        "\"appLabel\": \"" + gaTranResponseInfo.getCardScheme() + "\",\n" +
                        "\"authcode\": \"" + gaTranResponseInfo.getAuthcode() + "\",\n" +
//                        "\"bankLogo\": \"" + tInfo.getAuthCode() + "\",\n" +
//                        "\"payAuthCode\": \"" + tInfo.getAuthCode() + "\",\n" +
//                        "\"bankName\": \"" + tInfo.getAuthCode() + "\",\n" +
//                        "\"baseAppVersion\": \"" + packageInfo.versionName + "\",\n" +
                        "\"cardExpireDate\": \"" + gaTranResponseInfo.getCardExpireDate() + "\",\n" +
                        "\"cardHolderName\": \"" + gaTranResponseInfo.getCardHolderName() + "\",\n" +
                        "\"currency\": \"" + "NGN" + "\",\n" +
                        "\"datetime\": \"" + gaTranResponseInfo.getDatetime() + "\",\n" +
                        "\"maskedPan\": \"" + gaTranResponseInfo.getMaskedPan() + "\",\n" +
                        "\"message\": \"" + msg + "\",\n" +
                        "\"error\": \"" + e + "\",\n" +
                        "\"nuban\": \"" + "amt" + "\",\n" +
                        "\"pinType\": \"" + "amt" + "\",\n" +
                        "\"rrn\": \"" + gaTranResponseInfo.getRrn() + "\",\n" +
                        "\"stan\": \"" + gaTranResponseInfo.getStan() + "\",\n" +
                        "\"ptsp\": \"" + termParamInfo.getPtsp() + "\",\n" +
                        "\"transactionType\": \"" + gaTranResponseInfo.getTransactionType() + "\",\n" +
                        "\"tranCode\": \"" + gaTranResponseInfo.getTranCode() + "\",\n" +
                        "\"providerReference\": \"" + gaTranResponseInfo.getStan() + "\",\n" +
                        "\"transactionReference\": \"" + gaTranResponseInfo.getProviderReference() + "\",\n" +
                        "\"narration\": \"" + gaTranResponseInfo.getNarration() + "\",\n" +
                        "\"customerName\": \"" + "null" + "\",\n" +
                        "\"cardScheme\": \"" + gaTranResponseInfo.getCardScheme() + "\",\n" +

                        "\"retrievalNumber\": \"" + gaTranResponseInfo.getRrn() + "\",\n" +
                        "\"beneficiaryAccount\": \"" + "null" + "\",\n" +
                        "\"beneficiaryName\": \"" + "null" + "\",\n" +
                        "\"beneficiaryBankCode\": \"" + "null" + "\",\n" +
                        "\"walletAccount\": \"" + "null" + "\",\n" +

                        "\"statuscode\": \"" + gaTranResponseInfo.getStatuscode() + "\",\n" +

                        "\"merchantAddress\": \"" + termParamInfo.getCardAcceptionLocation() + "\",\n" +
                        "\"MerchantCategoryCode\": \"" + termParamInfo.getMcc() + "\",\n" +
                        "\"merchantId\": \"" + termParamInfo.getMerchantNo() + "\",\n" +
                        "\"merchantName\": \"" + termParamInfo.getMerchantName() + "\",\n" +
                        "\"TerminalID\": \"" + terminalId + "\"\n" +
                        "}";
                Log.d("TAG", "PARAMETER1=>: " + "NO ERROr 353");
                break;
        }
        Log.d("TAG", "PARAMETER1=>: " + data);
        return data;
    }
    public void addKCV(String seKey, String who) {
        Log.d("==>", "WHO =>: " + who);

        Log.d("==>", "seKey: " + seKey);
        String sKey = seKey.substring(0, 32);
        String kcv = seKey.substring(32, 38);
        Log.d("==>", who + "=>: " + sKey);
        Log.d("==>", "KCVV: " + kcv);

    }

}
