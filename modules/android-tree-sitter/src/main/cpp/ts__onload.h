#ifndef _Included__TS_JNI_ONLOAD_
#define _Included__TS_JNI_ONLOAD_
#include <jni.h>
#include "ts__log.h"
#include "ts_language.h"
#include "ts_lookahead_iterator.h"
#include "ts_node.h"
#include "ts_parser.h"
#include "ts_query.h"
#include "ts_query_cursor.h"
#include "ts_tree.h"
#include "ts_tree_cursor.h"
#include "ts_meta.h"
#include "ts_utf16string.h"
#include "ts_utf16string_factory.h"
#define TS_JNI_ONLOAD__DEFINE_METHODS_ARR
#define TS_JNI_ONLOAD__AUTO_REGISTER(env) \
  TSLanguage_Native_AutoRegisterNatives(env); \
  TSLookaheadIterator_Native_AutoRegisterNatives(env); \
  TSNode_Native_AutoRegisterNatives(env); \
  TSParser_Native_AutoRegisterNatives(env); \
  TSQuery_Native_AutoRegisterNatives(env); \
  TSQueryCursor_Native_AutoRegisterNatives(env); \
  TSTree_Native_AutoRegisterNatives(env); \
  TSTreeCursor_Native_AutoRegisterNatives(env); \
  TreeSitter_Native_AutoRegisterNatives(env); \
  UTF16String_Native_AutoRegisterNatives(env); \
  UTF16StringFactory_Native_AutoRegisterNatives(env); \

#endif
