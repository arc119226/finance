/**
 每日一次
 1.download worth day json
 2.convert json to sql
 3.import sql
*/
module.processor.ProcessorRunner.runDayByDay{
	startYear 1999
	startMonth 1
	startday 4
	endYear 2020
	endMonth 8
	endDay 25
	process{yyyyMmDd->
	    if(new File('./highlights_of_daily_trading/'+yyyyMmDd+'.sql').exists()){
	    	print '>'
	    }else{
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

			            def _sql = "REPLACE INTO `stock_tw`.`highlights_of_daily_trading` (`${fields.join('`,`')}`,`traded_day`) VALUES "
			            
			            for(int i=0;i<json.data.size;i++){
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
			    	new File('./highlights_of_daily_trading/').mkdir()
		     		new FileOutputStream('./highlights_of_daily_trading/'+yyyyMmDd+'.tmp').withWriter('UTF-8') { writer ->
		        		writer << resultSql+';'
		     		}
		     		new File('./highlights_of_daily_trading/'+yyyyMmDd+'.tmp').renameTo('./highlights_of_daily_trading/'+yyyyMmDd+'.sql')
					print '*'
				}
	    }
	}
}

module.db.SqlExecuter.execute{
    dir './highlights_of_daily_trading'
}
module.io.FileBetch.execute{
	clean './highlights_of_daily_trading'
	delete './highlights_of_daily_trading'
}
println 'import highlights_of_daily_trading done'

