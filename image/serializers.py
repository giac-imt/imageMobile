from rest_framework import serializers
from image.models import ImageSearch


class ImageSearchSerializer(serializers.ModelSerializer):

    class Meta:
        model = ImageSearch
        fields = ('client', 'date')

