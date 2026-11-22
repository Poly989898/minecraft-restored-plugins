package com.Zrips.CMI;

import com.Zrips.CMI.Components.CMIComponentPaper;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.list.itemnbt;
import com.Zrips.CMI.utils.CMIEncoder;
import com.Zrips.CMI.NBT.NMS;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMIServerProperties;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.NBT.CMINBTListType;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Reflections {
  private Class<?> MinecraftServerClass;
  private Object MinecraftServer;
  private Class<?> PlayerList;
  private Class<?> EntityHuman;
  private Class<?> EntityPlayer;
  private Class<?> EnumGameMode;
  private Class<?> CraftPlayer;
  private Class<?> CEntity;
  private Class<?> nbtTagCompound;
  private Class<?> nbtTagList;
  private Class<?> EnumHand;
  private Class<?> CraftBeehive;
  private Class<?> TileEntityBeehive;
  private Class<?> CraftItemStack;
  private Class<?> IStack;
  private Class<?> dimensionManager;
  private Class<?> PacketPlayOutAnimation;
  private CMI plugin;

  public Reflections(CMI cmi) {
    this.plugin = cmi;
    this.initialize();
  }

  private void initialize() {
    if (Version.isMojangMappings()) {
      try {
        this.MinecraftServerClass = this.nmsType(NMS.NmsType.MINECRAFT_SERVER);
        this.CraftBeehive = getCraftClass("block.impl.CraftBeehive");
        this.TileEntityBeehive = this.nmsType(NMS.NmsType.TILE_ENTITY_BEEHIVE);
        this.PlayerList = this.nmsType(NMS.NmsType.PLAYER_LIST);
        this.EntityHuman = this.nmsType(NMS.NmsType.ENTITY_HUMAN);
        this.PacketPlayOutAnimation = this.nmsType(NMS.NmsType.PACKET_ANIMATION);
        this.dimensionManager = this.nmsType(NMS.NmsType.DIMENSION_MANAGER);
        Object craftServer = getCraftClass("CraftServer").cast(Bukkit.getServer());
        this.MinecraftServer = craftServer.getClass().getMethod("getServer").invoke(craftServer);
        this.CraftPlayer = getCraftClass("entity.CraftPlayer");
        this.CraftItemStack = getCraftClass("inventory.CraftItemStack");
        this.EnumGameMode = this.nmsType(NMS.NmsType.ENUM_GAMEMODE);
        this.EntityPlayer = this.nmsType(NMS.NmsType.ENTITY_PLAYER);
        this.CEntity = this.nmsType(NMS.NmsType.ENTITY);
        this.nbtTagCompound = this.nmsType(NMS.NmsType.NBT_COMPOUND);
        this.nbtTagList = this.nmsType(NMS.NmsType.NBT_LIST);
        this.IStack = this.nmsType(NMS.NmsType.ITEM_STACK);
        this.EnumHand = this.nmsType(NMS.NmsType.ENUM_HAND);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      try {
        this.MinecraftServerClass = this.nmsType(NMS.NmsType.MINECRAFT_SERVER);
        this.CraftBeehive = getBukkitClass("block.impl.CraftBeehive");
        this.TileEntityBeehive = this.nmsType(NMS.NmsType.TILE_ENTITY_BEEHIVE);
        this.PlayerList = this.nmsType(NMS.NmsType.PLAYER_LIST);
        this.EntityHuman = this.nmsType(NMS.NmsType.ENTITY_HUMAN);
        this.PacketPlayOutAnimation = this.nmsType(NMS.NmsType.PACKET_ANIMATION);
        this.dimensionManager = this.nmsType(NMS.NmsType.DIMENSION_MANAGER);
        Object object = getBukkitClass("CraftServer").cast(Bukkit.getServer());
        this.MinecraftServer = object.getClass().getMethod("getServer").invoke(object);
        this.CraftPlayer = getBukkitClass("entity.CraftPlayer");
        this.CraftItemStack = getBukkitClass("inventory.CraftItemStack");
        this.EnumGameMode = this.nmsType(NMS.NmsType.ENUM_GAMEMODE);
        this.EntityPlayer = this.nmsType(NMS.NmsType.ENTITY_PLAYER);
        this.CEntity = this.nmsType(NMS.NmsType.ENTITY);
        this.nbtTagCompound = this.nmsType(NMS.NmsType.NBT_COMPOUND);
        this.nbtTagList = this.nmsType(NMS.NmsType.NBT_LIST);
        this.IStack = this.nmsType(NMS.NmsType.ITEM_STACK);
        this.EnumHand = this.nmsType(NMS.NmsType.ENUM_HAND);
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }
    } else {
      Class targetClass = null;

      try {
        targetClass = getBukkitClass("CraftServer");
      } catch (Throwable throwable3) {
        throwable3.printStackTrace();
      }

      try {
        this.CraftBeehive = getBukkitClass("block.impl.CraftBeehive");
      } catch (Throwable throwable4) {
      }

      try {
        this.TileEntityBeehive = this.getMinecraftClass("TileEntityBeehive");
      } catch (Throwable throwable5) {
      }

      try {
        this.PlayerList = this.getMinecraftClass("PlayerList");
      } catch (Throwable throwable6) {
      }

      try {
        this.EntityHuman = this.getMinecraftClass("EntityHuman");
      } catch (Throwable throwable7) {
      }

      try {
        this.PacketPlayOutAnimation = this.getMinecraftClass("PacketPlayOutAnimation");
      } catch (Throwable throwable8) {
      }

      try {
        this.dimensionManager = this.getMinecraftClass("DimensionManager");
      } catch (Throwable throwable9) {
      }

      Object handle = null;

      try {
        if (targetClass != null) {
          handle = targetClass.cast(Bukkit.getServer());
        }
      } catch (Throwable throwable10) {
        throwable10.printStackTrace();
      }

      try {
        this.MinecraftServerClass = this.getMinecraftClass("MinecraftServer");
      } catch (Throwable throwable11) {
        throwable11.printStackTrace();
      }

      try {
        if (handle != null) {
          this.MinecraftServer = handle.getClass().getMethod("getServer").invoke(handle);
        }
      } catch (Throwable throwable12) {
        throwable12.printStackTrace();
      }

      try {
        this.EnumGameMode = this.getMinecraftClass("EnumGamemode");
      } catch (Throwable throwable13) {
      }

      try {
        this.EntityPlayer = this.getMinecraftClass("EntityPlayer");
      } catch (Throwable throwable14) {
      }

      try {
        this.CraftPlayer = getBukkitClass("entity.CraftPlayer");
      } catch (Throwable throwable15) {
        throwable15.printStackTrace();
      }

      try {
        this.CEntity = this.getMinecraftClass("Entity");
      } catch (Throwable throwable16) {
        throwable16.printStackTrace();
      }

      try {
        this.nbtTagCompound = this.getMinecraftClass("NBTTagCompound");
      } catch (Throwable throwable17) {
        throwable17.printStackTrace();
      }

      try {
        this.nbtTagList = this.getMinecraftClass("NBTTagList");
      } catch (Throwable throwable18) {
        throwable18.printStackTrace();
      }

      try {
        this.CraftItemStack = getBukkitClass("inventory.CraftItemStack");
      } catch (Throwable throwable19) {
        throwable19.printStackTrace();
      }

      try {
        this.IStack = this.getMinecraftClass("ItemStack");
        this.EnumHand = this.getMinecraftClass("EnumHand");
      } catch (Throwable throwable20) {
        throwable20.printStackTrace();
      }
    }
  }

  private Class<?> nmsType(NMS.NmsType type) {
    return CMI.getInstance().getNMS().getNmsType(type);
  }

  @Deprecated
  public void sendPacket(Object object, Object packet) {
    CMILib.getInstance().getReflectionManager().sendPacket(object, packet);
  }

  private static Class<?> getCraftClass(String string) {
    try {
      return Class.forName("org.bukkit.craftbukkit." + string);
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    }
  }

  private static Class<?> getBukkitClass(String string) {
    try {
      return Class.forName(
          "org.bukkit.craftbukkit." + Version.getCurrent().toString() + "." + string);
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    }
  }

  public Class<?> getMinecraftClass(String string) {
    try {
      return Class.forName(
          "net.minecraft.server." + Version.getCurrent().toString() + "." + string);
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    }
  }

  public void exitBed(org.bukkit.entity.Player player) {
    try {
      Object object = this.CraftPlayer.getMethod("getHandle").invoke(player);
      var constructor = this.PacketPlayOutAnimation.getConstructor(this.CEntity, int.class);
      Object packetInstance = constructor.newInstance(object, 2);

      for (org.bukkit.entity.Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        CMILib.getInstance().getReflectionManager().sendPlayerPacket(onlinePlayer, packetInstance);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public void setOnGround(org.bukkit.entity.Player player, boolean flag) {
    try {
      Object object = this.CraftPlayer.getMethod("getHandle").invoke(player);
      object.getClass().getMethod("setOnGround", boolean.class).invoke(object, flag);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private Object getPlayerList() {
    Object object = null;

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
        if (Version.isPaperBranch()) {
          object =
              this.MinecraftServer.getClass()
                  .getMethod("getPlayerList")
                  .invoke(this.MinecraftServer);
        } else {
          object = this.MinecraftServer.getClass().getMethod("am").invoke(this.MinecraftServer);
        }
      } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R3)) {
        if (Version.isPaperBranch()) {
          object =
              this.MinecraftServer.getClass()
                  .getMethod("getPlayerList")
                  .invoke(this.MinecraftServer);
        } else {
          object = this.MinecraftServer.getClass().getMethod("ag").invoke(this.MinecraftServer);
        }
      } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
        object = this.MinecraftServer.getClass().getMethod("ah").invoke(this.MinecraftServer);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
        object = this.MinecraftServer.getClass().getMethod("ae").invoke(this.MinecraftServer);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
        object = this.MinecraftServer.getClass().getMethod("ac").invoke(this.MinecraftServer);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
        object = this.MinecraftServer.getClass().getMethod("bi").invoke(this.MinecraftServer);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
        object = this.MinecraftServer.getClass().getMethod("ab").invoke(this.MinecraftServer);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
        object = this.MinecraftServer.getClass().getMethod("ac").invoke(this.MinecraftServer);
      } else {
        object =
            this.MinecraftServer.getClass().getMethod("getPlayerList").invoke(this.MinecraftServer);
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    return object;
  }

  public void changePlayerLimit(int value) {
    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
        Bukkit.setMaxPlayers(value);
        return;
      }

      Object object = this.getPlayerList();
      String string = "maxPlayers";
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
        string = "h";
      } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
        string = "g";
      } else if ((!Version.isCurrentEqualOrHigher(Version.v1_19_R1)
              || !Version.isCurrentSubEqualOrHigher(2))
          && !Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
        if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
          string = "f";
        }
      } else {
        string = "g";
      }

      Field field = object.getClass().getSuperclass().getDeclaredField(string);
      field.setAccessible(true);
      field.setInt(object, value);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    CMILib.getInstance()
        .getReflectionManager()
        .setServerProperties(CMIServerProperties.max_players, value, true);
  }

  public void changeViewDistance(int value) {
    try {
      Object object = this.getPlayerList();
      String string = "a";
      if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
        string = "a";
      }

      Method method = object.getClass().getSuperclass().getMethod(string, int.class);
      method.invoke(object, value);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public void setGameMode(org.bukkit.entity.Player player, GameMode gameMode) {
    if (player != null && gameMode != null) {
      if (player.isOnline()) {
        player.setGameMode(gameMode);
      } else {
        Object object = CMILib.getInstance().getReflectionManager().getPlayerHandle(player);

        try {
          Method method = object.getClass().getMethod("a", this.EnumGameMode);
          Method getByIdMethod = this.EnumGameMode.getMethod("getById", int.class);
          Object nmsGameMode =
              this.EnumGameMode.cast(getByIdMethod.invoke(this.EnumGameMode, gameMode.getValue()));
          method.invoke(object, nmsGameMode);
        } catch (NoSuchMethodException
            | SecurityException
            | IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException value) {
        }
      }
    }
  }

  public void manageEnchantment(Enchantment enchantment, boolean flag) {
    if (!Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        try {
          Field field = Enchantment.class.getDeclaredField("byKey");
          boolean wasAccessible = field.isAccessible();
          field.setAccessible(true);
          HashMap hashMap = (HashMap) field.get(this);
          if (flag) {
            hashMap.remove(enchantment.getKey());
          } else {
            hashMap.put(enchantment.getKey(), enchantment);
          }

          if (!wasAccessible) {
            field.setAccessible(false);
          }
        } catch (NoSuchFieldException
            | SecurityException
            | IllegalArgumentException
            | IllegalAccessException value) {
          value.printStackTrace();
        }

        try {
          Field byNameField = Enchantment.class.getDeclaredField("byName");
          boolean byNameAccessible = byNameField.isAccessible();
          byNameField.setAccessible(true);
          HashMap byName = (HashMap) byNameField.get(this);
          if (flag) {
            byName.remove(enchantment.getName());
          } else {
            byName.put(enchantment.getName(), enchantment);
          }

          if (!byNameAccessible) {
            byNameField.setAccessible(false);
          }
        } catch (NoSuchFieldException
            | SecurityException
            | IllegalArgumentException
            | IllegalAccessException value2) {
          value2.printStackTrace();
        }
      } else {
        try {
          Field byIdField = Enchantment.class.getDeclaredField("byId");
          boolean byIdAccessible = byIdField.isAccessible();
          byIdField.setAccessible(true);
          HashMap byId = (HashMap) byIdField.get(this);
          Integer integer = null;

          try {
            integer = (Integer) enchantment.getClass().getMethod("getId").invoke(enchantment);
          } catch (InvocationTargetException | NoSuchMethodException value3) {
            value3.printStackTrace();
          }

          Field byNameFieldRestore = Enchantment.class.getDeclaredField("byName");
          boolean byNameRestoreAccessible = byNameFieldRestore.isAccessible();
          byNameFieldRestore.setAccessible(true);
          HashMap byNameRestore = (HashMap) byNameFieldRestore.get(this);
          if (flag) {
            byId.remove(integer);
            byNameRestore.remove(enchantment.getName());
          } else {
            byId.put(integer, enchantment);
            byNameRestore.put(enchantment.getName(), enchantment);
          }

          if (!byNameRestoreAccessible) {
            byNameFieldRestore.setAccessible(false);
          }

          if (!byIdAccessible) {
            byIdField.setAccessible(false);
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
  }

  public void setMotd(String string) {
    if (Version.isCurrentEqualOrHigher(Version.v26_1_0)) {
      if (Version.isPaperBranch()) {
        CMIComponentPaper cmiComponentPaper = new CMIComponentPaper(string);
        Bukkit.motd(cmiComponentPaper.getCombined());
      } else {
        Bukkit.setMotd(string);
      }
    } else {
      try {
        Method method =
            this.MinecraftServer.getClass()
                .getMethod(
                    Version.isCurrentEqualOrHigher(Version.v1_18_R1)
                        ? (Version.isCurrentEqualOrHigher(Version.v1_19_R1) ? "d" : "e")
                        : "setMotd",
                    String.class);
        method.invoke(this.MinecraftServer, string);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  public int getPing(org.bukkit.entity.Player player) {
    if (player == null) {
      return 999;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      return player.getPing();
    }

    try {
      Object object = CMILib.getInstance().getReflectionManager().getPlayerHandle(player);
      return Version.isCurrentEqualOrHigher(Version.v1_17_R1)
          ? (Integer) object.getClass().getField("e").get(object)
          : (Integer) object.getClass().getField("ping").get(object);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return 999;
    }
  }

  public int getHoneyLevel(Block block) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      if (!(block.getState() instanceof Beehive)) {
        return 0;
      }

      Beehive beehive = (Beehive) block.getState();
      org.bukkit.block.data.type.Beehive beehiveData =
          (org.bukkit.block.data.type.Beehive) beehive.getBlockData();
      return beehiveData.getHoneyLevel();
    } else {
      if (CMIMaterial.get(block) != CMIMaterial.BEE_NEST
          && CMIMaterial.get(block) != CMIMaterial.BEEHIVE) {
        return -1;
      }

      try {
        Object object = this.CraftBeehive.cast(block.getBlockData());
        Method method = object.getClass().getMethod("getHoneyLevel");
        return (Integer) method.invoke(object);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        return -1;
      }
    }
  }

  public int getBeesInside(Block block) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      if (!(block.getState() instanceof Beehive)) {
        return 0;
      }

      Beehive beehive = (Beehive) block.getState();
      return beehive.getEntityCount();
    } else {
      if (CMIMaterial.get(block) != CMIMaterial.BEE_NEST
          && CMIMaterial.get(block) != CMIMaterial.BEEHIVE) {
        return 0;
      }

      try {
        Object object =
            CMILib.getInstance().getReflectionManager().getTileEntityAt(block.getLocation());
        Object castObject = this.TileEntityBeehive.cast(object);
        String string = "j";
        if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
          if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
            string = "g";
          } else {
            string = "getBeeCount";
          }
        }

        Method method = castObject.getClass().getMethod(string);
        return (Integer) method.invoke(castObject);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        return 0;
      }
    }
  }

  private static boolean processOpeningOfTable(org.bukkit.entity.Player player, String string) {
    if (Version.isPaperBranch() && !Version.isCurrentLower(Version.v1_14_R1)) {
      try {
        Method method = player.getClass().getMethod(string, Location.class, boolean.class);
        method.invoke(player, null, true);
        return true;
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        return false;
      }
    } else {
      LC.info_versionNotSupported.sendMessage(player, new Object[0]);
      return false;
    }
  }

  public boolean openCartographyTable(org.bukkit.entity.Player player) {
    return processOpeningOfTable(player, "openCartographyTable");
  }

  public boolean openGrindstone(org.bukkit.entity.Player player) {
    return processOpeningOfTable(player, "openGrindstone");
  }

  public boolean openLoom(org.bukkit.entity.Player player) {
    return processOpeningOfTable(player, "openLoom");
  }

  public boolean openSmithingTable(org.bukkit.entity.Player player) {
    return processOpeningOfTable(player, "openSmithingTable");
  }

  public boolean openStonecutter(org.bukkit.entity.Player player) {
    return processOpeningOfTable(player, "openStonecutter");
  }

  public boolean openAnvil(org.bukkit.entity.Player player) {
    return processOpeningOfTable(player, "openAnvil");
  }

  public boolean openSignUI(org.bukkit.entity.Player player, Sign sign) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      return false;
    }

    String[] strings = new String[4];
    String[] signLines = new String[4];

    for (int value = 0; value < 4; value++) {
      strings[value] = sign.getLine(value);
      sign.setLine(value, CMIChatColor.deColorize(sign.getLine(value)));
      signLines[value] = sign.getLine(value);
    }

    sign.update();
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R2)) {
      player.sendSignChange(sign.getLocation(), signLines);
    }

    try {
      Object object =
          CMILib.getInstance().getReflectionManager().getTileEntityAt(sign.getLocation());
      if (object == null) {
        return false;
      }

      Object playerHandle = CMILib.getInstance().getReflectionManager().getPlayerHandle(player);
      String string = "isEditable";
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
        string = "isEditable";
      } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
        string = "h";
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        string = "f";
      }

      Field field = object.getClass().getDeclaredField(string);
      field.setAccessible(true);
      field.set(object, true);
      String handleFieldName = "h";
      String sendMethodName = "a";
      switch (Version.getCurrent()) {
        case v1_7_R1:
        case v1_7_R2:
        case v1_7_R3:
        case v1_7_R4:
        case v1_8_R1:
        case v1_8_R2:
        case v1_8_R3:
        case v1_9_R1:
        case v1_9_R2:
        case v1_10_R1:
        case v1_11_R1:
        case v1_12_R1:
          break;
        case v1_13_R1:
        case v1_13_R2:
          handleFieldName = "g";
          break;
        case v1_13_R3:
        case v1_14_R2:
        case v1_15_R2:
        default:
          handleFieldName = "c";
          sendMethodName = "a";
          break;
        case v1_14_R1:
          handleFieldName = "j";
          sendMethodName = "a";
          break;
        case v1_15_R1:
        case v1_16_R1:
        case v1_16_R2:
          handleFieldName = "c";
          sendMethodName = "a";
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
        Field playerField = object.getClass().getDeclaredField("i");
        playerField.setAccessible(true);
        playerField.set(object, player.getUniqueId());
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        Field playerGameField = object.getClass().getDeclaredField("g");
        playerGameField.setAccessible(true);
        playerGameField.set(object, player.getUniqueId());
      } else {
        Field namedField = object.getClass().getDeclaredField(handleFieldName);
        namedField.setAccessible(true);
        namedField.set(object, this.EntityHuman.cast(playerHandle));

        try {
          Method method = object.getClass().getMethod(sendMethodName, this.EntityHuman);
          method.invoke(object, this.EntityHuman.cast(playerHandle));
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }

      int signX = sign.getX();
      int signY = sign.getY();
      int signZ = sign.getZ();
      String positionMethodName = "d";
      switch (Version.getCurrent()) {
        case v1_7_R1:
        case v1_7_R2:
        case v1_7_R3:
        case v1_7_R4:
        case v1_8_R1:
        case v1_8_R2:
        case v1_8_R3:
          return false;
        case v1_9_R1:
          positionMethodName = "c";
          break;
        case v1_9_R2:
        case v1_10_R1:
        case v1_11_R1:
        case v1_12_R1:
          positionMethodName = "d";
      }

      if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
        var blockPositionClass = this.getMinecraftClass("BlockPosition$PooledBlockPosition");
        Method blockPositionFactory =
            blockPositionClass.getMethod(
                positionMethodName, double.class, double.class, double.class);
        Object blockPositionObject = blockPositionFactory.invoke(null, signX, signY, signZ);
        Object pooledPosition =
            this.getMinecraftClass("PacketPlayOutOpenSignEditor")
                .getConstructor(this.getMinecraftClass("BlockPosition"))
                .newInstance(blockPositionObject);
        CMILib.getInstance().getReflectionManager().sendPlayerPacket(player, pooledPosition);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        CMI.getInstance().getNMS().sendOpenSignEditorPacket(player, sign.getLocation());
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              try {
                var constructor =
                    this.getMinecraftClass("BlockPosition")
                        .getConstructor(int.class, int.class, int.class);
                Object packetObject = constructor.newInstance(signX, signY, signZ);
                Object packetObjectAlt =
                    this.getMinecraftClass("PacketPlayOutOpenSignEditor")
                        .getConstructor(this.getMinecraftClass("BlockPosition"))
                        .newInstance(packetObject);
                CMILib.getInstance()
                    .getReflectionManager()
                    .sendPlayerPacket(player, packetObjectAlt);
              } catch (Throwable throwable2) {
                throwable2.printStackTrace();
              }
            },
            5L);
      } else {
        var constructorAlt =
            this.getMinecraftClass("BlockPosition").getConstructor(int.class, int.class, int.class);
        Object packetObjectFromCtor = constructorAlt.newInstance(signX, signY, signZ);
        Object packetObjectWrapped =
            this.getMinecraftClass("PacketPlayOutOpenSignEditor")
                .getConstructor(this.getMinecraftClass("BlockPosition"))
                .newInstance(packetObjectFromCtor);
        CMILib.getInstance().getReflectionManager().sendPlayerPacket(player, packetObjectWrapped);
      }

      return true;
    } catch (Throwable throwable3) {
      throwable3.printStackTrace();

      for (int lineIndex = 0; lineIndex < 4; lineIndex++) {
        sign.setLine(lineIndex, strings[lineIndex]);
      }

      sign.update();
      return false;
    }
  }

  public void respawn(org.bukkit.entity.Player player) {
    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        try {
          CMIScheduler.runAtEntity(this.plugin, player, () -> player.spigot().respawn());
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }

        return;
      }

      Object object = this.MinecraftServerClass.getDeclaredMethod("getServer").invoke(null);
      Object playerList = object.getClass().getMethod("getPlayerList").invoke(object);
      if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              try {
                this.PlayerList.getMethod(
                        "moveToWorld",
                        CMILib.getInstance()
                            .getReflectionManager()
                            .getPlayerHandle(player)
                            .getClass(),
                        boolean.class)
                    .invoke(
                        playerList,
                        CMILib.getInstance().getReflectionManager().getPlayerHandle(player),
                        false);
              } catch (Throwable throwable2) {
                throwable2.printStackTrace();
              }
            },
            1L);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        Object playerConnection =
            CMILib.getInstance().getReflectionManager().getDimensionManager(player.getWorld());
        playerList
            .getClass()
            .getMethod(
                "moveToWorld",
                CMILib.getInstance().getReflectionManager().getPlayerHandle(player).getClass(),
                this.dimensionManager,
                boolean.class)
            .invoke(
                playerList,
                CMILib.getInstance().getReflectionManager().getPlayerHandle(player),
                playerConnection,
                false);
      } else {
        playerList
            .getClass()
            .getMethod(
                "moveToWorld",
                CMILib.getInstance().getReflectionManager().getPlayerHandle(player).getClass(),
                int.class,
                boolean.class)
            .invoke(
                playerList,
                CMILib.getInstance().getReflectionManager().getPlayerHandle(player),
                0,
                false);
      }
    } catch (Throwable throwable3) {
      throwable3.printStackTrace();
    }
  }

  public void openBook(org.bukkit.inventory.ItemStack itemStack, org.bukkit.entity.Player player) {
    if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      player.openBook(itemStack);
    } else {
      org.bukkit.inventory.ItemStack handItem = CMIItemStack.getItemInMainHand(player);

      try {
        CMIItemStack.setItemInMainHand(player, itemStack.clone());
        Object object = this.CraftPlayer.getMethod("getHandle").invoke(player);
        Method method = null;
        Object[] objects = null;
        String string = "a";
        switch (Version.getCurrent()) {
          case v1_7_R4:
            break;
          case v1_8_R1:
          case v1_8_R2:
          case v1_8_R3:
            string = "openBook";
            method = this.EntityPlayer.getMethod(string, this.IStack);
            break;
          case v1_9_R1:
          case v1_9_R2:
          case v1_10_R1:
          case v1_11_R1:
          case v1_12_R1:
            string = "a";
            objects = this.EnumHand.getEnumConstants();
            method = this.EntityPlayer.getMethod(string, this.IStack, this.EnumHand);
            break;
          case v1_13_R1:
          case v1_13_R2:
          case v1_13_R3:
          case v1_14_R1:
          case v1_14_R2:
          case v1_15_R1:
          case v1_15_R2:
          case v1_16_R1:
          case v1_16_R2:
          case v1_16_R3:
          case v1_17_R1:
          default:
            string = "openBook";
            objects = this.EnumHand.getEnumConstants();
            method = this.EntityPlayer.getMethod(string, this.IStack, this.EnumHand);
        }

        if (method != null) {
          Method craftMethod =
              this.CraftItemStack.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
          Object nmsItem = craftMethod.invoke(this.CraftItemStack, itemStack);
          switch (Version.getCurrent()) {
            case v1_8_R1:
            case v1_8_R2:
            case v1_8_R3:
              method.invoke(object, nmsItem);
              break;
            default:
              if (objects != null) {
                method.invoke(object, nmsItem, objects[0]);
              }
          }
        }
      } catch (Throwable throwable) {
      }

      CMIItemStack.setItemInMainHand(player, handItem);
    }
  }

  public void printItemNBT(org.bukkit.inventory.ItemStack itemStack, CommandSender commandSender) {
    if (!Version.isCurrentEqualOrLower(Version.v1_7_R4)) {
      CMINBT cminbt = new CMINBT(itemStack);
      this.printNBT(cminbt.getNbt(), 0, commandSender, null);
    }
  }

  public void printEntityNBT(org.bukkit.entity.Entity entity, CommandSender commandSender) {
    Object object = null;

    try {
      object = new CMINBT(entity).getNbt();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    this.printNBT(object, 0, commandSender, null);
  }

  public void printNBT(Object object, int value, CommandSender commandSender, List<String> list) {
    if (list == null) {
      list = new ArrayList();
    }

    if (this.nbtTagCompound.isInstance(object)) {
      CMINBT cminbt = new CMINBT(this.nbtTagCompound.cast(object));
      StringBuilder stringBuilder = new StringBuilder();

      for (int index = 0; index < value; index++) {
        stringBuilder.append("  ");
      }

      try {
        CMINBT nbtWrapper = new CMINBT(object);

        for (String string : nbtWrapper.getKeys()) {
          Object compound = nbtWrapper.getCompound(string);

          try {
            ArrayList<String> arrayList = new ArrayList(list);
            arrayList.add(string);
            ArrayList<String> entries = new ArrayList();
            CMINBT childNbt = new CMINBT(nbtWrapper.get(string));
            byte b = childNbt.getTypeId();
            switch (b) {
              case 1:
                entries.add(String.valueOf(cminbt.getByte(string)));
                break;
              case 2:
                entries.add(String.valueOf(cminbt.getShort(string)));
                break;
              case 3:
                entries.add(String.valueOf(cminbt.getInt(string)));
                break;
              case 4:
                entries.add(String.valueOf(cminbt.getLong(string)));
                break;
              case 5:
                entries.add(String.valueOf(cminbt.getFloat(string)));
                break;
              case 6:
                entries.add(String.valueOf(cminbt.getDouble(string)));
                break;
              case 7:
                StringBuilder builder = new StringBuilder();
                byte[] bs;
                int byteCount = (bs = cminbt.getByteArray(string)).length;

                for (int byteIndex = 0; byteIndex < byteCount; byteIndex++) {
                  byte currentByte = bs[byteIndex];
                  if (!builder.toString().isEmpty()) {
                    builder.append(",");
                  }

                  builder.append(currentByte);
                }

                builder.append("]");
                entries.add("[" + builder.toString());
                break;
              case 8:
                entries.add(CMIChatColor.deColorize(cminbt.getString(string)));
                break;
              case 9:
                var listValues = cminbt.getList(string, 8);
                if (listValues != null) {
                  for (int listIndex = 0; listIndex < listValues.size(); listIndex++) {
                    String listEntry = (String) listValues.get(listIndex);
                    if (!listEntry.isEmpty() && CMIEncoder.isEncoded(listEntry)) {
                      listEntry = "[Encrypted] " + CMIEncoder.decode(listEntry);
                    }

                    if (!listEntry.isEmpty()) {
                      entries.add(listEntry);
                    }
                  }
                }
              case 10:
              default:
                break;
              case 11:
                entries.add(Arrays.toString(cminbt.getIntArray(string)));
                break;
              case 12:
                try {
                  StringBuilder longBuilder = new StringBuilder();
                  long[] longValues;
                  int longCount = (longValues = cminbt.getLongArray(string)).length;

                  for (int longIndex = 0; longIndex < longCount; longIndex++) {
                    long longValue = longValues[longIndex];
                    if (!longBuilder.toString().isEmpty()) {
                      longBuilder.append(",");
                    }

                    longBuilder.append(longValue);
                  }

                  longBuilder.append("]");
                  entries.add("[" + longBuilder.toString());
                } catch (Throwable throwable) {
                }
            }

            String typeName = CMINBTListType.getType(b);
            if (!entries.isEmpty() && entries.size() > 1) {
              RawMessage rawMessage = new RawMessage();
              rawMessage.addText(
                  stringBuilder.toString()
                      + CMILC.getIM(
                          itemnbt.class,
                          "jump",
                          "[key]",
                          string,
                          "[type]",
                          CMILC.getIM(itemnbt.class, "type", "[type]", typeName)));
              rawMessage.addSuggestion(CMIList.listToString(arrayList, "."));
              rawMessage.addHover(LC.info_Click.getText());
              if (!(commandSender instanceof org.bukkit.entity.Player)
                  || !string.equalsIgnoreCase("recipes")
                      && !string.equalsIgnoreCase("tobedisplayed")) {
                for (String entryText : entries) {
                  if (!rawMessage.getShortRaw().isEmpty()) {
                    rawMessage.addText("\n");
                  }

                  if (entryText.length() > 50
                      && commandSender instanceof org.bukkit.entity.Player) {
                    rawMessage.addText(
                        stringBuilder.toString()
                            + "  "
                            + CMILC.getIM(
                                itemnbt.class,
                                "list",
                                "[value]",
                                entryText.substring(0, 50) + ".."));
                  } else {
                    rawMessage.addText(
                        stringBuilder.toString()
                            + "  "
                            + CMILC.getIM(itemnbt.class, "list", "[value]", entryText));
                  }

                  if (entryText.length() > 5000
                      && commandSender instanceof org.bukkit.entity.Player) {
                    rawMessage.addHover(entryText.substring(0, 5000) + "..");
                  } else {
                    rawMessage.addHover(entryText);
                  }

                  rawMessage.addInsertion(CMIList.listToString(arrayList, "."));
                  rawMessage.addSuggestion(entryText);
                }
              } else {
                StringBuilder listBuilder = new StringBuilder();

                for (String listEntryText : entries) {
                  if (!listBuilder.toString().isEmpty()) {
                    listBuilder.append("\n");
                  }

                  if (listEntryText.length() > 50) {
                    listBuilder.append(
                        stringBuilder.toString()
                            + "  "
                            + CMILC.getIM(
                                itemnbt.class,
                                "list",
                                "[value]",
                                listEntryText.substring(0, 50) + ".."));
                  } else {
                    listBuilder.append(
                        stringBuilder.toString()
                            + "  "
                            + CMILC.getIM(itemnbt.class, "list", "[value]", listEntryText));
                  }

                  if (listEntryText.length() > 5000) {
                    listBuilder.append(listEntryText.substring(0, 5000) + "..");
                  } else {
                    listBuilder.append(listEntryText);
                  }

                  rawMessage.addHover(listBuilder.toString());
                }
              }

              rawMessage.show(commandSender);
            } else if (b != 10) {
              RawMessage nbtMessage = new RawMessage();
              nbtMessage.addText(
                  stringBuilder.toString()
                      + CMILC.getIM(
                          itemnbt.class,
                          "jump",
                          "[key]",
                          string,
                          "[type]",
                          CMILC.getIM(itemnbt.class, "type", "[type]", typeName)));
              nbtMessage.addSuggestion(CMIList.listToString(arrayList, "."));
              nbtMessage.addHover(LC.info_Click.getText());
              if (!entries.isEmpty() && entries.size() == 1) {
                nbtMessage.addText(
                    CMILC.getIM(
                        itemnbt.class,
                        "value",
                        "[value]",
                        "&r" + CMIChatColor.deColorize((String) entries.get(0))));
                nbtMessage.addSuggestion((String) entries.get(0));
              } else {
                String nbtString = childNbt.getNbt().toString();
                if (nbtString.length() > 100 && commandSender instanceof org.bukkit.entity.Player) {
                  nbtString = nbtString.substring(0, 100) + "..";
                }

                nbtMessage.addText(
                    CMILC.getIM(
                        itemnbt.class,
                        "value",
                        "[value]",
                        "&r" + CMIChatColor.deColorize(nbtString)));
                nbtMessage.addSuggestion(childNbt.getNbt().toString());
              }

              nbtMessage.addHover(LC.info_Click.getText());
              nbtMessage.show(commandSender);
            } else {
              RawMessage nbtMessageAlt = new RawMessage();
              nbtMessageAlt.addText(
                  stringBuilder.toString()
                      + CMILC.getIM(
                          itemnbt.class,
                          "jump",
                          "[key]",
                          string,
                          "[type]",
                          CMILC.getIM(itemnbt.class, "type", "[type]", typeName)));
              nbtMessageAlt.addSuggestion(CMIList.listToString(arrayList, "."));
              nbtMessageAlt.addHover(LC.info_Click.getText());
              nbtMessageAlt.show(commandSender);
            }

            this.printNBT(compound, value + 1, commandSender, arrayList);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      } catch (Exception exception2) {
        exception2.printStackTrace();
      }
    }
  }

  public org.bukkit.inventory.ItemStack tryToMakeShulkerBox(
      org.bukkit.inventory.ItemStack itemStack) {
    if (itemStack == null) {
      return null;
    }

    if (Version.isCurrentEqualOrLower(Version.v1_10_R1)) {
      return itemStack;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (itemMeta == null) {
        return itemStack;
      }

      if (!(itemMeta instanceof BlockStateMeta)) {
        return itemStack;
      }

      if (!(((BlockStateMeta) itemMeta).getBlockState() instanceof ShulkerBox)) {
        return itemStack;
      }

      new CMINBT(itemStack).setInt("CMIBackPack", new Random().nextInt(Integer.MAX_VALUE));
      return itemStack;
    } else {
      CMINBT cminbt = new CMINBT(itemStack);

      try {
        CMINBT blockEntityNbt = new CMINBT(cminbt.getCompound("BlockEntityTag"));
        blockEntityNbt.set("Items", this.nbtTagList.newInstance());
        itemStack =
            (org.bukkit.inventory.ItemStack) cminbt.set("BlockEntityTag", blockEntityNbt.getNbt());
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }

      return (org.bukkit.inventory.ItemStack)
          cminbt.setInt("CMIBackPack", new Random().nextInt(Integer.MAX_VALUE));
    }
  }
}

