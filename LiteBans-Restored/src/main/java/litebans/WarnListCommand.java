package litebans;

import org.jetbrains.annotations.NotNull;

public final class WarnListCommand extends BanListCommand {
   public WarnListCommand(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public PunishmentType obtainPunishmentType() {
      return PunishmentType.WARN;
   }

   @Override
   public String getName() {
      return "warnlist";
   }
}
