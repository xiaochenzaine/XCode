package com.itsaky.androidide.treesitter.cmake;

import com.itsaky.androidide.treesitter.TSLanguage;
import com.itsaky.androidide.treesitter.TSLanguageCache;

public final class TSLanguageCmake {
  static {
    System.loadLibrary("tree-sitter-cpp");
  }

  private TSLanguageCmake() {
    throw new UnsupportedOperationException();
  }

  public static TSLanguage getInstance() {
    var language = TSLanguageCache.get("cmake");
    if (language != null) {
      return language;
    }
    language = TSLanguage.create("cmake", Native.getInstance());
    TSLanguageCache.cache("cmake", language);
    return language;
  }

  public static class Native {
    public static native long getInstance();
  }
}
