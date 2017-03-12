from django.conf.urls import url

from .views import Register, Login, TokenTest

urlpatterns = [
    url(r'^token-test/$', TokenTest.as_view(), name='login'),
    url(r'^login/$', Login.as_view(), name='login'),
    url(r'^register/$', Register.as_view(), name='register'),
]
