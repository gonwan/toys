#ifndef _LIST2_H
#define _LIST2_H


typedef struct _list_t {
    struct _list_t *prev;
    struct _list_t *next;
} list_t;


#define __LIST_HEAD_INIT(name) { &(name), &(name) }

#define LIST_HEAD(name) struct list_head name = __LIST_HEAD_INIT(name)

#define LIST_HEAD_INIT(ptr) do { \
    (ptr)->next = (ptr); (ptr)->prev = (ptr); \
} while (0)


static inline int list_empty(const list_t *head)
{
    list_t *next = head->next;
    return (next == head) && (next == head->prev);
}

static inline void __list_add(list_t *new, list_t *prev, list_t *next)
{
    new->next = next;
    new->prev = prev;
    next->prev = new;
    prev->next = new;
}

static inline void list_add(list_t *new, list_t *head)
{
    __list_add(new, head, head->next);
}

static inline void list_add_tail(list_t *new, list_t *head)
{
    __list_add(new, head->prev, head);
}

static inline void __list_del(list_t *prev, list_t *next)
{
    next->prev = prev;
    prev->next = next;
}

static inline void list_del(list_t *entry)
{
    __list_del(entry->prev, entry->next);
    entry->next = NULL;
    entry->prev = NULL;
}


#endif

