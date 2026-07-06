#ifndef TREE_SITTER_COMPAT_ALLOC_H_
#define TREE_SITTER_COMPAT_ALLOC_H_

#include <stdlib.h>

#ifndef ts_malloc
#define ts_malloc malloc
#endif

#ifndef ts_calloc
#define ts_calloc calloc
#endif

#ifndef ts_realloc
#define ts_realloc realloc
#endif

#ifndef ts_free
#define ts_free free
#endif

#endif
