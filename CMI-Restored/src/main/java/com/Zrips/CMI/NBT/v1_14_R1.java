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
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.minecraft.server.v1_14_R1.AxisAlignedBB;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.CriterionTriggers;
import net.minecraft.server.v1_14_R1.DedicatedServer;
import net.minecraft.server.v1_14_R1.DimensionManager;
import net.minecraft.server.v1_14_R1.EntityArmorStand;
import net.minecraft.server.v1_14_R1.EntityItem;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntitySlime;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.EnumDirection.EnumAxis;
import net.minecraft.server.v1_14_R1.EnumGamemode;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.EnumProtocolDirection;
import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.Items;
import net.minecraft.server.v1_14_R1.MobEffect;
import net.minecraft.server.v1_14_R1.MobEffects;
import net.minecraft.server.v1_14_R1.MovingObjectPositionBlock;
import net.minecraft.server.v1_14_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagDouble;
import net.minecraft.server.v1_14_R1.NBTTagFloat;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.NBTTagString;
import net.minecraft.server.v1_14_R1.NetworkManager;
import net.minecraft.server.v1_14_R1.Packet;
import net.minecraft.server.v1_14_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_14_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_14_R1.PacketPlayOutMount;
import net.minecraft.server.v1_14_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_14_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_14_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_14_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_14_R1.PacketPlayOutUpdateHealth;
import net.minecraft.server.v1_14_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import net.minecraft.server.v1_14_R1.PlayerInteractManager;
import net.minecraft.server.v1_14_R1.PlayerList;
import net.minecraft.server.v1_14_R1.RayTrace;
import net.minecraft.server.v1_14_R1.RayTrace.BlockCollisionOption;
import net.minecraft.server.v1_14_R1.RayTrace.FluidCollisionOption;
import net.minecraft.server.v1_14_R1.StatisticList;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.Vector3f;
import net.minecraft.server.v1_14_R1.VoxelShape;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_14_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_14_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftRayTraceResult;
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

public class v1_14_R1 implements NMS {
  @Override
  public synchronized Player getPlayer(CMIUser cmiUser) {
    try {
      if (cmiUser.isFakeAccount()) {
        return null;
      }

      File file = Util.getFile(cmiUser.getUniqueId());
      if (file.canRead() && file.exists()) {
        DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile = new GameProfile(cmiUser.getUniqueId(), cmiUser.getName(false));
        WorldServer level = value.getWorldServer(DimensionManager.OVERWORLD);
        EntityPlayer entityPlayer =
            new EntityPlayer(value, level, gameProfile, new PlayerInteractManager(level));
        CraftPlayer craftPlayer = entityPlayer.getBukkitEntity();
        if (craftPlayer != null) {
          craftPlayer.loadData();
          return craftPlayer;
        } else {
          return null;
        }
      } else {
        return null;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
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
      DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
      WorldServer level = (WorldServer) value.getWorlds().iterator().next();
      EntityPlayer entityPlayer =
          new EntityPlayer(value, level, gameProfile, new PlayerInteractManager(level));
      entityPlayer.playerConnection =
          new PlayerConnection(
              value, new NetworkManager(EnumProtocolDirection.SERVERBOUND), entityPlayer);
      entityPlayer.playerConnection.networkManager.socketAddress =
          new InetSocketAddress("127.0.0.1", 25566);
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

      WorldServer level = value.getWorldServer();
      if (level == null) {
        return;
      }

      level.removePlayer(value);
      value.getAdvancementData().a();
      DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
      PlayerList playerList = server.getPlayerList();
      playerList.players.remove(value);
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
    numbers[0] = posList.h(0);
    numbers[1] = posList.h(1);
    numbers[2] = posList.h(2);
    return numbers;
  }

  public Float[] getLocationView(NBTTagCompound value) {
    Float[] floats = new Float[2];
    NBTTagList rotationList = value.getList("Rotation", 5);
    floats[0] = (float) rotationList.h(0);
    floats[1] = (float) rotationList.h(1);
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

  private static NBTTagCompound setDimension(NBTTagCompound value, int dimension) {
    value.setInt("Dimension", dimension);
    return value;
  }

  private static NBTTagCompound setLocationWorld(
      NBTTagCompound value, long longValue, long longValue2) {
    value = setUUIDWorldLeast(value, longValue);
    return setUUIDWorldMost(value, longValue2);
  }

  private static NBTTagCompound setUUIDWorldLeast(NBTTagCompound value, long longValue) {
    value.setLong("WorldUUIDLeast", longValue);
    return value;
  }

  public static String getName(NBTTagCompound value) {
    NBTTagCompound bukkitTag = value.getCompound("bukkit");
    return bukkitTag.getString("lastKnownName");
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

      for (int index = 0; index < effects.size(); index++) {
        NBTTagCompound effect = effects.getCompound(index);
        if (effect.getByte("Id") == potionEffect.getType().getId()) {
          flag = true;
          effect.setByte("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
          effect.setByte("Amplifier", (byte) potionEffect.getAmplifier());
          effect.setInt("Duration", potionEffect.getDuration());
          effect.setByte("ShowParticles", (byte) (potionEffect.hasParticles() ? 1 : 0));
          effects.a(index, effect);
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
  public void updateCollisions(Player player, boolean collisionEnabled, boolean selfOnly) {}

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
    return shulkerBox.getInventory() == null ? null : shulkerBox.getInventory();
  }

  public Inventory getShulkerInv(Block block) {
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
      entityPlayer.b(StatisticList.ITEM_USED.b(Items.TOTEM_OF_UNDYING));
      CriterionTriggers.B.a(entityPlayer, CraftItemStack.asNMSCopy(itemStack));
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
  public void setEntityTag(Entity entity, String tagName, Object tagValue) {
    NBTTagCompound value = new NBTTagCompound();
    net.minecraft.server.v1_14_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
    NBTTagCompound entityTag = nmsEntity.save(value);
    if (tagValue instanceof String) {
      entityTag.setString(tagName, (String) tagValue);
    } else if (tagValue instanceof Short) {
      entityTag.setShort(tagName, (Short) tagValue);
    }

    nmsEntity.f(value);
  }

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    net.minecraft.server.v1_14_R1.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      String string = enchantment.getKey().getKey();
      NBTTagCompound itemTag = value.getTag();
      boolean flag = false;
      if (itemTag != null && itemTag.hasKey("ench")) {
        NBTTagList enchantments = itemTag.getList("ench", 10);

        for (int index = 0; index < enchantments.size(); index++) {
          if (enchantments.getCompound(index).getString("id").equalsIgnoreCase(string)) {
            enchantments.remove(index);
            flag = true;
            break;
          }
        }

        if (flag) {
          itemTag.set("ench", enchantments);
        }

        value.setTag(itemTag);
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
    net.minecraft.server.v1_14_R1.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      NBTTagCompound itemTag = value.getTag();
      if (itemTag == null) {
        itemTag = new NBTTagCompound();
      }

      NBTTagList tagList = new NBTTagList();
      if (values != null) {
        for (String entry : values) {
          tagList.add(new NBTTagString(entry));
        }
      }

      if (values == null) {
        itemTag.remove(key);
      } else {
        itemTag.set(key, tagList);
      }

      value.setTag(itemTag);
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
    value.getHandle().lastSentExp = -1;
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
      y -= 0.45;
    } else {
      y -= 0.45;
    }

    EntityArmorStand value =
        new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), x, y, z);
    value.setInvisible(true);
    value.setArms(false);
    value.setMarker(true);
    value.setNoGravity(true);
    value.setBasePlate(false);
    value.setSmall(small);
    if (small) {
      value.setRightArmPose(new Vector3f(-15.0F, -45.0F, 0.0F));
    }

    if (text != null && !text.isEmpty()) {
      ChatComponentText component =
          new ChatComponentText(CMIChatColor.translateAlternateColorCodes(text));
      value.setCustomName(component);
      if (!CMIChatColor.stripColor(text).isEmpty() && !text.replace(" ", "").isEmpty()) {
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
      Player player, Location location, String text, ItemStack itemStack) {
    EntityArmorStand value = createStand(location, text, itemStack != null);
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
  public FakeInfo spawnClickable(Player player, Location location, Object data, boolean glowing) {
    try {
      if (glowing) {
        EntitySlime value =
            new EntitySlime(EntityTypes.MAGMA_CUBE, ((CraftWorld) location.getWorld()).getHandle());
        value.setInvisible(true);
        value.setNoGravity(true);
        value.setNoAI(true);
        value.setSize(2, true);
        value.setLocation(
            location.getX(), location.getY() - 0.45, location.getZ() - 0.2, 0.0F, 0.0F);
        value.setCustomNameVisible(false);
        value.setSilent(true);
        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(value);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnPacket);
        FakeInfo fakeInfo = new FakeInfo().setObj(data).setPlayer(player).setId(value.getId());
        fakeInfo.setEntity(value);
        PacketHandler.fakeEntities.put(value.getId(), fakeInfo);
        return fakeInfo;
      }

      EntityArmorStand stand =
          new EntityArmorStand(
              ((CraftWorld) location.getWorld()).getHandle(),
              location.getX(),
              location.getY() - 0.35,
              location.getZ());
      stand.setInvisible(true);
      stand.setArms(false);
      stand.setNoGravity(true);
      stand.setBasePlate(false);
      if (data instanceof CMIHologram) {
        if (((CMIHologram) data).getHeight() > 1.7) {
          stand.setSmall(false);
          stand.setLocation(location.getX(), location.getY() - 0.75, location.getZ(), 0.0F, 0.0F);
        } else {
          stand.setSmall(true);
        }
      } else {
        stand.setSmall(true);
      }

      stand.setCustomNameVisible(false);
      PacketPlayOutSpawnEntityLiving standSpawnPacket = new PacketPlayOutSpawnEntityLiving(stand);
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(standSpawnPacket);
      FakeInfo fakeInfo = new FakeInfo().setObj(data).setPlayer(player).setId(stand.getId());
      fakeInfo.setEntity(stand);
      PacketHandler.fakeEntities.put(stand.getId(), fakeInfo);
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
        value
            .getHandle()
            .playerConnection
            .sendPacket(new PacketPlayOutEntityDestroy(new int[] {entityId}));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  @Override
  public void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String text, ItemStack itemStack) {
    if (cmiDataWatcher != null) {
      EntityArmorStand value = (EntityArmorStand) cmiDataWatcher.getStand();
      if (text == null || text.isEmpty()) {
        value.setCustomNameVisible(false);
      } else if (!CMIChatColor.stripColor(text).isEmpty() && !text.replace(" ", "").isEmpty()) {
        value.setCustomNameVisible(true);
      } else {
        value.setCustomNameVisible(false);
      }

      ChatComponentText component = new ChatComponentText(text);
      value.setCustomName(component);
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
      Player player, Location location, ItemStack itemStack, boolean glowing) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    EntityItem itemEntity =
        new EntityItem(
            ((CraftWorld) location.getWorld()).getHandle(),
            location.getX(),
            location.getY(),
            location.getZ());
    itemEntity.setItemStack(CraftItemStack.asNMSCopy(itemStack));
    itemEntity.setPosition(location.getX(), location.getY(), location.getZ());
    itemEntity.setNoGravity(true);
    PacketPlayOutEntityVelocity velocityPacket =
        new PacketPlayOutEntityVelocity(itemEntity.getId(), new Vec3D(0.0, 0.0, 0.0));
    PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(itemEntity);
    value.playerConnection.sendPacket(spawnPacket);
    PacketPlayOutEntityMetadata metadataPacket =
        new PacketPlayOutEntityMetadata(itemEntity.getId(), itemEntity.getDataWatcher(), true);
    value.playerConnection.sendPacket(metadataPacket);
    value.playerConnection.sendPacket(velocityPacket);
    return new CMIDataWatcher(
        itemEntity.getId(), itemEntity.getDataWatcher(), CMIHoloLineType.icon);
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
    if (cmiUser != null) {
      EntityPlayer renamedPlayer = null;
      if (!cmiUser.getTagName().equals(player.getName())) {
        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile =
            new GameProfile(player.getUniqueId(), CMIChatColor.translate(cmiUser.getTagName()));
        gameProfile.getProperties().putAll(value.getHandle().getProfile().getProperties());
        WorldServer renamedLevel = server.getWorldServer(value.getHandle().dimension);
        renamedPlayer =
            new EntityPlayer(
                server, renamedLevel, gameProfile, new PlayerInteractManager(renamedLevel));
        net.minecraft.server.v1_14_R1.Entity renamedEntity =
            renamedPlayer.getBukkitEntity().getHandle();
        renamedEntity.e(player.getEntityId());
      }

      EntityPlayer playerHandle = value.getHandle();
      int entityId = player.getEntityId();
      PacketPlayOutPlayerInfo removeInfoPacket =
          new PacketPlayOutPlayerInfo(
              EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] {playerHandle});
      Packet addInfoPacket = null;
      if (renamedPlayer != null) {
        addInfoPacket =
            new PacketPlayOutPlayerInfo(
                EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] {renamedPlayer});
      } else {
        addInfoPacket =
            new PacketPlayOutPlayerInfo(
                EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] {playerHandle});
      }

      PacketPlayOutEntityDestroy destroyPacket =
          new PacketPlayOutEntityDestroy(new int[] {entityId});
      PacketPlayOutNamedEntitySpawn spawnPacket = new PacketPlayOutNamedEntitySpawn(playerHandle);
      PacketPlayOutEntityEquipment equipmentPacket =
          new PacketPlayOutEntityEquipment(
              entityId,
              EnumItemSlot.HEAD,
              CraftItemStack.asNMSCopy(
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.Helmet)));
      PacketPlayOutEntityEquipment chestEquipmentPacket =
          new PacketPlayOutEntityEquipment(
              entityId,
              EnumItemSlot.CHEST,
              CraftItemStack.asNMSCopy(
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.ChestPlate)));
      PacketPlayOutEntityEquipment legsEquipmentPacket =
          new PacketPlayOutEntityEquipment(
              entityId,
              EnumItemSlot.LEGS,
              CraftItemStack.asNMSCopy(
                  cmiUser.getInventory().getItem(CMIPlayerInventory.CMIInventorySlot.Pants)));
      PacketPlayOutEntityEquipment feetEquipmentPacket =
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

      CraftWorld craftWorld = (CraftWorld) player.getWorld();
      World world = Bukkit.getWorld(player.getWorld().getUID());
      if (world == null) {
        craftWorld = (CraftWorld) Bukkit.getWorlds().get(0);
      } else {
        craftWorld = (CraftWorld) world;
      }

      WorldServer level = craftWorld.getHandle();
      DimensionManager dimension = DimensionManager.OVERWORLD;
      switch (player.getWorld().getEnvironment()) {
        case NORMAL:
          dimension = DimensionManager.OVERWORLD;
          break;
        case NETHER:
          dimension = DimensionManager.NETHER;
          break;
        case THE_END:
          dimension = DimensionManager.THE_END;
      }

      for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
        PlayerConnection connection = ((CraftPlayer) otherPlayer).getHandle().playerConnection;
        if (otherPlayer == player) {
          boolean flag = player.isFlying();
          connection.sendPacket(removeInfoPacket);
          connection.sendPacket(addInfoPacket);
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
                  dimension,
                  playerHandle.getWorld().worldData.getType(),
                  EnumGamemode.getById(player.getGameMode().getValue()));
          connection.sendPacket(respawnPacket);
          connection.sendPacket(positionPacket);

          for (MobEffect mobEffect : playerHandle.getEffects()) {
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
          connection.sendPacket(removeInfoPacket);
          connection.sendPacket(addInfoPacket);
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
          CMIScheduler.runTaskLater(
              () -> {
                connection.sendPacket(equipmentPacket);
                connection.sendPacket(chestEquipmentPacket);
                connection.sendPacket(legsEquipmentPacket);
                connection.sendPacket(feetEquipmentPacket);
                connection.sendPacket(mainHandPacket);
                connection.sendPacket(offHandPacket);
              },
              1L);
        }
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
      CMIParticle cmiParticle = cmiEffect.getParticle();
      if (cmiParticle != null) {
        if (cmiParticle.isParticle()) {
          Particle particle = cmiParticle.getParticle();
          if (particle != null) {
            DustOptions dustOptions = null;
            if (particle.toString().equalsIgnoreCase("REDSTONE")) {
              dustOptions = new DustOptions(cmiEffect.getColor(), cmiEffect.getSize());
            }

            PacketPlayOutWorldParticles value =
                new PacketPlayOutWorldParticles(
                    CraftParticle.toNMS(particle, dustOptions),
                    true,
                    (float) location.getX(),
                    (float) location.getY(),
                    (float) location.getZ(),
                    (float) cmiEffect.getOffset().getX(),
                    (float) cmiEffect.getOffset().getY(),
                    (float) cmiEffect.getOffset().getZ(),
                    cmiEffect.getSpeed(),
                    cmiEffect.getAmount());
            CraftPlayer craftPlayer = (CraftPlayer) player;
            if (craftPlayer.getHandle().playerConnection != null) {
              if (location.getWorld().equals(craftPlayer.getWorld())) {
                craftPlayer.getHandle().playerConnection.sendPacket(value);
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
    WorldServer level = ((CraftWorld) currentLocation.getWorld()).getHandle();
    WorldServer targetLevel = ((CraftWorld) targetLocation.getWorld()).getHandle();
    World world = Bukkit.getWorld(targetLocation.getWorld().getUID());
    if (world == null) {
      targetLevel = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
    } else {
      targetLevel = ((CraftWorld) world).getHandle();
    }

    if (((CraftPlayer) player).getHandle().activeContainer
        != ((CraftPlayer) player).getHandle().defaultContainer) {
      ((CraftPlayer) player).getHandle().closeInventory();
    }

    if (level == targetLevel) {
      value.playerConnection.teleport(targetLocation);
    } else {
      DimensionManager dimension = DimensionManager.OVERWORLD;
      switch (targetLocation.getWorld().getEnvironment()) {
        case NORMAL:
          dimension = DimensionManager.OVERWORLD;
          break;
        case NETHER:
          dimension = DimensionManager.NETHER;
          break;
        case THE_END:
          dimension = DimensionManager.THE_END;
      }

      ((CraftServer) Bukkit.getServer())
          .getHandle()
          .moveToWorld(value, dimension, true, targetLocation, true);
    }

    return true;
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
      if (CMIMaterial.isAir(block.getType())) {
        cmiHitBox.setMin(new Vector(1, 1, 1));
        cmiHitBox.setMax(new Vector(0, 0, 0));
      } else {
        try {
          IBlockData blockState = level.getType(value);
          VoxelShape shape = blockState.getCollisionShape(level, value);
          if (!shape.isEmpty()) {
            AxisAlignedBB bounds = shape.getBoundingBox();
            cmiHitBox.setMin(
                new Vector(bounds.a(EnumAxis.X), bounds.a(EnumAxis.Y), bounds.a(EnumAxis.Z)));
            cmiHitBox.setMax(
                new Vector(bounds.b(EnumAxis.X), bounds.b(EnumAxis.Y), bounds.b(EnumAxis.Z)));
          } else {
            cmiHitBox.setMin(new Vector(1, 1, 1));
            cmiHitBox.setMax(new Vector(0, 0, 0));
          }
        } catch (Exception exception) {
          try {
            IBlockData fallbackState = level.getType(value);
            Object object =
                fallbackState
                    .getClass()
                    .getMethod("g", level.getClass(), value.getClass())
                    .invoke(fallbackState, level, value);
            AxisAlignedBB fallbackBounds =
                (AxisAlignedBB) object.getClass().getMethod("a").invoke(object);
            cmiHitBox.setMin(
                new Vector(
                    fallbackBounds.a(EnumAxis.X),
                    fallbackBounds.a(EnumAxis.Y),
                    fallbackBounds.a(EnumAxis.Z)));
            cmiHitBox.setMax(
                new Vector(
                    fallbackBounds.b(EnumAxis.X),
                    fallbackBounds.b(EnumAxis.Y),
                    fallbackBounds.b(EnumAxis.Z)));
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
      cmiHitBox.setMin(new Vector(value.minX, value.minY, value.minZ));
      cmiHitBox.setMax(new Vector(value.maxX, value.maxY, value.maxZ));
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
    CraftWorld value = (CraftWorld) world;
    RayTrace clipContext =
        new RayTrace(
            new Vec3D(start.getX(), start.getY(), start.getZ()),
            new Vec3D(end.getX(), end.getY(), end.getZ()),
            BlockCollisionOption.COLLIDER,
            includeFluids ? FluidCollisionOption.ANY : FluidCollisionOption.NONE,
            null);
    MovingObjectPositionBlock hitResult = value.getHandle().rayTrace(clipContext);
    RayTraceResult rayTraceResult = CraftRayTraceResult.fromNMS(value, hitResult);
    return rayTraceResult == null ? null : rayTraceResult.getHitBlock();
  }
}
