from django.conf.urls import url, include

urlpatterns = [
    url(r'^auth/', include('my_secure_chat_backend.api.auth.urls'), name='auth'),
    url(r'^messages/', include('my_secure_chat_backend.api.messages.urls'), name='messages'),
]
