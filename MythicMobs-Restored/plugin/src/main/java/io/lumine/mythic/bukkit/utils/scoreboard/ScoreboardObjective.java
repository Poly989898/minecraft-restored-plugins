package io.lumine.mythic.bukkit.utils.scoreboard;

import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

@NonnullByDefault
public interface ScoreboardObjective {
   String getId();

   boolean shouldAutoSubscribe();

   void setAutoSubscribe(boolean flag);

   String getDisplayName();

   void setDisplayName(String str);

   DisplaySlot getDisplaySlot();

   void setDisplaySlot(DisplaySlot displaySlot);

   Map<String, Integer> getScores();

   boolean hasScore(String str);

   @Nullable
   Integer getScore(String str);

   void setScore(String str, int value);

   boolean removeScore(String str);

   void clearScores();

   void applyScores(Map<String, Integer> map);

   void applyLines(String... strs);

   void applyLines(Collection<String> strs);

   void subscribe(Player player);

   void unsubscribe(Player player);

   void unsubscribe(Player player, boolean flag);

   void unsubscribeAll();
}
