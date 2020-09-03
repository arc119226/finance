package module.currency
import java.text.SimpleDateFormat
class BitcoinInfo{
	def sqlDirName='currency'
	def tableName='currency'
	def coinPair=['USDT_BTC','USDT_ETH']
	def coinName=['BTC','ETH']
	def doSync(){
		Calendar calendar = Calendar.getInstance()
		calendar.add(Calendar.DAY_OF_MONTH,-0)
		def y = calendar.get(Calendar.YEAR)
		def m = String.format('%02d',calendar.get(Calendar.MONTH)+1)
		def d = String.format('%02d',calendar.get(Calendar.DAY_OF_MONTH))
		println "${y}${m}${d}"
		SimpleDateFormat start = new SimpleDateFormat("yyyyMMddHHmmss");
		def startDate = start.parse("${y}${m}${d}000000")

		SimpleDateFormat end = new SimpleDateFormat("yyyyMMddHHmmss");
		def endDate = end.parse("${y}${m}${d}235959")
		
		def startTime = startDate.getTime()/1000

		def endTime =  endDate.getTime()/1000
		println startDate
		println startTime
		println endDate
		println endTime

		for(int i=0;i<coinPair.size();i++){
			///////////////////////=
					def api = "https://poloniex.com/public?command=returnChartData&currencyPair=${coinPair[i]}&start=${startTime}&end=${endTime}&period=14400"
					println api
					def returnJson = module.web.Webget.download{
					    	url api
					    	decode 'utf-8'
					}
					module.parser.JsonConvert.convert{
									    input returnJson
										parseRule {json->
											def data = json[-1]
											println data.open
											println data.close
											println data.low
											println data.high
			////////
											def resultSql="REPLACE INTO `${tableName}`(`currency`,`traded_day`,`close`,`open`,`high`,`low`) values "
											resultSql+="('${coinName[i]}','${y}${m}${d}','${data.close}','${data.open}','${data.high}','${data.low}')"
											if(resultSql || !resultSql.endsWith('VALUES ')){
										    	module.io.Batch.exec{
							                		mkdirs "./${sqlDirName}"
							                		write "./${sqlDirName}/${new Date().getTime()}.sql",'UTF-8',"${resultSql};"
							           			}
									 			print '*'
									 		}
			////////
						 		
							}	
					}
			///////////////////////=
		}


		////////////
		module.db.SqlExecuter.exec{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import coin info done'
		}
		///////////
	}
	def static sync(@DelegatesTo(BitcoinInfo) Closure block){
	        BitcoinInfo m = new BitcoinInfo()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}