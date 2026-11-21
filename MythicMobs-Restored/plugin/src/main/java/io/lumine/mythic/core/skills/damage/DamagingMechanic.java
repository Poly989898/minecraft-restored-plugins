package io.lumine.mythic.core.skills.damage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.SkillAdapter;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public abstract class DamagingMechanic extends SkillMechanic {
   protected boolean triggerSkills;
   protected boolean triggerSkillDamage;
   protected boolean ignoresArmor;
   protected boolean preventImmunity;
   protected boolean preventKnockback;
   protected boolean ignoresEnchantments;
   protected PlaceholderString element;
   protected DamageCause cause;
   protected boolean ignoreInvulnerability;
   protected boolean damagesHelmet;
   protected boolean noAnger;
   protected boolean ignoreEffects;
   protected boolean ignoreShield;
   protected boolean ignoreResistance;
   @MythicField(
      name = "powerAffectsDamage",
      aliases = {"pad"},
      description = "Whether the skill's power affects damage",
      defValue = "true"
   )
   protected boolean powerAffectsDamage;
   @MythicField(
      name = "onDamage",
      aliases = {"then"},
      version = "5.13",
      description = "Skill to execute on each target that was actually damaged"
   )
   protected Optional<Skill> onDamageSkill = Optional.empty();
   private PlaceholderString placeholderTags;
   private PlaceholderString placeholderRawTags;
   protected Collection<String> tags = Lists.newArrayList();
   protected Map<String, PlaceholderDouble> bonusDamage = Maps.newTreeMap();
   protected Map<String, PlaceholderDouble> bonusDamageMods = Maps.newTreeMap();
   protected Map<String, PlaceholderString> parameters = new ConcurrentHashMap<>();

   @Deprecated
   public DamagingMechanic(SkillExecutor manager, String file, MythicLineConfig mlc) {
      this(manager, new File(file), mlc.getLine(), mlc);
   }

   public DamagingMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.triggerSkills = mlc.getBoolean(new String[]{"triggerskills", "ts"}, false);
      this.triggerSkillDamage = mlc.getBoolean(new String[]{"triggerskilldamage", "tsd"}, false);
      this.ignoresArmor = mlc.getBoolean(new String[]{"ignorearmor", "ia", "i"}, false);
      this.preventImmunity = mlc.getBoolean(new String[]{"preventimmunity", "pi"}, false);
      this.preventKnockback = mlc.getBoolean(new String[]{"preventknockback", "pkb", "pk"}, false);
      this.ignoresEnchantments = mlc.getBoolean(new String[]{"ignoreenchantments", "ignoreenchants", "ie"}, false);
      this.element = mlc.getPlaceholderString(new String[]{"element", "e", "damagetype"}, null);
      this.noAnger = mlc.getBoolean(new String[]{"noanger", "na"}, false);
      this.ignoreInvulnerability = mlc.getBoolean(new String[]{"ignoreinvulnerability", "ignoreinvulnerable", "ii"}, false);
      this.ignoreShield = mlc.getBoolean(new String[]{"ignoreshield", "is"}, false);
      this.damagesHelmet = mlc.getBoolean(new String[]{"damageshelmet", "dh"}, false);
      this.ignoreEffects = mlc.getBoolean(new String[]{"ignoreeffects", "ieff"}, false);
      this.ignoreResistance = mlc.getBoolean(new String[]{"ignoreresistance", "ir"}, false);
      this.cause = DamageCause.valueOf(mlc.getString(new String[]{"damagecause", "dc", "cause"}, "ENTITY_ATTACK").toUpperCase());
      this.powerAffectsDamage = mlc.getBoolean(new String[]{"poweraffectsdamage", "pad"}, true);
      if (this.element != null && !MythicBukkit.isVolatile()) {
         MythicLogger.errorMechanicConfig(this, mlc, "Custom damage types require MythicMobs Premium to use.");
         this.element = null;
      }

      this.placeholderTags = mlc.getPlaceholderString(new String[]{"tags", "tag"}, null);
      this.placeholderRawTags = mlc.getPlaceholderString(new String[]{"rawtags", "rtag"}, null);
      List<String> bonusDamage = mlc.getStringList(new String[]{"bonusdamage"}, null);
      if (bonusDamage != null) {
         if (!MythicBukkit.isVolatile()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Custom damage types require MythicMobs Premium to use.");
         } else {
            for (String entry : bonusDamage) {
               String[] parts = entry.split("[:\\s]+");
               if (parts.length == 2) {
                  try {
                     String type = parts[0].toUpperCase(Locale.ROOT);
                     PlaceholderDouble value = PlaceholderDouble.of(parts[1]);
                     this.bonusDamage.put(type, value);
                  } catch (NumberFormatException e) {
                     MythicLogger.errorMechanicConfig(this, mlc, "Invalid bonus damage value in: " + entry);
                  }
               } else {
                  MythicLogger.errorMechanicConfig(this, mlc, "Invalid bonus damage format: " + entry);
               }
            }
         }
      }

      List<String> bonusDamageModifiers = mlc.getStringList(new String[]{"bonusdamagemodifiers", "bonusdamagemods"}, null);
      if (bonusDamageModifiers != null) {
         if (!MythicBukkit.isVolatile()) {
            MythicLogger.errorMechanicConfig(this, mlc, "Custom damage types require MythicMobs Premium to use.");
         } else {
            for (String entryx : bonusDamageModifiers) {
               String[] parts = entryx.split("[:\\s]+");
               if (parts.length == 2) {
                  try {
                     String type = parts[0].toUpperCase(Locale.ROOT);
                     PlaceholderDouble value = PlaceholderDouble.of(parts[1]);
                     this.bonusDamageMods.put(type, value);
                  } catch (NumberFormatException ex) {
                     MythicLogger.errorMechanicConfig(this, mlc, "Invalid bonus damage modifier value in: " + entryx);
                  }
               } else {
                  MythicLogger.errorMechanicConfig(this, mlc, "Invalid bonus damage modifier format: " + entryx);
               }
            }
         }
      }

      String onDamageSkillName = mlc.getString(new String[]{"ondamageskill", "ondamage", "then"});
      this.getManager().queueSecondPass(() -> {
         if (onDamageSkillName != null) {
            this.onDamageSkill = this.getManager().getSkill(file, this, onDamageSkillName);
         }
      });
      this.buildParameters(mlc);
   }

   protected boolean doDamage(SkillMetadata data, AbstractEntity target, double amount) {
      return this.doDamage(data, target, amount, 1.0);
   }

   protected boolean doDamage(SkillMetadata data, AbstractEntity target, double amount, double multiplier) {
      SkillCaster caster = data.getCaster();
      String element = this.element == null ? null : this.element.get(caster);
      Map<String, Double> bonusDamage = new TreeMap<>();

      for (Entry<String, PlaceholderDouble> entry : this.bonusDamage.entrySet()) {
         bonusDamage.put(entry.getKey(), entry.getValue().get(data, target));
      }

      Map<String, Double> bonusDamageModifiers = new TreeMap<>();

      for (Entry<String, PlaceholderDouble> entry : this.bonusDamageMods.entrySet()) {
         bonusDamageModifiers.put(entry.getKey(), entry.getValue().get(data, target));
      }

      DamageMetadata meta = new DamageMetadata(
         caster,
         data,
         data.getCasterItem(),
         amount,
         bonusDamage,
         bonusDamageModifiers,
         element,
         multiplier,
         this.ignoresArmor,
         this.preventImmunity,
         this.preventKnockback,
         this.ignoresEnchantments,
         this.cause
      );
      meta.putBoolean("trigger_skills", this.triggerSkills);
      meta.putBoolean("trigger_skill_damage", this.triggerSkillDamage);
      meta.putBoolean("ignore_invulnerability", this.ignoreInvulnerability);
      meta.putBoolean("damages_helmet", this.damagesHelmet);
      meta.putBoolean("no_anger", this.noAnger);
      meta.putBoolean("ignore_shield", this.ignoreShield);
      meta.putBoolean("no_impact", this.preventKnockback);
      meta.putBoolean("ignore_effects", this.ignoreEffects);
      meta.putBoolean("ignore_resistance", this.ignoreResistance);
      if (element != null) {
         meta.putTag(element);
      }

      this.getTags(data, target).forEach(tag -> meta.putTag(tag));
      this.parameters.forEach((k, v) -> meta.putObject(k, v.get(data)));
      boolean damaged = SkillAdapter.get().doDamage(meta, target);
      if (damaged && this.onDamageSkill.isPresent()) {
         SkillMetadata thenData = data.deepCloneNoTargets();
         thenData.setEntityTarget(target);
         this.onDamageSkill.get().execute(thenData);
      }

      return damaged;
   }

   protected Collection<String> getTags(SkillMetadata data, AbstractEntity target) {
      Collection<String> tags = Lists.newArrayList(this.tags);
      if (this.placeholderTags != null) {
         String parsedTags = this.placeholderTags.get(data, target);
         if (parsedTags != null) {
            for (String tag : parsedTags.split(",")) {
               tags.add(tag.toUpperCase());
            }
         }
      }

      if (this.placeholderRawTags != null) {
         String parsedTags = this.placeholderRawTags.get(data, target);
         if (parsedTags != null) {
            Collections.addAll(tags, parsedTags.split(","));
         }
      }

      return tags;
   }

   protected void buildParameters(MythicLineConfig config) {
      for (Entry<String, String> entry : config.entrySet()) {
         String toLowerCase = entry.getKey().toLowerCase();
         switch (toLowerCase) {
            case "triggerskills":
            case "ts":
            case "triggerskilldamage":
            case "tsd":
            case "ignorearmor":
            case "ia":
            case "i":
            case "preventimmunity":
            case "pi":
            case "preventknockback":
            case "pkb":
            case "pk":
            case "ignoreenchantments":
            case "ignoreenchants":
            case "ie":
            case "element":
            case "e":
            case "damagetype":
            case "type":
            case "damagecause":
            case "dc":
            case "cause":
            case "cooldown":
            case "cd":
            case "delay":
            case "repeat":
            case "targetinterval":
            case "targeti":
            case "repeatinterval":
            case "repeati":
            case "power":
            case "powersplitbetweentargets":
            case "powersplit":
            case "splitpower":
            case "forcesync":
            case "sync":
            case "targetisorigin":
            case "sourceisorigin":
            case "castfromorigin":
            case "fromorigin":
            case "fo":
            case "origin":
            case "targetcreative":
            case "noanger":
            case "na":
            case "ignoreinvulnerability":
            case "ignoreinvulnerable":
            case "ii":
            case "damageshelmet":
            case "dh":
            case "ignoreeffects":
            case "ieff":
            case "ignoreresistance":
            case "ir":
            case "ondamageskill":
            case "ondamage":
            case "then":
               break;
            default:
               this.parameters.put(entry.getKey(), PlaceholderString.of(entry.getValue()));
         }
      }
   }
}
