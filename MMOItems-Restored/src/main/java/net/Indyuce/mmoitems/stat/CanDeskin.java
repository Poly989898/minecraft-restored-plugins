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
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, Type var5
   ) {
      String var6 = var4.getString("MMOITEMS_SKIN_ID");
      Player var7 = var2.getPlayer();
      if (var3.getNBTItem().getBoolean("MMOITEMS_CAN_DESKIN") && !var6.isEmpty()) {
         String var8 = var4.getString("MMOITEMS_ITEM_ID");
         var4.removeTag(new String[]{"MMOITEMS_SKIN_ID"});
         MMOItemTemplate var9 = MMOItems.plugin.getTemplates().getTemplateOrThrow(var5, var8);
         MMOItem var10 = var9.newBuilder(var2.getRPG()).build();
         ItemStack var11 = var9.newBuilder(var2.getRPG()).build().newBuilder().build();
         if (var10.hasData(ItemStats.ITEM_PARTICLES)) {
            JsonObject var12 = ((ParticleData)var10.getData(ItemStats.ITEM_PARTICLES)).toJson();
            var4.addTag(new ItemTag[]{new ItemTag("MMOITEMS_ITEM_PARTICLES", var12.toString())});
         } else {
            var4.removeTag(new String[]{"MMOITEMS_ITEM_PARTICLES"});
         }

         ItemStack var22 = var4.toItem();
         ItemMeta var13 = var22.getItemMeta();
         ItemMeta var14 = var11.getItemMeta();
         Integer var15 = var14.hasCustomModelData() ? var14.getCustomModelData() : null;
         var13.setCustomModelData(var15);
         if (var13.isUnbreakable()) {
            var13.setUnbreakable(var14.isUnbreakable());
            if (var13 instanceof Damageable && var14 instanceof Damageable) {
               ((Damageable)var13).setDamage(((Damageable)var14).getDamage());
            }
         }

         if (var13 instanceof LeatherArmorMeta && var14 instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta)var13).setColor(((LeatherArmorMeta)var14).getColor());
         }

         if (var13 instanceof ArmorMeta && var14 instanceof ArmorMeta) {
            ((ArmorMeta)var13).setTrim(((ArmorMeta)var14).getTrim());
            if (var14.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
               var13.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            } else {
               var13.removeItemFlags(new ItemFlag[]{ItemFlag.HIDE_ARMOR_TRIM});
            }
         }

         if (var4.hasTag("SkullOwner") && var22.getType() == Material.PLAYER_HEAD && var11.getType() == Material.PLAYER_HEAD) {
            MythicLib.plugin
               .getVersion()
               .getWrapper()
               .setProfile((SkullMeta)var13, ((SkullTextureData)var10.getData(ItemStats.SKULL_TEXTURE)).getGameProfile());
         }

         ItemStack var16 = var4.getItem();
         var16.setItemMeta(var13);
         var16.setType(var11.getType());

         try {
            String var17 = var4.getString("MMOITEMS_SKIN_TYPE");
            Type var18 = Objects.requireNonNullElse(Type.get(var17), Type.SKIN);
            MMOItemTemplate var19 = MMOItems.plugin.getTemplates().getTemplateOrThrow(var18, var6);
            Validate.notNull(var19, "Could not find item template of applied skin, type used " + var18.getName());
            MMOItem var20 = var19.newBuilder(var2.getRPG()).build();
            new SmartGive(var7).give(new ItemStack[]{var20.newBuilder().build()});
         } catch (Exception var21) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, "Could not retrieve item skin with ID '" + var6 + "' for player " + var2.getUniqueId());
         }

         Message.SKIN_REMOVED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var22)).send(var7);
         return true;
      } else {
         return false;
      }
   }
}
