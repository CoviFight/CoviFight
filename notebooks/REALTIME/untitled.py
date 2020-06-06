import pandas as pd
import os

import folium
from folium import plugins
#from sklearn.cluster import KMeans
import folium

import folium
from folium import plugins
import pandas as pd
import matplotlib.pyplot as plt

#%matplotlib inline

df1=pd.read_csv("school.csv")

def generateBaseMap(default_location=[28.4595, 77.0266], default_zoom_start=12):
    base_map = folium.Map(location=default_location, control_scale=True, zoom_start=default_zoom_start)
    return base_map

from folium.plugins import HeatMap
df1 = df1[df1['Latitude']>=1].copy()
df1['count'] = 1
base_map3 = generateBaseMap()
HeatMap(data=df1[['Latitude', 'Longitude', 'count']].groupby(['Latitude', 'Longitude']).sum().reset_index().values.tolist(), radius=8, max_zoom=13).add_to(base_map3)























df = pd.read_csv('final2.csv')
df2 = pd.read_csv('final.csv')

c=77.0697872
d=28.450810200000003
i=0
j=0
lines2=[]
for a,b in zip(df2.Latitude, df2.Longtitude):
    sec=i%60
    minn=int(i/60)
    sec2=(sec+1)%60
    minn2=minn+int((sec+1)/60)
    ttt=[sec,sec2,minn,minn2]
    tttx=[]
    for j in range(4):
        if(ttt[j]<=9):
            tttx+=['0'+str(ttt[j])]
        else:
            tttx+=[str(ttt[j])]
            
    lines2+= [
        {
            'coordinates': [
                [c, d],
                [b, a],
            ],
            'dates': [
                '2019-03-11T'+tttx[2]+":"+tttx[0]+":00",
                '2019-03-11T'+tttx[3]+":"+tttx[1]+":00"
#                 '2019-03-11T'+"00"+":"+tttx[0]+":00",
#                 '2019-03-11T'+"00"+":"+tttx[1]+":00"
            ],
            'color': 'blue'
        },
    ]
    c=b
    d=a
    i=i+1
lines2=lines2[1:]

c=77.0697872
d=28.450810200000003
i=0
j=0
lines=[]
for a,b in zip(df.Latitude, df.Longtitude):
    sec=i%60
    minn=int(i/60)
    sec2=(sec+1)%60
    minn2=minn+int((sec+1)/60)
    ttt=[sec,sec2,minn,minn2]
    tttx=[]
    for j in range(4):
        if(ttt[j]<=9):
            tttx+=['0'+str(ttt[j])]
        else:
            tttx+=[str(ttt[j])]
            
    lines+= [
        {
            'coordinates': [
                [c, d],
                [b, a],
            ],
            'dates': [
                '2019-03-11T'+tttx[2]+":"+tttx[0]+":00",
                '2019-03-11T'+tttx[3]+":"+tttx[1]+":00"
#                 '2019-03-11T'+"00"+":"+tttx[0]+":00",
#                 '2019-03-11T'+"00"+":"+tttx[1]+":00"
            ],
            'color': 'red'
        },
    ]
    c=b
    d=a
    i=i+1
lines=lines[1:]

folium.Marker(
    location=[28.450810200000003, 77.0697872], 
    icon=folium.Icon(color="green",icon="university", prefix='fa')
).add_to(base_map3)

folium.Marker(
    location=[28.457, 77.040288], 
    icon=folium.Icon(color="red",icon="home", prefix='fa')
).add_to(base_map3)

folium.Marker(
    location=[28.389777, 77.0555867], 
    icon=folium.Icon(color="blue",icon="home", prefix='fa')
).add_to(base_map3)

features = [
    {
        'type': 'Feature',
        'geometry': {
            'type': 'LineString',
            'coordinates': line['coordinates'],
        },
        'properties': {
            'times': line['dates'],
            'style': {
                'color': line['color'],
                'weight': line['weight'] if 'weight' in line else 3
            }
        }
    }
    for line in lines+lines2
]


plugins.TimestampedGeoJson({
    'type': 'FeatureCollection',
    'features': features,
}, period='PT1M', add_last_point=False).add_to(base_map3)

base_map3.render()