from django import forms
from django.contrib.auth.models import User

from rest_framework.authtoken.models import Token


class RegistrationForm(forms.Form):
    username = forms.CharField()
    password = forms.CharField()

    def clean_username(self):
        username = self.cleaned_data['username']
        if User.objects.filter(username=username).exists():
            raise forms.ValidationError("Userame exists")
        return username

    def save(self, *args, **kwargs):
        username = self.cleaned_data['username']
        password = self.cleaned_data['password']
        user = User.objects.create_user(username, password=password)
        Token.objects.create(user=user)
        return user
