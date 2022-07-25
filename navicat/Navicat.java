import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/*
 * Works with navicat 12.x only.
 */
public class Navicat {

    private static final String AES_KEY = "libcckeylibcckey";
    private static final String AES_IV  = "libcciv libcciv ";

    private static String bin2hex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static byte[] hex2bin(String str) {
        int len = str.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i/2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i+1), 16));
        }
        return data;
    }

    private static String encrypt(String str) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(AES_IV.getBytes(), 0, cipher.getBlockSize());
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedBytes = cipher.doFinal(str.getBytes());
        return bin2hex(encryptedBytes);
    }

    public static String decrypt(String str) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(AES_IV.getBytes(), 0, cipher.getBlockSize());
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] encryptedBytes = hex2bin(str);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static void main(String[] args) throws Exception {
        String s = Navicat.encrypt("123456");
        System.out.println(s);
        String s2 = Navicat.decrypt(s);
        System.out.println(s2);
    }

}
