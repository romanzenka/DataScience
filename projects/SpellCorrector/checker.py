import re, collections

def words(text) : return re.findall('[a-z]+', text.lower())

def train(features):
	model = collections.defaultdict(lambda: 1)
	for f in features:
		model[f] += 1
	return model

bigtxt = file('big.txt')
w = words(bigtxt.read())
#the frequency of the letters in known txt
NWORDS = train(w)

alphabet = 'abcdefghijklmnopqrstuvwxyz'

#poor man's way of calculating all words one edit distance away from input 'word'
def edits1(word):
    splits     = [(word[:i], word[i:]) for i in range(len(word) + 1)]
    deletes    = [a + b[1:] for a, b in splits if b]
    transposes = [a + b[1] + b[0] + b[2:] for a, b in splits if len(b)>1]
    replaces   = [a + c + b[1:] for a, b in splits for c in alphabet if b]
    inserts    = [a + c + b     for a, b in splits for c in alphabet]
    return set(deletes + transposes + replaces + inserts)

#use recursion to calculate edit distances 2 away.  this makes up 98.9% of all misspellings  edits of one are only 76% of all misspellings
def edits2(word):
    return set(e2 for e1 in edits1(word) for e2 in edits1(e1))

#trim the list of spell corection options so that they are all real words
def known_edits2(word):
    return set(e2 for e1 in edits1(word) for e2 in edits1(e1) if e2 in NWORDS)

#simple function that tells us if the word 'w' is in our dictionary of known words
def known(words): return set(w for w in words if w in NWORDS)

def correct(word):
    print "**********************"
    print "is the word in the dictionary? " + str(known([word]))
    print "words one edit distance away in the dictionary" + str(known(edits1(word)))
    print "words two edit distances away in the dictionary" + str(known_edits2(word))
    print [word]
    candidates = known([word]) or known(edits1(word)) or known_edits2(word) or [word]
    print "candidates: " + str(candidates)
	#
    return max(candidates, key=NWORDS.get) #This will return the value of 1 if the word is not there, so for unknown words, it will get a value of 1

#example usage
assert "candidates" == correct("canidates")
assert "spelling" == correct("spelling")
assert "corrector" == correct("correcter")


#for word in NWORDS:
#	print word + ":" + str(NWORDS[word])

