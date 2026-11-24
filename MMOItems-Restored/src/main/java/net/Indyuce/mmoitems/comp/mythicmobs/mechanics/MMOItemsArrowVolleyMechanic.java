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

   public MMOItemsArrowVolleyMechanic(SkillExecutor var1, File var2, String var3, MythicLineConfig var4) {
      super(var1, var2, var3, var4);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = var4.getPlaceholderInteger(new String[]{"amount", "arrows", "a"}, 20, new String[0]);
      this.spread = var4.getPlaceholderInteger(new String[]{"spread", "s"}, 45, new String[0]);
      this.fireTicks = var4.getPlaceholderInteger(new String[]{"fireticks", "ft", "f"}, 0, new String[0]);
      this.removeDelay = var4.getPlaceholderInteger(new String[]{"removedelay", "rd", "r"}, 200, new String[0]);
      this.velocity = var4.getPlaceholderFloat(new String[]{"velocity", "v"}, 20.0F, new String[0]);
      this.scale = var4.getPlaceholderFloat(new String[]{"statsscale", "ss"}, 1.0F, new String[0]);
      this.fullEvent = var4.getBoolean(new String[]{"fullevent", "fe"}, false);
      this.scalePerArrow = var4.getBoolean(new String[]{"scaleperarrow", "spa"}, false);
      this.fromOrigin = var4.getBoolean(new String[]{"fromorigin", "fo"}, false);
      this.allowPickup = var4.getBoolean(new String[]{"allowpickup", "ap"}, false);
      String var5 = var4.getString(new String[]{"arrowitem", "item", "ai"}, null, new String[0]);
      if (var5 != null) {
         ProvidedUIFilter var6 = UIFilterManager.getUIFilter(var5.replace("<&sp>", " ").replace("\"", ""), null);
         if (var6 != null && var6.isValid(null) && var6.getParent().fullyDefinesItem()) {
            var6.setAmount(1);
            this.arrowItem = var6.getItemStack(null);
         }
      }

      this.xOffset = var4.getPlaceholderDouble(new String[]{"startxoffset", "sxo"}, 0.0, new String[0]);
      this.yOffset = var4.getPlaceholderDouble(new String[]{"startyoffset", "syo"}, 3.0, new String[0]);
      this.zOffset = var4.getPlaceholderDouble(new String[]{"startzoffset", "szo"}, 0.0, new String[0]);
      this.fOffset = var4.getPlaceholderDouble(new String[]{"startfoffset", "sfo"}, 0.0, new String[0]);
      this.sOffset = var4.getPlaceholderDouble(new String[]{"startsoffset", "sso"}, 0.0, new String[0]);
   }

   public SkillResult castAtLocation(SkillMetadata var1, AbstractLocation var2) {
      if (var1.getCaster().getEntity().getBukkitEntity() instanceof Player) {
         this.executeMIVolley(
            var1.getCaster(),
            var1,
            var2,
            this.amount.get(var1),
            this.velocity.get(var1) * 0.1F,
            this.spread.get(var1),
            this.fireTicks.get(var1),
            this.removeDelay.get(var1),
            this.scale
         );
      } else {
         SkillAdapter.get()
            .executeVolley(
               var1.getCaster(),
               var2,
               this.amount.get(var1),
               this.velocity.get(var1) * 0.1F,
               this.spread.get(var1),
               this.fireTicks.get(var1),
               this.removeDelay.get(var1),
               this.allowPickup
            );
      }

      return SkillResult.SUCCESS;
   }

   public SkillResult castAtEntity(SkillMetadata var1, AbstractEntity var2) {
      if (var1.getCaster().getEntity().getBukkitEntity() instanceof Player) {
         this.executeMIVolley(
            var1.getCaster(),
            var1,
            var2.getLocation(),
            this.amount.get(var1, var2),
            this.velocity.get(var1) * 0.1F,
            this.spread.get(var1),
            this.fireTicks.get(var1),
            this.removeDelay.get(var1),
            this.scale
         );
      } else {
         SkillAdapter.get()
            .executeVolley(
               var1.getCaster(),
               var2.getLocation(),
               this.amount.get(var1, var2),
               this.velocity.get(var1) * 0.1F,
               this.spread.get(var1),
               this.fireTicks.get(var1),
               this.removeDelay.get(var1),
               this.allowPickup
            );
      }

      return SkillResult.SUCCESS;
   }

   public void executeMIVolley(
      @NotNull SkillCaster var1,
      @NotNull SkillMetadata var2,
      @NotNull AbstractLocation var3,
      int var4,
      float var5,
      float var6,
      int var7,
      int var8,
      @NotNull PlaceholderFloat var9
   ) {
      if (!syncEventBlock) {
         if (var1.getEntity().getBukkitEntity() instanceof Player) {
            Player var10 = (Player)var1.getEntity().getBukkitEntity();
            Location var11 = BukkitAdapter.adapt(var3);
            Location var12 = BukkitAdapter.adapt(this.fromOrigin ? var2.getOrigin() : var1.getLocation()).clone();
            Vector var13 = var12.getDirection().normalize();
            Vector var14 = new Vector(-var13.getZ(), 1.0E-5, var13.getX()).normalize();
            double var15 = this.fOffset.get(var2);
            double var17 = this.sOffset.get(var2);
            var12.setX(var12.getX() + this.xOffset.get(var2) + var13.getX() * var15 + var14.getX() * var17);
            var12.setY(var12.getY() + this.yOffset.get(var2) + var13.getY() * var15 + var14.getY() * var17);
            var12.setZ(var12.getZ() + this.zOffset.get(var2) + var13.getZ() * var15 + var14.getZ() * var17);
            Vector var19 = var11.toVector().subtract(var12.toVector()).normalize();
            ItemStack var20 = var10.getInventory().getItemInMainHand().clone();
            ItemStack var21 = this.arrowItem != null ? this.arrowItem.clone() : new ItemStack(Material.ARROW);
            ItemUse var22 = new ItemUse();
            float var23 = var9.get(var2);
            ArrayList<Arrow> var24 = new ArrayList();

            for (int var25 = 0; var25 < var4; var25++) {
               Arrow var26 = var10.getWorld().spawnArrow(var12, var19, var5, var6 / 10.0F);
               var26.setVelocity(var26.getVelocity());
               if (this.allowPickup) {
                  var26.setPickupStatus(PickupStatus.ALLOWED);
               } else {
                  var26.setPickupStatus(PickupStatus.DISALLOWED);
               }

               var26.setShooter(var10);
               syncEventBlock = true;
               EntityShootBowEvent var27 = new EntityShootBowEvent(var10, var20, var21, var26, EquipmentSlot.HAND, var23, false);
               if (this.fullEvent) {
                  Bukkit.getPluginManager().callEvent(var27);
               } else {
                  var22.handleCustomBows(var27);
               }

               syncEventBlock = false;
               if (var27.isCancelled()) {
                  var26.remove();
               } else {
                  if (var7 > 0) {
                     var26.setFireTicks(var7);
                  }

                  var24.add(var26);
                  if (this.scalePerArrow) {
                     var23 = var9.get(var2);
                  }
               }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(MMOItems.plugin, () -> {
               for (Arrow var2x : var24) {
                  var2x.remove();
               }

               var24.clear();
            }, var8);
         }
      }
   }
}
