package net.Indyuce.mmoitems.comp.mythicmobs.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.lib.api.crafting.uimanager.ProvidedUIFilter;
import io.lumine.mythic.lib.api.crafting.uimanager.UIFilterManager;
import java.io.File;
import java.util.ArrayList;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.listener.ItemUse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemsArrowVolleyMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @NotNull
   PlaceholderInt amount;
   @NotNull
   PlaceholderInt spread;
   @NotNull
   PlaceholderInt fireTicks;
   @NotNull
   PlaceholderInt removeDelay;
   @NotNull
   PlaceholderFloat velocity;
   @NotNull
   PlaceholderFloat scale;
   @NotNull
   PlaceholderDouble xOffset;
   @NotNull
   PlaceholderDouble yOffset;
   @NotNull
   PlaceholderDouble zOffset;
   @NotNull
   PlaceholderDouble fOffset;
   @NotNull
   PlaceholderDouble sOffset;
   @Nullable
   ItemStack arrowItem;
   boolean fullEvent;
   boolean scalePerArrow;
   boolean fromOrigin;
   boolean allowPickup;
   static boolean syncEventBlock;

   public MMOItemsArrowVolleyMechanic(SkillExecutor skillExecutor, File file, String text, MythicLineConfig mythicLineConfig) {
      super(skillExecutor, file, text, mythicLineConfig);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mythicLineConfig.getPlaceholderInteger(new String[]{"amount", "arrows", "a"}, 20, new String[0]);
      this.spread = mythicLineConfig.getPlaceholderInteger(new String[]{"spread", "s"}, 45, new String[0]);
      this.fireTicks = mythicLineConfig.getPlaceholderInteger(new String[]{"fireticks", "ft", "f"}, 0, new String[0]);
      this.removeDelay = mythicLineConfig.getPlaceholderInteger(new String[]{"removedelay", "rd", "r"}, 200, new String[0]);
      this.velocity = mythicLineConfig.getPlaceholderFloat(new String[]{"velocity", "v"}, 20.0F, new String[0]);
      this.scale = mythicLineConfig.getPlaceholderFloat(new String[]{"statsscale", "ss"}, 1.0F, new String[0]);
      this.fullEvent = mythicLineConfig.getBoolean(new String[]{"fullevent", "fe"}, false);
      this.scalePerArrow = mythicLineConfig.getBoolean(new String[]{"scaleperarrow", "spa"}, false);
      this.fromOrigin = mythicLineConfig.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.allowPickup = mythicLineConfig.getBoolean(new String[]{"allowpickup", "ap"}, false);
      String text2 = mythicLineConfig.getString(new String[]{"arrowitem", "item", "ai"}, null, new String[0]);
      if (text2 != null) {
         ProvidedUIFilter uIFilter = UIFilterManager.getUIFilter(text2.replace("<&sp>", " ").replace("\"", ""), null);
         if (uIFilter != null && uIFilter.isValid(null) && uIFilter.getParent().fullyDefinesItem()) {
            uIFilter.setAmount(1);
            this.arrowItem = uIFilter.getItemStack(null);
         }
      }

      this.xOffset = mythicLineConfig.getPlaceholderDouble(new String[]{"startxoffset", "sxo"}, 0.0, new String[0]);
      this.yOffset = mythicLineConfig.getPlaceholderDouble(new String[]{"startyoffset", "syo"}, 3.0, new String[0]);
      this.zOffset = mythicLineConfig.getPlaceholderDouble(new String[]{"startzoffset", "szo"}, 0.0, new String[0]);
      this.fOffset = mythicLineConfig.getPlaceholderDouble(new String[]{"startfoffset", "sfo"}, 0.0, new String[0]);
      this.sOffset = mythicLineConfig.getPlaceholderDouble(new String[]{"startsoffset", "sso"}, 0.0, new String[0]);
   }

   public SkillResult castAtLocation(SkillMetadata skillMetadata, AbstractLocation abstractLocation) {
      if (skillMetadata.getCaster().getEntity().getBukkitEntity() instanceof Player) {
         this.executeMIVolley(
            skillMetadata.getCaster(),
            skillMetadata,
            abstractLocation,
            this.amount.get(skillMetadata),
            this.velocity.get(skillMetadata) * 0.1F,
            this.spread.get(skillMetadata),
            this.fireTicks.get(skillMetadata),
            this.removeDelay.get(skillMetadata),
            this.scale
         );
      } else {
         SkillAdapter.get()
            .executeVolley(
               skillMetadata.getCaster(),
               abstractLocation,
               this.amount.get(skillMetadata),
               this.velocity.get(skillMetadata) * 0.1F,
               this.spread.get(skillMetadata),
               this.fireTicks.get(skillMetadata),
               this.removeDelay.get(skillMetadata),
               this.allowPickup
            );
      }

      return SkillResult.SUCCESS;
   }

   public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
      if (skillMetadata.getCaster().getEntity().getBukkitEntity() instanceof Player) {
         this.executeMIVolley(
            skillMetadata.getCaster(),
            skillMetadata,
            abstractEntity.getLocation(),
            this.amount.get(skillMetadata, abstractEntity),
            this.velocity.get(skillMetadata) * 0.1F,
            this.spread.get(skillMetadata),
            this.fireTicks.get(skillMetadata),
            this.removeDelay.get(skillMetadata),
            this.scale
         );
      } else {
         SkillAdapter.get()
            .executeVolley(
               skillMetadata.getCaster(),
               abstractEntity.getLocation(),
               this.amount.get(skillMetadata, abstractEntity),
               this.velocity.get(skillMetadata) * 0.1F,
               this.spread.get(skillMetadata),
               this.fireTicks.get(skillMetadata),
               this.removeDelay.get(skillMetadata),
               this.allowPickup
            );
      }

      return SkillResult.SUCCESS;
   }

   public void executeMIVolley(
      @NotNull SkillCaster skillCaster,
      @NotNull SkillMetadata skillMetadata,
      @NotNull AbstractLocation abstractLocation,
      int count,
      float value,
      float value2,
      int count2,
      int count3,
      @NotNull PlaceholderFloat placeholderFloat
   ) {
      if (!syncEventBlock) {
         if (skillCaster.getEntity().getBukkitEntity() instanceof Player) {
            Player bukkitEntity = (Player)skillCaster.getEntity().getBukkitEntity();
            Location location = BukkitAdapter.adapt(abstractLocation);
            Location location2 = BukkitAdapter.adapt(this.fromOrigin ? skillMetadata.getOrigin() : skillCaster.getLocation()).clone();
            Vector vector = location2.getDirection().normalize();
            Vector vector2 = new Vector(-vector.getZ(), 1.0E-5, vector.getX()).normalize();
            double value3 = this.fOffset.get(skillMetadata);
            double value4 = this.sOffset.get(skillMetadata);
            location2.setX(location2.getX() + this.xOffset.get(skillMetadata) + vector.getX() * value3 + vector2.getX() * value4);
            location2.setY(location2.getY() + this.yOffset.get(skillMetadata) + vector.getY() * value3 + vector2.getY() * value4);
            location2.setZ(location2.getZ() + this.zOffset.get(skillMetadata) + vector.getZ() * value3 + vector2.getZ() * value4);
            Vector vector3 = location.toVector().subtract(location2.toVector()).normalize();
            ItemStack item = bukkitEntity.getInventory().getItemInMainHand().clone();
            ItemStack item2 = this.arrowItem != null ? this.arrowItem.clone() : new ItemStack(Material.ARROW);
            ItemUse itemUse = new ItemUse();
            float value5 = placeholderFloat.get(skillMetadata);
            ArrayList<Arrow> arrows = new ArrayList();

            for (int i = 0; i < count; i++) {
               Arrow arrow = bukkitEntity.getWorld().spawnArrow(location2, vector3, value, value2 / 10.0F);
               arrow.setVelocity(arrow.getVelocity());
               if (this.allowPickup) {
                  arrow.setPickupStatus(PickupStatus.ALLOWED);
               } else {
                  arrow.setPickupStatus(PickupStatus.DISALLOWED);
               }

               arrow.setShooter(bukkitEntity);
               syncEventBlock = true;
               EntityShootBowEvent event = new EntityShootBowEvent(bukkitEntity, item, item2, arrow, EquipmentSlot.HAND, value5, false);
               if (this.fullEvent) {
                  Bukkit.getPluginManager().callEvent(event);
               } else {
                  itemUse.handleCustomBows(event);
               }

               syncEventBlock = false;
               if (event.isCancelled()) {
                  arrow.remove();
               } else {
                  if (count2 > 0) {
                     arrow.setFireTicks(count2);
                  }

                  arrows.add(arrow);
                  if (this.scalePerArrow) {
                     value5 = placeholderFloat.get(skillMetadata);
                  }
               }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MMOItems.plugin, () -> {
               for (Arrow var2x : arrows) {
                  var2x.remove();
               }

               arrows.clear();
            }, count3);
         }
      }
   }
}
