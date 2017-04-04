from django.contrib.auth.models import User
from django.db import models


class Message(models.Model):
    recipient = models.ForeignKey(User, related_name='received_messages')
    sender = models.ForeignKey(User, related_name='sent_messages')
    created = models.DateTimeField(auto_now_add=True)
    message = models.TextField()
