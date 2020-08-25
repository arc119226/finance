/**
每日同步一次
*/
@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql
def sql = Sql.newInstance('jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4',
						  'root',
						  'Esorn@ldorn110','com.mysql.jdbc.Driver')

def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")
stockCodes.each{
	def security_code=it.security_code
	def listing_day = it.listing_day
	if(Integer.valueOf(listing_day)<=20041015){
		listing_day='20041015'
	}
	module.processor.ProcessorRunner.runDayByDay{
		startYear Integer.valueOf(listing_day.toString()[0..3])
		startMonth Integer.valueOf(listing_day.toString()[4..5])
		startday Integer.valueOf(listing_day.toString()[6..7])
		endYear 2020
		endMonth 8
		endDay 24
		process{yyyyMmDd->
			if(!new File("./statistics_trade_per_minute/${yyyyMmDd}/${security_code}.sql").exists()){
				def _url = "https://www.twse.com.tw/exchangeReport/MI_5MINS?response=json&lang=en&date=${yyyyMmDd}&stockNo=${security_code}"
				//println _url
				def returnJson = module.web.Webget.download{
			         url _url
			         decode 'utf-8'
			    }
			    def resultSql=''
			    module.parser.JsonConvert.convert{
				    input returnJson
					parseRule {json->
						if(json.stat != 'OK'){
	               			return null
	            		}
	            		def fields = json.fields.collect(fieldNormalize)
	            		if(resultSql==''){
	            			resultSql = "REPLACE INTO `stock_tw`.`statistics_trade_per_minute` (`${fields.join('`,`')}`,`traded_day`,`security_code`) VALUES "
	            		}
	            		for(int i=0;i<json.data.size;i++){
	            			def data = json.data[i];
	            			def time = "${yyyyMmDd}${data[0].replaceAll(':','')}" 
	            			def acc_bid_orders = data[1].replaceAll(',','')
	            			def acc_bid_volume = data[2].replaceAll(',','')
	            			def acc_ask_orders = data[3].replaceAll(',','')
	            			def acc_ask_volume = data[4].replaceAll(',','')
	            			def acc_transaction = data[5].replaceAll(',','')
	            			def acc_trade_volume = data[6].replaceAll(',','')
	            			def acc_trade_value_ntm = data[7].replaceAll(',','')

	            			if(resultSql.endsWith('VALUES ')){
			                  resultSql+="\r\n('${time}','${acc_bid_orders}','${acc_bid_volume}','${acc_ask_orders}','${acc_ask_volume}','${acc_transaction}','${acc_trade_volume}','${acc_trade_value_ntm}','${yyyyMmDd}','${security_code}')"
			               }else{
			                   resultSql+="\r\n,('${time}','${acc_bid_orders}','${acc_bid_volume}','${acc_ask_orders}','${acc_ask_volume}','${acc_transaction}','${acc_trade_volume}','${acc_trade_value_ntm}','${yyyyMmDd}','${security_code}')"
			               }
	            		}
					}
				}
				if(!resultSql.endsWith('VALUES ')){
	    			new File('./statistics_trade_per_minute/'+yyyyMmDd+'/').mkdirs()
	 				new FileOutputStream('./statistics_trade_per_minute/'+yyyyMmDd+'/'+security_code+'.tmp').withWriter('UTF-8') { writer ->
	    				writer << resultSql+';'
	 				}
	 				new File('./statistics_trade_per_minute/'+yyyyMmDd+'/'+security_code+'.tmp').renameTo('./statistics_trade_per_minute/'+yyyyMmDd+'/'+security_code+'.sql')
	 				//print ', save sql done'
				}
				print ' '+security_code+'_'+yyyyMmDd
			}else{
				print security_code+'_'+yyyyMmDd+' done. '
			}
		}
	}
}
// module.db.SqlExecuter.execute{
//     dir './statistics_trade_per_minute'
// }
// module.io.FileBetch.execute{
// 	clean './statistics_trade_per_minute'
// }
// println 'import statistics_trade_per_minute done'
