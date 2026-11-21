package com.ticxo.modelengine.core21.mythic.compatibility;

import com.ticxo.modelengine.api.events.ModelDismountEvent;
import com.ticxo.modelengine.api.utils.CompatibilityManager;
import com.ticxo.modelengine.core.ModelEngine;
import com.ticxo.modelengine.core21.mythic.animation.MythicScriptReader;
import com.ticxo.modelengine.core21.mythic.utils.ModelEngineComponentRegistry;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.EventExecutor;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import java.util.ArrayList;
import java.util.Optional;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MythicCompatibility implements CompatibilityManager.CompatibilityConfiguration {
   private final ModelEngine plugin;
   @Getter
   private ModelEngineSupportImpl mythicSupport;
   @Getter
   private ModelEngineComponentRegistry customComponentRegistry;

   public MythicCompatibility(ModelEngine plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean tryApply(Plugin plugin) {
      this.customComponentRegistry = new ModelEngineComponentRegistry(this.plugin, new ArrayList<String>() {
         {
            this.add("com.ticxo.modelengine.core21.mythic.conditions");
            this.add("com.ticxo.modelengine.core21.mythic.mechanics");
            this.add("com.ticxo.modelengine.core21.mythic.targeters");
            this.add("com.ticxo.modelengine.core21.mythic.placeholders");
         }
      });
      this.plugin.getScriptReaderRegistry().register("mm", new MythicScriptReader());
      this.mythicSupport = new ModelEngineSupportImpl();
      MythicBukkit.inst().getCompatibility().setModelEngine(Optional.of(this.mythicSupport));
      Bukkit.getPluginManager().registerEvents(this.customComponentRegistry, plugin);
      this.plugin.getModelGenerator().importModels(true);
      Events.subscribe(ModelDismountEvent.class).handler(this::onDismounted);
      return true;
   }

   void onDismounted(ModelDismountEvent event) {
      MythicBukkit.inst().getMobManager().getActiveMob(event.getVehicle().getModeledEntity().getBase().getUUID()).ifPresent(mob -> {
         EventExecutor eventBus = MythicBukkit.inst().getSkillManager().getEventBus();
         SkillMetadata data = eventBus.buildSkillMetadata(SkillTriggers.DISMOUNTED, mob, BukkitAdapter.adapt(event.getPassenger()), mob.getLocation(), false);
         TriggeredSkill ts = eventBus.processTriggerMechanics(data);
         if (ts.getCancelled()) {
            event.setCancelled(true);
         }
      });
   }
}
