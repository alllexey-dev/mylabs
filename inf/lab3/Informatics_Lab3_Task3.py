# Author = Makarov Aleksey Ivanovich
# Group = P3119
# Date = 03.11.2025

# 502587 % 3 = 0

import re

def check_cron(cron):
    min_pattern = r"(\*|([0-5]?\d)(-[0-5]?\d)?|([0-5]?\d)(,([0-5]?\d))+)(/[0-5]?\d)?"
    hours_pattern = r"(\*|([01]?\d|2[0-3])(-([01]?\d|2[0-3]))?|([01]?\d|2[0-3])(,([01]?\d|2[0-3]))+)(/([01]?\d|2[0-3]))?"
    day_month_pattern = r"(\*|([1-9]|[12]\d|3[01])(-([1-9]|[12]\d|3[01]))?|([1-9]|[12]\d|3[01])(,([1-9]|[12]\d|3[01]))+)(/([1-9]|[12]\d|3[01]))?"
    month_pattern = r"(\*|([1-9]|1[0-2])(-([1-9]|1[0-2]))?|([1-9]|1[0-2])(,([1-9]|1[0-2]))+)(/([1-9]|1[0-2]))?"
    day_week_pattern = r"(\*|([0-6])(-[0-6])?|([0-6])(,[0-6])+)(/[0-6])?"

    cron_regex = re.compile(fr"^\s*{min_pattern}\s+{hours_pattern}\s+{day_month_pattern}\s+{month_pattern}\s+{day_week_pattern}\s*$")

    if cron_regex.match(cron):
        return check_ranges(cron)
    else:
        return False

def check_ranges(cron):
    split = cron.split(" ")

    for s in split:
        ranges = re.findall(r'(\d+)-(\d+)', s)
        for r in ranges:
            start, end = int(r[0]), int(r[1])
            if start > end:
                return False
    return True

tests = {
    "*/15 0 1,15 * 1-5": True,
    "30 14 * * *": True,
    "0 0 1 1 *": True,
    "0 0 * * 0": True,
    "5,10,15 8-10 * * 1,2,3": True,
    "60 12 * * *": False,
    "0 24 * * *": False,
    "0 0 * a *": False,
    "0 0 * 13 *": False,
    "* * * * -5": False,
    "0 0,12 1 */2 5-6": False
}

for k, v in tests.items():
    check = check_cron(k)
    print(f"Test: \"{k}\", expected \"{v}\", got \"{check}\"")


