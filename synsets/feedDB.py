import sqlite3
import os
import re
import xml.etree.ElementTree as ET


arasac_file = "./arasac_files.txt"
imagenet_struct = "./imagenet_synset_structure.xml"

def main():
    global cursor

    con = sqlite3.connect('./synset.sqlite')

    cursor = con.cursor()

    print "Creating database ..."

    cursor.execute('''DROP TABLE IF EXISTS arasac_synset''')
    cursor.execute('''DROP TABLE IF EXISTS imagenet_synset''')
    cursor.execute('''DROP TABLE IF EXISTS synset_matches''')

    cursor.execute('''
            CREATE TABLE arasac_synset (id INTEGER PRIMARY KEY, word TEXT UNIQUE ON CONFLICT IGNORE)
    ''')

    cursor.execute('''
            CREATE TABLE imagenet_synset (id INTEGER PRIMARY KEY, wnid TEXT, pnid TEXT, word TEXT)
    ''')

    cursor.execute('''
            CREATE TABLE synset_matches(imgnet_id INTEGER, arasac_id INTEGER)
    ''')

    print("Import arasac_synset ...")
    # Import arasac synset
    f = open(arasac_file)

    for filename in iter(f):
        word = os.path.splitext(filename)[0]
        word = re.sub("_\d+$", "", word).lower()
        cursor.execute('INSERT INTO arasac_synset (word) VALUES ("%s")' % word)
    f.close()

    cursor.execute("SELECT * FROM arasac_synset")

    print "Import arasac_synset finished"

    print("Import imagenet synset ...")
    # Import imagenet synset
    root = ET.parse(imagenet_struct).getroot()
    begin = root.findall("./synset")

    for child in begin:
        synset_parse(child, None)

    print "Import imagenet_synset finished"

    con.commit()
    cursor.close()
    con.close()

def synset_parse(ele, pnid):

    words = [word.strip().lower() for word in ele.get('words').split(",")]
    wnid = ele.get('wnid')

    for word in words:
        cursor.execute(
            'INSERT INTO imagenet_synset (wnid, pnid, word) VALUES ("%s", %s, "%s")'
            % (wnid, ('"%s"' % pnid) if pnid else "NULL", word))

    children = ele.findall("./synset")

    if len(children):
        for child in children:
            synset_parse(child, wnid)

if __name__ == "__main__":
    main()












