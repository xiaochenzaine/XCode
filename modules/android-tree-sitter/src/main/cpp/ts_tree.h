#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TSTree_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TSTree_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// edit (JLcom/itsaky/androidide/treesitter/TSInputEdit;)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSTree_Native_edit(JNIEnv *, jclass, jlong, jobject);
static JNINativeMethod TSTree_Native_edit = {"edit", "(JLcom/itsaky/androidide/treesitter/TSInputEdit;)V", nullptr};
#define TSTree_Native_edit__ARR_IDX 0
// delete (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSTree_Native_delete(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTree_Native_delete = {"delete", "(J)V", nullptr};
#define TSTree_Native_delete__ARR_IDX 1
// copy (J)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSTree_Native_copy(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTree_Native_copy = {"copy", "(J)J", nullptr};
#define TSTree_Native_copy__ARR_IDX 2
// rootNode (J)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSTree_Native_rootNode(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTree_Native_rootNode = {"rootNode", "(J)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSTree_Native_rootNode__ARR_IDX 3
// rootNodeWithOffset (JILcom/itsaky/androidide/treesitter/TSPoint;)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSTree_Native_rootNodeWithOffset(JNIEnv *, jclass, jlong, jint, jobject);
static JNINativeMethod TSTree_Native_rootNodeWithOffset = {"rootNodeWithOffset", "(JILcom/itsaky/androidide/treesitter/TSPoint;)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSTree_Native_rootNodeWithOffset__ARR_IDX 4
// changedRanges (JJ)[Lcom/itsaky/androidide/treesitter/TSRange;
JNIEXPORT jobjectArray JNICALL Java_com_itsaky_androidide_treesitter_TSTree_Native_changedRanges(JNIEnv *, jclass, jlong, jlong);
static JNINativeMethod TSTree_Native_changedRanges = {"changedRanges", "(JJ)[Lcom/itsaky/androidide/treesitter/TSRange;", nullptr};
#define TSTree_Native_changedRanges__ARR_IDX 5
// getLanguage (J)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSTree_Native_getLanguage(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTree_Native_getLanguage = {"getLanguage", "(J)J", nullptr};
#define TSTree_Native_getLanguage__ARR_IDX 6
// includedRanges (J)[Lcom/itsaky/androidide/treesitter/TSRange;
JNIEXPORT jobjectArray JNICALL Java_com_itsaky_androidide_treesitter_TSTree_Native_includedRanges(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTree_Native_includedRanges = {"includedRanges", "(J)[Lcom/itsaky/androidide/treesitter/TSRange;", nullptr};
#define TSTree_Native_includedRanges__ARR_IDX 7

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TSTree_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TSTree$Native"
#define TSTree_Native__METHOD_COUNT 8
void TSTree_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TSTree_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TSTree_Native_methods[8]; \
  TSTree_Native__SetJniMethods(TSTree_Native_methods, 8); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TSTree$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TSTree$Native"); } else { int rc = _env->RegisterNatives(cls, TSTree_Native_methods, 8); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TSTree$Native: %d", rc); } }
#endif
