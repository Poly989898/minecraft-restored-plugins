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

   public MMOItemsOnUseAura(SkillExecutor skillExecutor, File file, String text, MythicLineConfig mythicLineConfig) {
      super(skillExecutor, file, text, mythicLineConfig);
      this.skillName = mythicLineConfig.getPlaceholderString(
         new String[]{"skill", "s", "ondamagedskill", "ondamaged", "od", "onhitskill", "onhit", "oh", "meta", "m", "mechanics", "$", "()"},
         "skill not found",
         new String[0]
      );
      this.weaponTypes = mythicLineConfig.getString(new String[]{"weapons", "weapon", "w"}, "MUSKET", new String[0]);
      this.metaskill = GetSkill(this.skillName.get());
      this.cancelEvent = mythicLineConfig.getBoolean(new String[]{"cancelevent", "ce"}, false);
      ArrayList<String> texts = new ArrayList();
      if (this.weaponTypes.contains(",")) {
         texts.addAll(Arrays.asList(this.weaponTypes.split(",")));
      } else {
         texts.add(this.weaponTypes);
      }

      for (String skillName : texts) {
         try {
            MMOItemsOnUseAura.UseItemTypes useItemTypes = MMOItemsOnUseAura.UseItemTypes.valueOf(skillName.toUpperCase());
            this.auraWeapons.add(useItemTypes);
         } catch (IllegalArgumentException exception) {
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

   public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
      Object obj;
      if (MythicBukkit.inst().getMobManager().isActiveMob(abstractEntity)) {
         obj = MythicBukkit.inst().getMobManager().getMythicMobInstance(abstractEntity);
      } else {
         obj = new GenericCaster(abstractEntity);
      }

      new MMOItemsOnUseAura.Tracker((SkillCaster)obj, skillMetadata, abstractEntity);
      return SkillResult.SUCCESS;
   }

   @Nullable
   public static Skill GetSkill(String text) {
      if (SkillExists(text)) {
         Optional skill = MythicBukkit.inst().getSkillManager().getSkill(text);
         if (skill == null) {
            return null;
         }

         if (skill.isPresent()) {
            return (Skill)skill.get();
         }
      }

      return null;
   }

   public static boolean SkillExists(String text) {
      if (text == null) {
         return false;
      }

      Optional skill = MythicBukkit.inst().getSkillManager().getSkill(text);
      if (skill.isPresent()) {
         try {
            Skill resolvedSkill = (Skill)skill.get();
            return true;
         } catch (Exception exception) {
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
         this.registerAuraComponent(Events.subscribe(SpecialWeaponAttackEvent.class).filter(event -> {
            if (!event.getPlayer().getUniqueId().equals(((AbstractEntity)this.entity.get()).getUniqueId())) {
               return false;
            }

            if (MMOItemsOnUseAura.this.auraWeapons.size() == 0) {
               return true;
            }

            for (MMOItemsOnUseAura.UseItemTypes auraWeapon : MMOItemsOnUseAura.this.auraWeapons) {
               if (auraWeapon.getInst().isInstance(event.getWeapon())) {
                  return true;
               }
            }

            return false;
         }).handler(event -> {
            SkillMetadata skillMetadata = this.skillMetadata.deepClone();
            if (MMOItemsOnUseAura.this.metaskill == null) {
               MMOItemsOnUseAura.this.metaskill = MMOItemsOnUseAura.GetSkill(MMOItemsOnUseAura.this.skillName.get(skillMetadata, skillMetadata.getCaster().getEntity()));
            }

            AbstractEntity abstractEntity = BukkitAdapter.adapt(event.getTarget());
            skillMetadata.setTrigger(abstractEntity);
            if (this.executeAuraSkill(Optional.ofNullable(MMOItemsOnUseAura.this.metaskill), skillMetadata)) {
               this.consumeCharge();
               if (MMOItemsOnUseAura.this.cancelEvent) {
                  event.setCancelled(true);
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
