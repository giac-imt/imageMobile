from django.db import models

# Create your models here.


class ImageSearch(models.Model):
    client = models.CharField(max_length=200, default='')
    date = models.DateTimeField(auto_now_add=True)


class ImageResult(models.Model):
    url = models.CharField(max_length=200, default='')
    score = models.FloatField()
    img_search_key = models.ForeignKey(ImageSearch, on_delete=models.CASCADE, default='')
