package com.Zrips.CMI.Modules.Holograms;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Colors.CMIChatColor;

public class CMIFont {
  private char character;
  private double length;
  private static ConcurrentHashMap<Character, CMIFont> fontMap = new ConcurrentHashMap<>();
  private static FontMetrics fm = null;
  private static boolean fail = false;
  public static double multy = 0.92;

  CMIFont(char ch, double number) {
    this.character = ch;
    this.length = number;
  }

  public char getCharacter() {
    return this.character;
  }

  public double getLength() {
    return this.length;
  }

  public double getBoldLength() {
    return this.getCharacter() == ' ' ? this.getLength() : this.length + 1.0;
  }

  private static CMIFont get(char ch) {
    CMIFont cmiFont = fontMap.get(ch);
    if (cmiFont != null) {
      return cmiFont;
    }

    CMIFont.CMIFont_FallBack cmiFont_FallBack = CMIFont.CMIFont_FallBack.getDefaultFontInfo(ch);
    if (cmiFont_FallBack != null && !cmiFont_FallBack.equals(CMIFont.CMIFont_FallBack.DEFAULT)) {
      return fontMap.computeIfAbsent(ch, charKey -> new CMIFont(ch, cmiFont_FallBack.getLength()));
    }

    try {
      if (!fail && fm == null) {
        Font font = new Font("Arial", 0, 9);
        fm = new Canvas().getFontMetrics(font);
      }
    } catch (Exception exception) {
      fail = true;
    }

    if (fm != null) {
      return fontMap.computeIfAbsent(ch, charKey -> new CMIFont(ch, fm.charWidth(ch)));
    }

    CMIFont.CMIFont_FallBack fallBackInfo = CMIFont.CMIFont_FallBack.getDefaultFontInfo(ch);
    return fontMap.computeIfAbsent(ch, charKey -> new CMIFont(ch, fallBackInfo.getLength()));
  }

  public static double getWidth(String string) {
    string = CMIChatColor.translate(string);
    double number = 0.0;
    boolean flag = false;
    boolean bold = false;
    char[] chs;
    int value = (chs = string.toCharArray()).length;

    for (int index = 0; index < value; index++) {
      char ch = chs[index];
      if (ch == 167) {
        flag = true;
      } else if (flag) {
        flag = false;
        if (ch != 'l' && ch != 'L') {
          bold = false;
        } else {
          bold = true;
        }
      } else {
        CMIFont cmiFont = get(ch);
        number += bold ? cmiFont.getBoldLength() : cmiFont.getLength();
        number++;
      }
    }

    return number;
  }

  private enum CMIFont_FallBack {
    A('A', 5.0),
    a('a', 5.0),
    B('B', 5.0),
    b('b', 5.0),
    C('C', 5.0),
    c('c', 5.0),
    D('D', 5.0),
    d('d', 5.0),
    E('E', 5.0),
    e('e', 5.0),
    F('F', 5.0),
    f('f', 4.0),
    G('G', 5.0),
    g('g', 5.0),
    H('H', 5.0),
    h('h', 5.0),
    I('I', 3.0),
    i('i', 1.0),
    J('J', 5.0),
    j('j', 5.0),
    K('K', 5.0),
    k('k', 4.0),
    L('L', 5.0),
    l('l', 2.0),
    M('M', 5.0),
    m('m', 5.0),
    N('N', 5.0),
    n('n', 5.0),
    O('O', 5.0),
    o('o', 5.0),
    P('P', 5.0),
    p('p', 5.0),
    Q('Q', 5.0),
    q('q', 5.0),
    R('R', 5.0),
    r('r', 5.0),
    S('S', 5.0),
    s('s', 5.0),
    T('T', 5.0),
    t('t', 3.0),
    U('U', 5.0),
    u('u', 5.0),
    V('V', 5.0),
    v('v', 5.0),
    W('W', 5.0),
    w('w', 5.0),
    X('X', 5.0),
    x('x', 5.0),
    Y('Y', 5.0),
    y('y', 5.0),
    Z('Z', 5.0),
    z('z', 5.0),
    NUM_1('1', 5.0),
    NUM_2('2', 5.0),
    NUM_3('3', 5.0),
    NUM_4('4', 5.0),
    NUM_5('5', 5.0),
    NUM_6('6', 5.0),
    NUM_7('7', 5.0),
    NUM_8('8', 5.0),
    NUM_9('9', 5.0),
    NUM_0('0', 5.0),
    EXCLAMATION_POINT('!', 1.0),
    AT_SYMBOL('@', 6.0),
    NUM_SIGN('#', 5.0),
    DOLLAR_SIGN('$', 5.0),
    PERCENT('%', 5.0),
    UP_ARROW('^', 5.0),
    AMPERSAND('&', 5.0),
    ASTERISK('*', 3.0),
    LEFT_PARENTHESIS('(', 3.0),
    RIGHT_PERENTHESIS(')', 3.0),
    MINUS('-', 5.0),
    UNDERSCORE('_', 5.0),
    PLUS_SIGN('+', 5.0),
    EQUALS_SIGN('=', 5.0),
    LEFT_CURL_BRACE('{', 3.0),
    RIGHT_CURL_BRACE('}', 3.0),
    LEFT_BRACKET('[', 3.0),
    RIGHT_BRACKET(']', 3.0),
    COLON(':', 1.0),
    SEMI_COLON(';', 1.0),
    DOUBLE_QUOTE('"', 3.0),
    SINGLE_QUOTE('\'', 1.0),
    LEFT_ARROW('<', 4.0),
    RIGHT_ARROW('>', 4.0),
    QUESTION_MARK('?', 5.0),
    SLASH('/', 5.0),
    BACK_SLASH('\\', 5.0),
    LINE('|', 1.0),
    TILDE('~', 6.0),
    TICK('`', 2.0),
    PERIOD('.', 1.0),
    COMMA(',', 1.0),
    SPACE(' ', 3.0),
    DEFAULT('\uffff', 5.0);

    private char character;
    private double length;

    CMIFont_FallBack(char ch, double number) {
      this.character = ch;
      this.length = number;
    }

    public char getCharacter() {
      return this.character;
    }

    public double getLength() {
      return this.length;
    }

    public static CMIFont.CMIFont_FallBack getDefaultFontInfo(char ch) {
      CMIFont.CMIFont_FallBack[] cmiFont_FallBacks;
      int value = (cmiFont_FallBacks = values()).length;

      for (int index = 0; index < value; index++) {
        CMIFont.CMIFont_FallBack cmiFont_FallBack = cmiFont_FallBacks[index];
        if (cmiFont_FallBack.getCharacter() == ch) {
          return cmiFont_FallBack;
        }
      }

      return DEFAULT;
    }
  }
}
