from django.shortcuts import render
from django.http import HttpResponse
# Create your views here.

from rest_framework.response import Response
from rest_framework.views import APIView
from django.http import Http404

from image.models import ImageSearch as imgsrch
from image.serializers import ImageSearchSerializer
from rest_framework import status


class ImageSearch(APIView):
    # get qui va renvoyer toutes les infos sur le résultat
    def get(self, request, pk, format=None):
        image = imgsrch.objects.get(pk=pk)
        serializer = ImageSearchSerializer(image, many=False)
        return Response(serializer.data)

    # post pour créer le client/date
    # todo : demander à quoi correspond le champ de liaison dans models + doit-on le voir dans DB ?
    # todo : faire la recherche sift ici (image)
    def post(self, request, format=None):
        errors = {"error": "Erreur du format de la photo"}
        if len(request.data) is not 0:
            serializer = ImageSearchSerializer(data=request.data)
            if serializer.is_valid():
                serializer.save()
                response = Response(serializer.data, status=status.HTTP_201_CREATED)
                return response
            errors = serializer.errors
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
