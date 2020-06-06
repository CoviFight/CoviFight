from django.views.generic import TemplateView
from django.contrib.auth.mixins import LoginRequiredMixin
from main.models import Profile

class HomePage(TemplateView):
    template_name = 'home.html'

class Dashboard(LoginRequiredMixin,TemplateView):
    login_url = '/login/'
    model=Profile
    template_name = 'dashboard.html'

class ListMembersView(LoginRequiredMixin,TemplateView):
    login_url = '/login/'
    model=Profile
    template_name = 'general_public.html'

class HotspotView(LoginRequiredMixin,TemplateView):
    login_url = '/login/'
    model=Profile
    template_name = 'hotspots.html'

class MapsView(LoginRequiredMixin,TemplateView):
    login_url = '/login/'
    model=Profile
    template_name = 'maps.html'



