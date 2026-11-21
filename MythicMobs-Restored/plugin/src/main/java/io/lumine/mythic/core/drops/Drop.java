package io.lumine.mythic.core.drops;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.collections.AWeightedItem;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.numbers.RandomDouble;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.skills.placeholders.parsers.StaticDouble;
import io.lumine.mythic.core.utils.MathParser;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Drop extends AWeightedItem implements Cloneable, IDrop {
   private final String line;
   private final MythicLineConfig config;
   protected String dropVar = null;
   protected EquipSlot equipSlot = null;
   @Setter
   private PlaceholderDouble dropAmount = null;
   @Setter
   private PlaceholderDouble dropChance = null;
   private final List<SkillCondition> conditions = new ArrayList<>();
   private final List<SkillCondition> triggerConditions = new ArrayList<>();
   @Setter
   private double amount = 1.0;

   public Drop(String line, MythicLineConfig config) {
      this.line = line;
      this.config = config;

      try {
         String[] split = line.split(" ");
         int index = 1;
         if (split[0].contains(":")) {
            String[] bit = split[0].split(":", 2);
            this.dropVar = bit[1];
            if (EquipSlot.isEquipSlot(this.dropVar)) {
               this.equipSlot = EquipSlot.of(this.dropVar.toUpperCase());
            }
         }

         if (split.length > index && EquipSlot.isEquipSlot(split[index])) {
            this.equipSlot = EquipSlot.of(split[index].toUpperCase());
            index++;
         }

         if (split.length > index) {
            String token = split[index];
            if (!RandomDouble.matches(token) && !token.startsWith("'")) {
               this.dropVar = token;
               index++;
            } else {
               this.dropAmount = PlaceholderDouble.of(token);
               index++;
            }
         }

         if (this.dropAmount == null && split.length > index) {
            String token = split[index];
            if (RandomDouble.matches(token) || token.startsWith("'")) {
               this.dropAmount = PlaceholderDouble.of(token);
               index++;
            }
         }

         for (; split.length > index; index++) {
            String token = split[index];
            String parsedToken = unwrapTokenQuotes(token);
            if (parsedToken.startsWith("?~")) {
               this.loadInlineConditionToken(config, parsedToken.substring(2), true);
            } else if (parsedToken.startsWith("?")) {
               this.loadInlineConditionToken(config, parsedToken.substring(1), false);
            } else if (this.dropChance == null) {
               try {
                  this.weight = MathParser.evalChance(token);
               } catch (Exception e) {
                  this.dropChance = PlaceholderDouble.of(token);
               }
            }
         }
      } catch (Exception ex) {
         MythicLogger.errorDropConfig(this, config, "Invalid Syntax");
         MythicLogger.handleMinorError(ex);
      }

      this.loadInlineConditions(config);
      if (this.dropAmount == null) {
         this.dropAmount = PlaceholderDouble.of("1");
      }
   }

   public Drop(String line, MythicLineConfig config, double amount) {
      this(line, config);
      this.dropAmount = new StaticDouble(amount);
   }

   public Drop(String line, MythicLineConfig config, RandomDouble amount) {
      this(line, config);
      this.dropAmount = new io.lumine.mythic.core.skills.placeholders.parsers.RandomDouble(amount);
   }

   public boolean rollChance() {
      return this.rollChance(null);
   }

   public boolean rollChance(DropMetadata data) {
      double chance = this.dropChance != null ? this.dropChance.get(data) : this.weight;
      return chance == 1.0 || !(Numbers.randomDouble() > chance);
   }

   public boolean checkConditions(DropMetadata data) {
      if (!this.conditions.isEmpty()) {
         for (SkillCondition condition : this.conditions) {
            if (!condition.evaluateDropper(data)) {
               return false;
            }
         }
      }

      if (!this.triggerConditions.isEmpty()) {
         for (SkillCondition conditionx : this.triggerConditions) {
            if (!conditionx.evaluateDropCause(data)) {
               return false;
            }
         }
      }

      return true;
   }

   public void rollAmount(DropMetadata data) {
      this.amount = this.dropAmount.get(data);
   }

   @Override
   public int hashCode() {
      return this.getClass().getName().hashCode();
   }

   @Override
   public boolean equals(Object o) {
      return o == null ? false : o.getClass().equals(this.getClass());
   }

   public Drop addAmount(Drop other) {
      this.amount = this.amount + other.getAmount();
      return this.clone();
   }

   public Drop clone() {
      try {
         return (Drop)super.clone();
      } catch (CloneNotSupportedException e) {
         e.printStackTrace();
         return null;
      }
   }

   private void loadInlineConditions(MythicLineConfig config) {
      this.loadInlineConditions(config, false, new String[]{"condition", "conditions", "c"});
      this.loadInlineConditions(config, true, new String[]{"triggercondition", "triggerconditions", "tc"});
   }

   private void loadInlineConditions(MythicLineConfig config, boolean trigger, String[] keys) {
      List<String> lines = config.getStringList(keys, null);
      if (lines != null && !lines.isEmpty()) {
         for (String line : lines) {
            if (line != null && !line.isBlank()) {
               line = MythicLineConfigImpl.unparseBlock(line);
               SkillCondition condition = MythicBukkit.inst().getSkillManager().getCondition(line);
               if (condition instanceof InvalidCondition) {
                  MythicLogger.errorDropConfig(this, config, "Invalid " + (trigger ? "trigger " : "") + "condition");
               } else if (trigger) {
                  this.triggerConditions.add(condition);
               } else {
                  this.conditions.add(condition);
               }
            }
         }
      }
   }

   private void loadInlineConditionToken(MythicLineConfig config, String token, boolean trigger) {
      if (token != null && !token.isBlank()) {
         String search = token;
         boolean negate = false;
         if (token.startsWith("!")) {
            search = token.substring(1);
            negate = true;
         }

         String conditionLine = negate ? search + " CANCEL" : search;
         SkillCondition condition = MythicBukkit.inst().getSkillManager().getCondition(conditionLine);
         if (condition instanceof InvalidCondition) {
            MythicLogger.errorDropConfig(this, config, "Invalid inline " + (trigger ? "trigger " : "") + "condition");
         } else {
            if (trigger) {
               this.triggerConditions.add(condition);
            } else {
               this.conditions.add(condition);
            }
         }
      }
   }

   private static String unwrapTokenQuotes(String token) {
      if (token != null && token.length() >= 2) {
         char first = token.charAt(0);
         char last = token.charAt(token.length() - 1);
         return (first != '\'' || last != '\'') && (first != '"' || last != '"') ? token : token.substring(1, token.length() - 1);
      } else {
         return token;
      }
   }
}
