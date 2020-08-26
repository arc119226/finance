/**
每日一次
*/
def sql = module.db.SqlExecuter.dbConnection{}

def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")
sql.close()
stockCodes.each{it->
	def listing_day = it.listing_day
	def security_code = it.security_code
	if(Integer.valueOf(it.listing_day) < 19920101){
		listing_day='19920101'
	}
	def resultSql =''
	if(!new File("./monthly_trading_summary/${security_code}.sql").exists()){
		module.processor.ProcessorRunner.runYearByYear{
			startYear Calendar.getInstance().get(Calendar.YEAR) //Integer.valueOf(listing_day.toString()[0..3])
			startMonth Calendar.getInstance().get(Calendar.MONTH)+1//Integer.valueOf(listing_day.toString()[4..5])
			startday 1//Integer.valueOf(listing_day.toString()[6..7])
			endYear Calendar.getInstance().get(Calendar.YEAR)
			endMonth Calendar.getInstance().get(Calendar.MONTH)+1
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
module.io.FileBetch.execute{
	clean './monthly_trading_summary'
	delete './monthly_trading_summary'
}
println 'import monthly_trading_summary done'