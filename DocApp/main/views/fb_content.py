from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
import base64
from Crypto.PublicKey import RSA
from Cryptodome.Cipher import PKCS1_OAEP
from Cryptodome.Hash import SHA512
from firebase_admin import initialize_app, firestore, credentials
import os
# For this to work, make sure that environment variable GOOGLE_APPLICATION_CREDENTIALS is set properly. 

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CREDS_ADDRESS=BASE_DIR+'/views/creds.json'
cred=credentials.Certificate(CREDS_ADDRESS)
fb_app = initialize_app(credential=cred)
store = firestore.client(app=fb_app)
#-----------------
#Getdata function
#-----------------
db = firestore.client()
def getdata(phnumber, update_probability=False):
    privatekey = db.collection(u'Profile').document(u'' + phnumber).get().to_dict()['PrivateKey']
    rsakey = RSA.importKey(base64.b64decode(privatekey))
    # rsakey =  PKCS1_OAEP.new(rsakey, hashAlgo=SHA256, mgfunc=lambda x,y: pss.MGF1(x,y, SHA256))
    rsakey = PKCS1_OAEP.new(rsakey, SHA512)

    def decrypt_location(loc, rsakey):
        #         global rsakey
        b64_decoded_message = base64.b64decode(loc)
        decrypted = rsakey.decrypt(b64_decoded_message)
        return decrypted

    #         #temorary
    #         #3decimal places taken
    #         lat=round(locx[0],3)
    #         long=round(locx[1],3)
    #         return(str(lat)+','+str(long))
    #         #temp ends
    #         #decrypt and send
    #         #return(list(map(float,loc.split(','))))
    def rev_cipher(num):
        # add code here
        return num

    users_ref = db.collection(u'Profile').document(u'' + phnumber).collection('TimeStamps')
    docs = users_ref.stream()
    blth = {}
    loc = {}
    for doc in docs:
        bdic = doc.to_dict()
        time = bdic['TimeStamps']
        if (bdic['Activity'] == "STILL"):
            loci = bdic['Location']
            #             print(loci)
            loci = decrypt_location(loci, rsakey)
            if loci in loc:
                loc[loci]["time"] += 2
                loc[loci]["last"] = time
            else:
                loc[loci] = {"time": 2, "last": time}

        if 'MacAddress' in bdic:
            for bl in bdic['MacAddress']:
                bl1 = rev_cipher(bl)
                if bl1 in blth:
                    blth[bl1]["time"] += 2
                    blth[bl1]["last"] = time
                else:
                    blth[bl1] = {"time": 2, "last": time}
    blth2 = {}
    for i in blth:
        number = db.collection(u'Identify').document(u'' + i).get().to_dict()['Number']
        blth2[number] = {"time": blth[i]["time"], "last": blth[i]["last"]}

    def update_prob(blth):
        for i in blth:
            prob = db.collection(u'Profile').document(u'' + i).get().to_dict()['Probability']
            if blth[i]['time'] > 15:
                prob += 0.7
            elif blth[i]['time'] > 5:
                prob += 0.4
            else:
                prob += 0.2
            prob = min(prob, 1)*100
            pth = db.collection(u'Profile').document(u'' + i).update({u'Probability': prob})
            data = {
                u'time spent': blth[i]['time'],
                u'last': blth[i]['last']
            }

            db.collection(u'Profile').document(u'' + i).collection(u'contact').document().set(data, merge=True)

    if update_probability==True:
        update_prob(blth2)

    ## to conver to list
    loc2 = []
    for i in loc:
        loc2 += [{'geo': i.decode("utf-8"), 'last': loc[i]['last'], 'time': loc[i]['time']}]

    if update_probability:
        for i in loc2:
            locx = i['geo']
            locx = list(map(float, locx.split(',')))
            doc_ref = db.collection(u'HighRiskPlaces').document()
            doc_ref.set({
                u'name': i['geo'],
                u'location': firestore.GeoPoint(locx[0], locx[1])
            })

    return ({"devices": blth2, "location": loc2})
#------------------
#fin---------------
#------------------

@api_view(["GET"])
def get_highriskplaces(request):
    places = store.collection(u"HighRiskPlaces").get()
    payload = []
    for place in places:
        data = place.to_dict()
        location = data['location']
        name = data['name']
        data_dict = {
            "name": name,
            "latitude": location.latitude,
            "longitude": location.longitude,
        }
        payload.append(data_dict)
    return Response(payload, status=status.HTTP_200_OK)

@api_view(["GET"])
def get_all_profile(request):
    profiles = store.collection(u"Profile").get()
    payload = []
    for profile in profiles:
        data = profile.to_dict()
        payload.append(data)

    
    return Response(payload, status=status.HTTP_200_OK)

@api_view(["GET"])
def get_profile(request, id):
    profile = store.collection(u"Profile").document(id).get()

    if profile is None:
        return Response({"error": "Invalid user id."},status=status.HTTP_404_NOT_FOUND)
    '''
    timestamp_loc = "Profile/" + id +"/TimeStamps" 
    timestamps = store.collection(timestamp_loc).get()

    timestamps_list = []

    for timestamp in timestamps:
        timestamps_list.append(timestamp.to_dict())
    '''
    try:
        my_data=getdata(phnumber=id)
        devices=my_data["devices"]
        connectedDeviceData = []
        for device in devices:
            device_profile = db.collection(u"Profile").document(device).get()
            connectedDeviceData.append(device_profile.to_dict())
        timestamps_list=my_data["location"]
        profile_dict = profile.to_dict()
        profile_dict.update({'flag': True,"timestamps":timestamps_list, "devices_connected":connectedDeviceData })
        return Response(profile_dict,status=status.HTTP_200_OK)

    except:
        profile_dict = profile.to_dict()
        profile_dict.update({'flag':False})
        return Response(profile_dict,status=status.HTTP_200_OK)

@api_view(["GET"])
def update_status(request, id):
    profile = store.collection(u"Profile").document(id)

    if profile.get() is None:
        return Response({"error": "Invalid user id."},status=status.HTTP_404_NOT_FOUND)
    try:
        getdata(id, True)
        profile.update({"isPos": True, "Probability": 100})
        return Response(profile.get().to_dict(), status=status.HTTP_200_OK)
    except:
        return Response({'error':'Please activate private key'},status=status.HTTP_403_FORBIDDEN)



@api_view(["GET"])
def get_providers(request):
    providers = store.collection(u"Providers").get()
    payload = []

    for provider in providers:
        data = provider.to_dict()
        name = data['name']
        location = data['location']
        data_dict = {
            "name": name,
            "latitude": location.latitude,
            "longitude": location.longitude,
        }
        payload.append(data_dict)
    return Response(payload, status=status.HTTP_200_OK)

