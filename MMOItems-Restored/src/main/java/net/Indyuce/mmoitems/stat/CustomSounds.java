package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.CustomSound;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.SoundsEdition;
import net.Indyuce.mmoitems.stat.data.SoundData;
import net.Indyuce.mmoitems.stat.data.SoundListData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomSounds extends ItemStat<SoundListData, SoundListData> implements GemStoneStat, PlayerConsumable {
   public CustomSounds() {
      super("SOUNDS", Material.JUKEBOX, "Custom Sounds", new String[]{"The custom sounds your item will use."}, new String[0]);
   }

   public SoundListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection var2 = (ConfigurationSection)var1;
      SoundListData var3 = new SoundListData();

      for (CustomSound var7 : CustomSound.values()) {
         String var8 = var7.name().replace("_", "-").toLowerCase();
         if (var2.contains(var8)) {
            var3.set(var7, new SoundData(var2.get(var8)));
         }
      }

      return var3;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new SoundsEdition(var1.getPlayer(), var1.getEdited()).open(var1);
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("sounds")) {
         var1.getEditedSection().set("sounds", null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Custom Sounds successfully removed.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = (String)var3[0];
      String[] var5 = var2.split(" ");
      Validate.isTrue(var5.length == 3, var2 + " is not a valid [SOUND NAME] [VOLUME] [PITCH].");
      String var6 = var5[0].replace("-", "_");
      double var7 = MMOUtils.parseDouble(var5[1]);
      double var9 = MMOUtils.parseDouble(var5[2]);
      var1.getEditedSection().set("sounds." + var4 + ".sound", var6);
      var1.getEditedSection().set("sounds." + var4 + ".volume", var7);
      var1.getEditedSection().set("sounds." + var4 + ".pitch", var9);
      var1.registerTemplateEdition();
      var1.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + ChatColor.RED
               + UtilityMethods.caseOnWords(var4.replace(".", " "))
               + ChatColor.GRAY
               + " successfully changed to '"
               + var6
               + "'."
         );
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<SoundListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         SoundListData var3 = (SoundListData)var2.get();
         var3.mapData()
            .forEach(
               (var1x, var2x) -> var1.add(
                  ChatColor.GRAY
                     + "* "
                     + ChatColor.GREEN
                     + UtilityMethods.caseOnWords(var1x.getName().toLowerCase().replace("-", " ").replace("_", " "))
                     + ChatColor.GRAY
                     + ": "
                     + ChatColor.RED
                     + var2x.getVolume()
                     + " "
                     + var2x.getPitch()
               )
            );
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to access the sounds edition menu.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove all custom sounds.");
   }

   @NotNull
   public SoundListData getClearStatData() {
      return new SoundListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull SoundListData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull SoundListData var1) {
      ArrayList var2 = new ArrayList();
      SoundListData var3 = var1;
      var3.getCustomSounds().forEach(var2x -> {
         SoundData var3x = var3.get(var2x);
         var2.add(new ItemTag("MMOITEMS_SOUND_" + var2x.name(), var3x.getSound()));
         var2.add(new ItemTag("MMOITEMS_SOUND_" + var2x.name() + "_VOL", var3x.getVolume()));
         var2.add(new ItemTag("MMOITEMS_SOUND_" + var2x.name() + "_PIT", var3x.getPitch()));
      });
      return var2;
   }

   @Nullable
   public SoundListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      SoundListData var2 = new SoundListData();

      for (CustomSound var6 : CustomSound.values()) {
         ItemTag var7 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + var6.name(), var1);
         ItemTag var8 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + var6.name() + "_VOL", var1);
         ItemTag var9 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + var6.name() + "_PIT", var1);
         if (var7 != null && var8 != null && var9 != null) {
            String var10 = (String)var7.getValue();
            Double var11 = (Double)var8.getValue();
            Double var12 = (Double)var9.getValue();
            if (!var10.isEmpty()) {
               var2.set(var6, new SoundData(var10, var11, var12));
            }
         }
      }

      return var2.getCustomSounds().size() > 0 ? var2 : null;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();

      for (CustomSound var6 : CustomSound.values()) {
         ItemTag var7 = null;
         ItemTag var8 = null;
         ItemTag var9 = null;
         if (var1.getNBT().hasTag("MMOITEMS_SOUND_" + var6.name())) {
            var7 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + var6.name(), var1.getNBT(), SupportedNBTTagValues.STRING);
         }

         if (var1.getNBT().hasTag("MMOITEMS_SOUND_" + var6.name() + "_VOL")) {
            var8 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + var6.name() + "_VOL", var1.getNBT(), SupportedNBTTagValues.DOUBLE);
         }

         if (var1.getNBT().hasTag("MMOITEMS_SOUND_" + var6.name() + "_PIT")) {
            var9 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + var6.name() + "_PIT", var1.getNBT(), SupportedNBTTagValues.DOUBLE);
         }

         if (var7 != null && var8 != null && var9 != null) {
            var2.add(var7);
            var2.add(var8);
            var2.add(var9);
         }
      }

      SoundListData var10 = this.getLoadedNBT(var2);
      if (var10 != null) {
         var1.setData(ItemStats.CUSTOM_SOUNDS, var10);
      }
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem var1, @NotNull Player var2, boolean var3) {
      if (!var1.hasData(ItemStats.CUSTOM_SOUNDS)) {
         this.playDefaultSound(var2);
      } else {
         SoundListData var4 = (SoundListData)var1.getData(ItemStats.CUSTOM_SOUNDS);
         SoundData var5 = var4.get(CustomSound.ON_CONSUME);
         if (var5 == null) {
            this.playDefaultSound(var2);
         } else {
            String var6 = var5.getSound().toLowerCase().replace("_", ".");
            var2.getWorld().playSound(var2.getLocation(), var6, (float)var5.getVolume(), (float)var5.getPitch());
         }
      }
   }

   void playDefaultSound(@NotNull Player var1) {
      var1.getWorld().playSound(var1.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
   }
}
