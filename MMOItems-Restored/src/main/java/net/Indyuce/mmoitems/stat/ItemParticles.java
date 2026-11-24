package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import io.lumine.mythic.lib.version.VParticle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.ParticlesEdition;
import net.Indyuce.mmoitems.particle.api.ParticleType;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemParticles extends ItemStat<ParticleData, ParticleData> {
   public ItemParticles() {
      super(
         "ITEM_PARTICLES",
         Material.PINK_STAINED_GLASS,
         "Item Particles",
         new String[]{"The particles displayed when", "holding/wearing your item.", "", ChatColor.BLUE + "A tutorial is available on the wiki."},
         new String[]{"all", "!block"}
      );
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set(this.getPath(), null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ChatColor.GRAY + ".");
      } else {
         new ParticlesEdition(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
      }
   }

   public ParticleData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      return new ParticleData((ConfigurationSection)obj);
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull ParticleData particleData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(particleData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ParticleData particleData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), particleData.toJson().toString()));
      return list;
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<ParticleData> optional) {
      texts.add(ChatColor.YELLOW + "►" + " Left click to setup the item particles.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to clear.");
   }

   @NotNull
   public ParticleData getClearStatData() {
      return new ParticleData(ParticleType.AURA, VParticle.LARGE_EXPLOSION.get());
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String particleName = (String)objs[0];
      String normalizedType = text.toUpperCase().replace("-", "_").replace(" ", "_");
      if (particleName.equals("particle-type")) {
         ParticleType particleType = ParticleType.valueOf(normalizedType);
         editionInventory.getEditedSection().set("item-particles.type", particleType.name());
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Particle type successfully set to " + ChatColor.GOLD + particleType.getDefaultName() + ChatColor.GRAY + ".");
      } else if (particleName.equals("particle-color")) {
         String[] texts = text.split(" ");
         int count = Integer.parseInt(texts[0]);
         int amount = Integer.parseInt(texts[1]);
         int speed = Integer.parseInt(texts[2]);
         editionInventory.getEditedSection().set("item-particles.color.red", count);
         editionInventory.getEditedSection().set("item-particles.color.green", amount);
         editionInventory.getEditedSection().set("item-particles.color.blue", speed);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Particle color successfully set to "
                  + ChatColor.RED
                  + ChatColor.BOLD
                  + count
                  + ChatColor.GRAY
                  + " - "
                  + ChatColor.GREEN
                  + ChatColor.BOLD
                  + amount
                  + ChatColor.GRAY
                  + " - "
                  + ChatColor.BLUE
                  + ChatColor.BOLD
                  + speed
                  + ChatColor.GRAY
                  + "."
            );
      } else if (particleName.equals("particle")) {
         Particle particle = Particle.valueOf(normalizedType);
         editionInventory.getEditedSection().set("item-particles.particle", particle.name());
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Particle successfully set to "
                  + ChatColor.GOLD
                  + UtilityMethods.caseOnWords(particle.name().toLowerCase().replace("_", " "))
                  + ChatColor.GRAY
                  + "."
            );
      } else {
         double value = MMOUtils.parseDouble(text);
         editionInventory.getEditedSection().set("item-particles." + particleName, value);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + ChatColor.GOLD
                  + UtilityMethods.caseOnWords(particleName.replace("-", " "))
                  + ChatColor.GRAY
                  + " set to "
                  + ChatColor.GOLD
                  + value
                  + ChatColor.GRAY
                  + "."
            );
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      ParticleData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public ParticleData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         try {
            return new ParticleData(new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonObject());
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }
}
