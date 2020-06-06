import firebase_admin 
from firebase_admin import credentials, firestore
import pandas as pd
cred = credentials.Certificate("creds.json")
firebase_admin.initialize_app(cred)

db = firestore.client()
stores=db.collection(u'Providers')
df=pd.read_csv(r'store.csv')
#print(df.loc[0])
row=df.loc[0]
doc_ref=db.collection(u'HighRiskPlaces').document(row[1])
shape=df.shape
num_rows=shape[0]
for i in range(0,num_rows,10):
	row=df.loc[i]
	doc_ref=db.collection(u'HighRiskPlaces').document(row[1])
	doc_ref.set({
		u'name':row[1],
		u'location':firebase_admin.firestore.GeoPoint(row[2], row[3])
		})
