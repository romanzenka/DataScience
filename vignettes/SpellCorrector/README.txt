This example is a very simple project that uses python to do the auto spell correct that you see implemented in google every day. 

The insperation for this is from this page:
http://norvig.com/spell-correct.html

The best thing about this example is that it uses Math, but in a way that every programmer can understand.  It may not be completely correct (we can poke holes at it quite easily) but it shows a nice basis for how machine learning can be approached by a 'Hacker'

To do it correctly; look into this:
""" I’ve seen things similar to this, and I know it’s just a toy, but I really think he’s overanalyzing the problem instead of letting the learning algorithm do the heavy lifting. He’s only looking at the word level and not considering context – the difference between receipt and recite is handled by the neighboring words (whether they are nouns or verbs); there are properly spelled words that are grammatically incorrect that these approaches will never flag. Look at the split words case in the below monograph and how its solved: """
-Chris Ross 2015


