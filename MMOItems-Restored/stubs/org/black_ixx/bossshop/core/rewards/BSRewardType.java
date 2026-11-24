package org.black_ixx.bossshop.core.rewards;

import org.bukkit.entity.Player;
import org.black_ixx.bossshop.core.BSBuy;
import org.bukkit.event.inventory.ClickType;

public abstract class BSRewardType {
    public abstract Object createObject(Object o, boolean b);
    public abstract boolean validityCheck(String s, Object o);
    public abstract boolean canBuy(Player p, BSBuy buy, boolean b, Object o, ClickType t);
    public abstract void giveReward(Player p, BSBuy buy, Object o, ClickType t);
    public abstract String getDisplayReward(Player p, BSBuy buy, Object o, ClickType t);
    public abstract String[] createNames();
    public abstract boolean mightNeedShopUpdate();
    public abstract void enableType();
    public void register() { }
}
