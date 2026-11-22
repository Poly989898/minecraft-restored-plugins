package net.Zrips.CMILib;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Advancements.CMIAdvancement;
import net.Zrips.CMILib.Attributes.Attribute;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIPlayerConnection;
import net.Zrips.CMILib.Container.CMIPlayerInventory;
import net.Zrips.CMILib.Container.CMIServerProperties;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Skins.SkinManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scoreboard.Scoreboard;

public class Reflections {
  private Class<?> CraftServerClass;
  private Class<?> CraftWorldClass;
  private Class<?> WorldServerClass;
  private Object CraftServer;
  private Class<?> MinecraftServerClass;
  private Class<?> PropertyManagerClass;
  private Object MinecraftServer;
  private Class<?> EntityHuman;
  private Class<?> CraftPlayer;
  private Class<?> CraftEntity;
  private Class<?> CEntity;
  private Class<?> CItemEntity;
  private Class<?> nbtTagCompound;
  private Class<?> EntityLiving;
  private Class<?> BlockPosition;
  private Class<?> CraftBeehive;
  private Class<?> CraftNamespacedKey;
  private Class<?> Item;
  private Class<?> IStack;
  private Class<?> IStackTemplate;
  private Class<?> IChatBaseComponent;
  private Class<?> nmsChatSerializer;
  private Class<?> dimensionManager;
  private Class<?> CraftContainer;
  private Class<?> CraftContainers;
  private Class<?> PacketPlayOutOpenWindow;
  private Class<?> PacketPlayOutEntityTeleport;
  private Class<?> PacketPlayOutSpawnEntityLiving;
  private Class<?> PacketPlayOutEntityMetadata;
  private Class<?> PacketPlayOutSetSlot;
  private Class<?> DataWatcher;
  private Method sendPacket;
  private Class<?> AdvancementDataWorld;
  private Class<?> ChatDeserializer;
  private Class<?> SerializedAdvancement;
  private Class<?> LootDeserializationContext;
  private Class<?> CraftChatMessage;
  private Class<?> world;
  private Class<?> advancementFrameTypeClass = null;
  private Class<?> minecraftKeyClass = null;
  private Class<?> identifierClass = null;
  private Method resourceKeyMethod = null;
  private Method identifierMethod = null;
  private Class<?> advancementDisplayClass = null;
  private Class<?> advancementRequirementsClass = null;
  private Class<?> advancementProgressClass = null;
  private Class<?> packetPlayOutAdvancementsClass = null;
  Class<?> vec3DClass = null;
  Class<?> posMoveRotClass = null;
  Class<?> teleportPacketClass = null;
  private Field getConnectionField = null;
  private Object advancementRegistry;
  private CMILib plugin;
  Method nmsChatSerializerMethod = null;
  Method getProfileMethod;
  Method handleMEthod = null;
  private Method getWorldServerMeth = null;
  private Method getWorldWorldMeth = null;
  private Method getTileEntityAtMeth = null;
  private static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap<>();
  Field containerMenuField = null;
  Field containerIdField = null;
  private Method teleportMethod = null;
  private Constructor<?> teleportPacket = null;
  Constructor<?> vec3DConstructor = null;
  Constructor<?> posMoveRotConstructor = null;
  Method teleportFactoryMethod = null;
  private Constructor<?> keyConstructor = null;
  private Method minecraftKeyStaticA = null;
  private Method minecraftKeyStaticABase = null;
  private Field advancementRegistryBField = null;
  private Constructor<?> toastAdvancementDisplay = null;
  private Constructor<?> toastPacketPlayOutAdvancements = null;
  private Method serverGetMethod = null;
  private final Map<String, Method> lootPredicateManagerMethods = new HashMap<>();

  public Reflections(CMILib plugin) {
    this.plugin = plugin;
    this.initialize();
  }

  private void initialize() {
    if (Version.isCurrentEqualOrHigher(Version.v26_1_0)) {
      try {
        this.IStackTemplate = Class.forName("net.minecraft.world.item.ItemStackTemplate");
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    if (Version.isMojangMappings()) {
      try {
        this.world = Class.forName("net.minecraft.world.level.Level");
        this.sendPacket =
            Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl")
                .getMethod("send", Class.forName("net.minecraft.network.protocol.Packet"));
        this.CraftServerClass = Class.forName("org.bukkit.craftbukkit.CraftServer");
        this.CraftServer = this.CraftServerClass.cast(Bukkit.getServer());
        this.MinecraftServer =
            this.CraftServer.getClass().getMethod("getServer").invoke(this.CraftServer);
        this.CraftWorldClass = Class.forName("org.bukkit.craftbukkit.CraftWorld");
        this.MinecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
        this.CraftBeehive = Class.forName("org.bukkit.craftbukkit.block.impl.CraftBeehive");
        this.CraftNamespacedKey = Class.forName("org.bukkit.craftbukkit.util.CraftNamespacedKey");
        this.PacketPlayOutEntityTeleport =
            Class.forName("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket");
        this.EntityHuman = Class.forName("net.minecraft.world.entity.player.Player");
        this.PacketPlayOutSpawnEntityLiving =
            Class.forName("net.minecraft.network.protocol.game.ClientboundAddEntityPacket");
        this.PacketPlayOutEntityMetadata =
            Class.forName("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket");
        this.PacketPlayOutSetSlot =
            Class.forName("net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket");
        this.PacketPlayOutOpenWindow =
            Class.forName("net.minecraft.network.protocol.game.ClientboundOpenScreenPacket");
        this.CraftContainer = Class.forName("net.minecraft.world.inventory.AbstractContainerMenu");
        this.CraftContainers = Class.forName("net.minecraft.world.inventory.MenuType");
        this.DataWatcher = Class.forName("net.minecraft.network.syncher.SynchedEntityData");
        this.AdvancementDataWorld = Class.forName("net.minecraft.server.ServerAdvancementManager");
        this.ChatDeserializer = Class.forName("net.minecraft.util.GsonHelper");
        this.SerializedAdvancement =
            Class.forName("net.minecraft.advancements.Advancement$Builder");
        this.CraftChatMessage = Class.forName("org.bukkit.craftbukkit.util.CraftChatMessage");
        this.WorldServerClass = Class.forName("net.minecraft.server.level.ServerLevel");
        this.dimensionManager = Class.forName("net.minecraft.world.level.dimension.DimensionType");
        this.BlockPosition = Class.forName("net.minecraft.core.BlockPos");
        this.IChatBaseComponent = Class.forName("net.minecraft.network.chat.Component");
        this.CraftPlayer = Class.forName("org.bukkit.craftbukkit.entity.CraftPlayer");
        this.CraftEntity = Class.forName("org.bukkit.craftbukkit.entity.CraftEntity");
        this.PropertyManagerClass = Class.forName("net.minecraft.server.dedicated.Settings");
        this.CEntity = Class.forName("net.minecraft.world.entity.Entity");
        this.CItemEntity = Class.forName("net.minecraft.world.entity.item.ItemEntity");
        this.nbtTagCompound = Class.forName("net.minecraft.nbt.CompoundTag");
        this.EntityLiving = Class.forName("net.minecraft.world.entity.LivingEntity");
        this.Item = Class.forName("net.minecraft.world.item.Item");
        this.IStack = Class.forName("net.minecraft.world.item.ItemStack");
        this.advancementFrameTypeClass =
            Class.forName("net.minecraft.advancements.AdvancementType");
        this.minecraftKeyClass = Class.forName("net.minecraft.resources.ResourceKey");
        this.identifierClass = Class.forName("net.minecraft.resources.Identifier");
        this.resourceKeyMethod =
            this.minecraftKeyClass.getMethod("createRegistryKey", this.identifierClass);
        this.identifierMethod =
            this.identifierClass.getMethod("tryBuild", String.class, String.class);
        this.advancementDisplayClass = Class.forName("net.minecraft.advancements.DisplayInfo");
        this.advancementRequirementsClass =
            Class.forName("net.minecraft.advancements.AdvancementRequirements");
        this.advancementProgressClass =
            Class.forName("net.minecraft.advancements.AdvancementProgress");
        this.packetPlayOutAdvancementsClass =
            Class.forName(
                "net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket");
        this.vec3DClass = Class.forName("net.minecraft.world.phys.Vec3");
        this.posMoveRotClass = Class.forName("net.minecraft.world.entity.PositionMoveRotation");
        this.teleportPacketClass =
            Class.forName("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket");
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else {
      if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
        try {
          this.world = Class.forName("net.minecraft.world.level.World");
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        try {
          if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
            this.advancementFrameTypeClass =
                Class.forName("net.minecraft.advancements.AdvancementFrameType");
            this.minecraftKeyClass = Class.forName("net.minecraft.resources.MinecraftKey");
            this.advancementDisplayClass =
                Class.forName("net.minecraft.advancements.AdvancementDisplay");
            this.advancementRequirementsClass =
                Class.forName("net.minecraft.advancements.AdvancementRequirements");
            this.advancementProgressClass =
                Class.forName("net.minecraft.advancements.AdvancementProgress");
            this.packetPlayOutAdvancementsClass =
                Class.forName("net.minecraft.network.protocol.game.PacketPlayOutAdvancements");
          }

          if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
            this.vec3DClass = Class.forName("net.minecraft.world.phys.Vec3D");
            this.posMoveRotClass = Class.forName("net.minecraft.world.entity.PositionMoveRotation");
            this.teleportPacketClass =
                Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport");
          }

          if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
            this.sendPacket =
                Class.forName("net.minecraft.server.network.PlayerConnection")
                    .getMethod("b", Class.forName("net.minecraft.network.protocol.Packet"));
          } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
            this.sendPacket =
                Class.forName("net.minecraft.server.network.PlayerConnection")
                    .getMethod("a", Class.forName("net.minecraft.network.protocol.Packet"));
          } else {
            this.sendPacket =
                Class.forName("net.minecraft.server.network.PlayerConnection")
                    .getMethod(
                        "sendPacket", Class.forName("net.minecraft.network.protocol.Packet"));
          }

          this.CraftServerClass = getBukkitClass("CraftServer");
          this.CraftServer = this.CraftServerClass.cast(Bukkit.getServer());
          this.CraftWorldClass = getBukkitClass("CraftWorld");
          this.MinecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
          this.CraftBeehive = getBukkitClass("block.impl.CraftBeehive");
          this.CraftNamespacedKey = getBukkitClass("util.CraftNamespacedKey");
          this.PacketPlayOutEntityTeleport =
              Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport");
          this.EntityHuman = Class.forName("net.minecraft.world.entity.player.EntityHuman");
          if (Version.isCurrentEqualOrLower(Version.v1_18_R2)) {
            this.PacketPlayOutSpawnEntityLiving =
                Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving");
          } else {
            this.PacketPlayOutSpawnEntityLiving =
                Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity");
          }

          this.PacketPlayOutEntityMetadata =
              Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
          this.PacketPlayOutSetSlot =
              Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSetSlot");
          this.PacketPlayOutOpenWindow =
              Class.forName("net.minecraft.network.protocol.game.PacketPlayOutOpenWindow");
          this.CraftContainer = Class.forName("net.minecraft.world.inventory.Container");
          this.CraftContainers = Class.forName("net.minecraft.world.inventory.Containers");
          this.DataWatcher = Class.forName("net.minecraft.network.syncher.DataWatcher");
          this.AdvancementDataWorld = Class.forName("net.minecraft.server.AdvancementDataWorld");
          this.ChatDeserializer = Class.forName("net.minecraft.util.ChatDeserializer");
          this.SerializedAdvancement =
              Class.forName("net.minecraft.advancements.Advancement$SerializedAdvancement");
          if (Version.isCurrentEqualOrLower(Version.v1_20_R2)) {
            this.LootDeserializationContext =
                Class.forName("net.minecraft.advancements.critereon.LootDeserializationContext");
          }

          this.CraftChatMessage = getBukkitClass("util.CraftChatMessage");
          this.WorldServerClass = Class.forName("net.minecraft.server.level.WorldServer");
          this.dimensionManager =
              Class.forName("net.minecraft.world.level.dimension.DimensionManager");
          this.BlockPosition = Class.forName("net.minecraft.core.BlockPosition");
          this.IChatBaseComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
          if (Version.isCurrentLower(Version.v1_21_R5)) {
            this.nmsChatSerializer =
                Class.forName("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
          }

          this.MinecraftServer =
              this.CraftServer.getClass().getMethod("getServer").invoke(this.CraftServer);
          this.CraftPlayer = getBukkitClass("entity.CraftPlayer");
          this.CraftEntity = getBukkitClass("entity.CraftEntity");
          this.PropertyManagerClass =
              Class.forName("net.minecraft.server.dedicated.PropertyManager");
          this.CEntity = Class.forName("net.minecraft.world.entity.Entity");
          this.nbtTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
          this.EntityLiving = Class.forName("net.minecraft.world.entity.EntityLiving");
          this.Item = Class.forName("net.minecraft.world.item.Item");
          this.IStack = Class.forName("net.minecraft.world.item.ItemStack");
          String variable = "ax";
          if (Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
            variable = "aF";
          } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
            variable = "aD";
          } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
            variable = "aE";
          } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
            variable = "aB";
          } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
            variable = "az";
          } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
            variable = "ay";
          } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
            variable = "az";
          } else if (Version.isCurrentLower(Version.v1_18_R1)) {
            variable = "getAdvancementData";
          }

          Object advancementData =
              this.MinecraftServer.getClass().getMethod(variable).invoke(this.MinecraftServer);
          if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
            this.advancementRegistry =
                advancementData.getClass().getField("b").get(advancementData);
          } else {
            this.advancementRegistry =
                advancementData.getClass().getField("c").get(advancementData);
          }
        } catch (Throwable e) {
          e.printStackTrace();
        }
      } else {
        try {
          this.CraftServerClass = getBukkitClass("CraftServer");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.CraftWorldClass = getBukkitClass("CraftWorld");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.CraftBeehive = getBukkitClass("block.impl.CraftBeehive");
        } catch (Throwable var32) {
        }

        try {
          this.CraftNamespacedKey = getBukkitClass("util.CraftNamespacedKey");
        } catch (Throwable var31) {
        }

        try {
          this.CraftContainer = this.getMinecraftClass("Container");
        } catch (Throwable var30) {
        }

        try {
          this.AdvancementDataWorld = this.getMinecraftClass("AdvancementDataWorld");
        } catch (Throwable var29) {
        }

        try {
          this.ChatDeserializer = this.getMinecraftClass("ChatDeserializer");
        } catch (Throwable var28) {
        }

        try {
          this.SerializedAdvancement = this.getMinecraftClass("Advancement$SerializedAdvancement");
        } catch (Throwable var27) {
        }

        try {
          this.LootDeserializationContext = this.getMinecraftClass("LootDeserializationContext");
        } catch (Throwable var26) {
        }

        try {
          this.CraftContainers = this.getMinecraftClass("Containers");
        } catch (Throwable var25) {
        }

        try {
          this.PacketPlayOutEntityTeleport = this.getMinecraftClass("PacketPlayOutEntityTeleport");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.PacketPlayOutSpawnEntityLiving =
              this.getMinecraftClass("PacketPlayOutSpawnEntityLiving");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.DataWatcher = this.getMinecraftClass("DataWatcher");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.PacketPlayOutEntityMetadata = this.getMinecraftClass("PacketPlayOutEntityMetadata");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.PacketPlayOutSetSlot = this.getMinecraftClass("PacketPlayOutSetSlot");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.PacketPlayOutOpenWindow = this.getMinecraftClass("PacketPlayOutOpenWindow");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.WorldServerClass = this.getMinecraftClass("WorldServer");
        } catch (Throwable var18) {
        }

        try {
          this.dimensionManager = this.getMinecraftClass("DimensionManager");
        } catch (Throwable var17) {
        }

        try {
          this.BlockPosition = this.getMinecraftClass("BlockPosition");
        } catch (Throwable var16) {
        }

        try {
          this.CraftServer = this.CraftServerClass.cast(Bukkit.getServer());
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.MinecraftServerClass = this.getMinecraftClass("MinecraftServer");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          if (!Version.isCurrentHigher(Version.v1_8_R2)) {
            this.nmsChatSerializer = this.getMinecraftClass("ChatSerializer");
          } else {
            this.nmsChatSerializer = this.getMinecraftClass("IChatBaseComponent$ChatSerializer");
          }
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.MinecraftServer =
              this.CraftServer.getClass().getMethod("getServer").invoke(this.CraftServer);
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.PropertyManagerClass = this.getMinecraftClass("PropertyManager");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.CraftPlayer = getBukkitClass("entity.CraftPlayer");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.CraftEntity = getBukkitClass("entity.CraftEntity");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.CEntity = this.getMinecraftClass("Entity");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.nbtTagCompound = this.getMinecraftClass("NBTTagCompound");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.EntityLiving = this.getMinecraftClass("EntityLiving");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.Item = this.getMinecraftClass("Item");
        } catch (Throwable e) {
          e.printStackTrace();
        }

        try {
          this.IStack = this.getMinecraftClass("ItemStack");
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
    }
  }

  public String serializeText(String text) {
    try {
      Object serialized = this.textToIChatBaseComponent(text);
      return (String) serialized.getClass().getMethod("e").invoke(serialized);
    } catch (Throwable e) {
      e.printStackTrace();
      return text;
    }
  }

  public Object textToIChatBaseComponent(String text) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
      try {
        if (this.nmsChatSerializerMethod == null) {
          this.nmsChatSerializerMethod = this.CraftChatMessage.getMethod("fromJSON", String.class);
        }

        return this.nmsChatSerializerMethod.invoke(null, text);
      } catch (Throwable e) {
        e.printStackTrace();
        return text;
      }
    } else {
      try {
        if (this.nmsChatSerializerMethod == null) {
          this.nmsChatSerializerMethod = this.nmsChatSerializer.getMethod("a", String.class);
        }

        return this.nmsChatSerializerMethod.invoke(null, text);
      } catch (Throwable e) {
        e.printStackTrace();
        return text;
      }
    }
  }

  public int getCurrentTick() {
    if (Version.isFolia()) {
      return Bukkit.getCurrentTick();
    }

    try {
      return this.MinecraftServer.getClass().getField("currentTick").getInt(this.CraftServer);
    } catch (IllegalArgumentException
        | IllegalAccessException
        | NoSuchFieldException
        | SecurityException e) {
      e.printStackTrace();
      return (int) (System.currentTimeMillis() / 50L);
    }
  }

  public Object getItemInfo(int id, String fieldName) {
    try {
      Field field = this.getMinecraftClass("Block").getDeclaredField(fieldName);
      field.setAccessible(true);
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R2)) {
        Method method =
            this.getMinecraftClass("Block").getDeclaredMethod("getByCombinedId", int.class);
        Object res = method.invoke(this.getMinecraftClass("Block"), id);
        res = res.getClass().getMethod("getBlock").invoke(res);
        return field.get(res);
      } else {
        Method method = this.getMinecraftClass("Block").getDeclaredMethod("getById", int.class);
        Object res = method.invoke(this.getMinecraftClass("Block"), id);
        return field.get(res);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Class<?> getBukkitClass(String nmsClassString) {
    try {
      return Class.forName(
          "org.bukkit.craftbukkit." + Version.getCurrent().toString() + "." + nmsClassString);
    } catch (ClassNotFoundException var2) {
      return null;
    }
  }

  public Class<?> getMinecraftClass(String nmsClassString) {
    try {
      return Class.forName(
          "net.minecraft.server." + Version.getCurrent().toString() + "." + nmsClassString);
    } catch (ClassNotFoundException var3) {
      return null;
    }
  }

  public void setServerProperties(CMIServerProperties setting, Object value, boolean save) {
    if (Version.isMojangMappings()) {
      try {
        Object serverObject =
            this.MinecraftServer.getClass().getField("settings").get(this.MinecraftServer);
        Object prop = serverObject.getClass().getMethod("getProperties").invoke(serverObject);
        Object propertiesObject = prop.getClass().getField("properties").get(prop);
        Method setPropertyMethod =
            propertiesObject
                .getClass()
                .getDeclaredMethod("setProperty", String.class, String.class);
        setPropertyMethod.invoke(propertiesObject, setting.getPath(), String.valueOf(value));
        if (save) {
          serverObject.getClass().getMethod("forceSave").invoke(serverObject);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
      try {
        Object serverObject = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
          serverObject = this.MinecraftServer.getClass().getField("t").get(this.MinecraftServer);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
          serverObject = this.MinecraftServer.getClass().getField("s").get(this.MinecraftServer);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          serverObject = this.MinecraftServer.getClass().getField("r").get(this.MinecraftServer);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
          serverObject = this.MinecraftServer.getClass().getField("s").get(this.MinecraftServer);
        } else {
          serverObject = this.MinecraftServer.getClass().getField("u").get(this.MinecraftServer);
        }

        Object prop = serverObject.getClass().getMethod("a").invoke(serverObject);
        Object propertiesObject = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_21_R7)) {
          propertiesObject = prop.getClass().getField("al").get(prop);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
          propertiesObject = prop.getClass().getField("ak").get(prop);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
          propertiesObject = prop.getClass().getField("ac").get(prop);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          propertiesObject = prop.getClass().getField("ab").get(prop);
        } else {
          propertiesObject = prop.getClass().getField("Z").get(prop);
        }

        Method setPropertyMethod =
            propertiesObject
                .getClass()
                .getDeclaredMethod("setProperty", String.class, String.class);
        setPropertyMethod.invoke(propertiesObject, setting.getPath(), String.valueOf(value));
        if (save) {
          serverObject.getClass().getMethod("b").invoke(serverObject);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
      try {
        Object serverObject =
            this.MinecraftServer.getClass().getField("u").get(this.MinecraftServer);
        Object prop = serverObject.getClass().getMethod("a").invoke(serverObject);
        Object propertiesObject = prop.getClass().getField("Y").get(prop);
        Method setPropertyMethod =
            propertiesObject
                .getClass()
                .getDeclaredMethod("setProperty", String.class, String.class);
        setPropertyMethod.invoke(propertiesObject, setting.getPath(), String.valueOf(value));
        if (save) {
          serverObject.getClass().getMethod("b").invoke(serverObject);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
      try {
        Object serverObject =
            this.MinecraftServer.getClass().getField("u").get(this.MinecraftServer);
        Object prop = serverObject.getClass().getMethod("a").invoke(serverObject);
        Object propertiesObject = prop.getClass().getField("X").get(prop);
        Method setPropertyMethod =
            propertiesObject
                .getClass()
                .getDeclaredMethod("setProperty", String.class, String.class);
        setPropertyMethod.invoke(propertiesObject, setting.getPath(), String.valueOf(value));
        if (save) {
          serverObject.getClass().getMethod("b").invoke(serverObject);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R2)) {
      try {
        Object serverObject =
            this.MinecraftServer.getClass().getField("y").get(this.MinecraftServer);
        Object prop = serverObject.getClass().getMethod("a").invoke(serverObject);
        Object propertiesObject = prop.getClass().getField("Y").get(prop);
        Method setPropertyMethod =
            propertiesObject
                .getClass()
                .getDeclaredMethod("setProperty", String.class, String.class);
        setPropertyMethod.invoke(propertiesObject, setting.getPath(), String.valueOf(value));
        if (save) {
          serverObject.getClass().getMethod("b").invoke(serverObject);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
      try {
        Object serverObject =
            this.MinecraftServer.getClass().getField("z").get(this.MinecraftServer);
        Object prop = serverObject.getClass().getMethod("a").invoke(serverObject);
        Object propertiesObject = prop.getClass().getField("Y").get(prop);
        Method setPropertyMethod =
            propertiesObject
                .getClass()
                .getDeclaredMethod("setProperty", String.class, String.class);
        setPropertyMethod.invoke(propertiesObject, setting.getPath(), String.valueOf(value));
        if (save) {
          serverObject.getClass().getMethod("b").invoke(serverObject);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      try {
        Object serverObject =
            this.MinecraftServer.getClass().getField("x").get(this.MinecraftServer);
        Object prop = serverObject.getClass().getMethod("getProperties").invoke(serverObject);
        Object propertiesObject = prop.getClass().getField("X").get(prop);
        Method setPropertyMethod =
            propertiesObject
                .getClass()
                .getDeclaredMethod("setProperty", String.class, String.class);
        setPropertyMethod.invoke(propertiesObject, setting.getPath(), String.valueOf(value));
        if (save) {
          serverObject.getClass().getMethod("save").invoke(serverObject);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
      try {
        Object serverObject =
            this.MinecraftServer.getClass().getField("propertyManager").get(this.MinecraftServer);
        Object prop = serverObject.getClass().getMethod("getProperties").invoke(serverObject);
        Object propertiesObject = prop.getClass().getField("properties").get(prop);
        Method setPropertyMethod =
            propertiesObject
                .getClass()
                .getDeclaredMethod("setProperty", String.class, String.class);
        setPropertyMethod.invoke(propertiesObject, setting.getPath(), String.valueOf(value));
        if (save) {
          serverObject.getClass().getMethod("save").invoke(serverObject);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else {
      try {
        Method getServerMethod = this.MinecraftServerClass.getDeclaredMethod("getServer");
        Method getPropertyManagerMethod =
            this.MinecraftServerClass.getDeclaredMethod("getPropertyManager");
        Method setPropertyMethod =
            this.PropertyManagerClass.getDeclaredMethod("setProperty", String.class, Object.class);
        Method savePropertiesFileMethod =
            this.PropertyManagerClass.getDeclaredMethod("savePropertiesFile");
        Object manager = getPropertyManagerMethod.invoke(getServerMethod.invoke(null));
        setPropertyMethod.invoke(manager, setting.getPath(), value);
        if (save) {
          savePropertiesFileMethod.invoke(manager);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  @Deprecated
  public boolean isNbtSimilar(ItemStack is, ItemStack other) {
    return CMINBT.isNBTSimilar(is, other);
  }

  public GameProfile getProfile(Player player) {
    Object craftPlayer = this.CraftPlayer.cast(player);

    try {
      if (this.getProfileMethod == null) {
        this.getProfileMethod = this.CraftPlayer.getMethod("getProfile");
      }

      Object profile = this.getProfileMethod.invoke(craftPlayer);
      return (GameProfile) profile;
    } catch (NoSuchMethodException
        | SecurityException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Object getCraftPlayer(Player player) {
    try {
      return this.CraftPlayer.cast(player);
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public Object getPlayerHandle(Player player) {
    Object handle = null;

    try {
      if (this.handleMEthod == null) {
        this.handleMEthod = player.getClass().getMethod("getHandle");
      }

      handle = this.handleMEthod.invoke(player);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return handle;
  }

  public Object getEntityHandle(Entity ent) {
    Object handle = null;

    try {
      handle =
          this.CraftEntity.cast(ent)
              .getClass()
              .getMethod("getHandle")
              .invoke(this.CraftEntity.cast(ent));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return handle;
  }

  public Object getPlayerConnection(Player player) {
    Object connection = null;

    try {
      Object handle = this.getPlayerHandle(player);
      if (this.getConnectionField == null) {
        if (Version.isMojangMappings()) {
          this.getConnectionField = handle.getClass().getField("connection");
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R5)) {
          this.getConnectionField = handle.getClass().getField("g");
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
          this.getConnectionField = handle.getClass().getField("f");
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
          this.getConnectionField = handle.getClass().getField("c");
        } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          this.getConnectionField = handle.getClass().getField("b");
        } else {
          this.getConnectionField = handle.getClass().getField("playerConnection");
        }
      }

      connection = this.getConnectionField.get(handle);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return connection;
  }

  public Object getDimensionManager(World world) {
    Object dm = null;

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
        switch (world.getEnvironment()) {
          case NETHER:
            return this.dimensionManager.getField("NETHER").get(this.dimensionManager);
          case NORMAL:
            return this.dimensionManager.getField("OVERWORLD").get(this.dimensionManager);
          case THE_END:
            return this.dimensionManager.getField("NETHER").get(this.dimensionManager);
        }
      }

      Object worldServer =
          this.getCraftWorld(world)
              .getClass()
              .getMethod("getHandle")
              .invoke(this.getCraftWorld(world));
      dm =
          worldServer
              .getClass()
              .getField(Version.isCurrentEqualOrHigher(Version.v1_18_R1) ? "G" : "dimension")
              .get(worldServer);
    } catch (Throwable var4) {
    }

    return dm;
  }

  public String getServerName() {
    if (Version.isCurrentEqualOrLower(Version.v1_13_R2)) {
      Server srv = Bukkit.getServer();

      try {
        Object res = srv.getClass().getMethod("getServerName").invoke(srv);
        return (String) res;
      } catch (Throwable e) {
        e.printStackTrace();
        return Bukkit.getName();
      }
    } else {
      try {
        Object worldServer = this.getCraftWorld((World) Bukkit.getWorlds().get(0));
        return (String) worldServer.getClass().getMethod("getName").invoke(worldServer);
      } catch (Throwable e) {
        e.printStackTrace();
        return Bukkit.getName();
      }
    }
  }

  public Object getCraftWorld(World world) {
    return this.CraftWorldClass.cast(world);
  }

  public Object getWorldServer(World world) {
    Object obj = this.getCraftWorld(world);

    try {
      if (this.getWorldServerMeth == null) {
        this.getWorldServerMeth = obj.getClass().getMethod("getHandle");
      }

      return this.WorldServerClass.cast(this.getWorldServerMeth.invoke(obj));
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public Object getMineCraftWorld(World world) {
    Object obj = this.getCraftWorld(world);

    try {
      if (this.getWorldServerMeth == null) {
        this.getWorldServerMeth = obj.getClass().getMethod("getHandle");
      }

      Object handle = this.WorldServerClass.cast(this.getWorldServerMeth.invoke(obj));
      if (this.getWorldWorldMeth == null) {
        this.getWorldWorldMeth = handle.getClass().getMethod("getMinecraftWorld");
      }

      return this.world.cast(this.getWorldWorldMeth.invoke(handle));
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  private Object getBlockPosition(Location loc) {
    try {
      Constructor<?> constructor =
          this.BlockPosition.getConstructor(int.class, int.class, int.class);
      return constructor.newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public Object getTileEntityAt(Location loc) {
    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R2)) {
        try {
          Object ncw = this.getWorldServer(loc.getWorld());
          if (this.getTileEntityAtMeth == null) {
            if (Version.isMojangMappings()) {
              this.getTileEntityAtMeth =
                  ncw.getClass().getMethod("getBlockEntity", this.BlockPosition);
            } else {
              this.getTileEntityAtMeth =
                  ncw.getClass()
                      .getMethod(
                          Version.isCurrentEqualOrHigher(Version.v1_18_R1) ? "c_" : "getTileEntity",
                          this.BlockPosition);
            }
          }

          return this.getTileEntityAtMeth.invoke(ncw, this.getBlockPosition(loc));
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }

      Object cw = this.getCraftWorld(loc.getWorld());
      Method meth = cw.getClass().getMethod("getTileEntityAt", int.class, int.class, int.class);
      return meth.invoke(cw, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public void sendPlayerPacket(Player player, Object packet) throws Exception {
    this.sendPacket(this.getPlayerConnection(player), packet);
  }

  public void sendPacket(Object connection, Object packet) {
    if (connection != null && packet != null) {
      try {
        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          this.sendPacket.invoke(connection, packet);
        } else {
          connection
              .getClass()
              .getMethod("sendPacket", this.getClass("{nms}.Packet"))
              .invoke(connection, packet);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  @Deprecated
  public String toJson(ItemStack item) {
    return CMINBT.toJson(item);
  }

  public String getItemMinecraftName(ItemStack item) {
    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_21_R7)) {
        return null;
      }

      Object nmsStack = this.asNMSCopy(item);
      if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        Object pre =
            nmsStack
                .getClass()
                .getMethod(Version.isCurrentEqualOrHigher(Version.v1_18_R1) ? "c" : "getItem")
                .invoke(nmsStack);
        Object n =
            pre.getClass()
                .getMethod(Version.isCurrentEqualOrHigher(Version.v1_18_R1) ? "a" : "getName")
                .invoke(pre);
        Class<?> ll = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
          ll = Class.forName("net.minecraft.locale.LocaleLanguage");
        } else {
          ll = Class.forName("net.minecraft.server." + Version.getCurrent() + ".LocaleLanguage");
        }

        Object lla = ll.getMethod("a").invoke(ll);
        if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
          Method method = lla.getClass().getMethod("a", String.class);
          method.setAccessible(true);
          return (String) method.invoke(lla, (String) n);
        } else {
          return (String) lla.getClass().getMethod("a", String.class).invoke(lla, (String) n);
        }
      } else {
        Field field = this.Item.getField("REGISTRY");
        Object reg = field.get(field);
        Method meth = reg.getClass().getMethod("b", Object.class);
        meth.setAccessible(true);
        Method secmeth = nmsStack.getClass().getMethod("getItem");
        Object intermediateResult = secmeth.invoke(nmsStack);
        Object res = meth.invoke(reg, intermediateResult);
        return res.toString();
      }
    } catch (Throwable e) {
      return null;
    }
  }

  public void updateItemWithPacket(Player player, ItemStack item, int slot) {
    if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
      try {
        Constructor<?> packet =
            this.PacketPlayOutSetSlot.getConstructor(int.class, int.class, int.class, this.IStack);
        Object newPack =
            packet.newInstance(
                this.getActiveContainerId(player),
                this.getActiveContainerStateId(player),
                slot,
                CMINBT.asNMSCopy(item));
        this.sendPlayerPacket(player, newPack);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else {
      try {
        Constructor<?> packet =
            this.PacketPlayOutSetSlot.getConstructor(int.class, int.class, this.IStack);
        Object newPack =
            packet.newInstance(this.getActiveContainerId(player), slot, CMINBT.asNMSCopy(item));
        this.sendPlayerPacket(player, newPack);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  @Deprecated
  public ItemStack setTag(ItemStack item, Object tag) {
    return CMINBT.setTag(item, tag);
  }

  @Deprecated
  public Object getNbt(Entity entity) {
    return CMINBT.getNbt(entity);
  }

  @Deprecated
  public Object getNbt(ItemStack item) {
    return CMINBT.getNbt(item);
  }

  private static String getSuffix(String input) {
    int hash = Math.abs(input.hashCode());
    String hex = Integer.toHexString(hash);
    return "_" + hex.substring(0, Math.min(3, hex.length()));
  }

  private static String sanitizeName(String input) {
    if (input != null && !input.isEmpty()) {
      StringBuilder sb = new StringBuilder();

      for (char c : input.toCharArray()) {
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_') {
          sb.append(c);
        }
      }

      if (sb.length() < 3) {
        sb.append(getSuffix(input));
      }

      while (sb.length() < 3) {
        sb.append('_');
      }

      if (sb.length() > 16) {
        sb.setLength(16);
      }

      return sb.toString();
    } else {
      return "Player" + getSuffix("null");
    }
  }

  private static PlayerProfile getProfile(String name, String url) {
    name = sanitizeName(name);
    PlayerProfile profile =
        Bukkit.createPlayerProfile(
            UUID.nameUUIDFromBytes(url.getBytes()), name == null ? name : name.replace(" ", ""));
    PlayerTextures textures = profile.getTextures();

    try {
      textures.setSkin(new URL(url));
    } catch (MalformedURLException exception) {
      throw new RuntimeException("Invalid URL", exception);
    }

    profile.setTextures(textures);
    return profile;
  }

  public ItemStack setSkullTexture(ItemStack item, String customProfileName, String texture) {
    if (item == null) {
      return null;
    }

    try {
      GameProfile prof = null;
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
        String decodedString =
            new String(Base64.getMimeDecoder().decode(texture)).replace("\n", "").replace(" ", "");
        if (decodedString.contains("url\":\"")) {
          SkullMeta meta = (SkullMeta) item.getItemMeta();
          meta.setOwnerProfile(
              getProfile(
                  customProfileName, decodedString.split("url\":\"", 2)[1].split("\"", 2)[0]));
          item.setItemMeta(meta);
        }

        return item;
      } else {
        prof = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), null);
        PropertyMap properties = SkinManager.getPropertyMap(prof);
        properties.removeAll("textures");
        properties.put("textures", new Property("textures", texture));
        SkullMeta headMeta = (SkullMeta) item.getItemMeta();
        Field profileField = null;

        try {
          profileField = headMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
          e.printStackTrace();
        }

        if (profileField != null) {
          profileField.setAccessible(true);

          try {
            profileField.set(headMeta, prof);
          } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
          }
        }

        item.setItemMeta(headMeta);

        try {
          byte[] decodedBytes = Base64.getMimeDecoder().decode(texture);
          String decodedString = new String(decodedBytes);
          if (decodedString.contains("profileName\" : \"")) {
            String name = decodedString.split("profileName\" : \"", 2)[1].split("\"", 2)[0];
            item = (ItemStack) new CMINBT(item).setString("SkullOwner.Name", name);
          }
        } catch (Throwable var11) {
        }

        Object i =
            new CMINBT(item).setString("Id", UUID.nameUUIDFromBytes(texture.getBytes()).toString());
        return i == null ? null : (ItemStack) i;
      }
    } catch (Throwable e) {
      e.printStackTrace();
      return item;
    }
  }

  @Deprecated
  public Object getNbt(Block block) {
    return CMINBT.getNbt(block);
  }

  public void updateTileEntity(Location loadValue, Object tag) {
    if (tag != null) {
      try {
        Object ent = this.getTileEntityAt(loadValue);
        String ff = "load";
        switch (Version.getCurrent()) {
          case v1_10_R1:
          case v1_9_R1:
          case v1_9_R2:
          case v1_8_R1:
          case v1_8_R3:
          case v1_8_R2:
          case v1_11_R1:
            ff = "a";
            break;
          case v1_12_R1:
            ff = "load";
            break;
          case v1_13_R2:
          case v1_13_R1:
            ff = "create";
            break;
          case v1_7_R4:
            ff = "a";
        }

        Method meth = ent.getClass().getMethod(ff, this.nbtTagCompound);
        meth.invoke(ent, tag);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Deprecated
  public Object asNMSCopy(ItemStack item) {
    return CMINBT.asNMSCopy(item);
  }

  @Deprecated
  public Object asBukkitCopy(Object item) {
    return CMINBT.asBukkitCopy(item);
  }

  public Object getCraftServer() {
    return this.CraftServer;
  }

  public Object getDedicatedServer() {
    return this.MinecraftServer;
  }

  @Deprecated
  public ItemStack getItemInMainHand(Player player) {
    return CMIItemStack.getItemInMainHand(player);
  }

  @Deprecated
  public void setItemInMainHand(Player player, ItemStack item) {
    CMIItemStack.setItemInMainHand(player, item);
  }

  @Deprecated
  public ItemStack getItemInOffHand(Player player) {
    return CMIItemStack.getItemInOffHand(player);
  }

  @Deprecated
  public void setItemInOffHand(Player player, ItemStack item) {
    CMIItemStack.setItemInOffHand(player, item);
  }

  public Class<?> getClass(String classname) {
    try {
      String version =
          Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
      String path =
          classname
              .replace("{nms}", "net.minecraft.server." + version)
              .replace("{nm}", "net.minecraft." + version)
              .replace("{cb}", "org.bukkit.craftbukkit.." + version);
      return Class.forName(path);
    } catch (Throwable t) {
      t.printStackTrace();
      return null;
    }
  }

  @Deprecated
  public Object getNmsPlayer(Player p) throws Exception {
    return this.getPlayerHandle(p);
  }

  public Object getNmsScoreboard(Scoreboard s) throws Exception {
    Method getHandle = s.getClass().getMethod("getHandle");
    return getHandle.invoke(s);
  }

  public Object getFieldValue(Object instance, String fieldName) throws Exception {
    Field field = instance.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(instance);
  }

  public <T> T getFieldValue(Field field, Object obj) {
    try {
      return (T) field.get(obj);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Field getField(Class<?> clazz, String fieldName) throws Exception {
    Field field = clazz.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field;
  }

  public void setValue(Object instance, String field, Object value) {
    try {
      Field f = instance.getClass().getDeclaredField(field);
      f.setAccessible(true);
      f.set(instance, value);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  @Deprecated
  public void sendAllPacket(Object packet) throws Exception {
    for (Player p : Bukkit.getOnlinePlayers()) {
      Object connection = CMIPlayerConnection.getConnection(p);
      if (connection != null) {
        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          this.sendPacket.invoke(connection, packet);
        } else {
          connection
              .getClass()
              .getMethod("sendPacket", this.getClass("{nms}.Packet"))
              .invoke(connection, packet);
        }
      }
    }
  }

  @Deprecated
  public void sendListPacket(List<String> players, Object packet) {
    try {
      for (String name : players) {
        Object connection = CMIPlayerConnection.getConnection(Bukkit.getPlayer(name));
        if (connection != null) {
          if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
            this.sendPacket.invoke(connection, packet);
          } else {
            connection
                .getClass()
                .getMethod("sendPacket", this.getClass("{nms}.Packet"))
                .invoke(connection, packet);
          }
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public Class<?> getPrimitiveType(Class<?> Class) {
    return CORRESPONDING_TYPES.containsKey(Class) ? CORRESPONDING_TYPES.get(Class) : Class;
  }

  public Method getMethod(String MethodName, Class<?> Class, Class<?>... Parameters) {
    Class<?>[] T = this.toPrimitiveTypeArray(Parameters);

    for (Method M : Class.getMethods()) {
      if (M.getName().equals(MethodName)
          && this.equalsTypeArray(this.toPrimitiveTypeArray(M.getParameterTypes()), T)) {
        return M;
      }
    }

    return null;
  }

  public boolean equalsTypeArray(Class<?>[] firstTypes, Class<?>[] secondTypes) {
    if (firstTypes.length != secondTypes.length) {
      return false;
    }

    for (int i = 0; i < firstTypes.length; i++) {
      if (!firstTypes[i].equals(secondTypes[i])
          && !firstTypes[i].isAssignableFrom(secondTypes[i])) {
        return false;
      }
    }

    return true;
  }

  public Class<?>[] toPrimitiveTypeArray(Class<?>[] Classes) {
    int L = Classes != null ? Classes.length : 0;
    Class<?>[] T = new Class[L];

    for (int i = 0; i < L; i++) {
      if (Classes != null) {
        T[i] = this.getPrimitiveType(Classes[i]);
      }
    }

    return T;
  }

  public Object invokeMethod(String MethodName, Object Parameter) {
    try {
      return this.getMethod(MethodName, Parameter.getClass()).invoke(Parameter);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Object getPlayerField(Player Player, String Field) {
    try {
      Object P = this.getPlayerHandle(Player);
      return P.getClass().getField(Field).get(P);
    } catch (Error | Exception e) {
      return null;
    }
  }

  private Integer getActiveContainerStateId(Player player) {
    try {
      return this.getActiveContainerStateId(this.getPlayerHandle(player));
    } catch (IllegalArgumentException | SecurityException e) {
      e.printStackTrace();
      return null;
    }
  }

  private Integer getActiveContainerStateId(Object entityplayer) {
    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
        try {
          Field field = entityplayer.getClass().getField("bU");
          Object container = this.CraftContainer.cast(field.get(entityplayer));
          Method idsMethod = container.getClass().getMethod("j");
          Object ids = idsMethod.invoke(container);
          return (Integer) ids;
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return null;
  }

  public Integer getActiveContainerId(Player player) {
    try {
      return this.getActiveContainerId(this.getPlayerHandle(player));
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  private Integer getActiveContainerId(Object entityplayer) {
    String activeContainer = "activeContainer";
    String windowId = "windowId";
    if (Version.isMojangMappings()) {
      try {
        if (this.containerMenuField == null) {
          this.containerMenuField = this.EntityHuman.getField("containerMenu");
        }

        Object container = this.CraftContainer.cast(this.containerMenuField.get(entityplayer));
        if (this.containerIdField == null) {
          this.containerIdField = container.getClass().getField("containerId");
        }

        return (Integer) this.containerIdField.get(container);
      } catch (Throwable e) {
        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          e.printStackTrace();
        }

        return -1;
      }
    } else {
      try {
        if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
          windowId = "l";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          windowId = "j";
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_21_R7)) {
          activeContainer = "cn";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
          activeContainer = "cl";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R5)) {
          activeContainer = "cn";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
          activeContainer = "bR";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
          activeContainer = "cd";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          activeContainer = "cb";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
          activeContainer = "bS";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
          activeContainer = "bR";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
          activeContainer = "bP";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
          activeContainer = "bU";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R2)) {
          activeContainer = "bV";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
          activeContainer = "bW";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          activeContainer = "bV";
        }

        Object container = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          container =
              this.CraftContainer.cast(
                  this.EntityHuman.getField(activeContainer).get(entityplayer));
        } else {
          container =
              this.CraftContainer.cast(
                  entityplayer.getClass().getField(activeContainer).get(entityplayer));
        }

        return (Integer) container.getClass().getField(windowId).get(container);
      } catch (Throwable e) {
        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          e.printStackTrace();
        }

        return null;
      }
    }
  }

  private Object getContainer(String name) {
    try {
      Field field = this.CraftContainers.getDeclaredField(name);
      return field.get(this.CraftContainers);
    } catch (IllegalArgumentException
        | IllegalAccessException
        | NoSuchFieldException
        | SecurityException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void updateInventoryTitle(Player p, String title) {
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      if (CMIChatColor.stripColor(title).length() > 64) {
        title = title.substring(0, 63) + "~";
      }
    } else if (title.length() > 32) {
      title = title.substring(0, 31) + "~";
    }

    try {
      if (Version.isMojangMappings()) {
        Object entityplayer = this.getPlayerHandle(p);
        Object s = null;
        switch (CMIPlayerInventory.getTopInventory(p).getSize()) {
          case 9:
            s = this.getContainer("GENERIC_9x1");
            break;
          case 18:
            s = this.getContainer("GENERIC_9x2");
            break;
          case 27:
            s = this.getContainer("GENERIC_9x3");
            break;
          case 36:
            s = this.getContainer("GENERIC_9x4");
            break;
          case 45:
            s = this.getContainer("GENERIC_9x5");
            break;
          case 54:
            s = this.getContainer("GENERIC_9x6");
            break;
          default:
            return;
        }

        RawMessage rm = new RawMessage();
        rm.addText(title);
        Constructor<?> packet =
            this.PacketPlayOutOpenWindow.getConstructor(
                int.class, this.CraftContainers, this.IChatBaseComponent);
        Object newPack =
            packet.newInstance(
                this.getActiveContainerId(entityplayer),
                s,
                this.textToIChatBaseComponent(rm.getRaw()));
        this.sendPlayerPacket(p, newPack);
        p.updateInventory();
      } else if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
        Object entityplayer = this.getPlayerHandle(p);
        Object s = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          switch (CMIPlayerInventory.getTopInventory(p).getSize()) {
            case 9:
              s = this.getContainer("a");
              break;
            case 18:
              s = this.getContainer("b");
              break;
            case 27:
              s = this.getContainer("c");
              break;
            case 36:
              s = this.getContainer("d");
              break;
            case 45:
              s = this.getContainer("e");
              break;
            case 54:
              s = this.getContainer("f");
              break;
            default:
              return;
          }
        } else {
          switch (CMIPlayerInventory.getTopInventory(p).getSize()) {
            case 9:
              s = this.getContainer("GENERIC_9X1");
              break;
            case 18:
              s = this.getContainer("GENERIC_9X2");
              break;
            case 27:
              s = this.getContainer("GENERIC_9X3");
              break;
            case 36:
              s = this.getContainer("GENERIC_9X4");
              break;
            case 45:
              s = this.getContainer("GENERIC_9X5");
              break;
            case 54:
              s = this.getContainer("GENERIC_9X6");
              break;
            default:
              return;
          }
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          RawMessage rm = new RawMessage();
          rm.addText(title);
          Constructor<?> packet =
              this.PacketPlayOutOpenWindow.getConstructor(
                  int.class, this.CraftContainers, this.IChatBaseComponent);
          Object newPack =
              packet.newInstance(
                  this.getActiveContainerId(entityplayer),
                  s,
                  this.textToIChatBaseComponent(rm.getRaw()));
          this.sendPlayerPacket(p, newPack);
          p.updateInventory();
        } else {
          RawMessage rm = new RawMessage();
          rm.addText(title);
          Constructor<?> packet =
              this.PacketPlayOutOpenWindow.getConstructor(
                  int.class, this.CraftContainers, this.getMinecraftClass("IChatBaseComponent"));
          Object newPack =
              packet.newInstance(
                  this.getActiveContainerId(entityplayer),
                  s,
                  this.textToIChatBaseComponent(rm.getRaw()));
          this.sendPlayerPacket(p, newPack);
          Field field = entityplayer.getClass().getField("activeContainer");
          Object container = this.CraftContainer.cast(field.get(entityplayer));
          Method meth = entityplayer.getClass().getMethod("updateInventory", this.CraftContainer);
          meth.invoke(entityplayer, container);
        }
      } else if (Version.isCurrentEqualOrHigher(Version.v1_8_R2)) {
        Object entityplayer = this.getPlayerHandle(p);
        RawMessage rm = new RawMessage();
        rm.addText(title);
        Constructor<?> packet =
            this.PacketPlayOutOpenWindow.getConstructor(
                int.class, String.class, this.getMinecraftClass("IChatBaseComponent"), int.class);
        Object newPack =
            packet.newInstance(
                this.getActiveContainerId(entityplayer),
                "minecraft:chest",
                this.textToIChatBaseComponent(rm.getRaw()),
                CMIPlayerInventory.getTopInventory(p).getSize());
        this.sendPlayerPacket(p, newPack);
        Field field = entityplayer.getClass().getField("activeContainer");
        Object container = this.CraftContainer.cast(field.get(entityplayer));
        Method meth = entityplayer.getClass().getMethod("updateInventory", this.CraftContainer);
        meth.invoke(entityplayer, container);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Deprecated
  public ItemStack HideFlag(ItemStack item, int state) {
    return CMINBT.HideFlag(item, state);
  }

  public void superficialEntityTeleport(Player player, Object entity, Location targetLoc) {
    try {
      if (Version.isMojangMappings()) {
        if (entity != null
            && (this.CEntity.isInstance(entity) || this.CItemEntity.isInstance(entity))) {
          Object craft = entity.getClass().getMethod("getBukkitEntity").invoke(entity);
          int id = (Integer) craft.getClass().getMethod("getEntityId").invoke(craft);
          if (this.vec3DConstructor == null) {
            this.vec3DConstructor =
                this.vec3DClass.getConstructor(double.class, double.class, double.class);
          }

          if (this.posMoveRotConstructor == null) {
            this.posMoveRotConstructor =
                this.posMoveRotClass.getConstructor(
                    this.vec3DClass, this.vec3DClass, float.class, float.class);
          }

          if (this.teleportFactoryMethod == null) {
            this.teleportFactoryMethod =
                this.teleportPacketClass.getMethod(
                    "teleport", int.class, this.posMoveRotClass, Set.class, boolean.class);
          }

          Object position =
              this.vec3DConstructor.newInstance(
                  targetLoc.getX(), targetLoc.getY(), targetLoc.getZ());
          Object zeroVec = this.vec3DConstructor.newInstance(0.0, 0.0, 0.0);
          Object posMoveRot =
              this.posMoveRotConstructor.newInstance(
                  position, zeroVec, targetLoc.getYaw(), targetLoc.getPitch());
          Object packet =
              this.teleportFactoryMethod.invoke(null, id, posMoveRot, new HashSet(), false);
          this.sendPlayerPacket(player, packet);
          return;
        }

        return;
      }

      if (entity == null || !this.CEntity.isInstance(entity)) {
        return;
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_21_R2)) {
        Object craft = entity.getClass().getMethod("getBukkitEntity").invoke(entity);
        int id = (Integer) craft.getClass().getMethod("getEntityId").invoke(craft);
        if (this.vec3DConstructor == null) {
          this.vec3DConstructor =
              this.vec3DClass.getConstructor(double.class, double.class, double.class);
        }

        if (this.posMoveRotConstructor == null) {
          this.posMoveRotConstructor =
              this.posMoveRotClass.getConstructor(
                  this.vec3DClass, this.vec3DClass, float.class, float.class);
        }

        if (this.teleportFactoryMethod == null) {
          this.teleportFactoryMethod =
              this.teleportPacketClass.getMethod(
                  "a", int.class, this.posMoveRotClass, Set.class, boolean.class);
        }

        Object position =
            this.vec3DConstructor.newInstance(targetLoc.getX(), targetLoc.getY(), targetLoc.getZ());
        Object zeroVec = this.vec3DConstructor.newInstance(0.0, 0.0, 0.0);
        Object posMoveRot =
            this.posMoveRotConstructor.newInstance(
                position, zeroVec, targetLoc.getYaw(), targetLoc.getPitch());
        Object packet =
            this.teleportFactoryMethod.invoke(null, id, posMoveRot, new HashSet(), false);
        this.sendPlayerPacket(player, packet);
        return;
      }

      Object centity = this.CEntity.cast(entity);
      if (this.teleportMethod == null) {
        if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
          this.teleportMethod =
              this.CEntity.getMethod("a_", double.class, double.class, double.class);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          this.teleportMethod =
              this.CEntity.getMethod("p", double.class, double.class, double.class);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
          this.teleportMethod =
              this.CEntity.getMethod("a", double.class, double.class, double.class);
        } else {
          this.teleportMethod =
              this.CEntity.getMethod("setPosition", double.class, double.class, double.class);
        }
      }

      this.teleportMethod.invoke(centity, targetLoc.getX(), targetLoc.getY(), targetLoc.getZ());
      if (player == null) {
        return;
      }

      if (this.teleportPacket == null) {
        this.teleportPacket = this.PacketPlayOutEntityTeleport.getConstructor(this.CEntity);
      }

      Object packet = this.teleportPacket.newInstance(centity);

      try {
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          this.setValue(packet, "f", (byte) ((int) targetLoc.getYaw() * 256.0F / 360.0F));
          this.setValue(packet, "g", (byte) ((int) targetLoc.getPitch() * 256.0F / 360.0F));
        } else {
          this.setValue(packet, "e", (byte) ((int) targetLoc.getYaw() * 256.0F / 360.0F));
          this.setValue(packet, "f", (byte) ((int) targetLoc.getPitch() * 256.0F / 360.0F));
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }

      this.sendPlayerPacket(player, packet);
    } catch (Throwable e) {
      e.printStackTrace();
      this.teleportMethod = null;
    }
  }

  public void spawnInEntityData(Player player, Entity entity) {
    if (!Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
      try {
        Constructor<?> packet = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
          packet = this.PacketPlayOutSpawnEntityLiving.getConstructor(this.CEntity);
        } else {
          packet = this.PacketPlayOutSpawnEntityLiving.getConstructor(this.EntityLiving);
        }

        Object craftEntity = this.CraftEntity.cast(entity);
        Object craftEntityHandle = this.CraftEntity.getMethod("getHandle").invoke(craftEntity);
        if (this.EntityLiving.isInstance(craftEntityHandle)) {
          Object living = this.EntityLiving.cast(craftEntityHandle);
          Object newPack = packet.newInstance(living);
          this.sendPlayerPacket(player, newPack);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }

      try {
        Object craftEntity = this.CraftEntity.cast(entity);
        int id = (Integer) this.CraftEntity.getMethod("getEntityId").invoke(craftEntity);
        Object craftEntityHandle = this.CraftEntity.getMethod("getHandle").invoke(craftEntity);
        String methodName = "getDataWatcher";
        if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
          methodName = "al";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
          methodName = "ai";
        }

        Object watcher =
            craftEntityHandle.getClass().getMethod(methodName).invoke(craftEntityHandle);
        Constructor<?> packet = null;
        Object newPack = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
          packet = this.PacketPlayOutEntityMetadata.getConstructor(int.class, List.class);
          newPack = packet.newInstance(id, watcher.getClass().getMethod("b").invoke(watcher));
        } else {
          packet =
              this.PacketPlayOutEntityMetadata.getConstructor(
                  int.class, this.DataWatcher, boolean.class);
          newPack = packet.newInstance(id, watcher, true);
        }

        Object pack = newPack;
        this.sendPlayerPacket(player, pack);
        CMIScheduler.get()
            .runTaskLater(
                () -> {
                  try {
                    this.sendPlayerPacket(player, pack);
                  } catch (Throwable e) {
                    e.printStackTrace();
                  }
                },
                20L);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  public void playSound(Player player, Location location, Sound sound, float volume, float pitch) {
    Object cPlayer = this.getCraftPlayer(player);
    CMIScheduler.runTask(
        this.plugin,
        () -> {
          try {
            cPlayer
                .getClass()
                .getMethod("playSound", Location.class, Sound.class, float.class, float.class)
                .invoke(cPlayer, location, sound, volume, pitch);
          } catch (Throwable e) {
            e.printStackTrace();
          }
        });
  }

  public int getEggId(ItemStack item) {
    EntityType type = this.getEggType(item);
    return type == null ? 0 : type.getTypeId();
  }

  @Deprecated
  public EntityType getEggType(ItemStack item) {
    return CMINBT.getEggType(item);
  }

  @Deprecated
  public ItemStack setEggType(ItemStack item, EntityType etype) {
    return CMINBT.setEggType(item, etype);
  }

  public ItemStack modifyItemStack(ItemStack stack, String arguments) {
    return CMINBT.modifyItemStack(stack, arguments);
  }

  public int getHoneyLevel(Block block) {
    if (CMIMaterial.get(block) != CMIMaterial.BEE_NEST
        && CMIMaterial.get(block) != CMIMaterial.BEEHIVE) {
      return 0;
    }

    try {
      Object nb = this.CraftBeehive.cast(block.getBlockData());
      Method method = nb.getClass().getMethod("getHoneyLevel");
      return (Integer) method.invoke(nb);
    } catch (Throwable e) {
      e.printStackTrace();
      return 0;
    }
  }

  public int getMaxHoneyLevel(Block block) {
    if (CMIMaterial.get(block) != CMIMaterial.BEE_NEST
        && CMIMaterial.get(block) != CMIMaterial.BEEHIVE) {
      return 0;
    }

    try {
      Object nb = this.CraftBeehive.cast(block.getBlockData());
      Method method = nb.getClass().getMethod("getMaximumHoneyLevel");
      return (Integer) method.invoke(nb);
    } catch (Throwable e) {
      e.printStackTrace();
      return 5;
    }
  }

  @Deprecated
  public void playEffect(Player player, Location location, CMIEffect ef) {
    CMIEffectManager.playEffect(player, location, ef);
  }

  private Object getKey(String key) {
    try {
      if (Version.isMojangMappings()) {
        return this.resourceKeyMethod.invoke(this.identifierMethod.invoke("minecraft", key));
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
        if (this.minecraftKeyStaticA == null) {
          this.minecraftKeyStaticA = this.minecraftKeyClass.getMethod("a", String.class);
        }

        return this.minecraftKeyStaticA.invoke(null, key);
      } else {
        String namespace = key.contains(":") ? key.split(":", 2)[0] : "minecraft";
        String path = key.contains(":") ? key.split(":", 2)[1] : key;
        return this.getKey(namespace, path);
      }
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  private Object getIdentifier(String base, String key) {
    if (!Version.isMojangMappings()) {
      return null;
    }

    try {
      return this.identifierMethod.invoke(null, base, key);
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  private Object getKey(String base, String key) {
    try {
      if (Version.isMojangMappings()) {
        return this.resourceKeyMethod.invoke(null, this.identifierMethod.invoke(null, base, key));
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
        if (this.minecraftKeyStaticABase == null) {
          this.minecraftKeyStaticABase =
              this.minecraftKeyClass.getMethod("a", String.class, String.class);
        }

        return this.minecraftKeyStaticABase.invoke(null, base, key);
      } else {
        if (this.keyConstructor == null) {
          this.keyConstructor = this.minecraftKeyClass.getConstructor(String.class, String.class);
        }

        return this.keyConstructor.newInstance(base, key);
      }
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  private void removeAdvancementFromRegistry(Object ad) {
    try {
      if (this.advancementRegistryBField == null) {
        this.advancementRegistryBField = this.advancementRegistry.getClass().getField("b");
      }

      Map<Object, Object> advancements =
          (Map<Object, Object>) this.advancementRegistryBField.get(this.advancementRegistry);
      Method getId = ad.getClass().getMethod("getId");
      Object id = getId.invoke(ad);
      if (id != null) {
        Method getNamespace = id.getClass().getMethod("getNamespace");
        Method getKey = id.getClass().getMethod("getKey");
        String namespace = (String) getNamespace.invoke(id);
        String key = (String) getKey.invoke(id);
        advancements.remove(this.getKey(namespace, key));
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public void removeAdvancement(CMIAdvancement ad) {
    if (!Version.isCurrentLower(Version.v1_12_R1)
        && !Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
      if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        this.removeAdvancementFromRegistry(ad);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
        Bukkit.getUnsafe().removeAdvancement(ad.getId());

        try {
          Object server =
              this.MinecraftServerClass.getDeclaredMethod("getServer").invoke(this.MinecraftServer);
          Object AdvancementData = server.getClass().getMethod("getAdvancementData").invoke(server);
          Object REGISTRY = AdvancementData.getClass().getField("REGISTRY").get(AdvancementData);
          Map<Object, Object> advs =
              (Map<Object, Object>) REGISTRY.getClass().getField("advancements").get(REGISTRY);
          if (ad.getId() != null) {
            advs.remove(
                this.CraftNamespacedKey.getMethod("toMinecraft", NamespacedKey.class)
                    .invoke(this.CraftNamespacedKey, ad.getId()));
          }
        } catch (Exception | Error e) {
          e.printStackTrace();
        }
      } else {
        Bukkit.getUnsafe().removeAdvancement(ad.getId());

        try {
          Object REGISTRY =
              this.AdvancementDataWorld.getField("REGISTRY").get(this.AdvancementDataWorld);
          Map<Object, Object> advs =
              (Map<Object, Object>) REGISTRY.getClass().getField("advancements").get(REGISTRY);
          if (ad.getId() != null) {
            advs.remove(
                this.CraftNamespacedKey.getMethod("toMinecraft", NamespacedKey.class)
                    .invoke(this.CraftNamespacedKey, ad.getId()));
          }
        } catch (Exception | Error e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void showToast(CMIAdvancement advancement, Player... players) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
      CMIScheduler.runTaskAsynchronously(
          this.plugin,
          () -> {
            try {
              if (Version.isMojangMappings()) {
                Object frame = this.advancementFrameTypeClass.getField("TASK").get(null);
                if (advancement.getFrame().name().equals("CHALLENGE")) {
                  frame = this.advancementFrameTypeClass.getField("CHALLENGE").get(null);
                } else if (advancement.getFrame().name().equals("GOAL")) {
                  frame = this.advancementFrameTypeClass.getField("GOAL").get(null);
                }

                Object itemStack =
                    Version.isCurrentEqualOrHigher(Version.v26_1_0)
                        ? CMINBT.asItemStackTemplate(advancement.getItem())
                        : CMINBT.asNMSCopy(advancement.getItem());
                Object title =
                    this.textToIChatBaseComponent(
                        new RawMessage().addText(advancement.getTitle()).getRaw());
                Object description =
                    this.textToIChatBaseComponent(
                        new RawMessage().addText(advancement.getDescription()).getRaw());
                if (itemStack == null) {
                  return;
                }

                Object identifier =
                    this.getIdentifier(
                        advancement.getId().getNamespace(), advancement.getId().getKey());
                if (this.toastAdvancementDisplay == null) {
                  this.toastAdvancementDisplay =
                      this.advancementDisplayClass.getConstructor(
                          Version.isCurrentEqualOrHigher(Version.v26_1_0)
                              ? this.IStackTemplate
                              : this.IStack,
                          this.IChatBaseComponent,
                          this.IChatBaseComponent,
                          Optional.class,
                          this.advancementFrameTypeClass,
                          boolean.class,
                          boolean.class,
                          boolean.class);
                }

                Object display =
                    this.toastAdvancementDisplay.newInstance(
                        itemStack, title, description, Optional.empty(), frame, true, false, false);
                List<List<String>> adreq = Arrays.asList(Arrays.asList("CMITOAST"));
                Object requirements =
                    this.advancementRequirementsClass.getConstructor(List.class).newInstance(adreq);
                Object progress = this.advancementProgressClass.getConstructor().newInstance();
                this.advancementProgressClass
                    .getMethod("update", this.advancementRequirementsClass)
                    .invoke(progress, requirements);
                this.advancementProgressClass
                    .getMethod("grantProgress", String.class)
                    .invoke(progress, "CMITOAST");
                Map<Object, Object> progressMap = new HashMap<>();
                progressMap.put(identifier, progress);
                Set<Object> removedSet = new HashSet<>();
                removedSet.add(identifier);
                if (Version.isCurrentEqualOrHigher(Version.v26_3_0)) {
                  try {
                    if (this.toastPacketPlayOutAdvancements == null) {
                      this.toastPacketPlayOutAdvancements =
                          this.packetPlayOutAdvancementsClass.getConstructor(
                              boolean.class, List.class, Set.class, Map.class, boolean.class);
                    }
                  } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                  }

                  Object positionedAdvancement = null;

                  try {
                    Class<?> positionedAdvancementClass =
                        Class.forName(
                            this.packetPlayOutAdvancementsClass.getName()
                                + "$PositionedAdvancement");
                    Object serializedAdv =
                        this.SerializedAdvancement.getConstructor().newInstance();
                    this.SerializedAdvancement.getMethod("display", this.advancementDisplayClass)
                        .invoke(serializedAdv, display);
                    this.SerializedAdvancement.getMethod(
                            "requirements", this.advancementRequirementsClass)
                        .invoke(serializedAdv, requirements);
                    Object holder =
                        this.SerializedAdvancement.getMethod("build", this.identifierClass)
                            .invoke(serializedAdv, identifier);
                    Constructor<?> positionedAdvancementConstructor =
                        positionedAdvancementClass.getConstructor(
                            holder.getClass(), float.class, float.class);
                    positionedAdvancement =
                        positionedAdvancementConstructor.newInstance(holder, 0.0F, 0.0F);
                  } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                  }

                  List<Object> addedList = new ArrayList<>();
                  addedList.add(positionedAdvancement);

                  for (Player player : players) {
                    CMIPlayerConnection.sendPacket(
                        player,
                        this.toastPacketPlayOutAdvancements.newInstance(
                            false, addedList, new HashSet(), progressMap, true));
                    CMIPlayerConnection.sendPacket(
                        player,
                        this.toastPacketPlayOutAdvancements.newInstance(
                            false, new ArrayList(), removedSet, new HashMap(), true));
                  }
                } else {
                  Object serializedAdv = this.SerializedAdvancement.getConstructor().newInstance();
                  this.SerializedAdvancement.getMethod("display", this.advancementDisplayClass)
                      .invoke(serializedAdv, display);
                  this.SerializedAdvancement.getMethod(
                          "requirements", this.advancementRequirementsClass)
                      .invoke(serializedAdv, requirements);
                  Object holder =
                      this.SerializedAdvancement.getMethod("build", this.identifierClass)
                          .invoke(serializedAdv, identifier);
                  Set<Object> addedSet = new HashSet<>();
                  addedSet.add(holder);
                  removedSet.add(identifier);

                  try {
                    if (this.toastPacketPlayOutAdvancements == null) {
                      this.toastPacketPlayOutAdvancements =
                          this.packetPlayOutAdvancementsClass.getConstructor(
                              boolean.class, Collection.class, Set.class, Map.class, boolean.class);
                    }
                  } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                  }

                  for (Player player : players) {
                    CMIPlayerConnection.sendPacket(
                        player,
                        this.toastPacketPlayOutAdvancements.newInstance(
                            false, addedSet, new HashSet(), progressMap, true));
                    CMIPlayerConnection.sendPacket(
                        player,
                        this.toastPacketPlayOutAdvancements.newInstance(
                            false, new HashSet(), removedSet, new HashMap(), true));
                  }
                }
              } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
                Object frame = this.advancementFrameTypeClass.getField("a").get(null);
                if (advancement.getFrame().name().equals("CHALLENGE")) {
                  frame = this.advancementFrameTypeClass.getField("b").get(null);
                } else if (advancement.getFrame().name().equals("GOAL")) {
                  frame = this.advancementFrameTypeClass.getField("c").get(null);
                }

                Object itemStack = CMINBT.asNMSCopy(advancement.getItem());
                Object title =
                    this.textToIChatBaseComponent(
                        new RawMessage().addText(advancement.getTitle()).getRaw());
                Object description =
                    this.textToIChatBaseComponent(
                        new RawMessage().addText(advancement.getDescription()).getRaw());
                Object minecraftKey =
                    this.getKey(advancement.getId().getNamespace(), advancement.getId().getKey());
                if (this.toastAdvancementDisplay == null) {
                  this.toastAdvancementDisplay =
                      this.advancementDisplayClass.getConstructor(
                          this.IStack,
                          this.IChatBaseComponent,
                          this.IChatBaseComponent,
                          Optional.class,
                          this.advancementFrameTypeClass,
                          boolean.class,
                          boolean.class,
                          boolean.class);
                }

                Object display =
                    this.toastAdvancementDisplay.newInstance(
                        itemStack,
                        title,
                        description,
                        Version.isCurrentEqualOrHigher(Version.v1_21_R4)
                            ? Optional.empty()
                            : Optional.of(this.getKey(advancement.getBackground().getUrl())),
                        frame,
                        true,
                        false,
                        false);
                List<List<String>> adreq = Arrays.asList(Arrays.asList("CMITOAST"));
                Object requirements =
                    this.advancementRequirementsClass.getConstructor(List.class).newInstance(adreq);
                Object progress = this.advancementProgressClass.getConstructor().newInstance();
                this.advancementProgressClass
                    .getMethod("a", this.advancementRequirementsClass)
                    .invoke(progress, requirements);
                this.advancementProgressClass
                    .getMethod("a", String.class)
                    .invoke(progress, "CMITOAST");
                Map<Object, Object> progressMap = new HashMap<>();
                progressMap.put(minecraftKey, progress);
                Set<Object> addedSet = new HashSet<>();
                Object serializedAdv = this.SerializedAdvancement.getConstructor().newInstance();
                this.SerializedAdvancement.getMethod("a", this.advancementDisplayClass)
                    .invoke(serializedAdv, display);
                this.SerializedAdvancement.getMethod("a", this.advancementRequirementsClass)
                    .invoke(serializedAdv, requirements);
                Object holder =
                    this.SerializedAdvancement.getMethod("b", this.minecraftKeyClass)
                        .invoke(serializedAdv, minecraftKey);
                addedSet.add(holder);
                Set<Object> removedSet = new HashSet<>();
                removedSet.add(minecraftKey);
                if (Version.isCurrentEqualOrHigher(Version.v1_21_R4)) {
                  try {
                    if (this.toastPacketPlayOutAdvancements == null) {
                      this.toastPacketPlayOutAdvancements =
                          this.packetPlayOutAdvancementsClass.getConstructor(
                              boolean.class, Collection.class, Set.class, Map.class, boolean.class);
                    }
                  } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                  }

                  for (Player player : players) {
                    Object connection =
                        CMILib.getInstance().getReflectionManager().getPlayerConnection(player);
                    this.sendPacket(
                        connection,
                        this.toastPacketPlayOutAdvancements.newInstance(
                            false, addedSet, new HashSet(), progressMap, true));
                    this.sendPacket(
                        connection,
                        this.toastPacketPlayOutAdvancements.newInstance(
                            false, new HashSet(), removedSet, new HashMap(), true));
                  }
                } else {
                  try {
                    if (this.toastPacketPlayOutAdvancements == null) {
                      this.toastPacketPlayOutAdvancements =
                          this.packetPlayOutAdvancementsClass.getConstructor(
                              boolean.class, Collection.class, Set.class, Map.class);
                    }
                  } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                  }

                  for (Player player : players) {
                    Object connection =
                        CMILib.getInstance().getReflectionManager().getPlayerConnection(player);

                    try {
                      this.sendPacket(
                          connection,
                          this.toastPacketPlayOutAdvancements.newInstance(
                              false, addedSet, new HashSet(), progressMap));
                      this.sendPacket(
                          connection,
                          this.toastPacketPlayOutAdvancements.newInstance(
                              false, new HashSet(), removedSet, new HashMap()));
                    } catch (Throwable e) {
                      e.printStackTrace();
                    }
                  }
                }
              } else {
                Class<?> advClass = Class.forName("net.minecraft.advancements.Advancement");
                Method aMethod =
                    advClass.getMethod("a", JsonObject.class, this.LootDeserializationContext);
                Object adv = aMethod.invoke(null, advancement.getJSONObject(), null);
                Class<?> progressClass =
                    Class.forName("net.minecraft.advancements.AdvancementProgress");
                Object progress = progressClass.getConstructor().newInstance();
                Object requirements = adv.getClass().getMethod("g").invoke(adv);
                Method init = progressClass.getMethod("a", requirements.getClass());
                Method c = progressClass.getMethod("c", String.class);
                Method b = progressClass.getMethod("b");
                Object minecraftKey =
                    this.getKey(advancement.getId().getNamespace(), advancement.getId().getKey());
                Set<Object> removed = new HashSet<>();
                removed.add(minecraftKey);
                Set<Object> addedSet = new HashSet<>();
                Map<Object, Object> progressMap = new HashMap<>();
                init.invoke(progress, requirements);
                c.invoke(progress, "CMITOAST");
                b.invoke(progress);
                progressMap.put(minecraftKey, progress);
                Class<?> holderClass =
                    Class.forName("net.minecraft.advancements.AdvancementHolder");
                Constructor<?> holderCtor =
                    holderClass.getConstructor(minecraftKey.getClass(), adv.getClass());
                Object advancementHolder = holderCtor.newInstance(minecraftKey, adv);
                addedSet.add(advancementHolder);

                try {
                  if (this.toastPacketPlayOutAdvancements == null) {
                    this.toastPacketPlayOutAdvancements =
                        this.packetPlayOutAdvancementsClass.getConstructor(
                            boolean.class, Collection.class, Set.class, Map.class);
                  }
                } catch (Throwable e) {
                  e.printStackTrace();
                  return;
                }

                for (Player player : players) {
                  Object connection =
                      CMILib.getInstance().getReflectionManager().getPlayerConnection(player);

                  try {
                    this.sendPacket(
                        connection,
                        this.toastPacketPlayOutAdvancements.newInstance(
                            false, addedSet, new HashSet(), progressMap));
                    this.sendPacket(
                        connection,
                        this.toastPacketPlayOutAdvancements.newInstance(
                            false, new HashSet(), removed, new HashMap()));
                  } catch (Throwable e) {
                    e.printStackTrace();
                  }
                }
              }
            } catch (Throwable t) {
              t.printStackTrace();
            }
          });
    }
  }

  private Object getLootPredicateManager() {
    try {
      if (this.serverGetMethod == null) {
        this.serverGetMethod =
            Class.forName("net.minecraft.server.MinecraftServer").getMethod("getServer");
      }

      Object server = this.serverGetMethod.invoke(null);
      String versionMethod = "getLootPredicateManager";
      if (Version.isCurrentEqualOrLower(Version.v1_18_R1)) {
        versionMethod = "aH";
      } else if (Version.isCurrentEqualOrLower(Version.v1_18_R2)) {
        versionMethod = "aG";
      } else if (Version.isCurrentEqualOrLower(Version.v1_19_R1)) {
        versionMethod = "aI";
      } else if (Version.isCurrentEqualOrLower(Version.v1_19_R2)) {
        versionMethod = "aH";
      } else if (Version.isCurrentEqualOrLower(Version.v1_19_R3)) {
        versionMethod = "aI";
      }

      Method lootMethod = this.lootPredicateManagerMethods.get(versionMethod);
      if (lootMethod == null) {
        lootMethod = server.getClass().getMethod(versionMethod);
        this.lootPredicateManagerMethods.put(versionMethod, lootMethod);
      }

      return lootMethod.invoke(server);
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public void loadAdvancement(CMIAdvancement ad, String advancement) {
    if (!Version.isCurrentLower(Version.v1_12_R1)
        && !Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
      if (Bukkit.getAdvancement(ad.getId()) == null) {
        NamespacedKey key = ad.getId();

        try {
          Class<?> craftKey = getBukkitClass("util.CraftNamespacedKey");
          Method toMinecraft = craftKey.getMethod("toMinecraft", NamespacedKey.class);
          Object mcKey = toMinecraft.invoke(null, key);
          Class<?> jsonElementClass = Class.forName("com.google.gson.JsonElement");
          if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
            Object deser = this.AdvancementDataWorld.getField("b").get(null);
            Object jsonElement =
                deser
                    .getClass()
                    .getMethod("fromJson", String.class, Class.class)
                    .invoke(deser, advancement, jsonElementClass);
            Object jsonObject =
                this.ChatDeserializer.getMethod("m", jsonElementClass, String.class)
                    .invoke(null, jsonElement, "advancement");
            Object predicateMgr = this.getLootPredicateManager();
            Constructor<?> ctor =
                this.LootDeserializationContext.getConstructor(
                    mcKey.getClass(), predicateMgr.getClass());
            Object ldc = ctor.newInstance(mcKey, predicateMgr);
            Method deserialize =
                this.SerializedAdvancement.getMethod(
                    "a", jsonObject.getClass(), this.LootDeserializationContext);
            Object nms = deserialize.invoke(null, jsonObject, ldc);
            if (nms != null) {
              Object ax =
                  this.MinecraftServer.getClass().getMethod("az").invoke(this.MinecraftServer);
              Object c = ax.getClass().getField("c").get(ax);
              c.getClass()
                  .getMethod("a", Map.class)
                  .invoke(c, Collections.singletonMap(mcKey, nms));
            }
          } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
            Object deser = this.AdvancementDataWorld.getField("b").get(null);
            Object jsonElement =
                deser
                    .getClass()
                    .getMethod("fromJson", String.class, Class.class)
                    .invoke(deser, advancement, jsonElementClass);
            Object jsonObject =
                this.ChatDeserializer.getMethod("m", jsonElementClass, String.class)
                    .invoke(null, jsonElement, "advancement");
            Object predicateMgr = this.getLootPredicateManager();
            if (predicateMgr == null) {
              return;
            }

            Constructor<?> ctor =
                this.LootDeserializationContext.getConstructor(
                    mcKey.getClass(), predicateMgr.getClass());
            Object ldc = ctor.newInstance(mcKey, predicateMgr);
            Method deserialize =
                this.SerializedAdvancement.getMethod(
                    "a", jsonObject.getClass(), this.LootDeserializationContext);
            Object nms = deserialize.invoke(null, jsonObject, ldc);
            if (nms != null) {
              Object access = null;
              if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
                access =
                    this.MinecraftServer.getClass().getMethod("az").invoke(this.MinecraftServer);
              } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R2)) {
                access =
                    this.MinecraftServer.getClass().getMethod("ay").invoke(this.MinecraftServer);
              } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
                access =
                    this.MinecraftServer.getClass().getMethod("az").invoke(this.MinecraftServer);
              } else {
                access =
                    this.MinecraftServer.getClass().getMethod("ax").invoke(this.MinecraftServer);
              }

              Object c = access.getClass().getField("c").get(access);
              c.getClass()
                  .getMethod("a", Map.class)
                  .invoke(c, Collections.singletonMap(mcKey, nms));
            }
          } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
            Object deser = this.AdvancementDataWorld.getField("b").get(null);
            Object jsonElement =
                deser
                    .getClass()
                    .getMethod("fromJson", String.class, Class.class)
                    .invoke(deser, advancement, jsonElementClass);
            Object jsonObject =
                this.ChatDeserializer.getMethod("m", jsonElementClass, String.class)
                    .invoke(null, jsonElement, "advancement");
            Object predicateMgr = this.getLootPredicateManager();
            Constructor<?> ctor =
                this.LootDeserializationContext.getConstructor(
                    mcKey.getClass(), predicateMgr.getClass());
            Object ldc = ctor.newInstance(mcKey, predicateMgr);
            Method deserialize =
                this.SerializedAdvancement.getMethod(
                    "a", jsonObject.getClass(), this.LootDeserializationContext);
            Object nms = deserialize.invoke(null, jsonObject, ldc);
            this.advancementRegistry
                .getClass()
                .getMethod("a", Map.class)
                .invoke(this.advancementRegistry, Collections.singletonMap(mcKey, nms));
          } else if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
            Object deser = this.AdvancementDataWorld.getField("DESERIALIZER").get(null);
            Object jsonElement =
                deser
                    .getClass()
                    .getMethod("fromJson", String.class, Class.class)
                    .invoke(deser, advancement, jsonElementClass);
            Object jsonObject =
                this.ChatDeserializer.getMethod("m", jsonElementClass, String.class)
                    .invoke(null, jsonElement, "advancement");
            Object server = this.MinecraftServerClass.getDeclaredMethod("getServer").invoke(null);
            Object predicateMgr = this.getLootPredicateManager();
            Method deserialize =
                this.SerializedAdvancement.getMethod(
                    "a", jsonObject.getClass(), this.LootDeserializationContext);
            Object ldc =
                this.LootDeserializationContext.getConstructor(
                        mcKey.getClass(), predicateMgr.getClass())
                    .newInstance(mcKey, predicateMgr);
            Object nms = deserialize.invoke(null, jsonObject, ldc);
            if (nms != null) {
              Object advData = server.getClass().getMethod("getAdvancementData").invoke(server);
              Object registry = advData.getClass().getField("REGISTRY").get(advData);
              registry
                  .getClass()
                  .getMethod("a", Map.class)
                  .invoke(registry, Collections.singletonMap(mcKey, nms));
            }
          } else {
            Object deser = this.AdvancementDataWorld.getField("DESERIALIZER").get(null);
            Object nms =
                deser
                    .getClass()
                    .getMethod("fromJson", String.class, Class.class)
                    .invoke(deser, advancement, this.SerializedAdvancement);
            Object registry = this.AdvancementDataWorld.getField("REGISTRY").get(null);
            registry
                .getClass()
                .getMethod("a", Map.class)
                .invoke(registry, Collections.singletonMap(mcKey, nms));
          }
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }
  }

  public ItemStack addAttributes(List<Attribute> ls, ItemStack item) {
    if (ls.isEmpty()) {
      return item;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_21_R5)) {
      return item;
    }

    try {
      CMINBT nbt = new CMINBT(item);
      Object modifiers = nbt.getObjectList("AttributeModifiers", 10);
      if (modifiers == null) {
        modifiers = CMINBT.newNBTTagList(10);
      }

      Method sizeMeth = modifiers.getClass().getMethod("size");

      for (Attribute one : ls) {
        Object att = this.nbtTagCompound.newInstance();
        CMINBT subNBT = new CMINBT(att);
        subNBT.setString("AttributeName", one.getType().getFullName());
        subNBT.setString("Name", "generic." + one.getType().getName());
        if (one.getSlot() != null) {
          subNBT.setString("Slot", one.getSlot().getName());
        }

        if (one.getOperation() > 0 && one.getOperation() < 3) {
          subNBT.setInt("Operation", one.getOperation());
        }

        if (Version.isCurrentEqualOrLower(Version.v1_15_R2)) {
          UUID uuid = UUID.randomUUID();
          subNBT.setInt("UUIDLeast", (int) uuid.getLeastSignificantBits());
          subNBT.setInt("UUIDMost", (int) uuid.getMostSignificantBits());
        } else {
          Random rand = new Random();
          int[] uuids =
              new int[] {
                rand.nextInt(160000),
                rand.nextInt(160000),
                rand.nextInt(160000),
                rand.nextInt(160000)
              };
          subNBT.setIntArray("UUID", uuids);
        }

        subNBT.setDouble("Amount", one.getMod());
        CMINBT.addToList(modifiers, (Integer) sizeMeth.invoke(modifiers), att);
      }

      item = (ItemStack) nbt.set("AttributeModifiers", modifiers);
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return item;
  }
}
