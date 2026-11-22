package com.Zrips.CMI.Containers;

import java.lang.reflect.Method;
import net.Zrips.CMILib.Version.Version;

public class CMIVillagerProfession {
  private static Method kmet = null;
  private static Method met = null;

  public static String getName(Object object) {
    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      try {
        if (met == null) {
          kmet = object.getClass().getMethod("getKey");
          Object methodResult = kmet.invoke(object);
          met = methodResult.getClass().getMethod("getKey");
        }

        return (String) met.invoke(kmet.invoke(object));
      } catch (Exception exception) {
      }
    }

    return object.toString();
  }
}
