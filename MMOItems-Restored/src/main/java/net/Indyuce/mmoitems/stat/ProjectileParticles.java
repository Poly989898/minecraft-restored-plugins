package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.ProjectileParticlesData;
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

public class ProjectileParticles extends ItemStat<ProjectileParticlesData, ProjectileParticlesData> {
   public ProjectileParticles() {
      super(
         "PROJECTILE_PARTICLES",
         Material.LIME_STAINED_GLASS,
         "Projectile Particles",
         new String[]{"The projectile particle that your weapon shoots"},
         new String[]{"lute"}
      );
   }

   public ProjectileParticlesData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a valid config section");
      ConfigurationSection section = (ConfigurationSection)obj;
      Validate.isTrue(section.contains("particle"), "Could not find projectile particle");
      Particle particle = Particle.valueOf(section.getString("particle").toUpperCase().replace("-", "_").replace(" ", ""));
      return MMOUtils.isColorable(particle)
         ? new ProjectileParticlesData(particle, section.getInt("color.red"), section.getInt("color.green"), section.getInt("color.blue"))
         : new ProjectileParticlesData(particle);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set("projectile-particles", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed the projectile particle.");
      } else {
         new StatEdition(editionInventory, this)
            .enable(
               "Write in the chat the particle you want along with the color if applicable.",
               ChatColor.AQUA + "Format: {Particle} {Color}",
               "All particles can be found here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html"
            );
      }
   }

   @NotNull
   public ProjectileParticlesData getClearStatData() {
      return new ProjectileParticlesData(Particle.FLAME);
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<ProjectileParticlesData> optional) {
      if (optional.isPresent()) {
         ProjectileParticlesData projectileParticlesData = (ProjectileParticlesData)optional.get();
         Particle particle = projectileParticlesData.getParticle();
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + particle);
         if (MMOUtils.isColorable(particle)) {
            String text = particle == Particle.NOTE ? String.valueOf(projectileParticlesData.getRed()) : projectileParticlesData.getRed() + " " + projectileParticlesData.getGreen() + " " + projectileParticlesData.getBlue();
            texts.add(ChatColor.GRAY + "Color: " + ChatColor.GREEN + text);
         }
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      String[] texts = text.replace(", ", " ").replace(",", " ").split(" ");
      Particle particle = Particle.valueOf(texts[0].toUpperCase().replace("-", "_").replace(" ", "_"));
      if (MMOUtils.isColorable(particle)) {
         Validate.isTrue(texts.length <= 4, "Too many arguments provided.");
         if (particle.equals(Particle.NOTE)) {
            Validate.isTrue(
               texts.length == 2,
               "You must provide a color for this particle.\n"
                  + MMOItems.plugin.getPrefix()
                  + "NOTE particle colors only take a single value between 1 and 24.\n"
                  + MMOItems.plugin.getPrefix()
                  + ChatColor.AQUA
                  + "Format: {Particle} {Color}"
            );
            int count = Math.min(24, Math.max(1, Integer.parseInt(texts[1])));
            editionInventory.getEditedSection().set("projectile-particles.particle", particle.name());
            editionInventory.getEditedSection().set("projectile-particles.color.red", count);
            editionInventory.getEditedSection().set("projectile-particles.color.green", 0);
            editionInventory.getEditedSection().set("projectile-particles.color.blue", 0);
            editionInventory.registerTemplateEdition();
            editionInventory.getPlayer()
               .sendMessage(
                  MMOItems.plugin.getPrefix()
                     + "Particle successfully set to "
                     + UtilityMethods.caseOnWords(particle.name().toLowerCase().replace("_", " "))
                     + " with color "
                     + count
               );
         } else {
            Validate.isTrue(
               texts.length == 4, "You must provide a color for this particle.\n" + MMOItems.plugin.getPrefix() + ChatColor.AQUA + "Format: {Particle} {R G B}"
            );
            int count = texts[1] != null ? Math.min(255, Math.max(0, Integer.parseInt(texts[1]))) : 0;
            int count2 = texts[2] != null ? Math.min(255, Math.max(0, Integer.parseInt(texts[2]))) : 0;
            int count3 = texts[3] != null ? Math.min(255, Math.max(0, Integer.parseInt(texts[3]))) : 0;
            editionInventory.getEditedSection().set("projectile-particles.particle", particle.name());
            editionInventory.getEditedSection().set("projectile-particles.color.red", count);
            editionInventory.getEditedSection().set("projectile-particles.color.green", count2);
            editionInventory.getEditedSection().set("projectile-particles.color.blue", count3);
            editionInventory.registerTemplateEdition();
            editionInventory.getPlayer()
               .sendMessage(
                  MMOItems.plugin.getPrefix()
                     + "Particle successfully set to "
                     + UtilityMethods.caseOnWords(particle.name().toLowerCase().replace("_", " "))
                     + " with RGB color "
                     + count
                     + " "
                     + count2
                     + " "
                     + count3
               );
         }
      } else {
         Validate.isTrue(texts.length == 1, "That particle cannot be assigned a color");
         editionInventory.getEditedSection().set("projectile-particles.particle", particle.name());
         editionInventory.getEditedSection().set("projectile-particles.color.red", 0);
         editionInventory.getEditedSection().set("projectile-particles.color.green", 0);
         editionInventory.getEditedSection().set("projectile-particles.color.blue", 0);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix() + "Particle successfully set to " + UtilityMethods.caseOnWords(particle.name().toLowerCase().replace("_", " "))
            );
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull ProjectileParticlesData projectileParticlesData) {
      itemStackBuilder.addItemTag(this.getAppliedNBT(projectileParticlesData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ProjectileParticlesData projectileParticlesData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), projectileParticlesData.toString()));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      ProjectileParticlesData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   public ProjectileParticlesData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         try {
            JsonObject asJsonObject = new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonObject();
            Particle particle = Particle.valueOf(asJsonObject.get("Particle").getAsString());
            if (MMOUtils.isColorable(particle)) {
               return new ProjectileParticlesData(particle, asJsonObject.get("Red").getAsInt(), asJsonObject.get("Green").getAsInt(), asJsonObject.get("Blue").getAsInt());
            }

            return new ProjectileParticlesData(particle);
         } catch (JsonSyntaxException exception) {
            exception.printStackTrace();
         }
      }

      return null;
   }
}
