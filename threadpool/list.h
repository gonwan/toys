#ifndef _LIST_H
#define _LIST_H


typedef struct _list_t {
    struct _list_t *prev;
    struct _list_t *next;
} list_t;


#define LIST_INIT(ptr) do { \
    (ptr)->next = (ptr); (ptr)->prev = (ptr); \
} while (0)


static int list_empty(const list_t *head)
{
    list_t *next = head->next;
    return (next == head) && (next == head->prev);
}

static void __list_add(list_t *entry, list_t *prev, list_t *next)
{
    entry->next = next;
    entry->prev = prev;
    next->prev = entry;
    prev->next = entry;
}

static void list_add(list_t *entry, list_t *head)
{
    __list_add(entry, head, head->next);
}

static void list_add_tail(list_t *entry, list_t *head)
{
    __list_add(entry, head->prev, head);
}

static void __list_del(list_t *prev, list_t *next)
{
    next->prev = prev;
    prev->next = next;
}

static void list_del(list_t *entry)
{
    __list_del(entry->prev, entry->next);
    entry->next = NULL;
    entry->prev = NULL;
}


#endif
