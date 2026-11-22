package com.Zrips.CMI.NBT;

import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Display.CMIDisplayType;
import com.Zrips.CMI.Modules.Holograms.CMIDataWatcher;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import io.netty.channel.Channel;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public interface NMS {
  void setMiscLocation(Player player, Location location);

  void setMiscLocation(File file, Location location);

  void clearPotionEffects(Player player);

  void setPotionEffects(Player player, PotionEffect potionEffect);

  void relightChunk(Chunk chunk);

  void resendChunk(Chunk chunk, Player player);

  void unloadChunk(Chunk chunk, Player player);

  Player getPlayer(CMIUser cmiUser);

  void updateCollisions(Player player, boolean collisionEnabled, boolean selfOnly);

  int getTickLived(UUID uuid);

  void showResurection(Player player);

  boolean changeGodMode(Player player, boolean godMode);

  boolean getGodMode(Player player);

  @Deprecated
  ItemStack setNBTList(ItemStack itemStack, String key, List<String> values);

  void updateExpBar(Player player);

  Command getCommand(String name);

  void removeHologram(Player player, Integer entityId);

  CMIDataWatcher spawnHologram(Player player, Location location, String text, ItemStack itemStack);

  void updateHologram(
      Player player, CMIDataWatcher cmiDataWatcher, String text, ItemStack itemStack);

  CMIDataWatcher makeFloatingIcon(Player player, Location location, ItemStack itemStack);

  CMIDataWatcher makeFloatingIcon(
      Player player, Location location, ItemStack itemStack, boolean glowing);

  boolean clearSkin(Player player);

  void updatePlayerViaPackets(Player player);

  void unloadData(Player player);

  ItemStack removeEnchantNbt(ItemStack itemStack, Enchantment enchantment);

  boolean forceTeleport(Player player, Location location);

  CMIHitBox getBlockHitBox(CMIHitBox cmiHitBox);

  Block getBlockLookingAt(Player player, int maxDistance, boolean includeFluids);

  CMIHitBox getEntityHitBox(CMIHitBox cmiHitBox);

  Block getBlockLookingAt(World world, Vector start, Vector end, boolean includeFluids);

  FakeInfo spawnClickable(Player player, Location location, Object data, boolean glowing);

  Player createFakeOperator();

  String getWorldName(File file);

  void setEntityTag(Entity entity, String tagName, Object tagValue);

  /**
   * Sends a server packet through a player connection object that was obtained from the adapter
   * (see CMIPlayerConnection).
   */
  void sendPacketToConnection(Object playerConnection, Object packet);

  /** Returns the server-wide view distance as set in the player list. */
  int getServerViewDistance();

  /** Sets the server-wide view distance in the player list. */
  void setServerViewDistance(int value);

  /** Returns the server-wide simulation distance as set in the player list. */
  int getServerSimulationDistance();

  /** Sets the server-wide simulation distance in the player list. */
  void setServerSimulationDistance(int value);

  /**
   * Creates a vanilla anvil container for the player, fills it with the given slot items (slot
   * index to item, null values allowed), opens it with the raw JSON title and returns the top
   * inventory of the opened window.
   */
  Inventory openAnvilWindow(Player player, String rawTitle, Map<Integer, ItemStack> slotItems);

  /** Returns the netty channel of the player's network connection, or null if unavailable. */
  Channel getPlayerChannel(Player player);

  /** Returns the server class that corresponds to the given type for the running version. */
  Class<?> getNmsType(NmsType type);

  /** Sends the sign editor open packet for the sign at the given block location to the player. */
  void sendOpenSignEditorPacket(Player player, Location location);

  /**
   * Creates a display entity of the given type at the location and returns its handle, or null if
   * it could not be created.
   */
  Object createDisplayHandle(CMIDisplayType cmiDisplayType, Location location);

  /** Returns the Bukkit entity that wraps the display handle. */
  Entity getDisplayBukkitEntity(Object displayHandle);

  /** Returns the entity id of the display handle. */
  int getDisplayEntityId(Object displayHandle);

  /** Sends the packet that spawns the display entity with the given id at the location. */
  void sendDisplaySpawnPacket(Player player, Object displayHandle, int entityId, Location location);

  /** Sends the packet with the current entity data (metadata) of the display entity. */
  void sendDisplayDataPacket(Player player, Object displayHandle, int entityId);

  /** Sends the packet that removes the entity with the given id from the player's client. */
  void sendDisplayDestroyPacket(Player player, int entityId);

  /** Sends the packet that sets the passengers of the vehicle to the given entity ids. */
  void sendDisplayPassengersPacket(Set<Player> players, Entity vehicle, int[] passengerIds);

  /** Server classes the core needs to reference reflectively, resolved by the adapter. */
  enum NmsType {
    MINECRAFT_SERVER,
    PLAYER_LIST,
    ENTITY_HUMAN,
    ENTITY_PLAYER,
    ENTITY,
    PACKET_ANIMATION,
    DIMENSION_MANAGER,
    ENUM_GAMEMODE,
    ENUM_HAND,
    ITEM_STACK,
    NBT_COMPOUND,
    NBT_LIST,
    TILE_ENTITY_BEEHIVE
  }
}
