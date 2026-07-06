#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TSTreeCursor_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TSTreeCursor_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// newCursor (Lcom/itsaky/androidide/treesitter/TSNode;)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_newCursor(JNIEnv *, jclass, jobject);
static JNINativeMethod TSTreeCursor_Native_newCursor = {"newCursor", "(Lcom/itsaky/androidide/treesitter/TSNode;)J", nullptr};
#define TSTreeCursor_Native_newCursor__ARR_IDX 0
// currentTreeCursorNode (J)Lcom/itsaky/androidide/treesitter/TSTreeCursorNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_currentTreeCursorNode(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_currentTreeCursorNode = {"currentTreeCursorNode", "(J)Lcom/itsaky/androidide/treesitter/TSTreeCursorNode;", nullptr};
#define TSTreeCursor_Native_currentTreeCursorNode__ARR_IDX 1
// currentFieldName (J)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_currentFieldName(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_currentFieldName = {"currentFieldName", "(J)Ljava/lang/String;", nullptr};
#define TSTreeCursor_Native_currentFieldName__ARR_IDX 2
// currentNode (J)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_currentNode(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_currentNode = {"currentNode", "(J)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSTreeCursor_Native_currentNode__ARR_IDX 3
// delete (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_delete(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_delete = {"delete", "(J)V", nullptr};
#define TSTreeCursor_Native_delete__ARR_IDX 4
// gotoFirstChild (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_gotoFirstChild(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_gotoFirstChild = {"gotoFirstChild", "(J)Z", nullptr};
#define TSTreeCursor_Native_gotoFirstChild__ARR_IDX 5
// gotoNextSibling (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_gotoNextSibling(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_gotoNextSibling = {"gotoNextSibling", "(J)Z", nullptr};
#define TSTreeCursor_Native_gotoNextSibling__ARR_IDX 6
// gotoParent (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_gotoParent(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_gotoParent = {"gotoParent", "(J)Z", nullptr};
#define TSTreeCursor_Native_gotoParent__ARR_IDX 7
// currentFieldId (J)S
JNIEXPORT jshort JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_currentFieldId(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_currentFieldId = {"currentFieldId", "(J)S", nullptr};
#define TSTreeCursor_Native_currentFieldId__ARR_IDX 8
// gotoFirstChildForByte (JI)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_gotoFirstChildForByte(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSTreeCursor_Native_gotoFirstChildForByte = {"gotoFirstChildForByte", "(JI)J", nullptr};
#define TSTreeCursor_Native_gotoFirstChildForByte__ARR_IDX 9
// gotoFirstChildForPoint (JLcom/itsaky/androidide/treesitter/TSPoint;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_gotoFirstChildForPoint(JNIEnv *, jclass, jlong, jobject);
static JNINativeMethod TSTreeCursor_Native_gotoFirstChildForPoint = {"gotoFirstChildForPoint", "(JLcom/itsaky/androidide/treesitter/TSPoint;)Z", nullptr};
#define TSTreeCursor_Native_gotoFirstChildForPoint__ARR_IDX 10
// gotoLastChild (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_gotoLastChild(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_gotoLastChild = {"gotoLastChild", "(J)Z", nullptr};
#define TSTreeCursor_Native_gotoLastChild__ARR_IDX 11
// gotoPreviousSibling (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_gotoPreviousSibling(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_gotoPreviousSibling = {"gotoPreviousSibling", "(J)Z", nullptr};
#define TSTreeCursor_Native_gotoPreviousSibling__ARR_IDX 12
// gotoDescendant (JI)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_gotoDescendant(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSTreeCursor_Native_gotoDescendant = {"gotoDescendant", "(JI)V", nullptr};
#define TSTreeCursor_Native_gotoDescendant__ARR_IDX 13
// currentDescendantIndex (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_currentDescendantIndex(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_currentDescendantIndex = {"currentDescendantIndex", "(J)I", nullptr};
#define TSTreeCursor_Native_currentDescendantIndex__ARR_IDX 14
// depth (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_depth(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_depth = {"depth", "(J)I", nullptr};
#define TSTreeCursor_Native_depth__ARR_IDX 15
// reset (JLcom/itsaky/androidide/treesitter/TSNode;)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_reset(JNIEnv *, jclass, jlong, jobject);
static JNINativeMethod TSTreeCursor_Native_reset = {"reset", "(JLcom/itsaky/androidide/treesitter/TSNode;)V", nullptr};
#define TSTreeCursor_Native_reset__ARR_IDX 16
// resetTo (JJ)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_resetTo(JNIEnv *, jclass, jlong, jlong);
static JNINativeMethod TSTreeCursor_Native_resetTo = {"resetTo", "(JJ)V", nullptr};
#define TSTreeCursor_Native_resetTo__ARR_IDX 17
// copy (J)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSTreeCursor_Native_copy(JNIEnv *, jclass, jlong);
static JNINativeMethod TSTreeCursor_Native_copy = {"copy", "(J)J", nullptr};
#define TSTreeCursor_Native_copy__ARR_IDX 18

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TSTreeCursor_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TSTreeCursor$Native"
#define TSTreeCursor_Native__METHOD_COUNT 19
void TSTreeCursor_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TSTreeCursor_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TSTreeCursor_Native_methods[19]; \
  TSTreeCursor_Native__SetJniMethods(TSTreeCursor_Native_methods, 19); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TSTreeCursor$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TSTreeCursor$Native"); } else { int rc = _env->RegisterNatives(cls, TSTreeCursor_Native_methods, 19); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TSTreeCursor$Native: %d", rc); } }
#endif
