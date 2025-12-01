# Author = Makarov Aleksey Ivanovich
# Group = P3119
# Date = 03.11.2025

# 502587 % 5 = 2

import re

def do_the_thing(s):
    return re.sub(r'(-?)\b(\d+(,\d*)?\b)', lambda m: calc(m), s)

def calc(m):
    a = 5 * (float(m.group(0).replace(",", ".")) ** 3) - 13
    if a.is_integer():
        return str(int(a))
    return str(a)

tests = {
    "15 + 22 = 37 656 пао 7 9 апрвапрва567тсапроавп 1,1  7. 5.5 Ffghjgfhfg": "16862 + 53227 = 253252 1411502067 пао 1702 3632 апрвапрва911421302тсапроавп -8,-8  1702 818.875 Ffghjgfhfg",
    "54 - 34 = 20": "787307 - 196507 = 39987",
    "69 + 69 = 138": "1642532 + 1642532 = 13140347",
    "-5 + 5 = 0": "-638 + 612 = -13",
    "6 - 6 + 6 - 6 + 6 = 6": "1067 - 1067 + 1067 - 1067 + 1067 = 1067"
}

for k, v in tests.items():
    check = do_the_thing(k)
    print(f"Test: \"{k}\", expected \"{v}\", got \"{check}\"")
