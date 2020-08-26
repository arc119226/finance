# stock_tool
這是一個用groovy 寫的工具包
目的是將台灣證卷交易資料爬回本端dbdb 

爬資料的API

1
上市股票資訊

https://isin.twse.com.tw/isin/C_public.jsp?strMode=2

上櫃股票資訊

https://isin.twse.com.tw/isin/C_public.jsp?strMode=4

2
各日成交資訊

資料起始日20100104

https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=csv&date=20100104&stockNo=1101

3
本益比 淨值 殖利率

資料起始日200050902

https://www.twse.com.tw/exchangeReport/BWIBBU_d?response=csv&selectType=ALL&date=20050902

4
自營商當日買賣超

資料起始日20041217

https://www.twse.com.tw/fund/TWT43U?response=csv&date=20041217

5
投信當日買賣超

資料起始日20041217

https://www.twse.com.tw/fund/TWT44U?response=csv&date=20041217

6
外資當日買賣超

資料起始日20041217

https://www.twse.com.tw/fund/TWT38U?response=csv&date=20041217

7
個股日收盤價及月平均價

資料起始日19990105

https://www.twse.com.tw/exchangeReport/STOCK_DAY_AVG?response=csv&date=19990101&stockNo=1101

8
月成交資訊

資料起始日19920101

https://www.twse.com.tw/exchangeReport/FMSRFK?response=csv&date=19920101&stockNo=1101

9
年度成交資訊

資料起始1991

https://www.twse.com.tw/exchangeReport/FMNPTK?response=csv&stockNo=1101

10
當日融券賣出與借券賣出成交量值

資料起始日20080926

https://www.twse.com.tw/exchangeReport/TWTASU?response=csv&date=20080926

11
外資陸資持股比例

資料起始日20040211

https://www.twse.com.tw/fund/MI_QFIIS?response=csv&date=20040211&selectType=ALLBUT0999

12
市場成交資訊

資料起始日19900104

https://www.twse.com.tw/exchangeReport/FMTQIK?response=csv&date=19900101

13
外資及陸資投資類股彙總持股比率表

資料起始日20001207

https://www.twse.com.tw/fund/MI_QFIIS_cat?response=csv&date=20001207

14
每分鐘委託成交統計

資料起始日20041015

https://www.twse.com.tw/exchangeReport/MI_5MINS?response=csv&&date=20041015&stockNo=1101

15
信用交易統計 與 融資融券彙總

資料起始日20010101

https://www.twse.com.tw/exchangeReport/MI_MARGN?response=json&selectType=ALL&lang=en&date=20010101
