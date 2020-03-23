package com.example.ping.util;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;


public class Crypto {
    private static String TAG = Crypto.class.getName();
    public   static String KEY_ALIAS = "pingKeyAlias";
    public   static String SIGNATURE = "pingKeyAliasSignature";

    private static Crypto sInstance = null;
    private KeyStore mKeystore = null;
    private String pubKeyStr=null;

    private Crypto() {
        try {
            mKeystore = KeyStore.getInstance("AndroidKeyStore");
            mKeystore.load(null);

            if(mKeystore.getKey(KEY_ALIAS,null)==null){
                generateKeys();
            }
        } catch (Exception e) {
            LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"KeyStore Exception",true);
        }
    }

    public static Crypto getInstance() {
        if (sInstance == null) {
            sInstance = new Crypto();
        }
        return sInstance;
    }


    private void generateKeys(){
        try{
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.YEAR, 1);

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA);


            keyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(
                            KEY_ALIAS,
                            KeyProperties.PURPOSE_DECRYPT)
                            .setKeySize(1024)
                            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)//ENCRYPTION_PADDING_RSA_OAEP)
                            .build());

            KeyPair keyPair = keyPairGenerator.genKeyPair();


            pubKeyStr = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.NO_WRAP);


            SharedPreferencesManager.getInstance().setPublicKey(pubKeyStr);


            KeyPairGenerator signatureKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA);



            KeyGenParameterSpec parameterSpec  = new KeyGenParameterSpec.Builder(SIGNATURE,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                    .setCertificateSerialNumber(BigInteger.valueOf(777))
                    .setCertificateSubject(new X500Principal("CN=$SIGNATURE"))
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setCertificateNotBefore(startDate.getTime())
                    .setCertificateNotAfter(endDate.getTime())
                  .build();


            signatureKeyPairGenerator.initialize(parameterSpec);

            KeyPair kp = signatureKeyPairGenerator.generateKeyPair();
            PublicKey pk = kp.getPublic();
            PrivateKey prK =kp.getPrivate();

        } catch (Exception e){
            Log.e(TAG,"key generation error "+e.toString());
        }
    }


    public String getEncoded(String str){

        String enstr = new String();
        byte[] encodedBytes = null;
        try{
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, mKeystore.getCertificate(KEY_ALIAS).getPublicKey());
            encodedBytes = cipher.doFinal(str.getBytes());
        } catch (Exception e){
            LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"Encoded exception",true);
        }
        enstr = Base64.encodeToString(encodedBytes,Base64.DEFAULT);
        return enstr;
    }

    public  String getDecoded(String str){
        String destr = new String();
        byte[] encodedBytes = Base64.decode(str,Base64.DEFAULT);
        byte[] decodedBytes = null;
        try{
            KeyStore.Entry entry=  mKeystore.getEntry(KEY_ALIAS, null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            decodedBytes = cipher.doFinal(encodedBytes);
        }catch(Exception e){
            LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"Decoded exception",true);
        }
        try{destr = new String(decodedBytes);}catch (Exception e){}
        return destr;
    }

    public String sign(String message){


        String signatureString = null;

        try {
             KeyStore.Entry entry=  mKeystore.getEntry(SIGNATURE, null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKeyEntry.getPrivateKey());
            signature.update(message.getBytes());

            byte [] sign = signature.sign();
            signatureString = Base64.encodeToString(sign,Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }finally {
            return signatureString;
        }


    }


    public String verifySign(byte[] sign, String mes){

        String isValid =null;

        String decodeMessage = getDecoded(mes);
        Certificate certificate= null;
        try {
            certificate = mKeystore.getCertificate(SIGNATURE);
            if (certificate != null) {
                Signature sgn = Signature.getInstance("SHA256withRSA");
                sgn.initVerify(certificate);
                sgn.update(decodeMessage.getBytes());
                if(sgn.verify(sign)){
                    isValid = decodeMessage;
                    LogManager.getInstance().setLog(LogManager.LogType.DEBUG,TAG,"Decrypt message: "+decodeMessage,false);
                }else{
                    isValid = "Can't decrypt message";
                    LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"Can't decrypt message: ",true);
                }
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return isValid;
    }
}
