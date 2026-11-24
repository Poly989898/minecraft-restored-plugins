package me.TechsCode.UltraPermissions.base.views;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class TimePickerView extends GUI {
   private static final Phrase REMOVE_SECOND_ = Phrase.create("timePickerView.removeSecond", "Remove 1 Second");
   private static final Phrase REMOVE_MINUTE_ = Phrase.create("timePickerView.removeMinute", "Remove 1 Minute");
   private static final Phrase REMOVE_HOUR_ = Phrase.create("timePickerView.removeHour", "Remove 1 Hour");
   private static final Phrase REMOVE_DAY_ = Phrase.create("timePickerView.removeDay", "Remove 1 Day");
   private static final Phrase ADD_SECOND_ = Phrase.create("timePickerView.addSecond", "Add 1 Second");
   private static final Phrase ADD_MINUTE_ = Phrase.create("timePickerView.addMinute", "Add 1 Minute");
   private static final Phrase ADD_HOUR_ = Phrase.create("timePickerView.addHour", "Add 1 Hour");
   private static final Phrase ADD_DAY_ = Phrase.create("timePickerView.addDay", "Add 1 Day");
   private static final Phrase INCREMENT_ACTION = Phrase.create(
      "timePickerView.incrementAction", "**Click** to add **1**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase DECREMENT_ACTION = Phrase.create(
      "timePickerView.decrementAction", "**Click** to remove **1**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase INCREMENT_ACTION_ALT = Phrase.create(
      "timePickerView.incrementByAction", "**Shift + Click** to add **%number%**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase DECREMENT_ACTION_ALT = Phrase.create(
      "timePickerView.decrementByAction", "**Shift + Click** to remove **%number%**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase CONFIRM_TITLE = Phrase.create("timePickerView.confirm.title", "Confirm");
   private static final Phrase CONFIRM_ACTION = Phrase.create("timePickerView.confirm.action", "Click to confirm", Colors.GRAY);
   private static final Phrase MIN_TIME_TITLE = Phrase.create("timePickerView.minTime.title", "Minimum Duration Required");
   private static final Phrase MIN_TIME_LORE = Phrase.create("timePickerView.minTime.lore", "Minimum duration is set to **%time%**", Colors.GRAY, Colors.YELLOW);
   private final Player p;
   private final SpigotTechPlugin plugin;
   private long time = 0L;
   private final String titlePrefix;
   private final String noTimePhrase;
   private final boolean noTimeContinue;
   private long minTime;
   private static final Phrase TYPE_IN_NAME = Phrase.create("timePickerView.typeIn.name", "Type in duration");
   private static final Phrase TYPE_IN_LORE = Phrase.create("timePickerView.typeIn.lore", "Use a custom duration", Colors.GRAY);
   private static final Phrase TYPE_IN_USER_INPUT_MAIN_TITLE = Phrase.create("timePickerView.typeIn.userInput.mainTitle", "Custom Duration", Colors.AQUA);
   private static final Phrase TYPE_IN_USER_INPUT_SUB_TITLE = Phrase.create("timePickerView.typeIn.userInput.subTitle", "Enter a duration", Colors.GRAY);
   private static final Phrase TYPE_IN_USER_INPUT_FORMAT = Phrase.create(
      "timePickerView.typeIn.userInput.format",
      "Format: **5m** (5 Minutes), **5h** (5 Hours), **5d** (5 Days)",
      Colors.GRAY,
      Colors.GOLD,
      Colors.GOLD,
      Colors.GOLD
   );

   public TimePickerView(Player player, SpigotTechPlugin spigotTechPlugin, String text, String noTimePhrase, boolean flag, long minTime) {
      super(player, spigotTechPlugin);
      this.p = player;
      this.plugin = spigotTechPlugin;
      this.titlePrefix = text;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = flag;
      this.minTime = minTime;
   }

   public TimePickerView(Player player, SpigotTechPlugin spigotTechPlugin, String text, String noTimePhrase, boolean flag, long time, long minTime) {
      super(player, spigotTechPlugin);
      this.p = player;
      this.plugin = spigotTechPlugin;
      this.titlePrefix = text;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = flag;
      this.time = time;
      this.minTime = minTime;
   }

   @Override
   protected void construct(Model model) {
      model.setTitle(this.titlePrefix + " " + (this.time == 0L ? this.noTimePhrase : Tools.getTimeString(this.time)));
      model.setSlots(54);

      for (TimePickerView.Step step : TimePickerView.Step.values()) {
         if (this.time + step.baseIncrement >= 0L) {
            boolean flag = step.baseIncrement > 0L;
            model.button(step.slot, flag ? button -> this.IncrementButton(button, step) : button -> this.DecrementButton(button, step));
         }
      }

      model.button(41, this::TypeInButton);
      if ((this.minTime <= 0L || this.time < this.minTime) && this.minTime != 0L) {
         model.button(23, this::MinTimeButton);
      } else if (this.time != 0L || this.noTimeContinue) {
         model.button(23, this::ConfirmButton);
      }
   }

   private void IncrementButton(Button button, TimePickerView.Step step) {
      button.material(XMaterial.STONE_BUTTON)
         .name(Animation.wave(step.phrase.get(), Colors.GREEN, Colors.WHITE))
         .lore(INCREMENT_ACTION.get(), INCREMENT_ACTION_ALT.get().replace("%number%", String.valueOf(step.secondaryIncrementMultiplier)));
      button.action(actionType -> {
         if (!actionType.equals(ActionType.SHIFT_LEFT) && !actionType.equals(ActionType.SHIFT_RIGHT)) {
            this.time = this.time + step.baseIncrement;
         } else {
            this.time = this.time + step.secondaryIncrement;
         }
      });
   }

   private void DecrementButton(Button button, TimePickerView.Step step) {
      button.material(XMaterial.OAK_BUTTON)
         .name(Animation.wave(step.phrase.get(), Colors.RED, Colors.WHITE))
         .lore(DECREMENT_ACTION.get(), DECREMENT_ACTION_ALT.get().replace("%number%", String.valueOf(step.secondaryIncrementMultiplier)));
      button.action(actionType -> {
         if (!actionType.equals(ActionType.SHIFT_LEFT) && !actionType.equals(ActionType.SHIFT_RIGHT)) {
            this.time = this.time + step.baseIncrement;
         } else if (this.time < Math.abs(step.secondaryIncrement)) {
            this.time = 0L;
         } else {
            this.time = this.time + step.secondaryIncrement;
         }
      });
   }

   private void ConfirmButton(Button button) {
      button.material(XMaterial.EMERALD_BLOCK).name(Animation.wave(CONFIRM_TITLE.get(), Colors.GREEN, Colors.WHITE)).lore(CONFIRM_ACTION.get());
      button.action(actionType -> this.onResult(this.time));
   }

   private void MinTimeButton(Button button) {
      button.material(XMaterial.REDSTONE_BLOCK)
         .name(Animation.wave(MIN_TIME_TITLE.get(), Colors.RED, Colors.WHITE))
         .lore(MIN_TIME_LORE.get().replace("%time%", Tools.getTimeString(this.minTime)));
   }

   private void TypeInButton(Button button) {
      button.material(XMaterial.WRITABLE_BOOK).name(Animation.wave(TYPE_IN_NAME.get(), Colors.GREEN, Colors.WHITE)).lore(TYPE_IN_LORE.get());
      button.action(actionType -> {
         this.p.sendMessage(this.plugin.getPrefix() + TYPE_IN_USER_INPUT_FORMAT.get());
         new UserInput(this.p, this.plugin, "\u00a7l" + TYPE_IN_USER_INPUT_MAIN_TITLE.get(), TYPE_IN_USER_INPUT_SUB_TITLE.get()) {
            @Override
            public void onClose(Player viewer) {
               TimePickerView.this.reopen();
            }

            @Override
            public boolean onResult(String text) {
               TimePickerView.this.time = Tools.getTimeSecondsFromString(text);
               TimePickerView.this.reopen();
               return true;
            }
         };
      });
   }

   public abstract void onResult(long value);

   public enum Step {
      REMOVE_SECOND(TimePickerView.REMOVE_SECOND_, -1L, -1L, 10L, 11),
      REMOVE_MINUTE(TimePickerView.REMOVE_MINUTE_, -60L, -60L, 10L, 20),
      REMOVE_HOUR(TimePickerView.REMOVE_HOUR_, -3600L, -3600L, 10L, 29),
      REMOVE_DAY(TimePickerView.REMOVE_DAY_, -86400L, -86400L, 10L, 38),
      ADD_SECOND(TimePickerView.ADD_SECOND_, 1L, 1L, 10L, 17),
      ADD_MINUTE(TimePickerView.ADD_MINUTE_, 60L, 60L, 10L, 26),
      ADD_HOUR(TimePickerView.ADD_HOUR_, 3600L, 3600L, 10L, 35),
      ADD_DAY(TimePickerView.ADD_DAY_, 86400L, 86400L, 10L, 44);

      Phrase phrase;
      long baseIncrement;
      long secondaryIncrement;
      long secondaryIncrementMultiplier;
      int slot;

      Step(Phrase phrase, long baseIncrement, long secondaryUnit, long secondaryMultiplier, int count) {
         this.phrase = phrase;
         this.baseIncrement = baseIncrement;
         this.secondaryIncrement = secondaryUnit * secondaryMultiplier;
         this.secondaryIncrementMultiplier = secondaryMultiplier;
         this.slot = count;
      }
   }
}
