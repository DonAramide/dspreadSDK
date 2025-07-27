package com.dspread.demoui.activity.nibssImpl.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@RequiresApi(api = Build.VERSION_CODES.O)
public  class MyTripleDec {
    static Cipher encryptCipher;

    public static String encp(String key, String ivKey, String msg){
        System.out.println("key  ==> "+ key);
        System.out.println("ivKey  ==> "+ ivKey);
        System.out.println("msg  ==> "+ msg);

        byte[] secretKey = key.getBytes(); //9mng65v8jf4lxn93nabf981m
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "TripleDES");
        byte[] iv = ivKey.getBytes(); //a76nb5h9
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        String encodedMessage = null;

        try {

            encryptCipher = Cipher.getInstance("TripleDES/ECB/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            String secretMessage = msg; //Baeldung secret message
            Cipher encryptCipher = Cipher.getInstance("TripleDES/ECB/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            byte[] secretMessagesBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessagesBytes);

             encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

            System.out.println("secretMessagesBytes  ==> "+ Arrays.toString(secretMessagesBytes));
            System.out.println("encodedMessage  ==> "+ encodedMessage);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return encodedMessage;
    }




    public void dencp(String key, String ivKey, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        System.out.println("key  ==> "+ key);
        System.out.println("ivKey  ==> "+ ivKey);
        System.out.println("msg  ==> "+ msg);


        byte[] iv = ivKey.getBytes(); //a76nb5h9
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        byte[] secretKey = key.getBytes(); //9mng65v8jf4lxn93nabf981m
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "TripleDES");

        Cipher decryptCipher = Cipher.getInstance("TripleDES/ECB/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        byte[] decryptedMessageBytes = decryptCipher.doFinal( msg.getBytes(StandardCharsets.UTF_8));
        String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
       // Assertions.assertEquals(secretMessage, decryptedMessage);

        System.out.println("decryptedMessageBytes  ==> "+ Arrays.toString(decryptedMessageBytes));
        System.out.println("decryptedMessage  ==> "+ decryptedMessage);

    }
}
