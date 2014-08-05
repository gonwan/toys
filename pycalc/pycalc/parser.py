from pycalc import term, polynomial
# absolute import seems to have better support for single module debugging..
import platform
import sys
import timeit

if int(sys.version[0]) <= 2: input = raw_input

class Parser(object):

    ops = '()^*/+-'

    @staticmethod
    def run():
        print('pycalc v1.0 on %s (%s v%s)\n' 
              % (platform.system(), platform.python_implementation(), platform.python_version()))
        while True:
            line = input('> ')
            line = line.strip()
            if line == '':
                continue
            if line == 'QUIT' or line == 'quit':
                break
            t = timeit.Timer(lambda : Parser.calc(line))
            print('\n(%.4f seconds used)\n' % t.timeit(1))

    @staticmethod
    def calc(line):
        try:
            infix = Parser.str2infix(line)
            #print(infix)
            postfix = Parser.infix2postfix(infix)
            #print(postfix)
            res = Parser.postfix2result(postfix)
            print('= %s' % res)
        except Exception as e:
            print('ERROR: %s' % e.args)

    @staticmethod
    def priority(op):
        if op == '#':
            return 0
        elif op == '^':
            return 1
        elif op == '*' or op == '/':
            return 2
        elif op == '+' or op == '-':
            return 3
        else:
            return -1

    @staticmethod
    def str2infix(line):
        res = []
        i = 0
        if len(line) > 0 and line[0] == '-':
            line = '0' + line
        llen = len(line)
        while i < llen:
            if line[i] == ' ':
                pass
            elif line[i] in Parser.ops:
                res.append(line[i])
            elif line[i].islower():
                if (len(res) != 0) and (not res[-1] in Parser.ops):
                    res.append('*') # add mannually
                res.append(line[i])
            elif line[i].isdigit():
                num = 0
                while i < llen and line[i].isdigit():
                    num = num * 10 + int(line[i])
                    i += 1
                res.append(str(num))
                continue
            else:
                raise Exception('unexcepted character: %s' % line[i])
            i += 1
        return res

    @staticmethod
    def infix2postfix(infix):
        res = []
        opstack = []
        for obj in infix:
            if obj in Parser.ops:
                if obj == '(':
                    opstack.append(obj)
                elif obj == ')':
                    while len(opstack) != 0:
                        t = opstack.pop()
                        if t == '(':
                            break
                        res.append(t)
                    else:
                        raise Exception('cannot find matching \'(\'' )
                else:
                    while len(opstack) != 0:
                        t = opstack[-1]
                        if t == '(':
                            break
                        if Parser.priority(obj) < Parser.priority(t):
                            break
                        res.append(t)
                        opstack.pop()
                    opstack.append(obj)
            else: # not ops
                res.append(obj)
        # add remaining
        while len(opstack) != 0:
            t = opstack.pop()
            if t == '(':
                break
            res.append(t)
        return res

    @staticmethod
    def postfix2result(postfix):
        valstack= []
        for obj in postfix:
            if obj in Parser.ops:
                if len(valstack) < 2:
                    raise Exception('missing operands with \'%s\'' % obj)
                v1 = valstack.pop()
                v2 = valstack.pop()
                v = polynomial.Polynomial(term.Term())
                if obj == '^':
                    if len(v1) != 1 or len(v1.terms[0]) != 0:
                        raise Exception('non-numeric value used in exponent: %s' % v1)
                    coe = v1.terms[0].coeffient
                    if coe < 0:
                        raise Exception('negtive value used in exponent: %s' % coe)
                    v = v2 ** coe
                elif obj == '*':
                    v2 *= v1
                    v = v2
                elif obj == '/':
                    if len(v1) != 1 or len(v1.terms[0]) != 0:
                        raise Exception('non-numeric value used in divide: %s' % v1)
                    coe = v1.terms[0].coeffient
                    v2 /= coe
                    v = v2
                elif obj == '+':
                    v2 += v1
                    v = v2
                elif obj == '-':
                    v2 -= v1
                    v = v2
                valstack.append(v)
            elif obj.islower():
                t = term.Term(1, **{obj:1})
                valstack.append(polynomial.Polynomial(t))
            elif obj.isdigit():
                t = term.Term(int(obj))
                valstack.append(polynomial.Polynomial(t))
        # return
        if len(valstack) != 1:
            raise Exception('cannot calculate polynomial due to incorrect input')
        return valstack.pop()

if __name__ == '__main__':
    # use:
    # python -m pycalc.parser
    # PYTHONPATH=. pycalc/parser.py
    Parser.run()
    #t = timeit.Timer(Parser.run, 'from __main__ import Parser')
    #print(t.timeit(1))
