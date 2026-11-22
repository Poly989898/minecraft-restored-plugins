package com.Zrips.CMI.Modules.Ips;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class IpManager {
  private CMI plugin;
  HashMap<String, Set<CMIUser>> ips = new HashMap<>();
  private static final Pattern PATTERN =
      Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
  private static final Pattern ALTPATTERN =
      Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\_){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

  public boolean ipIsOk(String string) {
    return PATTERN.matcher(string).matches();
  }

  public boolean alternateIpIsOk(String string) {
    return ALTPATTERN.matcher(string).matches();
  }

  public IpManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void addIp(HashMap<String, Integer> hashMap, CMIUser cmiUser) {
    for (var entry : hashMap.entrySet()) {
      this.addIp((String) entry.getKey(), cmiUser);
    }
  }

  public void addIp(String string, CMIUser cmiUser) {
    var set = this.ips.get(string);
    if (set == null) {
      set = new HashSet();
    }

    set.add(cmiUser);
    this.ips.put(string, set);
  }

  public Set<CMIUser> getUsers(String string) {
    return this.ips.get(string);
  }

  public void clearCache() {
    this.ips.clear();
  }
}
