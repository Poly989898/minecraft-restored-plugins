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

   public SoundListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection section = (ConfigurationSection)obj;
      SoundListData soundListData = new SoundListData();

      for (CustomSound customSound : CustomSound.values()) {
         String text = customSound.name().replace("_", "-").toLowerCase();
         if (section.contains(text)) {
            soundListData.set(customSound, new SoundData(section.get(text)));
         }
      }

      return soundListData;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new SoundsEdition(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("sounds")) {
         editionInventory.getEditedSection().set("sounds", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Custom Sounds successfully removed.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String text2 = (String)objs[0];
      String[] texts = text.split(" ");
      Validate.isTrue(texts.length == 3, text + " is not a valid [SOUND NAME] [VOLUME] [PITCH].");
      String text3 = texts[0].replace("-", "_");
      double value = MMOUtils.parseDouble(texts[1]);
      double value2 = MMOUtils.parseDouble(texts[2]);
      editionInventory.getEditedSection().set("sounds." + text2 + ".sound", text3);
      editionInventory.getEditedSection().set("sounds." + text2 + ".volume", value);
      editionInventory.getEditedSection().set("sounds." + text2 + ".pitch", value2);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + ChatColor.RED
               + UtilityMethods.caseOnWords(text2.replace(".", " "))
               + ChatColor.GRAY
               + " successfully changed to '"
               + text3
               + "'."
         );
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<SoundListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         SoundListData soundListData = (SoundListData)optional.get();
         soundListData.mapData()
            .forEach(
               (var1x, var2x) -> texts.add(
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
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to access the sounds edition menu.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove all custom sounds.");
   }

   @NotNull
   public SoundListData getClearStatData() {
      return new SoundListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull SoundListData soundListData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(soundListData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull SoundListData soundListData) {
      ArrayList list = new ArrayList();
      SoundListData soundListData2 = soundListData;
      soundListData2.getCustomSounds().forEach(var2x -> {
         SoundData var3x = soundListData2.get(var2x);
         list.add(new ItemTag("MMOITEMS_SOUND_" + var2x.name(), var3x.getSound()));
         list.add(new ItemTag("MMOITEMS_SOUND_" + var2x.name() + "_VOL", var3x.getVolume()));
         list.add(new ItemTag("MMOITEMS_SOUND_" + var2x.name() + "_PIT", var3x.getPitch()));
      });
      return list;
   }

   @Nullable
   public SoundListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      SoundListData soundListData = new SoundListData();

      for (CustomSound customSound : CustomSound.values()) {
         ItemTag tagAtPath = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name(), itemTags);
         ItemTag tagAtPath2 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name() + "_VOL", itemTags);
         ItemTag tagAtPath3 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name() + "_PIT", itemTags);
         if (tagAtPath != null && tagAtPath2 != null && tagAtPath3 != null) {
            String text = (String)tagAtPath.getValue();
            Double value = (Double)tagAtPath2.getValue();
            Double value2 = (Double)tagAtPath3.getValue();
            if (!text.isEmpty()) {
               soundListData.set(customSound, new SoundData(text, value, value2));
            }
         }
      }

      return soundListData.getCustomSounds().size() > 0 ? soundListData : null;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();

      for (CustomSound customSound : CustomSound.values()) {
         ItemTag itemTag = null;
         ItemTag itemTag2 = null;
         ItemTag itemTag3 = null;
         if (readMMOItem.getNBT().hasTag("MMOITEMS_SOUND_" + customSound.name())) {
            itemTag = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
         }

         if (readMMOItem.getNBT().hasTag("MMOITEMS_SOUND_" + customSound.name() + "_VOL")) {
            itemTag2 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name() + "_VOL", readMMOItem.getNBT(), SupportedNBTTagValues.DOUBLE);
         }

         if (readMMOItem.getNBT().hasTag("MMOITEMS_SOUND_" + customSound.name() + "_PIT")) {
            itemTag3 = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name() + "_PIT", readMMOItem.getNBT(), SupportedNBTTagValues.DOUBLE);
         }

         if (itemTag != null && itemTag2 != null && itemTag3 != null) {
            list.add(itemTag);
            list.add(itemTag2);
            list.add(itemTag3);
         }
      }

      SoundListData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(ItemStats.CUSTOM_SOUNDS, loadedNBT);
      }
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem volatileMMOItem, @NotNull Player player, boolean flag) {
      if (!volatileMMOItem.hasData(ItemStats.CUSTOM_SOUNDS)) {
         this.playDefaultSound(player);
      } else {
         SoundListData data = (SoundListData)volatileMMOItem.getData(ItemStats.CUSTOM_SOUNDS);
         SoundData soundData = data.get(CustomSound.ON_CONSUME);
         if (soundData == null) {
            this.playDefaultSound(player);
         } else {
            String text = soundData.getSound().toLowerCase().replace("_", ".");
            player.getWorld().playSound(player.getLocation(), text, (float)soundData.getVolume(), (float)soundData.getPitch());
         }
      }
   }

   void playDefaultSound(@NotNull Player player) {
      player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
   }
}
