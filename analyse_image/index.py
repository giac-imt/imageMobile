# -*- coding: utf-8 -*-
"""
@author:HuangJie
@time:18-9-17 下午2:48

"""
import os

import h5py
import numpy as np

from analyse_image.extract_cnn_vgg16_keras import VGGNet


def get_imlist(path):
    return [os.path.join(path,f) for f in os.listdir(path) if f.endswith('.jpg')]


def index():
    db = img_paths = 'analyse_image/dataset-retr/train'
    img_list = get_imlist(db)
    
    print("--------------------------------------------------")
    print("         feature extraction starts")
    print("--------------------------------------------------")
    
    feats = []
    names = []

    model = VGGNet()
    for i, img_path in enumerate(img_list):
        norm_feat = model.extract_feat(img_path)
        feats.append(norm_feat)
        names.append(img_path)
        print("extracting feature from image No. %d , %d images in total" %((i+1), len(img_list)))

    feats = np.array(feats)
    names = np.array(names, dtype="S")
    # directory for storing extracted features
    #output = args["index"]
    output = 'featureCNN.h5'
    
    print("--------------------------------------------------")
    print("      writing feature extraction results ...")
    print("--------------------------------------------------")
    
    h5f = h5py.File(output, 'w')
    h5f.create_dataset('dataset_feat', data=feats)
    h5f.create_dataset('dataset_name', data=names)
    h5f.close()
