# Pictocatcher


A pokemon style Android game that uses the inception image classification network to
 promote pictogram assisted learning. The player is given a list of pictograms they must capture. The objective is
for the player to find these pictograms in the world and take a picture of them. The Inception
Image classification network is used to determine if the picture contains the item
represented in the selected pictogram. In this way, the association between pictograms and real world objects is
reinforced in the user's mind.

Pictograms act as visual support to aid those who have communication
impairments or disorders. They are often used for teaching in special needs
education centers.

The Aragonese Portal of Augmentative and Alternative Communication (ARASAAC)
provides an extensive set of pictograms with labels in many languages.
The game has been developped with pictogram labels in Spanish. However,
English labels are also available from [ARASAAAC](http://www.arasaac.org/pictogramas_color.php).


## How to run

Server:

App:

## Architecture

Pictocatcher is composed of two parts. The first is a server which evaluates images
with Google's [Inception](https://ai.googleblog.com/2016/03/train-your-own-image-classifier-with.html)
 image classification network. It then checks that the image's class corresponds to
 that of the selected pictogram usig the technique descibed [here.](#going-from-imagenet-labels-to-pictograms).

 <img src="pictures/system_overview.png" width="400" height="300" />

The second part is an android application. It provides the user with a set
of pictograms to capture. It allows the user to take pictures and send them to
the server. Communication between the app (clients) and the server is done through
 http post requests using [Google Volley](https://github.com/google/volley). Image
 storing on the mobile device is done with [Picasso](http://square.github.io/picasso/).

 In game screenshot:

 <img src="pictures/in_game.png" width="300" height="500" />

## Going from Imagenet labels to pictograms

<img src="pictures/relationship.png" width="350" height="100" />

The inception network acts as a 1000-way classifier. However, there are more than 1000 pictograms and
 there may not be an exacy correspondance between pictograms and Imagenet
labels. In some cases, Imagenet classes are more detailed than pictograms.
i.e. There are classes for many types of cat (tabby cat, egiptian cat, etc)
but there is only a generic cat pictogram. In other cases, a handfull of
pictograms can fall under only one imagenet label.

We solve this issue by using
[Wordnet](https://wordnet.princeton.edu), a lexical database. Wordnet defines
groups of cognitive synonym (synsets). Imagenet also maps its images with Wordnet synsets
as is shown [here](http://www.image-net.org/synset?wnid=[wnid]).
When the neural network decides a class, we search through the class synsets looking
for the corresponding pictogram label. If it is found, we assume the user has
taken a picture of the correct object.


## Other resources:

* Imagenet paper:
Olga Russakovsky, Jia Deng, Hao Su, Jonathan Krause, Sanjeev Satheesh, Sean Ma, Zhiheng Huang, Andrej Karpathy, Aditya Khosla, Michael Bernstein, Alexander C. Berg, and Li Fei-Fei. 2015. ImageNet Large Scale Visual Recognition Challenge. Int. J. Comput. Vision 115, 3 (December 2015), 211-252. DOI=http://dx.doi.org/10.1007/s11263-015-0816-y
or http://www.image-net.org/papers/imagenet_cvpr09.pdf
* Wordnet:
George A. Miller (1995). WordNet: A Lexical Database for English.
Communications of the ACM Vol. 38, No. 11: 39-41.
https://academic.oup.com/ijl/article/3/4/235/923280
* The full pictogram catalog used can be found at http://www.arasaac.org/pictogramas_color.php

