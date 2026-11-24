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

   public SkullTextureData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection section = (ConfigurationSection)obj;
      String text = section.getString("value");
      Validate.notNull(text, "Could not load skull texture value");
      String uuid = section.getString("uuid");
      Validate.notNull(uuid, "Could not find skull texture UUID: re-enter your skull texture value and one will be selected randomly.");
      Object profile = MythicLib.plugin.getVersion().getWrapper().newProfile(UUID.fromString(uuid), text);
      return new SkullTextureData(profile);
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<SkullTextureData> optional) {
      texts.add(ChatColor.GRAY + "Current Value: " + (optional.isPresent() ? ChatColor.GREEN + "Texture value provided" : ChatColor.RED + "None"));
      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      editionInventory.getEditedSection().set("skull-texture.value", text);
      editionInventory.getEditedSection().set("skull-texture.uuid", UUID.randomUUID().toString());
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " successfully changed to " + text + ".");
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull SkullTextureData skullTextureData) {
      if (itemStackBuilder.getItemStack().getType() == Material.PLAYER_HEAD) {
         if (skullTextureData.getGameProfile() != null) {
            MythicLib.plugin.getVersion().getWrapper().setProfile((SkullMeta)itemStackBuilder.getMeta(), skullTextureData.getGameProfile());
         }
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull SkullTextureData skullTextureData) {
      return new ArrayList<>();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set(this.getPath(), null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ".");
      } else {
         new StatEdition(editionInventory, this).enable("Write in the chat the text you want.");
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      try {
         ItemMeta itemMeta = readMMOItem.getNBT().getItem().getItemMeta();
         Validate.isTrue(itemMeta instanceof SkullMeta, "Item is not a skull");
         Object profile = MythicLib.plugin.getVersion().getWrapper().getProfile((SkullMeta)itemMeta);
         readMMOItem.setData(ItemStats.SKULL_TEXTURE, new SkullTextureData(profile));
      } catch (RuntimeException exception) {
      }
   }

   @Nullable
   public SkullTextureData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return null;
   }

   @NotNull
   public SkullTextureData getClearStatData() {
      return new SkullTextureData(null);
   }
}
