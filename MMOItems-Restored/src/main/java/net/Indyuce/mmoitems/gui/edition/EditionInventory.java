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

   public EditionInventory(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate) {
      this(player, mmoItemTemplate, true);
   }

   public EditionInventory(@NotNull Player player, @NotNull MMOItemTemplate mmoItemTemplate, boolean flag) {
      super(player);
      this.displaysBack = flag;
      this.ffp = new FriendlyFeedbackProvider(FFPMMOItems.get());
      this.ffp.activatePrefix(true, "Edition");
      this.template = mmoItemTemplate;
      this.configFile = mmoItemTemplate.getType().getConfigFile();
      VInventoryView openValue = VersionUtils.getOpen(player);
      if (openValue.getTopInventory().getHolder() instanceof EditionInventory) {
         this.cachedItem = ((EditionInventory)openValue.getTopInventory().getHolder()).cachedItem;
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

   public void open(@Nullable EditionInventory editionInventory) {
      this.previousPage = editionInventory == null ? 0 : editionInventory.previousPage;
      this.open();
   }

   @Deprecated
   public void open(int count) {
      this.previousPage = count;
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
      ConfigurationSection section = this.configFile.getConfig().getConfigurationSection(this.template.getId());
      Validate.notNull(
         section,
         "Could not find config section associated to the template '"
            + this.template.getType().getId()
            + "."
            + this.template.getId()
            + "': make sure the config section name is in capital letters"
      );
      return section.getConfigurationSection(this.editedModifier == null ? "base" : "modifiers." + this.editedModifier.getId() + ".stats");
   }

   public <R extends RandomStatData<S>, S extends StatData> Optional<R> getEventualStatData(ItemStat<R, S> itemStat) {
      Map map = this.editedModifier != null ? this.editedModifier.getItemData() : this.template.getBaseItemData();
      return map.containsKey(itemStat) ? Optional.of((R)map.get(itemStat)) : Optional.empty();
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
      ItemStack item = new ItemStack(Material.CHEST);
      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.addItemFlags(ItemFlag.values());
      itemMeta.setDisplayName(ChatColor.GREEN + "✤" + " Get the Item! " + "✤");
      ArrayList list = new ArrayList();
      list.add(ChatColor.GRAY + "");
      list.add(ChatColor.GRAY + "You may also use /mi give " + this.template.getType().getId() + " " + this.template.getId());
      list.add(ChatColor.GRAY + "");
      list.add(ChatColor.YELLOW + "▸" + " Left click to get the item.");
      list.add(ChatColor.YELLOW + "▸" + " Right click to reroll its stats.");
      itemMeta.setLore(list);
      item.setItemMeta(itemMeta);
      if (this.displaysBack) {
         ItemStack item2 = new ItemStack(Material.BARRIER);
         ItemMeta itemMeta2 = item2.getItemMeta();
         itemMeta2.setDisplayName(ChatColor.GREEN + "⇨" + " Back");
         item2.setItemMeta(itemMeta2);
         this.inventory.setItem(6, item2);
      }

      this.inventory.setItem(2, item);
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
