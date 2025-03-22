# How to USE?

可以从落雪或者水鱼拿出并转译为目标json格式(或类)
本软件已经提供了落雪的类可以直接拿来用

`你需要给的String格式如下:`
格式UserScore
```
{
    "userId": 12345,
    "userRating": {
        "rating": 85,
        "ratingList": [
            {
                "musicId": 101,
                "musicName": "Song A",
                "level": 12,
                "level_info": 12.5,
                "romVersion": 1,
                "achievement": 95,
                "rating": 88,
                "type": "typeA",
                "playCount": 5,
                "comboStatus": 1,
                "syncStatus": 2,
                "deluxscoreMax": 92000,
                "scoreRank": 1,
            },
            {
                "musicId": 102,
                "musicName": "Song B",
                "level": 10,
                "level_info": 10.3,
                "romVersion": 1,
                "achievement": 85,
                "rating": 82,
                "type": "typeB",
                "playCount": 3,
                "comboStatus": 0,
                "syncStatus": 1,
                "deluxscoreMax": 88000,
                "scoreRank": 2,
            }
        ],
        "newRatingList": [
            {
                "musicId": 103,
                "musicName": "Song C",
                "level": 11,
                "level_info": 11.0,
                "romVersion": 1,
                "achievement": 90,
                "rating": 85,
                "type": "typeC",
                "playCount": 2,
                "comboStatus": 1,
                "syncStatus": 2,
                "deluxscoreMax": 90000,
                "scoreRank": 1,
            }
        ],
        "nextRatingList": [
            {
                "musicId": 104,
                "musicName": "Song D",
                "level": 13,
                "level_info": 13.5,
                "romVersion": 1,
                "achievement": 80,
                "rating": 80,
                "type": "typeD",
                "playCount": 1,
                "comboStatus": 0,
                "syncStatus": 1,
                "deluxscoreMax": 85000,
                "scoreRank": 2,
            }
        ],
        "nextNewRatingList": [
            {
                "musicId": 105,
                "musicName": "Song E",
                "level": 14,
                "level_info": 14.0,
                "romVersion": 1,
                "achievement": 88,
                "rating": 83,
                "type": "typeE",
                "playCount": 0,
                "comboStatus": 0,
                "syncStatus": 0,
                "deluxscoreMax": 87000,
                "scoreRank": 2,
            }
        ]
    }
}
```
当然,每项数据都不一定必须,必须的有musicId ,musicName,level,level_info,achievement,type
## ratingList 是b35
## newratingList 是b15

`然后就是用户数据`

格式UserData
```
{
  "userId": 12345,
  "userName": "JohnDoe",
  "isLogin": true,
  "lastGameId": "G001",
  "lastRomVersion": "1.2.3",
  "lastDataVersion": "2.0.1",
  "lastLoginDate": "2023-10-01T12:34:56Z",
  "lastPlayDate": "2023-10-02T09:15:22Z",
  "playerRating": 85,
  "nameplateId": 101,
  "iconId": 201,
  "trophyId": 301,
  "isNetMember": 1,
  "isInherit": false,
  "totalAwake": 5,
  "dispRate": 100,
  "dailyBonusDate": "2023-10-01",
  "headPhoneVolume": "50",
  "banState": 0
}

```

这里的用户头像取决于`iconId`当然你可以修改这个地方,修改MaimaiImageService的方法即可,默认使用落雪的icon cdn

曲绘也是选择自落雪的cdn,你可以在此基础上二次开发,修改cdn,曲绘已默认使用多线程获取