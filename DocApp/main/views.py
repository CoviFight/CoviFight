from django.shortcuts import render
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from main.auth_helpers import get_jwt_with_user

@api_view(['POST'])
def register(request):
    try:
        username = request.data['username']
        email = request.data['email']
    except KeyError:
        return Response({'error':'Either username or email is not Provided'},status=status.HTTP_403_FORBIDDEN)

    if User.objects.filter(email=email).count() != 0:
        return Response({'error':'A User is already registered with this email id'},status=status.HTTP_403_FORBIDDEN)
    user = User(username=username,email=email)
    password = request.data['password']
    user.set_password(password)
    user.save()
    user.refresh_from_db()
    try:
        user.profile.name = request.data['name']
        user.profile.is_admin = request.data['is_admin']
    except KeyError:
        return Response({'error':'Profile data is not provided'},status=status.HTTP_403_FORBIDDEN)

    user.profile.save()

    token = get_jwt_with_user(user)

    return Response({'token':token,'username':user.username,'email':user.email},status=status.HTTP_201_CREATED)

@api_view(['POST'])
def login(request):
    try:
        username = request.data['username']
        password = request.data['password']
    except KeyError:
        return Response({'error':'Either username or password is not provided'},status=status.HTTP_403_FORBIDDEN)

    user = authenticate(request,username=username,password=password)

    if user:
        token = get_jwt_with_user(user)
        return Response({'token':token},status=status.HTTP_200_OK)
    else:
        return Response({'error':'Invalid Login details supplied.'},status=status.HTTP_403_FORBIDDEN)



class HomePage(TemplateView):
    template_name = 'home.html'