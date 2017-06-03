import classifier
import picto_matcher
import web
import time


time0 = time.time()

db_file = './../synsets/database/synset.sqlite3'
db = web.database(dbn='sqlite', db=db_file)

image_file = './testfiles/cat.jpg'

search_wnid = 'n02121808'

time1 = time.time()
print '------files loaded: %(a)f seconds\n' % {"a":(time1 - time0)}

cl = classifier.Classifier()

time2 = time.time()
print '------clasifier instantiated: %(a)f seconds\n' % {"a":time2 - time1}

result_mtx = cl.classify(image_file)
wnids = result_mtx[0]
scores = result_mtx[1]

time3 = time.time()
print '\n------image classified: %(a)f seconds\n' % {"a":time3 - time2}

thresh = 0.2
matcher = picto_matcher.PictoMatcher(db=db, picto_wNid=search_wnid, thresh=thresh)
result = matcher.match(wnids, scores)

time4 = time.time()
print '------pictogram matched: %(a)f seconds\n' % {"a":time4 - time3}

print result

