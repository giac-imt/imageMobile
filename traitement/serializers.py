from rest_framework import serializers
from traitement.models import Image


class ImageSerializer(serializers.ModelSerializer):

    class Meta:
        model = Image
        fields = ('description', 'base64_image')

