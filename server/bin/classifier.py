# image classifier class

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import node_lookup
import picto_matcher

import argparse
import os.path
import sys
import tarfile

import web
import numpy as np
from six.moves import urllib
import tensorflow as tf

FLAGS = None

DATA_URL = 'http://download.tensorflow.org/models/image/imagenet/inception-2015-12-05.tgz'

class Map(dict):
    """
    Example:
    m = Map({'first_name': 'Eduardo'}, last_name='Pool', age=24, sports=['Soccer'])
    """
    def __init__(self, *args, **kwargs):
        super(Map, self).__init__(*args, **kwargs)
        for arg in args:
            if isinstance(arg, dict):
                for k, v in arg.iteritems():
                    self[k] = v

        if kwargs:
            for k, v in kwargs.iteritems():
                self[k] = v

    def __getattr__(self, attr):
        return self.get(attr)

    def __setattr__(self, key, value):
        self.__setitem__(key, value)

    def __setitem__(self, key, value):
        super(Map, self).__setitem__(key, value)
        self.__dict__.update({key: value})

    def __delattr__(self, item):
        self.__delitem__(item)

    def __delitem__(self, key):
        super(Map, self).__delitem__(key)
        del self.__dict__[key]

class Classifier(object):

    def __init__(self):

        global FLAGS


        if FLAGS is None:
            FLAGS = Map()
            FLAGS.num_top_predictions = 20

        FLAGS.model_dir = './bin/tensorflow/model'





    def create_graph(self):
        global FLAGS
        """Creates a graph from saved GraphDef file and returns a saver."""
        # Creates graph from saved graph_def.pb.
        with tf.gfile.FastGFile(os.path.join(
                FLAGS.model_dir, 'classify_image_graph_def.pb'), 'rb') as f:
            graph_def = tf.GraphDef()
            graph_def.ParseFromString(f.read())
            _ = tf.import_graph_def(graph_def, name='')

    def run_inference_on_image(self, image):

        global FLAGS
        """Runs inference on an image.
  
        Args:
          image: Image file name.
  
        Returns:
          Nothing
        """
        if not tf.gfile.Exists(image):
            tf.logging.fatal('File does not exist %s', image)
        Classifier.image_data = tf.gfile.FastGFile(image, 'rb').read()
        self.create_graph()
        with tf.Session() as sess:

            # Runs the softmax tensor by feeding the image_data as input to the graph.
            softmax_tensor = sess.graph.get_tensor_by_name('softmax:0')
            predictions = sess.run(softmax_tensor,
                                   {'DecodeJpeg/contents:0': Classifier.image_data})
            predictions = np.squeeze(predictions)

            top_k = predictions.argsort()[-FLAGS.num_top_predictions:][::-1]
            score = {}
            wnid = {}
            index = 0
            for node_id in top_k:
                node_finder = node_lookup.NodeLookup(FLAGS)

                human_string = node_finder.id_to_string(node_id)
                score[index] = predictions[node_id]
                wnid[index] = node_finder.id_to_wnid(node_id)
                #print('node: %s, %s (score = %.5f)' %
                      #(wnid[index], human_string, score[index]))
                index += 1
            sess.close()



        return [wnid, score]

    def classify(self, target_img):

        global FLAGS

        FLAGS.image_file = target_img

        image = (FLAGS.image_file)
        return self.run_inference_on_image(image)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument(
        '--image_file',
        type=str,
        default=None,
        help='Absolute path to image file.'
    )
    parser.add_argument(
        '--num_top_predictions',
        type=int,
        default=20,
        help='Display this many predictions.'
    )
    FLAGS, unparsed = parser.parse_known_args()

    a = Classifier()
    result_mtx = a.classify(FLAGS.image_file)
    wnids = result_mtx[0]
    scores = result_mtx[1]

    db_file = './../synsets/database/synset.sqlite3'

    db = web.database(dbn='sqlite', db=db_file)

    catnid = 'n02121808'



    matcher = picto_matcher.PictoMatcher(db, catnid, 0.2)
    var = matcher.match(wnids, scores)
    print(var)
