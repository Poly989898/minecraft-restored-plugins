package net.Indyuce.mmocore.manager.profession;

import io.lumine.mythic.lib.util.FileUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.manager.MMOCoreManager;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ProfessionManager implements MMOCoreManager {
   private final Map<String, Profession> professions = new HashMap<>();
   private final Set<SpecificProfessionManager> professionManagers = new HashSet<>();

   public void register(Profession profession) {
      this.professions.put(profession.getId(), profession);
   }

   public void registerProfessionManager(@NotNull SpecificProfessionManager professionManager) {
      Validate.notNull(professionManager);
      this.professionManagers.add(professionManager);
   }

   public void loadProfessionConfigurations(Profession profession, ConfigurationSection config) {
      for (SpecificProfessionManager manager : this.professionManagers) {
         if (config.contains(manager.getStringKey())) {
            try {
               manager.setLinkedProfession(profession);
               manager.loadProfessionConfiguration(config.getConfigurationSection(manager.getStringKey()));
            } catch (RuntimeException exception) {
               MMOCore.plugin.getLogger().log(Level.WARNING, "Could not load profession config '" + manager.getStringKey() + "': " + exception.getMessage());
            }
         }
      }
   }

   public Profession get(String id) {
      return this.professions.get(id);
   }

   public boolean has(String id) {
      return this.professions.containsKey(id);
   }

   public Collection<Profession> getAll() {
      return this.professions.values();
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.professions.clear();
      } else {
         this.registerProfessionManager(MMOCore.plugin.alchemyManager);
         this.registerProfessionManager(MMOCore.plugin.mineManager);
         this.registerProfessionManager(MMOCore.plugin.enchantManager);
         this.registerProfessionManager(MMOCore.plugin.fishingManager);
         this.registerProfessionManager(MMOCore.plugin.smithingManager);
      }

      this.professionManagers.forEach(manager -> manager.initialize(clearBefore));
      FileUtils.loadObjectsFromFolder(
         MMOCore.plugin, "professions", true, (name, config) -> this.register(new Profession(name, config)), "Could not load profession from file '%s': %s"
      );

      for (Profession profession : this.professions.values()) {
         try {
            profession.getPostLoadAction().performAction();
         } catch (IllegalArgumentException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not postload profession " + profession.getId() + ": " + exception.getMessage());
         }
      }
   }
}
