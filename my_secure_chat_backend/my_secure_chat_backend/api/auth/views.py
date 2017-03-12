from django.contrib.auth import authenticate
from django.contrib.auth.models import User

from rest_framework import status, serializers
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from .forms import RegistrationForm


class UserSerializer(serializers.ModelSerializer):

    class Meta:
        model = User
        fields = ('id', 'username')


class TokenTest(APIView):
    authentication_classes = (TokenAuthentication, )
    permission_classes = (IsAuthenticated,)

    def get(self, request):
        return Response({"result": "Your token is OK"}, status=status.HTTP_200_OK)


class Login(APIView):

    def post(self, request):
        # Bad example, don't follow this
        username = request.data.get('username')
        password = request.data.get('password')

        if not username or not password:
            return Response({'errors': "Missing username or password"}, status=status.HTTP_400_BAD_REQUEST)

        # Authenticate and return key
        user = authenticate(username=username, password=password)
        if user:
            return Response({"token": user.auth_token.key}, status=status.HTTP_200_OK)
        else:
            return Response({'errors': "Wrong username or password"}, status=status.HTTP_400_BAD_REQUEST)


class Register(APIView):

    def post(self, request):
        # Create user + auth token
        form = RegistrationForm(data=request.data or None)
        if form.is_valid():
            user = form.save()
            serializer = UserSerializer(user)
            return Response({'user': serializer.data}, status=status.HTTP_200_OK)
        return Response({'errors': form.errors}, status=status.HTTP_400_BAD_REQUEST)
