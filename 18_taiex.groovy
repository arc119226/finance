
module.processor.ProcessorRunner.runMonthByMonth{
	startYear Calendar.getInstance().get(Calendar.YEAR)//Integer.valueOf(listing_day.toString()[0..3])
	startMonth Calendar.getInstance().get(Calendar.MONTH)+1//Integer.valueOf(listing_day.toString()[4..5])
	startday 1//Integer.valueOf(listing_day.toString()[6..7])
	endYear Calendar.getInstance().get(Calendar.YEAR)
	endMonth Calendar.getInstance().get(Calendar.MONTH)+1
	endDay 1
	process{yyyyMmDd->
		def resultSql =''
		if(!new File('./taiex/'+yyyyMmDd+'.sql').exists()){
		    def returnJson = module.web.Webget.download{
		         url "https://www.twse.com.tw/indicesReport/MI_5MINS_HIST?response=json&lang=en&date=${yyyyMmDd}"
		         decode 'utf-8'
		         validateTaiex true
		    }

			module.parser.JsonConvert.convert{
	        	input returnJson
	        	parseRule {json->
		            if(json.stat != 'OK'){
		               return null
		            }
		            def fields = json.fields.collect(fieldNormalize)
		            if(resultSql==''){
		            	resultSql = "REPLACE INTO `stock_tw`.`taiex` (`${fields.join('`,`')}`,`traded_day`) VALUES "
		            }
		            for(int i=0;i<json.data.size;i++){
		                def _data = json.data[i].collect(valueNormalise).join("','");
		                if(resultSql.endsWith('VALUES ')){
		                  resultSql+="\r\n('${_data}','${yyyyMmDd}')"
		               }else{
		                  resultSql+="\r\n,('${_data}','${yyyyMmDd}')"
		               }
		            }
	        	}
	    	}//convert
	    	if(resultSql && !resultSql.endsWith('VALUES ')){
				new File('./taiex/').mkdir()
				new FileOutputStream('./taiex/'+yyyyMmDd+'.tmp').withWriter('UTF-8') { writer ->
					writer << resultSql+';'
				}
				new File('./taiex/'+yyyyMmDd+'.tmp').renameTo('./taiex/'+yyyyMmDd+'.sql')
				print '*'
			}
		}else{
			print '>'
		}
	}//process
}//run
module.db.SqlExecuter.execute{
    dir './taiex'
}
module.io.FileBetch.execute{
	clean './taiex'
	delete './taiex'
}
println 'import taiex done'