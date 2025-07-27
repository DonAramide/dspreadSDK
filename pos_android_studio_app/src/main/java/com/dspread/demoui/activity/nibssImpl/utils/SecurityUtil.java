package com.dspread.demoui.activity.nibssImpl.utils;


import static java.nio.charset.StandardCharsets.UTF_8;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {

    public  static String EncryptData(String ptx)
    {
       String result = null;
        try {
           ptx = StringSplitter.GenerateRandomTextNo(3) + ptx + StringSplitter.GenerateRandomTextNo(4);
           String str = encrypt(ptx, Globals.ENKEY );
           byte[] datab = str.getBytes("UTF-8");
           result = Base64.encodeToString(datab, Base64.DEFAULT);
       }
       catch (Exception ex)
       {
           ex.printStackTrace();
           //Log.d("MT-Encrypt", "Message.." + str);
       }
        return  result;
    }

    public  static String DecryptData(String ptx)
    {

        System.out.println("its =>" + "DecryptData==>"+ ptx);
        String result = null;
        try {
            byte[] datab = Base64.decode(ptx, Base64.DEFAULT);
            String text = new String(datab, "UTF-8");
            String str = SecurityUtil.decrypt(text, Globals.ENKEY);
            result = str.substring(3, str.length() - 4);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Log.d("MT-Encrypt", "Message.." + str);
        }
        return  result;
    }

    public static byte[] encrypt2(byte[] data, byte[] key, byte[] ivs) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] finalIvs = new byte[16];
            int len = ivs.length > 16 ? 16 : ivs.length;
            System.arraycopy(ivs, 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt2(byte[] data, byte[] key, byte[] ivs) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            byte[] finalIvs = new byte[16];
            int len = ivs.length > 16 ? 16 : ivs.length;
            System.arraycopy(ivs, 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String GetRFC1123DateFormat() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }


    public static String generateXpaytoken(String resourcePath, String queryString, String requestBody, String sharedSecret) throws SignatureException {
        String timestamp = timeStamp();
        String beforeHash = timestamp + resourcePath + queryString + requestBody;
        String hash = hmacSha256Digest(beforeHash, sharedSecret);
        String token = "OJT:" + timestamp + ":" + hash + ":OJT";
        return token;
    }

    private static String timeStamp() {
        return String.valueOf(System.currentTimeMillis()/ 1000L);
    }

    private static String hmacSha256Digest(String data, String sharedSecret)
            throws SignatureException {
        return getDigest("HmacSHA256", sharedSecret, data, true);
    }

    private static String getDigest(String algorithm, String sharedSecret, String data,  boolean toLower) throws SignatureException {
        try {
            Mac sha256HMAC = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(sharedSecret.getBytes("UTF-8"), algorithm);
            sha256HMAC.init(secretKey);

            byte[] hashByte = sha256HMAC.doFinal(data.getBytes("UTF-8"));
            String hashString = toHex(hashByte);

            return toLower ? hashString.toLowerCase() : hashString;
        } catch (Exception e) {
            throw new SignatureException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    private static final String characterEncoding = "UTF-8";
    private static final String cipherTransformation = "AES/CBC/PKCS5Padding";
    private static final String aesEncryptionAlgorithm = "AES";
    public static final String key2 = "1234567812345678";



    public static String decrypt(String plainTextString, String SecretKey)
            throws KeyException, GeneralSecurityException,
            GeneralSecurityException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        byte[] cipheredBytes = Base64.decode(plainTextString,Base64.DEFAULT);
        byte[] keyBytes = getKeyBytes(SecretKey);
        return new String(decrypt(cipheredBytes, keyBytes, keyBytes),characterEncoding);
    }

    public static byte[] decrypt(byte[] cipherText, byte[] key, byte[] initialVector)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key,aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    public static byte[] encrypt(byte[] plainText, byte[] key, byte[] initialVector)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        plainText = cipher.doFinal(plainText);
        return plainText;
    }

    private static byte[] getKeyBytes(String key1) throws UnsupportedEncodingException {

        byte[] keyBytes = new byte[16];
        byte[] parameterKeyBytes = key2.getBytes(characterEncoding);
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
                Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }

    public static String encrypt(String plainText, String key)
            throws UnsupportedEncodingException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {

        byte[] plainTextbytes = plainText.getBytes(characterEncoding);
        byte[] keyBytes = getKeyBytes(key);
        byte[] bbe = encrypt(plainTextbytes, keyBytes, keyBytes);
        return new String(Base64.encode(bbe, Base64.DEFAULT),characterEncoding);
    }

    public static String computeHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        byte[] byteData = digest.digest(input.getBytes("UTF-8"));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++){
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String encrypPan(String number) {
        Log.d("Pan=>", "Incoming ST=> "+ number );
        String temp = number;
        int[] numbers = new int[temp.length()];
        String joined = "";

        joined  = String.format("%s*******%s", number.substring(0, 3), number.substring(number.length() - 4));
        System.out.println("Array after adding element: "+joined);
        return joined;
    }
    public static String getCard(String number) {
        Log.d("=","--Card Org"+number);

        String temp = number;
        String joined = "";
      if(number.startsWith("4")  && temp.length() == 16 ){

            joined="VISA CARD";
        } else if(number.startsWith("5") && temp.length() == 16 ){

            joined="MASTER CARD" ;
        }
        else if(number.startsWith("5") && temp.length() == 19 ){

            joined="VERVE CARD" ;
        } else {
            joined="OTHER CARD";

        }
        Log.d("=","--Card Org"+joined);
        System.out.println("Array after adding element: "+joined);
        return joined;
    }






    //=========================================================================================
    public static SecretKey createKey(final String algorithm, final int keysize, final Optional<Provider> provider, final Optional<SecureRandom> rng) throws NoSuchAlgorithmException {
        final KeyGenerator keyGenerator;
        if (provider.isPresent()) {
            keyGenerator = KeyGenerator.getInstance(algorithm, provider.get());
        } else {
            keyGenerator = KeyGenerator.getInstance(algorithm);
        }

        if (rng.isPresent()) {
            keyGenerator.init(keysize, rng.get());
        } else {
            // not really needed for the Sun provider which handles null OK
            keyGenerator.init(keysize);
        }

        return keyGenerator.generateKey();
    }

    public static IvParameterSpec createIV(final int ivSizeBytes, final Optional<SecureRandom> rng) {
        final byte[] iv = new byte[ivSizeBytes];
        final SecureRandom theRNG = rng.orElse(new SecureRandom());
        theRNG.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static IvParameterSpec readIV(final int ivSizeBytes, final InputStream is) throws IOException {
        final byte[] iv = new byte[ivSizeBytes];
        int offset = 0;
        while (offset < ivSizeBytes) {
            final int read = is.read(iv, offset, ivSizeBytes - offset);
            if (read == -1) {
                throw new IOException("Too few bytes for IV in input stream");
            }
            offset += read;
        }
        return new IvParameterSpec(iv);
    }

    public static void main(String[] args) throws Exception {
        final SecureRandom rng = new SecureRandom();
        // you somehow need to distribute this key
        final SecretKey aesKey = createKey("AES", 128, Optional.empty(), Optional.of(rng));
        final byte[] plaintext = "owlstead".getBytes(UTF_8);

        final byte[] ciphertext;
        {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            final Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec ivForCBC = createIV(aesCBC.getBlockSize(), Optional.of(rng));
            aesCBC.init(Cipher.ENCRYPT_MODE, aesKey, ivForCBC);

            baos.write(ivForCBC.getIV());

            try (final CipherOutputStream cos = new CipherOutputStream(baos, aesCBC)) {
                cos.write(plaintext);
            }

            ciphertext = baos.toByteArray();
        }

        final byte[] decrypted;
        {
            final ByteArrayInputStream bais = new ByteArrayInputStream(ciphertext);

            final Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final IvParameterSpec ivForCBC = readIV(aesCBC.getBlockSize(), bais);
            aesCBC.init(Cipher.DECRYPT_MODE, aesKey, ivForCBC);

            final byte[] buf = new byte[1_024];
            try (final CipherInputStream cis = new CipherInputStream(bais, aesCBC);
                 final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int read;
                while ((read = cis.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                decrypted = baos.toByteArray();
            }
        }

        System.out.println(new String(decrypted, UTF_8));
    }



    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}