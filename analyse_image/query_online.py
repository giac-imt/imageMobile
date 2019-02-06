# -*- coding: utf-8 -*-
"""
@author:HuangJie
@time:18-9-17 下午2:48

"""
import h5py
import numpy as np

from analyse_image.extract_cnn_vgg16_keras import VGGNet

'''
ap = argparse.ArgumentParser()
ap.add_argument("-query", required=True, help="Path to query which contains image to be queried")
ap.add_argument("-index", required=True, help="Path to index")
ap.add_argument("-result", required=True, help="Path for output retrieved images")
args = vars(ap.parse_args())
'''


def query(dir):
    # read in indexed images' feature vectors and corresponding image names
    h5f = h5py.File('./featureCNN.h5', 'r')
    feats = h5f['dataset_feat'][:]
    imgNames = h5f['dataset_name'][:]
    h5f.close()

    print("--------------------------------------------------")
    print("               searching starts")
    print("--------------------------------------------------")

    # read and show query image
    queryDir = dir

    # init VGGNet16 model
    model = VGGNet()

    # extract query image's feature, compute simlarity score and sort
    queryVec = model.extract_feat(queryDir)
    scores = np.dot(queryVec, feats.T)
    rank_ID = np.argsort(scores)[::-1]
    rank_score = scores[rank_ID]
    # print rank_ID
    # print rank_score


    # number of top retrieved images to show
    maxres = 5
    imlist = [imgNames[index].decode("utf-8") for i, index in enumerate(rank_ID[0:maxres])]
    imlistscore = [rank_score[0], rank_score[1], rank_score[2], rank_score[3], rank_score[4]]
    return imlist, imlistscore
