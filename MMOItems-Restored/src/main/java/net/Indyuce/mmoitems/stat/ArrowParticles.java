package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import io.lumine.mythic.lib.version.VParticle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.ArrowParticlesEdition;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.ArrowParticlesData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArrowParticles extends ItemStat<ArrowParticlesData, ArrowParticlesData> {
   public ArrowParticles() {
      super(
         "ARROW_PARTICLES",
         Material.SPECTRAL_ARROW,
         "Arrow Particles",
         new String[]{"Particles that display around", "the arrows your bow fires."},
         new String[]{"bow", "crossbow"}
      );
   }

   public ArrowParticlesData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a valid config section");
      ConfigurationSection section = (ConfigurationSection)obj;
      Validate.isTrue(section.contains("particle"), "Could not find arrow particle");
      Particle particle = Particle.valueOf(section.getString("particle").toUpperCase().replace("-", "_").replace(" ", "_"));
      int amount = section.getInt("amount");
      double offset = section.getDouble("offset");
      return MMOUtils.isColorable(particle)
         ? new ArrowParticlesData(particle, amount, offset, section.getInt("color.red"), section.getInt("color.green"), section.getInt("color.blue"))
         : new ArrowParticlesData(particle, amount, offset, section.getDouble("speed"));
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull ArrowParticlesData arrowParticlesData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(arrowParticlesData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ArrowParticlesData arrowParticlesData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), arrowParticlesData.toString()));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      ArrowParticlesData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public ArrowParticlesData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         try {
            JsonObject asJsonObject = new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonObject();
            Particle particle = Particle.valueOf(asJsonObject.get("Particle").getAsString());
            int asInt = asJsonObject.get("Amount").getAsInt();
            double asDouble = asJsonObject.get("Offset").getAsDouble();
            if (MMOUtils.isColorable(particle)) {
               return new ArrowParticlesData(particle, asInt, asDouble, asJsonObject.get("Red").getAsInt(), asJsonObject.get("Green").getAsInt(), asJsonObject.get("Blue").getAsInt());
            }

            return new ArrowParticlesData(particle, asInt, asDouble, asJsonObject.get("Speed").getAsDouble());
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      new ArrowParticlesEdition(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String text2 = (String)objs[0];
      if (text2.equals("color")) {
         String[] texts = text.split(" ");
         int count = Integer.parseInt(texts[0]);
         int count2 = Integer.parseInt(texts[1]);
         int count3 = Integer.parseInt(texts[2]);
         editionInventory.getEditedSection().set("arrow-particles.color.red", count);
         editionInventory.getEditedSection().set("arrow-particles.color.green", count2);
         editionInventory.getEditedSection().set("arrow-particles.color.blue", count3);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Particle color successfully set to "
                  + ChatColor.translateAlternateColorCodes('&', "&c&l" + count + "&7 - &a&l" + count2 + "&7 - &9&l" + count3)
            );
      } else if (text2.equals("particle")) {
         Particle particle = Particle.valueOf(text.toUpperCase().replace("-", "_").replace(" ", "_"));
         editionInventory.getEditedSection().set("arrow-particles.particle", particle.name());
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
      } else if (text2.equals("amount")) {
         int count = Integer.parseInt(text);
         editionInventory.getEditedSection().set("arrow-particles.amount", count);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GOLD + "Amount" + ChatColor.GRAY + " set to " + ChatColor.GOLD + count + ChatColor.GRAY + ".");
      } else {
         double value = MMOUtils.parseDouble(text);
         editionInventory.getEditedSection().set("arrow-particles." + text2, value);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + ChatColor.GOLD
                  + UtilityMethods.caseOnWords(text2.replace("-", " "))
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
   public void whenDisplayed(List<String> texts, Optional<ArrowParticlesData> optional) {
      if (optional.isPresent()) {
         ArrowParticlesData arrowParticlesData = (ArrowParticlesData)optional.get();
         texts.add(ChatColor.GRAY + "Current Value:");
         texts.add(ChatColor.GRAY + "* Particle: " + ChatColor.GOLD + UtilityMethods.caseOnWords(arrowParticlesData.getParticle().name().replace("_", " ").toLowerCase()));
         texts.add(ChatColor.GRAY + "* Amount: " + ChatColor.WHITE + arrowParticlesData.getAmount());
         texts.add(ChatColor.GRAY + "* Offset: " + ChatColor.WHITE + arrowParticlesData.getOffset());
         texts.add("");
         if (MMOUtils.isColorable(arrowParticlesData.getParticle())) {
            texts.add(
               ChatColor.translateAlternateColorCodes('&', "&7* Color: &c&l" + arrowParticlesData.getRed() + "&7 - &a&l" + arrowParticlesData.getGreen() + "&7 - &9&l" + arrowParticlesData.getBlue())
            );
         } else {
            texts.add(ChatColor.GRAY + "* Speed: " + ChatColor.WHITE + arrowParticlesData.getSpeed());
         }
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to edit.");
   }

   @NotNull
   public ArrowParticlesData getClearStatData() {
      return new ArrowParticlesData(VParticle.LARGE_EXPLOSION.get(), 1, 0.0, 1.0);
   }
}
