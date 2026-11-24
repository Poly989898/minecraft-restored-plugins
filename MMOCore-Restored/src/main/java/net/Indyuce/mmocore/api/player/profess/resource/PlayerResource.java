package net.Indyuce.mmocore.api.player.profess.resource;

import io.lumine.mythic.lib.version.Attributes;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.ClassOption;
import net.Indyuce.mmocore.api.quest.trigger.ManaTrigger;

public enum PlayerResource {
   HEALTH(
      data -> data.getPlayer().getHealth(),
      data -> data.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue(),
      (data, amount) -> data.heal(amount, PlayerResourceUpdateEvent.UpdateReason.REGENERATION),
      (data, amount) -> data.heal(amount, PlayerResourceUpdateEvent.UpdateReason.COMMAND),
      (data, amount) -> data.heal(-amount, PlayerResourceUpdateEvent.UpdateReason.COMMAND),
      (data, amount) -> data.getPlayer().setHealth(amount)
   ),
   MANA(
      PlayerData::getMana,
      data -> data.getStats().getStat("MAX_MANA"),
      (data, amount) -> data.giveMana(amount, PlayerResourceUpdateEvent.UpdateReason.REGENERATION),
      (data, amount) -> data.giveMana(amount, PlayerResourceUpdateEvent.UpdateReason.COMMAND),
      (data, amount) -> data.giveMana(-amount, PlayerResourceUpdateEvent.UpdateReason.COMMAND),
      (data, amount) -> data.setMana(amount)
   ),
   STAMINA(
      PlayerData::getStamina,
      data -> data.getStats().getStat("MAX_STAMINA"),
      (data, amount) -> data.giveStamina(amount, PlayerResourceUpdateEvent.UpdateReason.REGENERATION),
      (data, amount) -> data.giveStamina(amount, PlayerResourceUpdateEvent.UpdateReason.COMMAND),
      (data, amount) -> data.giveStamina(-amount, PlayerResourceUpdateEvent.UpdateReason.COMMAND),
      (data, amount) -> data.setStamina(amount)
   ),
   STELLIUM(
      PlayerData::getStellium,
      data -> data.getStats().getStat("MAX_STELLIUM"),
      (data, amount) -> data.giveStellium(amount, PlayerResourceUpdateEvent.UpdateReason.REGENERATION),
      (data, amount) -> data.giveStellium(amount, PlayerResourceUpdateEvent.UpdateReason.COMMAND),
      (data, amount) -> data.giveStellium(-amount, PlayerResourceUpdateEvent.UpdateReason.COMMAND),
      (data, amount) -> data.setStellium(amount)
   );

   private final String regenStat = this.name() + "_REGENERATION";
   private final String maxRegenStat = "MAX_" + this.name() + "_REGENERATION";
   private final ClassOption offCombatRegen = ClassOption.valueOf("OFF_COMBAT_" + this.name() + "_REGEN");
   private final Function<PlayerData, Double> current;
   private final Function<PlayerData, Double> max;
   private final BiConsumer<PlayerData, Double> regen;
   private final BiConsumer<PlayerData, Double> set;
   private final BiConsumer<PlayerData, Double> give;
   private final BiConsumer<PlayerData, Double> take;

   PlayerResource(
      Function<PlayerData, Double> current,
      Function<PlayerData, Double> max,
      BiConsumer<PlayerData, Double> regen,
      BiConsumer<PlayerData, Double> give,
      BiConsumer<PlayerData, Double> take,
      BiConsumer<PlayerData, Double> set
   ) {
      this.current = current;
      this.max = max;
      this.regen = regen;
      this.give = give;
      this.take = take;
      this.set = set;
   }

   public String getRegenStat() {
      return this.regenStat;
   }

   public String getMaxRegenStat() {
      return this.maxRegenStat;
   }

   public ClassOption getOffCombatRegen() {
      return this.offCombatRegen;
   }

   public double getCurrent(PlayerData player) {
      return this.current.apply(player);
   }

   public double getMax(PlayerData player) {
      return this.max.apply(player);
   }

   public void regen(PlayerData player, double amount) {
      this.regen.accept(player, amount);
   }

   public BiConsumer<PlayerData, Double> getConsumer(ManaTrigger.Operation operation) {
      switch (operation) {
         case SET:
            return this.set;
         case TAKE:
            return this.take;
         case GIVE:
            return this.give;
         default:
            throw new IllegalArgumentException("Operation cannot be null");
      }
   }
}
