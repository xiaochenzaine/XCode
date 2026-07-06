#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// newIterator (JS)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_newIterator(JNIEnv *, jclass, jlong, jshort);
static JNINativeMethod TSLookaheadIterator_Native_newIterator = {"newIterator", "(JS)J", nullptr};
#define TSLookaheadIterator_Native_newIterator__ARR_IDX 0
// delete (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_delete(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLookaheadIterator_Native_delete = {"delete", "(J)V", nullptr};
#define TSLookaheadIterator_Native_delete__ARR_IDX 1
// next (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_next(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLookaheadIterator_Native_next = {"next", "(J)Z", nullptr};
#define TSLookaheadIterator_Native_next__ARR_IDX 2
// currentSymbol (J)S
JNIEXPORT jshort JNICALL Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_currentSymbol(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLookaheadIterator_Native_currentSymbol = {"currentSymbol", "(J)S", nullptr};
#define TSLookaheadIterator_Native_currentSymbol__ARR_IDX 3
// currentSymbolName (J)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_currentSymbolName(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLookaheadIterator_Native_currentSymbolName = {"currentSymbolName", "(J)Ljava/lang/String;", nullptr};
#define TSLookaheadIterator_Native_currentSymbolName__ARR_IDX 4
// resetState (JS)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_resetState(JNIEnv *, jclass, jlong, jshort);
static JNINativeMethod TSLookaheadIterator_Native_resetState = {"resetState", "(JS)Z", nullptr};
#define TSLookaheadIterator_Native_resetState__ARR_IDX 5
// reset (JJS)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_reset(JNIEnv *, jclass, jlong, jlong, jshort);
static JNINativeMethod TSLookaheadIterator_Native_reset = {"reset", "(JJS)Z", nullptr};
#define TSLookaheadIterator_Native_reset__ARR_IDX 6
// language (J)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSLookaheadIterator_Native_language(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLookaheadIterator_Native_language = {"language", "(J)J", nullptr};
#define TSLookaheadIterator_Native_language__ARR_IDX 7

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TSLookaheadIterator_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TSLookaheadIterator$Native"
#define TSLookaheadIterator_Native__METHOD_COUNT 8
void TSLookaheadIterator_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TSLookaheadIterator_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TSLookaheadIterator_Native_methods[8]; \
  TSLookaheadIterator_Native__SetJniMethods(TSLookaheadIterator_Native_methods, 8); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TSLookaheadIterator$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TSLookaheadIterator$Native"); } else { int rc = _env->RegisterNatives(cls, TSLookaheadIterator_Native_methods, 8); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TSLookaheadIterator$Native: %d", rc); } }
#endif
