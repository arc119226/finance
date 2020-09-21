package module.taiex
/**
市場成交資訊
*/
class HighlightsOfDailyTrading{
	def sqlDirName = 'highlights_of_daily_trading'
	def dbName = 'findb'
	def tableName_highlights_of_daily_trading='highlights_of_daily_trading'
	def doSync(){
////////////////
		module.processor.ProcessorRunner.runMonthByMonth{
			startYear Calendar.getInstance().get(Calendar.YEAR)
			startMonth Calendar.getInstance().get(Calendar.MONTH)+1
			startday 1
			endYear Calendar.getInstance().get(Calendar.YEAR)
			endMonth Calendar.getInstance().get(Calendar.MONTH)+1
			endDay 1
			process{yyyyMmDd->
			    if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists()){
			    	print '>'
			    }else{
						 def z = [2330,2340,2350]
						Random rnd = new Random()
						def w = z[rnd.nextInt(z.size())]
						println 'wait'+ w
						sleep(w)
					    def returnJson = module.web.Webget.download{
					         url "https://www.twse.com.tw/exchangeReport/FMTQIK?response=json&lang=en&date=${yyyyMmDd}"
					         decode 'utf-8'
					         validateHighLight true
					    }

						def resultSql = module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return ''
					            }
					            def fields = json.fields.collect(fieldNormalize)

					            def _sql = "REPLACE INTO `${dbName}`.`${tableName_highlights_of_daily_trading}` (`${fields.join('`,`')}`,`traded_day`) VALUES "
					            
					            for(int i=0;i<json.data.size();i++){
					               def _data = json.data[i].collect(valueNormalise).join("','");
					               if(i==0){
					                  _sql+="\r\n('${_data}','${yyyyMmDd}')"
					               }else{
					                   _sql+="\r\n,('${_data}','${yyyyMmDd}')"
					               }
					            }
					            return _sql
				        	}
				    	}

					    if(resultSql && !resultSql.endsWith('VALUES ')){
					    	module.io.Batch.exec{
                				mkdirs "./${sqlDirName}/"
                				write "./${sqlDirName}/${yyyyMmDd}.sql",'UTF-8',"${resultSql};"
           					}
           					print '*'
						}
			    }
			}
		}

		module.db.SqlExecuter.exec{
		    dir "./${sqlDirName}/"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}/"
			delete "./${sqlDirName}/"
			info 'import highlights_of_daily_trading done'
		}
////////////////
	}
	def static sync(@DelegatesTo(HighlightsOfDailyTrading) Closure block){
	        HighlightsOfDailyTrading m = new HighlightsOfDailyTrading()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}