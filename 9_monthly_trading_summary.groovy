/**
每日一次
*/
@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql

def sql = Sql.newInstance('jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4',
						  'root',
						  'Esorn@ldorn110','com.mysql.jdbc.Driver')

def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")

stockCodes.each{it->
	def listing_day = it.listing_day
	def security_code = it.security_code
	if(Integer.valueOf(it.listing_day) < 19920101){
		listing_day='19920101'
	}
	def resultSql =''
	if(!new File("./monthly_trading_summary/${security_code}.sql").exists()){
		module.processor.ProcessorRunner.runYearByYear{
			startYear 2020 //Integer.valueOf(listing_day.toString()[0..3])
			startMonth 8//Integer.valueOf(listing_day.toString()[4..5])
			startday 1//Integer.valueOf(listing_day.toString()[6..7])
			endYear 2020
			endMonth 8
			endDay 1
			process{yyyyMmDd->
				sleep(10)
				def _url = "https://www.twse.com.tw/exchangeReport/FMSRFK?response=json&lang=en&date=${yyyyMmDd}&stockNo=${security_code}"
				def returnJson = module.web.Webget.download{
				     url _url
				     decode 'utf-8'
				     retry 100
				     sleeptime 50
				     }//mwebget
				module.parser.JsonConvert.convert{
				    	input returnJson
						parseRule {json->
							if(json.stat != 'OK'){
						    	return ''
					        }
					        def fields = json.fields.collect(fieldNormalize)
							if(resultSql==''){
						    	resultSql = "REPLACE INTO `stock_tw`.`monthly_trading_summary` (`${fields.join('`,`')}`,`security_code`) VALUES "
							}
							for(int i=0;i<json.data.size;i++){
						     	def _data = json.data[i].collect(valueNormalise).join("','");
							    if(resultSql.endsWith('VALUES ')){
							        resultSql+= "\r\n('${_data}','${security_code}')"
							    }else if(i==json.data.size-1){
							        resultSql+= "\r\n,('${_data}','${security_code}')"
							        
							    }else{
							        resultSql+= "\r\n,('${_data}','${security_code}')"
							    }
							}
							return resultSql
							//println ' parse json done'
						}
					}//json
				}//convert
			}//process
			if(resultSql && !resultSql.endsWith('VALUES ')){
	    	new File('./monthly_trading_summary/').mkdir()
		 		new FileOutputStream("./monthly_trading_summary/${security_code}.tmp").withWriter('UTF-8') { writer ->
		    		writer << resultSql+';'
		 		}
		 		new File("./monthly_trading_summary/${security_code}.tmp").renameTo("./monthly_trading_summary/${security_code}.sql")
	 		//print ', save sql done'
			}
			print '*'
	}else{
		print ">"
	}
}
module.db.SqlExecuter.execute{
    dir './monthly_trading_summary'
}
// module.io.FileBetch.execute{
// 	clean './monthly_trading_summary'
// }
println 'import monthly_trading_summary done'
