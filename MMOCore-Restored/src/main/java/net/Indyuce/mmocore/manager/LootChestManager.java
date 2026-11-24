package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.util.FileUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.loot.chest.LootChest;
import net.Indyuce.mmocore.loot.chest.LootChestRegion;
import net.Indyuce.mmocore.util.HashableLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootChestManager implements MMOCoreManager {
   private final Map<HashableLocation, LootChest> active = new HashMap<>();
   private final Map<String, LootChestRegion> regions = new HashMap<>();

   public boolean hasRegion(String id) {
      return this.regions.containsKey(id);
   }

   @NotNull
   public LootChestRegion getRegion(String id) {
      return Objects.requireNonNull(this.regions.get(id), "Could not find region with ID '" + id + "'");
   }

   public Collection<LootChestRegion> getRegions() {
      return this.regions.values();
   }

   public Collection<LootChest> getActive() {
      return this.active.values();
   }

   public void register(LootChest chest) {
      this.active.put(chest.getBlock().getLocation(), chest);
   }

   public void unregister(LootChest chest) {
      this.active.remove(chest.getBlock().getLocation());
   }

   @Nullable
   public LootChest getChest(Location loc) {
      return this.active.get(new HashableLocation(loc));
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.regions.values().forEach(region -> region.getRunnable().cancel());
         this.regions.clear();
      }

      FileUtils.loadObjectsFromFolder(MMOCore.plugin, "loot-chests", false, (key, config) -> {
         LootChestRegion region = new LootChestRegion(config);
         this.regions.put(region.getId(), region);
      }, "Could not load loot chest region '%s' from file '%s': %s");
   }
}
