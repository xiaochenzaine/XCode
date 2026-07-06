#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TSQueryCursor_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TSQueryCursor_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// newCursor ()J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_newCursor(JNIEnv *, jclass);
static JNINativeMethod TSQueryCursor_Native_newCursor = {"newCursor", "()J", nullptr};
#define TSQueryCursor_Native_newCursor__ARR_IDX 0
// delete (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_delete(JNIEnv *, jclass, jlong);
static JNINativeMethod TSQueryCursor_Native_delete = {"delete", "(J)V", nullptr};
#define TSQueryCursor_Native_delete__ARR_IDX 1
// exec (JJLcom/itsaky/androidide/treesitter/TSNode;)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_exec(JNIEnv *, jclass, jlong, jlong, jobject);
static JNINativeMethod TSQueryCursor_Native_exec = {"exec", "(JJLcom/itsaky/androidide/treesitter/TSNode;)V", nullptr};
#define TSQueryCursor_Native_exec__ARR_IDX 2
// exceededMatchLimit (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_exceededMatchLimit(JNIEnv *, jclass, jlong);
static JNINativeMethod TSQueryCursor_Native_exceededMatchLimit = {"exceededMatchLimit", "(J)Z", nullptr};
#define TSQueryCursor_Native_exceededMatchLimit__ARR_IDX 3
// setMatchLimit (JI)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_setMatchLimit(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQueryCursor_Native_setMatchLimit = {"setMatchLimit", "(JI)V", nullptr};
#define TSQueryCursor_Native_setMatchLimit__ARR_IDX 4
// getMatchLimit (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_getMatchLimit(JNIEnv *, jclass, jlong);
static JNINativeMethod TSQueryCursor_Native_getMatchLimit = {"getMatchLimit", "(J)I", nullptr};
#define TSQueryCursor_Native_getMatchLimit__ARR_IDX 5
// setByteRange (JII)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_setByteRange(JNIEnv *, jclass, jlong, jint, jint);
static JNINativeMethod TSQueryCursor_Native_setByteRange = {"setByteRange", "(JII)V", nullptr};
#define TSQueryCursor_Native_setByteRange__ARR_IDX 6
// setPointRange (JLcom/itsaky/androidide/treesitter/TSPoint;Lcom/itsaky/androidide/treesitter/TSPoint;)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_setPointRange(JNIEnv *, jclass, jlong, jobject, jobject);
static JNINativeMethod TSQueryCursor_Native_setPointRange = {"setPointRange", "(JLcom/itsaky/androidide/treesitter/TSPoint;Lcom/itsaky/androidide/treesitter/TSPoint;)V", nullptr};
#define TSQueryCursor_Native_setPointRange__ARR_IDX 7
// nextMatch (J)Lcom/itsaky/androidide/treesitter/TSQueryMatch;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_nextMatch(JNIEnv *, jclass, jlong);
static JNINativeMethod TSQueryCursor_Native_nextMatch = {"nextMatch", "(J)Lcom/itsaky/androidide/treesitter/TSQueryMatch;", nullptr};
#define TSQueryCursor_Native_nextMatch__ARR_IDX 8
// removeMatch (JI)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQueryCursor_Native_removeMatch(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQueryCursor_Native_removeMatch = {"removeMatch", "(JI)V", nullptr};
#define TSQueryCursor_Native_removeMatch__ARR_IDX 9

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TSQueryCursor_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TSQueryCursor$Native"
#define TSQueryCursor_Native__METHOD_COUNT 10
void TSQueryCursor_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TSQueryCursor_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TSQueryCursor_Native_methods[10]; \
  TSQueryCursor_Native__SetJniMethods(TSQueryCursor_Native_methods, 10); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TSQueryCursor$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TSQueryCursor$Native"); } else { int rc = _env->RegisterNatives(cls, TSQueryCursor_Native_methods, 10); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TSQueryCursor$Native: %d", rc); } }
#endif
