import base64
import json
import os
import tempfile

import classifier
import picto_matcher

import web

urls = ('/post_capture', 'PostCapture')

db_file = './../synsets/database/synset.sqlite3'
db = web.database(dbn='sqlite', db=db_file)

cl = classifier.Classifier()

app = web.application(urls, globals())


class PostCapture(object):

    def POST(self):

        data = json.loads(web.data())

        wnid = data["wnid"]
        imagestring = data["capture"]

        fd,path = tempfile.mkstemp(suffix=".jpeg", prefix='pc_')

        with os.fdopen(fd, 'w') as f:
            f.write(base64.decodestring(imagestring))
            f.close()
        print(path)
        response = {'result': int(self.match(image_file=path, search_wnid=wnid))}
        return json.dumps(response)

    def match(self, image_file, search_wnid):

        global db
        global cl

        result_mtx = cl.classify(image_file)
        wnids = result_mtx[0]
        scores = result_mtx[1]

        thresh = 0.2
        matcher = picto_matcher.PictoMatcher(db=db, picto_wNid=search_wnid, thresh=thresh)
        result = matcher.match(wnids, scores)
        return result








if __name__ == '__main__':

    app.run()
