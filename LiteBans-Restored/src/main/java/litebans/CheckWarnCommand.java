package litebans;

import org.jetbrains.annotations.NotNull;

public final class CheckWarnCommand extends CheckBanCommand {
   public CheckWarnCommand(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public PunishmentType obtainPunishmentType2() {
      return PunishmentType.WARN;
   }

   @Override
   public Message obtainMessage3() {
      return Message.WARNED;
   }

   @Override
   public Message obtainMessage4() {
      return Message.NO_WARN;
   }

   @Override
   public String getName() {
      return "checkwarn";
   }
}
