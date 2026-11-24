package net.Indyuce.mmoitems.api.interaction.weapon.untargeted.lute;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.player.PlayerMetadata;
import java.util.Random;
import net.Indyuce.mmoitems.api.util.SoundReader;
import net.Indyuce.mmoitems.stat.data.ProjectileParticlesData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@Deprecated
public interface LuteAttackHandler {
   Random RANDOM = new Random();

   void handle(
      PlayerMetadata var1, double var2, NBTItem var4, double var5, @NotNull Vector var7, @NotNull SoundReader var8, @NotNull ProjectileParticlesData var9
   );
}
