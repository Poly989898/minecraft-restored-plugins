package com.Zrips.CMI.NBT;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Holograms.CMIDataWatcher;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import com.Zrips.CMI.utils.Util;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.NBTCompressedStreamTools;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagDouble;
import net.minecraft.server.v1_7_R4.NBTTagFloat;
import net.minecraft.server.v1_7_R4.NBTTagList;
import net.minecraft.server.v1_7_R4.NBTTagString;
import net.minecraft.server.v1_7_R4.NetworkManager;
import net.minecraft.server.v1_7_R4.PacketPlayOutMapChunk;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_7_R4.PlayerAbilities;
import net.minecraft.server.v1_7_R4.PlayerConnection;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.server.v1_7_R4.WorldServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class v1_7_R4 implements NMS {
  @Override
  public synchronized Player getPlayer(CMIUser cmiUser) {
    try {
      GameProfile value = new GameProfile(cmiUser.getUniqueId(), cmiUser.getName());
      MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
      EntityPlayer entityPlayer =
          new EntityPlayer(
              server,
              server.getWorldServer(0),
              value,
              new PlayerInteractManager(server.getWorldServer(0)));
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
      GameProfile value =
          new GameProfile(
              CMI.getInstance().getPlayerManager().getFakeUserUUID(),
              CMI.getInstance().getPlayerManager().getFakeUserName());
      MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
      EntityPlayer entityPlayer =
          new EntityPlayer(
              server,
              server.getWorldServer(0),
              value,
              new PlayerInteractManager(server.getWorldServer(0)));
      entityPlayer.playerConnection =
          new PlayerConnection(server, new NetworkManager(false), entityPlayer);
      entityPlayer.playerConnection.networkManager.n = new InetSocketAddress("127.0.0.1", 25566);
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
    numbers[0] = posList.d(0);
    numbers[1] = posList.d(1);
    numbers[2] = posList.d(2);
    return numbers;
  }

  public Float[] getLocationView(NBTTagCompound value) {
    Float[] floats = new Float[2];
    NBTTagList rotationList = value.getList("Rotation", 5);
    floats[0] = rotationList.e(0);
    floats[1] = rotationList.e(1);
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

  public NBTTagCompound resetMaxHp(NBTTagCompound value) {
    if (value == null) {
      return null;
    }

    NBTTagList attributes = value.getList("Attributes", 10);

    for (int i = 0; i < attributes.size(); i++) {
      NBTTagCompound attribute = attributes.get(i);
      if (attribute.hasKey("Modifiers")
          && attribute.getString("Name").equalsIgnoreCase("generic.maxHealth")) {
        attribute.remove("Modifiers");
      }
    }

    return value;
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
          effect.setByte("ShowParticles", (byte) 1);
          break;
        }
      }

      if (!flag) {
        NBTTagCompound newEffect = new NBTTagCompound();
        newEffect.setByte("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
        newEffect.setByte("Amplifier", (byte) potionEffect.getAmplifier());
        newEffect.setInt("Duration", potionEffect.getDuration());
        newEffect.setByte("Id", (byte) potionEffect.getType().getId());
        newEffect.setByte("ShowParticles", (byte) 1);
        effects.add(newEffect);
      }

      saveData(value, file);
    }
  }

  @Override
  public boolean changeGodMode(Player player, boolean flag) {
    if (player == null) {
      return false;
    }

    EntityPlayer value = ((CraftPlayer) player).getHandle();

    try {
      PlayerAbilities abilities = value.abilities;
      Field field = abilities.getClass().getDeclaredField("isInvulnerable");
      field.setAccessible(true);
      field.setBoolean(abilities, flag);
      return true;
    } catch (NoSuchFieldException
        | SecurityException
        | IllegalArgumentException
        | IllegalAccessException value3) {
      return false;
    }
  }

  @Override
  public boolean getGodMode(Player player) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    return value.abilities.isInvulnerable;
  }

  @Override
  public void relightChunk(Chunk chunk) {
    ((CraftChunk) chunk).getHandle().initLighting();
    ((CraftChunk) chunk).getHandle().n();
  }

  @Override
  public void resendChunk(Chunk chunk, Player player) {
    try {
      ((CraftPlayer) player)
          .getHandle()
          .playerConnection
          .sendPacket(new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), true, 65535, 0));
    } catch (NoSuchMethodError noSuchMethodError) {
    }
  }

  @Override
  public void unloadChunk(Chunk chunk, Player player) {
    ((CraftPlayer) player)
        .getHandle()
        .playerConnection
        .sendPacket(new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), true, 0, 0));
  }

  @Override
  public void updateCollisions(Player player, boolean collidable, boolean pushable) {}

  public Inventory getShulkerInv(ItemStack itemStack) {
    return null;
  }

  public Inventory getShulkerInv(Block block) {
    return null;
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
  public void showResurection(Player player) {}

  @Override
  public void setEntityTag(Entity entity, String string, Object object) {}

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    return itemStack;
  }

  @Override
  public ItemStack setNBTList(ItemStack itemStack, String string, List<String> list) {
    net.minecraft.server.v1_7_R4.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
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
  public void updateExpBar(Player player) {
    CraftPlayer value = (CraftPlayer) player;
    value.getHandle().lastSentExp = -1;
  }

  @Override
  public Command getCommand(String string) {
    return ((CraftServer) Bukkit.getServer()).getCommandMap().getCommand(string);
  }

  @Override
  public CMIDataWatcher spawnHologram(
      Player player, Location location, String string, ItemStack itemStack) {
    return null;
  }

  @Override
  public void removeHologram(Player player, Integer integer) {}

  @Override
  public void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String string, ItemStack itemStack) {}

  @Override
  public CMIDataWatcher makeFloatingIcon(Player player, Location location, ItemStack itemStack) {
    return this.makeFloatingIcon(player, location, itemStack, false);
  }

  @Override
  public CMIDataWatcher makeFloatingIcon(
      Player player, Location location, ItemStack itemStack, boolean flag) {
    return null;
  }

  @Override
  public FakeInfo spawnClickable(Player player, Location location, Object object, boolean flag) {
    return null;
  }

  @Override
  public boolean clearSkin(Player player) {
    return false;
  }

  @Override
  public void updatePlayerViaPackets(Player player) {}

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
      CraftPlayer value = (CraftPlayer) player;
      if (value.getHandle().playerConnection != null) {
        Effect effect = cmiEffect.getParticle().getEffect();
        if (effect != null) {
          PacketPlayOutWorldParticles particlePacket =
              new PacketPlayOutWorldParticles(
                  effect.name(),
                  (float) location.getX(),
                  (float) location.getY(),
                  (float) location.getZ(),
                  (float) cmiEffect.getOffset().getX(),
                  (float) cmiEffect.getOffset().getY(),
                  (float) cmiEffect.getOffset().getZ(),
                  cmiEffect.getSpeed(),
                  cmiEffect.getAmount());
          value.getHandle().playerConnection.sendPacket(particlePacket);
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

    if (value.vehicle != null) {
      return false;
    }

    Location currentLocation = player.getLocation();
    Location targetLocation = location;
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
    cmiHitBox.setMin(new Vector(1, 1, 1));
    cmiHitBox.setMax(new Vector(0, 0, 0));
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

      AxisAlignedBB value = ((CraftEntity) entity).getHandle().boundingBox;
      cmiHitBox.setMin(new Vector(value.a, value.b, value.c));
      cmiHitBox.setMax(new Vector(value.d, value.e, value.f));
    } catch (NoSuchMethodError noSuchMethodError) {
    }

    return cmiHitBox;
  }

  @Override
  public Block getBlockLookingAt(Player player, int value, boolean flag) {
    if (value > 240) {
      value = 240;
    }

    if (value < 1) {
      value = 1;
    }

    ArrayList<Block> arrayList = new ArrayList();
    BlockIterator blockIterator = new BlockIterator(player, value);
    ArrayList<Block> resultBlocks = new ArrayList();

    while (blockIterator.hasNext()) {
      Block block = (Block) blockIterator.next();
      arrayList.add(block);
      if (value != 0 && arrayList.size() > value) {
        arrayList.remove(0);
      }

      Material material = block.getType();
      if ((!flag || block.getType().isSolid())
          && !CMIMaterial.AIR.equals(material)
          && !CMIMaterial.CAVE_AIR.equals(material)
          && !CMIMaterial.VOID_AIR.equals(material)) {
        break;
      }
    }

    if (arrayList.size() > 1) {
      resultBlocks.add((Block) arrayList.get(arrayList.size() - 2));
    }

    if (!arrayList.isEmpty()) {
      resultBlocks.add((Block) arrayList.get(arrayList.size() - 1));
    }

    return resultBlocks.isEmpty() ? null : (Block) resultBlocks.get(resultBlocks.size() - 1);
  }

  @Override
  public Block getBlockLookingAt(World world, Vector vector, Vector end, boolean flag) {
    int value = (int) vector.distance(end) + 1;
    BlockIterator blockIterator = new BlockIterator(world, vector, end, 0.05, value);
    ArrayList<Block> arrayList = new ArrayList();

    while (blockIterator.hasNext()) {
      Block block = (Block) blockIterator.next();
      arrayList.add(block);
      Material material = block.getType();
      if (!CMIMaterial.AIR.equals(material)
          && !CMIMaterial.CAVE_AIR.equals(material)
          && !CMIMaterial.VOID_AIR.equals(material)) {
        break;
      }
    }

    return arrayList.isEmpty() ? null : (Block) arrayList.get(arrayList.size() - 1);
  }
}
