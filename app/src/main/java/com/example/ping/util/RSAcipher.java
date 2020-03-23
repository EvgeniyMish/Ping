package com.example.ping.util;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.ping.MyApp;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

import static com.example.ping.util.Crypto.KEY_ALIAS;
import static com.example.ping.util.Crypto.SIGNATURE;

public class RSAcipher {

    private static final String TAG = "RSA Cipher";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    private String
            pubKeyStr=null,
            priKeyStr=null;
    private Key
            publicKey=null,
            privateKey=null;


    private static RSAcipher savedInstance=null;

    public static RSAcipher getInstance() throws KeyStoreException {

        if(savedInstance==null){
            savedInstance = new RSAcipher();
        }
        return savedInstance;
    }
    KeyStore mKeystore;
    private RSAcipher() throws KeyStoreException {
        mKeystore = KeyStore.getInstance("AndroidKeyStore");
        try {
            mKeystore.load(null);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        pubKeyStr = SharedPreferencesManager.getInstance().getPublicKey();


        if(pubKeyStr==null){
            generateKeys();
        }
    }

    private void generateKeys(){
        try{
            /*KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "BC");
            kpg.initialize(1024, new SecureRandom());
            kpg.initialize(new KeyGenParameterSpec.Builder(KEY_ALIAS,KeyProperties.PURPOSE_WRAP_KEY)

                    .build());
            KeyPair keyPair = kpg.genKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            byte[] pubByte = publicKey.getEncoded();
            pubKeyStr = new String(Base64.encodeToString(pubByte,Base64.DEFAULT));*/
            /*byte[] priByte = privateKey.getEncoded();
            priKeyStr = new String(Base64.encodeToString(priByte,Base64.DEFAULT));*/




            /////////////
            ///////////
            //////////

            //We create the start and expiry date for the key
            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.add(Calendar.YEAR, 1);

            //We are creating a RSA key pair and store it in the Android Keystore
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA);

            //We are creating the key pair with sign and verify purposes
       //     KeyGenParameterSpec parameterSpec = new KeyGenParameterSpec.Builder(KEY_ALIAS,
                   // KeyProperties.PURPOSE_DECRYPT )//KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
             //   setCertificateSerialNumber(BigInteger.valueOf(777))       //Serial number used for the self-signed certificate of the generated key pair, default is 1
              //  .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)

               // .setCertificateSubject(new X500Principal("CN=$KEY_ALIAS"))     //Subject used for the self-signed certificate of the generated key pair, default is CN=fake
                //.setDigests(KeyProperties.DIGEST_SHA256)                         //Set of digests algorithms with which the key can be used
                //.setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1) //Set of padding schemes with which the key can be used when signing/verifying
                //.setCertificateNotBefore(startDate.getTime())                         //Start of the validity period for the self-signed certificate of the generated, default Jan 1 1970
                //.setCertificateNotAfter(endDate.getTime())                            //End of the validity period for the self-signed certificate of the generated key, default Jan 1 2048
                //.setUserAuthenticationRequired(false)                             //Sets whether this key is authorized to be used only if the user has been authenticated, default false
                //.setUserAuthenticationValidityDurationSeconds(30)                //Duration(seconds) for which this key is authorized to be used after the user is successfully authenticated
              //  .build();


                    keyPairGenerator.initialize(
                            new KeyGenParameterSpec.Builder(
                                    KEY_ALIAS,
                                    KeyProperties.PURPOSE_DECRYPT)
                                    .setKeySize(1024)
                                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)//ENCRYPTION_PADDING_RSA_OAEP)
                                    .build());

            //Initialization of key generator with the parameters we have specified above
            //keyPairGenerator.initialize(parameterSpec);
          // keyPairGenerator.initialize(1024);
            //Generates the key pair
            KeyPair keyPair = keyPairGenerator.genKeyPair();

             privateKey = keyPair.getPrivate();
             publicKey = keyPair.getPublic();
            pubKeyStr = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.NO_WRAP);
                    ////////
            ////////
            ////////
           /* KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);*/
           /* mKeystore.setEntry(
                    KEY_ALIAS,
                    new KeyStore.PrivateKeyEntry(privateKey, mKeystore.getCertificateChain(KEY_ALIAS)),
                    new KeyProtection.Builder(KeyProperties.PURPOSE_DECRYPT)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .build());*/






            SharedPreferencesManager.getInstance().setPublicKey(pubKeyStr);
            //SharedPreferencesManager.getInstance(MyApp.getInstance().getApplicationContext()).setPrivateKey(priKeyStr);

            KeyStore keyStore = null;


                KeyPairGenerator signatureKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA);

          /*  signatureKeyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(
                            Crypto.SIGNATURE,
                            KeyProperties.PURPOSE_SIGN)
                            .build())
            ;*/

            KeyGenParameterSpec parameterSpec  = new KeyGenParameterSpec.Builder(SIGNATURE,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)//.run {
                .setCertificateSerialNumber(BigInteger.valueOf(777))       //Serial number used for the self-signed certificate of the generated key pair, default is 1
                .setCertificateSubject(new X500Principal("CN=$KEY_ALIAS"))     //Subject used for the self-signed certificate of the generated key pair, default is CN=fake
                .setDigests(KeyProperties.DIGEST_SHA256)                         //Set of digests algorithms with which the key can be used
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1) //Set of padding schemes with which the key can be used when signing/verifying
                .setCertificateNotBefore(startDate.getTime())                         //Start of the validity period for the self-signed certificate of the generated, default Jan 1 1970
                .setCertificateNotAfter(endDate.getTime())                            //End of the validity period for the self-signed certificate of the generated key, default Jan 1 2048
                //.setUserAuthenticationRequired(true)                             //Sets whether this key is authorized to be used only if the user has been authenticated, default false
                //.setUserAuthenticationValidityDurationSeconds(30)                //Duration(seconds) for which this key is authorized to be used after the user is successfully authenticated
                .build();


            //Initialization of key generator with the parameters we have specified above
            signatureKeyPairGenerator.initialize(parameterSpec);

                KeyPair kp = signatureKeyPairGenerator.generateKeyPair();
                PublicKey pk = kp.getPublic();
                PrivateKey prK =kp.getPrivate();


                /*keyStore.setEntry(
                        SIGNATURE,
                        new KeyStore.PrivateKeyEntry((PrivateKey) prK,mKeystore.getCertificateChain(SIGNATURE)),
                        new KeyProtection.Builder(KeyProperties.PURPOSE_SIGN)
                                .setDigests(KeyProperties.DIGEST_SHA256)
                                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                                // Only permit this key to be used if the user
                                // authenticated within the last ten minutes.
                                //.setUserAuthenticationRequired(true)
                                //.setUserAuthenticationValidityDurationSeconds(10 * 60)
                                .build());*/


        } catch (Exception e){
            Log.e(TAG,"key generation error "+e.toString());
        }
    }

    private Key getPubKey(){
        /*String pubKeyStr = SharedPreferencesManager.getInstance(MyApp.getInstance().getApplicationContext()).getPublicKey();
        byte[] sigBytes = Base64.decode(pubKeyStr, Base64.DEFAULT);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            return  keyFact.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }*/
        try {
            PublicKey key= mKeystore.getCertificate(KEY_ALIAS).getPublicKey();
            return key;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    private  Key getPriKey(){
        return getPrivateKey();
       /* String privKeyStr = SharedPreferencesManager.getInstance(MyApp.getInstance().getApplicationContext()).getPrivateKey();
        byte[] sigBytes = Base64.decode(privKeyStr, Base64.DEFAULT);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            return  keyFact.generatePrivate(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;*/
    }

    public String getEncoded(String str){
       /* if(publicKey==null){
            publicKey = getPubKey();
        }*/
        String enstr = new String();
        byte[] encodedBytes = null;
        try{

            /*Cipher c = Cipher.getInstance("RSA", "BC");
            c.init(Cipher.ENCRYPT_MODE,publicKey);*/
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, mKeystore.getCertificate(KEY_ALIAS).getPublicKey()/*((KeyStore.PrivateKeyEntry) mKeystore.getEntry(KEY_ALIAS, null)).getPrivateKey()*/);
            encodedBytes = cipher.doFinal(str.getBytes());
        } catch (Exception e){
            Log.e(TAG, "RSA Encryption error"+e.toString());
        }
        enstr = Base64.encodeToString(encodedBytes,Base64.DEFAULT);

        return enstr;
    }

    public  String getDecoded(String str){
        if(privateKey==null){
            privateKey = getPriKey();
        }

        String destr = new String();
        byte[] encodedBytes = Base64.decode(str,Base64.DEFAULT);
        byte[] decodedBytes = null;
        try{
            KeyStore.Entry entry=  mKeystore.getEntry(KEY_ALIAS, null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) entry;
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");//("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            //Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey()/*mKeystore.getCertificate(KEY_ALIAS).getPublicKey()*/);
           /* Cipher c = Cipher.getInstance("RSA", "BC");
            c.init(Cipher.DECRYPT_MODE,privateKey);*/
            decodedBytes = cipher.doFinal(encodedBytes);
        }catch(Exception e){
            Log.e(TAG, "RSA dEncryption error"+e.toString());
            Toast.makeText(MyApp.getInstance().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        try{destr = new String(decodedBytes);}catch (Exception e){}
        return destr;
    }

    private Key getPrivateKey() {
        // current users private/public keys

      /*  KeyGenParameterSpec par = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)

                .build();


        try {

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeystore.getEntry(KEY_ALIAS, null);
            return privateKeyEntry.getPrivateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }*/

        try {
            Key keyStorePrivateKey = mKeystore.getKey(KEY_ALIAS, null);

            return keyStorePrivateKey;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;



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
            //verifySign(sign, message);
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
                //We get the certificate from the keystore
        Certificate certificate= null;
        try {
            certificate = mKeystore.getCertificate(SIGNATURE);


        if (certificate != null) {
            //We decode the signature value


            //We check if the signature is valid. We use RSA algorithm along SHA-256 digest algorithm
             Signature sgn = Signature.getInstance("SHA256withRSA");

                //.update("TestString".getBytes())
            sgn.initVerify(certificate);
               sgn.update(decodeMessage.getBytes());



           if(sgn.verify(sign)){
               isValid = decodeMessage;
               Log.d("sign","Verified successfully");
           }else{
               isValid = "Can't decrypt message";
               Log.d("sign", "Verification failed");
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