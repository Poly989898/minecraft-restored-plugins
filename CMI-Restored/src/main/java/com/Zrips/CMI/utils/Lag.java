package com.Zrips.CMI.utils;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Lag implements Runnable {
  private List<Long> TicksList = new ArrayList<>();

  public int getTicks() {
    return this.TicksList.size();
  }

  public double getTPS() {
    return this.getTPS(3);
  }

  public double getTPS(int value) {
    var list = this.getLastTimes(value + 1);
    ArrayList<Double> arrayList = new ArrayList();
    if (!list.isEmpty()) {
      for (int count = 0; count < list.size() - 1; count++) {
        Long time = (Long) list.get(count + 1);
        if (time == null) {
          break;
        }

        double number = (Long) list.get(count) - (Long) list.get(count + 1);
        if (number <= 1000.0) {
          arrayList.add(20.0);
        } else {
          number = 20.0 / (number * 20.0 / 1000.0 / 20.0);
          number = number > 20.0 ? 20.0 : number;
          arrayList.add(number);
        }
      }
    }

    int countSecond = value;
    int countThird = 0;
    double amount = 0.0;

    for (int countFourth = 0;
        countFourth <= countSecond && countFourth <= arrayList.size() - 1;
        countFourth++) {
      amount += arrayList.get(countFourth);
      countThird++;
    }

    double amountSecond = amount / (countThird == 0 ? 1 : countThird);
    amountSecond = amountSecond > 20.0 ? 20.0 : amountSecond;
    return (int) (amountSecond * 100.0) / 100.0;
  }

  public List<Long> getLastTimes(int value) {
    ArrayList<Long> arrayList = new ArrayList();
    if (this.TicksList.isEmpty()) {
      return arrayList;
    }

    for (int count = 0; count < value && this.TicksList.size() > count; count++) {
      arrayList.add(this.TicksList.get(this.TicksList.size() - count - 1));
    }

    return arrayList;
  }

  @Override
  public void run() {
    this.TicksList.add(System.currentTimeMillis());
    if (this.TicksList.size() > 1800) {
      this.TicksList.remove(0);
    }
  }

  public static double getProcessCpuLoad() throws Exception {
    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = ObjectName.getInstance("java.lang:type=OperatingSystem");
    AttributeList attributeList =
        mBeanServer.getAttributes(objectName, new String[] {"ProcessCpuLoad"});
    if (attributeList.isEmpty()) {
      return 0.0;
    }

    Attribute attribute = (Attribute) attributeList.get(0);
    Double amount = (Double) attribute.getValue();
    return amount == -1.0 ? 0.0 : (int) (amount * 10000.0) / 100.0;
  }
}
