"""imageMobile URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include, re_path
from image import views

urlpatterns = [
    path('image/', views.ImageSearch.as_view()),
    re_path(r'^image/(?P<pk>[0-9]+)/?$', views.ImageResult.as_view()),
    path('index/', views.ImageIndex.as_view()),
    re_path(r'^image/(?P<url>[A-Za-z0-9/()_.]+)/?$', views.ImageBase64.as_view()),
    re_path(r'^zip/(?P<url>[A-Za-z0-9]+)/?$', views.ZipToDataset.as_view()),
    path('admin/', admin.site.urls),
]
