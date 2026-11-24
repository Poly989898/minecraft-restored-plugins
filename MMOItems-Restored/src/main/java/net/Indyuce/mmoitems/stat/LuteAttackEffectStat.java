package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Objects;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute.BruteLuteAttack;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute.CircularLuteAttack;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute.LuteAttackHandler;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute.SimpleLuteAttack;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute.SlashLuteAttack;
import net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute.WaveLuteAttack;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class LuteAttackEffectStat extends StringStat implements GemStoneStat {
   public LuteAttackEffectStat() {
      super(
         "LUTE_ATTACK_EFFECT",
         Material.DIAMOND_HORSE_ARMOR,
         "Lute Attack Effect",
         new String[]{"Changes how your lute behaves", "when right clicked.", "&9Tip: /mi list lute"},
         new String[]{"lute"}
      );
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         var1.getEditedSection().set("lute-attack-effect", null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed the lute attack effect.");
      } else {
         new StatEdition(var1, this).enable("Write in the chat the text you want.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      LuteAttackEffectStat.LuteAttackEffect var4 = LuteAttackEffectStat.LuteAttackEffect.valueOf(var2.toUpperCase().replace(" ", "_").replace("-", "_"));
      var1.getEditedSection().set("lute-attack-effect", var4.name());
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Lute attack effect successfully changed to " + var4.getName() + ".");
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      LuteAttackEffectStat.LuteAttackEffect var3 = LuteAttackEffectStat.LuteAttackEffect.valueOf(
         var2.toString().toUpperCase().replace(" ", "_").replace("-", "_")
      );
      var1.addItemTag(new ItemTag("MMOITEMS_LUTE_ATTACK_EFFECT", var3.name()));
      var1.getLore().insert("lute-attack-effect", var3.getName());
   }

   public enum LuteAttackEffect {
      WAVE(new WaveLuteAttack()),
      CIRCULAR(new CircularLuteAttack()),
      SIMPLE(new SimpleLuteAttack()),
      BRUTE(new BruteLuteAttack()),
      SLASH(new SlashLuteAttack());

      private final LuteAttackHandler handler;
      @NotNull
      private String name = UtilityMethods.caseOnWords(this.name().toLowerCase().replace("_", " "));

      LuteAttackEffect(LuteAttackHandler nullxx) {
         this.handler = nullxx;
      }

      public LuteAttackHandler getAttack() {
         return this.handler;
      }

      @Deprecated
      public String getDefaultName() {
         return this.name().charAt(0) + this.name().substring(1).toLowerCase();
      }

      public String getName() {
         return this.name;
      }

      public void setName(@NotNull String var1) {
         this.name = Objects.requireNonNull(var1);
      }

      public static LuteAttackEffectStat.LuteAttackEffect get(NBTItem var0) {
         try {
            return valueOf(var0.getString("MMOITEMS_LUTE_ATTACK_EFFECT"));
         } catch (IllegalArgumentException var2) {
            return null;
         }
      }
   }
}
