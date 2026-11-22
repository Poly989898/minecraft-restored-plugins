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
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_21_R7.CraftServer;
import org.bukkit.craftbukkit.v1_21_R7.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R7.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class v1_21_R7 implements NMS {
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
          WorldServer level = null;
          if (location != null && location.getWorld() != null) {
            level = ((CraftWorld) location.getWorld()).getHandle();
          } else {
            level = value.aH();
          }

          ClientInformation clientInfo = ClientInformation.a();
          EntityPlayer serverPlayer = new EntityPlayer(value, level, gameProfile, clientInfo);
          CraftPlayer craftPlayer = serverPlayer.getBukkitEntity();
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
      WorldServer level = value.aH();
      ClientInformation clientInfo = ClientInformation.a();
      EntityPlayer serverPlayer = new EntityPlayer(value, level, gameProfile, clientInfo);
      CraftPlayer craftPlayer = serverPlayer.getBukkitEntity();
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

      WorldServer level = value.A();
      if (level == null) {
        return;
      }

      level.a(value, RemovalReason.d);
      AdvancementDataPlayer advancements = value.U();
      advancements.a();
      DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
      PlayerList playerList = server.aj();
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
      NBTTagCompound value, long longValue, long longValue2) {
    value.a("WorldUUIDLeast", longValue);
    value.a("WorldUUIDMost", longValue2);
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

      for (int index = 0; index < effects.size(); index++) {
        NBTTagCompound effect = (NBTTagCompound) effects.a(index).get();
        if ((Integer) effect.e("Id").get() == potionEffect.getType().getId()) {
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
  public void resendChunk(Chunk chunk, Player player) {}

  @Override
  public void unloadChunk(Chunk chunk, Player player) {}

  @Override
  public void updateCollisions(Player player, boolean collisionEnabled, boolean selfOnly) {}

  private static void sendPacket(Player player, Packet<?> value) {
    ((CraftPlayer) player).getHandle().g.b(value);
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
  public void setEntityTag(Entity entity, String tagName, Object tagValue) {
    new NBTTagCompound();
    net.minecraft.world.entity.Entity value = ((CraftEntity) entity).getHandle();
  }

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    int value = itemStack.removeEnchantment(enchantment);
    return value > 0 ? itemStack : null;
  }

  @Override
  public ItemStack setNBTList(ItemStack itemStack, String key, List<String> values) {
    CMIItemPersistentDataContainer cmiItemPersistentDataContainer =
        new CMIItemPersistentDataContainer(itemStack);
    if (values == null) {
      cmiItemPersistentDataContainer.remove(key);
    } else {
      cmiItemPersistentDataContainer.set(key, values);
    }

    cmiItemPersistentDataContainer.save();
    return itemStack;
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
    player.sendExperienceChange(player.getExp());
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
    CraftArmorStand craftStand = (CraftArmorStand) value.getBukkitEntity();
    craftStand.setVisible(false);
    craftStand.setArms(false);
    craftStand.setMarker(true);
    craftStand.setGravity(false);
    craftStand.setBasePlate(false);
    craftStand.setSmall(small);
    if (small) {
      craftStand.setRightArmPose(new EulerAngle(-15.0, -45.0, 0.0));
    }

    if (text != null && !text.isEmpty()) {
      craftStand.setCustomName(CMIChatColor.translate(text));
      craftStand.setCustomNameVisible(
          !CMIChatColor.stripColor(text).isEmpty() && !text.replace(" ", "").isEmpty());
    } else {
      craftStand.setCustomNameVisible(false);
    }

    return value;
  }

  @Override
  public CMIDataWatcher spawnHologram(
      Player player, Location location, String text, ItemStack itemStack) {
    EntityArmorStand value = createStand(location, text, itemStack != null);

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
              value.ay(),
              78,
              new Vec3D(0.0, 0.0, 0.0),
              0.0);
      sendPacket(player, spawnPacket);
      CraftArmorStand craftStand = (CraftArmorStand) value.getBukkitEntity();
      int entityId = craftStand.getEntityId();
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
  public FakeInfo spawnClickable(Player player, Location location, Object data, boolean glowing) {
    return null;
  }

  @Override
  public void removeHologram(Player player, Integer entityId) {
    if (entityId != null) {
      try {
        PacketPlayOutEntityDestroy value = new PacketPlayOutEntityDestroy(new int[] {entityId});
        sendPacket(player, value);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  private static Packet getEntityMetaDataPacket(
      int value, net.minecraft.world.entity.Entity entity) {
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
      return (Packet) constructor.newInstance(value, entity.aD().c());
    } catch (Exception exception2) {
      exception2.printStackTrace();
      return null;
    }
  }

  @Override
  public void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String text, ItemStack itemStack) {
    if (cmiDataWatcher != null && cmiDataWatcher.getStand() instanceof EntityArmorStand) {
      EntityArmorStand value = (EntityArmorStand) cmiDataWatcher.getStand();
      CraftArmorStand craftStand = (CraftArmorStand) value.getBukkitEntity();
      craftStand.setCustomNameVisible(
          text != null
              && !text.isEmpty()
              && !CMIChatColor.stripColor(text).isEmpty()
              && !text.replace(" ", "").isEmpty());
      IChatBaseComponent component = CraftChatMessage.fromString(text)[0];
      value.b(component);

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
      Player player, Location location, ItemStack itemStack, boolean glowing) {
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
    if (glowing) {
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
            itemEntity.ay(),
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
            (profile, throwable) ->
                CMIScheduler.runTask(CMI.getInstance(), () -> player.setPlayerProfile(profile)));
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
        WorldServer level = ((CraftWorld) block.getWorld()).getHandle();
        IBlockData blockState = level.a_(value);
        VoxelShape shape = blockState.f(level, value);
        if (!shape.c()) {
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

      AxisAlignedBB value = ((CraftEntity) entity).getHandle().dj();
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

    Location location = player.getEyeLocation();
    Vector vector = location.getDirection().normalize().multiply(value);
    Vector normalized = vector.normalize();
    Vector step = normalized.multiply(0.75);
    location.add(step);
    RayTraceResult rayTraceResult =
        player.rayTraceBlocks(
            value, includeFluids ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER);
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
    Vector direction = end.clone().subtract(start).normalize();
    double distance = start.distance(end);

    try {
      RayTraceResult rayTraceResult =
          world.rayTraceBlocks(
              start.toLocation(world),
              direction,
              distance,
              includeFluids ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER);
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
