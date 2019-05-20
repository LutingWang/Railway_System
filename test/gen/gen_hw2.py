import random


def get_add_instruction():
    node_count = random.randint(150, 200)
    path = ""
    for _ in range(node_count):
        myNode = random.randint(0, 249)
        path += myNode.__str__() + " "
    return path


# 0
one_path_arg_instruction = [
    "PATH_GET_ID ",
    "CONTAINS_PATH "
]

# 1
one_pathId_arg_instruction = [
    "PATH_GET_BY_ID ",
    "PATH_SIZE ",
    "CONTAINS_PATH_ID ",
    "PATH_DISTINCT_NODE_COUNT "
]

# 2
two_pathId_arg_instruction = [
    "COMPARE_PATHS "
]

# 3
one_pathId_and_one_nodeId_arg_instruction = [
    "PATH_CONTAINS_NODE "
]

# 4
ond_nodeId_arg_instruction = [
    "CONTAINS_NODE "
]

# 5
two_nodeId_arg_instruction = [
    "CONTAINS_EDGE ",
    "IS_NODE_CONNECTED ",
    "SHORTEST_PATH_LENGTH "
]

# 6
no_arg_basic_instruction = [
    "PATH_COUNT",
    "DISTINCT_NODE_COUNT"
]

# linear
linear_instruction = [
    "PATH_ADD",
    "PATH_REMOVE",
    "PATH_REMOVE_BY_ID",
    "PATH_GET_ID",
    "PATH_GET_BY_ID",
    "CONTAINS_PATH ",
    "COMPARE_PATHS "
]

def getInstr(type):
    instr = random.choice(type)
    if instr in linear_instruction:
        getInstr.linear += 1
        if getInstr.linear >= 500:
            return None
    return instr



if __name__ == "__main__":
    getInstr.linear = 0
    with open("test.txt", "w") as file:
        id_set = set()
        id_count = 1

        # init six path
        for i in range(6):
            path = get_add_instruction()
            file.writelines("PATH_ADD " + path + "\n")
            id_set.add(id_count)
            id_count += 1

        # do 500 times query and do once change, so 20 times change will be done
        for i in range(7000 - 6):
            # do change
            if i % 500 == 0:
                choose = random.randint(0, 1)
                # add
                if choose == 0:
                    path = get_add_instruction()
                    file.writelines("PATH_ADD " + path + "\n")
                    id_set.add(id_count)
                    id_count += 1
                # remove
                else:
                    id_list = list(id_set)
                    path_id = random.choice(id_list)
                    id_set.remove(path_id)
                    path_id = str(path_id)
                    file.writelines("PATH_REMOVE_BY_ID " + path_id + "\n")
            # do check
            else:
                choose = random.randint(1, 20)
                if choose in range(1,4) and len(id_set) == 0:
                    exit(0)
                id_list = list(id_set)
                if choose == 1:
                    instruction = getInstr(one_pathId_arg_instruction)
                    if instruction is None:
                        continue
                    path_id = random.choice(id_list)
                    path_id = str(path_id)
                    file.writelines(instruction + path_id + "\n")
                if choose == 2:
                    instruction = getInstr(two_pathId_arg_instruction)
                    if instruction is None:
                        continue
                    path_id1 = random.choice(id_list)
                    path_id2 = random.choice(id_list)
                    path_id1 = str(path_id1)
                    path_id2 = str(path_id2)
                    file.writelines(instruction + path_id1 + " " + path_id2 + "\n")
                if choose == 3:
                    instruction = getInstr(one_pathId_and_one_nodeId_arg_instruction)
                    if instruction is None:
                        continue
                    path_id = random.choice(id_list)
                    path_id = str(path_id)
                    node_id = random.randint(0, 249)
                    node_id = str(node_id)
                    file.writelines(instruction + path_id + " " + node_id + "\n")
                if choose == 4:
                    instruction = getInstr(ond_nodeId_arg_instruction)
                    if instruction is None:
                        continue
                    node_id = random.randint(0, 249)
                    node_id = str(node_id)
                    file.writelines(instruction + node_id + "\n")
                if choose == 5 or choose > 6:
                    instruction = getInstr(two_nodeId_arg_instruction)
                    if instruction is None:
                        continue
                    node_id1 = random.randint(0, 249)
                    node_id2 = random.randint(0, 249)
                    node_id1 = str(node_id1)
                    node_id2 = str(node_id2)
                    file.writelines(instruction + node_id1 + " " + node_id2 + "\n")
                if choose == 6:
                    instruction = getInstr(no_arg_basic_instruction)
                    if instruction is None:
                        continue
                    file.writelines(instruction + "\n")

        exit(0)

