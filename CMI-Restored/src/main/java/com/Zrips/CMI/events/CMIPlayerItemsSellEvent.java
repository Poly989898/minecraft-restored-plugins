package com.Zrips.CMI.events;

import com.Zrips.CMI.Modules.Worth.WorthManager;
import java.util.HashMap;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.entity.Player;

public class CMIPlayerItemsSellEvent extends CMIPlayerEvent {
  private HashMap<CMIMaterial, Integer> amounts = new HashMap<>();
  private HashMap<CMIMaterial, Double> payments = new HashMap<>();
  private WorthManager.worthType sellType = WorthManager.worthType.all;
  private double totalPayment = 0.0;
  private int totalAmount = 0;

  public CMIPlayerItemsSellEvent(
      Player player,
      WorthManager.worthType worthType,
      int value,
      double number,
      HashMap<CMIMaterial, Integer> hashMap,
      HashMap<CMIMaterial, Double> paymentMap) {
    super(player);
    this.sellType = worthType;
    this.totalAmount = value;
    this.totalPayment = number;
    this.amounts = hashMap;
    this.payments = paymentMap;
  }

  public WorthManager.worthType getSellType() {
    return this.sellType;
  }

  public double getTotalPayment() {
    return this.totalPayment;
  }

  public int getTotalAmount() {
    return this.totalAmount;
  }

  public HashMap<CMIMaterial, Integer> getAmounts() {
    return this.amounts;
  }

  public HashMap<CMIMaterial, Double> getPayments() {
    return this.payments;
  }

  public void setTotalPayment(double number) {
    this.totalPayment = number;
  }
}
