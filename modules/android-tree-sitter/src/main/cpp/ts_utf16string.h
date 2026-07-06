#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_string_UTF16String_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_string_UTF16String_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// byteAt (JI)B
JNIEXPORT jbyte JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_byteAt(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod UTF16String_Native_byteAt = {"byteAt", "(JI)B", nullptr};
#define UTF16String_Native_byteAt__ARR_IDX 0
// setByteAt (JIB)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_setByteAt(JNIEnv *, jclass, jlong, jint, jbyte);
static JNINativeMethod UTF16String_Native_setByteAt = {"setByteAt", "(JIB)V", nullptr};
#define UTF16String_Native_setByteAt__ARR_IDX 1
// chatAt (JI)C
JNIEXPORT jchar JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_chatAt(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod UTF16String_Native_chatAt = {"chatAt", "(JI)C", nullptr};
#define UTF16String_Native_chatAt__ARR_IDX 2
// setCharAt (JIC)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_setCharAt(JNIEnv *, jclass, jlong, jint, jchar);
static JNINativeMethod UTF16String_Native_setCharAt = {"setCharAt", "(JIC)V", nullptr};
#define UTF16String_Native_setCharAt__ARR_IDX 3
// append (JLjava/lang/String;)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_append(JNIEnv *, jclass, jlong, jstring);
static JNINativeMethod UTF16String_Native_append = {"append", "(JLjava/lang/String;)V", nullptr};
#define UTF16String_Native_append__ARR_IDX 4
// appendPart (JLjava/lang/String;II)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_appendPart(JNIEnv *, jclass, jlong, jstring, jint, jint);
static JNINativeMethod UTF16String_Native_appendPart = {"appendPart", "(JLjava/lang/String;II)V", nullptr};
#define UTF16String_Native_appendPart__ARR_IDX 5
// insert (JLjava/lang/String;I)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_insert(JNIEnv *, jclass, jlong, jstring, jint);
static JNINativeMethod UTF16String_Native_insert = {"insert", "(JLjava/lang/String;I)V", nullptr};
#define UTF16String_Native_insert__ARR_IDX 6
// deleteChars (JII)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_deleteChars(JNIEnv *, jclass, jlong, jint, jint);
static JNINativeMethod UTF16String_Native_deleteChars = {"deleteChars", "(JII)V", nullptr};
#define UTF16String_Native_deleteChars__ARR_IDX 7
// deleteBytes (JII)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_deleteBytes(JNIEnv *, jclass, jlong, jint, jint);
static JNINativeMethod UTF16String_Native_deleteBytes = {"deleteBytes", "(JII)V", nullptr};
#define UTF16String_Native_deleteBytes__ARR_IDX 8
// replaceChars (JIILjava/lang/String;)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_replaceChars(JNIEnv *, jclass, jlong, jint, jint, jstring);
static JNINativeMethod UTF16String_Native_replaceChars = {"replaceChars", "(JIILjava/lang/String;)V", nullptr};
#define UTF16String_Native_replaceChars__ARR_IDX 9
// replaceBytes (JIILjava/lang/String;)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_replaceBytes(JNIEnv *, jclass, jlong, jint, jint, jstring);
static JNINativeMethod UTF16String_Native_replaceBytes = {"replaceBytes", "(JIILjava/lang/String;)V", nullptr};
#define UTF16String_Native_replaceBytes__ARR_IDX 10
// substring_chars (JII)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_substring_chars(JNIEnv *, jclass, jlong, jint, jint);
static JNINativeMethod UTF16String_Native_substring_chars = {"substring_chars", "(JII)J", nullptr};
#define UTF16String_Native_substring_chars__ARR_IDX 11
// substring_bytes (JII)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_substring_bytes(JNIEnv *, jclass, jlong, jint, jint);
static JNINativeMethod UTF16String_Native_substring_bytes = {"substring_bytes", "(JII)J", nullptr};
#define UTF16String_Native_substring_bytes__ARR_IDX 12
// subjstring_chars (JII)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_subjstring_chars(JNIEnv *, jclass, jlong, jint, jint);
static JNINativeMethod UTF16String_Native_subjstring_chars = {"subjstring_chars", "(JII)Ljava/lang/String;", nullptr};
#define UTF16String_Native_subjstring_chars__ARR_IDX 13
// subjstring_bytes (JII)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_subjstring_bytes(JNIEnv *, jclass, jlong, jint, jint);
static JNINativeMethod UTF16String_Native_subjstring_bytes = {"subjstring_bytes", "(JII)Ljava/lang/String;", nullptr};
#define UTF16String_Native_subjstring_bytes__ARR_IDX 14
// toString (J)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_toString(JNIEnv *, jclass, jlong);
static JNINativeMethod UTF16String_Native_toString = {"toString", "(J)Ljava/lang/String;", nullptr};
#define UTF16String_Native_toString__ARR_IDX 15
// length (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_length(JNIEnv *, jclass, jlong);
static JNINativeMethod UTF16String_Native_length = {"length", "(J)I", nullptr};
#define UTF16String_Native_length__ARR_IDX 16
// byteLength (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_byteLength(JNIEnv *, jclass, jlong);
static JNINativeMethod UTF16String_Native_byteLength = {"byteLength", "(J)I", nullptr};
#define UTF16String_Native_byteLength__ARR_IDX 17
// erase (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16String_Native_erase(JNIEnv *, jclass, jlong);
static JNINativeMethod UTF16String_Native_erase = {"erase", "(J)V", nullptr};
#define UTF16String_Native_erase__ARR_IDX 18

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define UTF16String_Native__CLASS_NAME "com/itsaky/androidide/treesitter/string/UTF16String$Native"
#define UTF16String_Native__METHOD_COUNT 19
void UTF16String_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define UTF16String_Native_AutoRegisterNatives(_env) \
  JNINativeMethod UTF16String_Native_methods[19]; \
  UTF16String_Native__SetJniMethods(UTF16String_Native_methods, 19); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/string/UTF16String$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/string/UTF16String$Native"); } else { int rc = _env->RegisterNatives(cls, UTF16String_Native_methods, 19); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/string/UTF16String$Native: %d", rc); } }
#endif
