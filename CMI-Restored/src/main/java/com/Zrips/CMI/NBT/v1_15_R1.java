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
import net.minecraft.server.v1_15_R1.AxisAlignedBB;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.CriterionTriggers;
import net.minecraft.server.v1_15_R1.DedicatedServer;
import net.minecraft.server.v1_15_R1.DimensionManager;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EntityItem;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EntitySlime;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EnumDirection.EnumAxis;
import net.minecraft.server.v1_15_R1.EnumGamemode;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import net.minecraft.server.v1_15_R1.EnumProtocolDirection;
import net.minecraft.server.v1_15_R1.IBlockData;
import net.minecraft.server.v1_15_R1.Items;
import net.minecraft.server.v1_15_R1.MobEffect;
import net.minecraft.server.v1_15_R1.MobEffects;
import net.minecraft.server.v1_15_R1.MovingObjectPositionBlock;
import net.minecraft.server.v1_15_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagDouble;
import net.minecraft.server.v1_15_R1.NBTTagFloat;
import net.minecraft.server.v1_15_R1.NBTTagList;
import net.minecraft.server.v1_15_R1.NBTTagString;
import net.minecraft.server.v1_15_R1.NetworkManager;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutAbilities;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_15_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_15_R1.PacketPlayOutMount;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_15_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_15_R1.PacketPlayOutRespawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_15_R1.PacketPlayOutUpdateHealth;
import net.minecraft.server.v1_15_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.PlayerList;
import net.minecraft.server.v1_15_R1.RayTrace;
import net.minecraft.server.v1_15_R1.RayTrace.BlockCollisionOption;
import net.minecraft.server.v1_15_R1.RayTrace.FluidCollisionOption;
import net.minecraft.server.v1_15_R1.StatisticList;
import net.minecraft.server.v1_15_R1.Vec3D;
import net.minecraft.server.v1_15_R1.Vector3f;
import net.minecraft.server.v1_15_R1.VoxelShape;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.CraftParticle;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftRayTraceResult;
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

public class v1_15_R1 implements NMS {
  @Override
  public Player getPlayer(CMIUser cmiUser) {
    try {
      if (cmiUser.isFakeAccount()) {
        return null;
      }

      File file = Util.getFile(cmiUser.getUniqueId());
      if (file.canRead() && file.exists()) {
        DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile = new GameProfile(cmiUser.getUniqueId(), cmiUser.getName(false));
        WorldServer worldServer = value.getWorldServer(DimensionManager.OVERWORLD);
        EntityPlayer entityPlayer =
            new EntityPlayer(
                value, worldServer, gameProfile, new PlayerInteractManager(worldServer));
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
  public Player createFakeOperator() {
    try {
      GameProfile gameProfile =
          new GameProfile(
              CMI.getInstance().getPlayerManager().getFakeUserUUID(),
              CMI.getInstance().getPlayerManager().getFakeUserName());
      DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
      WorldServer worldServer = (WorldServer) value.getWorlds().iterator().next();
      EntityPlayer entityPlayer =
          new EntityPlayer(value, worldServer, gameProfile, new PlayerInteractManager(worldServer));
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

      WorldServer worldServer = value.getWorldServer();
      if (worldServer == null) {
        return;
      }

      worldServer.removePlayer(value);
      value.getAdvancementData().a();
      DedicatedServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
      PlayerList playerList = minecraftServer.getPlayerList();
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

  @Override
  public void showResurection(Player player) {
    this.showResurection(player, new ItemStack(Material.TOTEM_OF_UNDYING));
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
  public void setEntityTag(Entity entity, String string, Object object) {
    NBTTagCompound value = new NBTTagCompound();
    net.minecraft.server.v1_15_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
    NBTTagCompound savedTag = nmsEntity.save(value);
    if (object instanceof String) {
      savedTag.setString(string, (String) object);
    } else if (object instanceof Short) {
      savedTag.setShort(string, (Short) object);
    }

    nmsEntity.f(value);
  }

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    net.minecraft.server.v1_15_R1.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
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
    net.minecraft.server.v1_15_R1.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
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
    value.getHandle().lastSentExp = -1;
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
      ChatComponentText customName = new ChatComponentText(CMIChatColor.translate(string));
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
  public FakeInfo spawnClickable(Player player, Location location, Object object, boolean flag) {
    try {
      if (flag) {
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
        PacketPlayOutSpawnEntityLiving magmaCubeSpawnPacket =
            new PacketPlayOutSpawnEntityLiving(value);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(magmaCubeSpawnPacket);
        ((CraftPlayer) player)
            .getHandle()
            .playerConnection
            .sendPacket(
                new PacketPlayOutEntityMetadata(value.getId(), value.getDataWatcher(), false));
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
      ((CraftPlayer) player)
          .getHandle()
          .playerConnection
          .sendPacket(
              new PacketPlayOutEntityMetadata(
                  armorStand.getId(), armorStand.getDataWatcher(), false));
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

      ChatComponentText chatComponent = new ChatComponentText(string);
      value.setCustomName(chatComponent);
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
    EntityItem itemEntity =
        new EntityItem(
            ((CraftWorld) location.getWorld()).getHandle(),
            location.getX(),
            location.getY(),
            location.getZ());
    itemEntity.setItemStack(CraftItemStack.asNMSCopy(itemStack));
    itemEntity.setPosition(location.getX(), location.getY(), location.getZ());
    itemEntity.setNoGravity(true);
    if (flag) {
      itemEntity.h(true);
    }

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
        DedicatedServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        GameProfile gameProfile =
            new GameProfile(player.getUniqueId(), CMIChatColor.translate(cmiUser.getTagName()));
        gameProfile.getProperties().putAll(value.getHandle().getProfile().getProperties());
        WorldServer renamedWorld = minecraftServer.getWorldServer(value.getHandle().dimension);
        renamedPlayer =
            new EntityPlayer(
                minecraftServer,
                renamedWorld,
                gameProfile,
                new PlayerInteractManager(renamedWorld));
        net.minecraft.server.v1_15_R1.Entity renamedEntity =
            renamedPlayer.getBukkitEntity().getHandle();
        renamedEntity.e(player.getEntityId());
      }

      EntityPlayer entityPlayer = value.getHandle();
      int entityId = player.getEntityId();
      PacketPlayOutPlayerInfo removePacket =
          new PacketPlayOutPlayerInfo(
              EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] {entityPlayer});
      Packet addPacket = null;
      if (renamedPlayer != null) {
        addPacket =
            new PacketPlayOutPlayerInfo(
                EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] {renamedPlayer});
      } else {
        addPacket =
            new PacketPlayOutPlayerInfo(
                EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] {entityPlayer});
      }

      PacketPlayOutEntityDestroy destroyPacket =
          new PacketPlayOutEntityDestroy(new int[] {entityId});
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

      CraftWorld craftWorld = (CraftWorld) player.getWorld();
      WorldServer worldServer = craftWorld.getHandle();
      DimensionManager dimensionManager = worldServer.worldProvider.getDimensionManager();
      switch (player.getWorld().getEnvironment()) {
        case NORMAL:
          dimensionManager = DimensionManager.OVERWORLD;
          break;
        case NETHER:
          dimensionManager = DimensionManager.NETHER;
          break;
        case THE_END:
          dimensionManager = DimensionManager.THE_END;
      }

      int heldSlot = player.getInventory().getHeldItemSlot();

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
                  dimensionManager,
                  0L,
                  entityPlayer.getWorld().worldData.getType(),
                  EnumGamemode.getById(player.getGameMode().getValue()));
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

                player.getInventory().setHeldItemSlot(heldSlot);
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
          CMIScheduler.runTaskLater(
              () -> {
                connection.sendPacket(helmetPacket);
                connection.sendPacket(chestPacket);
                connection.sendPacket(legsPacket);
                connection.sendPacket(feetPacket);
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
    if (player != null && location != null && cmiEffect != null && location.getWorld() != null) {
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
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    (float) cmiEffect.getOffset().getX(),
                    (float) cmiEffect.getOffset().getY(),
                    (float) cmiEffect.getOffset().getZ(),
                    cmiEffect.getSpeed(),
                    cmiEffect.getAmount());
            CraftPlayer craftPlayer = (CraftPlayer) player;
            if (craftPlayer.getHandle() != null
                && craftPlayer.getHandle().playerConnection != null) {
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

    value.stopRiding();
    WorldServer targetWorld = ((CraftWorld) location.getWorld()).getHandle();
    World world = location.getWorld();
    if (world == null) {
      targetWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
    } else {
      targetWorld = ((CraftWorld) world).getHandle();
    }

    if (((CraftPlayer) player).getHandle().activeContainer
        != ((CraftPlayer) player).getHandle().defaultContainer) {
      ((CraftPlayer) player).getHandle().closeInventory();
    }

    if (((CraftWorld) player.getLocation().getWorld()).getHandle() == targetWorld) {
      value.playerConnection.teleport(location);
    } else {
      ((CraftServer) Bukkit.getServer())
          .getHandle()
          .moveToWorld(
              value, targetWorld.worldProvider.getDimensionManager(), true, location, true);
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
      WorldServer worldServer = ((CraftWorld) block.getWorld()).getHandle();
      ChunkSnapshot chunkSnapshot =
          block
              .getWorld()
              .getEmptyChunkSnapshot(block.getX() >> 4, block.getZ() >> 4, false, false);
      int chunkX = block.getX() & 15;
      int chunkZ = block.getZ() & 15;
      if (CMIMaterial.isAir(chunkSnapshot.getBlockType(chunkX, block.getY(), chunkZ))) {
        cmiHitBox.setMin(new Vector(1, 1, 1));
        cmiHitBox.setMax(new Vector(0, 0, 0));
      } else {
        try {
          IBlockData blockData = worldServer.getType(value);
          VoxelShape voxelShape = blockData.getCollisionShape(worldServer, value);
          if (!voxelShape.isEmpty()) {
            AxisAlignedBB box = voxelShape.getBoundingBox();
            cmiHitBox.setMin(new Vector(box.a(EnumAxis.X), box.a(EnumAxis.Y), box.a(EnumAxis.Z)));
            cmiHitBox.setMax(new Vector(box.b(EnumAxis.X), box.b(EnumAxis.Y), box.b(EnumAxis.Z)));
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
                    fallbackBox.a(EnumAxis.X),
                    fallbackBox.a(EnumAxis.Y),
                    fallbackBox.a(EnumAxis.Z)));
            cmiHitBox.setMax(
                new Vector(
                    fallbackBox.b(EnumAxis.X),
                    fallbackBox.b(EnumAxis.Y),
                    fallbackBox.b(EnumAxis.Z)));
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
              BlockCollisionOption.COLLIDER,
              flag ? FluidCollisionOption.ANY : FluidCollisionOption.NONE,
              null);
      MovingObjectPositionBlock hitPosition = value.getHandle().rayTrace(trace);
      RayTraceResult rayTraceResult = CraftRayTraceResult.fromNMS(value, hitPosition);
      return rayTraceResult == null ? null : rayTraceResult.getHitBlock();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }
  }
}
