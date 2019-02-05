# Create your views here.

from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from image.models import ImageSearch as imgsrch
from image.serializers import ImageSearchSerializer
from analyse_image.index import index as idx
from analyse_image.query_online import query
import base64


class ImageSearch(APIView):
    # get qui va renvoyer toutes les infos sur le résultat
    def get(self, request, pk, format=None):
        image = imgsrch.objects.get(pk=pk)
        serializer = ImageSearchSerializer(image, many=False)
        return Response(serializer.data)

    # post pour créer le client/date
    def post(self, request, format=None):
        if len(request.data) is not 0:
            # Extraction du client
            client = request.META['HTTP_USER_AGENT']
            request.data["client"] = client

            # Extraction de l'image
            img_base64 = request.data["image_base64"]
            imgdata = base64.b64decode(img_base64)
            filename = 'image.jpg'
            with open(filename, 'wb') as f:
                image = f.write(imgdata)

            index = idx()
            #results = query(image)

            print(index)

            serializer = ImageSearchSerializer(data=request.data)
            if serializer.is_valid():
                serializer.save()
                response = Response(serializer.data, status=status.HTTP_201_CREATED)
                return response
            else:
                print(serializer.errors)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
