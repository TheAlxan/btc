##Ledger

###Introduction
Hello, This application currently acts like a ledger, meaning it only stores the transactions made in other wallets.<br><br>
The database contains two tables,<br>
**balance table**, which stores the transaction date, amount and the latest balance.<br>
**delayed table**, which stores the id of delayed records.<br><br>
Because there could be some delayed transaction reports coming to server, I used the delayed table to temporally mark delayed records to affect them in the latest balance.<br>
Also there is a timer in the application that triggers on certain configured delays to fix the inconsistency of the database and clear the delayed table.<br><br>

###Start up
Application takes two command line arguments which are optional:<br>
- ```ip``` : **String**, the ip address of the server
- ```port``` : **String**, the port number of the server<br><br>
Also, there is config file in the ```resources``` folder which is used to load the application config.
<br>**note**: Command line args override config file properties.<br>

<br><br>
For simplifying the test process for you, I used H2 database which can be set from the config file to act as an In-Memory database. Just uncomment the desired jdbc url.

###Server
The server contains two public and two admin endpoints just for development purposes.<br>
-```ip:port/save```, ```Http POST``` which saves a transaction record,<br>
-```ip/port/balance```, ```Http POST```which returns the balance in the specified range.

####Sample input and output
```127.0.0.1:8111/save```<br>
```
{
    "datetime": "2009-08-01T15:29:00+07:00",
    "amount": "10000"
}
```
returns
```
{
    "message": "Successful!"
}
```
<br><br>
```127.0.0.1:8111/balance```
```
{
    "startDatetime": "2019-08-03T11:30:00+07:00",
    "endDatetime": "2019-08-03T15:35:00+07:00"
}
```
<br>returns<br>
```
{
    "list": [
        {
            "datetime": "2019-08-03T11:30:00+07:00",
            "amount": 23500.00000000
        },
        {
            "datetime": "2019-08-03T12:30:00+07:00",
            "amount": 24000.00000000
        },
        {
            "datetime": "2019-08-03T13:30:00+07:00",
            "amount": 24500.00000000
        },
        {
            "datetime": "2019-08-03T14:30:00+07:00",
            "amount": 24500.00000000
        },
        {
            "datetime": "2019-08-03T15:30:00+07:00",
            "amount": 24500.00000000
        }
    ]
}
```