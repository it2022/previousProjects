# ones = [1 for i in ["hi", "bye", "you"]]
# print(ones + [str(i) for i in [6, 3, 8, 4]])

def g_iter(n):
    """Return the value of G(n), computed iteratively.

    >>> g_iter(1)
    1
    >>> g_iter(2)
    2
    >>> g_iter(3)
    3
    >>> g_iter(4)
    10
    >>> g_iter(5)
    22
    >>> from construct_check import check
    >>> check(HW_SOURCE_FILE, 'g_iter', ['Recursion'])
    True
    """
    total, i, z = 1,0,0
    while i <= n:
        while i <= 3:
            total = total + i
            i += 1
        else:
            total, z = total + i, total
        i += 1
    return total

print(g_iter(5))