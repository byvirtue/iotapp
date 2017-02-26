import requests
import json

def func(lis,field):
    for i in range(0,len(lis)):
        for key,value in lis[i].items():
            if key==field:
                return value;
        
ts_url1="https://api.thingspeak.com/channels/"
ts_url2="/feeds.json?api_key="
ts_url3="&results=1"
cnt=0;

maped={}
while(True):
    firebase=requests.get('https://iotdataviewer.firebaseio.com/alert.json')
    json_data = json.loads(firebase.text)
 #   print(json_data)
    for key ,value in json_data.items():
        reg_id=(value['reg_id'])
        api_id=(value['api_id'])
        ch_id=(value['ch_id'])
        field=(value['field'])
        try:
            maped[ch_id+field]
        except KeyError:
            maped[ch_id+field]=-1
        threshold=(value['threshold'])
        print(reg_id+" "+api_id+" "+ch_id+" "+field+" "+threshold)
        thingspeak=requests.get(ts_url1+ch_id+ts_url2+api_id+ts_url3)
        thing_data=json.loads(thingspeak.text)
        print(thingspeak.json())
        th_com=func(thing_data['feeds'],field)
        entry_id=func(thing_data['feeds'],"entry_id");
        
        print(th_com)
        if(th_com):
            if(float(th_com)>=float(threshold) and (int(entry_id)>int(maped[ch_id+field]))):
                url = 'https://fcm.googleapis.com/fcm/send'
                body = { "notification": {"title": "Alert","body": "channel id ="+ch_id+","+field+",value= "+threshold},"to" : reg_id}
                headers = {'Content-Type':'application/json','Authorization':'key=AAAAXrWHScs:APA91bGq_baVZNSrlSkZTBEjfeNY7NR4NUzkvO2gCpstiYL5cPb0Ri4otP3UtnVo9UzsXOhaD6_2AdL-lRxw1VUAI6hca-I_XhnlWRWJMOTPkIGmtAvRjwsF9nVwTzbQNOhTjYZzwIZH'}
                r=requests.post(url, data=json.dumps(body), headers=headers)
                print(r.json())
                maped[ch_id+field]=entry_id;




