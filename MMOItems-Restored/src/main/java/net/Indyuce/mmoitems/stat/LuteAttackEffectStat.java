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
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         editionInventory.getEditedSection().set("lute-attack-effect", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed the lute attack effect.");
      } else {
         new StatEdition(editionInventory, this).enable("Write in the chat the text you want.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      LuteAttackEffectStat.LuteAttackEffect luteAttackEffect = LuteAttackEffectStat.LuteAttackEffect.valueOf(text.toUpperCase().replace(" ", "_").replace("-", "_"));
      editionInventory.getEditedSection().set("lute-attack-effect", luteAttackEffect.name());
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Lute attack effect successfully changed to " + luteAttackEffect.getName() + ".");
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      LuteAttackEffectStat.LuteAttackEffect luteAttackEffect = LuteAttackEffectStat.LuteAttackEffect.valueOf(
         stringData.toString().toUpperCase().replace(" ", "_").replace("-", "_")
      );
      itemStackBuilder.addItemTag(new ItemTag("MMOITEMS_LUTE_ATTACK_EFFECT", luteAttackEffect.name()));
      itemStackBuilder.getLore().insert("lute-attack-effect", luteAttackEffect.getName());
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

      public void setName(@NotNull String text) {
         this.name = Objects.requireNonNull(text);
      }

      public static LuteAttackEffectStat.LuteAttackEffect get(NBTItem nbtItem) {
         try {
            return valueOf(nbtItem.getString("MMOITEMS_LUTE_ATTACK_EFFECT"));
         } catch (IllegalArgumentException exception) {
            return null;
         }
      }
   }
}
