import web
import numpy as np
import glob



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
            pnid = next_id

            # print('testing wnid: %s\n', next_id)

            while pnid != self.picto_wNid and pnid is not None:

                matches = self.db.query('''
                                    SELECT pnid FROM imagenet_synset WHERE wnid = $wnid''', vars={'wnid': next_id})

                pnids = set() #structure without duplicates
                for match in matches:
                    pnids.add(match.pnid)

                if len(pnids) != 1:
                    print 'Multiple parents or no parents error: selecting first match'

                pnid = pnids.pop()
                next_id = pnid

            if pnid is not None:  # we have found a match
                prob += result_weights[id]
                # print('match found wnid = %s\n', pnid)

                # testing: probability correction
                # if 0 < id < 4 and result_weights[0] > 0.35:
                    # correcting_factor = 0.75/(1-result_weights[0])
                    # prob -= result_weights[id]
                    # prob += result_weights[id] * correcting_factor

        print ('total %: %d', prob)


        return prob >= self.thresh








