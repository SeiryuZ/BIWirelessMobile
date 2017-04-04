import time
from django.contrib.auth import authenticate
from django.contrib.auth.models import User
from django.db.models import Q

from rest_framework import status, serializers
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from my_secure_chat_backend.apps.secure_messages.models import Message


class MessageSerializer(serializers.Serializer):
    id = serializers.ReadOnlyField()
    recipient = serializers.StringRelatedField()
    sender = serializers.StringRelatedField()
    message = serializers.CharField()
    created = serializers.SerializerMethodField('get_created_date')

    def get_created_date(self, obj):
        return int(time.mktime(obj.created.timetuple()))


class Index(APIView):
    authentication_classes = (TokenAuthentication, )
    permission_classes = (IsAuthenticated,)

    def get(self, request):
        messages = Message.objects.filter(
            Q(recipient=request.user) | Q(sender=request.user)
        )

        _from = request.GET.get('from', None)
        if _from:
            print "HERE", _from
            messages = messages.filter(id__gt=int(_from))

        messages = messages.order_by('id')
        serializers = MessageSerializer(messages, many=True)
        return Response({"messages": serializers.data}, status=status.HTTP_200_OK)
