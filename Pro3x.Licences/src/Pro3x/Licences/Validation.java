package Pro3x.Licences;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Validation
{

    private static List<String> getLicenceParams(boolean includeDateParams)
    {
        List<String> params = new LinkedList<String>();

        params.add("PID");
        params.add("PCODE");
        params.add("REFNO");
        params.add("FIRSTNAME");
        params.add("LASTNAME");
        params.add("COMPANY");
        params.add("EMAIL");
        params.add("COUNTRY");
        params.add("CITY");
        params.add("ZIPCODE");
        
        if(includeDateParams == true)
        {
            params.add("validfrom");
            params.add("validto");            
        }
        
        return params;
    }

    public static LicenceInfo readLicence(String path)
    {
        try
        {
            return new LicenceInfo(path);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private static boolean v1verify(String application, String data, String base64sign) throws Exception
    {
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        String base64hash = new BASE64Encoder().encode(sha.digest(data.getBytes("UTF-8")));

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] keyCode = md5.digest(application.getBytes("UTF-8"));

        Cipher rsa = Cipher.getInstance("AES/ECB/PKCS5Padding");
        rsa.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyCode, "AES"));
        
        byte[] hash64code = rsa.doFinal(new BASE64Decoder().decodeBuffer(base64sign));

        String hashString = new BASE64Encoder().encode(hash64code);
        return base64hash.equals(hashString);
    }
    
    private static boolean v2verify(String application, String data, String base64sign) throws Exception 
    {
        MessageDigest sha = MessageDigest.getInstance("SHA1");
        String base64hash = new BASE64Encoder().encode(sha.digest(data.getBytes("UTF-8")));

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] keyCode = md5.digest(application.getBytes("UTF-8"));
        
        Cipher rsa = Cipher.getInstance("AES/ECB/PKCS5Padding");
        rsa.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyCode, "AES"));
        
        byte[] hash64code = rsa.doFinal(base64hash.getBytes("UTF-8"));

        String hashString = new BASE64Encoder().encode(hash64code);
        return base64sign.equals(hashString);
    }

    public static boolean verifySignature(String path, String application)
    {
        try
        {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
            List<String> params = getLicenceParams(true);

            String data = "//";

            for(String param : params)
            {
                String info = doc.getElementsByTagName(param.toLowerCase()).item(0).getTextContent();
                data += " " + info.trim() + " //";
            }

            String base64sign = doc.getElementsByTagName("signature").item(0).getTextContent();
            
            return v2verify(application, data, base64sign)  || v1verify(application, data, base64sign);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Validation.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
}