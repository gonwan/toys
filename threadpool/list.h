#ifndef _LIST_H_
#define _LIST_H_


typedef struct _list_t {
    struct _list_t *prev;
    struct _list_t *next;
    void *data;
} list_t;


typedef void (*user_func_t)(void *data, void *user_data);
typedef int (*comp_func_t)(const void *data1, const void *data2);


list_t *list_alloc();

void list_free(list_t *list);

void list_free_all(list_t *list);

list_t *list_append(list_t *list, void *data);

list_t *list_prepend(list_t *list, void *data);

list_t *list_insert(list_t *list, void *data, int pos);

list_t *list_insert_sorted(list_t *list, void *data, comp_func_t func);

list_t *list_remove(list_t* list, void *data);

list_t *list_remove_link(list_t* list, list_t *link);

list_t *list_find(list_t *list, const void *data);

list_t *list_find_custom(list_t *list, const void *data, comp_func_t func);

list_t *list_first(list_t *list);

list_t *list_last(list_t *list);

int list_length(list_t *list);

void list_foreach(list_t *list, user_func_t func, void *user_data);

list_t *list_nth(list_t *list, int n);

list_t *list_copy(list_t *list);

list_t *list_reverse(list_t *list);

list_t *list_insertion_sort(list_t *list, comp_func_t func);

list_t *list_quick_sort(list_t *list, comp_func_t func);


#endif /* _LIST_H_ */
