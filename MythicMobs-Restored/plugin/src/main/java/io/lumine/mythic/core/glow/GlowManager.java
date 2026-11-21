package io.lumine.mythic.core.glow;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicPlayerLoadedEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlowManager extends ReloadableModule<MythicBukkit> {
   private final Map<Integer, GlowData> glowingEntities = new ConcurrentHashMap<>();

   public GlowManager(MythicBukkit plugin) {
      super(plugin, false);
      this.load(plugin);
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(MythicPlayerLoadedEvent.class)
         .handler(event -> plugin.getVolatileCodeHandler().getEntityHandler().initTeams(event.getPlayerData().getEntity()))
         .bindWith(this);
      Events.subscribe(EntityDeathEvent.class, EventPriority.MONITOR)
         .handler(event -> this.glowingEntities.remove(event.getEntity().getEntityId()))
         .bindWith(this);
   }

   @Override
   public void unload() {
   }

   public void setColor(@NotNull AbstractEntity glowingEntity, @NotNull GlowColor color, @NotNull Collection<AbstractPlayer> receivers) {
      Objects.requireNonNull(color, "Glow color cannot be null!");
      receivers.forEach(receiver -> this.setColor(glowingEntity, color, receiver));
   }

   public void setColor(@NotNull AbstractEntity glowingEntity, @NotNull GlowColor color, @NotNull AbstractPlayer receiver) {
      Objects.requireNonNull(color, "Glow color cannot be null!");
      GlowData data = this.glowingEntities.computeIfAbsent(glowingEntity.getEntityId(), id -> new GlowData(new ConcurrentHashMap<>()));
      data.set(receiver, color);
      this.getPlugin().getVolatileCodeHandler().getEntityHandler().setGlowColor(glowingEntity, color, receiver);
   }

   public void setGlowing(@NotNull AbstractEntity glowingEntity, boolean glowing, @NotNull Collection<AbstractPlayer> receivers) {
      receivers.forEach(player -> this.setGlowing(glowingEntity, glowing, player));
   }

   public void setGlowing(@NotNull AbstractEntity glowingEntity, boolean glowing, @NotNull AbstractPlayer receiver) {
      if (!glowing) {
         this.glowingEntities.computeIfPresent(glowingEntity.getEntityId(), (id, data) -> {
            data.remove(receiver);
            return (data.data().isEmpty() ? null : data);
         });
      }

      this.getPlugin().getVolatileCodeHandler().getEntityHandler().setGlowing(glowingEntity, glowing, receiver);
   }

   @Nullable
   public GlowData getGlowData(int entity) {
      return this.glowingEntities.get(entity);
   }

   @Nullable
   public GlowData getGlowData(AbstractEntity entity) {
      return this.getGlowData(entity.getEntityId());
   }
}
