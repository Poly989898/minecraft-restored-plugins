package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.SkullTextureData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkullTextureStat extends ItemStat<SkullTextureData, SkullTextureData> {
   public SkullTextureStat() {
      super(
         "SKULL_TEXTURE",
         Material.PLAYER_HEAD,
         "Skull Texture",
         new String[]{
            "The skull texture &nvalue&7 which can be found on",
            "head databases. 1.20+ users can also provide the",
            "texture URL directly (starting with https://...)."
         },
         new String[0],
         Material.PLAYER_HEAD
      );
   }

   public SkullTextureData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection var2 = (ConfigurationSection)var1;
      String var3 = var2.getString("value");
      Validate.notNull(var3, "Could not load skull texture value");
      String var4 = var2.getString("uuid");
      Validate.notNull(var4, "Could not find skull texture UUID: re-enter your skull texture value and one will be selected randomly.");
      Object var5 = MythicLib.plugin.getVersion().getWrapper().newProfile(UUID.fromString(var4), var3);
      return new SkullTextureData(var5);
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<SkullTextureData> var2) {
      var1.add(ChatColor.GRAY + "Current Value: " + (var2.isPresent() ? ChatColor.GREEN + "Texture value provided" : ChatColor.RED + "None"));
      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      var1.getEditedSection().set("skull-texture.value", var2);
      var1.getEditedSection().set("skull-texture.uuid", UUID.randomUUID().toString());
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " successfully changed to " + var2 + ".");
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull SkullTextureData var2) {
      if (var1.getItemStack().getType() == Material.PLAYER_HEAD) {
         if (var2.getGameProfile() != null) {
            MythicLib.plugin.getVersion().getWrapper().setProfile((SkullMeta)var1.getMeta(), var2.getGameProfile());
         }
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull SkullTextureData var1) {
      return new ArrayList<>();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         var1.getEditedSection().set(this.getPath(), null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ".");
      } else {
         new StatEdition(var1, this).enable("Write in the chat the text you want.");
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      try {
         ItemMeta var2 = var1.getNBT().getItem().getItemMeta();
         Validate.isTrue(var2 instanceof SkullMeta, "Item is not a skull");
         Object var3 = MythicLib.plugin.getVersion().getWrapper().getProfile((SkullMeta)var2);
         var1.setData(ItemStats.SKULL_TEXTURE, new SkullTextureData(var3));
      } catch (RuntimeException var4) {
      }
   }

   @Nullable
   public SkullTextureData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }

   @NotNull
   public SkullTextureData getClearStatData() {
      return new SkullTextureData(null);
   }
}
