package net.Indyuce.mmocore.api.player;

import java.util.UUID;
import org.bukkit.OfflinePlayer;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes;
import net.Indyuce.mmocore.api.player.profess.PlayerClass;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.PlayerProfessions;

public abstract class PlayerData extends SynchronizedDataHolder {
    protected PlayerData(MMOPlayerData data) { super(data); }
    public static PlayerData get(OfflinePlayer player) { return null; }
    public static PlayerData get(UUID id) { return null; }
    public PlayerProfessions getCollectionSkills() { return null; }
    public void setLastActivity(PlayerActivity activity) { }
    public boolean isOnline() { return false; }
    public int getLevel() { return 0; }
    public void giveExperience(double value, EXPSource source) { }
    public PlayerClass getProfess() { return null; }
    public void giveMana(double amount, PlayerResourceUpdateEvent.UpdateReason reason) { }
    public void giveStamina(double amount, PlayerResourceUpdateEvent.UpdateReason reason) { }
    public void giveStellium(double amount) { }
    public double getStellium() { return 0; }
    public PlayerAttributes getAttributes() { return null; }
    public double getMana() { return 0; }
    public double getStamina() { return 0; }
    public void setMana(double amount) { }
    public void setStamina(double amount) { }
}
