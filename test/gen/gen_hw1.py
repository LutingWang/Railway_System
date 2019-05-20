import random

def path_add(nodes):
    s = "PATH_ADD"
    for i in nodes:
        s += " " + str(i)
    return s

def path_remove(nodes):
    s = "PATH_REMOVE"
    for i in nodes:
        s += " " + str(i)
    return s

def path_remove_by_id(id):
    s = "PATH_REMOVE_BY_ID " + str(id)
    return s

def path_get_id(nodes):
    s = "PATH_GET_ID"
    for i in nodes:
        s += " " + str(i)
    return s

def path_get_by_id(id):
    s = "PATH_GET_BY_ID " + str(id)
    return s

def path_count():
    s = "PATH_COUNT"
    return s

def path_size(id):
    s = "PATH_SIZE " + str(id)
    return s

def path_distinct_node_count(id):
    s = "PATH_DISTINCT_NODE_COUNT " + str(id)
    return s

def contains_path(nodes):
    s = "CONTAINS_PATH"
    for i in nodes:
        s += " " + str(i)
    return s

def contains_path_id(id):
    s = "CONTAINS_PATH_ID " + str(id)
    return s

def distinct_node_count():
    s = "DISTINCT_NODE_COUNT"
    return s

def compare_paths(id1, id2):
    s = "COMPARE_PATHS " + str(id1) + " " + str(id2)
    return s

def path_contains_node(id, myNode):
    s = "PATH_CONTAINS_NODE " + str(id) + " " + str(myNode)
    return s

def gen_cor_path():
    i = random.randint(0, 299) * 1000
    global hash_base
    s = list(hash_base)
    s += [j for j in range(i+2, i+1000)]
    return s

def gen_wro_path():
    case = random.randint(0,2)
    if case == 0: # null
        return []
    if case == 1: # invalid
        return [2]
    if case == 2: # non-exist
        return [1, 2]

def gen_cor_id():
    return random.randint(1, 300)

def gen_wro_id():
    return 500

def gen_cor_node(id):
    ind = random.randint(0, 999)
    if ind >= 2:
        ind += (id-1) * 1000
    return ind

def gen_wro_node():
    return 1001

with open("./cases.txt", 'w') as f:
    i = 0
    hash_base = [0, 1]
    funcs = [path_add, path_remove, path_remove_by_id, path_get_id, path_get_by_id, path_count,
             path_size, path_distinct_node_count, contains_path, contains_path_id, distinct_node_count, 
             compare_paths, path_contains_node]
    for n_add in range(300):
        temp = hash_base + [j for j in range(i+2, i+1000)]
        f.write(path_add(temp))
        i += 1000
        f.write('\n')
        f.write(distinct_node_count())
        f.write('\n')
        f.write(path_distinct_node_count(n_add))
        f.write('\n')
    for n_rand in range(10000):
        command_type = random.randint(1, len(funcs))
        cor = (random.randint(0, 1) == 1)
        if command_type in [1, 3, 8]: # nodes
            if cor:
                s = funcs[command_type](gen_cor_path())
            else:
                s = funcs[command_type](gen_wro_path())
        elif command_type in [2, 4, 6, 7, 9]: # id
            if cor:
                s = funcs[command_type](gen_cor_id())
            else:
                s = funcs[command_type](gen_wro_id())
        elif command_type in [5, 10]: # non
            s = funcs[command_type]()
        elif command_type == 11:
            if cor:
                s = funcs[command_type](gen_cor_id(), gen_cor_id())
            else:
                s = funcs[command_type](gen_wro_id(), gen_wro_id())
        elif command_type == 12:
            if cor:
                id = gen_cor_id()
                s = funcs[command_type](id, gen_cor_node(id))
            else:
                s = funcs[command_type](gen_wro_id(), gen_wro_node())
        f.write(s)
        f.write('\n')
