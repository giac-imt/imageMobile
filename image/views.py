# Create your views here.

import base64
import os
from io import BytesIO

from PIL import Image
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from analyse_image.query_online import query
from image.models import ImageSearch as imgsrch
from image.serializers import ImageSearchSerializer


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
            #client = request.META['HTTP_USER_AGENT']
            #request.data["client"] = client

            # Extraction de l'image
            img_base64 = request.data["image_base64"]
            img_base64_string = img_base64 + ''
            img_data_split = img_base64_string.split(",")  # je split la data des infos
            data = img_data_split[1]  # la data se situe derriere la virgule
            imgdata = base64.b64decode(data)
            im = Image.open(BytesIO(imgdata))
            im.save('image.jpeg', 'JPEG')

            #index = idx()
            results = query('image.jpeg')

            print(results)

            serializer = ImageSearchSerializer(data=request.data)
            if serializer.is_valid():
                serializer.save()
                response = Response(serializer.data, status=status.HTTP_201_CREATED)
                return response
            else:
                print(serializer.errors)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
