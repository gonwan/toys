import copy

class Term(object):

    def __init__(self, coe=0, **degs):
        self.coeffient = coe
        # degrees of indeterminates
        self.degrees = [0] * 26
        for (k, v) in degs.items():
            if k.islower():
                self.degrees[ord(k)-ord('a')] = v

    @staticmethod
    def compare(a, b):
        for i in range(len(a.degrees)):
            if a.degrees[i] > b.degrees[i]:
                return -1
            elif a.degrees[i] < b.degrees[i]:
                return 1
        return 0

    def __len__(self):
        return len(self.degrees) - self.degrees.count(0)

    def __mul__(self, other):
        coe = self.coeffient * other.coeffient
        degs = [self.degrees[i]+other.degrees[i] for i in range(len(self.degrees))]
        t = Term(coe)
        t.degrees = degs
        return t

    def __imul__(self, other):
        self.coeffient *= other.coeffient
        for (i, v) in enumerate(other.degrees):
            self.degrees[i] += v
        return self

    def __str__(self):
        # ''.join() not helps much here
        if self.coeffient == 0:
            return '0'
        if len(self) == 0:
            return str(self.coeffient)
        res = '' if self.coeffient > 0 else '-'
        res += '' if abs(self.coeffient) == 1 else str(abs(self.coeffient))
        for (i, v) in enumerate(self.degrees):
            if v != 0:
                res += chr(ord('a')+i)
                if v != 1:
                    res += '^' + str(v)
        return res

if __name__ == '__main__':
    # use:
    # python -m pycalc.term
    # PYTHONPATH=. pycalc/term.py
    def test():
        t = Term()
        print(t)
        t = Term(-3)
        print(t)
        t = Term(1, a=2, b=5)
        print(t)
        t = Term(-1, z=3, a=2)
        print(t)
        t = Term(0, z=3, a=2)
        print(t)
        t = Term(-12, z=3, a=2)
        print(t)
        t2 = copy.deepcopy(t)
        t2.degrees[1] = 5
        t2.degrees[2] = 7
        print('%s, %s' % (t, t2))
        t3 = t * t2
        print(t3)
        print(len(t3))
    test()
