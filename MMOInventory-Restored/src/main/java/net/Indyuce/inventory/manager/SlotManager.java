package net.Indyuce.inventory.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.slot.SlotType;
import net.Indyuce.inventory.slot.restriction.ClassRestriction;
import net.Indyuce.inventory.slot.restriction.LevelRestriction;
import net.Indyuce.inventory.slot.restriction.LoreTagRestriction;
import net.Indyuce.inventory.slot.restriction.PermissionRestriction;
import net.Indyuce.inventory.slot.restriction.SlotRestriction;
import net.Indyuce.inventory.util.ConfigFile;
import net.Indyuce.inventory.util.LineConfig;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotManager {
   private final Map<Integer, CustomSlot> slots = new LinkedHashMap<>();
   private final Map<String, Function<LineConfig, SlotRestriction>> restrictionLoader = new HashMap<>();
   @Nullable
   private CustomSlot fill;
   private boolean registration = true;

   public SlotManager() {
      this.registerRestriction(LevelRestriction::new, "level");
      this.registerRestriction(ClassRestriction::new, "class", "classes");
      this.registerRestriction(PermissionRestriction::new, "permission", "perm", "permissionnode", "permnode");
      this.registerRestriction(LoreTagRestriction::new, "lartype", "loretag", "customtag");
   }

   public void register(CustomSlot slot) {
      Validate.isTrue(!this.slots.containsKey(slot.getIndex()), "A slot with the same index already exists");
      if (slot.getType() == SlotType.FILL) {
         this.fill = slot;
      } else {
         this.slots.put(slot.getIndex(), slot);
      }
   }

   public CustomSlot get(int index) {
      return this.slots.getOrDefault(index, null);
   }

   public Collection<CustomSlot> getLoaded() {
      return this.slots.values();
   }

   public SlotRestriction readRestriction(LineConfig config) {
      String id = config.getKey().toLowerCase().replace("-", "_").replace(" ", "_");

      for (String check : this.restrictionLoader.keySet()) {
         if (check.equals(id)) {
            return this.restrictionLoader.get(check).apply(config);
         }
      }

      throw new IllegalArgumentException("Could not find restriction with ID " + config.getKey());
   }

   public void registerRestriction(Function<LineConfig, SlotRestriction> function, String... ids) {
      Validate.isTrue(this.registration, "Slot restriction registration is disabled");
      Validate.notNull(function, "Function cannot not be null");

      for (String id : ids) {
         id = id.toLowerCase().replace("-", "_").replace(" ", "_");
         Validate.isTrue(!this.restrictionLoader.containsKey(id), "Database already contains a restriction with ID " + id);
         this.restrictionLoader.put(id, function);
      }
   }

   public boolean hasFiller() {
      return this.fill != null;
   }

   @NotNull
   public CustomSlot getFiller() {
      return Objects.requireNonNull(this.fill, "Filler is null");
   }

   public void reload() {
      if (this.registration) {
         this.registration = false;
      } else {
         this.fill = null;
         this.slots.clear();
      }

      FileConfiguration config = new ConfigFile("items").getConfig();

      for (String key : config.getKeys(false)) {
         try {
            ConfigurationSection section = config.getConfigurationSection(key);
            Validate.notNull(section, "Could not read config section");
            this.register(new CustomSlot(section));
         } catch (IllegalArgumentException exception) {
            MMOInventory.plugin.getLogger().log(Level.WARNING, "Could not load slot " + key + ": " + exception.getMessage());
         }
      }

      MMOInventory.plugin.getLogger().log(Level.INFO, "Successfully registered " + this.slots.size() + " inventory slots.");
   }

   public Set<CustomSlot> getCustomSlots() {
      return this.getLoaded().stream().filter(slot -> slot.getType().isCustom()).collect(Collectors.toSet());
   }
}
