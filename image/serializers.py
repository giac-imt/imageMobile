from rest_framework import serializers
from image.models import ImageSearch
from image.models import ImageResult


class ImageResultSerializer(serializers.ModelSerializer):
    class Meta:
        model = ImageResult
        fields = ('url', 'score', 'img_search_key')


class ImageSearchSerializer(serializers.ModelSerializer):
    #resultats = ImageResultSerializer(many=True)

    class Meta:
        model = ImageSearch
        fields = ('client', 'date', 'resultats')
