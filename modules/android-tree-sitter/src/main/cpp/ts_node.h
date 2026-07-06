#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TSNode_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TSNode_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// canAccess (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_canAccess(JNIEnv *, jclass, jlong);
static JNINativeMethod TSNode_Native_canAccess = {"canAccess", "(J)Z", nullptr};
#define TSNode_Native_canAccess__ARR_IDX 0
// getParent (Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getParent(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getParent = {"getParent", "(Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getParent__ARR_IDX 1
// getChildAt (Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getChildAt(JNIEnv *, jclass, jobject, jint);
static JNINativeMethod TSNode_Native_getChildAt = {"getChildAt", "(Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getChildAt__ARR_IDX 2
// getNamedChildAt (Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getNamedChildAt(JNIEnv *, jclass, jobject, jint);
static JNINativeMethod TSNode_Native_getNamedChildAt = {"getNamedChildAt", "(Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getNamedChildAt__ARR_IDX 3
// getChildByFieldName (Lcom/itsaky/androidide/treesitter/TSNode;[BI)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getChildByFieldName(JNIEnv *, jclass, jobject, jbyteArray, jint);
static JNINativeMethod TSNode_Native_getChildByFieldName = {"getChildByFieldName", "(Lcom/itsaky/androidide/treesitter/TSNode;[BI)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getChildByFieldName__ARR_IDX 4
// getFieldNameForChild (Lcom/itsaky/androidide/treesitter/TSNode;I)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getFieldNameForChild(JNIEnv *, jclass, jobject, jint);
static JNINativeMethod TSNode_Native_getFieldNameForChild = {"getFieldNameForChild", "(Lcom/itsaky/androidide/treesitter/TSNode;I)Ljava/lang/String;", nullptr};
#define TSNode_Native_getFieldNameForChild__ARR_IDX 5
// getChildByFieldId (Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getChildByFieldId(JNIEnv *, jclass, jobject, jint);
static JNINativeMethod TSNode_Native_getChildByFieldId = {"getChildByFieldId", "(Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getChildByFieldId__ARR_IDX 6
// getNextSibling (Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getNextSibling(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getNextSibling = {"getNextSibling", "(Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getNextSibling__ARR_IDX 7
// getPreviousSibling (Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getPreviousSibling(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getPreviousSibling = {"getPreviousSibling", "(Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getPreviousSibling__ARR_IDX 8
// getNextNamedSibling (Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getNextNamedSibling(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getNextNamedSibling = {"getNextNamedSibling", "(Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getNextNamedSibling__ARR_IDX 9
// getPreviousNamedSibling (Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getPreviousNamedSibling(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getPreviousNamedSibling = {"getPreviousNamedSibling", "(Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getPreviousNamedSibling__ARR_IDX 10
// getFirstChildForByte (Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getFirstChildForByte(JNIEnv *, jclass, jobject, jint);
static JNINativeMethod TSNode_Native_getFirstChildForByte = {"getFirstChildForByte", "(Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getFirstChildForByte__ARR_IDX 11
// getFirstNamedChildForByte (Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getFirstNamedChildForByte(JNIEnv *, jclass, jobject, jint);
static JNINativeMethod TSNode_Native_getFirstNamedChildForByte = {"getFirstNamedChildForByte", "(Lcom/itsaky/androidide/treesitter/TSNode;I)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getFirstNamedChildForByte__ARR_IDX 12
// getDescendantForByteRange (Lcom/itsaky/androidide/treesitter/TSNode;II)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getDescendantForByteRange(JNIEnv *, jclass, jobject, jint, jint);
static JNINativeMethod TSNode_Native_getDescendantForByteRange = {"getDescendantForByteRange", "(Lcom/itsaky/androidide/treesitter/TSNode;II)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getDescendantForByteRange__ARR_IDX 13
// getDescendantForPointRange (Lcom/itsaky/androidide/treesitter/TSNode;Lcom/itsaky/androidide/treesitter/TSPoint;Lcom/itsaky/androidide/treesitter/TSPoint;)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getDescendantForPointRange(JNIEnv *, jclass, jobject, jobject, jobject);
static JNINativeMethod TSNode_Native_getDescendantForPointRange = {"getDescendantForPointRange", "(Lcom/itsaky/androidide/treesitter/TSNode;Lcom/itsaky/androidide/treesitter/TSPoint;Lcom/itsaky/androidide/treesitter/TSPoint;)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getDescendantForPointRange__ARR_IDX 14
// getNamedDescendantForByteRange (Lcom/itsaky/androidide/treesitter/TSNode;II)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getNamedDescendantForByteRange(JNIEnv *, jclass, jobject, jint, jint);
static JNINativeMethod TSNode_Native_getNamedDescendantForByteRange = {"getNamedDescendantForByteRange", "(Lcom/itsaky/androidide/treesitter/TSNode;II)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getNamedDescendantForByteRange__ARR_IDX 15
// getNamedDescendantForPointRange (Lcom/itsaky/androidide/treesitter/TSNode;Lcom/itsaky/androidide/treesitter/TSPoint;Lcom/itsaky/androidide/treesitter/TSPoint;)Lcom/itsaky/androidide/treesitter/TSNode;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getNamedDescendantForPointRange(JNIEnv *, jclass, jobject, jobject, jobject);
static JNINativeMethod TSNode_Native_getNamedDescendantForPointRange = {"getNamedDescendantForPointRange", "(Lcom/itsaky/androidide/treesitter/TSNode;Lcom/itsaky/androidide/treesitter/TSPoint;Lcom/itsaky/androidide/treesitter/TSPoint;)Lcom/itsaky/androidide/treesitter/TSNode;", nullptr};
#define TSNode_Native_getNamedDescendantForPointRange__ARR_IDX 16
// isEqualTo (Lcom/itsaky/androidide/treesitter/TSNode;Lcom/itsaky/androidide/treesitter/TSNode;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_isEqualTo(JNIEnv *, jclass, jobject, jobject);
static JNINativeMethod TSNode_Native_isEqualTo = {"isEqualTo", "(Lcom/itsaky/androidide/treesitter/TSNode;Lcom/itsaky/androidide/treesitter/TSNode;)Z", nullptr};
#define TSNode_Native_isEqualTo__ARR_IDX 17
// getChildCount (Lcom/itsaky/androidide/treesitter/TSNode;)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getChildCount(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getChildCount = {"getChildCount", "(Lcom/itsaky/androidide/treesitter/TSNode;)I", nullptr};
#define TSNode_Native_getChildCount__ARR_IDX 18
// getNamedChildCount (Lcom/itsaky/androidide/treesitter/TSNode;)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getNamedChildCount(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getNamedChildCount = {"getNamedChildCount", "(Lcom/itsaky/androidide/treesitter/TSNode;)I", nullptr};
#define TSNode_Native_getNamedChildCount__ARR_IDX 19
// getNodeString (Lcom/itsaky/androidide/treesitter/TSNode;)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getNodeString(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getNodeString = {"getNodeString", "(Lcom/itsaky/androidide/treesitter/TSNode;)Ljava/lang/String;", nullptr};
#define TSNode_Native_getNodeString__ARR_IDX 20
// getStartByte (Lcom/itsaky/androidide/treesitter/TSNode;)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getStartByte(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getStartByte = {"getStartByte", "(Lcom/itsaky/androidide/treesitter/TSNode;)I", nullptr};
#define TSNode_Native_getStartByte__ARR_IDX 21
// getEndByte (Lcom/itsaky/androidide/treesitter/TSNode;)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getEndByte(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getEndByte = {"getEndByte", "(Lcom/itsaky/androidide/treesitter/TSNode;)I", nullptr};
#define TSNode_Native_getEndByte__ARR_IDX 22
// getStartPoint (Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSPoint;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getStartPoint(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getStartPoint = {"getStartPoint", "(Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSPoint;", nullptr};
#define TSNode_Native_getStartPoint__ARR_IDX 23
// getEndPoint (Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSPoint;
JNIEXPORT jobject JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getEndPoint(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getEndPoint = {"getEndPoint", "(Lcom/itsaky/androidide/treesitter/TSNode;)Lcom/itsaky/androidide/treesitter/TSPoint;", nullptr};
#define TSNode_Native_getEndPoint__ARR_IDX 24
// getType (Lcom/itsaky/androidide/treesitter/TSNode;)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getType(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getType = {"getType", "(Lcom/itsaky/androidide/treesitter/TSNode;)Ljava/lang/String;", nullptr};
#define TSNode_Native_getType__ARR_IDX 25
// getSymbol (Lcom/itsaky/androidide/treesitter/TSNode;)S
JNIEXPORT jshort JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getSymbol(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getSymbol = {"getSymbol", "(Lcom/itsaky/androidide/treesitter/TSNode;)S", nullptr};
#define TSNode_Native_getSymbol__ARR_IDX 26
// getGrammarSymbol (Lcom/itsaky/androidide/treesitter/TSNode;)S
JNIEXPORT jshort JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getGrammarSymbol(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getGrammarSymbol = {"getGrammarSymbol", "(Lcom/itsaky/androidide/treesitter/TSNode;)S", nullptr};
#define TSNode_Native_getGrammarSymbol__ARR_IDX 27
// isNull (Lcom/itsaky/androidide/treesitter/TSNode;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_isNull(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_isNull = {"isNull", "(Lcom/itsaky/androidide/treesitter/TSNode;)Z", nullptr};
#define TSNode_Native_isNull__ARR_IDX 28
// isNamed (Lcom/itsaky/androidide/treesitter/TSNode;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_isNamed(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_isNamed = {"isNamed", "(Lcom/itsaky/androidide/treesitter/TSNode;)Z", nullptr};
#define TSNode_Native_isNamed__ARR_IDX 29
// isExtra (Lcom/itsaky/androidide/treesitter/TSNode;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_isExtra(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_isExtra = {"isExtra", "(Lcom/itsaky/androidide/treesitter/TSNode;)Z", nullptr};
#define TSNode_Native_isExtra__ARR_IDX 30
// isMissing (Lcom/itsaky/androidide/treesitter/TSNode;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_isMissing(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_isMissing = {"isMissing", "(Lcom/itsaky/androidide/treesitter/TSNode;)Z", nullptr};
#define TSNode_Native_isMissing__ARR_IDX 31
// hasChanges (Lcom/itsaky/androidide/treesitter/TSNode;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_hasChanges(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_hasChanges = {"hasChanges", "(Lcom/itsaky/androidide/treesitter/TSNode;)Z", nullptr};
#define TSNode_Native_hasChanges__ARR_IDX 32
// hasErrors (Lcom/itsaky/androidide/treesitter/TSNode;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_hasErrors(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_hasErrors = {"hasErrors", "(Lcom/itsaky/androidide/treesitter/TSNode;)Z", nullptr};
#define TSNode_Native_hasErrors__ARR_IDX 33
// isError (Lcom/itsaky/androidide/treesitter/TSNode;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_isError(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_isError = {"isError", "(Lcom/itsaky/androidide/treesitter/TSNode;)Z", nullptr};
#define TSNode_Native_isError__ARR_IDX 34
// getParseState (Lcom/itsaky/androidide/treesitter/TSNode;)S
JNIEXPORT jshort JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getParseState(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getParseState = {"getParseState", "(Lcom/itsaky/androidide/treesitter/TSNode;)S", nullptr};
#define TSNode_Native_getParseState__ARR_IDX 35
// edit (Lcom/itsaky/androidide/treesitter/TSNode;Lcom/itsaky/androidide/treesitter/TSInputEdit;)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_edit(JNIEnv *, jclass, jobject, jobject);
static JNINativeMethod TSNode_Native_edit = {"edit", "(Lcom/itsaky/androidide/treesitter/TSNode;Lcom/itsaky/androidide/treesitter/TSInputEdit;)V", nullptr};
#define TSNode_Native_edit__ARR_IDX 36
// getNextParseState (Lcom/itsaky/androidide/treesitter/TSNode;)S
JNIEXPORT jshort JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getNextParseState(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getNextParseState = {"getNextParseState", "(Lcom/itsaky/androidide/treesitter/TSNode;)S", nullptr};
#define TSNode_Native_getNextParseState__ARR_IDX 37
// getDescendantCount (Lcom/itsaky/androidide/treesitter/TSNode;)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getDescendantCount(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getDescendantCount = {"getDescendantCount", "(Lcom/itsaky/androidide/treesitter/TSNode;)I", nullptr};
#define TSNode_Native_getDescendantCount__ARR_IDX 38
// getGrammarType (Lcom/itsaky/androidide/treesitter/TSNode;)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getGrammarType(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getGrammarType = {"getGrammarType", "(Lcom/itsaky/androidide/treesitter/TSNode;)Ljava/lang/String;", nullptr};
#define TSNode_Native_getGrammarType__ARR_IDX 39
// getLanguage (Lcom/itsaky/androidide/treesitter/TSNode;)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSNode_Native_getLanguage(JNIEnv *, jclass, jobject);
static JNINativeMethod TSNode_Native_getLanguage = {"getLanguage", "(Lcom/itsaky/androidide/treesitter/TSNode;)J", nullptr};
#define TSNode_Native_getLanguage__ARR_IDX 40

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TSNode_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TSNode$Native"
#define TSNode_Native__METHOD_COUNT 41
void TSNode_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TSNode_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TSNode_Native_methods[41]; \
  TSNode_Native__SetJniMethods(TSNode_Native_methods, 41); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TSNode$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TSNode$Native"); } else { int rc = _env->RegisterNatives(cls, TSNode_Native_methods, 41); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TSNode$Native: %d", rc); } }
#endif
