from django.db import models

# Create your models here.


class ImageSearch(models.Model):
    client = models.CharField(max_length=200, default='')
    date = models.TextField()


class ImageResult(models.Model):
    url = models.CharField(max_length=200, default='')
    score = models.TextField()
    foreign_key = models.IntegerField()
