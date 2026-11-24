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

   public ArrowParticlesData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a valid config section");
      ConfigurationSection var2 = (ConfigurationSection)var1;
      Validate.isTrue(var2.contains("particle"), "Could not find arrow particle");
      Particle var3 = Particle.valueOf(var2.getString("particle").toUpperCase().replace("-", "_").replace(" ", "_"));
      int var4 = var2.getInt("amount");
      double var5 = var2.getDouble("offset");
      return MMOUtils.isColorable(var3)
         ? new ArrowParticlesData(var3, var4, var5, var2.getInt("color.red"), var2.getInt("color.green"), var2.getInt("color.blue"))
         : new ArrowParticlesData(var3, var4, var5, var2.getDouble("speed"));
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull ArrowParticlesData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ArrowParticlesData var1) {
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

      ArrowParticlesData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public ArrowParticlesData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            JsonObject var3 = new JsonParser().parse((String)var2.getValue()).getAsJsonObject();
            Particle var4 = Particle.valueOf(var3.get("Particle").getAsString());
            int var5 = var3.get("Amount").getAsInt();
            double var6 = var3.get("Offset").getAsDouble();
            if (MMOUtils.isColorable(var4)) {
               return new ArrowParticlesData(var4, var5, var6, var3.get("Red").getAsInt(), var3.get("Green").getAsInt(), var3.get("Blue").getAsInt());
            }

            return new ArrowParticlesData(var4, var5, var6, var3.get("Speed").getAsDouble());
         } catch (JsonSyntaxException | IllegalStateException var8) {
         }
      }

      return null;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      new ArrowParticlesEdition(var1.getPlayer(), var1.getEdited()).open(var1);
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = (String)var3[0];
      if (var4.equals("color")) {
         String[] var11 = var2.split(" ");
         int var6 = Integer.parseInt(var11[0]);
         int var7 = Integer.parseInt(var11[1]);
         int var8 = Integer.parseInt(var11[2]);
         var1.getEditedSection().set("arrow-particles.color.red", var6);
         var1.getEditedSection().set("arrow-particles.color.green", var7);
         var1.getEditedSection().set("arrow-particles.color.blue", var8);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Particle color successfully set to "
                  + ChatColor.translateAlternateColorCodes('&', "&c&l" + var6 + "&7 - &a&l" + var7 + "&7 - &9&l" + var8)
            );
      } else if (var4.equals("particle")) {
         Particle var10 = Particle.valueOf(var2.toUpperCase().replace("-", "_").replace(" ", "_"));
         var1.getEditedSection().set("arrow-particles.particle", var10.name());
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Particle successfully set to "
                  + ChatColor.GOLD
                  + UtilityMethods.caseOnWords(var10.name().toLowerCase().replace("_", " "))
                  + ChatColor.GRAY
                  + "."
            );
      } else if (var4.equals("amount")) {
         int var9 = Integer.parseInt(var2);
         var1.getEditedSection().set("arrow-particles.amount", var9);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GOLD + "Amount" + ChatColor.GRAY + " set to " + ChatColor.GOLD + var9 + ChatColor.GRAY + ".");
      } else {
         double var5 = MMOUtils.parseDouble(var2);
         var1.getEditedSection().set("arrow-particles." + var4, var5);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + ChatColor.GOLD
                  + UtilityMethods.caseOnWords(var4.replace("-", " "))
                  + ChatColor.GRAY
                  + " set to "
                  + ChatColor.GOLD
                  + var5
                  + ChatColor.GRAY
                  + "."
            );
      }
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<ArrowParticlesData> var2) {
      if (var2.isPresent()) {
         ArrowParticlesData var3 = (ArrowParticlesData)var2.get();
         var1.add(ChatColor.GRAY + "Current Value:");
         var1.add(ChatColor.GRAY + "* Particle: " + ChatColor.GOLD + UtilityMethods.caseOnWords(var3.getParticle().name().replace("_", " ").toLowerCase()));
         var1.add(ChatColor.GRAY + "* Amount: " + ChatColor.WHITE + var3.getAmount());
         var1.add(ChatColor.GRAY + "* Offset: " + ChatColor.WHITE + var3.getOffset());
         var1.add("");
         if (MMOUtils.isColorable(var3.getParticle())) {
            var1.add(
               ChatColor.translateAlternateColorCodes('&', "&7* Color: &c&l" + var3.getRed() + "&7 - &a&l" + var3.getGreen() + "&7 - &9&l" + var3.getBlue())
            );
         } else {
            var1.add(ChatColor.GRAY + "* Speed: " + ChatColor.WHITE + var3.getSpeed());
         }
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to edit.");
   }

   @NotNull
   public ArrowParticlesData getClearStatData() {
      return new ArrowParticlesData(VParticle.LARGE_EXPLOSION.get(), 1, 0.0, 1.0);
   }
}
