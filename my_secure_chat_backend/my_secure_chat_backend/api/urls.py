from django.conf.urls import url, include

urlpatterns = [
    url(r'^auth/', include('my_secure_chat_backend.api.auth.urls'), name='auth'),
]
