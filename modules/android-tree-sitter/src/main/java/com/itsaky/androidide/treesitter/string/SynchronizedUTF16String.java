package com.itsaky.androidide.treesitter.string;

import com.itsaky.androidide.treesitter.util.Consumer;

/**
 * 同步版 UTF16String。原 android-tree-sitter 通过注解处理器生成，这里内置源码，避免额外构建插件。
 */
public class SynchronizedUTF16String extends UTF16String {
  public SynchronizedUTF16String(long pointer) { super(pointer); }
  @Override public synchronized byte byteAt(int index) { return super.byteAt(index); }
  @Override public synchronized void setByteAt(int index, byte b) { super.setByteAt(index, b); }
  @Override public synchronized char charAt(int index) { return super.charAt(index); }
  @Override public synchronized void setCharAt(int index, char c) { super.setCharAt(index, c); }
  @Override public synchronized void append(String str) { super.append(str); }
  @Override public synchronized void append(String str, int start, int end) { super.append(str, start, end); }
  @Override public synchronized void insert(int index, String str) { super.insert(index, str); }
  @Override public synchronized void delete(int start, int end) { super.delete(start, end); }
  @Override public synchronized void deleteBytes(int start, int end) { super.deleteBytes(start, end); }
  @Override public synchronized void replaceChars(int start, int end, String str) { super.replaceChars(start, end, str); }
  @Override public synchronized void replaceBytes(int start, int end, String str) { super.replaceBytes(start, end, str); }
  @Override public synchronized UTF16String subseqChars(int start, int end) { return super.subseqChars(start, end); }
  @Override public synchronized UTF16String subseqBytes(int start) { return super.subseqBytes(start); }
  @Override public synchronized UTF16String subseqBytes(int start, int end) { return super.subseqBytes(start, end); }
  @Override public synchronized String substringChars(int start) { return super.substringChars(start); }
  @Override public synchronized String substringChars(int start, int end) { return super.substringChars(start, end); }
  @Override public synchronized String substringBytes(int start) { return super.substringBytes(start); }
  @Override public synchronized String substringBytes(int start, int end) { return super.substringBytes(start, end); }
  @Override public synchronized int length() { return super.length(); }
  @Override public synchronized int byteLength() { return super.byteLength(); }
  @Override public synchronized CharSequence subSequence(int start, int end) { return super.subSequence(start, end); }
  @Override public synchronized void closeNativeObj() { super.closeNativeObj(); }
  @Override public synchronized String toString() { return super.toString(); }
  @Override public synchronized boolean equals(Object obj) { return super.equals(obj); }
  @Override public synchronized int hashCode() { return super.hashCode(); }
  @Override public synchronized void forEachChar(int start, int end, Consumer<Character> consumer) { super.forEachChar(start, end, consumer); }
  @Override public synchronized void forEachByte(int start, int end, Consumer<Byte> consumer) { super.forEachByte(start, end, consumer); }
  @Override public UTF16String synchronizedString() { return this; }
}
