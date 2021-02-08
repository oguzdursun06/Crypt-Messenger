import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.util.Base64;

public class CbcOfDes
{
    Cipher c;





    public String doEncryption(String s,SecretKey key,byte[] IVector) throws Exception
    {
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IVector));

        //sensitive information
        byte[] text = s.getBytes();

        // Encrypt the text
        byte[] textEncrypted = c.doFinal(text);

        return Base64.getEncoder().encodeToString(textEncrypted);
    }
    public String doDecryption(String ciphertext,SecretKey key,byte[] IVector)throws Exception
    {
        c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE,key,new IvParameterSpec(IVector));
        byte[] textDecrypted = c.doFinal(Base64.getDecoder().decode(ciphertext));
        return(new String(textDecrypted));
    }
}