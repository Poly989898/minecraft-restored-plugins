package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.util.FileUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.profess.ClassOption;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.api.player.profess.event.EventTriggerHandler;
import net.Indyuce.mmocore.api.player.profess.event.trigger.AttackEventTrigger;
import net.Indyuce.mmocore.api.player.profess.event.trigger.BlockBrokenTrigger;
import net.Indyuce.mmocore.api.player.profess.event.trigger.BlockPlacedTrigger;
import net.Indyuce.mmocore.api.player.profess.event.trigger.ClassChosenEventTrigger;
import net.Indyuce.mmocore.api.player.profess.event.trigger.LevelUpEventTrigger;
import net.Indyuce.mmocore.api.player.profess.event.trigger.MultipleLevelUpEventTrigger;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;

public class ClassManager implements MMOCoreManager {
   private final Map<String, PlayerClass> map = new HashMap<>();
   private PlayerClass defaultClass;
   private final Set<EventTriggerHandler> triggerHandlers = new HashSet<>();

   public ClassManager() {
      this.registerEvent(new LevelUpEventTrigger());
      this.registerEvent(new AttackEventTrigger());
      this.registerEvent(new ClassChosenEventTrigger());
      this.registerEvent(new BlockBrokenTrigger());
      this.registerEvent(new BlockPlacedTrigger());
      this.registerEvent(new MultipleLevelUpEventTrigger());
   }

   @Deprecated
   public void registerEvent(EventTriggerHandler handler) {
      this.triggerHandlers.add(handler);
   }

   public void register(PlayerClass playerClass) {
      this.map.put(playerClass.getId(), playerClass);
      MMOCore.plugin.statManager.getRegistered().addAll(playerClass.getStats());
   }

   public boolean has(String id) {
      return this.map.containsKey(id);
   }

   public PlayerClass get(String id) {
      return this.map.get(id);
   }

   public PlayerClass getOrThrow(String id) {
      PlayerClass found = this.map.get(id);
      Validate.notNull(found, "Could not find class with ID '" + id + "'");
      return found;
   }

   public Collection<PlayerClass> getAll() {
      return this.map.values();
   }

   public PlayerClass getDefaultClass() {
      return this.defaultClass;
   }

   @Override
   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         this.map.clear();
         this.triggerHandlers.forEach(HandlerList::unregisterAll);
      }

      FileUtils.loadObjectsFromFolder(
         MMOCore.plugin, "classes", true, (name, config) -> this.register(new PlayerClass(name, config)), "Could not load class from file '%s': %s"
      );

      for (PlayerClass profess : this.map.values()) {
         try {
            profess.getPostLoadAction().performAction();
         } catch (IllegalArgumentException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not post-load class '" + profess.getId() + "': " + exception.getMessage());
         }
      }

      this.defaultClass = this.map
         .values()
         .stream()
         .filter(professx -> professx.hasOption(ClassOption.DEFAULT))
         .findFirst()
         .orElse(new PlayerClass("HUMAN", "Human", Material.LEATHER_BOOTS));
      this.triggerHandlers.forEach(handler -> Bukkit.getPluginManager().registerEvents(handler, MMOCore.plugin));
   }
}
