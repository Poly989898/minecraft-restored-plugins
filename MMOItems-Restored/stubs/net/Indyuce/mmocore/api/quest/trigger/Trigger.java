package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.player.PlayerData;

public abstract class Trigger {
    public Trigger(MMOLineConfig config) { }
    public abstract void apply(PlayerData player);
}
