#!/usr/bin/env python

from __future__ import print_function
import re, sys, operator

# Evita Bakopoulou, UCInet: ebakopou


# Mileage may vary. If this crashes, make it lower
RECURSION_LIMIT = 9500
# We add a few more, because, contrary to the name,
# this doesn't just rule recursion: it rules the 
# depth of the call stack
sys.setrecursionlimit(RECURSION_LIMIT+10)

Y = (lambda h: lambda F: F(lambda x: h(h)(F)(x)))(lambda h: lambda F: F(lambda x: h(h)(F)(x)))

stop_words = set(open('../stop_words.txt').read().split(','))
words = re.findall('[a-z]{2,}', open(sys.argv[1]).read().lower())
words = [w for w in words if w not in stop_words] #this makes the lambda a bit faster, with two arguments instead of three
word_freqs = {}

wf_count = lambda f: lambda l: lambda d: (None if not l else ( f(l[1:])(d) if not\
    (d.update({l[0]: d[l[0]] + 1}) if l[0] in d else d.update({l[0]: 1})) else d ))
    
# Theoretically, we would just call count(words, word_freqs)
# Try doing that and see what happens.
for i in range(0, len(words), RECURSION_LIMIT):
    Y(wf_count)(words[i:i+RECURSION_LIMIT])(word_freqs)

word_list = sorted(word_freqs.iteritems(), key=operator.itemgetter(1), reverse=True)[:25]

wf_print = lambda f: lambda l: ( None if not l else ( f(l[1:]) if not print(l[0][0] + " - " + str(l[0][1])) else None ) )
Y(wf_print)(word_list)                        
