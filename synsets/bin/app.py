import web
import glob, os


urls = (
  '/', 'Index'
)

db_file = './database/synset.sqlite3'

db = web.database(dbn='sqlite', db=db_file)

arasaac_pics = './static/arasac_en_colored_pictograms'

app = web.application(urls, globals())
render = web.template.render('templates/', base='layout')

class Index:
    def GET(self):
        n_items = int(web.input(n_items=25).n_items)
        page = int(web.input(page=1).page)
        n_matches = db.query("SELECT COUNT(*) as n_matches FROM synset_matches")[0].n_matches

        matches = db.query('''
            SELECT ars.id, ars.word, GROUP_CONCAT(imgnets.wnid) as imgnet_wnids FROM synset_matches AS sm
            LEFT JOIN arasac_synset as ars ON sm.arasac_id = ars.id
            LEFT JOIN imagenet_synset as imgnets ON sm.imgnet_id = imgnets.id
            GROUP BY ars.id
            LIMIT $offset, $limit
        ''', vars={'offset': n_items*(page-1), 'limit': n_items})

        match_list = []

        for match in matches:
            match_info = {}

            match_info['word_id'] = match.id
            match_info['files'] = glob.glob("{imgroot}/{filename}.png".format(imgroot=arasaac_pics, filename=match.word)) + \
                          glob.glob("{imgroot}/{filename}_[0-9].png".format(imgroot=arasaac_pics, filename=match.word))

            match_info['imagenet'] = []
            for imgnet_wnid in list(set(match.imgnet_wnids.split(','))):
                match_info['imagenet'].append(imagenet_synset_traversing(imgnet_wnid))

            match_list.append(match_info)

        return render.index(match_list=match_list)

def imagenet_synset_traversing(imagenet_wnid):

    synset_info = {}
    synset_info['wnid'] = imagenet_wnid

    synset = db.query('''
        SELECT 
          GROUP_CONCAT(id)   as ids, 
          GROUP_CONCAT(wnid) as wnids, 
          GROUP_CONCAT(word) as words, 
          GROUP_CONCAT(pnid) as pnids 
        FROM imagenet_synset WHERE wnid = $wnid GROUP BY wnid
      ''', vars={'wnid': imagenet_wnid})[0]

    if synset.pnids is None:
        parent = None
    else:
        parent = list(set(synset.pnids.split(',')))
        if len(parent) > 1:
            synset_info['warning'] = "Found {} parents for {}".format(len(parent), imagenet_wnid)
            parent = parent[0]

    synset_info['words'] = synset.words

    if parent is not None:
        synset_info['parent'] = imagenet_synset_traversing(parent)
    else:
        synset_info['parent'] = None

    return synset_info


if __name__ == "__main__":
    app.run()