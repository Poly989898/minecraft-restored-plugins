package net.Indyuce.mmoitems.gui.edition;

import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.item.template.ModifierNode;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.PluginInventory;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EditionInventory extends PluginInventory {
   @Nullable
   protected Inventory inventory;
   protected MMOItemTemplate template;
   private final ConfigFile configFile;
   private final boolean displaysBack;
   @Deprecated
   private ModifierNode editedModifier = null;
   private ItemStack cachedItem;
   private int previousPage;
   @NotNull
   final FriendlyFeedbackProvider ffp;

   public EditionInventory(@NotNull Player var1, @NotNull MMOItemTemplate var2) {
      this(var1, var2, true);
   }

   public EditionInventory(@NotNull Player var1, @NotNull MMOItemTemplate var2, boolean var3) {
      super(var1);
      this.displaysBack = var3;
      this.ffp = new FriendlyFeedbackProvider(FFPMMOItems.get());
      this.ffp.activatePrefix(true, "Edition");
      this.template = var2;
      this.configFile = var2.getType().getConfigFile();
      VInventoryView var4 = VersionUtils.getOpen(var1);
      if (var4.getTopInventory().getHolder() instanceof EditionInventory) {
         this.cachedItem = ((EditionInventory)var4.getTopInventory().getHolder()).cachedItem;
      }
   }

   @Override
   public Inventory getInventory() {
      return this.inventory;
   }

   public abstract void arrangeInventory();

   public void refreshInventory() {
      Validate.notNull(this.inventory, "Inventory has never been opened");
      this.inventory.clear();
      this.addEditionItems();
      this.arrangeInventory();
   }

   public void open(@Nullable EditionInventory var1) {
      this.previousPage = var1 == null ? 0 : var1.previousPage;
      this.open();
   }

   @Deprecated
   public void open(int var1) {
      this.previousPage = var1;
      this.open();
   }

   @Override
   public void open() {
      if (this.inventory == null) {
         this.inventory = Bukkit.createInventory(this, 54, this.getName());
      }

      this.addEditionItems();
      this.arrangeInventory();
      super.open();
   }

   public abstract String getName();

   public MMOItemTemplate getEdited() {
      return this.template;
   }

   public ConfigurationSection getEditedSection() {
      ConfigurationSection var1 = this.configFile.getConfig().getConfigurationSection(this.template.getId());
      Validate.notNull(
         var1,
         "Could not find config section associated to the template '"
            + this.template.getType().getId()
            + "."
            + this.template.getId()
            + "': make sure the config section name is in capital letters"
      );
      return var1.getConfigurationSection(this.editedModifier == null ? "base" : "modifiers." + this.editedModifier.getId() + ".stats");
   }

   public <R extends RandomStatData<S>, S extends StatData> Optional<R> getEventualStatData(ItemStat<R, S> var1) {
      Map var2 = this.editedModifier != null ? this.editedModifier.getItemData() : this.template.getBaseItemData();
      return var2.containsKey(var1) ? Optional.of((R)var2.get(var1)) : Optional.empty();
   }

   public void registerTemplateEdition() {
      this.configFile.registerTemplateEdition(this.template);
      this.template = MMOItems.plugin.getTemplates().getTemplate(this.template.getType(), this.template.getId());
      this.updateCachedItem();
      this.refreshInventory();
   }

   public void updateCachedItem() {
      this.cachedItem = this.template.newBuilder(PlayerData.get(this.getPlayer()).getRPG()).build().newBuilder().buildSilently();
   }

   public ItemStack getCachedItem() {
      if (this.cachedItem != null) {
         return this.cachedItem;
      }

      this.updateCachedItem();
      return this.cachedItem;
   }

   public void addEditionItems() {
      ItemStack var1 = new ItemStack(Material.CHEST);
      ItemMeta var2 = var1.getItemMeta();
      var2.addItemFlags(ItemFlag.values());
      var2.setDisplayName(ChatColor.GREEN + "✤" + " Get the Item! " + "✤");
      ArrayList var3 = new ArrayList();
      var3.add(ChatColor.GRAY + "");
      var3.add(ChatColor.GRAY + "You may also use /mi give " + this.template.getType().getId() + " " + this.template.getId());
      var3.add(ChatColor.GRAY + "");
      var3.add(ChatColor.YELLOW + "▸" + " Left click to get the item.");
      var3.add(ChatColor.YELLOW + "▸" + " Right click to reroll its stats.");
      var2.setLore(var3);
      var1.setItemMeta(var2);
      if (this.displaysBack) {
         ItemStack var4 = new ItemStack(Material.BARRIER);
         ItemMeta var5 = var4.getItemMeta();
         var5.setDisplayName(ChatColor.GREEN + "⇨" + " Back");
         var4.setItemMeta(var5);
         this.inventory.setItem(6, var4);
      }

      this.inventory.setItem(2, var1);
      this.inventory.setItem(4, this.getCachedItem());
   }

   public int getPreviousPage() {
      return this.previousPage;
   }

   @NotNull
   public FriendlyFeedbackProvider getFFP() {
      return this.ffp;
   }
}
