/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Pro3x.Tests.Licences;

import Pro3x.Licences.LicenceInfo;
import Pro3x.Licences.Validation;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.Test;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Aco
 */
public class ValidationTesting {

    public ValidationTesting() {
    }

    @Test
    public void KeyReader()
    {
        try
        {
            String key64base = "PABSAFMAQQBLAGUAeQBWAGEAbAB1AGUAPgA8AE0AbwBkAHUAbAB1AHMAPgBzAG8AMgBpADYAagBUAHIARABIADEAVABEAEEASwBXAHQAcwBmAFIAeAA4ADQAVABaAHYAcwBOAG8AawBLAHMAbwBWAGcAMABmADQAcgB3AG4AOQBlAFMASwBWAFQARABQAE8ARQAzAEkAcgBSAGMAQgBWAFEAVwBrAFgAdgBJAGcAegBiADkAWgBqADcASABZAHoAUQA0ACsAdwA2AFgAOABRAGcAdQBTAGQAYgBVAGoAQwA0AFgAKwBFAEUAYwBYAGsAawB1AGcAWABPAEQAUgBkAGIAVwBKAGkAWABtACsAMQB3AG0AQgBmAGgATgBTAHUAaABnAFIAUABhAGMATgBtAGMARwArAFQAbwBJAEkAUAA5AFEAcABlAHgARwBIAHYAUgByAEYAOQAwAEgAYQBtAHkAUwBBADcAUgBnAEEAegBYAEoAeABjAEUASwBYAEQARQA9ADwALwBNAG8AZAB1AGwAdQBzAD4APABFAHgAcABvAG4AZQBuAHQAPgBBAFEAQQBCADwALwBFAHgAcABvAG4AZQBuAHQAPgA8AC8AUgBTAEEASwBlAHkAVgBhAGwAdQBlAD4A";
            byte[] ukey = new BASE64Decoder().decodeBuffer(key64base);
            String key = new String(ukey, "UTF-16LE");

            System.out.println("Public key: " + key);
//            CharBuffer bufferkey = unicode.decode(ByteBuffer.wrap(ukey));
//            bufferkey.rewind();
//            System.out.print(bufferkey.toString());
        }
        catch (IOException ex)
        {
            Logger.getLogger(ValidationTesting.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void VerifyLicenceSignature()
    {
        assert Validation.verifySignature("C:\\Users\\Aco\\Documents\\Company\\Pro3x Community\\Certifikat\\pro3x-licenca.xml", "Pro3x Nautilus Edition") == true;
    }

    @Test
    public void MD5KeyGenerator()
    {
        try
        {
            String password = "Pro3x Nautilus Edition";
            byte[] key = MessageDigest.getInstance("MD5").digest(password.getBytes("UTF-8"));

            System.out.println("MD5 Key: " + new BASE64Encoder().encode(key));
        }
        catch (Exception ex)
        {
            Logger.getLogger(ValidationTesting.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void AesKeyGenerator()
    {
        try
        {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);

            SecretKey key = keygen.generateKey();
            System.out.println("AES Base64 Random Key: " + new BASE64Encoder().encode(key.getEncoded()));
        }
        catch (Exception ex)
        {
            Logger.getLogger(ValidationTesting.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void AesCryptoTest()
    {
        try
        {
            SecretKeySpec key = new SecretKeySpec(new BASE64Decoder().decodeBuffer("P7fwKXGDisFgoDQetVUSsw=="), "AES");
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, key);
            System.out.println("AES Mode: " + aes.getAlgorithm());

            String data = "hello world";
            byte[] code = aes.doFinal(data.getBytes("UTF-8"));

            System.out.println("AES Code: " + new BASE64Encoder().encode(code));

            aes.init(Cipher.DECRYPT_MODE, key);
            assert data.equals(new String(aes.doFinal(code), "UTF-8")) == true;
        }
        catch (Exception ex)
        {
            Logger.getLogger(ValidationTesting.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void AesDecryptoTest()
    {
        try
        {
            String base64code = "PiU4t3rWFUcyq2nMc+lBSA==";
            byte[] code = new BASE64Decoder().decodeBuffer(base64code);

            String application = "Pro3x Nautilus Edition";
            byte[] key = MessageDigest.getInstance("MD5").digest(application.getBytes("UTF-8"));

            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));

            byte[] data = aes.doFinal(code);
            System.out.println("AES Decryption: " + new String(data, "UTF-8"));
        }
        catch (Exception ex)
        {
            Logger.getLogger(ValidationTesting.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}