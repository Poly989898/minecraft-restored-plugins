package com.Zrips.CMI.NBT;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Holograms.CMIDataWatcher;
import com.Zrips.CMI.Modules.Holograms.CMIHoloLineType;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import com.Zrips.CMI.utils.Util;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.PersistentData.CMIItemPersistentDataContainer;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection.EnumAxis;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTReadLimiter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R2.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class v1_21_R2 implements NMS {
  private boolean informed = false;

  @Override
  public Player getPlayer(CMIUser cmiUser) {
    if (!CMI.getInstance().isServerLoaded()) {
      return null;
    }

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
      WorldServer worldServer = value.J();
      ClientInformation clientInformation =
          new ClientInformation(
              "EN",
              3,
              EnumChatVisibility.a,
              true,
              0,
              EnumMainHand.a,
              false,
              false,
              ParticleStatus.a);
      EntityPlayer entityPlayer =
          new EntityPlayer(value, worldServer, gameProfile, clientInformation);
      CraftPlayer craftPlayer = entityPlayer.getBukkitEntity();
      if (craftPlayer != null) {
        craftPlayer.loadData();

        try {
          Location location = cmiUser.getLogOutLocation();
          if (location != null
              && location.getWorld() != null
              && !location.getWorld().equals(craftPlayer.getWorld())
              && craftPlayer.getLocation().toVector().distance(location.toVector()) < 1.0) {
            entityPlayer.spawnIn(((CraftWorld) location.getWorld()).getHandle());
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }

        return craftPlayer;
      }
    } catch (Throwable throwable2) {
      throwable2.printStackTrace();
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
      WorldServer worldServer = value.J();
      ClientInformation clientInformation =
          new ClientInformation(
              "EN",
              3,
              EnumChatVisibility.a,
              true,
              0,
              EnumMainHand.a,
              false,
              false,
              ParticleStatus.a);
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

      WorldServer worldServer = value.y();
      if (worldServer == null) {
        return;
      }

      worldServer.a(value, RemovalReason.c);
      AdvancementDataPlayer advancementData = value.S();
      advancementData.a();
      DedicatedServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
      PlayerList playerList = minecraftServer.ag();
      playerList.l.remove(value);
      ((CraftServer) Bukkit.getServer())
          .getScoreboardManager()
          .removePlayer(value.getBukkitEntity());
    } catch (Throwable throwable) {
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

  private static NBTTagCompound setDimension(NBTTagCompound value, int dimensionId) {
    value.a("Dimension", dimensionId);
    return value;
  }

  private static NBTTagCompound setLocationWorld(
      NBTTagCompound value, long longValue, long worldUuidMost) {
    value.a("WorldUUIDLeast", longValue);
    value.a("WorldUUIDMost", worldUuidMost);
    return value;
  }

  public static String getName(NBTTagCompound value) {
    NBTTagCompound bukkit = value.p("bukkit");
    return bukkit.l("lastKnownName");
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
      NBTTagList effects = value.c("ActiveEffects", 10);
      boolean flag = false;

      for (int i = 0; i < effects.size(); i++) {
        NBTTagCompound effect = effects.a(i);
        if (effect.f("Id") == potionEffect.getType().getId()) {
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

  public static void sendPacket(Player player, PacketPlayOutScoreboardTeam value) {
    ((CraftPlayer) player).getHandle().f.b(value);
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
    NBTTagCompound value = new NBTTagCompound();
    net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
    NBTTagCompound savedTag = nmsEntity.f(value);
    if (object instanceof String) {
      savedTag.a(string, (String) object);
    } else if (object instanceof Short) {
      savedTag.a(string, (Short) object);
    } else if (object instanceof Byte) {
      savedTag.a(string, (Byte) object);
    }

    nmsEntity.g(value);
  }

  private static IRegistryCustom getRegistry() {
    ((CraftServer) Bukkit.getServer()).getServer();
    return DedicatedServer.getDefaultRegistryAccess();
  }

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    net.minecraft.world.item.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      String string = enchantment.getKey().getKey();
      NBTTagCompound tag = (NBTTagCompound) value.a(getRegistry());
      boolean flag = false;
      if (tag != null && tag.e("ench")) {
        NBTTagList enchList = tag.c("ench", 10);

        for (int i = 0; i < enchList.size(); i++) {
          if (enchList.a(i).l("id").equalsIgnoreCase(string)) {
            enchList.remove(i);
            flag = true;
            break;
          }
        }

        if (flag) {
          tag.a("ench", enchList);
        }

        value.b(getRegistry(), tag);
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
    CraftPlayer craftPlayer = (CraftPlayer) player;

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
              value.aq(),
              78,
              new Vec3D(0.0, 0.0, 0.0),
              0.0);
      craftPlayer.getHandle().f.b(spawnPacket);
      CraftArmorStand craftArmorStand = (CraftArmorStand) value.getBukkitEntity();
      int entityId = craftArmorStand.getEntityId();
      craftPlayer.getHandle().f.b(new PacketPlayOutEntityMetadata(entityId, value.au().c()));
      if (itemStack != null) {
        var arrayList = Lists.newArrayList();
        arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        PacketPlayOutEntityEquipment equipmentPacket =
            new PacketPlayOutEntityEquipment(entityId, arrayList);
        craftPlayer.getHandle().f.b(equipmentPacket);
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
      CraftPlayer value = (CraftPlayer) player;

      try {
        PacketPlayOutEntityDestroy destroyPacket =
            new PacketPlayOutEntityDestroy(new int[] {integer});
        value.getHandle().f.b(destroyPacket);
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
      CraftArmorStand craftArmorStand = (CraftArmorStand) value.getBukkitEntity();
      craftArmorStand.setCustomNameVisible(
          string != null
              && !string.isEmpty()
              && !CMIChatColor.stripColor(string).isEmpty()
              && !string.replace(" ", "").isEmpty());
      IChatBaseComponent chatComponent = CraftChatMessage.fromString(string)[0];
      value.b(chatComponent);
      PacketPlayOutEntityMetadata metadataPacket =
          new PacketPlayOutEntityMetadata(cmiDataWatcher.getId(), value.au().c());
      CraftPlayer craftPlayer = (CraftPlayer) player;

      try {
        craftPlayer.getHandle().f.b(metadataPacket);
        var arrayList = Lists.newArrayList();
        arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        PacketPlayOutEntityEquipment equipmentPacket =
            new PacketPlayOutEntityEquipment(cmiDataWatcher.getId(), arrayList);
        craftPlayer.getHandle().f.b(equipmentPacket);
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
            CraftItemStack.asNMSCopy(itemStack),
            0.0,
            0.0,
            0.0);
    itemEntity.a_(location.getX(), location.getY(), location.getZ());
    itemEntity.getBukkitEntity().setGravity(false);
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
            itemEntity.aq(),
            78,
            new Vec3D(0.0, 0.0, 0.0),
            0.0);
    value.f.b(spawnPacket);
    PacketPlayOutEntityMetadata metadataPacket =
        new PacketPlayOutEntityMetadata(craftEntity.getEntityId(), itemEntity.au().c());
    value.f.b(metadataPacket);
    PacketPlayOutEntityVelocity velocityPacket =
        new PacketPlayOutEntityVelocity(craftEntity.getEntityId(), new Vec3D(0.0, 0.0, 0.0));
    value.f.b(velocityPacket);
    return new CMIDataWatcher(craftEntity.getEntityId(), itemEntity, CMIHoloLineType.icon);
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
  public void updatePlayerViaPackets(Player player) {
    CraftPlayer value = (CraftPlayer) player;
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    if (cmiUser != null) {
      CMIScheduler.runTask(
          CMI.getInstance(),
          () -> {
            try {
              player.leaveVehicle();

              for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!otherPlayer.getUniqueId().equals(player.getUniqueId())
                    && otherPlayer.canSee(player)) {
                  otherPlayer.hidePlayer(CMI.getInstance(), player);
                  otherPlayer.showPlayer(CMI.getInstance(), player);
                }
              }

              EntityPlayer playerHandle = value.getHandle();
              PlayerConnection connection = playerHandle.f;
              connection.b(new ClientboundPlayerInfoRemovePacket(List.of(value.getUniqueId())));
              connection.b(ClientboundPlayerInfoUpdatePacket.a(List.of(playerHandle)));

              try {
                WorldServer worldServer = playerHandle.y();
                connection.b(new PacketPlayOutRespawn(playerHandle.b(worldServer), (byte) 3));
              } catch (Throwable throwable) {
                throwable.printStackTrace();
              }

              Location location = player.getLocation();
              PositionMoveRotation positionMoveRotation = PositionMoveRotation.a(playerHandle);
              connection.b(
                  new PacketPlayOutPosition(
                      player.getEntityId(), positionMoveRotation, new HashSet()));
              DedicatedPlayerList dedicatedPlayerList =
                  ((CraftServer) Bukkit.getServer()).getHandle();
              dedicatedPlayerList.a(
                  playerHandle, ((CraftWorld) player.getLocation().getWorld()).getHandle());
              player.recalculatePermissions();
              dedicatedPlayerList.d(playerHandle);
              dedicatedPlayerList.e(playerHandle);
              playerHandle.z();
              player.giveExp(0);
              player.sendHealthUpdate();

              for (MobEffect mobEffect : playerHandle.eB()) {
                connection.b(new PacketPlayOutEntityEffect(playerHandle.aG(), mobEffect, true));
              }

              player.updateInventory();
            } catch (Throwable throwable2) {
              throwable2.printStackTrace();
            }
          });
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
    CMILib.getInstance().getReflectionManager().playEffect(player, location, cmiEffect);
  }

  @Override
  public boolean forceTeleport(Player player, Location location) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    if (player.getHealth() != 0.0
        && (!player.isDead() || player.getGameMode().equals(GameMode.SPECTATOR))) {
      if (value.f == null) {
        return false;
      }

      player.closeInventory();
      if (player.getLocation().getWorld() == location.getWorld()) {
        if (Version.isFolia()) {
          CMIScheduler.runAtEntity(CMI.getInstance(), player, () -> value.f.teleport(location));
        } else {
          value.f.teleport(location);
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
        VoxelShape voxelShape = blockData.b(worldServer, value);
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

      AxisAlignedBB value = ((CraftEntity) entity).getHandle().cR();
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
    RayTraceResult rayTraceResult =
        world.rayTraceBlocks(
            vector.toLocation(world),
            direction,
            distance,
            flag ? FluidCollisionMode.ALWAYS : FluidCollisionMode.NEVER);
    return rayTraceResult == null ? null : rayTraceResult.getHitBlock();
  }
}
