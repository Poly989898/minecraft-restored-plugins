package com.Zrips.CMI.NBT;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.BlockStateType;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Advancements.CMIAdvancement;
import com.Zrips.CMI.Modules.Holograms.CMIDataWatcher;
import com.Zrips.CMI.Modules.Holograms.CMIHoloLineType;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import com.Zrips.CMI.Modules.Packets.PacketHandler;
import com.Zrips.CMI.utils.Util;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.Attributes.Attribute;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Advancement.SerializedAdvancement;
import net.minecraft.advancements.Advancements;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.advancements.critereon.LootDeserializationContext;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection.EnumAxis;
import net.minecraft.core.Vector3f;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutAbilities;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.AdvancementDataWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.RayTrace.BlockCollisionOption;
import net.minecraft.world.level.RayTrace.FluidCollisionOption;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_18_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftRayTraceResult;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class v1_18_R1 implements NMS {
  private boolean informed = false;

  @Override
  public Player getPlayer(CMIUser cmiUser) {
    try {
      if (cmiUser.isFakeAccount()) {
        return null;
      }

      File file = Util.getFile(cmiUser.getUniqueId());
      if (!file.canRead() || !file.exists()) {
        return null;
      }

      DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
      GameProfile gameProfile = new GameProfile(cmiUser.getUniqueId(), cmiUser.getName(false));
      WorldServer level = (WorldServer) value.F().iterator().next();
      EntityPlayer entityPlayer = new EntityPlayer(value, level, gameProfile);
      CraftPlayer craftPlayer = entityPlayer.getBukkitEntity();
      if (craftPlayer != null) {
        craftPlayer.loadData();
        return craftPlayer;
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    return null;
  }

  @Override
  public Player createFakeOperator() {
    try {
      GameProfile gameProfile =
          new GameProfile(
              CMI.getInstance().getPlayerManager().getFakeUserUUID(),
              CMI.getInstance().getPlayerManager().getFakeUserName());
      DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
      WorldServer level = (WorldServer) value.F().iterator().next();
      EntityPlayer entityPlayer = new EntityPlayer(value, level, gameProfile);
      entityPlayer.b =
          new PlayerConnection(value, new NetworkManager(EnumProtocolDirection.a), entityPlayer);
      entityPlayer.b.a.l = new InetSocketAddress("127.0.0.1", 25566);
      CraftPlayer craftPlayer = entityPlayer.getBukkitEntity();
      craftPlayer.setOp(true);
      return craftPlayer;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public void unloadData(Player player) {
    try {
      if (player == null || player.isOnline()) {
        return;
      }

      EntityPlayer value = ((CraftPlayer) player).getHandle();
      if (value == null) {
        return;
      }

      WorldServer level = value.x();
      if (level == null) {
        return;
      }

      level.a(value, RemovalReason.c);
      AdvancementDataPlayer advancements = value.M();
      advancements.a();
      DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
      PlayerList playerList = server.ac();
      playerList.j.remove(value);
      ((CraftServer) Bukkit.getServer())
          .getScoreboardManager()
          .removePlayer(value.getBukkitEntity());
    } catch (Exception | Error value6) {
    }
  }

  public NBTTagCompound getNBTTagC(File file) {
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      NBTTagCompound value = NBTCompressedStreamTools.a(fileInputStream);
      fileInputStream.close();
      return value;
    } catch (FileNotFoundException fileNotFoundException) {
      return null;
    } catch (IOException ioException) {
      return null;
    }
  }

  public NBTTagCompound getNBTTagC(UUID uuid) {
    File file = Util.getFile(uuid);
    if (!file.exists()) {
      return null;
    }

    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      NBTTagCompound value = NBTCompressedStreamTools.a(fileInputStream);
      fileInputStream.close();
      return value;
    } catch (Exception exception) {
      return null;
    }
  }

  @Override
  public String getWorldName(File file) {
    NBTTagCompound value = this.getNBTTagC(file);
    if (value == null) {
      return null;
    }

    UUID uuid = new UUID(getUUIDWorldMost(value), getUUIDWorldLeast(value));
    return CMI.getInstance().getUtilManager().getWorldName(uuid);
  }

  @Override
  public void setMiscLocation(File file, Location location) {
    NBTTagCompound value = this.getNBTTagC(file);
    if (value != null) {
      value = this.setMiscLocation(location, value);
      saveData(value, file);
    }
  }

  @Override
  public void setMiscLocation(Player player, Location location) {
    NBTTagCompound value = this.getNBTTagC(player.getUniqueId());
    if (value != null) {
      value = this.setMiscLocation(location, value);
      this.saveData(value, player);
    }
  }

  private static NBTTagCompound setDimension(NBTTagCompound value, int dimension) {
    value.a("Dimension", dimension);
    return value;
  }

  private static NBTTagCompound setLocationWorld(
      NBTTagCompound value, long longValue, long longValue2) {
    value.a("WorldUUIDLeast", longValue);
    value.a("WorldUUIDMost", longValue2);
    return value;
  }

  public static String getName(NBTTagCompound value) {
    NBTTagCompound bukkitTag = value.p("bukkit");
    return bukkitTag.l("lastKnownName");
  }

  public static World getLocationWorld(NBTTagCompound value) {
    return Bukkit.getWorld(new UUID(getUUIDWorldMost(value), getUUIDWorldLeast(value)));
  }

  private static long getUUIDWorldMost(NBTTagCompound value) {
    return value.i("WorldUUIDMost");
  }

  private static long getUUIDWorldLeast(NBTTagCompound value) {
    return value.i("WorldUUIDLeast");
  }

  public NBTTagCompound setMiscLocation(Location location, NBTTagCompound value) {
    value =
        setLocationWorld(
            value,
            location.getWorld().getUID().getLeastSignificantBits(),
            location.getWorld().getUID().getMostSignificantBits());
    value = setDimension(value, location.getWorld().getEnvironment().getId());
    value = setLocationPosition(value, location.getX(), location.getY(), location.getZ());
    return setLocationView(value, location.getYaw(), location.getPitch());
  }

  public static NBTTagCompound setLocationPosition(
      NBTTagCompound value, double x, double y, double z) {
    NBTTagList posList = new NBTTagList();
    posList.add(NBTTagDouble.a(x));
    posList.add(NBTTagDouble.a(y));
    posList.add(NBTTagDouble.a(z));
    value.a("Pos", posList);
    return value;
  }

  public static NBTTagCompound setLocationView(NBTTagCompound value, float yaw, float pitch) {
    NBTTagList rotationList = new NBTTagList();
    rotationList.add(NBTTagFloat.a(yaw));
    rotationList.add(NBTTagFloat.a(pitch));
    value.a("Rotation", rotationList);
    return value;
  }

  // $VF: Could not inline inconsistent finally blocks
  // Please report this to the Vineflower issue tracker, at
  // https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the
  // rights to distribute it!)
  public static void saveData(NBTTagCompound value, File file) {
    if (value != null) {
      try {
        Throwable throwable = null;
        Object object = null;

        try {
          FileOutputStream fileOutputStream = new FileOutputStream(file);

          try {
            NBTCompressedStreamTools.a(value, fileOutputStream);
          } finally {
            if (fileOutputStream != null) {
              fileOutputStream.close();
            }
          }
        } catch (Throwable throwable2) {
          if (throwable == null) {
            throwable = throwable2;
          } else if (throwable != throwable2) {
            throwable.addSuppressed(throwable2);
          }

          throw throwable;
        }
      } catch (Exception exception) {
      }
    }
  }

  // $VF: Could not inline inconsistent finally blocks
  // Please report this to the Vineflower issue tracker, at
  // https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the
  // rights to distribute it!)
  public void saveData(NBTTagCompound value, Player player) {
    if (value != null) {
      try {
        Throwable throwable = null;
        Object object = null;

        try {
          FileOutputStream fileOutputStream = new FileOutputStream(Util.getFile(player));

          try {
            NBTCompressedStreamTools.a(value, fileOutputStream);
          } finally {
            if (fileOutputStream != null) {
              fileOutputStream.close();
            }
          }
        } catch (Throwable throwable2) {
          if (throwable == null) {
            throwable = throwable2;
          } else if (throwable != throwable2) {
            throwable.addSuppressed(throwable2);
          }

          throw throwable;
        }
      } catch (Exception exception) {
      }
    }
  }

  @Override
  public void clearPotionEffects(Player player) {
    File file = Util.getFile(player);
    this.clearPotionEffects(file);
  }

  public void clearPotionEffects(File file) {
    NBTTagCompound value = this.getNBTTagC(file);
    if (value != null) {
      value.r("ActiveEffects");
      saveData(value, file);
    }
  }

  @Override
  public void setPotionEffects(Player player, PotionEffect potionEffect) {
    File file = Util.getFile(player);
    this.setPotionEffects(potionEffect, file);
  }

  public void setPotionEffects(PotionEffect potionEffect, File file) {
    NBTTagCompound value = this.getNBTTagC(file);
    if (value != null) {
      NBTTagList effects = value.c("ActiveEffects", 10);
      boolean flag = false;

      for (int index = 0; index < effects.size(); index++) {
        NBTTagCompound effect = effects.a(index);
        if (effect.f("Id") == potionEffect.getType().getId()) {
          flag = true;
          effect.a("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
          effect.a("Amplifier", (byte) potionEffect.getAmplifier());
          effect.a("Duration", potionEffect.getDuration());
          effect.a("ShowParticles", (byte) (potionEffect.hasParticles() ? 1 : 0));
          effects.a(index, effect);
          break;
        }
      }

      if (!flag) {
        NBTTagCompound newEffect = new NBTTagCompound();
        newEffect.a("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
        newEffect.a("Amplifier", (byte) potionEffect.getAmplifier());
        newEffect.a("Duration", potionEffect.getDuration());
        newEffect.a("Id", (byte) potionEffect.getType().getId());
        newEffect.a("ShowParticles", (byte) (potionEffect.hasParticles() ? 1 : 0));
        effects.add(newEffect);
      }

      value.a("ActiveEffects", effects);
      saveData(value, file);
    }
  }

  @Override
  public void relightChunk(Chunk chunk) {}

  @Override
  public void resendChunk(Chunk chunk, Player player) {
    ((CraftPlayer) player)
        .getHandle()
        .b
        .a(new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle()));
  }

  @Override
  public void unloadChunk(Chunk chunk, Player player) {
    ((CraftPlayer) player)
        .getHandle()
        .b
        .a(new PacketPlayOutUnloadChunk(chunk.getX(), chunk.getZ()));
  }

  public ItemStack addAttributes(List<Attribute> attributes, ItemStack itemStack) {
    if (attributes.isEmpty()) {
      return itemStack;
    }

    net.minecraft.world.item.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value != null) {
      NBTTagCompound itemTag = null;
      if (value.r()) {
        itemTag = value.s();
      }

      if (itemTag == null) {
        itemTag = new NBTTagCompound();
        value.c(itemTag);
        itemTag = value.s();
      }

      NBTTagList attributeList = itemTag.c("AttributeModifiers", 10);

      for (Attribute attribute : attributes) {
        NBTTagCompound modifierTag = new NBTTagCompound();
        modifierTag.a("AttributeName", NBTTagString.a(attribute.getType().getFullName()));
        modifierTag.a("Name", NBTTagString.a(attribute.getType().getName()));
        modifierTag.a("Amount", NBTTagDouble.a(attribute.getMod()));
        modifierTag.a("Operation", NBTTagInt.a(attribute.getType().getAction()));
        modifierTag.a("Slot", NBTTagString.a(attribute.getSlot().getName()));
        UUID uuid = UUID.randomUUID();
        modifierTag.a("UUIDLeast", NBTTagInt.a((int) uuid.getLeastSignificantBits()));
        modifierTag.a("UUIDMost", NBTTagInt.a((int) uuid.getMostSignificantBits()));
        attributeList.add(modifierTag);
      }

      itemTag.a("AttributeModifiers", attributeList);
      value.c(itemTag);
      itemStack = CraftItemStack.asBukkitCopy(value);
    }

    return itemStack;
  }

  @Override
  public void updateCollisions(Player player, boolean collisionEnabled, boolean selfOnly) {}

  public static void sendPacket(Player player, PacketPlayOutScoreboardTeam value) {
    ((CraftPlayer) player).getHandle().b.a(value);
  }

  public Inventory getShulkerInv(ItemStack itemStack) {
    if (!CMIMaterial.get(itemStack).isShulkerBox()) {
      return null;
    }

    try {
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (itemMeta == null) {
        return null;
      }

      if (!(itemMeta instanceof BlockStateMeta)) {
        return null;
      }

      BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
      if (!blockStateMeta.hasBlockState()) {
        return null;
      }

      if (blockStateMeta.getBlockState() == null) {
        return null;
      }

      if (!(blockStateMeta.getBlockState() instanceof ShulkerBox)) {
        return null;
      }

      ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
      return shulkerBox.getInventory() == null ? null : shulkerBox.getInventory();
    } catch (Throwable throwable) {
      return null;
    }
  }

  public Inventory getShulkerInv(Block block) {
    if (!CMIMaterial.get(block).isShulkerBox()) {
      return null;
    }

    if (!(block.getState() instanceof ShulkerBox)) {
      return null;
    }

    ShulkerBox shulkerBox = (ShulkerBox) block.getState();
    return shulkerBox.getInventory() == null ? null : shulkerBox.getInventory();
  }

  @Override
  public int getTickLived(UUID uuid) {
    if (uuid == null) {
      return 0;
    } else {
      NBTTagCompound value = this.getNBTTagC(uuid);
      if (value == null) {
        return 0;
      } else {
        return !value.e("Spigot.ticksLived") ? 0 : value.h("Spigot.ticksLived");
      }
    }
  }

  @Override
  public void showResurection(Player player) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    if (value instanceof EntityPlayer) {
      EntityPlayer entityPlayer = value;
      entityPlayer.b(StatisticList.c.b(Items.sw));
      CriterionTriggers.B.a(
          entityPlayer, CraftItemStack.asNMSCopy(new ItemStack(Material.TOTEM_OF_UNDYING)));
    }

    value.c(1.0F);
    value.dV();
    if (CMI.getInstance().getTotemManager().getRegenerationDuration() > 0) {
      value.b(
          new MobEffect(
              MobEffects.j, CMI.getInstance().getTotemManager().getRegenerationDuration() * 20, 1));
    }

    if (CMI.getInstance().getTotemManager().getFireResistanceDuration() > 0) {
      value.b(
          new MobEffect(
              MobEffects.l,
              CMI.getInstance().getTotemManager().getFireResistanceDuration() * 20,
              1));
    }

    if (CMI.getInstance().getTotemManager().getAbsorbtionDuration() > 0) {
      value.b(
          new MobEffect(
              MobEffects.v, CMI.getInstance().getTotemManager().getAbsorbtionDuration() * 20, 1));
    }

    value.t.a(value, (byte) 35);
  }

  @Override
  public void setEntityTag(Entity entity, String tagName, Object tagValue) {
    NBTTagCompound value = new NBTTagCompound();
    net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
    NBTTagCompound entityTag = nmsEntity.f(value);
    if (tagValue instanceof String) {
      entityTag.a(tagName, (String) tagValue);
    } else if (tagValue instanceof Short) {
      entityTag.a(tagName, (Short) tagValue);
    } else if (tagValue instanceof Byte) {
      entityTag.a(tagName, (Byte) tagValue);
    }

    nmsEntity.g(value);
  }

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    net.minecraft.world.item.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      String string = enchantment.getKey().getKey();
      NBTTagCompound itemTag = value.s();
      boolean flag = false;
      if (itemTag != null && itemTag.e("ench")) {
        NBTTagList enchantments = itemTag.c("ench", 10);

        for (int index = 0; index < enchantments.size(); index++) {
          if (enchantments.a(index).l("id").equalsIgnoreCase(string)) {
            enchantments.remove(index);
            flag = true;
            break;
          }
        }

        if (flag) {
          itemTag.a("ench", enchantments);
        }

        value.c(itemTag);
      }

      if (flag) {
        return CraftItemStack.asBukkitCopy(value);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    return null;
  }

  @Override
  public ItemStack setNBTList(ItemStack itemStack, String key, List<String> values) {
    net.minecraft.world.item.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      NBTTagCompound itemTag = value.s();
      if (itemTag == null) {
        itemTag = new NBTTagCompound();
      }

      NBTTagList tagList = new NBTTagList();
      if (values != null) {
        for (String entry : values) {
          tagList.add(NBTTagString.a(entry));
        }
      }

      if (values == null) {
        itemTag.r(key);
      } else {
        itemTag.a(key, tagList);
      }

      value.c(itemTag);
      return CraftItemStack.asBukkitCopy(value);
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean changeGodMode(Player player, boolean godMode) {
    if (player == null) {
      return false;
    }

    player.setInvulnerable(godMode);
    return true;
  }

  @Override
  public boolean getGodMode(Player player) {
    return player.isInvulnerable();
  }

  @Override
  public void updateExpBar(Player player) {
    CraftPlayer value = (CraftPlayer) player;
    value.getHandle().cD = -1;
  }

  @Override
  public Command getCommand(String name) {
    return ((CraftServer) Bukkit.getServer()).getCommandMap().getCommand(name);
  }

  private static EntityArmorStand createStand(Location location, String text, boolean small) {
    double x = location.getX();
    double z = location.getZ();
    double y = location.getY();
    if (small) {
      x += 0.05;
      z -= 0.2;
      y -= 0.3;
    } else {
      y -= 0.45;
    }

    EntityArmorStand value =
        new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), x, y, z);
    value.j(true);
    value.r(false);
    value.t(true);
    value.e(true);
    value.s(true);
    value.a(small);
    if (small) {
      value.d(new Vector3f(-15.0F, -45.0F, 0.0F));
    }

    if (text != null && !text.isEmpty()) {
      IChatBaseComponent component = CraftChatMessage.fromString(CMIChatColor.translate(text))[0];
      value.a(component);
      if (!CMIChatColor.stripColor(text).isEmpty() && !text.replace(" ", "").isEmpty()) {
        value.n(true);
      } else {
        value.n(false);
      }
    } else {
      value.n(false);
    }

    return value;
  }

  @Override
  public CMIDataWatcher spawnHologram(
      Player player, Location location, String text, ItemStack itemStack) {
    EntityArmorStand value = createStand(location, text, itemStack != null);
    CraftPlayer craftPlayer = (CraftPlayer) player;

    try {
      craftPlayer.getHandle().b.a(new PacketPlayOutSpawnEntity(value, 78));

      try {
        craftPlayer.getHandle().b.a(new PacketPlayOutEntityMetadata(value.ae(), value.ai(), false));
        if (itemStack != null) {
          var arrayList = Lists.newArrayList();
          arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
          PacketPlayOutEntityEquipment equipmentPacket =
              new PacketPlayOutEntityEquipment(value.ae(), arrayList);
          craftPlayer.getHandle().b.a(equipmentPacket);
        }

        return new CMIDataWatcher(
            value.ae(),
            value,
            itemStack != null ? CMIHoloLineType.staticIcon : CMIHoloLineType.plainText);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    } catch (Exception exception2) {
      exception2.printStackTrace();
    }

    return null;
  }

  @Override
  public FakeInfo spawnClickable(Player player, Location location, Object data, boolean glowing) {
    try {
      if (glowing) {
        EntitySlime value =
            new EntitySlime(EntityTypes.X, ((CraftWorld) location.getWorld()).getHandle());
        value.j(true);
        value.e(true);
        value.a(true);
        value.b(2, true);
        value.a(location.getX(), location.getY() - 0.45, location.getZ() - 0.2, 0.0F, 0.0F);
        value.n(false);
        value.d(true);
        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(value);
        ((CraftPlayer) player).getHandle().b.a(spawnPacket);
        ((CraftPlayer) player)
            .getHandle()
            .b
            .a(new PacketPlayOutEntityMetadata(value.ae(), value.ai(), false));
        FakeInfo fakeInfo = new FakeInfo().setObj(data).setPlayer(player).setId(value.ae());
        PacketHandler.fakeEntities.put(value.ae(), fakeInfo);
        return fakeInfo;
      }

      EntityArmorStand stand =
          new EntityArmorStand(
              ((CraftWorld) location.getWorld()).getHandle(),
              location.getX(),
              location.getY() - 0.35,
              location.getZ());
      stand.j(true);
      stand.r(false);
      stand.e(true);
      stand.s(false);
      if (data instanceof CMIHologram) {
        if (((CMIHologram) data).getHeight() > 1.7) {
          stand.a(false);
          stand.a(location.getX(), location.getY() - 0.75, location.getZ(), 0.0F, 0.0F);
        } else {
          stand.a(true);
        }
      } else {
        stand.a(true);
      }

      stand.n(false);
      PacketPlayOutSpawnEntityLiving standSpawnPacket = new PacketPlayOutSpawnEntityLiving(stand);
      ((CraftPlayer) player).getHandle().b.a(standSpawnPacket);
      ((CraftPlayer) player)
          .getHandle()
          .b
          .a(new PacketPlayOutEntityMetadata(stand.ae(), stand.ai(), false));
      FakeInfo fakeInfo = new FakeInfo().setObj(data).setPlayer(player).setId(stand.ae());
      PacketHandler.fakeEntities.put(stand.ae(), fakeInfo);
      return fakeInfo;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public void removeHologram(Player player, Integer entityId) {
    if (entityId != null) {
      CraftPlayer value = (CraftPlayer) player;

      try {
        Packet destroyPacket = null;
        if (Version.isCurrentSubEqual(0)) {
          try {
            destroyPacket =
                PacketPlayOutEntityDestroy.class.getConstructor(int.class).newInstance(entityId);
          } catch (Throwable throwable) {
            destroyPacket = new PacketPlayOutEntityDestroy(new int[] {entityId});
          }
        } else {
          destroyPacket = new PacketPlayOutEntityDestroy(new int[] {entityId});
        }

        if (destroyPacket != null) {
          value.getHandle().b.a(destroyPacket);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  @Override
  public void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String text, ItemStack itemStack) {
    if (cmiDataWatcher != null && cmiDataWatcher.getStand() instanceof EntityArmorStand) {
      EntityArmorStand value = (EntityArmorStand) cmiDataWatcher.getStand();
      if (text == null || text.isEmpty()) {
        value.n(false);
      } else if (!CMIChatColor.stripColor(text).isEmpty() && !text.replace(" ", "").isEmpty()) {
        value.n(true);
      } else {
        value.n(false);
      }

      IChatBaseComponent component = CraftChatMessage.fromString(text)[0];
      value.a(component);
      PacketPlayOutEntityMetadata metadataPacket =
          new PacketPlayOutEntityMetadata(cmiDataWatcher.getId(), value.ai(), true);
      CraftPlayer craftPlayer = (CraftPlayer) player;

      try {
        craftPlayer.getHandle().b.a(metadataPacket);
        var arrayList = Lists.newArrayList();
        arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        PacketPlayOutEntityEquipment equipmentPacket =
            new PacketPlayOutEntityEquipment(cmiDataWatcher.getId(), arrayList);
        craftPlayer.getHandle().b.a(equipmentPacket);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  @Override
  public CMIDataWatcher makeFloatingIcon(Player player, Location location, ItemStack itemStack) {
    return this.makeFloatingIcon(player, location, itemStack, false);
  }

  @Override
  public CMIDataWatcher makeFloatingIcon(
      Player player, Location location, ItemStack itemStack, boolean glowing) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    EntityItem itemEntity =
        new EntityItem(
            ((CraftWorld) location.getWorld()).getHandle(),
            location.getX(),
            location.getY(),
            location.getZ(),
            CraftItemStack.asNMSCopy(itemStack));
    itemEntity.e(location.getX(), location.getY(), location.getZ());
    itemEntity.e(true);
    if (glowing) {
      itemEntity.i(true);
    }

    PacketPlayOutEntityVelocity velocityPacket =
        new PacketPlayOutEntityVelocity(itemEntity.ae(), new Vec3D(0.0, 0.0, 0.0));
    PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(itemEntity);
    value.b.a(spawnPacket);
    PacketPlayOutEntityMetadata metadataPacket =
        new PacketPlayOutEntityMetadata(itemEntity.ae(), itemEntity.ai(), true);
    value.b.a(metadataPacket);
    value.b.a(velocityPacket);
    return new CMIDataWatcher(itemEntity.ae(), itemEntity.ai(), CMIHoloLineType.icon);
  }

  public void playSound(Player player, Location location, Sound sound, float volume, float pitch) {
    CraftPlayer value = (CraftPlayer) player;
    value.playSound(location, sound, volume, pitch);
  }

  @Override
  public boolean clearSkin(Player player) {
    if (player == null) {
      return false;
    }

    CraftPlayer value = (CraftPlayer) player;
    GameProfile gameProfile = value.getProfile();
    if (gameProfile != null) {
      gameProfile.getProperties().removeAll("textures");
    }

    this.updatePlayerViaPackets(player);
    return true;
  }

  @Override
  public void updatePlayerViaPackets(final Player player) {
    final CraftPlayer value = (CraftPlayer) player;
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    if (cmiUser != null) {
      EntityPlayer renamedPlayer = null;
      if (!cmiUser.getTagName().equals(player.getName())) {
        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile =
            new GameProfile(player.getUniqueId(), CMIChatColor.translate(cmiUser.getTagName()));
        gameProfile.getProperties().putAll(value.getHandle().fp().getProperties());
        WorldServer renamedLevel = server.a(value.getHandle().P());
        renamedPlayer = new EntityPlayer(server, renamedLevel, gameProfile);
        net.minecraft.world.entity.Entity renamedEntity =
            renamedPlayer.getBukkitEntity().getHandle();
        renamedEntity.e(player.getEntityId());
      }

      EntityPlayer playerHandle = value.getHandle();
      int entityId = player.getEntityId();
      PacketPlayOutPlayerInfo removeInfoPacket =
          new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.d, new EntityPlayer[] {playerHandle});
      Packet addInfoPacket = null;
      if (renamedPlayer != null) {
        addInfoPacket =
            new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.a, new EntityPlayer[] {renamedPlayer});
      } else {
        addInfoPacket =
            new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.a, new EntityPlayer[] {playerHandle});
      }

      PacketPlayOutEntityDestroy destroyPacket =
          new PacketPlayOutEntityDestroy(new int[] {entityId});
      PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(playerHandle);
      var arrayList = Lists.newArrayList();
      arrayList.add(
          Pair.of(
              EnumItemSlot.f,
              CraftItemStack.asNMSCopy(
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.Helmet))));
      arrayList.add(
          Pair.of(
              EnumItemSlot.e,
              CraftItemStack.asNMSCopy(
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate))));
      arrayList.add(
          Pair.of(
              EnumItemSlot.d,
              CraftItemStack.asNMSCopy(
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.Pants))));
      arrayList.add(
          Pair.of(
              EnumItemSlot.c,
              CraftItemStack.asNMSCopy(
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.Boots))));
      arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(cmiUser.getItemInHand())));
      arrayList.add(Pair.of(EnumItemSlot.b, CraftItemStack.asNMSCopy(cmiUser.getItemInOffHand())));
      final PacketPlayOutEntityEquipment equipmentPacket =
          new PacketPlayOutEntityEquipment(entityId, arrayList);
      PacketPlayOutEntityMetadata metadataPacket =
          new PacketPlayOutEntityMetadata(entityId, value.getHandle().ai(), true);
      PacketPlayOutAbilities abilitiesPacket = new PacketPlayOutAbilities(value.getHandle().fr());
      PacketPlayOutMount mountPacket = null;
      if (player.getVehicle() != null) {
        mountPacket = new PacketPlayOutMount(((CraftEntity) player.getVehicle()).getHandle());
      }

      CraftWorld craftWorld = (CraftWorld) player.getWorld();
      WorldServer level = craftWorld.getHandle();
      EnumGamemode gameMode = EnumGamemode.a;
      switch (player.getGameMode()) {
        case CREATIVE:
          gameMode = EnumGamemode.b;
        case SURVIVAL:
        default:
          break;
        case ADVENTURE:
          gameMode = EnumGamemode.c;
          break;
        case SPECTATOR:
          gameMode = EnumGamemode.d;
      }

      final int heldSlot = player.getInventory().getHeldItemSlot();

      for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
        final PlayerConnection connection = ((CraftPlayer) otherPlayer).getHandle().b;
        if (otherPlayer == player) {
          final boolean flag = player.isFlying();
          connection.a(removeInfoPacket);
          connection.a(addInfoPacket);
          Location location = player.getLocation();
          HashSet hashSet = new HashSet();
          PacketPlayOutPosition positionPacket =
              new PacketPlayOutPosition(
                  location.getX(),
                  location.getY(),
                  location.getZ(),
                  location.getYaw(),
                  location.getPitch(),
                  hashSet,
                  0,
                  false);
          DimensionManager dimensionHolder = level.q_();
          PacketPlayOutRespawn respawnPacket =
              new PacketPlayOutRespawn(
                  dimensionHolder, level.aa(), 0L, gameMode, gameMode, false, false, false);
          connection.a(respawnPacket);
          connection.a(positionPacket);

          for (MobEffect mobEffect : playerHandle.dW()) {
            connection.a(new PacketPlayOutEntityEffect(entityId, mobEffect));
          }

          PacketPlayOutUpdateHealth healthPacket =
              new PacketPlayOutUpdateHealth(
                  (float) player.getHealth(), player.getFoodLevel(), player.getSaturation());
          connection.a(healthPacket);
          if (mountPacket != null) {
            connection.a(mountPacket);
          }

          connection.a(metadataPacket);
          connection.a(abilitiesPacket);
          Bukkit.getScheduler()
              .runTask(
                  CMI.getInstance(),
                  new Runnable() {
                    @Override
                    public void run() {
                      if (flag) {
                        player.setAllowFlight(true);
                        player.setFlying(true);
                      }

                      v1_18_R1.this.updateExpBar(player);
                      if (player.isOp()) {
                        ((CraftServer) Bukkit.getServer()).getHandle().a(value.getProfile());
                        value.recalculatePermissions();
                      }

                      player.getInventory().setHeldItemSlot(heldSlot);
                    }
                  });
        } else if (otherPlayer.canSee(player)) {
          connection.a(removeInfoPacket);
          connection.a(addInfoPacket);
          if (CMI.getInstance()
                      .getUtilManager()
                      .getDistance(otherPlayer.getLocation(), player.getLocation())
                  < CMI.getInstance().getUtilManager().getPlayerTrackingRange(player.getWorld())
              && destroyPacket != null) {
            connection.a(destroyPacket);
            connection.a(spawnPacket);
          }

          if (mountPacket != null) {
            connection.a(mountPacket);
          }

          connection.a(metadataPacket);
          Bukkit.getServer()
              .getScheduler()
              .scheduleSyncDelayedTask(
                  CMI.getInstance(),
                  new Runnable() {
                    @Override
                    public void run() {
                      connection.a(equipmentPacket);
                    }
                  },
                  1L);
        }
      }
    }
  }

  public void loadAdvancement(NamespacedKey namespacedKey, String string) {
    if (Bukkit.getAdvancement(namespacedKey) == null) {
      MinecraftKey value = CraftNamespacedKey.toMinecraft(namespacedKey);
      JsonElement jsonElement =
          (JsonElement) AdvancementDataWorld.b.fromJson(string, JsonElement.class);
      JsonObject jsonObject = ChatDeserializer.m(jsonElement, "advancement");
      SerializedAdvancement serializedAdvancement =
          SerializedAdvancement.a(
              jsonObject, new LootDeserializationContext(value, MinecraftServer.getServer().aH()));
      if (serializedAdvancement != null) {
        MinecraftServer.getServer()
            .ax()
            .c
            .a(Maps.newHashMap(Collections.singletonMap(value, serializedAdvancement)));
      }
    }
  }

  public void removeAdvancement(CMIAdvancement value) {
    Bukkit.getUnsafe().removeAdvancement(value.getId());

    try {
      DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
      Advancements advancements = server.ax().c;

      for (Entry entry : advancements.b.entrySet()) {
        if (((Advancement) entry.getValue()).h().a().equals(value.getId().getKey().toLowerCase())) {
          advancements.b.remove(entry.getKey());
          break;
        }
      }
    } catch (Exception | Error value4) {
      value4.printStackTrace();
    }
  }

  public void playEffect(Player player, Location location, Effect effect, Color color) {
    CMIParticle cmiParticle = CMIParticle.getCMIParticle(effect.name());
    CMIEffect cmiEffect = new CMIEffect(cmiParticle);
    cmiEffect.setAmount(0);
    cmiEffect.setSpeed(1.0F);
    cmiEffect.setColor(org.bukkit.Color.fromBGR(color.getBlue(), color.getGreen(), color.getRed()));
    this.playEffect(player, location, cmiEffect);
  }

  public void playEffect(
      Player player,
      Location location,
      Effect effect,
      float offsetX,
      float offsetY,
      float offsetZ,
      float speed,
      int value) {
    CMIParticle cmiParticle = CMIParticle.getCMIParticle(effect.name());
    CMIEffect cmiEffect = new CMIEffect(cmiParticle);
    cmiEffect.setAmount(value);
    cmiEffect.setOffset(new Vector(offsetX, offsetY, offsetZ));
    cmiEffect.setSpeed(speed);
    this.playEffect(player, location, cmiEffect);
  }

  public void playEffect(
      Player player,
      Location location,
      CMIParticle cmiParticle,
      float offsetX,
      float offsetY,
      float offsetZ,
      float speed,
      int value) {
    CMIEffect cmiEffect = new CMIEffect(cmiParticle);
    cmiEffect.setAmount(value);
    cmiEffect.setOffset(new Vector(offsetX, offsetY, offsetZ));
    cmiEffect.setSpeed(speed);
    this.playEffect(player, location, cmiEffect);
  }

  public void playEffect(Player player, Location location, CMIEffect cmiEffect) {
    if (player != null
        && player.isOnline()
        && location != null
        && cmiEffect != null
        && location.getWorld() != null) {
      CMIParticle cmiParticle = cmiEffect.getParticle();
      if (cmiParticle != null) {
        if (cmiParticle.isParticle()) {
          Particle particle = cmiParticle.getParticle();
          if (particle != null) {
            DustOptions dustOptions = null;
            if (particle.equals(Particle.REDSTONE)) {
              dustOptions = new DustOptions(cmiEffect.getColor(), cmiEffect.getSize());
            }

            PacketPlayOutWorldParticles value =
                new PacketPlayOutWorldParticles(
                    CraftParticle.toNMS(particle, dustOptions),
                    true,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    (float) cmiEffect.getOffset().getX(),
                    (float) cmiEffect.getOffset().getY(),
                    (float) cmiEffect.getOffset().getZ(),
                    cmiEffect.getSpeed(),
                    cmiEffect.getAmount());
            CraftPlayer craftPlayer = (CraftPlayer) player;
            if (craftPlayer.getHandle().b != null) {
              if (location.getWorld().equals(craftPlayer.getWorld())) {
                craftPlayer.getHandle().b.a(value);
              }
            }
          }
        }
      }
    }
  }

  @Override
  public boolean forceTeleport(Player player, Location location) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    if (value.dZ() == 0.0F || !value.bl()) {
      return false;
    }

    if (value.b == null) {
      return false;
    }

    if (value.bG()) {
      return false;
    }

    value.p();
    WorldServer level = ((CraftWorld) location.getWorld()).getHandle();
    World world = location.getWorld();
    if (world == null) {
      level = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
    } else {
      level = ((CraftWorld) world).getHandle();
    }

    ((CraftPlayer) player).getHandle().q();
    if (((CraftWorld) player.getLocation().getWorld()).getHandle() == level) {
      value.b.teleport(location);
    } else {
      ((CraftServer) Bukkit.getServer()).getHandle().respawn(value, level, true, location, true);
    }

    return true;
  }

  public HashMap<BlockStateType, String> getBlockStates(Block block) {
    HashMap<BlockStateType, String> hashMap = new HashMap();

    try {
      CraftBlockData value = (CraftBlockData) block.getBlockData();
      IBlockData blockState = value.getState();
      Map map = (Map) blockState.getClass().getMethod("a").invoke(blockState);
      if (map != null) {
        for (Entry entry : map.entrySet()) {
          BlockStateType blockStateType =
              BlockStateType.getByName(((IBlockState) entry.getKey()).f());
          if (blockStateType != null) {
            hashMap.put(blockStateType, ((Comparable) entry.getValue()).toString());
          }
        }
      }
    } catch (Exception exception) {
      try {
        CraftBlockData craftData = (CraftBlockData) block.getBlockData();
        IBlockData nmsState = craftData.getState();
        ImmutableMap immutableMap =
            (ImmutableMap) nmsState.getClass().getMethod("getStateMap").invoke(nmsState);

        for (Entry stateEntry : immutableMap.entrySet()) {
          BlockStateType stateType =
              BlockStateType.getByName(((IBlockState) stateEntry.getKey()).f());
          if (stateType != null) {
            hashMap.put(stateType, ((Comparable) stateEntry.getValue()).toString());
          }
        }
      } catch (Throwable throwable) {
        exception.printStackTrace();
        throwable.printStackTrace();
      }
    }

    return hashMap;
  }

  @Override
  public CMIHitBox getBlockHitBox(CMIHitBox cmiHitBox) {
    try {
      Block block = cmiHitBox.getBlock();
      if (block == null) {
        return cmiHitBox;
      }

      BlockPosition value = new BlockPosition(block.getX(), block.getY(), block.getZ());
      WorldServer level = ((CraftWorld) block.getWorld()).getHandle();
      ChunkSnapshot chunkSnapshot =
          block
              .getWorld()
              .getEmptyChunkSnapshot(block.getX() >> 4, block.getZ() >> 4, false, false);
      int localX = block.getX() % 16;
      localX = localX < 0 ? 16 + localX : localX;
      int localZ = block.getZ() % 16;
      localZ = localZ < 0 ? 16 + localZ : localZ;
      if (CMIMaterial.isAir(chunkSnapshot.getBlockType(localX, block.getY(), localZ))) {
        cmiHitBox.setMin(new Vector(1, 1, 1));
        cmiHitBox.setMax(new Vector(0, 0, 0));
      } else {
        IBlockData blockState = level.a_(value);
        VoxelShape shape = blockState.k(level, value);
        if (!shape.b()) {
          AxisAlignedBB bounds = shape.a();
          cmiHitBox.setMin(
              new Vector(bounds.a(EnumAxis.a), bounds.a(EnumAxis.b), bounds.a(EnumAxis.c)));
          cmiHitBox.setMax(
              new Vector(bounds.b(EnumAxis.a), bounds.b(EnumAxis.b), bounds.b(EnumAxis.c)));
        } else {
          cmiHitBox.setMin(new Vector(1, 1, 1));
          cmiHitBox.setMax(new Vector(0, 0, 0));
        }
      }
    } catch (NoSuchMethodError noSuchMethodError) {
    }

    return cmiHitBox;
  }

  @Override
  public CMIHitBox getEntityHitBox(CMIHitBox cmiHitBox) {
    try {
      Entity entity = cmiHitBox.getEntity();
      if (entity == null) {
        cmiHitBox.setMin(new Vector(1, 1, 1));
        cmiHitBox.setMax(new Vector(0, 0, 0));
        return cmiHitBox;
      }

      AxisAlignedBB value = ((CraftEntity) entity).getHandle().cw();
      cmiHitBox.setMin(new Vector(value.a, value.b, value.c));
      cmiHitBox.setMax(new Vector(value.d, value.e, value.f));
    } catch (NoSuchMethodError noSuchMethodError) {
    }

    return cmiHitBox;
  }

  @Override
  public Block getBlockLookingAt(Player player, int value, boolean includeFluids) {
    if (player == null) {
      return null;
    }

    if (value < 1) {
      value = 1;
    }

    if (value > 120) {
      value = 120;
    }

    Location location = player.getLocation().clone().add(0.0, player.getEyeHeight(), 0.0);
    Vector vector = location.getDirection().normalize().multiply(value);
    CraftWorld craftWorld = (CraftWorld) player.getWorld();
    RayTraceResult rayTraceResult =
        craftWorld.rayTraceBlocks(
            location,
            vector,
            value,
            includeFluids ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER);
    Block block = null;
    if (rayTraceResult == null) {
      block = player.getTargetBlock(null, value);
    } else {
      block = rayTraceResult.getHitBlock();
    }

    return block;
  }

  @Override
  public Block getBlockLookingAt(World world, Vector start, Vector end, boolean includeFluids) {
    try {
      CraftWorld value = (CraftWorld) world;
      RayTrace clipContext =
          new RayTrace(
              new Vec3D(start.getX(), start.getY(), start.getZ()),
              new Vec3D(end.getX(), end.getY(), end.getZ()),
              BlockCollisionOption.a,
              includeFluids ? FluidCollisionOption.c : FluidCollisionOption.a,
              null);
      MovingObjectPositionBlock hitResult = value.getHandle().a(clipContext);
      RayTraceResult rayTraceResult = CraftRayTraceResult.fromNMS(value, hitResult);
      return rayTraceResult == null ? null : rayTraceResult.getHitBlock();
    } catch (Throwable throwable) {
      if (!this.informed) {
        throwable.printStackTrace();
        this.informed = true;
      }

      return null;
    }
  }

  public Object createFakeEntityForTablist(Player player, String name) {
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
    WorldServer level = (WorldServer) value.F().iterator().next();
    GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes(name.getBytes()), name);
    EntityPlayer entityPlayer = new EntityPlayer(value, level, gameProfile);
    EntityPlayer playerHandle = entityPlayer.getBukkitEntity().getHandle();
    playerHandle.e(player.getEntityId());

    try {
      entityPlayer.listName =
          new ChatComponentText(CMIChatColor.translate(cmiUser.getDisplayName()));
    } catch (IllegalArgumentException | SecurityException value5) {
      value5.printStackTrace();
    }

    return entityPlayer;
  }

  public void sendPacketsForTablistSort(Player player, Player otherPlayer, Object data) {
    PacketPlayOutPlayerInfo value =
        new PacketPlayOutPlayerInfo(
            EnumPlayerInfoAction.e, new EntityPlayer[] {(EntityPlayer) data});
    ((CraftPlayer) player).getHandle().b.a(value);
    if (player.canSee(otherPlayer)) {
      value =
          new PacketPlayOutPlayerInfo(
              EnumPlayerInfoAction.a, new EntityPlayer[] {(EntityPlayer) data});
      ((CraftPlayer) player).getHandle().b.a(value);
    }
  }
}
