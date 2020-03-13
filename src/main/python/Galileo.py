from capstone import *

filename = "libc.so.6"
data = open(filename, "rb")
binary = bytearray(data.read())

text_segment_start = 0x17750
text_segment_size = 0x12bacd
text_segment_end = text_segment_start + text_segment_size

text_segment_binary = binary[text_segment_start:text_segment_end + 1]

max_inst_len = 16


class TrieNode(object):
    def __init__(self):
        self.children = {}
        self.startsAt = -1


def galileo(raw_bytes):
    root = TrieNode()

    for pos in range(1, len(raw_bytes)):
        if raw_bytes[pos] == 0xC3:
            build_from(pos, root, "ret", "", raw_bytes)

    return root


disassembler = Cs(CS_ARCH_X86, CS_MODE_32 + CS_MODE_LITTLE_ENDIAN)

def build_from(pos, parent_node, parent_mnemonic, parent_op_str, raw_bytes):
    for step in range(1, max_inst_len):
        start = pos - step

        if start < 0:
            break

        cur_bytes = raw_bytes[start:pos]

        for (address, size, mnemonic, op_str) in disassembler.disasm_lite(bytes(cur_bytes), 0):
            if size == step:
                inst = "%s %s" % (mnemonic, op_str)

                if inst not in parent_node.children:
                    node = TrieNode()
                    node.startsAt = start
                    parent_node.children[inst] = node
                else:
                    node = parent_node.children[inst]

                if not is_boring(parent_mnemonic, parent_op_str, mnemonic, op_str):
                    build_from(start, node, mnemonic, op_str, raw_bytes)


def is_boring(parent_mnemonic, parent_op_str, mnemonic, op_str):
    if parent_mnemonic == "ret":
        if mnemonic == "leave" or (mnemonic == "pop" and op_str == "ebp"):
            return True

    return mnemonic == "ret" or mnemonic == "jmp"


def search_for(root, gadget):
    cur = root

    for inst in reversed(gadget):
        if inst in cur.children:
            cur = cur.children[inst]
        else:
            return None

    return "0x%x" % cur.startsAt


root = galileo(text_segment_binary)
print search_for(root, ["xor eax, eax"])
print search_for(root, ["pop ecx", "pop edx"])
print search_for(root, ["mov dword ptr [edx + 0x18], eax"])
print search_for(root, ["add al, ch"])
print search_for(root, ["pop ebx"])
print search_for(root, ["call dword ptr gs:[0x10]"])