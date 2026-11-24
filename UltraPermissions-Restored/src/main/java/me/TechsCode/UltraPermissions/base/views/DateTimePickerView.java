package me.TechsCode.UltraPermissions.base.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class DateTimePickerView extends GUI {
   private static final Phrase REMOVE_SECOND_ = Phrase.create("dateTimePickerView.removeSecond", "Remove 1 Second");
   private static final Phrase REMOVE_MINUTE_ = Phrase.create("dateTimePickerView.removeMinute", "Remove 1 Minute");
   private static final Phrase REMOVE_HOUR_ = Phrase.create("dateTimePickerView.removeHour", "Remove 1 Hour");
   private static final Phrase REMOVE_DAY_ = Phrase.create("dateTimePickerView.removeDay", "Remove 1 Day");
   private static final Phrase ADD_SECOND_ = Phrase.create("dateTimePickerView.addSecond", "Add 1 Second");
   private static final Phrase ADD_MINUTE_ = Phrase.create("dateTimePickerView.addMinute", "Add 1 Minute");
   private static final Phrase ADD_HOUR_ = Phrase.create("dateTimePickerView.addHour", "Add 1 Hour");
   private static final Phrase ADD_DAY_ = Phrase.create("dateTimePickerView.addDay", "Add 1 Day");
   private static final Phrase INCREMENT_ACTION = Phrase.create(
      "dateTimePickerView.incrementAction", "**Click** to add **1**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase DECREMENT_ACTION = Phrase.create(
      "dateTimePickerView.decrementAction", "**Click** to remove **1**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase INCREMENT_ACTION_ALT = Phrase.create(
      "dateTimePickerView.incrementByAction", "**Shift + Click** to add **%number%**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase DECREMENT_ACTION_ALT = Phrase.create(
      "dateTimePickerView.decrementByAction", "**Shift + Click** to remove **%number%**", Colors.GRAY, Colors.AQUA, Colors.WHITE
   );
   private static final Phrase CONFIRM_TITLE = Phrase.create("dateTimePickerView.confirm.title", "Confirm");
   private static final Phrase CONFIRM_ACTION = Phrase.create("dateTimePickerView.confirm.action", "Click to confirm", Colors.GRAY);
   private final Player p;
   private final SpigotTechPlugin plugin;
   private long time = 0L;
   private final String titlePrefix;
   private final String noTimePhrase;
   private final boolean noTimeContinue;
   private String dateTimeFormat;
   private boolean showDayButtons;
   private static final Phrase TYPE_IN_NAME = Phrase.create("dateTimePickerView.typeIn.name", "Type in date");
   private static final Phrase TYPE_IN_LORE = Phrase.create("dateTimePickerView.typeIn.lore", "Use a custom date", Colors.GRAY);
   private static final Phrase TYPE_IN_USER_INPUT_MAIN_TITLE = Phrase.create("dateTimePickerView.typeIn.userInput.mainTitle", "Custom Date", Colors.AQUA);
   private static final Phrase TYPE_IN_USER_INPUT_SUB_TITLE = Phrase.create("dateTimePickerView.typeIn.userInput.subTitle", "Enter a date", Colors.GRAY);
   private static final Phrase TYPE_IN_USER_INPUT_FORMAT = Phrase.create(
      "dateTimePickerView.typeIn.userInput.format", "Format: **%format%**", Colors.GRAY, Colors.GOLD
   );
   private static final Phrase TYPE_IN_USER_INPUT_ERROR = Phrase.create(
      "dateTimePickerView.typeIn.userInput.format.error", "There was an error parsing the date", Colors.RED
   );

   public DateTimePickerView(Player player, SpigotTechPlugin plugin, String titlePrefix, String noTimePhrase, boolean noTimeContinue) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.titlePrefix = titlePrefix;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = noTimeContinue;
      this.dateTimeFormat = "dd-MM-yyyy HH:mm:ss";
      this.showDayButtons = true;
   }

   public DateTimePickerView(Player player, SpigotTechPlugin plugin, String titlePrefix, String noTimePhrase, boolean noTimeContinue, boolean showDayButtons) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.titlePrefix = titlePrefix;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = noTimeContinue;
      this.dateTimeFormat = "dd-MM-yyyy HH:mm:ss";
      this.showDayButtons = showDayButtons;
   }

   public DateTimePickerView(Player player, SpigotTechPlugin plugin, String titlePrefix, String noTimePhrase, boolean noTimeContinue, String dateTimeFormat) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.titlePrefix = titlePrefix;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = noTimeContinue;
      this.dateTimeFormat = dateTimeFormat;
      this.showDayButtons = true;
   }

   public DateTimePickerView(Player player, SpigotTechPlugin plugin, String titlePrefix, String noTimePhrase, boolean noTimeContinue, String dateTimeFormat, boolean showDayButtons) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.titlePrefix = titlePrefix;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = noTimeContinue;
      this.dateTimeFormat = dateTimeFormat;
      this.showDayButtons = showDayButtons;
   }

   public DateTimePickerView(Player player, SpigotTechPlugin plugin, String titlePrefix, String noTimePhrase, boolean noTimeContinue, long time) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.titlePrefix = titlePrefix;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = noTimeContinue;
      this.time = time;
      this.dateTimeFormat = "dd-MM-yyyy HH:mm:ss";
      this.showDayButtons = true;
   }

   public DateTimePickerView(Player player, SpigotTechPlugin plugin, String titlePrefix, String noTimePhrase, boolean noTimeContinue, long time, boolean showDayButtons) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.titlePrefix = titlePrefix;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = noTimeContinue;
      this.time = time;
      this.dateTimeFormat = "dd-MM-yyyy HH:mm:ss";
      this.showDayButtons = showDayButtons;
   }

   public DateTimePickerView(Player player, SpigotTechPlugin plugin, String titlePrefix, String noTimePhrase, boolean noTimeContinue, long time, String dateTimeFormat) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.titlePrefix = titlePrefix;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = noTimeContinue;
      this.time = time;
      this.dateTimeFormat = dateTimeFormat;
      this.showDayButtons = true;
   }

   public DateTimePickerView(Player player, SpigotTechPlugin plugin, String titlePrefix, String noTimePhrase, boolean noTimeContinue, long time, String dateTimeFormat, boolean showDayButtons) {
      super(player, plugin);
      this.p = player;
      this.plugin = plugin;
      this.titlePrefix = titlePrefix;
      this.noTimePhrase = noTimePhrase;
      this.noTimeContinue = noTimeContinue;
      this.time = time;
      this.dateTimeFormat = dateTimeFormat;
      this.showDayButtons = showDayButtons;
   }

   @Override
   protected void construct(Model model) {
      Date date = new Date(this.time);
      SimpleDateFormat format = new SimpleDateFormat(this.dateTimeFormat);
      String formatted = format.format(date);
      model.setTitle(this.titlePrefix + " " + (this.time == 0L ? this.noTimePhrase : formatted));
      model.setSlots(54);

      for (DateTimePickerView.Step step : DateTimePickerView.Step.values()) {
         if (this.time + step.baseIncrement >= 0L && (!step.isDayButton || this.showDayButtons)) {
            boolean isIncrement = step.baseIncrement > 0L;
            model.button(step.slot, isIncrement ? button -> this.IncrementButton(button, step) : button -> this.DecrementButton(button, step));
         }
      }

      model.button(41, this::TypeInButton);
      if (this.time != 0L || this.noTimeContinue) {
         model.button(23, this::ConfirmButton);
      }
   }

   private void IncrementButton(Button button, DateTimePickerView.Step step) {
      button.material(XMaterial.STONE_BUTTON)
         .name(Animation.wave(step.phrase.get(), Colors.GREEN, Colors.WHITE))
         .lore(INCREMENT_ACTION.get(), INCREMENT_ACTION_ALT.get().replace("%number%", String.valueOf(step.secondaryIncrementMultiplier)));
      button.action(actionType -> {
         if (!actionType.equals(ActionType.SHIFT_LEFT) && !actionType.equals(ActionType.SHIFT_RIGHT)) {
            this.time = this.time + step.baseIncrement * 1000L;
         } else {
            this.time = this.time + step.secondaryIncrement * 1000L;
         }
      });
   }

   private void DecrementButton(Button button, DateTimePickerView.Step step) {
      button.material(XMaterial.OAK_BUTTON)
         .name(Animation.wave(step.phrase.get(), Colors.RED, Colors.WHITE))
         .lore(DECREMENT_ACTION.get(), DECREMENT_ACTION_ALT.get().replace("%number%", String.valueOf(step.secondaryIncrementMultiplier)));
      button.action(actionType -> {
         if (!actionType.equals(ActionType.SHIFT_LEFT) && !actionType.equals(ActionType.SHIFT_RIGHT)) {
            this.time = this.time + step.baseIncrement * 1000L;
         } else if (this.time < Math.abs(step.secondaryIncrement * 1000L)) {
            this.time = 0L;
         } else {
            this.time = this.time + step.secondaryIncrement * 1000L;
         }
      });
   }

   private void ConfirmButton(Button button) {
      button.material(XMaterial.EMERALD_BLOCK).name(Animation.wave(CONFIRM_TITLE.get(), Colors.GREEN, Colors.WHITE)).lore(CONFIRM_ACTION.get());
      button.action(actionType -> this.onResult(this.time));
   }

   private void TypeInButton(Button button) {
      button.material(XMaterial.WRITABLE_BOOK).name(Animation.wave(TYPE_IN_NAME.get(), Colors.GREEN, Colors.WHITE)).lore(TYPE_IN_LORE.get());
      button.action(actionType -> {
         this.p.sendMessage(this.plugin.getPrefix() + TYPE_IN_USER_INPUT_FORMAT.get().replace("%format%", this.dateTimeFormat));
         new UserInput(this.p, this.plugin, "\u00a7l" + TYPE_IN_USER_INPUT_MAIN_TITLE.get(), TYPE_IN_USER_INPUT_SUB_TITLE.get()) {
            @Override
            public void onClose(Player closingPlayer) {
               DateTimePickerView.this.reopen();
            }

            @Override
            public boolean onResult(String text) {
               try {
                  DateTimePickerView.this.time = new SimpleDateFormat(DateTimePickerView.this.dateTimeFormat).parse(text).getTime();
                  DateTimePickerView.this.reopen();
                  return true;
               } catch (ParseException exception) {
                  DateTimePickerView.this.p.sendMessage(DateTimePickerView.this.plugin.getPrefix() + DateTimePickerView.TYPE_IN_USER_INPUT_ERROR.get());
                  return false;
               }
            }
         };
      });
   }

   public abstract void onResult(long time);

   public enum Step {
      REMOVE_SECOND(DateTimePickerView.REMOVE_SECOND_, -1L, -1L, 10L, 11, false),
      REMOVE_MINUTE(DateTimePickerView.REMOVE_MINUTE_, -60L, -60L, 10L, 20, false),
      REMOVE_HOUR(DateTimePickerView.REMOVE_HOUR_, -3600L, -3600L, 10L, 29, false),
      REMOVE_DAY(DateTimePickerView.REMOVE_DAY_, -86400L, -86400L, 10L, 38, true),
      ADD_SECOND(DateTimePickerView.ADD_SECOND_, 1L, 1L, 10L, 17, false),
      ADD_MINUTE(DateTimePickerView.ADD_MINUTE_, 60L, 60L, 10L, 26, false),
      ADD_HOUR(DateTimePickerView.ADD_HOUR_, 3600L, 3600L, 10L, 35, false),
      ADD_DAY(DateTimePickerView.ADD_DAY_, 86400L, 86400L, 10L, 44, true);

      Phrase phrase;
      long baseIncrement;
      long secondaryIncrement;
      long secondaryIncrementMultiplier;
      int slot;
      boolean isDayButton;

      Step(Phrase phrase, long baseIncrement, long secondaryIncrement, long secondaryIncrementMultiplier, int slot, boolean isDayButton) {
         this.phrase = phrase;
         this.baseIncrement = baseIncrement;
         this.secondaryIncrement = secondaryIncrement * secondaryIncrementMultiplier;
         this.secondaryIncrementMultiplier = secondaryIncrementMultiplier;
         this.slot = slot;
         this.isDayButton = isDayButton;
      }
   }
}
