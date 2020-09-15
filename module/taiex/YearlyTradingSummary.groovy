 package module.taiex
/**
年度成交資訊
*/
 class YearlyTradingSummary{
 	def sqlDirName = 'yearly_trading_summary'
 	def dbName = 'findb'
 	def tableName = 'yearly_trading_summary'
 	def doSync(){
 		/////////
 		def sql = module.db.SqlExecuter.dbConnection{}

		def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")
		sql.close()
		stockCodes.each{
			def security_code=it.security_code
			def resultSql = ''
			if(!new File("./${sqlDirName}/${security_code}.sql").exists()){
					def returnJson = module.web.Webget.download{
							     url "https://www.twse.com.tw/exchangeReport/FMNPTK?response=json&lang=en&stockNo=${security_code}"
							     decode 'utf-8'
							     retry 100
							     sleeptime 50
							}
				module.parser.JsonConvert.convert{
								    input returnJson
									parseRule {json->
									    if(json.stat != 'OK'){
									    	//print '.'
									    	return ''
								        }
								        resultSql = "REPLACE INTO `${dbName}`.`${tableName}` (`year`, `trade_volume`, `trade_value`, `transaction`, `highest_price`, `date_highest_price`, `lowest_price`, `date_lowest_price`, `average_closing_price`, `security_code`) VALUES "
								    	for(int i=0;i<json.data.size;()i++){
								    		def _data = json.data[i]
								    		def year = _data[0]
								    		def trade_volume = _data[1].replaceAll(',','')
								    		def trade_value = _data[2].replaceAll(',','')
								    		def transaction = _data[3].replaceAll(',','')
								    		def highest_price = _data[4].replaceAll(',','')
								    		def date_highest_price =null
								    		if(_data[5]){
								    				def dtH = _data[5].split('/')
								    				date_highest_price = String.format('%04d%02d%02d',year,Integer.valueOf(dtH[0]),Integer.valueOf(dtH[1]))
								    		}
								    		def date_lowest_price =null
								    		def lowest_price = _data[6].replaceAll(',','')
								    		if(_data[7]){
								    			def dtL = _data[7].split("/")
								    			date_lowest_price = String.format('%04d%02d%02d',year,Integer.valueOf(dtL[0]),Integer.valueOf(dtL[1]))
								    		}
								    		def average_closing_price = _data[8].replaceAll(',','')
								    		 if(resultSql.endsWith('VALUES ')){
								    		 	if(date_lowest_price && date_lowest_price){
								    				resultSql+= "\r\n('${year}', '${trade_volume}', '${trade_value}', '${transaction}', '${highest_price}', ${date_highest_price}, '${lowest_price}', ${date_lowest_price}, '${average_closing_price}', '${security_code}')"
								    		 	}
								    		 }else{
								    		 	if(date_lowest_price && date_lowest_price){
								    		 		resultSql+= "\r\n,('${year}', '${trade_volume}', '${trade_value}', '${transaction}', '${highest_price}', ${date_highest_price}, '${lowest_price}', ${date_lowest_price}, '${average_closing_price}', '${security_code}')"	
								    		 	}
								    		 }
								    	}

								    }
								}
				if(resultSql && !resultSql.endsWith('VALUES ')){
			    	module.io.Batch.exec{
                		mkdirs "./${sqlDirName}"
                		write "./${sqlDirName}/${security_code}.sql",'UTF-8',"${resultSql};"
           			}
					print '*'
				}
			}else{
					print ">"
			}
		}

		module.db.SqlExecuter.exec{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import yearly_trading_summary done'
		}
		println '.'
 		/////////
 	}
     def static sync(@DelegatesTo(YearlyTradingSummary) Closure block){
	        YearlyTradingSummary m = new YearlyTradingSummary()
	        block.delegate = m
	        block()
	        m.doSync()
    }
 }