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

   public RandomEnchantListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      return new RandomEnchantListData((ConfigurationSection)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.ENCHANTS)
            .enable("Write in the chat the enchant you want to add.", ChatColor.AQUA + "Format: {Enchant Name} {Enchant Level Numeric Formula}");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("enchants")) {
         Set<String> var3 = var1.getEditedSection().getConfigurationSection("enchants").getKeys(false);
         String var4 = Arrays.asList(var3.toArray(new String[0])).get(var3.size() - 1);
         var1.getEditedSection().set("enchants." + var4, null);
         if (var3.size() <= 1) {
            var1.getEditedSection().set("enchants", null);
         }

         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Successfully removed "
                  + var4.substring(0, 1).toUpperCase()
                  + var4.substring(1).toLowerCase().replace("_", " ")
                  + "."
            );
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String[] var4 = var2.split(" ");
      Enchantment var5 = getEnchant(var4[0]);
      Validate.notNull(
         var5,
         var4[0]
            + " is not a valid enchantment! All enchants can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html"
      );
      NumericStatFormula var6 = var4.length > 1 ? new NumericStatFormula(var2.substring(var2.indexOf(" ") + 1)) : new NumericStatFormula(1.0, 0.0, 0.0, 0.0);
      var6.fillConfigurationSection(var1.getEditedSection(), "enchants." + var5.getKey().getKey());
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + var5.getKey().getKey() + " " + var6 + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<RandomEnchantListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         RandomEnchantListData var3 = (RandomEnchantListData)var2.get();
         var3.getEnchants()
            .forEach(
               var2x -> var1.add(
                  ChatColor.GRAY + "* " + UtilityMethods.caseOnWords(var2x.getKey().getKey().replace("_", " ")) + " " + var3.getLevel(var2x).toString()
               )
            );
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add an enchant.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last enchant.");
   }

   @NotNull
   public EnchantListData getClearStatData() {
      return new EnchantListData();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      EnchantListData var2 = new EnchantListData();
      ItemStack var3 = var1.getNBT().getItem();
      if (var3.hasItemMeta()) {
         ItemMeta var4 = var3.getItemMeta();
         if (var4 != null) {
            for (Enchantment var6 : var4.getEnchants().keySet()) {
               var2.addEnchant(var6, var4.getEnchantLevel(var6));
            }

            if (var4 instanceof EnchantmentStorageMeta) {
               for (Enchantment var9 : ((EnchantmentStorageMeta)var4).getStoredEnchants().keySet()) {
                  var2.addEnchant(var9, ((EnchantmentStorageMeta)var4).getStoredEnchantLevel(var9));
               }
            }
         }
      }

      var1.setData(ItemStats.ENCHANTS, var2);
      ItemTag var7 = ItemTag.getTagAtPath("HSTRY_" + this.getId(), var1.getNBT(), SupportedNBTTagValues.STRING);
      if (var7 == null
         && (!var1.getNBT().getBoolean(ItemStats.DISABLE_ENCHANTING.getNBTPath()) || !var1.getNBT().getBoolean(ItemStats.DISABLE_REPAIRING.getNBTPath()))) {
         var1.computeStatHistory(ItemStats.ENCHANTS);
      }
   }

   @NotNull
   public static EnchantListData fromVanilla(@Nullable ItemStack var0) {
      EnchantListData var1 = new EnchantListData();
      if (var0 == null) {
         return var1;
      }

      for (Enchantment var3 : var0.getEnchantments().keySet()) {
         int var4 = var0.getEnchantmentLevel(var3);
         if (var4 != 0) {
            var1.addEnchant(var3, var4);
         }
      }

      return var1;
   }

   @Nullable
   public EnchantListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         ArrayList<String> var3 = ItemTag.getStringListFromTag(var2);
         EnchantListData var4 = new EnchantListData();

         for (String var6 : var3) {
            String[] var7 = var6.split(" ");
            if (var7.length >= 2) {
               String var8 = var7[0];
               String var9 = var7[1];
               Enchantment var10 = null;

               try {
                  var10 = getEnchant(var8);
               } catch (Exception var12) {
               }

               Integer var11 = SilentNumbers.IntegerParse(var9);
               if (var10 != null && var11 != null) {
                  var4.addEnchant(var10, var11);
               }
            }
         }

         return var4;
      } else {
         return null;
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull EnchantListData var2) {
      for (Enchantment var4 : var2.getEnchants()) {
         int var5 = var2.getLevel(var4);
         if (var1.getItemStack().getType() == Material.ENCHANTED_BOOK) {
            ((EnchantmentStorageMeta)var1.getMeta()).addStoredEnchant(var4, var5, true);
         } else {
            var1.getMeta().addEnchant(var4, var5, true);
         }

         for (EnchantPlugin var7 : MMOItems.plugin.getEnchantPlugins()) {
            Object var8 = var7.transfer(var4);
            if (var8 != null) {
               var7.handleEnchant(var1, var8, var5);
               break;
            }
         }
      }

      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull EnchantListData var1) {
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();

      for (Enchantment var5 : var1.getEnchants()) {
         var3.add(var5.getKey().getKey() + " " + var1.getLevel(var5));
      }

      var2.add(ItemTag.fromStringList(this.getNBTPath(), var3));
      return var2;
   }

   @NotNull
   @Override
   public UpgradeInfo loadUpgradeInfo(@Nullable Object var1) {
      return Enchants.EnchantUpgradeInfo.GetFrom(var1);
   }

   @NotNull
   @Override
   public StatData apply(@NotNull StatData var1, @NotNull UpgradeInfo var2, int var3) {
      if (var1 instanceof EnchantListData && var2 instanceof Enchants.EnchantUpgradeInfo) {
         EnchantListData var4 = (EnchantListData)var1;
         Enchants.EnchantUpgradeInfo var5 = (Enchants.EnchantUpgradeInfo)var2;

         for (Enchantment var7 : var5.getAffectedEnchantments()) {
            int var8 = var3;
            double var9 = var4.getLevel(var7);
            PlusMinusPercent var11 = var5.getPMP(var7);
            if (var11 != null) {
               if (var8 > 0) {
                  while (var8 > 0) {
                     var9 = var11.apply(var9);
                     var8--;
                  }
               } else if (var8 < 0) {
                  while (var8 < 0) {
                     var9 = var11.reverse(var9);
                     var8++;
                  }
               }

               var4.addEnchant(var7, SilentNumbers.floor(var9));
            }
         }

         return var4;
      } else {
         return var1;
      }
   }

   public static void separateEnchantments(@NotNull MMOItem var0) {
      if (!var0.hasData(ItemStats.DISABLE_REPAIRING) || !var0.hasData(ItemStats.DISABLE_ENCHANTING)) {
         boolean var1 = MMOItems.plugin.getConfig().getBoolean("stat-merging.additive-enchantments", false);
         if (var0.hasData(ItemStats.ENCHANTS)) {
            EnchantListData var2 = (EnchantListData)var0.getData(ItemStats.ENCHANTS);
            StatHistory var3 = var0.computeStatHistory(ItemStats.ENCHANTS);
            EnchantListData var4 = (EnchantListData)var3.recalculate(var0.getUpgradeLevel());
            HashMap<Enchantment, Integer> var5 = new HashMap<>();

            for (Enchantment var9 : Enchantment.values()) {
               int var10 = var2.getLevel(var9);
               int var11 = var4.getLevel(var9);
               if (var1) {
                  int var12 = var10 - var11;
                  if (var12 != 0) {
                     var5.put(var9, var12);
                  }
               } else if (var10 > var11) {
                  var5.put(var9, var10);
               }
            }

            if (var5.size() > 0) {
               EnchantListData var13 = new EnchantListData();

               for (Enchantment var15 : var5.keySet()) {
                  var13.addEnchant(var15, (Integer)var5.get(var15));
               }

               var3.registerExternalData(var13);
            }
         }
      }
   }

   @Nullable
   public static Enchantment getEnchant(String var0) {
      var0 = var0.toLowerCase().replace("-", "_");
      Enchantment var1 = Enchantment.getByKey(NamespacedKey.minecraft(var0));
      if (var1 != null) {
         return var1;
      }

      for (EnchantPlugin var3 : MMOItems.plugin.getEnchantPlugins()) {
         var1 = Enchantment.getByKey(var3.getNamespacedKey(var0));
         if (var1 != null) {
            return var1;
         }
      }

      return Enchantment.getByName(var0);
   }

   public static class EnchantUpgradeInfo implements UpgradeInfo {
      @NotNull
      HashMap<Enchantment, PlusMinusPercent> perEnchantmentOperations = new HashMap<>();

      @NotNull
      public static Enchants.EnchantUpgradeInfo GetFrom(@Nullable Object var0) {
         Validate.notNull(var0, FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Upgrade operation list must not be null", new String[0]));
         if (!(var0 instanceof List)) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Expected a list of strings instead of $i{0}", new String[]{var0.toString()})
            );
         }

         ArrayList<String> var1 = new ArrayList();
         boolean var2 = false;
         StringBuilder var3 = new StringBuilder();

         for (Object var5 : (List)var0) {
            if (var5 instanceof String) {
               var1.add((String)var5);
            } else {
               var2 = true;
               var3.append(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), " Invalid list entry $i{0}$b;", new String[]{var0.toString()}));
            }
         }

         if (var2) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not read enchantment list:", new String[0]) + var3.toString()
            );
         }

         if (var1.isEmpty()) {
            throw new IllegalArgumentException(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Upgrade operation list is empty", new String[0]));
         }

         Enchants.EnchantUpgradeInfo var16 = new Enchants.EnchantUpgradeInfo();

         for (String var6 : var1) {
            String[] var7 = var6.split(" ");
            if (var7.length >= 2) {
               String var8 = var7[0];
               String var9 = var7[1];
               char var10 = var9.charAt(0);
               if (var10 == 's') {
                  var9 = var9.substring(1);
               } else if (var10 != '+' && var10 != '-' && var10 != 'n') {
                  var9 = '+' + var9;
               }

               FriendlyFeedbackProvider var11 = new FriendlyFeedbackProvider(FFPMMOItems.get());
               PlusMinusPercent var12 = PlusMinusPercent.getFromString(var9, var11);
               Enchantment var13 = null;

               try {
                  var13 = Enchants.getEnchant(var8);
               } catch (Exception var15) {
               }

               if (var12 == null) {
                  var3.append(' ').append(((FriendlyFeedbackMessage)var11.getFeedbackOf(FriendlyFeedbackCategory.ERROR).get(0)).forConsole(var11.getPalette()));
                  var2 = true;
               }

               if (var13 == null) {
                  var3.append(FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), " Invalid Enchantment $i{0}$b.", new String[]{var8}));
                  var2 = true;
               }

               if (var12 != null && var13 != null) {
                  var16.addEnchantmentOperation(var13, var12);
               }
            } else {
               var2 = true;
               var3.append(
                  FriendlyFeedbackProvider.quickForConsole(
                     FFPMMOItems.get(), " Invalid list entry $i{0}$b. List entries are of the format 'esharpness +1$b'.", new String[]{var6}
                  )
               );
            }
         }

         if (var2) {
            throw new IllegalArgumentException(
               FriendlyFeedbackProvider.quickForConsole(FFPMMOItems.get(), "Could not read enchantment list:", new String[0]) + var3.toString()
            );
         } else {
            return var16;
         }
      }

      @Nullable
      public PlusMinusPercent getPMP(@NotNull Enchantment var1) {
         return this.perEnchantmentOperations.get(var1);
      }

      public void addEnchantmentOperation(@NotNull Enchantment var1, @NotNull PlusMinusPercent var2) {
         this.perEnchantmentOperations.put(var1, var2);
      }

      @NotNull
      public Set<Enchantment> getAffectedEnchantments() {
         return this.perEnchantmentOperations.keySet();
      }
   }
}
