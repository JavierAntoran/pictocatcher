import sqlite3
import os
import re
import xml.etree.ElementTree as ET


arasac_file = "./data/arasac_files.txt"
imagenet_struct = "./data/imagenet_synset_structure.xml"

def main():
    global con

    con = sqlite3.connect('./database/synset.sqlite3')

    import_synsets()

    synset_matcher()

    con.close()

def synset_matcher():
    ara_cursor = con.cursor()
    imgnet_cursor = con.cursor()
    write_cursor = con.cursor()

    ara_cursor.execute("SELECT * FROM arasac_synset")
    print "Begin synset matching..."

    for ara_id, ara_word in ara_cursor:
        imgnet_cursor.execute("SELECT id, word FROM imagenet_synset WHERE word LIKE ?", (ara_word,))
        for imgnet_id, imgnet_word in imgnet_cursor:
            write_cursor.execute(
                'INSERT INTO synset_matches (arasac_id, imgnet_id) VALUES (%d, %d)'
                % (ara_id, imgnet_id))
        print('Matching arasac synset %d:"%s"' % (ara_id, ara_word))

    con.commit()
    print "Synset matching finished"
    write_cursor.close()
    imgnet_cursor.close()
    ara_cursor.close()

def import_synsets():
    cursor = con.cursor()

    print "Creating database ..."

    cursor.execute('''DROP TABLE IF EXISTS arasac_synset''')
    cursor.execute('''DROP TABLE IF EXISTS imagenet_synset''')
    cursor.execute('''DROP TABLE IF EXISTS synset_matches''')

    cursor.execute('''
                CREATE TABLE arasac_synset (id INTEGER PRIMARY KEY, word TEXT UNIQUE ON CONFLICT IGNORE)
        ''')

    cursor.execute('''
                CREATE TABLE imagenet_synset (id INTEGER PRIMARY KEY, wnid TEXT, pnid TEXT, word TEXT, 
                CONSTRAINT wnid_word UNIQUE (wnid, pnid) ON CONFLICT IGNORE)
        ''')

    cursor.execute('''
                CREATE TABLE synset_matches(arasac_id INTEGER, imgnet_id INTEGER)
        ''')

    print('Import arasac_synset ...')
    # Import arasac synset
    f = open(arasac_file)

    for filename in iter(f):
        word = os.path.splitext(filename)[0]
        word = re.sub("_\d+$", "", word).lower()
        cursor.execute('INSERT INTO arasac_synset (word) VALUES ("%s")' % word)
    f.close()

    cursor.execute("SELECT * FROM arasac_synset")

    print('Import arasac_synset finished')

    print('Import imagenet_synset ...')
    # Import imagenet synset
    root = ET.parse(imagenet_struct).getroot()
    begin = root.findall("./synset")

    for child in begin:
        synset_parse(child, None)

    print('Import imagenet_synset finished')

    con.commit()
    cursor.close()

def synset_parse(ele, pnid):
    cursor = con.cursor()
    words = [word.strip().lower() for word in ele.get('words').split(",")]
    wnid = ele.get('wnid')

    for word in words:
        cursor.execute(
            'INSERT INTO imagenet_synset (wnid, pnid, word) VALUES ("%s", %s, "%s")'
            % (wnid, ('"%s"' % pnid) if pnid else "NULL", word))

    cursor.close()

    children = ele.findall("./synset")

    if len(children):
        for child in children:
            synset_parse(child, wnid)

if __name__ == "__main__":
    main()












