import web
from web.contrib.template import render_jinja
import glob



urls = (
  '/', 'Index'
)

db_file = './database/synset.sqlite3'

db = web.database(dbn='sqlite', db=db_file)

class PictoMatcher(object):

    def __init__(self, ):
        pass

    def match(self, picto, result_id, result_weights):
        pass



