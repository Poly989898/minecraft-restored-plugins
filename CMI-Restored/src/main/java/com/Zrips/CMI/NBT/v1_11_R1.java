package com.Zrips.CMI.NBT;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Holograms.CMIDataWatcher;
import com.Zrips.CMI.Modules.Holograms.CMIHoloLineType;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import com.Zrips.CMI.Modules.Packets.PacketHandler;
import com.Zrips.CMI.utils.Util;
import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.minecraft.server.v1_11_R1.AxisAlignedBB;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.EntityArmorStand;
import net.minecraft.server.v1_11_R1.EntityItem;
import net.minecraft.server.v1_11_R1.EntityMagmaCube;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EnumGamemode;
import net.minecraft.server.v1_11_R1.EnumItemSlot;
import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.EnumProtocolDirection;
import net.minecraft.server.v1_11_R1.Items;
import net.minecraft.server.v1_11_R1.MinecraftServer;
import net.minecraft.server.v1_11_R1.MobEffect;
import net.minecraft.server.v1_11_R1.MobEffects;
import net.minecraft.server.v1_11_R1.MovingObjectPosition;
import net.minecraft.server.v1_11_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagDouble;
import net.minecraft.server.v1_11_R1.NBTTagFloat;
import net.minecraft.server.v1_11_R1.NBTTagList;
import net.minecraft.server.v1_11_R1.NBTTagString;
import net.minecraft.server.v1_11_R1.NetworkManager;
import net.minecraft.server.v1_11_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_11_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_11_R1.PacketPlayOutMount;
import net.minecraft.server.v1_11_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_11_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_11_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_11_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_11_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_11_R1.PacketPlayOutUpdateHealth;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_11_R1.PlayerConnection;
import net.minecraft.server.v1_11_R1.PlayerInteractManager;
import net.minecraft.server.v1_11_R1.StatisticList;
import net.minecraft.server.v1_11_R1.Vec3D;
import net.minecraft.server.v1_11_R1.Vector3f;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_11_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class v1_11_R1 implements NMS {
  @Override
  public synchronized Player getPlayer(CMIUser cmiUser) {
    try {
      GameProfile gameProfile = new GameProfile(cmiUser.getUniqueId(), cmiUser.getName());
      MinecraftServer value = ((CraftServer) Bukkit.getServer()).getServer();
      EntityPlayer entityPlayer =
          new EntityPlayer(
              value,
              value.getWorldServer(0),
              gameProfile,
              new PlayerInteractManager(value.getWorldServer(0)));
      CraftPlayer craftPlayer = entityPlayer.getBukkitEntity();
      if (craftPlayer != null) {
        craftPlayer.loadData();
        return craftPlayer;
      } else {
        return null;
      }
    } catch (Exception exception) {
      return null;
    }
  }

  @Override
  public synchronized Player createFakeOperator() {
    try {
      GameProfile gameProfile =
          new GameProfile(
              CMI.getInstance().getPlayerManager().getFakeUserUUID(),
              CMI.getInstance().getPlayerManager().getFakeUserName());
      MinecraftServer value = ((CraftServer) Bukkit.getServer()).getServer();
      EntityPlayer entityPlayer =
          new EntityPlayer(
              value,
              value.getWorldServer(0),
              gameProfile,
              new PlayerInteractManager(value.getWorldServer(0)));
      entityPlayer.playerConnection =
          new PlayerConnection(
              value, new NetworkManager(EnumProtocolDirection.SERVERBOUND), entityPlayer);
      entityPlayer.playerConnection.networkManager.l = new InetSocketAddress("127.0.0.1", 25566);
      CraftPlayer craftPlayer = entityPlayer.getBukkitEntity();
      craftPlayer.setOp(true);
      return craftPlayer;
    } catch (Exception exception) {
      return null;
    }
  }

  @Override
  public void unloadData(Player player) {}

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

  public NBTTagCompound getNBTTagC(Player player) {
    File file = Util.getFile(player);

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

  @Override
  public String getWorldName(File file) {
    NBTTagCompound value = this.getNBTTagC(file);
    if (value == null) {
      return null;
    }

    UUID uuid = new UUID(getUUIDWorldMost(value), getUUIDWorldLeast(value));
    return CMI.getInstance().getUtilManager().getWorldName(uuid);
  }

  public Location getMiscLocation(NBTTagCompound value) {
    World world = getLocationWorld(value);
    double[] numbers = this.getLocationPosition(value);
    Float[] floats = this.getLocationView(value);
    Location location = new Location(world, numbers[0], numbers[1], numbers[2]);
    location.setPitch(floats[0]);
    location.setYaw(floats[1]);
    return location;
  }

  public double[] getLocationPosition(NBTTagCompound value) {
    double[] numbers = new double[3];
    NBTTagList posList = value.getList("Pos", 6);
    numbers[0] = posList.e(0);
    numbers[1] = posList.e(1);
    numbers[2] = posList.e(2);
    return numbers;
  }

  public Float[] getLocationView(NBTTagCompound value) {
    Float[] floats = new Float[2];
    NBTTagList rotationList = value.getList("Rotation", 5);
    floats[0] = rotationList.f(0);
    floats[1] = rotationList.f(1);
    return floats;
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
    NBTTagCompound value = this.getNBTTagC(player);
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
    value = setUUIDWorldLeast(value, longValue);
    return setUUIDWorldMost(value, worldUuidMost);
  }

  private static NBTTagCompound setUUIDWorldLeast(NBTTagCompound value, long longValue) {
    value.setLong("WorldUUIDLeast", longValue);
    return value;
  }

  public static String getName(NBTTagCompound value) {
    NBTTagCompound bukkit = value.getCompound("bukkit");
    return bukkit.getString("lastKnownName");
  }

  private static NBTTagCompound setUUIDWorldMost(NBTTagCompound value, long longValue) {
    value.setLong("WorldUUIDMost", longValue);
    return value;
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
    posList.add(new NBTTagDouble(x));
    posList.add(new NBTTagDouble(y));
    posList.add(new NBTTagDouble(z));
    value.set("Pos", posList);
    return value;
  }

  public static NBTTagCompound setLocationView(NBTTagCompound value, float yaw, float pitch) {
    NBTTagList rotationList = new NBTTagList();
    rotationList.add(new NBTTagFloat(yaw));
    rotationList.add(new NBTTagFloat(pitch));
    value.set("Rotation", rotationList);
    return value;
  }

  public static void saveData(NBTTagCompound value, File file) {
    if (value != null) {
      try {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        NBTCompressedStreamTools.a(value, fileOutputStream);
        fileOutputStream.close();
      } catch (Exception exception) {
      }
    }
  }

  public void saveData(NBTTagCompound value, Player player) {
    if (value != null) {
      try {
        FileOutputStream fileOutputStream = new FileOutputStream(Util.getFile(player));
        NBTCompressedStreamTools.a(value, fileOutputStream);
        fileOutputStream.close();
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
        NBTTagCompound effect = effects.get(i);
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
  public void relightChunk(Chunk chunk) {
    ((CraftChunk) chunk).getHandle().initLighting();
    World world = chunk.getWorld();
    CraftWorld value = (CraftWorld) world;
    WorldServer worldServer = value.getHandle();
    int minX = chunk.getX() * 16;
    int minZ = chunk.getZ() * 16;

    for (int x = minX; x <= minX + 16; x++) {
      for (int z = minZ; z <= minZ + 16; z++) {
        int highestY = world.getHighestBlockYAt(x, z);

        for (int y = 0; y <= highestY; y++) {
          worldServer.w(new BlockPosition(x, y, z));
        }
      }
    }
  }

  @Override
  public void resendChunk(Chunk chunk, Player player) {
    ((CraftPlayer) player)
        .getHandle()
        .playerConnection
        .sendPacket(new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535));
  }

  @Override
  public void unloadChunk(Chunk chunk, Player player) {
    ((CraftPlayer) player)
        .getHandle()
        .playerConnection
        .sendPacket(new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 0));
  }

  @Override
  public void updateCollisions(Player player, boolean collidable, boolean pushable) {}

  public static void sendPacket(Player player, PacketPlayOutScoreboardTeam value) {
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(value);
  }

  public Inventory getShulkerInv(ItemStack itemStack) {
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
    return shulkerBox.getInventory();
  }

  public Inventory getShulkerInv(Block block) {
    if (!(block.getState() instanceof ShulkerBox)) {
      return null;
    }

    ShulkerBox shulkerBox = (ShulkerBox) block.getState();
    return shulkerBox.getInventory();
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
  public void showResurection(Player player) {
    this.showResurection(player, null);
  }

  public void showResurection(Player player, ItemStack itemStack) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    if (value instanceof EntityPlayer) {
      EntityPlayer entityPlayer = value;
      entityPlayer.b(StatisticList.b(Items.cY));
    }

    value.setHealth(1.0F);
    value.removeAllEffects();
    if (CMI.getInstance().getTotemManager().getRegenerationDuration() > 0) {
      value.addEffect(
          new MobEffect(
              MobEffects.REGENERATION,
              CMI.getInstance().getTotemManager().getRegenerationDuration() * 20,
              1));
    }

    if (CMI.getInstance().getTotemManager().getFireResistanceDuration() > 0) {
      value.addEffect(
          new MobEffect(
              MobEffects.FIRE_RESISTANCE,
              CMI.getInstance().getTotemManager().getFireResistanceDuration() * 20,
              1));
    }

    if (CMI.getInstance().getTotemManager().getAbsorbtionDuration() > 0) {
      value.addEffect(
          new MobEffect(
              MobEffects.ABSORBTION,
              CMI.getInstance().getTotemManager().getAbsorbtionDuration() * 20,
              1));
    }

    value.world.broadcastEntityEffect(value, (byte) 35);
  }

  @Override
  public void setEntityTag(Entity entity, String string, Object object) {}

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    net.minecraft.server.v1_11_R1.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      int enchantId = (Integer) enchantment.getClass().getMethod("getId").invoke(enchantment);
      NBTTagCompound tag = value.getTag();
      boolean flag = false;
      if (tag != null && tag.hasKey("ench")) {
        NBTTagList enchList = tag.getList("ench", 10);

        for (int i = 0; i < enchList.size(); i++) {
          if (enchList.get(i).getShort("id") == enchantId) {
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
    net.minecraft.server.v1_11_R1.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return itemStack;
    }

    try {
      NBTTagCompound tag = value.getTag();
      if (tag == null) {
        tag = new NBTTagCompound();
      }

      NBTTagList tagList = new NBTTagList();
      if (list != null) {
        for (String item : list) {
          tagList.add(new NBTTagString(item));
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
      return itemStack;
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
    value.getHandle().lastSentExp = -1;
  }

  @Override
  public Command getCommand(String string) {
    return ((CraftServer) Bukkit.getServer()).getCommandMap().getCommand(string);
  }

  private static EntityArmorStand createStand(Location location, String string, boolean flag) {
    double x = location.getX();
    double z = location.getZ();
    if (flag) {
      x += 0.35;
      z -= 0.1;
    }

    EntityArmorStand value =
        new EntityArmorStand(
            ((CraftWorld) location.getWorld()).getHandle(), x, location.getY() - 0.45, z);
    value.setInvisible(true);
    value.setArms(false);
    value.setMarker(true);
    value.setNoGravity(true);
    value.setBasePlate(false);
    value.setSmall(flag);
    if (flag) {
      value.setRightArmPose(new Vector3f(-15.0F, 45.0F, 0.0F));
    }

    if (string != null && !string.isEmpty()) {
      value.setCustomName(CMIChatColor.translateAlternateColorCodes(string));
      if (!CMIChatColor.stripColor(string).isEmpty() && !string.replace(" ", "").isEmpty()) {
        value.setCustomNameVisible(true);
      } else {
        value.setCustomNameVisible(false);
      }
    } else {
      value.setCustomNameVisible(false);
    }

    try {
      Field field = value.getClass().getDeclaredField("bA");
      field.setAccessible(true);
      field.set(value, 2039583);
    } catch (NoSuchFieldException
        | SecurityException
        | IllegalArgumentException
        | IllegalAccessException value2) {
      value2.printStackTrace();
    }

    return value;
  }

  @Override
  public CMIDataWatcher spawnHologram(
      Player player, Location location, String string, ItemStack itemStack) {
    EntityArmorStand value = createStand(location, string, itemStack != null);
    CraftPlayer craftPlayer = (CraftPlayer) player;

    try {
      craftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntity(value, 78));

      try {
        craftPlayer
            .getHandle()
            .playerConnection
            .sendPacket(
                new PacketPlayOutEntityMetadata(value.getId(), value.getDataWatcher(), false));
        if (itemStack != null) {
          PacketPlayOutEntityEquipment equipmentPacket =
              new PacketPlayOutEntityEquipment(
                  value.getId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(itemStack));
          craftPlayer.getHandle().playerConnection.sendPacket(equipmentPacket);
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
  public void removeHologram(Player player, Integer integer) {
    if (integer != null) {
      CraftPlayer value = (CraftPlayer) player;

      try {
        value
            .getHandle()
            .playerConnection
            .sendPacket(new PacketPlayOutEntityDestroy(new int[] {integer}));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  @Override
  public void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String string, ItemStack itemStack) {
    if (cmiDataWatcher != null) {
      EntityArmorStand value = (EntityArmorStand) cmiDataWatcher.getStand();
      if (string == null || string.isEmpty()) {
        value.setCustomNameVisible(false);
      } else if (!CMIChatColor.stripColor(string).isEmpty() && !string.replace(" ", "").isEmpty()) {
        value.setCustomNameVisible(true);
      } else {
        value.setCustomNameVisible(false);
      }

      value.setCustomName(string);
      PacketPlayOutEntityMetadata metadataPacket =
          new PacketPlayOutEntityMetadata(cmiDataWatcher.getId(), value.getDataWatcher(), true);
      CraftPlayer craftPlayer = (CraftPlayer) player;

      try {
        craftPlayer.getHandle().playerConnection.sendPacket(metadataPacket);
        PacketPlayOutEntityEquipment equipmentPacket =
            new PacketPlayOutEntityEquipment(
                cmiDataWatcher.getId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(itemStack));
        craftPlayer.getHandle().playerConnection.sendPacket(equipmentPacket);
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
    EntityItem itemEntity = new EntityItem(((CraftWorld) location.getWorld()).getHandle());
    itemEntity.setItemStack(CraftItemStack.asNMSCopy(itemStack));
    itemEntity.setPosition(location.getX(), location.getY(), location.getZ());
    itemEntity.setNoGravity(true);
    PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(itemEntity, 2, 1);
    value.playerConnection.sendPacket(spawnPacket);
    PacketPlayOutEntityMetadata metadataPacket =
        new PacketPlayOutEntityMetadata(itemEntity.getId(), itemEntity.getDataWatcher(), true);
    value.playerConnection.sendPacket(metadataPacket);
    return new CMIDataWatcher(
        itemEntity.getId(), itemEntity.getDataWatcher(), CMIHoloLineType.icon);
  }

  @Override
  public FakeInfo spawnClickable(Player player, Location location, Object object, boolean flag) {
    try {
      if (flag) {
        EntityMagmaCube value = new EntityMagmaCube(((CraftWorld) location.getWorld()).getHandle());
        value.setInvisible(true);
        value.setNoGravity(true);
        value.setAI(false);
        value.setSize(2, true);
        value.setLocation(
            location.getX(), location.getY() - 0.45, location.getZ() - 0.2, 0.0F, 0.0F);
        value.setCustomNameVisible(false);
        value.setSilent(true);
        PacketPlayOutSpawnEntityLiving magmaCubeSpawnPacket =
            new PacketPlayOutSpawnEntityLiving(value);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(magmaCubeSpawnPacket);
        FakeInfo fakeInfo = new FakeInfo().setObj(object).setPlayer(player).setId(value.getId());
        fakeInfo.setEntity(value);
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
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(armorStandSpawnPacket);
      FakeInfo armorStandInfo =
          new FakeInfo().setObj(object).setPlayer(player).setId(armorStand.getId());
      armorStandInfo.setEntity(armorStand);
      PacketHandler.fakeEntities.put(armorStand.getId(), armorStandInfo);
      return armorStandInfo;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean clearSkin(Player player) {
    if (player == null) {
      return false;
    }

    CraftPlayer value = (CraftPlayer) player;
    GameProfile gameProfile = value.getProfile();
    gameProfile.getProperties().removeAll("textures");
    this.updatePlayerViaPackets(player);
    return true;
  }

  @Override
  public void updatePlayerViaPackets(Player player) {
    CraftPlayer value = (CraftPlayer) player;
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    EntityPlayer entityPlayer = value.getHandle();
    int entityId = player.getEntityId();
    PacketPlayOutPlayerInfo removePacket =
        new PacketPlayOutPlayerInfo(
            EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] {value.getHandle()});
    PacketPlayOutPlayerInfo addPacket =
        new PacketPlayOutPlayerInfo(
            EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] {value.getHandle()});
    PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(new int[] {entityId});
    PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(entityPlayer);
    PacketPlayOutEntityEquipment helmetPacket =
        new PacketPlayOutEntityEquipment(
            entityId,
            EnumItemSlot.HEAD,
            CraftItemStack.asNMSCopy(
                cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.Helmet)));
    PacketPlayOutEntityEquipment chestPacket =
        new PacketPlayOutEntityEquipment(
            entityId,
            EnumItemSlot.CHEST,
            CraftItemStack.asNMSCopy(
                cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate)));
    PacketPlayOutEntityEquipment legsPacket =
        new PacketPlayOutEntityEquipment(
            entityId,
            EnumItemSlot.LEGS,
            CraftItemStack.asNMSCopy(
                cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.Pants)));
    PacketPlayOutEntityEquipment feetPacket =
        new PacketPlayOutEntityEquipment(
            entityId,
            EnumItemSlot.FEET,
            CraftItemStack.asNMSCopy(
                cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.Boots)));
    PacketPlayOutEntityEquipment mainHandPacket =
        new PacketPlayOutEntityEquipment(
            entityId, EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(cmiUser.getItemInHand()));
    PacketPlayOutEntityEquipment offHandPacket =
        new PacketPlayOutEntityEquipment(
            entityId, EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(cmiUser.getItemInOffHand()));
    PacketPlayOutEntityMetadata metadataPacket =
        new PacketPlayOutEntityMetadata(entityId, value.getHandle().getDataWatcher(), true);
    PacketPlayOutAbilities abilitiesPacket =
        new PacketPlayOutAbilities(value.getHandle().abilities);
    PacketPlayOutMount mountPacket = null;
    if (player.getVehicle() != null) {
      mountPacket = new PacketPlayOutMount(((CraftEntity) player.getVehicle()).getHandle());
    }

    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
      PlayerConnection connection = ((CraftPlayer) otherPlayer).getHandle().playerConnection;
      if (otherPlayer == player) {
        boolean flag = player.isFlying();
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
                0);
        PacketPlayOutRespawn respawnPacket =
            new PacketPlayOutRespawn(
                player.getWorld().getEnvironment().getId(),
                entityPlayer.getWorld().getDifficulty(),
                entityPlayer.getWorld().worldData.getType(),
                EnumGamemode.getById(player.getGameMode().getValue()));
        connection.sendPacket(respawnPacket);
        connection.sendPacket(positionPacket);
        PacketPlayOutUpdateHealth healthPacket =
            new PacketPlayOutUpdateHealth(
                (float) player.getHealth(), player.getFoodLevel(), player.getSaturation());
        connection.sendPacket(healthPacket);
        if (mountPacket != null) {
          connection.sendPacket(mountPacket);
        }

        connection.sendPacket(metadataPacket);
        connection.sendPacket(abilitiesPacket);
        CMIScheduler.runTask(
            () -> {
              if (flag) {
                player.setAllowFlight(true);
                player.setFlying(true);
              }

              this.updateExpBar(player);
              if (player.isOp()) {
                ((CraftServer) Bukkit.getServer()).getHandle().addOp(value.getProfile());
                value.recalculatePermissions();
              }
            });
      } else if (otherPlayer.canSee(player)) {
        connection.sendPacket(removePacket);
        connection.sendPacket(addPacket);
        if (CMI.getInstance()
                .getUtilManager()
                .getDistance(otherPlayer.getLocation(), player.getLocation())
            < CMI.getInstance().getUtilManager().getPlayerTrackingRange(player.getWorld())) {
          connection.sendPacket(destroyPacket);
          connection.sendPacket(spawnPacket);
        }

        if (mountPacket != null) {
          connection.sendPacket(mountPacket);
        }

        connection.sendPacket(metadataPacket);
        connection.sendPacket(helmetPacket);
        connection.sendPacket(chestPacket);
        connection.sendPacket(legsPacket);
        connection.sendPacket(feetPacket);
        connection.sendPacket(mainHandPacket);
        connection.sendPacket(offHandPacket);
      }
    }
  }

  public void playEffect(Player player, Location location, Effect effect, Color color) {
    CMIParticle cmiParticle = CMIParticle.getCMIParticle(effect.name());
    CMIEffect cmiEffect = new CMIEffect(cmiParticle);
    cmiEffect.setAmount(0);
    cmiEffect.setSpeed(1.0F);
    cmiEffect.setColor(org.bukkit.Color.fromBGR(color.getBlue(), color.getGreen(), color.getRed()));
    this.playEffect(player, location, cmiEffect);
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
    if (location != null && cmiEffect != null && location.getWorld() != null) {
      PacketPlayOutWorldParticles value = null;
      if (cmiEffect.getParticle().getEffect() != null) {
        if (cmiEffect.getParticle().isParticle()) {
          Particle particle = cmiEffect.getParticle().getParticle();
          EnumParticle particleType =
              cmiEffect.getParticle().getEnumParticle() == null
                  ? null
                  : (EnumParticle) cmiEffect.getParticle().getEnumParticle();
          int[] values = cmiEffect.getParticle().getExtra();
          if (particleType == null) {
            EnumParticle[] allParticles;
            int particleCount = (allParticles = EnumParticle.values()).length;

            for (int i = 0; i < particleCount; i++) {
              EnumParticle candidate = allParticles[i];
              if (particle
                  .name()
                  .replace("_", "")
                  .equalsIgnoreCase(candidate.toString().replace("_", ""))) {
                particleType = candidate;
                if (cmiEffect.getParticle().getEffect().getData() != null) {
                  if (cmiEffect.getParticle().getEffect().equals(Material.class)) {
                    values = new int[1];
                  } else {
                    values = new int[1];
                  }
                }
                break;
              }

              if (cmiEffect
                  .getParticle()
                  .getName()
                  .replace("_", "")
                  .equalsIgnoreCase(candidate.toString().replace("_", ""))) {
                particleType = candidate;
                if (cmiEffect.getParticle().getEffect().getData() != null) {
                  if (cmiEffect.getParticle().getEffect().equals(Material.class)) {
                    values = new int[1];
                  } else {
                    values = new int[1];
                  }
                }
                break;
              }

              if (cmiEffect
                  .getParticle()
                  .getSecondaryName()
                  .replace("_", "")
                  .equalsIgnoreCase(candidate.toString().replace("_", ""))) {
                particleType = candidate;
                if (cmiEffect.getParticle().getEffect().getData() != null) {
                  if (cmiEffect.getParticle().getEffect().equals(Material.class)) {
                    values = new int[1];
                  } else {
                    values = new int[1];
                  }
                }
                break;
              }
            }

            if (values == null) {
              values = new int[0];
            }
          }

          if (particleType == null) {
            return;
          }

          if (cmiEffect.getParticle().getEnumParticle() == null) {
            cmiEffect.getParticle().setEnumParticle(particleType);
            cmiEffect.getParticle().setExtra(values);
          }

          value =
              new PacketPlayOutWorldParticles(
                  particleType,
                  true,
                  (float) location.getX(),
                  (float) location.getY(),
                  (float) location.getZ(),
                  (float) cmiEffect.getOffset().getX(),
                  (float) cmiEffect.getOffset().getY(),
                  (float) cmiEffect.getOffset().getZ(),
                  cmiEffect.getSpeed(),
                  cmiEffect.getAmount(),
                  values);
        }

        CraftPlayer craftPlayer = (CraftPlayer) player;
        if (craftPlayer.getHandle().playerConnection != null) {
          if (location.getWorld().equals(craftPlayer.getWorld())) {
            if (value != null) {
              craftPlayer.getHandle().playerConnection.sendPacket(value);
            }
          }
        }
      }
    }
  }

  @Override
  public boolean forceTeleport(Player player, Location location) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    if (value.getHealth() == 0.0F || value.dead) {
      return false;
    }

    if (value.playerConnection == null) {
      return false;
    }

    if (value.isVehicle()) {
      return false;
    }

    Location currentLocation = player.getLocation();
    Location targetLocation = location;
    value.stopRiding();
    WorldServer fromWorld = ((CraftWorld) currentLocation.getWorld()).getHandle();
    WorldServer toWorld = ((CraftWorld) targetLocation.getWorld()).getHandle();
    if (((CraftPlayer) player).getHandle().activeContainer
        != ((CraftPlayer) player).getHandle().defaultContainer) {
      ((CraftPlayer) player).getHandle().closeInventory();
    }

    if (fromWorld == toWorld) {
      value.playerConnection.teleport(targetLocation);
    } else {
      ((CraftServer) Bukkit.getServer())
          .getHandle()
          .moveToWorld(value, toWorld.dimension, true, targetLocation, true);
    }

    return true;
  }

  @Override
  public CMIHitBox getBlockHitBox(CMIHitBox cmiHitBox) {
    try {
      Block block = cmiHitBox.getBlock();
      BlockPosition value = new BlockPosition(block.getX(), block.getY(), block.getZ());
      WorldServer worldServer = ((CraftWorld) block.getWorld()).getHandle();
      if (CMIMaterial.isAir(block.getType())) {
        cmiHitBox.setMin(new Vector(1, 1, 1));
        cmiHitBox.setMax(new Vector(0, 0, 0));
      } else {
        AxisAlignedBB shape = worldServer.getType(value).d(worldServer, value);
        if (shape != null) {
          cmiHitBox.setMin(new Vector(shape.a, shape.b, shape.c));
          cmiHitBox.setMax(new Vector(shape.d, shape.e, shape.f));
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
    Vector vector = location.toVector();
    Vector scaledDirection = location.getDirection().normalize().multiply(value);
    Vector endPoint = vector.clone().add(scaledDirection);
    return this.getBlockLookingAt(player.getWorld(), vector, endPoint, flag);
  }

  @Override
  public Block getBlockLookingAt(World world, Vector vector, Vector end, boolean flag) {
    WorldServer value = ((CraftWorld) world).getHandle();
    MovingObjectPosition hitResult =
        value.rayTrace(
            new Vec3D(vector.getX(), vector.getY(), vector.getZ()),
            new Vec3D(end.getX(), end.getY(), end.getZ()),
            flag);
    return hitResult == null
        ? null
        : world.getBlockAt(hitResult.a().getX(), hitResult.a().getY(), hitResult.a().getZ());
  }
}
