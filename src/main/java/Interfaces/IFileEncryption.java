package Interfaces;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * Interface zur Ver- und Entschlüsselung von Dateien
 *
 * @author baez
 */
public interface IFileEncryption {

    /**
     * statisches salt um das Salt nicht jedes mal in die Datei schreiben zu müssen
     */
    byte[] salt = "[B@5cad8086".getBytes();

    /**
     * Verschlüsselt Datei
     *
     * @param password   Passwort für die Verschlüsselung
     * @param sourcePath Pfad zur Datei welche verschlüsselt werden soll
     * @param targetPath Ziepfad wo verschlüsselte Datei abgelegt werden soll
     * @return boolean ob Datei erfolgreich verschlüsselt werden konnte
     */
    public static boolean encryptFile(String password, String sourcePath, String targetPath) {
        PrintWriter logger = null;
        try {
            File logfile = new File("logfile.txt");
            if (!logfile.exists()) {
                logfile.createNewFile();
            }
            logger = new PrintWriter("logfile.txt", "UTF-8");
        } catch (Exception e) {
            /*
            Sinnlos hier ein Log zu schreiben, da Logger möglicherweise nicht richtig angelegt werden konnte
             */
            e.printStackTrace();
        }
        /*
        Dateien für die Input- und OutputStreams
         */
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        /*
        Input-, Output- und CipherStream Objekte zum lesen, schreiben und entschlüsseln von Dateien
         */
        FileInputStream fis;
        FileOutputStream fos;
        CipherInputStream cis;
        try {

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            /*
            Cipher-Modus festlegen
             */
            Cipher encrypt = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
            encrypt.init(Cipher.ENCRYPT_MODE, secret);

            /*
            Datei lesen -> byte-Array entschlüsseln -> Ergebnis schreiben
             */
            fis = new FileInputStream(sourceFile);
            cis = new CipherInputStream(fis, encrypt);

            /*
            Bei Bedarf Ziel-Datei anlegen
             */
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }

            fos = new FileOutputStream(targetFile);
            byte[] bTemp = new byte[8];
            int iTemp = cis.read(bTemp);
            while (iTemp != -1) {
                fos.write(bTemp, 0, iTemp);
                iTemp = cis.read(bTemp);
            }

            /*
            Schließen aller Streams
             */

            fos.flush();
            fos.close();
            cis.close();
            fis.close();
            if (logger != null) {
                logger.close();
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            if (logger != null) {
                logger.write("Crypto-Algorithmus nicht verfügbar\n" + e.toString());
            }
        } catch (NoSuchProviderException e) {
            if (logger != null) {
                logger.write("Crypto-Provider nicht verfügbar\n" + e.toString());
            }
        } catch (NoSuchPaddingException e) {
            if (logger != null) {
                logger.write("Padding-Variante nicht verfügbar\n" + e.toString());
            }
        } catch (InvalidKeyException e) {
            if (logger != null) {
                logger.write("Crypto-Key ungültig\n" + e.toString());
            }
        } catch (FileNotFoundException e) {
            if (logger != null) {
                logger.write("Die zu verschlüsselnde Datei ist nicht verfügbar\n" + e.toString());
            }
        } catch (IOException e) {
            if (logger != null) {
                logger.write("Eine Input- oder Output-Operation ist fehlgeschlagen\n" + e.toString());
            }
        } catch (InvalidKeySpecException e) {
            if (logger != null) {
                logger.write("Probleme mit der Key-Spezifizierung aufgetreten\n" + e.toString());
            }
        }
        if (logger != null) {
            logger.close();
        }
        return false;
    }

    /**
     * Entschlüsselt Datei
     *
     * @param password   Passwort für die Entschlüsselung
     * @param sourcePath Pfad zur Datei welche entschlüsselt werden soll
     * @param targetPath Ziepfad wo entschlüsselte Datei abgelegt werden soll
     * @return boolean ob Entschlüsselung erfolgreich war
     */
    public static boolean decryptFile(String password, String sourcePath, String targetPath) {

        PrintWriter logger = null;
        try {
            File logfile = new File("logfile.txt");
            if (!logfile.exists()) {
                logfile.createNewFile();
            }
            logger = new PrintWriter("logfile.txt", "UTF-8");
        } catch (Exception e) {
            /*
            Sinnlos hier ein Log zu schreiben, da Logger möglicherweise nicht richtig angelegt werden konnte
             */
            e.printStackTrace();
        }

        /*
        Dateien für die Input- und OutputStreams
         */
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);

        /*
        Input-, Output- und CipherStream Objekte zum lesen, schreiben und entschlüsseln von Dateien
         */
        FileInputStream fis;
        FileOutputStream fos;
        CipherInputStream cis;

    /*
    Key anlegen
     */
        SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(), "AES");
        try {

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            /*
            Cipher-Modus festlegen
             */
            Cipher decrypt = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
            decrypt.init(Cipher.DECRYPT_MODE, secret);
            /*
            Datei lesen -> byte-Array entschlüsseln -> Ergebnis schreiben
             */
            fis = new FileInputStream(sourceFile);
            cis = new CipherInputStream(fis, decrypt);
            /*
            Bei Bedarf Ziel-Datei anlegen
             */
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }
            fos = new FileOutputStream(targetFile);

            byte[] bTemp = new byte[8];
            int iTemp = cis.read(bTemp);

            while (iTemp != -1) {
                fos.write(bTemp, 0, iTemp);
                iTemp = cis.read(bTemp);
            }
            /*
            Schließen aller Streams
             */
            fos.flush();
            fos.close();
            cis.close();
            fis.close();
            if (logger != null) {
                logger.close();
            }
            return true;
        } catch (NoSuchAlgorithmException e) {
            if (logger != null) {
                logger.write("Crypto-Algorithmus nicht verfügbar\n" + e.toString());
            }
        } catch (NoSuchProviderException e) {
            if (logger != null) {
                logger.write("Crypto-Provider nicht verfügbar\n" + e.toString());
            }
        } catch (NoSuchPaddingException e) {
            if (logger != null) {
                logger.write("Padding-Variante nicht verfügbar\n" + e.toString());
            }
        } catch (InvalidKeyException e) {
            if (logger != null) {
                logger.write("Crypto-Key ungültig\n" + e.toString());
            }
        } catch (FileNotFoundException e) {
            if (logger != null) {
                logger.write("Die zu verschlüsselnde Datei ist nicht verfügbar\n" + e.toString());
            }
        } catch (IOException e) {
            if (logger != null) {
                logger.write("Eine Input- oder Output-Operation ist fehlgeschlagen\n" + e.toString());
            }
        } catch (InvalidKeySpecException e) {
            if (logger != null) {
                logger.write("Probleme mit der Key-Spezifizierung aufgetreten\n" + e.toString());
            }
        }
        if (logger != null) {
            logger.close();
        }
        return false;
    }

}
