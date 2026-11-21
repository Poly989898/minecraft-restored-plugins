package litebans;

import org.jetbrains.annotations.NotNull;

public final class CheckMuteCommand extends CheckBanCommand {
   public CheckMuteCommand(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public PunishmentType obtainPunishmentType2() {
      return PunishmentType.MUTE;
   }

   @Override
   public Message obtainMessage3() {
      return Message.CHECKMUTE_MUTED;
   }

   @Override
   public Message obtainMessage4() {
      return Message.NO_MUTE;
   }

   @Override
   public String getName() {
      return "checkmute";
   }
}
