package io.lumine.mythic.lib.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.fake.DamageCheckEvent;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.comp.interaction.TargetRestriction;
import io.lumine.mythic.lib.comp.interaction.relation.Relationship;
import io.lumine.mythic.lib.comp.interaction.relation.RelationshipHandler;
import io.lumine.mythic.lib.entity.ProjectileMetadata;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityManager {
   private final Set<TargetRestriction> restrictions = new HashSet<>();
   private final Set<RelationshipHandler> relHandlers = new HashSet<>();

   public void registerRestriction(TargetRestriction restriction) {
      this.restrictions.add(restriction);
   }

   public void registerRelationHandler(RelationshipHandler relationHandler) {
      this.relHandlers.add(relationHandler);
   }

   public Set<RelationshipHandler> getRelationHandlers() {
      return this.relHandlers;
   }

   @Deprecated
   @Nullable
   public ProjectileMetadata getCustomProjectile(Entity entity) {
      return ProjectileMetadata.get(entity);
   }

   @Deprecated
   public void registerCustomProjectile(Entity entity, ProjectileMetadata projectileData) {
   }

   @Deprecated
   public boolean canTarget(@NotNull Player source, @NotNull Entity target, @NotNull InteractionType interactionType) {
      return this.canInteract(source, target, interactionType);
   }

   public boolean canInteract(@NotNull Player source, @NotNull Entity target, @NotNull InteractionType interactionType) {
      if (!source.equals(target) && !target.isDead() && target instanceof LivingEntity && !(target instanceof ArmorStand)) {
         LivingEntity livingTarget = (LivingEntity)target;

         for (TargetRestriction restriction : this.restrictions) {
            if (!restriction.canTarget(source, livingTarget, interactionType)) {
               return false;
            }
         }

         if (target instanceof Player) {
            boolean pvpEnabled = target.getWorld().getPVP();
            if (pvpEnabled) {
               DamageCheckEvent damageCheckEvent = new DamageCheckEvent(source, target, interactionType);
               Bukkit.getPluginManager().callEvent(damageCheckEvent);
               pvpEnabled = !damageCheckEvent.isCancelled();
            }

            if (interactionType.isOffense() && !pvpEnabled) {
               return false;
            }

            if (!this.checkPvpInteractionRules(source, (Player)target, interactionType, pvpEnabled)) {
               return false;
            }
         } else if (!interactionType.isOffense() && !MythicLib.plugin.getMMOConfig().interactionRules.supportSkillsOnMobs) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean checkPvpInteractionRules(
      @NotNull Player source, @NotNull Player target, @NotNull InteractionType interactionType, @NotNull boolean pvpEnabled
   ) {
      if (source.equals(target)) {
         return MythicLib.plugin.getMMOConfig().interactionRules.isEnabled(pvpEnabled, interactionType, Relationship.SELF);
      }

      for (RelationshipHandler relHandler : this.relHandlers) {
         if (!MythicLib.plugin.getMMOConfig().interactionRules.isEnabled(pvpEnabled, interactionType, relHandler.getRelationship(source, target))) {
            return false;
         }
      }

      return true;
   }

   @Deprecated
   public void unregisterCustomProjectile(Projectile projectile) {
      projectile.removeMetadata("MythicLibProjectileMetadata", MythicLib.plugin);
   }
}
