package io.lumine.mythic.bukkit.listeners;

import io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.ItemKeys;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemDropEffectsListener extends ReloadableModule<MythicBukkit> implements Listener {
   private final Map<UUID, PacketItemDisplay> itemVfxBeams = new ConcurrentHashMap<>();

   public ItemDropEffectsListener(MythicBukkit plugin) {
      super(plugin, false);
   }

   public void load(MythicBukkit plugin) {
      if (ServerVersion.isPaper()) {
         Events.subscribe(PlayerAttemptPickupItemEvent.class).handler(event -> {
            Item item = event.getItem();
            if (this.shouldRemoveFancyEffects(item)) {
               this.removeFancyEffects(item);
            }
         }).bindWith(this);
      }
   }

   @Override
   public void unload() {
      for (PacketItemDisplay display : this.itemVfxBeams.values()) {
         display.destroy();
      }

      this.itemVfxBeams.clear();
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onItemSpawn(ItemSpawnEvent event) {
      if (this.getPlugin().getConfiguration().isFancyDropsEnabledEverywhere()) {
         this.applyFancyEffects(event.getEntity());
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onItemDrop(PlayerDropItemEvent event) {
      this.applyFancyEffects(event.getItemDrop());
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onItemMerge(ItemMergeEvent event) {
      Item original = event.getEntity();
      Item target = event.getTarget();
      if (this.shouldRemoveFancyEffects(original)) {
         this.removeFancyEffects(original);
      }

      if (this.shouldRemoveFancyEffects(target)) {
         this.removeFancyEffects(target);
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onHopperPickup(InventoryPickupItemEvent event) {
      Item item = event.getItem();
      if (this.shouldRemoveFancyEffects(item)) {
         this.removeFancyEffects(item);
      }
   }

   private boolean shouldRemoveFancyEffects(Item item) {
      ItemMeta meta = item.getItemStack().getItemMeta();
      return meta != null && meta.getPersistentDataContainer().has(ItemKeys.FANCY_DROP, PersistentDataType.STRING);
   }

   private void removeFancyEffects(Item item) {
      if (this.itemVfxBeams.containsKey(item.getUniqueId())) {
         this.itemVfxBeams.get(item.getUniqueId()).destroy();
         this.itemVfxBeams.remove(item.getUniqueId());
      }

      ItemMeta meta = item.getItemStack().getItemMeta();
      meta.getPersistentDataContainer().remove(ItemKeys.FANCY_DROP);
      item.getItemStack().setItemMeta(meta);
   }

   private void applyFancyEffects(Item item) {
      String mythicType = this.getPlugin().getItemManager().getMythicTypeFromItem(item.getItemStack());
      if (mythicType != null && !mythicType.isEmpty()) {
         Optional<MythicItem> mythicItem = this.getPlugin().getItemManager().getItem(mythicType);
         if (!mythicItem.isEmpty()) {
            MythicItem mItem = mythicItem.get();
            if (mItem.isDropGlow() || mItem.isDropVFX() || mItem.isDropBeam()) {
               Location location = item.getLocation();
               ItemMeta meta = item.getItemStack().getItemMeta();
               if (!meta.getPersistentDataContainer().has(ItemKeys.FANCY_DROP)) {
                  meta.getPersistentDataContainer().set(ItemKeys.FANCY_DROP, PersistentDataType.STRING, item.getUniqueId().toString());
                  item.getItemStack().setItemMeta(meta);
                  if (mItem.isDropGlow()) {
                     try {
                        GlowColor color = mItem.getDropGlowColor() != null ? GlowColor.valueOf(mItem.getDropGlowColor()) : GlowColor.WHITE;
                        Schedulers.of(location).runLater(() -> {
                           this.getPlugin().getGlowManager().setColor(BukkitAdapter.adapt(item), color, this.getPlugin().getBootstrap().getOnlinePlayers());
                           this.getPlugin().getGlowManager().setGlowing(BukkitAdapter.adapt(item), true, this.getPlugin().getBootstrap().getOnlinePlayers());
                        }, 1L);
                     } catch (Exception e) {
                        MythicLogger.error("Invalid glow color for item " + mItem.getInternalName());
                     }
                  }

                  if (mItem.isDropVFX()) {
                     ItemFactory vfxItem;
                     if (mItem.getDropVFXMaterial() == null) {
                        vfxItem = ItemFactory.of(Material.POTION);
                     } else {
                        vfxItem = ItemFactory.of(Material.valueOf(mItem.getDropVFXMaterial())).model(mItem.getDropVFXData());
                     }

                     if (mItem.getDropVFXColor() != null) {
                        vfxItem.color(mItem.getDropVFXColor());
                     }

                     PacketItemDisplay packetItemDisplay = PacketItemDisplay.create()
                        .item(vfxItem.build())
                        .billboard("VERTICAL")
                        .viewRange(200.0F)
                        .build(BukkitAdapter.adapt(location));
                     packetItemDisplay.getRenderer().spawn(() -> this.getPlugin().getBootstrap().getOnlinePlayers());
                     packetItemDisplay.getRenderer().mountEntity(BukkitAdapter.adapt(item));
                     this.itemVfxBeams.put(item.getUniqueId(), packetItemDisplay);
                     Schedulers.async().runRepeating(task -> {
                        if (!item.isDead() && task.getTimesRan() <= 10000) {
                           packetItemDisplay.getRenderer().updateRenderedPlayers();
                           packetItemDisplay.getRenderer().mountEntity(BukkitAdapter.adapt(item));
                           packetItemDisplay.getRenderer().update();
                        } else {
                           if (this.itemVfxBeams.containsKey(item.getUniqueId())) {
                              this.itemVfxBeams.get(item.getUniqueId()).destroy();
                              this.itemVfxBeams.remove(item.getUniqueId());
                           }

                           task.terminate();
                        }
                     }, 1L, 1L);
                  }

                  if (mItem.isDropBeam()) {
                     Schedulers.async().runRepeating(task -> {
                        if (!item.isDead() && task.getTimesRan() <= 1000) {
                           Color color = mItem.getDropBeamColor() != null ? mItem.getDropBeamColor().toBukkitColor() : Color.WHITE;

                           for (Player player : Bukkit.getOnlinePlayers()) {
                              if (player.getWorld() == item.getWorld() && !(player.getLocation().distance(item.getLocation()) > 50.0)) {
                                 this.createParticleBeam(player, item.getLocation(), color);
                              }
                           }
                        } else {
                           task.terminate();
                        }
                     }, 2L, 2L);
                  }
               }
            }
         }
      }
   }

   private void createParticleBeam(Player player, Location itemLocation, Color color) {
      for (int i = 0; i < 20; i++) {
         Location particleLocation = itemLocation.clone().add(0.0, 1.0 + i * 0.1, 0.0);
         DustOptions dustOptions = new DustOptions(color, 1.0F);
         player.spawnParticle(Particle.DUST, particleLocation, 1, 0.0, 0.0, 0.0, 1.0, dustOptions);
      }
   }
}
