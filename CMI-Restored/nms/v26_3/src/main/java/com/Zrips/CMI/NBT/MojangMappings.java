package com.Zrips.CMI.NBT;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Components.CMIComponentPaper;
import com.Zrips.CMI.Components.CMIComponentRoot;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Display.CMIDisplayType;
import com.Zrips.CMI.Modules.Holograms.CMIDataWatcher;
import com.Zrips.CMI.Modules.Holograms.CMIHoloLineType;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import com.Zrips.CMI.Modules.Packets.PacketHandler;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.papermc.paper.adventure.PaperAdventure;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.SignTextSlot;
import net.minecraft.world.level.dimension.DimensionType;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIPlayerConnection;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.PersistentData.CMIItemPersistentDataContainer;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.EntityEffect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftArmorStand;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class MojangMappings implements NMS {
  private boolean informed = false;

  private static File getFile(UUID uuid) {
    ServerLevel value = ((CraftServer) Bukkit.getServer()).getServer().overworld();
    Path path = value.getServer().getWorldPath(LevelResource.PLAYER_DATA_DIR);
    Path datFile = path.resolve(uuid + ".dat");
    return datFile.toFile();
  }

  @Override
  public Player getPlayer(CMIUser cmiUser) {
    if (CMI.getInstance().isFullyLoaded()
        && !cmiUser.isFakeAccount()
        && cmiUser.getName(false) != null) {
      File file = getFile(cmiUser.getUniqueId());
      if (file != null && file.canRead() && file.exists()) {
        try {
          DedicatedServer value = ((CraftServer) Bukkit.getServer()).getServer();
          GameProfile gameProfile = new GameProfile(cmiUser.getUniqueId(), cmiUser.getName(false));
          Location location = cmiUser.getLogOutLocation();
          ServerLevel level = null;
          if (location != null && location.getWorld() != null) {
            level = ((CraftWorld) location.getWorld()).getHandle();
          } else {
            level = value.overworld();
          }

          ClientInformation clientInfo = ClientInformation.createDefault();
          ServerPlayer serverPlayer = new ServerPlayer(value, level, gameProfile, clientInfo);
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
      ServerLevel level = value.overworld();
      ClientInformation clientInfo = ClientInformation.createDefault();
      ServerPlayer serverPlayer = new ServerPlayer(value, level, gameProfile, clientInfo);
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

      ServerPlayer value = ((CraftPlayer) player).getHandle();
      if (value == null) {
        return;
      }

      ServerLevel level = value.level();
      if (level == null) {
        return;
      }

      level.removePlayerImmediately(value, RemovalReason.UNLOADED_TO_CHUNK);
      PlayerAdvancements advancements = value.getAdvancements();
      advancements.clearTriggers();
      ((CraftServer) Bukkit.getServer())
          .getScoreboardManager()
          .removePlayer(value.getBukkitEntity());
    } catch (Throwable throwable) {
      if (Version.isTestServer()) {
        throwable.printStackTrace();
      }
    }
  }

  public CompoundTag getNBTTagC(File file) {
    try {
      return NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }
  }

  public CompoundTag getNBTTagC(UUID uuid) {
    File file = getFile(uuid);
    return !file.exists() ? null : this.getNBTTagC(file);
  }

  @Override
  public String getWorldName(File file) {
    CompoundTag value = this.getNBTTagC(file);
    if (value == null) {
      return null;
    }

    UUID uuid = new UUID(getUUIDWorldMost(value), getUUIDWorldLeast(value));
    return CMI.getInstance().getUtilManager().getWorldName(uuid);
  }

  @Override
  public void setMiscLocation(File file, Location location) {
    CompoundTag value = this.getNBTTagC(file);
    if (value != null) {
      value = this.setMiscLocation(location, value);
      saveData(value, file);
    }
  }

  @Override
  public void setMiscLocation(Player player, Location location) {
    if (!player.isOnline()) {
      CompoundTag value = this.getNBTTagC(player.getUniqueId());
      if (value != null) {
        value = this.setMiscLocation(location, value);
        saveData(value, player);
      }
    }
  }

  private static CompoundTag setLocationWorld(CompoundTag value, long longValue, long longValue2) {
    value.putLong("WorldUUIDLeast", longValue);
    value.putLong("WorldUUIDMost", longValue2);
    return value;
  }

  private static long getUUIDWorldMost(CompoundTag value) {
    return value.getLongOr("WorldUUIDMost", 0L);
  }

  private static long getUUIDWorldLeast(CompoundTag value) {
    return value.getLongOr("WorldUUIDLeast", 0L);
  }

  public CompoundTag setMiscLocation(Location location, CompoundTag value) {
    value =
        setLocationWorld(
            value,
            location.getWorld().getUID().getLeastSignificantBits(),
            location.getWorld().getUID().getMostSignificantBits());
    value.putString("Dimension", "minecraft:" + location.getWorld().getEnvironment().name());
    value = setLocationPosition(value, location.getX(), location.getY(), location.getZ());
    return setLocationView(value, location.getYaw(), location.getPitch());
  }

  private static CompoundTag setLocationPosition(CompoundTag value, double x, double y, double z) {
    ListTag posList = new ListTag();
    posList.add(DoubleTag.valueOf(x));
    posList.add(DoubleTag.valueOf(y));
    posList.add(DoubleTag.valueOf(z));
    value.put("Pos", posList);
    return value;
  }

  private static CompoundTag setLocationView(CompoundTag value, float yaw, float pitch) {
    ListTag rotationList = new ListTag();
    rotationList.add(DoubleTag.valueOf(yaw));
    rotationList.add(DoubleTag.valueOf(pitch));
    value.put("Rotation", rotationList);
    return value;
  }

  // $VF: Could not inline inconsistent finally blocks
  // Please report this to the Vineflower issue tracker, at
  // https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the
  // rights to distribute it!)
  private static void saveData(CompoundTag value, File file) {
    if (value != null) {
      try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
        NbtIo.writeCompressed(value, fileOutputStream);
      } catch (Exception exception) {
      }
    }
  }

  private static void saveData(CompoundTag value, Player player) {
    if (value != null) {
      saveData(value, getFile(player.getUniqueId()));
    }
  }

  @Override
  public void clearPotionEffects(Player player) {
    File file = getFile(player.getUniqueId());
    this.clearPotionEffects(file);
  }

  public void clearPotionEffects(File file) {
    CompoundTag value = this.getNBTTagC(file);
    if (value != null) {
      value.remove("ActiveEffects");
      saveData(value, file);
    }
  }

  @Override
  public void setPotionEffects(Player player, PotionEffect potionEffect) {
    File file = getFile(player.getUniqueId());
    this.setPotionEffects(potionEffect, file);
  }

  public void setPotionEffects(PotionEffect potionEffect, File file) {
    CompoundTag value = this.getNBTTagC(file);
    if (value != null) {
      ListTag effects = value.getListOrEmpty("ActiveEffects");
      boolean flag = false;

      for (int index = 0; index < effects.size(); index++) {
        CompoundTag effect = effects.getCompoundOrEmpty(index);
        if (effect.getIntOr("Id", -1) == potionEffect.getType().getId()) {
          flag = true;
          effect.putByte("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
          effect.putByte("Amplifier", (byte) potionEffect.getAmplifier());
          effect.putInt("Duration", potionEffect.getDuration());
          effect.putByte("ShowParticles", (byte) (potionEffect.hasParticles() ? 1 : 0));
          effects.add(index, effect);
          break;
        }
      }

      if (!flag) {
        CompoundTag newEffect = new CompoundTag();
        newEffect.putByte("Ambient", (byte) (potionEffect.isAmbient() ? 1 : 0));
        newEffect.putByte("Amplifier", (byte) potionEffect.getAmplifier());
        newEffect.putInt("Duration", potionEffect.getDuration());
        newEffect.putByte("Id", (byte) potionEffect.getType().getId());
        newEffect.putByte("ShowParticles", (byte) (potionEffect.hasParticles() ? 1 : 0));
        effects.add(newEffect);
      }

      value.put("ActiveEffects", effects);
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
    ((CraftPlayer) player).getHandle().connection.send(value);
  }

  @Override
  public int getTickLived(UUID uuid) {
    if (uuid == null) {
      return 0;
    }

    CompoundTag value = this.getNBTTagC(uuid);
    return value != null && value.contains("Spigot.ticksLived")
        ? value.getIntOr("Spigot.ticksLived", 0)
        : 0;
  }

  @Override
  public void showResurection(Player player) {
    showResurection(player, new ItemStack(Material.TOTEM_OF_UNDYING));
  }

  private static void showResurection(Player player, ItemStack itemStack) {
    if (player.getHealth() <= 0.0) {
      player.setHealth(1.0);
    }

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

    player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);
  }

  @Override
  public void setEntityTag(Entity entity, String tagName, Object tagValue) {}

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

  private static ArmorStand createStand(Location location, String text, boolean small) {
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

    ServerLevel value = ((CraftWorld) location.getWorld()).getHandle();
    ArmorStand stand = new ArmorStand(value, x, y, z);
    CraftArmorStand craftStand = (CraftArmorStand) stand.getBukkitEntity();
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
      CMIComponentBase cmiComponentBase =
          CMIComponentBase.containsMiniMessage(text)
              ? new CMIComponentPaper(true).add(new CMIComponentRoot(text))
              : CMIComponentBase.quickAdd(text);
      if (cmiComponentBase instanceof CMIComponentPaper) {
        craftStand.customName(((CMIComponentPaper) cmiComponentBase).getCombined());
      } else {
        craftStand.setCustomName(CMIChatColor.translate(text));
      }

      craftStand.setCustomNameVisible(
          !CMIChatColor.stripColor(text).isEmpty() && !text.replace(" ", "").isEmpty());
    } else {
      craftStand.setCustomNameVisible(false);
    }

    return stand;
  }

  @Override
  public CMIDataWatcher spawnHologram(
      Player player, Location location, String text, ItemStack itemStack) {
    ArmorStand value = createStand(location, text, itemStack != null);

    try {
      CraftEntity craftEntity = value.getBukkitEntity();
      float floatValue = location.getPitch();
      float yaw = location.getYaw();
      location = craftEntity.getLocation();
      ClientboundAddEntityPacket addPacket =
          new ClientboundAddEntityPacket(
              value.getId(),
              value.getUUID(),
              value.getX(),
              value.getY(),
              value.getZ(),
              floatValue,
              yaw,
              entityType("armor_stand"),
              0,
              Vec3.ZERO,
              0.0);
      sendPacket(player, addPacket);
      int entityId = value.getId();
      sendPacket(
          player,
          new ClientboundSetEntityDataPacket(
              entityId,
              Version.isPaperBranch()
                  ? value.getEntityData().packAll()
                  : value.getEntityData().getNonDefaultValues()));
      if (itemStack != null) {
        List list = List.of(Pair.of(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(itemStack)));
        ClientboundSetEquipmentPacket equipmentPacket =
            new ClientboundSetEquipmentPacket(entityId, list);
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
    try {
      ServerLevel value = ((CraftWorld) location.getWorld()).getHandle();
      Slime slime = new Slime((EntityType<Slime>) entityType("slime"), value);
      slime.setInvisible(true);
      slime.setNoGravity(true);
      slime.setNoAi(true);
      slime.setSize(1, true);
      slime.absSnapTo(location.getX(), location.getY() - 0.45, location.getZ() - 0.2, 0.0F, 0.0F);
      slime.setSilent(true);
      ClientboundAddEntityPacket spawnPacket =
          new ClientboundAddEntityPacket(
              slime.getId(),
              slime.getUUID(),
              location.getX(),
              location.getY(),
              location.getZ(),
              slime.getXRot(),
              slime.getYRot(),
              entityType("slime"),
              0,
              Vec3.ZERO,
              0.0);
      CMIPlayerConnection.sendPacket(player, spawnPacket);
      ClientboundSetEntityDataPacket dataPacket =
          new ClientboundSetEntityDataPacket(
              slime.getId(),
              Version.isPaperBranch()
                  ? slime.getEntityData().packAll()
                  : slime.getEntityData().getNonDefaultValues());
      CMIPlayerConnection.sendPacket(player, dataPacket);
      FakeInfo fakeInfo = new FakeInfo().setObj(data).setPlayer(player).setId(slime.getId());
      fakeInfo.setEntity(slime);
      PacketHandler.addFakeEntity(fakeInfo);
      return fakeInfo;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public void removeHologram(Player player, Integer entityId) {
    if (entityId != null) {
      try {
        ClientboundRemoveEntitiesPacket value =
            new ClientboundRemoveEntitiesPacket(new int[] {entityId});
        sendPacket(player, value);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  private static Packet<?> getEntityMetaDataPacket(
      int value, net.minecraft.world.entity.Entity entity) {
    try {
      return new ClientboundSetEntityDataPacket(
          value,
          Version.isPaperBranch()
              ? entity.getEntityData().packAll()
              : entity.getEntityData().getNonDefaultValues());
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String text, ItemStack itemStack) {
    if (cmiDataWatcher != null && cmiDataWatcher.getStand() instanceof ArmorStand) {
      ArmorStand value = (ArmorStand) cmiDataWatcher.getStand();
      CraftArmorStand craftStand = (CraftArmorStand) value.getBukkitEntity();
      boolean hasName =
          text != null
              && !text.isEmpty()
              && !CMIChatColor.stripColor(text).isEmpty()
              && !text.replace(" ", "").isEmpty();
      craftStand.setCustomNameVisible(hasName);
      CMIComponentBase cmiComponentBase =
          CMIComponentBase.containsMiniMessage(text)
              ? new CMIComponentPaper(true).add(new CMIComponentRoot(text))
              : CMIComponentBase.quickAdd(text);
      CraftArmorStand craftStandText = (CraftArmorStand) value.getBukkitEntity();
      if (cmiComponentBase instanceof CMIComponentPaper) {
        craftStandText.customName(((CMIComponentPaper) cmiComponentBase).getCombined());
      } else {
        craftStandText.setCustomName(CMIChatColor.translate(text));
      }

      try {
        sendPacket(player, getEntityMetaDataPacket(cmiDataWatcher.getId(), value));
        List list = List.of(Pair.of(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(itemStack)));
        ClientboundSetEquipmentPacket equipmentPacket =
            new ClientboundSetEquipmentPacket(cmiDataWatcher.getId(), list);
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
    ServerLevel value = ((CraftWorld) location.getWorld()).getHandle();
    ItemEntity itemEntity =
        new ItemEntity(
            value,
            location.getX(),
            location.getY(),
            location.getZ(),
            CraftItemStack.asNMSCopy(itemStack));
    itemEntity.setPos(location.getX(), location.getY(), location.getZ());
    itemEntity.getBukkitEntity().setGravity(false);
    itemEntity.setNeverPickUp();
    if (glowing) {
      itemEntity.getBukkitEntity().setGlowing(true);
    }

    CraftEntity craftEntity = itemEntity.getBukkitEntity();
    Location entityLocation = craftEntity.getLocation();
    Vec3 position = new Vec3(entityLocation.getX(), entityLocation.getY(), entityLocation.getZ());
    ClientboundAddEntityPacket spawnPacket =
        new ClientboundAddEntityPacket(
            itemEntity.getId(),
            itemEntity.getUUID(),
            position.x,
            position.y,
            position.z,
            0.0F,
            0.0F,
            entityType("item"),
            0,
            Vec3.ZERO,
            0.0);
    sendPacket(player, spawnPacket);
    sendPacket(player, getEntityMetaDataPacket(itemEntity.getId(), itemEntity));
    ClientboundSetEntityMotionPacket motionPacket =
        new ClientboundSetEntityMotionPacket(itemEntity.getId(), Vec3.ZERO);
    sendPacket(player, motionPacket);
    return new CMIDataWatcher(itemEntity.getId(), itemEntity, CMIHoloLineType.icon);
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
    if (player != null && !(player.getHealth() <= 0.0) && !player.isDead()) {
      player.closeInventory();
      ServerPlayer value = ((CraftPlayer) player).getHandle();

      try {
        if (player.getWorld() == location.getWorld()) {
          value.connection.teleport(
              location.getX(),
              location.getY(),
              location.getZ(),
              location.getYaw(),
              location.getPitch(),
              TeleportCause.PLUGIN);
        } else {
          ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
          value.teleportTo(
              level,
              location.getX(),
              location.getY(),
              location.getZ(),
              new HashSet(),
              location.getYaw(),
              location.getPitch(),
              true,
              TeleportCause.PLUGIN);
        }

        return true;
      } catch (Throwable throwable) {
        throwable.printStackTrace();
        return false;
      }
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
        return cmiHitBox;
      }

      ServerLevel value = ((CraftWorld) block.getWorld()).getHandle();
      BlockPos blockPos = new BlockPos(block.getX(), block.getY(), block.getZ());
      BlockState blockState = value.getBlockState(blockPos);
      VoxelShape shape = blockState.getCollisionShape(value, blockPos);
      if (!shape.isEmpty()) {
        AABB bounds = shape.bounds();
        cmiHitBox.setMin(new Vector(bounds.minX, bounds.minY, bounds.minZ));
        cmiHitBox.setMax(new Vector(bounds.maxX, bounds.maxY, bounds.maxZ));
      } else {
        cmiHitBox.setMin(new Vector(1, 1, 1));
        cmiHitBox.setMax(new Vector(0, 0, 0));
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

      net.minecraft.world.entity.Entity value = ((CraftEntity) entity).getHandle();
      AABB box = value.getBoundingBox();
      cmiHitBox.setMin(new Vector(box.minX, box.minY, box.minZ));
      cmiHitBox.setMax(new Vector(box.maxX, box.maxY, box.maxZ));
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

  @Override
  public void sendPacketToConnection(Object playerConnection, Object packet) {
    ((ServerGamePacketListenerImpl) playerConnection).send((Packet<?>) packet);
  }

  @Override
  public int getServerViewDistance() {
    return getPlayerList().getViewDistance();
  }

  @Override
  public void setServerViewDistance(int value) {
    getPlayerList().setViewDistance(value);
  }

  @Override
  public int getServerSimulationDistance() {
    return getPlayerList().getSimulationDistance();
  }

  @Override
  public void setServerSimulationDistance(int value) {
    getPlayerList().setSimulationDistance(value);
  }

  private static PlayerList getPlayerList() {
    return ((CraftServer) Bukkit.getServer()).getServer().getPlayerList();
  }

  @Override
  public Inventory openAnvilWindow(
      Player player, String rawTitle, Map<Integer, ItemStack> slotItems) {
    ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
    Component title =
        PaperAdventure.asVanilla(GsonComponentSerializer.gson().deserialize(rawTitle));
    OptionalInt containerId =
        serverPlayer.openMenu(
            new SimpleMenuProvider(
                (id, inventory, menuPlayer) -> {
                  AnvilMenu menu = new AnvilMenu(id, inventory);
                  if (slotItems != null) {
                    for (Map.Entry<Integer, ItemStack> entry : slotItems.entrySet()) {
                      if (entry.getKey() != null && entry.getValue() != null) {
                        menu.getSlot(entry.getKey())
                            .set(CraftItemStack.asNMSCopy(entry.getValue()));
                      }
                    }
                  }

                  return menu;
                },
                title));
    if (containerId.isEmpty()) {
      return null;
    }

    return serverPlayer.containerMenu.getBukkitView().getTopInventory();
  }

  @Override
  public Channel getPlayerChannel(Player player) {
    if (player == null) {
      return null;
    }

    ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
    return serverPlayer.connection == null || serverPlayer.connection.connection == null
        ? null
        : serverPlayer.connection.connection.channel;
  }

  @Override
  public Class<?> getNmsType(NmsType type) {
    return switch (type) {
      case MINECRAFT_SERVER -> MinecraftServer.class;
      case PLAYER_LIST -> PlayerList.class;
      case ENTITY_HUMAN -> net.minecraft.world.entity.player.Player.class;
      case ENTITY_PLAYER -> ServerPlayer.class;
      case ENTITY -> net.minecraft.world.entity.Entity.class;
      case PACKET_ANIMATION -> ClientboundAnimatePacket.class;
      case DIMENSION_MANAGER -> DimensionType.class;
      case ENUM_GAMEMODE -> GameType.class;
      case ENUM_HAND -> InteractionHand.class;
      case ITEM_STACK -> net.minecraft.world.item.ItemStack.class;
      case NBT_COMPOUND -> CompoundTag.class;
      case NBT_LIST -> ListTag.class;
      case TILE_ENTITY_BEEHIVE -> BeehiveBlockEntity.class;
    };
  }

  @Override
  public void sendOpenSignEditorPacket(Player player, Location location) {
    BlockPos blockPos =
        new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    sendPacket(player, new ClientboundOpenSignEditorPacket(blockPos, SignTextSlot.FRONT));
  }

  @Override
  public Object createDisplayHandle(CMIDisplayType cmiDisplayType, Location location) {
    try {
      ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
      net.minecraft.world.entity.Entity entity =
          switch (cmiDisplayType) {
            case Text -> new Display.TextDisplay(entityType("text_display"), level);
            case Block -> new Display.BlockDisplay(entityType("block_display"), level);
            case Item -> new Display.ItemDisplay(entityType("item_display"), level);
            case Interaction -> new Interaction(entityType("interaction"), level);
          };
      entity.absSnapTo(
          location.getX(),
          location.getY(),
          location.getZ(),
          location.getYaw(),
          location.getPitch());
      return entity;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
  public Entity getDisplayBukkitEntity(Object displayHandle) {
    return ((net.minecraft.world.entity.Entity) displayHandle).getBukkitEntity();
  }

  @Override
  public int getDisplayEntityId(Object displayHandle) {
    return ((net.minecraft.world.entity.Entity) displayHandle).getId();
  }

  @Override
  public void sendDisplaySpawnPacket(
      Player player, Object displayHandle, int entityId, Location location) {
    net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity) displayHandle;
    sendPacket(
        player,
        new ClientboundAddEntityPacket(
            entityId,
            entity.getUUID(),
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getPitch(),
            location.getYaw(),
            entity.getType(),
            0,
            Vec3.ZERO,
            0.0));
  }

  @Override
  public void sendDisplayDataPacket(Player player, Object displayHandle, int entityId) {
    sendPacket(
        player,
        getEntityMetaDataPacket(
            entityId, (net.minecraft.world.entity.Entity) displayHandle));
  }

  @Override
  public void sendDisplayDestroyPacket(Player player, int entityId) {
    sendPacket(player, new ClientboundRemoveEntitiesPacket(new int[] {entityId}));
  }

  @Override
  public void sendDisplayPassengersPacket(Set<Player> players, Entity vehicle, int[] passengerIds) {
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    buffer.writeVarInt(vehicle.getEntityId());
    buffer.writeVarIntArray(passengerIds);
    ClientboundSetPassengersPacket packet =
        ClientboundSetPassengersPacket.STREAM_CODEC.decode(buffer);
    for (Player player : players) {
      sendPacket(player, packet);
    }
  }

  @SuppressWarnings("unchecked")
  private static EntityType entityType(String key) {
    return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.withDefaultNamespace(key));
  }
}
