package com.dspread.demoui.activity.nibssImpl.utils;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

//import com.horizonpay.smartpossdk.data.PrinterConst;
//import com.iips.mbbs_horizon.R;
//import com.iips.mbbs_horizon.activity.MainActivity;
//import com.iips.mbbs_horizon.mbbs.IsoMessageClient;
//import com.iips.mbbs_horizon.mbbs.model.GaTranResponseInfo;

import com.dspread.demoui.R;
import com.dspread.demoui.activity.MainActivity;
import com.dspread.demoui.activity.nibssImpl.EmvTransResult;
import com.dspread.demoui.activity.nibssImpl.IsoMessageClient;
import com.dspread.demoui.activity.nibssImpl.model.GaTranResponseInfo;
import com.dspread.demoui.utils.TLV;

import org.jpos.iso.ISOMsg;

import java.nio.ByteBuffer;


public class FormUtility {

	public static String GetCcyLabel(String ccy)
	{
		String c = ccy;
		if(c == null)
			c ="";
		if(c.equals("NGN"))
			return "\u20A6";
		else if(c.equals("GHS"))
			return "\u20B5";
		else
			return c;
	}

	public static int GetCcyDecimal(String ccy)
	{
		int c = 2;
		if(ccy.equals("XOF"))
			return 0;
		else if(ccy.equals("XAF"))
			return 0;
		else
			return c;
	}
	
	


	public static void ShowSnapMessage(View v,  String msg)
	{
		Log.e("LOGGER", msg);
//		Snackbar.make(v, msg, Snackbar.LENGTH_LONG).show();
	}
	
	@SuppressLint("NewApi")
	public static byte[] ConvertBitmapToByteArray(Bitmap b)
	{
		int bytes = b.getByteCount();
		ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
		b.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

		byte[] array = buffer.array(); 
		return array;
	}
	
	public static Bitmap ConvertArrayToBitmpa(byte[] b)
	{
		Bitmap bitmap = BitmapFactory.decodeByteArray(b , 0, b.length);
		return bitmap;
	}
	
	public static void CloseApp(final Activity acty)
	{
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(acty);
		alertDialogBuilder.setTitle("Mobile App");
		alertDialogBuilder.setMessage("Are you ready to exit application?");
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
						acty.finish();
						//System.exit(0);
						Intent iform2 = new Intent(acty,  MainActivity.class);
						Bundle bundle2 = new Bundle();
						//bundle2.putParcelable("UserInfo", Globals.userInfo);
						iform2.putExtras(bundle2);
						acty.startActivity(iform2);

					}
				}).setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
		acty.finish();
	}

    public static void showAlertDialog(String title,String msg,Activity actx) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(actx);
        builder.setTitle(title); //R.string.title_incomplete_data);
        builder.setMessage(msg);
        /*builder.setPositiveButton(R.string.title_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(parent_view, "Discard clicked", Snackbar.LENGTH_SHORT).show();
            }
        });*/
        builder.setNegativeButton("OK", null);
        builder.show();
    }

	public static String getNibssMessage(@NonNull String rspCode){

		String message = "";
		switch (rspCode) {
			case "00":
				message = "Approved"; //Approve
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "01":
				message = "Refer to card issuer";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "02":
				message = "Refer to card issuer, special condition";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "03":
				message = "Invalid merchant";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "04":
				message = "Pick-up card";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "05":
				message = "Do not honor";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "06":
				message = "Error";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "07":
				message = " Pick-up card, special condition";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "08":
				message = "Honor with identification";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "09":
				message = " Request in progress";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "10":
				message = "Declined, partial";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "11":
				message = "Declined, VIP";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "12":
				message = "Invalid transaction";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "13":
				message = "Invalid amount";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "14":
				message = "Invalid card number";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "15":
				message = "No such issuer";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "16":
				message = "Declined, update track 3";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "17":
				message = "Customer cancellation";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "18":
				message = "Customer dispute";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "19":
				message = "Re-enter transaction";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "20":
				message = "Invalid response";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "21":
				message = "No action taken";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "22":
				message = "Suspected malfunction";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "23":
				message = "Unacceptable transaction fee";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "24":
				message = "File update not supported";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "25":
				message = "Unable to locate record";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "26":
				message = "Duplicate record";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "27":
				message = "File update edit error";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "28":
				message = "File update file locked";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "29":
				message = "File update failed";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "30":
				message = "Format error";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "31":
				message = "Bank not supported";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "32":
				message = "Completed partially";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "33":
				message = "Expired card, pick-up";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "34":
				message = "Suspected fraud, pick-up";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "35":
				message = "Contact acquirer, pick-up";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "36":
				message = "Restricted card, pick-up";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "37":
				message = "Call acquirer security, pick-up";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "38":
				message = "PIN tries exceeded, pick-up";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "39":
				message = "No credit account";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "40":
				message = "Function not supported";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "41":
				message = "Lost card";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "42":
				message = "No universal account";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "43":
				message = "Stolen card";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "44":
				message = "No investment account";
				Log.d("TAG", message+ " " + rspCode );
				
				break;

			case "51":
				message = "Not sufficient funds";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "52":
				message = "No check account";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "53":
				message = "No savings account";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "54":
				message = "Expired card";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "55":
				message = "Incorrect PIN";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "56":
				message = "No card record";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "57":
				message = "Transaction not permitted to cardholder";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "58":
				message = " Transaction not permitted on terminal";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "59":
				message = "Suspected fraud";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "60":
				message = "Contact acquirer";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "61":
				message = "Exceeds withdrawal limit";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "62":
				message = "Restricted card";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "63":
				message = "Security violation";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "64":
				message = "Original amount incorrect";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "65":
				message = "Exceeds withdrawal frequency";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "66":
				message = "Call acquirer security";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "67":
				message = "Hard capture";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "68":
				message = "Response received too late";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "75":
				message = "PIN tries exceeded";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "77":
				message = "Intervene, bank approval required";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "78":
				message = "Intervene, bank approval required for partial amount";
				Log.d("TAG", message+ " " + rspCode );
				
				break;

			case "90":
				message = "Cut-off in progress";
				Log.d("TAG", message+ " " + rspCode );
				
				break;

			case "91":
				message = "Issuer or switch inoperative";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "92":
				message = "Routing error";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "93":
				message = "Violation of law";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "94":
				message = "Duplicate transaction";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "95":
				message = "Reconcile error";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "96":
				message = "System malfunction";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			case "98":
				message = "Exceeds cash limit";
				Log.d("TAG", message+ " " + rspCode );
				
				break;
			default:
				message = "Ask NIBSS";
				Log.d("TAG", message+ " " + rspCode );

				break;
		}

		return  message;
	}


	public static String removeOddIndexCharacters(String allPin) {
		String s = allPin.substring(8);
		Log.d("TAG", "********  In Pin=>: " + s);
		// Stores the resultant string
		String new_string = "";

		for (int i = 0; i < s.length(); i++) {

			// If the current index is odd
			if (i % 2 == 0)

				// Skip the character
				continue;

			// Otherwise, append the
			// character
			new_string += s.charAt(i);
		}
		Log.d("TAG", "********  In Pin after =>: " + new_string);
		// Return the modified string
		return new_string;
	}
	public static String getPOSSocketRequest(ISOMsg msg, GaTranResponseInfo gaTranResponseInfo) {
		String s ="";
		String s59 = (msg.getString(59) == null )? "":msg.getString(59);
		String s62 = (msg.getString(62) == null )? "":msg.getString(62);
		String s38 = (msg.getString(38) == null )? "":msg.getString(38);



//		s = "{" +
//				"\"notification_code\": \""+gaTranResponseInfo.getTransactionType()+"\",\n" +
//				        "\"transType\": \"" + msg.getString(39) + "\",\n" +
//				"  \"upstream\": [\"posvas\",\"notification\"],\n" +
//				"  \"destination\": \"https://posvas.globalaccelerex.com/gass/GASService.svc/validate/\",\n" +
//				"  \"synchronous\": \"true\",\n" +
//				"  \"data\": {\n" +
//				"    \"trans_ref\": \""+ "K11"+msg.getString(7) +""+msg.getString(4)+ ""+ gaTranResponseInfo.getRrn() +""+gaTranResponseInfo.getStan()+"\",\n" +
//				"    \"de1\": \""+ msg.getString(0)+"\",\n" +
//				"    \"de2\": \""+gaTranResponseInfo.getMaskedPan()+"\",\n" +
//				"    \"de3\": \""+msg.getString(3)+"\",\n" +
//				"    \"de4\": \""+msg.getString(4)+"\",\n" +
//				"    \"de7\": \""+gaTranResponseInfo.getPosSocketDate()+"\",\n" +
//				"    \"de11\": \""+msg.getString(11)+"\",\n" +
//				"    \"de12\": \""+msg.getString(12)+"\",\n" +
//				"    \"de13\": \""+msg.getString(13)+"\",\n" +
//				"    \"de14\": \""+msg.getString(14)+"\",\n" +
//				"    \"de37\": \""+msg.getString(37)+"\",\n" +
//				"    \"de38\": \""+s38+"\",\n" +
//				"    \"de39\": \""+msg.getString(39)+"\",\n" +
//				"    \"de41\": \""+msg.getString(41)+"\",\n" +
//				"    \"de42\": \""+msg.getString(42)+"\",\n" +
//				"    \"de43\": \""+msg.getString(43)+"\",\n" +
//				"    \"de59\": \""+s59+"\",\n" +
//				"    \"de62\": \""+s62+"\",\n" +
//				"    \"serial_number\": \""+gaTranResponseInfo.getSerialNumber()+"\",\n" +
//				"    \"host_ip\": \""+gaTranResponseInfo.getIp()+"\",\n" +
//				"    \"host_port\": \""+gaTranResponseInfo.getPort()+"\"\n" +
//				"  },\n" +
//				"  \"billerNotification\": {\n" +
//				"    \"Reference\": \"IN-10101\", \n" +
//				"\t\"billerReference\":\"0000000\",\n" +
//				"    \"Currency\": \"NGN\",\n" +
//				"    \"Type\": \"invoice\",    \n" +
//				"    \"MaskedPAN\": \""+gaTranResponseInfo.getMaskedPan()+"\",\n" +
//				"    \"CardScheme\": \""+gaTranResponseInfo.getCardScheme()+"\",\n" +
//				"    \"CustomerName\": \""+""+"\",\n" +
//				"    \"StatusCode\": \""+gaTranResponseInfo.getStatuscode()+"\",\n" +
//				"    \"RetrievalReferenceNumber\": \""+gaTranResponseInfo.getRrn() +"\",\n" +
//				"    \"StatusDescription\": \""+gaTranResponseInfo.getMessage()+"\",\n" +
//				"    \"PaymentDate\": \""+gaTranResponseInfo.getDatetime()+"\",\n" +
//				"    \"AdditionalInformation\": [\n" +
//				"      {\n" +
//				"        \"Name\": \"Email\"\n" +
//				"      },\n" +
//				"      {\n" +
//				"        \"Value\": \"GaK11@GaK11.com\"\n" +
//				"      }\n" +
//				"    ],\n" +
//				"    \"Stan\": \""+gaTranResponseInfo.getStan()+"\",\n" +
//				"    \"CardExpiry\": \""+gaTranResponseInfo.cardExpireDate+"\",\n" +
//				"    \"CardHash\": \""+   IsoMessageClient.Hash256Message(msg.getString(2))+"\"\n" +
//				"}," +
//				"    \"extraInfo\": {\n" +
//				"     \"app_version\": \"Accelerex-NG-K-1.0.1\",\n" +
//				"    \"serial_number\": \""+gaTranResponseInfo.getSerialNumber()+"\",\n" +
//				"    \"latitude\": \""+gaTranResponseInfo.getLat()+"\",\n" +
//				"    \"longitude\": \""+gaTranResponseInfo.getLon()+"\"\n" +
//				"    },\n" +
//				"    \"Stan\": \""+gaTranResponseInfo.getStan()+"\",\n" +
//				"    \"CardExpiry\": \""+gaTranResponseInfo.cardExpireDate+"\",\n" +
//				"    \"CardHash\": \""+   IsoMessageClient.Hash256Message(gaTranResponseInfo.getPayAuthCode())+"\"\n" +
//				"}<<EOF>>";



		s = "{" +
				"notification_code:"+gaTranResponseInfo.getTransactionType()+",\n" +
				"transType:"  + msg.getString(39) +",\n" +
				 "upstream\": [\"posvas\",\"notification\"],\n" +
			 "destination\": \"https://posvas.globalaccelerex.com/gass/GASService.svc/validate/"+",\n" +
				 "synchronous\": \"true\",\n" +
				 "data\": {\n" +
				 "trans_ref\": \""+ "K11"+msg.getString(7) +""+msg.getString(4)+ ""+ gaTranResponseInfo.getRrn() +""+gaTranResponseInfo.getStan()+"\",\n" +
				 "de1\": \""+ msg.getString(0)+",\n" +
				 "de2\": \""+gaTranResponseInfo.getMaskedPan()+",\n" +
				 "de3\": \""+msg.getString(3)+",\n" +
				 "de4\": \""+msg.getString(4)+",\n" +
				 "de7\": \""+gaTranResponseInfo.getPosSocketDate()+",\n" +
				 "de11\": \""+msg.getString(11)+",\n" +
				 "de12\": \""+msg.getString(12)+",\n" +
				 "de13\": \""+msg.getString(13)+",\n" +
				 "de14\": \""+msg.getString(14)+",\n" +
				 "de37\": \""+msg.getString(37)+",\n" +
				 "de38\": \""+s38+",\n" +
				 "de39\": \""+msg.getString(39)+",\n" +
				 "de41\": \""+msg.getString(41)+",\n" +
				 "de42\": \""+msg.getString(42)+",\n" +
				 "de43\": \""+msg.getString(43)+",\n" +
				 "de59\": \""+s59+",\n" +
				 "de62\": \""+s62+",\n" +
				 "serial_number\": \""+gaTranResponseInfo.getSerialNumber()+",\n" +
				 "host_ip\": \""+gaTranResponseInfo.getIp()+",\n" +
				 "host_port\": \""+gaTranResponseInfo.getPort()+"\"\n" +
				"  },\n" +
				 "billerNotification\": {\n" +
				 "Reference\": \"IN-10101"+",\n" +
				 "billerReference\":\"0000000"+",\n" +
				 "Currency\": \"NGN"+",\n" +
				 "Type\": \"invoice"+",\n" +
				 "MaskedPAN\": \""+gaTranResponseInfo.getMaskedPan()+",\n" +
				 "CardScheme\": \""+gaTranResponseInfo.getCardScheme()+",\n" +
				 "CustomerName\": \""+""+",\n" +
				 "StatusCode\": \""+gaTranResponseInfo.getStatuscode()+",\n" +
				"RetrievalReferenceNumber\": \""+gaTranResponseInfo.getRrn() +",\n" +
				"StatusDescription\": \""+gaTranResponseInfo.getMessage()+",\n" +
				"PaymentDate\": \""+gaTranResponseInfo.getDatetime()+",\n" +
				"AdditionalInformation\": [\n" +
				"      {\n" +
				"        \"Name\": \"Email"+",\n" +
				"      },\n" +
				"      {\n" +
				"        \"Value\": \"GaK11@GaK11.com"+",\n" +
				"      }\n" +
				"    ],\n" +
				"Stan\": \""+gaTranResponseInfo.getStan()+",\n" +
				"CardExpiry\": \""+gaTranResponseInfo.cardExpireDate+",\n" +
				"CardHash\": \""+   IsoMessageClient.Hash256Message(msg.getString(2))+"\"\n" +
				"}," +
				"extraInfo\": {\n" +
				"     \"app_version\": \"Accelerex-NG-K-1.2.0\",\n" +
				"serial_number\": \""+gaTranResponseInfo.getSerialNumber()+",\n" +
				"latitude\": \""+gaTranResponseInfo.getLat()+",\n" +
				"longitude\": \""+gaTranResponseInfo.getLon()+"\"\n" +
				"},\n" +
				"Stan\": \""+gaTranResponseInfo.getStan()+",\n" +
				"CardExpiry\": \""+gaTranResponseInfo.cardExpireDate+",\n" +
				"CardHash\": \""+   IsoMessageClient.Hash256Message(gaTranResponseInfo.getPayAuthCode())+"\"\n" +
				"}<<EOF>>";





/*
  "extraInfo": {
        "app_version": "Accelerex-NG 1.9.2-20201012",
        "battery": "39",
        "cell_id": "40172",
        "csq": "",
        "imsi": "wifi",
        "lac": "3030",
        "latitude": "7.33547969",
        "longitude": "3.94114872",
        "mcc": "621",
        "mnc": "50",
        "paper": "PAPER OK",
        "serial_number": "N300W003130"
    },
 */
		Log.d("TAG", "********  In Pin after =>: " + s);
		// Return the modified string
		return s;
	}

	public static String getPOSCALHOMERequest(GaTranResponseInfo gaTranResponseInfo) {
		String s ="";
		//"     \"callbackUrl\": \"https://democallcallback.service.com/api/PTSP/PostHardwareGA\",\n" +
		/*
{
    "data": {
        "de1": "0810",


        "de39": "00",
        "de41": "2101JS56",
        "de42": "2101LA00000BM62",
        "de43": "KongaPay               LA           LANG",
        "de62": "01011N300W0031300900320010020GLOBAL ACCELEREX LTD11047BATTERY OK&PAPER OK&ACCELEREX-NG 1.9.2-20201012",
        "de7": "20210108204405",
        "trans_ref": "2101JS5630631020210108204405"
    },
    "destination": "",
    "extraInfo": {
        "app_version": "Accelerex-NG 1.9.2-20201012",
        "battery": "39",
        "cell_id": "40172",
        "csq": "",
        "imsi": "wifi",
        "lac": "3030",
        "latitude": "7.33547969",
        "longitude": "3.94114872",
        "mcc": "621",
        "mnc": "50",
        "paper": "PAPER OK",
        "serial_number": "N300W003130"
    },
    "notification_code": "GA1000000000000",
    "synchronous": "false"
}
 */


		/*
{
    "notification_code": "GA1000000000000",
    "data": {
        "trans_ref": "2214W23E00172920240402132317",
        "de1": "0810",
        "de3": "9D0000",
        "de7": "20240402132317",
        "de11": "001729",
        "de12": "132317",
        "de13": "0402",
        "de39": "00",
        "de41": "2214W23E",
        "de42": "2214RI188380016",
        "de43": "EBOH SUNDAY            3 OMOKU STREEXXNG",
        "de62": "01011G200WTB8821"
    },
    "extraInfo": {
        "serial_number": "G200WTB8821",
        "app_version": "Rex NGL 3.0.0-230224-Vanila-debug-120",
        "charge_state": "1",
        "battery": "100",
        "imsi": "WIFI LINK UP",
        "paper": "PAPER OK",
        "cell_id": 0,
        "lac": 0,
        "mcc": 0,
        "mnc": 0,
        "csq": 0
    }
}
 */


//		s = "{" +
//				"\"data\": {\n" +
//				"\"trans_ref\": \""+ gaTranResponseInfo.getRrn() +""+gaTranResponseInfo.getStan()+"\",\n" +
//				"\t\"de1\":\"0810\",\n" +
//				"\"de3\": \""+gaTranResponseInfo.getDe3()+"\",\n" +
//				"\"de7\": \""+gaTranResponseInfo.getDe7()+"\",\n" +
//				"\"de11\": \""+gaTranResponseInfo.getDe11()+"\",\n" +
//				"\"de12\": \""+gaTranResponseInfo.getDe12()+"\",\n" +
//				"\"de13\": \""+gaTranResponseInfo.getDe13()+"\",\n" +
//				"\"de39\": \""+gaTranResponseInfo.getDe39()+"\",\n" +
//				"\"de41\": \""+gaTranResponseInfo.getDe41()+"\",\n" +
//				"\"de42\": \""+gaTranResponseInfo.getDe42()+"\",\n" +
//				"\"de43\": \""+gaTranResponseInfo.getDe43()+"\",\n" +
//				"\"de62\": \""+gaTranResponseInfo.getDe62()+"\"\n" +
//				" },\n" +
//				"    \"extraInfo\": {\n" +
//				"     \"app_version\": \"Accelerex-NG-K-1.1.0\",\n" +
//
//
//				"    \"battery\": \""+gaTranResponseInfo.getBatteryStatus()+"\",\n" +
//				"    \"cell_id\": \""+gaTranResponseInfo.getCellId()+"\",\n" +
//				"    \"csq\": \""+ ""+"\",\n" +
//				"    \"imsi\": \""+gaTranResponseInfo.getImsi()+"\",\n" +
//				"    \"lac\": \""+"0000"+"\",\n" +
//
//				"    \"mcc\": \""+gaTranResponseInfo.getMcc()+"\",\n" +
//				"    \"mnc\": \""+gaTranResponseInfo.getMnc()+"\",\n" +
//
//
//				"     \"app_version\": \"Accelerex-NG-K-1.1.0\",\n" +
//
//				"    \"latitude\": \""+gaTranResponseInfo.getLat()+"\",\n" +
//				"    \"longitude\": \""+gaTranResponseInfo.getLon()+"\",\n" +
//				"    \"serial_number\": \""+gaTranResponseInfo.getSerialNumber()+"\",\n" +
//				"    \"paper\": \""+gaTranResponseInfo.getPaperStatus()+"\",\n" +
//				"\"notification_code\": \""+ gaTranResponseInfo.getRrn() +""+gaTranResponseInfo.getStan()+"\"" +
//
//				"    },\n" +
//				"    \"subscriptionReference\": \"false\"\n" +
//				"}<<EOF>>";


//		s = "{" +
//				"\"data\": {\n" +
//				"\"trans_ref\": \""+ gaTranResponseInfo.getRrn() +""+gaTranResponseInfo.getStan()+"\",\n" +
//				"\"de1\":\"0810\",\n" +
//				"\"de3\": \""+gaTranResponseInfo.getDe3()+"\",\n" +
//				"\"de7\": \""+gaTranResponseInfo.getDe7()+"\",\n" +
//				"\"de11\": \""+gaTranResponseInfo.getDe11()+"\",\n" +
//				"\"de12\": \""+gaTranResponseInfo.getDe12()+"\",\n" +
//				"\"de13\": \""+gaTranResponseInfo.getDe13()+"\",\n" +
//				"\"de39\": \""+gaTranResponseInfo.getDe39()+"\",\n" +
//				"\"de41\": \""+gaTranResponseInfo.getDe41()+"\",\n" +
//				"\"de42\": \""+gaTranResponseInfo.getDe42()+"\",\n" +
//				"\"de43\": \""+gaTranResponseInfo.getDe43()+"\",\n" +
//				"\"de62\": \""+gaTranResponseInfo.getDe62()+"\"\n" +
//				" },\n" +
//				"    \"extraInfo\": {\n" +
//				"     \"app_version\": \"Accelerex-NG-K-1.1.0\",\n" +
//
//
//				"    \"battery\": \""+gaTranResponseInfo.getBatteryStatus()+"\",\n" +
//				"    \"cell_id\": \""+gaTranResponseInfo.getCellId()+"\",\n" +
//				"    \"csq\": \""+ ""+"\",\n" +
//				"    \"imsi\": \""+gaTranResponseInfo.getImsi()+"\",\n" +
//				"    \"lac\": \""+"0000"+"\",\n" +
//
//				"    \"mcc\": \""+gaTranResponseInfo.getMcc()+"\",\n" +
//				"    \"mnc\": \""+gaTranResponseInfo.getMnc()+"\",\n" +
//
//
//				"     \"app_version\": \"Accelerex-NG-K-1.1.0\",\n" +
//
//				"    \"latitude\": \""+gaTranResponseInfo.getLat()+"\",\n" +
//				"    \"longitude\": \""+gaTranResponseInfo.getLon()+"\",\n" +
//				"    \"serial_number\": \""+gaTranResponseInfo.getSerialNumber()+"\",\n" +
//				"    \"paper\": \""+gaTranResponseInfo.getPaperStatus()+"\",\n" +
//				"\"notification_code\": \""+ gaTranResponseInfo.getRrn() +""+gaTranResponseInfo.getStan()+"\"" +
//
//				"    },\n" +
//				"    \"subscriptionReference\": \"false\"\n" +
//				"}<<EOF>>";




		     s = "{\n" +
				"\t\"notification_code\": \""+ gaTranResponseInfo.getRrn() +""+gaTranResponseInfo.getStan()+"\",\n" +
				"\t\"data\": {\n" +
				"trans_ref\": \""+ gaTranResponseInfo.getRrn() +""+gaTranResponseInfo.getStan()+"\",\n" +
				"\t\t\"de1\":\"0810\",\n" +
				"\t\t\"de3\": \""+gaTranResponseInfo.getDe3()+"\",\n" +
				"\t\t\"de7\": \""+gaTranResponseInfo.getDe7()+"\",\n" +
				"\t\t\"de11\": \""+gaTranResponseInfo.getDe11()+"\",\n" +
				"\t\t\"de12\": \""+gaTranResponseInfo.getDe12()+"\",\n" +
				"\t\t\"de13\": \""+gaTranResponseInfo.getDe13()+"\",\n" +
				"\t\t\"de39\": \""+gaTranResponseInfo.getDe39()+"\",\n" +
				"\t\t\"de41\": \""+gaTranResponseInfo.getDe41()+"\",\n" +
				"\t\t\"de42\": \""+gaTranResponseInfo.getDe42()+"\",\n" +
				"\t\t\"de43\": \""+gaTranResponseInfo.getDe43()+"\",\n" +
				"\t\t\"de62\": \""+gaTranResponseInfo.getDe62()+"\"\n" +
				 "\t},\n"+
				"\t\"extraInfo\": {\n" +
				"\t\t\"serial_number\": \""+gaTranResponseInfo.getSerialNumber()+"\",\n" +
				"\t\t\"app_version\": \"Accelerex-NG-K-1.2.0\",\n" +
				"\t\t\"charge_state\": \"1\",\n" +
				"\t\t\"battery\": \""+gaTranResponseInfo.getBatteryStatus()+"\",\n" +
				"\t\t\"imsi\": \""+gaTranResponseInfo.getImsi()+"\",\n" +
				"\t\t\"paper\": \""+gaTranResponseInfo.getPaperStatus()+"\",\n" +
				"\t\t\"cell_id\": \""+gaTranResponseInfo.getCellId()+"\",\n" +
				"\t\t\"latitude\": \""+gaTranResponseInfo.getLat()+"\",\n" +
				 "\t\t\"longitude\": \""+gaTranResponseInfo.getLon()+"\",\n" +
				"\t\t\"lac\": \""+"0"+"\",\n" +
				"\t\t\"mcc\": \""+"0"+"\",\n" +
				"\t\t\"mnc\":\""+"0"+"\",\n" +
				"\t\t\"csq\": \""+ "0"+"\"\n" +
				"\t}\n "+
				  "}<<EOF>>";

 	Log.d("TAG", "********  Call Home  Payload =>: " + s);
		// Return the modified string
		return s;
	}

/*
{"notification_code":"GA1000000000000",
"data": {"trans_ref":"2070AL3200192220200519140654",
"de1":"0810",
"de3":"9D0000,
"de7":"20200519140654",
"de11":"001922",
"de12":"140654",
"de13":"0519",
"de39":"00",
"de41":"2070AL32",
"de42":"FBP204011021396",
"de43":"GLOBAL ACCELEREX TEST LA LANG",
"de62":"01011G200WT098950900320010020GLOBAL ACCELEREX LTD1105450&&Accelerex 2.0.0-190520-LINT-118&40279&3029&272&1&012015272018013777860"},
"extraInfo":
{"serial_number":"G200WT09895",
 "app_version":"Accelerex 2.0.0-190520-LINT-118",
 "battery":"50",
 "paper":"PAPEROK",
 "imsi":"272018013777860", "
 latitude":"53.304590",
 "longitude":"-6.214187",
 "cell_id":40279,
  "lac":3029,
  "mcc":272,
  "mnc":1,
  "csq":0 }}
 */

	public static String get4F0(String msg) {
		//String  str = "5061082001023973551D2411601003777571";
		String result = "";
		String separator ="D";
		int sepPos = msg.indexOf(separator);
		String value = msg.substring(sepPos + separator.length());
		if (msg.length() > 16) {
			result = value.substring(4,7);
			System.out.println("=> "+result);
		}else {
			result = value.substring(0,3);
			System.out.println("=> "+result);
		}

		return result;
	}
	public static int getBatteryPercentage(Context context) {

		if (Build.VERSION.SDK_INT >= 21) {

			BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
			return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

		} else {

			IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = context.registerReceiver(null, iFilter);

			int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
			int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

			double batteryPct = level / (double) scale;

			return (int) (batteryPct * 100);
		}
	}



	public static String getConnectivityManager(Context context) {
		final String[] connectedServices = {""};
		final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
		manager.registerNetworkCallback(
				new NetworkRequest.Builder()
						.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
						.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
						.addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
						.build(),
				new ConnectivityManager.NetworkCallback() {
					@Override
					public void onAvailable(Network network) {
						/** here to get the available info
						 this ternary operation is not quite true, because non-metered
						 doesn't yet mean, that it's wifi
						 nevertheless, for simplicity let's assume that's true
						 */

						 connectedServices[0] = (manager.isActiveNetworkMetered() ? "SIM" : "WIFI");


					}

					@Override
					public void onCapabilitiesChanged(Network network,
													  NetworkCapabilities networkCapabilities) {
						/**here to get the change network info
						 Value is TRANSPORT_CELLULAR, TRANSPORT_WIFI,
						 TRANSPORT_BLUETOOTH, TRANSPORT_ETHERNET, TRANSPORT_VPN
						 */
						if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
							Log.i("vvv", "connected to TRANSPORT_CELLULAR");

						}
					}
				});
		return connectedServices[0];
	}


//	public static EmvTransResult showEmvTransResult_() {
//		EmvTransResult emvTransResult = new EmvTransResult();
//
//		StringBuilder builder = new StringBuilder();
//		StringBuilder builder2 = new StringBuilder();
//
//		TLV tlvDataList = null;
//		String tlv = null;
//		try {
//			///tlv =  getTlvByTags(EmvUtil.tags);
//			tlvDataList = TLV.fromBinary(tlv);
//
//			//AppLog.d(TAG, "ICC Data 2: " + "\n" + tlv);
//			emvTransResult.setICCData(tlv);
//			Log.e("THEM", "1");
//			emvTransResult.setCard_no(readPan());
//			Log.e("THEM", "2 ==>" + readPan());
//			//  emvTransResult.setCardOrg(CardUtil.getCardTypFromAid(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID).getValue()));
//			//  Log.e("THEM", "Card org 1B => "+ CardUtil.getCardTypFromAid(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID).getValue()));
//			Log.e("THEM", "3");
//
//			builder.append("---------------------------------------------------\n");
////            builder.append("Trans Amount: " + CardUtil.getCurrencyName(DeviceHelper.getEmvHandler().getTagValue(EmvTags.EMV_TAG_TM_CURCODE).substring(1)) + " "
////                    + FormatUtils.formatAmount(DeviceHelper.getEmvHandler().getTagValue(EmvTags.EMV_TAG_TM_AUTHAMNTN), 3, ",", 2) + "\n");
////            builder.append("Card No: " + readPan() + "\n");
////            builder.append("Card Org: " + CardUtil.getCardTypFromAid(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID).getValue()) + "\n");
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//
//
////        builder.append("Card ExpiryDate: " + readCardExpiryDate() + "\n");
////        emvTransResult.setCardExpiryDate(readCardExpiryDate());
//
//		Log.e("THEM", "4");
//		if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_CHNAME) != null) {
//			builder.append("Card Holder Name: " + tlvDataList.getTLV(EmvTags.EMV_TAG_IC_CHNAME).getGBKValue() + "\n");
//			emvTransResult.setCardHolderName(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_CHNAME).getGBKValue());
//		}
//		Log.e("THEM", "5");
//		if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_PANSN) != null) {
//			builder.append("Card Sequence Number: " + tlvDataList.getTLV(EmvTags.EMV_TAG_IC_PANSN).getValue() + "\n");
//			emvTransResult.setCardSequence(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_PANSN).getValue());
//		}
//
////      if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_SERVICECODE) != null) {
////      }
//
//		Log.e("THEM", "6");
//		if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_ISSCOUNTRYCODE) != null) {
//			builder.append("Card Issuer Country Code: " + tlvDataList.getTLV(EmvTags.EMV_TAG_IC_ISSCOUNTRYCODE).getValue() + "\n");
//			emvTransResult.setCardIssuerCountryCode(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_ISSCOUNTRYCODE).getValue());
//		}
//
//		Log.e("THEM", "7");
//		if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_APNAME) != null) {
//			builder.append("App name: " + tlvDataList.getTLV(EmvTags.EMV_TAG_IC_APNAME).getGBKValue() + "\n");
//			emvTransResult.setAppname(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_APNAME).getGBKValue());
//		}
//
//
//		Log.e("THEM", "8");
//		if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_APPLABEL) != null) {
//			builder.append("App label : " + tlvDataList.getTLV(EmvTags.EMV_TAG_IC_APPLABEL).getGBKValue() + "\n");
//			emvTransResult.setApplabel(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_APPLABEL).getGBKValue());
//		}
//
//		Log.e("THEM", "9");
//		if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_TRACK1DATA) != null) {
//			builder.append("Card Track 1: " + tlvDataList.getTLV(EmvTags.EMV_TAG_IC_TRACK1DATA).getValue() + "\n");
//			emvTransResult.setCardTrack1(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_TRACK1DATA).getValue());
//		}
//
//		Log.e("THEM", "Card org= 1 => " + tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID));
//		// Log.e("THEM", "Card org= 1A => " +CardUtil.getCardTypFromAid (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID).getValue()));
//		if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID) != null) {
//			builder.append("Card Org: " + CardUtil.getCardTypFromAid(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID).getValue() + "\n"));
//			emvTransResult.setCardOrg(CardUtil.getCardTypFromAid(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID).getValue()));
//		}
//
//		Log.e("THEM", "9I");
//		if (tlvDataList.getTLV(EmvTags.EMV_TAG_IC_TRACK1DATA) != null) {
//			builder.append("EMV_TAG_IC_AID: " + tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID).getValue() + "\n");
//			emvTransResult.setField4F(tlvDataList.getTLV(EmvTags.EMV_TAG_IC_AID).getValue());
//		}
//
//		Log.e("THEM", "10");
//		builder.append("Card Track 2: " + EmvUtil.readTrack2() + "\n");
//		emvTransResult.setCardTrack2(EmvUtil.readTrack2());
//		builder.append("----------------------------\n");
//		for (String tag : EmvUtil.tags) {
//			builder.append(tag + "=" + tlvDataList.getTLV(tag) + "\n");
//			builder2.append(tag + "=" + tlvDataList.getTLV(tag) + "\n");
//			Log.e("DATA", builder.toString());
//			Log.e("DATA222", builder2.toString());
//		}
//
//
//		System.out.println("tlvDataList.getTLV(tag)B =>\n" + builder2);
//		emvTransResult.setTag(String.valueOf(builder2));
//		return emvTransResult;
//	}




}
