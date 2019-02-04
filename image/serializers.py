from rest_framework import serializers
from image.models import ImageSearch


class ImageSerializer(serializers.ModelSerializer):

    class Meta:
        model = ImageSearch
        fields = ('description', 'base64_image')

