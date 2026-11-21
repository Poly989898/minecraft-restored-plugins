package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.auras.Aura;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class MythicAuraStartEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Aura aura;
   private final SkillCaster caster;
   private final SkillMetadata skillMetadata;
   private final String auraName;
   private final Optional<String> auraGroup;
   private final Optional<AbstractEntity> targetEntity;
   private final Optional<AbstractLocation> targetLocation;
   private final int duration;
   private final int charges;
   private final int stacks;
   private boolean cancelled = false;

   public MythicAuraStartEvent(
      @NotNull Aura aura,
      @NotNull SkillCaster caster,
      @NotNull SkillMetadata skillMetadata,
      @NotNull String auraName,
      @Nullable String auraGroup,
      @Nullable AbstractEntity targetEntity,
      @Nullable AbstractLocation targetLocation,
      int duration,
      int charges,
      int stacks
   ) {
      this.aura = aura;
      this.caster = caster;
      this.skillMetadata = skillMetadata;
      this.auraName = auraName;
      this.auraGroup = Optional.ofNullable(auraGroup);
      this.targetEntity = Optional.ofNullable(targetEntity);
      this.targetLocation = Optional.ofNullable(targetLocation);
      this.duration = duration;
      this.charges = charges;
      this.stacks = stacks;
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
