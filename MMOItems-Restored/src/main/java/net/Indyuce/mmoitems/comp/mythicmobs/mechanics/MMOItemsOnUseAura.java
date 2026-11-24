package net.Indyuce.mmoitems.comp.mythicmobs.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.skills.auras.Aura.AuraTracker;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.event.item.SpecialWeaponAttackEvent;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Lute;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.Musket;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemsOnUseAura extends Aura implements ITargetedEntitySkill {
   @NotNull
   PlaceholderString skillName;
   @NotNull
   String weaponTypes;
   @Nullable
   Skill metaskill;
   boolean cancelEvent;
   boolean forceAsPower;
   @NotNull
   final ArrayList<MMOItemsOnUseAura.UseItemTypes> auraWeapons = new ArrayList<>();

   public MMOItemsOnUseAura(SkillExecutor var1, File var2, String var3, MythicLineConfig var4) {
      super(var1, var2, var3, var4);
      this.skillName = var4.getPlaceholderString(
         new String[]{"skill", "s", "ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh", "meta", "m", "mechanics", "$", "()"},
         "skill not found",
         new String[0]
      );
      this.weaponTypes = var4.getString(new String[]{"weapons", "weapon", "w"}, "MUSKET", new String[0]);
      this.metaskill = GetSkill(this.skillName.get());
      this.cancelEvent = var4.getBoolean(new String[]{"cancelevent", "ce"}, false);
      ArrayList<String> var5 = new ArrayList();
      if (this.weaponTypes.contains(",")) {
         var5.addAll(Arrays.asList(this.weaponTypes.split(",")));
      } else {
         var5.add(this.weaponTypes);
      }

      for (String var7 : var5) {
         try {
            MMOItemsOnUseAura.UseItemTypes var8 = MMOItemsOnUseAura.UseItemTypes.valueOf(var7.toUpperCase());
            this.auraWeapons.add(var8);
         } catch (IllegalArgumentException var9) {
         }
      }

      if (this.metaskill == null) {
         (new BukkitRunnable() {
            public void run() {
               MMOItemsOnUseAura.this.metaskill = MMOItemsOnUseAura.GetSkill(MMOItemsOnUseAura.this.skillName.get());
            }
         }).runTaskLater(MMOItems.plugin, 1L);
      }
   }

   public SkillResult castAtEntity(SkillMetadata var1, AbstractEntity var2) {
      Object var3;
      if (MythicBukkit.inst().getMobManager().isActiveMob(var2)) {
         var3 = MythicBukkit.inst().getMobManager().getMythicMobInstance(var2);
      } else {
         var3 = new GenericCaster(var2);
      }

      new MMOItemsOnUseAura.Tracker((SkillCaster)var3, var1, var2);
      return SkillResult.SUCCESS;
   }

   @Nullable
   public static Skill GetSkill(String var0) {
      if (SkillExists(var0)) {
         Optional var1 = MythicBukkit.inst().getSkillManager().getSkill(var0);
         if (var1 == null) {
            return null;
         }

         if (var1.isPresent()) {
            return (Skill)var1.get();
         }
      }

      return null;
   }

   public static boolean SkillExists(String var0) {
      if (var0 == null) {
         return false;
      }

      Optional var1 = MythicBukkit.inst().getSkillManager().getSkill(var0);
      if (var1.isPresent()) {
         try {
            Skill var2 = (Skill)var1.get();
            return true;
         } catch (Exception var3) {
            return false;
         }
      } else {
         return false;
      }
   }

   private class Tracker extends AuraTracker implements IParentSkill, Runnable {
      public Tracker(SkillCaster nullx, SkillMetadata nullxx, AbstractEntity nullxxx) {
         super(nullx, nullxxx, nullxx);
         this.start();
      }

      public void auraStart() {
         this.registerAuraComponent(Events.subscribe(SpecialWeaponAttackEvent.class).filter(var1 -> {
            if (!var1.getPlayer().getUniqueId().equals(((AbstractEntity)this.entity.get()).getUniqueId())) {
               return false;
            }

            if (MMOItemsOnUseAura.this.auraWeapons.size() == 0) {
               return true;
            }

            for (MMOItemsOnUseAura.UseItemTypes var3 : MMOItemsOnUseAura.this.auraWeapons) {
               if (var3.getInst().isInstance(var1.getWeapon())) {
                  return true;
               }
            }

            return false;
         }).handler(var1 -> {
            SkillMetadata var2 = this.skillMetadata.deepClone();
            if (MMOItemsOnUseAura.this.metaskill == null) {
               MMOItemsOnUseAura.this.metaskill = MMOItemsOnUseAura.GetSkill(MMOItemsOnUseAura.this.skillName.get(var2, var2.getCaster().getEntity()));
            }

            AbstractEntity var3 = BukkitAdapter.adapt(var1.getTarget());
            var2.setTrigger(var3);
            if (this.executeAuraSkill(Optional.ofNullable(MMOItemsOnUseAura.this.metaskill), var2)) {
               this.consumeCharge();
               if (MMOItemsOnUseAura.this.cancelEvent) {
                  var1.setCancelled(true);
               }
            }
         }));
         this.executeAuraSkill(MMOItemsOnUseAura.this.onStartSkill, this.skillMetadata);
      }
   }

   enum UseItemTypes {
      LUTE(Lute.class),
      MUSKET(Musket.class);

      @NotNull
      final Class inst;

      @NotNull
      public Class getInst() {
         return this.inst;
      }

      UseItemTypes(@NotNull Class nullxx) {
         this.inst = nullxx;
      }
   }
}
