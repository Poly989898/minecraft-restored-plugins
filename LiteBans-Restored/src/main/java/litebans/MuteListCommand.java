package litebans;

import org.jetbrains.annotations.NotNull;

public final class MuteListCommand extends BanListCommand {
   public MuteListCommand(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public PunishmentType obtainPunishmentType() {
      return PunishmentType.MUTE;
   }

   @Override
   public String getName() {
      return "mutelist";
   }
}
