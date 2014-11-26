#include "list.h"
#include <assert.h>
#include <stdlib.h>
#include <string.h>


static void *malloc0(size_t size) {
    void *mem_block = malloc(size);
    memset(mem_block, 0, size);
    return mem_block;
}

list_t *list_alloc() {
    return (list_t *) malloc0(sizeof(list_t));
}

void list_free(list_t *list) {
    free(list);
}

void list_free_all(list_t *list) {
    list_t *next;

    next = NULL;
    while (list) {
        next = list->next;
        free(list);
        list = next;
    }
}

list_t *list_append(list_t *list, void *data) {
    list_t *new_list;
    list_t *last;

    new_list = list_alloc();
    new_list->data = data;
    new_list->next = NULL;
    if (list) {
        last = list_last(list);
        last->next = new_list;
        new_list->prev = last;
        return list;
    } else {
        new_list->prev = NULL;
        return new_list;
    }
}

list_t *list_prepend(list_t *list, void *data) {
    list_t *new_list;

    new_list = list_alloc();
    new_list->data = data;
    new_list->next = list;
    if (list) {
        new_list->prev = list->prev;
        if (list->prev) {
            list->prev->next = new_list;
        }
        list->prev = new_list;
    } else {
        new_list->prev = NULL;
    }
    return new_list;
}

list_t *list_insert(list_t *list, void *data, int pos) {
    list_t *new_list;
    list_t *tmp_list;

    if (pos < 0) {
        return list_append(list, data);
    } else if (pos == 0) {
        return list_prepend(list, data);
    }
    tmp_list = list_nth(list, pos);
    if (!tmp_list) {
        return list_append(list, data);
    }

    new_list = list_alloc();
    new_list->data = data;
    new_list->prev = tmp_list->prev;
    if (tmp_list->prev) {
        tmp_list->prev->next = new_list;
    }
    new_list->next = tmp_list;
    tmp_list->prev = new_list;
    if (tmp_list == list) {
        return new_list;
    } else {
        return list;
    }
}

list_t *list_insert_sorted(list_t *list, void *data, comp_func_t func) {
    list_t *tmp_list;
    list_t *new_list;
    int cmp;

    assert(func);

    tmp_list = list;
    if (!list) {
        new_list = list_alloc();
        new_list->data = data;
        return new_list;
    }

    cmp = func(data, tmp_list->data);
    while ((tmp_list->next) && (cmp > 0)) {
        tmp_list = tmp_list->next;
        cmp = func(data, tmp_list->data);
    }
    new_list = list_alloc();
    new_list->data = data;
    if ((!tmp_list->next) && (cmp > 0)) {
        tmp_list->next = new_list;
        new_list->prev = tmp_list;
        return list;
    }

    if (tmp_list->prev) {
        tmp_list->prev->next = new_list;
        new_list->prev = tmp_list->prev;
    }
    new_list->next = tmp_list;
    tmp_list->prev = new_list;
    if (tmp_list == list) {
        return new_list;
    } else {
        return list;
    }
}

list_t *list_remove(list_t* list, void *data) {
    list_t *tmp_list;

    tmp_list = list;
    while (tmp_list) {
        if (tmp_list->data != data) {
            tmp_list = tmp_list->next;
        } else {
            if (tmp_list->prev) {
                tmp_list->prev->next = tmp_list->next;
            }
            if (tmp_list->next) {
                tmp_list->next->prev = tmp_list->prev;
            }
            if (list == tmp_list) {
                list = list->next;
            }
            /* You can also free memory of *data */
            list_free(tmp_list);
            break;
        }
    }
    return list;
}

list_t *list_remove_link(list_t* list, list_t *link) {
    if (link) {
        if (link->prev) {
            link->prev->next = link->next;
        }
        if (link->next) {
            link->next->prev = link->prev;
        }
        if (link == list) {
            list = list->next;
        }
        link->prev = NULL;
        link->next = NULL;
    }
    return list;
}

list_t *list_find(list_t *list, const void *data) {
    while (list) {
        if (list->data == data) {
            break;
        }
        list = list->next;
    }
    return list;
}

list_t *list_find_custom(list_t *list, const void *data, comp_func_t func) {
    assert(func);

    while (list) {
        if (!func(list->data, data)) {
            break;
        }
        list = list->next;
    }
    return list;
}

list_t *list_first(list_t *list) {
    if (list) {
        while (list->prev) {
            list = list->prev;
        }
    }
    return list;
}

list_t *list_last(list_t *list) {
    if (list) {
        while (list->next) {
            list = list->next;
        }
    }
    return list;
}

int list_length(list_t *list) {
    int len;

    len = 0;
    while (list) {
        len++;
        list = list->next;
    }
    return len;
}

void list_foreach(list_t *list, user_func_t func, void *user_data) {
    assert(func);

    while (list) {
        list_t *next = list->next;
        func(list->data, user_data);
        list = next;
    }
}

list_t *list_nth(list_t *list, int n) {
    while ((n-- > 0) && list) {
        list = list->next;
    }
    return list;
}

list_t *list_copy(list_t *list) {
    list_t *new_list;

    new_list = NULL;
    if (list) {
        list_t *last;
        new_list = list_alloc();
        new_list->data = list->data;
        new_list->prev = NULL;
        last = new_list;
        list = last->next;
        while (list) {
            last->next = list_alloc();
            last->next->prev = last;
            last = last->next;
            last->data = list->data;
            list = list->next;
        }
        last->next = NULL;
    }
    return new_list;
}

list_t *list_reverse(list_t *list) {
    list_t *last;

    last = NULL;
    while (list) {
        last = list;
        list = last->next;
        last->next = last->prev;
        last->prev = list;
    }
    return last;
}

static void list_insertion_sort_internal(
    list_t **list_start, list_t *list_end, comp_func_t func) {
    list_t *i, **j, *save;
    list_t *new_list;

    new_list = list_end;
    for (i = *list_start; i != list_end; i = save) {
        for (j = &new_list; *j != list_end; j = &((*j)->next)) {
            if (func((*j)->data, i->data) > 0) {
                break;
            }
        }
        save = i->next;
        /* insert before j */
        i->next = *j;
        *j = i;
    }
    *list_start = new_list;
}

list_t *list_insertion_sort(list_t *list, comp_func_t func) {
    list_insertion_sort_internal(&list, NULL, func);
    return list;
}

static void list_quick_sort_internal(
    list_t **list_start, list_t *list_end, int size, comp_func_t func) {
    const int diff = 10;
    int left_count, right_count, all_count;
    list_t *curr, *pivot, *right;

    right = NULL;
    all_count = size;
    while (all_count > 1) {
        list_t **left_walk, **right_walk;
        if (all_count < diff) {
            list_insertion_sort_internal(list_start, list_end, func);
            break;
        }
        left_count = right_count = 0;
        /* take first element as pivot */
        pivot = *list_start;
        /* set up left & right halves */
        left_walk = list_start;
        right_walk = &right;
        /* walk through the list */
        for (curr = (*list_start)->next; curr != list_end; curr = curr->next) {
            if (func(curr, pivot) < 0) {
                /* add to left */
                left_count += 1;
                *left_walk = curr;
                left_walk = &(curr->next);
            } else {
                /* add to right */
                right_count += 1;
                *right_walk = curr;
                right_walk = &(curr->next);
            }
        }
        /* glue the halves together */
        *left_walk = pivot; /* add pivot */
        pivot->next = right; /* add right list */
        *right_walk = list_end; /* terminate right list */
        /* only recurse on smaller half */
        if (left_count < right_count) {
            list_quick_sort_internal(list_start, pivot, left_count, func);
            list_start = &(pivot->next);
            all_count = right_count;
        } else {
            list_quick_sort_internal(&(pivot->next), list_end, right_count, func);
            list_end = pivot;
            all_count = left_count;
        }
    }
}

list_t *list_quick_sort(list_t *list, comp_func_t func) {
    int size = list_length(list);
    list_quick_sort_internal(&list, NULL, size, func);
    return list;
}
