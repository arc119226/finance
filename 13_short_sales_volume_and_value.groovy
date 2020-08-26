/**
 每日一次
 1.download worth day json
 2.convert json to sql
 3.import sql
*/
module.processor.ProcessorRunner.runDayByDay{
	startYear Calendar.getInstance().get(Calendar.YEAR)
	startMonth Calendar.getInstance().get(Calendar.MONTH)+1
	startday 1
	endYear Calendar.getInstance().get(Calendar.YEAR)
	endMonth Calendar.getInstance().get(Calendar.MONTH)+1
	endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
	process{yyyyMmDd->
	    if(new File('./short_sales_volume_and_value/'+yyyyMmDd+'.sql').exists()){
	    	print '>'
	    }else{
			    def returnJson = module.web.Webget.download{
			         url "https://www.twse.com.tw/exchangeReport/TWTASU?response=json&lang=en&date=${yyyyMmDd}"
			         decode 'utf-8'
			         validateShortSell true
			    }

				def resultSql = module.parser.JsonConvert.convert{
		        	input returnJson
		        	parseRule {json->
			            if(json.stat != 'OK'){
			               return ''
			            }
			            def fields = json.fields.collect(fieldNormalize)

			            def _sql = "REPLACE INTO `stock_tw`.`short_sales_volume_and_value` (`security_code`,`securities_lending_volume`,`securities_lending_value`,`borrow_volume`,`borrow_value`,`traded_day`) VALUES "
			            
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
			    	new File('./short_sales_volume_and_value/').mkdir()
		     		new FileOutputStream('./short_sales_volume_and_value/'+yyyyMmDd+'.tmp').withWriter('UTF-8') { writer ->
		        		writer << resultSql+';'
		     		}
		     		new File('./short_sales_volume_and_value/'+yyyyMmDd+'.tmp').renameTo('./short_sales_volume_and_value/'+yyyyMmDd+'.sql')
					print '*'
				}
	    }
	}
}

module.db.SqlExecuter.execute{
    dir './short_sales_volume_and_value'
}
module.io.FileBetch.execute{
	clean './short_sales_volume_and_value'
	delete './short_sales_volume_and_value'
}
println 'import short_sales_volume_and_value done'

