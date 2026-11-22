package net.Zrips.CMILib.Items;

import java.util.HashMap;
import org.bukkit.MusicInstrument;

public enum CMIMusicInstrument {
  PONDER,
  SING,
  SEEK,
  FEEL,
  ADMIRE,
  CALL,
  YEARN,
  DREAM;

  static HashMap<String, MusicInstrument> instruments = new HashMap<>();

  public MusicInstrument get() {
    return getByName(this.toString());
  }

  public static CMIMusicInstrument get(String name) {
    for (CMIMusicInstrument one : values()) {
      if (one.toString().equalsIgnoreCase(name)) {
        return one;
      }
    }

    return null;
  }

  public static MusicInstrument getByName(String name) {
    return instruments.get(name.toLowerCase().replace("_GOAT_HORN", ""));
  }

  static {
    try {
      for (MusicInstrument one : MusicInstrument.values()) {
        instruments.put(
            one.toString().replace("_GOAT_HORN", "").replace("_", "").toLowerCase(), one);
        instruments.put(one.toString().replace("_", "").toLowerCase(), one);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}
