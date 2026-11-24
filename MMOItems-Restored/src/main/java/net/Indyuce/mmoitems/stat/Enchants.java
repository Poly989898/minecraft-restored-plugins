package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackMessage;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.PlusMinusPercent;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.comp.enchants.EnchantPlugin;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.EnchantListData;
import net.Indyuce.mmoitems.stat.data.random.RandomEnchantListData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.data.type.UpgradeInfo;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import net.Indyuce.mmoitems.stat.type.Upgradable;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Enchants extends ItemStat<RandomEnchantListData, EnchantListData> implements Upgradable {
   public Enchants() {
      super("ENCHANTS", Material.ENCHANTED_BOOK, "Enchantments", new String[]{"The item enchants."}, new String[0]);
   }

   public RandomEnchantListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      return new RandomEnchantListData((ConfigurationSection)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.ENCHANTS)
            .enable("Write in the chat the enchant you want to add.", ChatColor.AQUA + "Format: {Enchant Name} {Enchant Level Numeric Formula}");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("enchants")) {
         Set<String> keys = editionInventory.getEditedSection().getConfigurationSection("enchants").getKeys(false);
         String text = Arrays.asList(keys.toArray(new String[0])).get(keys.size() - 1);
         editionInventory.getEditedSection().set("enchants." + text, null);
         if (keys.size() <= 1) {
            editionInventory.getEditedSection().set("enchants", null);
         }

         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Successfully removed "
                  + text.substring(0, 1).toUpperCase()
                  + text.substring(1).toLowerCase().replace("_", " ")
                  + "."
            );
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String[] texts = text.split(" ");
      Enchantment enchant = getEnchant(texts[0]);
      Validate.notNull(
         enchant,
         texts[0]
            + " is not a valid enchantment! All enchants can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html"
      );
      NumericStatFormula numericStatFormula = texts.length > 1 ? new NumericStatFormula(text.substring(text.indexOf(" ") + 1)) : new NumericStatFormula(1.0, 0.0, 0.0, 0.0);
      numericStatFormula.fillConfigurationSection(editionInventory.getEditedSection(), "enchants." + enchant.getKey().getKey());
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + enchant.getKey().getKey() + " " + numericStatFormula + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomEnchantListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         RandomEnchantListData randomEnchantListData = (RandomEnchantListData)optional.get();
         randomEnchantListData.getEnchants()
            .forEach(
               var2x -> texts.add(
                  ChatColor.GRAY + "* " + UtilityMethods.caseOnWords(var2x.getKey().getKey().replace("_", " ")) + " " + randomEnchantListData.getLevel(var2x).toString()
               )
            );
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add an enchant.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last enchant.");
   }

   @NotNull
   public EnchantListData getClearStatData() {
      return new EnchantListData();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      EnchantListData enchantListData = new EnchantListData();
      ItemStack item = readMMOItem.getNBT().getItem();
      if (item.hasItemMeta()) {
         ItemMeta itemMeta = item.getItemMeta();
         if (itemMeta != null) {
            for (Enchantment key : itemMeta.getEnchants().keySet()) {
               enchantListData.addEnchant(key, itemMeta.getEnchantLevel(key));
            }

            if (itemMeta instanceof EnchantmentStorageMeta) {
               for (Enchantment key : ((EnchantmentStorageMeta)itemMeta).getStoredEnchants().keySet()) {
                  enchantListData.addEnchant(key, ((EnchantmentStorageMeta)itemMeta).getStoredEnchantLevel(key));
               }
            }
         }
      }

      readMMOItem.setData(ItemStats.ENCHANTS, enchantListData);
      ItemTag tagAtPath = ItemTag.getTagAtPath("HSTRY_" + this.getId(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
      if (tagAtPath == null
         && (!readMMOItem.getNBT().getBoolean(ItemStats.DISABLE_ENCHANTING.getNBTPath()) || !readMMOItem.getNBT().getBoolean(ItemStats.DISABLE_REPAIRING.getNBTPath()))) {
         readMMOItem.computeStatHistory(ItemStats.ENCHANTS);
      }
   }

   @NotNull
   public static EnchantListData fromVanilla(@Nullable ItemStack item) {
      EnchantListData enchantListData = new EnchantListData();
      if (item == null) {
         return enchantListData;
      }

      for (Enchantment key : item.getEnchantments().keySet()) {
         int enchantmentLevel = item.getEnchantmentLevel(key);
         if (enchantmentLevel != 0) {
            enchantListData.addEnchant(key, enchantmentLevel);
         }
      }

      return enchantListData;
   }

   @Nullable
   public EnchantListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         ArrayList<String> stringListFromTag = ItemTag.getStringListFromTag(tagAtPath);
         EnchantListData enchantListData = new EnchantListData();

         for (String text : stringListFromTag) {
            String[] texts = text.split(" ");
            if (texts.length >= 2) {
               String enchantName = texts[0];
               String levelText = texts[1];
               Enchantment enchantment = null;

               try {
                  enchantment = getEnchant(enchantName);
               } catch (Exception exception) {
               }

               Integer value = SilentNumbers.IntegerParse(levelText);
               if (enchantment != null && value != null) {
                  enchantListData.addEnchant(enchantment, value);
               }
            }
         }

         return enchantListData;
      } else {
         return null;
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull EnchantListData enchantListData) {
      for (Enchantment enchant : enchantListData.getEnchants()) {
         int level = enchantListData.getLevel(enchant);
         if (itemStackBuilder.getItemStack().getType() == Material.ENCHANTED_BOOK) {
            ((EnchantmentStorageMeta)itemStackBuilder.getMeta()).addStoredEnchant(enchant, level, true);
         } else {
            itemStackBuilder.getMeta().addEnchant(enchant, level, true);
         }

         for (EnchantPlugin enchantPlugin : MMOItems.plugin.getEnchantPlugins()) {
            Object obj = enchantPlugin.transfer(enchant);
            if (obj != null) {
               enchantPlugin.handleEnchant(itemStackBuilder, obj, level);
               break;
            }
         }
      }

      itemStackBuilder.addItemTag(this.getAppliedNBT(enchantListData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull EnchantListData enchantListData) {
      ArrayList list = new ArrayList();
      ArrayList sortedEnchants = new ArrayList();

      for (Enchantment enchant : enchantListData.getEnchants()) {
         sortedEnchants.add(enchant.getKey().getKey() + " " + enchantListData.getLevel(enchant));
      }

      list.add(ItemTag.fromStringList(this.getNBTPath(), sortedEnchants));
      return list;
   }

   @NotNull
   @Override
   public UpgradeInfo loadUpgradeInfo(@Nullable Object obj) {
      return Enchants.EnchantUpgradeInfo.GetFrom(obj);
   }

   @NotNull
   @Override
   public StatData apply(@NotNull StatData statData, @NotNull UpgradeInfo upgradeInfo, int count) {
      if (statData instanceof EnchantListData && upgradeInfo instanceof Enchants.EnchantUpgradeInfo) {
         EnchantListData enchantListData = (EnchantListData)statData;
         Enchants.EnchantUpgradeInfo enchantUpgradeInfo = (Enchants.EnchantUpgradeInfo)upgradeInfo;

         for (Enchantment affectedEnchantment : enchantUpgradeInfo.getAffectedEnchantments()) {
            int levelCopy = count;
            double level = enchantListData.getLevel(affectedEnchantment);
            PlusMinusPercent pMP = enchantUpgradeInfo.getPMP(affectedEnchantment);
            if (pMP != null) {
               if (levelCopy > 0) {
                  while (levelCopy > 0) {
                     level = pMP.apply(level);
                     levelCopy--;
                  }
               } else if (levelCopy < 0) {
                  while (levelCopy < 0) {
                     level = pMP.reverse(level);
                     levelCopy++;
                  }
               }

               enchantListData.addEnchant(affectedEnchantment, SilentNumbers.floor(level));
            }
         }

         return enchantListData;
      } else {
         return statData;
      }
   }

   public static void separateEnchantments(@NotNull MMOItem mmoItem) {
      if (!mmoItem.hasData(ItemStats.DISABLE_REPAIRING) || !mmoItem.hasData(ItemStats.DISABLE_ENCHANTING)) {
         boolean flag = MMOItems.plugin.getConfig().getBoolean("stat-merging.additive-enchantments", false);
         if (mmoItem.hasData(ItemStats.ENCHANTS)) {
            EnchantListData data = (EnchantListData)mmoItem.getData(ItemStats.ENCHANTS);
            StatHistory statHistory = mmoItem.computeStatHistory(ItemStats.ENCHANTS);
            EnchantListData enchantListData = (EnchantListData)statHistory.recalculate(mmoItem.getUpgradeLevel());
            HashMap<Enchantment, Integer> map = new HashMap<>();

            for (Enchantment enchantment : Enchantment.values()) {
               int level = data.getLevel(enchantment);
               int appliedLevel = enchantListData.getLevel(enchantment);
               if (flag) {
                  int count = level - appliedLevel;
                  if (count != 0) {
                     map.put(enchantment, count);
                  }
               } else if (level > appliedLevel) {
                  map.put(enchantment, level);
               }
            }

            if (map.size() > 0) {
               EnchantListData mergedData = new EnchantListData();

               for (Enchantment key : map.keySet()) {
                  mergedData.addEnchant(key, (Integer)map.get(key));
               }

               statHistory.registerExternalData(mergedData);
            }
         }
      }
   }

   @Nullable
   public static Enchantment getEnchant(String text) {
      text = text.toLowerCase().replace("-", "_");
      Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(text));
      if (enchantment != null) {
         return enchantment;
      }

      for (EnchantPlugin enchantPlugin : MMOItems.plugin.getEnchantPlugins()) {
         enchantment = Enchantment.getByKey(enchantPlugin.getNamespacedKey(text));
         if (enchantment != null) {
            return enchantment;
         }
      }

      return Enchantment.getByName(text);
   }

   public static class EnchantUpgradeInfo implements UpgradeInfo {
      @NotNull
      HashMap<Enchantment, PlusMinusPercent> perEnchantmentOperations = new HashMap<>();

      @NotNull
      public static Enchants.EnchantUpgradeInfo GetFrom(@Nullable Object obj) {
         Validate.notNull(obj, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Upgrade operation list must not be null", new String[0]));
         if (!(obj instanceof List)) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Expected a list of strings instead of $i{0}", new String[]{obj.toString()})
            );
         }

         ArrayList<String> texts = new ArrayList();
         boolean flag = false;
         StringBuilder builder = new StringBuilder();

         for (Object element : (List)obj) {
            if (element instanceof String) {
               texts.add((String)element);
            } else {
               flag = true;
               builder.append(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), " Invalid list entry $i{0}$b;", new String[]{obj.toString()}));
            }
         }

         if (flag) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not read enchantment list:", new String[0]) + builder.toString()
            );
         }

         if (texts.isEmpty()) {
            throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Upgrade operation list is empty", new String[0]));
         }

         Enchants.EnchantUpgradeInfo enchantUpgradeInfo = new Enchants.EnchantUpgradeInfo();

         for (String text : texts) {
            String[] enchantParts = text.split(" ");
            if (enchantParts.length >= 2) {
               String enchantName = enchantParts[0];
               String levelText = enchantParts[1];
               char ch = levelText.charAt(0);
               if (ch == 's') {
                  levelText = levelText.substring(1);
               } else if (ch != '+' && ch != '-' && ch != 'n') {
                  levelText = '+' + levelText;
               }

               FriendlyFeedbackProvider friendlyFeedbackProvider = new FriendlyFeedbackProvider(FFPMMOItems.get());
               PlusMinusPercent plusMinusPercent = PlusMinusPercent.getFromString(levelText, friendlyFeedbackProvider);
               Enchantment enchantment = null;

               try {
                  enchantment = Enchants.getEnchant(enchantName);
               } catch (Exception exception) {
               }

               if (plusMinusPercent == null) {
                  builder.append(' ').append(((FriendlyFeedbackMessage)friendlyFeedbackProvider.getFeedbackOf(FriendlyFeedbackCategory.ERROR).get(0)).forConsole(friendlyFeedbackProvider.getPalette()));
                  flag = true;
               }

               if (enchantment == null) {
                  builder.append(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), " Invalid Enchantment $i{0}$b.", new String[]{enchantName}));
                  flag = true;
               }

               if (plusMinusPercent != null && enchantment != null) {
                  enchantUpgradeInfo.addEnchantmentOperation(enchantment, plusMinusPercent);
               }
            } else {
               flag = true;
               builder.append(
                  FriendlyFeedbackProvider.quickForConsole(
                     FFPMMOItems.get(), " Invalid list entry $i{0}$b. List entries are of the format 'esharpness +1$b'.", new String[]{text}
                  )
               );
            }
         }

         if (flag) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not read enchantment list:", new String[0]) + builder.toString()
            );
         } else {
            return enchantUpgradeInfo;
         }
      }

      @Nullable
      public PlusMinusPercent getPMP(@NotNull Enchantment enchantment) {
         return this.perEnchantmentOperations.get(enchantment);
      }

      public void addEnchantmentOperation(@NotNull Enchantment enchantment, @NotNull PlusMinusPercent plusMinusPercent) {
         this.perEnchantmentOperations.put(enchantment, plusMinusPercent);
      }

      @NotNull
      public Set<Enchantment> getAffectedEnchantments() {
         return this.perEnchantmentOperations.keySet();
      }
   }
}
