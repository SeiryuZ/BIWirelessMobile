from django.conf.urls import url

from .views import Index, Add

urlpatterns = [
    url(r'^index/$', Index.as_view(), name='index'),
    url(r'^add/$', Add.as_view(), name='add'),
]
