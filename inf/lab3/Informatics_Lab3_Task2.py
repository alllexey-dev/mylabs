# Author = Makarov Aleksey Ivanovich
# Group = P3119
# Date = 03.11.2025

# 502587 % 5 = 2

import re

def do_the_thing(s):
    return re.sub(r'-?\d+', lambda m: str(5 * (int(m.group(0)) ** 3) - 13), s)

tests = {
    "15 + 22 = 37": "16862 + 53227 = 253252",
    "54 - 34 = 20": "787307 - 196507 = 39987",
    "69 + 69 = 138": "1642532 + 1642532 = 13140347",
    "-5 + 5 = 0": "-638 + 612 = -13",
    "6 - 6 + 6 - 6 + 6 = 6": "1067 - 1067 + 1067 - 1067 + 1067 = 1067"
}

for k, v in tests.items():
    check = do_the_thing(k)
    print(f"Test: \"{k}\", expected \"{v}\", got \"{check}\"")
