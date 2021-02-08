import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class OfbOfDes
{
    public SecretKey myDesKey;
    Cipher c;
    public IvParameterSpec iv;


    public OfbOfDes(SecretKey key,byte[] IVector) throws Exception
    {
        // Generate the Key
        this.iv=new IvParameterSpec(IVector);
        myDesKey=key;
        // Create the cipher
        c = Cipher.getInstance("DES/OFB/PKCS5Padding");

    }


    public String doEncryption(String s) throws Exception
    {
        // Initialize the cipher for encryption
        c.init(Cipher.ENCRYPT_MODE, myDesKey,iv);

        //sensitive information
        byte[] text = s.getBytes();

        // Encrypt the text
        byte[] textEncrypted = c.doFinal(text);
        return Base64.getEncoder().encodeToString(textEncrypted);
    }
    public String doDecryption(String ciphertext,SecretKey key,byte[] IVector)throws Exception
    {

        // Initialize the same cipher for decryption
        c.init(Cipher.DECRYPT_MODE, myDesKey,new IvParameterSpec(IVector));

        // Decrypt the text
        byte[] textDecrypted = c.doFinal(Base64.getDecoder().decode(ciphertext));

        return(new String(textDecrypted));
    }
}