package lk.ac.icbt.sunrisedental.util;

import java.security.*; import javax.crypto.spec.PBEKeySpec; import java.util.Base64;

public final class PasswordUtil {
    private static final int ITERATIONS=120_000; private static final int KEY_LENGTH=256;
    private PasswordUtil() { }
    public static String hash(String password) { try { byte[] salt=new byte[16];SecureRandom.getInstanceStrong().nextBytes(salt);return "PBKDF2$"+ITERATIONS+"$"+Base64.getEncoder().encodeToString(salt)+"$"+Base64.getEncoder().encodeToString(derive(password,salt,ITERATIONS));}catch(GeneralSecurityException e){throw new IllegalStateException(e);} }
    public static boolean matches(String password,String stored) { try { String[] p=stored.split("\\$");if(p.length!=4||!"PBKDF2".equals(p[0]))return false;byte[] actual=derive(password,Base64.getDecoder().decode(p[2]),Integer.parseInt(p[1]));return MessageDigest.isEqual(actual,Base64.getDecoder().decode(p[3]));}catch(IllegalArgumentException|GeneralSecurityException e){return false;} }
    private static byte[] derive(String password,byte[] salt,int iterations)throws GeneralSecurityException { return javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(password.toCharArray(),salt,iterations,KEY_LENGTH)).getEncoded(); }
}
