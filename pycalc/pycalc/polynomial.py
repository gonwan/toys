from pycalc import term
# absolute import seems to have better support for single module debugging..
import copy
import functools
import sys

class Polynomial(object):
    
    def __init__(self, *terms):
        # sorted
        self.terms = list(terms)
        self._compact()

    '''
    def _compact(self):
        res = []
        for t in self.terms:
            i = 0
            ins = True
            while i < len(res):
                cp = term.Term.compare(t, res[i])
                if cp > 0:
                    break
                elif cp == 0:
                    ins = False
                    res[i].coeffient += t.coeffient
                    break
                i += 1
            if ins:
                res.insert(i, copy.deepcopy(t))
        # at least one term
        if len(res) == 0:
            res.append(term.Term())
        self.terms = res
    '''

    # run time of '(a+b+c+d+e)^10' changed from 4.5s to 0.25s by using this version of _compact()
    def _compact(self):
        if len(self) == 0:
            self.terms.append(term.Term())
            return
        if len(self) == 1:
            return
        if int(sys.version[0]) <= 2:
            self.terms.sort(cmp=term.Term.compare)
        else:
            self.terms.sort(key=functools.cmp_to_key(term.Term.compare))
        res = []
        curr = copy.deepcopy(self.terms[0])
        curr.coeffient = 0
        for t in self.terms:
            if term.Term.compare(curr, t) == 0:
                curr.coeffient += t.coeffient
            else:
                res.append(curr)
                curr = t
        res.append(curr)
        self.terms = res

    def __len__(self):
        return len(self.terms)

    def __add__(self, other):
        p = copy.deepcopy(self)
        p += other
        return p

    def __iadd__(self, other):
        self.terms.extend(copy.deepcopy(other.terms))
        self._compact()
        return self

    def __sub__(self, other):
        p = copy.deepcopy(self)
        p -= other
        return p

    def __isub__(self, other):
        ts = copy.deepcopy(other.terms)
        for t in ts:
            t.coeffient = -t.coeffient
        self.terms.extend(ts)
        self._compact()
        return self

    def __mul__(self, other):
        res = []
        for obj in self.terms:
            for obj2 in other.terms:
                t = obj * obj2
                res.append(t)
        p = Polynomial(*res)
        p._compact()
        return p

    def __div__(self, div):
        p = copy.deepcopy(self)
        for t in p.terms:
            t.coeffient /= float(div)
        return p

    def __idiv__(self, div):
        for t in self.terms:
            t.coeffient /= float(div)
        return self

    # for python3
    __truediv__ = __div__
    __itruediv__ = __idiv__

    def __pow__(self, exp):
        '''
        if exp == 0:
            return 1
        if exp == 1:
            return self
        # find highest
        high = 31
        while high > 0:
            if exp & (1 << high):
                break
            high -= 1
        # calculate
        res = Polynomial(term.Term(1))
        explist = [self]
        curr = 0
        while True:
            if exp & (1 << curr):
                res *= explist[curr]
            if curr >= high:
                break
            p = explist[curr] * explist[curr]
            explist.append(p)
            #print(p)
            curr += 1
        return res
        '''
        p = Polynomial(term.Term(1))
        while exp > 0:
            p *= self
            exp -= 1
        return p

    def __str__(self):
        res = ''
        for (i, t) in enumerate(self.terms):
            if i == 0:
                res += str(t)
            else:
                if t.coeffient > 0:
                    res += ' + ' + str(t)
                elif t.coeffient < 0:
                    res += ' - ' + str(t)[1:]
        return res

if __name__ == '__main__':
    # use:
    # python -m pycalc.polynomial
    # PYTHONPATH=. pycalc/polynomial.py
    def test():
        t = term.Term(3, a=2, b=4)
        t2 = term.Term(4, a=2, b=4)
        t3 = term.Term(3, z=7, a=3, b=4)
        p1 = Polynomial(t, t2)
        p2 = Polynomial(t2, t3)
        print(p1)
        print(p2)
        p3 = p1 + p2
        print(p3)
        print(len(p3))
    test()
