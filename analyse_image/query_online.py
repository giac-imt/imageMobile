# -*- coding: utf-8 -*-
"""
@author:HuangJie
@time:18-9-17 下午2:48

"""
import h5py
import matplotlib.image as mpimg
import matplotlib.pyplot as plt
import numpy as np

from analyse_image.extract_cnn_vgg16_keras import VGGNet

'''
ap = argparse.ArgumentParser()
ap.add_argument("-query", required=True, help="Path to query which contains image to be queried")
ap.add_argument("-index", required=True, help="Path to index")
ap.add_argument("-result", required=True, help="Path for output retrieved images")
args = vars(ap.parse_args())
'''

# read in indexed images' feature vectors and corresponding image names
h5f = h5py.File('./featureCNN.h5', 'r')
feats = h5f['dataset_feat'][:]
imgNames = h5f['dataset_name'][:]
h5f.close()

print("--------------------------------------------------")
print("               searching starts")
print("--------------------------------------------------")
    
# read and show query image
queryDir = './dataset-retr/train/ukbench00000.jpg'
queryImg = mpimg.imread(queryDir)
plt.figure()
plt.subplot(2, 1, 1)
plt.imshow(queryImg)
plt.title("Query Image")
plt.axis('off')

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
maxres = 3
imlist = [imgNames[index] for i,index in enumerate(rank_ID[0:maxres])]
print("top %d images in order are: " %maxres, imlist)
 

# show top #maxres retrieved result one by one
for i, im in enumerate(imlist):
    image = mpimg.imread('./dataset-retr/train'+"/"+im)
    plt.subplot(2, 3, i+4)
    plt.imshow(image)
    plt.title("search output %d" % (i + 1))
    plt.axis('off')
plt.show()
