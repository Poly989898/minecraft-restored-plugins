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

   public ProjectileParticlesData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a valid config section");
      ConfigurationSection var2 = (ConfigurationSection)var1;
      Validate.isTrue(var2.contains("particle"), "Could not find projectile particle");
      Particle var3 = Particle.valueOf(var2.getString("particle").toUpperCase().replace("-", "_").replace(" ", ""));
      return MMOUtils.isColorable(var3)
         ? new ProjectileParticlesData(var3, var2.getInt("color.red"), var2.getInt("color.green"), var2.getInt("color.blue"))
         : new ProjectileParticlesData(var3);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         var1.getEditedSection().set("projectile-particles", null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed the projectile particle.");
      } else {
         new StatEdition(var1, this)
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
   public void whenDisplayed(List<String> var1, Optional<ProjectileParticlesData> var2) {
      if (var2.isPresent()) {
         ProjectileParticlesData var3 = (ProjectileParticlesData)var2.get();
         Particle var4 = var3.getParticle();
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + var4);
         if (MMOUtils.isColorable(var4)) {
            String var5 = var4 == Particle.NOTE ? String.valueOf(var3.getRed()) : var3.getRed() + " " + var3.getGreen() + " " + var3.getBlue();
            var1.add(ChatColor.GRAY + "Color: " + ChatColor.GREEN + var5);
         }
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String[] var4 = var2.replace(", ", " ").replace(",", " ").split(" ");
      Particle var5 = Particle.valueOf(var4[0].toUpperCase().replace("-", "_").replace(" ", "_"));
      if (MMOUtils.isColorable(var5)) {
         Validate.isTrue(var4.length <= 4, "Too many arguments provided.");
         if (var5.equals(Particle.NOTE)) {
            Validate.isTrue(
               var4.length == 2,
               "You must provide a color for this particle.\n"
                  + MMOItems.plugin.getPrefix()
                  + "NOTE particle colors only take a single value between 1 and 24.\n"
                  + MMOItems.plugin.getPrefix()
                  + ChatColor.AQUA
                  + "Format: {Particle} {Color}"
            );
            int var6 = Math.min(24, Math.max(1, Integer.parseInt(var4[1])));
            var1.getEditedSection().set("projectile-particles.particle", var5.name());
            var1.getEditedSection().set("projectile-particles.color.red", var6);
            var1.getEditedSection().set("projectile-particles.color.green", 0);
            var1.getEditedSection().set("projectile-particles.color.blue", 0);
            var1.registerTemplateEdition();
            var1.getPlayer()
               .sendMessage(
                  MMOItems.plugin.getPrefix()
                     + "Particle successfully set to "
                     + UtilityMethods.caseOnWords(var5.name().toLowerCase().replace("_", " "))
                     + " with color "
                     + var6
               );
         } else {
            Validate.isTrue(
               var4.length == 4, "You must provide a color for this particle.\n" + MMOItems.plugin.getPrefix() + ChatColor.AQUA + "Format: {Particle} {R G B}"
            );
            int var9 = var4[1] != null ? Math.min(255, Math.max(0, Integer.parseInt(var4[1]))) : 0;
            int var7 = var4[2] != null ? Math.min(255, Math.max(0, Integer.parseInt(var4[2]))) : 0;
            int var8 = var4[3] != null ? Math.min(255, Math.max(0, Integer.parseInt(var4[3]))) : 0;
            var1.getEditedSection().set("projectile-particles.particle", var5.name());
            var1.getEditedSection().set("projectile-particles.color.red", var9);
            var1.getEditedSection().set("projectile-particles.color.green", var7);
            var1.getEditedSection().set("projectile-particles.color.blue", var8);
            var1.registerTemplateEdition();
            var1.getPlayer()
               .sendMessage(
                  MMOItems.plugin.getPrefix()
                     + "Particle successfully set to "
                     + UtilityMethods.caseOnWords(var5.name().toLowerCase().replace("_", " "))
                     + " with RGB color "
                     + var9
                     + " "
                     + var7
                     + " "
                     + var8
               );
         }
      } else {
         Validate.isTrue(var4.length == 1, "That particle cannot be assigned a color");
         var1.getEditedSection().set("projectile-particles.particle", var5.name());
         var1.getEditedSection().set("projectile-particles.color.red", 0);
         var1.getEditedSection().set("projectile-particles.color.green", 0);
         var1.getEditedSection().set("projectile-particles.color.blue", 0);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix() + "Particle successfully set to " + UtilityMethods.caseOnWords(var5.name().toLowerCase().replace("_", " "))
            );
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull ProjectileParticlesData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ProjectileParticlesData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.toString()));
      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      ProjectileParticlesData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   public ProjectileParticlesData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            JsonObject var3 = new JsonParser().parse((String)var2.getValue()).getAsJsonObject();
            Particle var4 = Particle.valueOf(var3.get("Particle").getAsString());
            if (MMOUtils.isColorable(var4)) {
               return new ProjectileParticlesData(var4, var3.get("Red").getAsInt(), var3.get("Green").getAsInt(), var3.get("Blue").getAsInt());
            }

            return new ProjectileParticlesData(var4);
         } catch (JsonSyntaxException var5) {
            var5.printStackTrace();
         }
      }

      return null;
   }
}
