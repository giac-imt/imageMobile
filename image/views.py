# Create your views here.

import base64
import os
from io import BytesIO

from PIL import Image
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from analyse_image.query_online import query
from analyse_image.index import index as idx
from image.models import ImageSearch as imgsrch
from image.models import ImageResult as imgrslt
from image.serializers import ImageSearchSerializer
from image.serializers import ImageResultSerializer


class ImageSearch(APIView):
    # get qui va renvoyer toutes les infos sur le résultat
    def get(self, request, pk, format=None):
        image = imgsrch.objects.get(pk=pk)
        serializer = ImageSearchSerializer(image, many=False)
        return Response(serializer.data)

    # post pour créer le client/date
    def post(self, request, format=None):
        if len(request.data) is not 0:
            # Supprimer l'image prise par la camera si existante sur le serveur
            if os.path.isfile('image.jpeg'):
                os.remove('image.jpeg')

            # Extraction du client
            # client = request.META['HTTP_USER_AGENT']
            # request.data["client"] = client

            # Extraction de l'image
            img_base64 = request.data["image_base64"]
            # img_base64_string = img_base64 + ''
            # img_data_split = img_base64_string.split(",")  # je split la data des infos
            # data = img_data_split[1]  # la data se situe derriere la virgule
            imgdata = base64.b64decode(img_base64)
            im = Image.open(BytesIO(imgdata))
            im.save('image.jpeg', 'JPEG')

            # index = idx()
            results = query('image.jpeg')

            # Preparation de l'objet Search
            serializer = ImageSearchSerializer(data=request.data)
            # Enregistrement si valide pour avoir l'id
            if serializer.is_valid():
                serializer.save()

                # Pour les 5 premières images, remplissage de l'objet Result avec l'id de Search
                for i in range(0, 5):
                    serializer_results = ImageResultSerializer(data={'url': results[0][i], 'score': results[1][i],
                                                                     'img_search_key': imgsrch.objects.last().id})
                    if serializer_results.is_valid():
                        serializer_results.save()
                    else:
                        print(serializer_results.errors)
                else:
                    print(serializer.errors)
                response = Response(serializer.data, status=status.HTTP_201_CREATED)
                return response
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
