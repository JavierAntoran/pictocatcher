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
        pass

    def match(self, result_wNid, result_weights):

        N_results = len(result_wNid)
        prob = 0

        for n_id, id in result_wNid:

            next_id = id
            while pnid != self.pictowNid and pnid is not None:

                matches = self.db.query('''
                                    SELECT pnid FROM imagenet_synset WHERE wnid = $id''', vars={'id': next_id})
                pnid = np.unique(matches)

                if len(pnid) != 1:
                    print 'Multiple parents or no parents error: selecting first match'
                    pnid = pnid[0]
                next_id = pnid

            if pnid is not None:
                prob += result_weights[n_id]

        return prob >= self.thresh








