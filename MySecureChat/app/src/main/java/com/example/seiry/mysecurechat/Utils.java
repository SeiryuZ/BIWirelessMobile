package com.example.seiry.mysecurechat;

import android.util.Base64;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by steven on 4/23/17.
 */

public class Utils {

    private static Utils instance = null;
    private static Cipher cipher;
    private static SecretKeySpec key;

    // So this class cannot be instantiated directly
    protected Utils() {}

    // Singleton pattern
    public static Utils getInstance() {
        if (Utils.instance == null) {
            instance = new Utils();
            try {
                // Add Bouncy Castle as provider
                Security.insertProviderAt(new BouncyCastleProvider(), 1);

                // Instantiate cipher instance and secret key (16 bit- weakest)
                cipher = Cipher.getInstance("AES/ECB/ZeroBytePadding", "BC");
                key = new SecretKeySpec("1234567890abcdef".getBytes("UTF-8"), "AES");
            }
            catch (NoSuchAlgorithmException e) {}
            catch (NoSuchProviderException e) {}
            catch (NoSuchPaddingException e) {}
            catch (UnsupportedEncodingException e) {}
        }
        return instance;
    }

    public String encrypt(String message) {
        // Encrypt message in AES with keys
        // And then encode it in base64 so it doesn't lose bits in encoding compared to normal String
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64.encode(cipher.doFinal(message.getBytes()), Base64.DEFAULT), "UTF-8");
        }
        catch (InvalidKeyException e) {}
        catch (BadPaddingException e) {}
        catch (UnsupportedEncodingException e) {}
        catch (IllegalBlockSizeException e) {}
        return "";
    }

    public String decrypt(String message){
        /*
            Decrypt messages if possible, if not possible return String as is
         */

        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.decode(message.getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8");
        }
        catch (InvalidKeyException e) {}
        catch (BadPaddingException e) {}
        catch (UnsupportedEncodingException e) {}
        catch (IllegalBlockSizeException e) {}
        catch (IllegalArgumentException e) {}

        return message;
    }

}
