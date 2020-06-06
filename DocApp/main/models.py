from django.db import models
from django.contrib.auth.models import User

from django.dispatch import receiver
from django.db.models.signals import post_save

# Create your models here.


class Profile(models.Model):
    user = models.OneToOneField(User,on_delete=models.CASCADE)
    is_admin = models.BooleanField(default=False)
    name = models.CharField(max_length=100)

    def __str__(self):
        return self.user.username


@receiver(post_save,sender=User)
def create_profile(sender,instance,created,**kwargs):
    if created:
        Profile.objects.create(user=instance)
        instance.profile.save()


@receiver(post_save,sender=User)
def update_profile(sender,instance,**kwargs):
    instance.profile.save()
    
