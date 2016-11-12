package com.rev;

import org.junit.Test;

import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kevin.gann on 11/11/16.
 */
public class CipherStrengthTest {

    @Test
    public void blah() throws NoSuchAlgorithmException {
        boolean unlimited = Cipher.getMaxAllowedKeyLength("RC5") >= 256;
        System.out.println("Unlimited cryptography enabled: " + unlimited);
    }
}
