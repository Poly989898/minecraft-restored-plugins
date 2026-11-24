package net.Indyuce.mmoitems.comp.mmocore;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Optional;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.load.MMOLoader;
import net.Indyuce.mmocore.api.quest.objective.Objective;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.loot.chest.condition.Condition;
import net.Indyuce.mmocore.loot.droptable.dropitem.DropItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.crafting.ConditionalDisplay;
import net.Indyuce.mmoitems.comp.mmocore.crafting.AttributeCondition;
import net.Indyuce.mmoitems.comp.mmocore.crafting.ExperienceCraftingTrigger;
import net.Indyuce.mmoitems.comp.mmocore.crafting.ProfessionCondition;
import net.Indyuce.mmoitems.comp.mmocore.crafting.StelliumCondition;
import net.Indyuce.mmoitems.comp.mmocore.load.GetMMOItemObjective;
import net.Indyuce.mmoitems.comp.mmocore.load.ItemTemplateDropItem;
import net.Indyuce.mmoitems.comp.mmocore.load.MMOItemTrigger;
import net.Indyuce.mmoitems.comp.mmocore.load.MMOItemsBlockType;
import net.Indyuce.mmoitems.comp.mmocore.load.MineMIBlockExperienceSource;
import net.Indyuce.mmoitems.comp.mmocore.load.RandomItemDropItem;
import net.Indyuce.mmoitems.comp.mmocore.load.SmeltMMOItemExperienceSource;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class MMOCoreMMOLoader extends MMOLoader {
   private static final ItemStat HEALTH_REGENERATION = new DoubleStat(
      "HEALTH_REGENERATION", Material.BREAD, "Health Regeneration", new String[]{"Amount of health pts regenerated every second."}
   );
   private static final ItemStat MAX_HEALTH_REGENERATION = new DoubleStat(
      "MAX_HEALTH_REGENERATION", Material.BREAD, "Max Health Regeneration", new String[]{"Percentage of max health regenerated every second."}
   );
   private static final ItemStat MANA_REGENERATION = new DoubleStat(
      "MANA_REGENERATION", Material.LAPIS_LAZULI, "Mana Regeneration", new String[]{"Amount of mana pts regenerated every second."}
   );
   private static final ItemStat MAX_MANA_REGENERATION = new DoubleStat(
      "MAX_MANA_REGENERATION", Material.LAPIS_LAZULI, "Max Mana Regeneration", new String[]{"Percentage of max mana regenerated every second."}
   );
   private static final ItemStat STAMINA_REGENERATION = new DoubleStat(
      "STAMINA_REGENERATION", Material.LIGHT_BLUE_DYE, "Stamina Regeneration", new String[]{"Amount of stamina pts regenerated every second."}
   );
   private static final ItemStat MAX_STAMINA_REGENERATION = new DoubleStat(
      "MAX_STAMINA_REGENERATION", Material.LIGHT_BLUE_DYE, "Max Stamina Regeneration", new String[]{"Percentage of max stamina regenerated every second."}
   );
   private static final ItemStat MAX_STAMINA = new DoubleStat(
      "MAX_STAMINA", Material.LIGHT_BLUE_DYE, "Max Stamina", new String[]{"Adds stamina to your max stamina bar."}
   );
   private static final ItemStat MAX_STELLIUM = new DoubleStat("MAX_STELLIUM", Material.ENDER_EYE, "Max Stellium", new String[]{"Additional maximum stellium."});
   private static final ItemStat ADDITIONAL_EXPERIENCE = new DoubleStat(
      "ADDITIONAL_EXPERIENCE", Material.EXPERIENCE_BOTTLE, "Additional Experience", new String[]{"Additional MMOCore main class experience in %."}
   );

   public MMOCoreMMOLoader() {
      MMOCore.plugin.loadManager.registerLoader(this);
      MMOCore.plugin.mineManager.registerBlockType(block -> {
         Optional<CustomBlock> optional = MMOItems.plugin.getCustomBlocks().getFromBlock(block.getBlockData());
         return optional.<BlockType>map(MMOItemsBlockType::new);
      });
      MMOItems.plugin.getStats().register(HEALTH_REGENERATION);
      MMOItems.plugin.getStats().register(MAX_HEALTH_REGENERATION);
      MMOItems.plugin.getStats().register(MANA_REGENERATION);
      MMOItems.plugin.getStats().register(MAX_MANA_REGENERATION);
      MMOItems.plugin.getStats().register(STAMINA_REGENERATION);
      MMOItems.plugin.getStats().register(MAX_STAMINA_REGENERATION);
      MMOItems.plugin.getStats().register(MAX_STAMINA);
      MMOItems.plugin.getStats().register(MAX_STELLIUM);
      MMOItems.plugin.getStats().register(ADDITIONAL_EXPERIENCE);
      MMOItems.plugin
         .getCrafting()
         .registerCondition(
            "profession", ProfessionCondition::new, new ConditionalDisplay("&a✔ Requires #level# in #profession#", "&c✖ Requires #level# in #profession#")
         );
      MMOItems.plugin
         .getCrafting()
         .registerCondition("stellium", StelliumCondition::new, new ConditionalDisplay("&a✔ Requires #stellium# Stellium", "&c✖ Requires #stellium# Stellium"));
      MMOItems.plugin
         .getCrafting()
         .registerCondition(
            "attribute", AttributeCondition::new, new ConditionalDisplay("&a✔ Requires #points# #attribute#", "&c✖ Requires #points# #attribute#")
         );
      MMOItems.plugin.getCrafting().registerTrigger("exp", ExperienceCraftingTrigger::new);
   }

   public Condition loadCondition(MMOLineConfig mmoLineConfig) {
      return null;
   }

   public Trigger loadTrigger(MMOLineConfig mmoLineConfig) {
      return mmoLineConfig.getKey().equals("mmoitem") ? new MMOItemTrigger(mmoLineConfig) : null;
   }

   public DropItem loadDropItem(MMOLineConfig mmoLineConfig) {
      if (mmoLineConfig.getKey().equals("mmoitem") || mmoLineConfig.getKey().equals("mmoitemtemplate")) {
         return new ItemTemplateDropItem(mmoLineConfig);
      } else {
         return mmoLineConfig.getKey().equals("miloot") ? new RandomItemDropItem(mmoLineConfig) : null;
      }
   }

   public Objective loadObjective(MMOLineConfig mmoLineConfig, ConfigurationSection section) {
      return mmoLineConfig.getKey().equals("getmmoitem") ? new GetMMOItemObjective(section, mmoLineConfig) : null;
   }

   public ExperienceSource<?> loadExperienceSource(MMOLineConfig mmoLineConfig, ExperienceDispenser experienceDispenser) {
      if (mmoLineConfig.getKey().equals("minemiblock")) {
         return new MineMIBlockExperienceSource(experienceDispenser, mmoLineConfig);
      } else {
         return mmoLineConfig.getKey().equalsIgnoreCase("smeltmmoitem") ? new SmeltMMOItemExperienceSource(experienceDispenser, mmoLineConfig) : null;
      }
   }

   public BlockType loadBlockType(MMOLineConfig mmoLineConfig) {
      return !mmoLineConfig.getKey().equalsIgnoreCase("miblock")
            && !mmoLineConfig.getKey().equals("mmoitemsblock")
            && !mmoLineConfig.getKey().equals("mmoitem")
            && !mmoLineConfig.getKey().equals("mmoitems")
         ? null
         : new MMOItemsBlockType(mmoLineConfig);
   }
}
