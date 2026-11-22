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
import java.lang.reflect.InvocationTargetException;
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
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftRayTraceResult;
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

public class v1_17_R1 implements NMS {
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
      WorldServer worldServer = (WorldServer) value.getWorlds().iterator().next();
      EntityPlayer entityPlayer = new EntityPlayer(value, worldServer, gameProfile);
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
      WorldServer worldServer = (WorldServer) value.getWorlds().iterator().next();
      EntityPlayer entityPlayer = new EntityPlayer(value, worldServer, gameProfile);
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

      WorldServer worldServer = value.getWorldServer();
      if (worldServer == null) {
        return;
      }

      worldServer.a(value, RemovalReason.c);
      value.getAdvancementData().a();
      DedicatedServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
      PlayerList playerList = minecraftServer.getPlayerList();
      playerList.j.remove(value);
      ((CraftServer) Bukkit.getServer())
          .getScoreboardManager()
          .removePlayer(value.getBukkitEntity());
    } catch (Exception | Error value5) {
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

  private static NBTTagCompound setDimension(NBTTagCompound value, int dimensionId) {
    value.setInt("Dimension", dimensionId);
    return value;
  }

  private static NBTTagCompound setLocationWorld(
      NBTTagCompound value, long longValue, long worldUuidMost) {
    value.setLong("WorldUUIDLeast", longValue);
    value.setLong("WorldUUIDMost", worldUuidMost);
    return value;
  }

  public static String getName(NBTTagCompound value) {
    NBTTagCompound bukkit = value.getCompound("bukkit");
    return bukkit.getString("lastKnownName");
  }

  public static World getLocationWorld(NBTTagCompound value) {
    return Bukkit.getWorld(new UUID(getUUIDWorldMost(value), getUUIDWorldLeast(value)));
  }

  private static long getUUIDWorldMost(NBTTagCompound value) {
    return value.getLong("WorldUUIDMost");
  }

  private static long getUUIDWorldLeast(NBTTagCompound value) {
    return value.getLong("WorldUUIDLeast");
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
    value.set("Pos", posList);
    return value;
  }

  public static NBTTagCompound setLocationView(NBTTagCompound value, float yaw, float pitch) {
    NBTTagList rotationList = new NBTTagList();
    rotationList.add(NBTTagFloat.a(yaw));
    rotationList.add(NBTTagFloat.a(pitch));
    value.set("Rotation", rotationList);
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
      value.remove("ActiveEffects");
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
      NBTTagList effects = value.getList("ActiveEffects", 10);
      boolean flag = false;

      for (int i = 0; i < effects.size(); i++) {
        NBTTagCompound effect = effects.getCompound(i);
        if (effect.getByte("Id") == potionEffect.getType().getId()) {
          flag = true;
          effect.setByte("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
          effect.setByte("Amplifier", (byte) potionEffect.getAmplifier());
          effect.setInt("Duration", potionEffect.getDuration());
          effect.setByte("ShowParticles", (byte) (potionEffect.hasParticles() ? 1 : 0));
          effects.a(i, effect);
          break;
        }
      }

      if (!flag) {
        NBTTagCompound newEffect = new NBTTagCompound();
        newEffect.setByte("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
        newEffect.setByte("Amplifier", (byte) potionEffect.getAmplifier());
        newEffect.setInt("Duration", potionEffect.getDuration());
        newEffect.setByte("Id", (byte) potionEffect.getType().getId());
        newEffect.setByte("ShowParticles", (byte) (potionEffect.hasParticles() ? 1 : 0));
        effects.add(newEffect);
      }

      value.set("ActiveEffects", effects);
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
        .sendPacket(new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle()));
  }

  @Override
  public void unloadChunk(Chunk chunk, Player player) {
    ((CraftPlayer) player)
        .getHandle()
        .b
        .sendPacket(new PacketPlayOutUnloadChunk(chunk.getX(), chunk.getZ()));
  }

  public ItemStack addAttributes(List<Attribute> list, ItemStack itemStack) {
    if (list.isEmpty()) {
      return itemStack;
    }

    net.minecraft.world.item.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value != null) {
      NBTTagCompound tag = null;
      if (value.hasTag()) {
        tag = value.getTag();
      }

      if (tag == null) {
        tag = new NBTTagCompound();
        value.setTag(tag);
        tag = value.getTag();
      }

      NBTTagList attributeModifiers = tag.getList("AttributeModifiers", 10);

      for (Attribute attribute : list) {
        NBTTagCompound modifierTag = new NBTTagCompound();
        modifierTag.set("AttributeName", NBTTagString.a(attribute.getType().getFullName()));
        modifierTag.set("Name", NBTTagString.a(attribute.getType().getName()));
        modifierTag.set("Amount", NBTTagDouble.a(attribute.getMod()));
        modifierTag.set("Operation", NBTTagInt.a(attribute.getType().getAction()));
        modifierTag.set("Slot", NBTTagString.a(attribute.getSlot().getName()));
        UUID uuid = UUID.randomUUID();
        modifierTag.set("UUIDLeast", NBTTagInt.a((int) uuid.getLeastSignificantBits()));
        modifierTag.set("UUIDMost", NBTTagInt.a((int) uuid.getMostSignificantBits()));
        attributeModifiers.add(modifierTag);
      }

      tag.set("AttributeModifiers", attributeModifiers);
      value.setTag(tag);
      itemStack = CraftItemStack.asBukkitCopy(value);
    }

    return itemStack;
  }

  @Override
  public void updateCollisions(Player player, boolean collidable, boolean pushable) {}

  public static void sendPacket(Player player, PacketPlayOutScoreboardTeam value) {
    ((CraftPlayer) player).getHandle().b.sendPacket(value);
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
        return !value.hasKey("Spigot.ticksLived") ? 0 : value.getInt("Spigot.ticksLived");
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

    value.setHealth(1.0F);
    value.removeAllEffects();
    if (CMI.getInstance().getTotemManager().getRegenerationDuration() > 0) {
      value.addEffect(
          new MobEffect(
              MobEffects.j, CMI.getInstance().getTotemManager().getRegenerationDuration() * 20, 1));
    }

    if (CMI.getInstance().getTotemManager().getFireResistanceDuration() > 0) {
      value.addEffect(
          new MobEffect(
              MobEffects.l,
              CMI.getInstance().getTotemManager().getFireResistanceDuration() * 20,
              1));
    }

    if (CMI.getInstance().getTotemManager().getAbsorbtionDuration() > 0) {
      value.addEffect(
          new MobEffect(
              MobEffects.v, CMI.getInstance().getTotemManager().getAbsorbtionDuration() * 20, 1));
    }

    value.t.broadcastEntityEffect(value, (byte) 35);
  }

  @Override
  public void setEntityTag(Entity entity, String string, Object object) {
    NBTTagCompound value = new NBTTagCompound();
    net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
    NBTTagCompound savedTag = nmsEntity.save(value);
    if (object instanceof String) {
      savedTag.setString(string, (String) object);
    } else if (object instanceof Short) {
      savedTag.setShort(string, (Short) object);
    } else if (object instanceof Byte) {
      savedTag.setByte(string, (Byte) object);
    }

    nmsEntity.load(value);
  }

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    net.minecraft.world.item.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      String string = enchantment.getKey().getKey();
      NBTTagCompound tag = value.getTag();
      boolean flag = false;
      if (tag != null && tag.hasKey("ench")) {
        NBTTagList enchList = tag.getList("ench", 10);

        for (int i = 0; i < enchList.size(); i++) {
          if (enchList.getCompound(i).getString("id").equalsIgnoreCase(string)) {
            enchList.remove(i);
            flag = true;
            break;
          }
        }

        if (flag) {
          tag.set("ench", enchList);
        }

        value.setTag(tag);
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
  public ItemStack setNBTList(ItemStack itemStack, String string, List<String> list) {
    net.minecraft.world.item.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      NBTTagCompound tag = value.getTag();
      if (tag == null) {
        tag = new NBTTagCompound();
      }

      NBTTagList tagList = new NBTTagList();
      if (list != null) {
        for (String item : list) {
          tagList.add(NBTTagString.a(item));
        }
      }

      if (list == null) {
        tag.remove(string);
      } else {
        tag.set(string, tagList);
      }

      value.setTag(tag);
      return CraftItemStack.asBukkitCopy(value);
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean changeGodMode(Player player, boolean flag) {
    if (player == null) {
      return false;
    }

    player.setInvulnerable(flag);
    return true;
  }

  @Override
  public boolean getGodMode(Player player) {
    return player.isInvulnerable();
  }

  @Override
  public void updateExpBar(Player player) {
    CraftPlayer value = (CraftPlayer) player;
    value.getHandle().cC = -1;
  }

  @Override
  public Command getCommand(String string) {
    return ((CraftServer) Bukkit.getServer()).getCommandMap().getCommand(string);
  }

  private static EntityArmorStand createStand(Location location, String string, boolean flag) {
    double x = location.getX();
    double z = location.getZ();
    double y = location.getY();
    if (flag) {
      x += 0.05;
      z -= 0.2;
      y -= 0.3;
    } else {
      y -= 0.45;
    }

    EntityArmorStand value =
        new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), x, y, z);
    value.setInvisible(true);
    value.setArms(false);
    value.setMarker(true);
    value.setNoGravity(true);
    value.setBasePlate(true);
    value.setSmall(flag);
    if (flag) {
      value.setRightArmPose(new Vector3f(-15.0F, -45.0F, 0.0F));
    }

    if (string != null && !string.isEmpty()) {
      IChatBaseComponent customName =
          CraftChatMessage.fromString(CMIChatColor.translate(string))[0];
      value.setCustomName(customName);
      if (!CMIChatColor.stripColor(string).isEmpty() && !string.replace(" ", "").isEmpty()) {
        value.setCustomNameVisible(true);
      } else {
        value.setCustomNameVisible(false);
      }
    } else {
      value.setCustomNameVisible(false);
    }

    return value;
  }

  @Override
  public CMIDataWatcher spawnHologram(
      Player player, Location location, String string, ItemStack itemStack) {
    EntityArmorStand value = createStand(location, string, itemStack != null);
    CraftPlayer craftPlayer = (CraftPlayer) player;

    try {
      craftPlayer.getHandle().b.sendPacket(new PacketPlayOutSpawnEntity(value, 78));

      try {
        craftPlayer
            .getHandle()
            .b
            .sendPacket(
                new PacketPlayOutEntityMetadata(value.getId(), value.getDataWatcher(), false));
        if (itemStack != null) {
          var arrayList = Lists.newArrayList();
          arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
          PacketPlayOutEntityEquipment equipmentPacket =
              new PacketPlayOutEntityEquipment(value.getId(), arrayList);
          craftPlayer.getHandle().b.sendPacket(equipmentPacket);
        }

        return new CMIDataWatcher(
            value.getId(),
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
  public FakeInfo spawnClickable(Player player, Location location, Object object, boolean flag) {
    try {
      if (flag) {
        EntitySlime value =
            new EntitySlime(EntityTypes.X, ((CraftWorld) location.getWorld()).getHandle());
        value.setInvisible(true);
        value.setNoGravity(true);
        value.setNoAI(true);
        value.setSize(2, true);
        value.setLocation(
            location.getX(), location.getY() - 0.45, location.getZ() - 0.2, 0.0F, 0.0F);
        value.setCustomNameVisible(false);
        value.setSilent(true);
        PacketPlayOutSpawnEntityLiving magmaCubeSpawnPacket =
            new PacketPlayOutSpawnEntityLiving(value);
        ((CraftPlayer) player).getHandle().b.sendPacket(magmaCubeSpawnPacket);
        ((CraftPlayer) player)
            .getHandle()
            .b
            .sendPacket(
                new PacketPlayOutEntityMetadata(value.getId(), value.getDataWatcher(), false));
        FakeInfo fakeInfo = new FakeInfo().setObj(object).setPlayer(player).setId(value.getId());
        PacketHandler.fakeEntities.put(value.getId(), fakeInfo);
        return fakeInfo;
      }

      EntityArmorStand armorStand =
          new EntityArmorStand(
              ((CraftWorld) location.getWorld()).getHandle(),
              location.getX(),
              location.getY() - 0.35,
              location.getZ());
      armorStand.setInvisible(true);
      armorStand.setArms(false);
      armorStand.setNoGravity(true);
      armorStand.setBasePlate(false);
      if (object instanceof CMIHologram) {
        if (((CMIHologram) object).getHeight() > 1.7) {
          armorStand.setSmall(false);
          armorStand.setLocation(
              location.getX(), location.getY() - 0.75, location.getZ(), 0.0F, 0.0F);
        } else {
          armorStand.setSmall(true);
        }
      } else {
        armorStand.setSmall(true);
      }

      armorStand.setCustomNameVisible(false);
      PacketPlayOutSpawnEntityLiving armorStandSpawnPacket =
          new PacketPlayOutSpawnEntityLiving(armorStand);
      ((CraftPlayer) player).getHandle().b.sendPacket(armorStandSpawnPacket);
      ((CraftPlayer) player)
          .getHandle()
          .b
          .sendPacket(
              new PacketPlayOutEntityMetadata(
                  armorStand.getId(), armorStand.getDataWatcher(), false));
      FakeInfo armorStandInfo =
          new FakeInfo().setObj(object).setPlayer(player).setId(armorStand.getId());
      PacketHandler.fakeEntities.put(armorStand.getId(), armorStandInfo);
      return armorStandInfo;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public void removeHologram(Player player, Integer integer) {
    if (integer != null) {
      CraftPlayer value = (CraftPlayer) player;

      try {
        Packet destroyPacket = null;
        if (Version.isCurrentSubEqual(0)) {
          try {
            destroyPacket =
                PacketPlayOutEntityDestroy.class.getConstructor(int.class).newInstance(integer);
          } catch (Throwable throwable) {
            destroyPacket = new PacketPlayOutEntityDestroy(new int[] {integer});
          }
        } else {
          destroyPacket = new PacketPlayOutEntityDestroy(new int[] {integer});
        }

        if (destroyPacket != null) {
          value.getHandle().b.sendPacket(destroyPacket);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  @Override
  public void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String string, ItemStack itemStack) {
    if (cmiDataWatcher != null && cmiDataWatcher.getStand() instanceof EntityArmorStand) {
      EntityArmorStand value = (EntityArmorStand) cmiDataWatcher.getStand();
      if (string == null || string.isEmpty()) {
        value.setCustomNameVisible(false);
      } else if (!CMIChatColor.stripColor(string).isEmpty() && !string.replace(" ", "").isEmpty()) {
        value.setCustomNameVisible(true);
      } else {
        value.setCustomNameVisible(false);
      }

      IChatBaseComponent chatComponent = CraftChatMessage.fromString(string)[0];
      value.setCustomName(chatComponent);
      PacketPlayOutEntityMetadata metadataPacket =
          new PacketPlayOutEntityMetadata(cmiDataWatcher.getId(), value.getDataWatcher(), true);
      CraftPlayer craftPlayer = (CraftPlayer) player;

      try {
        craftPlayer.getHandle().b.sendPacket(metadataPacket);
        var arrayList = Lists.newArrayList();
        arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        PacketPlayOutEntityEquipment equipmentPacket =
            new PacketPlayOutEntityEquipment(cmiDataWatcher.getId(), arrayList);
        craftPlayer.getHandle().b.sendPacket(equipmentPacket);
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
      Player player, Location location, ItemStack itemStack, boolean flag) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    EntityItem itemEntity =
        new EntityItem(
            ((CraftWorld) location.getWorld()).getHandle(),
            location.getX(),
            location.getY(),
            location.getZ(),
            CraftItemStack.asNMSCopy(itemStack));
    itemEntity.setPosition(location.getX(), location.getY(), location.getZ());
    itemEntity.setNoGravity(true);
    if (flag) {
      itemEntity.setGlowingTag(true);
    }

    PacketPlayOutEntityVelocity velocityPacket =
        new PacketPlayOutEntityVelocity(itemEntity.getId(), new Vec3D(0.0, 0.0, 0.0));
    PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(itemEntity);
    value.b.sendPacket(spawnPacket);
    PacketPlayOutEntityMetadata metadataPacket =
        new PacketPlayOutEntityMetadata(itemEntity.getId(), itemEntity.getDataWatcher(), true);
    value.b.sendPacket(metadataPacket);
    value.b.sendPacket(velocityPacket);
    return new CMIDataWatcher(
        itemEntity.getId(), itemEntity.getDataWatcher(), CMIHoloLineType.icon);
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
        DedicatedServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile =
            new GameProfile(player.getUniqueId(), CMIChatColor.translate(cmiUser.getTagName()));
        gameProfile.getProperties().putAll(value.getHandle().getProfile().getProperties());
        WorldServer renamedWorld =
            minecraftServer.getWorldServer(value.getHandle().getSpawnDimension());
        renamedPlayer = new EntityPlayer(minecraftServer, renamedWorld, gameProfile);
        net.minecraft.world.entity.Entity renamedEntity =
            renamedPlayer.getBukkitEntity().getHandle();
        renamedEntity.e(player.getEntityId());
      }

      EntityPlayer entityPlayer = value.getHandle();
      int entityId = player.getEntityId();
      PacketPlayOutPlayerInfo removePacket =
          new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.d, new EntityPlayer[] {entityPlayer});
      Packet addPacket = null;
      if (renamedPlayer != null) {
        addPacket =
            new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.a, new EntityPlayer[] {renamedPlayer});
      } else {
        addPacket =
            new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.a, new EntityPlayer[] {entityPlayer});
      }

      PacketPlayOutEntityDestroy destroyPacket = null;
      if (Version.isCurrentSubEqual(0)) {
        try {
          destroyPacket =
              PacketPlayOutEntityDestroy.class.getConstructor(int.class).newInstance(entityId);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      } else {
        destroyPacket = new PacketPlayOutEntityDestroy(new int[] {entityId});
      }

      PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(entityPlayer);
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
          new PacketPlayOutEntityMetadata(entityId, value.getHandle().getDataWatcher(), true);
      PacketPlayOutAbilities abilitiesPacket =
          new PacketPlayOutAbilities(value.getHandle().getAbilities());
      PacketPlayOutMount mountPacket = null;
      if (player.getVehicle() != null) {
        mountPacket = new PacketPlayOutMount(((CraftEntity) player.getVehicle()).getHandle());
      }

      CraftWorld craftWorld = (CraftWorld) player.getWorld();
      WorldServer worldServer = craftWorld.getHandle();
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
          connection.sendPacket(removePacket);
          connection.sendPacket(addPacket);
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
          DimensionManager dimensionManager = worldServer.getDimensionManager();
          PacketPlayOutRespawn respawnPacket =
              new PacketPlayOutRespawn(
                  dimensionManager,
                  worldServer.getDimensionKey(),
                  0L,
                  gameMode,
                  gameMode,
                  false,
                  false,
                  false);
          connection.sendPacket(respawnPacket);
          connection.sendPacket(positionPacket);

          for (MobEffect mobEffect : entityPlayer.getEffects()) {
            connection.sendPacket(new PacketPlayOutEntityEffect(entityId, mobEffect));
          }

          PacketPlayOutUpdateHealth healthPacket =
              new PacketPlayOutUpdateHealth(
                  (float) player.getHealth(), player.getFoodLevel(), player.getSaturation());
          connection.sendPacket(healthPacket);
          if (mountPacket != null) {
            connection.sendPacket(mountPacket);
          }

          connection.sendPacket(metadataPacket);
          connection.sendPacket(abilitiesPacket);
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

                      v1_17_R1.this.updateExpBar(player);
                      if (player.isOp()) {
                        ((CraftServer) Bukkit.getServer()).getHandle().addOp(value.getProfile());
                        value.recalculatePermissions();
                      }

                      player.getInventory().setHeldItemSlot(heldSlot);
                    }
                  });
        } else if (otherPlayer.canSee(player)) {
          connection.sendPacket(removePacket);
          connection.sendPacket(addPacket);
          if (CMI.getInstance()
                      .getUtilManager()
                      .getDistance(otherPlayer.getLocation(), player.getLocation())
                  < CMI.getInstance().getUtilManager().getPlayerTrackingRange(player.getWorld())
              && destroyPacket != null) {
            connection.sendPacket(destroyPacket);
            connection.sendPacket(spawnPacket);
          }

          if (mountPacket != null) {
            connection.sendPacket(mountPacket);
          }

          connection.sendPacket(metadataPacket);
          Bukkit.getServer()
              .getScheduler()
              .scheduleSyncDelayedTask(
                  CMI.getInstance(),
                  new Runnable() {
                    @Override
                    public void run() {
                      connection.sendPacket(equipmentPacket);
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
              jsonObject,
              new LootDeserializationContext(
                  value, MinecraftServer.getServer().getLootPredicateManager()));
      if (serializedAdvancement != null) {
        MinecraftServer.getServer()
            .getAdvancementData()
            .c
            .a(Maps.newHashMap(Collections.singletonMap(value, serializedAdvancement)));
      }
    }
  }

  public void removeAdvancement(CMIAdvancement value) {
    Bukkit.getUnsafe().removeAdvancement(value.getId());

    try {
      DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
      Advancements advancements = dedicatedServer.getAdvancementData().c;

      for (Entry entry : advancements.b.entrySet()) {
        if (((Advancement) entry.getValue())
            .getName()
            .getKey()
            .equals(value.getId().getKey().toLowerCase())) {
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
                craftPlayer.getHandle().b.sendPacket(value);
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
    if (value.getHealth() == 0.0F || value.isRemoved()) {
      return false;
    }

    if (value.b == null) {
      return false;
    }

    if (value.isVehicle()) {
      return false;
    }

    value.stopRiding();
    WorldServer targetWorld = ((CraftWorld) location.getWorld()).getHandle();
    World world = location.getWorld();
    if (world == null) {
      targetWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
    } else {
      targetWorld = ((CraftWorld) world).getHandle();
    }

    if (((CraftPlayer) player).getHandle().bV != ((CraftPlayer) player).getHandle().bU) {
      ((CraftPlayer) player).getHandle().closeInventory();
    }

    if (((CraftWorld) player.getLocation().getWorld()).getHandle() == targetWorld) {
      value.b.teleport(location);
    } else {
      ((CraftServer) Bukkit.getServer())
          .getHandle()
          .moveToWorld(value, targetWorld, true, location, true);
    }

    return true;
  }

  public HashMap<BlockStateType, String> getBlockStates(Block block) {
    HashMap<BlockStateType, String> hashMap = new HashMap();

    try {
      CraftBlockData value = (CraftBlockData) block.getBlockData();
      IBlockData blockData = value.getState();
      ImmutableMap immutableMap =
          (ImmutableMap) blockData.getClass().getMethod("getStateMap").invoke(blockData);

      for (Entry entry : immutableMap.entrySet()) {
        BlockStateType blockStateType =
            BlockStateType.getByName(((IBlockState) entry.getKey()).getName());
        if (blockStateType != null) {
          hashMap.put(blockStateType, ((Comparable) entry.getValue()).toString());
        }
      }
    } catch (Exception exception) {
      try {
        CraftBlockData fallbackCraftData = (CraftBlockData) block.getBlockData();
        IBlockData fallbackBlockData = fallbackCraftData.getState();
        Map map = (Map) fallbackBlockData.getClass().getMethod("b").invoke(fallbackBlockData);

        for (Entry fallbackEntry : map.entrySet()) {
          BlockStateType fallbackStateType =
              BlockStateType.getByName(((IBlockState) fallbackEntry.getKey()).getName());
          if (fallbackStateType != null) {
            hashMap.put(fallbackStateType, ((Comparable) fallbackEntry.getValue()).toString());
          }
        }
      } catch (IllegalAccessException
          | IllegalArgumentException
          | InvocationTargetException
          | NoSuchMethodException
          | SecurityException value5) {
        exception.printStackTrace();
        value5.printStackTrace();
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
      WorldServer worldServer = ((CraftWorld) block.getWorld()).getHandle();
      ChunkSnapshot chunkSnapshot =
          block
              .getWorld()
              .getEmptyChunkSnapshot(block.getX() >> 4, block.getZ() >> 4, false, false);
      int chunkX = block.getX() % 16;
      chunkX = chunkX < 0 ? 16 + chunkX : chunkX;
      int chunkZ = block.getZ() % 16;
      chunkZ = chunkZ < 0 ? 16 + chunkZ : chunkZ;
      if (CMIMaterial.isAir(chunkSnapshot.getBlockType(chunkX, block.getY(), chunkZ))) {
        cmiHitBox.setMin(new Vector(1, 1, 1));
        cmiHitBox.setMax(new Vector(0, 0, 0));
      } else {
        try {
          IBlockData blockData = worldServer.getType(value);
          VoxelShape voxelShape = blockData.getCollisionShape(worldServer, value);
          if (!voxelShape.isEmpty()) {
            AxisAlignedBB box = voxelShape.getBoundingBox();
            cmiHitBox.setMin(new Vector(box.a(EnumAxis.a), box.a(EnumAxis.b), box.a(EnumAxis.c)));
            cmiHitBox.setMax(new Vector(box.b(EnumAxis.a), box.b(EnumAxis.b), box.b(EnumAxis.c)));
          } else {
            cmiHitBox.setMin(new Vector(1, 1, 1));
            cmiHitBox.setMax(new Vector(0, 0, 0));
          }
        } catch (Exception exception) {
          try {
            IBlockData fallbackBlockData = worldServer.getType(value);
            Object object =
                fallbackBlockData
                    .getClass()
                    .getMethod("g", worldServer.getClass(), value.getClass())
                    .invoke(fallbackBlockData, worldServer, value);
            AxisAlignedBB fallbackBox =
                (AxisAlignedBB) object.getClass().getMethod("a").invoke(object);
            cmiHitBox.setMin(
                new Vector(
                    fallbackBox.a(EnumAxis.a),
                    fallbackBox.a(EnumAxis.b),
                    fallbackBox.a(EnumAxis.c)));
            cmiHitBox.setMax(
                new Vector(
                    fallbackBox.b(EnumAxis.a),
                    fallbackBox.b(EnumAxis.b),
                    fallbackBox.b(EnumAxis.c)));
          } catch (Exception exception2) {
            exception2.printStackTrace();
            exception.printStackTrace();
          }
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

      AxisAlignedBB value = ((CraftEntity) entity).getHandle().getBoundingBox();
      cmiHitBox.setMin(new Vector(value.a, value.b, value.c));
      cmiHitBox.setMax(new Vector(value.d, value.e, value.f));
    } catch (NoSuchMethodError noSuchMethodError) {
    }

    return cmiHitBox;
  }

  @Override
  public Block getBlockLookingAt(Player player, int value, boolean flag) {
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
            location, vector, value, flag ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER);
    Block block = null;
    if (rayTraceResult == null) {
      block = player.getTargetBlock(null, value);
    } else {
      block = rayTraceResult.getHitBlock();
    }

    return block;
  }

  @Override
  public Block getBlockLookingAt(World world, Vector vector, Vector end, boolean flag) {
    try {
      CraftWorld value = (CraftWorld) world;
      RayTrace trace =
          new RayTrace(
              new Vec3D(vector.getX(), vector.getY(), vector.getZ()),
              new Vec3D(end.getX(), end.getY(), end.getZ()),
              BlockCollisionOption.a,
              flag ? FluidCollisionOption.c : FluidCollisionOption.a,
              null);
      MovingObjectPositionBlock hitPosition = value.getHandle().rayTrace(trace);
      RayTraceResult rayTraceResult = CraftRayTraceResult.fromNMS(value, hitPosition);
      return rayTraceResult == null ? null : rayTraceResult.getHitBlock();
    } catch (Throwable throwable) {
      if (!this.informed) {
        throwable.printStackTrace();
        this.informed = true;
      }

      return null;
    }
  }

  public Object createFakeEntityForTablist(Player player, String string) {
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
    WorldServer worldServer = (WorldServer) value.getWorlds().iterator().next();
    GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes(string.getBytes()), string);
    EntityPlayer entityPlayer = new EntityPlayer(value, worldServer, gameProfile);
    EntityPlayer nmsPlayer = entityPlayer.getBukkitEntity().getHandle();
    nmsPlayer.e(player.getEntityId());

    try {
      entityPlayer.listName =
          new ChatComponentText(CMIChatColor.translate(cmiUser.getDisplayName()));
    } catch (IllegalArgumentException | SecurityException value5) {
      value5.printStackTrace();
    }

    return entityPlayer;
  }

  public void sendPacketsForTablistSort(Player player, Player target, Object object) {
    PacketPlayOutPlayerInfo value =
        new PacketPlayOutPlayerInfo(
            EnumPlayerInfoAction.e, new EntityPlayer[] {(EntityPlayer) object});
    ((CraftPlayer) player).getHandle().b.sendPacket(value);
    if (player.canSee(target)) {
      value =
          new PacketPlayOutPlayerInfo(
              EnumPlayerInfoAction.a, new EntityPlayer[] {(EntityPlayer) object});
      ((CraftPlayer) player).getHandle().b.sendPacket(value);
    }
  }
}
