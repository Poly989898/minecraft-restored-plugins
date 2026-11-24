package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.SmartGive;
import io.lumine.mythic.lib.gson.JsonObject;
import java.util.Objects;
import java.util.logging.Level;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import net.Indyuce.mmoitems.stat.data.SkullTextureData;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class CanDeskin extends BooleanStat implements ConsumableItemInteraction {
   public CanDeskin() {
      super(
         "CAN_DESKIN",
         Material.LEATHER,
         "Can Deskin?",
         new String[]{"Players can deskin their item", "and get their skin back", "from the item."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, Type type
   ) {
      String mMOITEMSSKINID = nbtItem.getString("MMOITEMS_SKIN_ID");
      Player player = playerData.getPlayer();
      if (consumable.getNBTItem().getBoolean("MMOITEMS_CAN_DESKIN") && !mMOITEMSSKINID.isEmpty()) {
         String mMOITEMSITEMID = nbtItem.getString("MMOITEMS_ITEM_ID");
         nbtItem.removeTag(new String[]{"MMOITEMS_SKIN_ID"});
         MMOItemTemplate templateOrThrow = MMOItems.plugin.getTemplates().getTemplateOrThrow(type, mMOITEMSITEMID);
         MMOItem mmoItem = templateOrThrow.newBuilder(playerData.getRPG()).build();
         ItemStack item = templateOrThrow.newBuilder(playerData.getRPG()).build().newBuilder().build();
         if (mmoItem.hasData(ItemStats.ITEM_PARTICLES)) {
            JsonObject jsonObject = ((ParticleData)mmoItem.getData(ItemStats.ITEM_PARTICLES)).toJson();
            nbtItem.addTag(new ItemTag[]{new ItemTag("MMOITEMS_ITEM_PARTICLES", jsonObject.toString())});
         } else {
            nbtItem.removeTag(new String[]{"MMOITEMS_ITEM_PARTICLES"});
         }

         ItemStack item2 = nbtItem.toItem();
         ItemMeta itemMeta = item2.getItemMeta();
         ItemMeta itemMeta2 = item.getItemMeta();
         Integer value = itemMeta2.hasCustomModelData() ? itemMeta2.getCustomModelData() : null;
         itemMeta.setCustomModelData(value);
         if (itemMeta.isUnbreakable()) {
            itemMeta.setUnbreakable(itemMeta2.isUnbreakable());
            if (itemMeta instanceof Damageable && itemMeta2 instanceof Damageable) {
               ((Damageable)itemMeta).setDamage(((Damageable)itemMeta2).getDamage());
            }
         }

         if (itemMeta instanceof LeatherArmorMeta && itemMeta2 instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta)itemMeta).setColor(((LeatherArmorMeta)itemMeta2).getColor());
         }

         if (itemMeta instanceof ArmorMeta && itemMeta2 instanceof ArmorMeta) {
            ((ArmorMeta)itemMeta).setTrim(((ArmorMeta)itemMeta2).getTrim());
            if (itemMeta2.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
               itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            } else {
               itemMeta.removeItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            }
         }

         if (nbtItem.hasTag("SkullOwner") && item2.getType() == Material.PLAYER_HEAD && item.getType() == Material.PLAYER_HEAD) {
            MythicLib.plugin
               .getVersion()
               .getWrapper()
               .setProfile((SkullMeta)itemMeta, ((SkullTextureData)mmoItem.getData(ItemStats.SKULL_TEXTURE)).getGameProfile());
         }

         ItemStack item3 = nbtItem.getItem();
         item3.setItemMeta(itemMeta);
         item3.setType(item.getType());

         try {
            String mMOITEMSSKINTYPE = nbtItem.getString("MMOITEMS_SKIN_TYPE");
            Type type2 = Objects.requireNonNullElse(Type.get(mMOITEMSSKINTYPE), Type.SKIN);
            MMOItemTemplate templateOrThrow2 = MMOItems.plugin.getTemplates().getTemplateOrThrow(type2, mMOITEMSSKINID);
            Validate.notNull(templateOrThrow2, "Could not find item template of applied skin, type used " + type2.getName());
            MMOItem mmoItem2 = templateOrThrow2.newBuilder(playerData.getRPG()).build();
            new SmartGive(player).give(new ItemStack[]{mmoItem2.newBuilder().build()});
         } catch (Exception exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not retrieve item skin with ID '" + mMOITEMSSKINID + "' for player " + playerData.getUniqueId());
         }

         Message.SKIN_REMOVED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(item2)).send(player);
         return true;
      } else {
         return false;
      }
   }
}
