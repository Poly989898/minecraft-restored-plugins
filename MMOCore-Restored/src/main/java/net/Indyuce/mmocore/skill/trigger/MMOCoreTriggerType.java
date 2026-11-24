package net.Indyuce.mmocore.skill.trigger;

import io.lumine.mythic.lib.skill.trigger.TriggerType;
import org.jetbrains.annotations.NotNull;

public class MMOCoreTriggerType {
   @NotNull
   public static TriggerType ENTER_COMBAT = new TriggerType("ENTER_COMBAT");
   @NotNull
   public static TriggerType QUIT_COMBAT = new TriggerType("QUIT_COMBAT");

   public static void registerAll() {
      TriggerType.register(ENTER_COMBAT);
      TriggerType.register(QUIT_COMBAT);
   }
}
