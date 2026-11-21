package io.lumine.mythic.bukkit.listeners.triggers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.EventExecutor;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.triggers.meta.InputEdgeState;
import io.lumine.mythic.core.skills.triggers.meta.InputState;
import io.lumine.mythic.core.skills.triggers.meta.PlayerInputMeta;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerInputTriggerListener extends PluginModule<MythicBukkit> {
   private final ConcurrentHashMap<UUID, InputState> lastStateByPlayer = new ConcurrentHashMap<>();

   public PlayerInputTriggerListener(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(PlayerInputEvent.class).handler(this::onPlayerInput).bindWith(this);
      Events.subscribe(PlayerQuitEvent.class).handler(event -> this.lastStateByPlayer.remove(event.getPlayer().getUniqueId())).bindWith(this);
   }

   @Override
   public void unload() {
      this.lastStateByPlayer.clear();
   }

   private void onPlayerInput(PlayerInputEvent event) {
      Player player = event.getPlayer();
      UUID playerId = player.getUniqueId();
      InputState currentState = InputState.from(event.getInput());
      InputState previousState = this.lastStateByPlayer.getOrDefault(playerId, InputState.EMPTY);
      this.lastStateByPlayer.put(playerId, currentState);
      InputEdgeState edgeState = InputEdgeState.from(previousState, currentState);
      if (edgeState.any()) {
         AbstractEntity triggerEntity = BukkitAdapter.adapt(player);
         PlayerInputMeta triggerMeta = new PlayerInputMeta(currentState);
         EventExecutor bus = this.getPlugin().getSkillManager().getEventBus();
         Entity vehicle = player.getVehicle();
         if (vehicle != null) {
            Optional<ActiveMob> maybeMob = this.getPlugin().getMobManager().getActiveMob(vehicle.getUniqueId());
            if (maybeMob.isPresent()) {
               this.fireTriggers(bus, edgeState, triggerMeta, maybeMob.get(), triggerEntity);
            }
         }

         SkillCaster caster = this.getPlugin().getSkillManager().getCaster(triggerEntity);
         if (caster != null) {
            this.fireTriggers(bus, edgeState, triggerMeta, caster, triggerEntity);
         }
      }
   }

   private void fireTriggers(EventExecutor bus, InputEdgeState edgeState, PlayerInputMeta meta, SkillCaster caster, AbstractEntity triggerEntity) {
      if (edgeState.forward()) {
         bus.processTrigger(SkillTriggers.INPUT_FORWARD, meta, caster, triggerEntity, false);
      }

      if (edgeState.backward()) {
         bus.processTrigger(SkillTriggers.INPUT_BACKWARD, meta, caster, triggerEntity, false);
      }

      if (edgeState.left()) {
         bus.processTrigger(SkillTriggers.INPUT_LEFT, meta, caster, triggerEntity, false);
      }

      if (edgeState.right()) {
         bus.processTrigger(SkillTriggers.INPUT_RIGHT, meta, caster, triggerEntity, false);
      }

      if (edgeState.jump()) {
         bus.processTrigger(SkillTriggers.INPUT_JUMP, meta, caster, triggerEntity, false);
      }

      if (edgeState.sneak()) {
         bus.processTrigger(SkillTriggers.INPUT_SNEAK, meta, caster, triggerEntity, false);
      }

      if (edgeState.sprint()) {
         bus.processTrigger(SkillTriggers.INPUT_SPRINT, meta, caster, triggerEntity, false);
      }
   }
}
