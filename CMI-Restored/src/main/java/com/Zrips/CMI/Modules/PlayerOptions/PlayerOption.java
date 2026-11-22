package com.Zrips.CMI.Modules.PlayerOptions;

import com.Zrips.CMI.AllListeners.NoTargetListener;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.SpeedTracker.CMISpeedTracker;
import java.util.HashMap;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public enum PlayerOption {
  visibleHolograms("holograms", "{gcp}Visible holograms", CMIMaterial.BLACK_STAINED_GLASS) {
    @Override
    public void afterToggle(CMIUser cmiUser, boolean flag) {
      if (cmiUser.isOnline()) {
        CMI.getInstance().getHologramManager().hideAllHolograms(cmiUser.getUniqueId());
        CMI.getInstance().getHologramManager().updatePlayer(cmiUser.getPlayer());
      }
    }
  },
  shiftSignEdit("shiftedit", "{gcp}Sign shift edit", CMIMaterial.OAK_SIGN),
  totemBossBar("totembar", "{gcp}Totem BossBar", CMIMaterial.TOTEM_OF_UNDYING) {
    @Override
    public void afterToggle(CMIUser cmiUser, boolean flag) {
      if (cmiUser.isOnline()) {
        CMI.getInstance().getTotemManager().toggleBar(cmiUser, cmiUser.getOptionState(this));
      }
    }
  },
  bossBarCompass("compass", "{gcp}Compass BossBar", CMIMaterial.COMPASS),
  tagSound("{gcp}Chat tagging sound", CMIMaterial.PAPER),
  chatSpy(false, "{gcp}Chat spy", CMIMaterial.BUCKET) {
    @Override
    public void afterToggle(CMIUser cmiUser, boolean flag) {
      if (flag) {
        CMI.getInstance().getPlayerManager().addSocialSpy(cmiUser.getUniqueId());
      } else {
        CMI.getInstance().getPlayerManager().removeSocialSpy(cmiUser.getUniqueId());
      }
    }
  },
  cmdSpy(false, "{gcp}Command spy", CMIMaterial.WATER_BUCKET) {
    @Override
    public void afterToggle(CMIUser cmiUser, boolean flag) {
      if (flag) {
        CMI.getInstance().getPlayerManager().addCommandSpy(cmiUser.getUniqueId());
      } else {
        CMI.getInstance().getPlayerManager().removeCommandSpy(cmiUser.getUniqueId());
      }
    }
  },
  signSpy(false, "{gcp}Sign spy", CMIMaterial.LAVA_BUCKET) {
    @Override
    public void afterToggle(CMIUser cmiUser, boolean flag) {
      if (flag) {
        CMI.getInstance().getPlayerManager().addSignSpy(cmiUser.getUniqueId());
      } else {
        CMI.getInstance().getPlayerManager().removeSignSpy(cmiUser.getUniqueId());
      }
    }
  },
  acceptingPM("msg", "{gcp}Accepting private messages", CMIMaterial.MAP),
  acceptingTPA("tp", "{gcp}Accepting teleportations to you", CMIMaterial.CLOCK),
  acceptingMoney("pay", "{gcp}Accepting money payments", CMIMaterial.KNOWLEDGE_BOOK),
  chatbubble("{gcp}Chat bubbles", CMIMaterial.LANTERN, Version.v1_19_R3),
  pmSound("{gcp}Private messages sounds", CMIMaterial.STONE_BUTTON),
  rideMe("{gcp}Players can ride you", CMIMaterial.SADDLE),
  pveDamageNumbers("pvenumbers", "{gcp}Shows PvE damage numbers", CMIMaterial.WOODEN_SWORD),
  pvpDamageNumbers("pvpnumbers", "{gcp}Shows PvP damage numbers", CMIMaterial.DIAMOND_SWORD),
  InformDurability("durability", "{gcp}Inform on item durability loss", CMIMaterial.ANVIL),
  receivePets("{gcp}Do you want to receive pets as gifts", CMIMaterial.LEAD),
  deathMessages("{gcp}Do you want to show death messages", CMIMaterial.DEAD_BUSH),
  notarget(false, "{gcp}Do you want to be ignored by mobs", CMIMaterial.BEDROCK) {
    @Override
    public void afterToggle(CMIUser cmiUser, boolean flag) {
      if (flag) {
        if (cmiUser.isOnline()) {
          NoTargetListener.toggleOn(cmiUser);
        }
      } else {
        NoTargetListener.toggleOff(cmiUser);
      }
    }
  },
  showVehicleSpeed(
      false, "{gcp}Do you want to show speed while inside vehicle", CMIMaterial.MINECART) {
    @Override
    public void afterToggle(CMIUser cmiUser, boolean flag) {
      if (flag) {
        if (cmiUser.isOnline()) {
          CMIScheduler.runAtEntity(
              CMI.getInstance(),
              cmiUser.getPlayer(),
              () -> {
                Entity entity = cmiUser.getPlayer().getVehicle();
                if (entity == null) {
                  CMISpeedTracker.unregister(cmiUser.getUniqueId());
                } else {
                  CMISpeedTracker.register(cmiUser.getUniqueId());
                }
              });
        }
      } else {
        CMISpeedTracker.unregister(cmiUser.getUniqueId());
      }
    }
  };

  private int slot;
  private boolean defaults = true;
  private boolean globalEnabled = true;
  private ItemStack icon;
  private String desc;
  private String shortName = null;
  private Version versionFrom;
  private static HashMap<String, PlayerOption> cache = new HashMap<>();
  public static String defaultString;

  static {
    PlayerOption[] optionValues;
    int optionCount = (optionValues = values()).length;

    for (int index = 0; index < optionCount; index++) {
      PlayerOption option = optionValues[index];
      cache.put(option.toString().toLowerCase(), option);
      cache.put(option.getShortName().toLowerCase(), option);
    }

    defaultString = "";
  }

  PlayerOption(String string, CMIMaterial cmiMaterial, Version version) {
    this(10, true, null, string, cmiMaterial, version);
  }

  PlayerOption(boolean flag, String string, CMIMaterial cmiMaterial) {
    this(10, flag, null, string, cmiMaterial, null);
  }

  PlayerOption(String string, String description, CMIMaterial cmiMaterial) {
    this(10, true, string, description, cmiMaterial, null);
  }

  PlayerOption(String string, CMIMaterial cmiMaterial) {
    this(10, true, null, string, cmiMaterial, null);
  }

  PlayerOption(
      int value,
      boolean flag,
      String string,
      String description,
      CMIMaterial cmiMaterial,
      Version version) {
    this.slot = value;
    this.versionFrom = version;
    this.defaults = flag;
    this.desc = description;
    this.shortName = string == null ? this.toString() : string;
    this.icon = cmiMaterial.newItemStack();
  }

  public int getUISlot() {
    return this.slot;
  }

  public boolean isEnabled() {
    return this.defaults;
  }

  public String getDesc() {
    return this.desc;
  }

  public void setDesc(String string) {
    this.desc = string;
  }

  void setEnabled(Boolean enabledByDefault) {
    this.defaults = enabledByDefault;
  }

  public void afterToggle(CMIUser cmiUser, boolean flag) {}

  public static PlayerOption get(String string) {
    return cache.get(string.toLowerCase());
  }

  private static String to(boolean flag) {
    return flag ? "1" : "0";
  }

  private static boolean from(String string) {
    return string.equals("1");
  }

  public static HashMap<PlayerOption, Boolean> fromString(String string) {
    HashMap<PlayerOption, Boolean> hashMap = new HashMap();
    if (string != null && string.contains(":")) {
      String[] strings = string.split(":");
      int value = 0;
      PlayerOption[] playerOptions;
      int optionCount = (playerOptions = values()).length;

      for (int index = 0; index < optionCount; index++) {
        PlayerOption playerOption = playerOptions[index];
        if (strings.length > value) {
          hashMap.put(playerOption, from(strings[value]));
        }

        value++;
      }

      return hashMap;
    } else {
      return hashMap;
    }
  }

  public static String toString(HashMap<PlayerOption, Boolean> hashMap) {
    StringBuilder stringBuilder = new StringBuilder();
    PlayerOption[] playerOptions;
    int value = (playerOptions = values()).length;

    for (int index = 0; index < value; index++) {
      PlayerOption playerOption = playerOptions[index];
      Boolean stateValue = (Boolean) hashMap.get(playerOption);
      if (stateValue == null) {
        stateValue = playerOption.isEnabled();
      }

      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(":");
      }

      stringBuilder.append(to(stateValue));
    }

    return stringBuilder.toString();
  }

  public static String toDefaultValueString() {
    return PlayerOptionsManager.defaultString;
  }

  public boolean isGlobalEnabled() {
    return this.globalEnabled;
  }

  public void setGlobalEnabled(boolean flag) {
    this.globalEnabled = flag;
  }

  public ItemStack getIcon() {
    return this.icon;
  }

  public void setIcon(ItemStack itemStack) {
    this.icon = itemStack;
  }

  public Version getVersionFrom() {
    return this.versionFrom;
  }

  public String getShortName() {
    return this.shortName;
  }
}
