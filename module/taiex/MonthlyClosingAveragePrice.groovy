package module.taiex
class MonthlyClosingAveragePrice{
	def sqlDirName = 'monthly_closing_average_price'

	def doSync(){
///////////
		def sql = module.db.SqlExecuter.dbConnection{}

		def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")
		sql.close()

		stockCodes.each{it->
			def security_code = it.security_code
			def listing_day = String.format("%04d%02d%02d",Integer.valueOf(it.listing_day.toString()[0..3]),Integer.valueOf(it.listing_day.toString()[4..5]),1)
			//println "${Integer.valueOf(listing_day)} vs 19990105"
			if(Integer.valueOf(listing_day) < 19990105){
				listing_day='19990101'
			}
			def resultSql =''
			if(!new File("./${sqlDirName}/${security_code}.sql").exists()){
				print listing_day
				module.processor.ProcessorRunner.runMonthByMonth{
					startYear Calendar.getInstance().get(Calendar.YEAR)//Integer.valueOf(listing_day.toString()[0..3])
					startMonth Calendar.getInstance().get(Calendar.MONTH)+1//Integer.valueOf(listing_day.toString()[4..5])
					startday 1//Integer.valueOf(listing_day.toString()[6..7])
					endYear Calendar.getInstance().get(Calendar.YEAR)
					endMonth Calendar.getInstance().get(Calendar.MONTH)+1
					endDay 1
					process{yyyyMmDd->
						sleep(1)
						def _url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY_AVG?response=json&lang=en&stockNo=${security_code}&date=${yyyyMmDd}"
						//print '.'
						def returnJson = module.web.Webget.download{
						     url _url
						     decode 'utf-8'
						     retry 100
						     sleeptime 50
						     validate true
						}
						module.parser.JsonConvert.convert{
						    input returnJson
							parseRule {json->
							    if(json.stat != 'OK'){
							    	//print '.'
							    	return ''
						        }
						        def fields = json.fields.collect(fieldNormalize)
						        if(resultSql==''){
					            	resultSql = "REPLACE INTO `stock_tw`.`monthly_closing_average_price` (`${fields[0..1].join('`,`')}`,`traded_day`,`security_code`) VALUES "
							    }
							          
							    for(int i=0;i<json.data.size;i++){
							     	def _data = json.data[i].collect(valueNormalise)[0..1].join("','");
							         if(resultSql.endsWith('VALUES ')){
							        	def td = json.data[i][0].replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$1$3$5');

							        	resultSql+= "\r\n('${_data}','${td}','${security_code}')"
							        }else if(i==json.data.size-1){
							        	resultSql+= "\r\n,('${_data}','${yyyyMmDd}','${security_code}')"
							        }else{
							        	def td = json.data[i][0].replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$1$3$5');

							            resultSql+= "\r\n,('${_data}','${td}','${security_code}')"
							        }
							    }
							    //println ' parse json done'
							    return resultSql
							}
						}
					}//process
				}//run
				if(resultSql || !resultSql.endsWith('VALUES ')){
			    	module.io.Batch.exec{
                		mkdirs "./${sqlDirName}"
                		write "./${sqlDirName}/${security_code}.tmp",'UTF-8',"${resultSql};"
                		rename "./${sqlDirName}/${security_code}.tmp","./${sqlDirName}/${security_code}.sql"
           			}
			 		print '*'
				}		
			}else{
				print '>'
			}
		}

		module.db.SqlExecuter.execute{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.execute{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import monthly_closing_average_price done'
		}
		println 

///////////
	}

	def static sync(@DelegatesTo(MonthlyClosingAveragePrice) Closure block){
	        MonthlyClosingAveragePrice m = new MonthlyClosingAveragePrice()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}