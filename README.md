# stock_tool
這是一個用groovy 寫的工具包
目的是將台灣證卷交易資料爬回本端dbdb 

爬資料的API

上市股票資訊

https://isin.twse.com.tw/isin/C_public.jsp?strMode=2

上櫃股票資訊

https://isin.twse.com.tw/isin/C_public.jsp?strMode=4

各日成交資訊

資料起始日20100104

https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=20100104&stockNo=1101

本益比 淨值 殖利率

資料起始日200050902

https://www.twse.com.tw/exchangeReport/BWIBBU_d?response=json&selectType=ALL&date=20050902&lang=en

自營商當日買賣超

資料起始日20041217

https://www.twse.com.tw/fund/TWT43U?response=json&date=20041217

投信當日買賣超

資料起始日20041217

https://www.twse.com.tw/fund/TWT44U?response=json&date=20041217

外資當日買賣超

資料起始日20041217

https://www.twse.com.tw/fund/TWT38U?response=json&date=20041217

個股日收盤價及月平均價

資料起始日19990105

https://www.twse.com.tw/exchangeReport/STOCK_DAY_AVG?response=json&lang=en&date=19990101&stockNo=1101
