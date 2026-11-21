package io.lumine.mythic.core.drops;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.ICommandDrop;
import io.lumine.mythic.api.drops.IMultiDrop;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.collections.WeightedCollection;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.numbers.RandomDouble;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.conditions.InvalidCondition;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.Stats;
import java.util.List;
import lombok.Getter;

public class DropTable {
   @Getter
   private final MythicBukkit plugin;
   @Getter
   private final DropExecutor manager;
   @Getter
   private final String fileName;
   @Getter
   private final String internalName;
   @Getter
   private final String displayName;
   @Getter
   private final MythicConfig config;
   protected List<SkillCondition> conditions = Lists.newArrayList();
   protected List<SkillCondition> conditionsTrigger = Lists.newArrayList();
   @Getter
   private WeightedCollection<Drop> drops = new WeightedCollection<>();
   private int minItems = -1;
   private int maxItems = -1;
   private RandomDouble bonusLevelItems = new RandomDouble("0");
   private RandomDouble bonusLuckItems = new RandomDouble("0");
   private StatType biasStat = Stats.LOOT_BIAS;
   private boolean hasConditions = false;

   public DropTable(String file, String name, MythicConfig mc) {
      this(MythicBukkit.inst().getDropManager(), file, name, mc);
   }

   public DropTable(DropExecutor manager, String file, String name, MythicConfig mc) {
      this.plugin = manager.getPlugin();
      this.manager = manager;
      this.config = mc;
      this.fileName = file;
      this.internalName = name;
      this.displayName = mc.getString("Display");
      int totalItems = mc.getInteger("TotalItems", -1);
      this.maxItems = mc.getInteger("MaxItems", totalItems);
      this.minItems = mc.getInteger("MinItems", totalItems);
      this.bonusLevelItems = new RandomDouble(mc.getString("BonusLevelItems", "0"));
      this.bonusLuckItems = new RandomDouble(mc.getString("BonusLuckItems", "0"));
      String biasStat = mc.getString("BiasStat", null);
      if (biasStat != null) {
         this.getPlugin()
            .getStatManager()
            .getStat(biasStat)
            .ifPresentOrElse(
               stat -> this.biasStat = stat,
               () -> MythicLogger.error("Invalid BiasStat " + biasStat + " supplied for DropTable " + this.internalName + ". Falling back to LOOT_BIAS.")
            );
      }

      List<String> strDrops = mc.getStringList("Drops");
      manager.queueSecondPass(() -> {
         for (String sx : strDrops) {
            sx = MythicLineConfigImpl.unparseBlock(sx);
            Drop drop = manager.getDrop(file, sx);
            if (!(drop instanceof InvalidDrop)) {
               this.drops.add(drop);
            }
         }
      });

      for (String s : mc.getStringList("Conditions")) {
         s = MythicLineConfigImpl.unparseBlock(s);
         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(s);
         if (!(sc instanceof InvalidCondition)) {
            this.conditions.add(sc);
         }
      }

      for (String sx : mc.getStringList("TriggerConditions")) {
         sx = MythicLineConfigImpl.unparseBlock(sx);
         SkillCondition sc = this.getPlugin().getSkillManager().getCondition(sx);
         if (!(sc instanceof InvalidCondition)) {
            this.conditionsTrigger.add(sc);
         }
      }
   }

   public DropTable(String file, String name, List<String> drops) {
      this(MythicBukkit.inst().getDropManager(), file, name, drops, false);
   }

   public DropTable(DropExecutor manager, String file, String name, List<String> drops) {
      this(manager, file, name, drops, false);
   }

   public DropTable(String file, String name, List<String> drops, boolean delayed) {
      this(MythicBukkit.inst().getDropManager(), file, name, drops, delayed);
   }

   public DropTable(DropExecutor manager, String file, String name, List<String> drops, boolean delayed) {
      this.plugin = manager.getPlugin();
      this.manager = manager;
      this.config = null;
      this.fileName = file;
      this.internalName = name;
      this.displayName = null;
      if (delayed) {
         manager.queueSecondPass(() -> {
            for (String sx : drops) {
               sx = MythicLineConfigImpl.unparseBlock(sx);
               Drop dropx = manager.getDrop(file, sx);
               if (!(dropx instanceof InvalidDrop)) {
                  this.drops.add(dropx);
               }
            }
         });
      } else {
         for (String s : drops) {
            s = MythicLineConfigImpl.unparseBlock(s);
            Drop drop = manager.getDrop(file, s);
            if (!(drop instanceof InvalidDrop)) {
               this.drops.add(drop);
            }
         }
      }
   }

   public boolean hasDrops() {
      return this.drops.size() > 0;
   }

   public LootBag generate() {
      return this.generate(new DropMetadataImpl(null, null));
   }

   public LootBag generate(DropMetadata meta) {
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Generating LootBag");
      LootBag drops = new LootBag(meta);
      if (this.conditions.size() > 0) {
         for (SkillCondition mc : this.conditions) {
            if (!mc.evaluateDropper(meta)) {
               return drops;
            }
         }
      }

      if (this.conditionsTrigger.size() > 0) {
         for (SkillCondition mcx : this.conditionsTrigger) {
            if (!mcx.evaluateDropCause(meta)) {
               return drops;
            }
         }
      }

      int amountModifiers = this.applyBonusLuckModifiers(meta);
      double bias = this.applyBiasModifier(meta);
      if (this.minItems > 0 && this.maxItems > 0) {
         int diff = this.maxItems - this.minItems;
         int amount;
         if (diff > 0) {
            amount = Numbers.randomInt(diff) + this.minItems + amountModifiers;
         } else {
            amount = this.maxItems + amountModifiers;
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Amount (Rand {1} to {2}): {0}", amount, this.minItems, this.maxItems);

         for (Drop drop : this.drops.getBiased(amount, bias)) {
            if (drop.checkConditions(meta) && !this.handleDrop(meta, drops, drop)) {
               return drops;
            }
         }
      } else if (this.minItems > 0) {
         int diff = this.drops.size() - this.minItems;
         int amount;
         if (diff > 0) {
            amount = Numbers.randomInt(diff) + this.minItems + amountModifiers;
         } else {
            amount = this.minItems + amountModifiers;
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Amount (Min {1}): {0}", amount, this.minItems);

         for (Drop dropx : this.drops.getBiased(amount, bias)) {
            if (dropx.checkConditions(meta) && !this.handleDrop(meta, drops, dropx)) {
               return drops;
            }
         }
      } else if (this.maxItems > 0) {
         int items = 0 - amountModifiers;

         for (Drop dropxx : this.drops.getView()) {
            if (dropxx.rollChance(meta) && dropxx.checkConditions(meta)) {
               items++;
               if (!this.handleDrop(meta, drops, dropxx)) {
                  return drops;
               }
            }

            if (items >= this.maxItems) {
               break;
            }
         }

         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Amount (Max {1}): {0}", items, this.maxItems);
      } else {
         for (Drop dropxx : this.drops.getView()) {
            if (dropxx.rollChance(meta) && dropxx.checkConditions(meta) && !this.handleDrop(meta, drops, dropxx)) {
               return drops;
            }
         }
      }

      return drops;
   }

   private double applyBiasModifier(DropMetadata meta) {
      double bias = meta.getBias() / 100.0;
      if (this.biasStat == null) {
         return bias;
      } else if (meta.getDropper().isEmpty()) {
         return bias;
      } else {
         StatRegistry statRegistry = meta.getDropper().get().getStatRegistry();
         return statRegistry != null && !statRegistry.isEmpty() ? bias + statRegistry.get(this.biasStat) / 100.0 : bias;
      }
   }

   private int applyBonusLuckModifiers(DropMetadata meta) {
      int amountModifiers = 0;
      double bonusLevelMod = this.bonusLevelItems.get();
      double bonusLuckMod = this.bonusLuckItems.get();
      if (bonusLevelMod != 0.0) {
         amountModifiers += meta.getDropper().isPresent() ? (int)(meta.getDropper().get().getLevel() * bonusLevelMod) : 0;
      }

      if (bonusLuckMod != 0.0) {
         double causeLuck = meta.getCause().isPresent() ? (int)(meta.getCause().get().getLuck() * bonusLuckMod) : 0.0;
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "BonusLuckMod: {0} - Killer Luck: {1}", bonusLuckMod, causeLuck);
         amountModifiers = (int)(amountModifiers + causeLuck);
      }

      return amountModifiers;
   }

   private boolean handleDrop(DropMetadata meta, LootBag drops, Drop drop) {
      drop.rollAmount(meta);
      if (drop instanceof IMultiDrop multiDrop) {
         if (meta.tick() > 100) {
            return false;
         }

         for (int i = 0; i < drop.getAmount(); i++) {
            drops.add(meta, multiDrop.get(meta));
         }
      } else if (drop instanceof ICommandDrop) {
         drops.add(meta, drop);
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "Handling Item {0} amount {1}", drop.getLine(), drop.getAmount());
         drops.add(meta, drop);
      }

      return true;
   }

   public boolean usesWeights() {
      return this.minItems > 0 && this.maxItems > 0 ? true : this.minItems > 0;
   }
}
