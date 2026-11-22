package com.Zrips.CMI.NBT;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Holograms.CMIDataWatcher;
import com.Zrips.CMI.Modules.Holograms.CMIHoloLineType;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import com.Zrips.CMI.Modules.Packets.PacketHandler;
import com.Zrips.CMI.utils.Util;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection.EnumAxis;
import net.minecraft.core.Vector3f;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.RayTrace.BlockCollisionOption;
import net.minecraft.world.level.RayTrace.FluidCollisionOption;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftRayTraceResult;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class v1_20_R2 implements NMS {
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
      WorldServer worldServer = value.D();
      ClientInformation clientInformation =
          new ClientInformation(
              "EN", 3, EnumChatVisibility.a, true, 0, EnumMainHand.a, false, false);
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
      WorldServer worldServer = value.D();
      ClientInformation clientInformation =
          new ClientInformation("EN", 3, EnumChatVisibility.a, true, 0, EnumMainHand.a, true, true);
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

      WorldServer worldServer = value.x();
      if (worldServer == null) {
        return;
      }

      worldServer.a(value, RemovalReason.c);
      AdvancementDataPlayer advancementData = value.O();
      advancementData.a();
      DedicatedServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
      PlayerList playerList = minecraftServer.ac();
      playerList.l.remove(value);
      ((CraftServer) Bukkit.getServer())
          .getScoreboardManager()
          .removePlayer(value.getBukkitEntity());
    } catch (Throwable throwable) {
    }
  }

  public NBTTagCompound getNBTTagC(File file) {
    try {
      FileInputStream fileInputStream = new FileInputStream(file);
      NBTTagCompound value = NBTCompressedStreamTools.a(fileInputStream);
      fileInputStream.close();
      return value;
    } catch (Throwable throwable) {
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
    ((CraftPlayer) player).getHandle().c.b(value);
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
    this.showResurection(player, new ItemStack(Material.TOTEM_OF_UNDYING));
  }

  public void showResurection(Player player, ItemStack itemStack) {
    EntityPlayer value = ((CraftPlayer) player).getHandle();
    if (value instanceof EntityPlayer) {
      EntityPlayer entityPlayer = value;
      entityPlayer.b(StatisticList.c.b(Items.sw));
      CriterionTriggers.B.a(entityPlayer, CraftItemStack.asNMSCopy(itemStack));
    }

    value.c(1.0F);
    value.en();
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

    value.dL().a(value, (byte) 35);
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

  @Override
  public ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment) {
    net.minecraft.world.item.ItemStack value = CraftItemStack.asNMSCopy(itemStack);
    if (value == null) {
      return null;
    }

    try {
      String string = enchantment.getKey().getKey();
      NBTTagCompound tag = value.v();
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

        value.c(tag);
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
      NBTTagCompound tag = value.v();
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
        tag.r(string);
      } else {
        tag.a(string, tagList);
      }

      value.c(tag);
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
    value.getHandle().cz = -1;
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
    value.j(true);
    value.r(false);
    value.u(true);
    value.e(true);
    value.s(true);
    value.t(flag);
    if (flag) {
      value.d(new Vector3f(-15.0F, -45.0F, 0.0F));
    }

    if (string != null && !string.isEmpty()) {
      IChatBaseComponent customName =
          CraftChatMessage.fromString(CMIChatColor.translate(string))[0];
      value.b(customName);
      if (!CMIChatColor.stripColor(string).isEmpty() && !string.replace(" ", "").isEmpty()) {
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
      Player player, Location location, String string, ItemStack itemStack) {
    EntityArmorStand value = createStand(location, string, itemStack != null);
    CraftPlayer craftPlayer = (CraftPlayer) player;

    try {
      craftPlayer.getHandle().c.b(new PacketPlayOutSpawnEntity(value, 78));

      try {
        int entityId = value.ah();
        craftPlayer.getHandle().c.b(new PacketPlayOutEntityMetadata(entityId, value.al().c()));
        if (itemStack != null) {
          var arrayList = Lists.newArrayList();
          arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
          PacketPlayOutEntityEquipment equipmentPacket =
              new PacketPlayOutEntityEquipment(entityId, arrayList);
          craftPlayer.getHandle().c.b(equipmentPacket);
        }

        return new CMIDataWatcher(
            entityId,
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
            new EntitySlime(EntityTypes.aL, ((CraftWorld) location.getWorld()).getHandle());
        value.j(true);
        value.e(true);
        value.a(true);
        value.b(2, true);
        value.a(location.getX(), location.getY() - 0.45, location.getZ() - 0.2, 0.0F, 0.0F);
        value.n(false);
        value.d(true);
        PacketPlayOutSpawnEntity magmaCubeSpawnPacket = new PacketPlayOutSpawnEntity(value);
        ((CraftPlayer) player).getHandle().c.b(magmaCubeSpawnPacket);
        ((CraftPlayer) player)
            .getHandle()
            .c
            .b(new PacketPlayOutEntityMetadata(value.ah(), value.al().c()));
        FakeInfo fakeInfo = new FakeInfo().setObj(object).setPlayer(player).setId(value.ah());
        fakeInfo.setEntity(value);
        PacketHandler.fakeEntities.put(value.ah(), fakeInfo);
        return fakeInfo;
      }

      EntityArmorStand armorStand =
          new EntityArmorStand(
              ((CraftWorld) location.getWorld()).getHandle(),
              location.getX(),
              location.getY() - 0.35,
              location.getZ());
      armorStand.j(true);
      armorStand.r(false);
      armorStand.e(true);
      armorStand.s(false);
      if (object instanceof CMIHologram) {
        if (((CMIHologram) object).getHeight() > 1.7) {
          armorStand.a(false);
          armorStand.a(location.getX(), location.getY() - 0.75, location.getZ(), 0.0F, 0.0F);
        } else {
          armorStand.a(true);
        }
      } else {
        armorStand.a(true);
      }

      armorStand.n(false);
      PacketPlayOutSpawnEntity armorStandSpawnPacket = new PacketPlayOutSpawnEntity(armorStand);
      ((CraftPlayer) player).getHandle().c.b(armorStandSpawnPacket);
      ((CraftPlayer) player)
          .getHandle()
          .c
          .b(new PacketPlayOutEntityMetadata(armorStand.ah(), armorStand.al().c()));
      FakeInfo armorStandInfo =
          new FakeInfo().setObj(object).setPlayer(player).setId(armorStand.ah());
      armorStandInfo.setEntity(armorStand);
      PacketHandler.fakeEntities.put(armorStand.ah(), armorStandInfo);
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
        PacketPlayOutEntityDestroy destroyPacket =
            new PacketPlayOutEntityDestroy(new int[] {integer});
        value.getHandle().c.b(destroyPacket);
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
        value.n(false);
      } else if (!CMIChatColor.stripColor(string).isEmpty() && !string.replace(" ", "").isEmpty()) {
        value.n(true);
      } else {
        value.n(false);
      }

      IChatBaseComponent chatComponent = CraftChatMessage.fromString(string)[0];
      value.b(chatComponent);
      PacketPlayOutEntityMetadata metadataPacket =
          new PacketPlayOutEntityMetadata(cmiDataWatcher.getId(), value.al().c());
      CraftPlayer craftPlayer = (CraftPlayer) player;

      try {
        craftPlayer.getHandle().c.b(metadataPacket);
        var arrayList = Lists.newArrayList();
        arrayList.add(Pair.of(EnumItemSlot.a, CraftItemStack.asNMSCopy(itemStack)));
        PacketPlayOutEntityEquipment equipmentPacket =
            new PacketPlayOutEntityEquipment(cmiDataWatcher.getId(), arrayList);
        craftPlayer.getHandle().c.b(equipmentPacket);
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
    itemEntity.e(location.getX(), location.getY(), location.getZ());
    itemEntity.e(true);
    if (flag) {
      itemEntity.i(true);
    }

    PacketPlayOutEntityVelocity velocityPacket =
        new PacketPlayOutEntityVelocity(itemEntity.ah(), new Vec3D(0.0, 0.0, 0.0));
    PacketPlayOutSpawnEntity spawnPacket = new PacketPlayOutSpawnEntity(itemEntity);
    value.c.b(spawnPacket);
    PacketPlayOutEntityMetadata metadataPacket =
        new PacketPlayOutEntityMetadata(itemEntity.ah(), itemEntity.al().c());
    value.c.b(metadataPacket);
    value.c.b(velocityPacket);
    return new CMIDataWatcher(itemEntity.ah(), itemEntity, CMIHoloLineType.icon);
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
  public void updatePlayerViaPackets(Player player) {
    CraftPlayer value = (CraftPlayer) player;
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    if (cmiUser != null) {
      Bukkit.getScheduler()
          .runTask(
              CMI.getInstance(),
              () -> {
                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                  if (!otherPlayer.getUniqueId().equals(player.getUniqueId())
                      && otherPlayer.canSee(player)) {
                    otherPlayer.hidePlayer(CMI.getInstance(), player);
                    otherPlayer.showPlayer(CMI.getInstance(), player);
                  }
                }

                EntityPlayer playerHandle = value.getHandle();
                ClientboundPlayerInfoRemovePacket removePacket =
                    new ClientboundPlayerInfoRemovePacket(List.of(value.getUniqueId()));
                EntityPlayer nmsPlayer = value.getHandle();
                PlayerConnection connection = nmsPlayer.c;
                connection.b(removePacket);

                try {
                  ((CraftServer) Bukkit.getServer())
                      .getHandle()
                      .respawn(
                          nmsPlayer,
                          ((CraftWorld) player.getWorld()).getHandle(),
                          true,
                          value.getLocation(),
                          true,
                          RespawnReason.PLUGIN);
                } catch (Throwable throwable) {
                  CMIMessages.consoleMessage("&5Try updating server to fix this issue.");
                  throwable.printStackTrace();
                }

                PacketPlayOutEntityMetadata metadataPacket =
                    new PacketPlayOutEntityMetadata(player.getEntityId(), nmsPlayer.al().c());
                connection.b(metadataPacket);
                ClientboundPlayerInfoUpdatePacket updatePacket =
                    ClientboundPlayerInfoUpdatePacket.a(List.of(playerHandle));
                connection.b(updatePacket);
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
    if (value.eu() != 0.0F && (value.bs() || player.getGameMode().equals(GameMode.SPECTATOR))) {
      if (value.c == null) {
        return false;
      }

      if (value.bO()) {
        return false;
      }

      value.p();
      WorldServer targetWorld = ((CraftWorld) location.getWorld()).getHandle();
      World world = location.getWorld();
      if (world == null) {
        targetWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
      } else {
        targetWorld = ((CraftWorld) world).getHandle();
      }

      ((CraftPlayer) player).getHandle().q();
      if (((CraftWorld) player.getLocation().getWorld()).getHandle() == targetWorld) {
        value.c.teleport(location);
      } else {
        try {
          ((CraftServer) Bukkit.getServer())
              .getHandle()
              .respawn(value, targetWorld, true, location, true, RespawnReason.PLUGIN);
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
        VoxelShape voxelShape = blockData.c(worldServer, value);
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

      AxisAlignedBB value = ((CraftEntity) entity).getHandle().cG();
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
      MovingObjectPositionBlock hitPosition = value.getHandle().a(trace);
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
    WorldServer worldServer = value.D();
    GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes(string.getBytes()), string);
    ClientInformation clientInformation =
        new ClientInformation("EN", 3, EnumChatVisibility.a, true, 0, EnumMainHand.a, true, true);
    EntityPlayer entityPlayer =
        new EntityPlayer(value, worldServer, gameProfile, clientInformation);
    EntityPlayer nmsPlayer = entityPlayer.getBukkitEntity().getHandle();
    nmsPlayer.e(player.getEntityId());

    try {
      entityPlayer.listName =
          new ChatComponentText(CMIChatColor.translate(cmiUser.getDisplayName()));
    } catch (IllegalArgumentException | SecurityException value6) {
      value6.printStackTrace();
    }

    return entityPlayer;
  }

  public void sendPacketsForTablistSort(Player player, Player target, Object object) {
    PacketPlayOutPlayerInfo value =
        new PacketPlayOutPlayerInfo(
            EnumPlayerInfoAction.e, new EntityPlayer[] {(EntityPlayer) object});
    ((CraftPlayer) player).getHandle().c.b(value);
    if (player.canSee(target)) {
      value =
          new PacketPlayOutPlayerInfo(
              EnumPlayerInfoAction.a, new EntityPlayer[] {(EntityPlayer) object});
      ((CraftPlayer) player).getHandle().c.b(value);
    }
  }
}
