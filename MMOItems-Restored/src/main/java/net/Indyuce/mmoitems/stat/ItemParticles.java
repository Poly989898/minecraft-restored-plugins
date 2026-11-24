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
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         var1.getEditedSection().set(this.getPath(), null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ChatColor.GRAY + ".");
      } else {
         new ParticlesEdition(var1.getPlayer(), var1.getEdited()).open(var1);
      }
   }

   public ParticleData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      return new ParticleData((ConfigurationSection)var1);
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull ParticleData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ParticleData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.toJson().toString()));
      return var2;
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<ParticleData> var2) {
      var1.add(ChatColor.YELLOW + "►" + " Left click to setup the item particles.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to clear.");
   }

   @NotNull
   public ParticleData getClearStatData() {
      return new ParticleData(ParticleType.AURA, VParticle.LARGE_EXPLOSION.get());
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = (String)var3[0];
      String var5 = var2.toUpperCase().replace("-", "_").replace(" ", "_");
      if (var4.equals("particle-type")) {
         ParticleType var12 = ParticleType.valueOf(var5);
         var1.getEditedSection().set("item-particles.type", var12.name());
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Particle type successfully set to " + ChatColor.GOLD + var12.getDefaultName() + ChatColor.GRAY + ".");
      } else if (var4.equals("particle-color")) {
         String[] var11 = var2.split(" ");
         int var7 = Integer.parseInt(var11[0]);
         int var8 = Integer.parseInt(var11[1]);
         int var9 = Integer.parseInt(var11[2]);
         var1.getEditedSection().set("item-particles.color.red", var7);
         var1.getEditedSection().set("item-particles.color.green", var8);
         var1.getEditedSection().set("item-particles.color.blue", var9);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Particle color successfully set to "
                  + ChatColor.RED
                  + ChatColor.BOLD
                  + var7
                  + ChatColor.GRAY
                  + " - "
                  + ChatColor.GREEN
                  + ChatColor.BOLD
                  + var8
                  + ChatColor.GRAY
                  + " - "
                  + ChatColor.BLUE
                  + ChatColor.BOLD
                  + var9
                  + ChatColor.GRAY
                  + "."
            );
      } else if (var4.equals("particle")) {
         Particle var10 = Particle.valueOf(var5);
         var1.getEditedSection().set("item-particles.particle", var10.name());
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
      } else {
         double var6 = MMOUtils.parseDouble(var2);
         var1.getEditedSection().set("item-particles." + var4, var6);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + ChatColor.GOLD
                  + UtilityMethods.caseOnWords(var4.replace("-", " "))
                  + ChatColor.GRAY
                  + " set to "
                  + ChatColor.GOLD
                  + var6
                  + ChatColor.GRAY
                  + "."
            );
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      ParticleData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public ParticleData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            return new ParticleData(new JsonParser().parse((String)var2.getValue()).getAsJsonObject());
         } catch (JsonSyntaxException | IllegalStateException var4) {
         }
      }

      return null;
   }
}
