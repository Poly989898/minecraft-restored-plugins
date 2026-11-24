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
      String soundId = (String)objs[0];
      String[] texts = text.split(" ");
      Validate.isTrue(texts.length == 3, text + " is not a valid [SOUND NAME] [VOLUME] [PITCH].");
      String soundName = texts[0].replace("-", "_");
      double value = MMOUtils.parseDouble(texts[1]);
      double pitch = MMOUtils.parseDouble(texts[2]);
      editionInventory.getEditedSection().set("sounds." + soundId + ".sound", soundName);
      editionInventory.getEditedSection().set("sounds." + soundId + ".volume", value);
      editionInventory.getEditedSection().set("sounds." + soundId + ".pitch", pitch);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + ChatColor.RED
               + UtilityMethods.caseOnWords(soundId.replace(".", " "))
               + ChatColor.GRAY
               + " successfully changed to '"
               + soundName
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
      SoundListData soundData = soundListData;
      soundData.getCustomSounds().forEach(var2x -> {
         SoundData var3x = soundData.get(var2x);
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
         ItemTag volumeTag = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name() + "_VOL", itemTags);
         ItemTag pitchTag = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name() + "_PIT", itemTags);
         if (tagAtPath != null && volumeTag != null && pitchTag != null) {
            String text = (String)tagAtPath.getValue();
            Double value = (Double)volumeTag.getValue();
            Double pitchValue = (Double)pitchTag.getValue();
            if (!text.isEmpty()) {
               soundListData.set(customSound, new SoundData(text, value, pitchValue));
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
         ItemTag pitchItemTag = null;
         ItemTag volumeItemTag = null;
         if (readMMOItem.getNBT().hasTag("MMOITEMS_SOUND_" + customSound.name())) {
            itemTag = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
         }

         if (readMMOItem.getNBT().hasTag("MMOITEMS_SOUND_" + customSound.name() + "_VOL")) {
            pitchItemTag = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name() + "_VOL", readMMOItem.getNBT(), SupportedNBTTagValues.DOUBLE);
         }

         if (readMMOItem.getNBT().hasTag("MMOITEMS_SOUND_" + customSound.name() + "_PIT")) {
            volumeItemTag = ItemTag.getTagAtPath("MMOITEMS_SOUND_" + customSound.name() + "_PIT", readMMOItem.getNBT(), SupportedNBTTagValues.DOUBLE);
         }

         if (itemTag != null && pitchItemTag != null && volumeItemTag != null) {
            list.add(itemTag);
            list.add(pitchItemTag);
            list.add(volumeItemTag);
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
