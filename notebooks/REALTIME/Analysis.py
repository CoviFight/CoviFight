import pandas as pd
import time
df2 = pd.read_csv('final2.csv')

c=77.0697872
d=28.450810200000003
i=0
j=0
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
            
    line= {
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
        }
    c=b
    d=a
    i=i+1
    print('{','coordinates:',line['coordinates'],',')
    print("status:'Moving', Vehical: 'Car', Trust: ",43+i%20+i/20+i%3+i%2+i%1)
    print('dates:',line['dates'],'}')
    #print('coordinates',line('coordinates'))
    print()
    time.sleep(.25)