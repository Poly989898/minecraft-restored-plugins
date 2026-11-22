package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;

public class CMICompass {
  private static boolean compassBossBar = false;
  private static boolean compassRequireCompass = false;
  private static boolean recoveryRequireCompass = false;
  private static boolean recoveryAsRegularCompass = false;
  private static int compassUpdateInterval = 200;
  private static String compassShape;
  private static String compassColor;
  private static String compassHomeIcon;
  private static String compassSpawnIcon;
  private static String compassDeathIcon;
  private static String compassTargetIcon;
  private static List<String> compassColors = new ArrayList<>();
  private static boolean compassShowHome;
  private static boolean compassShowSpawn;
  private static boolean compassShowDeath;
  private static boolean compassShowCompass;

  public static void loadConfig() {
    ConfigReader configReader = CMI.getInstance().getConfigManager().getConfig();
    configReader.addComment(
        "Compass.BossBar",
        new String[] {"Enable EXPERIMENTAL boss bar compass", "Only for 1.9+ servers"});
    compassBossBar = configReader.get("Compass.BossBar", false);
    PlayerOption.bossBarCompass.setGlobalEnabled(compassBossBar);
    if (!CMIModule.bossBarCompass.isEnabled()) {
      PlayerOption.bossBarCompass.setGlobalEnabled(false);
    }

    configReader.addComment(
        "Compass.RequireCompass", new String[] {"Requires to hold compass in had to see it"});
    compassRequireCompass = configReader.get("Compass.RequireCompass", false);
    if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
      configReader.addComment(
          "Compass.RecoveryAsRegular",
          new String[] {
            "When enabled we will use recovery compass as regular one when it comes to compass"
                + " requirements to show bossbar"
          });
      recoveryAsRegularCompass = configReader.get("Compass.RecoveryAsRegular", false);
      configReader.addComment(
          "Compass.RequireRecoveryCompass",
          new String[] {
            "Requires to hold recovery compass in had to see death location in bossbar"
          });
      recoveryRequireCompass = configReader.get("Compass.RequireRecoveryCompass", false);
    }

    configReader.addComment(
        "Compass.UpdateInterval", new String[] {"Compass update interval in milliseconds"});
    compassUpdateInterval =
        CMINumber.clamp(configReader.get("Compass.UpdateInterval", 200), 20, 20000);
    configReader.addComment(
        "Compass.Shape",
        new String[] {"Keep same spacing between each direction. Length can be any you want"});
    compassShape =
        configReader.get(
            "Compass.Shape",
            "------------SW-------------W-------------NW-------------N-------------NE-------------E-------------SE-------------S-");
    compassColor = configReader.get("Compass.Color", "&7");
    compassShowHome = configReader.get("Compass.ShowHome", true);
    compassHomeIcon = configReader.get("Compass.Home", "۩");
    compassShowSpawn = configReader.get("Compass.ShowSpawn", true);
    compassSpawnIcon = configReader.get("Compass.Spawn", "⤋");
    compassShowDeath = configReader.get("Compass.ShowDeath", true);
    compassDeathIcon = configReader.get("Compass.Death", "☠");
    compassShowCompass = configReader.get("Compass.ShowCompassTarget", true);
    compassTargetIcon = configReader.get("Compass.CompassTarget", "ꖴ");
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      getCompassColors().clear();
      configReader.addComment(
          "Compass.Gradient",
          new String[] {
            "Only for 1.16+ servers", "Defines color transition from one direction to another"
          });
      boolean flag = configReader.get("Compass.Gradient.Use", true);
      CMIChatColor cmiChatColor =
          CMIChatColor.getByCustomName(configReader.get("Compass.Gradient.SouthColor", "white"));
      CMIChatColor firstColor =
          CMIChatColor.getByCustomName(configReader.get("Compass.Gradient.WestColor", "orange"));
      CMIChatColor secondColor =
          CMIChatColor.getByCustomName(configReader.get("Compass.Gradient.NorthColor", "vulcan"));
      CMIChatColor thirdColor =
          CMIChatColor.getByCustomName(
              configReader.get("Compass.Gradient.EastColor", "bitter_lemon"));
      if (flag) {
        cmiChatColor = cmiChatColor == null ? CMIChatColor.getByCustomName("White") : cmiChatColor;
        firstColor = firstColor == null ? CMIChatColor.getByCustomName("White") : firstColor;
        secondColor = secondColor == null ? CMIChatColor.getByCustomName("White") : secondColor;
        thirdColor = thirdColor == null ? CMIChatColor.getByCustomName("White") : thirdColor;

        for (int value = 0; value < compassShape.length() / 4; value++) {
          getCompassColors()
              .add(
                  CMIChatColor.mixColors(
                          cmiChatColor, firstColor, value * 100.0 / (compassShape.length() / 4 - 1))
                      .getHex());
        }

        for (int firstSegment = 0; firstSegment < compassShape.length() / 4; firstSegment++) {
          getCompassColors()
              .add(
                  CMIChatColor.mixColors(
                          firstColor,
                          secondColor,
                          firstSegment * 100.0 / (compassShape.length() / 4 - 1))
                      .getHex());
        }

        for (int secondSegment = 0; secondSegment < compassShape.length() / 4; secondSegment++) {
          getCompassColors()
              .add(
                  CMIChatColor.mixColors(
                          secondColor,
                          thirdColor,
                          secondSegment * 100.0 / (compassShape.length() / 4 - 1))
                      .getHex());
        }

        for (int thirdSegment = 0; thirdSegment < compassShape.length() / 4; thirdSegment++) {
          getCompassColors()
              .add(
                  CMIChatColor.mixColors(
                          thirdColor,
                          cmiChatColor,
                          thirdSegment * 100.0 / (compassShape.length() / 4 - 1))
                      .getHex());
        }
      }
    }
  }

  public static boolean isBossBarCompassEnabled() {
    return compassBossBar;
  }

  public static boolean isCompassRequireCompass() {
    return compassRequireCompass;
  }

  public static int getCompassUpdateInterval() {
    return compassUpdateInterval;
  }

  public static String getCompassShape() {
    return compassShape;
  }

  public static String getCompassColor() {
    return compassColor;
  }

  public static String getCompassHomeIcon() {
    return compassHomeIcon;
  }

  public static String getCompassSpawnIcon() {
    return compassSpawnIcon;
  }

  public static String getCompassDeathIcon() {
    return compassDeathIcon;
  }

  public static String getCompassTargetIcon() {
    return compassTargetIcon;
  }

  public static boolean isRecoveryRequireCompass() {
    return recoveryRequireCompass;
  }

  public static boolean isRecoveryAsRegularCompass() {
    return recoveryAsRegularCompass;
  }

  public static boolean isCompassShowHome() {
    return compassShowHome;
  }

  public static boolean isCompassShowSpawn() {
    return compassShowSpawn;
  }

  public static boolean isCompassShowDeath() {
    return compassShowDeath;
  }

  public static boolean isCompassShowCompass() {
    return compassShowCompass;
  }

  public static List<String> getCompassColors() {
    return compassColors;
  }
}
