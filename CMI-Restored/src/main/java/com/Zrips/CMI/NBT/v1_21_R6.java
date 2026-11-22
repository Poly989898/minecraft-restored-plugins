package com.Zrips.CMI.NBT;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIPlayerConnection;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Holograms.CMIDataWatcher;
import com.Zrips.CMI.Modules.Holograms.CMIHoloLineType;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import com.Zrips.CMI.utils.Util;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.PersistentData.CMIItemPersistentDataContainer;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection.EnumAxis;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTReadLimiter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.EntityEffect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_21_R6.CraftServer;
import org.bukkit.craftbukkit.v1_21_R6.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R6.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class v1_21_R6 implements NMS {
  private static Constructor<?> constructor = null;
  private boolean informed = false;

  @Override
  public Player getPlayer(CMIUser cmiUser) {
    if (CMI.getInstance().isFullyLoaded()
        && !cmiUser.isFakeAccount()
        && cmiUser.getName(false) != null) {
      File file = Util.getFile(cmiUser.getUniqueId());
      if (file != null && file.canRead() && file.exists()) {
        try {
          DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
          GameProfile gameProfile = new GameProfile(cmiUser.getUniqueId(), cmiUser.getName(false));
          Location location = cmiUser.getLogOutLocation();
          WorldServer worldServer = null;
          if (location != null && location.getWorld() != null) {
            worldServer = ((CraftWorld) location.getWorld()).getHandle();
          } else {
            worldServer = value.aL();
          }

          ClientInformation clientInformation = ClientInformation.a();
          EntityPlayer entityPlayer =
              new EntityPlayer(value, worldServer, gameProfile, clientInformation);
          CraftPlayer craftPlayer = entityPlayer.getBukkitEntity();
          if (craftPlayer != null) {
            craftPlayer.loadData();
            return craftPlayer;
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }

        return null;
      } else {
        return null;
      }
    } else {
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
      WorldServer worldServer = value.aL();
      ClientInformation clientInformation = ClientInformation.a();
      EntityPlayer entityPlayer =
          new EntityPlayer(value, worldServer, gameProfile, clientInformation);
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

      WorldServer worldServer = value.A();
      if (worldServer == null) {
        return;
      }

      worldServer.a(value, RemovalReason.d);
      AdvancementDataPlayer advancementData = value.U();
      advancementData.a();
      DedicatedServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
      PlayerList playerList = minecraftServer.am();
      playerList.k.remove(value);
      ((CraftServer) Bukkit.getServer())
          .getScoreboardManager()
          .removePlayer(value.getBukkitEntity());
    } catch (Throwable throwable) {
      if (Version.isTestServer()) {
        throwable.printStackTrace();
      }
    }
  }

  public NBTTagCompound getNBTTagC(File file) {
    NBTTagCompound value = null;

    try {
      value = NBTCompressedStreamTools.a(file.toPath(), NBTReadLimiter.a());
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }

    return value;
  }

  public NBTTagCompound getNBTTagC(UUID uuid) {
    File file = Util.getFile(uuid);
    return !file.exists() ? null : this.getNBTTagC(file);
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
    if (!player.isOnline()) {
      NBTTagCompound value = this.getNBTTagC(player.getUniqueId());
      if (value != null) {
        value = this.setMiscLocation(location, value);
        saveData(value, player);
      }
    }
  }

  private static NBTTagCompound setLocationWorld(
      NBTTagCompound value, long longValue, long worldUuidMost) {
    value.a("WorldUUIDLeast", longValue);
    value.a("WorldUUIDMost", worldUuidMost);
    return value;
  }

  private static long getUUIDWorldMost(NBTTagCompound value) {
    return (Long) value.f("WorldUUIDMost").get();
  }

  private static long getUUIDWorldLeast(NBTTagCompound value) {
    return (Long) value.f("WorldUUIDLeast").get();
  }

  public NBTTagCompound setMiscLocation(Location location, NBTTagCompound value) {
    value =
        setLocationWorld(
            value,
            location.getWorld().getUID().getLeastSignificantBits(),
            location.getWorld().getUID().getMostSignificantBits());
    value.a("Dimension", "minecraft:" + location.getWorld().getEnvironment().name());
    value = setLocationPosition(value, location.getX(), location.getY(), location.getZ());
    return setLocationView(value, location.getYaw(), location.getPitch());
  }

  private static NBTTagCompound setLocationPosition(
      NBTTagCompound value, double x, double y, double z) {
    NBTTagList posList = new NBTTagList();
    posList.add(NBTTagDouble.a(x));
    posList.add(NBTTagDouble.a(y));
    posList.add(NBTTagDouble.a(z));
    value.a("Pos", posList);
    return value;
  }

  private static NBTTagCompound setLocationView(NBTTagCompound value, float yaw, float pitch) {
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
  private static void saveData(NBTTagCompound value, File file) {
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

  private static void saveData(NBTTagCompound value, Player player) {
    if (value != null) {
      saveData(value, Util.getFile(player));
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
      NBTTagList effects = value.p("ActiveEffects");
      boolean flag = false;

      for (int i = 0; i < effects.size(); i++) {
        NBTTagCompound effect = (NBTTagCompound) effects.a(i).get();
        if ((Integer) effect.e("Id").get() == potionEffect.getType().getId()) {
          flag = true;
          effect.a("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
          effect.a("Amplifier", (byte) potionEffect.getAmplifier());
          effect.a("Duration", potionEffect.getDuration());
          effect.a("ShowParticles", (byte) (potionEffect.hasParticles() ? 1 : 0));
          effects.a(i, effect);
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
  public void resendChunk(Chunk chunk, Player player) {}

  @Override
  public void unloadChunk(Chunk chunk, Player player) {}

  @Override
  public void updateCollisions(Player player, boolean collidable, boolean pushable) {}

  private static void sendPacket(Player player, Packet<?> value) {
    ((CraftPlayer) player).getHandle().g.b(value);
  }

  public Inventory getShulkerInv(ItemStack itemStack) {
    if (!CMIMaterial.get(itemStack).isShulkerBox()) {
      return null;
    }

    try {
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (!(itemMeta instanceof BlockStateMeta)) {
        return null;
      }

      BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
      if (!(blockStateMeta.getBlockState() instanceof ShulkerBox)) {
        return null;
      }

      ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
      return shulkerBox.getInventory();
    } catch (Throwable throwable) {
      return null;
    }
  }

  public Inventory getShulkerInv(Block block) {
    if (CMIMaterial.get(block).isShulkerBox() && block.getState() instanceof ShulkerBox) {
      ShulkerBox shulkerBox = (ShulkerBox) block.getState();
      return shulkerBox.getInventory() == null ? null : shulkerBox.getInventory();
    } else {
      return null;
    }
  }

  @Override
  public int getTickLived(UUID uuid) {
    if (uuid == null) {
      return 0;
    }

    NBTTagCompound value = this.getNBTTagC(uuid);
    return value != null && value.b("Spigot.ticksLived")
        ? (Integer) value.e("Spigot.ticksLived").get()
        : 0;
  }

  @Override
  public void showResurection(Player player) {
    showResurection(player, new ItemStack(Material.TOTEM_OF_UNDYING));
  }

  private static void showResurection(Player player, ItemStack itemStack) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    if (value instanceof EntityPlayer) {
      EntityPlayer entityPlayer = value;
      entityPlayer.b(StatisticList.c.b(Items.sw));
      CriterionTriggers.C.a(entityPlayer, CraftItemStack.asNMSCopy(itemStack));
    }

    player.setHealth(1.0);

    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
      player.removePotionEffect(potionEffect.getType());
    }

    if (CMI.getInstance().getTotemManager().getFireResistanceDuration() > 0) {
      player.addPotionEffect(
          new PotionEffect(
              PotionEffectType.FIRE_RESISTANCE,
              CMI.getInstance().getTotemManager().getFireResistanceDuration() * 20,
              1));
    }

    if (CMI.getInstance().getTotemManager().getAbsorbtionDuration() > 0) {
      player.addPotionEffect(
          new PotionEffect(
              PotionEffectType.ABSORPTION,
              CMI.getInstance().getTotemManager().getAbsorbtionDuration() * 20,
              1));
    }

    if (CMI.getInstance().getTotemManager().getRegenerationDuration() > 0) {
      player.addPotionEffect(
          new PotionEffect(
              PotionEffectType.REGENERATION,
              CMI.getInstance().getTotemManager().getRegenerationDuration() * 20,
              1));
    }

    player.playEffect(EntityEffect.TOTEM_RESURRECT);
  }

  @Override
  public void setEntityTag(Entity entity, String string, Object object) {
    new NBTTagCompound();
    net.minecraft.world.entity.Entity value = ((CraftEntity) entity).getHandle();
  }

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    int value = itemStack.removeEnchantment(enchantment);
    return value > 0 ? itemStack : null;
  }

  @Override
  public ItemStack setNBTList(ItemStack itemStack, String string, List<String> list) {
    CMIItemPersistentDataContainer cmiItemPersistentDataContainer =
        new CMIItemPersistentDataContainer(itemStack);
    if (list == null) {
      cmiItemPersistentDataContainer.remove(string);
    } else {
      cmiItemPersistentDataContainer.set(string, list);
    }

    cmiItemPersistentDataContainer.save();
    return itemStack;
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
    player.sendExperienceChange(player.getExp());
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
    CraftArmorStand craftArmorStand = (CraftArmorStand) value.getBukkitEntity();
    craftArmorStand.setVisible(false);
    craftArmorStand.setArms(false);
    craftArmorStand.setMarker(true);
    craftArmorStand.setGravity(false);
    craftArmorStand.setBasePlate(false);
    craftArmorStand.setSmall(flag);
    if (flag) {
      craftArmorStand.setRightArmPose(new EulerAngle(-15.0, -45.0, 0.0));
    }

    if (string != null && !string.isEmpty()) {
      craftArmorStand.setCustomName(CMIChatColor.translate(string));
      craftArmorStand.setCustomNameVisible(
          !CMIChatColor.stripColor(string).isEmpty() && !string.replace(" ", "").isEmpty());
    } else {
      craftArmorStand.setCustomNameVisible(false);
    }

    return value;
  }

  @Override
  public CMIDataWatcher spawnHologram(
      Player player, Location location, String string, ItemStack itemStack) {
    EntityArmorStand value = createStand(location, string, itemStack != null);

    try {
      CraftEntity craftEntity = value.getBukkitEntity();
      location = craftEntity.getLocation();
      PacketPlayOutSpawnEntity spawnPacket =
          new PacketPlayOutSpawnEntity(
              craftEntity.getEntityId(),
              craftEntity.getUniqueId(),
              location.getX(),
              location.getY(),
              location.getZ(),
              location.getPitch(),
              location.getYaw(),
              value.ax(),
              78,
              new Vec3D(0.0, 0.0, 0.0),
              0.0);
      sendPacket(player, spawnPacket);
      CraftArmorStand craftArmorStand = (CraftArmorStand) value.getBukkitEntity();
      int entityId = craftArmorStand.getEntityId();
      sendPacket(player, getEntityMetaDataPacket(entityId, value));
      if (itemStack != null) {
        var arrayList = Lists.newArrayList();
        arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        PacketPlayOutEntityEquipment equipmentPacket =
            new PacketPlayOutEntityEquipment(entityId, arrayList);
        sendPacket(player, equipmentPacket);
      }

      return new CMIDataWatcher(
          entityId,
          value,
          itemStack != null ? CMIHoloLineType.staticIcon : CMIHoloLineType.plainText);
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public FakeInfo spawnClickable(Player player, Location location, Object object, boolean flag) {
    return null;
  }

  @Override
  public void removeHologram(Player player, Integer integer) {
    if (integer != null) {
      try {
        PacketPlayOutEntityDestroy value = new PacketPlayOutEntityDestroy(new int[] {integer});
        sendPacket(player, value);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  private static Packet getEntityMetaDataPacket(
      int value, net.minecraft.world.entity.Entity nmsEntity) {
    if (constructor == null) {
      try {
        var packetClass =
            Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata");
        constructor = packetClass.getConstructor(int.class, List.class);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }

    try {
      return (Packet) constructor.newInstance(value, nmsEntity.aC().c());
    } catch (Exception exception2) {
      exception2.printStackTrace();
      return null;
    }
  }

  @Override
  public void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String string, ItemStack itemStack) {
    if (cmiDataWatcher != null && cmiDataWatcher.getStand() instanceof EntityArmorStand) {
      EntityArmorStand value = (EntityArmorStand) cmiDataWatcher.getStand();
      CraftArmorStand craftArmorStand = (CraftArmorStand) value.getBukkitEntity();
      craftArmorStand.setCustomNameVisible(
          string != null
              && !string.isEmpty()
              && !CMIChatColor.stripColor(string).isEmpty()
              && !string.replace(" ", "").isEmpty());
      IChatBaseComponent chatComponent = CraftChatMessage.fromString(string)[0];
      value.b(chatComponent);

      try {
        sendPacket(player, getEntityMetaDataPacket(cmiDataWatcher.getId(), value));
        var arrayList = Lists.newArrayList();
        arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        PacketPlayOutEntityEquipment equipmentPacket =
            new PacketPlayOutEntityEquipment(cmiDataWatcher.getId(), arrayList);
        sendPacket(player, equipmentPacket);
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
    itemEntity.a_(location.getX(), location.getY(), location.getZ());
    itemEntity.getBukkitEntity().setGravity(false);
    itemEntity.e(true);
    if (flag) {
      itemEntity.getBukkitEntity().setGlowing(true);
    }

    CraftEntity craftEntity = itemEntity.getBukkitEntity();
    Location entityLocation = craftEntity.getLocation();
    PacketPlayOutSpawnEntity spawnPacket =
        new PacketPlayOutSpawnEntity(
            craftEntity.getEntityId(),
            craftEntity.getUniqueId(),
            entityLocation.getX(),
            entityLocation.getY(),
            entityLocation.getZ(),
            entityLocation.getPitch(),
            entityLocation.getYaw(),
            itemEntity.ax(),
            0,
            new Vec3D(0.0, 0.0, 0.0),
            0.0);
    sendPacket(player, spawnPacket);
    sendPacket(player, getEntityMetaDataPacket(craftEntity.getEntityId(), itemEntity));
    PacketPlayOutEntityVelocity velocityPacket =
        new PacketPlayOutEntityVelocity(craftEntity.getEntityId(), new Vec3D(0.0, 0.0, 0.0));
    sendPacket(player, velocityPacket);
    return new CMIDataWatcher(craftEntity.getEntityId(), itemEntity, CMIHoloLineType.icon);
  }

  @Override
  public boolean clearSkin(Player player) {
    if (!Version.isPaperBranch()) {
      return false;
    }

    PlayerProfile playerProfile = player.getPlayerProfile();
    PlayerTextures playerTextures = playerProfile.getTextures();
    playerTextures.clear();
    playerProfile.setTextures(playerTextures);
    playerProfile
        .update()
        .whenComplete(
            (completedProfile, throwable) ->
                CMIScheduler.runTask(
                    CMI.getInstance(), () -> player.setPlayerProfile(completedProfile)));
    return true;
  }

  @Override
  public void updatePlayerViaPackets(Player player) {}

  @Override
  public boolean forceTeleport(Player player, Location location) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    if (player.getHealth() != 0.0
        && (!player.isDead() || player.getGameMode().equals(GameMode.SPECTATOR))) {
      player.closeInventory();
      if (player.getLocation().getWorld() == location.getWorld()) {
        if (Version.isFolia()) {
          CMIScheduler.runAtEntity(CMI.getInstance(), player, () -> value.g.teleport(location));
        } else {
          ((PlayerConnection) CMIPlayerConnection.getConnection(player))
              .teleport(
                  location.getX(),
                  location.getY(),
                  location.getZ(),
                  location.getYaw(),
                  location.getPitch(),
                  TeleportCause.PLUGIN);
        }
      } else {
        try {
          if (Version.isFolia()) {
            CMI.getInstance().getTeleportHandler().teleportPlayer(player, location, false);
          } else {
            ((CraftServer) Bukkit.getServer())
                .getHandle()
                .respawn(value, true, RemovalReason.a, RespawnReason.PLUGIN, location);
          }
        } catch (Throwable throwable) {
          CMIMessages.consoleMessage("&5Try updating server to fix this issue.");
          throwable.printStackTrace();
        }
      }

      return true;
    } else {
      return false;
    }
  }

  @Override
  public CMIHitBox getBlockHitBox(CMIHitBox cmiHitBox) {
    try {
      Block block = cmiHitBox.getBlock();
      if (block == null) {
        return cmiHitBox;
      }

      if (CMIMaterial.isAir(block.getWorld().getBlockData(block.getLocation()).getMaterial())) {
        cmiHitBox.setMin(new Vector(1, 1, 1));
        cmiHitBox.setMax(new Vector(0, 0, 0));
      } else {
        BlockPosition value = new BlockPosition(block.getX(), block.getY(), block.getZ());
        WorldServer worldServer = ((CraftWorld) block.getWorld()).getHandle();
        IBlockData blockData = worldServer.a_(value);
        VoxelShape voxelShape = blockData.f(worldServer, value);
        if (!voxelShape.c()) {
          AxisAlignedBB box = voxelShape.a();
          cmiHitBox.setMin(new Vector(box.a(EnumAxis.a), box.a(EnumAxis.b), box.a(EnumAxis.c)));
          cmiHitBox.setMax(new Vector(box.b(EnumAxis.a), box.b(EnumAxis.b), box.b(EnumAxis.c)));
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

      AxisAlignedBB value = ((CraftEntity) entity).getHandle().de();
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

    Location location = player.getEyeLocation();
    Vector vector = location.getDirection().normalize().multiply(value);
    Vector unitVector = vector.normalize();
    Vector offset = unitVector.multiply(0.75);
    location.add(offset);
    RayTraceResult rayTraceResult =
        player.rayTraceBlocks(value, flag ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER);
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
    Vector direction = end.clone().subtract(vector).normalize();
    double distance = vector.distance(end);

    try {
      RayTraceResult rayTraceResult =
          world.rayTraceBlocks(
              vector.toLocation(world),
              direction,
              distance,
              flag ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER);
      return rayTraceResult == null ? null : rayTraceResult.getHitBlock();
    } catch (Throwable throwable) {
      if (!this.informed) {
        throwable.printStackTrace();
        this.informed = true;
      }

      return null;
    }
  }
}
