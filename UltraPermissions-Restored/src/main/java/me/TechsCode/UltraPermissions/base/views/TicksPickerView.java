package me.TechsCode.UltraPermissions.base.views;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class TicksPickerView extends GUI {
   private static final Phrase REMOVE_OneTick_ = Phrase.create("ticksPickerView.removeOneTick", "Remove 1 Tick");
   private static final Phrase REMOVE_FiveTicks_ = Phrase.create("ticksPickerView.removeFiveTicks", "Remove 5 Ticks");
   private static final Phrase REMOVE_TenTicks_ = Phrase.create("ticksPickerView.removeTenTicks", "Remove 10 Ticks");
   private static final Phrase REMOVE_HundredTicks_ = Phrase.create("ticksPickerView.removeHundredTicks", "Remove 100 Ticks");
   private static final Phrase ADD_OneTick_ = Phrase.create("ticksPickerView.addOneTick", "Add 1 Tick");
   private static final Phrase ADD_FiveTicks_ = Phrase.create("ticksPickerView.addFiveTicks", "Add 5 Ticks");
   private static final Phrase ADD_TenTicks_ = Phrase.create("ticksPickerView.addTenTicks", "Add 10 Ticks");
   private static final Phrase ADD_HundredTicks_ = Phrase.create("ticksPickerView.addHundredTicks", "Add 100 Ticks");
   private static final Phrase INCREMENT_ACTION = Phrase.create("ticksPickerView.incrementAction", "Click to add", Colors.GRAY);
   private static final Phrase DECREMENT_ACTION = Phrase.create("ticksPickerView.decrementAction", "Click to remove", Colors.GRAY);
   private static final Phrase CONFIRM_TITLE = Phrase.create("ticksPickerView.confirm.title", "Confirm");
   private static final Phrase CONFIRM_ACTION = Phrase.create("ticksPickerView.confirm.action", "Click to confirm", Colors.GRAY);
   private long ticks = 0L;
   private final String titlePrefix;
   private final String noTicksPhrase;
   private final boolean noTicksContinue;

   public TicksPickerView(Player player, SpigotTechPlugin spigotTechPlugin, String text, String noTicksPhrase, boolean flag) {
      super(player, spigotTechPlugin);
      this.titlePrefix = text;
      this.noTicksPhrase = noTicksPhrase;
      this.noTicksContinue = flag;
   }

   @Override
   protected void construct(Model model) {
      model.setTitle(this.titlePrefix + " " + (this.ticks == 0L ? this.noTicksPhrase : Tools.getTicksString(this.ticks)));
      model.setSlots(54);

      for (TicksPickerView.Step step : TicksPickerView.Step.values()) {
         if (this.ticks + step.baseIncrement >= 0L) {
            boolean flag = step.baseIncrement > 0L;
            model.button(step.slot, flag ? button -> this.IncrementButton(button, step) : button -> this.DecrementButton(button, step));
         }
      }

      if (this.ticks != 0L || this.noTicksContinue) {
         model.button(23, this::ConfirmButton);
      }
   }

   private void IncrementButton(Button button, TicksPickerView.Step step) {
      button.material(XMaterial.STONE_BUTTON).name(Animation.wave(step.phrase.get(), Colors.GREEN, Colors.WHITE)).lore(INCREMENT_ACTION.get());
      button.action(actionType -> this.ticks = this.ticks + step.baseIncrement);
   }

   private void DecrementButton(Button button, TicksPickerView.Step step) {
      button.material(XMaterial.OAK_BUTTON).name(Animation.wave(step.phrase.get(), Colors.RED, Colors.WHITE)).lore(DECREMENT_ACTION.get());
      button.action(actionType -> this.ticks = this.ticks + step.baseIncrement);
   }

   private void ConfirmButton(Button button) {
      button.material(XMaterial.EMERALD_BLOCK).name(Animation.wave(CONFIRM_TITLE.get(), Colors.GREEN, Colors.WHITE)).lore(CONFIRM_ACTION.get());
      button.action(actionType -> this.onResult(this.ticks));
   }

   public abstract void onResult(long value);

   public enum Step {
      REMOVE_OneTick(TicksPickerView.REMOVE_OneTick_, -1L, 11),
      REMOVE_FiveTicks(TicksPickerView.REMOVE_FiveTicks_, -5L, 20),
      REMOVE_TenTicks(TicksPickerView.REMOVE_TenTicks_, -10L, 29),
      REMOVE_HundredTicks(TicksPickerView.REMOVE_HundredTicks_, -100L, 38),
      ADD_OneTick(TicksPickerView.ADD_OneTick_, 1L, 17),
      ADD_FiveTicks(TicksPickerView.ADD_FiveTicks_, 5L, 26),
      ADD_TenTicks(TicksPickerView.ADD_TenTicks_, 10L, 35),
      ADD_HundredTicks(TicksPickerView.ADD_HundredTicks_, 100L, 44);

      Phrase phrase;
      long baseIncrement;
      int slot;

      Step(Phrase phrase, long value, int count) {
         this.phrase = phrase;
         this.baseIncrement = value;
         this.slot = count;
      }
   }
}
