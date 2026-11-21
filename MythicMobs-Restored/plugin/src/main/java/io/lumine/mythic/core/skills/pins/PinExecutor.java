package io.lumine.mythic.core.skills.pins;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.pins.PinManager;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.config.properties.Property;
import io.lumine.mythic.bukkit.utils.config.properties.types.ConfigurationSectionProp;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.particles.Particle;
import io.lumine.mythic.bukkit.utils.particles.ParticleBuilder;
import io.lumine.mythic.bukkit.utils.particles.Particles;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.serialize.Region;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.config.Scope;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PinExecutor extends ReloadableModule<MythicBukkit> implements PinManager {
   private static final ConfigurationSectionProp REMOVER = Property.ConfigurationSection(Scope.CONFIG, "");
   @Getter
   private final Map<String, Pin> pins = Maps.newConcurrentMap();
   @Getter
   private final Map<String, PinRegion> regions = Maps.newConcurrentMap();
   private final Map<String, List<PinRegion>> gridIndex = Maps.newConcurrentMap();
   private Task pinUpdateTask;
   private final Map<AbstractLocation, PacketTextDisplay> pinHolograms = Maps.newConcurrentMap();
   private final Collection<AbstractPlayer> playersViewingPins = Sets.newHashSet();
   final int cellSize = 128;

   public PinExecutor(MythicBukkit plugin) {
      super(plugin, false);
      this.load(plugin);
      Events.subscribe(PlayerQuitEvent.class).handler(event -> {
         AbstractPlayer player = BukkitAdapter.adapt(event.getPlayer());
         if (this.playersViewingPins.contains(player)) {
            this.toggleViewingPins(player);
         }
      }).bindWith(this);
      Events.subscribe(PlayerInteractEvent.class)
         .filter(event -> event.getHand() == EquipmentSlot.HAND)
         .filter(event -> event.getItem() != null)
         .filter(event -> event.getItem().getType() == Material.BLAZE_ROD)
         .handler(event -> {
            ItemMeta meta = event.getItem().getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            if (pdc.has(MobKeys.PIN_WAND)) {
               event.setCancelled(true);
               Player player = event.getPlayer();
               AbstractLocation location = BukkitAdapter.adapt(player.getLocation());
               String pinName = pdc.get(MobKeys.PIN_WAND, PersistentDataType.STRING);
               String pinPack = pdc.get(MobKeys.PIN_PACK, PersistentDataType.STRING);
               Optional<Pack> maybePack = this.getPlugin().getPackManager().getPack(pinPack);
               if (maybePack.isEmpty()) {
                  CommandHelper.sendError(player, "Pack not found");
               } else {
                  Pack pack = maybePack.get();
                  Optional<Pin> maybePin = this.getPin(pinName);
                  if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                     if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (!(maybePin.isPresent() && maybePin.get() instanceof MultiPin pin)) {
                           CommandHelper.sendError(player, "Multi-pin not found or not a multi-pin");
                        } else if (!pin.getLocations().isEmpty()) {
                           pin.getLocations().remove(pin.getLocations().size() - 1);
                           pin.save();
                           CommandHelper.sendSuccess(player, "Removed the most recent location from multi-pin " + pinPack + "." + pinName);
                        } else {
                           CommandHelper.sendError(player, "No locations to remove");
                        }
                     }
                  } else if (maybePin.isEmpty()) {
                     CommandHelper.sendSuccess(player, "Created new multi-pin " + pinPack + "." + pinName + " at location");
                     this.createMultiPin(pack, pinName, location);
                  } else {
                     Pin pin = maybePin.get();
                     if (!(pin instanceof MultiPin)) {
                        CommandHelper.sendError(player, "Pin is not a multi-pin");
                        return;
                     }

                     pin.getLocations().add(location);
                     pin.save();
                     CommandHelper.sendSuccess(player, "Added location to multi-pin " + pinPack + "." + pinName);
                  }
               }
            }
         })
         .bindWith(this);
   }

   public void load(MythicBukkit mythicBukkit) {
      for (Pack pack : this.getPlugin().getPackManager().getPacks()) {
         File file = pack.getPackFile("pins.yml");
         if (file.exists()) {
            MythicConfigImpl baseConfig = new MythicConfigImpl(file);

            for (String node : baseConfig.getKeys("")) {
               try {
                  if (baseConfig.isSet(node + ".Locations")) {
                     this.pins.put(node, new MultiPin(pack, file, node));
                  } else {
                     this.pins.put(node, new SinglePin(pack, file, node));
                  }
               } catch (Throwable e) {
                  MythicLogger.error("Failed to load pin {0} - world may be unloaded", node);
               }
            }
         }
      }

      for (Pack packx : this.getPlugin().getPackManager().getPacks()) {
         File file = packx.getPackFile("regions.yml");
         if (file.exists()) {
            MythicConfigImpl baseConfig = new MythicConfigImpl(file);

            for (String node : baseConfig.getKeys("")) {
               try {
                  this.regions.put(node, new PinRegion(packx, file, node));
               } catch (Throwable ex) {
                  MythicLogger.error("Failed to load PinRegion {0} - world may be unloaded", node);
               }
            }
         }
      }

      this.generateGridIndex();
   }

   @Override
   public void unload() {
      this.disablePinHolograms();
      this.pins.clear();
      this.regions.clear();
   }

   private File getPinFile(Pack pack) {
      return pack.getPackFile("pins.yml");
   }

   private File getRegionFile(Pack pack) {
      return pack.getPackFile("regions.yml");
   }

   public void createPin(Pack pack, String node, AbstractLocation location) {
      SinglePin pin = new SinglePin(pack, this.getPinFile(pack), node, location);
      pin.save();
      this.pins.put(node, pin);
      if (!this.playersViewingPins.isEmpty()) {
         this.createPinHologram(pin, location);
      }
   }

   public MultiPin createMultiPin(Pack pack, String node, AbstractLocation location) {
      MultiPin pin = new MultiPin(pack, this.getPinFile(pack), node, location);
      pin.save();
      this.pins.put(node, pin);
      if (!this.playersViewingPins.isEmpty()) {
         this.createPinHologram(pin, location);
      }

      return pin;
   }

   public PinRegion createRegion(Pack pack, String node, Region region) {
      return this.createRegion(pack, node, region, Collections.emptyList());
   }

   public PinRegion createRegion(Pack pack, String node, Region region, Collection<String> tags) {
      PinRegion pin = new PinRegion(pack, this.getRegionFile(pack), node, region, tags);
      pin.save();
      this.regions.put(node, pin);
      this.generateGridIndex();
      return pin;
   }

   @Override
   public Optional<Pin> getPin(String key) {
      return Optional.ofNullable(this.pins.get(key));
   }

   @Override
   public Optional<PinRegion> getRegion(String key) {
      return Optional.ofNullable(this.regions.get(key));
   }

   public void movePin(SinglePin singlePin, AbstractLocation location) {
      AbstractLocation oldLocation = singlePin.getLocations().get(0);
      PacketTextDisplay hologram = this.pinHolograms.remove(oldLocation);
      if (hologram != null) {
         hologram.destroy();
      }

      singlePin.getLocations().clear();
      singlePin.getLocations().add(location);
      singlePin.save();
      if (!this.playersViewingPins.isEmpty()) {
         this.createPinHologram(singlePin, location);
      }
   }

   public boolean removeMultiPinPoint(MultiPin multiPin, AbstractLocation location) {
      PacketTextDisplay hologram = this.pinHolograms.remove(location);
      if (hologram != null) {
         hologram.destroy();
      }

      multiPin.getLocations().remove(location);
      multiPin.save();
      return true;
   }

   public boolean removePin(Pack pack, String node) {
      Pin pin = this.pins.remove(node);
      if (pin == null) {
         return false;
      } else {
         File file = this.getPinFile(pack);
         if (!file.exists()) {
            return false;
         } else {
            REMOVER.fdelete(file, node);

            for (AbstractLocation loc : pin.getLocations()) {
               PacketTextDisplay hologram = this.pinHolograms.remove(loc);
               if (hologram != null) {
                  hologram.destroy();
               }
            }

            return true;
         }
      }
   }

   public boolean removeRegion(Pack pack, String node) {
      PinRegion pin = this.regions.remove(node);
      if (pin == null) {
         return false;
      } else {
         this.generateGridIndex();
         File file = this.getRegionFile(pack);
         if (!file.exists()) {
            return false;
         } else {
            REMOVER.fdelete(file, node);
            return true;
         }
      }
   }

   public ItemStack createWand(Pack pack, String nodeName) {
      return ItemFactory.of(Material.BLAZE_ROD)
         .name("<gold>Pin Wand <gray>(<aqua>" + nodeName + "<gray>)")
         .lore("<yellow>Pack<gray>: <white>" + pack.getKey())
         .breakable(false)
         .enchant(Enchantment.VANISHING_CURSE)
         .hideAttributes()
         .pdc(pdc -> {
            pdc.set(MobKeys.PIN_WAND, PersistentDataType.STRING, nodeName);
            pdc.set(MobKeys.PIN_PACK, PersistentDataType.STRING, pack.getKey());
         })
         .build();
   }

   public boolean toggleViewingPins(AbstractPlayer player) {
      if (this.playersViewingPins.contains(player)) {
         this.playersViewingPins.remove(player);
         if (this.playersViewingPins.isEmpty()) {
            this.disablePinHolograms();
         }

         return false;
      } else {
         if (this.playersViewingPins.isEmpty()) {
            this.playersViewingPins.add(player);
            this.setupPinHolograms();
         } else {
            this.playersViewingPins.add(player);
         }

         return true;
      }
   }

   public void setupPinHolograms() {
      for (Pin pin : this.pins.values()) {
         for (AbstractLocation loc : pin.getLocations()) {
            this.createPinHologram(pin, loc);
         }
      }

      this.pinUpdateTask = Schedulers.async().runRepeating(() -> {
         for (PacketTextDisplay hologram : this.pinHolograms.values()) {
            hologram.getRenderer().updateRenderedPlayers();
         }

         ParticleBuilder particle = Particle.FLAME.create();

         for (PinRegion region : this.regions.values()) {
            for (AbstractPlayer player : this.playersViewingPins) {
               double d1 = player.getBukkitEntity().getLocation().distanceSquared(region.getRegion().getMin().toLocation());
               double d2 = player.getBukkitEntity().getLocation().distanceSquared(region.getRegion().getMax().toLocation());
               if (d1 < 16384.0 || d2 < 16384.0) {
                  Particles.drawOutline3D((Player)player.getBukkitEntity(), region.getRegion(), particle);
               }
            }
         }
      }, 20L, 20L);
   }

   public void createPinHologram(Pin pin, AbstractLocation location) {
      PacketTextDisplay hologram = PacketTextDisplay.create()
         .lineWidth(200)
         .scale(new AbstractVector(0.65, 0.65, 0.65))
         .yOffset(0.0F)
         .text("<gold>Pin\n<white>" + pin.getPropertyNode())
         .cullingDistance(32)
         .build(location);
      hologram.getRenderer().spawn(() -> this.playersViewingPins);
      this.pinHolograms.put(location, hologram);
   }

   public void disablePinHolograms() {
      for (PacketTextDisplay hologram : this.pinHolograms.values()) {
         hologram.destroy();
      }

      if (this.pinUpdateTask != null) {
         this.pinUpdateTask.terminate();
      }

      this.pinHolograms.clear();
      this.playersViewingPins.clear();
   }

   public void generateGridIndex() {
      this.gridIndex.clear();

      for (PinRegion region : this.regions.values()) {
         int minCellX = (int)(region.getRegion().getMin().getBlockX() / 128.0);
         int maxCellX = (int)(region.getRegion().getMax().getBlockX() / 128.0);
         int minCellZ = (int)(region.getRegion().getMin().getBlockZ() / 128.0);
         int maxCellZ = (int)(region.getRegion().getMax().getBlockZ() / 128.0);

         for (int cx = minCellX; cx <= maxCellX; cx++) {
            for (int cz = minCellZ; cz <= maxCellZ; cz++) {
               String cellKey = cx + "," + cz;
               this.gridIndex.computeIfAbsent(cellKey, k -> new ArrayList<>()).add(region);
            }
         }
      }
   }

   public List<PinRegion> getRegions(AbstractLocation loc) {
      List<PinRegion> result = new ArrayList<>();
      int cellX = loc.getBlockX() / 128;
      int cellZ = loc.getBlockZ() / 128;
      String cellKey = cellX + "," + cellZ;
      List<PinRegion> candidates = this.gridIndex.get(cellKey);
      if (candidates != null) {
         int x = loc.getBlockX();
         int y = loc.getBlockY();
         int z = loc.getBlockZ();
         String worldName = loc.getWorld().getName();

         for (PinRegion region : candidates) {
            if (region.getRegion().getMin().getWorld().equals(worldName) && region.getRegion().inRegion(x, y, z)) {
               result.add(region);
            }
         }
      }

      return result;
   }

   public List<PinRegion> getRegions(Location loc) {
      List<PinRegion> result = new ArrayList<>();
      int cellX = loc.getBlockX() / 128;
      int cellZ = loc.getBlockZ() / 128;
      String cellKey = cellX + "," + cellZ;
      List<PinRegion> candidates = this.gridIndex.get(cellKey);
      if (candidates != null) {
         int x = loc.getBlockX();
         int y = loc.getBlockY();
         int z = loc.getBlockZ();
         String worldName = loc.getWorld().getName();

         for (PinRegion region : candidates) {
            if (region.getRegion().getMin().getWorld().equals(worldName) && region.getRegion().inRegion(x, y, z)) {
               result.add(region);
            }
         }
      }

      return result;
   }
}
