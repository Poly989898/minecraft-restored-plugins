package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.Zrips.CMILib.FileHandler.ConfigReader;

public class CMIEncoder {
  static Cipher encryptor;
  static Cipher decryptor;
  private static final String fineName = "security.key";

  public static void initialize() {
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(CMI.getInstance(), "security.key");
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      configReader.addComment("KEY", new String[] {"DO NOT EDIT THIS!"});
      String string = configReader.get("KEY", UUID.randomUUID().toString().replace("-", ""));
      String keyString =
          configReader.get("KEY128", UUID.randomUUID().toString().replace("-", "").substring(16));
      configReader.save();
      SecretKeySpec secretKeySpec = new SecretKeySpec(string.getBytes(), "AES");
      SecretKeySpec keySpec = new SecretKeySpec(keyString.getBytes(), "AES");

      try {
        Security.setProperty("crypto.policy", "unlimited");
      } catch (Exception | Error value) {
      }

      try {
        encryptor = Cipher.getInstance("AES");
        decryptor = Cipher.getInstance("AES");
        encryptor.init(1, secretKeySpec);
        decryptor.init(2, secretKeySpec);
        CMI.getInstance().consoleMessage("Initialized Cipher256 " + encryptor.getAlgorithm());
      } catch (Exception | Error value2) {
        try {
          encryptor = Cipher.getInstance("AES");
          decryptor = Cipher.getInstance("AES");
          encryptor.init(1, keySpec);
          decryptor.init(2, keySpec);
          CMI.getInstance().consoleMessage("Initialized Cipher128 " + encryptor.getAlgorithm());
        } catch (Exception | Error value3) {
          CMI.getInstance()
              .consoleMessage("&eFailed to setup encoder by using key in security.key file.");
          CMI.getInstance()
              .consoleMessage("&eEncryption will be disabled. This will not effect plugins work");
          CMI.getInstance()
              .consoleMessage(
                  "&eThis is usually caused by outdated Java version in your server. Get atleast"
                      + " Java 8 u162");
          encryptor = null;
          decryptor = null;
        }
      }
    }
  }

  public static String encode(String string) {
    if (encryptor == null) {
      return string;
    }

    try {
      Encoder encoder = Base64.getEncoder();
      return encoder.encodeToString(encryptor.doFinal(string.getBytes("UTF8")));
    } catch (Exception | Error value) {
      return string;
    }
  }

  public static boolean isEncoded(String string) {
    if (string != null && string.length() % 4 == 0) {
      try {
        Decoder decoder = Base64.getDecoder();
        decoder.decode(string);
        return true;
      } catch (Exception | Error value) {
        return false;
      }
    } else {
      return false;
    }
  }

  public static String decode(String string) {
    if (decryptor == null) {
      return string;
    }

    try {
      Decoder decoder = Base64.getDecoder();
      return new String(decryptor.doFinal(decoder.decode(string)), "UTF8");
    } catch (Exception | Error value) {
      return null;
    }
  }

  public static List<String> encode(List<String> list) {
    if (encryptor == null) {
      return list;
    }

    try {
      Encoder encoder = Base64.getEncoder();

      for (int value = 0; value < list.size(); value++) {
        list.set(
            value,
            encoder.encodeToString(encryptor.doFinal(((String) list.get(value)).getBytes("UTF8"))));
      }

      return list;
    } catch (Exception | Error value2) {
      return list;
    }
  }

  @Deprecated
  public static List<String> decode2(List<String> list) {
    return decodeList(list);
  }

  public static List<String> decodeList(List<String> list) {
    if (decryptor == null) {
      return list;
    }

    list = new ArrayList(list);

    try {
      Decoder decoder = Base64.getDecoder();

      for (int value = 0; value < list.size(); value++) {
        list.set(
            value, new String(decryptor.doFinal(decoder.decode((String) list.get(value))), "UTF8"));
      }

      return list;
    } catch (Exception | Error value2) {
      value2.printStackTrace();
      return null;
    }
  }
}
