import web
import numpy as np
from web.contrib.template import render_jinja
import glob



urls = (
  '/', 'Index'
)

db_file = './../synsets/database/synset.sqlite3'

dbi = web.database(dbn='sqlite', db=db_file)


class PictoMatcher(object):

    def __init__(self, db, picto_wNid, thresh):
        self.db = db
        self.picto_wNid = picto_wNid
        self. thresh = thresh
        self.db.printing = False

    def match(self, result_wNid, result_weights):

        prob = 0

        for id in result_wNid:

            next_id = result_wNid[id]
            pnid = 0
            while pnid != self.picto_wNid and pnid is not None:

                matches = self.db.query('''
                                    SELECT pnid FROM imagenet_synset WHERE wnid = $wnid''', vars={'wnid': next_id})

                pnids = set()
                for match in matches:
                    pnids.add(match.pnid)

                if len(pnids) != 1:
                    print 'Multiple parents or no parents error: selecting first match'

                pnid = pnids.pop()
                next_id = pnid

            if pnid is not None:
                prob += result_weights[id]

        return prob >= self.thresh








