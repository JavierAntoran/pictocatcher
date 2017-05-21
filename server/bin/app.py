import web
import json
import tempfile
import base64
import os

urls = ('/post_capture', 'PostCapture')

db_file = './../../synsets/database/synset.sqlite3'
db = web.database(dbn = 'sqlite', db = db_file)

app = web.application(urls, globals())


class PostCapture(object):

    def POST(self):

        data = json.loads(web.data(), strict=False)

        wnid = data["wnid"]
        imagestring = data["capture"]

        fd,path = tempfile.mkstemp(suffix=".jpeg", prefix='pc_')

        with os.fdopen(fd, 'w') as f:
            f.write(base64.decodestring(imagestring))
            f.close()


        response = {'result': True}
        return json.dumps(response)




if __name__ == '__main__':
    app.run()
